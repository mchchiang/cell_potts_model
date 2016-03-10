package cell_potts_model;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

@SuppressWarnings("serial")
public class PottsControlPanel extends JPanel implements ActionListener {
	private JPanel simParamsPanel;
	private JPanel modelParamsPanel;
	
	private JLabel lblWidth;
	private JTextField txtWidth;
	
	private JLabel lblHeight;
	private JTextField txtHeight;
	
	private JLabel lblAlpha;
	private JTextField txtAlpha;
	
	private JLabel lblMotility;
	private JTextField txtMotility;
	
	private JLabel lblRotateDiff;
	private JTextField txtRotateDiff;
	
	private JLabel lblLambda;
	private JTextField txtLambda;
	
	private JLabel lblNumOfSteps;
	private JTextField txtNumOfSteps;
	
	private JButton btnRun;
	
	private PottsView view;
	
	public PottsControlPanel(PottsView view){
		this.view = view;
		
		lblWidth = new JLabel("Width: ");
		txtWidth = new JTextField(3);
		
		lblHeight = new JLabel("Height: ");
		txtHeight = new JTextField(3);
		
		txtLambda = new JTextField(3);
		lblLambda = new JLabel("Lambda: ");
		
		txtAlpha = new JTextField(3);
		lblAlpha = new JLabel("Alpha: ");
		
		txtMotility = new JTextField(3);
		lblMotility = new JLabel("Motility: ");
		
		txtRotateDiff = new JTextField(3);
		lblRotateDiff = new JLabel("Rotate Diff: ");		
		
		txtNumOfSteps = new JTextField(3);
		lblNumOfSteps = new JLabel("Steps: ");
		
		btnRun = new JButton("Run");
		btnRun.addActionListener(this);
		
		modelParamsPanel = new JPanel();
		modelParamsPanel.add(lblAlpha);
		modelParamsPanel.add(txtAlpha);
		modelParamsPanel.add(lblLambda);
		modelParamsPanel.add(txtLambda);
		modelParamsPanel.add(lblMotility);
		modelParamsPanel.add(txtMotility);
		modelParamsPanel.add(lblRotateDiff);
		modelParamsPanel.add(txtRotateDiff);
		
		simParamsPanel = new JPanel();
		simParamsPanel.add(lblWidth);
		simParamsPanel.add(txtWidth);
		simParamsPanel.add(lblHeight);
		simParamsPanel.add(txtHeight);
		simParamsPanel.add(lblNumOfSteps);
		simParamsPanel.add(txtNumOfSteps);
		simParamsPanel.add(btnRun);
		
		setLayout(new BorderLayout());
		add(modelParamsPanel, BorderLayout.NORTH);
		add(simParamsPanel, BorderLayout.SOUTH);
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnRun){
			Thread runthread = new Thread(){
				@Override
				public void run(){					
					int nx = Integer.parseInt(txtWidth.getText());
					int ny = Integer.parseInt(txtHeight.getText());
					int q = 1;
					double temp = 1.0;
					double lambda = Double.parseDouble(txtLambda.getText());
					double alpha = Double.parseDouble(txtAlpha.getText());
					double beta = 16.0;
					double motility = Double.parseDouble(txtMotility.getText());
					double rotateDiff = Double.parseDouble(txtRotateDiff.getText());
					int seed = -1;
					int numOfSweeps = Integer.parseInt(txtNumOfSteps.getText());
					int nequil = 0;
					SpinReader reader = new SpinReader();
					reader.openReader("init_spin_1000_2.dat");
					btnRun.setEnabled(false);
					CellPottsModel model = new CellPottsModel(
							nx, ny, q, temp, lambda, alpha, beta, motility, rotateDiff,
							seed, numOfSweeps, nequil, new DataWriter [] {}, true);
					model.initSpin(reader.readSpins());
					model.initPolarity();
					view.setModel(model);
					view.initImage();
					model.run();
					view.stopDrawingImage();
					btnRun.setEnabled(true);
				}
			};
			runthread.start();
		}
	}
}
