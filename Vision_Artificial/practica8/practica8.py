import os
import numpy as np
import matplotlib.pyplot as plt
from PIL import Image

FILENAME = "img1.jpeg"        # Imagen a clasificar
RANDOM_SEED = 42
EPS_COV = 1e-6
MAX_PLOT_POINTS = 1000
PLOT_COLORS = ['r', 'g', 'b', 'c', 'm', 'y', 'k']

np.random.seed(RANDOM_SEED)

def clamp(v, lo, hi):
    return max(lo, min(hi, v))

def robust_cov(X):
    C = np.cov(X, rowvar=False)
    # Regulariza si está singular/mal condicionada
    if not np.isfinite(np.linalg.cond(C)) or np.linalg.det(C) == 0:
        C = C + np.eye(C.shape[0]) * EPS_COV
    return C

def mahalanobis_distance(x, mean, cov):
    diff = x - mean
    inv_cov = np.linalg.pinv(cov)
    d2 = float(diff @ inv_cov @ diff.T)
    return np.sqrt(max(d2, 0.0))

def pedir_entero_pos(msg):
    while True:
        try:
            v = int(input(msg).strip())
            if v > 0:
                return v
        except Exception:
            pass
        print("Por favor escribe un entero positivo.")

def pedir_rectangulo_interactivo(ax, color='r', clase_idx=1):
    print(f"Para la clase {clase_idx}: haz 2 clics sobre la imagen para marcar las esquinas opuestas del rectángulo.")
    pts = plt.ginput(2, timeout=-1)
    while pts is None or len(pts) < 2:
        print("No se detectaron 2 clics. Intenta de nuevo.")
        pts = plt.ginput(2, timeout=-1)

    (x1, y1), (x2, y2) = pts
    x, y = min(x1, x2), min(y1, y2)
    w, h = abs(x2 - x1), abs(y2 - y1)

    rect = plt.Rectangle((x, y), w, h, fill=False, edgecolor=color, linewidth=2)
    ax.add_patch(rect)
    ax.text(x + w / 2, y - 10, f"Clase {clase_idx}", color=color,
            fontsize=12, ha='center', va='bottom')
    plt.draw(); plt.pause(0.05)
    return (x, y, w, h)

def extraer_muestra_desde_rect(A, rect, num_reps):
    H, W, _ = A.shape
    x, y, w, h = rect
    x0 = clamp(int(round(x)), 0, W - 1)
    y0 = clamp(int(round(y)), 0, H - 1)
    x1 = clamp(int(round(x + w)), 0, W)
    y1 = clamp(int(round(y + h)), 0, H)

    if x1 <= x0 or y1 <= y0:
        print("Advertencia: rectángulo degenerado; se ajustará a 1x1 píxel.")
        x1 = clamp(x0 + 1, 0, W)
        y1 = clamp(y0 + 1, 0, H)

    subimg = A[y0:y1, x0:x1, :]
    h_sub, w_sub, _ = subimg.shape
    data = subimg.reshape(h_sub * w_sub, 3)

    if data.shape[0] > num_reps:
        idx = np.random.choice(data.shape[0], size=num_reps, replace=False)
        data = data[idx, :]
    elif data.shape[0] < num_reps:
        print(f"Advertencia: la región tiene solo {data.shape[0]} píxeles, usando todos.")
    return data.astype(float)

# =======================
#    CLASIFICADORES
# =======================
def entrenar_parametros(X, y, n_clases):
    """ Calcula medias y covarianzas por clase en X,y. """
    means, covs, counts = [], [], []
    for c in range(1, n_clases + 1):
        Xc = X[y == c]
        mu = np.mean(Xc, axis=0)
        C = robust_cov(Xc)
        means.append(mu)
        covs.append(C)
        counts.append(Xc.shape[0])
    return means, covs, counts

def predecir(x, means, covs, metodo):
    """ metodo: 'euclidiana' | 'mahalanobis' """
    dists = []
    for mu, C in zip(means, covs):
        if metodo == 'euclidiana':
            d = float(np.linalg.norm(x - mu))
        elif metodo == 'mahalanobis':
            d = mahalanobis_distance(x, mu, C)
        else:
            raise ValueError("Método no soportado.")
        dists.append(d)
    dists = np.array(dists)
    return int(np.argmin(dists)) + 1  # clases 1..K

# =======================
#      EVALUACIONES
# =======================
def accuracy(y_true, y_pred):
    return float(np.mean(y_true == y_pred)) if len(y_true) > 0 else 0.0

def resubstitution_accuracy(X, y, n_clases, metodo):
    means, covs, _ = entrenar_parametros(X, y, n_clases)
    y_hat = np.array([predecir(x, means, covs, metodo) for x in X])
    return accuracy(y, y_hat)

