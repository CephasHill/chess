package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import websocket.commands.UserGameCommand;

public class WebSocketHandler {
    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        try {
            Gson gson = new Gson();
            UserGameCommand command = gson.fromJson(message, UserGameCommand.class);
            switch (command.getCommandType()) {
                case CONNECT -> connect();
                case MAKE_MOVE -> makeMove();
                case LEAVE -> leave();
                case RESIGN -> resign();
            }
        } catch (Exception e ) {

        }
    }

    private void connect() {
    }

    private void makeMove() {
    }

    private void leave() {
    }

    private void resign() {
    }
}
