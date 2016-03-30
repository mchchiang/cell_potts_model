package cell_potts_model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * CellPottsModel.java
 * 
 * The main kernel which implements the cellular Potts model.
 * 
 * @author Michael Chiang
 *
 */
public class CellPottsModel extends SpinModel {
	
	private int nx, ny;
	private int q;	
	private int [][] spin;

	//parameters for the simulation
	private double temperature;
	private double lambda;
	private double motility;

	private int numOfSweeps = 0;
	private int nequil = 0;

	//parameters for cell adhesion energy
	private double alpha;
	private double beta;

	//physical quantities of each cell
	private int delta; //average length of each cell

	private double [] area;
	private double [] areaTarget;

	//for measuring the centre of mass of the cells
	private double [] xcm;
	private double [] ycm;
	private double [] xcmNew;
	private double [] ycmNew;

	//variables for measuring <R^2>
	private double [] rx;
	private double [] ry;

	//variables for motility
	private double [] px;
	private double [] py;
	private double [] theta;
	private double rotateDiff = 0.1;
	private List<ArrayList<Integer>> spinXPos;
	private List<ArrayList<Integer>> spinYPos;

	//variables for calculating acceptance rate
	private double acceptRate;

	//varaibles for generating random numbers
	private int seed;
	private Random rand;

	//whether to notify or not the observers about spin updates
	private boolean notify = false;

	//data writers
	private DataWriter [] writers;

	//constructors
	/**
	 * Initialise the cellular Potts model
	 * @param nx number of columns in the lattice
	 * @param ny number of rows in the lattice
	 * @param q number of cells
	 * @param temp effective temperature
	 * @param lambda strength on area constraint
	 * @param alpha interfacial energy between cells
	 * @param beta free boundary energy
	 * @param motility cell motility strength (P)
	 * @param rotateDiff rotational diffusion coefficient
	 * @param seed seed for random generator
	 * @param n number of Monte-Carlo steps (MCS) to take in the simulation
	 * @param nequil number of MCS to take before making measurements
	 * @param writers data writers to store measurements to file
	 * @param notify whether or not to notify any observers when the spin 
	 * at a lattice site is updated
	 */
	public CellPottsModel(int nx, int ny, int q, double temp, 
			double lambda, double alpha, double beta, 
			double motility, double rotateDiff, int seed, 
			int n, int nequil, DataWriter [] writers,
			boolean notify){
		this.nx = nx;
		this.ny = ny;
		this.q = q;
		this.seed = seed;
		this.temperature = temp;
		this.lambda = lambda;
		this.motility = motility;
		this.alpha = alpha;
		this.beta = beta;
		this.rotateDiff = rotateDiff;
		this.numOfSweeps = n;
		this.nequil = nequil;
		this.writers = writers;
		this.notify = notify;
	}

	//constructor used for unit testing only!
	protected CellPottsModel(int nx, int ny, int q, double [] areaTarget, 
			double temp, double lambda, double alpha, double beta, double motility, int seed){
		this.nx = nx;
		this.ny = ny;
		this.q = q;
		this.areaTarget = areaTarget;
		this.temperature = temp;
		this.lambda = lambda;
		this.alpha = alpha;
		this.beta = beta;
		this.motility = motility;
		this.seed = seed;
	}

	/**
	 * Initialise the model
	 */
	public void init(){
		acceptRate = 0.0;

		area = new double [q+1];
		areaTarget = new double [q+1];

		xcm = new double [q+1];
		ycm = new double [q+1];
		xcmNew = new double [q+1];
		ycmNew = new double [q+1];

		rx = new double [q+1];
		ry = new double [q+1];

		px = new double [q+1];
		py = new double [q+1];
		theta = new double [q+1];

		spinXPos = new ArrayList<ArrayList<Integer>>();
		spinYPos = new ArrayList<ArrayList<Integer>>();

		for (int i = 0; i <= q; i++){
			spinXPos.add(new ArrayList<Integer>());
			spinYPos.add(new ArrayList<Integer>());
		}

		rand = new Random();
	}

	//initialisation of the spins

