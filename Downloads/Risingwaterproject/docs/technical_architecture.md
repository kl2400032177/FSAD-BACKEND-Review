# Technical Architecture

## Objective

Build a supervised machine learning system that predicts flood risk from meteorological inputs and exposes the best model through a Flask web application.

## Modules

- `data/flood_weather_data.csv`: Historical weather dataset used for model training.
- `src/generate_dataset.py`: Creates a reproducible sample dataset for local execution.
- `src/train_model.py`: Performs preprocessing, model training, evaluation, chart generation, and model saving.
- `src/predict.py`: Loads the saved model artifact and returns prediction results.
- `app.py`: Flask application entry point.
- `templates/index.html`: User interface for entering weather readings.
- `static/style.css`: Application styling.
- `models/best_flood_model.joblib`: Saved best-performing model artifact generated after training.
- `models/model_report.txt`: Accuracy, classification report, and confusion matrix.

## Machine Learning Workflow

1. Read historical weather observations.
2. Separate input features and target label.
3. Split the dataset into training and testing subsets.
4. Scale numerical values.
5. Encode categorical fields.
6. Train Decision Tree, Random Forest, KNN, and XGBoost classifiers.
7. Compare model accuracies.
8. Save the best pipeline with metadata.
9. Use the saved artifact for web predictions.

## Input Features

- District
- Season
- Annual rainfall in millimetres
- Monsoon rainfall in millimetres
- Rainfall in last 24 hours
- Rainfall in last 7 days
- Cloud visibility in kilometres
- River level in metres
- Humidity percentage
- Soil moisture percentage
- Temperature in Celsius

## Output

- `Low Flood Risk`
- `High Flood Risk`
- Flood probability percentage
- Model name and model accuracy

## System Requirements

Hardware:

- Intel i3 processor or above
- Minimum 4 GB RAM
- Minimum 2 GB storage

Software:

- Windows, Linux, or macOS
- Python 3.8 or above
- Flask
- Scikit-learn
- XGBoost
- Pandas
- NumPy
- Matplotlib

## Future Enhancements

- Connect real-time rainfall and river gauge APIs.
- Add maps for multi-region monitoring.
- Store predictions in a database.
- Add SMS or email alerts for high-risk predictions.
- Deploy the trained system on IBM Cloud.
