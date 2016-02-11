package cell_potts_model;

import java.util.Observable;

public abstract class SpinModel extends Observable {
	public abstract int getNumOfRows();
	public abstract int getNumOfColumns();
	public abstract void setTemp(double t);
	public abstract double getTemp();
	public abstract int getTypesOfSpin();
	public abstract int getSpin(int i, int j);
	public abstract void setSpin(int i, int j, int value);
	public abstract double getTotalEnergy();
	public abstract double getTotalSpin();
}
