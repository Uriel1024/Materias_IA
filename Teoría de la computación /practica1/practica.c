#include <stdio.h>
#include <stdlib.h>
//#include <string.h>

typedef struct{
    char *c1;
}Cadenas; 

//como no podemos usar diseñamos nuestras propias funciones strlen y strstr
int strlen(const char *cadena) {
    int contador = 0;
    
    // Iterar hasta encontrar el carácter nulo '\0'
    while(cadena[contador] != '\0') {
        contador++;
    }
    
    return contador;
} 

char* strstr(const char *haystack, const char *needle) {
    if (*needle == '\0') {
        return (char *)haystack;
    }
    for (int i = 0; haystack[i] != '\0'; i++) {
        int j = 0;
        while (haystack[i + j] != '\0' && needle[j] != '\0' && haystack[i + j] == needle[j]) {
            j++;
        }
        if (needle[j] == '\0') {
            return (char *)&haystack[i];
        }
    }
    return NULL;
}

int menu(){
    int op;
    printf("\n\n\n1. Cambiar cadena. \n");
    printf("2. Obtener prefijo y sufijo. \n");
    printf("3. Obtener la subcadena. \n ");
    printf("4. Obtener la subsecuencia \n");
    printf("5. Potencia de una cadena \n");
    printf("-1. Para salir  ");
    printf("\n Ingresa una opcion para poder continuar: ");
    scanf("%d", &op);

    return op;  
}

Cadenas* cambiarCadena(){
    Cadenas *res = (Cadenas *)malloc(sizeof(Cadenas));  

    res->c1 = (char *)malloc(100 * sizeof(char));
    printf("\nIngresa la cadena : ");
    scanf(" %[^\n]", res->c1);

    return res;
}


void prefijo_sufijo(char cadena[]){
    int tam = strlen(cadena);
    int pr = tam + 1;

    while(pr > tam){
    printf("\n\nIngresa el tamaño de elementos a recortar para la cadena (no mayor a %d):", tam );
    scanf("%d", &pr);
    }

    printf("El prefico de la cadena %s de %d elementos es:\n\n", cadena,tam);
    for(int i=0; i < (tam - pr) ; i++){
        printf("%c",cadena[i]);
    }

    printf("\n\nEl sufijo de la cadena %s de elementos %d es:\n\n",cadena,tam );
    for(int i = pr ; i <= tam ; i++){
        printf("%c",cadena[i]);
    }
}

void subcadena(char cad[]){
    Cadenas *cad2 = cambiarCadena();
    
    while(strlen(cad2->c1) > strlen(cad)){
        free(cad2->c1);
        free(cad2);
        cad2 = cambiarCadena();
    } 
    
    char *str_2;
    str_2 = strstr(cad, cad2->c1); 
    
    if (str_2 != NULL){
        int tam = strlen(cad) - strlen(str_2);
        
        char final[strlen(cad) - strlen(cad2->c1) + 1]; 
        
        for(int i = 0; i < tam; i++){
            final[i] = cad[i];
        }

        int inicio_resto = tam + strlen(cad2->c1);
        int j = tam; // Índice para seguir llenando el arreglo 'final'
        
        for(int i = inicio_resto; cad[i] != '\0'; i++){
            final[j] = cad[i];
            j++;
        }
        
        final[j] = '\0'; 

        printf("\n\nLa cadena resultante sin '%s' es : %s\n", cad2->c1, final);
    
    } else {
        printf("\n\nNo existe la subcadena %s en la cadena %s\n", cad2->c1, cad);
    }

    free(cad2->c1);
    free(cad2);
}

void subsecuencia(char cad[]){
    Cadenas *cad2 = cambiarCadena();
    
    int tam_s = strlen(cad);
    char final[tam_s + 1];
    int j = 0;
    
    for(int i = 0; cad[i] != '\0'; i++){
        int encontrado = 0;
        for(int k = 0; cad2->c1[k] != '\0'; k++){
            if(cad[i] == cad2->c1[k]){
                encontrado = 1;
                break;
            }
        }
        if(!encontrado){
            final[j] = cad[i];
            j++;
        }
    }
    final[j] = '\0';

    printf("\n\nLa subsecuencia resultante sin '%s' es : %s\n", cad2->c1, final);

    free(cad2->c1);
    free(cad2);
}  
void potencia(char cadenapot[]){
    int pot;
    printf("\n\nIngresa la potencia de la cadena:");
    scanf("%d", &pot);
    int n = strlen(cadenapot);
    

    if(pot > 0){ 
        printf("La cadena %s, a la potencia %d es: \n\n", cadenapot, pot);
        for(int i =0; i < pot; i++ ){
            for(int j = 0; j < n; j++){
                printf("%c",cadenapot[j]);
            }
        }
    }else if (pot< 0){
        printf("La cadena %s, a la potencia %d es:\n\n", cadenapot, pot);
        for(int i =0; i < (pot * -1); i++ ){
            for(int j = 0; j < n; j++){
                printf("%c",cadenapot[(n-1) - j]);
            }
        }
    }else{
        printf("\nLa cadena %s a la potencia %d es:   \n",cadenapot,pot);
    }

    printf("\n\n\n");
}

int main() {
    Cadenas *cadena = cambiarCadena();
    printf("\n La cadena S es %s", cadena->c1);
    int op = menu();
    while (op != -1){

        switch (op){
            case 1:
                free(cadena->c1);
                free(cadena);
                cadena = cambiarCadena();
                printf("\n La cadena S es %s", cadena->c1);
            break;
            case 2:prefijo_sufijo(cadena->c1);            
            break;
            case 3:subcadena(cadena->c1);
            break;
            case 4:subsecuencia(cadena->c1);
            break;
            case 5:potencia(cadena->c1);
            break;
            default:printf("Ingesa una opcion valida. ");
            break;
        }
        op = menu();    
    }
    free(cadena->c1);

    free(cadena);

    return 0;
}