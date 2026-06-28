/*
Una rana quiere subir a la parte mas alta de una escalera con exactamente N escalones. 
Para subir escalones, inevitablemente tiene que saltar. 
En cada salto, la rana puede subir desde 1 hasta k escalones. 
Si la escalera tuviera 4 escalones, y k=3 la rana podría subir la escalera con 7 
suceciones de saltos distintas
1,1,1,1
1,1,2
1,2,1
1,3
2,1,1
2,2
3,1
*/ 


#include <iostream>
using namespace std;

int saltos(int, int);

int main(){
  int k,n;

  cout<<"Ingresa el numero de escalones que debe subir la rana (n >= 1): ";
  cin>>n;
  cout<<endl<<"Ingresa el salto mas largo que puede dar la rana: ";
  cin>>k;

  cout<<endl<<"El numero de posibilidades que tiene la rana son: "<< saltos(n,k)<<endl;
  return 0;
}

int saltos (int n, int k){
  if(k > n){
    return -1;
  }

  if(n == 1){
    return 1;
  } //caso base
  
  int formas[n + 1];
  formas[0] = 1; // en el escalon 1 solo hay una forma de subirlo 
   
  for(int i = 1; i <= n; i++){
    formas[i] = 0 ; //inicializamos en 0 para evitar almacenar basura en la cache 
    for(int j = 1 ; j <= k ; j++){
      if(i - j >= 0){
        formas[i] += formas[i - j];
      }
    }
  }
  return formas[n];
}
