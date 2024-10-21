package org.vectorspacedatabase.vectordb.embeddings;

import ai.djl.MalformedModelException;
import ai.djl.inference.Predictor;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelNotFoundException;
import ai.djl.huggingface.translator.TextEmbeddingTranslatorFactory;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.training.util.ProgressBar;
import ai.djl.translate.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vectorspacedatabase.vectordb.document.Document;

import java.io.IOException;
import java.util.*;


public class HuggingFaceEmbeddingModel implements EmbeddingStrategy, AutoCloseable {

    private static final Logger log = LoggerFactory.getLogger(HuggingFaceEmbeddingModel.class);

    private final String MODEL_URL;

    private ZooModel<String, float[]> model; // <sentence, embedding>

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String modelUrl;

        public Builder withModelUrl(String modelUrl) {
            this.modelUrl = modelUrl;
            return this;
        }

        public HuggingFaceEmbeddingModel build() throws ModelNotFoundException, MalformedModelException, IOException {
            return new HuggingFaceEmbeddingModel(this);
        }
    }

    public HuggingFaceEmbeddingModel(Builder builder) throws ModelNotFoundException, MalformedModelException, IOException {
        this.MODEL_URL = builder.modelUrl;
        initializeModel();
    }

    private void initializeModel() throws ModelNotFoundException, MalformedModelException, IOException {
        Criteria<String, float[]> criteria = Criteria.builder()
                .setTypes(String.class, float[].class)
                .optModelUrls(MODEL_URL)
                .optTranslatorFactory(new TextEmbeddingTranslatorFactory())
                .optEngine("PyTorch")
                .optProgress(new ProgressBar())
                .build();

        this.model = criteria.loadModel();
    }

    @Override
    public List<Float> embed(Document document) {
        try (Predictor<String, float[]> predictor = model.newPredictor()) {
            String content = document.getContent();

            log.info("Embedding document with Id: {}", document.getId());
            log.info("Document content: {}", document.getContent());

            if(content == null || content.isEmpty()) {
                log.error("Document content is empty or null for doc id: {}", document.getId());
                return new ArrayList<>();
            }

            float[] embedding = predictor.predict(content);
            List<Float> result = new ArrayList<>();

            for(float value: embedding) {
                result.add(value);
            }

            log.info("Generated embedding with size: {}", result.size());

            return result;
        } catch (TranslateException e) {
            log.error("Error while generating embeddings for document with id: {}", document.getId());
            return new ArrayList<>();
        }
    }

    @Override
    public void close() {
        if (model != null) {
            model.close();
        }
    }

}
