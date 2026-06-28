
import random
import numpy as np

N_INDIVIDUOS = 100
N_GENERACIONES = 500


#falta dejar una generacion donde los trabajos optimos por maquina donde sean conocidos los mejores casos
 
a, b = 20,100
tiempos_maquinas = [[random.randint(1,10) for _ in range(a)]for _ in range(b)]
N_GENES = b 
	
def primera_gen(n_trabajos, n_maquinas, n_individuos):
	generacion = []
	for _ in range(n_individuos):
		individuo = [random.randint(0, n_maquinas - 1) for _ in range(n_trabajos)]
		generacion.append(individuo)
	return generacion


def calcular_makespan(cromosoma, tiempos_maquinas, n_maquinas):
	cargas_maquinas = [0] * n_maquinas
    
    
	for trabajo_index, maquina_asignada in enumerate(cromosoma):
        

		tiempo_p = tiempos_maquinas[trabajo_index][maquina_asignada] 
        
		cargas_maquinas[maquina_asignada] += tiempo_p
    
	makespan = max(cargas_maquinas)
	return makespan

def get_fitness(cromosoma, tiempos_maquinas, n_maquinas):
	makespan = calcular_makespan(cromosoma, tiempos_maquinas, n_maquinas)
	if makespan == 0:
		return 10000 
	return 1.0 / makespan

def mutacion(ind, n_maquinas):
	PROB_MUTACION = 0.1
	if random.random() < PROB_MUTACION:
		j = random.randint(0, len(ind) - 1) 
		nueva_maquina = random.randint(0, n_maquinas - 1)
		ind[j] = nueva_maquina

def reproducir(padre1, padre2):
	n_genes = len(padre1)
	punto_corte = random.randint(1, n_genes - 1)
	hijo1 = padre1[:punto_corte] + padre2[punto_corte:]
	hijo2 = padre2[:punto_corte] + padre1[punto_corte:]
	return hijo1, hijo2

def get_ruleta(generacion, tiempos_maquinas, n_maquinas):
	ruleta = []
	fit = 0
	for individuo in generacion:
		fit += get_fitness(individuo, tiempos_maquinas, n_maquinas) 
		ruleta.append(fit)
	return ruleta
	
def girar_ruleta(ruleta):
	total = ruleta[-1]
	r = random.uniform(0, total)
	i = 0
	while i < len(ruleta) and ruleta[i] < r:
		i += 1
	return i

def torneo(ind1, ind2, ind3, ind4, tiempos_maquinas, n_maquinas):
	m = {1: ind1, 2: ind2, 3: ind3, 4: ind4}
	
	l = [(get_fitness(ind1, tiempos_maquinas, n_maquinas), 1)]
	l.append((get_fitness(ind2, tiempos_maquinas, n_maquinas), 2))
	l.append((get_fitness(ind3, tiempos_maquinas, n_maquinas), 3))
	l.append((get_fitness(ind4, tiempos_maquinas, n_maquinas), 4))
    
	l.sort(key=lambda item: item[0], reverse=True) 
	return m[l[0][1]], m[l[1][1]]


if __name__ == '__main__':
	actual_gen = primera_gen(N_GENES, a, N_INDIVIDUOS)
	
	best_makespan = float('inf')
	best_cromosoma = []
	
	print(f"Trabajos (n): {N_GENES}, maquinas (m): {a}\n")

	for k in range(N_GENERACIONES):
		ruleta = get_ruleta(actual_gen, tiempos_maquinas, a)
		next_gen = []
		
		current_best_makespan = float('inf')
		current_best_cromosoma = None
		for ind in actual_gen:
			ms = calcular_makespan(ind, tiempos_maquinas, a)
			if ms < current_best_makespan:
				current_best_makespan = ms
				current_best_cromosoma = ind
		
		if current_best_makespan < best_makespan:
			best_makespan = current_best_makespan
			best_cromosoma = current_best_cromosoma
		
		for _ in range(N_INDIVIDUOS//2):
			i = girar_ruleta(ruleta)
			j = i
			while j == i:
				j = girar_ruleta(ruleta)
			
			padre1 = actual_gen[i]
			padre2 = actual_gen[j]
			
			hijo1, hijo2 = reproducir(padre1, padre2)
            
			mutacion(hijo1, a)
			mutacion(hijo2, a)
			
			finalista1, finalista2 = torneo(hijo1, hijo2, padre1, padre2, tiempos_maquinas, a)
			
			next_gen.append(finalista1)
			next_gen.append(finalista2)
			
		actual_gen = next_gen
		print(f"Gen {k+1}: mejor fitness encontrado hasta ahora: {best_makespan}")		

	print(f'El mejor fitness  encontrado es: {best_makespan}')
	print(f'Con la asignacion : {best_cromosoma}')
	
	cargas_finales = [0] * a
	print("\nInterpretación de la asignación:")
	for trabajo_index, maquina_asignada in enumerate(best_cromosoma):
		tiempo_p = tiempos_maquinas[trabajo_index][maquina_asignada] 
		cargas_finales[maquina_asignada] += tiempo_p
		print(f"Trabajo {trabajo_index+1} asignado a Máquina {maquina_asignada+1}. Tiempo requerido: {tiempo_p}")

	print(f"\n: {cargas_finales}")