clc % limpia pantalla
clear all %limpia todo
close all % cierro todo
warning off all % apaga todas las advertencias
disp('Hola mundo')

%% Programa que dado un clasificador con 3 clases toma una decision
%%tomando como criterio de comparacion la distancia euclidiana

c1 = [1 2 1 4 3;2 0 3 1 1]
c2 = [3 3 4 6 5;4 7 2 5 0]
c3 = [7 6 9 8 7;3 9 0 1 2]
vector = [6; -3]

media1 = mean(c1,2)
media2 = mean(c2,2)
media3 = mean(c3,2)

%graficando las clases
plot(c1(1,:),c1(2,:),'ro','MarkerSize',10,'MarkerFaceColor','r')
grid on
hold on
plot(c2(1,:),c1(2,:),'ro','MarkerSize',10,'MarkerFaceColor','b')
plot(c3(1,:),c3(2,:),'ro','MarkerSize',10,'MarkerFaceColor','k')
plot(vector(1,:),vector(2,:),'go','MarkerSize',10,'MarkerFaceColor','g')


legend('Clase 1', 'Clase 2','clase 3', 'vector')
disp('fin de proceso')