def stratified_kfold_splits(y, k):
    """ Devuelve lista de (train_idx, test_idx) estratificada por clase. """
    y = np.asarray(y)
    clases = np.unique(y)
    # índices por clase
    indices_por_clase = {c: np.where(y == c)[0].tolist() for c in clases}
    # barajar
    for c in clases:
        rng = np.random.default_rng(RANDOM_SEED + int(c))
        rng.shuffle(indices_por_clase[c])

    # tamaño de pliegues por clase
    folds = [([], []) for _ in range(k)]  # (train, test) se llenará luego
    # Reparto round-robin a k pliegues por clase
    for c in clases:
        idxs = indices_por_clase[c]
        for i, idx in enumerate(idxs):
            folds[i % k][1].append(idx)  # test para ese fold

    # Entrenamiento = resto
    all_indices = set(range(len(y)))
    splits = []
    for _, test_idx in folds:
        test_idx = sorted(test_idx)
        train_idx = sorted(list(all_indices.difference(test_idx)))
        splits.append((np.array(train_idx), np.array(test_idx)))
    return splits

def kfold_accuracy(X, y, n_clases, metodo, k=5):
    # Ajustar k al mínimo tamaño de clase
    clases, counts = np.unique(y, return_counts=True)
    min_count = int(np.min(counts))
    if min_count < 2:
        # Con una sola muestra por clase no hay CV estratificada viable
        return np.nan
    k = max(2, min(k, min_count))  # 2 <= k <= min_count

    folds = stratified_kfold_splits(y, k)
    accs = []
    for train_idx, test_idx in folds:
        Xtr, ytr = X[train_idx], y[train_idx]
        Xte, yte = X[test_idx], y[test_idx]
        means, covs, _ = entrenar_parametros(Xtr, ytr, n_clases)
        y_hat = np.array([predecir(x, means, covs, metodo) for x in Xte])
        accs.append(accuracy(yte, y_hat))
    return float(np.mean(accs)) if len(accs) > 0 else np.nan

def loo_accuracy(X, y, n_clases, metodo):
    N = X.shape[0]
    if N < 2:
        return np.nan
    y_hat = np.zeros(N, dtype=int)
    for i in range(N):
        mask = np.ones(N, dtype=bool)
        mask[i] = False
        Xtr, ytr = X[mask], y[mask]
        means, covs, _ = entrenar_parametros(Xtr, ytr, n_clases)
        y_hat[i] = predecir(X[i], means, covs, metodo)
    return accuracy(y, y_hat)

def evaluar_todos_los_metodos(X, y, n_clases):
    metodos = ['euclidiana', 'mahalanobis']
    resultados = {}
    for m in metodos:
        resub = resubstitution_accuracy(X, y, n_clases, m)
        kcv  = kfold_accuracy(X, y, n_clases, m, k=5)
        loo  = loo_accuracy(X, y, n_clases, m)
        # Promedio ignorando NaN si los hubiera
        vals = [v for v in [resub, kcv, loo] if np.isfinite(v)]
        mean_acc = float(np.mean(vals)) if len(vals) else np.nan
        resultados[m] = {
            'resubstitution': resub,
            'cross_validation(k=5)': kcv,
            'leave_one_out': loo,
            'promedio': mean_acc
        }
    # Reporte
    print("\n===== EVALUACIÓN DE MÉTODOS =====")
    for m, r in resultados.items():
        print(f"\nMétodo: {m.upper()}")
        print(f"  Resubstitution           : {r['resubstitution']:.4f}")
        print(f"  Cross-Validation (k=5)   : {np.nan if np.isnan(r['cross_validation(k=5)']) else r['cross_validation(k=5)']:.4f}")
        print(f"  Leave-One-Out            : {np.nan if np.isnan(r['leave_one_out']) else r['leave_one_out']:.4f}")
        print(f"  >>> Promedio (criterio)  : {np.nan if np.isnan(r['promedio']) else r['promedio']:.4f}")

    # El más eficiente = mayor promedio
    mejor = None
    mejor_acc = -1.0
    for m, r in resultados.items():
        if np.isfinite(r['promedio']) and r['promedio'] > mejor_acc:
            mejor_acc = r['promedio']; mejor = m
    if mejor is None:
        print("\nNo fue posible determinar el método más eficiente (datos insuficientes).")
    else:
        print(f"\n>>> MÉTODO MÁS EFICIENTE: {mejor.upper()} (promedio = {mejor_acc:.4f})")
    return resultados, mejor

