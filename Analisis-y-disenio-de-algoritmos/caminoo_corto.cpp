#include <iostream>
using namespace std;

int camino_corto(int, int ,int, int, int,char []);


int main () {
  char array[] = {'F','U','F','U','D' };
  int M = 20, T = 0, U = 3 , F = 2 , D = 1; 
  T = sizeof(array) / sizeof(array[0]);
  int seg = camino_corto(M, T, U, F, D, array);
  cout<<"El maximo numero de caminos es: "<<seg<<endl;
  return 0;
}

int camino_corto(int M, int T, int U, int F, int D, char array[]){
  int time=0;
  for(int i = 0 ; i < T; i++){
    if(array[i] == 'U'  || array[i] == 'D'){
      time += U + D;
    }else if(array[i] == 'F'){
      time += 2*F;
    }
    if(time > M || time ==  M ){
      return i + 1;
      break;
    }
  }
  return  -7;
}
