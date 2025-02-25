package data;

import database.*;
import server.ServerException;

import java.sql.SQLException;
import java.util.*;

/**
 * Classe che modella l'insieme di transazioni.
 *
 * @author Alessandro Ferrulli, Nazim Elmadhi.
 */
public class Data {
	/**
	 * Insieme di transazioni.
	 */
	private List<Example> data;
	/**
	 * Numero di transazioni.
	 */
	private final int numberOfExamples;
	/**
	 * Insieme di attributi.
	 */
	private final List<Attribute> attributeSet;

	/**
	 * Costruttore parametrizzato.
	 *
	 * @param table nome della tabella
	 * @throws DatabaseConnectionException se si verifica un errore di connessione al database.
	 * @throws SQLException se si verifica un errore di accesso al database.
	 * @throws EmptySetException se si verifica un errore d'insieme vuoto.
	 * @throws NoValueException se si verifica un errore di valore non presente.
	 * @throws ServerException se si verifica un errore del server.
	 */
	public Data(String table) throws DatabaseConnectionException, SQLException, EmptySetException, NoValueException, ServerException {
	    DbAccess database = new DbAccess();
		try {
			database.initConnection();
		} catch (DatabaseConnectionException e) {
			throw new DatabaseConnectionException(e.getMessage());
		}

		try {
			TableData tableData = new TableData(database);
			TableSchema tableSchema = new TableSchema(database, table);
			data = tableData.getDistinctTransazioni(table);

			numberOfExamples = data.size();
			attributeSet = new ArrayList<>();

			for(int i = 0; i < tableSchema.getNumberOfAttributes(); i++) {
				if(tableSchema.getColumn(i).isNumber()) {
					attributeSet.add(new ContinuousAttribute(tableSchema.getColumn(i).getColumnName(), i,
							(double) tableData.getAggregateColumnValue(table, tableSchema.getColumn(i), QUERY_TYPE.MIN),
							(double) tableData.getAggregateColumnValue(table, tableSchema.getColumn(i), QUERY_TYPE.MAX)));
				} else {
					HashSet<Object> distinctValues = (HashSet<Object>) tableData.getDistinctColumnValues(table, tableSchema.getColumn(i));
					TreeSet<String> values = new TreeSet<>();
					for(Object o : distinctValues) {
						values.add((String) o);
					}
					attributeSet.add(new DiscreteAttribute(tableSchema.getColumn(i).getColumnName(), i, values));
				}
			}
		} catch (SQLException | EmptySetException e) {
			throw new ServerException(e.getMessage());
		}
	}


	/**
	 * Restituisce il numero di esempi in data set.
	 *
	 * @return numberOfExamples numero di esempi in data set
	 */
	public int getNumberOfExamples(){
		return this.numberOfExamples;
	}

	/**
	 * Restituisce il numero di attributi in attribute set.
	 *
	 * @return numero di attributi in attribute set
	 */
	public int getNumberOfAttributes(){
		return attributeSet.size();
	}

	/**
	 * Restituisce l'attributo in attribute set d'indice index.
	 *
	 * @param index indice dell'attributo da restituire
	 * @return attributo in attribute set d'indice index
	 */
	Attribute getAttribute(int index){
		return attributeSet.get(index);
	}

	/**
	 * Restituisce l'attributo in posizione attributeIndex, nella riga in posizione exampleIndex.
	 *
	 * @param exampleIndex indice dell'attributo
	 * @param attributeIndex nome dell'attributo
	 *
	 * @return valore assunto in data dall'attributo in posizione attributeIndex, nella riga in
	 * posizione exampleIndex
	 */
	public Object getAttributeValue(int exampleIndex, int attributeIndex){
		return this.data.get(exampleIndex).get(attributeIndex);
	}

