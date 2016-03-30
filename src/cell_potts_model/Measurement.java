package cell_potts_model;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Measurement.java
 * 
 * Run a single trial for a particular set of parameters in the experiment. It
 * can be run in a separate thread for parallel computation
 * 
 * @author MichaelChiang
 *
 */

public class Measurement implements Runnable {
	
	private int trial;
	
	private DataWriter [] writers;
	
	private CellPottsModel model;
	
	private ArrayList<ThreadCompleteListener> threadListeners = 
			new ArrayList<ThreadCompleteListener>();

	/**
	 * Initialise the simulation trial
	 * @param nx number of columns in the lattice
	 * @param ny number of rows in the lattice
	 * @param q number of cells
	 * @param temp effective temperature
	 * @param lambda strength on area constraint
	 * @param alpha interfacial energy between cells
	 * @param beta free boundary energy
	 * @param motility cell motility strength (P)
	 * @param rotateDiff rotational diffusion coefficient
	 * @param n number of Monte-Carlo steps (MCS) to take in the simulation
	 * @param nequil number of MCS to take before making measurements
	 * @param trial trial number
	 * @param spin initial condition of the lattice
	 * @param filepath output file directory
	 * @param writeCM whether or not to write centre of mass data to file
	 */
	public Measurement(int nx, int ny, int q,
			double temp, double lambda,
			double alpha, double beta, double motility, double rotateDiff,
			int n, int nequil, int trial, int [][] spin, String filepath, 
			boolean writeCM){
		
		this.trial = trial;
		
		writers = new DataWriter [5];
		if (writeCM){
			writers[0] = new CMWriter();
		} else {
			writers[0] = new NullWriter();
		}
		writers[1] = new R2Writer();
		writers[2] = new A2Writer();
		writers[3] = new EnergyWriter();
		writers[4] = new StatisticsWriter(n, nequil);
		
		String name = String.format(
				"%d_%d_%d_a_%.1f_lam_%.1f_P_%.1f_D_%.1f_t_%d_run_%d.dat",
				nx, ny, q, alpha, lambda, motility, rotateDiff, n, trial);
		
		writers[0].openWriter(Paths.get(filepath, "cm_" + name).toString());
		writers[1].openWriter(Paths.get(filepath, "r2_" + name).toString());
		writers[2].openWriter(Paths.get(filepath, "a2_" + name).toString());
		writers[3].openWriter(Paths.get(filepath, "energy_" + name).toString());
		writers[4].openWriter(Paths.get(filepath, "stats_" + name).toString());
		
		
		//Initialise the model
		model = new CellPottsModel(nx, ny, q, temp, lambda, 
				alpha, beta, motility, rotateDiff, -1, n, nequil, writers, false);
		model.initSpin(spin);
		model.initPolarity();
	}

	@Override
	public void run() {
		System.out.printf(Calendar.getInstance().getTime() + 
				"\t - Running: a = %.1f\tp = %.1f\ttrial %d\n", 
				model.getAlpha(), model.getMotility(), trial);
		model.run();
		
		for (int i = 0; i < writers.length; i++){
			writers[i].closeWriter();
		}
		
		notifyThreadCompleteListener();
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
