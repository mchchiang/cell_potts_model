package cell_potts_model;

public class CellPottsModelMeasurements_0206 {
	
	private DataWriter r2Writer = new R2Writer();
	private DataWriter cmWriter = new NullWriter();
	private DataWriter energyWriter = new EnergyWriter();
	private DataWriter [] writers = new DataWriter [3];
	
	private int nx = 100;
	private int ny = 100;
	private int q = 200;
	private double temp = 1.0;
	private double lambda = 1.0;
	private double alpha = 2.0;
	private double beta = 16.0;
	private double motility = 0.0;
	private int seed = -1;
	
	private CellPottsModel model;
	
	public CellPottsModelMeasurements_0206(){		
		writers[0] = cmWriter;
		writers[1] = r2Writer;
		writers[2] = energyWriter;	
		
		alpha = 0.1;
		double inc = 0.1;
		double maxAlpha = 3.0;
		int numOfRepeats = 10;
		int numOfSweeps = 6000;
		int nequil = 1000;
		
		while (alpha <= maxAlpha){
			for (int i = 0; i < numOfRepeats; i++){
				System.out.println("Running: a = " + alpha + "\ttrial " + (i+1));
				cmWriter.openWriter(String.format("cm_%d_%d_a_%.1f_P_%.1f_run_%d.dat",
						nx, ny, alpha, motility, (i+1)));
				r2Writer.openWriter(String.format("r2_%d_%d_a_%.1f_P_%.1f_run_%d.dat",
						nx, ny, alpha, motility, (i+1)));
				energyWriter.openWriter(String.format("energy_%d_%d_a_%.1f_P_%.1f_run_%d.dat",
						nx, ny, alpha, motility, (i+1)));
				model = new CellPottsModel(
						nx, ny, q, temp, lambda, alpha, beta, motility, seed,
						writers);
				model.initSpin();
				model.run(numOfSweeps, nequil);
				cmWriter.closeWriter();
				r2Writer.closeWriter();
				energyWriter.closeWriter();
			}
			alpha = alpha + inc;
		}
	}	
	
	public static void main (String [] args){
		new CellPottsModelMeasurements_0206();
	}
}
