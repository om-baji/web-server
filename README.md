# ⚡ High-Concurrency TCP Server Models in Java with Windows C++ Stress Client

This project demonstrates **three TCP server models in Java** — **Single-Threaded**, **Multi-Threaded**, and **Thread-Pool-based** — built to explore and benchmark concurrent socket communication. It also includes a **high-performance, multi-threaded C++ client for Windows**, capable of simulating **10,000+ concurrent TCP connections** for stress testing and load analysis.

---

### 📁 Project Structure

```
.
├── client.cpp               # High-concurrency C++ client (Windows only)
├── client.exe               # Precompiled executable for testing
├── README.md                # You're reading it
├── SingleThreaded/
│   ├── Server.java
│   └── Client.java          # Java server + client (handles 1 client at a time)
├── MultiThreaded/
│   ├── Server.java
│   └── Client.java          # Spawns a thread per incoming connection
└── ThreadPool/
    ├── Server.java
    └── Client.java          # Uses a fixed-size thread pool to manage load
```

---

### 🧠 Java Server Models Explained

| Model              | Concurrency             | Scalability         | Notes                                      |
| ------------------ | ----------------------- | ------------------- | ------------------------------------------ |
| **SingleThreaded** | ❌ One client at a time  | ❌ Not scalable      | Easy to understand; useful for teaching    |
| **MultiThreaded**  | ✅ One thread per client | ⚠️ Risky under load | Good for moderate traffic; thread overhead |
| **ThreadPool**     | ✅ Pooled threads        | ✅ Scales well       | Optimal for high-concurrency environments  |

---

### ⚙️ How to Build & Run

> 💡 All Java servers listen on port `8000` by default.

#### 🔧 Compile and Run a Java Server

```bash
# Example: Run ThreadPool server
cd ThreadPool
javac Server.java
java Server
```

#### 📡 Run Java Client (Optional)

```bash
javac Client.java
java Client
```

You can do the same for `MultiThreaded/` and `SingleThreaded/` directories.

---

### 🖥️ C++ High-Concurrency Client (Windows)

The C++ client is designed to simulate 10,000 concurrent connections to the server using **WinSock2** and C++ threads. It prints per-request latency and summaries to the console.

#### ✅ Features

* Uses `std::thread` to spawn 10,000+ connections
* Measures latency for each connection
* Real-time terminal logging:

  * `INFO` for connection attempts
  * `SUCCESS` for successful responses
  * `ERROR` for connection or communication failures
* Summarized stats after execution

#### 🛠 Build and Run (on Windows)

```bash
# Compile
g++ client.cpp -o client.exe -lws2_32

# Run test
client.exe
```

---

### 📈 Sample Output

```
[INFO] Starting test with 10000 clients...
[RESPONSE] Thread 102: "Hello from server!" (6 ms)
[RESPONSE] Thread 209: "Hello from server!" (4 ms)
...
[SUCCESS] Test complete
[INFO] Total Success: 9934
[INFO] Total Failures: 66
```

---


