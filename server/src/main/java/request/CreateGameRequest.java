package request;

public record CreateGameRequest(String gameName, String authorization, String storageType) {
}
