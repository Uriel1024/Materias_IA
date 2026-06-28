import numpy as np
import matplotlib.pyplot as plt

# PROGRAMA QUE CLASIFICA UN VECTOR RESPECTO A N CLASES (Euclidiana o Mahalanobis)

# --- 1. SOLICITAR PARÁMETROS DE CLASIFICACIÓN ---

def obtener_parametro(mensaje, validacion):
    """Función auxiliar para manejar la entrada de usuario y la validación."""
    while True:
        try:
            valor = input(mensaje)
            # Evaluar la entrada si es necesario (para números, principalmente)
            if 'num_clases' in validacion:
                 valor = int(valor)
                 if 1 <= valor <= 49:
                     return valor
                 else:
                     print('NO es posible generar más de 49 clases, por favor ingresa un número válido (1-49).')
            elif 'num_objetos' in validacion:
                valor = int(valor)
                if valor > 0:
                    return valor
                else:
                    print('El numero de objetos debe ser mayor que 0.')
            elif 'dispersión' in validacion:
                valor = float(valor)
                if valor > 0:
                    return valor
                else:
                    print('La dispersión debe ser mayor que 0.')
            elif 'centroide' in validacion or 'coordenada' in validacion:
                 return float(valor)
            elif 'modelo' in validacion:
                 valor = int(valor)
                 if valor in [1, 2]:
                     return valor
                 else:
                     print('Opción no válida, por favor elige 1 (Euclidiana) o 2 (Mahalanobis).')
            else:
                return valor

        except ValueError:
            print("Entrada no válida. Por favor, ingresa un número.")

# Solicitar el número de clases
num_cl = obtener_parametro('Ingresa el numero de clases a generar: ', 'num_clases')

# Solicitar el número de objetos por clase
num_de_objetos = obtener_parametro('Ingresa el numero de objetos por clase: ', 'num_objetos')

# Lista para guardar las clases
matrices = []
dispersiones = [] # Se usa para guardar las dispersiones (disper_x, disper_y) si son necesarias

# --- 2. GENERACIÓN DE CLASES ---

for i in range(num_cl):
    print(f"\n--- CLASE {i + 1} ---")
    
    # Solicitar la dispersión (desviación estándar) para la clase actual
    while True:
        disper_x = obtener_parametro(f'Ingresa la dispersion en x para la clase {i+1}: ', 'dispersión')
        disper_y = obtener_parametro(f'Ingresa la dispersion en y para la clase {i+1}: ', 'dispersión')
        if disper_x > 0 and disper_y > 0:
            break
        
    dispersiones.append((disper_x, disper_y))

    # Solicitar el centroide de la clase actual
    centro_x = obtener_parametro(f'Ingresa el centroide de x para la clase {i+1}: ', 'centroide')
    centro_y = obtener_parametro(f'Ingresa el centroide de y para la clase {i+1}: ', 'centroide')

    # Generación de los puntos de la clase (distribución normal)
    x_coords = centro_x + np.random.randn(1, num_de_objetos) * disper_x
    y_coords = centro_y + np.random.randn(1, num_de_objetos) * disper_y
    
    # Apilamos las coordenadas para formar una matriz 2xN
    clase = np.vstack([x_coords, y_coords])
    matrices.append(clase)

# --- 3. CICLO PRINCIPAL DE CLASIFICACIÓN ---

