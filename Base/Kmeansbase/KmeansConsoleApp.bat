cls
@echo off

net start MySQL57

start java -jar .\KmeansBase\src\main\java\org\example\KmeansServer\out\artifacts\KmeansServer_jar\KmeansServer.jar 8080

start java -jar .\KmeansBase\src\main\java\org\example\KmeansClient\out\artifacts\KmeansClient_jar\KmeansClient.jar 127.0.0.1 8080