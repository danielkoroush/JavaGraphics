
import java.awt.Point;
class MapPoint{
	public Point location=null;
	public boolean expanded=false;
	public Point pointer=null;
	public Double totalCost=null;
	public boolean inList=false;
	
	public MapPoint(Point l,double cost){
		location=l;
		totalCost=new Double(cost);
		pointer=null;
	}
	public void setPointer(int x,int y){
		pointer=new Point(x,y);
	}
	public void setPointer(Point p){
		pointer=p;
	}
public int getX(){
	return (int) location.getX();
}
public int getY(){
	return (int) location.getY();
}
	public void newCost(double c){
		totalCost=new Double(c);
	}
	public double getCost(){
		return totalCost.doubleValue();
	}
	public void expand(){
		expanded=true;
	}

	}