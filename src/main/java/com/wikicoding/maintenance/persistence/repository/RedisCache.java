package com.wikicoding.maintenance.persistence.repository;

import com.wikicoding.maintenance.persistence.datamodel.CachedTicket;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RedisCache extends CrudRepository<CachedTicket, String> {
    List<CachedTicket> findAllByTechnicianName(String technicianName);
}
