package org.vectorspacedatabase.vectordb.document;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.util.Assert;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class Document {
    @JsonProperty(index = 1)
    private final String id;

    @JsonProperty(index = 2)
    private final Map<String, Object>  metadata;

    @JsonProperty(index = 3)
    private final String content;

    @JsonProperty(index = 4)
    private List<Float> embedding;

    public Document(String id, String content, Map<String, Object> metadata) {
        this.id = id;
        this.content = content;
        this.metadata = metadata;
        this.embedding = new ArrayList<>();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private String content;
        private Map<String, Object> metadata = new HashMap<>();

        public Builder withMetadata(String key, Object value) {
            Assert.notNull(key, "key must not be null");
            Assert.notNull(value, "value must not be null");
            this.metadata.put(key, value);
            return this;
        }

        public Builder withContent(File file) throws IllegalArgumentException, IOException, FileNotFoundException {
            String fileName = file.getName();
            metadata.put("filename", fileName);

            if(fileName.endsWith(".pdf")) {
                this.content = extractTextFromPDF(file);
            } else if(fileName.endsWith(".docx")) {
                this.content = extractTextFromWord(file);   
            } else {
                throw new IllegalArgumentException("Unsupported file formate: " + fileName);
            }
            
            return this;
        }

        private String extractTextFromPDF(File file) throws IOException {
            PDDocument pdfDocument = PDDocument.load(file);
            PDFTextStripper pdfTextStripper = new PDFTextStripper();
            String text = pdfTextStripper.getText(pdfDocument);
            pdfDocument.close();
            return text;
        }

        private String extractTextFromWord(File file) throws IOException {
            XWPFDocument document = new XWPFDocument(new FileInputStream(file));
            StringBuilder content = new StringBuilder();

            List<XWPFParagraph> paragraphs = document.getParagraphs();
            for(XWPFParagraph paragraph: paragraphs) {
                content.append(paragraph.getText()).append("\n");
            }
            document.close();

            return content.toString();
        }

        private String generateId(String content) {
            return String.valueOf(content.hashCode());
        }

        public Document build() {
            Assert.notNull(content, "Document must not be null");
            this.id = generateId(content);
            return new Document(id, content, metadata);
        }

    }

    public String getId() {
        return id;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public String getContent() {
        return content;
    }

    public List<Float> getEmbedding() {
        return embedding;
    }

    public void setEmbedding(List<Float> embedding) {
        this.embedding = embedding;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Document document = (Document) o;
        return Objects.equals(id, document.id) && Objects.equals(metadata, document.metadata) && Objects.equals(content, document.content) && Objects.equals(embedding, document.embedding);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, metadata, content, embedding);
    }

    @Override
    public String toString() {
        return "Document{" +
                "id='" + id + '\'' +
                ", metadata=" + metadata +
                ", content='" + content + '\'' +
                ", embedding=" + embedding +
                '}';
    }
}
