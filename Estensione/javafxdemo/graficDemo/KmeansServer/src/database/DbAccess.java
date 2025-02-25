package database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
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
    String DRIVER_CLASS_NAME = "com.mysql.cj.jdbc.Driver";
    /**
     * Costante per il nome del DBMS.
     */
    final String DBMS = "jdbc:mysql";
    /**
     * Costante per il nome del server.
     */
    String SERVER = "localhost";
    /**
     * Costante per il nome del database.
     */
    String DATABASE = "MapDB";

    /**
     * Costante per la porta del server.
     */
    final String PORT = "3306";

    /**
     * Costante per l'utente del database.
     */
    String USER_ID = "MapUser";
    /**
     * Costante per la password dell'utente del database.
     */
    String PASSWORD = "map"; //map
    /**
     * Connessione al database.
     */
    Connection conn;

    /**
    * Costruttore di classe parametrizzato.
    *
    * @param server nome del server.
    * @param dbName nome del database.
    * @param user nome dell'utente.
    * @param psw password dell'utente.
    */
    public DbAccess(String server, String dbName, String user, String psw) {
        SERVER = server;
        DATABASE = dbName;
        USER_ID = user;
        PASSWORD = psw;
    }

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

    /**
     * Imposta il nome del database.
     *
     * @param  DATABASE nome del database.
     */
    protected void setDATABASE(String DATABASE) { this.DATABASE = DATABASE; }

    /**
     * Imposta l'utente del database.
     *
     * @param USER_ID utente del database.
     */
    protected void setUSERID(String USER_ID) {
        this.USER_ID = USER_ID;
    }

    /**
     * Imposta la password dell'utente del database.
     *
     * @param PASSWORD password dell'utente del database.
     */
    protected void setPASSWORD(String PASSWORD) {
        this.PASSWORD = PASSWORD;
    }

    /**
     * Restituisce il nome del database.
     * @return DATABASE
     */
    protected String getDATABASE() {
        return DATABASE;
    }

    /**
     * Restituisce l'utente del database.
     * @return USER_ID
     */
    protected String getUSER_ID() {
        return USER_ID;
    }

    /**
     * Restituisce la password dell'utente del database.
     * @return PASSWORD
     */
    protected String getPASSWORD() {
        return PASSWORD;
    }
}



