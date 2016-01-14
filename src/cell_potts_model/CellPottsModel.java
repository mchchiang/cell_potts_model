package cell_potts_model;

import java.util.Random;

public class CellPottsModel {
	
	private int nx, ny, nxmax, nymax;
	private int q;	
	private int [][] spin;
	
	//parameters for the simulation
	private double temperature;
	private double lambda;
	
	//physical quantities of each cell
	private int delta; //average length of each cell
	
	private double [] area;
	private double [] areaTarget;
	
	//for measuring the centre of mass of the cells
	private double [] xcm;
	private double [] ycm;
	
	//seed for generating random numbers
	private int seed;
	private Random rand;
	
	public CellPottsModel(){
		
		nxmax = 500;
		nymax = 500;	
		nx = 40;
		ny = 40;
		q = 25;
		seed = -1;
		temperature = 1;
		lambda = 0.1;
		
		spin = new int [nx][ny];
		area = new double [q];
		areaTarget = new double [q];
		xcm = new double [q];
		ycm = new double [q];
		
		rand = new Random(seed);
	}
	
	
	public void run(int numOfSweeps){
		
		//initialisation setting each of the Q cells as square
		delta = (int) (Math.sqrt(nx*ny)/ (double) q);
		
		for (int i = 0; i < q; i++){
			areaTarget[i] = (double) (nx*ny) / (double) q;
			area[i] = 0.0;
		}
		
		int ind1, ind2, cellind;
		for (int i = 0; i < nx; i++){
			for (int j = 0; j < ny; j++){
				ind1 = i / delta;
				ind2 = j / delta;
				cellind = (int) (ind2 * Math.sqrt(q)) + ind1;
				
				if (cellind > q) cellind = q;
				
				spin[i][j] = cellind+1; //use q = 0 as empty cells
				area[spin[i][j]] += 1.0;
			}
		}
		
		int iup, idown, jup, jdown, i, j;
		for (int n = 0;  n < numOfSweeps; n++){
			for (int k = 0; k < nx * ny; k++){
				i = rand.nextInt(nx);
				j = rand.nextInt(ny);
				
				iup = i+1;
				idown = i-1;
				jup = j+1;
				jdown = j-1;
				
				//apply periodic boundary condition
				if (i == nx-1) iup = 0;
				if (i == 0) idown = nx-1;
				if (j == ny-1) jup = 0;
				if (j == 0) jdown = ny-1;
				
			}
		}
	}
	
	public double pottsEnergy(int i, int j){
		double alpha = 2.0;
		double beta = 16.0;
		double energy = alpha;
		
		if (i == j) energy = 0.0;
		if (i != j && (i==0) || (j==0)) energy = beta;
		
		return energy;
	}
	
}
