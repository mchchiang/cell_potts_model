package cell_potts_model;

public class CellPottsModelMeasurements_0218 {
	
	public CellPottsModelMeasurements_0218(){		
		
		int nx = 100;
		int ny = 100;
		int q = 200;
		double temp = 1.0;
		double lambda = 1.0;
		double alpha = 0.5;
		double beta = 16.0;
		double motility = 0.0;
		int seed = -1;
		
		double inc = 0.5;
		double maxAlpha = 7.0;
		int numOfRepeats = 10;
		int numOfSweeps = 100000;
		int nequil = 10000;
		
		SpinReader reader = new SpinReader();
		reader.openReader("init_spin.dat");
		int [][] spin = reader.readSpins();
		
		DataWriter r2Writer = new R2Writer();
		DataWriter cmWriter = new CMWriter();
		DataWriter energyWriter = new EnergyWriter();
		DataWriter statsWriter = new StatisticsWriter(numOfSweeps, nequil);
		
		DataWriter [] writers = new DataWriter [4];		
		writers[0] = cmWriter;
		writers[1] = r2Writer;
		writers[2] = energyWriter;	
		writers[3] = statsWriter;
		
		CellPottsModel model = new CellPottsModel(
				nx, ny, q, temp, lambda, alpha, beta, motility, seed,
				numOfSweeps, nequil, writers);
		
		Thread t = new Thread(model);
		t.start();
	}	
	
	public static void main (String [] args){
		new CellPottsModelMeasurements_0218();
	}
}
