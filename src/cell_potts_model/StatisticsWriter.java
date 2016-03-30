package cell_potts_model;

/**
 * StatisticsWriter.java
 * 
 * Write the parameters used in the simulation and other overall statistics
 * of the simulation (e.g. the acceptance rate and the number of cells alive)
 * to file
 * 
 * @author Michael Chiang
 *
 */
public class StatisticsWriter extends DataWriter {
	
	private int numOfSweeps; 
	private int nequil;
	
	/**
	 * Initialise the stats writer
	 * @param numOfSweeps number of sweeps or MCS for the simulation
	 * @param nequil number of MCS to take before measurement begins
	 */
	public StatisticsWriter(int numOfSweeps, int nequil){
		this.numOfSweeps = numOfSweeps;
		this.nequil = nequil;
	}
	
	@Override
	public void writeData(CellPottsModel model, int time) {
		if (time == numOfSweeps-1){
			writer.printf("nx %d\n", model.getNumOfRows());
			writer.printf("ny %d\n", model.getNumOfColumns());
			writer.printf("q %d\n", model.getTypesOfSpin());
			writer.printf("alpha %.1f\n", model.getAlpha());
			writer.printf("beta %.1f\n", model.getBeta());
			writer.printf("lambda %.1f\n", model.getLambda());
			writer.printf("temperature %.1f\n", model.getTemp());
			writer.printf("motility %.1f\n", model.getMotility());
			writer.printf("rotate_diffusion %.1f\n", model.getRotateDiff());
			writer.printf("accept_rate %.8f\n", model.getAcceptRate());
			writer.printf("num_of_sweeps %d\n", numOfSweeps);
			writer.printf("nequil %d\n", nequil);
			writer.printf("cells_alive %d\n", model.getNumOfCellsAlive());
		}
	}

}
