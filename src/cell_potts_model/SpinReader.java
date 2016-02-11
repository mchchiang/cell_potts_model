package cell_potts_model;

import java.io.*;

public class SpinReader {
	
	private BufferedReader reader;
	
	
	public void openReader(String filename){
		try {
			reader = new BufferedReader(new FileReader(filename));
		} catch (IOException e){
			reader = null;
		}
	}
	
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
			
		}
		return spin;
	}
	
	
	public void closeReader(){
		try {
			reader.close();
		} catch (IOException e) {}
		
		reader = null;
	}
}
