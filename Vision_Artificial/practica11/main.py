from PIL import Image
import numpy as np
from collections import deque
import math

def binarizar(img):
    matriz = np.array(img)
    filas = len(matriz)
    columnas = len(matriz[0])
    byn = np.zeros((filas, columnas))
    for i in range(filas):
        for j in range(columnas):
            x = 100
            x = np.float64(x)
            x += matriz[i][j][0]
            x += matriz[i][j][1]
            x += matriz[i][j][2]
            x = x // 3
            byn[i][j] = 255 if x < 240 else 0
    return byn

def componentes(binary_img):
    labeled = np.zeros_like(binary_img, dtype=np.int32)
    current_label = 1
    rows, cols = binary_img.shape
    areas = []
    
    # Direcciones de los 8 vecinos
    directions = [(-1,-1), (-1,0), (-1,1),
                  (0,-1),          (0,1),
                  (1,-1),  (1,0), (1,1)]
    
    for i in range(rows):
        for j in range(cols):
            if binary_img[i,j] == 255 and labeled[i,j] == 0:  # Píxel no etiquetado
                queue = deque()
                queue.append((i,j))
                labeled[i,j] = current_label
                current_label_size = 1 # Area
                
                while queue:
                    x, y = queue.popleft()
                    for dx, dy in directions:
                        nx, ny = x + dx, y + dy
                        if (0 <= nx < rows and 0 <= ny < cols and 
                            binary_img[nx,ny] == 255 and labeled[nx,ny] == 0):
                            labeled[nx,ny] = current_label
                            queue.append((nx,ny))
                            current_label_size += 1
                
                current_label += 1
                areas.append(current_label_size)
    return labeled, current_label - 1, areas

def getAngulo(imagen, area):
    rows, cols = imagen.shape
    sx, sy, sxx, syy, sxy = 0, 0, 0, 0, 0
    
    for i in range(rows):
        for j in range(cols):
            if imagen[i,j] == 1:
                sx += i
                sy += j
                sxx += i*i
                syy += j*j
                sxy += i*j
    mxx = sxx - sx**2//area
    myy = syy - sy**2//area
    mxy = sxy - sx*sy//area
    
    return math.atan((mxx - myy + math.sqrt((mxx - myy)**2 + 4*mxy**2)) / 2*mxy)

def filtrar(img, num):
    img2 = np.zeros_like(img)
    rows, cols = img.shape
    for i in range(rows):
        for j in range(cols):
            if img[i][j] == num:
                img2[i][j] = 1
    return img2

def calcular_momentos_espaciales(imagen, p_max=3, q_max=3):
    """
    Calcula los momentos espaciales (m_pq) hasta el orden p_max y q_max.
    
    m_pq = sum_x sum_y x^p * y^q * I(x, y)
    
    Args:
        imagen (np.array): Matriz de intensidades de la imagen (I(x, y)).
        p_max (int): Máximo orden para p.
        q_max (int): Máximo orden para q.
        
    Returns:
        dict: Un diccionario con las claves (p, q) y sus valores de momento.
    """
    momentos_espaciales = {}
    H, W = imagen.shape  # Altura (y) y Ancho (x)
    
    # Crear coordenadas x y y
    x_coords = np.arange(W)
    y_coords = np.arange(H)
    
    for p in range(p_max + 1):
        for q in range(q_max + 1):
            if p + q <= max(p_max, q_max): # Calcular hasta el orden máximo
                # Crear matrices x^p y y^q para el cálculo vectorizado
                X_p = x_coords**p
                Y_q = (y_coords**q).reshape(-1, 1)
                
                # Multiplicación elemento a elemento y suma
                # Esto es sum_x sum_y (x^p * y^q) * I(x, y)
                momento = np.sum(imagen * Y_q * X_p)
                momentos_espaciales[(p, q)] = momento
                
    return momentos_espaciales