	/**
	 * Initialise the lattice with random spins
	 */
	public void initSpin(){
		init();

		spin = new int [nx][ny];

		//initialising each of the Q cells as a square with length delta
		delta = (int) (Math.sqrt((nx*ny)/ (double) q));

		for (int i = 0; i < q; i++){
			areaTarget[i] = (double) (nx*ny) / (double) q;
			area[i] = 0.0;
		}

		int ind1, ind2, cellind;
		for (int i = 0; i < nx; i++){
			for (int j = 0; j < ny; j++){
				ind1 = i / delta;
				ind2 = j / delta;
				cellind = (int) (ind2 * Math.sqrt(q)) + ind1 + 1; //use q = 0 as empty cells

				if (cellind >= q) cellind = q;

				spin[i][j] = cellind; 
				area[spin[i][j]] += 1.0;

				spinXPos.get(cellind).add(i);
				spinYPos.get(cellind).add(j);
			}
		}
	}

	/**
	 * Initialise the lattice with specified spin configuration
	 * @param spin initial condition of the lattice
	 */
	public void initSpin(int [][] spin){
		init();

		if (spin.length == nx && spin[0].length == ny){

			//deep copy the spin
			this.spin = new int [nx][ny];
			for (int i = 0; i < nx; i++){
				for (int j = 0; j < ny; j++){
					this.spin[i][j] = spin[i][j];
					area[spin[i][j]] += 1.0;
					spinXPos.get(spin[i][j]).add(i);
					spinYPos.get(spin[i][j]).add(j);
				}
			}

			/*
			 * set the target area for each cell (exclude cells with zero area),
			 * which is the same for all cells
			 */
			int cellsAlive = 0;
			for (int i = 1; i <= q; i++){
				if (area[i] > 0.000001){
					cellsAlive++;
				}
			}
			double target = (double) (nx*ny) / (double) cellsAlive;
			for (int i = 0; i <= q; i++){
				areaTarget[i] = target;
			}
		}		
	}

	/**
	 * Initialise random polarity vector to each cell
	 */
	public void initPolarity(){
		for (int i = 1; i<= q; i++){
			theta[i] = rand.nextDouble() * 2 * Math.PI;
			px[i] = Math.cos(theta[i]);
			py[i] = Math.sin(theta[i]);
		}
	}
	
	/**
	 * Change the polarity vector of each cell via a rotation diffusion
	 * process
	 */
	public void updatePolarity(){
		for (int i = 1; i <= q; i++){
			theta[i] += Math.sqrt(6 * rotateDiff) * (rand.nextDouble()*2-1);
			px[i] = Math.cos(theta[i]);
			py[i] = Math.sin(theta[i]);
		}
	}

	/**
	 * Run the model
	 */
	public void run(){
		acceptRate = 0.0;

		for (int n = 0;  n < numOfSweeps; n++){
			for (int k = 0; k < nx*ny; k++){
				nextStep();	
			}

			updatePolarity();

			//only start measuring CM right before equilibrium is reached
			if (n >= nequil){
				calculateCM(n);
			}
			if (n > nequil && n < numOfSweeps){
				updateR();
			}
			if (n >= nequil && n < numOfSweeps-1){
				writeData(n);
			}
			
		}
		acceptRate /= (double) ((long) numOfSweeps * nx * ny);//potentially big
		writeData(numOfSweeps-1);
	}

