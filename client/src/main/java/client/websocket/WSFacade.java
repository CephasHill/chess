package client.websocket;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

public class WSFacade extends Endpoint {
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
                    Gson gson = new Gson();
                    JsonObject json = gson.fromJson(message, JsonObject.class);

                    String typeString = json.get("serverMessageType").getAsString();
                    ServerMessage.ServerMessageType type = switch (typeString) {
                        case "NOTIFICATION" -> ServerMessage.ServerMessageType.NOTIFICATION;
                        case "LOAD_GAME" -> ServerMessage.ServerMessageType.LOAD_GAME;
                        default -> ServerMessage.ServerMessageType.ERROR;
                    };
                    String msg = json.get("message").getAsString() + "\n";

                    String serverMessageJson = gson.toJson(Map.of("data", msg, "type", type));
                    ServerMessage serverMessage = new ServerMessage(type, msg) {
                        @Override
                        public Object getData() {
                            return msg;
                        }
                    };
                    notificationHandler.notify(serverMessage);
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(500, "WSFacade constructor: " + ex.getMessage());
        }
    }

    //Endpoint requires this method, but you don't have to do anything
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void connect(GameData data, AuthData authData) throws ResponseException {
        System.out.print("step2, entered WSFacade.connect\n");
        try {
            UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authData, data.gameID());
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (Exception e) {
            throw new ResponseException(500, "WSFacade.connect: " + e.getMessage());
        }
    }
}
