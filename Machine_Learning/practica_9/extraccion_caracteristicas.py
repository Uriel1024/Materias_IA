import numpy as np
import pandas as pd
from sklearn.utils import Bunch
from skimage.io import imread_collection, imread
from skimage.transform import resize
from sklearn.model_selection import train_test_split
from sklearn.decomposition import PCA
import os
import glob
from pathlib import Path
from IPython.display import display
	
#para no tener problemas con las rutas gg
BASE_DIR = Path(__file__).resolve().parent.parent
clase = "fruits	"
path  = BASE_DIR /f"practica_9/pruebas/train/{clase}"

size_pi = 64

def segmentacion(dim = (size_pi,size_pi)):
	#para guaradar las carpetas
	carpetas = [nombre for nombre in os.listdir(path) if  os.path.isdir(os.path.join(path, nombre))]
	imgs = [img for img in glob.glob(str(path) + "/*/*.jpg")]
	carpetas.sort()
	#listas para guardar los datos
	images = []
	data = []
	target = []
	# Calcula la forma esperada (height * width * channels)
	expected_data_len = dim[0] * dim[1] * 3
	# Calcula la forma de la imagen (height, width, channels)
	expected_image_shape = (dim[0], dim[1], 3)
	iter = 0

	#recorremos las carpetas e imagenes para poder crear el dataset numerico
	for j in imgs:
		try:
			imagen = imread(j)
			height, width = imagen.shape[:2]

			if imagen.ndim == 2: # Es escala de grises
				# Convierte a color replicando el canal
				imagen = np.stack((imagen,)*3, axis=-1)
			elif imagen.ndim == 4: # Es RGBA (con canal alfa)
				# Elimina el canal alfa
				imagen = imagen[:, :, :3]


			#las img muy peqenias no se pueden redimensionar bien
			if height < size_pi and width < size_pi:
				print(f"la imagen {j} es demasiado pequenia y se omite")
				continue

			imagen_redim = resize(imagen, dim)
			if imagen_redim.shape != expected_image_shape:
				print(f"La imagen {j} no tiene la forma esperada despues de redimensionar y se omite")
				continue

			images.append(imagen_redim)
			data.append(imagen_redim.flatten())
			target.append(carpetas.index(os.path.basename(os.path.dirname(j))))

			nombre_clase = os.path.basename(os.path.dirname(j))
			if iter % 20 == 0:
				print(f"procesada la imagen {j} de la clase {nombre_clase}")
		
		except ValueError:
			print(f"error, la clase {nombre_clase} no se encuentra en las carpetas")
			continue
		except Exception as e:
			print(f"error al procesar la imagen {j}: {e}")
			continue
		iter += 1

	print(f"Total de imgs procesaedas es de:{len(data)}")

	#devolvemos un objeto tipo bunch con los datos

	return Bunch(data = np.array(data), target = np.array(target), images = np.array(images), target_names = carpetas)

def guardar_csv(data_bunch, nombre_archivo=f"validation_{clase}.csv"):
    archivo = f"{path}/{nombre_archivo}"

    df_nuevo = pd.DataFrame(data_bunch.data)
    
   
    df_nuevo['target'] = data_bunch.target

    if os.path.exists(archivo):
        try:

            df_existente = pd.read_csv(archivo)
            
    
            df_final = pd.concat([df_existente, df_nuevo], axis=1)

            df_final.to_csv(archivo, index=False)
            print(f"Datos añadidos como nuevas columnas a: {archivo}")

        except pd.errors.EmptyDataError:

            df_nuevo.to_csv(archivo, index=False)
            print(f"Archivo existente vacío, guardando datos iniciales en: {archivo}")
        except Exception as e:
            print(f"Error al leer/procesar el CSV existente: {e}. Guardando el nuevo DataFrame por separado.")

            df_nuevo.to_csv(archivo, index=False)
            
    else:
       
        df_nuevo.to_csv(archivo, index=False)
        print(f"Archivo creado y datos iniciales guardados en: {archivo}")
	


if __name__ == '__main__':
	data = segmentacion()
	print(data.data.shape)
	guardar_csv(data)