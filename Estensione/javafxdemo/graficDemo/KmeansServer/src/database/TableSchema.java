package database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Classe che rappresenta lo schema di una tabella
 */
public class TableSchema {

    /**
     * Classe che rappresenta una colonna di una tabella.
     *
     * @author Alessandro Ferrulli, Nazim Elmadhi.
     */
    public class Column{
        /**
         * Nome della colonna.
         */
        private String name;
        /**
         * Tipo della colonna.
         */
        private String type;

        /**
         * Costruttore parametrizzato.
         *
         * @param name nome della colonna.
         * @param type tipo della colonna.
         */
        Column(String name,String type){
            this.name=name;
            this.type=type;
        }

        /**
         * Restituisce il nome della colonna.
         *
         * @return nome della colonna.
         */
        public String getColumnName(){
            return name;
        }

        /**
         * Restituisce il tipo della colonna.
         *
         * @return tipo della colonna.
         */
        public boolean isNumber(){
            return type.equals("number");
        }

        /**
         * Restituisce una stringa che rappresenta la colonna.
         *
         * @return stringa che rappresenta la colonna.
         */
        @Override
        public String toString(){
            return name+":"+type;
        }
    }
    /**
     * Riferimento al database.
     */
    private DbAccess db;
    /**
     * Lista delle colonne della tabella.
     */
    private List<Column> tableSchema=new ArrayList<Column>();

    /**
     * Schema corrente della tabella.
     */
    private static String currentTabelSchema;

    /**
     * Costruttore parametrizzato.
     *
     * @param db riferimento al database.
     * @param tableName nome della tabella.
     *
     * @throws SQLException se si verifica un errore nell'interazione col database.
     */
    public TableSchema(DbAccess db, String tableName) throws SQLException{
        this.db=db;
        HashMap<String,String> mapSQL_JAVATypes=new HashMap<String, String>();
        //http://java.sun.com/j2se/1.3/docs/guide/jdbc/getstart/mapping.html
        mapSQL_JAVATypes.put("CHAR","string");
        mapSQL_JAVATypes.put("VARCHAR","string");
        mapSQL_JAVATypes.put("TINYTEXT","string");
        mapSQL_JAVATypes.put("LONGVARCHAR","string");
        mapSQL_JAVATypes.put("DATE","string");
        mapSQL_JAVATypes.put("BIT","string");
        mapSQL_JAVATypes.put("SHORT","number");
        mapSQL_JAVATypes.put("INT","number");
        mapSQL_JAVATypes.put("LONG","number");
        mapSQL_JAVATypes.put("FLOAT","number");
        mapSQL_JAVATypes.put("DOUBLE","number");
        mapSQL_JAVATypes.put("DECIMAL","number");


        Connection con=db.getConnection();
        DatabaseMetaData meta = con.getMetaData();
        ResultSet res = meta.getColumns(con.getCatalog(), null, tableName, null);

        while (res.next()) {

            if(mapSQL_JAVATypes.containsKey(res.getString("TYPE_NAME")))
                tableSchema.add(new Column(
                        res.getString("COLUMN_NAME"),
                        mapSQL_JAVATypes.get(res.getString("TYPE_NAME")))
                );
        }

        currentTabelSchema = TableSchema.this.tbScToString();
        res.close();
    }

    /**
     * Restituisce il numero di attributi della tabella.
     *
     * @return numero di attributi della tabella.
     */
    public int getNumberOfAttributes(){
        return tableSchema.size();
    }

    /**
     * Restituisce la colonna d'indice index.
     *
     * @param index indice della colonna.
     *
     * @return colonna d'indice index.
     */
    public Column getColumn(int index){
        return tableSchema.get(index);
    }

    /**
     * Restituisce la colonna di nome name.
     *
     * @return colonna di nome name.
     */
    public String tbScToString(){
        String s="";
        for(Column c : tableSchema) {
            s += c.toString().replace(":string", "").replace(":number", "").replace("[", "") + " ";
        }
        return s;
    }

    /**
     * Restituisce una stringa che rappresenta lo schema della tabella.
     *
     * @return stringa che rappresenta lo schema della tabella.
     */
    public static String getCurrentTableSchema() {
    	return currentTabelSchema;
    }

}




