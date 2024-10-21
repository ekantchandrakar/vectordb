package org.vectorspacedatabase.vectordb;

import ai.djl.MalformedModelException;
import ai.djl.repository.zoo.ModelNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vectorspacedatabase.vectordb.document.Document;
import org.vectorspacedatabase.vectordb.embeddings.EmbeddingModel;
import org.vectorspacedatabase.vectordb.embeddings.EmbeddingStrategy;
import org.vectorspacedatabase.vectordb.embeddings.HuggingFaceEmbeddingModel;
import org.vectorspacedatabase.vectordb.persistence.FileStorage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class VectorDatabase {

    public static final Logger log = LoggerFactory.getLogger(VectorDatabase.class);

    protected Map<String, Document> contentStore = new ConcurrentHashMap<>();

    private EmbeddingStrategy embeddingStrategy;

    private final FileStorage fileStorage;

    public VectorDatabase(EmbeddingStrategy embeddingStrategy, FileStorage fileStorage) {
        this.embeddingStrategy = embeddingStrategy;
        this.fileStorage = fileStorage;
    }

    public void setEmbeddingStrategy(EmbeddingStrategy embeddingStrategy) {
        this.embeddingStrategy = embeddingStrategy;
    }

    public void add(List<Document> documents) {
        for(Document document : documents) {
            log.info("Embedding for document with id : {}", document.getId());
            List<Float> embedding = this.embeddingStrategy.embed(document);
            document.setEmbedding(embedding);
            this.contentStore.put(document.getId(), document);
            this.fileStorage.saveDocumentIntoFile(document.getId(), document);
        }
    }

    // load files from storage
    public Document getDocument(String documentId) {
        return this.contentStore.get(documentId);
    }

    public Document getDocumentFromFile(String documentId) {
        return this.fileStorage.loadDocumentFromFile(documentId);
    }

    public Optional<Boolean> delete(List<String> documentIdsToDelete) {
        for(String id: documentIdsToDelete) {
            this.contentStore.remove(id);
        }

        return Optional.of(Boolean.TRUE);
    }

    @Override
    public String toString() {
        return "VectorDatabase{" +
                "contentStore=" + contentStore +
                '}';
    }

    public static void main(String[] args) throws IOException, ModelNotFoundException, MalformedModelException {

        try {
            final String OPEN_AI_API_KEY = "";

            EmbeddingStrategy openAIEmbeddingStrategy = new EmbeddingModel.Builder()
                    .withApiKey(OPEN_AI_API_KEY)
                    .withModel("text-embedding-ada-002")
                    .withApiEndPoint("https://api.openai.com/v1/embeddings")
                    .build();

            EmbeddingStrategy huggingFaceEmbeddingStrategy = new HuggingFaceEmbeddingModel.Builder()
                    .withModelUrl("djl://ai.djl.huggingface.pytorch/sentence-transformers/all-MiniLM-L6-v2")
                    .build();

            log.info("Embedding model: {}", huggingFaceEmbeddingStrategy);

            VectorDatabase vdb = new VectorDatabase(huggingFaceEmbeddingStrategy, new FileStorage());

            File pdfFile = Paths.get(ClassLoader.getSystemResource("test.pdf").toURI()).toFile();

            List<Document> documents = List.of(Document.builder().withContent(pdfFile).withMetadata("meta1", "value1").build());

            vdb.add(documents);

            log.info("Content store is: {}", vdb);

        } catch (Exception e) {
            log.error("Error initializing EmbeddingModel", e);
        }

    }
}