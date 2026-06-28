import numpy as np

def get_x1(matriz, soluciones, x1):
    if np.linalg.det(matriz) == 0:
        print("El determinante es 0, por lo que no tiene inversa.")
    total = [0] * len(matriz)
    for i in range(len(matriz)):
        for j in range(len(matriz[:])):
            if i == j:
                continue
            total[i] += matriz[i][j] * x1[j]
        total[i] = (1 / matriz[i][i]) * (soluciones[i] - total[i])

    return total

def error(tol,xi,xf):
    validar = [False]*len(xi)
    for i in range(len(xi)):
        if abs(xi[i] - xf[i]) <= tol:
            validar[i] = True   
    return validar

"""
matriz = [[2.3572, -1.4271], [13.1582, 12.21], ]
sol = [-33.4752, 141.0952]
xi = [1, 1]
tol = .01

    
xf = get_x1(matriz, sol, xi)
tabla = error(tol,xi,xf)
ite = 1
"""

if __name__ == '__main__':


    print(f"El determinante de la matriz es {np.linalg.det(D)}\n\n\n")
    print(f"La matriz inversa es : {np.linalg.inv(D)}\n\n\n")
    print(f"La matriz $ es: {np.dot(D,sol)}")


    xf = get_x1(matriz,sol,xi)
    tabla = error(tol,xi,xf)
    
    for j in range(n):
        print(f"El valor de x{j+1} en la iteracion {ite} es de: {xf[j]}")
    print("\n\n\n")



    while (False in tabla):
        ite += 1
        xi = xf 
        xf = get_x1(matriz,sol,xi)
        tabla = error(tol,xi,xf)
        for j in range(n):
            print(f"El valor de x{j+1} en la iteracion {ite} es de: {xf[j]}")
        print("\n\n\n")