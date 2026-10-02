#include <stdio.h>
#include <stdlib.h>
#include "pila.h"

#define MAX 100

typedef struct {
    char *c1;
} Cadenas;

int menu() {
    printf("\n\n1. Union \n");
    printf("2. Interseccion \n");
    printf("3. Diferencia \n");
    printf("4. Concatenacion \n");
    printf("5. Potencia \n");
    printf("-1. Salir \n");
    printf("Ingresa la operacion que deseas realizar: ");
    int op;
    scanf("%d", &op);
    return op;
}

int strlen_chafa(const char *cadena) {
    int contador = 0;
    while (cadena[contador] != '\0') {
        contador++;
    }
    return contador;
}

char* invertirCadena(const char *cad) {
    int n = strlen_chafa(cad);
    char *res = (char *)malloc((n + 1) * sizeof(char));
    if (res == NULL) {
        return NULL;
    }

    for (int i = 0; i < n; i++) {
        res[i] = cad[n - 1 - i];
    }
    res[n] = '\0';

    return res;
}

char* concatenarCadenas(const char *s1, const char *s2) {
    int len1 = strlen_chafa(s1);
    int len2 = strlen_chafa(s2);
    char *res = (char *)malloc((len1 + len2 + 1) * sizeof(char));
    if (res == NULL) return NULL;

    int idx = 0;
    for (int i = 0; i < len1; i++) {
        res[idx++] = s1[i];
    }
    for (int j = 0; j < len2; j++) {
        res[idx++] = s2[j];
    }
    res[idx] = '\0';
    return res;
}

void recorrerPila(Pila *p) {
    if (isEmpty(p)) {
        printf("La pila está vacía.\n");
        return;
    }

    Nodo *actual = p->Tope;
    while (actual != NULL) {
        printf("Elemento: %s\n", actual->palabra);
        actual = actual->sig;
    }
}

Pila invertirPila(Pila *origen) {
    Pila aux, copia;
    ini(&aux);
    ini(&copia);

    Nodo *actual = origen->Tope;
    while (actual != NULL) {
        char *cadInvertida = invertirCadena(actual->palabra);
        push(&aux, crearNodo(cadInvertida));
        free(cadInvertida);
        actual = actual->sig;
    }

    while (!isEmpty(&aux)) {
        Nodo *nodo = pop(&aux);
        push(&copia, nodo);
    }

    return copia;
}

void cargarPilaDesdeArreglo(Pila *p, char arreglo[][MAX], int tam) {
    ini(p);
    for (int i = 0; i < tam; i++) {
        if (!existeEnPila(p, arreglo[i])) {
            push(p, crearNodo(arreglo[i]));
        }
    }
}

Pila unionAlfabetos(Pila *p1, Pila *p2) {
    Pila resultado;
    ini(&resultado);

    Nodo *aux = p1->Tope;
    while (aux != NULL) {
        if (!existeEnPila(&resultado, aux->palabra)) {
            push(&resultado, crearNodo(aux->palabra));
        }
        aux = aux->sig;
    }

    aux = p2->Tope;
    while (aux != NULL) {
        if (!existeEnPila(&resultado, aux->palabra)) {
            push(&resultado, crearNodo(aux->palabra));
        }
        aux = aux->sig;
    }

    return resultado;
}

Pila interseccion(Pila *p1, Pila *p2) {
    Pila resultado;
    ini(&resultado);

    Nodo *aux = p1->Tope;
    while (aux != NULL) {
        if (existeEnPila(p2, aux->palabra) && !existeEnPila(&resultado, aux->palabra)) {
            push(&resultado, crearNodo(aux->palabra));
        }
        aux = aux->sig;
    }
    return resultado;
}

Pila diferencia(Pila *p1, Pila *p2) {
    Pila resultado;
    ini(&resultado);

    Nodo *aux = p1->Tope;
    while (aux != NULL) {
        if (!existeEnPila(p2, aux->palabra) && !existeEnPila(&resultado, aux->palabra)) {
            push(&resultado, crearNodo(aux->palabra));
        }
        aux = aux->sig;
    }
    return resultado;
}

Pila concatenacion(Pila *p1, Pila *p2) {
    Pila resultado;
    ini(&resultado);

    Nodo *aux1 = p1->Tope;
    while (aux1 != NULL) {
        Nodo *aux2 = p2->Tope;
        while (aux2 != NULL) {
            char *nuevaCad = concatenarCadenas(aux1->palabra, aux2->palabra);
            if (nuevaCad != NULL) {
                if (!existeEnPila(&resultado, nuevaCad)) {
                    push(&resultado, crearNodo(nuevaCad));
                }
                free(nuevaCad);
            }
            aux2 = aux2->sig;
        }
        aux1 = aux1->sig;
    }
    return resultado;
}

