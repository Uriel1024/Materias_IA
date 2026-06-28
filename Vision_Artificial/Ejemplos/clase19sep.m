clc
clear all
close all
warning off all
%Programa que me genera clases con N individuos
c1x = randn(1,1000);
c1y = randn(1,1000);

c2x = randn(1,1000)+3;
c2y = randn(1,1000)+5;


c3x = (randn(1,1000)-6)*2;
c3y = (randn(1,1000)-8)*2;


plot(c1x(1,:),c1y(1,:),"ro","MarkerSize",10,"MarkerFaceColor",'r')
grid on 
hold on
plot(c2x(1,:),c2y(1,:),"bo","MarkerSize",10,"MarkerFaceColor",'b')

plot(c3x(1,:),c3y(1,:),"go","MarkerSize",10,"MarkerFaceColor",'g')

legend('Clase 1', 'Clase 2',"Clase 3")




disp("fin de proceso")