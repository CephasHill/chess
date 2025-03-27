package client;

import exception.ResponseException;
import model.request.*;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;
    private final String storageType = "sql";

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);
        try {
            facade.clearDatabase(new DeleteRequest(server.storageType));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @BeforeEach
    void clearDatabaseBeforeEach() {
        try {
            facade.clearDatabase(new DeleteRequest(storageType));
        } catch (ResponseException e) {
            fail("Failed to clear database before test: " + e.getMessage());
        }
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    void registerPos() throws Exception {
        var registerResult = facade.register(new RegisterRequest("username","password","email@email.com", storageType));
        assertTrue(registerResult.authToken().length() > 10);
    }
    @Test
    void registerNeg() {
        assertThrows(Exception.class, () -> facade.register(new RegisterRequest(null,"password","email@email.com", storageType)));
    }
    @Test
    void loginPos() throws Exception {
        var registerRes = facade.register(new RegisterRequest("username","password","email@email.com", storageType));
        facade.logout(new LogoutRequest(registerRes.authToken(),storageType));
        var res = facade.login(new LoginRequest("username","password",storageType));
        assertTrue(res.authToken().length() > 10);
    }
    @Test
    void loginNeg() {
        assertThrows(Exception.class, () -> facade.login(new LoginRequest("username","password",storageType)));
    }
    @Test
    void logoutPos() throws Exception {
        var registerRes = facade.register(new RegisterRequest("username","password","email@email.com", storageType));
        assertDoesNotThrow(() -> facade.logout(new LogoutRequest(registerRes.authToken(),storageType)));
    }
    @Test
    void logoutNeg() {
        assertThrows(Exception.class, () -> facade.logout(new LogoutRequest("badAuth",storageType)));
    }
    @Test
    void createGamePos() throws Exception {
        var registerRes = facade.register(new RegisterRequest("username","password","email@email.com", storageType));
        String auth = registerRes.authToken();
        assertDoesNotThrow(() -> facade.createGame(new CreateGameRequest("gameName", auth, storageType)));
    }
    @Test
    void createGameID() throws Exception {
        var registerRes = facade.register(new RegisterRequest("username", "password", "email@email.com", storageType));
        String auth = registerRes.authToken();
        var createRes = facade.createGame(new CreateGameRequest("gameName", auth, storageType));
        assertEquals(1,createRes.gameID());
    }
    @Test
    void createGameNeg() {
        assertThrows(Exception.class, () -> facade.createGame(new CreateGameRequest("gameName","badAuth",storageType)));
    }
    @Test
    void joinGamePos() throws Exception {
        var registerRes = facade.register(new RegisterRequest("username","password","email@email.com", storageType));
        String auth = registerRes.authToken();
        var createRes = facade.createGame(new CreateGameRequest("gameName", auth, storageType));
        assertDoesNotThrow(() -> facade.joinGame(new JoinGameRequest("WHITE",createRes.gameID(),auth, storageType)));
    }
    @Test
    void joinGameNeg() {
        assertThrows(Exception.class, () -> facade.joinGame(new JoinGameRequest("WHITE",1,"badAuth",storageType)));
    }
    @Test
    void listGamesPos() throws Exception {
        var registerRes = facade.register(new RegisterRequest("username","password","email@email.com", storageType));
        String auth = registerRes.authToken();
        facade.createGame(new CreateGameRequest("game1", auth, storageType));
        facade.createGame(new CreateGameRequest("game2", auth, storageType));
        facade.createGame(new CreateGameRequest("game3", auth, storageType));
        var listRes = facade.listGames(new ListGamesRequest(auth, storageType));
        assertEquals(3, listRes.games().size());
    }
    @Test
    void listGamesNeg() {
        assertThrows(Exception.class, () -> facade.listGames(new ListGamesRequest("badAuth",storageType)));
    }
    @Test
    void clearDatabase() throws Exception {
        assertDoesNotThrow(() -> facade.clearDatabase(new DeleteRequest(storageType)));
    }
}
