 # Rodríguez Juárez Héctor Sajoni
# Velásquez Arrieta Eduardo Uriel

import random
import numpy as np

limite = 5
iteraciones = 50
num_obreras = 20
num_observadoras = 20
tot_ob= 10
dulces = ["decoy_detonators" ,"love_potion", "extendable_ears" ,"skiving_snackbox" ,"fever_fudge", "puking_pastilles","nosebleed_nougat"]
peso = [4,2,5,5,2,1.5,1]
ganancia = [10,8,12,6,3,2,2]
minimo = [0, 3, 0, 2, 0, 0, 0]

def explorar():
	individuo = [0] * 7
	individuo[1] = 3
	individuo[3] = 2
	capacidad = 14
	i = random.randint(0,6)
	for j in range(7):
		k = (i+j)%7
		max_obj = capacidad // peso[k]
		max_obj = min(max_obj, tot_ob)
		r1 = random.uniform(0,1)
		individuo[k] += round(r1*(max_obj - minimo[0])) # random.randint(0,max_obj)
		capacidad -= individuo[k]*peso[k]
		if k == 1: capacidad += 6
		if k == 3: capacidad += 10
	
	#for i in range(7):
	#	r1 = random.uniform(0,1)
	#	individuo[i] += round(r1*(tot_ob-individuo[i]))
	return individuo

def primera_gen():
	generacion = []
	best_fit = 0
	best = []
	for _ in range(num_obreras):
		abeja = explorar()
		fit = get_fitness(abeja)
		if fit > best_fit:
			best = abeja.copy()
			best_fit = fit
		generacion.append(abeja)
	return generacion, best, best_fit

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
	#print(i)
	return i

if __name__ == '__main__':
	abejas, best, best_fit = primera_gen()
	
	print(f"Los individios que cumplen la condicion de la primera iteracion son: \n\n{abejas}\n")
	repeticiones = [0]*num_obreras
	fit = [get_fitness(abeja) for abeja in abejas]
	
	for _ in range(iteraciones):
		# Ciclo de obreras
		for i in range(num_obreras):
			j = random.randint(0,6)
			k = i
			while k == i:
				k = random.randint(0,num_obreras-1)
			r2 = random.uniform(-1,1)
			nueva_sol = abejas[i].copy()
			nueva_sol[j] = round(abejas[i][j] + r2*(abejas[i][j] - abejas[k][j]))
			
			nueva_sol[j] = max(minimo[j],nueva_sol[j])
			nueva_sol[j] = min(tot_ob,nueva_sol[j])
            
			f = get_fitness(nueva_sol)
			if f > fit[i]:
				abejas[i] = nueva_sol
				fit[i] = f
			else: repeticiones[i]+=1
			if repeticiones[i] >= limite:
				abejas[i] = explorar()
				
			if f > best_fit:
				best = abejas[i].copy()
				best_fit = f
				
		ruleta = get_ruleta(abejas)
		# Ciclo de obseravadoras
		for _ in range(num_observadoras):
			i = girar_ruleta(ruleta)
			observadora = abejas[i].copy()
			j = random.randint(0,6)
			k = i
			while k == i:
				k = random.randint(0,num_obreras-1)
			r2 = random.uniform(-1,1)
			observadora[j] = round(observadora[j] + r2*(observadora[j] - abejas[k][j]))
			f = get_fitness(observadora)
			if f > fit[i]:
				abejas[i] = nueva_sol
				fit[i] = f
			else: repeticiones[i]+=1
			if repeticiones[i] >= limite:
				abejas[i] = explorar()
		#print('el mejor fit es', best_fit, 'del sujeto:', best, 'con peso', np.dot(best, peso))


	print('el mejor fit es', best_fit, 'del sujeto:', best, 'con peso', np.dot(best, peso))
