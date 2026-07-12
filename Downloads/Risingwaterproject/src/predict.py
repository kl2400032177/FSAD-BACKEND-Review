from pathlib import Path

import joblib
import pandas as pd


MODEL_PATH = Path("models/best_flood_model.joblib")


def load_model() -> dict:
    if not MODEL_PATH.exists():
        raise FileNotFoundError(
            "Model artifact not found. Run `python src/train_model.py` before starting the app."
        )
    return joblib.load(MODEL_PATH)


def predict_flood(input_data: dict) -> dict:
    artifact = load_model()
    features = artifact["features"]
    row = pd.DataFrame([{feature: input_data[feature] for feature in features}])
    pipeline = artifact["pipeline"]
    prediction = int(pipeline.predict(row)[0])

    if hasattr(pipeline, "predict_proba"):
        probability = float(pipeline.predict_proba(row)[0][1])
    else:
        probability = 1.0 if prediction else 0.0

    return {
        "prediction": prediction,
        "risk_label": "High Flood Risk" if prediction else "Low Flood Risk",
        "probability": round(probability * 100, 2),
        "model_name": artifact["model_name"],
        "accuracy": round(artifact["accuracy"] * 100, 2),
    }
