import javax.swing.*;  
import java.awt.*;
import java.awt.image.*;
import java.util.*;


class ImagePanel extends JPanel {

	private BufferedImage bimg; // The image currently on display
	public int press=-1;
	public int x;
	public int y;
	public Vector pre=new Vector();
	public Vector next=new Vector();

        /**
         * This method gets the information of the pixel that was choosen by
         * the user via the mouse
         * argument is a specifier that is relative to the url argument. 
         *
         * @param  i  pixel int that includes RGB and alpha
         * @param  x the X-axis location of the pixel, 
         * @param  y the Y-axis location of the pixel,
         */
	public void setpress(int i,int x,int y){
		
		press=i;
		this.x=x;
		this.y=y;

	}

        /**
         * This method returns the pixel located at X,Y cordinate of
         * BufferedImage bimg
         *  
         *
         * @param  bimg  a BufferedImage
         * @param  x the X-axis location of the pixel, 
         * @param  y the Y-axis location of the pixel,
	 * @return  pixel at location (x,y) of the bufferedImage bimg
         */
	public int getPixel(BufferedImage bimg,int x,int y){
		return(bimg.getRGB(x,y));
		
	}

	/**
	 * constructor.  
         * 
         */
	public ImagePanel(){ }

        /**
         * This constructor sets a new instance of ImagePanel with 
         * BufferedImage bimg
         *  
         *
         * @param  bimg  a BufferedImage
         */
	public ImagePanel(BufferedImage bimg){ setImage(bimg); }

        /**
         * This constructor sets a new instance of ImagePanel with 
         * String filename that contains an image
         *  
         *
         * @param  filename  a String
         */
	public ImagePanel(String filename){ setImage(filename); }

	/**
         * This method returns true if it can create new instance of
         * ImagePanel by loading an image from filename else it will
         *  return false
         *
         * @param  fileName  a String
	 * @return  boolean
         */
	public boolean setImage(String fileName){
		BufferedImage b=ImageLoader.loadImage(fileName);
		if(b==null){ bimg=null;  return false; }
		setImage(b);
		return true;
	}

	/**
         * This method will create new instance of
         * ImagePanel by loading an image from the bimg 
         *  
         *
         * @param  bimg BufferedImage
         */
	public void setImage(BufferedImage bimg){
		if(bimg==null)return;
		this.bimg=bimg;
		Dimension size=new Dimension(bimg.getWidth(this),bimg.getHeight(this));
		setMinimumSize(size); setPreferredSize(size);
		repaint();
	}

	// Paint b ontop of my image at location x,y
	public void paintImage(BufferedImage b, int x, int y){ 
		if(bimg==null)return;
		Graphics2D g=bimg.createGraphics(); 
		g.drawImage(b,null,x,y); 
		repaint();
	}

	/**
         * This method will return BufferedImage of an image
         *  
         *
	 * @return BufferedImage which is a copy of the image
         */
	public BufferedImage getImage(){ return bimg; }

	/**
         * This method will return copy of BufferedImage of an image
         *  
         *
	 * @return BufferedImage which is a copy of the image in ARGB TYPE
         */
	public BufferedImage copyImage(){ 
		if(bimg==null)return null;
		BufferedImage b=new BufferedImage(bimg.getWidth(this), bimg.getHeight(this), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g=b.createGraphics();
		g.drawImage(bimg,null,0,0);
		return b;
	}

	/**
         * This method will return copy of BufferedImage of an image
         *  This method is used for saving files.
         *
	 * @return BufferedImage which is a copy of the image in RGB TYPE
         */
	public BufferedImage copyImage2(){ 
		if(bimg==null)return null;
		BufferedImage b=new BufferedImage(bimg.getWidth(this), bimg.getHeight(this), BufferedImage.TYPE_INT_RGB);
		Graphics2D g=b.createGraphics();
		g.drawImage(bimg,null,0,0);
		return b;
	}


	/**
         * This method will width of an image
         *
	 * @return int which represent image's width length in pixels
         */
	public int getImageWidth(){ if(bimg==null)return -1; return bimg.getWidth(this); }


	/**
         * This method will height of an image
         *
	 * @return int which represent image's height length in pixels
         */
	public int getImageHeight(){ if(bimg==null)return -1; return bimg.getHeight(this); }
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		if(bimg!=null)g.drawImage(bimg,0,0,this);
	}

