package com.wikicoding.maintenance.persistence.datamodel;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "tickets")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String ticketId;
    private String description;
    private LocalDateTime createdAt = LocalDateTime.now();
    private boolean isComplete = false;
    private String technicianName;
    @Version
    private int version;

    public Ticket(String description, String technicianName) {
        this.description = description;
        this.technicianName = technicianName;
    }

    public Ticket(CachedTicket cachedTicket) {
        this.ticketId = cachedTicket.getId();
        this.description = cachedTicket.getDescription();
        this.createdAt = cachedTicket.getCreatedAt();
        this.isComplete = cachedTicket.isComplete();
        this.technicianName = cachedTicket.getTechnicianName();
        this.version = cachedTicket.getVersion();
    }
}
