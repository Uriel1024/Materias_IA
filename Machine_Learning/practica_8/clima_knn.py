import pandas as pd
from datetime import datetime
from sklearn.neighbors import KNeighborsRegressor

# Cargamos el dataset
data = pd.read_csv('hourly2.csv')

# Obtenemos la hora y el dia actuales
now = datetime.now()
hora_actual = now.replace(minute = 0, second = 0, microsecond = 0)

hora_actual = hora_actual.hour
dia_actual = now.timetuple().tm_yday
target = [[dia_actual, hora_actual]]

# Obtenemos las columnas de dias y horas de la cdmx 
data['time'] = pd.to_datetime(data['date'])
data['time'] -= pd.Timedelta(hours=6)
data['day'] = data['time'].dt.day_of_year
data['hour'] = data['time'].dt.hour

# Entrenamos el modelo
y = data['temperature_2m']
x = data[['day', 'hour']].values
#print(y)
knn = KNeighborsRegressor()
knn.fit(x, y)
print(knn.predict(target))
