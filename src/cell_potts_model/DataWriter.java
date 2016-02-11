package cell_potts_model;

import java.io.*;

public abstract class DataWriter {
	
	protected PrintWriter writer = null;
	
	public void openWriter(String filename){
		try {
			writer = new PrintWriter(new FileWriter(filename));
		} catch (IOException e){
			writer = null;
			System.out.println("Cannot open file: " + filename);
		}
	}
	
	public void closeWriter(){
		writer.close();
		writer = null;
	}
	
	public abstract void writeData(CellPottsModel model, int time);
}
