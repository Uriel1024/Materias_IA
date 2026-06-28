clc
clear all
close all
warning off all 

%leyendo una imagen 
a = imread('peppers.png');
[m,n] = size(a)
figure(1)
imshow (a)
figure(2)
impixel(a)



disp('Fin de proceso, mis chavos...');
