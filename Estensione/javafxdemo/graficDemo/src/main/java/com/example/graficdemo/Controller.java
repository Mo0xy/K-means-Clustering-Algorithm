package com.example.graficdemo;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.io.*;
import java.net.URL;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Questa classe rappresenta il controller dell'applicazione.
 *
 * @author Alessandro Ferrulli e Nazim Elmadhi.
 */
public class Controller implements Initializable {
    /**
     * TreeView che rappresenta la struttura delle cartelle.
     */
    @FXML
    private TreeView<String> fileTreeView = new TreeView<>();

    /**
     * Accordion che contiene i pannelli relativi ai cluster nella tab "file".
     */
    @FXML
    private Accordion fileAccordion;

    /**
     * Accordion che contiene i pannelli relativi ai cluster nella tab "database".
     */
    @FXML
    private Accordion dbAccordion;

    /**
     * TextField per inserire il nome della tabella.
     */
    @FXML
    private TextField tableName;

    /**
     * TextField per inserire il numero di cluster.
     */
    @FXML
    private TextField noOfClusters;

    /**
     * TextField per inserire il nome del database.
     */
    @FXML
    private TextField databaseName;

    /**
     * TextField per inserire l'username del database.
     */
    @FXML
    private TextField username;

    /**
     * TextField per inserire la password del database.
     */
    @FXML
    private TextField password;

    /**
     * BorderPane che contiene l'Aaccordion.
     */
    @FXML
    private BorderPane borderPane;

    /**
     * TreeItem che rappresenta la radice del TreeView.
     */
    @FXML
    private TreeItem<String> rootItem;

    /**
     * Stringa che rappresenta l'output del server.
     */
    private String serverOutput;

    /**
     * ArrayList che contiene i cluster.
     */
    private static ArrayList<Cluster> clusters;

    /**
     * Enum che rappresenta i campi del database.
     *
     *
     */
    protected enum fields{
        /**
         * Nome del Database
         */
        DBNAME,
        /**
         * Username
         */
        USER_ID,
        /**
         * password
         */
        PSW,
        /**
         * Nome della Tabella
         */
        TABLE,
        /**
         * Numero dei Cluster.
         */
        NOCLUSTERS;

    }

    /**
     * Booleano che indica se l'output del server è valido.
     * Se si verifica un'eccezione, il valore di questo
     * campo viene impostato a false.
     */
    private boolean validOutput = false;

    /**
     * Metodo che inizializza il controller.
     *
     * @param location URL che rappresenta la posizione del file FXML.
     * @param resources ResourceBundle che rappresenta le risorse localizzate.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        makeTreeView();
    }

    /**
     * Metodo per creare il TreeView.
     */
    @FXML
    protected void makeTreeView() {
        File chosenDirectory = new File("graficDemo\\KmeansServer\\saves");
        fileTreeView.setRoot(createTreeItem(chosenDirectory));

        setEventHandler(chosenDirectory);
        try {
            onTreeItemClickedAction();
        } catch (RuntimeException e) {
            //System.out.println(e.getMessage());
            displayError("No clusters found");
        }
    }

