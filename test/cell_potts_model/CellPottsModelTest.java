package cell_potts_model;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

public class CellPottsModelTest {
	private final int nx = 4;
	private final int ny = 4;
	private final int q = 4;
	private final double alpha = 2.0;
	private final double beta = 16.0;
	private final double tol = 0.000001;
	private final double temperature = 1;
	private final double lambda = 1;
	private final double motility = 1.0;
	private final int seed = -1;
	private final int numOfSweeps = 0;
	private final int nequil = 0;
	
	@Test
	public void testPottsEnergy1(){
		CellPottsModel model = new CellPottsModel(
				nx, ny, q, temperature, lambda, alpha, beta, motility, seed,
				numOfSweeps, nequil, new DataWriter [] {new NullWriter()}, false);
		assertEquals("Returned wrong energy value for a pair of same spins",
				model.pottsEnergy(3,3), 0.0, tol);
	}
	
	@Test
	public void testPottsEnergy2(){
		CellPottsModel model = new CellPottsModel(
				nx, ny, q, temperature, lambda, alpha, beta, motility, seed,
				numOfSweeps, nequil, new DataWriter [] {new NullWriter()}, false);
		assertEquals("Returned wrong energy value for a pair that involves q = 0",
				model.pottsEnergy(0,2), beta, tol);
	}
	
	@Test
	public void testPottsEnergy3(){
		CellPottsModel model = new CellPottsModel(
				nx, ny, q, temperature, lambda, alpha, beta, motility, seed,
				numOfSweeps, nequil, new DataWriter [] {new NullWriter()}, false);
		assertEquals("Returned wrong energy value",
				model.pottsEnergy(1,2), alpha, tol);
	}
	
