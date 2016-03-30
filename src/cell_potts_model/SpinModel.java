package cell_potts_model;

import java.util.Observable;

/**
 * SpinModel.java
 * 
 * An interface which specifies the basic behaviours of a lattice
 * model. The visualisation classes access the model via this interface.
 * 
 * @author Michael Chiang
 *
 */

public abstract class SpinModel extends Observable {
	
	/**
	 * Return the number of rows in the lattice
	 */
	public abstract int getNumOfRows();
	
	/**
	 * Return the number of columns in the lattice
	 */
	public abstract int getNumOfColumns();
	
	/**
	 * Set the temperature of the model
	 */
	public abstract void setTemp(double t);
	
	/**
	 * Get the temperature of the model
	 */
	public abstract double getTemp();
	
	/**
	 * Get the total possible number of spin values in the model
	 */
	public abstract int getTypesOfSpin();
	
	/**
	 * Get the spin value at a particular lattice site
	 * @param i column index of the site
	 * @param j row index of the site
	 */
	public abstract int getSpin(int i, int j);
	
	/**
	 * Set the spin value at a particular lattice site
	 * @param i column index of the site 
	 * @param j row index of the site
	 * @param value new value for the site
	 */
	public abstract void setSpin(int i, int j, int value);
	
	/**
	 * Get the total energy of the current lattice configuration
	 */
	public abstract double getTotalEnergy();
	
	/**
	 * Get the total sum of the spin values
	 */
	public abstract double getTotalSpin();
}
