//Youssef Aboul-Naja  (Student#: 991489073)
//Hoa Nguyen  (Student#: 991043116)

import javax.swing.JInternalFrame;
import java.awt.event.*;
import java.awt.*;

/**
 * This class creates an internal frame
 */ 
public class ICInternalFrame extends JInternalFrame {
	private static int x=0;
	private static int y=0;
    public ICInternalFrame(String title, ImagePanel ip) {
        super(title, 
              false, //resizable
              false, //closable
              false, //maximizable
              false);//iconifiable

        //...Create the GUI and put it in the window...
		getContentPane().add(ip);

        //...Then set the window size or call pack...
        pack();

        //Set the window's location.
		if(TextureMap.TOOL_IN_USE == TextureMap.BASIC_MOSAIC)
	        setLocation(x,y+95);
		else
			setLocation(x,y);
		x = x + 25;
		y= y + 25;
    }
	public ICInternalFrame(String title){
		super(title, 
              false, //resizable
              false, //closable
              false, //maximizable
              false);//iconifiable
        //...Then set the window size or call pack...
        //pack();
		getContentPane().setLayout(new GridLayout(1,15));
        //Set the window's location.
        setLocation(0,0);
	}
	
	public static void reset(){
		x = 0;
		y = 0;
	}
}
