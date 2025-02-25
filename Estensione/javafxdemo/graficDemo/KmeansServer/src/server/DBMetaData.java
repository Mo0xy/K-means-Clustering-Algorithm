package server;

import database.DbAccess;

/**
 * Classe che gestisce i metadati del database.
 *
 * @author Alessandro Ferrulli, Nazim Elmadhi.
 */

public class DBMetaData extends DbAccess {

    /**
     * Istanza di DBMetaData.
     */
    private static DBMetaData dbMetaData;

    /**
     * Nome del database.
     */
    private String dbName;

    /**
     * Nome utente del database.
     */
    private String userID;

    /**
     * Password del database.
     */
    private String password;

    /**
     * Costruttore della classe.
     */
    private DBMetaData(){
        super("127.0.0.1", null, null, null);
    }

    /**
     * Metodo che restituisce l'istanza di DBMetaData.
     *
     * @return Istanza di DBMetaData.
     */
    public static DBMetaData getDBMetaDataInstance(){
        if(dbMetaData == null) {
            dbMetaData = new DBMetaData();
        }
        return dbMetaData;
    }

    /**
     * Metodo per impostare il nome del database.
     *
     * @param DATABASE Nome del database.
     */
    @Override
    protected void setDATABASE(String DATABASE) {
        dbName = DATABASE;
        super.setDATABASE(DATABASE);
    }

    /**
     * Metodo per impostare l'utente del database.
     *
     * @param USER_ID utente del database.
     */
    @Override
    protected void setUSERID(String USER_ID) {
        userID = USER_ID;
        super.setUSERID(USER_ID);
    }

    /**
     * Metodo per impostare la password del database.
     *
     * @param PASSWORD Password del database.
     */
    @Override
    protected void setPASSWORD(String PASSWORD) {
        password = PASSWORD;
        super.setPASSWORD(PASSWORD);
    }

    /**
     * Metodo per ottenere il nome del database.
     *
     * @param db nome del database.
     * @param user nome utente del database.
     * @param psw password del database.
     */
    public void updateDBParameters(String db, String user, String psw){
        setDATABASE(db);
        setUSERID(user);
        setPASSWORD(psw);

    }

    /**
     * Metodo per ottenere l'istanza di DbAccess corrente.
     *
     * @return Istanza di DbAccess.
     */
    protected DbAccess getCurrentDb(){
        return new DbAccess("127.0.0.1", getDbName(), getUserID(), getPassword());
    }

    /**
     * Metodo per ottenere il nome del database.
     *
     * @return Nome del database.
     */
    protected String getDbName() {
        return super.getDATABASE();
    }

    /**
     * Metodo per ottenere l'utente del database.
     *
     * @return Utente del database.
     */
    protected String getUserID() {
        return super.getUSER_ID();
    }

    /**
     * Metodo per ottenere la password del database.
     *
     * @return Password del database.
     */
    protected String getPassword() {
        return super.getPASSWORD();
    }
}
