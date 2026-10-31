package dataaccess;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class DatabaseSchema {
    private DatabaseSchema() {}

    public static void initialize() throws DataAccessException {
        DatabaseManager.createDatabase();

        String createUsers =
                """ 
                CREATE TABLE IF NOT EXISTS users (
                    username VARCHAR(255) PRIMARY KEY,
                    password VARCHAR(255) NOT NULL,
                    email VARCHAR(255) NOT NULL
                )
                """;
        String createAuths =
                """
                CREATE TABLE IF NOT EXISTS auths (
                    authToken VARCHAR(255) PRIMARY KEY,
                    username VARCHAR(255) NOT NULL
                )
                """;
        String createGames =
                """
                CREATE TABLE IF NOT EXISTS games (
                    gameID INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                    whiteUsername VARCHAR(255),
                    blackUsername VARCHAR(255),
                    gameName VARCHAR(255) NOT NULL,
                    chessGame LONGTEXT NOT NULL
                )
                """;

        Connection connection = null;
        Statement statement = null;

        try {
            connection = DatabaseManager.getConnection();
            statement = connection.createStatement();

            statement.execute(createUsers);
            statement.execute(createAuths);
            statement.execute(createGames);
        } catch (SQLException e) {
            throw new DataAccessException("Error-- Unable to initialize database schema", e);
    }
}