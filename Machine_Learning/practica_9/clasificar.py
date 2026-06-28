import os 
from pathlib import Path
import shutil


BASE_DIR = Path(__file__).resolve().parent.parent
directory = BASE_DIR /"practica_9/test/validation"

fruits = ['cempedak', 'camu_camu', 'sugar_apple', 'rose_leaf_bramble', 
'chenet', 'barberry', 'avocado', 'quince', 'plumcot', 'longan', 'horned_melon', 
'rose_hip', 'redcurrant', 'pineapple', 'mabolo', 'jackfruit', 'black_mullberry', 
'taxus_baccata', 'ackee', 'sea_buckthorn', 'lemon', 'pomegranate', 'langsat', 'jaboticaba', 
'guava', 'grapes', 'mango', 'acerola', 'kumquat', 'greengage', 'grapefruit', 'olive', 'cherimoya',
 'white_currant', 'emblic', 'medlar', 'elderberry', 'kiwi', 'santol', 'prikly_pear', 'kaffir_lime', 
 'salak', 'sapodilla', 'hog_plum', 'watermelon', 'malay_apple', 'yali_pear', 'raspberry', 'damson', 
 'banana', 'black_berry', 'dragonfruit', 'fig', 'acai', 'brazil_nut', 'hawthorn', 'cupuacu', 'cashew',
  'mangosteen', 'jambul', 'passion_fruit', 'apple', 'ugli_fruit', 'barbadine', 'feijoa', 'jujube', 'cranberry', 
  'otaheite_apple', 'grenadilla', 'chico', 'dewberry', 'mountain_soursop', 'morinda', 'pear', 'jocote', 
  'papaya', 'goumi', 'jamaica_cherry', 'custard_apple', 'rambutan', 'mandarine', 'cluster_fig', 
  'orange', 'pawpaw', 'indian_strawberry', 'abiu', 'strawberry_guava', 'durian', 'chokeberry', 'tomato', 
  'yellow_plum', 'coconut', 'oil_palm', 'finger_lime', 'ambarella', 'hard_kiwi', 'gooseberry', 
  'apricot', 'mock_strawberry']

vegetables = ['jalepeno', 'potato', 'sweetcorn', 'beetroot', 'spinach', 'cabbage', 'bell pepper', 'peas', 
'ginger', 'chilli pepper', 'cauliflower', 'onion', 'garlic', 'capsicum', 'turnip', 'bitter_gourd', 
'lablab', 'sweetpotato', 'lettuce', 'eggplant', 'carrot', 'paprika', 'betel_nut', 'raddish', 'cucumber']

directorios = os.listdir(directory)

frutas = f"{directory}/fruits"
verduaras = f"{directory}/verduaras"

for i in directorios:
 
    full_path = directory / i 

    if not os.path.exists(frutas):
        os.makedirs(frutas)
    if not os.path.exists(verduaras):
        os.makedirs(verduaras)

    if full_path.is_dir(): 
        if i in fruits:
            print(f"Carpeta: {i} es una fruta y se mueve a fruits")
            shutil.move(full_path, frutas) # Mueve la carpeta completa
        elif i in vegetables:
            print(f"Carpeta: {i} es una verdura y se mueve a verduaras")
            shutil.move(full_path, verduaras) # Mueve la carpeta completa
        else:
            print(f"Carpeta: {i} no se clasificó (ni fruta ni verdura). Se omite.")
    else:
        # Esto imprimirá los archivos sueltos como 'img_xxx.jpg'
        print(f"Elemento: {i} es un archivo y se omite el movimiento.")