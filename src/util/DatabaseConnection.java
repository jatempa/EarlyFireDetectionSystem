package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    //Parameters for connection to database.
    static final String DATABASE = "prototype";
    static final String USERNAME = "root";
    static final String PASSWORD = "sistemasitm";
    static final String BASE_URL = "jdbc:mysql://localhost/" + DATABASE;
    private static DatabaseConnection instance;
    private Connection connection;

    private DatabaseConnection() throws SQLException {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            this.connection = DriverManager.getConnection(BASE_URL, USERNAME, PASSWORD);
        } catch (ClassNotFoundException ex) {
            System.out.println("Database Connection Creation Failed : " + ex.getMessage());
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public static DatabaseConnection getInstance() throws SQLException {
        if (instance == null)
            instance = new DatabaseConnection();
        else if (instance.getConnection().isClosed())
            instance = new DatabaseConnection();

        return instance;
    }
}