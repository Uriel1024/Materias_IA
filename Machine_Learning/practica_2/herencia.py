import random
import math

cromosomas = 20
parejas = 50

def gen_padres():
	madre = [[0 for _ in range(cromosomas)] for _ in range (parejas)] 
	padre = [[0 for _ in range(cromosomas)] for _ in range(parejas)] 
	for i in range(parejas):
		for j in range (cromosomas):
			madre[i][j] = random.randint(1,9)
			padre[i][j] = random.randint(1,9)
	return padre, madre


def herencia(padres, madres):	
	hijo1 = [[0 for _ in range(cromosomas)] for _ in range (parejas)]
	hijo2 = [[0 for _ in range(cromosomas)] for _ in range (parejas)]

	r1 , r2 = incesto()


	for i in range(parejas):
		for j in range(cromosomas):
			suma = padres[r1[i]][j] + madres[r2[i]][j]
			if  suma %2 == 0:
				hijo1[i][j] = suma //2
				hijo2[i][j] = suma //2
			else:
				if random.random()  < 0.5:
					hijo1[i][j] = suma //2
					hijo2[i][j] = (suma //2) + 1
				else:
					hijo2[i][j] = suma //2
					hijo1[i][j] = (suma //2) + 1
	return hijo1,hijo2


def torneo():




	
#para evitar que se reproduzcan entre hermanos y genera %2 ==0:r 
def incesto():
	hermanos = True
	hermano1 = list(range(parejas))
	hermano2 = list(range(parejas))
	while hermanos:
		random.shuffle(hermano1)
		random.shuffle(hermano2)
		hermanos = False
		for i in range(parejas):
			if hermano1[i] == hermano2[i]: 
				hermanos = True
	return hermano1, hermano2



def mutacion(hijos):
	for i in range(parejas):
		if random.random() < 0.15: #el minimo, en caso de ser necesario se debe de agregar un poco mas (hasta 25%)
			j = random.randint(0, cromosomas -1 )
			hijos[i][j] = random.randint(1,9)
	return hijos

def incesto():
	hermanos = True
	hermano1 = list(range(parejas))
	hermano2 = list(range(parejas))
	while hermanos:
		hermanos = False
		random.shuffle(hermano1)
		random.shuffle(hermano2)
		for i in range(parejas):
			if hermano1[i] == hermano2[i]:
				hermanos = True
	return hermano1, hermano2


#para pode encontrar el hijo perfecto (todos los cromosomas con valor de 9). 
def ario(personas):
    return any(all(c == 9 for c in individuo) for individuo in personas)

if __name__ == "__main__":
    padres, madres = gen_padres()
    print(f"Los padres son {padres}")
    print(f"Las madres son {madres}")

    hijo1, hijo2 = herencia(padres, madres)
    generaciones = 0
    hijo_perfecto = False

    while not hijo_perfecto:
        generaciones += 1
        print(f"Generación: {generaciones}\n")

        hijo1, hijo2 = herencia(hijo1, hijo2)
        print(f"hijo1 antes de mutacion:{hijo1} ")
        print(f"hijo2 antes de mutacion:{hijo2} ")

        hijo1 = mutacion(hijo1)
        hijo2 = mutacion(hijo2)
        if ario(hijo1) or ario(hijo2):
            hijo_perfecto = True
        print(f"hijo 1: {hijo1}")
        print(f"hijo 2: {hijo2}")

    print(f"\nÚltima generación:\nHijo1: {hijo1}\nHijo2: {hijo2}")
    print(f"\nDespués de {generaciones} generaciones se encontró al hijo perfecto.")



