package cell_potts_model;

import java.io.*;
import java.util.ArrayList;

public class CalculateMSDMultiThread implements ThreadCompleteListener{

	private int nx, ny, time;
	private volatile double [][] data, msd;
	private int numOfThreads;
	private int completedThread;
	private String outputFile;
	
	public CalculateMSDMultiThread(int nx, int ny, int cells, 
			int time, int numOfThreads, String dataFile, 
			String outputFile) throws IOException {

		this.nx = nx;
		this.ny = ny;
		this.time = time;
		this.numOfThreads = numOfThreads;
		this.outputFile = outputFile;

		int x = cells * 2 + 3;
		int y = time;

		//retrieve cm data into array
		BufferedReader reader = new BufferedReader(new FileReader(dataFile));

		int count = 0;
		String line;

		data = new double [y][x];
		msd = new double [time][cells];		

		String [] array;
		while (reader.ready() && count < y){
			line = reader.readLine();
			array = line.trim().split("\\s++");
			if (array.length > 0 && !(array.length == 1 && array[0].equals(""))){
				/*if (array.length != x){
					System.out.println("error in line " + count + " array length: " 
							+ array.length + " x: " + x);
				}*/
				for (int i = 0; i < x; i++){
					data[count][i] = Double.parseDouble(array[i]);
				}
				count++;
			}
		}
		reader.close();
		
		//assign cell calculation to individual threads
		completedThread = 0;
		
		if (numOfThreads > cells){//maximum one thread per cell!
			numOfThreads = cells;
		}
		int cellsPerThread = (int) Math.round((double) cells / (double) numOfThreads);
		int cellCount = 1;
		int startCell = 0, endCell = 0;
		
		for (int i = 0; i < numOfThreads; i++){
			startCell = cellCount;
			if (i < numOfThreads-1){
				endCell = cellCount + cellsPerThread - 1;
			} else {
				endCell = cells;
			}
			MSDThread job = new MSDThread(startCell, endCell, i+1);
			job.addThreadCompleteListener(this);
			Thread t =  new Thread(job);
			t.start();
			cellCount += cellsPerThread;
		}
	}
	
	class MSDThread implements Runnable {
		
		private int startCell, endCell, index;
		
		private ArrayList<ThreadCompleteListener> threadListeners = 
				new ArrayList<ThreadCompleteListener>();
		
		public MSDThread(int startCell, int endCell, int index){
			this.startCell = startCell;
			this.endCell = endCell;
			this.index = index;
		}

		@Override
		public void run() {
			double sum = 0.0;
			for (int j = 0; j < time; j++){
				if (j % 1000 == 0 || j == time-1) {
					System.out.println("Thread " + index + " - computing dt = " + j);
				}
				//writer.printf("%d ", j);
				for (int k = startCell; k <= endCell; k++){
					sum = 0.0;
					if (j != 0){
						for (int i = 0; i < time-j; i++){
							sum += mag2(xDiff(data[i+j][k*2+1], data[i][k*2+1]),
									yDiff(data[i+j][k*2+2], data[i][k*2+2]));
						}
					}
					sum /= (time-j);
					msd[j][k-1] = sum;
				}
			}
			this.notifyThreadCompleteListener();
		}
		
		/**
		 * Add to the list of listeners to notify 
		 * when the simulation has completed
		 * @param l listener
		 */
		public void addThreadCompleteListener(ThreadCompleteListener l){
			threadListeners.add(l);
		}
		
		/**
		 * Remove from the list of listeners to notify 
		 * when the simulation has completed
		 * @param l listener
		 */
		public void removeThreadCompleteListener(ThreadCompleteListener l){
			threadListeners.remove(l);
		}

		/**
		 * Notify the registered listeners that the simulation has completed
		 */
		public void notifyThreadCompleteListener(){
			for (ThreadCompleteListener l : threadListeners){
				l.notifyThreadComplete(this);
			}
		}
	}

	/**
	 * Calculate the difference between the x components of two points in 
	 * periodic boundary conditions
	 * @param x1 x component of point 1
	 * @param x2 x component of point 2
	 * @return the difference between the x components of two points
	 */
	public double xDiff(double x1, double x2){
		double dx = x1-x2;
		if (dx > (double) nx / 2.0){
			dx -= nx;
		} else if (dx < (double) -nx / 2.0){
			dx += nx;
		}
		return dx;
	}

	/**
	 * Calculate the difference between the y components of two points in 
	 * periodic boundary conditions
	 * @param y1 y component of point 1
	 * @param y2 y component of point 2
	 * @return the difference between the y components of two points
	 */
	public double yDiff(double y1, double y2){
		double dy = y1-y2;
		if (dy > (double) ny / 2.0){
			dy -= ny;
		} else if (dy < (double) -ny / 2.0){
			dy += ny;
		}
		return dy;
	}

	/**
	 * Return the magnitude squared of the vector (x,y)
	 * @param x x component of the vector
	 * @param y y component of the vector
	 * @return the magnitude squared of the vector
	 */
	public double mag2(double x, double y){
		return x * x + y * y;
	}

	@Override
	public void notifyThreadComplete(Runnable r) {
		completedThread++;
		
		//write the data to file when completed
		if (completedThread == numOfThreads){
			PrintWriter writer;
			try {
				writer = new PrintWriter(
						new BufferedWriter(new FileWriter(outputFile)));
				double avg;
				for (int i = 0; i < msd.length; i++){
					avg = 0.0;
					for (int j = 0; j < msd[0].length; j++){
						//writer.printf("%.8f ", msd[i][j]);
						avg += msd[i][j];
					}
					avg /= (double) msd[0].length;
					writer.printf("%d %.8f\n", i, avg);
				}
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void main (String [] args) throws IOException {
		int nx = Integer.parseInt(args[0]);
		int ny = Integer.parseInt(args[1]);
		int cells = Integer.parseInt(args[2]);
		int time = Integer.parseInt(args[3]);
		int numOfThreads = Integer.parseInt(args[4]);
		String dataFile = args[5];
		String outputFile = args[6];
		new CalculateMSDMultiThread(nx,ny,cells,time,numOfThreads,dataFile,outputFile);
	}
}
