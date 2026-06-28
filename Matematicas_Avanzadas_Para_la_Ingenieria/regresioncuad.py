import numpy as np

#obtener para la funcion 
def get_c2(paso,c0,c1,c2):
    total = 0
    for i in range(len(sem)):
        total += (c0 + c1*sem[i] + c2*(sem[i]**2) - vent[i]) * (sem[i]**2) 
    return c2 - (paso/len(sem)) * total


def get_c1(paso,c0,c1,c2):
    total = 0
    for i in range(len(sem)):
        total += (c0 + c1*sem[i]  + c2*(sem[i]**2) - vent[i]) * sem[i]
    return c1 - (paso/len(sem)) * total


def get_c0(paso,c0,c1,c2):
    total = 0
    for i in range(len(sem)):
        total += (c0 + c1*sem[i] + c2*(sem[i]**2) - vent[i]) 
    return c0 - (paso/len(sem)) * total


sem = [2,3,5,7,9,4]
vent = [480,482,460,405,324,475]


c0,c1,c2,paso,tol,ite = 450, 23,-4,.001,.0001,1

c0f = get_c0(paso,c0,c1,c2)
c1f = get_c1(paso,c0,c1,c2)
c2f = get_c2(paso,c0,c1,c2)

print(f"El valor de c0 es de {c0f} con un error de {abs(c0f - c0)}, en la iteracion {ite}")
print(f"El valor de c1 es de {c1f} con un error de {abs(c1f - c1)}, en la iteracion {ite}")
print(f"El valor de c2 es de {c2f} con un error de {abs(c2f - c2)}, en la iteracion {ite} \n\n")

    

while (abs(c1f - c1) >= tol) or (abs(c0f - c0) >= tol) or (abs(c2f - c2) >= tol):
    ite += 1
    c0, c1,c2 = c0f, c1f,c2f
    c0f = get_c0(paso,c0,c1,c2)
    c1f = get_c1(paso,c0,c1,c2)
    c2f = get_c2(paso,c0,c1,c2)
 

print(f"El valor de c0 en es de {c0f} con un error de {abs(c0f - c0)}, en la iteracion {ite}")
print(f"El valor de c1 en es de {c1f} con un error de {abs(c1f - c1)}, en la iteracion {ite}")
print(f"El valor de c2 es de {c2f} con un error de {abs(c2f - c2)}, en la iteracion {ite}\n\n")
