package database;

import database.TableSchema.Column;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Classe che contiene i metodi per ottenere i dati da una tabella.
 *
 * @author Alessandro Ferrulli, Nazim Elmadhi.
 */
public class TableData {
    /**
     * Riferimento al database.
     */
    private DbAccess db;

    /**
     * Costruttore della classe.
     * @param db riferimento al database
     */
    public TableData(DbAccess db) {
        this.db = db;
    }

    /**
     * Restituisce tutte le transazioni distinte della tabella specificata.
     * @param table nome della tabella.
     *
     * @return tutte le transazioni distinte della tabella specificata.
     *
     * @throws SQLException se si verifica un errore nell'esecuzione della query.
     * @throws EmptySetException se la tabella è vuota.
     */
    public List<Example> getDistinctTransazioni(String table) throws SQLException, EmptySetException{
        TableSchema ts = new TableSchema(db, table);
        Statement s = db.getConnection().createStatement();
        ResultSet rs = s.executeQuery("SELECT DISTINCT * " + "FROM " + table + ";");
        List<Example> list = new ArrayList<>();
        while (rs.next()) {
            Example ex = new Example();
            for (int i = 0; i < ts.getNumberOfAttributes(); i++) {
                if (ts.getColumn(i).isNumber()) ex.add(rs.getDouble(ts.getColumn(i).getColumnName()));
                else ex.add(rs.getString(ts.getColumn(i).getColumnName()));
            }
            list.add(ex);
        }
        s.close();
        rs.close();
        if (list.isEmpty()) throw new EmptySetException("Table " + table + " is empty");
        return list;
    }

    /**
     * Restituisce tutti i valori distinti della colonna specificata.
     *
     * @param table nome della tabella.
     * @param column nome della colonna.
     *
     * @return tutti i valori distinti della colonna specificata.
     *
     * @throws SQLException se si verifica un errore nell'esecuzione della query.
     */
    public  Set<Object>getDistinctColumnValues(String table,Column column) throws SQLException{
        Statement s = db.getConnection().createStatement();
        ResultSet rs = s.executeQuery("SELECT DISTINCT " + column.getColumnName() + " " + "FROM " + table + " " + "ORDER BY " + column.getColumnName() + ";");
        HashSet<Object> set = new HashSet<>();
        while (rs.next()) {
            if (column.isNumber()) set.add(rs.getDouble(column.getColumnName()));
            else set.add(rs.getString(column.getColumnName()));
        }
        s.close();
        rs.close();
        return set;

    }

    /**
     * Recupera il valore aggregato di una colonna specifica da una tabella del database.
     *
     * @param table     Il nome della tabella del database.
     * @param column    Un oggetto che rappresenta la colonna da aggregare.
     * @param aggregate Il tipo di aggregazione da eseguire (ad esempio, SUM, AVG).
     *
     * @return Un oggetto che rappresenta il valore aggregato. Il tipo effettivo dipende dal tipo di colonna.
     *
     * @throws SQLException     Se si verifica un errore nell'accesso al database.
     * @throws NoValueException Se non c'è alcun valore per la colonna specificata nel result set.
     */
    public  Object getAggregateColumnValue(String table,Column column,QUERY_TYPE aggregate) throws SQLException,NoValueException{
        Object ret;
        Statement s = db.getConnection().createStatement();
        ResultSet rs = s.executeQuery("SELECT " + aggregate + "(" + column.getColumnName() + ") AS aggregata " + "FROM " + table + ";");
        try {
            if (rs.next()) {
                if (column.isNumber()) ret = rs.getDouble("aggregata");
                else ret = rs.getString("aggregata");
            } else throw new NoValueException("No value found for the column " + column.getColumnName());
        } finally {
            s.close();
            rs.close();
        }
        return ret;
    }

}
