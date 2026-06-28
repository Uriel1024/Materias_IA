import random
n = 6
def get_diezmado(original,pos,valores):
	diezmado ,new_pos = [], []
	input("\n\nLa senial con diezmado es (presiona cualquier tecla para continuar)")
	for i in range(n):
		if (pos[i] == 0) or (pos[i] % valores[0]== 0):
			diezmado.append(original[i])
			new_pos.append(pos[i]//valores[0])
	return diezmado,new_pos


def get_interpolado(original,valores):
	interpolacion = []
	new_pos=[]
	input("\n\nLa senial con interpolacion es (presiona cualquier tecla para continuar)")
	#se usa interpolacion con el metodo escalonado
	for j in range(n):
		for k in range(valores[1]):
			interpolacion.append(original[j])

	for i in range(n*valores[1]):
			new_pos.append(i - origen*valores[1])
	return interpolacion,new_pos


def get_desplazamiento(original, pos, des):
	despl = original.copy()
	new_pos = []
	input("\n\nLa senial con desplazamiento es (presiona cualquier tecla para continuar)")
	a = [0 for _ in range(abs(des))]
	if des > 0:
		despl = a + despl
		inicio_pos = pos[0] - des	
	else:
		despl = despl + a
		inicio_pos = pos[0] - des 
	nueva_longitud = len(original) + abs(des)
	for i in range(nueva_longitud):
		new_pos.append(inicio_pos + i) 
	return despl, new_pos

original = [random.randint(1,9) for _ in range(n)]
origen = random.randint(0,n-1)
pos = []
for i in range(n):
	pos.append(i-origen)
operaciones = ['Diezmado', 'Interpolacion', 'Desplazamiento']
print(original)
print(pos)
valores = [random.randint(1,3) for _ in range(2)]
for i in range(2):
	print(f"El valor de {operaciones[i]} es : {valores[i]}")
des = random.randint(-3,3)
print(f"El valor de {operaciones[2]} es: {des} ")

print(get_diezmado(original,pos,valores))
print(get_interpolado(original,valores))
print(get_desplazamiento(original,pos,des))
