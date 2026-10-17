package com.traincast;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.GsonBuilder;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.io.FileWriter;
import java.util.Arrays;

public class RailRadarTest {
    public static void main(String[] args) throws Exception {
        String apiKey = System.getenv("RAILRADAR_API_KEY");
        System.out.println("Key received ");

        List<String> trainNumbers = Arrays.asList(
                "12952", // Mumbai Rajdhani
                "12951", // August Kranti Rajdhani
                "12301", // Howrah Rajdhani
                "12002", // Bhopal Shatabdi
                "12622", // Tamil Nadu Express
                "12615", // Grand Trunk Express
                "12137", // Punjab Mail
                "12009"  // Shatabdi Express
        );

        List<Map<String, Object>> allPredictions = new ArrayList<>();

        for (String trainNumber : trainNumbers) {
            try {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("https://api.railradar.in/v1/trains/" + trainNumber + "/live"))
                        .header("Authorization", "Bearer " + apiKey)
                        .GET()
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                System.out.println("\n=== Train " + trainNumber + " ===");
                System.out.println("Status Code: " + response.statusCode());

                JsonObject root = JsonParser.parseString(response.body()).getAsJsonObject();
                JsonObject data = root.getAsJsonObject("data");

                String trainName = data.get("trainName").getAsString();
                int delayMinutes = data.get("delayMinutes").getAsInt();
                String liveStatus = data.get("status").getAsString();

                JsonObject currentLocation = data.getAsJsonObject("currentLocation");
                String currentStation = currentLocation.get("stationName").getAsString();
                double distanceFromOrigin = currentLocation.get("distanceFromOriginKm").getAsDouble();

                JsonObject nextHalt = data.getAsJsonObject("nextHalt");
                String nextStation = nextHalt.get("stationName").getAsString();

                System.out.println("---- PARSED DATA ----");
                System.out.println("Train: " + trainNumber + " - " + trainName);
                System.out.println("Live Status: " + liveStatus);
                System.out.println("Delay: " + delayMinutes + " min");
                System.out.println("Current Location: " + currentStation + " (" + distanceFromOrigin + " km from origin)");
                System.out.println("Next Halt: " + nextStation);

                SimulationEngine engine = new SimulationEngine();

                String sectionType = TrainPredictor.estimateSectionType(delayMinutes);
                int currentHour = TrainPredictor.getCurrentHour();

                List<Disruption> predictedDisruptions = engine.generateDisruptions(
                        delayMinutes,
                        sectionType,
                        currentHour
                );

                int totalPredictedExtraDelay = 0;
                for (Disruption d : predictedDisruptions) {
                    totalPredictedExtraDelay += d.getDelayMinutes();
                }
                int confidence = TrainPredictor.calculateConfidence(delayMinutes, totalPredictedExtraDelay, sectionType);

                double nextHaltDistance = nextHalt.get("distance").getAsDouble();
                double remainingDistance = nextHaltDistance - distanceFromOrigin;
                JsonObject trainInfo = data.getAsJsonObject("train");
                double avgSpeed = trainInfo.get("avgSpeed").getAsDouble();

                double predictedETA = TrainPredictor.calculateETA(
                        remainingDistance, avgSpeed, delayMinutes, totalPredictedExtraDelay, trainNumber
                );

                System.out.println("Section Type Estimated: " + sectionType);
                System.out.println("Number of predicted disruptions: " + predictedDisruptions.size());
                for (Disruption d : predictedDisruptions) {
                    d.displayInfo();
                }
                System.out.println("Total predicted extra delay: " + totalPredictedExtraDelay + " min");
                System.out.println("Final Predicted ETA to next halt: " + predictedETA + " minutes");

                System.out.println("\n--- LIVE SIMULATION (Demo) ---");
                for (int i = 1; i <= 3; i++) {
                    List<Disruption> liveUpdate = engine.generateDisruptions(delayMinutes, sectionType, currentHour);
                    int liveExtraDelay = 0;
                    for (Disruption d : liveUpdate) {
                        liveExtraDelay += d.getDelayMinutes();
                    }

                    double liveETA = TrainPredictor.calculateETA(remainingDistance, avgSpeed, delayMinutes, liveExtraDelay, trainNumber);
                    System.out.println("Update " + i + ": " + liveUpdate.size() + " disruption(s), Live ETA = " + liveETA + " min");

                    Thread.sleep(2000);
                }

                Map<String, Object> output = new HashMap<>();
                output.put("trainNumber", trainNumber);
                output.put("trainName", trainName);
                output.put("liveStatus", liveStatus);
                output.put("currentStation", currentStation);
                output.put("currentDelayMin", delayMinutes);
                output.put("nextStation", nextStation);
                output.put("estimatedSectionType", sectionType);
                output.put("predictedDisruptions", predictedDisruptions);
                output.put("totalPredictedExtraDelayMin", totalPredictedExtraDelay);
                output.put("predictedETAMinutes", predictedETA);
                output.put("confidence", confidence);
                output.put("generatedAt", java.time.LocalDateTime.now().toString());

                allPredictions.add(output);

            } catch (Exception e) {
                System.out.println("Error fetching train " + trainNumber + ": " + e.getMessage());
            }
        }

        com.google.gson.Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonOutput = gson.toJson(allPredictions);


        try (FileWriter writer = new FileWriter("prediction_output.json")) {
            writer.write(jsonOutput);
            System.out.println("\nJSON file saved with " + allPredictions.size() + " trains: prediction_output.json");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}