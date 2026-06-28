import requests
from bs4 import BeautifulSoup
import os
import time
from urllib.parse import urljoin, urlparse

# --- CONFIGURACIÓN ---
# ⚠️ IMPORTANTE: Reemplaza esta URL con la página que quieres scrapear
URL_BASE = "https://pixabay.com/images/search/watermelon%20image/" 
CARPETA_DESCARGA = "bananas1"
# ---------------------

def crear_carpeta():
    """Crea la carpeta de descarga si no existe."""
    if not os.path.exists(CARPETA_DESCARGA):
        os.makedirs(CARPETA_DESCARGA)
        print(f"📦 Carpeta creada: '{CARPETA_DESCARGA}'")

def descargar_imagen(url_imagen, nombre_archivo):
    """Descarga una imagen desde una URL dada."""
    try:
        # Petición GET con timeout
        respuesta = requests.get(url_imagen, stream=True, timeout=10)
        
        if respuesta.status_code == 200:
            # Ruta completa para guardar el archivo
            ruta_completa = os.path.join(CARPETA_DESCARGA, nombre_archivo)
            
            # Abre y escribe el contenido de la imagen en modo binario
            with open(ruta_completa, 'wb') as archivo:
                for chunk in respuesta.iter_content(1024):
                    if chunk:
                        archivo.write(chunk)
            print(f"  ✅ Descargada: {nombre_archivo}")
            return True
        else:
            print(f"  ❌ Error HTTP {respuesta.status_code} al descargar: {url_imagen}")
            return False
            
    except requests.exceptions.RequestException as e:
        print(f"  ❌ Error de conexión/timeout: {e}")
        return False

def extraer_y_descargar_imagenes(url_pagina):
    """
    Se conecta a la URL, extrae enlaces de imágenes y las descarga.
    """
    crear_carpeta()
    
    print(f"📡 Conectando a: {url_pagina}")
    try:
        # Obtener el contenido de la página
        respuesta_pagina = requests.get(url_pagina, timeout=15)
        respuesta_pagina.raise_for_status()  # Lanza una excepción para códigos de error 4xx/5xx
    except requests.exceptions.RequestException as e:
        print(f"🛑 Error al obtener la página: {e}")
        return

    # Analizar el HTML
    soup = BeautifulSoup(respuesta_pagina.text, 'html.parser')
    
    # Encontrar todas las etiquetas de imagen (<img>)
    etiquetas_img = soup.find_all('img')
    
    print(f"🔎 Encontradas {len(etiquetas_img)} posibles etiquetas de imagen.")
    
    imagenes_descargadas = 0
    
    for i, img in enumerate(etiquetas_img):
        # Intentar obtener la URL desde el atributo 'src'
        url_relativa = img.get('src')
        
        if url_relativa:
            # 💡 Construir la URL absoluta: Algunas URLs son relativas (/img/foto.jpg)
            url_absoluta = urljoin(url_pagina, url_relativa)
            
            # Limpieza y filtrado básico (opcional, pero ayuda)
            if not url_absoluta.startswith(('http', 'https')):
                continue # Saltar si no parece una URL válida
                
            # Generar un nombre de archivo único, intentando usar la extensión
            parsed_url = urlparse(url_absoluta)
            nombre_sugerido = os.path.basename(parsed_url.path) or f"imagen_{i+1:03d}.jpg"
            
            # Generar un nombre final para evitar archivos con nombres muy largos o inválidos
            nombre_final = f"img_{i+1:03d}_{nombre_sugerido[-15:].replace('/', '_')}"
            
            # Asegurar la extensión mínima si no se encuentra (ej: .jpg)
            if not os.path.splitext(nombre_final)[1]:
                 nombre_final += ".jpg"

            print(f"\nProcesando imagen {i+1}...")
            if descargar_imagen(url_absoluta, nombre_final):
                imagenes_descargadas += 1
            
            # ⏳ Pausa para ser amable con el servidor
            time.sleep(0.3) 
            
    print(f"\n--- ✅ Proceso Finalizado ---")
    print(f"Imágenes descargadas en '{CARPETA_DESCARGA}': {imagenes_descargadas}")

# --- Ejecución Principal ---
if __name__ == "__main__":
    extraer_y_descargar_imagenes(URL_BASE)