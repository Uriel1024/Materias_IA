import requests
import os
import time

# ⚠️ CONFIGURACIÓN CLAVE ⚠️
# Reemplaza 'TU_API_KEY_AQUI' con la clave que obtuviste de Pexels
PEXELS_API_KEY = "umNSIbxId1gcFDen22YSywuAF9IynLOJ6eoTz6mG4UxcwW8aCWBQod5Z" 
TEMA_BUSQUEDA = "Watermelon" # El tema de las imágenes que quieres buscar
CANTIDAD_IMAGENES = 500  # Número máximo de imágenes a descargar
CARPETA_DESCARGA = "imagenes_pexels1"
# -----------------------------

def crear_carpeta():
    """Crea la carpeta de descarga si no existe."""
    if not os.path.exists(CARPETA_DESCARGA):
        os.makedirs(CARPETA_DESCARGA)
        print(f"📦 Carpeta creada: '{CARPETA_DESCARGA}'")

def descargar_imagen(url_imagen, nombre_archivo):
    """Descarga una imagen desde una URL dada."""
    try:
        ruta_completa = os.path.join(CARPETA_DESCARGA, nombre_archivo)
        
        # Petición GET con timeout
        respuesta = requests.get(url_imagen, stream=True, timeout=10)
        respuesta.raise_for_status() # Lanza error para 4xx/5xx
        
        # Abre y escribe el contenido de la imagen en modo binario
        with open(ruta_completa, 'wb') as archivo:
            for chunk in respuesta.iter_content(1024):
                if chunk:
                    archivo.write(chunk)
            print(f"  ✅ Descargada: {nombre_archivo}")
            return True
    except requests.exceptions.RequestException as e:
        print(f"  ❌ Error de conexión/descarga para {nombre_archivo}: {e}")
        return False

def obtener_urls_de_api(query, per_page):
    """
    Busca imágenes en la API de Pexels y devuelve una lista de URLs directas.
    """
    url_api = "https://api.pexels.com/v1/search"
    
    headers = {
        "Authorization": PEXELS_API_KEY
    }
    
    params = {
        "query": query,
        "per_page": per_page,
        "orientation": "landscape"
    }

    print(f"📡 Buscando '{query}' en la API de Pexels...")
    try:
        respuesta = requests.get(url_api, headers=headers, params=params, timeout=15)
        respuesta.raise_for_status() # Manejo de errores de API
        
        data = respuesta.json()
        
        urls_descarga = []
        for foto in data.get('photos', []):
            # Usaremos la URL de tamaño original ('original')
            url_directa = foto['src']['original']
            urls_descarga.append(url_directa)
            
        print(f"🔎 API encontró {len(urls_descarga)} URLs de imágenes.")
        return urls_descarga
        
    except requests.exceptions.RequestException as e:
        print(f"🛑 Error al comunicarse con la API de Pexels: {e}")
        return []
    except Exception as e:
        print(f"🛑 Ocurrió un error al procesar la respuesta de la API: {e}")
        return []

# --- PROCESO PRINCIPAL ---
if __name__ == "__main__":
    if PEXELS_API_KEY == "TU_API_KEY_AQUI":
        print("🛑 ERROR: Por favor, reemplaza 'TU_API_KEY_AQUI' con tu clave real de Pexels.")
    else:
        crear_carpeta()
        
        # 1. Obtener URLs usando la API
        lista_de_urls = obtener_urls_de_api(TEMA_BUSQUEDA, CANTIDAD_IMAGENES)
        
        if not lista_de_urls:
            print("No se encontraron URLs para descargar o hubo un error en la API.")
        else:
            # 2. Iterar y Descargar las imágenes
            print("\n--- 📥 Iniciando descarga de imágenes ---")
            imagenes_descargadas = 0
            
            for i, url in enumerate(lista_de_urls):
                # Intentamos obtener la extensión del archivo de la URL
                nombre_base, extension = os.path.splitext(url.split('?')[0]) # Ignorar parámetros de consulta
                extension_usar = extension if extension.lower() in ['.jpg', '.jpeg', '.png', '.gif'] else ".jpeg"

                nombre_archivo = f"{TEMA_BUSQUEDA}_{i+1:02d}{extension_usar}"
                
                print(f"Procesando imagen {i+1}...")
                if descargar_imagen(url, nombre_archivo):
                    imagenes_descargadas += 1
                
                time.sleep(0.3) # Pausa amigable
                
            print(f"\n--- 🥳 Proceso finalizado ---")
            print(f"Total de imágenes descargadas: {imagenes_descargadas}")