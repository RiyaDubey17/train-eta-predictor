package com.traincast;

public class Disruption {
    private String type;
    private int delayMinutes;
    private String description;



    // Constructor
    public Disruption(String type, int delayMinutes, String description) {
        this.type = type;
        this.delayMinutes = delayMinutes;
        this.description = description;
    }

    // Getters
    public String getType() {
        return type;
    }

    public int getDelayMinutes() {
        return delayMinutes;
    }

    public String getDescription() {
        return description;
    }

    // Display method
    public void displayInfo() {
        System.out.println("Disruption Type : " + type);
        System.out.println("Extra Delay     : " + delayMinutes + " minutes");
        System.out.println("Description     : " + description);
        System.out.println("-----------------------------");

    }

    @Override
    public String toString() {
        return type + " → +" + delayMinutes + " min (" + description + ")";
    }
}