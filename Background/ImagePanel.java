import javax.swing.*;  
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;  
import java.util.*;

class ImagePanel extends JPanel {

	private BufferedImage bimg; // The image currently on display

	public ImagePanel(){ }
	public ImagePanel(BufferedImage bimg){ setImage(bimg); }
	public ImagePanel(String filename){ setImage(filename); }
	
	public boolean setImage(String fileName){
		setImage(ImageLoader.loadImage(fileName));
		return true;
	}

	public void setImage(BufferedImage bimg){
		if(bimg==null)return;
		this.bimg=bimg;
		Dimension size=new Dimension(bimg.getWidth(this),bimg.getHeight(this));
		setMinimumSize(size); setPreferredSize(size);
		repaint();
	}

	// Paint b ontop of my image at location x,y
	public void paintImage(BufferedImage b, int x, int y){ 
		Graphics2D g=bimg.createGraphics(); 
		g.drawImage(b,null,x,y); 
		repaint();
	}

	public BufferedImage getImage(){ return bimg; }

	public BufferedImage copyImage(){ 
		BufferedImage b=new BufferedImage(bimg.getWidth(this), bimg.getHeight(this), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g=b.createGraphics();
		g.drawImage(bimg,null,0,0);
		return b;
	}
	public BufferedImage copyImage2(BufferedImage bim){ 
		if(bim==null)return null;
		BufferedImage b=new BufferedImage(bim.getWidth(null), bim.getHeight(null), BufferedImage.TYPE_INT_RGB);
		Graphics2D g=b.createGraphics();
		g.drawImage(bimg,null,0,0);
		return b;
	}

	public int getImageWidth(){ return bimg.getWidth(this); }
	public int getImageHeight(){ return bimg.getHeight(this); }


	public void paintComponent(Graphics g){
		super.paintComponent(g);
		if(bimg!=null)g.drawImage(bimg,0,0,this);
	}
}
