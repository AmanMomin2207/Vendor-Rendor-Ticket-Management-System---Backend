package com.sankey.ticketmanagement.service;

import com.mongodb.client.gridfs.model.GridFSFile;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Service
public class FileStorageService {

    private final GridFsTemplate gridFsTemplate;
    private final GridFsOperations gridFsOperations;

    public FileStorageService(GridFsTemplate gridFsTemplate,
                              GridFsOperations gridFsOperations) {
        this.gridFsTemplate = gridFsTemplate;
        this.gridFsOperations = gridFsOperations;
    }

    // Upload file → returns GridFS fileId as String
    public String uploadFile(MultipartFile file) throws IOException {
        ObjectId fileId = gridFsTemplate.store(
                file.getInputStream(),
                file.getOriginalFilename(),
                file.getContentType()
        );
        return fileId.toString();
    }

    // Download file as InputStream
    public InputStream downloadFile(String fileId) throws IOException {
        GridFSFile gridFSFile = gridFsTemplate.findOne(
                new Query(Criteria.where("_id").is(new ObjectId(fileId)))
        );

        if (gridFSFile == null) {
            throw new RuntimeException("File not found: " + fileId);
        }

        return gridFsOperations.getResource(gridFSFile).getInputStream();
    }

    // Delete file from GridFS
    public void deleteFile(String fileId) {
        gridFsTemplate.delete(
                new Query(Criteria.where("_id").is(new ObjectId(fileId)))
        );
    }

    // Get file metadata
    public GridFSFile getFileMetadata(String fileId) {
        return gridFsTemplate.findOne(
                new Query(Criteria.where("_id").is(new ObjectId(fileId)))
        );
    }
}