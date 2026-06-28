import numpy as np
import matplotlib.pyplot as plt

def f(x,y):
    return (np.log(1 + x**2 + y**2)	)

def gradx(x,y):
    return ((2*x)/(1 + x**2 + y**2 ) - (2*x)/(1+x**4))	

def grady(x,y):
	return (2*y)/(1 + x**2 + y**2 )

def minimizar(x,y,paso):
	return (x - paso*(gradx(x,y))) , (y - paso*(grady(x,y)))

def maximizar(x,y,paso):
	return x + paso*(gradx(x,y)), y + paso*(grady(x,y))

tolerancia , x0,y0, paso,ite = .01,.5, .5, .2, 1
x1,y1 = minimizar(x0,y0,paso)

print(f"El valor de x es {x1}, el de y es {y1} en la {ite} iteracion con un error de {np.sqrt((x0 - x1)**2  + (y0 - y1)**2 )}\n\n")


while np.sqrt((x0 - x1)**2  + (y0 - y1)**2 )  >= tolerancia:	
	ite += 1
	x0 = x1
	y0 = y1
	x1,y1 = minimizar(x0,y0,paso)
	print(f"El valor de x es {x1}, el de y es {y1} en la {ite} iteracion con un error de {np.sqrt((x0 - x1)**2  + (y0 - y1)**2 )}\n\n")


print("Maximizacion \n\n\n\n\n")

ite,x0,y0= 0,.5,.5
x,y = maximizar(x0,y0,paso)
while np.sqrt((x0 - x)**2  + (y0 - y)**2 ) > tolerancia:
	ite+=1
	print(f"El valor de x es {x}, el de y es {y} en la {ite} iteracion con un error de {np.sqrt((x0 - x)**2  + (y0 - y)**2 )}\n\n")
	x0 = x
	y0 = y
	x,y = maximizar(x,y,paso)
	if ite == 100:
		break


