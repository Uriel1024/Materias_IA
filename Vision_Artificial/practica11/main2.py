import cv2
import numpy as np
import os # Para listar archivos en un directorio

# --- 1. Funciones para calcular momentos (copiadas y adaptadas para cv2) ---
# OpenCV ya tiene una función cv2.moments que calcula m_pq y mu_pq
# y cv2.HuMoments que usa los mu_pq para obtener los Phi_i directamente.
# No necesitamos reimplementar las funciones de momentos si usamos cv2.

def get_hu_moments_from_contour(contour):
    """
    Calcula los 7 Momentos de Hu para un contorno dado.
    Utiliza las funciones integradas de OpenCV.
    """
    # Calcula los momentos espaciales y centrales directamente de OpenCV
    # Estos son los m_pq y mu_pq
    moments = cv2.moments(contour)
    
    # Calcula los 7 Momentos de Hu a partir de los momentos centrales
    # Estos son los Phi_i
    hu_moments = cv2.HuMoments(moments).flatten()
    
    return hu_moments

# --- 2. Función de Clasificación de Figuras ---

def clasificar_figura(contour, hu_moments):
    """
    Clasifica una figura geométrica basándose en el número de vértices del polígono
    aproximado y los Momentos de Hu para formas no poligonales (círculos).
    
    Args:
        contour (np.array): El contorno de la figura.
        hu_moments (np.array): Los 7 Momentos de Hu de la figura.
        
    Returns:
        str: El nombre de la figura ("Triángulo", "Cuadrado/Rectángulo", "Círculo", "Pentágono", "Hexágono", "Otra").
    """
    
    # Calcular el área del contorno
    area = cv2.contourArea(contour)
    
    # Opcional: Ignorar contornos muy pequeños (posible ruido)
    if area < 50: # Umbral de área, ajustar según tus imágenes
        return "Ruido"

    # Aproximar el contorno a un polígono
    # epsilon es el parámetro de precisión. Un valor más pequeño = más vértices.
    # 0.04 * cv2.arcLength(contour, True) es una heurística común.
    epsilon = 0.04 * cv2.arcLength(contour, True)
    approx = cv2.approxPolyDP(contour, epsilon, True)
    
    num_vertices = len(approx)
    
    # --- Clasificación por número de vértices ---
    if num_vertices == 3:
        return "Triángulo"
    
    elif num_vertices == 4:
        # Podría ser un cuadrado o un rectángulo.
        # Podemos hacer una verificación de la relación de aspecto para diferenciarlos
        # Pero para este problema, los agruparemos.
        x, y, w, h = cv2.boundingRect(approx)
        aspect_ratio = float(w)/h
        if 0.9 <= aspect_ratio <= 1.1: # Rango de tolerancia para cuadrado
             return "Cuadrado"
        else:
             return "Rectángulo"

    elif num_vertices == 5:
        return "Pentágono"
        
    elif num_vertices == 6:
        return "Hexágono"
        
    # --- Clasificación de Círculos usando Momentos de Hu ---
    # Los Momentos de Hu son menos sensibles a los vértices y más a la forma general.
    # Para los círculos, se puede usar una combinación de num_vertices > 6-8
    # y verificar la circularidad o un umbral en el momento Hu.
    
    # Una forma de identificar círculos es viendo que no tienen pocos vértices
    # y su momento Hu se acerca al de un círculo perfecto (que tiene bajos valores)
    # y además, que tengan una alta "circularidad".
    
    # Circularidad = (4 * pi * Area) / (Perímetro^2)
    # Un círculo perfecto tiene circularidad = 1.
    
    perimeter = cv2.arcLength(contour, True)
    if perimeter == 0:
        return "Otra" # Evitar división por cero
        
    circularity = (4 * np.pi * area) / (perimeter**2)

    # Si tiene muchos vértices (no es un polígono simple) y alta circularidad
    # Y los momentos de Hu (especialmente Phi_1 y Phi_2) son pequeños,
    # es probable que sea un círculo.
    # Los valores de Hu son logarítmicos para un mejor escalado, pero aquí los comparamos directamente.
    
    # NOTA: Los umbrales para los Momentos de Hu (como hu_moments[0]) y circularidad
    # DEBEN ajustarse a tu conjunto de datos y a cómo se ven tus círculos.
    # Aquí se usan valores de ejemplo.
    if num_vertices > 6 and circularity > 0.7: # Ejemplo de umbral de circularidad para círculo
        # hu_moments[0] es Phi_1. Círculos perfectos tienen valores muy bajos en Hu.
        # Puedes entrenar un clasificador, pero para este caso, heurísticas.
        return "Círculo"
        
    return "Otra" # Cualquier otra forma no clasificada
    
