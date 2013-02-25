//Youssef Aboul-Naja  (Student#: 991489073)
//Hoa Nguyen  (Student#: 991043116)

import javax.swing.*;  
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;  
import java.util.*;
import java.awt.geom.GeneralPath;
/**
 * This class relates to the resultant final image.
 */ 
public class ResultantImagePanel extends ImagePanel implements MouseListener {
	private TextureMap tmApp;
 
	int clicked=0;//number of clicks so far
	//the array stores the (x,y) coordinates pressed on final image
	int[][] points = new int[4][2];

	int r1,c1,r2,c2,r3,c3,r4,c4;
	int u1,v1,u2,v2,u3,v3,u4,v4;
	int u11,v11,u21,v21,u31,v31,u41,v41;
	int smallestError=-1;
	private static final int MAX_BOUNDARY = 1000000000;

	MatrixSolver backward, forward;
	  
	int lowerRowBound,upperRowBound;
	int lowerColumnBound,upperColumnBound;
	  
	BufferedImage upToDate;//up to date current image
	BufferedImage temp;//this image is used to draw the black lines on

	/**
	 * constructor, create a new resultant image panel (a.k.a the mosaic) with a path
	 * to the image and a pointer to the main frame
	 * @param filename the path to the image to be put in this resultamt image panel
	 * @param tmApp the main frame in which this resultant image panel is to be displayed on
	 **/
	public ResultantImagePanel(String filename, TextureMap tmApp)
	{ 
		super(filename); 
		this.tmApp=tmApp;
		addMouseListener(this);
		upToDate = copyImg(getImage());
		temp = copyImg(getImage());
	}
  
	/**
	 * constructor, create a new resultant image panel (a.k.a the mosaic) with a given image
	 * @param img the image to be put in this resultamt image panel
	 * @param tmApp the main frame in which this resultant image panel is to be displayed on
	 **/
	public ResultantImagePanel(BufferedImage img, TextureMap tmApp){
		super(img);
		this.tmApp = tmApp;
		addMouseListener(this);
		upToDate = copyImg(getImage());
		temp = copyImg(getImage());
	}
 
	/**
	 * update the row,column upper bounds and the row, column lower bounds
	 * for each point clicked by the user on this image panel
	 * @param row the x coordiate of the clicked location
	 * @param column the y coordinate of the clicked location 
	 */
	private void updateBounds(int row, int column){
		if (row < lowerRowBound) { lowerRowBound = row; }
		if (row > upperRowBound) { upperRowBound = row; }
		if (column < lowerColumnBound) { lowerColumnBound = column; }
		if (column > upperColumnBound) { upperColumnBound = column; }
	}
  
	/**
	 * apply the mapping operation once the user has choosen 4 points on this image panel
	 * this includes the normal texture map, basic mosaic op., advanced mosiac op.
	 */
	private void applyTexture(){
	  
		//pulling out the 4 clicks from the final image & updating the bounds
		     
		//first point click
		r1= points[0][0]; c1= points[0][1];
		lowerRowBound=r1; upperRowBound=r1;
		lowerColumnBound=c1; upperRowBound=c1;
		     
		//second point clicked
		r2= points[1][0]; c2= points[1][1];
		updateBounds(r2,c2);
		     
		//third point clicked
		r3= points[2][0]; c3= points[2][1];
		updateBounds(r3,c3);
	     
		//fourth point clicked
		r4= points[3][0]; c4= points[3][1];
		updateBounds(r4,c4);
		    
		// retrive texture image
		BufferedImage textureImg = (tmApp.texturePanel).getOriginalImage();
		// the width and height of the texture image
		int width=textureImg.getWidth();
		int height=textureImg.getHeight();
		  
		forward = null; backward = null;
	  
		// now depending on what option the user chose, we take action appropriately
		if(tmApp.TOOL_IN_USE == tmApp.NORMAL_TEXTURE_MAP)
		{
			//finding out the corresponing quad to quad mapping
			backward = new MatrixSolver(0,0,width-1,0,width-1,height-1,0,height-1,r1,c1,r2,c2,r3,c3,r4,c4);
			int i_min = lowerRowBound, i_max = upperRowBound;
			int j_min = lowerColumnBound, j_max = upperColumnBound;
			normalTextureMap(textureImg, i_min, i_max, j_min, j_max);
		}
		else if(tmApp.TOOL_IN_USE == tmApp.BASIC_MOSAIC)
		{
			TextureImagePanel tip = tmApp.texturePanel;
			u1 = tip.pointsClicked[0][0];
			v1 = tip.pointsClicked[0][1];
			u2 = tip.pointsClicked[1][0];
			v2 = tip.pointsClicked[1][1];
			u3 = tip.pointsClicked[2][0];
			v3 = tip.pointsClicked[2][1];
			u4 = tip.pointsClicked[3][0];
			v4 = tip.pointsClicked[3][1];
			backward = new MatrixSolver(u1,v1,u2,v2,u3,v3,u4,v4,r1,c1,r2,c2,r3,c3,r4,c4);
			forward = new MatrixSolver(r1,c1,r2,c2,r3,c3,r4,c4,u1,v1,u2,v2,u3,v3,u4,v4);
			basicMosaic(textureImg);
		}
		
		//re-setting clicked to zero so we can map the texture to final image more
		//than once.
		clicked=0;
	}
  
