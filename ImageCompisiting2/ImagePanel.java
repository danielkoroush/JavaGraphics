import javax.swing.*;  
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.*;
import java.util.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.awt.Point;

class ImagePanel extends JPanel {

	public BufferedImage bimg;
	public BufferedImage Origninalbimg;
	public BufferedImage cutImage; // The image currently on display
	public int press=-1;
	public int x;
	public int y;
	public Vector points=new Vector();
    Min min;
	public int size=0;
	private int gaussNormalizingValue;
	public MouseEvent e1=null;
	public Vector cut1=new Vector();
	Vector cut=new Vector();
	public Vector cut3=new Vector();
	boolean flag=true;
	public Vector pre=new Vector();
	public Vector next=new Vector();
	public double [][] gaussian;
	public double [][][] CostMap;
	
//	global variables for mapping images
	public ImageCompositing icApp;
	public boolean mapImageEnabled=false;
	public boolean mappingComplete=false;
	public int clickCtr=0;
	public Coord clickA, clickB, clickC, clickD;//the coords of the user's clicks
	public Coord clickATexture, clickBTexture, clickCTexture, clickDTexture;//texture coords
	private int smallestX=0, smallestY=0;
	private int [] lowHigh;//stores the lowest and highest x and y values from the clicked
	private BufferedImage foreground;//the foreground shown in it's window
	private BufferedImage background;//the background shown in it's window
	
	public void setpress(int i,int x,int y){
		
		press=i;
		int pixel=(255<<24)+(255<<16)+(0<<8)+0;
		
		this.x=x;
		this.y=y;
		points.addElement(new Point(x,y));
		bimg.setRGB(x,y,pixel);
		setImage(bimg);
		//System.out.println(x);
		//System.out.println(y);

	}
	public int getPixel(BufferedImage bimg,int x,int y){
		return(bimg.getRGB(x,y));
		
	}

	public ImagePanel(){ }
	public ImagePanel(BufferedImage bimg){ setImage(bimg); }
	public ImagePanel(String filename){
		
	//	if (bimg==null){
		//	flag=true;
	//	}else{
		//	flag=false;
		//}
		setImage(filename);
		Origninalbimg= copyImage();
		}
	
	public boolean setImage(String fileName){
		BufferedImage b=ImageLoader.loadImage(fileName);
		if(b==null){ bimg=null;  return false; }
		setImage(b);
		Origninalbimg=b;
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
		if(bimg==null)return;
		Graphics2D g=bimg.createGraphics(); 
		g.drawImage(b,null,x,y); 
		repaint();
	}

	public BufferedImage getImage(){ return bimg; }

