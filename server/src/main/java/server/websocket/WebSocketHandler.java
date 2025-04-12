package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import websocket.commands.UserGameCommand;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;

public class WebSocketHandler {
    private final ConnectionManager connections = new ConnectionManager();
    Session session;

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        this.session = session;
        try {
            Gson gson = new Gson();
            UserGameCommand command = gson.fromJson(message, UserGameCommand.class);
            switch (command.getCommandType()) {
                case CONNECT -> connect(command);
                case MAKE_MOVE -> makeMove();
                case LEAVE -> leave();
                case RESIGN -> resign();
            }
        } catch (Exception e ) {

        }
    }

    private void connect(UserGameCommand command) throws IOException {
        String username = command.getAuthData().username();
        String authToken = command.getAuthData().authToken();
        connections.add(username, session);
        var message = String.format("%s connected to the game", username);
        var serverMessage = new NotificationMessage(message);
        connections.broadcast(username, serverMessage);
    }

    private void makeMove() {
    }

    private void leave() {
    }

    private void resign() {
    }
}
