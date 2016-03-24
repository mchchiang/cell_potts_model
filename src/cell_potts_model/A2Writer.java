package cell_potts_model;


/**
 * Write the non-Gaussian parameter of the system at each MCS to file
 * @author Michael Chiang
 *
 */
public class A2Writer extends DataWriter {
	
	@Override
	public void openWriter(String filename){
		super.openWriter(filename);
		writer.println("# t alpha2 r4");
	}

	@Override
	public void writeData(CellPottsModel model, int time) {
		double [] a2 = model.alpha2();
		writer.printf("%d %.8f %.8f\n", time, a2[0], a2[1]);
	}

}
