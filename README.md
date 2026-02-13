# Health and Safety Reporting System

A robust, multithreaded client-server application developed in Java for managing workplace health and safety reports. This project demonstrates core principles of Distributed Systems, Concurrent Programming, and Data Persistence.

## 🚀 Features

- **Multithreaded Server:** Supports multiple simultaneous client connections using a dedicated thread-per-client model.
- **Secure Authentication:** User passwords are protected using industry-standard **SHA-256 hashing**, ensuring credentials are never stored in plain text.
- **Robust Data Persistence:** Users and reports are persisted to disk (`users.txt`, `reports.txt`) with an optimized I/O strategy to ensure high performance and data integrity across sessions.
- **Concurrent Data Management:** A thread-safe `SharedObject` utilizes synchronization and atomic variables (`AtomicInteger`) to prevent race conditions during report generation and user registration.
- **Reliable Communication Protocol:** Implements a custom signaling protocol (` >> `) to ensure robust synchronization between the client and server during user input prompts.

## 🏗 Architecture

The system is designed with a clear separation of concerns:

- **Server:** Listens for incoming socket connections and dispatches `ServerThread` handlers.
- **ServerThread:** Manages the lifecycle of a single client connection.
- **MenuHandler:** Contains the business logic for navigating the system (Login, Report Creation, Assignment).
- **SharedObject:** The centralized, synchronized repository for all in-memory data and file I/O operations.
- **ClientHandler:** The user-facing CLI application that communicates with the server.

## 🛠 Setup and Usage

### Prerequisites
- Java Development Kit (JDK) 8 or higher.

### Compilation
Compile all source files from the project root:
```bash
javac src/*.java
```

### Running the System
1. **Start the Server:**
   ```bash
   java -cp src Server
   ```
2. **Start the Client (in a separate terminal):**
   ```bash
   java -cp src ClientHandler
   ```

## 📂 Project Structure

- `src/Server.java`: Entry point for the server application.
- `src/ClientHandler.java`: Entry point for the client application.
- `src/SharedObject.java`: Thread-safe data management and persistence.
- `src/User.java`: User model with SHA-256 hashing logic.
- `src/Report.java`: Report model with thread-safe ID generation.
- `src/MenuHandler.java`: Handles user interaction and command processing.
- `src/ServerThread.java`: Manages the socket connection for an individual client.

## 🎓 Academic Highlights
- **Performance:** Optimized O(N) loading logic to prevent redundant file writes.
- **Integrity:** Implemented logic to preserve unique report IDs across server restarts.
- **Security:** Integrated `java.security.MessageDigest` for one-way cryptographic hashing.
