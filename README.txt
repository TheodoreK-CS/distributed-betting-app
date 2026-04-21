compile cmd from project root :
javac -cp ".;lib\gson-2.10.1.jar" -d out src\*.java src\MessageEssentials\*.java src\RNG_Server\*.java

run from 7 different terminals from inside the out file with this order:
java RNG_Server.RNGServer 9090
java Reducer 7000 master_ip 6000 2
java Worker 6001 reducer_ip 7000 rng_ip 9090
java Worker 6002 reducer_ip 7000 rng_ip 9090
java Master 6000 worker1_ip 6001 worker2_ip 6002
java -cp ".;..\lib\gson-2.10.1.jar" Manager naster_ip 6000
java DummyClient master_ip 6000


here are the json paths:
..\jsons\game1.json
..\jsons\game2.json
..\jsons\game3.json
..\jsons\game4.json
..\jsons\game5.json
..\jsons\game6.json
