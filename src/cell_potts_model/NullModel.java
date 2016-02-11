package cell_potts_model;

public class NullModel extends SpinModel {

	@Override
	public int getNumOfRows() {
		return 0;
	}

	@Override
	public int getNumOfColumns() {
		return 0;
	}

	@Override
	public void setTemp(double t) {}

	@Override
	public int getSpin(int i, int j) {
		return 0;
	}

	@Override
	public void setSpin(int i, int j, int value) {}

	@Override
	public double getTotalEnergy() {
		return 0;
	}

	@Override
	public double getTotalSpin() {
		return 0;
	}

	@Override
	public int getTypesOfSpin() {
		return 0;
	}

	@Override
	public double getTemp() {
		return 0;
	}
	
}
