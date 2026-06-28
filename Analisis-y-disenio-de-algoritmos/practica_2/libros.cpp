//comentario de prueba
#include <iostream>
#include <ostream>
using namespace std;
#include <cmath>
#include <ctime>

int libros(int[], int[], int);
void quicksort(int[], int , int); //vamos a ordenar de manera ascendente el numero de libros que destruye cada amigo
int partition(int[], int, int);


int main(){
  srand(time(0));
  cout<<"Ingresa el numero de amigos en la fiesta:";
  int n;
  cin >> n;
  int tiempo[n];
  int des[n];
  for(int i = 0 ; i < n ; i++){
    cout<<endl<<"A cuantos min vive el amigo  " << i  + 1 << " :";
    cin >> tiempo[i];
    des[i]  = rand() %  6; //genera aleatorimante la cantidad de libros que destruye cada amigo
  }
  quicksort(des, 0, n -1); //ordenamos el arreglo  
  
  for(int j = 0 ; j < n ; j++){
    cout<<"Amigo 1: " << tiempo[j] << '\t' << des[j]<<endl;
  }
  int desu = libros(tiempo , des, n);
  cout<<endl<<"La cantidad de libros destruidos es: " << desu << endl; 
}


int libros(int tiempo[], int des[], int n){
  int destruidos = 0; 
  int sum = 0;
  
  for(int i = 0; i < n ; i ++  ){
    for(int j = i; j<n ; j++){
      sum += des[j] ;
    }
    destruidos += (tiempo[i]) * sum;
  }
  return destruidos;
}



void quicksort(int array[], int ini, int n){
  if(ini < n){
    int piv =  partition(array, ini, n);
    quicksort(array, ini,  piv -1);
    quicksort(array, piv + 1, n);
  } 
}

int partition(int array[], int ini, int n){
  int piv = array[n];
  int it = (ini - 1);
  for(int j = ini; j <= n - 1; j++){
    if(array[j] >= piv){
      it++;
      swap(array[it], array[j]);
    }
  }
  swap(array[it + 1], array[n]);
  return it + 1; 
}



