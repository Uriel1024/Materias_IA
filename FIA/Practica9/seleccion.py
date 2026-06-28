# Importamos todas las librerias para poder ejecutar el codigo 
import joblib
import pandas as pd
import matplotlib.pyplot as plt
from sklearn.feature_selection import RFE
from sklearn.model_selection import train_test_split
from sklearn.ensemble import RandomForestClassifier
from sklearn.metrics import accuracy_score, precision_score, recall_score, f1_score, roc_auc_score, confusion_matrix, ConfusionMatrixDisplay


archivo = input('Dime el nombre del archivo CSV: ')
file_name = archivo + ".csv"
dataframe = pd.read_csv(file_name)

X = dataframe.iloc[:, :-1]
y = dataframe.iloc[:, -1]

maximo = X.shape[1]
modelo = RandomForestClassifier(random_state=42)
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)


print("\nTrabajando en la eleccion...")
best_features = pd.Index([])
score = []
values = []
n_features = 0
roc = 0

for i in range(2, maximo + 1):

    selector_caracteristicas = RFE(modelo, n_features_to_select=i, step=1)
    selector_caracteristicas = selector_caracteristicas.fit(X_train, y_train)
    selected_features = X.columns[selector_caracteristicas.support_]

    X_new = dataframe.loc[:, selected_features]
    X_ntrain, X_ntest, y_train, y_test = train_test_split(X_new, y, test_size=0.2, random_state=42)
    modelo.fit(X_ntrain, y_train)
    y_pred = modelo.predict(X_ntest)

    roc_auc = roc_auc_score(y_test, y_pred)
    score.append(roc_auc)
    values.append(i)

    if roc_auc > roc:
        roc = roc_auc
        best_features = selected_features
        n_features = i


title = f"Selección de características usando RFE ({file_name})"
plt.figure(figsize=(10, 6))
plt.plot(values, score, marker='o')
plt.xlabel('Número de características seleccionadas')
plt.ylabel('ROC AUC')
plt.title(title)
plt.grid(True)
plt.show()        


print("\nMejor ROC AUC: ", roc)
message = f"\nMuestra las {n_features} características seleccionadas: \n"
print(message)
for feature in best_features:
    print(feature )


#se  vuelve a entrenar el modelo son con las mejores carateristicas 
X_best = dataframe.loc[:, best_features]
X_btrain, X_btest, y_train, y_test = train_test_split(X_best, y, test_size=0.2, random_state=42)
modelo.fit(X_btrain,y_train)
y_pred = modelo.predict(X_btest)


#se calculan todas las metricas para evaluar el rendimiento del modelo 
accuracy = accuracy_score(y_test, y_pred)
precision = precision_score(y_test, y_pred)
recall = recall_score(y_test, y_pred)
f1 = f1_score(y_test, y_pred)
conf_matrix = confusion_matrix(y_test, y_pred)
roc_auc = roc_auc_score(y_test, y_pred)



print("\nMetricas de rendimiento: \n")
print("Exactitud (Accuracy): ",accuracy )
print("Precisión (Precision):  ", precision)
print("Sensibilidad (Recall): ", recall)
print("Puntuación F1 (F1 score): ", f1)
print("ROC AUC: ", roc_auc)


print("Matriz de confusión: ")
disp = ConfusionMatrixDisplay(confusion_matrix=conf_matrix)
disp.plot(cmap=plt.cm.PuBuGn)
plt.show()



model_and_features = {
    'model': modelo,
    'feature_names': best_features
}

output_file_name = f'{archivo}cancer_random_forest_model.joblib'
joblib.dump(model_and_features, output_file_name)
