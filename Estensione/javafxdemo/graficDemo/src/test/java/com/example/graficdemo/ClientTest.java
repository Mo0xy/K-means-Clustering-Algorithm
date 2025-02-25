package com.example.graficdemo;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;

class ClientTest {

    String dbName = "MapDB";
    String user = "MapUser";
    String psw = "map";
    String table = "playtennis";
    int numIter = 4;

    @BeforeEach
    void setUp() throws IOException {

        Client.ip = "localhost";
        Client.port = 8080;
        Client.getClientInstance().sendDbParameters(dbName, user, psw);

    }

    /**
     * Test per il metodo getClientInstance.
     * @throws IOException in caso di errore d'I/O.
     */
    @Test
    void getClientInstanceTest() throws IOException {
        assertNotNull(Client.getClientInstance());
    }

    /**
     * Test per il metodo learningFromFile.
     * @throws IOException in caso di errore d'I/O.
     * @throws ServerException in caso di errore del server.
     * @throws ClassNotFoundException in caso di errore di classe non trovata.
     */
    @Test
    void learningFromFileTest() throws IOException, ServerException, ClassNotFoundException {
        assertNotNull(Client.getClientInstance().learningFromFile(dbName, table, String.valueOf(numIter)));
    }

    /**
     * Test per il metodo learningFromDbTable.
     *
     * @throws IOException in caso di errore d'I/O.
     * @throws ServerException in caso di errore del server.
     * @throws ClassNotFoundException in caso di errore di classe non trovata.
     */
    @Test
    void learningFromDbTableTest() throws IOException, ServerException, ClassNotFoundException {
        assertNotNull(Client.getClientInstance().learningFromDbTable(table, String.valueOf(numIter)));
    }

    /**
     * Test per il metodo storeTableFromDb.
     */
    @Test
    void storeClusterInFileTest() {
        assertDoesNotThrow(() -> Client.getClientInstance().storeClusterInFile(dbName, user, psw));
    }

    /**
     * Test per il metodo getTableSchema.
     */
    @Test
    void getTableSchemaTest() {
        assertDoesNotThrow(() -> Client.getClientInstance().getTableSchema());
    }

    /**
     * Test per il metodo sendDbParameters.
     */
    @Test
    void sendDbParametersTest() {
        assertDoesNotThrow(() -> Client.getClientInstance().sendDbParameters(dbName, user, psw));
    }
}