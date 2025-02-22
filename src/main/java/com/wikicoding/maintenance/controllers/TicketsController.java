package com.wikicoding.maintenance.controllers;

import com.wikicoding.maintenance.dtos.CreateTicketRequest;
import com.wikicoding.maintenance.persistence.datamodel.Ticket;
import com.wikicoding.maintenance.services.JwtService;
import com.wikicoding.maintenance.services.TicketsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tickets")
@RequiredArgsConstructor
public class TicketsController {
    private final TicketsService ticketsService;
    private final JwtService jwtService;

    @GetMapping
    public ResponseEntity<Object> getAllTickets(@RequestParam(name = "page", defaultValue = "0", required = false) int page,
                                                @RequestParam(name = "pageSize", defaultValue = "25", required = false) int pageSize,
                                                @RequestParam(name = "sort", defaultValue = "ASC", required = false) String sortType) {
        Page<Ticket> tickets = ticketsService.getTickets(page, pageSize, sortType);
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/{technicianName}")
    public ResponseEntity<Object> getAllTicketsByTechnicianName(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "pageSize", defaultValue = "25", required = false) int pageSize,
            @RequestParam(name = "sort", defaultValue = "ASC", required = false) String sortType,
            @PathVariable(name = "technicianName") String technicianName) {
        Page<Ticket> tickets = ticketsService.getTicketsByTechnician(page, pageSize, sortType, technicianName);
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/ticketId/{ticketId}")
    public ResponseEntity<Object> getTicketById(@PathVariable(name = "ticketId") String ticketId) {
        if (ticketId.trim().isEmpty()) throw new IllegalArgumentException("ticketId can't be null");
        Ticket ticket = ticketsService.getTicketById(ticketId);

        return ResponseEntity.ok(ticket);
    }

    @PostMapping
    public ResponseEntity<Object> createTicket(@RequestBody CreateTicketRequest request,
                                               @RequestHeader("Authorization") String userToken) {
        String username = getUsernameFromToken(userToken);
        Ticket ticket = ticketsService.createTicket(request.getDescription(), username);

        return ResponseEntity.status(HttpStatus.CREATED).body(ticket);
    }

    @PatchMapping("/{ticketId}")
    public ResponseEntity<Object> updateTicketStatus(@PathVariable(name = "ticketId") String ticketId,
                                                     @RequestHeader("Authorization") String userToken) {
        String username = getUsernameFromToken(userToken);
        Ticket ticket = ticketsService.changeTicketCompletion(ticketId, username);

        return ResponseEntity.ok(ticket);
    }

    @DeleteMapping("/{ticketId}")
    public ResponseEntity<Object> deleteTicket(@PathVariable(name = "ticketId") String ticketId) {
        ticketsService.deleteTicket(ticketId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Delete ticket with id " + ticketId);
    }

    private String getUsernameFromToken(String userToken) {
        int bearerNumberOfChars = 7;
        return jwtService.extractUsername(userToken.substring(bearerNumberOfChars));
    }
}
