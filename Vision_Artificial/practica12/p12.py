import os
import math
import numpy as np
import cv2
from tkinter import Tk, filedialog, Toplevel, Label, Frame, Button, LEFT, BOTH, X
try:
    from PIL import Image, ImageTk
except Exception:
    Image = None
    ImageTk = None
from typing import List, Tuple

# ------------------------- Configuración -------------------------
MIN_AREA_THRESHOLD = 100
NUM_SIMILAR = 5
# RANGES usados para normalizar (same as pr2)
RANGES = np.array([
    [0.35, 0.95],   # Circularidad
    [1.00, 1.80],   # Aspect Ratio
    [0.00, 0.50],   # Excentricidad
    [0.02, 0.25],   # Perim/Area
    [0.40, 1.10]    # Compacidad
], dtype=float)

# Pesos para combinar (features vs visual)
WEIGHT_FEATURES = 0.5
WEIGHT_VISUAL = 0.5
# Pesos visual (hist vs ORB)
VISUAL_HIST_WEIGHT = 0.4
VISUAL_ORB_WEIGHT = 0.6

# ------------------------- Dialog helpers -------------------------
def choose_folder(title: str = 'Seleccione la carpeta del dataset') -> str:
    root = Tk()
    root.withdraw()
    folder = filedialog.askdirectory(title=title)
    root.destroy()
    if not folder:
        raise RuntimeError('No se seleccionó carpeta.')
    return folder


def choose_file(title: str = 'Seleccione la imagen de prueba') -> str:
    root = Tk()
    root.withdraw()
    filetypes = [('Imágenes', ('*.png', '*.jpg', '*.jpeg', '*.bmp')), ('All', '*.*')]
    filename = filedialog.askopenfilename(title=title, filetypes=filetypes)
    root.destroy()
    if not filename:
        return None
    return filename

# ------------------------- Feature extraction -------------------------
def extract_5_features_from_contour(contour: np.ndarray) -> np.ndarray:
    """Dado un contorno (Nx1x2 int), devuelve el vector raw de 5 características:
    [circularityProxy, aspectRatio, Excentricity, perim/area, compacidad]
    """
    # area
    area = cv2.contourArea(contour)
    if area <= 0:
        return None
    x, y, w, h = cv2.boundingRect(contour)
    ancho = float(w)
    alto = float(h)
    areaBoundingBox = ancho * alto + 1e-9
    circularityProxy = area / areaBoundingBox

    aspectRatio = ancho / (alto + 1e-9)
    if aspectRatio < 1.0:
        aspectRatio = 1.0 / aspectRatio

    # coordenadas de puntos del contorno
    pts = contour.reshape(-1, 2)
    rows = pts[:, 1].astype(float)
    cols = pts[:, 0].astype(float)
    r_mean = rows.mean()
    c_mean = cols.mean()
    mu_20 = np.sum((rows - r_mean) ** 2)
    mu_02 = np.sum((cols - c_mean) ** 2)
    mu_11 = np.sum((rows - r_mean) * (cols - c_mean))
    Excentricity = math.sqrt((mu_20 - mu_02) ** 2 + 4 * (mu_11 ** 2)) / (area + np.finfo(float).eps)

    perimetro = float(cv2.arcLength(contour, True))
    relacion_perimetro_area = perimetro / (area + np.finfo(float).eps)

    compacidad = (4.0 * math.pi * area) / (perimetro ** 2 + np.finfo(float).eps)

    return np.array([circularityProxy, aspectRatio, Excentricity, relacion_perimetro_area, compacidad], dtype=float)

# Normalización min-max usando RANGES
def normalize_features_raw(raw: np.ndarray) -> np.ndarray:
    norm = np.zeros_like(raw)
    for f in range(5):
        lo, hi = RANGES[f]
        v = (raw[f] - lo) / (hi - lo + 1e-12)
        v = max(0.0, min(1.0, v))
        norm[f] = v
    return norm

