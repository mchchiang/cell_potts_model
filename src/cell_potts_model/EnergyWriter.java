package cell_potts_model;

public class EnergyWriter extends DataWriter {

	@Override
	public void writeData(CellPottsModel model, double time) {
		writer.println();		
		writer.printf("%.8f %.8f", time, model.getTotalEnergy());
	}

}
