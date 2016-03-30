package cell_potts_model;

import java.awt.*;

import javax.swing.*;

/**
 * PottsView.java
 * 
 * Handle the window operations for the GUI. Main class of the program
 * for simulations with visualisation.
 * 
 * @author Michael Chiang
 *
 */

@SuppressWarnings("serial")
public class PottsView extends JFrame {
	private SpinModel model = new NullModel();
	private PottsViewPanel viewPanel;
	private PottsControlPanel controlPanel;
	
	//constructor
	public PottsView(){
		this.setSize(1000, 1000);
		this.setTitle("Cellular Potts Model");
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setResizable(true);
		
		viewPanel = new PottsViewPanel(model);
		controlPanel = new PottsControlPanel(this);
		
		Container content = this.getContentPane();
		content.add(viewPanel, BorderLayout.CENTER);
		content.add(controlPanel, BorderLayout.SOUTH);
		
		this.setVisible(true);
	}
	
	public static void main (String [] args){
		new PottsView();
	}
	
	/**
	 * Set the model to be display on screen
	 * @param model
	 */
	public void setModel(SpinModel model){
		viewPanel.setModel(model);
	}
	
	/**
	 * Start drawing the model configuration to screen
	 */
	public void initImage(){
		viewPanel.initImage();
	}
	
	/**
	 * Stop drawing the model configuration to screen
	 */
	public void stopDrawingImage(){
		viewPanel.stopDrawingImage();
	}
}
