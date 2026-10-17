# Traincast — Prediction Output Format

## File: `prediction_output.json`

This file is an **array** of train prediction objects — one object per train.

## Fields (per train object)

| Field | Type | Description |
|---|---|---|
| `trainNumber` | string | Train number, e.g. "12952" |
| `trainName` | string | Full train name |
| `liveStatus` | string | "running" / "not-started" / etc. |
| `currentStation` | string | Station where the train currently is |
| `currentDelayMin` | number | Current delay in minutes (from live data) |
| `nextStation` | string | Next halt station |
| `estimatedSectionType` | string | "CLEAR" / "NORMAL" / "BUSY" |
| `predictedDisruptions` | array | List of predicted disruption objects (see below) |
| `totalPredictedExtraDelayMin` | number | Sum of all predicted extra delay (minutes) |
| `predictedETAMinutes` | number | Final predicted ETA to next halt (minutes) |
| `generatedAt` | string | Timestamp when this prediction was generated |

## `predictedDisruptions` — Object Structure

| Field | Type | Description |
|---|---|---|
| `type` | string | e.g. "HEAVY_RAIN", "HIGH_CONGESTION", "SIGNAL_HALT" |
| `delayMinutes` | number | Extra delay this disruption adds |
| `description` | string | Human-readable description |

## Example

```json
[
  {
    "trainNumber": "12952",
    "trainName": "New Delhi - Mumbai Central Tejas Rajdhani Express",
    "liveStatus": "running",
    "currentStation": "Churaman Nagri",
    "currentDelayMin": 18,
    "nextStation": "Kota Jn",
    "estimatedSectionType": "NORMAL",
    "predictedDisruptions": [
      {
        "type": "HEAVY_RAIN",
        "delayMinutes": 22,
        "description": "Heavy rain reducing sectional speed"
      }
    ],
    "totalPredictedExtraDelayMin": 22,
    "predictedETAMinutes": 187.5,
    "generatedAt": "2026-09-04T12:46:43"
  }
]