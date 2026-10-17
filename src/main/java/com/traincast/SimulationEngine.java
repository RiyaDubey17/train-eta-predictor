package com.traincast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SimulationEngine {

    private Random random = new Random();

    class Factor {
        String name;
        int minDelay;
        int maxDelay;
        double baseProbability;

        Factor(String name, int minDelay, int maxDelay, double baseProbability) {
            this.name = name;
            this.minDelay = minDelay;
            this.maxDelay = maxDelay;
            this.baseProbability = baseProbability;
        }
    }

    private List<Factor> factors = new ArrayList<>();

    public SimulationEngine() {
        factors.add(new Factor("HEAVY_RAIN", 12, 28, 0.32));
        factors.add(new Factor("DENSE_FOG", 18, 42, 0.20));
        factors.add(new Factor("HIGH_CONGESTION", 15, 48, 0.45));
        factors.add(new Factor("TEMP_SPEED_RESTRICTION", 8, 24, 0.26));
        factors.add(new Factor("ENGINEERING_BLOCK", 25, 60, 0.11));
        factors.add(new Factor("SIGNAL_HALT", 6, 18, 0.28));
    }

    public List<Disruption> generateDisruptions(int currentDelay, String sectionType, int hourOfDay) {
        List<Disruption> result = new ArrayList<>();

        for (Factor f : factors) {
            double probability = f.baseProbability;

            if (sectionType.equalsIgnoreCase("BUSY")) {
                if (f.name.equals("HIGH_CONGESTION") || f.name.equals("SIGNAL_HALT")) {
                    probability += 0.20;
                }
            } else if (sectionType.equalsIgnoreCase("CLEAR")) {
                probability -= 0.12;
            }

            if (hourOfDay >= 7 && hourOfDay <= 10 || hourOfDay >= 17 && hourOfDay <= 21) {
                if (f.name.equals("HIGH_CONGESTION")) {
                    probability += 0.15;
                }
            }

            if (currentDelay > 20) {
                probability += 0.10;
            }

            if (probability > 0.85) probability = 0.85;
            if (probability < 0.05) probability = 0.05;

            if (random.nextDouble() <= probability) {
                int delay = f.minDelay + random.nextInt(f.maxDelay - f.minDelay + 1);

                if (currentDelay > 30) {
                    delay = (int) (delay * 1.15);
                }

                result.add(new Disruption(f.name, delay, getDescription(f.name)));
            }
        }

        if (sectionType.equalsIgnoreCase("CLEAR") && currentDelay > 10 && random.nextDouble() < 0.4) {
            int recovery = 5 + random.nextInt(12);
            result.add(new Disruption("RECOVERY", -recovery, "Good conditions - partial delay recovery"));
        }

        return result;
    }

    public List<Disruption> generateDisruptions() {
        return generateDisruptions(15, "NORMAL", 14);
    }

    private String getDescription(String type) {
        switch (type) {
            case "HEAVY_RAIN": return "Heavy rain reducing sectional speed";
            case "DENSE_FOG": return "Dense fog affecting visibility and speed";
            case "HIGH_CONGESTION": return "High traffic density on the section";
            case "TEMP_SPEED_RESTRICTION": return "Temporary Speed Restriction (TSR)";
            case "ENGINEERING_BLOCK": return "Engineering / maintenance block";
            case "SIGNAL_HALT": return "Unscheduled signal halt";
            case "RECOVERY": return "Partial recovery due to clear conditions";
            default: return "Operational disruption";
        }
    }

    public void printAllFactors() {
        System.out.println("Available Simulation Factors:");
        for (Factor f : factors) {
            System.out.printf("- %-25s | %2d-%2d min | Base Prob: %.0f%%\n",
                    f.name, f.minDelay, f.maxDelay, f.baseProbability * 100);
        }
    }
}