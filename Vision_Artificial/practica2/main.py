from PIL import Image
import numpy as np
import random
import time
import os

clear = lambda: os.system('clear')

def mediana(matriz, fil, col, k):
    filas = len(matriz)
    columnas = len(matriz[0])
    l = []
    for i in range(-1,2):
        for j in range(-1,2):
            if not (0<=fil+i<filas and 0<=col+j<columnas): 
                l.append(0)
                continue
            l.append(matriz[fil+i][col+j][k])
    l.sort()
    return l[4]

def filtro_mediana(image_sp):
    matriz = np.array(image_sp)
    filas = len(matriz)
    columnas = len(matriz[0])
    #new_matriz = np.zeros((filas, columnas, 3), dtype=np.int_)
    new_matriz = matriz.copy()
    for k in range(3):
        for i in range(filas):
            for j in range(columnas):
                new_matriz[i][j][k] = mediana(matriz, i, j, k)
    h = Image.fromarray(new_matriz)
    h.show()

def salt_pepper(percent):
    matriz = np.array(img)
    for fila in matriz:
        for i in range(len(fila)):
            r = random.randint(0,100)
            if r < percent:
                r = random.randint(0,1)
                if r: fila[i] = np.zeros(3)
                else: fila[i] = [255,255,255]
    h = Image.fromarray(matriz)
    return h


def sel_img():
    print("\n\n\n1.Imagen de Berserk.")
    print("2.Imagen de evangelon.")
    print("3.Imagen de evangelon(variante).")
    print("4.Imagen de Berserk(variante).")
    print("5.Imagen de perfect blue.")
    print("6.Imagen de The bends.")
    print("7.Imagen de luna.")
    while True:
        op = input("Selecciona una opcion para poder aplicarle ruido(salt & pepper) y regresarla a su forma original: ")
        if op in ['1','2','3','4','5','6','7']:
            return Image.open(f'img{op}.jpeg')
            break
    else:
        print("Ingresa una opcion valida")


if __name__ == "__main__":
    print("Bienvenido a la segunda práctica de Vision Artificial.")
    img = sel_img()
    while True:
        print("1.Aplicar filro de mediana")
        print("2.Seleccionar la imagen")
        print("3.Salir del programa")
        opcion = input("Ingresa una opcion para poder continuar: ")
        if opcion == 'm' or opcion == '1':
            start_time = time.time()
            try:
                percent = int(input("Ingresa el porcentaje de ruido para la image: "))
                if not 0 < percent < 100:
                    raise ValueError
                image_sp = salt_pepper(percent)
            except ValueError:
                print("opcion no valida.")
            print("Imagen con sal y pimienta\n\n")
            image_sp.show()
            filtro_mediana(image_sp)
            end_time = time.time()
            print(f'Tarea realizada en {end_time - start_time} segundos')
            time.sleep(3)
            clear()
        elif opcion == '2':
            img = sel_img()
            clear()
        elif opcion == 's' or opcion ==     '3':
            print("Programa finalizado")
            break
        else:
            print(f"{opcion} No es una opcion valida, por favor ingresa una opcion valida para poder continar.")