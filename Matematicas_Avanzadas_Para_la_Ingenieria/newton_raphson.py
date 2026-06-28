def evaluar(funcion, derivada,x, polinomio):
	cociente,residuo  = 0,0 
	for i in range(polinomio):
		cociente += funcion[i]*(x**i)
		residuo += derivada[i]*(x**i)
	return x - (cociente/residuo)
x0 = float(input("Ingresa x_0:"))	
error = float(input('Ingresa el error: '))
polinomio = int(input("Ingresa el grado mas alto del polinomio:"))
polinomio += 1
raices = [0] * polinomio 
derivada = [0] * polinomio
for i in range(polinomio):
	raices[i] = float(input(f"Ingresa el coeficiente de la potencia ^{i}:"))
	#para mandar alv la constante de la funcion 
	if i == 0:	
		continue
	else:
		derivada[i-1] = raices[i] * i 
x = evaluar(raices,derivada,x0,polinomio)
j = 0
while abs(x - x0) > error: 
	x0 = x
	x = evaluar(raices,derivada,x0,polinomio) 
	j+=1
	if j == 10000:
		break
print(f"El valor de la raiz aproximado es: {x}; y se encontró despues de {j} iteraciones.")