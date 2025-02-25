package server;

import data.Data;
import data.OutofRangeSampleSize;
import database.*;
import mining.KmeansMiner;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.SQLException;

/**
 * Classe che gestisce la connessione con un client.
 *
 * @author Alessandro Ferrulli, Nazim Elmadhi.
 */
public class ServerOneClient extends Thread{

    /***
     * Socket per la comunicazione con il client.
     */
    private Socket socket;

    /**
     * Stream d'input e output per la comunicazione con il client.
     */
    private ObjectInputStream in;

    /**
     * Stream d'input e output per la comunicazione con il client.
     */
    private ObjectOutputStream out;

    /**
     * Oggetto che gestisce l'algoritmo di clustering.
     */
    private KmeansMiner kmeans;

    /**
     * Nome della tabella da cui estrarre i dati.
     */
    //static String tableName = null;

    /**
     * Costruttore della classe.
     *
     * @param s Socket per la comunicazione con il client.
     *
     * @throws IOException Eccezione lanciata in caso di errore d'I/O.
     */
    public ServerOneClient(Socket s) throws IOException{
        socket = s;
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
        this.start();
    }

    /**
     * Metodo che gestisce la comunicazione con il client.
     */
    @Override
    public void run() {
        //DBMetaData dbMetaData;
        String dbName = "";
        String tableName = "";
        Integer numberOfClusters = 0;

        while (true) {
            try {

                System.out.println("ServerOneClient: waiting for request");
                int request = (Integer) in.readObject();
                System.out.println("ServerOneClient: request received");

                switch (request) {
                    case 0:
                        System.out.println("ServerOneClient: request 0");
                        try {
                            tableName = storeTableFromDb();
                            if (tableName == null)
                                throw new ServerException("Table name is null");
                            out.writeObject("OK");

                            break;
                        } catch (ServerException e){
                            out.writeObject(e.getMessage());
                            break;
                        }
                    case 1:
                        System.out.println("ServerOneClient: request 1");
                        numberOfClusters = (Integer) in.readObject();
                        String result;
                        try {
                            result = learningFromDb(dbName, tableName, numberOfClusters);
                        } catch (ServerException | DatabaseConnectionException | OutofRangeSampleSize e){
                            out.writeObject(e.getMessage());
                            break;
                        }

                        out.writeObject("OK");
                        out.writeObject(result);

                        System.out.println("ServerOneClient: request 1 done");
                        break;
                    case 2:
                        System.out.println("ServerOneClient: request 2");
                        try {
                            String db = (String)in.readObject();
                            String userID = (String) in.readObject();
                            String psw = (String) in.readObject();
                            String result2 = storeClusterInFile(db, userID, psw, tableName, numberOfClusters );
                            out.writeObject("OK");
                            out.writeObject(result2);
                        } catch (FileNotFoundException e) {
                            System.out.println("Cannot save file!");
                            System.out.println(e.getMessage());
                            out.writeObject(e.getMessage());
                        }
                        break;

                    case 3:
                        System.out.println("ServerOneClient: request 3");
                        String resultFromFile;
                        try {
                            resultFromFile = learningFromFile();
                        } catch (DatabaseConnectionException | ServerException e){
                            out.writeObject(e.getMessage());
                            break;
                        }

                        out.writeObject("OK");
                        out.writeObject(resultFromFile);
                        break;

                    case 4:
                        System.out.println("ServerOneClient: request 4");
                        out.writeObject(TableSchema.getCurrentTableSchema());
                        break;

                    case 5:
                        System.out.println("ServerOneClient: request 5");
                        dbName = (String) in.readObject();
                        String user = (String) in.readObject();
                        String psw = (String) in.readObject();
                        DBMetaData.getDBMetaDataInstance().setDATABASE(dbName);
                        DBMetaData.getDBMetaDataInstance().setUSERID(user);
                        DBMetaData.getDBMetaDataInstance().setPASSWORD(psw);

                        break;
                }
            } catch (IOException e){
                System.out.println("ServerOneClient: IOException");
                break;
            } catch (ClassNotFoundException e){
                System.out.println("ServerOneClient: ClassNotFoundException");
            }

        }
        try {
            socket.close();
        } catch (IOException e){
            System.out.println("ServerOneClient: failed to close socket");
        }
    }

