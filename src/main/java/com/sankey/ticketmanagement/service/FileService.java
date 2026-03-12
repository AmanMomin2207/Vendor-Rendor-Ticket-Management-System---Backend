package com.sankey.ticketmanagement.service;

import com.sankey.ticketmanagement.exception.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
public class FileService {

    private static final List<String> ALLOWED_TYPES = List.of(
            "application/pdf",
            "image/jpeg", "image/jpg", "image/png",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
    );

    private static final long MAX_SIZE = 10 * 1024 * 1024; // 10MB

    public TicketAttachmentData processFile(MultipartFile file) throws IOException {

        if (file.isEmpty())
            throw new BadRequestException("File is empty");

        if (!ALLOWED_TYPES.contains(file.getContentType()))
            throw new BadRequestException(
                "File type not allowed. Allowed: PDF, JPG, PNG, DOC, DOCX, XLS, XLSX");

        if (file.getSize() > MAX_SIZE)
            throw new BadRequestException("File size exceeds 10MB limit");

        String base64 = Base64.getEncoder().encodeToString(file.getBytes());

        return new TicketAttachmentData(
                UUID.randomUUID().toString(),   // unique id
                file.getOriginalFilename(),
                file.getContentType(),
                file.getSize(),
                base64
        );
    }

    // Simple inner record to carry data back to controller
    public record TicketAttachmentData(
        String id,
        String fileName,
        String fileType,
        long fileSize,
        String base64Data
    ) {}
}