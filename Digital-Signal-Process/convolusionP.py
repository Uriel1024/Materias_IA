import numpy as np
import random
a = random.randint(6,11)
s1 = [random.randint(1,9) for _ in range (random.randint(2,6))]
s2 = [random.randint(1,9) for _ in range (random.randint(2,6))]


print(f"La senial 1 es: {s1}")
print(f"La senial 2 es: {s2}")

if len(s1) > len(s2):
	for i in range(len(s2),len(s1)):
		s2.append(0)
else:
	for i in range(len(s1),len(s2)):
		s1.append(0)

print(f"La senial 1 es: {s1}")
print(f"La senial 2 es: {s2}")

matriz = [[0 for _ in range(len(s1))]for _ in range(len(s1))]
for i in range(len(s1)):
	for j in range(len(s1[:])):
		matriz[i][j] = s1[j-i] 
matriz = np.array(matriz)
s2 = np.array(s2)
print("La matriz es: ")
for i in matriz:
	print(i)
print(f"\n\nLa senial convolucionada es {np.dot(matriz,s2)}")	