    /**
     * Metodo per gestire l'evento di click su un elemento del TreeView.
     */
    private void onTreeItemClickedAction() throws RuntimeException {

        fileTreeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if(!newValue.getValue().equals("saves")) {
                    String selectedValue = newValue.getValue();
                    String[] splitValue = selectedValue.split("\\.");
                    splitValue = splitValue[0].split("-");
                        String result = "";
                        try {
                            result = loadFromFile(splitValue[0], splitValue[1], splitValue[2]);
                        } catch (ServerException e) {
                            if (e.getMessage().contains("The last packet successfully received from the server was") | e.getMessage().contains("Communications link failure"))
                                displayError("Error while connecting to Database. Try to start MySQL service.");
                            else displayError(e.getMessage());
                        }
                    try {
                        updateClustersView(result, fileAccordion);
                    } catch (IOException e) {
                        displayError("Error while creating cluster panes");
                    }
                }
            }
        });
    }

    /**
     * Metodo per gestire l'evento di click sul bottone "Salva".
     *
     * @throws IOException in caso di errore d'I/O.
     * @throws ServerException in caso di errore del server.
     * @throws ClassNotFoundException in caso di errore di classe non trovata.
     */
    @FXML
    protected void onSaveButtonAction() throws IOException, ServerException, ClassNotFoundException {
        if (serverOutput != null) {
            if (getDatabaseConnectionInput().stream().allMatch(Objects::nonNull) && validOutput) {
                Client.getClientInstance().storeClusterInFile(
                        getDatabaseConnectionInput().get(fields.DBNAME.ordinal()),
                        getDatabaseConnectionInput().get(fields.USER_ID.ordinal()),
                        getDatabaseConnectionInput().get(fields.PSW.ordinal()));

                displayMessage("File saved");
            } else {
                displayError("Please check all the fields and compute the clustering first");
            }
        }
    }

    /**
     * Metodo per gestire l'evento di click sul bottone "Compute".
     *
     */
    @FXML
    protected void onComputeButtonAction()  {
            if (getDatabaseConnectionInput().stream().allMatch(Objects::nonNull)) {
                if (getDatabaseConnectionInput().get(fields.DBNAME.ordinal()).contains("-") || getDatabaseConnectionInput().get(fields.TABLE.ordinal()).contains("-")) {
                    displayError("Database or table name cannot contain '-' character");
                    return;
                }
                try {
                    Client.getClientInstance().sendDbParameters(
                            getDatabaseConnectionInput().get(fields.DBNAME.ordinal()),
                            getDatabaseConnectionInput().get(fields.USER_ID.ordinal()),
                            getDatabaseConnectionInput().get(fields.PSW.ordinal()));
                } catch (IOException e) {
                    displayError("Error while sending database parameters");
                }

                    try {
                        if (getDatabaseConnectionInput().get(fields.NOCLUSTERS.ordinal()).matches("\\d*"))
                            if (Integer.parseInt(getDatabaseConnectionInput().get(fields.NOCLUSTERS.ordinal())) > 0) {
                                serverOutput = Client.getClientInstance().learningFromDbTable(
                                        getDatabaseConnectionInput().get(fields.TABLE.ordinal()),
                                        getDatabaseConnectionInput().get(fields.NOCLUSTERS.ordinal()));

                                updateClustersView(serverOutput, dbAccordion);
                                validOutput = true;
                            }
                            else displayError("Number of clusters must be greater than 0");
                        else displayError("Please enter a number");

                    } catch (ServerException e) {
                        if (e.getMessage().contains("The last packet successfully received from the server was") | e.getMessage().contains("Communications link failure"))
                            displayError("Error while connecting to Database. Try to start MySQL service.");
                        if (e.getMessage().contains("Table") && e.getMessage().contains("doesn't exist"))
                            displayError("Table doesn't exist");
                        if (e.getMessage().contains("Access denied for user") && e.getMessage().contains("to database"))
                            displayError("Database doesn't exist");
                        if (e.getMessage().contains("using password: YES") && e.getMessage().contains("Access denied for user"))
                            displayError("Access denied. Check username and password");
                        if (e.getMessage().contains("Number of clusters is greater than number of examples"))
                            displayError("Number of clusters is greater than number of examples");
                        validOutput = false;
                    } catch (IOException e) {
                        displayError("Error while creating cluster panes");
                    } catch (ClassNotFoundException e) {
                        displayError("Class not found");
                    } catch (NumberFormatException e) {
                        displayError("Number of clusters must be a number");
                    }
            } else {
                displayError("Please fill all the fields");
                validOutput = false;
            }
    }

    /**
     * Metodo per ottenere i parametri di connessione al database.
     *
     * @return ArrayList che contiene i parametri di connessione al database.
     */
    private ArrayList<String> getDatabaseConnectionInput() {
        ArrayList<String> fields = new ArrayList<>();

        fields.add(databaseName.getText());
        if (databaseName.getText().isEmpty()) fields.add(null);


        fields.add(username.getText());
        if (username.getText().isEmpty()) fields.add(null);


        fields.add(password.getText());
        if (password.getText().isEmpty()) fields.add(null);


        fields.add(tableName.getText());
        if (tableName.getText().isEmpty()) fields.add(null);


        fields.add(noOfClusters.getText());
        if (noOfClusters.getText().isEmpty()) fields.add(null);

        return fields;
    }

    /**
     * Metodo per richiedere al server di eseguire il clustering da file.
     *
     * @param dbName Nome del database.
     * @param tabName Nome della tabella.
     * @param numOfIter Numero di iterazioni.
     * @return Stringa che rappresenta l'output del server.
     * @throws ServerException in caso di errore del server.
     */
    protected String loadFromFile(String dbName, String tabName, String numOfIter) throws ServerException {
        Client client;
        String result = "";

        try {
            client = Client.getClientInstance();
            result = client.learningFromFile(dbName, tabName, numOfIter);
        } catch (IOException e) {
            System.out.println("failed to initialize client");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (ServerException e) {
            throw new ServerException("Error while connecting to Database. Try to start MySQL service.");
        }

        return result;
    }

    /**
     * Metodo per aggiornare la vista dei cluster.
     *
     * @param fileOutput Stringa che rappresenta l'output del server.
     * @param accordion Accordion che contiene i pannelli relativi ai cluster.
     * @throws IOException in caso di errore d'I/O.
     * @throws NumberFormatException in caso di errore di formato del numero.
     * @throws NullPointerException in caso di oggetto nullo.
     */
    private void updateClustersView(String fileOutput, Accordion accordion) throws IOException, NumberFormatException, NullPointerException {

        clusters = new ArrayList<>();
        ArrayList<ArrayList<String>> examplesList = new ArrayList<>();
        try {

            String[] splittedString = fileOutput.split("\n");


            //se il fileoutput proviene dal server
            if (splittedString[1].contains("Number of iterations")) {
                StringBuilder tempOutput = new StringBuilder();
                for (int i = Integer.parseInt(getDatabaseConnectionInput().get(4)) + 2; i < splittedString.length; i++) {
                    tempOutput.append(splittedString[i]);
                    if (i < splittedString.length - 1) {
                        tempOutput.append("\n");
                    }
                }
                splittedString = tempOutput.toString().split("\n");

                for (int i = 0; i < Integer.parseInt(getDatabaseConnectionInput().get(4)); i++) {
                    clusters.add(new Cluster());
                }
                createSaveButton();

            } else { //se invece si legge da file
                for (String s : splittedString) {
                    if (s.contains("Centroid")) clusters.add(new Cluster());
                }
            }

            int k = 0;
            for (int i = 0; i < splittedString.length; i++) {
                if (splittedString[i].contains("Centroid")) {
                    clusters.get(k).setCentroid(getCentroidData(splittedString[i]));

                }
                while (splittedString[i].contains("dist=")) {
                    clusters.get(k).getExamples().add(getExampleData(splittedString[i]));
                    i++;
                }
                if (splittedString[i].contains("AvgDistance=")) {
                    clusters.get(k).setAvgDistance(splittedString[i].replace("AvgDistance=", ""));
                    k++;
                }
            }

            if (!accordion.getPanes().isEmpty()) {
                accordion.getPanes().clear();
            }

        } catch (NullPointerException e) {
            System.out.println(e.getMessage());
            throw new NullPointerException("No clusters found");
        } catch (NumberFormatException e) {
            System.out.println(e.getMessage());
            throw new NumberFormatException("Number of clusters must be a number");
        }

        try {
            int clusterIndex = 0;
            for (Cluster cluster : clusters) {
                TitledPane pane = createClusterTitledPane(cluster, clusterIndex++);
                accordion.getPanes().add(pane);
            }
        } catch (IOException e) {
            //System.out.println(e.getMessage());
            throw new IOException("Error while creating cluster panes");
        }

    }

    /**
     * Metodo per creare il bottone "Save".
     */
    private void createSaveButton() {
        Button saveButton = new Button();
        saveButton.setText("Save");
        saveButton.setMinSize(70, 30);
        saveButton.setMaxSize(70, 30);
        borderPane.setCenter(saveButton);

        saveButton.setOnAction(event -> {
            try {
                onSaveButtonAction();
            } catch (IOException | ServerException | ClassNotFoundException e) {
                System.out.println(e.getMessage());
            }
        });
    }

    /**
     * Metodo per creare un TitledPane per un cluster.
     *
     * @param cluster Cluster da visualizzare.
     * @param clusterIndex Indice del cluster.
     * @return TitledPane che rappresenta il cluster.
     * @throws IOException in caso di errore d'I/O.
     */
    private TitledPane createClusterTitledPane(Cluster cluster, int clusterIndex) throws IOException {
        return new TitledPane("Cluster " + clusterIndex, createClusterContent(cluster, clusterIndex));
    }

    /**
     * Metodo per creare il contenuto di un cluster.
     *
     * @param cluster Cluster da visualizzare.
     * @param clusterIndex Indice del cluster.
     * @return VBox che rappresenta il contenuto del cluster.
     * @throws IOException in caso di errore d'I/O.
     */
    private VBox createClusterContent(Cluster cluster, int clusterIndex) throws IOException {
        VBox vBox = new VBox();
        ArrayList<ArrayList<String>> data = new ArrayList<>();
        data.add(cluster.getCentroid());

        TableView<ArrayList<String>> centroidTable = createTable(data, Client.getClientInstance().getTableSchema(), true, clusterIndex);
        TableView<ArrayList<String>> examplesTable = createTable(cluster.getExamples(), Client.getClientInstance().getTableSchema(), false, clusterIndex);

        Label centroidTableLabel = createTableLabel("Centroid:");
        Label examplesTableLabel = createTableLabel("Examples:");

        setTablesPosition(centroidTable, examplesTable, centroidTableLabel, examplesTableLabel);
        vBox.getChildren().addAll(
                centroidTableLabel,
                centroidTable,
                examplesTableLabel,
                examplesTable
        );

        return vBox;
    }

    /**
     * Metodo per creare un label per una tabella.
     *
     * @param text Testo del label.
     * @return Label che rappresenta la tabella.
     */
    private Label createTableLabel(String text) {
        Label label = new Label(text);
        label.setMinSize(40, 20);
        return label;
    }

    /**
     * Metodo per creare una tabella.
     *
     * @param data Dati della tabella.
     * @param tableSchema Schema della tabella.
     * @param setAvgDist Booleano che indica se visualizzare la distanza media.
     * @param clusterIndex Indice del cluster.
     * @return TableView che rappresenta la tabella.
     */
    private TableView<ArrayList<String>> createTable(ArrayList<ArrayList<String>> data, ArrayList<String> tableSchema, boolean setAvgDist, int clusterIndex) {
        TableView<ArrayList<String>> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        updateTable(table, tableSchema);

        if (setAvgDist) {
            TableColumn<ArrayList<String>, String> avgDistColumn = new TableColumn<>("AvgDistance");
            avgDistColumn.setCellValueFactory(cellData -> new SimpleStringProperty(clusters.get(clusterIndex).getAvgDistance()));
            table.getColumns().add(avgDistColumn);
            table.getItems().addAll(data);

        } else {
            TableColumn<ArrayList<String>, String> distColumn = new TableColumn<>("dist");
            populateColumn(distColumn, table.getColumns().size());
            table.getColumns().add(distColumn);
            table.getItems().addAll(data);
        }

        table.refresh();

        return table;
    }

    /**
     * Metodo per popolare una colonna di una tabella.
     *
     * @param column Colonna da popolare.
     * @param columnIndex Indice della colonna.
     */
    private void populateColumn(TableColumn<ArrayList<String>, String> column, int columnIndex) {
        column.setCellValueFactory(cellData -> {
            ArrayList<String> rowData = cellData.getValue();
            if (columnIndex < rowData.size()) {
                return new SimpleStringProperty(rowData.get(columnIndex));
            } else {
                return new SimpleStringProperty("");
            }
        });
    }

    /**
     * Metodo per aggiornare una tabella.
     *
     * @param table Tabella da aggiornare.
     * @param tableSchema Schema della tabella.
     */
    private void updateTable(TableView<ArrayList<String>> table, ArrayList<String> tableSchema) {

        for (String attribute : tableSchema) {
            TableColumn<ArrayList<String>, String> column = new TableColumn<>(attribute);
            table.getColumns().add(column);
            column.setCellValueFactory(cellData -> {
                ArrayList<String> rowData = cellData.getValue();
                int columnIndex = tableSchema.indexOf(attribute);
                if (columnIndex >= 0 && columnIndex < rowData.size()) {
                    return new SimpleStringProperty(rowData.get(columnIndex));
                } else {
                    return new SimpleStringProperty("");
                }
            });
        }
    }

    /**
     * Metodo per ottenere i dati del centroide.
     * @param centroidLine Stringa che rappresenta il centroide.
     * @return ArrayList che rappresenta i dati del centroide.
     */
    private ArrayList<String> getCentroidData(String centroidLine) {

        return new ArrayList<String>(Arrays.asList(centroidLine.split(":=")[1].replace(
                "Centroid=(", "").replace(
                ")", "").split(" ")));
    }

    /**
     * Metodo per ottenere gli esempi relativi al centroide.
     *
     * @param examplesLine Stringa che rappresenta gli esempi.
     * @return ArrayList che rappresenta i dati di esempio.
     */
    private ArrayList<String> getExampleData(String examplesLine) {
        String[] temp = examplesLine.replace("[", "").replace(
                "] ", "").replace("dist=", ", ").split(", ");

        return new ArrayList<String>(Arrays.asList(temp));
    }

    /**
     * Metodo per impostare la posizione delle tabelle.
     *
     * @param centroidTable Tabella del centroide.
     * @param examplesTable Tabella degli esempi.
     * @param labels Label.
     */
    private void setTablesPosition(TableView<ArrayList<String>> centroidTable, TableView<ArrayList<String>> examplesTable, Label... labels) {

        centroidTable.setMinHeight(80);
        centroidTable.setMaxHeight(100);
        VBox.setVgrow(examplesTable, Priority.ALWAYS);
        VBox.setMargin(centroidTable, new Insets(5, 0, 0, 0));
        VBox.setMargin(examplesTable, new Insets(20, 0, 0, 0));
        for (Label l : labels)
            VBox.setMargin(l, new Insets(15, 0, 0, 0));

    }

    /**
     * Metodo per impostare l'evento di watch su una cartella.
     *
     * @param rootFolder Cartella da monitorare.
     */
    private void setEventHandler(File rootFolder) {
        try {
            WatchService watchService = FileSystems.getDefault().newWatchService();
            Path folderPath = rootFolder.toPath();
            folderPath.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE);

            Thread watcherThread = new Thread(() -> {
                try {
                    WatchKey key;
                    while ((key = watchService.take()) != null) {
                        for (WatchEvent<?> event : key.pollEvents()) {
                            WatchEvent.Kind<?> kind = event.kind();

                            if (kind == StandardWatchEventKinds.OVERFLOW) {
                                continue;
                            }

                            // Aggiorna il treeView quando vengono apportate modifiche
                            Platform.runLater(() -> updateTreeView(rootFolder));
                        }

                        key.reset();
                    }
                } catch (InterruptedException e) {
                    System.out.println(e.getMessage());
                }
            });

            watcherThread.setDaemon(true);
            watcherThread.start();

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Metodo per aggiornare il TreeView.
     *
     * @param rootFolder Cartella radice.
     */
    private void updateTreeView(File rootFolder) {
        TreeItem<String> newRootItem = createTreeItem(rootFolder);
        fileTreeView.setRoot(newRootItem);
    }

    /**
     * Metodo per creare un TreeItem.
     *
     * @param file File da visualizzare.
     * @return TreeItem che rappresenta il file.
     */
    private TreeItem<String> createTreeItem(File file) {
        TreeItem<String> treeItem = new TreeItem<>(file.getName());
        if (file.isDirectory()) {
            for (File subFile : Objects.requireNonNull(file.listFiles())) {
                treeItem.getChildren().add(createTreeItem(subFile));
            }
        }
        return treeItem;
    }

    /**
     * Metodo per visualizzare un messaggio di errore.
     *
     * @param message Messaggio di errore.
     */
    protected static void displayError(String message) {
        Stage stage = new Stage();
        stage.setResizable(false);
        stage.setTitle("Error");
        stage.setMinWidth(250);
        stage.setMinHeight(120);

        VBox vBox = new VBox();
        if (message == null) message = "Error";

        Label label = new Label(message);
        Button button = new Button("OK");

        VBox.setMargin(label, new javafx.geometry.Insets(0, 3, 15, 3));
        vBox.getChildren().add(label);
        vBox.setAlignment(Pos.TOP_CENTER);
        VBox.setMargin(button, new javafx.geometry.Insets(2, 0, 2, 0));
        vBox.getChildren().add(button);
        vBox.setAlignment(Pos.BOTTOM_CENTER);

        stage.setScene(new javafx.scene.Scene(vBox));
        stage.show();

        button.setOnAction(event -> stage.close());
    }

    /**
     * Metodo per visualizzare un messaggio.
     *
     * @param message Messaggio da visualizzare.
     */
    private void displayMessage(String message) {
        Stage stage = new Stage();
        stage.setResizable(false);
        stage.setMinWidth(250);
        stage.setMinHeight(120);

        VBox vBox = new VBox();
        if (message == null) message = "";

        Label label = new Label(message);
        Button button = new Button("OK");

        VBox.setMargin(label, new javafx.geometry.Insets(0, 3, 15, 3));
        vBox.getChildren().add(label);
        vBox.setAlignment(Pos.TOP_CENTER);
        VBox.setMargin(button, new javafx.geometry.Insets(2, 0, 2, 0));
        vBox.getChildren().add(button);
        vBox.setAlignment(Pos.BOTTOM_CENTER);

        stage.setScene(new javafx.scene.Scene(vBox));
        stage.show();

        button.setOnAction(event -> stage.close());
    }
}

/**
 * Classe che rappresenta un cluster.
 */
class Cluster {
    /**
     * ArrayList che rappresenta il centroide.
     */
    private ArrayList<String> centroid;
    /**
     * ArrayList che rappresenta gli esempi.
     */
    private ArrayList<ArrayList<String>> examples;

    /**
     * Stringa che rappresenta la distanza media.
     */
    private String avgDistance;

    /**
     * Costruttore di classe.
     */
    public Cluster(){
        centroid = new ArrayList<>();
        examples = new ArrayList<>();
    };

    /**
     * Metodo per impostare il centroide.
     *
     * @param centroid Centroide da impostare.
     */
    void setCentroid(ArrayList<String> centroid) {
        this.centroid = centroid;
    }

    /**
     * Metodo per impostare la distanza media.
     *
     * @param avgDistance Distanza media da impostare.
     */
    void setAvgDistance(String avgDistance) {
        this.avgDistance = avgDistance;
    }

    /**
     * Metodo per ottenere gli esempi.
     *
     * @return ArrayList che rappresenta gli esempi.
     */
    ArrayList<ArrayList<String>> getExamples() {
        return examples;
    }

    /**
     * Metodo per ottenere il centroide.
     *
     * @return ArrayList che rappresenta il centroide.
     */
    ArrayList<String> getCentroid() {
        return centroid;
    }

    /**
     * Metodo per ottenere la distanza media.
     *
     * @return Stringa che rappresenta la distanza media.
     */
    String getAvgDistance() {
        return avgDistance;
    }
}
