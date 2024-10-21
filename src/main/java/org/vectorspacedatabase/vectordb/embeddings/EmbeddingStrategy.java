package org.vectorspacedatabase.vectordb.embeddings;

import org.vectorspacedatabase.vectordb.document.Document;

import java.util.List;

public interface EmbeddingStrategy {
    List<Float> embed(Document document);
}
