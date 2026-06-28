import cv2
import numpy as np
import mediapipe as mp
from sklearn.ensemble import RandomForestClassifier
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import StandardScaler
from sklearn.metrics import classification_report, confusion_matrix, accuracy_score
from sklearn.pipeline import make_pipeline
import pickle
import os
from tqdm import tqdm

# ==================== PARTE 1: EXTRACCIÓN DE FEATURES ====================

class FaceFeatureExtractor:
    def __init__(self):
        self.mp_face_mesh = mp.solutions.face_mesh
        self.face_mesh = self.mp_face_mesh.FaceMesh(
            static_image_mode=True,
            max_num_faces=1,
            refine_landmarks=True,
            min_detection_confidence=0.5
        )
    
    def get_landmarks(self, image):
        """Detecta landmarks faciales en una imagen"""
        # Convertir a RGB
        rgb_image = cv2.cvtColor(image, cv2.COLOR_BGR2RGB)
        
        # Procesar con MediaPipe
        results = self.face_mesh.process(rgb_image)
        
        if results.multi_face_landmarks:
            return results.multi_face_landmarks[0]
        return None
    
    def extract_features(self, landmarks):
        """Extrae features geométricas de los landmarks"""
        if landmarks is None:
            return None
        
        # Convertir landmarks a numpy array
        points = np.array([[lm.x, lm.y, lm.z] for lm in landmarks.landmark])
        
        features = []
        
        # ===== FEATURE 1: SIMETRÍA FACIAL =====
        # Comparar mitad izquierda vs derecha
        left_half = points[:234]
        right_half = points[234:468]
        right_half_flipped = np.flip(right_half, axis=0)
        symmetry_score = 1 / (1 + np.mean(np.abs(left_half - right_half_flipped)))
        features.append(symmetry_score)
        
        # ===== FEATURE 2-5: RATIOS FACIALES =====
        # Puntos clave (índices de MediaPipe Face Mesh)
        left_eye = points[33]
        right_eye = points[263]
        nose_tip = points[1]
        left_mouth = points[61]
        right_mouth = points[291]
        chin = points[152]
        forehead = points[10]
        left_face = points[234]
        right_face = points[454]
        
        # Distancias clave
        eye_distance = np.linalg.norm(left_eye - right_eye)
        face_width = np.linalg.norm(left_face - right_face)
        face_height = np.linalg.norm(forehead - chin)
        mouth_width = np.linalg.norm(left_mouth - right_mouth)
        nose_to_chin = np.linalg.norm(nose_tip - chin)
        
        # Ratios
        features.append(eye_distance / face_width)  # Ratio ojos/cara
        features.append(face_height / face_width)   # Ratio altura/ancho
        features.append(mouth_width / face_width)   # Ratio boca/cara
        features.append(nose_to_chin / face_height) # Posición nariz
        
        # ===== FEATURE 6-8: ÁNGULOS FACIALES =====
        # Ángulo de la mandíbula
        jaw_left = points[172]
        jaw_right = points[397]
        jaw_center = points[152]
        
        vec1 = jaw_left - jaw_center
        vec2 = jaw_right - jaw_center
        jaw_angle = np.arccos(np.clip(np.dot(vec1[:2], vec2[:2]) / 
                              (np.linalg.norm(vec1[:2]) * np.linalg.norm(vec2[:2])), -1, 1))
        features.append(jaw_angle)
        
        # Ángulo de los ojos
        eye_vec = right_eye - left_eye
        eye_angle = np.arctan2(eye_vec[1], eye_vec[0])
        features.append(abs(eye_angle))  # Inclinación de ojos
        
        # Ángulo cejas-ojos
        left_eyebrow = points[70]
        eyebrow_eye_dist = np.linalg.norm(left_eyebrow - left_eye)
        features.append(eyebrow_eye_dist / face_height)
        
        # ===== FEATURE 9-12: PROPORCIONES ÁUREAS =====
        golden_ratio = 1.618
        
        # Proporción facial ideal
        third_upper = np.linalg.norm(forehead - left_eye)
        third_middle = np.linalg.norm(left_eye - nose_tip)
        third_lower = np.linalg.norm(nose_tip - chin)
        
        features.append(third_upper / (third_middle + 1e-6))
        features.append(third_middle / (third_lower + 1e-6))
        features.append(abs((face_height / face_width) - golden_ratio))
        
        # Proporción ojo-nariz
        eye_to_nose = np.linalg.norm(left_eye - nose_tip)
        features.append(eye_to_nose / face_height)
        
        # ===== FEATURE 13-15: CARACTERÍSTICAS ADICIONALES =====
        # Ancho de nariz
        nose_left = points[218]
        nose_right = points[438]
        nose_width = np.linalg.norm(nose_left - nose_right)
        features.append(nose_width / face_width)
        
        # Altura de frente
        forehead_height = np.linalg.norm(forehead - left_eye)
        features.append(forehead_height / face_height)
        
        # Profundidad facial (usando coordenada z)
        face_depth = np.std(points[:, 2])  # Variación en profundidad
        features.append(face_depth)
        
        return np.array(features)

