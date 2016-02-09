package cell_potts_model;

public class CMWriter extends DataWriter {

	@Override
	public void writeData(CellPottsModel model, double time) {
		writer.println();
		int q = model.getTypesOfSpin();
		writer.printf("%.8f ", time);
		for (int i = 0; i < q; i++){
			writer.printf("%.8f %.8f ", model.getXCM(i), model.getYCM(i));
		}		
	}

}
