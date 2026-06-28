import numpy as np

def jacobi(E,S,x):
	return (S - np.dot(E,x))

def matrices(matriz,sol):
	D = [[0 for _ in range(len(matriz))]for _ in range(len(matriz[:]))]
	R = [[0 for _ in range(len(matriz))]for _ in range(len(matriz[:]))]
	for i in range(len(matriz)):
		for j in range(len(matriz[:])):
			if i == j:
				D[i][j] = 1/matriz[i][j]
			else:
				R[i][j] = matriz[i][j]
	E = np.dot(D,R)
	S = np.dot(D,sol)
	return E, S

def error(tol,xi,xf):
    validar = [False]*len(xi)
    for i in range(len(xi)):
        if abs(xi[i] - xf[i]) <= tol:
            validar[i] = True   
    return validar
    	
matriz = [[7.25,3.15,1.2],[-8.12,-21.2,5.4],[-3.41,12.05,-15.01]]
sol = [9.9285,104.3746,-76.2122]
x = [1,1,1]
tol,ite = .01,1

if __name__ == '__main__':
	E, S = matrices(matriz, sol)
	print(f"La matriz E tiene los valores:\n {E}\n\n\n")
	print(f"El vector S tiene los valores:  {S}\n\n\n")
	if np.linalg.det(E) != 0:
		xf = jacobi(E,S,x)
		for j in range(len(xf)):
			print(f"El valor de x{j+1} en la iteracion {ite} es de: {xf[j]}")
		print("\n\n\n\n")

		tabla = error(tol,x,xf)
		while np.sqrt( (xf[0] - x[0])**2 + (xf[1] - x[1])**2 + (xf[2] - x[2])**2   ) > tol:
			ite +=1 
			x = xf
			xf = jacobi(E,S,x)
			tabla = error(tol,x,xf)

		for j in range(len(xf)):
			print(f"El valor de x{j+1} en la iteracion {ite} es de: {xf[j]}")
		print("\n\n\n")
	else:
		print("El determinante es igual a 0, por lo que no tiene inversa.")