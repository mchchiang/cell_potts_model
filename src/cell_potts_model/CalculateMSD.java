package cell_potts_model;

import java.io.*;

public class CalculateMSD {

	private int nx, ny;

	public CalculateMSD(int nx, int ny, int cells, 
			int time, String dataFile, String outputFile) throws IOException {

		this.nx = nx;
		this.ny = ny;

		int x = cells * 2 + 3;
		int y = time;

		BufferedReader reader = new BufferedReader(new FileReader(dataFile));

		int count = 0;
		String line;

		double [][] data = new double [y][x];

		String [] array;
		while (reader.ready() && count < y){
			line = reader.readLine();
			array = line.trim().split("\\s++");
			if (array.length > 0 && !(array.length == 1 && array[0].equals(""))){
				if (array.length != x){
					System.out.println("error in line " + count + " array length: " 
							+ array.length + " x: " + x);
				}
				for (int i = 0; i < x; i++){
					data[count][i] = Double.parseDouble(array[i]);
				}
				count++;
			}
		}
		reader.close();

		PrintWriter writer = new PrintWriter(
				new BufferedWriter(new FileWriter(outputFile)));

		double sum;
		for (int j = 0; j < time; j++){
			System.out.println("Computing dt = " + j);
			writer.printf("%d ", j);
			for (int k = 1; k <= cells; k++){
				sum = 0.0;
				if (j != 0){
					for (int i = 0; i < time-j; i++){
						sum += mag2(xDiff(data[i+j][k*2+1], data[i][k*2+1]),
								yDiff(data[i+j][k*2+2], data[i][k*2+2]));
					}
				}
				sum /= (time-j);
				writer.printf("%f ", sum);
			}
			writer.printf("\n");
		}

		writer.close();
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


	public static void main (String [] args) throws IOException {
		int nx = Integer.parseInt(args[0]);
		int ny = Integer.parseInt(args[1]);
		int cells = Integer.parseInt(args[2]);
		int time = Integer.parseInt(args[3]);
		String dataFile = args[4];
		String outputFile = args[5];
		new CalculateMSD(nx,ny,cells,time,dataFile,outputFile);
	}
}
