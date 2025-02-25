package server;

import data.Data;
import data.OutofRangeSampleSize;
import database.DatabaseConnectionException;
import database.EmptySetException;
import database.NoValueException;
import mining.KmeansMiner;
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
public class ServerOneClient extends Thread {

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
    static String tableName = null;

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
        while (true) {
            try {
                System.out.println("ServerOneClient: waiting for request");
                int request = -1;
                try {
                    request = (Integer) in.readObject();
                } catch (ClassNotFoundException e){
                    System.out.println("ServerOneClient: ClassNotFoundException");
                    out.writeObject(e.getMessage());
                }

                System.out.println("ServerOneClient: request received");

                switch (request) {
                    case 0: //kmeans.storeTableFromDb();
                        System.out.println("ServerOneClient: request 0");
                        try {
                            tableName = storeTableFromDb();
                            System.out.println("ServerOneClient: tableName received");
                            if (tableName == null)
                                throw new ServerException("Table name is null");
                            out.writeObject("OK");

                        } catch (ServerException e){
                            out.writeObject(e.getMessage());
                            break;
                        }
                        break;

                    case 1:
                        System.out.println("ServerOneClient: request 1");
                        try {
                            int numberOfClusters = (Integer) in.readObject();
                            String result;

                            result = learningFromDb(tableName, numberOfClusters);
                            System.out.println("ServerOneClient: result received");
                            out.writeObject("OK");
                            out.writeObject(result);
                        } catch (ServerException e){
                            if (e.getMessage().contains("You have an error in your SQL syntax"))
                                out.writeObject("You have an error in your SQL syntax");
                            else if (e.getMessage().contains("Table") && e.getMessage().contains("doesn't exist"))
                                out.writeObject("Table doesn't exist");
                            else
                                out.writeObject(e.getMessage());
                            break;
                        } catch (IOException e){
                            System.out.println("ServerOneClient: IOException");
                            System.out.println(":: " + e.getMessage());
                            break;
                        }
                        break;

                    case 3:
                        System.out.println("ServerOneClient: request 3");
                        try {
                            String resultFromFile = learningFromFile();
                            out.writeObject("OK");
                            out.writeObject(resultFromFile);
                        } catch (ServerException e){
                            out.writeObject("Error: " + e.getMessage());
                        }

                        break;
                }
            } catch (IOException e){
                System.out.println("Error: " + e.getMessage());
                System.out.println("ServerOneClient: IOException");
                break;
            } catch (ClassNotFoundException e){
                System.out.println("ServerOneClient: ClassNotFoundException");
                break;
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
    public String storeTableFromDb(){
        String TableName = null;
        try {
            TableName = (String) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return TableName;
    }

    /**
     * Metodo che gestisce l'algoritmo di clustering.
     *
     * @param tableName Nome della tabella da cui estrarre i dati.
     * @param numberOfClusters Numero di cluster da creare.
     *
     * @return Stringa contenente i risultati dell'algoritmo di clustering.
     *
     * @throws ServerException Eccezione lanciata in caso di errore.
     */
    public String learningFromDb(String tableName, int numberOfClusters) throws ServerException{
        String result = null;
        Data data = null;
        try {
            data = new Data(tableName);

            if (numberOfClusters > data.getNumberOfExamples())
                throw new ServerException("Number of clusters is greater than number of examples");

            kmeans = new KmeansMiner(numberOfClusters);
            int numberOfIterations = kmeans.kmeans(data);
            result = "\nNumber of iterations: " + numberOfIterations + "\n" +
                    kmeans.getC().toString() + "\n" +
                    kmeans.getC().toString(data);

            storeClusterInFile(numberOfClusters);
        } catch (DatabaseConnectionException e){
            System.out.println("ServerOneClient: DatabaseConnectionException (!)");
            System.out.println(e.getMessage());
        } catch (OutofRangeSampleSize e2) {
            System.out.println("ServerOneClient: OutOfRangeSampleSizeException (!)");
            System.out.println(e2.getMessage());
        } catch (NoValueException | SQLException | EmptySetException e) {
            if (e.getMessage().contains("You have an error in your SQL syntax")) {
                throw new ServerException("You have an error in your SQL syntax");
            }
            throw new ServerException(e.getMessage());
        }

        return result;
    }

    //2

    /***
     * Metodo che salva i cluster in un file.
     *
     * @param numberOfIteration Numero d'iterazioni.
     *
     */
    private void storeClusterInFile(int numberOfIteration){
        String result = null;
        try {
            String fileName = ".\\KmeansBase\\src\\main\\java\\org\\example\\KmeansServer\\saves/" + tableName + "_" + numberOfIteration + ".dat";
            kmeans.save(fileName);
            System.out.println("Cluster saved in " + fileName);
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }


    //3

    /**
     * Metodo che gestisce l'algoritmo di clustering da un file.
     *
     * @return Stringa contenente i risultati dell'algoritmo di clustering.
     *
     * @throws ServerException Eccezione lanciata in caso di errore.
     */
    public String learningFromFile() throws ServerException {
        String result = "file not found.\n";

        try {
            String tableName = (String) in.readObject();
            Data data = new Data(tableName);
            String numberOfIterations = in.readObject().toString();
            String fileName = ".\\KmeansBase\\src\\main\\java\\org\\example\\KmeansServer\\saves/" + tableName + "_" + numberOfIterations + ".dat";

            kmeans = new KmeansMiner(fileName);
            kmeans.save(fileName);
            result = kmeans.getC().toString(data);
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (DatabaseConnectionException | NoValueException | SQLException | EmptySetException | ServerException e) {
            if (e.getMessage().contains("You have an error in your SQL syntax")) {
                try {
                    in.readObject();
                } catch (IOException | ClassNotFoundException e1) {
                    System.out.println("Error: " + e1.getMessage());
                }
                throw new ServerException("You have an error in your SQL syntax");
            }
            throw new ServerException(e.getMessage());

        }
        return result;
    }
}
