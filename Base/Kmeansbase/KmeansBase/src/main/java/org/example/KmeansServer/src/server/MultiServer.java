package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Classe che gestisce la connessione con diversi client.
 *
 * @author Alessandro Ferrulli, Nazim Elmadhi.
 */

public class  MultiServer extends Thread {
    /**
     * Porta su cui il server è in ascolto.
     */
    private static int port;

    /**
     * Metodo main che avvia il server.
     *
     * @param args Parametri da riga di comando.
     */
    public static void main(String[] args) {
        MultiServer server = new MultiServer(8080);
        server.start();
    }

    /**
     * Costruttore della classe.
     *
     * @param port Porta su cui il server è in ascolto.
     */
    public  MultiServer(int port){
        MultiServer.port = port;
    }

    /**
     * Metodo che gestisce la comunicazione con il client.
     */
    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server started");
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Client accepted");
                try {
                    new ServerOneClient(socket);
                } catch (IOException e) {
                    socket.close();
                    System.out.println("socket closed");
                }
            }

        } catch (IOException e) {
            System.out.println("Server error: " + e.getMessage());
        }
    }

}
