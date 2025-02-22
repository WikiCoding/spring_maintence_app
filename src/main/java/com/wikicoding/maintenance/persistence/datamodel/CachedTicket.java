package com.wikicoding.maintenance.persistence.datamodel;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.time.LocalDateTime;

@RedisHash(value = "tickets", timeToLive = 60L)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CachedTicket implements Serializable {
    @Id
    private String id;
    private String technicianName;
    private String description;
    private LocalDateTime createdAt;
    private boolean isComplete;
    private int version;

    public CachedTicket(Ticket ticket) {
        this.id = ticket.getTicketId();
        this.technicianName = ticket.getTechnicianName();
        this.description = ticket.getDescription();
        this.createdAt = ticket.getCreatedAt();
        this.isComplete = ticket.isComplete();
        this.version = ticket.getVersion();
    }
}
