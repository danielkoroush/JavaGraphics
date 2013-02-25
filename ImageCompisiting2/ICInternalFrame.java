import javax.swing.JInternalFrame;


public class ICInternalFrame extends JInternalFrame {
	ImagePanel ip2;
	int red;
	int green;
	int blue;
	
    public ICInternalFrame(String title, ImagePanel ip) {
        super(title, 
              false, //resizable
              true, //closable
              false, //maximizable
              false);//iconifiable

        //...Create the GUI and put it in the window...
	ip2=ip;
	getContentPane().add(ip);

        //...Then set the window size or call pack...
        pack();

        //Set the window's location.
        setLocation(0,78);
    }
    public ICInternalFrame(String title, ImagePanel ip,int x, int y) {
        super(title, 
              false, //resizable
              true, //closable
              false, //maximizable
              false);//iconifiable

        //...Create the GUI and put it in the window...
	ip2=ip;
	getContentPane().add(ip);

        //...Then set the window size or call pack...
        pack();

        //Set the window's location.
        setLocation(x,y);
    }
}