def calcular_momentos_centrales(momentos_espaciales):
    """
    Calcula los momentos centrales (mu_pq) hasta el orden 3.
    Requiere m_00, m_10 y m_01 para el cálculo del centroide.
    """
    mu = {}
    m = momentos_espaciales
    
    # 1. Calcular el centroide (x_barra, y_barra)
    m00 = m.get((0, 0), 1e-10) # m00 es el área total, evitar división por cero
    x_barra = m.get((1, 0), 0) / m00
    y_barra = m.get((0, 1), 0) / m00
    
    # Invarianza a la traslación: mu_00 = m_00, mu_10 = 0, mu_01 = 0
    mu[(0, 0)] = m00
    mu[(1, 0)] = 0.0
    mu[(0, 1)] = 0.0
    
    # Momentos centrales de segundo orden (p+q=2)
    mu[(2, 0)] = m.get((2, 0), 0) - x_barra * m.get((1, 0), 0)
    mu[(0, 2)] = m.get((0, 2), 0) - y_barra * m.get((0, 1), 0)
    mu[(1, 1)] = m.get((1, 1), 0) - x_barra * m.get((0, 1), 0)
    
    # Momentos centrales de tercer orden (p+q=3)
    mu[(3, 0)] = m.get((3, 0), 0) - 3*x_barra*m.get((2, 0), 0) + 2*(x_barra**2)*m.get((1, 0), 0)
    mu[(1, 2)] = m.get((1, 2), 0) - 2*y_barra*m.get((1, 1), 0) - x_barra*m.get((0, 2), 0) + 2*y_barra**2 * m.get((1, 0), 0)
    mu[(2, 1)] = m.get((2, 1), 0) - 2*x_barra*m.get((1, 1), 0) - y_barra*m.get((2, 0), 0) + 2*x_barra**2 * m.get((0, 1), 0)
    mu[(0, 3)] = m.get((0, 3), 0) - 3*y_barra*m.get((0, 2), 0) + 2*(y_barra**2)*m.get((0, 1), 0)

    # Nota: Las fórmulas de mu_12 y mu_21 son más complejas de lo que se suele simplificar.
    # Aquí usamos las formas más completas basadas en el centroide y los momentos espaciales.
    # Mu_12 = m_12 - y_barra*m_11 - 2*x_barra*m_02 + 2*x_barra*y_barra*m_01 (Esta es la forma simplificada del cv2)
    # Usando la fórmula completa:
    mu_12_completo = m.get((1, 2), 0) - x_barra*m.get((0, 2), 0) - 2*y_barra*m.get((1, 1), 0) + 2*(y_barra**2)*m.get((1, 0), 0)
    mu_21_completo = m.get((2, 1), 0) - y_barra*m.get((2, 0), 0) - 2*x_barra*m.get((1, 1), 0) + 2*(x_barra**2)*m.get((0, 1), 0)

    # Reemplazamos los valores por las fórmulas más robustas que se encuentran en la literatura
    mu[(1, 2)] = mu_12_completo
    mu[(2, 1)] = mu_21_completo
    
    return mu

def calcular_momentos_normalizados(momentos_centrales):
    """
    Calcula los momentos centrales normalizados (eta_pq) hasta el orden 3.
    """
    eta = {}
    mu = momentos_centrales
    
    mu00 = mu.get((0, 0), 1e-10)
    
    for (p, q), valor in mu.items():
        if p + q >= 2: # Solo se normalizan los momentos de orden 2 y 3
            # gamma = (p + q) / 2 + 1
            # eta_pq = mu_pq / mu_00^gamma
            
            gamma = (p + q) / 2.0 + 1.0
            eta[(p, q)] = valor / (mu00 ** gamma)
            
    # Los momentos de orden 0 y 1 no se normalizan de esta manera, pero podemos incluirlos
    eta[(0, 0)] = 1.0 # eta_00 siempre es 1.0
    eta[(1, 0)] = 0.0 # eta_10 siempre es 0.0
    eta[(0, 1)] = 0.0 # eta_01 siempre es 0.0
    
    return eta

