#include <iostream>
using namespace std;

int cortesV(int[],int);

int main(){
  int valor[] = {1,5,8,9,10,17,17,20};

  int n = 4;
  cout<<endl<<"Ingresa el tamanio de la varilla a cortar(1-8): ";
  cin>>n;
  cout<<endl<<"El mejor costo es: " << cortesV(valor, n)<<endl;
  return 0;
}



int cortesV(int valor[], int n ){
  int T[ n+1 ];

  for(int i = 0; i <=n ;i++){
    T[i] = 0;
  }  //for para evitar guardar basura en el arreglo 

  for(int i = 1 ; i <= n ; i++){  
    for (int j = 1; j <= i; j++){
      if(T[i] < valor[j - 1] + T[i - j]){
        T[i] =  valor[j - 1] + T[i - j];
      }
    }
  }
  return T[n];
}
