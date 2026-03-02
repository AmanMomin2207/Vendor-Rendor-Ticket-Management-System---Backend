package com.sankey.ticketmanagement.repository;

import com.sankey.ticketmanagement.model.TicketHistory;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TicketHistoryRepository extends MongoRepository<TicketHistory, String> {
}