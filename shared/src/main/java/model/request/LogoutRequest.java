package model.request;

public record LogoutRequest(String authToken, String storageType) {
}
