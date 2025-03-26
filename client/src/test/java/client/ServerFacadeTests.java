package client;

import model.request.DeleteRequest;
import model.request.LoginRequest;
import model.request.LogoutRequest;
import model.request.RegisterRequest;
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

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    void registerPos() throws Exception {
        var registerResult = facade.register(new RegisterRequest("username","password","email@email.com", storageType));
        assertTrue(registerResult.authData().authToken().length() > 10);
    }
    @Test
    void registerNeg() throws Exception {
        var registerResult = facade.register(new RegisterRequest("username","password","email@email.com", storageType));
        assertThrows(Exception.class, () -> facade.register(new RegisterRequest("username","password","email@email.com", storageType)));
    }
    @Test
    void loginPos() throws Exception {
        var registerReq = facade.register(new RegisterRequest("username","password","email@email.com", storageType));
        facade.logout(new LogoutRequest(registerReq.authData().authToken(),storageType));
        var res = facade.login(new LoginRequest("username","password",storageType));
        assertTrue(res.authToken().length() > 10);
    }
    @Test
    void loginNeg() {
        assertThrows(Exception.class, () -> facade.login(new LoginRequest("username","password",storageType)));
    }
    @Test
    void logoutPos() throws Exception {
        var registerReq = facade.register(new RegisterRequest("username","password","email@email.com", storageType));
        assertDoesNotThrow(() -> facade.logout(new LogoutRequest(registerReq.authData().authToken(),storageType)));
    }
    @Test
    void logoutNeg() {
        assertThrows(Exception.class, () -> facade.logout(new LogoutRequest("badAuth",storageType)));
    }
}
