import javax.swing.*;  
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;  
import java.util.*;

class ForegroundImagePanel extends ImagePanel implements MouseListener {
	private ImageCompositing icApp;
	int x=-1;
	int y=-1;

	public ForegroundImagePanel(String filename, ImageCompositing icApp){ 
		super(filename); 
		this.icApp=icApp;
		addMouseListener(this);
	}
	public void mousePressed(MouseEvent e) {
		BufferedImage bimg=icApp.internalForegroundFrame.ip2.copyImage();
		int pixel=bimg.getRGB(e.getX(),e.getY());
		setpress(pixel,e.getX(),e.getY());

		
	}
	public void mouseReleased(MouseEvent e) { }
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mouseClicked(MouseEvent e) {}
}
