package database;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Classe usata per la connessione al database.
 *
 * @author Alessandro Ferrulli, Nazim Elmadhi.
 */
public class DbAccess {
    /**
     * Costante per il nome del driver.
     */
    private String DRIVER_CLASS_NAME = "com.mysql.cj.jdbc.Driver";
    /**
     * Costante per il nome del DBMS.
     */
    private final String DBMS = "jdbc:mysql";
    /**
     * Costante per il nome del server.
     */
    private String SERVER = "localhost";
    /**
     * Costante per il nome del database.
     */
    private String DATABASE = "MapDB";

    /**
     * Costante per la porta del server.
     */
    private final String PORT = "3306";

    /**
     * Costante per l'utente del database.
     */
    private String USER_ID = "MapUser";
    /**
     * Costante per la password dell'utente del database.
     */
    private String PASSWORD = "map";
    /**
     * Connessione al database.
     */
    private Connection conn;


    /**
     * Inizializza la connessione al database.
     *
     * @throws DatabaseConnectionException se la connessione non può essere inizializzata.
     */
    public void initConnection() throws DatabaseConnectionException{
        try {
            Class.forName(DRIVER_CLASS_NAME);
            String connectionString = DBMS+"://"+SERVER+":"+PORT+"/"+DATABASE+"?user="+USER_ID+"&password="+PASSWORD+"&serverTimezone=UTC";
            conn = java.sql.DriverManager.getConnection(connectionString);
        } catch (Exception e) {
            throw new DatabaseConnectionException(e.getMessage());
        }
    }

    /**
     * Restituisce la connessione al database.
     *
     * @return connessione al database.
     */
    Connection getConnection(){
        return conn;
    }

    /**
     * Chiude la connessione al database.
     *
     * @throws SQLException se si verifica un errore nella chiusura della connessione.
     */
    void closeConnection() throws SQLException {
        try {
            conn.close();
        } catch (SQLException e) {
            System.out.println("Can not close connection");
        }
    }

}