# ==================== PARTE 2: PROCESAMIENTO ====================

def process_dataset(extractor, images, labels, desc="Processing"):
    """Procesa un conjunto de imágenes y extrae features"""
    X = []
    y = []
    
    for i, img in enumerate(tqdm(images, desc=desc)):
        try:
            # Si es path, cargar imagen
            if isinstance(img, str):
                image = cv2.imread(img)
            else:
                # Si es del dataset de HF, convertir PIL a OpenCV
                image = cv2.cvtColor(np.array(img), cv2.COLOR_RGB2BGR)
            
            if image is None:
                continue
            
            # Extraer landmarks y features
            landmarks = extractor.get_landmarks(image)
            features = extractor.extract_features(landmarks)
            
            if features is not None:
                X.append(features)
                y.append(labels[i] if isinstance(labels, list) else labels)
        
        except Exception as e:
            continue
    
    return np.array(X), np.array(y)

def train_model(X_train, y_train):
    """Entrena el modelo Random Forest"""
    print("\nEntrenando Random Forest...")
    
    # Normalizar features
    scaler = StandardScaler()
    X_train_scaled = scaler.fit_transform(X_train)
    
    # Random Forest con buenos hiperparámetros
    rf = RandomForestClassifier(
        n_estimators=150,
        max_depth=15,
        min_samples_split=10,
        min_samples_leaf=4,
        max_features='sqrt',
        random_state=42,
        n_jobs=-1,
        class_weight='balanced'
    )
    
    rf.fit(X_train_scaled, y_train)
    
    return rf, scaler

# ==================== PARTE 3: PIPELINE PRINCIPAL ====================

