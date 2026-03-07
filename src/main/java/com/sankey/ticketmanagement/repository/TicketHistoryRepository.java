package com.sankey.ticketmanagement.repository;

import com.sankey.ticketmanagement.model.TicketHistory;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TicketHistoryRepository extends MongoRepository<TicketHistory, String> {
    List<TicketHistory> findByTicketIdOrderByChangedAtAsc(String ticketId);
}