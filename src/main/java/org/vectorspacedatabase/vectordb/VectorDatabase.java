package org.vectorspacedatabase.vectordb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vectorspacedatabase.vectordb.document.Document;
import org.vectorspacedatabase.vectordb.embeddings.EmbeddingModel;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class VectorDatabase {

    public static final Logger log = LoggerFactory.getLogger(VectorDatabase.class);

    protected Map<String, Document> contentStore = new ConcurrentHashMap<>();

    private EmbeddingModel embeddingModel;

    public VectorDatabase(EmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    public void add(List<Document> documents) {
        for(Document document : documents) {
            log.info("Embedding for document with id : {}", document.getId());
            float[] embedding = this.embeddingModel.embed();
            document.setEmbedding(embedding);
            this.contentStore.put(document.getId(), document);
        }
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

    public static void main(String[] args) throws URISyntaxException, IOException {
        EmbeddingModel embeddingModel = new EmbeddingModel();
        VectorDatabase vdb = new VectorDatabase(embeddingModel);

        File pdfFile = Paths.get(ClassLoader.getSystemResource("test.pdf").toURI()).toFile();

        List<Document> documents = List.of(Document.builder().withContent(pdfFile).withMetadata("meta1", "value1").build());

        vdb.add(documents);

        log.info("Content store is: {}", vdb);
    }
}