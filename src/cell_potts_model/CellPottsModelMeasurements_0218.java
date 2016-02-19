package cell_potts_model;

import java.util.ArrayList;

public class CellPottsModelMeasurements_0218 implements ThreadCompleteListener {
	
	private int trial = 1;
	private int numOfThreads = 10;
	private ArrayList<CellPottsModel> models;
	private int nx = 100;
	private int ny = 100;
	private int q = 200;
	private double temp = 1.0;
	private double lambda = 1.0;
	private double alpha = 0.5;
	private double beta = 16.0;
	private double motility = 0.0;
	private int seed = -1;
	
	private double inc = 0.5;
	private double maxAlpha = 7.0;
	private int numOfRepeats = 10;
	private int numOfSweeps = 10000;
	private int nequil = 1000;
	
	private int [][] spin;
	
	private DataWriter [][] writers;
	
	public CellPottsModelMeasurements_0218(){		
		
		SpinReader reader = new SpinReader();
		reader.openReader("init_spin.dat");
		spin = reader.readSpins();
		
		writers = new DataWriter [numOfThreads][4];		
		
		models = new ArrayList<CellPottsModel>();
		
		for (int i = 0; i < numOfThreads; i++){		
			writers[i][0] = new NullWriter();
			writers[i][1] = new R2Writer();
			writers[i][2] = new EnergyWriter();	
			writers[i][3] = new StatisticsWriter(numOfSweeps, nequil);
			
			openWriters(i);
			
			CellPottsModel model = new CellPottsModel(
					nx, ny, q, temp, lambda, alpha, beta, motility, seed,
					numOfSweeps, nequil, writers[i]);
			model.initSpin(spin);
			model.addThreadCompleteListener(this);
			models.add(model);
			
			runNewThread(i);
			if (trial < 10){
				trial++;
			} else {
				trial = 1;
				alpha = alpha + inc;
			}
		}
	}	
	
	public void openWriters(int index){
		writers[index][0].openWriter("cm_" + getOutputFileName());
		writers[index][1].openWriter("r2_" + getOutputFileName());
		writers[index][2].openWriter("energy_" + getOutputFileName());
		writers[index][3].openWriter("stats_" + getOutputFileName());
	}
	
	public void closeWriters(int index){
		for (int i = 0; i < 4; i++){
			writers[index][i].closeWriter();
		}
	}
	
	public String getOutputFileName(){
		return String.format("%d_%d_%d_a_%.1f_lam_%.1f_P_%.1f_t_%d_run_%d.dat",
				nx, ny, q, alpha, lambda, motility, numOfSweeps, trial);
	}
	
	public void runNewThread(int index){
		Thread t = new Thread(models.get(index));
		t.start();
		System.out.println("Running: a = " + alpha + "\ttrial " + trial);
	}
	
	
	@Override
	public void notifyThreadComplete(Runnable r) {
		CellPottsModel model = (CellPottsModel) r;
		int index = models.indexOf(model);	
		closeWriters(index);
		
		model.setAlpha(alpha);
		model.initSpin(spin);
		
		openWriters(index);
		
		runNewThread(index);
		
		if (trial < 10){
			trial++;
		} else {
			trial = 1;
			alpha = alpha + inc;
		}
	}
	
	public static void main (String [] args){
		new CellPottsModelMeasurements_0218();
	}
}
