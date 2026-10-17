package com.traincast;

import java.time.LocalDateTime;
import java.util.Map;

public class TrainPredictor {

    private static final Map<String, Integer> historicalAvgDelay = Map.of(
                "12952", 10,
                "12951", 18,
                "12301", 25,
                "12622", 45,
                "12615", 85,
                "12137", 40,
                "12009", 9,
                "12953", 22,
                "12423", 15,
                "12259", 20
        );

    public static double calculateETA(double remainingDistanceKm, double avgSpeedKmph,
                                      int currentDelayMin, int predictedExtraDelayMin,
                                      String trainNumber) {

        double baseTravelTimeMin = (remainingDistanceKm / avgSpeedKmph) * 60;

        int histDelay = historicalAvgDelay.getOrDefault(trainNumber, 20);
        double historicalComponent = histDelay * 0.20;

        double totalETAMin = baseTravelTimeMin + currentDelayMin + predictedExtraDelayMin + historicalComponent;

        return totalETAMin;
    }

    public static int getCurrentHour() {
        return LocalDateTime.now().getHour();
    }

    public static String estimateSectionType(int currentDelayMin) {
        if (currentDelayMin > 25) return "BUSY";
        else if (currentDelayMin > 10) return "NORMAL";
        else return "CLEAR";
    }
    public static int calculateConfidence(int currentDelayMin, int predictedExtraDelayMin, String sectionType) {
        int confidence = 96;  // high base

        if (currentDelayMin > 40) confidence -= 6;
        else if (currentDelayMin > 25) confidence -= 3;

        if (predictedExtraDelayMin > 35) confidence -= 5;
        else if (predictedExtraDelayMin > 20) confidence -= 2;

        if (sectionType.equals("BUSY")) confidence -= 3;

        // Keep between 88 and 98
        if (confidence < 88) confidence = 88;
        if (confidence > 98) confidence = 98;

        return confidence;
    }


}