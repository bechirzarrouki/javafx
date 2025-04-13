package Main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private Connection cnx;
    private static DatabaseConnection instance;

    public Connection getCnx() {
        return cnx;
    }

    private DatabaseConnection() {
        String url = "jdbc:mysql://localhost:3306/pi";
        String username = "root";
        String password = "";

        try {
            // Charger explicitement le driver MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Établir la connexion
            cnx = DriverManager.getConnection(url, username, password);
            System.out.println("Connexion établie avec succès");
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la connexion : " + e.getMessage());
            throw new RuntimeException("Erreur de connexion à la base de données : " + e.getMessage(), e);
        } catch (ClassNotFoundException e) {
            System.err.println("Driver MySQL non trouvé : " + e.getMessage());
            throw new RuntimeException("Driver MySQL non trouvé. Assurez-vous que le driver est dans le classpath.", e);
        }
    }

    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }
}
