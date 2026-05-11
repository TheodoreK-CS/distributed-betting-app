Before running, ensure all files are compiled into a central output folder (e.g., out).

javac -cp ".;lib\gson-2.10.1.jar" -d out src\MessageEssentials\*.java src\RNG_Server\*.java src\*.java

Open a separate terminal window for each step.
They must be started in this specific order to ensure sockets are ready to receive connections.


java -cp out RNG_Server.RNGServer 9090
//9090 is the port the server listens on.

java -cp out Reducer 7000 127.0.0.1 8000 2
//7000 (Reducer Port), 127.0.0.1 (Master IP), 8000 (Master Port), 2 (Number of Workers to wait for).

java -cp out Master 8000 127.0.0.1 6001 127.0.0.1 6002
//8000 (Master Port), followed by pairs of Worker IP and Worker Port.

//Worker 1
java -cp out Worker 6001 127.0.0.1 7000 127.0.0.1 9090
//6001 (Worker Port), 127.0.0.1 (Reducer IP), 7000 (Reducer Port), 127.0.0.1 (RNG IP), 9090 (RNG Port).

//Worker 2
java -cp out Worker 6002 127.0.0.1 7000 127.0.0.1 9090
//Same as Worker 1, but using port 6002 for this instance.

java -cp "out;lib\gson-2.10.1.jar" Manager 127.0.0.1 8000
java -cp out DummyClient 127.0.0.1 8000
//127.0.0.1 (Master IP), 8000 (Master Port).