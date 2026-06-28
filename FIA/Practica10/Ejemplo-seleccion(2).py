import joblib
import pandas as pd
import matplotlib.pyplot as plt
from sklearn.feature_selection import RFE
from sklearn.model_selection import train_test_split
from sklearn.ensemble import RandomForestClassifier
from sklearn.metrics import accuracy_score, precision_score, recall_score, f1_score, roc_auc_score, confusion_matrix, ConfusionMatrixDisplay


entered_file_name = input("Dime el nombre del archivo CSV: ")

file_name = entered_file_name + ".csv"

# Carga el conjunto de datos de entrada
dataframe = pd.read_csv(file_name)

# Carga las características o variables predictoras en X (todas las columnas menos la última)
X = dataframe.iloc[:, :-1]

# La variable objetivo 'y' (target) es la última columna
y = dataframe.iloc[:, -1]

# Obtiene el número de columnas de X
maximo = X.shape[1]

# Crea un modelo de bosque aleatorio
modelo = RandomForestClassifier(random_state=42)


# Divide el conjunto de datos en entrenamiento (80%) y pruebas (20%)
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)


# Inicio de la selección de características

print("\nTrabajando en la seleccion...")
best_features = pd.Index([])
scores=[]
values=[]
n_features = 0
roc = 0

for i in range(2, maximo+1):
    
    # Crea el selector RFE para seleccionar i características
    selector_caracteristicas = RFE(modelo, n_features_to_select=i, step=1)
   
    # Ajustar el selector al conjunto de datos
    selector_caracteristicas = selector_caracteristicas.fit(X_train, y_train)
    
    # Obtener las características seleccionadas
    selected_features = X.columns[selector_caracteristicas.support_]

    # Crea un nuevo dataframe únicamente con las características seleccionadas
    X_new = dataframe.loc[:, selected_features]

    # Divide el nuevo dataframe en 80% de entrenamiento y 20% de prueba 
    X_ntrain, X_ntest, y_train, y_test = train_test_split(X_new, y, test_size=0.2, random_state=42)

    # Ajusta el modelo 
    modelo.fit(X_ntrain,y_train)

    # Hace predicciones con el modelo ajustado 
    y_pred = modelo.predict(X_ntest)

    # Calcular ROC AUC ('y' reales vs 'y' predichas)
    roc_auc = roc_auc_score(y_test, y_pred)

    # Guarda para graficar
    scores.append(roc_auc)
    values.append(i)
    
    # Guarda el mejor ROC AUC hallado hasta el momento
    if roc_auc > roc:
        roc = roc_auc
        best_features = selected_features
        n_features = i

# Fin de la selección de características
  

# Graficar resultados
title = f"Selección de características usando RFE ({file_name})"
plt.figure(figsize=(10, 6))
plt.plot(values, scores, marker='o')
plt.xlabel('Número de características seleccionadas')
plt.ylabel('ROC AUC')
plt.title(title)
plt.grid(True)
plt.show()        
      
# Muestra la lista de características seleccionadas   
print("\nMejor ROC AUC: ", roc ) 
message = f"\nMuestra las {n_features} características seleccionadas:\n"
print(message)
for feature in best_features:
        print(feature)

# Crea un dataframe con las mejores características seleccionadas
X_best = dataframe.loc[:, best_features]

# Divide el conjunto de datos en 80% de entrenamiento y 20% de prueba 
X_btrain, X_btest, y_train, y_test = train_test_split(X_best, y, test_size=0.2, random_state=42)

# Ajusta el modelo
modelo.fit(X_btrain,y_train)

    # Hace predicciones
y_pred = modelo.predict(X_btest)

# Calcular las métricas ('y' reales vs 'y' predichas)
accuracy = accuracy_score(y_test, y_pred)
precision = precision_score(y_test, y_pred)
recall = recall_score(y_test, y_pred)
f1 = f1_score(y_test, y_pred)
conf_matrix = confusion_matrix(y_test, y_pred)
roc_auc = roc_auc_score(y_test, y_pred)

# Imprimir las métricas
print("\nMetricas de rendimiento:\n")
print("Exactitud (Accuracy):", accuracy)
print("Precisión (Precision):", precision)
print("Sensibilidad (Recall):", recall)
print("Puntuación F1 (F1 Score):", f1)
print("ROC AUC: ",roc_auc )


# Despliega la matriz de confusión
print("Matriz de confusión")
disp = ConfusionMatrixDisplay(confusion_matrix=conf_matrix)
disp.plot(cmap=plt.cm.PuBuGn)
plt.show()


# Salva el modelo entrenado para futuras aplicaciones
model_and_features = {
    'model': modelo,
    'feature_names': best_features
}

output_file_name = f"{entered_file_name}_random_forest_model.joblib"
joblib.dump(model_and_features, output_file_name)