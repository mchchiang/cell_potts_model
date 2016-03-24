package cell_potts_model;

/**
 * Write the total energy of the system at each MCS to file
 * @author Michael Chiang
 *
 */
public class EnergyWriter extends DataWriter {

	@Override
	public void writeData(CellPottsModel model, int time) {
		writer.println();		
		writer.printf("%d %.8f", time, model.getTotalEnergy());
	}

}