	public BufferedImage copyImage(){ 
		if(bimg==null)return null;
		BufferedImage b=new BufferedImage(bimg.getWidth(this), bimg.getHeight(this), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g=b.createGraphics();
		g.drawImage(bimg,null,0,0);
		return b;
	}
	
	public BufferedImage rotate(){ 
		
		BufferedImage b=new BufferedImage(Origninalbimg.getHeight(this),Origninalbimg.getWidth(this), BufferedImage.TYPE_INT_ARGB);
for (int i=0;i<getWidth();i++){
	for (int j=0;j<getHeight();j++){
		int Pixel=Origninalbimg.getRGB(i,j);
		b.setRGB(j,i,Pixel);
	}
}
bimg=b;
Origninalbimg=b;
setImage(b);
	return b;
	}
	
	public BufferedImage copyImage2(){ 
		if(bimg==null)return null;
		BufferedImage b=new BufferedImage(bimg.getWidth(this), bimg.getHeight(this), BufferedImage.TYPE_INT_RGB);
		Graphics2D g=b.createGraphics();
		g.drawImage(Origninalbimg,null,0,0);
		return b;
	}
	public BufferedImage copyImage3(){ 
		if(bimg==null)return null;
		BufferedImage b=new BufferedImage(bimg.getWidth(this), bimg.getHeight(this), BufferedImage.TYPE_INT_RGB);
		Graphics2D g=b.createGraphics();
		g.drawImage(bimg,null,0,0);
		return b;
	}

	public int getImageWidth(){ if(bimg==null)return -1; return bimg.getWidth(this); }
	public int getImageHeight(){ if(bimg==null)return -1; return bimg.getHeight(this); }


	public void paintComponent(Graphics g){
		super.paintComponent(g);
		if(bimg!=null)g.drawImage(bimg,0,0,this);
	}
	public BufferedImage cut(){
		
		BufferedImage cutImage=new BufferedImage(getWidth(),getHeight(),BufferedImage.TYPE_INT_ARGB);
		QuickSort q=new QuickSort();
		Vector y=new Vector();
		if (cut.size()>1){
			cut=q.quickSortX(cut);
			Point temp;
			Point temp2;
			for (int i=0;i<cut.size()-1;i++){
				temp=(Point) cut.elementAt(i);
				temp2=(Point) cut.elementAt(i+1);
				if (temp.x==temp2.x){
					y.add(temp);
				}else{
					y.add(temp);
					y=q.quickSortY(y);
					temp=(Point) y.elementAt(0);
					temp2=(Point) y.elementAt(y.size()-1);
					for (int j=temp.y;j<temp2.y;j++){
						
						cutImage.setRGB(temp2.x,j,(Origninalbimg.getRGB(temp2.x,j)));
					}
					y=new Vector();
				}
			}
		}
		/*JFrame jBackground=new JFrame("CUT");
		jBackground.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ImagePanel ipBackground =new ImagePanel(New);
		jBackground.getContentPane().add(ipBackground,BorderLayout.CENTER);
		jBackground.pack();
		jBackground.setVisible(true); */
		return cutImage;
	}

 public BufferedImage Channel(BufferedImage bimg, String color){
		int w=bimg.getWidth(null), h=bimg.getHeight(null);
		if (color.equals("RED")){
		for(int x=0;x<w;x++){
			for(int y=0;y<h;y++){
				int pixel=bimg.getRGB(x,y); // Alpha is also in there
				int alpha = (pixel >> 24) & 0xff;
				int red   = (pixel >> 16) & 0xff;
				pixel=(alpha<<24)+(red<<16)+(0<<8)+0;
				bimg.setRGB(x,y,pixel);
			}
		}
		}else if (color.equals("GREEN")){
			for(int x=0;x<w;x++){
				for(int y=0;y<h;y++){
					int pixel=bimg.getRGB(x,y); // Alpha is also in there
					int alpha = (pixel >> 24) & 0xff;
					int green = (pixel >> 8 ) & 0xff;
					pixel=(alpha<<24)+(0<<16)+(green<<8)+0;
					bimg.setRGB(x,y,pixel);
				}
			}
		}else{
			for(int x=0;x<w;x++){
				for(int y=0;y<h;y++){
					int pixel=bimg.getRGB(x,y); // Alpha is also in there
					int alpha = (pixel >> 24) & 0xff;
					int blue = (pixel      ) & 0xff;
					pixel=(alpha<<24)+(0<<16)+(0<<8)+blue;
					bimg.setRGB(x,y,pixel);
				}
			}
		}
		
		return bimg;
	} 

 public BufferedImage setGray(BufferedImage bimg){
		int w=bimg.getWidth(null), h=bimg.getHeight(null);

		for(int x=0;x<w;x++){
			for(int y=0;y<h;y++){
				int pixel=bimg.getRGB(x,y); // Alpha is also in there
				int red   = (pixel >> 16) & 0xff;
				int green = (pixel >> 8 ) & 0xff;
				int blue  = (pixel      ) & 0xff;
				int alpha = (pixel >> 24) & 0xff;
				if (x==0) {
					red=(int)((red+blue+green)/3);
				}



				pixel=(alpha<<24)+(red<<16)+(red<<8)+red;
				bimg.setRGB(x,y,pixel);
			}
		}
		return bimg;
	} 
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



public static double logElement(int x, int y){
	double theta=1;
   double g = 0;
   for(double ySubPixel = y - 0.5; ySubPixel < y + 0.6; ySubPixel += 0.1){
     for(double xSubPixel = x - 0.5; xSubPixel < x + 0.6; xSubPixel += 0.1){
	double s = -((xSubPixel*xSubPixel)+(ySubPixel*ySubPixel))/
	  (2*theta*theta);
	g = g + (1/(Math.PI*Math.pow(theta,4)))*
	  (1+s)*
	  Math.pow(Math.E,s);
     }
   }
   g = -g/121;
   //System.out.println(g);
   return g;
 }



public void createLOGMask2(){
	int size=7;
	double sigma=1;
	gaussian = new double [7][7];
	double d=-1/((Math.PI)*Math.pow(sigma,4));

	
	for (int i=0;i<size;i++){
	for (int j=0;j<size;j++){
		int x=(((int) size/2)*-1)+i;
		int y=(((int) size/2)*-1)+j;
		double d2=((x*x)+(y*y))/(2*sigma*sigma);
		double d3=Math.pow(Math.E,-1*(((x*x)+(y*y))/(2*sigma*sigma)));
		gaussian[i][j]=d*(1-d2)*d3;
		//System.out.println(x+" "+y+" "+gaussian[i][j]*482);
		
	
		
	}

	}

	}


public void cost(BufferedImage bimg){
	int w=bimg.getWidth(null), h=bimg.getHeight(null);
	bimg=setGray(bimg);
	CostMap=new double[w][h][8];
	double [][] ZeroCrossings=new double[w][h];
	double [][] lap=new double[w][h];
	double [][] Fx=new double[w][h];
	double [][] Fy=new double[w][h];
	double [][] G=new double[w][h];
	double [][] GD=new double[w][h];
	double MaxG=1;


	double [][] mask={{5,3,0,3,5},{3,-12,-24,-12,3},{0,-24,-40,-24,0},{3,-12,-24,-12,3},{5,3,0,3,5}};

	for (int i=0;i<w;i++){
  	  for (int j=0;j<h;j++){
			lap[i][j]=0;
			G[i][j]=0;
			Fx[i][j]=0;
			Fy[i][j]=0;
			GD[i][j]=-1;
			if ((i+1<w) &&(j+1<h) &&(i-1>0)&&(j-1>0)){
	
			
				int pixelY=bimg.getRGB(i,j+1) & 0xff;
		
				int pixelX=bimg.getRGB(i+1,j) & 0xff;
	
				int pixel9=bimg.getRGB(i,j) & 0xff;

				Fy[i][j]=pixelY-pixel9;
				Fx[i][j]=pixelX-pixel9;


				//G[i][j]=(Fx[i][j]+Fy[i][j])/2;
				G[i][j]=Math.sqrt((Math.pow(Fx[i][j],2)+Math.pow(Fy[i][j],2)));

				
				

				if (MaxG<G[i][j]){
					MaxG=G[i][j];
				}
				
                if(Fx[i][j] != 0) {
                   GD[i][j] = Math.atan(Math.abs(Fy[i][j]) / Math.abs(Fx[i][j]));
                    if(Fy[i][j] < 0.0 && Fx[i][j] < 0.0){
                    	GD[i][j] =GD[i][j]+ Math.PI;
                    } else if(Fx[i][j] < 0.0){
                    	GD[i][j]  = Math.PI - GD[i][j];
                    }else if(Fy[i][j] < 0.0){
                    	GD[i][j]  =GD[i][j]*(-1);
                    }
                } else{
                	
                
                    if(Fy[i][j] > 0){
                    	GD[i][j]  = (Math.PI/2);
                    }
                    if(Fy[i][j]  < 0){
                    	GD[i][j]  = -(Math.PI/2);
                    }
                    if(Fy[i][j]  == 0){
                    	GD[i][j]  = 0;
                    }
                }
                
				
			}
			
			for (int k=-2;k<2;k++){
				for (int k2=-2;k2<2;k2++){

						int pixel=bimg.getRGB(checkW(i,k,w),checkH(j,k2,h)) & 0xff;

					lap[i][j]+=mask[k- -2][k2- -2]*pixel;
				}
			}





		}
  	  
	}

//Zero Crossing
	for (int i=0;i<w;i++){
		for (int j=0;j<h;j++){

	         if (j-1>0){
				if ((lap[i][j-1]*lap[i][j]<0) && (Math.abs(lap[i][j])<Math.abs(lap[i][j-1]))){
					ZeroCrossings[i][j]=1;
					}
	         } if(j+1<h){
				if ((lap[i][j+1]*lap[i][j]<0) && (Math.abs(lap[i][j])<Math.abs(lap[i][j+1]))){
					ZeroCrossings[i][j]=1;
				}
	         } if(i+1<w){
				if ((lap[i+1][j]*lap[i][j]<0) && (Math.abs(lap[i][j])<Math.abs(lap[i+1][j]))){
					ZeroCrossings[i][j]=1;
				}
	         } if(i-1>0){
				if ((lap[i-1][j]*lap[i][j]<0) && (Math.abs(lap[i][j])<Math.abs(lap[i-1][j]))){
					ZeroCrossings[i][j]=1;
				}
	         }if ((i-1>0)&&(j+1<h)){
				if ((lap[i-1][j+1]*lap[i][j]<0) && (Math.abs(lap[i][j])<Math.abs(lap[i-1][j+1]))){
					ZeroCrossings[i][j]=1;
				}
	         }	if ((i-1>0)&&(j-1>0)){
				if ((lap[i-1][j-1]*lap[i][j]<0) && (Math.abs(lap[i][j])<Math.abs(lap[i-1][j-1]))){
					ZeroCrossings[i][j]=1;
				}
	         }	if ((i+1<w)&&(j+1<h)){
				if ((lap[i+1][j+1]*lap[i][j]<0) && (Math.abs(lap[i][j])<Math.abs(lap[i+1][j+1]))){
					ZeroCrossings[i][j]=1;
				}
	         }if ((i+1<w)&&(j-1>0)){
				if ((lap[i+1][j-1]*lap[i][j]<0) && (Math.abs(lap[i][j])<Math.abs(lap[i+1][j-1]))){
					ZeroCrossings[i][j]=1;
				}
	         }
	         
            					
			}
		}
	

	
	
	         
	double FZ=1;
	double FG=1;
	double FD=1;
	int i1=0;

	for (int i=0;i<w;i++){
		for (int j=0;j<h;j++){
			i1=0;
			for (int k=-1;k<2;k++){
				for (int  h1=-1;h1<2;h1++){
					
					if (k!=0 || h1!=0){
						
						if (i+k>=0 && i+k<w && j+h1>=0 && j+h1<h){
							
						FZ=ZeroCrossings[i+k][j+h1];
						FG=1D-(G[i+k][j+h1]/MaxG);
						double r;
						if (Math.abs(k*h1)==0){
							FG=FG*(1/Math.sqrt(2)); 
							r=1;
						}else{
							r= Math.sqrt(2);
						}
                        double c = GD[i][j] - (Math.PI/2);
                        double a=1;
                        double f = GD[i+k][j+h1] -  (Math.PI/2);

                        if(h != 0)  {
                            a = Math.atan((double)Math.abs(k) / (double)Math.abs(h1));
                            if(k < 0 && h1 < 0){
                                a = a+Math.PI;
                            }else if (h1 < 0){
                                a = Math.PI -a;
                            } else if(k < 0){
                                a  = a*( -1);
                            }
                        } else{
                            if(k > 0){
                                a = Math.PI/2;
                            }
                            if(k < 0){
                                a = -Math.PI/2;
                            }
                            if(k == 0){
                                a = 0;
                            }
                        }
                        double r2 = r* Math.cos(Math.abs(c - a) % (Math.PI*2));
                        if(r2< 0){
                            a = a+Math.PI;
                            r2 = r * Math.cos(Math.abs(c - a) % (Math.PI*2));
                        }
                        double r3 = r* Math.cos(Math.abs(a - f) % (Math.PI*2));
                        if(r2 > 1){
                            r2 = 1;
                        }else if(r2 < -1){
                            r2 = -1;
                        }
                        if(r3 > 1){
                            r3 = 1;
                        }else if(r3 < -1){
                            r3= -1;
                        }
                       double d5 = (2/(3*Math.PI))* (Math.acos(r2) + Math.acos(r3));
                        FD=d5;
						}else{
							FD=1;
							FZ=1;
							FG=1;
						}
						
						CostMap[i][j][i1]= ((.43 * FZ)+(.43 * FG)+(.14*FD));

						i1++;
												
					}
				}
			}
			
		}
	} 
	

    
    
    
	}


public int checkH(int y,int i,int h){
	if (y+i<0){
		return (y-i);
	}else if(y+i>=h){
		return y-i;
	}else{
		return y+i;
	}
}
public int checkW(int x,int i, int w){
	if (x+i<0){
		return (x-i);
	}else if(x+i>=w){
		return x-i;
	}else{
		return x+i;
	}
}
public boolean checkBound(Point p, int i, int j , int w, int h){
	if (((p.getX()+i)>=0) && ((p.getX()+i)<w)  && ((p.getY()+j)>=0) &&((p.getY()+j<h))){
		return true;
	}else {
		return false;
	}
}

public Point returnPoint(int x,int y){
	return (new Point(x,y));
}

 public MapPoint [][] search(int x, int y){
 	min=new Min();
 	int w=getImageWidth();
 	int h=getImageHeight();
	
	
	int u=-1;
	MapPoint p=new MapPoint(new Point(x,y),0);
	
    Point [] N= new Point[8];
    
	MapPoint [][]  NewMap=new MapPoint [getImageWidth()][getImageHeight()];
	
    p.inList=true;
	NewMap[x][y]=p;
min.add(0,p.location);

	
	
	//add(p);
	
	MapPoint temp;
    Point temp2;
	MapPoint q;
	/*
	System.out.println("COST"+min.root.cost);
	System.out.println("L"+min.root.l);
	System.out.println("R"+min.root.list.size());
	System.out.println(min.getMin());
	*/
	
   // try {
       // BufferedWriter out = new BufferedWriter(new FileWriter("TREE.j"));
	while (min.size!=0){
		
	//System.out.println(min.size);
	    temp2=min.getMin();
//	    System.out.println("FIRST "+temp2.x+"  "+temp2.y+"   "+L2[temp2.x].size());
	    //System.out.println(L2[temp2.x]);
	
		q=(MapPoint) NewMap[temp2.x][temp2.y];
		NewMap[temp2.x][temp2.y].expand();
		q.expand();
		//NewMap[q.getX()][q.getY()]=q;
		
		//NewMap[q.getX()][q.getY()].expanded=true;
		for (int i=0;i<8;i++){
			if (i==0){
				if (checkBound(q.location,-1,-1,w,h)){
						if (NewMap[q.getX()-1][q.getY()-1]==null){
							NewMap[q.getX()-1][q.getY()-1] =new MapPoint(returnPoint(q.getX()-1,q.getY()-1),CostMap[q.getX()][q.getY()][i]);
							N[i]=new Point(q.getX()-1,q.getY()-1);
						}else if (! NewMap[q.getX()-1][q.getY()-1].expanded){
							N[i]=new Point(q.getX()-1,q.getY()-1);
						}else{
							N[i]=null;
						}
					
				}else{
					N[i]=null;
				}
			}else if (i==1){
				if (checkBound(q.location,-1,0,w,h)){
					 if ((NewMap[q.getX()-1][q.getY()]==null)){
					 	NewMap[q.getX()-1][q.getY()]=new MapPoint(returnPoint(q.getX()-1,q.getY()),CostMap[q.getX()][q.getY()][i]);
					 	N[i]=new Point(q.getX()-1,q.getY());
					 }else if (! NewMap[q.getX()-1][q.getY()].expanded){
							N[i]=new Point(q.getX()-1,q.getY());
						}else{
							N[i]=null;
						}
					
				}else{
					N[i]=null;
				}
			}else if (i==2){
				if (checkBound(q.location,-1,1,w,h)){
					if (NewMap[q.getX()-1][q.getY()+1]==null){
						NewMap[q.getX()-1][q.getY()+1]=new MapPoint(returnPoint(q.getX()-1,q.getY()+1),CostMap[q.getX()][q.getY()][i]);
						N[i]=new Point(q.getX()-1,q.getY()+1);
					}else if (! NewMap[q.getX()-1][q.getY()+1].expanded){
						N[i]=new Point(q.getX()-1,q.getY()+1);
					}else{
						N[i]=null;
					}
					
				}else{
					N[i]=null;
				}
			}else if(i==3){
				if (checkBound(q.location,0,-1,w,h)){
					if (NewMap[q.getX()][q.getY()-1]==null){
						NewMap[q.getX()][q.getY()-1]=new MapPoint(returnPoint(q.getX(),q.getY()-1),CostMap[q.getX()][q.getY()][i]);
						N[i]=new Point(q.getX(),q.getY()-1);
					}else if(! NewMap[q.getX()][q.getY()-1].expanded){
						
						N[i]=new Point(q.getX(),q.getY()-1);
					}else{
						N[i]=null;
					}				
				}else{
					N[i]=null;
				}
			}else if(i==4){
				if (checkBound(q.location,0,1,w,h)){
					if (NewMap[q.getX()][q.getY()+1]==null){
						NewMap[q.getX()][q.getY()+1]=new MapPoint(returnPoint(q.getX(),q.getY()+1),CostMap[q.getX()][q.getY()][i]);
						N[i]=new Point(q.getX(),q.getY()+1);
				}else if(! NewMap[q.getX()][q.getY()+1].expanded){
					N[i]=new Point(q.getX(),q.getY()+1);
				}else{
					N[i]=null;
				}
					
				}else{
					N[i]=null;
				}
			}else if(i==5){
				if (checkBound(q.location,1,-1,w,h)){
					if (NewMap[q.getX()+1][q.getY()-1]==null){
						NewMap[q.getX()+1][q.getY()-1]=new MapPoint(returnPoint(q.getX()+1,q.getY()-1),CostMap[q.getX()][q.getY()][i]);
						N[i]=new Point(q.getX()+1,q.getY()-1);
				}else if((! NewMap[q.getX()+1][q.getY()-1].expanded)){
					N[i]=new Point(q.getX()+1,q.getY()-1);
				}else{
					N[i]=null;
				}
				
				}else{
					N[i]=null;
				}
			}else if(i==6){
				if (checkBound(q.location,1,0,w,h)){
					if (NewMap[q.getX()+1][q.getY()]==null){
						NewMap[q.getX()+1][q.getY()]=new MapPoint(returnPoint(q.getX()+1,q.getY()),CostMap[q.getX()][q.getY()][i]);
						N[i]=new Point(q.getX()+1,q.getY());
				}else if (! NewMap[q.getX()+1][q.getY()].expanded){
					N[i]=new Point(q.getX()+1,q.getY());
				}else{
					N[i]=null;
				}

				}else{
					N[i]=null;
				}
			}else if(i==7){
				if (checkBound(q.location,1,1,w,h)){
					if (NewMap[q.getX()+1][q.getY()+1]==null){
						NewMap[q.getX()+1][q.getY()+1]=new MapPoint(returnPoint(q.getX()+1,q.getY()+1),CostMap[q.getX()][q.getY()][i]);
						N[i]=new Point(q.getX()+1,q.getY()+1);
				}else if(! NewMap[q.getX()+1][q.getY()+1].expanded){
					N[i]=new Point(q.getX()+1,q.getY()+1);
				}else{
					N[i]=null;
				}
				
				}else{
					N[i]=null;
				}
			}
			
		}
		
		double tempCost=-1;
		for (int i=0;i<8;i++){
			if (N[i]!=null){
					temp=NewMap[N[i].x][N[i].y];
				if (!temp.expanded){
					tempCost=q.getCost()+temp.getCost();
					
                    if ((temp.inList) && (tempCost < temp.getCost())){
                    	//System.out.println("SENDING FOR DELETE "+point1.x);
                    	min.Delete(min.find(temp.getCost()),temp.location);
                        //remove(point1);
                        temp.inList=false;
                    }
                    if(! temp.inList) {
                        
                        NewMap[N[i].x][N[i].y]. pointer= q.location;
                        	//out.write (code(N[i].getX(),N[i].getY(),tempCost));
                        	//out.write("\n");
                        NewMap[N[i].x][N[i].y].newCost(tempCost);
                        NewMap[N[i].x][N[i].y].inList=true;
                            //temp.newCost (tempCost);
                            //temp.pointer = q.location;
                            min.add(tempCost,N[i]);
                          //System.out.println("SIZE AFTER ADD IS "+L2[code(temp.getX(),temp.getY(),temp.getCost())].size());
                    }
				}
			}
		}
	}
    //}catch (Exception e){
    	//}	
    
				
				
				
				
				/*
				if ((NewMap[N[i].getX()][N[i].getY()]==null) ||(!(NewMap[N[i].getX()][N[i].getY()].expanded))){
				tempCost=N[i].getCost()+q.getCost();
				//u=q2.Position(L,N[i],0,L.size());
				//u=q2.Exists(L,N[i].location);
				//int t=code(N[i].getX(),N[i].getY());
				if (L2[t]==null){
					u=-1;
				}else{
					u=1;
				}
				if (u!=-1){
					int u2=q2.Search(L2[t],N[i]);
					if (u2!=-1){
					temp=(MapPoint) L2[t].elementAt(u2);
					if ((temp.pointer!=null) && (temp.getCost()>tempCost)){
						L2[t].removeElementAt(u2);
						if (L2[t].size()==0) L2[t]=null;
						u=-1;
						L.removeElementAt(q2.Exists(L,N[i].location));
					}
					}
				}
				if (u==-1){
					N[i].newCost(tempCost);
					N[i].pointer=q.location;
					//u=code(N[i].getX(),N[i].getY());
					if (L2[u]==null){
						L2[u]=new Vector();
						L2[u].addElement(N[i]);
					}else{
						L2[u].addElement(N[i]);
					}

					L.addElement(N[i]);
				}
			}
			}
				
		}
	}*/
	/*	
    try {
        BufferedWriter out = new BufferedWriter(new FileWriter("Map.j"));
        for (int i=0;i<w;i++){
        	for (int j=0;j<h;j++){
    
        			if (NewMap[i][j]!=null){
							  out.write("X ="+i+"  Y= "+j+"  "+NewMap[i][j].pointer);
        			out.write("\n");
        			
        		}else{
					  out.write("X ="+i+"  Y= "+j+"  "+"NULL");
	        			out.write("\n");
        		}
        	}
        }
        out.close();
        
        
    } catch (Exception e) {
    	System.out.println("HEREEEEEEE"+e);
    } */
	return NewMap;
	}
	
	

	
public void LiveWire(int x,int y){
	
}
public void createZer2(){
	gaussian = new double [4][4];
	gaussian[0][0]=0;
	gaussian[0][1]=1;
	gaussian[0][2]=0;
	gaussian[1][0]=1;
	gaussian[1][1]=-4;
	gaussian[1][2]=1;
	gaussian[2][0]=0;
	gaussian[2][1]=1;
	gaussian[2][2]=0;
	
}
public void createZeroMask(){
	int size=7;
	double sigma=1;
	gaussian = new double [size][size];
	double d=-1/((Math.PI)*Math.pow(sigma,4));

	
	for (int i=0;i<size;i++){
	for (int j=0;j<size;j++){
		int x=(((int) size/2)*-1)+i;
		int y=(((int) size/2)*-1)+j;
		double d2=((x*x)+(y*y))/(2*sigma*sigma);
		double d3=Math.pow(Math.E,-1*(((x*x)+(y*y))/(2*sigma*sigma)));
		gaussian[i][j]=d*(1-d2)*d3;
		//System.out.println(x+" "+y+" "+gaussian[i][j]*482);
		
	
		
	}

	}

	}

public void createMask(){
	double sigma=1.0;
	//int size=(int) (((sigma*4)+1)/1.5);
	int size=5;
	
	gaussian = new double [size][size];

	double s=1/(2*Math.PI*Math.pow(sigma,2));
	for (int i=0;i<size;i++){
		for (int j=0;j<size;j++){
			int x=(((int) size/2)*-1)+i;
			int y=(((int) size/2)*-1)+j;
			double s2=Math.pow(Math.E,-1*(((x*x)+(y*y))/(2*Math.pow(sigma,2))));
			gaussian[i][j]=s*s2;
			
		}
	}

	}
public static double singlePixel(BufferedImage bimg, int x, int y, double [][] mask, int size ){
double output = 0;
int x1=((int) size/2)*-1;
int y1=((int) size/2)*-1;

for(int i=0;i<size;++i){
for(int j=0;j<size;++j){


	int pixel=bimg.getRGB(x+x1,y+y1);
output = output + (pixel * mask[i][j]);
y1++;
}
y1=((int) size/2)*-1;
x1++;
}
return output;
}
public static double GaussianSinglePixel(BufferedImage bimg, int x, int y, double [][] mask, int size ){
	double output = 0;
	int x1=((int) size/2)*-1;
	int y1=((int) size/2)*-1;

	for(int i=0;i<size;++i){
	for(int j=0;j<size;++j){


		int pixel=bimg.getRGB(x+x1,y+y1);
		int red   = (pixel >> 16) & 0xff;
	output = output + (red * mask[i][j]);
	y1++;
	}
	y1=((int) size/2)*-1;
	x1++;
	}
	return output;
	}

public BufferedImage applyLOG2(double sigma)
{
	int sizeOfArray;
	double gaussArray [][];
	
	//finds the size of the array need for this value of sigma and create the array
	sizeOfArray = getArraySize(sigma,1);
	gaussArray = new double[sizeOfArray][sizeOfArray];
	
	//fill the 'gaussArray' appropriately
	gaussArray=fillArray(sigma,sizeOfArray,1);
	
	//normalize the gaussian array
	gaussArray=normalizeArray(gaussArray,sizeOfArray,gaussNormalizingValue);

	//apply the mask to the image
	bimg = applyMask(gaussArray,sizeOfArray);
	
	return bimg;
}

/**
 * Creates an array of gaussian values using the gasuian functions and the user input sigma value.
 * Applies the normalized array to the foreground image.
 * Repaints it to screen.
 * @param sigma the value to be used for sigma found in the gaussian and laplacian of gaussian functions
 */
public BufferedImage applyGaussianSmoothing(double sigma)
{
	int sizeOfArray;
	double gaussArray [][];
	
	//finds the size of the array need for this value of sigma and create the array
	sizeOfArray = getArraySize(sigma,0);
	gaussArray = new double[sizeOfArray][sizeOfArray];
	
	//fill the 'gaussArray' appropriately
	gaussArray=fillArray(sigma,sizeOfArray,0);
	
	//normalize the gaussian array
	gaussArray=normalizeArray(gaussArray,sizeOfArray,gaussNormalizingValue);

	//apply the mask to the image
	bimg = applyMask(gaussArray,sizeOfArray);
	
	//places the image on the FGMask frame
   //ICInternalFrame icMask = icApp.internalForegroundFrameMask;
   //ImagePanel ipMask = (ImagePanel)icMask.getContentPane().getComponent(0);
   //ipMask.setImage(bimg);
	return bimg;
}

private BufferedImage applyMask(double [][] array, int sizeOfArray)
{
	int pixel, alpha, red, green, blue, grey;
	double pixelSum;
	
	//get the image in the frame internalForegroundFrame and store as a BufferedImage
   //ICInternalFrame ic = icApp.internalForegroundFrameMask;
   //ImagePanel ip = (ImagePanel)ic.getContentPane().getComponent(0);
   //BufferedImage bimg = ip.getImage();
   
   BufferedImage bimgEdited = copyImage();
   int w=bimgEdited.getWidth(null), h=bimgEdited.getHeight(null);
   //iterates through all of the positions (x,y) that the top left corner of 'array' can possibly be
   for (int x=0; x<=(w-sizeOfArray); x++)
	{
		for (int y=0; y<=(h-sizeOfArray); y++)
		{
			pixelSum=0;
			//applies cross-correlation to 'array' (through positions (u,v)) and the image
			for (int u=0; u<sizeOfArray; u++)
			{
				for (int v=0; v<sizeOfArray; v++)
				{
					pixel=bimg.getRGB(x+u,y+v);
			        alpha = (pixel >> 24) & 0xff;
			        red   = (pixel >> 16) & 0xff;
			        green = (pixel >> 8 ) & 0xff;
			        blue  = (pixel      ) & 0xff;
			        
			        //find the grey scale value of the pixel
			        grey=(red+green+blue)/3;
			        
			        //rallys the sum of the cross-correlation
			        pixelSum=pixelSum+(grey*(array[u][v]));
				}
			}
			alpha=255;
	        red=(int)pixelSum;
	       	green=(int)pixelSum;
	      	blue=(int)pixelSum;
	      	
			//apply the new pixel to the center
			pixel=(alpha<<24)+(red<<16)+(green<<8)+blue;
		    bimgEdited.setRGB(x+(int)Math.floor(sizeOfArray/2),y+(int)Math.floor(sizeOfArray/2),pixel);
		}
	}
   
   return bimgEdited;
}

/**
 * Normalizes the array by dividing each element by the sum of all elements in the aray
 * @param array The array to be normalized
 * @param sizeOfArray The size of the array that is to be normalized
 * @param normalizingValue The sum of all the elemnts in the array to be normalized
 * @return Returns the normalized array
 */
private double [][] normalizeArray(double [][] array,int sizeOfArray , int normalizingValue)
{
	double [][] normalizedArray = new double[sizeOfArray][sizeOfArray];
	
	for (int x=0; x<sizeOfArray; x++)
	{
		for (int y=0; y<sizeOfArray; y++)
		{
			normalizedArray[x][y]=(array[x][y]/normalizingValue);
		}
	}
	
	return normalizedArray;
}

/**
 * Fills the input array of size 'sizeOfArray' using the appropriate function
 * @param sigma The sigma value of input by the user
 * @param sizeOfArray The size of the square array
 * @return The 2D filled in array 
 */
private double [][] fillArray(double sigma, int sizeOfArray, int flag)
{
	double array [][] = new double[sizeOfArray][sizeOfArray];
	 	
	//fill the array
	int gaussElementValue;
	for (int x=0; x<=Math.ceil(sizeOfArray/2); x++)
	{
		for (int y=0; y<=Math.ceil(sizeOfArray/2); y++)
		{
			if (flag==0)
			{
				//find the value
				gaussElementValue=gaussianFunc(x,y,sigma);

				//assign the 'gaussElementValue' to x, y and its odd and even reflections.
				array[correspondingX(x,sizeOfArray)][correspondingY(y,sizeOfArray)]=gaussElementValue;
				array[correspondingX((-1)*x,sizeOfArray)][correspondingY(y,sizeOfArray)]=gaussElementValue;
				array[correspondingX(x,sizeOfArray)][correspondingY((-1)*y,sizeOfArray)]=gaussElementValue;
				array[correspondingX((-1)*x,sizeOfArray)][correspondingY((-1)*y,sizeOfArray)]=gaussElementValue;
				
				//increase the 'nomalizinValue' so that it ends up being the sum of all elements in the array
				gaussNormalizingValue=gaussNormalizingValue+(4*gaussElementValue);
			}
			if (flag==1)
			{
				//find the value
				gaussElementValue=LoGFunc(x,y,sigma);

				//assign the 'gaussElementValue' to x, y and its odd and even reflections.
				array[correspondingX(x,sizeOfArray)][correspondingY(y,sizeOfArray)]=gaussElementValue;
				array[correspondingX((-1)*x,sizeOfArray)][correspondingY(y,sizeOfArray)]=gaussElementValue;
				array[correspondingX(x,sizeOfArray)][correspondingY((-1)*y,sizeOfArray)]=gaussElementValue;
				array[correspondingX((-1)*x,sizeOfArray)][correspondingY((-1)*y,sizeOfArray)]=gaussElementValue;
				
				//increase the 'nomalizinValue' so that it ends up being the sum of all elements in the array
				gaussNormalizingValue=gaussNormalizingValue+(4*gaussElementValue);
			}
		}
	}
	
	return array;
}

/**
 * Finds the 'x' value that corresponds to the 'gaussArray' from the method applyGaussianSmoothing(double)
 * @param x The integer value corresponding to 'x' from the gaussian function
 * @param size The length of the array
 * @return An integer value that corresponds to the 'gaussArray' from the method applyGaussianSmoothing(double)
 */


private int correspondingX(int x, int size)
{
	int middle = (int)Math.floor(size/2);
	return ((-1)*x)+middle;
}

/**
 * Finds the 'y' value that corresponds to the 'gaussArray' from the method applyGaussianSmoothing(double)
 * @param y The integer value corresponding to 'x' from the gaussian function
 * @param size The length of the array
 * @return An integer value that corresponds to the 'gaussArray' from the method applyGaussianSmoothing(double)
 */
private int correspondingY(int y, int size)
{
	int middle = (int)Math.floor(size/2);
	return y+middle;
}

/**
 * Calculates an appropriate size for the array that depends on sigma.
 * @param sigma The sigma value (square root of variance) of the gaussian function
 * @return An integer value that will be the size of the array
 */
private int getArraySize(double sigma, int flag)
{
	int ctr=0;
	while (true) //will check the values that will be put in the corner at size ctr until this value reaches 1
	{
		if (flag==0){if (gaussianFunc(ctr,ctr,sigma)<=1) break;}
		if (flag==1){if (LoGFunc(ctr,ctr,sigma)<=1) break;}
		ctr=ctr+1;
	}
	ctr=ctr+1;
	return ctr+(ctr-1);
}

/**
 * Calculates the the gaussian value at (x,y) with the specified sigma.
 * @param x The x value of the gaussian function
 * @param y The y value of the gaussian function
 * @param sigma The sigma value (square root of variance) of the gaussian function
 * @return the value of the gaussian function using the inputs x, y and sigma
 */
private int gaussianFunc(int x, int y, double sigma)
{
	double variance = Math.pow(sigma,2);
	int gaussVal=(int)((1/(2*Math.PI*variance))*(Math.exp((-1)*((x*x+y*y)/(2*variance)))));
	//int gaussVal = (int)((1/(2*Math.PI*variance))*(Math.exp((Math.pow(x,2)+Math.pow(y,2))/(2*variance))));
	//int gaussVal = (int)((90)*(Math.exp((-1)*(Math.pow(x,2)+Math.pow(y,2))/(2*variance))));
	if (gaussVal==0) return 1;
	return gaussVal;
}

/**
 * Calculates the the LoG value at (x,y) with the specified sigma.
 * @param x The x value of the LoG function
 * @param y The y value of the LoG function
 * @param sigma The sigma value (square root of variance) of the LoG function
 * @return the value of the LoG function using the inputs x, y and sigma
 */
private int LoGFunc(int x, int y, double sigma)
{
	double LoGVal=(-1)*(1/((Math.PI)*(Math.pow(sigma,4))))*(1-((x*x+y*y)/(2*sigma*sigma)))
		*(Math.exp((-1)*((x*x+y*y)/(2*sigma*sigma))));
	if (LoGVal==0) return 1;
	return (int)LoGVal;
}

//remove
public BufferedImage applyLOG2(BufferedImage bimg){
	int size=7;
	createLOGMask2();
	int w=bimg.getWidth(null), h=bimg.getHeight(null);

	double [][] data=new double[w][h];
	double min=0;
	double max=0;
	int g=0;
	int x1=((int) size/2);
	int y1=((int) size/2);
	double [] d=new double [h*w];
	BufferedImage b=new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
	double SumRed=0;
	for (int i=x1;i<w-size;i++){
		for (int j=y1;j<h-size;j++){
			//System.out.println("X "+i+"  Y "+j);
			data[i][j]=singlePixel(bimg,i,j,gaussian,size);
		}
	}



		for (int i=0;i<w;i++){
			for(int j=0;j<h;j++){
				d[g]=data[i][j];
				g++;
		}

	}
		

		
		
	QuickSort q=new QuickSort();
	d=q.quickSort(d);
	max=d[(int)(d.length*.95)];
	min=d[(int)(d.length*.10)];

	System.out.println("MAX IS "+max);
	System.out.println("MIN IS "+min);

	for(int i=0;i<w;i++){
	for(int j=0;j<h;j++){
	int pixel=(int) (((data[i][j]-min))*(255/(max-min)));
	if (pixel>=0){	

	int pixel2=(255<<24)+(0<<16)+(0<<8)+0;
	b.setRGB(i,j,pixel2);
	}else{
	int pixel2=(255<<24)+(190<<16)+(190<<8)+190;
	b.setRGB(i,j,pixel2);
	}
	}
	}


	return b;
	}




public BufferedImage ZeroCrossing(BufferedImage bimg){
	int size=7;
	createZeroMask();
	//createZer2();
	int w=bimg.getWidth(null), h=bimg.getHeight(null);
	double [][] data=new double[w][h];
	double min=0;
	double max=0;
	int g=0;
	int x1=((int) size/2);
	int y1=((int) size/2);
	BufferedImage b=new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
	double SumRed=0;
	for (int i=x1;i<w-size;i++){
		for (int j=y1;j<h-size;j++){

			data[i][j]=singlePixel(bimg,i,j,gaussian,size);
		}
	}


	for(int i=x1;i<w-size;i++){
	for(int j=y1;j<h-size;j++){
		if (data[i][j]==0){
			System.out.println(i+"  "+j);
		}
		if ((data[i+1][j]*data[i][j])<0){
			if (Math.abs(data[i][j])<Math.abs(data[i+1][j])){
			int pixel2=(255<<24)+(255<<16)+(0<<8)+0;
			b.setRGB(i,j,pixel2);
			}
		}else if ((data[i][j-1]*data[i][j])<0){
			if (Math.abs(data[i][j])<Math.abs(data[i][j-1])){
			int pixel2=(255<<24)+(255<<16)+(0<<8)+0;
			b.setRGB(i,j,pixel2);
			}
		}else if ((data[i][j]*data[i-1][j])<0){
			if (Math.abs(data[i][j])<Math.abs(data[i-1][j])){
			int pixel2=(255<<24)+(255<<16)+(0<<8)+0;
			b.setRGB(i,j,pixel2);
			}
		}else if ((data[i][j]*data[i][j+1])<0){
			if (Math.abs(data[i][j])<Math.abs(data[i][j+1])){
			int pixel2=(255<<24)+(255<<16)+(0<<8)+0;
			b.setRGB(i,j,pixel2);
			}
		}
		/*else if ((data[i][j]*data[i+1][j+1])<0){
			if (Math.abs(data[i][j])<Math.abs(data[i+1][j+1])){
				int pixel2=(255<<24)+(255<<16)+(0<<8)+0;
				b.setRGB(i,j,pixel2);
				}
		}else if ((data[i][j]*data[i+1][j-1])<0){
				if (Math.abs(data[i][j])<Math.abs(data[i+1][j-1])){
					int pixel2=(255<<24)+(255<<16)+(0<<8)+0;
					b.setRGB(i,j,pixel2);
					}
		}else if ((data[i][j]*data[i-1][j-1])<0){
			if (Math.abs(data[i][j])<Math.abs(data[i-1][j-1])){
				int pixel2=(255<<24)+(255<<16)+(0<<8)+0;
				b.setRGB(i,j,pixel2);
				}
	}else if ((data[i][j]*data[i-1][j+1])<0){
		if (Math.abs(data[i][j])<Math.abs(data[i-1][j+1])){
			int pixel2=(255<<24)+(255<<16)+(0<<8)+0;
			b.setRGB(i,j,pixel2);
			}
}*/
		
		}
	}


	return b;
	}


/**
 * Computes the image gradient of the BufferedImage 'bimg' and then returns 
 * it to be displayed after being normalized and processed so that it can be
 * viewed.
 * @param bimg The BufferedImage will be processed to 
 * compute the image gradient. 
 * @return The visualized result of computing the image gradient of 'bimg'.
 */
public BufferedImage applyGradient(BufferedImage bimg){
	int w=bimg.getWidth(null), h=bimg.getHeight(null);
	BufferedImage b=setGray(bimg);
	BufferedImage output=new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
	
	double Gx=0;
	double Gy=0;
	double G=0;
	
	/* find the largest and smallest values for both Gx and Gy: */
	//finds the first pixel
	int pixel=bimg.getRGB(0,0) & 0xff;
	int pixelUnder=bimg.getRGB(1,0) & 0xff;
	int pixelRight=bimg.getRGB(0,1) & 0xff;
	
	//sets the first value for Gx and Gy to be the smallest and largest
	double smallestGx=pixelRight-pixel;
	double largestGx=smallestGx;
	double smallestGy=pixelUnder-pixel;
	double largestGy=smallestGy;
	
	//loop that searches for the largest and smallest values for both Gx and Gy
	for (int i=0; i<w-1; i++)
	{
		for (int j=0; j<h-1; j++)
		{   
			pixel=bimg.getRGB(i,j) & 0xff;
			pixelUnder=bimg.getRGB(i+1,j) & 0xff;
			pixelRight=bimg.getRGB(i,j+1) & 0xff;
			
			Gx=pixelRight-pixel;
			if (Gx<smallestGx) smallestGx=Gx;
			else if (Gx>largestGx) largestGx=Gx;
			
			Gy=pixelUnder-pixel;
			if (Gy<smallestGy) smallestGy=Gy;
			else if (Gy>largestGy) largestGy=Gy;
		}
	} //by this point largest and smallest of Gx and Gy are found
	
	/* apply normalized image gradient to the image: */
	for (int i=0; i<w-1; i++)
	{
		for (int j=0; j<h-1; j++)
		{   
			pixel=bimg.getRGB(i,j) & 0xff;
			pixelUnder=bimg.getRGB(i+1,j) & 0xff;
			pixelRight=bimg.getRGB(i,j+1) & 0xff;
			
			Gx=pixelRight-pixel;
			Gy=pixelUnder-pixel;
			
			Gx=normalize(0,255,smallestGx,largestGx,Gx);
			Gy=normalize(0,255,smallestGy,largestGy,Gy);
			
			G=(Gx+Gy)/2;
			
			pixel=(255<<24)+((int)Gx<<16)+((int)Gx <<8)+(int)Gx;
			output.setRGB(i,j,pixel);
		}
	}
	
	return output;
}

/**
 * Calculates the histogram for a BufferedImage. The indices of
 * the histogram represent the possible pixel intensity values 
 * and the values at those indices represent the number of pixels
 * that have those values. This is used to determine a percent of
 * pixels that have a certain percentage of the intensity value.
 * @param b The BufferedImage to create a histogram for.
 * @return A histogram of integer values.
 */
private int [] getHistogram(BufferedImage b)
{
	int [] pixelValue = new int[256];
	
	int w=bimg.getWidth(null), h=bimg.getHeight(null);
	
	for (int i=0;i<w;i++){
		for (int j=0;j<h;j++){
			int pixel=bimg.getRGB(i,j) & 0xff;
			pixelValue[pixel]=pixelValue[pixel]+1;
		}
	}
	for (int i=0;i<256;i++){System.out.print(pixelValue[i]+"-");}
	System.out.println();
	return pixelValue;
}

/**
 * This function normalizes the pixel so that it's new "normalized"
 * value will be between 'a' and 'b'.
 * @param c The minimum size of all pixels of type 'pixelIn' from the image.
 * @param d The maximum size of all pixels of type 'pixelIn' from the image.
 * @param pixelIn The pixel to be normalized
 * @return The normalized pixel.
 */
private int normalize(double a, double b, double c, double d, double pixelIn)
{
	double E = d-c;
	
	if (E==0) E=1;

	return (int)((pixelIn-c)*((b-a)/(E))+a);
}

/**
 * 
 * @param bimg The BufferedImage that will be processed to 
 * compute the magnitude of the image gradient. 
 * @return The viewable BufferedImage that was processed to compute
 * the magnitude of the image gradient.
 */
public BufferedImage applyMagnitudeOfGradient(BufferedImage bimg){
	int w=bimg.getWidth(null), h=bimg.getHeight(null);
	bimg=setGray(bimg);
	BufferedImage b=new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
	double Gx=0;
	double Gy=0;
	double G=0;
	for (int i=1;i<w-1;i++){
		for (int j=1;j<h-1;j++){

			int pixel=bimg.getRGB(i+1,j+1) & 0xff;
			int pixel2=bimg.getRGB(i-1,j+1)& 0xff;
			int pixel3=bimg.getRGB(i-1,j)& 0xff;
			int pixel4=bimg.getRGB(i+1,j)& 0xff;
			int pixel5=bimg.getRGB(i+1,j-1)& 0xff;
			int pixel6=bimg.getRGB(i-1,j-1)& 0xff;
			int pixel7=bimg.getRGB(i,j-1)& 0xff;;
			int pixel8=bimg.getRGB(i,j+1)& 0xff;

			Gx=pixel-pixel2+(-2*pixel3)+(2*pixel4)+pixel5-pixel6;
			if (Gx < 0) Gx = -Gx;
			Gy=pixel-pixel2+(-2*pixel7)+(2*pixel8)+pixel5-pixel6;
			if (Gy < 0) Gy = -Gy;
			G=(Gy+Gx);
			if (i>10 && i<12 && j>9 && j<11){
				System.out.println(Gy);
				System.out.println(Gx);
				System.out.println((Gy*Gy)+(Gx*Gx));
			}
			if (G > 255) G=255;
			//System.out.println(G);
			pixel=(255<<24)+((int)G<<16)+((int)G <<8)+(int)G;
			b.setRGB(i,j,pixel);
			
		}
	}
	return b;
}







public BufferedImage applyGaussian(BufferedImage bimg){
createMask();
int size=5;
int w=bimg.getWidth(null), h=bimg.getHeight(null);
BufferedImage b=new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
double SumRed=0;
int x1=((int) size/2);
int y1=((int) size/2);
for (int i=x1;i<w-size;i++){
	for (int j=y1;j<h-size;j++){
		
		
		int temp=(int)(GaussianSinglePixel(bimg,i,j,gaussian,size));
		int pixel=(255<<24)+(temp<<16)+(temp<<8)+temp;
		b.setRGB(i,j,pixel);
	}
}



return b;
}
/*
public int pop(MinList m)
{
    int i = m.pop();
    if(i < 0)
    {
        return -1;
    } else
    {
                    size--;
        return i;
    }
}*/
public Point getMin(){
	size--;
	return min.getMin();

}









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
	
	/**
	 * Stores 4 coordinates as they are clicked as 'Coord' objects.
	 * @param x The x position of the point licked in the background
	 * @param y The y position of the point licked in the background
	 */
	public void setCoords(int x, int y,ImageCompositing icApp )
	{
		
		
		
		if (clickCtr==0)
		{
			clickA=new Coord(x,y,clickCtr);
		}
		else if (clickCtr==1)
		{
			clickB=new Coord(x,y,clickCtr);
		}
		else if (clickCtr==2)
		{
			clickC=new Coord(x,y,clickCtr);
		}
		else//if (clickCtr==3)
		{
			clickD=new Coord(x,y,clickCtr);
			
			mappingComplete=true;
			/*System.out.println("("+clickA.getX()+","+clickA.getY()+")\n"+
							"("+clickB.getX()+","+clickB.getY()+")\n"+
							"("+clickC.getX()+","+clickC.getY()+")\n"+
							"("+clickD.getX()+","+clickD.getY()+")\n");*/ 
			// test works - clicked points get stored properly

			//get the highest and lowest x's and y's
			lowHigh=getHighsAndLows(clickA,clickB,clickC,clickD);
			smallestX=lowHigh[0];
			smallestY=lowHigh[1];
			
			icApp.setMapped(lowHigh[0],lowHigh[1]);
			
			
		}
		
	}
	
	/**
	 * Creates the warped image
	 * @param texture the text that is used to create the warped image
	 * @param distImg if true applies distance shading to the image
	 * @param red the red value of the distance shading
	 * @param green the green value of the distance shading
	 * @param blue the blue value of the distance shading
	 * @return
	 */
	public BufferedImage createMappedImage(BufferedImage texture,boolean distImg,int red,int green,int blue)
	{
		//store the coordinate of the texture image
		int w=texture.getWidth(null), h=texture.getHeight(null);
		clickATexture = new Coord(0,0,0);
		clickBTexture = new Coord(w,0,1);
		clickCTexture = new Coord(w,h,2);
		clickDTexture = new Coord(0,h,3);
		
		//declare BufferedImage that stores the warped output
		BufferedImage output = new BufferedImage(lowHigh[2]-lowHigh[0], lowHigh[3]-lowHigh[1], BufferedImage.TYPE_INT_ARGB);
		BufferedImage distShading = new BufferedImage(lowHigh[2]-lowHigh[0], lowHigh[3]-lowHigh[1], BufferedImage.TYPE_INT_ARGB);

		relatePointsToOutput(lowHigh[0],lowHigh[1]);
		
		//create a texture map object
		TextureMap t=new TextureMap
			(0,0,w,0,w,h,0,h,
			 clickA.getRelX(),clickA.getRelY(),clickB.getRelX(),clickB.getRelY(),
			 clickC.getRelX(),clickC.getRelY(),clickD.getRelX(),clickD.getRelY());
		
		//if(distImg) return setDistImage(texture,red,green,blue);
		
		//iterate through all pixels of 'output'
		for (int y=0;y<output.getHeight(null);y++)
		{
			for (int x=0;x<output.getWidth(null);x++)
			{
				//if inRange then apply to the output because 'x' and 'y' are in a region
				//where the user clicked.
				if(inRange(t.texU(x,y),t.texV(x,y),w,h))
				{
					
					try
					{
						int pixel=texture.getRGB((int)Math.floor(t.texU(x,y)),(int)Math.floor(t.texV(x,y))); // Alpha is also in there
						output.setRGB(x,y,pixel);
						distShading.setRGB(x,y,(255<<24)+(red<<16)+(green<<8)+blue);
						//REMOVESystem.out.println("PASSED:  "+w+"x"+h+"  ("+(int)Math.floor(t.texU(x,y))+","+(int)Math.floor(t.texV(x,y))+")");
					}
					catch(ArrayIndexOutOfBoundsException e)
					{
						//REMOVESystem.out.println("FAILED:  "+w+"x"+h+"  ("+(int)Math.floor(t.texU(x,y))+","+(int)Math.floor(t.texV(x,y))+")");
					}
				}
				else
				{
					output.setRGB(x,y,0<<24);
					distShading.setRGB(x,y,0<<24);
					//REMOVESystem.out.println("ALPHA:   "+w+"x"+h+"  ("+(int)Math.floor(t.texU(x,y))+","+(int)Math.floor(t.texV(x,y))+")");
				}
			}
		}
		
		
		return output;
	}
	
	/**
	 * Distance Shading:<br>
	 * This is a feature that makes the warped image appear to be in 3D space.
	 * It does this by making the area next to the smaller edge from the warped
	 * image dark.  <br>There are 2 types of options: <br>1)Can choose the colour
	 * the shading. This will simulate fog.<br>2)Can choose the ratio of the image that
	 * gets the gradient shading aplied to it.
	 * @param img the image to apply distance shading to
	 * @param red the red value of the distance shading
	 * @param green the green value of the distance shading
	 * @param blue the blue value of the distance shading
	 * @param gradSize the size of the distance shading
	 * @return
	 */
	public BufferedImage setDistImage(BufferedImage img, int red, int green, int blue, double gradSize)
	{
	    
	    
	    int[] sEdge=findSmallestEdge();
	    
	    BufferedImage output = new BufferedImage(img.getWidth(null),img.getHeight(null),BufferedImage.TYPE_INT_ARGB);
	    
	    //output = createUniColouredBufferedImage(red,green,blue,img.getWidth(null),img.getHeight(null));
	    //System.out.println("e1="+sEdge[0]+" e2="+sEdge[1]+" height="+img.getHeight(null));
	    if(sEdge[0]==0 && sEdge[1]==1)
	    {
	        for (int y=0;y<(img.getHeight(null)*gradSize);y++)
			{
				for (int x=0;x<img.getWidth(null);x++)
				{
				    double coeff=y/(img.getHeight()*gradSize);
				    double alpha=255-(255*(coeff));
				    //System.out.println("("+x+","+y+") "+ (int)alpha);
				    output.setRGB(x,y,((int)alpha)<<24);
				}
			}
	    }
	    else if(sEdge[0]==1 && sEdge[1]==2)
	    {
	        for (int y=0;y<img.getHeight(null);y++)
			{
				for (int x=img.getWidth(null)-1;x>(img.getWidth(null)*gradSize);x--)
				{
				    double alpha=255-((img.getWidth(null)-x)/(img.getWidth(null)*gradSize))*255;
				    //System.out.println("1"+alpha);
				    img.setRGB(x,y,((int)alpha)<<24);
				}
			}
	    }
	    else if(sEdge[0]==2 && sEdge[1]==3)
	    {
	        for (int y=img.getHeight(null);y>(img.getHeight(null)*gradSize);y--)
			{
				for (int x=0;x<img.getWidth(null);x++)
				{
				    double alpha=255-((img.getHeight(null)-y)/(img.getHeight(null)*gradSize))*255;
				    
				    img.setRGB(x,y,((int)alpha)<<24);
				}
			}
	        
	    }
	    else//if(sEdge[0]==3 && sEdge[1]==0)
	    {
	        for (int y=0;y<img.getHeight(null);y++)
			{
				for (int x=0;x<(img.getWidth(null)*gradSize);x++)
				{
				    double alpha=255-(x/(img.getWidth(null)*gradSize))*255;
				    
				    img.setRGB(x,y,((int)alpha)<<24);
				}
			}
	        
	    }
	    
	    return output;
	}
	
	public BufferedImage createUniColouredBufferedImage(int red,int green,int blue,int w,int h)
	{
	    BufferedImage output = new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);
	    
	    //colours 'output'
	    for (int y=0;y<output.getHeight(null);y++)
		{
			for (int x=0;x<output.getWidth(null);x++)
			{
			    int pixel=(red<<16)+(green<<8)+blue;
				output.setRGB(x,y,pixel);
			}
		}
	    
	    return output;
	}
	