	/**
         * This method will set alphas of all pixels which are close in RGB color to pixel2
         * to 0 and will return the result as an new BufferedImage
	 *
	 * @ param BufferedImage bimg BufferedImage of a picture which we will set its alpha to 0
	 * @ param int pixel2 which will use to compare colors with pixels from bimg
	 * @return BufferedImage
         */
	public BufferedImage setAlpha(BufferedImage bimg, int pixel2){
		int w=bimg.getWidth(null), h=bimg.getHeight(null);
		int Palpha = (pixel2 >> 24) & 0xff;
		int Pred   = (pixel2 >> 16) & 0xff;
		int Pgreen = (pixel2 >> 8 ) & 0xff;
		int Pblue  = (pixel2      ) & 0xff;
		for(int x1=0;x1<w;x1++){
			for(int y1=0;y1<h;y1++){
				int pixel=bimg.getRGB(x1,y1); // Alpha is also in there
				int alpha = (pixel >> 24) & 0xff;
				int red   = (pixel >> 16) & 0xff;
				int green = (pixel >> 8 ) & 0xff;
				int blue  = (pixel      ) & 0xff;

				if ((green<Pgreen+50)&&(green>Pgreen-50)&&(red<Pred+50)&&(red>Pred-50)&&(blue>Pblue-50)&&(blue<Pblue+50)){
				alpha=0;
				}else{				 
				alpha=255;
				}


				pixel=(alpha<<24)+(red<<16)+(green<<8)+blue;
				bimg.setRGB(x1,y1,pixel);
			}
		}
		return bimg;
	}

	/**
         * This method will changes color of each pixel in the BufferedImage bimg that is close
         * to pixel2. it will sets the RGB color to r,g,b
	 *
	 * @ param BufferedImage bimg BufferedImage of a picture which we will change its color
	 * @ param int pixel2 which will use to compare colors with pixels from bimg
	 * @ param int r which repesent the red-nees of the new color
	 * @ param int b which repesent the blue-nees of the new color
	 * @ param int g which repesent the green-nees of the new color
	 * @return BufferedImage
         */
	public BufferedImage setColor(BufferedImage bimg, int pixel2, int b,int r,int g){
		int w=bimg.getWidth(null), h=bimg.getHeight(null);
		int Palpha = (pixel2 >> 24) & 0xff;
		int Pred   = (pixel2 >> 16) & 0xff;
		int Pgreen = (pixel2 >> 8 ) & 0xff;
		int Pblue  = (pixel2      ) & 0xff;
		
		for(int x1=0;x1<w;x1++){
			for(int y1=0;y1<h;y1++){
				int pixel=bimg.getRGB(x1,y1); // Alpha is also in there
				int alpha = (pixel >> 24) & 0xff;
				int red   = (pixel >> 16) & 0xff;
				int green = (pixel >> 8 ) & 0xff;
				int blue  = (pixel      ) & 0xff;

				if ((green<Pgreen+50)&&(green>Pgreen-50)&&(red<Pred+50)&&(red>Pred-50)&&(blue>Pblue-50)&&(blue<Pblue+50)){
				green=g;
				red=r;
				blue=b;
				}


				pixel=(alpha<<24)+(red<<16)+(green<<8)+blue;
				bimg.setRGB(x1,y1,pixel);
			}
		}
		setpress(bimg.getRGB(x,y),x,y);
		return bimg;
	}
}
