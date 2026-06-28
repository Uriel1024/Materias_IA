#import numpy as np
import math

funciones = {
	'sen' : math.sin,
	'cos' : math.cos,
	'tan' : math.tan,
	'arcos' : math.acos,
	'arsin' : math.asin
}

def convertir(funcion.lower()):
	print(funcion)
	if func in funciones:
		print("El texto contiene alguna funcion trigonmetrica.")
	else:
		print("no contiene funciones trigonmetricas")


def evaluar(x): 
	cociente = x*math.sin(x) - 2
	residuo = x*math.cos(x) + math.sin(x)
	return x - (cociente/residuo)

def newton():
	x0 = float(input("Ingresa x_0:"))	
	error = float(input('Ingresa el error: '))

	x = evaluar(x0)
	j = 0
	while abs(x - x0) > error: 
		x0 = x
		x = evaluar(x0) 
		j+=1
		if j == 1000:
			break

	print(f"El valor de la raiz aproximado es: {x}; y se encontró despues de {j} iteraciones.")
	fval = x*(math.sin(x)) - 2 
	print(f"el valor aproximado es de: {fval}")


if __name__ == '__main__':
	a = input("Ingresa una funcion:")
	convertir(a)