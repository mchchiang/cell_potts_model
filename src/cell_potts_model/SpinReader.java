package cell_potts_model;

import java.io.*;

/**
 * SpinReader.java
 * 
 * Read the file which specifies the initial condition of the lattice. 
 * The file must be in the following format:<br><br>
 * 
 * [length] [width]<br><br>
 * 
 * [actual spins in the lattice]
 *  
 * @author Michael Chiang
 *
 */
public class SpinReader {
	
	private BufferedReader reader;
	
	/**
	 * Open the file with the initial condition of the lattice
	 * @param filename name of the file
	 */
	public void openReader(String filename){
		try {
			reader = new BufferedReader(new FileReader(filename));
		} catch (IOException e){
			reader = null;
		}
	}
	
	/**
	 * Read the lattice configuration from file
	 * @return a 2D integer array which stores the lattice initial 
	 * condition as specified by the file
	 */
	public int [][] readSpins(){
		int [][] spin = new int [1][1];
		try {
			String line = reader.readLine();
			String [] args = line.split("\\s+");
			int nx = Integer.parseInt(args[0]);
			int ny = Integer.parseInt(args[1]);
			
			spin = new int [nx][ny];
			
			for (int i = 0; i < ny; i++){
				line = reader.readLine();
				args = line.split("\\s+");
				for (int j = 0; j < nx; j++){
					spin[j][i] = Integer.parseInt(args[j]);
				}
			}
			
		} catch (IOException e) {
			System.out.println("Error in reading file!");
		}
		return spin;
	}
	
	/**
	 * Close the file reader
	 */
	public void closeReader(){
		try {
			reader.close();
		} catch (IOException e) {}
		
		reader = null;
	}
}
