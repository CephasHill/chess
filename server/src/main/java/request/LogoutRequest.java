package request;

public record LogoutRequest(String authToken, String storageType) {
}
