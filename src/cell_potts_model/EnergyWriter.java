package cell_potts_model;

public class EnergyWriter extends DataWriter {

	@Override
	public void writeData(CellPottsModel model, int time) {
		writer.println();		
		writer.printf("%d %.8f", time, model.getTotalEnergy());
	}

}
