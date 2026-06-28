#importamos las librerias necesarias y los dataset 
from sklearn.cluster import KMeans, SpectralClustering, DBSCAN, Birch, AgglomerativeClustering
import pandas as pd
import numpy as np
from sklearn.datasets import load_iris, make_blobs, make_moons
import matplotlib.pyplot as plt
from sklearn.metrics import silhouette_score
from sklearn.decomposition import PCA
import seaborn as sns

RANDOM_STATE = 42

#precargamos las variables de los dataset para poder trabajarlos 
path_mall_costumers = "https://docs.google.com/spreadsheets/d/e/2PACX-1vR3-zrTL0T9NSuA9BKqrs1JR2UxLI6-faydh4eUo91In88M4DgVaogIxbHeUN0UamV0-j1S25KLbBpg/pub?gid=2104197020&single=true&output=csv"



data_iris = load_iris()
x_iris, y_iris, z_iris  = data_iris.data, data_iris.feature_names, data_iris.target


x_blobs, y_blobs = make_blobs(n_samples = 300,centers = 3,cluster_std = 1.0,random_state = RANDOM_STATE)  

x_moons, y_moons = make_moons(n_samples = 300,  random_state = RANDOM_STATE)

df_iris = pd.DataFrame(x_iris, columns=y_iris)
df_blobs = pd.DataFrame(x_blobs)
df_moons = pd.DataFrame(x_moons)



def entrenamiento(data_x, df, dataset):	
	modelos = {
		"KMeans":  KMeans(n_clusters =3, random_state = RANDOM_STATE, n_init = 'auto'),
		"SpectralClustering": SpectralClustering(n_clusters=2, assign_labels='discretize',random_state=RANDOM_STATE),
		"DBSCAN":  DBSCAN(eps=0.3, min_samples=10), 
		"Birch": Birch(n_clusters=None), 
		"AgglomerativeClustering": AgglomerativeClustering(n_clusters = 3 )
	}	

	for nombre,model in modelos.items():
		model.fit(data_x)
		df['Cluster'] = model.labels_
		silhouette_avg = silhouette_score(data_x,model.labels_)
		print(f"El silhouette_score del dataset {dataset} con el modelo {nombre} es de: {silhouette_avg:.2f}")
		pca = PCA(n_components= 2)
		reduced_data = pca.fit_transform(data_x)

		plt.figure(figsize=(8, 6))
		for cluster in range(3):
		    plt.scatter(
		        reduced_data[model.labels_ == cluster, 0],
		        reduced_data[model.labels_ == cluster, 1],
		        label=f"Cluster {cluster}"
		    )
		
		plt.title(f"{nombre} Clustering en el {dataset} Dataset (PCA Reduced)")
		plt.xlabel("Principal Component 1")
		plt.ylabel("Principal Component 2")
		plt.legend()
		plt.show()


def plot_iris(x_iris, y_iris):
	pca = PCA(n_components=2)
	# Ajustamos el modelo a nuestros datos escalados y los transformamos
	X_pca = pca.fit_transform(x_iris)

	print(f"Dimensiones de los datos despues de PCA: {X_pca.shape}")

	df_pca = pd.DataFrame(data=X_pca, columns=['Componente Principal 1', 'Componente Principal 2'])
	df_pca['Especie'] = y_iris

	#Mapeamos los números de las especies a sus nombres para la leyenda
	species_map = {0: 'Setosa', 1: 'Versicolor', 2: 'Virginica'}
	df_pca['Especie'] = df_pca['Especie'].map(species_map)

	# Creamos el gráfico de dispersión
	plt.figure(figsize=(10, 7))
	sns.scatterplot(
	    x='Componente Principal 1',
	    y='Componente Principal 2',
	    hue='Especie',
	    data=df_pca,
	    palette='viridis',
	    s=100, # Tamaño de los puntos
	    alpha=0.8 # Transparencia
	)

	plt.title('Dataset Iris reducido a 2 Dimensiones con PCA')
	plt.xlabel('Componente Principal 1')
	plt.ylabel('Componente Principal 2')
	plt.legend(title='Especie de Iris')
	plt.grid(True)
	plt.show()

def plot_blobs_moons(x, dataset):
	plt.scatter(x[:, 0], x[:, 1], s=50, c='gray', marker='o', edgecolor='k')
	plt.title(f"Informacion generada del dataset {dataset}")
	plt.xlabel("Feature 1")
	plt.ylabel("Feature 2")
	plt.show()

if __name__ == "__main__":

	#para el iris dataset
	plot_iris(x_iris,z_iris)
	entrenamiento(x_iris,df_iris,"iris")

	#para mostrar el bobs dataset 
	plot_blobs_moons(x_blobs,"blobs")
	entrenamiento(x_blobs,df_blobs,"blobs")
	#para mostrar el moon dataset
	plot_blobs_moons(x_moons,"moons")
	entrenamiento(x_moons,df_moons,"moons")

