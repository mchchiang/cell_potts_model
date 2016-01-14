package cell_potts_model;

import static org.junit.Assert.*;

import org.junit.Test;

public class CellPottsModelTest {

	private final double alpha = 2.0;
	private final double beta = 16.0;
	private final double tol = 0.000001;
	
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

}
