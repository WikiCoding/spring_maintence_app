package com.wikicoding.maintenance.services;

import com.wikicoding.maintenance.controllers.exceptions.EntryNotFoundException;
import com.wikicoding.maintenance.persistence.datamodel.CachedTicket;
import com.wikicoding.maintenance.persistence.datamodel.Ticket;
import com.wikicoding.maintenance.persistence.repository.RedisCache;
import com.wikicoding.maintenance.persistence.repository.TicketRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TicketsService {
    private final TicketRepository ticketRepository;
    private final RedisCache redisCache;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Transactional
    public Ticket createTicket(String description, String technicianName) {
        // no validation for existence of a user because the user itself already was verified and allowed to access this service
        // this technicianName is extracted from the token claims which comes in the authorization header of the http request :)
        Ticket ticket = new Ticket(description, technicianName);

        return ticketRepository.save(ticket);
    }

    public Page<Ticket> getTickets(int page, int pageSize, String sortType) {
        Sort sort = sortType.trim().equalsIgnoreCase("ASC") ?
                Sort.by("ticketId").ascending() :
                Sort.by("ticketId").descending();

        Pageable pageable = PageRequest.of(page, pageSize, sort);

        Page<Ticket> tickets = ticketRepository.findAll(pageable);

        // bulk save of the returned tickets to cache
        bulkTicketsSaveToCache(tickets);

        return tickets;
    }

    public Page<Ticket> getTicketsByTechnician(int page, int pageSize, String sortType, String technicianName) {
        Sort sort = sortType.trim().equalsIgnoreCase("ASC") ?
                Sort.by("ticketId").ascending() :
                Sort.by("ticketId").descending();

        Pageable pageable = PageRequest.of(page, pageSize, sort);

        Page<Ticket> tickets = ticketRepository.findAllByTechnicianName(technicianName, pageable);

        bulkTicketsSaveToCache(tickets);

        return tickets;
    }

    public Ticket getTicketById(String ticketId) {
        Optional<CachedTicket> cachedTicket = redisCache.findById(ticketId);
        if (cachedTicket.isEmpty()) log.warn("Ticket with id {} not found in cache. Cache miss.", ticketId);

        if (cachedTicket.isPresent()) {
            log.info("Cache hit! Returning ticket id {}", ticketId);
            return new Ticket(cachedTicket.get());
        }

        Ticket ticket = ticketRepository.findById(ticketId).orElseThrow(() -> {
            log.error("Ticket with id {} not found in repository", ticketId);
            return new EntryNotFoundException("Ticket with id " + ticketId + " not found in repository");
        });

        log.info("Saving found ticket in cache");
        redisCache.save(new CachedTicket(ticket));

        return ticket;
    }

    @Transactional
    public Ticket changeTicketCompletion(String ticketId, String technicianName) {
        Ticket ticket = getTicketAndValidateOwnership(ticketId, technicianName);

        int newVersion = ticket.getVersion() + 1;
        ticket.setComplete(!ticket.isComplete());
        ticket.setVersion(newVersion);

        Ticket saved = ticketRepository.save(ticket);
        redisCache.deleteById(ticketId); // avoiding cache mismatch with the database on concurrency after entry is changed

        publishNotificationToManager(ticketId);

        return saved;
    }

    @Transactional
    public void deleteTicket(String ticketId) {
        try {
            ticketRepository.deleteById(ticketId);
            redisCache.deleteById(ticketId); // avoiding cache mismatch with the database on concurrency after entry is deleted
            log.info("Deleted ticket with id {}", ticketId);
        } catch (Exception e) {
            log.error("Could not delete ticket id {} due to {}", ticketId, e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    private Ticket getTicketAndValidateOwnership(String ticketId, String technicianName) {
        Optional<CachedTicket> cachedTicket = redisCache.findById(ticketId);

        Ticket ticket = new Ticket();

        if (cachedTicket.isEmpty()) {
            log.warn("Ticket with id {} not found in cache. Cache miss.", ticketId);

            ticket = ticketRepository.findById(ticketId).orElseThrow(() -> {
                log.error("Ticket with id {} not found", ticketId);
                return new EntryNotFoundException("Ticket not found");
            });

            log.info("Found ticket with ticket id {} and will be stored in cache for 60 secs", ticketId);
            redisCache.save(new CachedTicket(ticket));
        }

        if (cachedTicket.isPresent()) {
            log.info("Cache hit! Returning ticket id {}", ticketId);
            ticket = new Ticket(cachedTicket.get());
        }

        if (!ticket.getTechnicianName().equals(technicianName)) {
            log.error("Technician {} doesn't own ticket {}", technicianName, ticketId);
            throw new IllegalArgumentException("Technician " + technicianName + " doesn't own ticket" + ticketId);
        }

        return ticket;
    }

    private void publishNotificationToManager(String ticketId) {
        try {
            String topic = "completed-tickets";
            var ack = kafkaTemplate.send(topic, ticketId).get();
            log.info("Kafka message sent: {}", ack);
        } catch (Exception e) {
            log.error("Failed to send Kafka message", e);
            throw new RuntimeException("Failed to send Kafka message", e);
        }
    }

    private void bulkTicketsSaveToCache(Page<Ticket> tickets) {
        // bulk save of the returned tickets to cache. Can't use saveAll because then the tickets keys are not stored as ticket:ticketId
        tickets.forEach(ticket -> redisCache.save(new CachedTicket(ticket)));
        log.info("Stored {} tickets to cache", tickets.getTotalElements());
    }
}
