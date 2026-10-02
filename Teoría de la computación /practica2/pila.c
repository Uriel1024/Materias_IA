#include <stdio.h>
#include <stdlib.h>
#include "pila.h"

// Copia una cadena a otra de forma segura
static void copiarCadena(char *destino, const char *origen) {
    int i = 0;
    while (origen[i] != '\0' && i < MAX_PALABRA - 1) {
        destino[i] = origen[i];
        i++;
    }
    destino[i] = '\0';
}

// Compara dos cadenas (retorna 0 si son idénticas)
static int compararCadenas(const char *s1, const char *s2) {
    int i = 0;
    while (s1[i] != '\0' && s1[i] == s2[i]) {
        i++;
    }
    return (unsigned char)s1[i] - (unsigned char)s2[i];
}

Nodo *crearNodo(const char *valor) {
    Nodo *nuevo = (Nodo *)malloc(sizeof(Nodo));
    if (nuevo == NULL) {
        printf("Error de memoria.\n");
        exit(1);
    }
    copiarCadena(nuevo->palabra, valor);
    nuevo->sig = NULL;
    return nuevo;
}

void ini(Pila *p) {
    p->size = 0;
    p->Tope = NULL;
}

int isEmpty(Pila *p) {
    return p->Tope == NULL ? 1 : 0;
}

int sizeStructure(Pila *p) {
    return p->size;
}

void push(Pila *p, Nodo *n) {
    if (n == NULL) return;
    n->sig = p->Tope;
    p->Tope = n;
    p->size++;
}

Nodo *pop(Pila *p) {
    if (isEmpty(p))
        return NULL;

    Nodo *res = p->Tope;
    p->Tope = p->Tope->sig;
    p->size--;
    res->sig = NULL;

    return res;
}

int existeEnPila(Pila *p, const char *valor) {
    Nodo *aux = p->Tope;
    while (aux != NULL) {
        if (compararCadenas(aux->palabra, valor) == 0) {
            return 1;
        }
        aux = aux->sig;
    }
    return 0;
}

void imprimir(Pila *p) {
    if (isEmpty(p)) {
        printf("{ }\n");
        return;
    }
    Nodo *aux = p->Tope;
    printf("{ ");
    while (aux) {
        printf("\"%s\" ", aux->palabra);
        aux = aux->sig;
    }
    printf("}\n");
}

void liberarPila(Pila *p) {
    while (!isEmpty(p)) {
        Nodo *n = pop(p);
        free(n);
    }
}