    /**
     * Metodo che salva il nome della tabella da cui estrarre i dati.
     *
     * @return Nome della tabella da cui estrarre i dati.
     */
    private String storeTableFromDb(){
        String tableName = null;
        try {
            tableName = (String) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        return tableName;
    }

    /**
     * Metodo che gestisce l'algoritmo di clustering.
     *
     * @param dbName Nome del database.
     * @param tableName Nome della tabella da cui estrarre i dati.
     * @param numberOfClusters Numero di cluster da creare.
     *
     * @return Stringa contenente i risultati dell'algoritmo di clustering.
     *
     * @throws ServerException Eccezione lanciata in caso di errore.
     * @throws DatabaseConnectionException Eccezione lanciata in caso di errore di connessione al database.
     * @throws OutofRangeSampleSize Eccezione lanciata in caso di errore di dimensione del campione.
     */
    private String learningFromDb(String dbName, String tableName, int numberOfClusters) throws ServerException, DatabaseConnectionException, OutofRangeSampleSize{
        String result = null;
        Data data;
        try {
            data = new Data(dbName,
                    DBMetaData.getDBMetaDataInstance().getUserID(),
                    DBMetaData.getDBMetaDataInstance().getPassword(),
                    tableName);

            if (numberOfClusters > data.getNumberOfExamples())
                throw new OutofRangeSampleSize("Number of clusters is greater than number of examples");
            kmeans = new KmeansMiner(numberOfClusters);
            int numberOfIterations = kmeans.kmeans(data);
            result = "\nNumber of iterations: " + numberOfIterations + "\n" +
                    kmeans.getC().toString() + "\n" +
                    kmeans.getC().toString(data);

        } catch (DatabaseConnectionException e){
            System.out.println("ServerOneClient: DatabaseConnectionException (!)");
            throw new DatabaseConnectionException(e.getMessage());
        } catch (OutofRangeSampleSize e) {
            System.out.println("ServerOneClient: OutOfRangeSampleSizeException (!)");
            throw new ServerException(e.getMessage());
        } catch (SQLException e) {
            System.out.println("ServerOneClient: SQLException (!)");
            throw new DatabaseConnectionException(e.getMessage());
        } catch (EmptySetException e)  {
            System.out.println("ServerOneClient: EmptySetException (!)");
            throw new DatabaseConnectionException(e.getMessage());
        } catch (NoValueException e) {
            System.out.println("ServerOneClient: NoValueException (!)");
            throw new DatabaseConnectionException(e.getMessage());
        }

        return result;
    }


    /***
     * Metodo che salva i cluster in un file.
     *
     * @param dbName Nome del database.
     * @param userID Nome utente del database.
     * @param psw Password del database.
     * @param tableName Nome della tabella.
     * @param numberOfIteration Numero d'iterazioni.
     *
     *
     * @return Stringa contenente il risultato dell'operazione.
     */
    private String storeClusterInFile(String dbName, String userID, String psw, String tableName, int numberOfIteration){
        String result = null;

        String fileName = "graficDemo/KmeansServer/saves/" + dbName + "-" + tableName + "-" + numberOfIteration + ".dat";
        kmeans.save(fileName, new String[]{dbName, userID, psw});
        result = "Cluster saved in " + fileName;
        return result;
    }

    /**
     * Metodo che gestisce l'algoritmo di clustering da un file.
     *
     * @return Stringa contenente i risultati dell'algoritmo di clustering.
     *
     * @throws ServerException Eccezione lanciata in caso di errore.
     * @throws DatabaseConnectionException Eccezione lanciata in caso di errore di connessione al database.
     */
    private String learningFromFile() throws DatabaseConnectionException, ServerException {
        String result = "file not found.\n";
        try {
            String dbName = (String) in.readObject();
            String tableName = (String) in.readObject();
            String numberOfIterations = in.readObject().toString();

            String fileName = "graficDemo/KmeansServer/saves/" + dbName + "-" + tableName + "-" + numberOfIterations + ".dat";
            System.out.println(fileName);
            kmeans = new KmeansMiner(fileName);
            Data data = new Data(dbName,
                    DBMetaData.getDBMetaDataInstance().getUserID(),
                    DBMetaData.getDBMetaDataInstance().getPassword(),
                    tableName);

            kmeans.save(fileName, new String[]{
                    DBMetaData.getDBMetaDataInstance().getDbName(),
                    DBMetaData.getDBMetaDataInstance().getUserID(),
                    DBMetaData.getDBMetaDataInstance().getPassword(),
            });
            result = kmeans.getC().toString(data);
        } catch (IOException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (NoValueException | SQLException | EmptySetException e) {
            throw new ServerException(e.getMessage());
        }
        return result;
    }


}


