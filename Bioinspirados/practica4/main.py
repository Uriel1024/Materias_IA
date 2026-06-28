import random

def reciproco_matriz(matriz, q):
	for i in range(len(matriz)):
		for j in range(len(matriz[0])):
			if matriz[i][j] != 0:
				matriz[i][j] = q / matriz[i][j]
	return matriz

def reciproco_arr(lista, q):
	for i in range(len(lista)):
		if lista[i] != 0:
			lista[i] = q / lista[i]
	return lista

def calc_suma(feromonas, dis_inv, a, b, n):
	suma = [0] * n
	for i in range(n):
		for j in range(n):
			suma[i] += (feromonas[i][j]**a) * (dis_inv[i][j]**b)
	return suma

def fitness(nodosv, distancias):
	dist_total = []
	for camino_h in nodosv:
		hormiga = 0
		if len(camino_h) > 1:
			for i in range(len(camino_h) - 1):
				nodo_inicio = camino_h[i]
				nodo_fin = camino_h[i+1]
				hormiga += distancias[nodo_inicio][nodo_fin]
		dist_total.append(hormiga)
	return dist_total

def actualizar_feromonas(feromonas, nodosv, calidad, p):
	n = len(feromonas)
	# Evaporación
	for i in range(n):
		for j in range(n):
			feromonas[i][j] = (1 - p) * feromonas[i][j]

	# Depósito (usando la 'calidad', que es 1/distancia)
	for k, hormiga in enumerate(nodosv):
		deposito = calidad[k] 

		for i in range(len(hormiga) - 1):
			nodo_i = hormiga[i]
			nodo_f = hormiga[i + 1]

			feromonas[nodo_i][nodo_f] += deposito
			feromonas[nodo_f][nodo_i] += deposito

	return feromonas

def definir_rutas(feromonas, dis_inv, suma, n, a, b):
	proba = [[0.0 for _ in range(n)] for _ in range(n)]

	for i in range(n):
		if suma[i] != 0.0:
			for j in range(n):
				numerador = (feromonas[i][j]**a) * (dis_inv[i][j]**b)
				proba[i][j] = numerador / suma[i]

	camino_actual = [[k] for k in range(n)]
	
	for k in range(n):
		actual = k 
		nodosv = {k} #
		
		while len(nodosv) < n:
			
			proba_disponible = []
			nodos_elegibles = []
			
			# Filtrar nodos no visitados
			for j in range(n):
				if j not in nodosv:
					prob_ij = proba[actual][j]
					proba_disponible.append(prob_ij)
					nodos_elegibles.append(j)
			
			suma_disponible = sum(proba_disponible)
			
			if suma_disponible == 0:
				break
			
			# Construir ruleta de selección
			ruleta = []
			prob_acumulada = 0.0
			for p_val in proba_disponible:
				prob_normalizada = p_val / suma_disponible
				prob_acumulada += prob_normalizada
				ruleta.append(prob_acumulada)

			r = random.uniform(0, 1) 
			idx = 0
			
			while idx < len(ruleta) and ruleta[idx] < r:
				idx += 1
			
			if idx < len(nodos_elegibles):
				siguiente_nodo = nodos_elegibles[idx]
				camino_actual[k].append(siguiente_nodo)
				nodosv.add(siguiente_nodo)
				actual = siguiente_nodo
			else:
				
				break 

		# Para regresar al nodo de inicio si el camino está completo
		if len(camino_actual[k]) == n:
			camino_actual[k].append(k)	
	return camino_actual

def indice_min(lista):
	if not lista:
		return -1, float('inf')
	indc = 0
	valor = lista[0]
	for i in range(1, len(lista)):
		if lista[i] < valor:
			indc = i
			valor = lista[i]
	return indc, valor

# Variables Globales
distancias  = ([
	[0,6,9,17,13,21],
	[6,0,19,21,12,18],
	[9,19,0,20,23,11],
	[17,21,20,0,15,10],
	[13,12,23,15,0,21],
	[21,18,11,10,21,0]
])

p, q, a, b = 0.2, 1.0, 1.5, 0.8 
n = len(distancias)
feromonas = [[0.1 for _ in range(n)] for _ in range(n)]

dis_inv = [row[:] for row in distancias] # Hacer una copia
reciproco_matriz(dis_inv, q)

fitness_global = float('inf')
mejor_ruta_global = []
mejores_rutas = []
ite = 0

if __name__ == '__main__':

	while ite < 50:
		ite += 1
		
		suma = calc_suma(feromonas, dis_inv, a, b, n)
		
		canmino_actual = definir_rutas(feromonas, dis_inv, suma, n, a, b)
		
		fit = fitness(canmino_actual, distancias)
		
		g, best_fitness = indice_min(fit)
		mejor_ruta_actual = canmino_actual[g]
		
		print(f"La mejor ruta de la generación {ite} es {mejor_ruta_actual} con un fitness de {best_fitness}")

		calidad = list(fit) 
		reciproco_arr(calidad, q) 

		feromonas = actualizar_feromonas(feromonas, canmino_actual, calidad, p)
		
		if best_fitness < fitness_global:
			fitness_global = best_fitness
			mejor_ruta_global = mejor_ruta_actual
			
		if best_fitness == 63:
			if mejor_ruta_actual not in mejores_rutas:
				mejores_rutas.append(mejor_ruta_actual)

	print(f"\n\nEl mejor camino que se encontró es: {mejor_ruta_global} con un fitness de {fitness_global} ")
	print(f"\n\nLas mejores rutas (con fitness 63) encontradas son: {mejores_rutas}")