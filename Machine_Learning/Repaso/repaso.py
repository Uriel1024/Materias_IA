import numpy as np
from sklearn.neural_network import MLPRegressor
from sklearn.datasets import load_iris, load_diabetes, load_breast_cancer
from sklearn.model_selection import train_test_split
from sklearn.metrics import accuracy_score, mean_squared_error, r2_score
import pandas as pd
import matplotlib as plt
from IPython.display import display

RANDOM_STATE = 42
data_iris = load_iris()
x, y = data_iris.data, data_iris.target

x_train, x_test, y_train, y_test = train_test_split(x,y,test_size=.2,random_state = RANDOM_STATE)

regr = MLPRegressor(random_state = RANDOM_STATE, max_iter = 1000)
regr.fit(x_train,y_train)
y_pred = regr.predict(x_test)
mse = mean_squared_error(y_test,y_pred)
r2 = r2_score(y_test,y_pred)
metrics = []
metrics.append(mse)
metrics.append(r2)

results = pd.DataFrame(metrics)
display(results)