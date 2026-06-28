import numpy as np
import matplotlib.pyplot as plt


def get_c2(paso,c0,c1,c2):
    total = 0
    for i in range(len(gasto)):
        total += (c0 + c1*cachorros[i] + c2*raciones[i] - gasto[i]) * raciones[i]
    return c2 - (paso/len(gasto)) * total


def get_c1(paso,c0,c1,c2):
    total = 0
    for i in range(len(gasto)):
        total += (c0 + c1*cachorros[i] + c2*raciones[i] - gasto[i]) * cachorros[i]
    return c1 - (paso/len(gasto)) * total


def get_c0(paso,c0,c1,c2):
    total = 0
    for i in range(len(gasto)):
        total += (c0 + c1*cachorros[i] + c2*raciones[i] - gasto[i]) 
    return c0 - (paso/len(gasto)) * total


gasto = [24.6,33,36.6,39.8,28.6]
cachorros = [1,3,4,4,2]
raciones = [11,13,13,14,12]

c0,c1,c2,paso,tol,ite = -4, 3,2,.01,.2,1
c0f = get_c0(paso,c0,c1,c2)
c1f = get_c1(paso,c0,c1,c2)
c2f = get_c2(paso,c0,c1,c2)



print(f"El valor de c0 es de {c0f} con un error de {abs(c0f - c0)}, en la iteracion {ite}")
print(f"El valor de c1 es de {c1f} con un error de {abs(c1f - c1)}, en la iteracion {ite}")
print(f"El valor de c2 es de {c2f} con un error de {abs(c2f - c2)}, en la iteracion {ite}\n\n\n")


while (abs(c1f - c1) >= tol)  or (abs(c0f - c0) >= tol) or (abs( c2f  - c2) >= tol):
    ite += 1
    c0, c1,c2 = c0f, c1f,c2f
    c0f = get_c0(paso,c0,c1,c2)
    c1f = get_c1(paso,c0,c1,c2)
    c2f = get_c2(paso,c0,c1,c2)
    
        
    print(f"El valor de c0 en es de {c0f} con un error de {abs(c0f - c0)}, en la iteracion {ite}")
    print(f"El valor de c1 en es de {c1f} con un error de {abs(c1f - c1)}, en la iteracion {ite}")
    print(f"El valor de c2 en es de {c2f} con un error de {abs(c2f - c2)}, en la iteracion {ite}\n\n\n")
