package cell_potts_model;

import java.util.ArrayList;

public class TestPottsModel {
	
	static class MyRunnable implements Runnable{

		@Override
		public void run() {
			for (int i = 0; i < 10000; i++){
				System.out.println(i);
			}
		}
		
	}
	
	public static void main (String [] args){
		
		Runnable r = new MyRunnable();
		
		Thread t1 = new Thread(r);
		Thread t2 = new Thread(r);
		t1.start();
		t2.start();
		
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
