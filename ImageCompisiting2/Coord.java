class Coord{
	private int x;
	private int y; 
	private int relX;
	private int relY;
	private int pixelNumber;
	
    public Coord(int xCoord, int yCoord, int ctr){
        x=xCoord;
        y=yCoord;
        relX=-1;
        relY=-1;
        pixelNumber=ctr;
    }
    
    public int getX()
    {
    	return x;
    }
    
    public int getY()
    {
    	return y;
    }
    
    public int getRelX()
    {
    	return relX;
    }
    
    public int getRelY()
    {
    	return relY;
    }
    
    public void setRelX(int outputX)
    {
    	relX=outputX;
    }
    
    public void setRelY(int outputY)
    {
    	relY=outputY;
    }
    
    public int getPixelNumber()
    {
    	return pixelNumber;
    }
    
    
}