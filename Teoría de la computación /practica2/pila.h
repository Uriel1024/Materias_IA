#ifndef PILA_H
#define PILA_H

#define MAX_PALABRA 100

typedef struct nodo {
    char palabra[MAX_PALABRA];
    struct nodo *sig;
} Nodo;

typedef struct pila {
    Nodo *Tope;
    int size;
} Pila;

Nodo *crearNodo(const char *valor);
Nodo *pop(Pila *p);
void push(Pila *p, Nodo *n);
void ini(Pila *p);
int sizeStructure(Pila *p);
int isEmpty(Pila *p);
void imprimir(Pila *p);
void liberarPila(Pila *p);
int existeEnPila(Pila *p, const char *valor);

#endif