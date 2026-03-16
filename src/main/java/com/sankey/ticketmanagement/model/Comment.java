package com.sankey.ticketmanagement.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "comments")
public class Comment {

    @Id
    private String id;

    private String ticketId;

    private String authorId;

    private String authorName;

    private Role authorRole;

    private String message;

    private LocalDateTime createdAt;
}