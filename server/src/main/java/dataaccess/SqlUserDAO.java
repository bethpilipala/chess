package dataaccess;

import model.UserData;

import java.sql.SQLException;

public class SqlUserDAO implements UserDAO {

    @Override
    public void clear() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement("DELETE FROM users")) {
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessException("Error- unable to clear the users", e);
        }
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        if (user == null || user.username() == null) {
            throw new DataAccessException("Error: Bad request");
        }

        String sql = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.username());
            ps.setString(2, user.password());
            ps.setString(3, user.email());
            ps.executeUpdate();
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) { // MySQL duplicate key
                throw new DataAccessException("Error: Already Taken", e);
            }
            throw new DataAccessException("Error: unable to create user", e);
        }
    }
    @Override
    public UserData getUser(String username) throws DataAccessException {
        String sql = "SELECT username, password, email FROM users WHERE username = ?";
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (var rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return new UserData(rs.getString("username"), rs.getString("password"), rs.getString("email"));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: unable to get user", e);
        }
    }
}