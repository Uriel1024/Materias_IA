import math
import matplotlib.pyplot as plt

distancias_iniciales = {
    (0, 1): 4,
    (0, 2): 5,
    (0, 3): math.sqrt(10), # X1-X4
    (0, 4): 5,
    (1, 2): math.sqrt(17), # X2-X3
    (1, 3): 3,            # X2-X4 -> ¡Mínima distancia inicial!
    (1, 4): 4,
    (2, 3): math.sqrt(20),
    (2, 4): math.sqrt(13),
    (3, 4): math.sqrt(17)
}


clusters = {i: [i] for i in range(5)}
distancias = distancias_iniciales.copy()

x_posiciones = {i: i for i in range(5)}

def encontrar_par_minimo(distancias):
    min_dist = float('inf')
    par_min = None
    for (i, j), dist in distancias.items():
        if dist < min_dist:
            min_dist = dist
            par_min = (i, j)
    return par_min, min_dist

def actualizar_distancias(distancias, cl_i, cl_j, nuevo_cl):
    nueva_distancias = {}
    cl_a_borrar = {cl_i, cl_j}
    
    clusters_restantes = set()
    for (a, b) in distancias.keys():
        clusters_restantes.add(a)
        clusters_restantes.add(b)
        
    clusters_restantes = clusters_restantes - cl_a_borrar
    for k in clusters_restantes:
        cl_i_k = tuple(sorted((cl_i, k)))
        d_i_k = distancias.get(cl_i_k)

        cl_j_k = tuple(sorted((cl_j, k)))
        d_j_k = distancias.get(cl_j_k)

        nueva_clave = tuple(sorted((nuevo_cl, k)))

        dist_valida = [d for d in [d_i_k, d_j_k] if d is not None]
        if dist_valida:
            nueva_distancias[nueva_clave] = min(dist_valida)

    distancias_restantes = {}
    for (a, b), dist in distancias.items():
        if not (a in cl_a_borrar or b in cl_a_borrar):
            distancias_restantes[(a, b)] = dist

    distancias_restantes.update(nueva_distancias)
    
    return distancias_restantes

pasos_fusion = []
next_cluster_id = len(clusters) # El ID del primer clúster fusionado será 5 (índices 0-4)

dendrograma_coords = [] 

while len(clusters) > 1:
    (cl_i, cl_j), dist_fusion = encontrar_par_minimo(distancias)

    if cl_i > cl_j:
        cl_i, cl_j = cl_j, cl_i

    # 1. Realizar la Fusión
    nuevo_cl = next_cluster_id
    
    x_i = x_posiciones[cl_i]
    x_j = x_posiciones[cl_j]

    nueva_x = (x_i + x_j) / 2
    
    dendrograma_coords.append({
        'x_i': x_i,
        'x_j': x_j,
        'y': dist_fusion,
        'new_x': nueva_x
    })

    # 3. Actualizar la Estructura de Clústeres
    
    # Fusión de los puntos y eliminación de los antiguos
    puntos_i = clusters[cl_i]
    puntos_j = clusters[cl_j]
    clusters[nuevo_cl] = puntos_i + puntos_j
    del clusters[cl_i]
    del clusters[cl_j]
    
    # Actualizar posiciones X
    x_posiciones[nuevo_cl] = nueva_x
    del x_posiciones[cl_i]
    del x_posiciones[cl_j]
    
    # Guardar el paso (solo para referencia, como en el ejemplo anterior)
    pasos_fusion.append([cl_i, cl_j, dist_fusion, len(clusters[nuevo_cl])])

    # 4. Recalcular las Distancias
    distancias = actualizar_distancias(distancias, cl_i, cl_j, nuevo_cl)
    
    next_cluster_id += 1

print("Pasos de Fusión (Estructura del Dendrograma):")
for step in pasos_fusion:
    print(f"ID {step[0]} y {step[1]} se unen a distancia {step[2]:.4f}")

# --- Función de Dibujo ---

def dibujar_dendrograma_manual(coords, num_puntos, etiquetas):
    """
    Dibuja el dendrograma a partir de las coordenadas calculadas manualmente.
    """
    fig, ax = plt.subplots(figsize=(8, 5))

    current_heights = {i: 0 for i in range(num_puntos)}

    for step in coords:
        x_i = step['x_i']
        x_j = step['x_j']
        y_fusion = step['y']
        new_x = step['new_x']
        

        h_i = current_heights.get(x_i, 0)
        ax.plot([x_i, x_i], [h_i, y_fusion], 'b-')

        h_j = current_heights.get(x_j, 0)
        ax.plot([x_j, x_j], [h_j, y_fusion], 'r-')
        
        # 2. Dibujar la Barra Horizontal que conecta los clústeres
        ax.plot([x_i, x_j], [y_fusion, y_fusion], 'g-')

        ax.plot([new_x, new_x], [y_fusion, y_fusion], 'k-') 

        if x_i in current_heights: del current_heights[x_i]
        if x_j in current_heights: del current_heights[x_j]

        current_heights[new_x] = y_fusion
        
    # Configuración del gráfico
    ax.set_xticks(range(num_puntos))
    ax.set_xticklabels(etiquetas)
    ax.set_xlabel("Puntos de datos")
    ax.set_ylabel("Distancia ")
    ax.set_title("Dendrograma ")
    ax.grid(axis='y', linestyle='--')
    plt.show()


etiquetas = [f"X{i+1}" for i in range(5)] # Etiquetas X1, X2, X3, X4, X5
dibujar_dendrograma_manual(dendrograma_coords, 5, etiquetas)