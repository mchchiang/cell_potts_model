package cell_potts_model;

import java.util.ArrayList;

public class TestPottsModel {
	public static void main (String [] args){
		/*int nx = 200;
		int ny = 200;
		int q = 100;
		int [][] spin = new int [nx][ny];
		double [] area = new double [q];
		
		int ind1, ind2, cellind;
		
		int delta = (int) Math.sqrt((double) nx * ny) / q;
		
		for (int i = 0; i < nx; i++){
			for (int j = 0; j < ny; j++){
				ind1 = i / delta;
				ind2 = j / delta;
				cellind = (int) (ind2 * Math.sqrt(q) + ind1);
				
				if (cellind >= q) cellind = q;
				
				spin[i][j] = cellind+1;
				//area[spin[i][j]] = area[spin[i][j]] +1;
				
			}
		}
		
		printList(spin);*/
		ArrayList<Integer> list = new ArrayList<Integer>();
		list.add(10);
		System.out.println(list.contains(10));
		
	}
	
	public static void printList(int [][] list){
		for (int i = 0; i < list.length; i++){
			for (int j = 0; j < list[i].length; j++){
				System.out.print(list[i][j] + " ");
			}
			System.out.println();
		}
	}
}
