package com.sankey.ticketmanagement.repository;

import com.sankey.ticketmanagement.model.Priority;
import com.sankey.ticketmanagement.model.Ticket;
import com.sankey.ticketmanagement.model.TicketStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TicketRepository extends MongoRepository<Ticket, String> {

    long countBycreatedBy(String createdBy);

    long countBycreatedByAndStatus(String createdBy, TicketStatus status);

    long countByStatus(TicketStatus status);

    long countByAssignedTo(String assignedTo);

    long countByAssignedToAndStatus(String assignedTo, TicketStatus status);

    List<Ticket> findByAssignedTo(String assignedTo);

    Page<Ticket> findByCreatedBy(String createdBy, Pageable pageable);

    Page<Ticket> findByAssignedTo(String assignedTo, Pageable pageable);

    Page<Ticket> findByStatus(TicketStatus status, Pageable pageable);

    Page<Ticket> findByPriority(Priority priority, Pageable pageable);

    Page<Ticket> findByStatusAndPriority(TicketStatus status,
                                        Priority priority,
                                        Pageable pageable);

    Page<Ticket> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    Page<Ticket> findByStatusAndTitleContainingIgnoreCase(
        TicketStatus status,
        String title,
        Pageable pageable);

    List<Ticket> findByAssignedToAndStatus(String assignedTo, TicketStatus status);

}