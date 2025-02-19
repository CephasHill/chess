# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```

## Server Diagram

[![Server Diagram](ServerDiagram.png)](https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2GADEaMBUljAASij2SKoWckgQaIEA7gAWSGBiiKikALQAfOSUNFAAXDAA2gAKAPJkACoAujAA9D4GUAA6aADeAETtlMEAtih9pX0wfQA0U7jqydAc45MzUyjDwEgIK1MAvpjCJTAFrOxclOX9g1AjYxNTs33zqotQyw9rfRtbO58HbE43FgpyOonKUCiMUyUAAFJForFKJEAI4+NRgACUh2KohOhVk8iUKnU5XsKDAAFUOrCbndsYTFMo1Kp8UYdKUAGJITgwamURkwHRhOnAUaYRnElknUG4lTlNA+BAIHEiFRsyXM0kgSFyFD8uE3RkM7RS9Rs4ylBQcDh8jqM1VUPGnTUk1SlHUoPUKHxgVKw4C+1LGiWmrWs06W622n1+h1g9W5U6Ai5lCJQpFQSKqJVYFPAmWFI6XGDXDp3SblVZPQN++oQADW6ErU32jsohfgyHM5QATE4nN0y0MxWMYFXHlNa6l6020C3Vgd0BxTF5fP4AtB2OSYAAZCDRJIBNIZMQBHPhzDZcxs4tp6p1JqtAzqBJoIei0azF5vDgHO8FPmlzDrco4rDMzz6K8SzgQC5zAgUCYoKUCAHjysL7oeqLorE2JIQUrosqU5JUjSn4oCaRLhgUlrcryBqCsKMDkY6ogADxJoRpJGtorEqARYZuh6uqZLG-rTiGXGqDRHLRjAYnxnKKAcbkUmlNOs7oJgQFQCpSGlJhPLZrm7a6UmOmlCBFYTBBGmNs2NkHNeGB6cUlz9oOvQDOWYE2dMfR2XOsGYEEEDMR0MCbGAICpHxymcYJRGKsq2nwZQrlqshACiUDeFApkqRZOV5Ve3YuUmd6lAALE4ACMQ59KM6jAOSKzFdA5Q+MEQbQEgABeKDLAcAQ-kskUxFFMVxSpamje8RgQGoaAAOTMJNqThSO4o6RlTryu1+V3oVaVlAdpU5GAu2XDV9VeU1qgtfcfQHZ1wR+r1A1DWYnCrt4fiBF4KDoHuB6+Mwx7pJk503rK1AlhU0hZbuWX1FlzQtC+qhvt0gVaQB+QWf0uPzo5qVApQiFKShoO+hhNNgNhGJ4UpAlUUJqH2GDAZBppaCUUyboyaU0goNwmQwMTZOprt4KYWDxkIFgR3mSdllOWVl0VW5ZQeUOS4-Z4f0bpCtq7tCMAAOKjqyEOntDeRFtr5QVBbKPo-Yo44zz9loKZgGq0T3tzhMcHk1AlOZSh0JW01GHR9bjO4XFrMC0RJFiR9g3c3WPv82a0n5FGPVQP1Yj4fkanILEMdqNnM656GbMskLFsUjAVfMBAABmEvKjAHtNdNCVN6SA9qGbsRS8CMvyhPYA16oCtK9rx1h5ZUxj6o4yVP0Y8AJLSNvtW9gAzFVTwnpkBrWX0Xw6AgoANtfvm308Y8AHKjo5MCNOrF1XTrAcQ4N7W23hUXeo4D5H1PufKYl99Q+VGOBJ499H7PyQX5EBoxP6jG-r-b6K5DbrkCNgHwUBsDcHgCJQwNcUiQyyBrW8TtKi1AaO7T23Uc5ziHB-Uc-5tb+zXoHLhDlX59F4Rg2+odUwRz2shT0eoa6wjgNQmuicsTJwrolUeFIM4l0+nXXmedqKF1ksXUumi1IKMyEoiRFFG6p3UELOSNdFKZRmto90Y8HQ7S1pHFRXobGjiXgVFWQjxGQMPqTZymtciVV1l5feUSpEEN+sQgIlhRaoWSDAAAUhAHkltRyBFQSABs9tmCwxKM7KolInwtDHl7ER75egUOAJkqAcAICoSgLMJJ-C4bhwJgHAKQdREQTaR0rpPTkERNGFA0mOlZHggAFYFLQEo-JPI1EoDREzSxnjiK6PMQY4mxjBamKtCcsuLMtEj3dGs7Zo5YRJPOc3S5MUUBlJgH6OQMAQDdOgDANAEBmDyDnEPVShzkoql8XEqmWyNnBLUCZZWuRCZ-3MAA0oCSegDLkR4+5pQXr-MBbAcFWk4X6QpCAFwqSiH-QCF4dpXYvSwGANgChhB4iJDoXbGJTDBnO0RsjVG6NjB+2GUI6RCF8j6Q4KLCkKBpAACFYQizFigdRzNMop3zqUBVmrVWwjeU4y5GqlVGG0HoAwkK1IwqnulPxcjhaKsyCEtFGKKnYtxfrFcQA)

