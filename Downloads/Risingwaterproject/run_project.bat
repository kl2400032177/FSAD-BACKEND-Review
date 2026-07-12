@echo off
python -m pip install -r requirements.txt
python src\generate_dataset.py
python src\train_model.py
python app.py
