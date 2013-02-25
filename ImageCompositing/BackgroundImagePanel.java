import javax.swing.*;  
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;  
import java.util.*;

class BackgroundImagePanel extends ImagePanel implements MouseListener {
	private ImageCompositing icApp;

	int x=-1;
	int y=-1;

	ImagePanel ipComposite;
	
	public BackgroundImagePanel(String filename, ImageCompositing icApp){ 
		super(filename); 
		this.icApp=icApp;
		addMouseListener(this);
	}
	public void mousePressed(MouseEvent e) {
				BufferedImage bimg=icApp.internalBackgroundFrame.ip2.copyImage();
				int pixel=bimg.getRGB(e.getX(),e.getY());
				setpress(pixel,e.getX(),e.getY());
				

		if ((icApp.internalForegroundFrame!=null)&&(icApp.internalForegroundFrame.ip2.press!=-1)){
			icApp.internalBackgroundFrame.ip2.pre.addElement(icApp.internalBackgroundFrame.ip2.copyImage());
			BufferedImage b=icApp.internalForegroundFrame.ip2.setAlpha(icApp.internalForegroundFrame.ip2.copyImage(),icApp.internalForegroundFrame.ip2.press);
			icApp.internalBackgroundFrame.ip2.paintImage(b,e.getX()-37,e.getY()-23);
			icApp.internalBackgroundFrame.ip2.next.addElement(icApp.internalBackgroundFrame.ip2.copyImage());
		}


		
		
	}
	public void mouseReleased(MouseEvent e) {
		x=e.getX();
		y=e.getY();
		
	
		 }
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mouseClicked(MouseEvent e) {}
}