	/**
	 * Finds the smallest edge from the polygon made by the user. Stores them in an
	 * integer array as follow:
	 * &nbsp &nbsp &nbsp &nbsp smallestEdge[0] = one point that makes the edge
	 * &nbsp &nbsp &nbsp &nbsp smallestEdge[0] = the other point that makes the edge
	 * @return an integer array with the indices (i.e. Coord.pixelNumber) of the smallest edge
	 */
	private int[] findSmallestEdge()
	{
	    int[] smallestEdge=new int[2];
	    
	    smallestEdge[0]=0;
	    smallestEdge[1]=1;
	    
	    double smallest=edgeLength(clickA.getRelX(),clickA.getRelY(),clickB.getRelX(),clickB.getRelY());
	    
	    
	    
	    //assume that the first and second click make the smallest edge
	    //int smallestX=Math.abs(clickB.getRelX()-clickA.getRelX());
	    //int smallestY=Math.abs(clickB.getRelY()-clickA.getRelY());
	    
	    if (edgeLength(clickB.getRelX(),clickB.getRelY(),clickC.getRelX(),clickC.getRelY())<smallest)
	    {
	        smallestEdge[0]=1;
	        smallestEdge[1]=2;
	        smallest=edgeLength(clickB.getRelX(),clickB.getRelY(),clickC.getRelX(),clickC.getRelY());
	    }//if the second and third click make the smallest
	    
	    if (edgeLength(clickC.getRelX(),clickC.getRelY(),clickD.getRelX(),clickD.getRelY())<smallest)
	    {
	        smallestEdge[0]=2;
	        smallestEdge[1]=3;
	        smallest=edgeLength(clickC.getRelX(),clickC.getRelY(),clickD.getRelX(),clickD.getRelY());
	    }//if the third and fourth click make the smallest
	    
	    if (edgeLength(clickD.getRelX(),clickD.getRelY(),clickA.getRelX(),clickA.getRelY())<smallest)
	    {
	        smallestEdge[0]=3;
	        smallestEdge[1]=0;
	        smallest=edgeLength(clickD.getRelX(),clickD.getRelY(),clickA.getRelX(),clickA.getRelY());
	    }//if the fourth and first click make the smallest
	    
	    //ELSE the first and second click make the smallest edge 
	    
	    return smallestEdge;
	}
	
