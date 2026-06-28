import numpy as np

def calcularC(matriz, cnext, c):
    for i in range(n):
        total=0
        for j in range(m):
            pred = c[0]
            for k in range(1,n):
                pred += c[k] *  matriz[k][j]
            error = pred - matriz[0][j]
            if i == 0:
                xnm = 1
            else:
                xnm = matriz[i][j]
            total += error*xnm 
        cnext[i] = c[i] - paso*(1/m)*total 
    return cnext

n = int(input("Ingresa el num de variables: "))
m =int(input("Ingresa el num de objetos por variable: "))
tol = float(input("Ingresa el tolerancia: "))
paso = float(input("Ingresa el paso: "))
c = []
for i in range(n):
    c.append(float(input(f"Ingresa el valor inicial de c{i}:")))
matriz = []

for i in range(n):
    matriz.append([])
    for j in range(m):
        if i == 0:
            matriz[i].append(float(input(f"Ingresa el valor de la variable dependiente y en la posicion {j}:")))
        else:
            matriz[i].append(float(input(f"Ingresa el valor de la variable x{i} en la posicion {j}:")))

cnext = [0]*n


calcularC(matriz,cnext,c)
print(cnext)

ite = 1
while ite < 3:
    ite += 1
    c = cnext
    cnext = calcularC(matriz,cnext,c)
    print(cnext)
