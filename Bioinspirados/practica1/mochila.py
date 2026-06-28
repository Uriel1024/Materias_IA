#Practica realizada por  Rodríguez Juárez Héctor Sajoni y Velazquez Arrieta Eduardo Uriel 
import random
import numpy as np
#delimitamos las variables para no salirnos de los rangos del problema 
n_genes = 7
n_individuos = 10
tot_ob= 10
n_generaciones = 10

dulces = ["decoy_detonators" ,"love_potion", "extendable_ears" ,"skiving_snackbox" ,"fever_fudge", "puking_pastilles","nosebleed_nougat"]
peso = [4,2,5,5,2,1.5,1]
ganancia = [10,8,12,6,3,2,2]

def primera_gen():
	generacion = []
	for _ in range(n_individuos):
		individuo = [0] * n_genes
		individuo[1] = 3
		individuo[3] = 2
		capacidad = 14
		i = random.randint(0,6)
		for j in range(n_genes):
			k = (i+j)%7
			max_obj = int(capacidad // peso[k])
			max_obj = min(max_obj, tot_ob)
			individuo[k] += random.randint(0,max_obj)
			capacidad -= individuo[k]*peso[k]z	
			if k == 1: capacidad += 6
			if k == 3: capacidad += 10
		generacion.append(individuo)
	return generacion

def mutacion(ind):
	if random.random() < .1: #probabilidad de alterar un gen del .1 o 10%
		j = random.randint(0, n_genes -1) #seleccionamos un cromosoma al azar
		#capacidad = np.dot(ind, pesos) // pesos[j]
		capacidad = int(max(0, (30 - np.dot(ind, peso)) // peso[j]))

		if j == 1:
			ind[j] = random.randint(3,capacidad) if capacidad >= 4 else 3
		elif j == 3:
			ind[j] = random.randint(2,capacidad) if capacidad >= 4 else 2
		else:
			ind[j] = random.randint(0,capacidad) if capacidad >= 1 else 0

def get_fitness(cromosoma):
	if cromosoma[1] < 3 or cromosoma[3] < 2 or np.dot(peso,cromosoma) > 30:
		return 1
	return np.dot(ganancia,cromosoma)

def get_ruleta(generacion):
	ruleta = []
	fit = 0
	for individuo in generacion:
		fit += get_fitness(individuo)
		ruleta.append(fit)
	return ruleta
	
#ruleta para que los hijos se reproduzcan con ruleta
def girar_ruleta(ruleta):
	total = ruleta[-1]
	r = random.randint(1, total)
	i = 0
	while i < len(ruleta) and ruleta[i] < r: 
		i += 1
	return i

def reproducir(padre1, padre2):
	hijo1 = [0]*7
	hijo2 = [0]*7
	for i in range(7):
		hijo1[i] = padre1[i]
		hijo2[i] = padre2[i]
		if random.random() < 0.5:
			aux = hijo1[i]
			hijo1[i] = hijo2[i]
			hijo2[i] = aux
	return hijo1, hijo2

def torneo(ind1, ind2, ind3, ind4):
	m = {1: ind1, 2: ind2, 3: ind3, 4: ind4}
	l = [(get_fitness(ind1), 1)]
	l.append((get_fitness(ind2), 2))
	l.append((get_fitness(ind3), 3))
	l.append((get_fitness(ind4), 4))
	l.sort()
	return m[l[0][1]], m[l[1][1]]

if __name__ == '__main__':
	actual_gen = primera_gen()
	
	print(f"Los individios que cumplen la condicion de la primera generacion son: \n\n{actual_gen}\n")
	best = []
	best_fit = 0
	
	for k in range(n_generaciones):
		ruleta = get_ruleta(actual_gen)
		next_gen = []
		for _ in range(n_individuos//2):
			i = girar_ruleta(ruleta)
			j = i
			while j == i:
				j = girar_ruleta(ruleta)
			
			ind1, ind2 = [], []
			if random.random() < .85:
				ind1, ind2 = reproducir(actual_gen[i], actual_gen[j])
				mutacion(ind1)
				mutacion(ind2)
				ind1, ind2 = torneo(ind1, ind2, actual_gen[i], actual_gen[j])
			else:
				ind1 = actual_gen[i]
				ind2 = actual_gen[j]
			
			next_gen.append(ind1)
			next_gen.append(ind2)
			if get_fitness(ind1) > best_fit and np.dot(ind1, peso) <= 30:
				best = ind1.copy()
				best_fit = get_fitness(ind1)
			if get_fitness(ind2) > best_fit and np.dot(ind2, peso) <= 30:
				best = ind2.copy()	
				best_fit = get_fitness(ind2)
			
		actual_gen = next_gen
		print(f"La generacion es {k}: \n {actual_gen}")		

	print(f"La ultima generacion son: \n {actual_gen}")
	print(f'\nEl mejor fit es: {best_fit} \n' )
	print('De la mochila con los siguientes dulces. \n')
	for i in range(len(best)):
		print(f"producto: {dulces[i]}, con una cantidad de {best[i]}\n")
	print('\nCon peso', np.dot(best, peso))
	