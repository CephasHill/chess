package websocket.messages;

public class LoadGameMessage extends ServerMessage {
    String game;
    public LoadGameMessage(String game) {
        super(ServerMessageType.LOAD_GAME, game);
        this.game = game;
    }
    public String getData() {
        return this.game;
    }
}
