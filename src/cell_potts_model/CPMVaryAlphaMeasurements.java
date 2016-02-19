package cell_potts_model;

import java.util.ArrayList;
import java.nio.file.Paths;

public class CPMVaryAlphaMeasurements implements ThreadCompleteListener {
	
	private ArrayList<CellPottsModel> models;
	private int nx, ny, q;
	private double temp, lambda, alpha, beta, motility;
	private int seed = -1;
	
	private double inc, maxAlpha;
	private int numOfSweeps, nequil;
	
	private int trial = 1;
	private int maxTrial = 1;
	private int numOfThreads = 1;
	
	private String outputFilePath;
	
	private int [][] spin;
	
	private DataWriter [][] writers;
	private DataWriter nullWriter = new NullWriter();
	private DataWriter cmWriter = new CMWriter();
	
	private boolean completeAllTrials = false;
	
	public CPMVaryAlphaMeasurements(
			int nx, int ny, int q,
			double temp, double lambda,
			double startAlpha, double maxAlpha, double alphaInc,
			double beta, double motility,
			int n, int nequil, int maxTrial, int numOfThreads,
			String spinFile, String filepath){	
		
		this.nx = nx;
		this.ny = ny;
		this.q = q;
		this.temp = temp;
		this.lambda = lambda;
		this.beta = beta;
		this.motility = motility;
		this.alpha = startAlpha;
		this.maxAlpha = maxAlpha;
		this.inc = alphaInc;
		this.numOfSweeps = n;
		this.nequil = nequil;
		this.maxTrial = maxTrial;
		this.numOfThreads = numOfThreads;
		this.outputFilePath = filepath;
		
		SpinReader reader = new SpinReader();
		reader.openReader(spinFile);
		spin = reader.readSpins();
		
		if (spin.length != nx || spin[0].length != ny){
			throw new IllegalArgumentException(
					"Inconsistent spin data dimension. Expect " + nx + " x " + 
					ny + " but was " + spin.length + " x " + spin[0].length);
		}
		
		writers = new DataWriter [numOfThreads][4];		
		
		models = new ArrayList<CellPottsModel>();
		
		for (int i = 0; i < numOfThreads; i++){		
			if (trial == 1){
				writers[i][0] = new CMWriter();
			} else {
				writers[i][0] = nullWriter;
			}
			writers[i][1] = new R2Writer();
			writers[i][2] = new EnergyWriter();	
			writers[i][3] = new StatisticsWriter(numOfSweeps, nequil);
			
			openWriters(i);
			
			CellPottsModel model = new CellPottsModel(
					nx, ny, q, temp, lambda, alpha, beta, motility, seed,
					numOfSweeps, nequil, writers[i]);
			model.initSpin();
			model.addThreadCompleteListener(this);
			models.add(model);
			
			runNewThread(i);
			if (trial < maxTrial){
				trial++;
			} else {
				trial = 1;
				alpha = alpha + inc;
			}
		}
	}	
	
	public void openWriters(int index){
		String name = getOutputFileName();
		writers[index][0].openWriter(Paths.get(outputFilePath, "cm_" + name).toString());
		writers[index][1].openWriter(Paths.get(outputFilePath, "r2_" + name).toString());
		writers[index][2].openWriter(Paths.get(outputFilePath, "energy_" + name).toString());
		writers[index][3].openWriter(Paths.get(outputFilePath, "stats_" + name).toString());
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
		if (!completeAllTrials){
			CellPottsModel model = (CellPottsModel) r;
			int index = models.indexOf(model);	
			closeWriters(index);
			
			model.setAlpha(alpha);
			model.initSpin();
			
			if (trial == 1){
				writers[index][0] = cmWriter;
			} else {
				writers[index][0] = nullWriter;
			}
			
			openWriters(index);
			
			runNewThread(index);
			
			if (trial < maxTrial){
				trial++;
			} else {
				trial = 1;
				alpha = alpha + inc;
				if (alpha > maxAlpha){
					completeAllTrials = true;
				}
			}
		}
	}
	
	public static void main (String [] args){
		int nx = Integer.parseInt(args[0]);
		int ny = Integer.parseInt(args[1]);
		int q = Integer.parseInt(args[2]);
		double temp = Double.parseDouble(args[3]);
		double lambda = Double.parseDouble(args[4]);
		double startAlpha = Double.parseDouble(args[5]);
		double maxAlpha = Double.parseDouble(args[6]);
		double inc = Double.parseDouble(args[7]);
		double beta = Double.parseDouble(args[8]);
		double motility = Double.parseDouble(args[9]);
		int numOfSweeps = Integer.parseInt(args[10]);
		int nequil = Integer.parseInt(args[11]);
		int maxTrial = Integer.parseInt(args[12]);
		int numOfThreads = Integer.parseInt(args[13]);
		String spinFile = args[14];
		String outputFilePath = args[15];
		new CPMVaryAlphaMeasurements(nx, ny, q, temp, lambda,
				startAlpha, maxAlpha, inc, beta, motility,
				numOfSweeps, nequil, maxTrial, numOfThreads,
				spinFile, outputFilePath);
	}
}
