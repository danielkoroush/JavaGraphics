//Youssef Aboul-Naja  (Student#: 991489073)
//Hoa Nguyen  (Student#: 991043116)

import javax.swing.*;  
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;  
import java.util.*;

/**
 * This class relates to the texture image that shall be used
 * in the texture mapping process.
 */ 
public class TextureImagePanel extends ImagePanel implements MouseListener {
	private TextureMap tmApp;
	int pointsClicked[][] = new int[4][2]; // used to store the clicked locations
	int numClicks = 0; // the number of clicks so far
	BufferedImage upToDate ; // used to draw circles, lines and stuff
	BufferedImage originalImg; // the original image

	/**
	 * constructor, create a new texture image panel with a path to a given image
	 * @param filename the path to the image to be put in this texture image panel
	 * @param tmApp the main frame in which this texture image panel is to be displayed on
	 **/
	public TextureImagePanel(String filename, TextureMap tmApp){ 
		super(filename); 
		this.tmApp=tmApp;
		upToDate = getImage();
		originalImg = copyImg(getImage());
		addMouseListener(this);
	}
	
	/**
	 * constructor, create a new texture image panel with a given image
	 * @param img the image to be put in this texture image panle
	 * @param tmApp the main frame in which this texture image panel is to be displayed on
	 **/
	public TextureImagePanel(BufferedImage img, TextureMap tmApp){
		super(img);
		this.tmApp = tmApp;
		upToDate = getImage();
		originalImg = copyImg(getImage());
		addMouseListener(this);
	}
	
	/**
	 * This method is used to capture the user's clicks on image.
	 * @param e the event comes from the user's action
	 */ 
	public void mousePressed(MouseEvent e) 
	{
		// only listen to the clicks when the number of clicks < 4 
		// and the tool in use is basic mosaic or advanced mosaic
		if(numClicks<4 && tmApp.TOOL_IN_USE == tmApp.BASIC_MOSAIC)
		{
			pointsClicked[numClicks][0]=e.getX();
			pointsClicked[numClicks][1]=e.getY();

			drawOval(e.getX(), e.getY());
			
			if(numClicks>0)
			{
				int x1=pointsClicked[numClicks-1][0];
				int y1=pointsClicked[numClicks-1][1];
				int x2=pointsClicked[numClicks][0];
				int y2=pointsClicked[numClicks][1];
				drawLine(x1,y1,x2,y2);
				if(numClicks == 3)
				{
					drawLine(pointsClicked[0][0], pointsClicked[0][1],x2,y2);
				}
			}

			numClicks++;
		}
	}
	
	// not interested in this event
	public void mouseReleased(MouseEvent e) { }
	// not interested in this event
	public void mouseEntered(MouseEvent e) {}
	// not interested in this event
	public void mouseExited(MouseEvent e) {}
	// not interested in this event	
	public void mouseClicked(MouseEvent e) {}
	
	/**
	 * return the uptodate image (we may never call this method, have it here in case
	 * we may need to use it later)
	 * @return the uptodate image
	 */
	public BufferedImage getUpToDateImage(){ return upToDate; }
	
	/**
	 * return the original image
	 * @return the the original image
	 */
	public BufferedImage getOriginalImage()	{ return originalImg; }
	
	/**
	 * return the number of clicks on this image so far
	 */
	public int getNumClicks(){ return numClicks; }
}