	/**
	 * This method is used to capture the user's clicks on image.
	 * @param e the event comes from the user's action
	 */ 
	public void mousePressed(MouseEvent e) {
		// if TOOL_IN_USE is NORMAL_TEXTURE_MAP then keep listening
		// otherwise only take action when the texture image has been clicked 4 times
		if((tmApp.texturePanel!=null && tmApp.texturePanel.getNumClicks()==4) || tmApp.TOOL_IN_USE==tmApp.NORMAL_TEXTURE_MAP)
		{
			//capturing the clicks
			points[clicked][0] = e.getX();
			points[clicked][1] = e.getY();
			   
			drawOval(e.getX(), e.getY());
		    
			//drawing the black lines 
			if(clicked>0) 
			{
				int x1=points[clicked-1][0];
				int y1=points[clicked-1][1];
				int x2=points[clicked][0];
				int y2=points[clicked][1];
				drawLine(x1,y1,x2,y2);
			}  
		     
			clicked++;
			     
			if(clicked==4)
			{
				drawOval(points[3][0],points[3][1]);
				drawLine(points[3][0],points[3][1], points[0][0], points[0][1]);
				//once we have 4 clicks, we apply the mapping
				applyTexture();
			}
		}
	}
	
	/**
	 * This method does the normal texture mapping operation
	 * @param textureImg the texture to be mapped onto the image of this image panel
	 * @param i_min the lower row bound
	 * @param i_max the upper row bound
	 * @param j_min the lower column bound
	 * @param j_max the upper column bound
	 */
	private void normalTextureMap(BufferedImage textureImg, int i_min, int i_max, int j_min, int j_max)
	{
		//perform the actual mapping process
		for(int i=i_min; i<i_max; i++)
		{
			for(int j=j_min; j<j_max; j++)
			{
				try
				{
					int X = (int)Math.floor(backward.texU(i,j));
					int Y = (int)Math.floor(backward.texV(i,j));
					upToDate.setRGB(i,j,textureImg.getRGB(X,Y));
				}
				catch(Exception e1)
				{
					try
					{
						upToDate.setRGB(i,j,upToDate.getRGB(i,j));
					}
					catch(Exception e2){}
				}
			}
		}
		setImage(upToDate);
		upToDate = copyImg(upToDate);
		temp = copyImg(upToDate);
	}
	
