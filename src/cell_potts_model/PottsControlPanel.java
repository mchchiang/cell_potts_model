package cell_potts_model;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

@SuppressWarnings("serial")
public class PottsControlPanel extends JPanel implements ActionListener {
	private JPanel paramsPanel;
	
	private JLabel lblWidth;
	private JTextField txtWidth;
	
	private JLabel lblHeight;
	private JTextField txtHeight;
	
	private JLabel lblTemperature;
	private JTextField txtTemperature;
	
	private JLabel lblEnergy;
	private JTextField txtEnergy;
	
	private JLabel lblNumOfSteps;
	private JTextField txtNumOfSteps;
	
	private JCheckBox chkGraphicsOn;
	
	private JButton btnRun;
	private JButton btnStop;
	
	private PottsView view;
	
	public PottsControlPanel(PottsView view){
		this.view = view;
		
		lblWidth = new JLabel("Width: ");
		txtWidth = new JTextField(3);
		
		lblHeight = new JLabel("Height: ");
		txtHeight = new JTextField(3);
		
		txtTemperature = new JTextField(3);
		lblTemperature = new JLabel("Temperature (T): ");
		
		txtEnergy = new JTextField(3);
		lblEnergy = new JLabel("Energy (J): ");
		
		txtNumOfSteps = new JTextField(3);
		lblNumOfSteps = new JLabel("Steps: ");
		
		chkGraphicsOn = new JCheckBox("Turn Graphics On", true);
		
		btnRun = new JButton("Run");
		btnRun.addActionListener(this);
		
		btnStop = new JButton("Stop");
		btnStop.addActionListener(this);
		
		paramsPanel = new JPanel();
		paramsPanel.add(lblWidth);
		paramsPanel.add(txtWidth);
		paramsPanel.add(lblHeight);
		paramsPanel.add(txtHeight);
		paramsPanel.add(lblTemperature);
		paramsPanel.add(txtTemperature);
		paramsPanel.add(lblEnergy);
		paramsPanel.add(txtEnergy);
		paramsPanel.add(lblNumOfSteps);
		paramsPanel.add(txtNumOfSteps);
		paramsPanel.add(chkGraphicsOn);
		paramsPanel.add(btnRun);
		paramsPanel.add(btnStop);
		
		setLayout(new BorderLayout());
		add(paramsPanel, BorderLayout.CENTER);
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnRun){
			Thread runthread = new Thread(){
				@Override
				public void run(){
//					System.out.println("Clicked");
					/*int width = Integer.parseInt(txtWidth.getText());
					int height = Integer.parseInt(txtHeight.getText());
					double temperature = Double.parseDouble(txtTemperature.getText());
					double energy = Double.parseDouble(txtEnergy.getText());
					int steps = Integer.parseInt(txtNumOfSteps.getText());*/
					int nx = 200;
					int ny = 200;
					int q = 1000;
					double temp = 1.0;
					double lambda = 1.0;
					double alpha = 1.0;
					double beta = 16.0;
					double motility = 4.0;
					double rotateDiff = 0.1;
					int seed = -1;
					int numOfSweeps = 10000;
					int nequil = 0;
					SpinReader reader = new SpinReader();
					reader.openReader("init_spin_1000_2.dat");
					btnRun.setEnabled(false);
					DataWriter cmWriter = new CMWriter();
					DataWriter r2Writer = new R2Writer();
					String filename = String.format("%d_%d_%d_a_%.1f_lam_%.1f_P_%.1f_n_%d_run_%d.dat",
							nx, ny, q, alpha, lambda, motility, numOfSweeps, 3);
					//cmWriter.openWriter("cm_" + filename);
					//r2Writer.openWriter("r2_" + filename);
					CellPottsModel model = new CellPottsModel(
							nx, ny, q, temp, lambda, alpha, beta, motility, rotateDiff,
							seed, numOfSweeps, nequil, new DataWriter [] {new NullWriter()}, true);
					model.initSpin(reader.readSpins());
					model.initPolarity();
					view.setModel(model);
					view.initImage();
					model.run();
					view.stopDrawingImage();
					//cmWriter.closeWriter();
					//r2Writer.closeWriter();
					btnRun.setEnabled(true);
				}
			};
			runthread.start();
		} else if (e.getSource() == btnStop){
			
		}
	}
}