	@Test
	public void testGetSpin(){
		CellPottsModel model = new CellPottsModel(
				nx, ny, q, temperature, lambda, alpha, beta, motility, seed,
				numOfSweeps, nequil, new DataWriter [] {new NullWriter()}, false);
		model.initSpin();
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
				temperature, lambda, alpha, beta, motility, seed);
		
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
				temperature, lambda, alpha, beta, motility, seed);
		
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
		
		CellPottsModel model = new CellPottsModel(
				nx, ny, q, temperature, lambda, alpha, beta, motility, seed,
				numOfSweeps, nequil, new DataWriter [] {new NullWriter()}, false);
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
		CellPottsModel model = new CellPottsModel(4, 4, 4, areaTarget, 
				temperature, lambda, alpha, beta, motility, seed);
		
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
				temperature, lambda, alpha, beta, motility, seed);
		
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
				temperature, lambda, alpha, beta, motility, seed);
		
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
				temperature, lambda, alpha, beta, motility, seed);
		
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
				temperature, lambda, alpha, beta, motility, seed);
		
		model.initSpin(spin);
		assertEquals("Returned wrong ycm value for spin 4",
				2.0/3.0, model.calculateCM(model.getSpinYPos(4), 4), tol);
	}
	
	@Test
	public void testCalculateDeltaCM1a(){
		int [][] spin = new int [][]{
				{1,3,3,1},
				{1,3,2,1},
				{4,2,2,2},
				{4,3,2,4}
		};
		
		double [] areaTarget = new double [] {4.0, 4.0, 4.0, 4.0};
		CellPottsModel model = new CellPottsModel(4, 4, 4, areaTarget, 
				temperature, lambda, alpha, beta, motility, seed);
		
		model.initSpin(spin);
		
		assertEquals("Returned wrong delta xcm value",
				17.0/6.0-3.0, model.calculateDeltaCM(1, 1, 2, false)[0], tol);
	}
	
	@Test
	public void testCalculateDeltaCM1b(){
		int [][] spin = new int [][]{
				{1,3,3,1},
				{1,3,2,1},
				{4,2,2,2},
				{4,3,2,4}
		};
		
		double [] areaTarget = new double [] {4.0, 4.0, 4.0, 4.0};
		CellPottsModel model = new CellPottsModel(4, 4, 4, areaTarget, 
				temperature, lambda, alpha, beta, motility, seed);
		
		model.initSpin(spin);
		
		assertEquals("Returned wrong delta ycm value",
				17.0/6.0-3.0, model.calculateDeltaCM(1, 1, 2, false)[1], tol);
	}
	
	@Test
	public void testCalculateDeltaCM2a(){
		int [][] spin = new int [][]{
				{1,3,3,1},
				{1,3,2,1},
				{4,2,2,2},
				{4,3,2,4}
		};
		
		double [] areaTarget = new double [] {4.0, 4.0, 4.0, 4.0};
		CellPottsModel model = new CellPottsModel(4, 4, 4, areaTarget, 
				temperature, lambda, alpha, beta, motility, seed);
		
		model.initSpin(spin);
		
		assertEquals("Returned wrong delta xcm value",
				-1.0/3.0, model.calculateDeltaCM(1, 1, 3, true)[0], tol);
	}
	
	@Test
	public void testCalculateDeltaCM2b(){
		int [][] spin = new int [][]{
				{1,3,3,1},
				{1,3,2,1},
				{4,2,2,2},
				{4,3,2,4}
		};
		
		double [] areaTarget = new double [] {4.0, 4.0, 4.0, 4.0};
		CellPottsModel model = new CellPottsModel(4, 4, 4, areaTarget, 
				temperature, lambda, alpha, beta, motility, seed);
		
		model.initSpin(spin);
		
		assertEquals("Returned wrong delta ycm value",
				7.0/3.0-2.25, model.calculateDeltaCM(1, 1, 3, true)[1], tol);
	}
	
	@Test
	public void testHasSameNeighbours1(){
		int [][] spin = new int [][]{
				{3,3,4,4,4,3},
				{3,3,4,4,3,3},
				{1,1,2,2,2,1},
				{1,1,2,2,2,1},
				{1,2,2,2,2,1},
				{3,3,3,4,4,3}
		};
		
		double [] areaTarget = new double [] {4.0, 4.0, 4.0, 4.0};
		CellPottsModel model = new CellPottsModel(6, 6, 4, areaTarget, 
				temperature, lambda, alpha, beta, motility, seed);
		
		model.initSpin(spin);
		
		assertTrue("Expect to have same neighbours", model.hasSameNeighbours(0, 0));	
	}
	
	@Test
	public void testHasSameNeighbours2(){
		int [][] spin = new int [][]{
				{3,3,4,4,4,3},
				{3,3,4,4,3,3},
				{1,1,2,2,2,1},
				{1,1,2,2,2,1},
				{1,2,2,2,2,1},
				{3,3,3,4,4,3}
		};
		
		double [] areaTarget = new double [] {4.0, 4.0, 4.0, 4.0};
		CellPottsModel model = new CellPottsModel(6, 6, 4, areaTarget, 
				temperature, lambda, alpha, beta, motility, seed);
		
		model.initSpin(spin);
		
		assertTrue("Expect to have same neighbours", model.hasSameNeighbours(3, 3));
	}
	
	@Test
	public void testHasSameNeighbours3(){
		int [][] spin = new int [][]{
				{3,3,4,4,4,3},
				{3,3,4,4,3,3},
				{1,1,2,2,2,1},
				{1,1,2,2,2,1},
				{1,2,2,2,2,1},
				{3,3,3,4,4,3}
		};
		
		double [] areaTarget = new double [] {4.0, 4.0, 4.0, 4.0};
		CellPottsModel model = new CellPottsModel(6, 6, 4, areaTarget, 
				temperature, lambda, alpha, beta, motility, seed);
		
		model.initSpin(spin);
		
		assertFalse("Expect to have same neighbours", model.hasSameNeighbours(3, 0));
	}
	
	@Test
	public void testHasSameNeighbours4(){
		int [][] spin = new int [][]{
				{3,3,4,4,4,3},
				{3,3,4,4,3,3},
				{1,1,2,2,2,1},
				{1,1,2,2,2,1},
				{1,2,2,2,2,1},
				{3,3,3,4,4,3}
		};
		
		double [] areaTarget = new double [] {4.0, 4.0, 4.0, 4.0};
		CellPottsModel model = new CellPottsModel(6, 6, 4, areaTarget, 
				temperature, lambda, alpha, beta, motility, seed);
		
		model.initSpin(spin);
		
		assertFalse("Expect to have same neighbours", model.hasSameNeighbours(1, 3));
	}
	
	@Test
	public void testHasSameNeighbours5(){
		int [][] spin = new int [][]{
				{3,3,4,4,4,3},
				{3,3,4,4,3,3},
				{1,1,2,2,2,1},
				{1,1,2,2,2,1},
				{1,2,2,2,2,1},
				{3,3,3,4,4,3}
		};
		
		double [] areaTarget = new double [] {4.0, 4.0, 4.0, 4.0};
		CellPottsModel model = new CellPottsModel(6, 6, 4, areaTarget, 
				temperature, lambda, alpha, beta, motility, seed);
		
		model.initSpin(spin);
		
		assertFalse("Expect to have same neighbours", model.hasSameNeighbours(4, 2));
	}
	
	
	@Test
	public void testXDiff1(){
		CellPottsModel model = new CellPottsModel(
				7, 7, q, temperature, lambda, alpha, beta, motility, seed,
				numOfSweeps, nequil, new DataWriter [] {new NullWriter()}, false);
		model.initSpin();
		
		assertEquals("Return wrong xDiff value",
				-2.0 , model.xDiff(2, 4), tol);
	}
	
	@Test
	public void testXDiff2(){
		CellPottsModel model = new CellPottsModel(
				7, 7, q, temperature, lambda, alpha, beta, motility, seed,
				numOfSweeps, nequil, new DataWriter [] {new NullWriter()}, false);
		model.initSpin();
		
		assertEquals("Return wrong xDiff value",
				3.0 , model.xDiff(5, 2), tol);
	}
	
	//boundary cases
	@Test
	public void testXDiff3(){
		CellPottsModel model = new CellPottsModel(
				7, 7, q, temperature, lambda, alpha, beta, motility, seed,
				numOfSweeps, nequil, new DataWriter [] {new NullWriter()}, false);
		model.initSpin();
		
		assertEquals("Return wrong xDiff value",
				3.0 , model.xDiff(2, 6), tol);
	}
	
	@Test
	public void testXDiff4(){
		CellPottsModel model = new CellPottsModel(
				7, 7, q, temperature, lambda, alpha, beta, motility, seed,
				numOfSweeps, nequil, new DataWriter [] {new NullWriter()}, false);
		model.initSpin();
		
		assertEquals("Return wrong xDiff value",
				-2.0 , model.xDiff(6, 1), tol);
	}
	
	@Test
	public void testYDiff1(){
		CellPottsModel model = new CellPottsModel(
				7, 7, q, temperature, lambda, alpha, beta, motility, seed,
				numOfSweeps, nequil, new DataWriter [] {new NullWriter()}, false);
		model.initSpin();
		
		assertEquals("Return wrong xDiff value",
				-2.0 , model.yDiff(2, 4), tol);
	}
	
	@Test
	public void testYDiff2(){
		CellPottsModel model = new CellPottsModel(
				7, 7, q, temperature, lambda, alpha, beta, motility, seed,
				numOfSweeps, nequil, new DataWriter [] {new NullWriter()}, false);
		model.initSpin();
		
		assertEquals("Return wrong xDiff value",
				3.0 , model.yDiff(5, 2), tol);
	}
	
	//boundary cases
	@Test
	public void testYDiff3(){
		CellPottsModel model = new CellPottsModel(
				7, 7, q, temperature, lambda, alpha, beta, motility, seed,
				numOfSweeps, nequil, new DataWriter [] {new NullWriter()}, false);
		model.initSpin();
		
		assertEquals("Return wrong xDiff value",
				3.0 , model.xDiff(2, 6), tol);
	}
	
	@Test
	public void testYDiff4(){
		CellPottsModel model = new CellPottsModel(
				7, 7, q, temperature, lambda, alpha, beta, motility, seed,
				numOfSweeps, nequil, new DataWriter [] {new NullWriter()}, false);
		model.initSpin();
		
		assertEquals("Return wrong xDiff value",
				-2.0 , model.yDiff(6, 1), tol);
	}
	
	@Test
	public void testDot1(){
		CellPottsModel model = new CellPottsModel(
				nx, ny, q, temperature, lambda, alpha, beta, motility, seed,
				numOfSweeps, nequil, new DataWriter [] {new NullWriter()}, false);
		assertEquals("Return wrong dot product",
				0.0, model.dot(2.0, 0.0, 0.0, 7.0), tol);
	}
	
	@Test
	public void testDot2(){
		CellPottsModel model = new CellPottsModel(
				nx, ny, q, temperature, lambda, alpha, beta, motility, seed,
				numOfSweeps, nequil, new DataWriter [] {new NullWriter()}, false);
		assertEquals("Return wrong dot product",
				154.0, model.dot(15.0, 2.0, 8.0, 17.0), tol);
	}
	
	@Test
	public void testMag2_1(){
		CellPottsModel model = new CellPottsModel(
				nx, ny, q, temperature, lambda, alpha, beta, motility, seed,
				numOfSweeps, nequil, new DataWriter [] {new NullWriter()}, false);
		assertEquals("Return wrong magnitude squared value",
				0.0, model.mag2(0.0, 0.0), tol);
	}
	
	@Test
	public void testMag2_2(){
		CellPottsModel model = new CellPottsModel(
				nx, ny, q, temperature, lambda, alpha, beta, motility, seed,
				numOfSweeps, nequil, new DataWriter [] {new NullWriter()}, false);
		assertEquals("Return wrong magnitude squared value",
				25.0, model.mag2(3.0, 4.0), tol);
	}
	
	@Test
	public void testMag2_3(){
		CellPottsModel model = new CellPottsModel(
				nx, ny, q, temperature, lambda, alpha, beta, motility, seed,
				numOfSweeps, nequil, new DataWriter [] {new NullWriter()}, false);
		assertEquals("Return wrong magnitude squared value",
				169.0, model.mag2(5.0, 12.0), tol);
	}
	
	
}
