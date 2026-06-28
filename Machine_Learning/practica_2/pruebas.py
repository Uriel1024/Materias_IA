import random

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


def herencia(padres, madres):   
    hijo1 = [[0 for _ in range(cromosomas)] for _ in range (parejas)]
    hijo2 = [[0 for _ in range(cromosomas)] for _ in range (parejas)]

    r1 , r2 = incesto()

    for i in range(parejas):
        for j in range(cromosomas):
            suma = padres[r1[i]][j] + madres[r2[i]][j]
            #hijo1[i][j] = suma // 2
            #hijo2[i][j] = (suma + 1) // 2
            if suma % 2 == 0:
                hijo1[i][j] = suma //2
                hijo2[i][j] = suma //2
            else:
                if random.random() < .5:
                    hijo1[i][j] = suma //2
                    hijo2[i][j] = (suma //2) + 1 
                else:
                    hijo2[i][j] = suma //2
                    hijo1[i][j] = (suma //2) + 1 
    return hijo1,hijo2



if __name__ == "__main__":
    padres, madres = gen_padres()
    print(f"Los padres son {padres}")
    print(f"Las madres son {madres}")

    hijo1, hijo2 = herencia(padres, madres)
    generaciones = 0
    hijo_perfecto = False


    while generaciones != 4:
        generaciones += 1
        print(f"\n\nGeneración: {generaciones}\n")
        
        hijo1, hijo2 = herencia(hijo1, hijo2)
        print(f"hijo1 antes de mutacion:{hijo1} ")
        print(f"hijo2 antes de mutacion:{hijo2} ")

