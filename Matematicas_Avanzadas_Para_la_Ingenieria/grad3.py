import numpy as np
import matplotlib.pyplot as plt

def gradx(x,y,z):
    return (2*x - y +1) 	

def grady(x,y,z):
	return (2*y - x)

def gradz(x,y,z):
  return (2*z - 2)

def minimizar(x,y,z,paso):
	return (x - paso*(gradx(x,y,z))) , (y - paso*(grady(x,y,z))) ,(z - paso * (gradz(x,y,z)))

def maximizar(x,y,z,paso):
	return (x + paso*(gradx(x,y,z))) , (y + paso*(grady(x,y,z))) ,(z + paso * (gradz(x,y,z)))


tolerancia , x0,y0,z0, paso,ite = .049,1, 1,1,.25, 1
x1,y1,z1 = minimizar(x0,y0,z0,paso)
print("Maximizacion de la funcion: ")

print(f"El valor de x es {x1}, el de y es {y1}, el de z es {z1} en la {ite} iteracion con un error de {np.sqrt((x0 - x1)**2  + (y0 - y1)**2 )}\n\n")

while np.sqrt((x0 - x1)**2  + (y0 - y1)**2 + (z0-z1)**2 )  > tolerancia:	
	x0 = x1
	y0 = y1
	z0 = z1
	x1,y1,z1 = minimizar(x0,y0,z0,paso)
	print(f"El valor de x es {x1}, el de y es {y1}, el de z es {z1} en la {ite} iteracion con un error de {np.sqrt((x0 - x1)**2  + (y0 - y1)**2 )}\n\n")
	ite += 1
ite  = 1

x0,y0,z0 = 1,1,1
x1,y1,z1 = maximizar(x0,y0,z0,paso)

print("\n\n\n\nMaximizacion de la funcion:")
print(x1,y1,z1)
print(f"El valor de x es {x1}, el de y es {y1}, el de z es {z1} en la {ite} iteracion con un error de {np.sqrt((x0 - x1)**2  + (y0 - y1)**2 )}\n\n")

while np.sqrt((x0 - x1)**2  + (y0 - y1)**2 + (z0-z1)**2 )  > tolerancia and ite <= 4:	
	x0,y0, z1 = x1,y1,z1 
	x1,y1,z1=maximizar(x0,y0,z0,paso)
	print(f"El valor de x es {x1}, el de y es {y1}, el de z es {z1} en la {ite} ite	racion con un error de {np.sqrt((x0 - x1)**2  + (y0 - y1)**2 )}\n\n")
	ite += 1


