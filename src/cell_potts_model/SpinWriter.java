package cell_potts_model;

/**
 * Write the lattice configuration after the final MCS to file
 * @author Michael Chiang
 *
 */
public class SpinWriter extends DataWriter {
	
	private int numOfSweeps;
	
	public SpinWriter(int sweeps){
		numOfSweeps = sweeps;
	}
	
	@Override
	public void writeData(CellPottsModel model, int time) {
		if (numOfSweeps-1 == time){
			System.out.println("Writing spin file");
			int nx = model.getNumOfColumns();
			int ny = model.getNumOfRows();
			writer.println(nx + " " + ny);
			for (int i = 0; i < ny; i++){
				for (int j = 0; j < nx; j++){
					writer.print(model.getSpin(j, i) + " ");
				}
				writer.println();
			}
		}
	}

}
