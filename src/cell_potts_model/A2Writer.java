package cell_potts_model;

public class A2Writer extends DataWriter {
	
	@Override
	public void openWriter(String filename){
		super.openWriter(filename);
		writer.println("# t alpha2 r4 r4_error");
	}

	@Override
	public void writeData(CellPottsModel model, int time) {
		double [] a2 = model.alpha2();
		writer.printf("%d %.8f %.8f %.8f\n", time, a2[0], a2[1], a2[2]);
	}

}
