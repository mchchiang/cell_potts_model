package cell_potts_model;

public class CellPottsModelMeasurements_0211 {
	
	public CellPottsModelMeasurements_0211(){		
		
		int nx = 100;
		int ny = 100;
		int q = 200;
		double temp = 1.0;
		double lambda = 1.0;
		double alpha = 0.5;
		double beta = 16.0;
		double motility = 0.0;
		double rotateDiff = 0.1;
		int seed = -1;
		
		double inc = 0.5;
		double maxAlpha = 7.0;
		int numOfRepeats = 10;
		int numOfSweeps = 100000;
		int nequil = 10000;
		
		SpinReader reader = new SpinReader();
		reader.openReader("init_spin.dat");
		int [][] spin = reader.readSpins();
		
		/*for (int i = 0; i < ny; i++){
			for (int j = 0; j < nx; j++){
				System.out.print(spin[j][i] + " ");
			}
			System.out.println();
		}*/
		
		DataWriter r2Writer = new R2Writer();
		DataWriter cmWriter = new CMWriter();
		DataWriter energyWriter = new EnergyWriter();
		DataWriter statsWriter = new StatisticsWriter(numOfSweeps, nequil);
		
		DataWriter [] writers = new DataWriter [4];		
		writers[0] = cmWriter;
		writers[1] = r2Writer;
		writers[2] = energyWriter;	
		writers[3] = statsWriter;
		
		while (alpha <= maxAlpha){
			for (int i = 0; i < numOfRepeats; i++){
				System.out.println("Running: a = " + alpha + "\ttrial " + (i+1));
				String filename = String.format("%d_%d_%d_a_%.1f_lam_%.1f_P_%.1f_t_%d_run_%d.dat",
						nx, ny, q, alpha, lambda, motility, numOfSweeps, (i+1));
				if (i == 0){
					cmWriter.openWriter("cm_" + filename);
				}
				r2Writer.openWriter("r2_" + filename);
				energyWriter.openWriter("energy_" + filename);
				statsWriter.openWriter("stats_" + filename);
				CellPottsModel model = new CellPottsModel(
						nx, ny, q, temp, lambda, alpha, beta, motility, 
						rotateDiff, seed, numOfSweeps, nequil, writers, false);
				model.initSpin(spin);
				model.run();
				
				if (i == 0){
					cmWriter.closeWriter();
				}
				r2Writer.closeWriter();
				energyWriter.closeWriter();
				statsWriter.closeWriter();
			}
			alpha = alpha + inc;
		}
	}	
	
	public static void main (String [] args){
		new CellPottsModelMeasurements_0211();
	}
}
