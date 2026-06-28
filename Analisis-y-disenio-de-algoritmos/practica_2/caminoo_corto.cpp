#include <iostream>
using namespace std;

int camino_corto(int, int ,int, int, int,char []);


int main () {
  int n , M;
  cout<<"Ingresa el numero de segmentos de camino que va a recorrer: ";
  cin>>n;
  cout<<"Ingresa el tiempo que tiene para correr";
  cin >> M;
  char array[n];
  cout<<"Ingresa los segmentos de camino que va a recorrer (U,F,D): ";
  for(int i = 0 ; i < n ; i++){
    cout<< i + 1 << " segmento : ";
    cin >> array[i];
  }
  int  U = 3 , F = 2 , D = 1;  // M es la cantidad de tiempo, T es el tamanio del arreglo, U el tiempo de subida, F el tiempo en plano , D el tiempo de bajada 
  int seg = camino_corto(M, n, U, F, D, array);
  cout<<"El maximo numero de segmenots es: "<<seg<<endl;
  return 0;
}

int camino_corto(int M, int T, int U, int F, int D, char array[]){
  int time=0;
  for(int i = 0 ; i < T; i++){
    if(array[i] == 'U'  || array[i] == 'D' || array[i] == 'u'|| array[i] == 'd'){
      time += U + D;
    }else if(array[i] == 'F' || array[i] == 'f'){
      time += 2*F;
    }
    if(time > M || time ==  M){
      return i + 1;
      break;
    }
  }
  return  -7;
}
