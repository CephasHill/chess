package websocket.messages;

public class ErrorMessage extends ServerMessage {
    String errorMessage;
    public ErrorMessage(String errorMessage) {
        super(ServerMessageType.ERROR, errorMessage);
        this.errorMessage = errorMessage;
    }
    public String getData() {
        return errorMessage;
    }
}
