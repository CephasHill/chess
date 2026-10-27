# BYU CS 240 Chess

This repository contains a modular multiplayer chess application built in Java
for BYU's CS 240 course (Winter 2025). It uses a client/server architecture
with three Maven modules:

- **`shared`** contains the chess model, request/response records, and
  WebSocket message types shared by the client and server.
- **`server`** contains the Spark HTTP server, services, data-access objects,
  persistence implementations, and WebSocket infrastructure.
- **`client`** contains a command-line REPL, HTTP client facade, terminal board
  renderer, and WebSocket client components.

The project demonstrates chess-rule validation, authentication, game
management, HTTP APIs, persistence, and the design of a networked gameplay
workflow.

## What is implemented

### Chess model

The `shared` module includes:

- An 8x8 board with the standard initial setup
- Pieces and movement generation for the king, queen, rook, bishop, knight,
  and pawn
- Captures and pawn promotion
- Move validation that prevents a move from leaving the moving side's king in
  check
- Check, checkmate, and stalemate detection
- Board/game value objects suitable for JSON serialization

The chess model is covered by the course/pass-off tests under
`shared/src/test`.

### HTTP server

The server starts a Spark application and registers these routes:

| Method | Route | Purpose |
| --- | --- | --- |
| `POST` | `/user` | Register a user and return an auth token |
| `POST` | `/session` | Log in and return an auth token |
| `DELETE` | `/session` | Log out an auth token |
| `GET` | `/game` | List games; requires `Authorization` |
| `POST` | `/game` | Create a game; requires `Authorization` |
| `PUT` | `/game` | Join a game as white or black; requires `Authorization` |
| `DELETE` | `/db` | Clear all users, auth tokens, and games |
| WebSocket | `/ws` | Endpoint for the partially implemented game protocol |

The server is organized into handlers, services, and data-access objects.
Both in-memory and MySQL-backed data-access implementations are present.
`server.Server` defaults to the MySQL implementation (`storageType = "sql"`).

### Command-line client

Run the client to use the REPL:

#### Before signing in

- `register <username> <password> <email>`
- `login <username> <password>`
- `help`
- `quit`

#### After signing in

- `list`
- `create <gameName>`
- `join <gameNumber> <white|black>`
- `observe <gameNumber>`
- `logout`
- `help`
- `quit`

#### After joining a game

- `print` or `redraw` to render the current board
- `leave`
- `help`
- `quit`

The board renderer can display a game from the white or black perspective.
However, the client currently does not provide a command for making a chess
move.

## Current scope and future work

The core chess model, authentication flow, game management, and HTTP API are
implemented. The following items represent opportunities for continued
development:

- Complete server-side WebSocket handling for `MAKE_MOVE`, `LEAVE`, and
  `RESIGN` so those gameplay actions are processed end-to-end.
- Resolve authenticated users and selected games from WebSocket commands instead
  of using a temporary username placeholder during connection handling.
- Add client gameplay commands that send moves and support leave/resign flows
  over the existing WebSocket connection.
- Expand the `observe` workflow so selecting a listed game establishes a real
  observer connection and loads the selected game state.
- Synchronize board state updates from server-side moves to connected clients.
- SQL configuration is not included in this repository. The server expects a
  `db.properties` resource with MySQL connection settings before the default
  SQL mode can start.

The `starter-code` directory contains the course-provided snapshots for the
assignment phases. It is reference material and is not the active source used
by the Maven build.

## Project layout

```text
.
├── client/
│   ├── src/main/java/       Command-line client and HTTP/WebSocket facades
│   └── src/test/java/       Client/server-facade tests
├── server/
│   ├── src/main/java/       HTTP server, services, handlers, and DAOs
│   ├── src/main/resources/  Web UI resources
│   └── src/test/java/       DAO, service, API, database, and WebSocket tests
├── shared/
│   ├── src/main/java/       Chess model and shared protocol/data classes
│   └── src/test/java/       Chess model and pass-off tests
├── starter-code/            Course starter snapshots
├── 10k-architecture.png    Architecture diagram
├── ChessDesign.png          Chess design diagram
└── ServerDiagram.png        HTTP/server sequence diagram
```

## Requirements

- Java Development Kit 21 or newer. The root POM compiles with
  `maven.compiler.source` and `maven.compiler.target` set to 21.
- Maven
- MySQL, if running the default SQL-backed server

The checked-in project does not include a Maven wrapper. On Windows, verify
that `java` and `mvn` are available on `PATH` before building.

## Database configuration

When the server runs in its default SQL mode, `DatabaseManager` loads
`db.properties` from the server runtime classpath. Create
`server/src/main/resources/db.properties` locally with values matching the
MySQL instance you intend to use:

```properties
db.host=localhost
db.port=3306
db.name=chess
db.user=your_mysql_user
db.password=your_mysql_password
```

Do not commit real credentials. The server creates the database and its
`users`, `auth`, and `games` tables during startup.

The codebase also contains in-memory DAOs for tests and development, but the
normal `Main` entry point does not switch to memory mode automatically.

## Build, test, and run

Run these commands from the repository root:

```sh
# Compile all modules
mvn compile

# Run the full test suite
mvn test

# Run only the chess/model tests
mvn -pl shared test

# Build the executable jars
mvn package

# Run the server on port 8080
mvn -pl server exec:java

# Run the client on port 8080
mvn -pl client exec:java

# Use a different client port
mvn -pl client exec:java -Dexec.args="8081"
```

The server listens on port `8080` by default. The client accepts one optional
port argument. Build output is written beneath each module's `target`
directory, including assembled jars named `client.jar` and `server.jar`.

## Testing

Tests are distributed across the three modules. In addition to unit tests for
the chess model, the repository contains tests for:

- Client HTTP facade behavior
- In-memory data access
- Service behavior
- HTTP API behavior
- MySQL database behavior
- WebSocket protocol behavior

Some integration tests require a running/configured MySQL database and the
course-provided pass-off dependency jar in `server/lib`.
