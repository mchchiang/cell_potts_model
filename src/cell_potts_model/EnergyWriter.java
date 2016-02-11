package cell_potts_model;

public class EnergyWriter extends DataWriter {

	@Override
	public void writeData(CellPottsModel model, int time) {
		writer.println();		
		writer.printf("%.8f %.8f", (double) time, model.getTotalEnergy());
	}

}
