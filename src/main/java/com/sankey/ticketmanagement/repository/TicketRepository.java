package com.sankey.ticketmanagement.repository;

import com.sankey.ticketmanagement.model.Ticket;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TicketRepository extends MongoRepository<Ticket, String> {

    List<Ticket> findByCreatedBy(String userId);

    List<Ticket> findByAssignedTo(String vendorId);

    List<Ticket> findByStatus(String status);
}