//Programa que da cambio con la menor cantidad de monedas posbiles(en caso de que sean monedas infinitas).
#include <iostream>
using namespace std;
#include <cmath>
int cambio(int,int [],int[],int);

int denominacion[] = {1,2,5,10,20};
int monedas[] = {0,0,0,0,0};
 

int main(){
 
  int n = sizeof(denominacion) / sizeof(denominacion[0]);
  int x; 
  cout<<"Ingresa la cantidade de dinero: ";
  cin >>x;
  cambio(x, denominacion, monedas, n);
  cout<<"\nLas monedas son ";
  for(int i=0 ; i < n ; i++){
   cout<<"\nDenominacion: "<<denominacion[i]<<" \t cantidad: "<<monedas[i]<<endl; 
  }
  return 0;
}


int cambio(int x, int denominacion[], int monedas[], int n){
  if (x == 0){
    return 0;
  }
  if( x>= denominacion[n-1]){
    monedas[n-1] = floor(x / denominacion[n-1]);
     = x % denominacion[n-1];
    cambio(x,  denominacion ,monedas, n-1);
  }else{
    cambio(x, denominacion, monedas,n-1);
  }
  return 0;
}


