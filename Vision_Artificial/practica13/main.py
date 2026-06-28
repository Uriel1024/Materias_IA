import numpy as np
import matplotlib.pyplot as plt
from matplotlib.backend_bases import MouseButton
from scipy.cluster.hierarchy import dendrogram
from matplotlib.patches import Circle
import tkinter as tk
from tkinter import filedialog
from PIL import Image

class InteractiveClusteringApp:
    def __init__(self):
        self.points = []
        self.image = None
        self.fig = None
        self.ax = None
        self.num_points = 5  # Por defecto 5 puntos
        
    def load_image(self):
        """Cargar imagen usando diálogo de archivo"""
        root = tk.Tk()
        root.withdraw()
        
        file_path = filedialog.askopenfilename(
            title="Selecciona una imagen",
            filetypes=[
                ("Imágenes", "*.png *.jpg *.jpeg *.bmp *.gif"),
                ("Todos los archivos", "*.*")
            ]
        )
        
        if file_path:
            self.image = Image.open(file_path)
            print(f"✅ Imagen cargada: {file_path}")
            return True
        else:
            print("❌ No se seleccionó ninguna imagen")
            return False
    
    def euclidean_distance(self, p1, p2):
        """Calcula la distancia euclidiana entre dos puntos"""
        return np.sqrt(np.sum((p1 - p2)**2))
    
    def calculate_distance_matrix(self, points):
        """Calcula la matriz de distancias euclidianas"""
        n = len(points)
        dist_matrix = np.zeros((n, n))
        for i in range(n):
            for j in range(n):
                if i != j:
                    dist_matrix[i, j] = self.euclidean_distance(points[i], points[j])
        return dist_matrix
    
    def complete_linkage_distance(self, cluster1_indices, cluster2_indices, dist_matrix_original):
        """Calcula la distancia de enlace completo (máxima distancia)"""
        max_dist = 0
        for i in cluster1_indices:
            for j in cluster2_indices:
                dist = dist_matrix_original[i, j]
                if dist > max_dist:
                    max_dist = dist
        return max_dist
    
    def onclick(self, event):
        """Maneja los clics del mouse para seleccionar puntos"""
        if event.inaxes != self.ax:
            return
        
        if event.button is MouseButton.LEFT and len(self.points) < self.num_points:
            x, y = event.xdata, event.ydata
            self.points.append([x, y])
            
            # Dibujar el punto
            self.ax.plot(x, y, 'ro', markersize=10, markeredgecolor='yellow', 
                        markeredgewidth=2, zorder=5)
            self.ax.text(x, y-15, f'P{len(self.points)}', fontsize=12, 
                        ha='center', color='white', fontweight='bold',
                        bbox=dict(boxstyle='round', facecolor='red', alpha=0.7))
            
            self.fig.canvas.draw()
            
            print(f"✓ Punto P{len(self.points)} seleccionado: ({x:.2f}, {y:.2f})")
            
            if len(self.points) == self.num_points:
                print(f"\n✅ Se han seleccionado todos los {self.num_points} puntos")
                print("Cierra la ventana para continuar con el análisis...")
    
    def select_points_on_image(self):
        """Permite seleccionar puntos en la imagen"""
        if self.image is None:
            print("❌ Primero debes cargar una imagen")
            return False
        
        # Preguntar cuántos puntos quiere seleccionar
        print("\n" + "="*70)
        try:
            num = input(f"¿Cuántos puntos deseas seleccionar? (por defecto {self.num_points}): ")
            if num.strip():
                self.num_points = int(num)
        except:
            print(f"Usando valor por defecto: {self.num_points} puntos")
        
        print("="*70)
        print(f"\n📍 Instrucciones:")
        print(f"  • Haz clic con el mouse en la imagen para marcar {self.num_points} puntos")
        print(f"  • Los puntos se numerarán automáticamente: P1, P2, P3, ...")
        print(f"  • Una vez seleccionados todos los puntos, cierra la ventana")
        print("="*70 + "\n")
        
        self.points = []
        
        # Crear figura y mostrar imagen
        self.fig, self.ax = plt.subplots(figsize=(12, 8))
        self.ax.imshow(self.image)
        self.ax.set_title(f'Selecciona {self.num_points} puntos en la imagen (Puntos seleccionados: 0/{self.num_points})', 
                         fontsize=14, fontweight='bold')
        self.ax.axis('on')
        
        # Conectar evento de clic
        self.fig.canvas.mpl_connect('button_press_event', 
                                   lambda event: self.onclick_with_title_update(event))
        
        plt.tight_layout()
        plt.show()
        
        if len(self.points) < self.num_points:
            print(f"\n⚠️  Solo se seleccionaron {len(self.points)} puntos de {self.num_points}")
            continuar = input("¿Deseas continuar con estos puntos? (s/n): ")
            if continuar.lower() != 's':
                return False
        
        return len(self.points) >= 2
    
    def onclick_with_title_update(self, event):
        """Versión mejorada de onclick que actualiza el título"""
        self.onclick(event)
        self.ax.set_title(f'Selecciona {self.num_points} puntos en la imagen (Puntos seleccionados: {len(self.points)}/{self.num_points})', 
                         fontsize=14, fontweight='bold')
        self.fig.canvas.draw()
    
    def perform_clustering(self):
        """Ejecuta el algoritmo de clustering jerárquico"""
        if len(self.points) < 2:
            print("❌ Se necesitan al menos 2 puntos para realizar clustering")
            return
        
        points_array = np.array(self.points)
        point_names = [f'P{i+1}' for i in range(len(self.points))]
        
        print("\n" + "="*70)
        print(" CLUSTERING JERÁRQUICO ASCENDENTE - ENLACE COMPLETO")
        print("="*70)
        
        print("\n📍 PUNTOS SELECCIONADOS:")
        for i, (name, point) in enumerate(zip(point_names, points_array)):
            print(f"  {name} = ({point[0]:.2f}, {point[1]:.2f})")
        
        # Calcular matriz de distancias
        dist_matrix = self.calculate_distance_matrix(points_array)
        
        print("\n📐 MATRIZ DE DISTANCIAS INICIAL")
        print("="*70)
        self.print_matrix(dist_matrix, point_names)
        
        # Ejecutar algoritmo de clustering
        clusters = {i: [i] for i in range(len(self.points))}
        cluster_names = {i: point_names[i] for i in range(len(self.points))}
        next_cluster_id = len(self.points)
        merge_history = []
        
        active_clusters = list(range(len(self.points)))
        current_dist_matrix = dist_matrix.copy()
        current_labels = point_names.copy()
        
        iteration = 1
        
        # Guardar en archivo TXT
        output_file = "clustering_imagen.txt"
        with open(output_file, 'w', encoding='utf-8') as f:
            f.write("="*70 + "\n")
            f.write(" CLUSTERING JERÁRQUICO - PUNTOS SELECCIONADOS EN IMAGEN\n")
            f.write("="*70 + "\n\n")
            
            f.write("PUNTOS SELECCIONADOS:\n")
            for i, (name, point) in enumerate(zip(point_names, points_array)):
                f.write(f"  {name} = ({point[0]:.2f}, {point[1]:.2f})\n")
            
            f.write("\n\nMATRIZ DE DISTANCIAS INICIAL:\n")
            f.write("-"*70 + "\n")
            self.write_matrix_to_file(f, dist_matrix, point_names)
        
        while len(active_clusters) > 1:
            print(f"\n{'='*70}")
            print(f"ITERACIÓN {iteration}")
            print(f"{'='*70}")
            
            # Encontrar distancia mínima
            n = len(active_clusters)
            min_dist = float('inf')
            min_i, min_j = -1, -1
            
            for i in range(n):
                for j in range(i+1, n):
                    if current_dist_matrix[i, j] < min_dist:
                        min_dist = current_dist_matrix[i, j]
                        min_i, min_j = i, j
            
            cluster_i = active_clusters[min_i]
            cluster_j = active_clusters[min_j]
            
            print(f"\n✓ Distancia mínima: {min_dist:.2f}")
            print(f"✓ Fusionando: {current_labels[min_i]} con {current_labels[min_j]}")
            
            # Crear nuevo cluster
            new_cluster = clusters[cluster_i] + clusters[cluster_j]
            clusters[next_cluster_id] = new_cluster
            cluster_names[next_cluster_id] = f"C{next_cluster_id}"
            
            print(f"✓ Nuevo cluster: C{next_cluster_id} = {{{', '.join([point_names[k] for k in new_cluster])}}}")
            
            merge_history.append({
                'cluster1': cluster_i,
                'cluster2': cluster_j,
                'new_cluster': next_cluster_id,
                'distance': min_dist,
                'iteration': iteration
            })
            
            # Actualizar matriz de distancias
            new_active = [c for idx, c in enumerate(active_clusters) if idx not in [min_i, min_j]]
            new_active.append(next_cluster_id)
            
            new_size = len(new_active)
            new_matrix = np.zeros((new_size, new_size))
            new_labels = []
            
            for i, c1 in enumerate(new_active):
                if c1 == next_cluster_id:
                    new_labels.append(f"C{next_cluster_id}")
                else:
                    idx = active_clusters.index(c1)
                    new_labels.append(current_labels[idx])
            
            for i in range(new_size):
                for j in range(i+1, new_size):
                    c1, c2 = new_active[i], new_active[j]
                    
                    if c1 == next_cluster_id or c2 == next_cluster_id:
                        cluster1_points = clusters[c1]
                        cluster2_points = clusters[c2]
                        dist = self.complete_linkage_distance(cluster1_points, cluster2_points, dist_matrix)
                    else:
                        idx1 = active_clusters.index(c1)
                        idx2 = active_clusters.index(c2)
                        dist = current_dist_matrix[idx1, idx2]
                    
                    new_matrix[i, j] = dist
                    new_matrix[j, i] = dist
            
            print("\n📊 MATRIZ ACTUALIZADA:")
            self.print_matrix(new_matrix, new_labels)
            
            # Guardar en archivo
            with open(output_file, 'a', encoding='utf-8') as f:
                f.write(f"\n\n{'='*70}\n")
                f.write(f"ITERACIÓN {iteration}\n")
                f.write(f"{'='*70}\n")
                f.write(f"Distancia mínima: {min_dist:.2f}\n")
                f.write(f"Clusters fusionados: {current_labels[min_i]} y {current_labels[min_j]}\n")
                f.write(f"Nuevo cluster: C{next_cluster_id}\n\n")
                f.write("Matriz actualizada:\n")
                self.write_matrix_to_file(f, new_matrix, new_labels)
            
            active_clusters = new_active
            current_dist_matrix = new_matrix
            current_labels = new_labels
            next_cluster_id += 1
            iteration += 1
        
        print(f"\n{'='*70}")
        print(f"✅ Archivo '{output_file}' generado exitosamente!")
        print("="*70)
        
        # Generar visualizaciones
        self.generate_visualizations(points_array, point_names, merge_history, clusters, cluster_names, dist_matrix)
    
    def print_matrix(self, matrix, labels):
        """Imprime matriz formateada en consola"""
        print("\n      " + "  ".join(f"{lab:>6}" for lab in labels))
        print("    " + "-" * (8 * len(labels)))
        for i, label in enumerate(labels):
            row_str = f"  {label:>4} |"
            for j in range(len(labels)):
                if i == j:
                    row_str += "   -   "
                else:
                    row_str += f" {matrix[i, j]:>5.2f} "
            print(row_str)
    
    def write_matrix_to_file(self, file, matrix, labels):
        """Escribe matriz en archivo TXT"""
        file.write("      " + "  ".join(f"{lab:>6}" for lab in labels) + "\n")
        file.write("    " + "-" * (8 * len(labels)) + "\n")
        for i, label in enumerate(labels):
            row_str = f"  {label:>4} |"
            for j in range(len(labels)):
                if i == j:
                    row_str += "   -   "
                else:
                    row_str += f" {matrix[i, j]:>5.2f} "
            file.write(row_str + "\n")
    
    def draw_nested_circles(self, ax, points_array, point_names, clusters, cluster_names, merge_history):
        """Dibuja círculos anidados representando la jerarquía de clusters"""
        
        # Calcular centro promedio de cada cluster
        cluster_centers = {}
        cluster_radii = {}
        
        # Primero calcular centros y radios para puntos individuales
        for i in range(len(points_array)):
            cluster_centers[i] = points_array[i]
            cluster_radii[i] = 20  # Radio base para puntos individuales
        
        # Calcular centros y radios para clusters compuestos
        for merge in merge_history:
            c_id = merge['new_cluster']
            points_in_cluster = [points_array[i] for i in clusters[c_id]]
            center = np.mean(points_in_cluster, axis=0)
            cluster_centers[c_id] = center
            
            # Radio basado en la distancia máxima desde el centro
            max_dist = 0
            for p in points_in_cluster:
                dist = np.linalg.norm(p - center)
                if dist > max_dist:
                    max_dist = dist
            
            # Radio del cluster más margen
            cluster_radii[c_id] = max_dist + 40 + (merge['iteration'] * 20)
        
        # Dibujar círculos de mayor a menor (del cluster final a los individuales)
        sorted_merges = sorted(merge_history, key=lambda x: x['iteration'], reverse=True)
        
        colors = plt.cm.Set3(np.linspace(0, 1, len(sorted_merges) + len(points_array)))
        color_idx = 0
        
        # Dibujar círculos de clusters compuestos
        for merge in sorted_merges:
            c_id = merge['new_cluster']
            center = cluster_centers[c_id]
            radius = cluster_radii[c_id]
            
            circle = Circle(center, radius, fill=True, 
                          facecolor=colors[color_idx], 
                          edgecolor='black', linewidth=2.5, 
                          alpha=0.3, zorder=1)
            ax.add_patch(circle)
            
            # Etiqueta del cluster
            ax.text(center[0], center[1] + radius - 15, 
                   f'C{c_id}', 
                   fontsize=12, fontweight='bold', 
                   ha='center', va='top',
                   bbox=dict(boxstyle='round,pad=0.5', 
                           facecolor='yellow', 
                           edgecolor='black', 
                           linewidth=1.5, alpha=0.9),
                   zorder=10)
            
            # Mostrar contenido del cluster
            cluster_content = ', '.join([point_names[k] for k in clusters[c_id]])
            ax.text(center[0], center[1] + radius - 40, 
                   f'{{{cluster_content}}}', 
                   fontsize=9, ha='center', va='top',
                   style='italic',
                   bbox=dict(boxstyle='round,pad=0.3', 
                           facecolor='white', 
                           alpha=0.7),
                   zorder=9)
            
            color_idx += 1
        
        # Dibujar puntos individuales encima
        for i, (point, name) in enumerate(zip(points_array, point_names)):
            ax.scatter(point[0], point[1], s=400, 
                      c=[colors[color_idx + i]], 
                      marker='o', edgecolors='black', 
                      linewidths=3, zorder=15, alpha=0.9)
            ax.text(point[0], point[1], name, 
                   fontsize=11, ha='center', va='center', 
                   fontweight='bold', color='black',
                   zorder=16)
    
    def draw_cluster_connections(self, ax, points_array, point_names, clusters, merge_history):
        """Dibuja líneas conectando puntos que pertenecen al mismo cluster con el mismo color"""
        colors = plt.cm.Set3(np.linspace(0, 1, len(merge_history) + len(points_array)))
        
        # Para cada fusión, dibujar líneas entre los puntos del cluster
        for idx, merge in enumerate(merge_history):
            cluster_points_indices = clusters[merge['new_cluster']]
            cluster_points = [points_array[i] for i in cluster_points_indices]
            cluster_point_names = [point_names[i] for i in cluster_points_indices]
            
            # Dibujar líneas entre todos los puntos del cluster
            for i in range(len(cluster_points)):
                for j in range(i+1, len(cluster_points)):
                    p1 = cluster_points[i]
                    p2 = cluster_points[j]
                    ax.plot([p1[0], p2[0]], [p1[1], p2[1]], 
                           color=colors[idx], linewidth=2.5, linestyle='-', 
                           alpha=0.7, zorder=4)
    
    def generate_visualizations(self, points_array, point_names, merge_history, clusters, cluster_names, dist_matrix):
        """Genera dendrograma y visualizaciones"""
        
        # Construir matriz de enlace para scipy
        linkage_matrix = []
        
        for idx, merge in enumerate(merge_history):
            c1 = merge['cluster1']
            c2 = merge['cluster2']
            
            linkage_matrix.append([
                c1,
                c2,
                merge['distance'],
                len(clusters[merge['new_cluster']])
            ])
        
        linkage_matrix = np.array(linkage_matrix)
        
        # Crear visualizaciones con múltiples subplots
        fig = plt.figure(figsize=(20, 12))
        gs = fig.add_gridspec(2, 2, hspace=0.3, wspace=0.25, height_ratios=[1.2, 1])
        
        # Subplot 1: Imagen con puntos y conexiones de clusters
        ax1 = fig.add_subplot(gs[0, 0])
        ax1.imshow(self.image)
        
        # Primero dibujar las líneas de conexión
        self.draw_cluster_connections(ax1, points_array, point_names, clusters, merge_history)
        
        # Luego dibujar los puntos encima
        colors = plt.cm.Set3(np.linspace(0, 1, len(merge_history) + len(points_array)))
        for i, (point, name) in enumerate(zip(points_array, point_names)):
            # Determinar el color del punto basado en su cluster final
            point_color = 'red'  # Color por defecto
            for idx, merge in enumerate(merge_history):
                if i in clusters[merge['new_cluster']]:
                    point_color = colors[idx]
                    break
            
            ax1.plot(point[0], point[1], 'o', markersize=12, 
                    color=point_color, markeredgecolor='yellow', 
                    markeredgewidth=2, zorder=5)
            ax1.text(point[0], point[1]-20, name, fontsize=11, ha='center', 
                    color='white', fontweight='bold',
                    bbox=dict(boxstyle='round', facecolor=point_color, alpha=0.8))
        
        ax1.set_title('Puntos Seleccionados en la Imagen\n(Líneas muestran clusters con mismo color)', 
                     fontsize=13, fontweight='bold')
        ax1.axis('on')
        
        # Subplot 2: Dendrograma
        ax2 = fig.add_subplot(gs[0, 1])
        
        dend = dendrogram(linkage_matrix, labels=point_names, ax=ax2,
                         color_threshold=0, above_threshold_color='#2E86AB',
                         leaf_font_size=12)
        
        ax2.set_title('Dendrograma - Clustering Jerárquico\n(Método: Enlace Completo)', 
                     fontsize=13, fontweight='bold', pad=15)
        ax2.set_xlabel('Puntos Seleccionados', fontsize=12, fontweight='bold')
        ax2.set_ylabel('Distancia Euclidiana', fontsize=12, fontweight='bold')
        ax2.grid(axis='y', alpha=0.4, linestyle='--', linewidth=0.8)
        
        # Anotar fusiones en el dendrograma
        icoord = np.array(dend['icoord'])
        dcoord = np.array(dend['dcoord'])
        
        for i, (xi, yi, merge) in enumerate(zip(icoord, dcoord, merge_history)):
            fusion_height = yi[1]
            x_center = (xi[1] + xi[2]) / 2
            
            if fusion_height > 0:
                bbox_props = dict(boxstyle='round,pad=0.5', 
                                facecolor='yellow', 
                                edgecolor='black', 
                                linewidth=1.5,
                                alpha=0.9)
                
                ax2.text(x_center, fusion_height + max(dcoord.flatten()) * 0.03, 
                        f'C{merge["new_cluster"]}\nd={fusion_height:.2f}',
                        ha='center', va='bottom', fontsize=9, 
                        fontweight='bold',
                        bbox=bbox_props,
                        zorder=10)
        
        for label in ax2.get_xticklabels():
            label.set_fontweight('bold')
            label.set_color('#A23B72')
        
        # Subplot 3: Historial de fusiones
        ax3 = fig.add_subplot(gs[1, 0])
        ax3.axis('off')
        
        tree_text = "HISTORIAL DE FUSIONES:\n" + "="*50 + "\n\n"
        
        for merge in merge_history:
            c1_name = cluster_names.get(merge['cluster1'], f"P{merge['cluster1']+1}")
            c2_name = cluster_names.get(merge['cluster2'], f"P{merge['cluster2']+1}")
            new_c_name = f"C{merge['new_cluster']}"
            dist = merge['distance']
            
            cluster_content = ', '.join([point_names[k] for k in clusters[merge['new_cluster']]])
            
            tree_text += f"Paso {merge['iteration']}:\n"
            tree_text += f"  {c1_name} + {c2_name} → {new_c_name}\n"
            tree_text += f"  Distancia: {dist:.2f}\n"
            tree_text += f"  Contiene: {{{cluster_content}}}\n\n"
        
        ax3.text(0.05, 0.95, tree_text, transform=ax3.transAxes,
                fontsize=10, verticalalignment='top', fontfamily='monospace',
                bbox=dict(boxstyle='round', facecolor='lightblue', 
                         alpha=0.8, edgecolor='black', linewidth=2))
        
        ax3.set_title('Historial de Fusiones', fontsize=13, fontweight='bold')
        
        # Subplot 4: Círculos anidados mostrando jerarquía
        ax4 = fig.add_subplot(gs[1, 1])
        
        self.draw_nested_circles(ax4, points_array, point_names, clusters, cluster_names, merge_history)
        
        ax4.set_title('Jerarquía de Clusters (Círculos Anidados)', 
                     fontsize=13, fontweight='bold')
        ax4.set_xlabel('Coordenada X (píxeles)', fontsize=11, fontweight='bold')
        ax4.set_ylabel('Coordenada Y (píxeles)', fontsize=11, fontweight='bold')
        ax4.grid(True, alpha=0.3, linestyle='--')
        ax4.invert_yaxis()
        
        # Ajustar límites del gráfico
        all_points = np.array(points_array)
        margin = 100
        ax4.set_xlim(all_points[:, 0].min() - margin, all_points[:, 0].max() + margin)
        ax4.set_ylim(all_points[:, 1].max() + margin, all_points[:, 1].min() - margin)
        
        plt.tight_layout()
        plt.savefig('clustering_resultado.png', dpi=300, bbox_inches='tight')
        print(f"✅ Gráfico 'clustering_resultado.png' generado exitosamente!")
        plt.show()

def main():
    print("="*70)
    print(" CLUSTERING JERÁRQUICO INTERACTIVO CON IMAGEN")
    print("="*70)
    
    app = InteractiveClusteringApp()
    
    # Cargar imagen
    if not app.load_image():
        print("\n❌ No se puede continuar sin una imagen")
        return
    
    # Seleccionar puntos
    if not app.select_points_on_image():
        print("\n❌ No se seleccionaron suficientes puntos")
        return
    
    # Realizar clustering
    app.perform_clustering()
    
    print("\n" + "="*70)
    print("✅ PROCESO COMPLETADO")
    print("="*70)
    print("\nArchivos generados:")
    print("  • clustering_imagen.txt - Cálculos detallados")
    print("  • clustering_resultado.png - Visualizaciones")
    print("="*70)

if __name__ == "__main__":
    main()