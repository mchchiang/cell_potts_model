package cell_potts_model;

import java.util.ArrayList;

public class TestPottsModel implements ThreadCompleteListener {
	
	
	static class MyRunnable implements Runnable{

		private ArrayList<ThreadCompleteListener> listeners = 
				new ArrayList<ThreadCompleteListener>();
		
		@Override
		public void run() {
			for (int i = 0; i < 10000; i++){
				System.out.println(i);
			}
			notifyThreadCompleteListener();			
		}
		
		public void addThreadCompleteListener(ThreadCompleteListener l){
			listeners.add(l);
		}
		
		public void removeThreadCompleteListener(ThreadCompleteListener l){
			listeners.remove(l);
		}
		
		public void notifyThreadCompleteListener(){
			for (ThreadCompleteListener l : listeners){
				l.notifyThreadComplete(this);
			}
		}
		
	}
	
	public void run(){
		for (int i = 0; i < 3; i++){
			MyRunnable r = new MyRunnable();
			r.addThreadCompleteListener(this);
			Thread t = new Thread(r);
			t.start();
		}		
	}
	
	public static void main (String [] args){
		TestPottsModel m = new TestPottsModel();
		m.run();
	}
	
	public static void printList(int [][] list){
		for (int i = 0; i < list.length; i++){
			for (int j = 0; j < list[i].length; j++){
				System.out.print(list[i][j] + " ");
			}
			System.out.println();
		}
	}

	@Override
	public void notifyThreadComplete(Runnable r) {
		System.out.println("I am still alive!");
	}
}
