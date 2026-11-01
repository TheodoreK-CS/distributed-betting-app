# Distributed MapReduce Betting & Simulation Platform

A distributed betting and game simulation system written in Java, paired with an Android client app. The backend uses a custom MapReduce architecture (`Master`, `Worker`, and `Reducer` nodes) over TCP sockets, a multithreaded Producer-Consumer Random Number Generator server (`RNGServer`) with SHA-256 verification, and a `Manager` CLI for loading games via JSON.

## How to Build & Run

### 1. Compile the Backend
Open a terminal in the **project root directory** and compile the source files into the `out` directory:

`javac -cp ".;lib\gson-2.10.1.jar" -d out src\*.java src\MessageEssentials\*.java src\RNG_Server\*.java`

*(On Linux/macOS, replace `;` with `:` and `\` with `/`)*

### 2. Run the Cluster Locally (`127.0.0.1`)
Open **7 separate terminal windows**, navigate inside the `out` folder (`cd out`), and run the components in this order:

- **Terminal 1 (RNG Server on port 9090):**
  `java RNG_Server.RNGServer 9090`

- **Terminal 2 (Reducer on port 7000, expecting 3 workers):**
  `java Reducer 7000 127.0.0.1 6000 3`

- **Terminal 3 (Worker 1 on port 6001):**
  `java Worker 6001 127.0.0.1 7000 127.0.0.1 9090`

- **Terminal 4 (Worker 2 on port 6002):**
  `java Worker 6002 127.0.0.1 7000 127.0.0.1 9090`

- **Terminal 5 (Worker 3 on port 6003):**
  `java Worker 6003 127.0.0.1 7000 127.0.0.1 9090`

- **Terminal 6 (Master on port 6000, connected to all 3 workers):**
  `java Master 6000 127.0.0.1 6001 127.0.0.1 6002 127.0.0.1 6003`

- **Terminal 7 (Manager CLI):**
  `java -cp ".;..\lib\gson-2.10.1.jar" Manager 127.0.0.1 6000`

### 3. Load Games via Manager
In the **Manager** terminal, select option `1 (Insert Game)` and enter the relative paths to the JSON files located in the `jsons` folder:
- `..\jsons\game1.json`
- `..\jsons\game2.json`
- `..\jsons\game3.json`
- `..\jsons\game4.json`
- `..\jsons\game5.json`
- `..\jsons\game6.json`

### 4. Run the Client (CLI or Android)
To test gameplay from the command line, open an **8th terminal** inside `out` and start `DummyClient`:

`java DummyClient 127.0.0.1 6000`

To use the **Android App**, update the Master IP address in the Android network configuration before building the APK:
- Use `10.0.2.2` if running on the Android Studio Emulator on the same PC as `Master`.
- Use your machine's LAN IPv4 address (e.g., `192.168.1.x`) if running on a physical device or across multiple machines on the same network. *(Note: When running across multiple machines, replace `127.0.0.1` in the terminal commands above with the respective LAN IPs of each node, and make sure the `num_workers` argument passed to `Reducer` matches the exact number of workers registered on `Master`).*

## Implementation Details

- **Hash-Based Sharding (`Master.java`):** Point operations (`add_game`, `remove_game`, `play_bet`, `change_game`, `review_game`) are routed to a specific `Worker` node by hashing the game name: `Math.abs(gameName.hashCode() % workerPorts.size())`.
- **MapReduce Queries (`Master.java`, `Worker.java`, `Reducer.java`):** Broadcast queries (`search_games` with multi-filter criteria, `game_profit`, and `player_profit`) are assigned a unique request ID (`id_pairing`) and sent to all `Worker` nodes (`communicate_all`). Each `Worker` filters its local state in a synchronized snapshot and sends partial results to `Reducer`, which merges the lists once all `num_workers` have responded and forwards the final result back to `Master`.
- **Producer-Consumer RNG & SHA-256 Verification (`RNG_Server/`):** When a game is added, `RNGServer` initializes a dedicated bounded `Buffer` (`MAX_CAPACITY = 10` using `wait()` and `notifyAll()`) and a background `Producer` thread. The `Producer` generates random outcomes (`0-99`) using `SecureRandom`, appends the game's secret `HashKey`, and computes a **SHA-256** hash (`Tuple`). When a bet is placed, a `Consumer` thread sends a `Tuple` to the `Worker`, which independently recomputes the SHA-256 hash using the game's secret key to verify the outcome before calculating payouts (`low`, `medium`, `high` risk multipliers, or `10x/20x/40x` jackpot when `winningNumber % 100 == 0`).

## Project Structure

- `src/Master.java`: Cluster entry point, player ID registration, consistent hash routing, and MapReduce broadcast handler.
- `src/Worker.java`: Game state storage, SHA-256 RNG verification, payout calculation, and local map filtering.
- `src/Reducer.java`: Collects and merges mapped search and profit results from workers by request ID.
- `src/Manager.java`: Admin CLI using `Gson` to add/remove games, update min/max bets & risk levels, and view platform/player net profit statistics.
- `src/DummyClient.java`: Interactive player CLI for registering, adding balance, filtering games, placing bets, and submitting star reviews.
- `src/RNG_Server/`: Contains `RNGServer.java`, `Producer.java`, `Consumer.java`, `Buffer.java`, and `Tuple.java`.
- `src/MessageEssentials/`: Serializable protocol and data models (`Message.java`, `Game.java`, `PlayerStats.java`, `Bet.java`, `RandomResult.java`).
