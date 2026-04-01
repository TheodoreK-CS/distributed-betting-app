compile cmd from project root :
javac -cp ".;lib\gson-2.10.1.jar" -d out src\*.java src\MessageEssentials\*.java src\RNG_Server\*.java

run from 7 different terminals from inside the out file with this order:
java RNG_Server.RNGServer
java Reducer 7000 6000 2
java Worker 6001 7000
java Worker 6002 7000
java Master 6000 6001 6002
java -cp ".;..\lib\gson-2.10.1.jar" Manager 6000
java DummyClient 6000


here are the json paths:
..\jsons\game1.json
..\jsons\game2.json
..\jsons\game3.json
..\jsons\game4.json
..\jsons\game5.json
..\jsons\game6.json
