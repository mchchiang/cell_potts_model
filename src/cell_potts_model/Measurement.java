package cell_potts_model;

import java.nio.file.Paths;
import java.util.ArrayList;

public class Measurement implements Runnable {
	
	private int trial;
	
	private DataWriter [] writers;
	
	private CellPottsModel model;
	
	private ArrayList<ThreadCompleteListener> threadListeners = 
			new ArrayList<ThreadCompleteListener>();

	public Measurement(int nx, int ny, int q,
			double temp, double lambda,
			double alpha, double beta, double motility,
			int n, int nequil, int trial, int [][] spin, String filepath, 
			boolean writeCM){
		
		this.trial = trial;
		
		writers = new DataWriter [4];
		if (writeCM){
			writers[0] = new CMWriter();
		} else {
			writers[0] = new NullWriter();
		}
		writers[1] = new R2Writer();
		writers[2] = new EnergyWriter();
		writers[3] = new StatisticsWriter(n, nequil);
		
		String name = String.format("%d_%d_%d_a_%.1f_lam_%.1f_P_%.1f_t_%d_run_%d.dat",
				nx, ny, q, alpha, lambda, motility, n, trial);
		
		writers[0].openWriter(Paths.get(filepath, "cm_" + name).toString());
		writers[1].openWriter(Paths.get(filepath, "r2_" + name).toString());
		writers[2].openWriter(Paths.get(filepath, "energy_" + name).toString());
		writers[3].openWriter(Paths.get(filepath, "stats_" + name).toString());
		
		//initialise the model
		model = new CellPottsModel(nx, ny, q, temp, lambda, 
				alpha, beta, motility, -1, n, nequil, writers, false);
		model.initSpin(spin);
	}

	@Override
	public void run() {
		System.out.println("Running: a = " + model.getAlpha() + "\ttrial " + trial);
		model.run();
		
		for (int i = 0; i < writers.length; i++){
			writers[i].closeWriter();
		}
		
		notifyThreadCompleteListener();
	}

	//for notifying the listeners when the model has finished running
	public void addThreadCompleteListener(ThreadCompleteListener l){
		threadListeners.add(l);
	}

	public void removeThreadCompleteListener(ThreadCompleteListener l){
		threadListeners.remove(l);
	}

	public void notifyThreadCompleteListener(){
		for (ThreadCompleteListener l : threadListeners){
			l.notifyThreadComplete(this);
		}
	}
}