def main_training_pipeline(num_celebrities=1000, num_nonfamous=1000):
    """Pipeline completo de entrenamiento"""
    
    print("="*70)
    print("HOLLYWOOD FACE CLASSIFIER - TRAINING PIPELINE")
    print("="*70)
    print(f"\nUsando {num_celebrities} celebridades y {num_nonfamous} no-famosos")
    print("Para más datos, cambia los parámetros en la función\n")
    
    # 1. Inicializar extractor
    extractor = FaceFeatureExtractor()
    
    # 2. Cargar datasets desde HuggingFace
    from datasets import load_dataset
    
    print("Descargando datasets...")
    
    # Dataset de celebridades
    print("   - Celebrity dataset...")
    celeb_dataset = load_dataset("ares1123/celebrity_dataset", split='train')
    celeb_subset = celeb_dataset.shuffle(seed=42).select(range(num_celebrities))
    
    # Dataset de no-famosos (usando CelebA como alternativa)
    print("   - Non-famous dataset...")
    nonfamous_dataset = load_dataset("nielsr/CelebA-faces", split='train')
    nonfamous_subset = nonfamous_dataset.shuffle(seed=123).select(range(num_nonfamous))
    
    # 3. Procesar celebridades (label = 1)
    celeb_images = [celeb_subset[i]['image'] for i in range(len(celeb_subset))]
    celeb_labels = [1] * len(celeb_images)
    X_celeb, y_celeb = process_dataset(extractor, celeb_images, celeb_labels, 
                                       desc="Procesando celebridades")
    
    # 4. Procesar no-famosos (label = 0)
    nonfamous_images = [nonfamous_subset[i]['image'] for i in range(len(nonfamous_subset))]
    X_nonfamous, y_nonfamous = process_dataset(extractor, nonfamous_images, 0, 
                                               desc="Procesando no-famosos")
    
    # 5. Combinar datasets
    X = np.vstack([X_celeb, X_nonfamous])
    y = np.concatenate([y_celeb, y_nonfamous])
    
    print(f"   Dataset procesado:")
    print(f"   Total imágenes: {X.shape[0]}")
    print(f"   Features por imagen: {X.shape[1]}")
    print(f"   Famosos: {np.sum(y==1)}")
    print(f"   No famosos: {np.sum(y==0)}")
    
    # 6. Split train/test
    X_train, X_test, y_train, y_test = train_test_split(
        X, y, test_size=0.2, random_state=42, stratify=y
    )
    
    # 7. Entrenar modelo
    rf, scaler = train_model(X_train, y_train)
    
    # 8. Evaluar
    print("\n" + "="*70)
    print(" EVALUACIÓN DEL MODELO")
    print("="*70)
    
    X_train_scaled = scaler.transform(X_train)
    X_test_scaled = scaler.transform(X_test)
    
    train_acc = rf.score(X_train_scaled, y_train)
    test_acc = rf.score(X_test_scaled, y_test)
    
    print(f"\n✓ Train Accuracy: {train_acc*100:.2f}%")
    print(f"✓ Test Accuracy:  {test_acc*100:.2f}%")
    print(f"  Overfitting:    {(train_acc-test_acc)*100:.2f}%", end="")
    
    if (train_acc - test_acc) < 0.15:
        print(" (Bueno)")
    else:
        print(" (Alto)")
    
    # Predicciones
    y_pred = rf.predict(X_test_scaled)
    
    print("\n--- Classification Report ---")
    print(classification_report(y_test, y_pred, 
                                target_names=['No Famoso', 'Famoso']))
    
    print("\n--- Confusion Matrix ---")
    cm = confusion_matrix(y_test, y_pred)
    print(f"              Pred: No Famoso  Pred: Famoso")
    print(f"Real: No Famoso      {cm[0][0]:6d}         {cm[0][1]:6d}")
    print(f"Real: Famoso         {cm[1][0]:6d}         {cm[1][1]:6d}")
    
    # Feature importance
    print("\n--- Top 10 Features más Importantes ---")
    feature_names = [
        'Simetría', 'Ratio ojos/cara', 'Ratio altura/ancho', 
        'Ratio boca/cara', 'Posición nariz', 'Ángulo mandíbula',
        'Inclinación ojos', 'Distancia cejas', 'Proporción upper/middle',
        'Proporción middle/lower', 'Desv. golden ratio', 
        'Proporción ojo-nariz', 'Ancho nariz', 'Altura frente', 
        'Profundidad facial'
    ]
    
    importances = rf.feature_importances_
    indices = np.argsort(importances)[::-1][:10]
    
    for i, idx in enumerate(indices):
        print(f"{i+1:2d}. {feature_names[idx]:25s}: {importances[idx]:.4f}")
    
    # 9. dGuardar modelo
    print("\n" + "="*70)
    pipe = make_pipeline(
        extractor(),
        scaler(),
        rf.predict()
    )
    #joblib.dump(rf, 'hollywood_model.pkl')
    #joblib.dump(scaler, 'hollywood_scaler.pkl')
    #joblib.dump(extractor, 'hollywood_extractor.pkl')
    pickle.dump(pipe, 'hollywood_pipeline.pkl')
    print(" Modelos guardados:")
    print("   - hollywood_pipeline.pkl")
    print("="*70)
    
    return rf, scaler, extractor

# ==================== PARTE 4: PREDICCIÓN ====================

def predict_image(image_path, model_path='hollywood_pipeline.pkl'):
    """Predice si una persona en la imagen es famosa o no"""
    
    image = cv2.imread(image_path)
    if image is None:
        return "Error: No se pudo cargar la imagen"

    # Cargar modelo y scaler
    with open(model_path, 'rb') as archivo:
        pipe = pickle.load(archivo)
      
    # Normalizar y predecir
    prediction = pipe(image_path)[0]['score']
    
    # Resultado
    if prediction > .3:
        return {
            'resultado': 'FAMOSO / Rasgos tipo Hollywood'
        }
    else:
        return {
            'resultado': 'NO FAMOSO / Rasgos comunes'
        }

# ==================== EJEMPLO DE USO ====================

if __name__ == "__main__":
    import sys
    
    if len(sys.argv) > 1 and sys.argv[1] == "train":
        # Modo entrenamiento
        print("\n Iniciando entrenamiento...\n")
        
        # Puedes cambiar estos números (por defecto 1000 de cada uno)
        rf, scaler, extractor = main_training_pipeline(
            num_celebrities=1000,
            num_nonfamous=1000
        )
        
        print("\n Entrenamiento completado!\n")
        
    elif len(sys.argv) > 1:
        # Modo predicción
        image_path = sys.argv[1]
        result = predict_image(image_path)
        print(f"\nAnalizando: {image_path}\n")
        os.system('cls')
        print("="*50)
        print("RESULTADO:")
        print("="*50)
        if isinstance(result, dict):
            for key, value in result.items():
                print(f"  {key}: {value}")
        else:
            print(f"  {result}")
        print("="*50)
        
    else:
        print("\n" + "="*50)
        print("USO DEL SCRIPT:")
        print("="*50)
        print("  Entrenar:  python famosos.py train")
        print("  Predecir:  python famosos.py imagen.jpg")
        print("="*50 + "\n")
