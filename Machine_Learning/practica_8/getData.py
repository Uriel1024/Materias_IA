import openmeteo_requests
import pandas as pd
import requests_cache
from retry_requests import retry

cache_session = requests_cache.CachedSession('.cache', expire_after = -1)
retry_session = retry(cache_session, retries = 5, backoff_factor=0.2)
openmeteo = openmeteo_requests.Client(session=retry_session)

url = "https://archive-api.open-meteo.com/v1/archive"
params = {
    "latitude": 19.4285,
    "longitude": -99.1277,
    "start_date": "2025-10-01",
    "end_date": "2025-11-10",
    "hourly": ["temperature_2m", "wind_speed_100m", "wind_direction_100m"],
    "timezone": "auto",
    "utm_source": "chatgpt.com"
}

responses = openmeteo.weather_api(url, params=params)

response = responses[0]

hourly = response.Hourly()
hourly_temperatures_2m = hourly.Variables(0).ValuesAsNumpy()
hourly_wind_speed_100m = hourly.Variables(1).ValuesAsNumpy()
hourly_wind_direction_100m = hourly.Variables(2).ValuesAsNumpy()

hourly_data = {"date": pd.date_range(
    start = pd.to_datetime(hourly.Time(), unit = "s", utc = True),
    end = pd.to_datetime(hourly.TimeEnd(), unit = "s", utc = True),
    freq = pd.Timedelta(seconds = hourly.Interval()),
    inclusive = "left"
)}

hourly_data["temperature_2m"] = hourly_temperatures_2m
hourly_data["wind_speed_100m"] = hourly_wind_speed_100m
hourly_data["wind_direction_100m"] = hourly_wind_direction_100m

hourly_dataframe = pd.DataFrame(data = hourly_data)
print("\nHourly data\n", hourly_dataframe)
hourly_dataframe.to_csv('hourly2.csv')