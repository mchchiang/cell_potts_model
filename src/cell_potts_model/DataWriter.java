package cell_potts_model;

import java.io.*;

/**
 * An abstract data writer which handles the process of creating the 
 * output stream. The actual process of writing the data is delegated 
 * to the sub-classes
 * @author Michael Chiang
 *
 */
public abstract class DataWriter {
	
	protected PrintWriter writer = null;
	
	/**
	 * Open a new file stream for writing data
	 * @param filename name of the file
	 */
	public void openWriter(String filename){
		try {
			writer = new PrintWriter(new FileWriter(filename));
		} catch (IOException e){
			writer = null;
			System.out.println("Cannot open file: " + filename);
		}
	}
	
	/**
	 * Close the file stream
	 */
	public void closeWriter(){
		writer.flush();
		writer.close();
		writer = null;
	}
	
	/**
	 * Write data to file
	 * @param model the simulation model
	 * @param time the current time step of the simulation
	 */
	public abstract void writeData(CellPottsModel model, int time);
}
