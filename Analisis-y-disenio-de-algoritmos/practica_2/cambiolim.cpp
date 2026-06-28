//Programa que da cambio con la menor cantidad de monedas posbiles(en caso de que sean monedas limitadas).
#include <iostream>
using namespace std;
#include <cmath>

int cambio(int,int [],int[],int,int[]);
int total(int[],int[],int); //calcula el total de dinero para ver si es posible dar el cambio de las monedas


int main(){
  int denominacion[] = {1,2,5,10,20};
  int monedas[] = {0,0,0,0,0};
  int cantidad[] = {200,100,50,10,4};
  int n = sizeof(denominacion) / sizeof(denominacion[0]);
  int x; 
  cout<<"Ingresa la cantidade de dinero: ";
  cin >>x;
  cambio(x, denominacion, monedas, n, cantidad);
  cout<<"\nLas monedas son ";  
  for(int i=0 ; i < n ; i++){
    cout<<"\nDenominacion: "<<denominacion[i]<<" \t cantidad: "<<monedas[i]<< "\t cantidad restante de monedas: "<< cantidad[i]<<endl; 
  }
  return 0;
}


int cambio(int x, int denominacion[], int monedas[], int n, int cantidad[]){
  if (x == 0){
    return 0;
  }
  int tot = total(denominacion,cantidad, n); 
  if(tot <  x){
    return 0;
  }else{
      if( x>= denominacion[n-1] && cantidad[n-1] > 0){
        int  mon= floor(x / denominacion[n-1]);
        if(mon <= cantidad[n-1]){  // por si las monedas son suficientes
          monedas[n-1] = mon;
          x = x % denominacion[n-1];
          cantidad[n-1] -=  mon;
          cambio(x,  denominacion ,monedas, n-1 , cantidad);
        }else if(mon > cantidad[n-1]){ //si las monedas no son suficientes
          x = x - (cantidad[n-1] * denominacion[n-1]); // actualiza la cantidad de dinero
          monedas[n-1] = cantidad[n-1]; // actualiza el numero de monedas que se utilizaron   
          cantidad[n-1] -= cantidad[n-1];// resta el total de las monedas, se asume que se ocuparon todas
          cambio(x,denominacion,monedas,n-1,cantidad); // llamada recursiva 
      }
    }else{
      cambio(x, denominacion, monedas,n-1, cantidad);
    }
  }
  return 0;
}


int total(int denominacion[] , int cantidad[], int n){
  int tot = 0;
  for(int i = 0 ; i<n ;i++){
    tot += cantidad[i] * denominacion[i];
  }
  return tot;
}

