from werkzeug.datastructures import ImmutableMultiDict
from flask import Flask, render_template, request

from src.predict import predict_flood


app = Flask(__name__)


SEASONS = ["winter", "pre_monsoon", "monsoon", "post_monsoon"]
DISTRICTS = [
    "Kurnool",
    "Vijayawada",
    "Rajahmundry",
    "Nellore",
    "Tirupati",
    "Anantapur",
    "Visakhapatnam",
    "Kadapa",
]


DEFAULT_VALUES = {
    "district": "Vijayawada",
    "season": "monsoon",
    "annual_rainfall_mm": 1200,
    "monsoon_rainfall_mm": 760,
    "rainfall_24h_mm": 95,
    "rainfall_7d_mm": 360,
    "cloud_visibility_km": 3.8,
    "river_level_m": 6.4,
    "humidity_percent": 84,
    "soil_moisture_percent": 72,
    "temperature_c": 28,
}


def parse_form(form: ImmutableMultiDict) -> dict:
    values = {
        "district": form.get("district", DEFAULT_VALUES["district"]),
        "season": form.get("season", DEFAULT_VALUES["season"]),
    }
    for key, default in DEFAULT_VALUES.items():
        if key in {"district", "season"}:
            continue
        values[key] = float(form.get(key, default))
    return values


@app.route("/", methods=["GET", "POST"])
def index():
    values = DEFAULT_VALUES.copy()
    result = None
    error = None

    if request.method == "POST":
        try:
            values = parse_form(request.form)
            result = predict_flood(values)
        except Exception as exc:
            error = str(exc)

    return render_template(
        "index.html",
        districts=DISTRICTS,
        seasons=SEASONS,
        values=values,
        result=result,
        error=error,
    )


if __name__ == "__main__":
    app.run(debug=True)
