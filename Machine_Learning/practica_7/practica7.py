from sklearn.model_selection import cross_validate, KFold, train_test_split, LeaveOneOut
from sklearn.metrics import  make_scorer, mean_squared_error, r2_score
from sklearn.datasets import load_iris, load_breast_cancer, load_wine
#no se especificio que modelo usar para la practica, por lo que se decidio usar dos modelos que ya conocemos 
from sklearn.ensemble import RandomForestRegressor
from sklearn.linear_model import LinearRegression
import matplotlib.pyplot as plt
import numpy as np
import pandas as pd
import seaborn as sns

data_wine = load_wine()
data_iris = load_iris()
data_breast_cancer = load_breast_cancer()

#cargamos los dataset
datasets = {
"iris" : data_iris, 
"breast_cancer": data_breast_cancer, 
"Wine": data_wine 
}

#funcion para optener las variables de cada dataset
def get_data(data):
	x ,y = data.data, data.target
	return x, y


def validacion_cruzada():
	#definimos la funcion de cross validation (se va a dividir el dataset en 5) 
	kf = KFold(n_splits = 5, shuffle = True, random_state = 42)

	modelos = {
	"RandomForestRegressor": RandomForestRegressor(random_state =42),
	"LinearRegression": LinearRegression()
	}

	Modelo_lista =  [] 
	Dataset= []
	MSE= []
	R2=  []
	for nombre, modelo in modelos.items():
		for n_dataset,dataset in datasets.items():
			x, y = get_data(dataset)
			scores = cross_validate(modelo, x, y, cv=kf ,scoring=('r2', 'neg_mean_squared_error'),return_train_score=True)
			mean_mse = np.mean(scores["test_neg_mean_squared_error"])
			mean_r2 = np.mean(scores["train_r2"])
			Dataset.append(n_dataset)
			MSE.append(mean_mse)
			R2.append(mean_r2)
			Modelo_lista.append(nombre)

	resultados_df = pd.DataFrame({
        "Modelo": Modelo_lista,
        "Dataset": Dataset,
        "MSE_Promedio": MSE,
        "R2_Promedio_Train": R2
    })

	graficar_resultados(resultados_df) 

def graficar_resultados(df):
    # Asegúrate de que los valores de MSE sean positivos para graficar la magnitud
    df['MSE_Positivo'] = -df['MSE_Promedio']

    # --- Gráfico de Barras para R2 Promedio de Entrenamiento ---
    plt.figure(figsize=(10, 5))
    bar_r2 = df.pivot(index='Dataset', columns='Modelo', values='R2_Promedio_Train')
    bar_r2.plot(kind='bar', ax=plt.gca())

    plt.title('R² Promedio (Train) por Modelo y Dataset')
    plt.ylabel('R² Promedio (Train)')
    plt.xticks(rotation=0)
    plt.legend(title='Modelo')
    plt.grid(axis='y', linestyle='--')
    plt.tight_layout()
    plt.show()

    plt.figure(figsize=(10, 5))
    bar_mse = df.pivot(index='Dataset', columns='Modelo', values='MSE_Positivo')
    bar_mse.plot(kind='bar', ax=plt.gca())

    plt.title('MSE Promedio (Test) por Modelo y Dataset')
    plt.ylabel('MSE Promedio (Test) - Magnitud')
    plt.xticks(rotation=0)
    plt.legend(title='Modelo')
    plt.grid(axis='y', linestyle='--')
    plt.tight_layout()
    plt.show()
    # 

#separa de manera clasica el modelo 
def validacion_70():
	modelos = {
	"RandomForestRegressor": RandomForestRegressor(random_state =42),
	"LinearRegression": LinearRegression()
	}



	rest = {"Modelo": [], "Dataset": [],"MSE": [], "R2": []}
	for nombre, modelo in modelos.items():
		for n_dataset, dataset in datasets.items():
			x,y = get_data(dataset)
			x_train, x_test, y_train, y_test = train_test_split(x,y,test_size = .3, random_state = 42)
			modelo.fit(x_train,y_train)
			y_pred = modelo.predict(x_test)
			rest["MSE"].append(mean_squared_error(y_test,y_pred))
			rest["R2"].append(r2_score(y_test,y_pred))
			rest["Modelo"].append(nombre)
			rest["Dataset"].append(n_dataset)


	resultados_df = pd.DataFrame(rest)
	graficar_resultados_1(resultados_df,"70/30")