	/**
	 * This method does the basic mosaicing operation
	 * @param textureImg the texture to be mapped onto the image of this image panel
	 */
	private void basicMosaic(BufferedImage textureImg)
	{
		
		//perform the actual mapping process
		// the coordinates of the extreme points resulting from 
		// doing a forward mapping from the texture to the mosaic image
		int min_x=0, max_x=0, min_y=0, max_y=0;
		
		if(tmApp.AUTO_WARP)
		{
			u11=u1;v11=v1;u21=u2;v21=v2;u31=u3;v31=v3;u41=u4;v41=v4;

			for(int b=0; b<256; b++)
			{
				String binary = Integer.toBinaryString(b);
				errorReducer(textureImg,binary,1);
				//System.out.println(b);
				
			}

			for(int b=0; b<256; b++)
			{
				String binary = Integer.toBinaryString(b);
				errorReducer(textureImg,binary,-1);
				//System.out.println(b);
				
			}

			u1=u11;v1=v11;u2=u21;v2=v21;u3=u31;v3=v31;u4=u41;v4=v41;
		}
		
		// do a forward mapping to find the extreme points
		for(int i=0; i<textureImg.getWidth(); i++)
		{
			for(int j=0; j<textureImg.getHeight(); j++)
			{
				int X = (int)Math.floor(forward.texU(i,j));
				int Y = (int)Math.floor(forward.texV(i,j));
				if (X<min_x) { min_x=X;}
				if (X>max_x) { max_x=X;}
				if (Y<min_y) { min_y=Y;}
				if (Y>max_y) { max_y=Y;}
			}
		}
		
		backward=new MatrixSolver(u1,v1,u2,v2,u3,v3,u4,v4,r1,c1,r2,c2,r3,c3,r4,c4);
		
		// figure out the right size for the final image
		int width=getImage().getWidth();
		int height=getImage().getHeight();
		if(min_x <0) width = width-min_x;
		if(max_x>getImage().getWidth()) width = width + max_x - getImage().getWidth();
		if(min_y<0) height = height - min_y;
		if(max_y > getImage().getHeight()) height = height + max_y - getImage().getHeight(); 
  
		// the final image
		BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
  
		// used to shift pixels in negative location to the right location in the final image
		int correctionX,correctionY;
   
		if(min_x<0){
			correctionX = -1*min_x;
		}else{
			correctionX=min_x;
		}
		   
		if(min_y<0){
			correctionY = -1*min_y;
		}else{
			correctionY = min_y;
		}
		
		// do the stiching process
		for(int i=min_x; i<width; i++)
		{
			for(int j=min_y; j<height; j++)
			{	   
				try
				{					
					/*
					 //without bi-linear interpolation
					 int X = (int)Math.floor(backward.texU(i,j));
					 int Y = (int)Math.floor(backward.texV(i,j));
					 bi.setRGB(i+correctionX,j+correctionY,textureImg.getRGB(X,Y));
					 */

					/*
					 // with bi-linear interpolation
					 double x = backward.texU(i,j);
					 double y = backward.texV(i,j);
					 bi.setRGB(i+correctionX,j+correctionY,biLinearInter(x,y,textureImg));
					 */
					if(tmApp.BLENDING)
					{
						double x = backward.texU(i,j);
						double y = backward.texV(i,j);
						boolean overlap = overlap(i, j, (int)x, (int)y, upToDate, textureImg);
						if(overlap)
						{
							int blendingPixel = colorBlend(i, j, x, y, upToDate, textureImg);
							if(blendingPixel != -1)
								bi.setRGB(i+correctionX,j+correctionY,blendingPixel);
							else 
								bi.setRGB(i+correctionX,j+correctionY,biLinearInter(x,y,textureImg));
						}
						else 
						{
							bi.setRGB(i+correctionX,j+correctionY,biLinearInter(x,y,textureImg));
						}
					}

					if(!tmApp.BLENDING)
					{
						double x = backward.texU(i,j);
						double y = backward.texV(i,j);
						bi.setRGB(i+correctionX,j+correctionY,biLinearInter(x,y,textureImg));
					}
					
				}
				catch (Exception e1)
				{
					try { bi.setRGB(i+correctionX,j+correctionY,upToDate.getRGB(i,j));}
					catch(Exception e2){};
				}
				
			}
		}

		// create a new Resulting Image Panel storing the resulting mosaic
		tmApp.resultantImagePanel = new ResultantImagePanel(bi, tmApp);
		// create a new internal frame to store and show the resuling image panel to the user
		tmApp.internalImageFrame = tmApp.createInternalFrame("current mosaic", tmApp.resultantImagePanel, tmApp.internalImageFrame);
		// remove the used used texture frame
		tmApp.destroyTextureFrame();
		// update the GUI
		tmApp.update();
		// make copies of current mosaic for later use
		setImage(upToDate);
		upToDate = copyImg(upToDate);
		temp = copyImg(upToDate);
	}
	
