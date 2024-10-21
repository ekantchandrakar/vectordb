package org.vectorspacedatabase.vectordb.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vectorspacedatabase.vectordb.document.Document;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

public class FileStorage {

    private static final Logger log = LoggerFactory.getLogger(FileStorage.class);

    private static final String STORAGE_DIRECTORY = "storage";

    private static final String CACHE_FILE = "cache.json";

    private final ObjectMapper objectMapper;

    public FileStorage() {
        this.objectMapper = new ObjectMapper();
        creteStorageDirectory();
    }

    // Create the storage directory if it doesn't exist
    private void creteStorageDirectory() {
        Path storagePath = Paths.get(STORAGE_DIRECTORY);
        try {
            if(Files.notExists(storagePath)) {
                Files.createDirectories(storagePath);
                log.info("Storage directory created at: {}", storagePath.toAbsolutePath());
            }
        } catch (RuntimeException e) {
            throw new RuntimeException("Failed to create storage directory", e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Save a document to /storage/document_id.json
    public void saveDocumentIntoFile(String documentId, Document document) {
        Path documentPath = Paths.get(STORAGE_DIRECTORY, documentId + ".json");
        try {
            String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(document);
            Files.write(documentPath, json.getBytes(StandardCharsets.UTF_8));
            log.info("Document {} saved into file: {}", documentId, documentPath.toAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException("Error while saving document to file: " + documentPath, e);
        }
    }

    // Load a document from /storage/document_id.json
    public Document loadDocumentFromFile(String documentId) {
        Path documentPath = Paths.get(STORAGE_DIRECTORY, documentId + ".json");

        if (Files.notExists(documentPath)) {
            log.warn("Document {} not found in file: {}", documentId, documentPath.toAbsolutePath());
            return null;
        }

        try {
            String json = new String(Files.readAllBytes(documentPath), StandardCharsets.UTF_8);
            Document document = objectMapper.readValue(json, Document.class);
            log.info("Document {} loaded from file: {}", documentId, documentPath.toAbsolutePath());
            return document;
        } catch (IOException e) {
            throw new RuntimeException("Error while loading document from file: " + documentPath, e);
        }
    }
}
