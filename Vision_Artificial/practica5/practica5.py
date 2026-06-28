import numpy as np
import matplotlib.pyplot as plt

# PROGRAMA QUE CLASIFICA UN VECTOR RESPECTO A N CLASES (Clasificador por Distancia al Centroide)

# --- 1. SOLICITAR PARÁMETROS DE CLASIFICACIÓN ---

# Solicitar el número de clases
while True:
    try:
        num_cl = int(input('Ingresa el numero de clases a generar (max 49): '))
        if 1 <= num_cl <= 49:
            break
        else:
            print('NO es posible generar más de 49 clases, por favor ingresa un número válido (1-49).')
    except ValueError:
        print("Entrada no válida. Por favor, ingresa un número entero.")

# Solicitar el número de objetos por clase
while True:
    try:
        num_de_objetos = int(input('Ingresa el numero de objetos por clase: '))
        if num_de_objetos > 0:
            break
        else:
            print('El numero de objetos debe ser mayor que 0.')
    except ValueError:
        print("Entrada no válida. Por favor, ingresa un número entero.")

# Solicitar la dispersión (desviación estándar)
while True:
    try:
        disper_x = float(input('Ingresa la dispersion en x (desviación estándar): '))
        disper_y = float(input('Ingresa la dispersion en y (desviación estándar): '))
        if disper_x > 0 and disper_y > 0:
            break
        else:
            print('La dispersion debe ser mayor que 0.')
    except ValueError:
        print("Entrada no válida. Por favor, ingresa un número.")

# --- 2. GENERACIÓN DE CLASES ---

# Lista para guardar las clases (similar al 'cell' de MATLAB)
matrices = []

for i in range(num_cl):
    # Solicitar el centroide de cada clase
    while True:
        try:
            print(f'\nIngresa el centroide de x para la clase {i + 1}')
            centro_x = float(input(":"))
            print(f'Ingresa el centroide de y para la clase {i + 1}')
            centro_y = float(input(":"))
            break
        except ValueError:
            print("Centroide no válido. Por favor, ingresa un número.")

    # Generación de los puntos de la clase (distribución normal)
    # MATLAB: (centro_x + randn(1, num_de_objetos)*disper_x)
    # Python: (centro_x + np.random.randn(1, num_de_objetos) * disper_x)
    
    x_coords = centro_x + np.random.randn(1, num_de_objetos) * disper_x
    y_coords = centro_y + np.random.randn(1, num_de_objetos) * disper_y
    
    # Apilamos las coordenadas para formar una matriz 2xN
    clase = np.vstack([x_coords, y_coords])
    matrices.append(clase)

# --- 3. CICLO PRINCIPAL DE CLASIFICACIÓN ---

op = 0
while op != -1:
    print("-" * 30)
    # Pedimos el vector al usuario
    try:
        vx = float(input('dame el valor de la coord en x='))
        vy = float(input('dame el valor de la coord en y='))
    except ValueError:
        print("Coordenada no válida. Por favor, ingresa números.")
        continue

    # Vector de entrada (vector columna 2x1)
    vector = np.array([[vx], [vy]])
    
    # Lista para almacenar las medias (centroides)
    medias = []
    
    # Obtenemos la media (centroide) de cada clase
    # mean(..., axis=1) en numpy calcula la media por fila
    for clase in matrices:
        media = np.mean(clase, axis=1, keepdims=True)
        medias.append(media)

    # Calcular la distancia euclidiana de cada vector a los centroides de las clases
    distancias = np.zeros(num_cl)
    for j in range(num_cl):
        # np.linalg.norm calcula la distancia euclidiana
        distancias[j] = np.linalg.norm(vector - medias[j])

    # Determinar la clase más cercana
    minimo = np.min(distancias)
    
    # np.argmin encuentra el índice del valor mínimo. Le sumamos 1 para obtener el número de clase.
    clase_asignada = np.argmin(distancias) + 1 

    # --- 4. RESULTADOS Y GRAFICACIÓN ---

    # Umbral de decisión (se usa 800 como en tu código, aunque es un umbral muy alto)
    UMBRAL = 800 
    
    if minimo > UMBRAL:
        print(f"\nEl vector no pertenece a ninguna clase debido a que la distancia de {minimo:.2f}")
        print(f"es mayor al umbral de {UMBRAL} puntos para poder pertenecer a alguna clase.")
    else:
        print(f"\nEl vector pertenece a la CLASE: {clase_asignada}")
        print(f"Con una distancia mínima de {minimo:.4f}")
        
        # Iniciar la figura para graficar
        plt.figure(figsize=(10, 8))
        plt.title('Clasificación de vectores por clase (Distancia al Centroide)')
        plt.xlabel('Eje X')
        plt.ylabel('Eje Y')
        plt.grid(True)
        plt.hold = True # Equivalente al hold on de MATLAB

        # Graficar las clases y los centroides
        leyendas = []
        for i in range(num_cl):
            x_coords = matrices[i][0, :]
            y_coords = matrices[i][1, :]
            
            # Genera un color RGB aleatorio para cada clase (similar a rand(1,3) de MATLAB)
            color_rgb = np.random.rand(3)

            # Graficar los puntos de la clase
            plt.plot(x_coords, y_coords, 'o', 
                     color=color_rgb, 
                     markersize=8, 
                     markeredgecolor='black',
                     label=f'Clase {i + 1}')
            
            # Graficar el centroide (opcional, pero útil para visualización)
            plt.plot(medias[i][0, 0], medias[i][1, 0], 'X', 
                     color='k', 
                     markersize=10, 
                     markeredgewidth=2)
                     
            leyendas.append(f'Clase {i + 1}')

        # Graficar el vector desconocido
        plt.plot(vector[0, 0], vector[1, 0], 'P', # Se usa 'P' para un pentágono que se distingue mejor
                 markersize=12, 
                 markerfacecolor='green', 
                 markeredgecolor='black',
                 label='Vector Desconocido')
                 
        leyendas.append('Vector Desconocido')

        # Mostrar la leyenda y la gráfica
        plt.legend(loc='best')
        plt.show()

    # Preguntar al usuario si desea continuar
    try:
        op = int(input('\nDeseas continuar con el programa (-1 para salir): '))
    except ValueError:
        print("Entrada no válida. Terminando el programa.")
        op = -1

print('Fin de programa, ahi nos vemos. 👋')