	private double edgeLength(int x1,int y1,int x2,int y2)
	{
	    return Math.sqrt((x2-x1)*(x2-x1)+(y2-y1)*(y2-y1));
	}

	/**
	 * Sets the fields 'relX' and 'relY' as the clicked points relative to the
	 * polygon specified by the user
	 * @param sX the smallest value of x from the licked points
	 * @param sY the smallest value of x from the licked points
	 */
	private void relatePointsToOutput(int sX, int sY)
	{
		clickA.setRelX(clickA.getX()-sX);
		clickA.setRelY(clickA.getY()-sY);
		clickB.setRelX(clickB.getX()-sX);
		clickB.setRelY(clickB.getY()-sY);
		clickC.setRelX(clickC.getX()-sX);
		clickC.setRelY(clickC.getY()-sY);
		clickD.setRelX(clickD.getX()-sX);
		clickD.setRelY(clickD.getY()-sY);
	}
	
	/**
	 * Checks whether or not the pixel from the output is in the the range of the
	 * polygon that the user specified.
	 * @param u the texU value
	 * @param v the texV value
	 * @param w the width of the texture
	 * @param h the height of the texture
	 * @return
	 */
	private boolean inRange(double u,double v,double w,double h)
	{
		if (u<0 || w<=u) return false;
		if (v<0 || h<=v) return false;
		return true;
	}
	
