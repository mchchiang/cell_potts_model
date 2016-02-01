package cell_potts_model;

import java.awt.*;

import javax.swing.*;

@SuppressWarnings("serial")
public class IsingView extends JFrame {
	private SpinModel model = new NullModel();
	private IsingViewPanel viewPanel;
	private IsingControlPanel controlPanel;
	
	public IsingView(){
		this.setSize(1000, 1000);
		this.setTitle("Ising Model");
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setResizable(true);
		
		viewPanel = new IsingViewPanel(model);
		controlPanel = new IsingControlPanel(this);
		
		Container content = this.getContentPane();
		content.add(viewPanel, BorderLayout.CENTER);
		content.add(controlPanel, BorderLayout.SOUTH);
		
		this.setVisible(true);
	}
	
	public static void main (String [] args){
		new IsingView();
	}
	
	public void setModel(SpinModel model){
		viewPanel.setModel(model);
	}
	
	public void initImage(){
		viewPanel.initImage();
	}
	
	public void stopDrawingImage(){
		viewPanel.stopDrawingImage();
	}
}
