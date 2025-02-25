package com.example.graficdemo;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Classe che avvia l'interfaccia grafica dell'applicazione.
 */
public class GUI extends Application {

    /**
     * Stage dell'applicazione.
     */
    private static Stage stage;

    /**
     * Metodo per avviare l'applicazione.
     *
     * @param stage Lo stage principale dell'applicazione, dove verranno visualizzate le scene.
     *
     * @throws IOException in caso di errore d'I/O.
     */
    @Override
    public void start(Stage stage) throws IOException {
        startMenuLoader(stage);
    }

    /**
     * Metodo per avviare il loader del menu iniziale.
     *
     * @param stage Stage da avviare.
     * @throws IOException in caso di errore d'I/O.
     */
    void startMenuLoader(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(GUI.class.getResource("start.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 748, 411);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setTitle("K-means");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        PauseTransition delay = new PauseTransition(Duration.seconds(3));

        delay.setOnFinished(event -> {
            stage.setOnCloseRequest(e -> {
                Platform.exit();
                System.exit(0);
            });
            stage.close();
            try {
                fileInterfaceLoader(GUI.stage);
            } catch (IOException e) {
                System.out.println("Error: " + e.getMessage());
            }
        });
        delay.play();
    }

    /**
     * Metodo per avviare il loader dell'interfaccia grafica.
     *
     * @param stage stage da avviare.
     * @throws IOException in caso di errore d'I/O.
     */
    void fileInterfaceLoader(Stage stage) throws IOException {
        stage = new Stage();
        stage.initStyle(StageStyle.DECORATED);
        FXMLLoader fxmlLoader = new FXMLLoader(GUI.class.getResource("UpdatedUserInterface.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root, 930, 581);
        stage.setTitle("K-means");
        stage.setScene(scene);
        stage.setResizable(true);
        stage.show();

    }

    /**
     * Metodo per avviare l'applicazione.
     * @param args Parametri da riga di comando.
     */
    public static void main(String[] args) {
        Client.ip = args[0];
        try {
        Client.port = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            Controller.displayError("Invalid port");
            Platform.exit();
        }
        launch();
    }
}

/**
 * Classe che rappresenta il client che si occupa di inviare le richieste al server.
 */
class Client {
    /**
     * Istanza del client.
     */
    private static Client client;
    /**
     * Stream di output.
     */
    private ObjectOutputStream out;
    /**
     * Stream di input.
     */
    private ObjectInputStream in ; // stream con richieste del client

    /**
     * Indirizzo IP del server a cui collegarsi.
     */
    protected static String ip;
    /**
     * Porta del server a cui collegarsi.
     */
    protected static int port;

    /**
     * Costruttore della classe.
     *
     * @param ip Indirizzo IP del server.
     * @param port Porta del server.
     */
    private Client(String ip, int port) {

        try {
            InetAddress addr = InetAddress.getByName(ip); //ip
            //System.out.println("addr = " + addr);
            Socket socket = new Socket(addr, port); //Port
            //System.out.println(socket);

            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream()); // stream con richieste del client
        } catch (UnknownHostException e) {
            Controller.displayError("Unknown host");
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            if (e.getMessage().equals("Connection refused: connect")) {
                Controller.displayError("Server not available. Please turn on the server");
            }
            if (e.getMessage().equals("Connection timed out: connect")) {
                Controller.displayError("server unreachable");
                PauseTransition delay = new PauseTransition(Duration.seconds(2));
                delay.setOnFinished(event -> {
                    Platform.exit();});
                delay.play();
            }

            if (e.getMessage().equals("Connection reset"))
                Controller.displayError("Connection lost");
        }
    }

    /**
     * Metodo per ottenere l'istanza del client.
     *
     * @return Istanza del client.
     *
     * @throws IOException in caso di errore d'I/O.
     */
    public static Client getClientInstance() throws IOException {
        if (client == null) {
            client = new Client(ip, port);
        }
        return client;
    }


    /**
     * Metodo per richiedere al server di eseguire il clustering.
     *
     * @param dbName Nome del database.
     * @param tabName Nome della tabella.
     * @param NumOfClusters Numero di cluster.
     *
     * @return Stringa che rappresenta l'output del clustering.
     *
     * @throws ServerException in caso di errore del server.
     * @throws IOException in caso di errore d'I/O.
     * @throws ClassNotFoundException in caso di errore di classe non trovata.
     */
    protected String learningFromFile(String dbName, String tabName, String NumOfClusters) throws ServerException, IOException, ClassNotFoundException {
        out.writeObject(3);

        out.writeObject(dbName);
        out.writeObject(tabName);
        out.writeObject(NumOfClusters);
        String result = (String)in.readObject();
        if(result.equals("OK"))
            return (String)in.readObject();

        else throw new ServerException(result);

    }

    /**
     * Metodo per richiedere al server di salvare il nome della tabella su file.
     *
     * @param tableName Nome della tabella.
     *
     * @throws ServerException in caso di errore del server.
     * @throws IOException in caso di errore d'I/O.
     * @throws ClassNotFoundException in caso di errore di classe non trovata.
     */
    private void storeTableFromDb(String tableName) throws ServerException, IOException, ClassNotFoundException{
        out.writeObject(0);

        out.writeObject(tableName);
        String result = (String)in.readObject();
        if(!result.equals("OK"))
            throw new ServerException(result);

    }

    /**
     * Metodo per richiedere al server di eseguire il clustering.
     *
     * @param tableName Nome della tabella.
     * @param nOfClusters Numero di cluster.
     *
     * @return Stringa che rappresenta l'output del clustering.
     *
     * @throws SocketException in caso di errore di connessione.
     * @throws ServerException in caso di errore del server.
     * @throws IOException in caso di errore d'I/O.
     * @throws ClassNotFoundException in caso di errore di classe non trovata.
     */
    String learningFromDbTable(String tableName, String nOfClusters) throws ServerException, IOException, ClassNotFoundException{

        storeTableFromDb(tableName);

        out.writeObject(1);

        out.writeObject(Integer.valueOf(nOfClusters));

        String result = (String)in.readObject();

        if(result != null && result.equals("OK") ) {
            result = (String)in.readObject();
            return result;
        }
        else throw new ServerException(result);


    }

    /**
     * Metodo per richiedere al server di salvare il clustering su file.
     *
     * @param dbName Nome del database.
     * @param userID Nome utente del database.
     * @param psw Password del database.
     *
     * @throws SocketException in caso di errore di connessione.
     * @throws ServerException in caso di errore del server.
     * @throws IOException in caso di errore d'I/O.
     * @throws ClassNotFoundException in caso di errore di classe non trovata.
     */
    void storeClusterInFile(String dbName, String userID, String psw) throws ServerException, IOException, ClassNotFoundException{
        out.writeObject(2);
        out.writeObject(dbName);
        out.writeObject(userID);
        out.writeObject(psw);

        String result = (String) in.readObject();
        if(result.equals("OK")) {
            result = (String) in.readObject();
            //System.out.println(result);
        } else {
            throw new ServerException(result);
        }
    }

    /**
     * Metodo per richiedere al server di salvare lo schema del database.
     *
     * @return ArrayList di stringhe che rappresenta lo schema del database.
     *
     * @throws IOException in caso di errore d'I/O.
     */
    ArrayList<String> getTableSchema() throws IOException {
        out.writeObject(4);
        String result = null;
        try {
            result = (String) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }

        assert result != null;
        return new ArrayList<String>(Arrays.asList(result.split(" ")));
    }

    /**
     * Metodo per inviare i parametri del database al server.
     *
     * @param dbName Nome del database.
     * @param username Nome utente del database.
     * @param password Password del database.
     *
     * @throws IOException in caso di errore d'I/O.
     */
    void sendDbParameters(String dbName, String username, String password) throws IOException {
        if (out != null) {
                out.writeObject(5);

                out.writeObject(dbName);
                out.writeObject(username);
                out.writeObject(password);

        } else {
            Controller.displayError("Error in sending db parameters");
        }
    }
}