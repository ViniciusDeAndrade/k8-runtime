package br.ufpe.cin.gfads.experiment;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;


public class SettleTuple {

	private static final String path = "/home/vinicius/Documentos/logging/kubernetes/";
	private String fileName;
	private List<Tuple>tuples = new LinkedList<>();

	public SettleTuple(String fileName) {
		this.fileName = fileName;
	}

	public boolean addTuple(Tuple tuple) {
		return this.tuples.add(tuple);
	}

	/**
	 * this method is responsible for make a csv file with the data of
	 * the running process, move and schedule.
	 */
	
	public void flushCSV() {
		try {

			FileWriter fw = new FileWriter(path+fileName);
			BufferedWriter buff = new BufferedWriter(fw);
			buff.write("tech" + " , "+ "app" +" , " + "replicas" +" , " + "callIndex" +" , "+ "TimeToMove");
			for (Tuple tuple : tuples) {				
				buff.newLine();
				buff.write(tuple.toStringCSV());
				
			}
			buff.flush();
			buff.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	

}
