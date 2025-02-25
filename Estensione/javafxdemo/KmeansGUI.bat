cls
@echo off

echo avvio servizio mySQL

net start mySQL57

echo starting kmeans server

start cmd /k java -jar ".\graficDemo\KmeansServer\out\artifacts\KmeansServer_jar\KmeansServer.jar" 8080

cls

echo starting kmeans Client

java -jar ".\graficDemo\out\artifacts\graficDemo_jar\graficDemo.jar" 127.0.0.1 8080

