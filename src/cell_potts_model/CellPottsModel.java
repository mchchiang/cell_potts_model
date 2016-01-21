package cell_potts_model;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

public class CellPottsModel extends SpinModel {

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
	private double [] xx;
	private double [] xy;
	private double [] yx;
	private double [] yy;
	private double [] xcm;
	private double [] ycm;

	//seed for generating random numbers
	private int seed;
	private Random rand;
	
	private PrintWriter cmWriter = null;

	//constructors
	public CellPottsModel(){
		nxmax = 500;
		nymax = 500;	
		nx = 50;
		ny = 50;
		q = 10;
		seed = -1;
		temperature = 50;
		lambda = 0.1;

		spin = new int [ny][nx];
		area = new double [q+1];
		areaTarget = new double [q+1];

		xx = new double [q+1];
		xy = new double [q+1];
		yx = new double [q+1];
		yy = new double [q+1];
		xcm = new double [q+1];
		ycm = new double [q+1];
		rand = new Random(seed);
		
		try {
			cmWriter = new PrintWriter(new BufferedWriter(new FileWriter("cm.csv")));
			cmWriter.print("t,");
			
			for (int i = 0; i <= q; i++){
				cmWriter.print(i + "x," + i + "y,");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	//constructor used for unit testing only!
	protected CellPottsModel(int [][] initSpin, double [] area, 
			double [] areaTarget, int q, double temp, double lambda, int seed){
		this.ny = initSpin.length;
		this.nx = initSpin[0].length;
		this.spin = initSpin;
		this.q = q;
		this.area = area;
		this.areaTarget = areaTarget;
		this.temperature = temp;
		this.lambda = lambda;
		this.seed = seed;

		xx = new double [q+1];
		xy = new double [q+1];
		yx = new double [q+1];
		yy = new double [q+1];
		xcm = new double [q+1];
		ycm = new double [q+1];
		rand = new Random(seed);
	}


	public void init(){
		//initialising each of the Q cells as a square with length delta
		delta = (int) (Math.sqrt((nx*ny)/ (double) q));

		for (int i = 0; i < q; i++){
			areaTarget[i] = (double) (nx*ny) / (double) q;
			area[i] = 0.0;
		}

		int ind1, ind2, cellind;
		for (int i = 0; i < ny; i++){
			for (int j = 0; j < nx; j++){
				ind1 = i / delta;
				ind2 = j / delta;
				cellind = (int) (ind1 * Math.sqrt(q)) + ind2 + 1; //use q = 0 as empty cells

				if (cellind >= q) cellind = q;

				spin[i][j] = cellind; 
				area[spin[i][j]] += 1.0;
			}
		}
	}

	public void run(int numOfSweeps){
		init();
		calculateCM();
		writeCM(0);
		for (int n = 0;  n < numOfSweeps; n++){
			for (int k = 0; k < nx * ny; k++){
				nextStep();	
			}
			calculateCM();
			writeCM(n+1);
		}
		
		cmWriter.close();
	}

	public void nextStep(){
		int iup, idown, jup, jdown, i, j, p;
		int newSpin = 0;

		i = rand.nextInt(ny);
		j = rand.nextInt(nx);

		iup = i+1;
		idown = i-1;
		jup = j+1;
		jdown = j-1;

		//apply periodic boundary condition
		if (i == ny-1) iup = 0;
		if (i == 0) idown = ny-1;
		if (j == nx-1) jup = 0;
		if (j == 0) jdown = nx-1;

		/*
		 * randomly pick one of its neighbour's spin (including 
		 * the ones located along the diagonals with respect to
		 * the lattice site
		 */
		p = rand.nextInt(8);
		if (p == 0){
			newSpin = spin[iup][j];
		} else if (p == 1){
			newSpin = spin[idown][j];
		} else if (p == 2){
			newSpin = spin[i][jup];
		} else if (p == 3){
			newSpin = spin[i][jdown];
		} else if (p == 4){
			newSpin = spin[iup][jup];
		} else if (p == 5){
			newSpin = spin[idown][jup];
		} else if (p == 6){
			newSpin = spin[iup][jdown];
		} else if (p == 7){
			newSpin = spin[idown][jdown];
		} 

		//update area of the affected cells due to spin change
		double newAreaNewSpin, newAreaOldSpin;

		if (newSpin != spin[i][j]){
			newAreaNewSpin = area[newSpin]+1;
			newAreaOldSpin = area[spin[i][j]]-1;
		} else {
			newAreaNewSpin = area[newSpin];
			newAreaOldSpin = area[spin[i][j]];
		}

		//implement the metropolis algorithm
		double negDeltaE = negDeltaE(i, j, newSpin, 
				area[spin[i][j]], area[newSpin], newAreaOldSpin, newAreaNewSpin);

		if (Math.log(rand.nextDouble()) <= negDeltaE / temperature){
			area[spin[i][j]] = newAreaOldSpin;
			area[newSpin] = newAreaNewSpin;
			spin[i][j] = newSpin;

			this.setChanged();
			this.notifyObservers(new Object [] {i,j});
		}
	}

	//calculate the negative of the change in energy due to spin change
	public double negDeltaE(int i, int j, int newSpin, 
			double oldAreaOldSpin, double oldAreaNewSpin,
			double newAreaOldSpin, double newAreaNewSpin){

		int iup, idown, jup, jdown;

		iup = i+1;
		idown = i-1;
		jup = j+1;
		jdown = j-1;

		//apply periodic boundary condition
		if (i == ny-1) iup = 0;
		if (i == 0) idown = ny-1;
		if (j == nx-1) jup = 0;
		if (j == 0) jdown = nx-1;

		double eold, enew;
		//calculate the change in energy due to spin change
		//energy changes due to pair-wise spin interaction
		eold = -(pottsEnergy(spin[i][j], spin[iup][j]) + 
				pottsEnergy(spin[i][j], spin[idown][j]) +
				pottsEnergy(spin[i][j], spin[i][jup]) +
				pottsEnergy(spin[i][j], spin[i][jdown]) +
				pottsEnergy(spin[i][j], spin[iup][jup]) +
				pottsEnergy(spin[i][j], spin[idown][jup]) +
				pottsEnergy(spin[i][j], spin[iup][jdown]) +
				pottsEnergy(spin[i][j], spin[idown][jdown]));

		enew = -(pottsEnergy(newSpin, spin[iup][j]) + 
				pottsEnergy(newSpin, spin[idown][j]) +
				pottsEnergy(newSpin, spin[i][jup]) +
				pottsEnergy(newSpin, spin[i][jdown]) +
				pottsEnergy(newSpin, spin[iup][jup]) +
				pottsEnergy(newSpin, spin[idown][jup]) +
				pottsEnergy(newSpin, spin[iup][jdown]) +
				pottsEnergy(newSpin, spin[idown][jdown]));

		//no restriction in the size of empty area (i.e. spin = 0)
		if (spin[i][j] != 0){
			eold -= lambda * Math.pow(oldAreaOldSpin - areaTarget[spin[i][j]], 2);
			enew -= lambda * Math.pow(newAreaOldSpin - areaTarget[spin[i][j]], 2);
		}

		if (newSpin != 0){
			eold -= lambda * Math.pow(oldAreaNewSpin - areaTarget[newSpin], 2);
			enew -= lambda * Math.pow(newAreaNewSpin - areaTarget[newSpin], 2);
		}

		return enew - eold;
	}


	/**
	 * returns the pair-wise Potts energy between two spins
	 * @param i spin 1
	 * @param j spin 2
	 * @return the pair-wise Potts energy between the two spins
	 */
	public double pottsEnergy(int i, int j){
		double alpha = 2.0;
		double beta = 16.0;
		double energy = alpha;

		if (i == j) energy = 0.0;
		if (i != j && (i==0) || (j==0)) energy = beta;

		return energy;
	}

	public void calculateCM(){
		for (int i = 0; i <= q; i++){
			xx[i] = 0;
			xy[i] = 0;
			yx[i] = 0;
			yy[i] = 0;
		}
		
		for (int i = 0; i < ny; i++){
			for (int j = 0; j < nx; j++){
				/*
				 * need to first convert the 2D space into a 3D tube to 
				 * satisfy the periodic boundary condition
				 */
				xx[spin[i][j]] += Math.cos((double) j * 2 * Math.PI / (double) nx);
				xy[spin[i][j]] += Math.sin((double) j * 2 * Math.PI / (double) nx);
				yx[spin[i][j]] += Math.cos((double) i * 2 * Math.PI / (double) ny);
				yy[spin[i][j]] += Math.sin((double) i * 2 * Math.PI / (double) ny);
			}
		}
		
		
		for (int i = 0; i <= q; i++){
			//average all the transformed coordinates
			xx[i] /= area[i];
			xy[i] /= area[i];
			yx[i] /= area[i];
			yy[i] /= area[i];
			
			//convert back to the 2D coordinates to get the CM
			xcm[i] = (Math.atan2(-xy[i], -xx[i]) + Math.PI) * (double) nx / (2 * Math.PI);
			ycm[i] = (Math.atan2(-yy[i], -yx[i]) + Math.PI) * (double) ny / (2 * Math.PI);
		}
	}
	
	public void writeCM(int time){
		cmWriter.println();
		cmWriter.print(time + ",");
		for (int i = 0; i <= q; i++){
			cmWriter.print(xcm[i] + "," + ycm[i] + ",");
		}
	}
	

	//accessor methods
	@Override
	public int getSpin(int i, int j){
		return spin[i][j];
	}

	@Override
	public int getNumOfRows() {
		return ny;
	}

	@Override
	public int getNumOfColumns() {
		return nx;
	}

	@Override
	public void setTemp(double t) {
		if (t >= 0){
			temperature = t;
		}
	}

	@Override
	public void setSpin(int i, int j, int value) {
		if (0 <= value && value <= q){
			area[spin[i][j]]--;
			spin[i][j] = value;
			area[value]++;
		}
	}

	@Override
	public double getTotalEnergy() {
		return 0;
	}

	@Override
	public double getTotalSpin() {
		return 0;
	}

	@Override
	public int getTypesOfSpin(){
		return q+1;
	}


	//printing methods
	public void printSpins(){
		System.out.println();
		for (int i = 0; i < ny; i++){
			for (int j = 0; j < nx; j++){
				System.out.print(spin[i][j] + " ");
			}
			System.out.println();
		}
	}

	public void printBoundaries(){
		System.out.println();

		int i, j, iup, idown, jup, jdown;
		for (i = 0; i < ny; i++){
			for (j = 0; j < nx; j++){
				iup = i+1;
				idown = i-1;
				jup = j+1;
				jdown = j-1;
				if (i == ny-1) iup = 0;
				if (i == 0) idown = ny-1;
				if (j == nx-1) jup = 0;
				if (j == 0) jdown = nx-1;

				if (spin[i][j] != spin[iup][j] ||
						spin[i][j] != spin[idown][j] ||
						spin[i][j] != spin[i][jup] ||
						spin[i][j] != spin[i][jdown] ||
						spin[i][j] != spin[iup][jup] ||
						spin[i][j] != spin[idown][jup] ||
						spin[i][j] != spin[iup][jdown] ||
						spin[i][j] != spin[idown][jdown]){
					System.out.print("1 ");
				} else {
					System.out.print("0 ");
				}
			}
		}
	}
}
