import numpy as np
import matplotlib.pyplot as plt

# PROGRAMA QUE DADO UN CLASIFICADOR CON 8 CLASES TOMA UNA DECISIÓN
# TOMANDO COMO CRITERIO DE DECISIÓN A LA DISTANCIA EUCLIDEANA

# DEFINIENDO LAS CLASES
# En Python, randi([a,b], filas, columnas) se simula con np.random.randint(a, b+1, size=(filas, columnas))
# Se usa b+1 porque el límite superior en randint es exclusivo.

# Definimos 8 clases (c1 a c8), cada una con 2 filas (coordenadas x, y) y 8 columnas (8 puntos de muestra).
c1 = np.random.randint(0, 3 + 1, size=(2, 8))
c2 = np.random.randint(3, 6 + 1, size=(2, 8))
c3 = np.random.randint(6, 9 + 1, size=(2, 8))
c4 = np.random.randint(9, 12 + 1, size=(2, 8))
c5 = np.random.randint(12, 15 + 1, size=(2, 8))
c6 = np.random.randint(15, 18 + 1, size=(2, 8))
c7 = np.random.randint(18, 21 + 1, size=(2, 8))
c8 = np.random.randint(21, 24 + 1, size=(2, 8))

# Lista de todas las clases para iterar fácilmente más tarde
clases = [c1, c2, c3, c4, c5, c6, c7, c8]
colores = ['r', 'b', 'k', 'm', 'c', 'y', 'lime', 'purple'] # Colores para las 8 clases

op = 121

# Ciclo principal para ejecutar iteradamente el problema hasta que el
# usuario decida terminarlo
while op != -1:
    try:
        # Pidiendo las coordenadas al usuario
        vx = float(input('dame el valor de la coord en x='))
        vy = float(input('dame el valor de la coord en y='))
    except ValueError:
        print("Entrada no válida. Por favor, ingresa números.")
        continue
    
    # Vector de entrada (transpuesto para que sea un vector columna de 2x1)
    vector = np.array([[vx], [vy]])

    # CALCULANDO LOS PARÁMETROS DE CADA CLASE (Media/Centroide)
    # En NumPy, mean(..., axis=1) calcula la media a lo largo de las filas (promedio de cada coordenada)
    medias = [np.mean(c, axis=1, keepdims=True) for c in clases]

    # CALCULANDO LAS DISTANCIAS EUCLIDEANAS (Distancia al centroide)
    # En NumPy, la norma euclideana (distancia) se calcula con np.linalg.norm(vector)
    dist_total = []
    for media in medias:
        distancia = np.linalg.norm(media - vector)
        dist_total.append(distancia)

    dist_total = np.array(dist_total)
    print(f"\nDistancias a los centroides: {dist_total}")

    # ENCONTRANDO LA CLASE MÁS CERCANA
    minimo = np.min(dist_total)
    print(f"Distancia mínima: {minimo:.4f}")

    if minimo > 40:
        print("El vector no pertenece a ninguna clase.\n")
    else:
        # np.argmin encuentra el índice del valor mínimo
        # Le sumamos 1 porque los índices de Python empiezan en 0, pero las clases son 1 a 8.
        dato = np.argmin(dist_total) + 1 
        
        print(f'El vector desconocido pertenece a la clase {dato}\n')

        # GRAFICANDO LAS CLASES Y EL VECTOR
        plt.figure(figsize=(10, 8))
        
        # Graficando los puntos de cada clase
        etiquetas = []
        for i, c in enumerate(clases):
            plt.plot(c[0, :], c[1, :], marker='o', linestyle='', markersize=10, 
                     markerfacecolor=colores[i], markeredgecolor='black', 
                     label=f'Clase {i+1}')
            
        # Graficando el vector desconocido
        plt.plot(vector[0, 0], vector[1, 0], 'go', markersize=12, markerfacecolor='green', markeredgecolor='black', label='Vector Desconocido')

        # Graficando los centroides (opcional, pero útil)
        for i, media in enumerate(medias):
             plt.plot(media[0, 0], media[1, 0], marker='x', markersize=12, markeredgecolor='black', label=f'Centroide {i+1}')
        
        plt.grid(True)
        plt.xlabel('Coordenada X')
        plt.ylabel('Coordenada Y')
        plt.title('Clasificación por Distancia Euclidiana al Centroide')
        plt.legend(loc='best', bbox_to_anchor=(1.05, 1), borderaxespad=0.)
        plt.gca().set_aspect('equal', adjustable='box') # Asegura que la escala sea la misma en ambos ejes
        plt.show()

    # Preguntando al usuario si desea continuar
    try:
        op = int(input('Deseas seguir calculando las distancias del vector (-1 para terminar el programa): '))
    except ValueError:
        print("Entrada no válida. Terminando el programa.")
        op = -1
        
print('\nfin de proceso....')