package org.vectorspacedatabase.vectordb.embeddings;

import java.util.List;
import java.util.Objects;

public class Embedding {
    private String object;
    private List<Float> embedding  ;

    private Integer index;

    public String getObject() {
        return object;
    }

    public List<Float> getEmbedding() {
        return embedding;
    }

    public Integer getIndex() {
        return index;
    }

    public Embedding() {}


    public Embedding(String object, List<Float> embedding, Integer index) {
        this.object = object;
        this.embedding = embedding;
        this.index = index;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Embedding embedding1 = (Embedding) o;
        return Objects.equals(object, embedding1.object) && Objects.equals(embedding, embedding1.embedding) && Objects.equals(index, embedding1.index);
    }

    @Override
    public int hashCode() {
        return Objects.hash(object, embedding, index);
    }

    @Override
    public String toString() {
        return "Embedding{" +
                "object='" + object + '\'' +
                ", embedding=" + embedding +
                ", index=" + index +
                '}';
    }
}
