package com.wikicoding.maintenance.persistence.repository;

import com.wikicoding.maintenance.persistence.datamodel.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, String> {
    Page<Ticket> findAll(Pageable pageable);
    Page<Ticket> findAllByTechnicianName(String technicianName, Pageable pageable);
}
