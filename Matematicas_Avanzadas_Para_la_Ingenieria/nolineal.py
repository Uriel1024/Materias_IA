import numpy as np

def jacobiano_inv(xn):
	print(f"\nEl valor de la matriz es {np.linalg.inv([[2*xn[0],2*xn[1]],[2*xn[0],-1]])}")	
	return np.linalg.inv([[2*xn[0],2*xn[1]],[-2*xn[0],1]])

def f1(x,y):
	return x**2 + y**2 -1

def f2(x,y):
	return y - x**2
	
def jacobiano(x0):
	x_e = [f1(x0[0],x0[1]), f2(x0[0],x0[1])]
	jaco = jacobiano_inv(x0)
	print(f"EL valor del vector xe es {x_e}")
	return x0 - (np.dot(jaco,x_e)) 

x0 = np.array([1,1])
error,ite = .005,1
xf =  jacobiano(x0)
x0 = np.array([1,1])

print(f"Tiene un error de {np.linalg.norm(x0 - xf)}")
print(f"El valor de las variables se encontro despues de {ite} iteraciones y es: ")
for i in range(len(xf)):
	print(f"Para la variable x{i+1} es  {xf[i]}")

while(error < np.linalg.norm(x0 - xf)):
	x0 = xf
	xf =  jacobiano(x0)
	ite += 1
	print(f"\n\nEl valor de las variables  {ite} iteraciones y es: ")
	for i in range(len(xf)):
		print(f"Para la variable x{i+1} es  {xf[i]}")
	print(f"Tiene un error de {np.linalg.norm(x0 - xf)}")
