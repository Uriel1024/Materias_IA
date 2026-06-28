from sklearn.metrics import mean_squared_error, r2_score
from sklearn.model_selection import train_test_split
from sklearn.neighbors import KNeighborsRegressor
from sklearn.linear_model import LinearRegression
from sklearn.neural_network import MLPRegressor
import matplotlib.pyplot as plt
from sklearn.ensemble import RandomForestRegressor
import pandas as pd
import numpy as np
from datetime import datetime
from IPython.display import display
from datetime import datetime
import math
import requests

path = "https://docs.google.com/spreadsheets/d/e/2PACX-1vSCcEizuNqpQAA9U5kGNvMAT6xhX320sSxSP9idJpb0S5kfuT69xQum_b_BHUnVa6iqhK9howBHubny/pub?gid=1764788546&single=true&output=csv"

data = pd.read_csv(path)

#coordenadas de la cdmx para 
latitud = 19.4285
longitud = -99.1277

rm = 42

api_url = "https://api.open-meteo.com/v1/forecast" 


#las caracteristicas para entrenar y evaluar el modelo

data ['time'] = pd.to_datetime(data['time'])

data['day_of_year'] = data['time'].dt.day_of_year
data['hour'] = data['time'].dt.hour

features = ["day_of_year", "hour", "relative_humidity_2m (%)", "dew_point_2m (°C)", 
	"wind_speed_100m (km/h)", "wind_direction_100m (°)"]

y_column = ["temperature_2m (°C)"]

x = data[features]
y = data[y_column]
print(data)

x_train, x_test, y_train, y_test = train_test_split(x,y,test_size = .25, random_state =	 rm)

def entrenamiento():
	modelo = LinearRegression()
	#modelo = RandomForestRegressor(max_depth = 100, n_estimators = 10, random_state = rm)
	#modelo = MLPRegressor()
	#modelo = KNeighborsRegressor()
	rest = {"modelo": [], "R2": [], "MSE": []}
	modelo.fit(x_train,y_train)
	y_pred = modelo.predict(x_test)

	rest["modelo"].append("Random Forest Regressor")
	rest["R2"].append(r2_score(y_test,y_pred))
	rest["MSE"].append(mean_squared_error(y_test,y_pred))

	return rest, modelo

def obtener_hora():
	now = datetime.now()
	hora_exacta = now.replace(minute = 0, second = 0, microsecond = 0)
	if now.minute >= 30:
		hora_exacta += pd.Timedelta(hours = 1)
	return hora_exacta.strftime('%Y-%m-%dT%H:%M')


def obtener_pronostico(fecha_hora: str):
	start_date = pd.to_datetime(fecha_hora).strftime("%Y-%m-%d")

	parametros = {
		'latitude' : latitud,
		'longitude' : longitud,
		'hourly' : 'relative_humidity_2m,dew_point_2m,wind_speed_100m,wind_direction_100m',
		'timezone': 'America/Mexico_City',
		'start_date': start_date, 
		'end_date': start_date, 
	}

	try: 
		response = requests.get(api_url,params= parametros)
		response.raise_for_status()
		data = response.json()

		hourly_data = pd.DataFrame({
			'time': pd.to_datetime(data['hourly']['time']),
 			'relative_humidity_2m (%)': data['hourly']['relative_humidity_2m'],
			'dew_point_2m (°C)': data['hourly']['dew_point_2m'],
			# Agregando las variables de viento
			'wind_speed_100m (km/h)': data['hourly']['wind_speed_100m'],
			'wind_direction_100m (°)': data['hourly']['wind_direction_100m'],
		})

		target_time = pd.to_datetime(fecha_hora).replace(minute=0, second=0)
		pronostico = hourly_data[hourly_data['time'] == target_time]

		if not pronostico.empty:
				pronostico['day_of_year'] = target_time.day_of_year
				pronostico['hour'] = target_time.hour
				return pronostico[features].iloc[0].tolist()
		else:
			prin(f"Error: no se pudo obtener la info con la api.")
			return None
	except requests.exceptions.RequestException as e:
		print(f"Error al conectar con la api {e}")
		return None



if __name__ == '__main__':
	resultados, modelo = entrenamiento()
	
	print(resultados)
	dia_hora = obtener_hora()
	#print(f"\n\n {dia_hora}")
	
	caracteristicas = obtener_pronostico(dia_hora)
	
	if caracteristicas is 	not None:
		entrada = pd.DataFrame([caracteristicas], columns=features)
		prediccion = modelo.predict(entrada)[0]
		print(f"La temperatura es de {prediccion} C")
	else:
		print("No fue posible obtener la temperatura.")