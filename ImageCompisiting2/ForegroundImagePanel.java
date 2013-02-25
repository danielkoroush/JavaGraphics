
import java.awt.image.*;
import java.awt.event.*;
import java.util.Vector;
import java.awt.Point;


class ForegroundImagePanel extends ImagePanel implements MouseListener, MouseMotionListener {
	private ImageCompositing icApp;
	int x=-1;
	int y=-1;
	MapPoint [][] p;
	public BufferedImage bimg=getImage();
	public BufferedImage Solidbimg=getImage();
	public BufferedImage Oldbimg;
	Point seed;
	boolean cool=true;

	
	long time;
	long time2=0;
	public ForegroundImagePanel(String filename, ImageCompositing icApp){ 
		super(filename); 
		this.icApp=icApp;
		addMouseListener(this);
		
	}
	public void mousePressed(MouseEvent e) {
		BufferedImage bimg=icApp.internalForegroundFrame.ip2.copyImage();
		int pixel=bimg.getRGB(e.getX(),e.getY());
		setpress(pixel,e.getX(),e.getY());
	if (CostMap!=null){	
      is(e);
	}
		
	}
	public void is(MouseEvent e){
		
		if (e1==null){
			addMouseMotionListener(this);
			p=search(e.getX(),e.getY());
			e1=e;
			bimg=getImage();
			icApp.internalForegroundFrame.ip2.pre.addElement((icApp.internalForegroundFrame.ip2.copyImage()));
			Oldbimg=getImage();

		}else{
		
		    MapPoint temp=p[e.getX()][e.getY()];

		    int pixel=(125<<24)+(255<<16)+(0<<8)+0;
		    cut1.addElement(cut);
		    icApp.internalForegroundFrame.ip2.pre.addElement((icApp.internalForegroundFrame.ip2.copyImage()));
		    while(temp.pointer!=null){
		    	cut.addElement(temp.location);
		    	bimg.setRGB(temp.getX(),temp.getY(),pixel);
		    	temp=p[(int)temp.pointer.getX()][(int)temp.pointer.getY()];
		    }
		    icApp.internalForegroundFrame.ip2.pre.addElement((icApp.internalForegroundFrame.ip2.copyImage()));
		    cut3.addElement(cut);
		    setImage(bimg);
		    Oldbimg=getImage();
		    p=search(e.getX(),e.getY());
		    e1=e;
		    
		}
	}
	
	public void mouseReleased(MouseEvent e) { }
	public void mouseEntered(MouseEvent e) {
	}
	public void mouseExited(MouseEvent e) {

		//removeMouseMotionListener(this);
	//	setImage(Oldbimg);
		}
	public void mouseClicked(MouseEvent e) {}
    public void mouseDragged(MouseEvent e) {
}
public void setTime(){
	if (cool) time=System.currentTimeMillis();
}
public void mouseMoved(MouseEvent e) {

	BufferedImage liveWire;

if (e1!=null){
	if (time2==0){
		time2=time;
		setImage(Oldbimg);
	}else if(time-time2>1000){
		//p=search(e.getX(),e.getY());
		//Oldbimg=copyImage();
		//setImage(Oldbimg);
		//p=search(seed.x,seed.x);
	}else{
		setImage(Oldbimg);
	}
	liveWire=copyImage();

	
    MapPoint temp=p[e.getX()][e.getY()];
  
    int pixel=(125<<24)+(0<<16)+(0<<8)+255;
    while(temp.pointer!=null){

    	liveWire.setRGB(temp.getX(),temp.getY(),pixel);
    	temp=p[(int)temp.pointer.getX()][(int)temp.pointer.getY()];
    }
    setImage(liveWire);
    time2=time;
    setTime();
	seed=new Point(e.getX(),e.getY());



}
}
}