	/**
	 * return the pixel which is a cobination of the neigbour pixels around (x,y)
	 * @param x the real value of x coordinate
	 * @param y the real value of y coordinate
	 * @param texture the texture image
	 * @ return the pixel which is a cobination of the neigbour pixels around (x,y)
	 **/
	public int biLinearInter(double x, double y, BufferedImage texture)
	{
		// get all the neighbour locations of (x, y) 
		int x1 = (int)Math.floor(x);
		int x2 = x1+1;
		int y1 = (int)Math.floor(y);
		int y2 = y1+1;
		// get alpha_x and alpha_y
		double alpha_x = Math.abs(x1-x);
		double alpha_y= Math.abs(y1-y);
		// pull out all the neighbour pixels
		int n = texture.getRGB(x1, y2);
		int m = texture.getRGB(x1, y1);
		int p = texture.getRGB(x2, y1);
		int q = texture.getRGB(x2, y2);
		
		// pull out all the A,R,B,G components of each of the neighbour pixels
		int n_alpha  = (n >> 24) & 0xff;
		int n_red    = (n >> 16) & 0xff;
		int n_green  = (n >> 8 ) & 0xff;
		int n_blue   = (n      ) & 0xff;
		
		int m_alpha  = (m >> 24) & 0xff;
		int m_red    = (m >> 16) & 0xff;
		int m_green  = (m >> 8 ) & 0xff;
		int m_blue   = (m      ) & 0xff;

		int p_alpha  = (p >> 24) & 0xff;
		int p_red    = (p >> 16) & 0xff;
		int p_green  = (p >> 8 ) & 0xff;
		int p_blue   = (p      ) & 0xff;

		int q_alpha  = (q >> 24) & 0xff;
		int q_red    = (q >> 16) & 0xff;
		int q_green  = (q >> 8 ) & 0xff;
		int q_blue   = (q      ) & 0xff;
		
		// compute the resulting pixel using bi-linear interpolation 
		int alpha = (int)(alpha_y*alpha_x*q_alpha + alpha_y*(1-alpha_x)*n_alpha +
						  alpha_x*(1-alpha_y)*p_alpha + (1-alpha_x)*(1-alpha_y)*m_alpha);

		int red   = (int)(alpha_y*alpha_x*q_red + alpha_y*(1-alpha_x)*n_red +
						  alpha_x*(1-alpha_y)*p_red + (1-alpha_x)*(1-alpha_y)*m_red);
		
		int green = (int)(alpha_y*alpha_x*q_green + alpha_y*(1-alpha_x)*n_green +
						  alpha_x*(1-alpha_y)*p_green + (1-alpha_x)*(1-alpha_y)*m_green);

		int blue  = (int)(alpha_y*alpha_x*q_blue + alpha_y*(1-alpha_x)*n_blue +
						 alpha_x*(1-alpha_y)*p_blue + (1-alpha_x)*(1-alpha_y)*m_blue); 

		int pixel = (alpha<<24)+(red<<16)+(green<<8)+blue;

		return pixel;

	}
	