	/**
	 * Take a single elementary step in the simulation
	 */
	public void nextStep(){
		int i, j, p;
		int newSpin = 0;

		i = rand.nextInt(nx);
		j = rand.nextInt(ny);

		int oldSpin = spin[i][j];

		//only perform calculations if the neighbours don't have the same spin
		if (!hasSameNeighbours(i,j)){
			/*
			 * randomly pick one of its neighbour's spin (including 
			 * the ones located at the corners with respect to
			 * the lattice site)
			 */
			p = rand.nextInt(8);
			if (p == 0){
				newSpin = spin[iup(i)][j];
			} else if (p == 1){
				newSpin = spin[idown(i)][j];
			} else if (p == 2){
				newSpin = spin[i][jup(j)];
			} else if (p == 3){
				newSpin = spin[i][jdown(j)];
			} else if (p == 4){
				newSpin = spin[iup(i)][jup(j)];
			} else if (p == 5){
				newSpin = spin[idown(i)][jup(j)];
			} else if (p == 6){
				newSpin = spin[iup(i)][jdown(j)];
			} else if (p == 7){
				newSpin = spin[idown(i)][jdown(j)];
			} 

			//update area of the affected cells due to spin change
			double newAreaNewSpin, newAreaOldSpin;

			if (newSpin != oldSpin){
				newAreaNewSpin = area[newSpin]+1;
				newAreaOldSpin = area[oldSpin]-1;
			} else {
				newAreaNewSpin = area[newSpin];
				newAreaOldSpin = area[oldSpin];
			}

			//implement the metropolis algorithm
			double negDeltaE = negDeltaE(i, j, newSpin, 
					area[oldSpin], area[newSpin], newAreaOldSpin, newAreaNewSpin);

			double totalEnergy = negDeltaE;

			//only run the motility calculation if it is non-zero
			if (motility > 0.000001){
				totalEnergy += motilityE(i, j, newSpin, motility);
			} 

			if (Math.log(rand.nextDouble()) <= totalEnergy / temperature){
				area[spin[i][j]] = newAreaOldSpin;
				area[newSpin] = newAreaNewSpin;
				spin[i][j] = newSpin;
				spinXPos.get(oldSpin).remove(new Integer(i));
				spinYPos.get(oldSpin).remove(new Integer(j));
				spinXPos.get(newSpin).add(new Integer(i));
				spinYPos.get(newSpin).add(new Integer(j));

				acceptRate = acceptRate + 1.0;

				if (notify){
					this.setChanged();
					this.notifyObservers(new Object [] {i,j});
				}
			}
		}
	}

	/**
	 * Check if all neighbours of the specified lattice site have the same spin
	 * @param i column index of the site
	 * @param j row index of the site
	 * @return return <code>true</code> if all neighbours have the same spin
	 */
	public boolean hasSameNeighbours(int i, int j){
		int cellSpin = spin[i][j];
		if (cellSpin == spin[iup(i)][j] &&
				cellSpin == spin[idown(i)][j] &&
				cellSpin == spin[i][jup(j)] &&
				cellSpin == spin[i][jdown(j)] &&
				cellSpin == spin[iup(i)][jup(j)] &&
				cellSpin == spin[idown(i)][jup(j)] &&
				cellSpin == spin[iup(i)][jdown(j)] &&
				cellSpin == spin[idown(i)][jdown(j)]){
			return true;
		}
		return false;
	}

	/**
	 * Calculate the energy from active cell motility
	 * @param i column index of the lattice site considered in the spin copy attempt
	 * @param j row index of the lattice site considered in the spin copy attempt
	 * @param newSpin new spin value for the spin copy attempt
	 * @param p cell motility strength
	 * @return energy from active cell motility
	 */
	public double motilityE(int i, int j, int newSpin, double p){
		double energy = 0.0;
		double [] dcmOld = calculateDeltaCM(i,j, spin[i][j], true);
		double [] dcmNew = calculateDeltaCM(i,j, newSpin, false);
		energy += p * dot(dcmOld[0], dcmOld[1], px[spin[i][j]], py[spin[i][j]]);
		energy += p * dot(dcmNew[0], dcmNew[1], px[newSpin], py[newSpin]);
		return energy;
	}

