package client.websocket;

import com.google.gson.Gson;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WSFacade {
    Session session;
    NotificationHandler notificationHandler;

    public WSFacade(int port, NotificationHandler notificationHandler) throws ResponseException {
        try {
            String url = "ws://localhost:" + port;
            URI socketURI = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
                    notificationHandler.notify(serverMessage);
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(500, "WSFacade constructor:" + ex.getMessage());
        }
    }

    public void connect(GameData data, AuthData authData) throws ResponseException {
        try {
            this.session.getBasicRemote().sendObject(new UserGameCommand(UserGameCommand.CommandType.CONNECT, authData, data.gameID()));
        } catch (Exception e) {
            throw new ResponseException(500, "WSFacade.connect: " + e.getMessage());
        }
    }
}
