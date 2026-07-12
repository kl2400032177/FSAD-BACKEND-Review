from pathlib import Path

import joblib
import matplotlib.pyplot as plt
import pandas as pd
from sklearn.compose import ColumnTransformer
from sklearn.metrics import accuracy_score, classification_report, confusion_matrix
from sklearn.model_selection import train_test_split
from sklearn.neighbors import KNeighborsClassifier
from sklearn.pipeline import Pipeline
from sklearn.preprocessing import OneHotEncoder, StandardScaler
from sklearn.tree import DecisionTreeClassifier
from sklearn.ensemble import RandomForestClassifier, GradientBoostingClassifier

try:
    from src.generate_dataset import build_dataset
except ModuleNotFoundError:
    from generate_dataset import build_dataset


DATA_PATH = Path("data/flood_weather_data.csv")
MODEL_PATH = Path("models/best_flood_model.joblib")
REPORT_PATH = Path("models/model_report.txt")
CHART_PATH = Path("static/model_accuracy.png")
TARGET = "flood"


def load_data() -> pd.DataFrame:
    if not DATA_PATH.exists():
        DATA_PATH.parent.mkdir(parents=True, exist_ok=True)
        build_dataset().to_csv(DATA_PATH, index=False)
    return pd.read_csv(DATA_PATH)


def get_xgboost_classifier():
    try:
        from xgboost import XGBClassifier

        return XGBClassifier(
            n_estimators=180,
            max_depth=4,
            learning_rate=0.06,
            subsample=0.9,
            colsample_bytree=0.9,
            eval_metric="logloss",
            random_state=42,
        )
    except Exception:
        return GradientBoostingClassifier(random_state=42)


def make_pipeline(classifier: object, numeric_features: list[str], categorical_features: list[str]) -> Pipeline:
    preprocessor = ColumnTransformer(
        transformers=[
            ("num", StandardScaler(), numeric_features),
            ("cat", OneHotEncoder(handle_unknown="ignore"), categorical_features),
        ]
    )
    return Pipeline(steps=[("preprocessor", preprocessor), ("classifier", classifier)])


def train() -> dict[str, float]:
    df = load_data()
    X = df.drop(columns=[TARGET])
    y = df[TARGET]

    numeric_features = X.select_dtypes(include=["number"]).columns.tolist()
    categorical_features = X.select_dtypes(exclude=["number"]).columns.tolist()

    X_train, X_test, y_train, y_test = train_test_split(
        X, y, test_size=0.2, random_state=42, stratify=y
    )

    models = {
        "Decision Tree": DecisionTreeClassifier(max_depth=7, random_state=42),
        "Random Forest": RandomForestClassifier(n_estimators=180, max_depth=9, random_state=42),
        "KNN": KNeighborsClassifier(n_neighbors=7),
        "XGBoost": get_xgboost_classifier(),
    }

    results: dict[str, float] = {}
    trained_pipelines: dict[str, Pipeline] = {}
    reports: list[str] = []

    for name, classifier in models.items():
        pipeline = make_pipeline(classifier, numeric_features, categorical_features)
        pipeline.fit(X_train, y_train)
        predictions = pipeline.predict(X_test)
        accuracy = accuracy_score(y_test, predictions)
        results[name] = accuracy
        trained_pipelines[name] = pipeline
        reports.append(f"{name} accuracy: {accuracy * 100:.2f}%")
        reports.append(classification_report(y_test, predictions, target_names=["No Flood", "Flood"]))
        reports.append(f"Confusion matrix:\n{confusion_matrix(y_test, predictions)}\n")

    best_name = max(results, key=results.get)
    best_pipeline = trained_pipelines[best_name]

    MODEL_PATH.parent.mkdir(parents=True, exist_ok=True)
    joblib.dump(
        {
            "model_name": best_name,
            "accuracy": results[best_name],
            "pipeline": best_pipeline,
            "features": X.columns.tolist(),
            "sample_input": X.iloc[0].to_dict(),
        },
        MODEL_PATH,
    )

    REPORT_PATH.write_text(
        "Flood Prediction Model Report\n"
        "=============================\n\n"
        + "\n".join(reports)
        + f"\nBest model: {best_name} ({results[best_name] * 100:.2f}%)\n",
        encoding="utf-8",
    )

    CHART_PATH.parent.mkdir(parents=True, exist_ok=True)
    plt.figure(figsize=(8, 4.5))
    plt.bar(results.keys(), [score * 100 for score in results.values()], color=["#3b82f6", "#14b8a6", "#f59e0b", "#ef4444"])
    plt.ylabel("Accuracy (%)")
    plt.title("Flood Prediction Model Accuracy")
    plt.ylim(0, 100)
    plt.tight_layout()
    plt.savefig(CHART_PATH)
    plt.close()

    print(f"Best model: {best_name} with accuracy {results[best_name] * 100:.2f}%")
    print(f"Saved model to {MODEL_PATH}")
    return results


if __name__ == "__main__":
    train()
