//Youssef Aboul-Naja  (Student#: 991489073)
//Hoa Nguyen  (Student#: 991043116)

import javax.swing.*;  
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;  
import java.util.*;

public class ImagePanel extends JPanel {

	private BufferedImage bimg; // The image currently on display
	
	/**
	 * Constructor, create an empty ImagePanel
	 */
	public ImagePanel(){ }
	
	/**
	 * Constructor, create a ImagePanel with a BufferedImage
	 * @param bimg the BufferedImage of this Image Panel
	 */
	public ImagePanel(BufferedImage bimg){ setImage(bimg); }
	
	/**
	 * Constructor, create a ImagePanel with a BufferedImage by loading up the image specified by 
	 * the path to the image
	 * @param filename the path to the image
	 */
	public ImagePanel(String filename){ setImage(filename); }
	 
	/**
	 * Set the image of this to an image specified by fileName
	 * @param fileName the path to the image
	 * @return true if the image was successfully set, false otherwise
	 */
	public boolean setImage(String fileName){
		BufferedImage b=ImageLoader.loadImage(fileName);
		if(b==null){ bimg=null;  return false; }
		setImage(b);
		return true;
	}
	
	/**
	 * Set the image of this to an image
	 * @param bimg the image to be set.
	 */
	public void setImage(BufferedImage bimg)
	{
		if(bimg==null)return;
		this.bimg=bimg;
		Dimension size=new Dimension(bimg.getWidth(this),bimg.getHeight(this));
		setMinimumSize(size); setPreferredSize(size);
		repaint();
	}

	/**
	 * Paint b ontop of my image at location x,y
	 * @param b the image to be painted on this Image Panel
	 * @param x the x coordinate of the location
	 * @param y the y coordinate of the location
	 **/
	public void paintImage(BufferedImage b, int x, int y){ 
		if(bimg==null)return;
		Graphics2D g=bimg.createGraphics(); 
		g.drawImage(b,null,x,y); 
		repaint();
	}

	/**
	 * Return the BufferedImage of this Image Panel
	 * @return the BufferedImage of this Image Panel
	 */
	public BufferedImage getImage(){ return bimg; }

	/**
	 * Return a copy of the BufferedImage of this Image Panel
	 * @return a copy of the BufferedImage of this Image Panel
	 */
	public BufferedImage copyImage(){ 
		if(bimg==null)return null;
		BufferedImage b=new BufferedImage(bimg.getWidth(this), bimg.getHeight(this), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g=b.createGraphics();
		g.drawImage(bimg,null,0,0);
		return b;
	}

	/**
	 * return the width of the BufferedImage of this Image Panel
	 * @return the width of the BufferedImage of this Image Panel
	 */
	public int getImageWidth(){ if(bimg==null)return -1; return bimg.getWidth(this); }
	
	/**
	 * return the height of the BufferedImage of this Image Panel
	 * @return the height of the BufferedImage of this Image Panel
	 */
	public int getImageHeight(){ if(bimg==null)return -1; return bimg.getHeight(this); }
	
	/**
	 * Draw a line on this Image Panel from (x1,y1) to (x2, y2)
	 * @param x1 the x coordinate of (x1,y1)
	 * @param y1 the y coordinate of (x1,y1)
	 * @param x2 the x coordinate of (x2,y2)
	 * @param y2 the y coordinate of (x2,y2)
	 */
	public void drawLine(int x1, int y1, int x2, int y2)
	{
		Graphics g = bimg.getGraphics();
		g.setColor(Color.red);
		g.drawLine(x1,y1,x2,y2);
		setImage(bimg);
	}
 
	/**
	 * Draw a circle centered at (x,y)
	 * @param x the x coordiate of the center
	 * @param y the y coordiate of the center
	 */
	public void drawOval(int x, int y)
	{
		Graphics g = bimg.getGraphics();
		g.setColor(Color.red);
		g.fillOval(x-3,y-3,6,6);
		setImage(bimg);
	}
 
	/**
	 * return a copy of the BufferedImage br
	 * @param br the image to be copied
	 * @return a copy of the BufferedImage br
	 */
	public BufferedImage copyImg(BufferedImage br)
	{
		int width = br.getWidth();
		int height = br.getHeight();
		BufferedImage copy = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);

		for(int i=0; i<width; i++)
		{
			for(int j=0; j<height; j++)
			{
				copy.setRGB(i,j,getImage().getRGB(i,j));
			}
		}
		return copy;
	}

	/**
	 * paint this ImagePanel
	 * @param g the Graphics used to paint
	 */
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		if(bimg!=null)g.drawImage(bimg,0,0,this);
	}
}
