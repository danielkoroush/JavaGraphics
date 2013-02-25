import java.awt.Point;
class Min{

    public Min(){
        root = null;
        size = 0;
    }

   public void add(double cost, Point p){
   
    	MinNode y=null;
    	MinNode x=root;
    	
    	while (x!=null){
    		y=x;
    		if (cost==y.cost){
    			y.list.addElement(p);
    			size++;
    			return;
    		}else if(cost<x.cost){
    			x=x.l;
    		}else{
    			x=x.r;
    		}
    	}
    	MinNode s=new MinNode(cost,y,null,null);
    	s.list.addElement(p);
    	if (y==null){
    		root=s;
    		size++;
    		
    	}else if(s.cost<y.cost){
    		y.l=s;
    		size++;
    	}else{
    		y.r=s;
    		size++;
    	}
   }


public MinNode successor(MinNode x){
	if (x.r!=null){
		return MIN(x.r);
	}
	MinNode y=x.p;
	while ((y!=null) &&(x==y.r)){
		x=y;
		y=y.p;
	}
	return y;
}
public MinNode MIN(MinNode x){
		while (x.l!=null){
		x=x.l;
	}
	return x;
}

    public Point getMin()   {
    	
    	MinNode y=root;

    	while ((y.l)!=null){
    		y=y.l;
    	}
    	
    	if ((y.list.size())>1){
    		
    		Point p=(Point)y.list.remove(0);
    		size--;
    		
    		return p;
    	}else{
    		Point p=(Point) y.list.remove(0);	
    		Delete(y,null);

    		return p;
    	}	
    }
    
    public MinNode find(double cost){
    	MinNode x=root;
    	while((x!=null) && (x.cost!=cost)){
    		if (cost<x.cost){
    			x=x.l;
    		}else{
    			x=x.r;
    		}
    	}
    	return x;
    }
    public boolean Delete(MinNode z,Point p){
    	
    	
    	
    	if ((z.list.size()>1) &&(p!=null)){
    		Point temp;
    		for (int i=0;i<z.list.size();i++){
    			

    			temp=(Point)z.list.elementAt(i);
    			
    			//System.out.println(temp.x+"   "+temp.y);
    			if ((p.x==temp.x) && (p.y==temp.y)){
    				
    				z.list.remove(i);
    				size--;
    				return true;
    			}
    			
    		}
    		return false;
    	}
    	MinNode y;
    	MinNode x;
    	size--;
    	if ((z.l==null) || (z.r==null)){
    		y=z;
    	}else{
    		y=successor(z);
    	}
    	if (y.l!=null){
    		x=y.l;
    	}else{
    		x=y.r;
    	}
    	if (x!=null){
    		x.p=y.p;
    	}
    	if (y.p==null){
    		root=x;
    	}else if(y==y.p.l){
    		y.p.l=x;
    	}else{
    		y.p.r=x;
    	}
    	if (y.cost!=z.cost){
    		
    		z.cost=y.cost;
    		//y.l=z.l;
    		//y.p=z.p;
    		//y.r=z.r;
    		y.list=z.list;		
    	}
    	return true;
    }
    public void myDelete(MinNode z,Point p){
    	size--;
    	if ((z.list.size()>1) &&(p!=null)){
    		Point temp;
    		for (int i=0;i<z.list.size();i++){
    			//System.out.println(z.list.size());

    			temp=(Point)z.list.elementAt(i);
    			
    			//System.out.println(temp.x+"   "+temp.y);
    			if ((p.x==temp.x) && (p.y==temp.y)){
    				z.list.remove(i);
    				return;
    			}
    			
    		}
    		return;
    	}
    	MinNode y;
    	MinNode x;
    	if((z.l==null)&& (z.r==null)){
    		if (z.p==null){
    			z=null;
    		}else if(z.cost<z.p.cost){
    			z.p.l=null;
    		}else{
    			z.p.r=null;
    		}
    		return;
    	}
    	if ((z.l==null) &&(z.r!=null)){
    		if (z.p==null){
    			z.r.p=null;
    		}else if(z.cost<z.p.cost){
    			z.p.l=z.r;
    		}else{
    			z.p.r=z.r;
    		}
    		return;
    	}
    	if ((z.l!=null) &&(z.r==null)){
    		if (z.p==null){
    			z.r.p=null;
    		}else if(z.cost<z.p.cost){
    			z.p.l=z.r;
    		}else{
    			z.p.r=z.r;
    		}
    		return;
    	}
    	 y=successor(z);
    	if (y.cost>root.cost){
    		y.p.r=y.l;
    	}else{
    		y.p.l=y.r;
    	}
    	y.r=z.r;
    	y.l=z.l;
    	y.p=z.p;
    }


    public MinNode root;
    public int size;
    
}