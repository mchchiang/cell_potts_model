package cell_potts_model;

public class CPMVaryAlphaMeasurements implements ThreadCompleteListener {
	
	private int nx, ny, q;
	private double temp, lambda, alpha, beta, motility, rotateDiff;
	
	private double inc, maxAlpha;
	private int numOfSweeps, nequil;
	
	private int trial = 1;
	private int maxTrial = 1;
	
	private String outputFilePath;
	
	private int [][] spin;
	
	private boolean completeAllTrials = false;
	
	public CPMVaryAlphaMeasurements(
			int nx, int ny, int q,
			double temp, double lambda,
			double startAlpha, double maxAlpha, double alphaInc,
			double beta, double motility, double rotateDiff, 
			int n, int nequil, int maxTrial, int numOfThreads,
			String spinFile, String filepath){	
		
		this.nx = nx;
		this.ny = ny;
		this.q = q;
		this.temp = temp;
		this.lambda = lambda;
		this.beta = beta;
		this.motility = motility;
		this.rotateDiff = rotateDiff;
		this.alpha = startAlpha;
		this.maxAlpha = maxAlpha;
		this.inc = alphaInc;
		this.numOfSweeps = n;
		this.nequil = nequil;
		this.maxTrial = maxTrial;
		this.outputFilePath = filepath;
		
		SpinReader reader = new SpinReader();
		reader.openReader(spinFile);
		spin = reader.readSpins();
		
		for (int i = 0; i < numOfThreads; i++){
			if (completeAllTrials){
				break;
			}
			runNewThread();
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
	
	public void runNewThread(){
		boolean writeCM = false;
		if (trial == 1) writeCM = true;
		Measurement experiment = new Measurement(nx, ny, q, temp, lambda, 
				alpha, beta, motility, rotateDiff, numOfSweeps, nequil, trial, 
				spin, outputFilePath, writeCM);
		experiment.addThreadCompleteListener(this);
		Thread t = new Thread(experiment);
		t.start();
	}
	
	
	@Override
	public synchronized void notifyThreadComplete(Runnable r) {		
		if (!completeAllTrials){
			runNewThread();
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
		double rotateDiff = Double.parseDouble(args[10]);
		int numOfSweeps = Integer.parseInt(args[11]);
		int nequil = Integer.parseInt(args[12]);
		int maxTrial = Integer.parseInt(args[13]);
		int numOfThreads = Integer.parseInt(args[14]);
		String spinFile = args[15];
		String outputFilePath = args[16];
		new CPMVaryAlphaMeasurements(nx, ny, q, temp, lambda,
				startAlpha, maxAlpha, inc, beta, motility,
				rotateDiff, numOfSweeps, nequil, maxTrial, numOfThreads,
				spinFile, outputFilePath);
	}
}
