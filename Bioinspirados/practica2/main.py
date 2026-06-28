import operator
import math
import random
import numpy
from functools import partial
from deap import algorithms
from deap import base
from deap import creator
from deap import tools
from deap import gp

# Define new functions
def pDiv(left, right):
    try:
        return left / right
    except ZeroDivisionError:
        return 1
    
def myPow(base, exp):
    exp = exp // 1
    if base == 0:
        return 0
    try:
        return base**exp
    except:
        return 1

pset = gp.PrimitiveSet("MAIN", 2)
pset.addPrimitive(operator.add, 2)
pset.addPrimitive(operator.mul, 2)
pset.addPrimitive(pDiv, 2)
#pset.addPrimitive(myPow, 2)
pset.addEphemeralConstant("rand105", partial(random.randint, 2, 5))
pset.renameArguments(ARG0='x', ARG1='y')



creator.create("FitnessMin", base.Fitness, weights=(-1.0,))
creator.create("Individual", gp.PrimitiveTree, fitness=creator.FitnessMin)

toolbox = base.Toolbox()
toolbox.register("expr", gp.genHalfAndHalf, pset=pset, min_=1, max_=2)
toolbox.register("individual", tools.initIterate, creator.Individual, toolbox.expr)
toolbox.register("population", tools.initRepeat, list, toolbox.individual)
toolbox.register("compile", gp.compile, pset=pset)

def evalSymbReg(individual, points):
    # Transform the tree expression in a callable function
    func = toolbox.compile(expr=individual)
    # Evaluate the mean squared error between the expression
    # and the real function : x^3 * 5y^2 + x/2
    sqerrors = ((func(x, y) - x**3 * 5*y**2 - x/2)**2 for x, y in points)
    return math.fsum(sqerrors) / len(points),

POINTS = [
    (x/10., y/10.) 
    for x in range(-50, 50, 10) 
    for y in range(-50, 50, 10)
]

toolbox.register("evaluate", evalSymbReg, points=POINTS)
toolbox.register("select", tools.selTournament, tournsize=20)
#toolbox.register("select", tools.selRoulette)
toolbox.register("mate", gp.cxOnePoint)
toolbox.register("expr_mut", gp.genFull, min_=0, max_=2)
toolbox.register("mutate", gp.mutUniform, expr=toolbox.expr_mut, pset=pset)

toolbox.decorate("mate", gp.staticLimit(key=operator.attrgetter("height"), max_value=8))
toolbox.decorate("mutate", gp.staticLimit(key=operator.attrgetter("height"), max_value=8))

def main():
    estado_aleatorio = random.getstate()

    pop = toolbox.population(n=70)
    hof = tools.HallOfFame(1)

    stats_fit = tools.Statistics(lambda ind: ind.fitness.values)
    stats_size = tools.Statistics(len)
    mstats = tools.MultiStatistics(fitness=stats_fit, size=stats_size)
    mstats.register("avg", numpy.mean)
    mstats.register("std", numpy.std)
    mstats.register("min", numpy.min)
    mstats.register("max", numpy.max)

    a = 8
    #while a > 1:
    pop, log = algorithms.eaSimple(pop, toolbox, 
        0.5, 0.1, 50, stats=mstats, halloffame=hof, verbose=True)
    print(hof[0])
    func = toolbox.compile(hof[0])
    sqerrors = ((func(x, y) - x**3 * 5*y**2 - x/2)**2 for x, y in POINTS)
    a = math.fsum(sqerrors) / len(POINTS)
    print(a)
    
    with open('estado_random.txt', 'w') as archivo:
        archivo.write(str(estado_aleatorio))

    return pop, log, hof

if __name__ == "__main__":
    main()