	/**
	 * return true if the location (i,j) of mosaic and location (x, y) of texture overlap
	 * @return true if the location (i,j) of mosaic and location (x, y) of texture overlap
	 * @param i, j the x, y coordinates of the overlap location in the mosiac
	 * @param _x, _y the x, y coordinates of the overlap location in the texture
	 */
	public boolean overlap(int i, int j, int x, int y, BufferedImage mosaic, BufferedImage texture)
	{
		try
		{
			mosaic.getRGB(i, j);
			texture.getRGB(x, y);
			return true;
		}
		catch (Exception e){ return false; }
	}
	/**
	 * return the blending pixel int the overlap region, return -1 if there is something wrong
	 * @param i, j the x, y coordinates of the overlap location in the mosiac
	 * @param _x, _y the x, y coordinates of the overlap location in the texture
	 * @return the blending pixel int the overlap region, return -1 if there is something wrong
	 */
	public int colorBlend(int i, int j, double _x, double _y, BufferedImage mosaic, BufferedImage texture)
	{
		try 
		{
			int textureWidth = texture.getWidth();
			int textureHeight = texture.getHeight();
			int mosaicWidth = mosaic.getWidth();
			int mosaicHeight = mosaic.getHeight();
			
			int x = (int)_x; 
			int y = (int)_y;
			
			// pixels on mosaic and texture
			int texturePixel = biLinearInter(_x, _y, texture);
			int mosaicPixel = mosaic.getRGB(i,j);
			
			// pull out all A,R,G,B components
			int textureAlpha  = (texturePixel >> 24) & 0xff;
			int textureRed    = (texturePixel >> 16) & 0xff;
			int textureGreen  = (texturePixel >> 8 ) & 0xff;
			int textureBlue   = (texturePixel      ) & 0xff;
		
			int mosaicAlpha   = (mosaicPixel >> 24) & 0xff;
			int mosaicRed     = (mosaicPixel >> 16) & 0xff;
			int mosaicGreen   = (mosaicPixel >> 8 ) & 0xff;
			int mosaicBlue    = (mosaicPixel      ) & 0xff;
			
			// find the smallest distance form the location to the edges accordingly
			int d1 = MAX_BOUNDARY;
			if(overlap(0,j,x,y,mosaic,texture) && ((int)Math.abs(i) < d1)) d1 = (int)Math.abs(i);
			if(overlap(i,0,x,y,mosaic,texture) && ((int)Math.abs(j) < d1)) d1 = (int)Math.abs(j);
			if(overlap(mosaicWidth,0,x,y,mosaic,texture) && (mosaicWidth-(int)Math.abs(i) < d1)) d1 = mosaicWidth-(int)Math.abs(i);
			if(overlap(0,mosaicHeight,x,y,mosaic,texture) && (mosaicHeight-(int)Math.abs(j) < d1)) d1 = mosaicHeight-(int)Math.abs(j);
		
			int d2 = MAX_BOUNDARY;

			x = (int) Math.abs(x);
			y=(int)Math.abs(y);

			if(overlap(i,j,0,y,mosaic,texture) && (x < d2)) d2 = x;
			if(overlap(i,j,x,0,mosaic,texture) && (y < d2)) d2 = y;
			if(overlap(i,j,textureWidth,y,mosaic,texture) && (textureWidth-x < d2)) d2 = textureWidth-x;
			if(overlap(i,j,x,textureHeight,mosaic,texture) && (textureHeight-y < d2)) d2 = textureHeight-y; 
			
			// if everything is good, make the resulting pixel and return it.
			if(d1 == MAX_BOUNDARY || d2 == MAX_BOUNDARY || textureAlpha == 0 || mosaicAlpha == 0)
				return -1;
			else 
			{

				int alpha = (int)((mosaicAlpha*d1*d1*d1 + textureAlpha*d2*d2*d2)/(d1*d1*d1+d2*d2*d2));
				int red = (int)((mosaicRed*d1*d1*d1 + textureRed*d2*d2*d2)/(d1*d1*d1+d2*d2*d2));
				int green = (int)((mosaicGreen*d1*d1*d1 + textureGreen*d2*d2*d2)/(d1*d1*d1+d2*d2*d2));
				int blue = (int)((mosaicBlue*d1*d1*d1 + textureBlue*d2*d2*d2)/(d1*d1*d1+d2*d2*d2));
		
				int pixel = (alpha<<24)+(red<<16)+(green<<8)+blue;
				return pixel;
			}
		}
		catch (Exception e){ return -1; }

	}

