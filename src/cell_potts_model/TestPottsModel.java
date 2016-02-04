package cell_potts_model;

import java.util.ArrayList;

public class TestPottsModel {
	public static void main (String [] args){
		
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
