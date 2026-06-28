#Instalar scikit-fuzzy
# pip install scikit-fuzzy

import tkinter as tk
from tkinter import ttk
import matplotlib.pyplot as plt
import numpy as np
import skfuzzy as fuzz
from skfuzzy import control as ctrl

# Universos para entradas y salida
x_pantalla  = np.arange(0, 10.1, 0.1)
x_interrupciones = np.arange(0, 10.1, 1)
x_ruido = np.arange(0,100.1,0.1)
x_fatiga = np.arange(0, 10.1, 0.1)

# Variables lingüisticas de entrada
pantalla = ctrl.Antecedent(x_pantalla, 'pantalla')
interrupciones = ctrl.Antecedent(x_interrupciones, 'interrupciones')
ruido = ctrl.Antecedent(x_ruido, 'ruido')

# Variable para la salida de la fatiga total
fatiga = ctrl.Consequent(x_fatiga, 'fatiga')

# Funciones de membresía para pantalla 
pantalla['pocas'] = fuzz.zmf(x_pantalla, 2, 4)
pantalla['moderadas'] = fuzz.trapmf(x_pantalla, [3, 5, 6, 7])
pantalla['muchas'] = fuzz.smf(x_pantalla, 6, 8)

#Interrupciones
interrupciones['pocas'] = fuzz.zmf(x_interrupciones, 2, 4)
interrupciones['moderadas'] = fuzz.trapmf(x_interrupciones, [3, 5, 6, 7])
interrupciones['muchas'] = fuzz.smf(x_interrupciones, 6,8)

#nivele de ruido 

ruido['bajo'] = fuzz.zmf(x_ruido, 30,50)
ruido['medio'] = fuzz.trapmf(x_ruido, [40, 60, 70, 80])
ruido['alto'] = fuzz.smf(x_ruido, 70,85)

# Funciones de membresía para el nivel de fatiga
fatiga['bajo'] = fuzz.zmf(x_fatiga, 2, 4)
fatiga['medio'] = fuzz.trapmf(x_fatiga, [3, 5, 6, 7])
fatiga['alto'] = fuzz.smf(x_fatiga, 6, 8)

# Reglas difusas    
rules = [
    ctrl.Rule(pantalla['pocas'] & interrupciones['pocas'] & ruido['bajo'], fatiga['bajo']),
    ctrl.Rule(pantalla['moderadas'] & interrupciones['pocas'] & ruido['bajo'], fatiga['bajo']),
    ctrl.Rule(pantalla['muchas'] & interrupciones['pocas'] & ruido['bajo'], fatiga['medio']),

    ctrl.Rule(pantalla['pocas'] & interrupciones['pocas'] & ruido['medio'], fatiga['medio']),
    ctrl.Rule(pantalla['moderadas'] & interrupciones['pocas'] & ruido['medio'], fatiga['medio']),
    ctrl.Rule(pantalla['muchas'] & interrupciones['pocas'] & ruido['medio'], fatiga['alto']),

    ctrl.Rule(pantalla['pocas'] & interrupciones['pocas'] & ruido['alto'], fatiga['bajo']),
    ctrl.Rule(pantalla['moderadas'] & interrupciones['pocas'] & ruido['alto'], fatiga['medio']),
    ctrl.Rule(pantalla['muchas'] & interrupciones['pocas'] & ruido['alto'], fatiga['alto']),



    ctrl.Rule(pantalla['pocas'] & interrupciones['moderadas'] & ruido['bajo'], fatiga['bajo']),
    ctrl.Rule(pantalla['moderadas'] & interrupciones['moderadas'] & ruido['bajo'], fatiga['medio']),
    ctrl.Rule(pantalla['muchas'] & interrupciones['moderadas'] & ruido['bajo'], fatiga['medio']),
    

    ctrl.Rule(pantalla['pocas'] & interrupciones['moderadas'] & ruido['medio'], fatiga['medio']),
    ctrl.Rule(pantalla['moderadas'] & interrupciones['moderadas'] & ruido['medio'], fatiga['medio']),
    ctrl.Rule(pantalla['muchas'] & interrupciones['moderadas'] & ruido['medio'], fatiga['alto']),
    

    ctrl.Rule(pantalla['pocas'] & interrupciones['moderadas'] & ruido['alto'], fatiga['medio']),
    ctrl.Rule(pantalla['moderadas'] & interrupciones['moderadas'] & ruido['alto'], fatiga['alto']),
    ctrl.Rule(pantalla['muchas'] & interrupciones['moderadas'] & ruido['alto'], fatiga['alto']),
    


    ctrl.Rule(pantalla['pocas'] & interrupciones['muchas'] & ruido['bajo'], fatiga['medio']),
    ctrl.Rule(pantalla['moderadas'] & interrupciones['muchas'] & ruido['bajo'], fatiga['alto']),
    ctrl.Rule(pantalla['muchas'] & interrupciones['muchas'] & ruido['bajo'], fatiga['alto']),

    ctrl.Rule(pantalla['pocas'] & interrupciones['muchas'] & ruido['medio'], fatiga['medio']),
    ctrl.Rule(pantalla['moderadas'] & interrupciones['muchas'] & ruido['medio'], fatiga['alto']),
    ctrl.Rule(pantalla['muchas'] & interrupciones['muchas'] & ruido['medio'], fatiga['alto']),


        ctrl.Rule(pantalla['pocas'] & interrupciones['muchas'] & ruido['alto'], fatiga['alto']),
    ctrl.Rule(pantalla['moderadas'] & interrupciones['muchas'] & ruido['alto'], fatiga['alto']),
    ctrl.Rule(pantalla['muchas'] & interrupciones['muchas'] & ruido['alto'], fatiga['alto']),



]