# ------------------------- Build database -------------------------
def build_database(dataset_folder: str) -> Tuple[np.ndarray, List[str], List[np.ndarray], List[np.ndarray]]:
    """Devuelve:
      - databaseFeatures: array (N,5) de características RAW (no normalizadas)
      - fileList: lista de nombres
      - db_hists: lista de histogramas HSV (flatten) por imagen
      - db_descs: lista de descriptores ORB por imagen
    """
    files = [f for f in os.listdir(dataset_folder) if f.lower().endswith(('.png', '.jpg', '.jpeg', '.bmp'))]
    files_sorted = sorted(files)
    N = len(files_sorted)
    databaseFeatures = np.zeros((N, 5), dtype=float)
    db_hists = [None] * N
    db_descs = [None] * N

    orb = cv2.ORB_create(nfeatures=500)

    for i, fn in enumerate(files_sorted):
        path = os.path.join(dataset_folder, fn)
        img_bgr = cv2.imread(path)
        if img_bgr is None:
            print(f'Warning: no se pudo leer {path}')
            continue

        # Visual features
        hsv = cv2.cvtColor(img_bgr, cv2.COLOR_BGR2HSV)
        hist = cv2.calcHist([hsv], [0, 1], None, [50, 60], [0, 180, 0, 256])
        cv2.normalize(hist, hist)
        db_hists[i] = hist.flatten()

        gray = cv2.cvtColor(img_bgr, cv2.COLOR_BGR2GRAY)
        kp, des = orb.detectAndCompute(gray, None)
        db_descs[i] = des

        # Geometric features: segment by "paint-like" assumption (non-white pixels)
        imgf = img_bgr.astype(float) / 255.0
        mask = ((imgf[:, :, 0] < 0.95) | (imgf[:, :, 1] < 0.95) | (imgf[:, :, 2] < 0.95)).astype('uint8') * 255
        # find contours
        contours, _ = cv2.findContours(mask, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
        if not contours:
            # fallback default
            databaseFeatures[i, :] = np.array([0.78, 1.04, 0.01, 0.15, 0.85])
            continue
        # pick largest contour by area
        areas = [cv2.contourArea(c) for c in contours]
        max_idx = int(np.argmax(areas))
        if areas[max_idx] < MIN_AREA_THRESHOLD:
            databaseFeatures[i, :] = np.array([0.78, 1.04, 0.01, 0.15, 0.85])
            continue
        raw = extract_5_features_from_contour(contours[max_idx])
        if raw is None:
            databaseFeatures[i, :] = np.array([0.78, 1.04, 0.01, 0.15, 0.85])
        else:
            databaseFeatures[i, :] = raw

    return databaseFeatures, files_sorted, db_hists, db_descs

# ------------------------- Similarities -------------------------
def feature_similarity_scores(query_raw: np.ndarray, databaseFeatures: np.ndarray) -> np.ndarray:
    """Compute similarity scores in [0,1] from raw features.
    We normalize using RANGES to [0,1], then compute Euclidean distances and convert to similarities.
    """
    q_norm = normalize_features_raw(query_raw)
    db_norm = np.zeros_like(databaseFeatures)
    for i in range(databaseFeatures.shape[0]):
        db_norm[i, :] = normalize_features_raw(databaseFeatures[i, :])
    dists = np.linalg.norm(db_norm - q_norm, axis=1)
    maxd = np.max(dists) if np.max(dists) > 0 else 1.0
    sims = 1.0 - (dists / (maxd + 1e-12))
    sims = np.clip(sims, 0.0, 1.0)
    return sims


def hist_similarity(h1, h2):
    if h1 is None or h2 is None:
        return 0.0
    corr = cv2.compareHist(h1.astype('float32'), h2.astype('float32'), cv2.HISTCMP_CORREL)
    return (corr + 1.0) / 2.0


def orb_similarity(des_q, des_db):
    if des_q is None or des_db is None:
        return 0.0
    try:
        bf = cv2.BFMatcher(cv2.NORM_HAMMING, crossCheck=True)
        matches = bf.match(des_q, des_db)
        if not matches:
            return 0.0
        matches = sorted(matches, key=lambda x: x.distance)
        good = [m for m in matches if m.distance < 60]
        denom = max(1, min(len(des_q), len(des_db)))
        return min(1.0, len(good) / float(denom))
    except Exception:
        return 0.0


def visual_similarity_scores(query_path: str, db_hists: List[np.ndarray], db_descs: List[np.ndarray]) -> np.ndarray:
    q_img = cv2.imread(query_path)
    if q_img is None:
        return np.zeros(len(db_hists), dtype=float)
    hsv = cv2.cvtColor(q_img, cv2.COLOR_BGR2HSV)
    q_hist = cv2.calcHist([hsv], [0, 1], None, [50, 60], [0, 180, 0, 256])
    cv2.normalize(q_hist, q_hist)
    q_hist = q_hist.flatten()
    gray = cv2.cvtColor(q_img, cv2.COLOR_BGR2GRAY)
    orb = cv2.ORB_create(nfeatures=500)
    kpq, desq = orb.detectAndCompute(gray, None)

    scores = np.zeros(len(db_hists), dtype=float)
    for i in range(len(db_hists)):
        hs = hist_similarity(q_hist, db_hists[i]) if db_hists[i] is not None else 0.0
        osim = orb_similarity(desq, db_descs[i]) if db_descs[i] is not None else 0.0
        scores[i] = VISUAL_HIST_WEIGHT * hs + VISUAL_ORB_WEIGHT * osim
    return scores

# ------------------------- Combined ranking -------------------------
def rank_by_mode(mode: str, query_raw: np.ndarray, databaseFeatures: np.ndarray, fileList: List[str], db_hists: List[np.ndarray], db_descs: List[np.ndarray], query_image_path: str, topN: int = 5):
    mode = mode.lower()
    if mode == 'features':
        sims = feature_similarity_scores(query_raw, databaseFeatures)
        idx_sorted = np.argsort(-sims)
        return [(fileList[i], float(sims[i])) for i in idx_sorted[:topN]]
    elif mode == 'visual':
        vs = visual_similarity_scores(query_image_path, db_hists, db_descs)
        idx_sorted = np.argsort(-vs)
        return [(fileList[i], float(vs[i])) for i in idx_sorted[:topN]]
    elif mode == 'combined':
        feat_s = feature_similarity_scores(query_raw, databaseFeatures)
        vis_s = visual_similarity_scores(query_image_path, db_hists, db_descs)
        combined = WEIGHT_FEATURES * feat_s + WEIGHT_VISUAL * vis_s
        idx_sorted = np.argsort(-combined)
        return [(fileList[i], float(combined[i]), float(feat_s[i]), float(vis_s[i])) for i in idx_sorted[:topN]]
    else:
        raise ValueError('Modo desconocido: use features|visual|combined')

# ------------------------- Main interactive flow -------------------------
def main():
    print('--- Combined Image Similarity (features + visual) ---')
    dataset = choose_folder()
    print('Construyendo base de datos desde:', dataset)
    databaseFeatures, fileList, db_hists, db_descs = build_database(dataset)
    print('Base construida. Imágenes encontradas:', len(fileList))

    while True:
        query = choose_file('Seleccione la imagen consulta (Cancelar para salir)')
        if not query:
            print('Saliendo.')
            break
        # extract query features: segment and use largest object
        img_bgr = cv2.imread(query)
        if img_bgr is None:
            print('No se pudo leer la imagen de consulta.')
            continue
        imgf = img_bgr.astype(float) / 255.0
        mask = ((imgf[:, :, 0] < 0.95) | (imgf[:, :, 1] < 0.95) | (imgf[:, :, 2] < 0.95)).astype('uint8') * 255
        contours, _ = cv2.findContours(mask, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
        if not contours:
            print('No se detectaron objetos en la imagen consulta. Se usará vector por defecto.')
            query_raw = np.array([0.78, 1.04, 0.01, 0.15, 0.85], dtype=float)
        else:
            areas = [cv2.contourArea(c) for c in contours]
            max_idx = int(np.argmax(areas))
            if areas[max_idx] < MIN_AREA_THRESHOLD:
                print('Objeto más grande demasiado pequeño; usando vector por defecto.')
                query_raw = np.array([0.78, 1.04, 0.01, 0.15, 0.85], dtype=float)
            else:
                raw = extract_5_features_from_contour(contours[max_idx])
                query_raw = raw if raw is not None else np.array([0.78, 1.04, 0.01, 0.15, 0.85], dtype=float)

        #mode_in = input('Modo de similitud - elija 1) features  2) visual  3) combined  [3]: ').strip()
        mode_in = input('Iniciamos comparacion?').strip()
        # Map numeric choices (and allow textual input for backward compatibility)
        if mode_in == '':
            mode = 'combined'
        else:
            m = mode_in.strip().lower()
            if m == '1' or m == 'features':
                mode = 'features'
            elif m == '2' or m == 'visual':
                mode = 'visual'
            elif m == '3' or m == 'combined':
                mode = 'combined'
            else:
                print('Entrada inválida para modo; se usará "combined" por defecto.')
                mode = 'combined'
        topN = input(f'Cuántos resultados mostrar? [default {NUM_SIMILAR}]: ').strip()
        try:
            topN = int(topN) if topN != '' else NUM_SIMILAR
        except Exception:
            topN = NUM_SIMILAR

        results = rank_by_mode(mode, query_raw, databaseFeatures, fileList, db_hists, db_descs, query, topN=topN)

        print('\n--- Resultados ---')
        if mode == 'combined':
            print('Nombre | combined_score | feat_sim | vis_sim')
            for r in results:
                print(f"{r[0]}  | {r[1]:.4f} | {r[2]:.4f} | {r[3]:.4f}")
        else:
            print('Nombre | score')
            for r in results:
                print(f"{r[0]}  | {r[1]:.4f}")

        # Mostrar comparaciones en una interfaz (en lugar de guardarlas)
        def show_results_gui(query_path: str, dataset_folder: str, results_list, mode_str: str):
            # Si Pillow está disponible, usamos Tkinter + ImageTk para miniaturas.
            if Image is not None and ImageTk is not None:
                root = Tk()
                root.withdraw()
                win = Toplevel()
                win.title('Resultados de similitud')

                def load_thumbnail(path, max_w=240, max_h=240):
                    try:
                        img = Image.open(path).convert('RGB')
                        w, h = img.size
                        scale = min(max_w / max(w, 1), max_h / max(h, 1), 1.0)
                        nw = max(1, int(w * scale))
                        nh = max(1, int(h * scale))
                        img = img.resize((nw, nh), Image.LANCZOS)
                        return ImageTk.PhotoImage(img)
                    except Exception:
                        return None

                # Query image
                qthumb = load_thumbnail(query_path, max_w=280, max_h=280)
                if qthumb is not None:
                    lblq = Label(win, text='Consulta')
                    lblq.pack()
                    lq = Label(win, image=qthumb)
                    lq.image = qthumb
                    lq.pack(padx=6, pady=6)

                # Results row
                row = Frame(win)
                row.pack(fill=BOTH, padx=6, pady=6)
                for i, item in enumerate(results_list, start=1):
                    name = item[0]
                    score_text = ''
                    if mode_str == 'combined':
                        score_text = f"combined={item[1]:.3f}\nfeat={item[2]:.3f}\nvis={item[3]:.3f}"
                    else:
                        score_text = f"score={item[1]:.3f}"
                    path = os.path.join(dataset_folder, name)
                    t = load_thumbnail(path, max_w=200, max_h=200)
                    col = Frame(row, bd=1, relief='solid')
                    col.pack(side=LEFT, padx=4, pady=4)
                    if t is not None:
                        il = Label(col, image=t)
                        il.image = t
                        il.pack()
                    lbl = Label(col, text=f"{name}\n{score_text}")
                    lbl.pack()

                btn = Button(win, text='Cerrar', command=win.destroy)
                btn.pack(pady=8)
                win.mainloop()
            else:
                # Fallback: concatenar imágenes con OpenCV y mostrar en una ventana.
                qimg = cv2.imread(query_path)
                thumbs = []
                if qimg is None:
                    print('No se pudo abrir la imagen consulta para mostrar GUI.')
                    return
                for item in results_list:
                    name = item[0]
                    p = os.path.join(dataset_folder, name)
                    img = cv2.imread(p)
                    if img is None:
                        # placeholder blanco
                        img = np.ones_like(qimg) * 255
                    thumbs.append(img)

                h = max([qimg.shape[0]] + [t.shape[0] for t in thumbs])
                total_w = qimg.shape[1] + sum([t.shape[1] for t in thumbs])
                canvas = np.ones((h, total_w, 3), dtype=np.uint8) * 255
                x = 0
                canvas[0:qimg.shape[0], x:x+qimg.shape[1]] = qimg
                x += qimg.shape[1]
                for t in thumbs:
                    canvas[0:t.shape[0], x:x+t.shape[1]] = t
                    x += t.shape[1]
                cv2.imshow('Resultados de similitud', canvas)
                cv2.waitKey(0)
                cv2.destroyAllWindows()

        show_ui = input('¿Mostrar resultados en interfaz? (s/n) [s]: ').strip().lower()
        if show_ui == '' or show_ui == 's':
            show_results_gui(query, dataset, results, mode)
        else:
            print('No se muestran resultados.')

        cont = input('¿Desea consultar otra imagen? (s/n) [s]: ').strip().lower()
        if cont == '' or cont == 's':
            continue
        else:
            break

    print('Fin.')

if __name__ == '__main__':
    main()