```
actor Client
participant Server
participant Handler
participant Service
participant DataAccess
database db

entryspacing 0.9
group #navy Registration #white
Client -> Server: [POST] /user\n{"username":" ", "password":" ", "email":" "}
Server -> Handler: {"username":" ", "password":" ", "email":" "}
Handler -> Service: register(RegisterRequest)
Service -> DataAccess: getUser(username)
DataAccess -> db:Find UserData by username
DataAccess --> Service: null
Service -> DataAccess:createUser(userData)
DataAccess -> db:Add UserData
Service -> DataAccess:createAuth(authData)
DataAccess -> db:Add AuthData
Service --> Handler: RegisterResult
Handler --> Server: {"username" : " ", "authToken" : " "}
Server --> Client: 200\n{"username" : " ", "authToken" : " "}
end

group #orange Login #white
#success
Client -> Server: [POST] /session\n{username, password}
Server->Handler:{"username":"","password":""}
Handler->Service:login(LoginRequest)
Service->DataAccess:getUser(username)
DataAccess->db:Find UserData by username
Service<--DataAccess:userData
Service->DataAccess:createAuth(authData)
DataAccess->db:Add AuthData
Service<--DataAccess:authToken
Handler<--Service:LoginResult
Server<--Handler:{"username":"","authToken":""}
Client<--Server:200\n{"username":"","authToken":""}
#no user match
Service<--DataAccess:null
Handler<--Service:Error
Server<--Handler:Error
Client<--Server:401\n{"message":"Error: unauthorized"}
#password mismatch
Service<--DataAccess:"password doesn't match username"
Handler<--Service:Error
Server<--Handler:Error
Client<--Server:401\n{"message":"Error: unathorized"}
end

group #green Logout #white
Client -> Server: [DELETE] /session\nauthToken
Server->Handler:{"authToken":""}
Handler->Service:logout(LogoutRequest)
Service->DataAccess:logout(authToken)
DataAccess->db:Delete authToken
Handler<--Service:LogoutResult
Server<--Handler:{}
Client<--Server:200\n{}
end

group #red List Games #white
Client -> Server: [GET] /game\nauthToken
Server->Handler:{"authToken:"}
Handler->Service:listGames(ListGamesRequest)
Service->DataAccess:getAuthorized(authToken)
DataAccess->db:Authorize
Service->DataAccess:listGames(authToken)
DataAccess->db:Get list of all games
Service<--DataAccess:gamesList
Handler<--Service:ListGamesResult
Server<--Handler:{ "games": [{"gameID": 1234, "whiteUsername":"", "blackUsername":"", "gameName:""} ]}
Client<--Server:200\n{ "games": [{"gameID": 1234, "whiteUsername":"", "blackUsername":"", "gameName:""} ]}
end

group #purple Create Game #white
Client -> Server: [POST] /game\nauthToken\n{gameName}
Server->Handler:{"authToken":"","gameName":""}
Handler->Service:createGame(CreateGameRequest)
Service->DataAccess:getAuthorized(authToken)
DataAccess->db:Authorize
Service->DataAccess:createGame(gameName)
DataAccess->db:Add GameData
Service<--DataAccess:gameData
Handler<--Service:CreateGameResult
Server<--Handler:{"gameID":""}
Client<--Server:200\n{"gameID":""}
end

group #yellow Join Game #black
Client -> Server: [PUT] /game\nauthToken\n{playerColor, gameID}
Server->Handler:{"authToken":"","playerColor":"","gameID":""}
Handler->Service:joinGame(JoinGameRequest)
Service->DataAccess:getAuthorized(authToken)
DataAccess->db:Authorize
Service->DataAccess:joinGame(gameID)
DataAccess->db:check that color not taken
Service<--DataAccess:null
Handler<--Service:JoinGameResult
Server<--Handler:{}
Client<--Server:200\n{}
Service<--DataAccess:Error: color taken
Handler<--Service:etc.
end

group #gray Clear application #white
Client -> Server: [DELETE] /db
Server->Handler:{}
Handler->Service:deleteDB(DeleteRequest)
Service->DataAccess:deleteDB()
DataAccess->db:Delete database
Service<--DataAccess:null
Handler<--Service:DeleteResult
Server<--Handler:{}
Client<--Server:200\n{}
end
```