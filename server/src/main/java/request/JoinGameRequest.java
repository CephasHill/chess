package request;

public record JoinGameRequest(String playerColor, int gameID, String authToken, String storageType) {
}
