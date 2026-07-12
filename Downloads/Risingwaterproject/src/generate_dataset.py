from pathlib import Path

import numpy as np
import pandas as pd


DATA_PATH = Path("data/flood_weather_data.csv")


def build_dataset(rows: int = 1200, seed: int = 42) -> pd.DataFrame:
    rng = np.random.default_rng(seed)
    districts = np.array(
        [
            "Kurnool",
            "Vijayawada",
            "Rajahmundry",
            "Nellore",
            "Tirupati",
            "Anantapur",
            "Visakhapatnam",
            "Kadapa",
        ]
    )
    seasons = np.array(["winter", "pre_monsoon", "monsoon", "post_monsoon"])

    district = rng.choice(districts, rows)
    season = rng.choice(seasons, rows, p=[0.13, 0.21, 0.49, 0.17])
    river_level_m = rng.normal(4.2, 1.9, rows).clip(0.5, 12)
    annual_rainfall_mm = rng.normal(1050, 330, rows).clip(300, 2300)
    monsoon_rainfall_mm = rng.normal(590, 260, rows).clip(50, 1600)
    rainfall_24h_mm = rng.gamma(2.2, 22, rows).clip(0, 280)
    rainfall_7d_mm = (rainfall_24h_mm * rng.uniform(2.0, 5.7, rows) + rng.normal(35, 45, rows)).clip(0, 850)
    cloud_visibility_km = rng.normal(7.2, 2.4, rows).clip(0.4, 15)
    humidity_percent = rng.normal(72, 16, rows).clip(25, 99)
    soil_moisture_percent = rng.normal(55, 19, rows).clip(8, 98)
    temperature_c = rng.normal(29, 4.5, rows).clip(17, 43)

    is_monsoon = (season == "monsoon").astype(int)
    high_rain = rainfall_24h_mm > 90
    saturated = (soil_moisture_percent > 68) & (rainfall_7d_mm > 300)
    low_visibility = cloud_visibility_km < 4.2
    river_warning = river_level_m > 6.2

    score = (
        0.026 * rainfall_24h_mm
        + 0.006 * rainfall_7d_mm
        + 0.0025 * monsoon_rainfall_mm
        + 0.34 * river_level_m
        + 0.018 * humidity_percent
        + 0.025 * soil_moisture_percent
        - 0.24 * cloud_visibility_km
        + 0.95 * is_monsoon
        + 1.1 * high_rain.astype(int)
        + 0.9 * saturated.astype(int)
        + 0.75 * low_visibility.astype(int)
        + 1.15 * river_warning.astype(int)
        + rng.normal(0, 0.95, rows)
        - 8.5
    )

    flood = (score > 0).astype(int)

    return pd.DataFrame(
        {
            "district": district,
            "season": season,
            "annual_rainfall_mm": annual_rainfall_mm.round(1),
            "monsoon_rainfall_mm": monsoon_rainfall_mm.round(1),
            "rainfall_24h_mm": rainfall_24h_mm.round(1),
            "rainfall_7d_mm": rainfall_7d_mm.round(1),
            "cloud_visibility_km": cloud_visibility_km.round(1),
            "river_level_m": river_level_m.round(2),
            "humidity_percent": humidity_percent.round(1),
            "soil_moisture_percent": soil_moisture_percent.round(1),
            "temperature_c": temperature_c.round(1),
            "flood": flood,
        }
    )


def main() -> None:
    DATA_PATH.parent.mkdir(parents=True, exist_ok=True)
    dataset = build_dataset()
    dataset.to_csv(DATA_PATH, index=False)
    print(f"Dataset created at {DATA_PATH} with {len(dataset)} rows")


if __name__ == "__main__":
    main()
