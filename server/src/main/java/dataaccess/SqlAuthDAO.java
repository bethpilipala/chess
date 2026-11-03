package dataaccess;

import model.AuthData;

import java.sql.SQLException;

public class SqlAuthDAO implements AuthDAO {

    @Override
    public void clear() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement("DELETE FROM auths")) {
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error: not able to clear auths ", e);
        }
    }

    @Override
    public void createAuth(AuthData auth) throws DataAccessException {
        if (auth == null || auth.authToken() == null) {
            throw new DataAccessException("Error: Bad Request");
        }

        String sql = "REPLACE INTO auths (authToken, username) VALUES (?, ?)";
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(sql)) {
            ps.setString(1, auth.authToken());
            ps.setString(2, auth.username());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessException("Error: unable to create auth", e);
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        String sql = "SELECT authToken, username FROM auths WHERE authToken = ?";
        try (var conn = DatabaseManager.getConnection(); var ps = conn.prepareStatement(sql)) {
            ps.setString(1, authToken);

            try (var rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return new AuthData(rs.getString("authToken"), rs.getString("username"));
            }

        } catch (SQLException e) {
            throw new DataAccessException("Error: unable to get the auth", e);
        }
    }


    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection(); var ps = conn.prepareStatement("DELETE FROM auths WHERE authToken = ?")) {
            ps.setString(1, authToken);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessException("Error: Unable to delete auth", e);
        }
    }
}