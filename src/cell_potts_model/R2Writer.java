package cell_potts_model;

public class R2Writer extends DataWriter {

	@Override
	public void writeData(CellPottsModel model, double time) {
		writer.println();	
		double [] r2Data = model.calculateR2();
		writer.printf("%.8f %.8f %.8f", time, r2Data[0], r2Data[1]);		
	}
}
