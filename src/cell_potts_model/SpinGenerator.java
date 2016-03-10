package cell_potts_model;

import java.io.*;

public class SpinGenerator {
	public static void main (String [] args) throws IOException {
		PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("single_cell.dat")));
		writer.println("20 20");
		
		int nx = 50;
		int ny = 50;
		
		for (int i = 0; i < nx; i++){
			for (int j = 0; j < ny; j++){
				int dx = i - nx/2;
				int dy = j - ny/2;
				if ((dx * dx + dy * dy) < 16){
					writer.print("1 ");
				} else {
					writer.print("0 ");
				}
			}
			writer.println();
		}
		
		writer.close();
	}
}
