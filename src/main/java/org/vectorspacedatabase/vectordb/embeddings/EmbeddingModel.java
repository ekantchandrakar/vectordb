package org.vectorspacedatabase.vectordb.embeddings;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.LoggerFactory;
import org.vectorspacedatabase.vectordb.document.Document;

import org.slf4j.Logger;
import org.vectorspacedatabase.vectordb.utility.GenericUtils;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class EmbeddingModel implements EmbeddingStrategy {

    private static final Logger log = LoggerFactory.getLogger(EmbeddingModel.class);

    private final String API_KEY;

    private final String MODEL_NAME;

    private final String OPENAI_EMBEDDING_URL ;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        public String apiKey;
        private String modelName;
        private String apiEndpoint;

        public Builder withApiKey(String apiKey) {
            this.apiKey = apiKey;
            return this;
        }

        public Builder withModel(String modelName) {
            this.modelName = modelName;
            return this;
        }

        public Builder withApiEndPoint(String apiEndPoint) {
            this.apiEndpoint = apiEndPoint;
            return this;
        }

        public EmbeddingModel build() {
            return new EmbeddingModel(this);
        }
    }

    public EmbeddingModel(Builder builder) {
        this.API_KEY = builder.apiKey;
        this.MODEL_NAME = builder.modelName;
        this.OPENAI_EMBEDDING_URL  = builder.apiEndpoint;
    }


    @Override
    public List<Float> embed(Document document) {
        try {
            String content = document.getContent();

            log.info("Embedding document with ID: {}", document.getId());
            log.info("Document content: {}", content);

            // Early return if content is empty or null
            if (content == null || content.isEmpty()) {
                log.error("Document content is null or empty for document: {}", document.getId());
                return new ArrayList<>();
            }

            Embedding embedding = mockfetchEmbeddingFromOpenAI(content);

            log.info("Received embedding: {}", embedding);

            return embedding.getEmbedding();
        } catch (Exception e) {
            log.error("Error while generating embedding from documnet : {}", document.getId());
            return new ArrayList<>(List.of(0.5f, 0.5f));
        }
    }

    private Embedding mockfetchEmbeddingFromOpenAI(String content) throws IOException {
        // Mock JSON response similar to OpenAI embedding response
        String mockJsonResponse = """
                {
                  "object": "embedding",
                  "embedding": [
                    0.0023064255, -0.009327292, 0.01325675, -0.00112345, 0.007654321,
                    0.00876543, -0.0032123456, 0.004321234, 0.0012345678, -0.0009876543
                  ],
                  "index": 0
                }
                """;
        ObjectMapper mapper = new ObjectMapper();
        Embedding embedding = mapper.readValue(mockJsonResponse, Embedding.class);
        return embedding;
    }

    private Embedding fetchEmbeddingFromOpenAI(String content) throws IOException, InterruptedException {
        // Create the request body
        String requestBody = GenericUtils.buildRequestBody(content, MODEL_NAME, "float");

        log.info("Request body to OpenAI: " + requestBody);

        // Create an HTTP client
        HttpClient client = HttpClient.newHttpClient();

        // Create the HTTP request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(OPENAI_EMBEDDING_URL))
                .header("Authorization", "Bearer " + API_KEY)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        // Send the request and get the response
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        log.info("Response body from OpenAI: " + response.body());
        // Parse the JSON response into an Embedding Object
        ObjectMapper mapper = new ObjectMapper();
        Embedding embedding = mapper.readValue(response.body(), Embedding.class);

        return embedding;
    }
}
