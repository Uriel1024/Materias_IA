#Instalar scikit-fuzzy
# pip install scikit-fuzzy

import tkinter as tk
from tkinter import ttk
import matplotlib.pyplot as plt
import numpy as np
import skfuzzy as fuzz
from skfuzzy import control as ctrl

# Universos para entradas y salida
x_servicio = np.arange(0, 10.1, 0.1)
x_comida = np.arange(0, 10.1, 0.1)
x_propina = np.arange(0, 30.1, 0.1)

# Variables lingüisticas de entrada
servicio = ctrl.Antecedent(x_servicio, 'servicio')
comida = ctrl.Antecedent(x_comida, 'comida')

# Variable lingüistica de salida
propina = ctrl.Consequent(x_propina, 'propina')

# Funciones de membresía para servicio y comida
servicio['pésimo'] = fuzz.zmf(x_servicio, 2, 4)
servicio['regular'] = fuzz.trapmf(x_servicio, [3.5, 5, 6.5, 7.5])
servicio['excelente'] = fuzz.smf(x_servicio, 7, 9)

comida['asquerosa'] = fuzz.zmf(x_comida, 2, 5)
comida['deliciosa'] = fuzz.smf(x_comida, 5, 8)

# Funciones de membresía para propina
propina['mísera'] = fuzz.zmf(x_propina, 5, 10)
propina['regular'] = fuzz.trapmf(x_propina, [8, 12, 18, 22])
propina['generosa'] = fuzz.smf(x_propina, 20, 28)

# Reglas difusas
rules = [
    ctrl.Rule(servicio['pésimo'] & comida['asquerosa'], propina['mísera']),
    ctrl.Rule(servicio['regular'] & comida['asquerosa'], propina['mísera']),
    ctrl.Rule(servicio['excelente'] & comida['asquerosa'], propina['mísera']),
    ctrl.Rule(servicio['pésimo'] & comida['deliciosa'], propina['regular']),
    ctrl.Rule(servicio['regular'] & comida['deliciosa'], propina['regular']),
    ctrl.Rule(servicio['excelente'] & comida['deliciosa'], propina['generosa']),
]

sistema = ctrl.ControlSystem(rules)

# Gráficas de funciones de membresía
def mostrar_graficas():
    fig, axs = plt.subplots(3, 1, figsize=(10, 12))

    # Servicio
    axs[0].plot(x_servicio, servicio['pésimo'].mf, label='pésimo')
    axs[0].plot(x_servicio, servicio['regular'].mf, label='regular')
    axs[0].plot(x_servicio, servicio['excelente'].mf, label='excelente')
    axs[0].set_title('Servicio (0 a 10)')
    axs[0].set_xticks(np.arange(0, 11, 1))
    axs[0].legend()
    axs[0].grid(True)

    # Comida
    axs[1].plot(x_comida, comida['asquerosa'].mf, label='asquerosa')
    axs[1].plot(x_comida, comida['deliciosa'].mf, label='deliciosa')
    axs[1].set_title('Comida (0 a 10)')
    axs[1].set_xticks(np.arange(0, 11, 1))
    axs[1].legend()
    axs[1].grid(True)

    # Propina
    axs[2].plot(x_propina, propina['mísera'].mf, label='mísera')
    axs[2].plot(x_propina, propina['regular'].mf, label='regular')
    axs[2].plot(x_propina, propina['generosa'].mf, label='generosa')
    axs[2].set_title('Propina (%)')
    axs[2].set_xticks(np.arange(0, 31, 5))
    axs[2].legend()
    axs[2].grid(True)

    plt.tight_layout()
    plt.show()

# Cálculo automático
def calcular(event=None):
    sim = ctrl.ControlSystemSimulation(sistema)
    sim.input['servicio'] = servicio_slider.get()
    sim.input['comida'] = comida_slider.get()
    sim.compute()
    resultado_var.set(f"Propina sugerida: {sim.output['propina']:.2f} %")
    servicio_valor.set(f"{servicio_slider.get():.1f}")
    comida_valor.set(f"{comida_slider.get():.1f}")


# Crear GUI
ventana = tk.Tk()
ventana.title("Propina Difusa")
ventana.geometry("440x320")

frame = tk.Frame(ventana)
frame.pack(pady=5, fill='x')

# Servicio slider
tk.Label(frame, text="Servicio (0 a 10):").pack(anchor='w')
servicio_slider = ttk.Scale(frame, from_=0, to=10, orient='horizontal', command=calcular)
servicio_slider.pack(fill='x', padx=10)
servicio_valor = tk.StringVar(value="0.0")
tk.Label(frame, textvariable=servicio_valor).pack(anchor='e', padx=10)

# Comida slider
tk.Label(frame, text="Comida (0 a 10):").pack(anchor='w')
comida_slider = ttk.Scale(frame, from_=0, to=10, orient='horizontal', command=calcular)
comida_slider.pack(fill='x', padx=10)
comida_valor = tk.StringVar(value="0.0")
tk.Label(frame, textvariable=comida_valor).pack(anchor='e', padx=10)

# Resultado
resultado_var = tk.StringVar()
tk.Label(ventana, textvariable=resultado_var, font=('Arial', 12)).pack(pady=10)

tk.Button(ventana, text="Ver funciones de membresía", command=mostrar_graficas).pack(pady=5)

ventana.mainloop()