	/**
	 * Calculate the negative of the change in energy due to spin change
	 * @param i column index of the lattice site considered in the spin copy attempt
	 * @param j row index of the lattice site considered in the spin copy attempt
	 * @param newSpin new spin value for the spin copy attempt
	 * @param oldAreaOldSpin current area of the cell with the current spin 
	 * of the considered lattice site
	 * @param oldAreaNewSpin current area of the cell with the proposed spin
	 * @param newAreaOldSpin new area of the cell with the current spin
	 * @param newAreaNewSpin new area of the cell with the proposed spin
	 * @return
	 */
	public double negDeltaE(int i, int j, int newSpin, 
			double oldAreaOldSpin, double oldAreaNewSpin,
			double newAreaOldSpin, double newAreaNewSpin){

		int iup = iup(i);
		int idown = idown(i);
		int jup = jup(j);
		int jdown = jdown(j);

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
	 * Return the pair-wise Potts energy between two spins
	 * @param i spin 1
	 * @param j spin 2
	 * @return the pair-wise Potts energy between the two spins
	 */
	public double pottsEnergy(int i, int j){
		double energy = alpha;

		if (i == j) energy = 0.0;
		if (i != j && (i==0) || (j==0)) energy = beta;

		return energy;
	}

	/**
	 * Calculate the change in the centre of mass (CM) of a cell when the spin 
	 * copy attempt is accepted
	 * @param x column index of the lattice site considered in the spin copy attempt
	 * @param y row index of the lattice site considered in the spin copy attempt
	 * @param spin the index of the cell of interest
	 * @param remove whether the site is added to or removed from the specified cell
	 * @return a vector storing the change in the CM
	 */
	public double [] calculateDeltaCM(int x, int y, int spin, boolean remove){
		ArrayList<Integer> xPos = spinXPos.get(spin);
		ArrayList<Integer> yPos = spinYPos.get(spin);
		double xcm = calculateCM(xPos, nx);
		double ycm = calculateCM(yPos, ny);
		double xcmNew, ycmNew;

		Integer xint = new Integer(x);
		Integer yint = new Integer(y);

		if (remove){
			xPos.remove(xint);
			yPos.remove(yint);
			xcmNew = calculateCM(xPos, nx);
			ycmNew = calculateCM(yPos, ny);
			xPos.add(xint);
			yPos.add(yint);
		} else {
			xPos.add(xint);
			yPos.add(yint);
			xcmNew = calculateCM(xPos, nx);
			ycmNew = calculateCM(yPos, ny);
			xPos.remove(xint);
			yPos.remove(yint);
		}
		return new double []{xDiff(xcmNew, xcm), yDiff(ycmNew, ycm)};
	}

	/**
	 * Calculate a component of the centre of mass (CM) for a particular cell
	 * @param pos an array storing the positions of lattice sites of the cell
	 * @param length the length of the lattice
	 * @return the specified component of the cell's CM
	 */
	public double calculateCM(ArrayList<Integer> pos, int length){
		int n = pos.size();
		double cm = 0;

		int width = (int) Math.ceil(0.05 * length);

		int x = 0;
		boolean inCriticalRegion = false;
		for (int i = 0; i < n; i++){
			x = pos.get(i);
			cm += x;
			if (x > length-1-width || x < width){
				inCriticalRegion = true;
				break;
			}	
		}

		if (!inCriticalRegion){
			cm /= (double) n;
			return cm + 0.5;
		}

		cm = 0;

		double leftCount = 0;
		double rightCount = 0;
		double leftSum = 0;
		double rightSum = 0;
		double total = 0;

		for (int i = 0; i < n; i++){
			x = pos.get(i);
			if (x < length / 2){
				leftCount++;
				leftSum += (x+0.5);//shift to centre of cell
			} else {
				rightCount++;
				rightSum += (x+0.5);
			}
		}

		if (leftCount > rightCount){
			total = leftSum + rightSum - rightCount * length;
		} else {
			total = rightSum + leftSum + leftCount * length;
		}

		/*
		 * correction for cases if leftCount == rightCount that there 
		 * is a chance the addition above will yield a result outside the
		 * boundary
		 */

		if (total < 0){
			total = leftSum + leftCount * length + rightSum;
		} else if (total > length * n){
			total = leftSum + rightSum - rightCount * length;
		}

		cm = (double) total / (double) n;

		return cm;
	}

	/**
	 * Calculate the centre of mass (CM) for all cells
	 */
	public void calculateCM(int n){
		for (int i = 0; i <= q; i++){
			xcm[i] = xcmNew[i];
			ycm[i] = ycmNew[i];

			xcmNew[i] = calculateCM(spinXPos.get(i), nx);
			ycmNew[i] = calculateCM(spinYPos.get(i), ny);
		}
	}

	/**
	 * Update the position vector of all cells
	 */
	public void updateR(){
		for (int i = 1; i <= q; i++){
			rx[i] += xDiff(xcmNew[i], xcm[i]);
			ry[i] += yDiff(ycmNew[i], ycm[i]);
		}
	}

	/**
	 * Return the mean square displacement (MSD) at the current MCS
	 * @return an array storing the MSD (1st element) 
	 * and the error (2nd element)
	 */
	public double [] calculateR2(){
		double r2 = 0.0;
		double r2Sq = 0.0; //for computing the error of R^2
		double value = 0.0;
		int count = 0;
		for (int i = 1; i <= q; i++){
			if (area[i] > 0.000001){//only measure cells with non-zero area
				value = mag2(rx[i], ry[i]);
				r2 += value;
				r2Sq += value * value;
				count++;
			}
		}
		r2 /= (double) count;
		r2Sq /= (double) count;

		//use unbiased estimate for errors
		return new double [] 
				{r2, Math.sqrt((r2Sq - r2 * r2) * ((double) count / (double) (count-1)))};

	}

	/**
	 * Calculate the non-Gaussian parameter
	 * @return an array of two elements storing the non-Gaussian parameter and
	 * the fourth moment of the probability distribution of the cell's 
	 * displacement
	 */
	public double [] alpha2(){
		double r2 = 0.0;
		double r4 = 0.0;
		double value = 0.0;
		int count = 0;
		for (int i = 1; i <= q; i++){
			if (area[i] > 0.000001){
				value = mag2(rx[i], ry[i]);
				r2 += value;
				value = value * value;
				r4 += value;
				count++;
			}
		}

		r2 /= (double) count;
		r4 /= (double) count;

		double a2 = ((1.0 / 2.0) * r4 / (r2 * r2)) - 1.0;
		
		return new double [] {a2, r4};
	}

	//vector related operations
	//calculate the difference between two points in periodic B.C.
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

	/**
	 * Return the dot product of two vectors
	 * @param x1 x component of vector 1
	 * @param y1 y component of vector 1
	 * @param x2 x component of vector 2
	 * @param y2 y component of vector 2
	 * @return the dot product of the two vectors
	 */
	public double dot(double x1, double y1, double x2, double y2){
		return x1 * x2 + y1 * y2;
	}

	/**
	 * Notify the data writers to write data to file
	 * @param time the current Monte-Carlo Step of the simulation
	 */
	public void writeData(int time){
		for (int i = 0; i < writers.length; i++){
			writers[i].writeData(this, time);
		}
	}

	//periodic boundary methods
	private int iup(int i){
		if (i == nx-1) return 0;
		return i+1;
	}

	private int idown(int i){
		if (i == 0) return nx-1;
		return i-1;
	}

	private int jup(int j){
		if (j == ny-1) return 0;
		return j+1;
	}

	private int jdown(int j){
		if (j == 0) return ny-1;
		return j-1;
	}

	//accessor methods
	protected ArrayList<Integer> getSpinXPos(int spin){
		return spinXPos.get(spin);
	}
	protected ArrayList<Integer> getSpinYPos(int spin){
		return spinYPos.get(spin);
	}

	/**
	 * Set the total number of MCS to taken in the simulation
	 * @param n number of MCS
	 */
	public void setNumOfSweeps(int n){
		if (n >= 0){
			numOfSweeps = n;
		}
	}

	/**
	 * Return the total number of MCS taken in the simulation
	 */
	public int getNumOfSweeps(){
		return numOfSweeps;
	}

	/**
	 * Set the number of MCS to take before making measurements
	 * @param n number of MCS
	 */
	public void setNEquil(int n){
		if (n >= 0){
			nequil = n;
		}
	}

	/**
	 * Return the number of MCS taken before making measurements
	 */
	public int getNEquil(){
		return nequil;
	}

	/**
	 * Return the acceptance rate of the current simulation
	 */
	public double getAcceptRate(){
		return acceptRate;
	}

	/**
	 * Return the horizontal component of the center of mass for cell q
	 * @param q cell index
	 */
	public double getXCM(int q){
		return xcmNew[q];
	}
	
	/**
	 * Return the vertical component of the center of mass for cell q
	 * @param q cell index
	 */
	public double getYCM(int q){
		return ycmNew[q];
	}

	/**
	 * Set the interfacial energy between cells (alpha)
	 * @param a new interfacial energy
	 */
	public void setAlpha(double a){
		this.alpha = a;
	}

	/**
	 * Return the interfacial energy between cells (alpha)
	 */
	public double getAlpha(){
		return alpha;
	}

	/**
	 * Set the free boundary energy (beta)
	 * @param b new free boundary energy
	 */
	public void setBeta(double b){
		this.beta = b;
	}
	
	/**
	 * Return the free boundary energy (beta)
	 */
	public double getBeta(){
		return beta;
	}

	/**
	 * Set the strength of area constraint (lambda)
	 * @param l new area constraint strength
	 */
	public void setLambda(double l){
		if (l >= 0){
			this.lambda = l;
		}
	}

	/**
	 * Return the strength of area constraint (lambda)
	 */
	public double getLambda(){
		return lambda;
	}

	/**
	 * Set the cell motility strength
	 * @param p new cell motility value
	 */
	public void setMotility(double p){
		if (p >= 0){
			this.motility = p;
		}
	}

	/**
	 * Return the cell motility strength (P)
	 */
	public double getMotility(){
		return motility;
	}

	/**
	 * Return the rotational diffusion coefficient
	 */
	public double getRotateDiff(){
		return rotateDiff;
	}

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
	public double getTemp(){
		return temperature;
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
		double energy = 0.0;
		//summing pairwise spin energy
		for (int i = 0; i < nx; i++){
			for (int j = 0; j < ny; j++){
				energy += pottsEnergy(spin[i][j], spin[idown(i)][j]) +
						pottsEnergy(spin[i][j], spin[idown(i)][jdown(j)]) +
						pottsEnergy(spin[i][j], spin[i][jdown(j)]) +
						pottsEnergy(spin[i][j], spin[iup(i)][jdown(j)]);
			}
		}

		//summing energy associated with elastic area constraint
		for(int i = 0; i <= q; i++){
			energy += lambda * Math.pow(area[i] - areaTarget[i], 2);
		}
		return energy;
	}

	@Override
	public double getTotalSpin() {
		return 0;
	}

	@Override
	public int getTypesOfSpin(){
		return q+1;
	}
	
	/**
	 * Get the number of cells alive
	 */
	public int getNumOfCellsAlive(){
		int cellsAlive = 0;
		for (int i = 1; i <= q; i++){
			if (area[i] > 0.000001){
				cellsAlive++;
			}
		}
		return cellsAlive;
	}

	//printing methods
	/**
	 * Print the current lattice configuration to the system console
	 */
	public void printSpins(){
		System.out.println();
		for (int i = 0; i < ny; i++){
			for (int j = 0; j < nx; j++){
				System.out.print(spin[j][i] + " ");
			}
			System.out.println();
		}
	}

	/**
	 * Print the boundaries of the cells to the system console
	 */
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

	public static void main (String [] args){
		int nx = 200;
		int ny = 200;
		int q = 1000;
		double temp = 1.0;
		double lambda = 1.0;
		double alpha = 8.0;
		double beta = 16.0;
		double motility = 0.0;
		double rotateDiff = 0.0;
		int numOfSweeps = 10000;
		int nequil = 0;
		int seed = -1;
		int run = 1;

		SpinReader reader = new SpinReader();
		reader.openReader("init_spin_1000_2.dat");
		String filename = String.format("%d_%d_%d_a_%.1f_lam_%.1f_P_%.1f_D_%.1f_t_%d_run_%d.dat",
				nx, ny, q, alpha, lambda, motility, rotateDiff, numOfSweeps, run);
		DataWriter r2Writer = new R2Writer();
		DataWriter ergWriter = new EnergyWriter();
		DataWriter statsWriter = new StatisticsWriter(numOfSweeps, nequil);
		DataWriter a2Writer = new A2Writer();
		r2Writer.openWriter("r2_" + filename);
		ergWriter.openWriter("energy_" + filename);
		a2Writer.openWriter("a2_" + filename);
		statsWriter.openWriter("stats_" + filename);
		CellPottsModel model = new CellPottsModel(
				nx, ny, q, temp, lambda, alpha, beta, motility, 
				rotateDiff, seed, numOfSweeps, nequil, 
				new DataWriter [] {r2Writer, a2Writer, ergWriter, statsWriter}, false);
		model.initSpin(reader.readSpins());
		model.initPolarity();
		model.run();
		r2Writer.closeWriter();
		ergWriter.closeWriter();
		statsWriter.closeWriter();
		a2Writer.closeWriter();
		reader.closeReader();
	}
}
