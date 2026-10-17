package com.traincast;

public class Train {
    private String trainNumber;
    private String trainName;
    private int currentDelay;     // minutes
    private double currentSpeed;  // km/h
    private String status;

    public Train(String trainNumber, String trainName, int currentDelay, double currentSpeed, String status) {
        this.trainNumber = trainNumber;
        this.trainName = trainName;
        this.currentDelay = currentDelay;
        this.currentSpeed = currentSpeed;
        this.status = status;
    }

    public String  getTrainNumber() {
        return trainNumber;
    }

    public String getTrainName() {
        return trainName;
    }

    public int getCurrentDelay() {
        return currentDelay;
    }

    public double getCurrentSpeed() {
        return currentSpeed;
    }

    public String getStatus() {
        return status;
    }

    public void updateStatus() {
        if (currentDelay == 0) {
            status = "ON TIME";
        } else if (currentDelay <= 15) {
            status = "SLIGHTLY DELAYED";
        } else {
            status = "DELAYED";
        }
    }

    public void displayInfo() {
        System.out.println("Train Number: " + trainNumber);
        System.out.println("Train Name: " + trainName);
        System.out.println("Current Delay: " + currentDelay);
        System.out.println("Current Speed: " + currentSpeed);
        System.out.println("Status: " + status);
        System.out.println("-----------------------------");
    }
}