	/**
	 * Finds the smallest and lowest x and y values from each of the 4 coordinates input.
	 * The following is how they are stored:
	 * &nbsp &nbsp &nbsp &nbsp lowHigh[0]=the smallest x value
	 * &nbsp &nbsp &nbsp &nbsp lowHigh[1]=the smallest y value
	 * &nbsp &nbsp &nbsp &nbsp lowHigh[2]=the largest x value
	 * &nbsp &nbsp &nbsp &nbsp lowHigh[3]=the largest y value
	 * @param A the coordinate of the first click
	 * @param B the coordinate of the second click
	 * @param C the coordinate of the third click
	 * @param D the coordinate of the fourth click
	 * @return All the highest and lowest x's and y's stored in an array
	 */
	private int[] getHighsAndLows(Coord A,Coord B,Coord C,Coord D)
	{
		int[] lowHigh=new int[4];
		int smallestX=A.getX(), largestX=A.getX();
		int smallestY=A.getY(), largestY=A.getY();
		
		//set the smallest x value to be 'smallestX'
		if (B.getX()<smallestX) smallestX=B.getX();
		if (C.getX()<smallestX) smallestX=C.getX();
		if (D.getX()<smallestX) smallestX=D.getX();
		lowHigh[0]=smallestX;
		
		//set the smallest y value to be 'smallestY'
		if (B.getY()<smallestY) smallestY=B.getY();
		if (C.getY()<smallestY) smallestY=C.getY();
		if (D.getY()<smallestY) smallestY=D.getY();
		lowHigh[1]=smallestY;
		
		//set the largest x value to be 'largestX'
		if (B.getX()>largestX) largestX=B.getX();
		if (C.getX()>largestX) largestX=C.getX();
		if (D.getX()>largestX) largestX=D.getX();
		lowHigh[2]=largestX;
		
		//set the largest y value to be 'largestY'
		if (B.getY()>largestY) largestY=B.getY();
		if (C.getY()>largestY) largestY=C.getY();
		if (D.getY()>largestY) largestY=D.getY();
		lowHigh[3]=largestY;
		
		return lowHigh;
	}

}