sistema = ctrl.ControlSystem(rules)

# Gráficas de funciones de membresía
def mostrar_graficas():
    fig, axs = plt.subplots(4, 1, figsize=(10, 12))

    # horas de pantalla 
    axs[0].plot(x_pantalla, pantalla['pocas'].mf, label='pocas')
    axs[0].plot(x_pantalla, pantalla['moderadas'].mf, label='moderadas')
    axs[0].plot(x_pantalla, pantalla['muchas'].mf, label='muchas')
    axs[0].set_title('Horas en pantalla (0 a 10)')
    axs[0].set_xticks(np.arange(0, 11, 1))
    axs[0].legend()
    axs[0].grid(True)

    # Interrupciones
    axs[1].plot(x_interrupciones, interrupciones['pocas'].mf, label='pocas')
    axs[1].plot(x_interrupciones, interrupciones['moderadas'].mf, label='moderadas')
    axs[1].plot(x_interrupciones, interrupciones['muchas'].mf, label = 'muchas')
    axs[1].set_title('Interrupciones (0 a 10)')
    axs[1].set_xticks(np.arange(0, 11, 1))
    axs[1].legend()
    axs[1].grid(True)


    # nivel de ruido 

    axs[2].plot(x_ruido, ruido['bajo'].mf, label='bajo')
    axs[2].plot(x_ruido, ruido['medio'].mf, label='medio')
    axs[2].plot(x_ruido, ruido['alto'].mf, label = 'alto')
    axs[2].set_title('Nivel de ruido  (0 a 100)')
    axs[2].set_xticks(np.arange(0, 110, 10))
    axs[2].legend()
    axs[2].grid(True)


    # Propina
    axs[3].plot(x_fatiga, fatiga['bajo'].mf, label='bajo')
    axs[3].plot(x_fatiga, fatiga['medio'].mf, label='medio')
    axs[3].plot(x_fatiga, fatiga['alto'].mf, label='alto')
    axs[3].set_title('Nivel de fatiga (%)')
    axs[3].set_xticks(np.arange(0, 10, 1))
    axs[3].legend()
    axs[3].grid(True)

    plt.tight_layout()  
    plt.show()



# Cálculo automático
def calcular(event=None):
    sim = ctrl.ControlSystemSimulation(sistema)
    sim.input['pantalla'] = horas_slider.get()
    sim.input['interrupciones'] = interrupciones_slider.get()
    sim.input['ruido'] = ruido_slider.get()
    sim.compute()   
    resultado_var.set(f"Nivel de estres: {sim.output['fatiga'] :2.0f}  (de 10)")
    horas_valor.set(f"{horas_slider.get():.1f}")
    interrupciones_valor.set(f"{interrupciones_slider.get():1.0f}")
    ruido_valor.set(f"{ruido_slider.get():.1f}")

    nivel = sim.output['fatiga']
    if nivel < 4:
        mensaje = "Nivel de fatiga: BAJO"
    elif nivel < 7:
        mensaje = "Nivel de fatiga: MEDIO"
    else:
        mensaje = "Nivel de fatiga: ALTO"

    resultado_var.set(f"{mensaje} ({nivel:.1f} de 10)")


# Crear GUI
ventana = tk.Tk()
ventana.title("Nivel de estres")
ventana.geometry("854x480")

frame = tk.Frame(ventana)
frame.pack(pady=5, fill='x')

# Horas en pantalla  slider
tk.Label(frame, text="Horas en pantalla(0 a 10):").pack(anchor='w')
horas_slider = ttk.Scale(frame, from_=0, to=10, orient='horizontal', command=calcular)
horas_slider.pack(fill='x', padx=10)
horas_valor = tk.StringVar(value="0.0")
tk.Label(frame, textvariable=horas_valor).pack(anchor='e', padx=10)

#Numero de interrupciones slider
tk.Label(frame, text="Numero de interrupciones (0 a 10):").pack(anchor='w')
interrupciones_slider = ttk.Scale(frame, from_=0, to=10, orient='horizontal', command=calcular)
interrupciones_slider.pack(fill='x', padx=10)
interrupciones_valor = tk.StringVar(value="0.0")
tk.Label(frame, textvariable=interrupciones_valor).pack(anchor='e', padx=10)

#Nivel de ruido slider
tk.Label(frame, text="Nivel de ruido (0 a 100):").pack(anchor='w')
ruido_slider = ttk.Scale(frame, from_=0, to=100, orient='horizontal', command=calcular)
ruido_slider.pack(fill='x', padx=10)
ruido_valor = tk.StringVar(value="0.0")
tk.Label(frame, textvariable=ruido_valor).pack(anchor='e', padx=10)



# Resultados
resultado_var = tk.StringVar()
tk.Label(ventana, textvariable=resultado_var, font=('Arial', 12)).pack(pady=10)
tk.Button(ventana, text="Ver funciones de membresía", command=mostrar_graficas).pack(pady=5)

ventana.mainloop()