# =======================
#          MAIN
# =======================
def main():
    # 1) Cargar imagen
    if not os.path.exists(FILENAME):
        raise FileNotFoundError(f"No se encontró '{FILENAME}'. Coloca el archivo en la misma carpeta del script.")
    A = np.array(Image.open(FILENAME).convert("RGB"))
    H, W, _ = A.shape

    # 2) Mostrar imagen
    fig1 = plt.figure("Imagen cargada")
    ax1 = plt.gca()
    ax1.imshow(A)
    ax1.set_title("Imagen cargada")
    plt.tight_layout(); plt.draw(); plt.pause(0.05)

    # 3) Parámetros
    num_clases = pedir_entero_pos("¿Cuántas clases quieres? ")
    num_reps   = pedir_entero_pos("¿Cuántos individuos (representantes) por clase (general para todas)? ")

    # 4) Selección de regiones -> dataset mezclado X,y
    classes = []
    for i in range(1, num_clases + 1):
        color = PLOT_COLORS[(i - 1) % len(PLOT_COLORS)]
        plt.figure(fig1.number); plt.sca(ax1)
        plt.title("Imagen cargada (haz 2 clics para delimitar la región)")
        rect = pedir_rectangulo_interactivo(ax1, color=color, clase_idx=i)
        data = extraer_muestra_desde_rect(A, rect, num_reps)
        classes.append(data)

    # Dataset mezclado (clases mezcladas)
    X_list, y_list = [], []
    for i, data in enumerate(classes, start=1):
        X_list.append(data)
        y_list.append(np.full((data.shape[0],), i, dtype=int))
    X = np.vstack(X_list)
    y = np.concatenate(y_list)
    # Mezclar orden global (solo estética; estratificación se maneja aparte)
    idx_perm = np.random.permutation(len(y))
    X, y = X[idx_perm], y[idx_perm]

    # 5) Nube RGB (muestra) — visual
    fig2 = plt.figure("Muestra de representantes en espacio RGB")
    ax2 = fig2.add_subplot(111, projection='3d')
    legend_entries = []
    for i, data in enumerate(classes, start=1):
        color = PLOT_COLORS[(i - 1) % len(PLOT_COLORS)]
        data_plot = data
        if data.shape[0] > MAX_PLOT_POINTS:
            idx = np.random.choice(data.shape[0], size=MAX_PLOT_POINTS, replace=False)
            data_plot = data[idx, :]
        ax2.scatter(data_plot[:, 0], data_plot[:, 1], data_plot[:, 2], s=10, c=color, marker='.')
        legend_entries.append(f"Clase {i}")
    ax2.set_xlabel("R"); ax2.set_ylabel("G"); ax2.set_zlabel("B")
    ax2.set_title("Muestra de representantes en espacio RGB")
    ax2.legend(legend_entries); ax2.grid(True)
    plt.tight_layout(); plt.draw(); plt.pause(0.05)

    # 6) Entrenar parámetros "globales" iniciales
    means_global, covs_global, _ = entrenar_parametros(X, y, num_clases)

    # 7) Evaluar eficiencia de métodos (Resubstitution, k-Fold, LOO)
    resultados, mejor_metodo = evaluar_todos_los_metodos(X, y, num_clases)

    # 8) Menú interactivo de clasificación por clic (usa el mejor método por defecto)
    try:
        metodo_map = {1: 'euclidiana', 2: 'mahalanobis'}
        while True:
            print("\nMenú:\n1) Distancia Euclidiana\n2) Distancia Mahalanobis\n3) Salir")
            try:
                metodo_sel = int(input("Elige un método (1, 2 o 3): ").strip())
            except Exception:
                print("Opción inválida."); continue

            if metodo_sel == 3:
                print("BYE"); break
            if metodo_sel not in (1, 2):
                print("Opción inválida."); continue

            metodo_nombre = metodo_map[metodo_sel]
            print(f"Usando método: {metodo_nombre.upper()}")

            while True:
                print("Seleccione el píxel desconocido (clic en la imagen).")
                plt.figure(fig1.number); plt.sca(ax1)
                plt.title("Haz clic en el píxel a clasificar (presiona Esc para abortar)")
                pts = plt.ginput(1, timeout=-1)
                if pts is None or len(pts) < 1:
                    print("No se detectó clic."); break

                x_click, y_click = pts[0]
                # --- NUEVO: si el punto cae fuera de la imagen, no pertenece a ninguna ---
                if not (0 <= x_click < W and 0 <= y_click < H):
                    print("El punto seleccionado está fuera de la imagen: no pertenece a ninguna clase.")
                    # Marca en el plot con X roja fuera de imagen (si se ve)
                    ax1.scatter([x_click], [y_click], s=100, c='r', marker='x')
                    plt.draw(); plt.pause(0.05)
                    otro = input("¿Deseas otro vector? (S/N): ").strip().upper()
                    if otro != 'S':
                        break
                    else:
                        continue

                col = clamp(int(round(x_click)), 0, W - 1)
                row = clamp(int(round(y_click)), 0, H - 1)
                c_unknown = A[row, col, :].astype(float)

                # Marcar en imagen
                ax1.scatter([x_click], [y_click], s=100, c='k', marker='*')
                ax1.text(x_click + 10, y_click, "Vector desconocido", color='k', fontsize=10)
                plt.draw(); plt.pause(0.05)

                # Marcar en RGB
                plt.figure(fig2.number)
                ax2.scatter([c_unknown[0]], [c_unknown[1]], [c_unknown[2]], s=100, c='k', marker='*')
                plt.draw(); plt.pause(0.05)

                # Clasificar con parámetros globales (de todas las muestras)
                clase_asignada = predecir(c_unknown, means_global, covs_global, metodo_nombre)
                print(f"El vector desconocido pertenece a la clase {clase_asignada}.")

                otro = input("¿Deseas otro vector? (S/N): ").strip().upper()
                if otro != 'S':
                    break
    except KeyboardInterrupt:
        print("\nInterrupción por teclado. Saliendo...")

if __name__ == "__main__":
    main()