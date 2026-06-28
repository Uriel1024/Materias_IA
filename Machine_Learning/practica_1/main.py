import os
from diccionario import pensamiento
from diccionario import preguntas
from diccionario import caracteristicas
from collections import Counter


class Juego_20Q:
    def __init__(self):
        
        self.objetos_posibles = list(pensamiento.keys())
        self.caracteristicas_usadas = set()
        self.respuestas_jugador = {}

    def pregunta_optima(self):
     
        if not self.objetos_posibles:
            return None

        contador = Counter()

        for objeto in self.objetos_posibles:
            caracteristicas_objeto = pensamiento[objeto]
     
            for caracteristica, valor in caracteristicas_objeto.items():
                if valor and caracteristica not in self.caracteristicas_usadas and caracteristica in preguntas:
                    contador[caracteristica] += 1

        if not contador:
            return None

        mejor_caracteristica = None
        mejor_diferencia = float('inf')
        total_objetos = len(self.objetos_posibles)

        for caracteristica, count in contador.items():
            diferencia = abs(count - (total_objetos / 2))
            if diferencia < mejor_diferencia:
                mejor_diferencia = diferencia
                mejor_caracteristica = caracteristica

        return mejor_caracteristica


    def hacer_preguntas(self, caracteristica):
        if caracteristica not in preguntas:
            return None
        pregunta = preguntas[caracteristica]
        while True:
            respuesta = input(pregunta.strip() + " ").lower()
            if respuesta in ['s', 'si', 'y', 'yes']:
                self.respuestas_jugador[caracteristica] = True
                self.caracteristicas_usadas.add(caracteristica)
                return True
            elif respuesta in ['no', 'n']:
                self.respuestas_jugador[caracteristica] = False
                self.caracteristicas_usadas.add(caracteristica)
                return False
            else:
                print('Por favor ingresa una opcion valida(s/n)')


#Filtra los objetos con las caracteristicas que el usuario afirme que tiene
    def filtrar_objetos(self):
        
        nuevos_objetos = []
        
        for objeto in self.objetos_posibles:
            coincide = True
            for caracteristica, valor_esperado in self.respuestas_jugador.items():
                if caracteristica in pensamiento[objeto]:
                    valor_objeto = pensamiento[objeto][caracteristica]
                    if valor_objeto != valor_esperado:
                        coincide = False
                        break
              
                elif valor_esperado: 
                    coincide = False
                    break
            
            if coincide:
                nuevos_objetos.append(objeto)
        
        self.objetos_posibles = nuevos_objetos


    def adivinar(self):
        if len(self.objetos_posibles) == 1: 
            objeto = self.objetos_posibles[0]
            respuesta = input(f"¿Estas pensando en {objeto}? s/n: ").strip().lower()
            if respuesta in ['s', 'si', 'y', 'yes']:
                print("!Adivine!")
                return True
            else:
                print("No logre adivinar :(")
                return False
        elif len(self.objetos_posibles) == 0:
                print("Mi base de conocimientos no contiene lo que buscas, !ganaste!.")
                return True
        return False


    def jugar(self):
        print("Bienvenido al juego 20Q")
        print("Tratare de adivinar lo que tienes en mente.")
        print("Responde con 's' pra si,  o con 'n' para no. \n ")
       
       
        max_preguntas = 20
        preguntas_realizadas = 0

        while preguntas_realizadas < max_preguntas and len(self.objetos_posibles) > 1:
            caracteristica = self.pregunta_optima()

            if not caracteristica:
                break

            respuesta = self.hacer_preguntas(caracteristica)
            preguntas_realizadas += 1
            self.filtrar_objetos()

            print(f"Posibilidads restantes: {len(self.objetos_posibles)}")
            print(f"Numero de preguntas realizadas: {preguntas_realizadas}\n")

            if len(self.objetos_posibles) <=3:
                if self.adivinar():
                    return
                else:
                    break

        car = False
        i = 0
        if len(self.objetos_posibles) > 1:
            print("\nSe me acabaron las preguntas. ¿Acaso estabas pensando en algo de estos?")
            while  i < len(self.objetos_posibles) and not car:
                objeto = self.objetos_posibles[i]
                while True:
                    op = input(f"¿Acaso estabas pensando en {objeto} (s/n)?").lower().strip()

                    if op in ['s', 'si', 'yes', 'y']:
                        print("Adivine, GG ez win.")
                        car = True
                        break
                    elif op in ['n','no']: 
                        break
                    else:
                        print("Opcion no valida, por favor ingresa una opcion valida (s/n).")
                i += 1  
        if not car:
            print("Haz acabado con mi base de conocimientos, ganaste.GG")

                
if __name__ == "__main__":
    juego = Juego_20Q()
    juego.jugar()
