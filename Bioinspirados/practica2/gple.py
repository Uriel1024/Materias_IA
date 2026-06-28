from gplearn.genetic import SymbolicRegressor
from sklearn.ensemble import RandomForestRegressor
from sklearn.tree import DecisionTreeRegressor
from sklearn.utils.random import check_random_state
from mpl_toolkits.mplot3d import Axes3D
import matplotlib.pyplot as plt
import numpy as np
import graphviz

x0 = np.arange(-1,1,1/10.)
x1 = np.arange(-1,1,1/10.)

x0, x1 = np.meshgrid(x0,x1)
y_truth = x**3 * 5 * y**2 = x/2


ax = plt.figure().add_subplot(projection='3d')
ax.set_xlim(-1,1)
ax.set_ylim(-1,1)
surf = ax.plot_surface(x0,x1,y_truth,rstride=1,cstride=1, color='blue', alpha=0.5)
plt.show()

rng = check_random_state(0)


#muestras para el entrenamiento 
x_train = rng.uniform(-1,1,100).reshape(50,2)
y_train = x_train[:,0]**2 - x_train[:,1]**2 +x_train[:,1] -1  

#muestras para la evaluacion 
x_test = rng.uniform(-1,1,100).reshape(50,2)
y_test = x_test[:,0]**2 - x_test[:,1]**2 + x_test[:,1] -1


#definimos el modelo con las metricas para entrenamiento
est_gp = SymbolicRegressor(population_size=5000,
                           generations=20, stopping_criteria=0.01,
                           p_crossover=0.7, p_subtree_mutation=0.1,
                           p_hoist_mutation=0.05, p_point_mutation=0.1,
                           max_samples=0.9, verbose=1,
                           parsimony_coefficient=0.01, random_state=0)
#entrenamos el modelo
est_gp.fit(x_train, y_train)

print(est_gp._program)

est_tree = DecisionTreeRegressor()
est_tree.fit(x_train,y_train)
est_rf = DecisionTreeRegressor()
est_rf.fit(x_train,y_train)

y_gp = est_gp.predict(np.c_[x0.ravel(), x1.ravel()]).reshape(x0.shape)
score_gp = est_gp.score(x_test,y_test)
y_tree = est_tree.predict(np.c_[x0.ravel(), x1.ravel()]).reshape(x0.shape)
score_tree = est_tree.score(x_test,y_test)
y_rf = est_rf.predict(np.c_[x0.ravel(), x1.ravel()]).reshape(x0.shape)
score_rf = est_rf.score(x_test,y_test)

fig = plt.figure(figsize=(12,10))


for i, (y, score, title) in enumerate([(y_truth, None, "Ground Truth"),
                                       (y_gp, score_gp, "SymbolicRegressor"),
                                       (y_tree, score_tree, "DecisionTreeRegressor"),
                                       (y_rf, score_rf, "RandomForestRegressor")]):

    ax = fig.add_subplot(2, 2, i+1, projection='3d')
    ax.set_xlim(-1, 1)
    ax.set_ylim(-1, 1)
    surf = ax.plot_surface(x0, x1, y, rstride=1, cstride=1, color='green', alpha=0.5)
    points = ax.scatter(x_train[:, 0], x_train[:, 1], y_train)
    if score is not None:
        score = ax.text(-.7, 1, .2, "$R^2 =\/ %.6f$" % score, 'x', fontsize=14)
    plt.title(title)
plt.show()

