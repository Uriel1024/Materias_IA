import cv2
import numpy as np
import os
import random

# Definir las posibles formas y sus parámetros
def draw_circle(img, color):
    center = (random.randint(50, 250), random.randint(50, 250))
    radius = random.randint(15, 45)
    cv2.circle(img, center, radius, color, -1)

def draw_rectangle(img, color):
    # Aseguramos que la relación de aspecto no sea cercana a 1 (para ser más rectángulo que cuadrado)
    w = random.randint(60, 100)
    h = random.randint(20, 50)
    pt1_x = random.randint(50, 300 - w - 50)
    pt1_y = random.randint(50, 300 - h - 50)
    cv2.rectangle(img, (pt1_x, pt1_y), (pt1_x + w, pt1_y + h), color, -1)

def draw_square(img, color):
    # Aseguramos que la relación de aspecto sea cercana a 1
    s = random.randint(40, 70)
    pt1_x = random.randint(50, 300 - s - 50)
    pt1_y = random.randint(50, 300 - s - 50)
    cv2.rectangle(img, (pt1_x, pt1_y), (pt1_x + s, pt1_y + s), color, -1)
    
def draw_triangle(img, color):
    # Generar un triángulo con puntos aleatorios
    pts = np.array([
        (random.randint(50, 250), random.randint(50, 250)),
        (random.randint(50, 250), random.randint(50, 250)),
        (random.randint(50, 250), random.randint(50, 250))
    ], np.int32)
    pts = pts.reshape((-1, 1, 2))
    cv2.fillPoly(img, [pts], color)

# Lista de todas las funciones de dibujo de figuras
shape_drawers = [
    draw_circle, 
    draw_rectangle, 
    draw_square, 
    draw_triangle
]

# Colores al azar (BGR)
colors = [
    (0, 0, 255),    # Rojo
    (255, 0, 0),    # Azul
    (0, 255, 0),    # Verde
    (255, 255, 0),  # Amarillo
    (255, 0, 255),  # Magenta
    (0, 255, 255)   # Cian
]

# --- Función Principal para Generar el Dataset ---

def generar_dataset(num_imagenes=10, output_dir="dataset_formas"):
    """
    Genera un conjunto de imágenes con figuras geométricas aleatorias.
    
    Args:
        num_imagenes (int): Número de imágenes a generar.
        output_dir (str): Directorio donde se guardarán las imágenes.
    """
    
    # 1. Crear el directorio si no existe
    if not os.path.exists(output_dir):
        os.makedirs(output_dir)
        print(f"Directorio '{output_dir}' creado.")
    
    # 2. Generar cada imagen
    for i in range(1, num_imagenes + 1):
        # Inicializar una imagen blanca (300x300 píxeles)
        img = np.zeros((300, 300, 3), dtype=np.uint8) + 255
        
        # Elegir un número aleatorio de formas para la imagen (entre 2 y 5)
        num_shapes = random.randint(2, 5)
        
        # Dibujar las formas
        for _ in range(num_shapes):
            # Elegir una forma al azar
            drawer = random.choice(shape_drawers)
            # Elegir un color al azar
            color = random.choice(colors)
            
            # Dibujar la forma
            drawer(img, color)
            
        # 3. Guardar la imagen
        filename = f"forma_{i:02d}.png"
        filepath = os.path.join(output_dir, filename)
        cv2.imwrite(filepath, img)
        print(f"Guardada imagen: {filepath}")
        
    print(f"\nGeneración de dataset completada. Total de {num_imagenes} imágenes creadas.")

# --- Ejecución ---
if __name__ == '__main__':
    generar_dataset(num_imagenes=10)
    print("\nAhora puedes usar el código de reconocimiento de figuras, apuntándolo al directorio 'dataset_formas'.")