#usa el 100 del modelo para entrenar y evaluar
def overfitting():
	modelos = {
	"RandomForestRegressor": RandomForestRegressor(random_state =42),
	"LinearRegression": LinearRegression()
	}



	rest = {"Modelo": [], "Dataset": [],"MSE": [], "R2": []}
	for nombre, modelo in modelos.items():
		for n_dataset, dataset in datasets.items():
			x,y = get_data(dataset)
			modelo.fit(x,y)
			y_pred = modelo.predict(x)
			rest["MSE"].append(mean_squared_error(y,y_pred))
			rest["R2"].append(r2_score(y,y_pred))
			rest["Modelo"].append(nombre)
			rest["Dataset"].append(n_dataset)


	resultados_df = pd.DataFrame(rest)
	graficar_resultados_1(resultados_df,"100")



def graficar_resultados_1(df,modelo):
    plt.style.use('seaborn-v0_8-whitegrid')
    
    fig, axes = plt.subplots(1, 2, figsize=(16, 6))

    datasets_unicos = df['Dataset'].unique()
    ancho_barra = 0.35
    x_pos = np.arange(len(datasets_unicos))


    df_rf = df[df['Modelo'] == 'RandomForestRegressor']

    df_lr = df[df['Modelo'] == 'LinearRegression']

    rects1 = axes[0].bar(x_pos - ancho_barra/2, df_rf['MSE'], ancho_barra, label='RandomForestRegressor', color='#1f77b4')

    rects2 = axes[0].bar(x_pos + ancho_barra/2, df_lr['MSE'], ancho_barra, label='LinearRegression', color='#ff7f0e')

    axes[0].set_title('Error Cuadrático Medio (MSE) por Modelo y Dataset', fontsize=14)
    axes[0].set_ylabel('MSE (Menor es Mejor)')
    axes[0].set_xticks(x_pos)
    axes[0].set_xticklabels(datasets_unicos, rotation=0)
    axes[0].legend(title='Modelo')


    axes[1].bar(x_pos - ancho_barra/2, df_rf['R2'], ancho_barra, label='RandomForestRegressor', color='#1f77b4')

    axes[1].bar(x_pos + ancho_barra/2, df_lr['R2'], ancho_barra, label='LinearRegression', color='#ff7f0e')

    axes[1].set_title('Coeficiente de Determinación (R²) por Modelo y Dataset', fontsize=14)
    axes[1].set_ylabel('R² Score (Cercano a 1 es Mejor)')
    axes[1].set_xticks(x_pos)
    axes[1].set_xticklabels(datasets_unicos, rotation=0)
    axes[1].legend(title='Modelo')
    axes[1].axhline(0, color='grey', linewidth=0.8, linestyle='--') # Línea en R2=0

    plt.suptitle(f'Comparación de Desempeño de Modelos (Split {modelo})', fontsize=16, y=1.02)
    plt.tight_layout(rect=[0, 0, 1, 0.98])
    plt.show()

	#leave one out 
def iguana():
	loo = LeaveOneOut()
 	
	modelos = {
	"RandomForestRegressor": RandomForestRegressor(random_state =42),
	"LinearRegression": LinearRegression()
	}
	scorers = {
        'mse': make_scorer(mean_squared_error, greater_is_better=False), # False para que mayor sea peor (error)
    }

	resultados = []
	for n_dataset,dataset in datasets.items():
		x,y = get_data(dataset)
		
		for nombre, modelo in modelos.items():
			scores = cross_validate(modelo, x, y, cv=loo, scoring=scorers, return_train_score=False)
			resultados.append({
                "Modelo": nombre,
                "Dataset": n_dataset,
                "MSE_LOO": np.mean(-scores['test_mse']), # Negado para obtener valor positivo
            })

	df_resultados = pd.DataFrame(resultados)
	graficar_resultados_iguana(df_resultados)


def graficar_resultados_iguana(df):
    plt.style.use('seaborn-v0_8-whitegrid')
    plt.figure(figsize=(10, 6))
    
    # Crear el gráfico de barras agrupado con Seaborn
    ax = sns.barplot(
        x="Dataset", 
        y="MSE_LOO", 
        hue="Modelo", 
        data=df, 
        palette="pastel"
    )
    
    # Personalizar el gráfico
    plt.title('Comparación de Modelos - MSE (Leave-One-Out Cross-Validation)', fontsize=14)
    plt.xlabel('Dataset', fontsize=12)
    plt.ylabel('MSE (Menor es Mejor)', fontsize=12)
    plt.xticks(rotation=0)
    plt.legend(title='Modelo', loc='upper right')

    # Añadir valores sobre las barras para mayor claridad
    for container in ax.containers:
        # Asegúrate de redondear los valores para que no ocupen mucho espacio
        ax.bar_label(container, fmt='%.3f', label_type='edge', padding=3)
    
    plt.tight_layout()
    plt.show()


if __name__ == '__main__':	
	#validacion_cruzada()
	#validacion_70()
	#overfitting()
	iguana()
