package com.sankey.ticketmanagement.repository;

import com.sankey.ticketmanagement.model.Ticket;
import com.sankey.ticketmanagement.model.TicketStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TicketRepository extends MongoRepository<Ticket, String> {

    long countBycreatedBy(String createdBy);

    long countBycreatedByAndStatus(String createdBy, TicketStatus status);

    long countByStatus(TicketStatus status);

    long countByAssignedTo(String assignedTo);

    long countByAssignedToAndStatus(String assignedTo, TicketStatus status);

    List<Ticket> findByAssignedTo(String assignedTo);
}