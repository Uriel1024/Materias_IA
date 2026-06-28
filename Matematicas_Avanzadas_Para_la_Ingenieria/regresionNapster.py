import numpy as np
import math
import matplotlib.pyplot as plt


def get_c4(paso,c0,c1,c2,c3,c4):
    total = 0
    for i in range(len(sem)):
        total += (c0 + c1*sem[i] + c2*math.sin(c3*sem[i] + c4) - vent[i]) * c2 *math.cos(c3*sem[i] + c4)  
    return c4 - (paso/len(sem)) * total

def get_c3(paso,c0,c1,c2,c3,c4):
    total = 0
    for i in range(len(sem)):
        total += (c0 + c1*sem[i] + c2*math.sin(c3*sem[i] + c4) - vent[i]) * c2 *math.cos(c3*sem[i] + c4) * sem[i] 
    return c3 - (paso/len(sem)) * total

def get_c2(paso,c0,c1,c2,c3,c4):
    total = 0
    for i in range(len(sem)):
        total += (c0 + c1*sem[i] + c2*math.sin(c3*sem[i] + c4) - vent[i]) * math.sin(c3*sem[i] + c4) 
    return c2 - (paso/len(sem)) * total

def get_c1(paso,c0,c1,c2,c3,c4):
    total = 0
    for i in range(len(sem)):
        total += (c0 + c1*sem[i]  + c2*math.sin(c3*sem[i] + c4) - vent[i]) * sem[i]
    return c1 - (paso/len(sem)) * total

def get_c0(paso,c0,c1,c2,c3,c4):
    total = 0
    for i in range(len(sem)):
        total += (c0 + c1*sem[i] + c2*math.sin(c3*sem[i] + c4) - vent[i]) 
    return c0 - (paso/len(sem)) * total


sem = [.5,.7,.6,.9,3,4,8,9,10,11,5,13]
vent = [1.5,1.9,1.7,2.21,5.5,5.9,13.15,12.9,16.5,17.9,8.2,21]


c0,c1,c2,c3,c4,paso,tol,ite = 1,2,2,2,-1,.01,.00001,1
    
c0f = get_c0(paso,c0,c1,c2,c3,c4)
c1f = get_c1(paso,c0,c1,c2,c3,c4)
c2f = get_c2(paso,c0,c1,c2,c3,c4)
c3f = get_c3(paso,c0,c1,c2,c3,c4)
c4f = get_c4(paso,c0,c1,c2,c3,c4)

print(f"El valor de c0 es de {c0f} , en la iteracion {ite}")
print(f"El valor de c1 es de {c1f} , en la iteracion {ite}")
print(f"El valor de c2 es de {c2f} , en la iteracion {ite}")
print(f"El valor de c3 es de {c3f} , en la iteracion {ite}")
print(f"El valor de c4 es de {c4f} , en la iteracion {ite} \n\n")
    

while np.sqrt( (c0f - c0)**2 + (c1f - c1)**2 +  (c2f - c2)**2 + (c3f - c3)**2 + (c4f - c4)**2 ) > tol:
    ite += 1
    c0, c1,c2,c3,c4 = c0f, c1f,c2f,c3f,c4f
    c0f = get_c0(paso,c0,c1,c2,c3,c4)
    c1f = get_c1(paso,c0,c1,c2,c3,c4)
    c2f = get_c2(paso,c0,c1,c2,c3,c4)
    c3f = get_c3(paso,c0,c1,c2,c3,c4)
    c4f = get_c4(paso,c0,c1,c2,c3,c4)
    if ite == 100000:
        break
 


print(f"El valor de c0 es de {c0f} , en la iteracion {ite}")
print(f"El valor de c1 es de {c1f} , en la iteracion {ite}")
print(f"El valor de c2 es de {c2f} , en la iteracion {ite}")
print(f"El valor de c3 es de {c3f} , en la iteracion {ite}")
print(f"El valor de c4 es de {c4f} , en la iteracion {ite} \n\n")
    


def modelo_ajustado(x, c0, c1, c2, c3, c4):
    return c0 + c1*x + c2*np.sin(c3*x + c4)


x_min = np.min(sem)
x_max = np.max(sem)

x_fit = np.linspace(x_min, x_max, 100)

y_fit = modelo_ajustado(x_fit, c0f, c1f, c2f, c3f, c4f)

plt.figure(figsize=(10, 6)) 

plt.scatter(sem, vent, color='red', label='Datos Originales (vent)', zorder=5)

plt.plot(x_fit, y_fit, color='blue', label='Modelo Ajustado', linewidth=2)

# Añadir etiquetas y título
plt.title('Ajuste de Curva por Descenso de Gradiente')
plt.xlabel('Eje X (sem)')
plt.ylabel('Eje Y (vent)')
plt.legend() # Muestra la leyenda con las etiquetas
plt.grid(True) # Muestra una cuadrícula para mejor referencia
plt.show() # Muestra la gráfica