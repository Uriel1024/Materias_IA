from PIL import Image
import numpy as np
from collections import deque

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
    ignorar = []
    
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
                current_label_size = 1
                
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
                ignorar.append(False)
                ignorados = 0
                if(current_label_size <= 100):
                    ignorar[current_label] = True
                    ignorados += 1
    return labeled, current_label - 1, ignorar, ignorados

def procesar(num):
    nombre = 'imgs/img'+str(num)+'.jpg'
    img = Image.open(nombre)
    byn = binarizar(img)

    _, num_objects, _, ignorados = componentes(byn)
    print(f'Objetos contados en la imagen {num}: {num_objects - ignorados}')

for i in range(50):
    procesar(i+1)