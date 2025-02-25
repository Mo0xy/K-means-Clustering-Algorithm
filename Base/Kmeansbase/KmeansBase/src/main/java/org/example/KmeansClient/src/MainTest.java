package src;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

/**
 * Classe che modella il client.
 *
 * @author Alessandro Ferrulli, Nazim Elmadhi.
 */

public class MainTest {
    /**
     * Stream di output.
     */
    private ObjectOutputStream out;
    /**
     * Stream di input.
     */
    private ObjectInputStream in; // stream con richieste del client.

    /**
     * Costruttore della classe.
     *
     * @param ip ip remoto a cui connettersi.
     * @param port porta del server.
     *
     * @throws IOException in caso di errore d'I/O.
     */
    public MainTest(String ip, int port) throws IOException{
        InetAddress addr = InetAddress.getByName(ip); //ip
        System.out.println("addr = " + addr);
        Socket socket = new Socket(addr, port); //Port
        System.out.println(socket);

        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());// stream con richieste del client
    }

    /**
     * Metodo che stampa il menu e restituisce la scelta dell'utente.
     *
     * @return Scelta dell'utente.
     */
    private int menu(){
        int answer;
        System.out.println("Choose an option:");
        do{
            System.out.println("(1) Load Data from File");
            System.out.println("(2) Load Data from Database");
            System.out.print("Answer:");
            answer=Keyboard.readInt();
        }
        while(answer<=0 || answer>2);
        return answer;
    }

    /**
     * Metodo per richiedere al server di eseguire il clustering.
     *
     * @return Stringa che rappresenta l'output del clustering.
     *
     * @throws SocketException in caso di errore di connessione.
     * @throws ServerException in caso di errore del server.
     * @throws IOException in caso di errore d'I/O.
     * @throws ClassNotFoundException in caso di errore di classe non trovata.
     */
    private String learningFromFile() throws SocketException, ServerException, IOException, ClassNotFoundException{
        System.out.print("Table Name:");
        String tabName=Keyboard.readString();
        System.out.print("Number of clusters:");
        int k=Keyboard.readInt();
        if (k == Integer.MIN_VALUE | k <= 0) throw new ServerException("Invalid number of clusters!");

        out.writeObject(3);
        out.writeObject(tabName);
        out.writeObject(k);
        String result = (String) in.readObject();
        if (result.equals("OK"))
            return (String) in.readObject();
        else throw new ServerException(result);
    }

    /**
     * Metodo per richiedere al server di salvare il nome della tabella su file.
     *
     * @throws SocketException in caso di errore di connessione.
     * @throws ServerException in caso di errore del server.
     * @throws IOException in caso di errore d'I/O.
     * @throws ClassNotFoundException in caso di errore di classe non trovata.
     */
    private void storeTableFromDb() throws SocketException,ServerException,IOException,ClassNotFoundException{
        out.writeObject(0);
        System.out.print("Table Name:");
        String tabName=Keyboard.readString();
        out.writeObject(tabName);
        String result = (String)in.readObject();
        if(!result.equals("OK"))
            throw new ServerException(result);
    }

    /**
     * Metodo per richiedere al server di eseguire il clustering.
     *
     * @throws SocketException        in caso di errore di connessione.
     * @throws ServerException        in caso di errore del server.
     * @throws IOException            in caso di errore d'I/O.
     * @throws ClassNotFoundException in caso di errore di classe non trovata.
     */
    private void learningFromDbTable() throws SocketException, ServerException, IOException, ClassNotFoundException{
        System.out.print("Number of clusters:");
        int k=Keyboard.readInt();
        if (k == Integer.MIN_VALUE | k <= 0) throw new ServerException("Invalid number of clusters!");

        out.writeObject(1);
        out.writeObject(k);
        String result = (String)in.readObject();
        if(result!= null && result.equals("OK")){
            System.out.println("Clustering output: " + in.readObject());
        }
        else throw new ServerException(result);
    }

    /**
     * Metodo main che avvia il client.
     *
     * @param args Parametri da riga di comando.
     */
    public static void main(String[] args) {
        String ip=args[0];
        int port = Integer.parseInt(args[1]);
        MainTest main=null;
        try {
            main=new MainTest(ip,port);
        }
        catch (IOException e){
            System.out.println(e.getMessage());
            return;
        }
        do{
            int menuAnswer=main.menu();
            switch(menuAnswer)
            {
                case 1:
                    while(true) {
                        try {
                            String kmeans = main.learningFromFile();
                            System.out.println(kmeans);
                            break;
                        } catch (IOException | ClassNotFoundException | ServerException e) {
                            System.out.println(e.getMessage());

                        }
                    }
                    break;
                case 2: // learning from db
                    char answer='y';
                    do{
                        try {
                            main.storeTableFromDb();
                        } catch (IOException | ClassNotFoundException | ServerException e) {
                            System.out.println(e.getMessage());
                        }
                        try {
                            main.learningFromDbTable();
                            System.out.println("Clustering saved on file");

                        } catch (ClassNotFoundException | IOException | ServerException e) {
                            System.out.println("Error: " + e.getMessage());
                        }

                        System.out.print("Do you want to compute again?(y/n)");
                        answer=Keyboard.readChar();

                    } while(answer=='y');
                    break;
                default:
                    System.out.println("Invalid Option!");
            }
            char answer;
            do{
                System.out.print("Do you want to choose another menu option?(y/n)");
                answer=Keyboard.readChar();
                if(answer=='y' || answer=='n')
                    break;
                System.out.println("Invalid Option!");
            } while(true);
            if(answer !='y')
                break;
        }
        while(true);
    }
}



