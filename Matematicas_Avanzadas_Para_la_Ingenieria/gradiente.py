import math
import numpy as np
import matplotlib.pyplot as plt

def f(x):
    return ((1/1500 * ( (x**2 * np.cos(x)) -x)))


def grad(x):
    return ((1/1500 * ( (2*x*np.cos(x)) -(x**2*np.sin(x)) -1)))

def minimizar(x,paso):
	return x - paso*(grad(x))

def maximizar(x,paso):
	return x + paso*(grad(x))

tolerancia , x0, paso,ite = .001, 5, 50, 0 
x1 = minimizar(x0,paso)
while abs(x0 - x1) > tolerancia:	
	ite += 1
	x0 = x1
	print(f"El valor de x despues de la  iteracion {ite} es: {x1}")
	x1 = minimizar(x0,paso)



print(f"La funcion que tratamos encontar la tasa de maximo crecimiento es: 1/500*(x^2 * cos(x) - x) ")
print(f"El valor de x para minimzar la funcion es  {x1} y se encontró despues de {ite} iteraciones.\n\n\n\n\n\n")

ite,x0,= 0,5
x = maximizar(x0,paso)
while abs(x0 - x) > tolerancia:
	ite+=1
	x0 = x
	print(f"El valor de x despues de la iteracion {ite} es: {x}")
	x = maximizar(x,paso)

puntx = [x1,x]
punty = [f(x1),f(x)]

print(f"El valor de x para maximizar la funcion es  {x} y se encontró despues de {ite} iteraciones.")


x_plot = np.linspace(-10,10,1000)
plt.scatter(puntx,punty,s=60,color='red',label = 'Puntos min y max')
y = f(x_plot)
plt.plot(x_plot,y)
plt.xlabel("x")
plt.ylabel("f(x)")
plt.title(" Funcion f'(x) = 1/1500 * (2*x*cos(x) - x**2sin(x) - 1) ")
plt.grid(True)
plt.show()