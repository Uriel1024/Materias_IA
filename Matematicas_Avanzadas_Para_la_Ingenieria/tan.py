import numpy as np

def jacobiano_inv(xn):
	x,y,z = xn	
	x_row = [y* np.cos(x*y) - z*np.exp(-x*z), x*np.cos(x*y) ,-x*np.exp(-x*z)]
	y_row = [z*x/np.sqrt(x**2 + y**2 ),  z*y/np.sqrt(x**2 + y**2 ), np.sqrt(x**2 + y**2) ]
	sec2 = 1 / (np.cos(x/y)**2)
	z_row = [sec2 * (-y / x**2), sec2 * (1 / x), -np.sin(z) ]
	matrix = [x_row,y_row,z_row]
	print(f"El  valor de la matriz inversa es: \n {np.linalg.inv(matrix)}")
	return np.linalg.inv(matrix)

def f1(x,y,z):
	return np.sin(x*y) + np.exp(-x*z) - .9

def f2(x,y,z):
	return z * np.sqrt(x**2 + y**2 ) - 6.7

def f3(x,y,z):
	return 	np.tan(y/x) + np.cos(z) + 3.2

def jacobiano(x0):
	x_e = [f1(x0[0],x0[1],x0[2]), f2(x0[0],x0[1],x0[2]), f3(x0[0],x0[1],x0[2])]
	jaco = jacobiano_inv(x0)
	print(f"EL valor del vector xe es {x_e}")
	return x0 - (np.dot(jaco,x_e)) 

error,ite = .8,1
x0 = np.array([1,1,1])
xf =  jacobiano(x0)

print(f"Tiene un error de {np.linalg.norm(x0 - xf)}")
print(f"El valor de las variables se encontro despues de {ite} iteraciones y es: ")
for i in range(len(xf)):
	print(f"Para la variable x{i+1} es  {xf[i]}")

while(np.linalg.norm(x0 - xf) > error ):
	x0 = xf	
	xf =  jacobiano(x0)
	ite += 1
	print(f"\n\nEl valor de las variables  {ite} iteraciones y es: ")
	for i in range(len(xf)):
		print(f"Para la variable x{i+1} es  {xf[i]}")
	print(f"Tiene un error de {np.linalg.norm(x0 - xf)}")
