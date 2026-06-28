import pandas as pd
import numpy as np
from sklearn.model_selection import train_test_split
from sklearn.metrics import accuracy_score
from sklearn.ensemble import RandomForestClassifier

RM = 42
data_fruits = pd.read_csv("validation_fruits.csv")
data_vegetables = pd.read_csv("validation_vegetables.csv")


print(data_fruits)
print(data_vegetables)




def entrenamiento():
	modelo = RandomForestClassifier(random_state = RM)


	#modelo.fit(x,y)

if __name__ == '__main__':
	entrenamiento()

