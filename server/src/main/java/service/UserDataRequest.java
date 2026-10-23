package service;

public record UserDataRequest(
        String username,
        String password,
        String email
) {}