# Traincast

Real-time train ETA prediction using live railway data and a dynamic disruption-modeling engine, built in Java with a LightGBM model trained in Python.

Built for **Smart India Hackathon (SIH)** on the problem of forecasting train delays.

---

## Problem

Indian Railways' published arrival times are mostly based on distance divided by speed, plus a fixed buffer. They ignore live conditions such as accumulated delay, congestion, weather and time of day. Traincast predicts the **remaining travel time** of a running train using these live signals, so the ETA at upcoming stations is more realistic.

## How it works

1. **Historical data** (`historical_train_data.csv`) is used to train a gradient-boosted regression model (LightGBM).
2. The model predicts `remaining_time_minutes` for a train from its current state.
3. The Java engine (`com.traincast`) simulates disruptions (`SimulationEngine` and `Disruption`) and adds their delay to the baseline ETA.
4. Output is written to `prediction_output.json`.

### Model features

| Feature | Meaning |
| --- | --- |
| `current_delay` | Delay of the train right now |
| `remaining_distance` | Distance left to the destination/next stop |
| `scheduled_section_time` | Scheduled travel time of the section |
| `historical_avg_delay_train` | Average delay of this train historically |
| `historical_avg_delay_section` | Average delay of this section historically |
| `section_type` | Type of track section |
| `hour` | Hour of day |
| `day_of_week` | Day of the week |
| `weather_code` | Encoded weather condition |
| `congestion_score` | Track congestion indicator |
| `recent_delay_trend` | Whether delay is growing or shrinking |
| `train_type_encoded` | Encoded train category |

**Target:** `remaining_time_minutes`

**Model settings:** `LGBMRegressor` with 300 estimators, learning rate 0.05, max depth 6, 31 leaves, 80/20 train-test split. Evaluated with MAE and RMSE (in minutes).

## Disruption model

`SimulationEngine` rolls each factor independently using a base probability that is adjusted by context:

| Factor | Delay (min) | Base probability |
| --- | --- | --- |
| `HEAVY_RAIN` | 12-28 | 32% |
| `DENSE_FOG` | 18-42 | 20% |
| `HIGH_CONGESTION` | 15-48 | 45% |
| `TEMP_SPEED_RESTRICTION` | 8-24 | 26% |
| `ENGINEERING_BLOCK` | 25-60 | 11% |
| `SIGNAL_HALT` | 6-18 | 28% |

Adjustments:
- **Busy section:** +20% for `HIGH_CONGESTION` and `SIGNAL_HALT`.
- **Clear section:** -12% for every factor.
- **Rush hours (07-10 and 17-21):** +15% for `HIGH_CONGESTION`.
- **Current delay above 20 min:** +10% for every factor. Above 30 min, each delay is also multiplied by 1.15.
- **Recovery:** on a clear section with a current delay above 10 min, there is a 40% chance of recovering 5-16 min.

Probabilities are clamped between 5% and 85%.

## Project structure

```
.
├── src/main/java/com/traincast/   # Main, SimulationEngine, Disruption
├── out/                           # Compiled output
├── historical_train_data.csv      # Training data
├── train_model.py                 # Trains and saves the LightGBM model
├── eta_model.pkl                  # Trained model
├── prediction_output.json         # Sample/latest prediction output
├── pom.xml                        # Maven config (Java 21)
└── README.md
```

## Requirements

- JDK 21
- Maven 3.8+
- Python 3.9+ (only needed to retrain the model)

## Setup and run

### 1. Clone

```bash
git clone https://github.com/RiyaDubey17/train-eta-predictor.git
cd train-eta-predictor
```

### 2. (Optional) Retrain the model

A trained model (`eta_model.pkl`) is already included. To retrain:

```bash
pip install pandas numpy lightgbm scikit-learn joblib
python train_model.py
```

This prints MAE and RMSE and saves a fresh `eta_model.pkl`.

### 3. Build and run the Java engine

```bash
mvn compile
java -cp target/classes com.traincast.Main
```

Alternatively, open the folder in IntelliJ IDEA or VS Code and run `Main` directly.

The program prints all available factors, runs one random disruption simulation, and prints each disruption plus the total extra delay.

### 4. Check the result

Predictions are written to `prediction_output.json`.

## Tech stack

- **Java 21** (Maven) for the real-time engine
- **Python**, **pandas**, **scikit-learn**, **LightGBM** for model training
- **joblib** for model serialization

## Roadmap

- [ ] Web UI (enter a train number, see live ETA and delay)
- [ ] REST API wrapper around the engine
- [ ] Live data integration with a railway API
- [ ] Per-station ETA breakdown

## Contributing

Fork the repo, create a branch, and open a pull request.

## License

Add a license of your choice (MIT is a common default).

