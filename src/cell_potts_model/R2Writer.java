package cell_potts_model;

/**
 * Write the mean square displacement of the cells (from their initial
 * positions) at each MCS to file
 * 
 * @author Michael Chiang
 *
 */
public class R2Writer extends DataWriter {
	
	@Override
	public void openWriter(String filename){
		super.openWriter(filename);
		writer.println("# time, r2, r2_error");
	}
	
	@Override
	public void writeData(CellPottsModel model, int time) {
		writer.println();	
		double [] r2Data = model.calculateR2();
		writer.printf("%d %.8f %.8f", time, r2Data[0], r2Data[1]);		
	}
}
