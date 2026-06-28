import numpy as np
import matplotlib.pyplot as plt

def get_c1(paso,c0,c1):
    total = 0
    for i in range(len(sem)):
        total += (c0 + c1*sem[i] - vent[i]) * sem[i]

    return c1 - (paso/len(sem)) * total


def get_c0(paso,c0,c1):
    total = 0
    for i in range(len(sem)):
        total += (c0 + c1*sem[i] - vent[i]) 
    return c0 - (paso/len(sem)) * total


sem = [.14,.28,.57,3,3.71,8,8.28,8.71,10]
vent = [220,277,261,305,292,403,442,490,512]


c0,c1,paso,tol,ite = .5, 200,.02,3,1
c0f = get_c0(paso,c0,c1)
c1f = get_c1(paso,c0,c1)


print(f"El valor de c0 es de {c0f} con un error de {abs(c0f - c0)}, en la iteracion {ite}")
print(f"El valor de c1 es de {c1} con un error de {abs(c1f - c1)}, en la iteracion {ite}\n\n")


while (abs(c1f - c1) > tol) or (abs(c0f - c0) > tol):
    ite += 1
    c0, c1 = c0f, c1f
    c0f = get_c0(paso,c0,c1)
    c1f = get_c1(paso,c0,c1)
    
    print(f"El valor de c0 en es de {c0f} con un error de {abs(c0f - c0)}, en la iteracion {ite}")
    print(f"El valor de c1 en es de {c1} con un error de {abs(c1f - c1)}, en la iteracion {ite}\n\n")


print(f"El valor de c0 en es de {c0f} con un error de {abs(c0f - c0)}, en la iteracion {ite}")
print(f"El valor de c1 en es de {c1} con un error de {abs(c1f - c1)}, en la iteracion {ite}\n\n")