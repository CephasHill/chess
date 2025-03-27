package service;

import dataaccess.DataAccessException;
import model.request.DeleteRequest;
import model.request.LoginRequest;
import model.request.LogoutRequest;
import model.request.RegisterRequest;
import model.result.*;
import org.junit.jupiter.api.*;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServiceTests {

    private UserService userService;
    private GameService gameService;
    private ClearService clearService;

    @BeforeEach
    public void setup() {
        // Initialize your service (you might use dependency injection or mocks in a real scenario)
        userService = new UserService();
        gameService = new GameService(); // Replace with your actual instantiation4
        clearService = new ClearService();
        clearService.clearAll(new DeleteRequest("mem"));
        // Clear any existing data if your service has a clear method
        // Assuming this exists for test isolation
    }

    @Test
    @Order(1) // Replace X with the order number
    @DisplayName("Register positive test")
    public void registerPos() throws DataAccessException {
        RegisterResult res = userService.register(new RegisterRequest("name", "pass", "email", "mem"));

        assertNotNull(res);
        assertEquals("name", res.authData().username());
    }
    @Test
    @Order(2) // Replace X with the order number
    @DisplayName("Register negative test")
    public void registerNeg() {

        assertThrows(DataAccessException.class, () -> {
            userService.register(new RegisterRequest("name","l",null, "mem")); // Duplicate username
        });
    }
    @Test
    @Order(3) // Replace X with the order number
    @DisplayName("Logout positive test")
    public void logoutPos() throws DataAccessException {
        RegisterResult regRes = userService.register(new RegisterRequest("name", "pass", "email", "mem"));
        LogoutResult res = userService.logout(new LogoutRequest(regRes.authData().authToken(),"mem"));
        assertNotNull(res);
        assertEquals(new LogoutResult(), res);
    }
    @Test
    @Order(4) // Replace X with the order number
    @DisplayName("Logout negative test")
    public void logoutNeg() {

        assertThrows(DataAccessException.class, () -> {
            userService.logout(new LogoutRequest("j","mem"));
        });
    }
    @Test
    @Order(5) // Replace X with the order number
    @DisplayName("Login positive test")
    public void loginPos() throws DataAccessException {
        RegisterResult regRes = userService.register(new RegisterRequest("user", "pass", "email","mem"));
        LogoutResult logoutRes = userService.logout(new LogoutRequest(regRes.authData().authToken(),"mem"));
        LoginResult loginRes = userService.login(new LoginRequest("user","pass","mem"));
        assertNotNull(loginRes);

    }
    @Test
    @Order(6) // Replace X with the order number
    @DisplayName("Login negative test")
    public void loginNeg() {

        assertThrows(DataAccessException.class, () -> {
            userService.login(new LoginRequest("name","pass","mem"));
        });
    }
    @Test
    @Order(7) // Replace X with the order number
    @DisplayName("List games positive test")
    public void listPos() throws DataAccessException {
        RegisterResult regRes = userService.register(new RegisterRequest("user", "pass", "email","mem"));
        CreateGameResult createRes = gameService.createGame("game",regRes.authData().authToken(), "mem");
        ListGamesResult listRes = gameService.listGames(regRes.authData().authToken(), "mem");

        assertNotNull(listRes);
        assertEquals(new ListGamesResult(new ArrayList<>(listRes.games())), listRes);

    }
    @Test
    @Order(8) // Replace X with the order number
    @DisplayName("List games negative test")
    public void listNeg() {

        assertThrows(DataAccessException.class, () -> {
            gameService.listGames("", "mem");
        });
    }
    @Test
    @Order(9) // Replace X with the order number
    @DisplayName("Create game positive test")
    public void createPos() throws DataAccessException {
        RegisterResult regRes = userService.register(new RegisterRequest("user", "pass", "email","mem"));
        CreateGameResult createRes = gameService.createGame("game",regRes.authData().authToken(), "mem");

        assertNotNull(createRes);
        assertEquals(new CreateGameResult(createRes.gameID()), createRes);

    }
    @Test
    @Order(10) // Replace X with the order number
    @DisplayName("Create game negative test")
    public void createNeg() {

        assertThrows(DataAccessException.class, () -> {
            gameService.createGame("game",null, "mem");
        });
    }
    @Test
    @Order(11) // Replace X with the order number
    @DisplayName("Join game positive test")
    public void joinPos() throws DataAccessException {
        RegisterResult regRes = userService.register(new RegisterRequest("user", "pass", "email","mem"));
        CreateGameResult createRes = gameService.createGame("game",regRes.authData().authToken(), "mem");
        JoinGameResult joinRes = gameService.joinGame("white", createRes.gameID(), regRes.authData().authToken(), "mem");

        assertNotNull(joinRes);

    }
    @Test
    @Order(12) // Replace X with the order number
    @DisplayName("Join game negative test")
    public void joinNeg() {

        assertThrows(DataAccessException.class, () -> {
            gameService.joinGame("white",1,"black", "mem");
        });
    }
    @Test
    @Order(11) // Replace X with the order number
    @DisplayName("clear positive test")
    public void clearPos() throws DataAccessException {
        RegisterResult regRes = userService.register(new RegisterRequest("user", "pass", "email","mem"));
        CreateGameResult createRes = gameService.createGame("game",regRes.authData().authToken(), "mem");
        JoinGameResult joinRes = gameService.joinGame("white", createRes.gameID(), regRes.authData().authToken(), "mem");
        ClearService clearService = new ClearService();
        clearService.clearAll(new DeleteRequest("mem"));

        assertNotNull(joinRes);

    }
    @Test
    @Order(12) // Replace X with the order number
    @DisplayName("clear negative test")
    public void clearNeg() {

        assertThrows(NullPointerException.class, () -> {
            clearService.clearAll(null);
        });
    }
}