def calcular_hu_invariantes(eta):
    """
    Calcula los 7 Momentos Invariantes de Hu (Phi_i) utilizando los eta_pq.
    Devuelve los primeros 4.
    """
    eta20 = eta.get((2, 0), 0)
    eta02 = eta.get((0, 2), 0)
    eta11 = eta.get((1, 1), 0)
    eta30 = eta.get((3, 0), 0)
    eta12 = eta.get((1, 2), 0)
    eta21 = eta.get((2, 1), 0)
    eta03 = eta.get((0, 3), 0)
    
    # Primeros 4 Momentos de Hu (Rotational Invariants)
    
    # 1. Phi_1 (Invariante Simple)
    Phi1 = eta20 + eta02
    
    # 2. Phi_2 (Eje de simetría)
    Phi2 = (eta20 - eta02)**2 + 4 * eta11**2
    
    # 3. Phi_3 (Simetría Oblicua)
    Phi3 = (eta30 - 3*eta12)**2 + (3*eta21 - eta03)**2
    
    # 4. Phi_4 (Simetría Oblicua con términos cruzados)
    Phi4 = (eta30 + eta12)**2 + (eta21 + eta03)**2
    
    # Los otros momentos (Phi5, Phi6 y Phi7) son combinaciones más complejas.
    # Phi7 es el invariante torcido (skew invariant).
    return Phi1, Phi2, Phi3, Phi4
    #return [float(Phi1), float(Phi2), float(Phi3), float(   )]

def procesar(num):
    print(f'Procesando imagen {num}')
    nombre = 'imgs2/img'+str(num)+'.jpg'
    img = Image.open(nombre)
    byn = binarizar(img)

    etiquetado, num_objects, areas = componentes(byn)
    caracteristicas = []
    nombres = []
    for i in range(num_objects):
        obj = filtrar(etiquetado, i+1)
        angulo = getAngulo(obj, areas[i])
        m = calcular_momentos_espaciales(obj)
        mu = calcular_momentos_centrales(m)
        eta = calcular_momentos_normalizados(mu)
        inv1, inv2, inv3, inv4 = calcular_hu_invariantes(eta)
        print(f'\tObjeto {i+1}: area={areas[i]}, angulo={angulo}, momentos={[inv1, inv2, inv3, inv4]}')
        caracteristicas.append((inv1, inv2, inv3, inv4))
        nombres.append(f'Imagen {num}, objeto {i+1}')
    
    return caracteristicas, nombres

def kmeans(X, k, max_iters=100, tol=1e-4):
    # 1. Inicialización aleatoria de centroides
    np.random.seed(0)
    indices = np.random.choice(len(X), k, replace=False)
    centroids = X[indices]

    for _ in range(max_iters):
        # 2. Asignación: asignar cada punto al centroide más cercano
        distances = np.linalg.norm(X[:, None] - centroids, axis=2)
        labels = np.argmin(distances, axis=1)

        new_centroids = np.array([
            X[labels == i].mean(axis=0) if np.any(labels == i) else centroids[i]
            for i in range(k)
        ])

        # 3. Comprobación de convergencia
        if np.linalg.norm(new_centroids - centroids) < tol:
            break

        centroids = new_centroids

    return labels


data = []
nombres = []
for i in range(6):
    d, nom = procesar(i+1)
    data += d
    nombres += nom

data = np.array(data)
labels = kmeans(data, k=5)

def nombrar(label):
    if label == 0: return 'rectangulo'
    if label == 2: return 'flecha'
    if label == 1: return 'triangulo'
    if label == 3: return 'ciculo'
    if label == 4: return 'estrella'
etiquetas = list(map(nombrar, labels))

print("Etiquetas:")
for i in range(len(labels)):
    print(f'{nombres[i]}: clase -> {etiquetas[i]}')