Pila obtenerPotencia(Pila *p1, int potencia) {
    Pila actual;
    ini(&actual);

    if (potencia == 0) {
        push(&actual, crearNodo(""));
        return actual;
    }

    Pila base;
    if (potencia < 0) {
        base = invertirPila(p1);
        potencia = -potencia;
    } else {
        ini(&base);
        Nodo *aux = p1->Tope;
        while (aux != NULL) {
            push(&base, crearNodo(aux->palabra));
            aux = aux->sig;
        }
    }

    Nodo *aux = base.Tope;
    while (aux != NULL) {
        push(&actual, crearNodo(aux->palabra));
        aux = aux->sig;
    }

    for (int i = 1; i < potencia; i++) {
        Pila temp = concatenacion(&actual, &base);
        liberarPila(&actual);
        actual = temp;
    }

    liberarPila(&base);
    return actual;
}

int main() {
    int alfa1, alfa2;

    printf("Ingresa el numero de palabras que tiene el primer lenguaje: ");
    scanf("%d", &alfa1);
    char alfabeto1[alfa1][MAX];
    for (int i = 0; i < alfa1; i++) {
        printf("Ingresa el elemento %d del alfabeto 1: ", i + 1);
        scanf(" %[^\n]", alfabeto1[i]);
    }

    printf("Ingresa el numero de palabras que tiene el segundo lenguaje: ");
    scanf("%d", &alfa2);
    char alfabeto2[alfa2][MAX];
    for (int i = 0; i < alfa2; i++) {
        printf("Ingresa el elemento %d del alfabeto 2: ", i + 1);
        scanf(" %[^\n]", alfabeto2[i]);
    }

    Pila pAlfa1, pAlfa2;
    cargarPilaDesdeArreglo(&pAlfa1, alfabeto1, alfa1);
    cargarPilaDesdeArreglo(&pAlfa2, alfabeto2, alfa2);

    int op = menu();
    while (op != -1) {
        Pila resultado;
        ini(&resultado);

        switch (op) {
            case 1:
                resultado = unionAlfabetos(&pAlfa1, &pAlfa2);
                printf("\nLa union de los alfabetos es: ");
                imprimir(&resultado);
                liberarPila(&resultado);
                break;
            case 2:
                resultado = interseccion(&pAlfa1, &pAlfa2);
                printf("\nLa interseccion de los alfabetos es: ");
                imprimir(&resultado);
                liberarPila(&resultado);
                break;
            case 3:
                resultado = diferencia(&pAlfa1, &pAlfa2);
                printf("\nLa diferencia (Alfabeto 1 - Alfabeto 2) es: ");
                imprimir(&resultado);
                liberarPila(&resultado);

                resultado = diferencia(&pAlfa2, &pAlfa1);
                printf("\nLa diferencia (Alfabeto 2 - Alfabeto 1) es: ");
                imprimir(&resultado);
                liberarPila(&resultado);
                break;
            case 4:
                resultado = concatenacion(&pAlfa1, &pAlfa2);
                printf("\nLa concatenacion (Alfabeto 1 * Alfabeto 2) es: ");
                imprimir(&resultado);
                liberarPila(&resultado);
                
                resultado = concatenacion(&pAlfa2, &pAlfa1);
                printf("\nLa concatenacion (Alfabeto 2 * Alfabeto 1) es: ");
                imprimir(&resultado);
                liberarPila(&resultado);

                break;
            case 5:
                printf("\nOperacion de potencia .\n");
                printf("Ingresa la potencia para el alfabeto 1: ");
                int pot;
                scanf("%d", &pot);
                resultado = obtenerPotencia(&pAlfa1, pot);
                printf("\n\n La potencia a la %d del alfabeto 1 es: ", pot);
                imprimir(&resultado);
                liberarPila(&resultado);

                printf("Ingresa la potencia para el alfabeto 2: ");
                scanf("%d", &pot);
                resultado = obtenerPotencia(&pAlfa2, pot);
                printf("\n\n La potencia a la %d del alfabeto 2 es: ", pot);
                imprimir(&resultado);
                liberarPila(&resultado);
                break;
            default:
                printf("Ingresa una opcion valida.\n");
                break;
        }

        op = menu();
    }

    liberarPila(&pAlfa1);
    liberarPila(&pAlfa2);

    printf("Programa finalizado.\n");
    return 0;
}