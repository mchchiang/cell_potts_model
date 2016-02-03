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
	private double [] xcmNew;
	private double [] ycmNew;

	//variables for measuring <R^2>
	private double [] drx;
	private double [] dry;

	//variables for cell motility
	private double [] px;
	private double [] py;

	//seed for generating random numbers
	private int seed;
	private Random rand;

	private PrintWriter cmWriter = null;
	private PrintWriter r2Writer = null;

	//constructors
	public CellPottsModel(){
		nxmax = 50;
		nymax = 50;	
		nx = 50;
		ny = 50;
		q = 20;
		seed = -1;
		temperature = 1;
		lambda = 0.1;

		spin = new int [nx][ny];
		area = new double [q+1];
		areaTarget = new double [q+1];

		init();

		try {
			cmWriter = new PrintWriter(new BufferedWriter(new FileWriter("cm.dat")));
			r2Writer = new PrintWriter(new BufferedWriter(new FileWriter("r2_200_1000_j2_2.dat")));			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//constructor used for unit testing only!
	protected CellPottsModel(int [][] initSpin, double [] area, 
			double [] areaTarget, int q, double temp, double lambda, int seed){
		this.nx = initSpin.length;
		this.ny = initSpin[0].length;
		this.spin = initSpin;
		this.q = q;
		this.area = area;
		this.areaTarget = areaTarget;
		this.temperature = temp;
		this.lambda = lambda;
		this.seed = seed;
		init();
	}

	public void init(){
		xx = new double [q+1];
		xy = new double [q+1];
		yx = new double [q+1];
		yy = new double [q+1];
		xcm = new double [q+1];
		ycm = new double [q+1];
		xcmNew = new double [q+1];
		ycmNew = new double [q+1];

		drx = new double [q+1];
		dry = new double [q+1];
		
		px = new double [q+1];
		py = new double [q+1];

		rand = new Random();
	}


	public void initSpin(){
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
			}
		}
	}

	public void run(int numOfSweeps){
		//init();
		//printSpins();
		double x = 1.0 / Math.sqrt(2);
		for (int i = 0; i <= q; i++){
			px[i] = x;
			py[i] = x;
		}
		
		calculateCM();
		writeCM(0);
		for (int n = 0;  n < numOfSweeps; n++){
			for (int k = 0; k < nx * ny; k++){
				nextStep();	
			}
			//if (n % 2000 == 0){
			System.out.println("Sweep: " + n);
			//}

			calculateCM();
			if (n > 1000){
				updatedr();
				writeR2(n+1, calculateR2());	
			}
			writeCM(n+1);
		}

		cmWriter.close();
		r2Writer.close();
	}

	public void nextStep(){
		int i, j, p;
		int newSpin = 0;

		i = rand.nextInt(nx);
		j = rand.nextInt(ny);

		/*
		 * randomly pick one of its neighbour's spin (including 
		 * the ones located along the diagonals with respect to
		 * the lattice site
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

		if (Math.log(rand.nextDouble()) <= (negDeltaE + motilityE(i,j,newSpin))/ temperature){
			area[spin[i][j]] = newAreaOldSpin;
			area[newSpin] = newAreaNewSpin;
			spin[i][j] = newSpin;

			this.setChanged();
			this.notifyObservers(new Object [] {i,j});
		}
	}

	//calculate the energy 
	public double motilityE(int i, int j, int newSpin){
		double energy = 0.0;
		double [] dcmOld = calculateDeltaCM(i,j, spin[i][j], true);
		double [] dcmNew = calculateDeltaCM(i,j, newSpin, false);
		energy += 4.0 * dot(dcmOld[0], dcmOld[1], px[spin[i][j]], py[spin[i][j]]);
		energy += 4.0 * dot(dcmNew[0], dcmNew[1], px[newSpin], py[newSpin]);
		return energy;
	}

	//calculate the negative of the change in energy due to spin change
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


	public double [] calculateDeltaCM(int x, int y, int cellSpin, boolean remove){
		double xx = 0;
		double xy = 0;
		double yx = 0;
		double yy = 0;
		
		for (int i = 0; i < nx; i++){
			for (int j = 0; j < ny; j++){
				if (spin[i][j] == cellSpin && x != i && y != j){
					xx += Math.cos((double) i * 2 * Math.PI / (double) nx);
					xy += Math.sin((double) i * 2 * Math.PI / (double) nx);
					yx += Math.cos((double) j * 2 * Math.PI / (double) ny);
					yy += Math.sin((double) j * 2 * Math.PI / (double) ny);
				}
			}
		}
		
		double xxNew, xyNew, yxNew, yyNew, xcm, ycm, xcmNew, ycmNew;
		
		if (!remove){
			xxNew = xx + Math.cos((double) x * 2 * Math.PI / (double) nx);
			xyNew = xy + Math.sin((double) x * 2 * Math.PI / (double) nx);
			yxNew = yx + Math.cos((double) y * 2 * Math.PI / (double) ny);
			yyNew = yy + Math.sin((double) y * 2 * Math.PI / (double) ny);
			xxNew /= (double) (area[cellSpin] + 1);
			xyNew /= (double) (area[cellSpin] + 1);
			yxNew /= (double) (area[cellSpin] + 1);
			yyNew /= (double) (area[cellSpin] + 1);
			xx /= (double) area[cellSpin];
			xy /= (double) area[cellSpin];
			yx /= (double) area[cellSpin];
			yy /= (double) area[cellSpin];
			
		} else {
			xxNew = xx;
			xyNew = xy;
			yxNew = yx;
			yyNew = yy;
			xx += Math.cos((double) x * 2 * Math.PI / (double) nx);
			xy += Math.sin((double) x * 2 * Math.PI / (double) nx);
			yx += Math.cos((double) y * 2 * Math.PI / (double) ny);
			yy += Math.sin((double) y * 2 * Math.PI / (double) ny);
			xx /= (double) area[cellSpin];
			xy /= (double) area[cellSpin];
			yx /= (double) area[cellSpin];
			yy /= (double) area[cellSpin];
			xxNew /= (double) (area[cellSpin] - 1);
			xyNew /= (double) (area[cellSpin] - 1);
			yxNew /= (double) (area[cellSpin] - 1);
			yyNew /= (double) (area[cellSpin] - 1);
		}
		xcmNew = (Math.atan2(-xyNew, -xxNew) + Math.PI) * (double) nx / (2 * Math.PI);
		ycmNew = (Math.atan2(-yyNew, -yxNew) + Math.PI) * (double) ny / (2 * Math.PI);
		xcm = (Math.atan2(-xy, -xx) + Math.PI) * (double) nx / (2 * Math.PI);
		ycm = (Math.atan2(-yy, -yx) + Math.PI) * (double) ny / (2 * Math.PI);
		
		return new double [] {xDiff(xcmNew, xcm), yDiff(ycmNew, ycm)};
	}


	public void calculateCM(){
		for (int i = 0; i <= q; i++){
			xx[i] = 0;
			xy[i] = 0;
			yx[i] = 0;
			yy[i] = 0;
		}

		for (int i = 0; i < nx; i++){
			for (int j = 0; j < ny; j++){
				/*
				 * need to first convert the 2D space into a 3D tube to 
				 * satisfy the periodic boundary condition
				 */
				xx[spin[i][j]] += Math.cos((double) i * 2 * Math.PI / (double) nx);
				xy[spin[i][j]] += Math.sin((double) i * 2 * Math.PI / (double) nx);
				yx[spin[i][j]] += Math.cos((double) j * 2 * Math.PI / (double) ny);
				yy[spin[i][j]] += Math.sin((double) j * 2 * Math.PI / (double) ny);
			}
		}

		for (int i = 0; i <= q; i++){
			//average all the transformed coordinates
			xx[i] /= area[i];
			xy[i] /= area[i];
			yx[i] /= area[i];
			yy[i] /= area[i];

			//transfer new cm to old cm
			xcm[i] = xcmNew[i];
			ycm[i] = ycmNew[i];

			//convert back to the 2D coordinates to get the CM
			xcmNew[i] = (Math.atan2(-xy[i], -xx[i]) + Math.PI) * (double) nx / (2 * Math.PI);
			ycmNew[i] = (Math.atan2(-yy[i], -yx[i]) + Math.PI) * (double) ny / (2 * Math.PI);

		}
	}

	public void updatedr(){
		for (int i = 1; i <= q; i++){
			drx[i] += xDiff(xcmNew[i], xcm[i]);
			dry[i] += yDiff(ycmNew[i], ycm[i]);
		}
	}

	public double calculateR2(){
		double dr2 = 0.0;
		int count = 0;
		for (int i = 1; i <= q; i++){
			if (area[i] > 0.000001){
				dr2 += mag2(drx[i], dry[i]);
				count++;
			}
		}
		return dr2 / (double) (count);

	}

	//vector methods
	
	//calculate the difference between two points in periodic B.C.
	public double xDiff(double x1, double x2){
		double dx = x1-x2;
		if (dx > (double) nx / 2.0) {
			dx -= nx;
		} else if (dx < (double) -nx / 2.0) {
			dx += nx;
		}
		return dx;
	}

	public double yDiff(double y1, double y2){
		double dy = y1-y2;
		if (dy > (double) ny / 2.0){
			dy -= ny;
		} else if (dy < (double) -ny / 2.0) {
			dy += ny;
		}
		return dy;
	}

	public double mag2(double x, double y){
		return x * x + y * y;
	}

	public double dot(double x1, double y1, double x2, double y2){
		return x1 * x2 + y1 * y2;
	}

	public void writeCM(int time){
		cmWriter.println();
		for (int i = 0; i <= q; i++){
			cmWriter.printf("%.6f %.6f %.6f", (double) time, xcmNew[i], ycmNew[i]);
		}
	}

	public void writeR2(int time, double r2){
		r2Writer.println();
		r2Writer.printf("%d %.6f", time, r2);
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


	//printing methods
	public void printSpins(){
		System.out.println();
		for (int i = 0; i < ny; i++){
			for (int j = 0; j < nx; j++){
				System.out.print(spin[j][i] + " ");
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

	public static void main (String [] args){
		CellPottsModel model = new CellPottsModel();
		model.initSpin();
		model.run(10000);
	}
}