	/**
	 * For any pixel clicked on the texture image, it could have one of 9 positions:
	 * where the pixel was clicked and its 8 adjacent neighboors.  Thus there are
	 * 9^4 different positions that teh different 4 pixels from the click could be at.
	 * What this method does is that it explores a specific positioning for the 4 pixels
	 * and checks the value of the Error produced in the interestion region of the 
	 * texture and mosaic image (assuming that these 4 pixels shall me the ones used on 
	 * the texture image when the texture & mosaic images are stitched together).
	 */
	private void errorReducer(BufferedImage textureImg,String b,int m)
	{
		//the way we determine whether or not a pixel wil be moved is that a pixel can
		//move (1) or not move (0)...There are 4 pixels of interest. They are the follow:
		//(a1,b1) (a2,b2) (a3,b3) (a4,b4)
		
		//for a1 to more, a_1 must equal to 1, else it is equal to 0.
		//same notion is used with b_1,a_2,b_2,... etc
		int a_1,b_1,a_2,b_2,a_3,b_3,a_4,b_4;

		//the way we assign values to these incremental variables is that we pass
		//a binary number in string format. We then pull the values out of this binary
		//number and assign each number to a variable in a systematic manner.
		a_1 = Integer.parseInt(b.substring(0,1));
		try{b_1 = Integer.parseInt(b.substring(1,2));} 
		catch(Exception e1){b_1=0;}
		try{a_2 = Integer.parseInt(b.substring(2,3));} 
		catch(Exception e2){a_2=0;}
		try{b_2 = Integer.parseInt(b.substring(3,4));} 
		catch(Exception e3){b_2=0;}
		try{a_3 = Integer.parseInt(b.substring(4,5));} 
		catch(Exception e4){a_3=0;}
		try{b_3 = Integer.parseInt(b.substring(5,6));} 
		catch(Exception e5){b_3=0;}
		try{a_4 = Integer.parseInt(b.substring(6,7));} 
		catch(Exception e6){a_4=0;}
		try{b_4 = Integer.parseInt(b.substring(7));}   
		catch(Exception e7){b_4=0;}

		//setting the intial values the clicks
		int a1=u11,b1=v11;
		int a2=u21,b2=v21;
		int a3=u31,b3=v31;
		int a4=u41,b4=v41;
		
		//we assume intially that the error is zero for this 4 point positioning
		int currentError=0;

		int iter=1;

		//we shall now start moving the pixels accoring to the incremental variables
		//a_1,b_1,a_2,b_2,..etc.
		while(iter<5)
		{
			try
			{
				if(iter==3)
				{
					a1=u1;b1=v1;
					a2=u2;b2=v2;
					a3=u3;b3=v3;
					a4=u4;b4=v4;
				}

				if(iter==1 || iter==2)
				{
				
					if(iter!=2)
					{
						a1+=(a_1*iter*m);
						a2+=(a_2*iter*m);
						a3+=(a_3*iter*m);
						a4+=(a_4*iter*m);
					
						b1+=(b_1*iter*m);
						b2+=(b_2*iter*m);
						b3+=(b_3*iter*m);
						b4+=(b_4*iter*m);
					}
					else
					{
						a1+=(a_1*iter*-1*m);
						a2+=(a_2*iter*-1*m);
						a3+=(a_3*iter*-1*m);
						a4+=(a_4*iter*-1*m);
					
						b1+=(b_1*iter*-1*m);
						b2+=(b_2*iter*-1*m);
						b3+=(b_3*iter*-1*m);
						b4+=(b_4*iter*-1*m);
					}
				}
				else
				{
					if(iter!=4)
					{
						a1+=(a_1*(iter-2)*m);
						a2+=(a_2*(iter-2)*m);
						a3+=(a_3*(iter-2)*m);
						a4+=(a_4*(iter-2)*m);
					
						b1+=(b_1*(iter-2)*-1*m);
						b2+=(b_2*(iter-2)*-1*m);
						b3+=(b_3*(iter-2)*-1*m);
						b4+=(b_4*(iter-2)*-1*m);

					}
					else
					{
						a1+=(a_1*(iter-2)*-1*m);
						a2+=(a_2*(iter-2)*-1*m);
						a3+=(a_3*(iter-2)*-1*m);
						a4+=(a_4*(iter-2)*-1*m);

						b1+=(b_1*(iter-2)*m);
						b2+=(b_2*(iter-2)*m);
						b3+=(b_3*(iter-2)*m);
						b4+=(b_4*(iter-2)*m);
					
					
					}
				}

                //we then set up a MatrixSolver that does forward mapping (from texture to mosaic img)
				MatrixSolver f= new MatrixSolver(r1,c1,r2,c2,r3,c3,r4,c4,a1,b1,a2,b2,a3,b3,a4,b4);
				
				//we calculate the enclosed region that is formed from the 4 pixels that
				//are in the texture image.
				int max_u = a1, min_u=a1, max_v=b1, min_v=b1;
				if (a2>max_u){max_u=a2;} if (a3>max_u){max_u=a3;} if (a4>max_u){max_u=a4;}
				if (a2<min_u){min_u=a2;} if (a3<min_u){min_u=a3;} if (a4<min_u){min_u=a4;}
				if (b2>max_v){max_v=b2;} if (b3>max_v){max_v=b3;} if (b4>max_v){max_v=b4;}
				if (b2<min_v){min_v=b2;} if (b3<min_v){min_v=b3;} if (b4<min_v){min_v=b4;}

				currentError=0;

				//we create a GeneralPath so that we know if a certain pixel is inside the
				//enclosed region in the texture image (if the pixel is inside the enclosed
				//region, then it will be in the interesction of the texture & mosaic img).
				GeneralPath gp = new GeneralPath();
				gp.moveTo(a1,b1);
				gp.lineTo(a2,b2);
				gp.lineTo(a3,b3);
				gp.lineTo(a4,b4);
				gp.closePath();
				int num=0;
				
				//We know iterate through the enclosed region in the texture Image
				for(int i=min_u; i<max_u; i++)
				{
					for(int j=min_v; j<max_v; j++)
					{
						//if the pixel (i,j) is inside the enclosed region, then proceed
						if(gp.contains(i,j))
						{
							num++;
							//we do a forward mapping for this pixel
							int u = (int)Math.floor(f.texU(i,j));
							int v = (int)Math.floor(f.texV(i,j));
							
							//we pull the RGB value from the texture image for the pixel (i,j)
							int pixelTex = textureImg.getRGB(i,j);
							int grayScaleTex = ( ((pixelTex>>16) & 0xff) + ((pixelTex>>8) & 0xff) + ((pixelTex) & 0xff) );

							//we pull the RGB value from the mosaic image that will blend with the
							//pixel (i,j) that is from the texture image.
							int pixelMos = upToDate.getRGB(u,v);
							int grayScaleMos = ( ((pixelMos>>16) & 0xff) + ((pixelMos>>8) & 0xff) + ((pixelMos) & 0xff) );
							
							//we know calculate the Error that is produced from this pixel give these set
							//of 4 point positins.
							currentError += ((grayScaleMos - grayScaleTex) * (grayScaleMos - grayScaleTex));

							//if the current Error is larger than the smallest error we encountered, then
							//we can stop.  Such 4 pixel positions are not desirable.
							//Initially, smallestError is negative, so we will not "break" for this case, but
							//any other case, we shall break.
							if(((currentError/num) > smallestError)&&(smallestError>0)){ 
								break; 
							}
						}	
					
					}
				}
				
				//To have an accurate notion of error, we have to have an average Error.
				//that is the error produce divided by the number of pixel that were considered
				if(num!=0){currentError = currentError/num;}
				
				//if currentError is smaller than smallestError, then update smallestError
				if((smallestError<0)||(currentError<smallestError))
				{
					smallestError = currentError;
					//update the forward mapping Matrix solver and the dynamic bounds
					//to this more desirable 4 pixel arrangments
					forward=f;
					u11=a1;u21=a2;u31=a3;u41=a4;v11=b1;v21=b2;v31=b3;v41=b4;
				}

				iter++;//incremenet the iter variable.

			}
			catch(Exception e){	iter++;	}
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
