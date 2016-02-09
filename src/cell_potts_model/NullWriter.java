package cell_potts_model;

public class NullWriter extends DataWriter {
	@Override
	public void openWriter(String filename){}
	
	@Override
	public void closeWriter(){}
	
	@Override
	public void writeData(CellPottsModel model, double time) {}

}
