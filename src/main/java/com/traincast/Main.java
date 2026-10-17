package com.traincast;

import java.util.List;

public class Main {
    public static void main(String[] args) {

        SimulationEngine engine = new SimulationEngine();

        System.out.println("=== Available Factors ===");
        engine.printAllFactors();

        System.out.println("\n=== Random Simulation ===");
        List<Disruption> disruptions = engine.generateDisruptions();

        int totalExtraDelay = 0;
        for (Disruption d : disruptions) {
            d.displayInfo();
            totalExtraDelay += d.getDelayMinutes();
        }

        System.out.println("Total Extra Delay from Simulation: " + totalExtraDelay + " minutes");
    }
}