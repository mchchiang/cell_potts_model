package cell_potts_model;

import javax.swing.*;
import java.awt.*;
import java.util.Observable;
import java.util.Observer;

@SuppressWarnings("serial")
public class IsingViewPanel extends JPanel implements Observer {
	
	private SpinModel model;
	private int cellSize;
	private Color [] colours;
	
	public IsingViewPanel(SpinModel model){
		this.model = model;
		this.model.addObserver(this);
		setColours();
	}
	
	public void setModel(SpinModel model){
		this.model.deleteObserver(this);
		this.model = model;
		this.model.addObserver(this);
		setColours();
		repaint();
	}
	
	public void turnOnGraphics(){
		this.model.addObserver(this);
	}
	
	public void turnOffGraphics(){
		this.model.deleteObserver(this);
	}
	
	public void setColours(){
		int typesOfSpin = model.getTypesOfSpin();
		colours = new Color [typesOfSpin];
		float s, b, h;
		for (int i = 0; i < typesOfSpin; i++){
			h = (float) Math.random();
			b = (float) (Math.random() * 0.5 + 0.5);
			s = (float) (Math.random() * 0.5 + 0.5);
			colours[i] = new Color(Color.HSBtoRGB(h, s, b));
		}
	}
	
	@Override
	public void paint(Graphics g){
		super.paint(g);
		int width = this.getWidth();
		int height = this.getHeight();
		
		//determine the pixel per square
		int rows = model.getNumOfRows();
		int columns = model.getNumOfColumns();
		
		if (rows != 0 || columns != 0){
			int cellWidth = width / columns;
			int cellHeight = height / rows;
			if (cellWidth > cellHeight){
				cellSize = cellHeight;
			} else {
				cellSize = cellWidth;
			}
			
			for (int i = 0; i < rows; i++){
				for (int j = 0; j < columns; j++){
					g.setColor(colours[model.getSpin(i, j)]);
					g.fillRect(j * cellSize, i * cellSize, cellSize, cellSize);
				}
			}
		}
	}
	
	@Override
	public void update(Observable o, Object spin) {
		Object [] spinIndices = (Object []) spin;
		drawSpin((Integer) spinIndices[0], (Integer) spinIndices[1]);
	}	
	
	public void drawSpin(int i, int j){
		Graphics g = this.getGraphics();
		g.setColor(colours[model.getSpin(i, j)]);
		g.fillRect(j * cellSize, i * cellSize, cellSize, cellSize);
	}
}
