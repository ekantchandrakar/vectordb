package org.vectorspacedatabase.vectordb.similaritysearch.distancemetrices;

import java.util.List;

public class DistanceMetrics {
    private DistanceMetrics() {
        throw new UnsupportedOperationException("This is the distance metrics utility class, can't be instantiated");
    }

    public static double dotProduct(List<Float> vectorX, List<Float> vectorY) {
        if(vectorX == null || vectorY == null) {
            throw new RuntimeException("Vectors must be NotNull");
        }

        if(vectorX.size() != vectorY.size()) {
            throw new IllegalArgumentException("Vectors size must be same");
        }

        double result = 0;
        for(int index = 0; index < vectorX.size(); index++) {
            result += (vectorX.get(index) * vectorY.get(index));
        }

        return result;
    }

    public static double cosineSimilarity(List<Float> vectorX, List<Float> vectorY) {
        if(vectorX == null || vectorY == null) {
            throw new RuntimeException("Vectors must be NotNull");
        }

        if(vectorX.size() != vectorY.size()) {
            throw new IllegalArgumentException("Vectors size must be same");
        }

        double dotProduct = dotProduct(vectorX, vectorY);
        double normalizeX = normalization(vectorX);
        double normalizeY = normalization(vectorY);

        if(normalizeX == 0 || normalizeY == 0) {
            throw new IllegalArgumentException("Vectors must not have zero normalization");
        }

        return dotProduct / (Math.sqrt(normalizeX) * Math.sqrt(normalizeY));
    }

    private static double normalization(List<Float> vector) {
        return dotProduct(vector, vector);
    }

    public static double euclideanDistance(List<Float> vectorX, List<Float> vectorY) {
        if(vectorX == null || vectorY == null) {
            throw new RuntimeException("Vectors must be NotNull");
        }

        if (vectorX.size() != vectorY.size()) {
            throw new IllegalArgumentException("Vectors size must be same");
        }

        double result = 0;
        for(int index = 0; index < vectorX.size(); index++) {
            result += Math.pow((vectorX.get(index) - vectorY.get(index)), 2);
        }

        return Math.sqrt(result);
    }

    public static double manhattanDistance(List<Float> vectorX, List<Float> vectorY) {
        if(vectorX == null || vectorY == null) {
            throw new RuntimeException("Vectors must be NotNull");
        }

        if(vectorX.size() != vectorY.size()) {
            throw new IllegalArgumentException("Vectors size must be same");
        }

        double result = 0;
        for(int index = 0; index < vectorX.size(); index++) {
            result += Math.abs(vectorX.get(index) - vectorY.get(index));
        }

        return result;
    }
}