op = 0
while op != -1:
    print("-" * 40)
    
    # Pedimos el vector al usuario
    vx = obtener_parametro('dame el valor de la coord en x=', 'coordenada')
    vy = obtener_parametro('dame el valor de la coord en y=', 'coordenada')
    vector = np.array([[vx], [vy]])

    # Selección del modelo de distancia
    print("\nQue modelo deseas usar para calcular las distancias a las clases")
    print("1. Distancia Euclidiana.")
    print("2. Distancia de Mahalanobis.")
    modelo = obtener_parametro("Tu opcion: ", 'modelo')
    
    # Lista de centroides y distancias
    medias = []
    for clase in matrices:
        # mean(..., axis=1) calcula la media por fila y keepdims=True mantiene la forma 2x1
        medias.append(np.mean(clase, axis=1, keepdims=True))

    distancias = np.zeros(num_cl)

    if modelo == 1:
        print("\nHas escogido la distancia Euclidiana.")
        # Calcular la distancia euclidiana: np.linalg.norm(vector)
        for j in range(num_cl):
            distancias[j] = np.linalg.norm(vector - medias[j])
            
    else: # modelo == 2
        print("\nHas escogido la distancia de Mahalanobis.")
        
        # Calcular la distancia de Mahalanobis
        for i in range(num_cl):
            matriz_clase = matrices[i]
            media_clase = medias[i]
            
            # Cálculo de la Matriz de Covarianza
            # np.cov(matriz_clase) calcula la covarianza asumiendo que las observaciones son columnas (axis=1)
            # Para que funcione como MATLAB, debemos transponer la matriz primero: np.cov(matriz_clase)
            matriz_cov = np.cov(matriz_clase)
            
            # Cálculo de la Inversa de la Matriz de Covarianza
            try:
                sigma_inv = np.linalg.inv(matriz_cov)
            except np.linalg.LinAlgError:
                # Si la matriz es singular (no invertible), se usa la pseudo-inversa (Moore-Penrose)
                print(f"Advertencia: Matriz de covarianza de Clase {i+1} es singular. Usando pseudo-inversa.")
                sigma_inv = np.linalg.pinv(matriz_cov) 
            
            # Vector diferencia: (X - mu)
            diff_vector = vector - media_clase
            
            # Distancia de Mahalanobis: sqrt((X - mu)^T * Sigma^-1 * (X - mu))
            # En NumPy/Python: (diff_vector.T @ sigma_inv) @ diff_vector
            distancia_cuadrada = diff_vector.T @ sigma_inv @ diff_vector
            
            # La distancia cuadrada es un array [[valor]], se extrae el valor y se calcula la raíz
            distancias[i] = np.sqrt(distancia_cuadrada[0, 0])

    # --- 4. DETERMINAR LA CLASE MÁS CERCANA ---

    minimo = np.min(distancias)
    clase_asignada = np.argmin(distancias) + 1 
    
    # Umbral de decisión (se usa 400 como en tu código)
    UMBRAL = 250
    
    if minimo > UMBRAL:
        print(f"\nEl vector no pertenece a ninguna clase. Distancia mínima ({minimo:.2f})")
        print(f"es mayor al umbral de {UMBRAL} puntos.")
    else:
        print(f"\nEl vector pertenece a la CLASE: {clase_asignada}")
        print(f"Con una distancia de {minimo:.4f}")

    # --- 5. GRAFICACIÓN ---

    plt.figure(figsize=(10, 8))
    plt.title(f'Clasificación de vectores ({("Euclidiana" if modelo == 1 else "Mahalanobis")})')
    plt.xlabel('Eje X')
    plt.ylabel('Eje Y')
    plt.grid(True)
    
    leyendas = []
    
    # Graficar las clases y los centroides
    for i in range(num_cl):
        x_coords = matrices[i][0, :]
        y_coords = matrices[i][1, :]
        
        # Genera un color RGB aleatorio para cada clase
        color_rgb = np.random.rand(3)

        # Graficar los puntos de la clase
        plt.plot(x_coords, y_coords, 'o', 
                 color=color_rgb, 
                 markersize=8, 
                 markeredgecolor='black',
                 label=f'Clase {i + 1}')
        
        # Graficar el centroide
        plt.plot(medias[i][0, 0], medias[i][1, 0], 'X', 
                 color='k', 
                 markersize=10, 
                 markeredgewidth=2)
                 
        leyendas.append(f'Clase {i + 1}')

    # Graficar el vector desconocido
    plt.plot(vector[0, 0], vector[1, 0], 'P', 
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
        op = -1
        
print('Fin de programa, ahi nos vemos. 👋')