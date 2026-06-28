import math
import random as rand
import numpy as np

population = 50
iteraciones = 20
a = 0.8
b1 = 0.7
b2 = 1.2
def target(x, y):
    return x**2 + y**2 + 25 * (math.sin(x) + math.sin(y))

p = np.ones(2*population).reshape(population, 2)
for i in range(population):
    p[i][0] = 5*rand.uniform(-1,1)
    p[i][1] = 5*rand.uniform(-1,1)



pbest = p.copy()
gbest = pbest[0]
for e in p:
    if target(*e) < target(*gbest):
        gbest = e

v = np.zeros(2*population).reshape(population, 2)
print(f'Particula 0, tiempo 0 -> pos: {p[0]}, v: {v[0]}, pbest: {pbest[0]}, gbest: {gbest}, fun: {target(*p[0])}')
for j in range(iteraciones):
    for i in range(population):
        r1 = rand.uniform(0,1)
        r2 = rand.uniform(0,1)
        v[i] = a*v[i] + b1*r1*(pbest[i] - p[i]) + b2*r2*(gbest - p[i])
        p[i] = p[i] + v[i]
        if p[i][0] > 5: p[i][0] = 5
        if p[i][1] > 5: p[i][1] = 5
        if p[i][0] < -5: p[i][0] = -5
        if p[i][1] < -5: p[i][1] = -5
        
        if target(*p[i]) < target(*pbest[i]): pbest[i] = p[i]
        if i == 0:
            print(f'Particula 0, tiempo {j+1} -> pos: {p[i]}, v: {v[i]}, pbest: {pbest[i]}, gbest: {gbest}, fun: {target(*p[i])}')

    if target(*p[i]) < target(*gbest): gbest = p[i]
        
print('Mejor valor:', target(*gbest))