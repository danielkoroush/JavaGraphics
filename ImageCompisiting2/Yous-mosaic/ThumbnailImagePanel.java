//Youssef Aboul-Naja  (Student#: 991489073)
//Hoa Nguyen  (Student#: 991043116)

import javax.swing.*;  
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;  
import java.util.*;

/**
 * This class is used to create thumbnail panels to add to the thumbnail frame in class TextureMap
 * Each ThumbnailImagePanel holds the original image and a scaled image which will be put in the thumbnail frame.
 */ 
class ThumbnailImagePanel extends ImagePanel implements MouseListener {
	private TextureMap tmApp; // used to get the mosaic frame and image frame from class TextureMap
	private MatrixSolver ms; // used to make the scaled version of the original image
	private BufferedImage originalImg; // the original image (the one when first gets loaded up by user
	private BufferedImage thumbnailImg; // the scaled image of the original image
	private final int SIZE = 60; // the size of the scaled image
	private boolean isClicked; // the number of clicks on this image panel, if this image panel has been clicked once
								// it will be de-activated
	/**
	 * Constructor, create a thumbnail image panel to be put in the thumbnail frame
	 * @param: filename the path to the image
	 * @param: tmApp the main GUI frame in which this image panel is displayed
	 **/
	public ThumbnailImagePanel(String filename, TextureMap tmApp){ 
		super(filename); 
		this.tmApp=tmApp; 
		isClicked = false;
		originalImg = this.copyImage(); // make a copy of the original image
		// make a thumbnail image with the given size
		thumbnailImg = new BufferedImage(SIZE,SIZE, BufferedImage.TYPE_INT_ARGB);
		int w = this.getImageWidth();
		int h = this.getImageHeight();
		// create a new MatrixSolover to generate pixels for the thumbnail image
		ms = new MatrixSolver(0,0,w-1,0,w-1,h-1,0,h-1,0,0,SIZE-1,0,SIZE-1,SIZE-1,0,SIZE-1);
		setThumbnail(); // set the thumbnail image to the panel
		addMouseListener(this); // listen to user on this image panel
	}
	
	/**
	 * Do a backward mapping from the original image to the thumbnail image
	 **/
	public void setThumbnail(){
		for(int i=0; i<SIZE-1; i++) {
			for(int j=0; j<SIZE-1; j++){
				try{
					int X = (int) Math.floor( ms.texU(i,j));
					int Y = (int) Math.floor(ms.texV(i,j));
					thumbnailImg.setRGB(i,j,originalImg.getRGB(X,Y));
				}catch(Exception e){}
			}
		}
		   
		setImage(thumbnailImg);
	}
	
	/**
	 * return the original image of this ThumnailImagePanel
	 * @ return return the original image of this ThumnailImagePanel
	 **/
	public BufferedImage getOriginalImage(){
		return originalImg;
	}
	
	/**
	 * Listen to the user action on this thumbnail image panel. If this thumbnail image panel has benn clicked once, 
	 * this thumbnail image panel will be deactivated. If there are a mosaic image and a texture image currently on 
	 * display, user's action will be ignored until the texture image is mapped to the mosaic image.
	 * If there is no image has been chosen for the mosaic image, this image will be chosen as the mosaic image, 
	 * otherwise this will be set as the texture image.
	 * @param e the mouselistenter to listen to the user's action  
	 **/
	public void mousePressed(MouseEvent e) {
		// take action only when this has not been clicked and there is 0 or 1 image has been chosen
		if(!isClicked && tmApp.totalImages!=2){
			isClicked = true;
			if(tmApp.totalImages==0){
				tmApp.resultantImagePanel=new ResultantImagePanel(originalImg, tmApp);
				tmApp.internalImageFrame = tmApp.createInternalFrame("current mosaic", tmApp.resultantImagePanel, tmApp.internalImageFrame);
				Container c = tmApp.internalThumbnailFrame.getContentPane();
				c.remove(this);
				c.update(c.getGraphics());
				tmApp.internalThumbnailFrame.pack();
				if(c.getComponentCount() == 0)
					tmApp.destroyThumbnailFrame();
			}
			else {
				tmApp.texturePanel=new TextureImagePanel(originalImg, tmApp);
				tmApp.internalTextureFrame = tmApp.createInternalFrame("current texture", tmApp.texturePanel, tmApp.internalTextureFrame);
				Container c = tmApp.internalThumbnailFrame.getContentPane();
				c.remove(this);
				c.update(c.getGraphics());
				tmApp.internalThumbnailFrame.pack();
				if(c.getComponentCount() == 0)
					tmApp.destroyThumbnailFrame();
			}
			tmApp.totalImages++;
			tmApp.update();
		}
	}
	// not interested in this method
	public void mouseReleased(MouseEvent e) { }
	// not interested in this method
	public void mouseEntered(MouseEvent e) {}
	// not interested in this method
	public void mouseExited(MouseEvent e) {}
	// not interested in this method
	public void mouseClicked(MouseEvent e) {}
}
