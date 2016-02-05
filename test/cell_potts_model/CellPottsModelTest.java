package cell_potts_model;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

public class CellPottsModelTest {

	private final double alpha = 2.0;
	private final double beta = 16.0;
	private final double tol = 0.000001;
	private final double temperature = 1;
	private final double lambda = 1;
	private final double motility = 1.0;
	private final int seed = -1;
	
	@Test
	public void testPottsEnergy1(){
		CellPottsModel model = new CellPottsModel();
		assertEquals("Returned wrong energy value for a pair of same spins",
				model.pottsEnergy(3,3), 0.0, tol);
	}
	
	@Test
	public void testPottsEnergy2(){
		CellPottsModel model = new CellPottsModel();
		assertEquals("Returned wrong energy value for a pair that involves q = 0",
				model.pottsEnergy(0,2), beta, tol);
	}
	
	@Test
	public void testPottsEnergy3(){
		CellPottsModel model = new CellPottsModel();
		assertEquals("Returned wrong energy value",
				model.pottsEnergy(1,2), alpha, tol);
	}
	
	@Test
	public void testGetSpin(){
		CellPottsModel model = new CellPottsModel();
		model.setSpin(2, 3, 1);
		assertEquals("Returned wrong value when getting spin",
				1, model.getSpin(2, 3));
	}
	
	@Test
	public void testNegDeltaE1(){
		int [][] spin = new int [][] {
				{1,2,2,1},
				{1,2,3,4},
				{4,3,3,4},
				{1,2,3,4}
		};
		double [] areaTarget = new double [] {4.0, 4.0, 4.0, 4.0, 4.0};
		double lambda = 2;
		CellPottsModel model = new CellPottsModel(4, 4, 4, areaTarget, 
				temperature, lambda, motility, seed);
		
		model.initSpin(spin);
		
		//simulate a switch in spin
		model.setSpin(2, 1, 4);
		
		double negDE = model.negDeltaE(2, 1, 4, 4.0, 4.0, 3.0, 5.0);
		assertEquals("Returned wrong negative delta E value",
				-4.0, negDE, tol);
	}
	
	@Test
	public void testNegDeltaE2(){
		int [][] spin = new int [][] {
				{1,2,2,1},
				{1,2,3,4},
				{4,3,3,4},
				{1,2,3,4}
		};
		double [] areaTarget = new double [] {4.0, 4.0, 4.0, 4.0, 4.0};
		double lambda = 2;
		CellPottsModel model = new CellPottsModel(4, 4, 4, areaTarget, 
				temperature, lambda, motility, seed);
		
		model.initSpin(spin);
		
		//simulate a switch in spin
		model.setSpin(2, 1, 4);
		
		double negDE = model.negDeltaE(2, 1, 4, 4.0, 4.0, 3.0, 5.0);
		assertEquals("Returned wrong negative delta E value",
				-4.0, negDE, tol);
	}
	
	@Test
	public void testCalculateCM1a(){
		/* test for the configuration
				{1,1,1,1}
				{1,2,2,1}
				{1,2,2,1}
				{1,1,1,1}
		*/
		
		CellPottsModel model = new CellPottsModel();
		ArrayList<Integer> xPos = new ArrayList<Integer>();
		xPos.add(1);
		xPos.add(1);
		xPos.add(2);
		xPos.add(2);
		assertEquals("Returned wrong xcm value for spin 2",
				2.5, model.calculateCM(xPos, 4), tol);
		
	}
	
	//same test as testCalculateCM1a but with internal array list
	@Test
	public void testCalculateCM1b(){
		int [][] spin = new int [][]{
				{1,1,1,1},
				{1,2,2,1},
				{1,2,2,1},
				{1,1,1,1}
		};
		
		double [] areaTarget = new double [] {4.0, 4.0};
		CellPottsModel model = new CellPottsModel(4, 4, 2, areaTarget, 
				temperature, lambda, motility, seed);
		
		model.initSpin(spin);
		assertEquals("Returned wrong xcm value for spin 2",
				2.5, model.calculateCM(model.getSpinXPos(2), 4), tol);
	}
	
	
	@Test
	public void testCalculateCM2a(){
		int [][] spin = new int [][]{
				{1,3,3,1},
				{1,3,2,1},
				{4,2,2,2},
				{4,3,2,4}
		};
		
		double [] areaTarget = new double [] {4.0, 4.0, 4.0, 4.0};
		CellPottsModel model = new CellPottsModel(4, 4, 4, areaTarget, 
				temperature, lambda, motility, seed);
		
		model.initSpin(spin);
		assertEquals("Returned wrong xcm value for spin 3",
				1.0, model.calculateCM(model.getSpinXPos(3), 4), tol);
	}
	
	@Test
	public void testCalculateCM2b(){
		int [][] spin = new int [][]{
				{1,3,3,1},
				{1,3,2,1},
				{4,2,2,2},
				{4,3,2,4}
		};
		
		double [] areaTarget = new double [] {4.0, 4.0, 4.0, 4.0};
		CellPottsModel model = new CellPottsModel(4, 4, 4, areaTarget, 
				temperature, lambda, motility, seed);
		
		model.initSpin(spin);
		assertEquals("Returned wrong ycm value for spin 3",
				2.25, model.calculateCM(model.getSpinYPos(3), 4), tol);
	}

	@Test
	public void testCalculateCM2c(){
		int [][] spin = new int [][]{
				{1,3,3,1},
				{1,3,2,1},
				{4,2,2,2},
				{4,3,2,4}
		};
		
		double [] areaTarget = new double [] {4.0, 4.0, 4.0, 4.0};
		CellPottsModel model = new CellPottsModel(4, 4, 4, areaTarget, 
				temperature, lambda, motility, seed);
		
		model.initSpin(spin);
		assertEquals("Returned wrong xcm value for spin 4",
				11.0/3.0, model.calculateCM(model.getSpinXPos(4), 4), tol);
	}
	
	@Test
	public void testCalculateCM2d(){
		int [][] spin = new int [][]{
				{1,3,3,1},
				{1,3,2,1},
				{4,2,2,2},
				{4,3,2,4}
		};
		
		double [] areaTarget = new double [] {4.0, 4.0, 4.0, 4.0};
		CellPottsModel model = new CellPottsModel(4, 4, 4, areaTarget, 
				temperature, lambda, motility, seed);
		
		model.initSpin(spin);
		assertEquals("Returned wrong ycm value for spin 4",
				2.0/3.0, model.calculateCM(model.getSpinYPos(4), 4), tol);
	}
}
