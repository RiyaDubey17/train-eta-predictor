import pandas as pd
import numpy as np
import lightgbm as lgb
from sklearn.model_selection import train_test_split
from sklearn.metrics import mean_absolute_error, mean_squared_error
import joblib

# Load data
df = pd.read_csv("historical_train_data.csv")

features = [
    "current_delay",
    "remaining_distance",
    "scheduled_section_time",
    "historical_avg_delay_train",
    "historical_avg_delay_section",
    "section_type",
    "hour",
    "day_of_week",
    "weather_code",
    "congestion_score",
    "recent_delay_trend",
    "train_type_encoded"
]

target = "remaining_time_minutes"

X = df[features]
y = df[target]

X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)

model = lgb.LGBMRegressor(
    n_estimators=300,
    learning_rate=0.05,
    max_depth=6,
    num_leaves=31,
    random_state=42
)

model.fit(X_train, y_train)

preds = model.predict(X_test)
mae = mean_absolute_error(y_test, preds)
rmse = np.sqrt(mean_squared_error(y_test, preds))

print(f"MAE: {mae:.2f} minutes")
print(f"RMSE: {rmse:.2f} minutes")

joblib.dump(model, "eta_model.pkl")
print("Model saved successfully as eta_model.pkl")