package websocket.messages;

public class NotificationMessage extends ServerMessage {
    String message;
    public NotificationMessage(String message) {
        super(ServerMessageType.NOTIFICATION, message);
        this.message = message;
    }
    public String getData() {
        return this.message;
    }
}
