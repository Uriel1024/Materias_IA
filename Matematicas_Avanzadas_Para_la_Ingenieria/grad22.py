import numpy as np

def f(x,y):
    return (x**2 + y**2 + x*y + x + y)

def gradx(x,y):
    return (2*x + y + 1)

def grady(x,y):
	return (2*y + x + 1)

def minimizar(x,y,paso):
	return x - paso*(gradx(x,y)), y - paso*(grady(x,y))

def maximizar(x,y,paso):
	return x + paso*(gradx(x,y)), y + paso*(grady(x,y))

tolerancia , x0,y0, paso,ite = .0001,.5, .5, .2, 1
x1,y1 = minimizar(x0,y0,paso)


while np.sqrt((x0 - x1)**2  + (y0 - y1)**2 )  >= tolerancia:
	ite += 1
	print(f"El valor de x es {x1}, el de y es {y1} en la {ite} iteracion con un error de {np.sqrt((x0 - x1)**2  + (y0 - y1)**2 )}\n\n")
	x0 = x1
	y0 = y1
	x1,y1 = minimizar(x0,y0,paso)

print(f"El valor de x para minimzar la funcion es  {x1} y se encontró despues de {ite} iteraciones.")
print(f"El valor de y para minimzar la funcion es  {y1} y se encontró despues de {ite} iteraciones.")
print(f"El valor de z es {f(x1,y1)}\n\n\n\n")

ite,x0,y0= 0,.5,.5
x,y = maximizar(x0,y0,paso)
while np.sqrt((x0 - x)**2  + (y0 - y)**2 ) > tolerancia:
	ite+=1
	x0 = x
	y0 = y
	x,y = maximizar(x,y,paso)
	if ite == 10:
		break

print(f"El valor de x para maximizar la funcion es  {x} y se encontró despues de {ite} iteraciones.")
print(f"El valor de y para maximizar la funcion es  {y} y se encontró despues de {ite} iteraciones.")
print(f"El valor de z es {f(x,y)}\n\n\n\n")