# --- 3. Función Principal de Procesamiento de Imagen ---

def procesar_imagen_y_contar_figuras(imagen_path):
    """
    Carga una imagen, detecta y clasifica figuras geométricas, y las cuenta.
    
    Args:
        imagen_path (str): Ruta al archivo de imagen.
        
    Returns:
        dict: Un diccionario con el recuento de cada tipo de figura.
        np.array: La imagen con las figuras dibujadas y etiquetadas.
    """
    
    img = cv2.imread(imagen_path)
    if img is None:
        print(f"Error: No se pudo cargar la imagen {imagen_path}")
        return {}, None
        
    img_display = img.copy() # Copia para dibujar
    
    # Preprocesamiento
    gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
    
    # Aplicar suavizado (filtro Gaussiano) para reducir el ruido
    blurred = cv2.GaussianBlur(gray, (5, 5), 0)
    
    # Umbralización para obtener una imagen binaria (blanco y negro)
    # cv2.THRESH_BINARY_INV: Invierte los colores (fondo negro, objetos blancos)
    _, thresh = cv2.threshold(blurred, 127, 255, cv2.THRESH_BINARY_INV)
    
    # Detección de contornos
    # cv2.RETR_EXTERNAL: Recupera solo los contornos externos (evita contornos internos de agujeros)
    # cv2.CHAIN_APPROX_SIMPLE: Comprime segmentos horizontales, verticales y diagonales
    contours, _ = cv2.findContours(thresh, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
    
    conteo_figuras = {
        "Triángulo": 0,
        "Cuadrado": 0,
        "Rectángulo": 0,
        "Círculo": 0,
        "Pentágono": 0,
        "Hexágono": 0,
        "Otra": 0
    }
    
    # Procesar cada contorno encontrado
    for contour in contours:
        # Calcular los Momentos de Hu
        hu_moments = get_hu_moments_from_contour(contour)
        
        # Clasificar la figura
        tipo_figura = clasificar_figura(contour, hu_moments)
        
        if tipo_figura != "Ruido":
            conteo_figuras[tipo_figura] += 1
            
            # --- Visualización (opcional) ---
            # Dibujar el contorno
            cv2.drawContours(img_display, [contour], -1, (0, 255, 0), 2) # Verde
            
            # Poner el texto de la etiqueta
            # Obtener el centro del momento 0 (centroide) para colocar el texto
            M = cv2.moments(contour)
            if M["m00"] != 0:
                cX = int(M["m10"] / M["m00"])
                cY = int(M["m01"] / M["m00"])
                cv2.putText(img_display, tipo_figura, (cX - 20, cY), cv2.FONT_HERSHEY_SIMPLEX, 0.5, (255, 0, 0), 2) # Azul
            # --- Fin Visualización ---
            
    return conteo_figuras, img_display

# --- 4. Función para Procesar Múltiples Imágenes ---

def procesar_multiples_imagenes(directorio_imagenes):
    """
    Procesa todas las imágenes en un directorio dado y muestra el conteo de figuras.
    """
    
    archivos_imagen = [f for f in os.listdir(directorio_imagenes) if f.lower().endswith(('.png', '.jpg', '.jpeg', '.bmp', '.gif'))]
    
    if not archivos_imagen:
        print(f"No se encontraron imágenes en el directorio: {directorio_imagenes}")
        return
        
    resultados_totales = {}
    
    for i, archivo_nombre in enumerate(archivos_imagen):
        imagen_path = os.path.join(directorio_imagenes, archivo_nombre)
        print(f"\n--- Procesando imagen {i+1}/{len(archivos_imagen)}: {archivo_nombre} ---")
        
        conteo, img_resultado = procesar_imagen_y_contar_figuras(imagen_path)
        
        print("Conteo de figuras:")
        for figura, cantidad in conteo.items():
            if cantidad > 0:
                print(f"  {figura}: {cantidad}")
                # Sumar al total general
                resultados_totales[figura] = resultados_totales.get(figura, 0) + cantidad
        
        # Mostrar la imagen con las figuras detectadas
        # Es recomendable solo mostrar la última o guardar si son muchas
        # cv2.imshow(f"Resultados para {archivo_nombre}", img_resultado)
        # cv2.waitKey(0) # Espera a que se presione una tecla para cerrar la ventana
        # cv2.destroyAllWindows()
        
        # Si tienes muchas imágenes, puedes guardarlas en una carpeta de salida
        # cv2.imwrite(f"output/processed_{archivo_nombre}", img_resultado)
        
    print("\n--- Conteo Total de Figuras en Todas las Imágenes ---")
    for figura, cantidad in resultados_totales.items():
        print(f"  {figura}: {cantidad}")
        
    return resultados_totales

# --- Ejemplo de Uso Principal ---
if __name__ == '__main__':
    # Crear un directorio de ejemplo y algunas imágenes (si no existen)
    output_dir = "imagenes_geometricas"
    if not os.path.exists(output_dir):
        os.makedirs(output_dir)
        print(f"Directorio '{output_dir}' creado para imágenes de ejemplo.")

        # Función para crear una imagen de prueba
        def crear_imagen_prueba(filename, shapes):
            img = np.zeros((300, 300, 3), dtype=np.uint8) + 255 # Fondo blanco
            for shape_type, params in shapes:
                if shape_type == "circulo":
                    cv2.circle(img, params["center"], params["radius"], params["color"], -1)
                elif shape_type == "rectangulo":
                    cv2.rectangle(img, params["pt1"], params["pt2"], params["color"], -1)
                elif shape_type == "triangulo":
                    pts = np.array(params["pts"], np.int32)
                    pts = pts.reshape((-1, 1, 2))
                    cv2.fillPoly(img, [pts], params["color"])
                elif shape_type == "pentagono":
                    # Simple forma de dibujar un pentágono
                    center_x, center_y = params["center"]
                    radius = params["radius"]
                    angle_step = 2 * np.pi / 5
                    pts = []
                    for i in range(5):
                        x = int(center_x + radius * np.cos(i * angle_step + np.pi/2))
                        y = int(center_y + radius * np.sin(i * angle_step + np.pi/2))
                        pts.append([x, y])
                    pts = np.array(pts, np.int32).reshape((-1, 1, 2))
                    cv2.fillPoly(img, [pts], params["color"])
            cv2.imwrite(os.path.join(output_dir, filename), img)

        # Crear imágenes de ejemplo
        crear_imagen_prueba("imagen1.png", [
            ("circulo", {"center": (100, 100), "radius": 40, "color": (0, 0, 255)}),
            ("rectangulo", {"pt1": (150, 50), "pt2": (250, 120), "color": (255, 0, 0)}),
            ("triangulo", {"pts": [[50, 250], [150, 250], [100, 180]], "color": (0, 255, 0)})
        ])
        crear_imagen_prueba("imagen2.png", [
            ("circulo", {"center": (50, 50), "radius": 20, "color": (0, 0, 255)}),
            ("cuadrado", {"pt1": (180, 180), "pt2": (250, 250), "color": (255, 255, 0)}),
            ("triangulo", {"pts": [[200, 50], [250, 50], [225, 20]], "color": (0, 255, 255)}),
            ("pentagono", {"center": (100, 200), "radius": 40, "color": (255, 0, 255)})
        ])
        crear_imagen_prueba("imagen3.png", [
            ("circulo", {"center": (150, 150), "radius": 70, "color": (0, 0, 255)}),
            ("rectangulo", {"pt1": (20, 20), "pt2": (80, 60), "color": (255, 0, 0)}),
            ("cuadrado", {"pt1": (200, 20), "pt2": (250, 70), "color": (0, 255, 0)})
        ])
        print("Imágenes de ejemplo creadas.")
    
    # Directorio donde se encuentran tus imágenes
    # Asegúrate de que este directorio contenga las imágenes que quieres procesar.
    directorio_de_imagenes = output_dir 
    
    # Procesar las imágenes
    procesar_multiples_imagenes(directorio_de_imagenes)
    
    print("\nProcesamiento completado.")