package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;

import java.util.UUID;

public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public AuthResult register(RegisterRequest req) throws BadRequestException, AlreadyTakenException, DataAccessException {

        validateRegister(req);

        if (userDAO.getUser(req.username()) != null)
            throw new AlreadyTakenException("Error: already taken");

        userDAO.createUser(new UserData(req.username(), req.password(), req.email()));

        String token = UUID.randomUUID().toString();
        authDAO.createAuth(new AuthData(token, req.username()));

        return new AuthResult(req.username(), token);
    }

    public AuthResult login(LoginRequest req)
            throws BadRequestException, UnauthorizedException, DataAccessException {

        validateLogin(req);

        UserData user = userDAO.getUser(req.username());
        if (user == null || !user.password().equals(req.password()))
            throw new UnauthorizedException("Error: unauthorized");

        String token = UUID.randomUUID().toString();
        authDAO.createAuth(new AuthData(token, user.username()));

        return new AuthResult(user.username(), token);
    }

    public void logout(String authToken) throws UnauthorizedException {
        if (authToken == null || authToken.isBlank() || authDAO.getAuth(authToken) == null) 
            throw new UnauthorizedException("Error: unauthorized");
        authDAO.deleteAuth(authToken);
    }

    private void validateRegister(RegisterRequest req) throws BadRequestException {
        if (req == null ||
                isBlank(req.username()) ||
                isBlank(req.password()) ||
                isBlank(req.email())) {
            throw new BadRequestException("Error: bad request");
        }
    }

    private void validateLogin(LoginRequest req) throws BadRequestException {
        if (req == null ||
                isBlank(req.username()) ||
                isBlank(req.password())) {
            throw new BadRequestException("Error: bad request");
        }
    }

    private boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}