	/**
	 * Restituisce l'attributo in posizione attributeIndex, nella riga in posizione exampleIndex.
	 *
	 * @param k numero di clusters.
	 *
	 * @return array di indici di centroidi.
	 *
	 * @throws OutofRangeSampleSize se k è minore di 0 o maggiore del numero di esempi.
	 */
	public int[] sampling(int k) throws OutofRangeSampleSize {
		if(k <= 0 || k > getNumberOfExamples())
			throw new OutofRangeSampleSize("invalid k value");

		int[] centroidIndexes = new int[k];
		//choose k random different centroids in data.
		Random rand = new Random();
		rand.setSeed(System.currentTimeMillis());
		for (int i = 0; i < k; i++){
			boolean found = false;
			int c;
			do
			{
				found = false;
				c = rand.nextInt(getNumberOfExamples());
				// verify that centroid[c] is not equal to a centroide already stored in CentroidIndexes
				for(int j = 0; j < i; j++)
					if(compare(centroidIndexes[j], c)){
						found = true;
						break;
					}
			}
			while(found);
			centroidIndexes[i] = c;
		}
		return centroidIndexes;

	}

	/**
	 * Confronta le righe i e j della tabella.
	 *
	 * @param i riga.
	 * @param j colonna.
	 *
	 * @return true se le righe i e j sono uguali, false altrimenti.
	 */
	private boolean compare(int i, int j){
		for(int k = 0; k < getNumberOfAttributes(); k++)
			if(!getAttributeValue(i,k).equals(getAttributeValue(j,k)))
				return false;
		return true;
	}


	/**
	 * Calcola il prototipo di un attributo discreto.
	 *
	 * @param clusteredSet insieme di righe.
	 * @param attribute attributo discreto.
	 *
	 * @return prototipo.
	 */
	String computePrototype(Set<Integer> clusteredSet, DiscreteAttribute attribute){
		int maxValue = 0;
		String prototype = attribute.iterator().next();
		for(String value : attribute){
			int frequency = attribute.frequency(this, clusteredSet, value);
			if(frequency > maxValue){
				maxValue = frequency;
				prototype = value;
			}

		}
		return prototype;
	}

	/**
	 * Calcola il prototipo di un attributo continuo.
	 *
	 * @param clusteredSet insieme di righe.
	 * @param attribute attributo continuo.
	 *
	 * @return prototipo.
	 */
	double computePrototype(Set<Integer> clusteredSet, ContinuousAttribute attribute){
		double sum = 0;
		for(int index : clusteredSet){
			sum += (double) data.get(index).get(attribute.getIndex());
		}
		return sum / (double) clusteredSet.size();
	}

	/**
	 * Calcola il prototipo di un attributo.
	 *
	 * @param clusteredSet insieme di righe.
	 * @param attribute attributo.
	 *
	 * @return prototipo.
	 */
	Object computePrototype(Set<Integer> clusteredSet, Attribute attribute){
		if(attribute instanceof DiscreteAttribute)
			return computePrototype(clusteredSet, (DiscreteAttribute) attribute);
		else
			return computePrototype(clusteredSet, (ContinuousAttribute) attribute);
	}


	/**
	 * Restituisce l'insieme di transazioni.
	 *
	 * @param index indice della transazione.
	 *
	 * @return transazione in posizione index.
	 */
	public Tuple getItemSet(int index){
		Tuple tuple = new Tuple(attributeSet.size());
		for(int i = 0; i < attributeSet.size(); i++){
			if(attributeSet.get(i) instanceof DiscreteAttribute){
				tuple.add(new DiscreteItem((DiscreteAttribute) attributeSet.get(i), getAttributeValue(index, i)), i);
			}
			else if(attributeSet.get(i) instanceof ContinuousAttribute){
				tuple.add(new ContinuousItem((ContinuousAttribute) attributeSet.get(i), (Double) getAttributeValue(index, i)), i);
			}
		}
		return tuple;
	}

	/**
	 * Crea una stringa in cui memorizza lo schema della tabella e le transazioni memorizzate in data
	 * opportunamente enumerate.
	 *
	 * @return stringa che modella lo stato dell'oggetto
	 */
	@Override
	public String toString(){
		String dataStr = "";
		int i = 0;
		for(Example e: data){
			dataStr += i+1 + ": " + e.toString();
			dataStr = dataStr.substring(0, dataStr.length() - 1);
			dataStr += "\n";
			i++;
		}
		return dataStr;
	}

}
