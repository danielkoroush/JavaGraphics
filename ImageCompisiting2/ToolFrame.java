import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import java.awt.Dimension;
import javax.swing.JToolBar;

import java.awt.*;

public class ToolFrame extends JInternalFrame {

	JPanel p=new JPanel();
	
    public ToolFrame(String title, JToolBar toolBar) {
        super(title, 
              true, //resizable
              false, //closable
              false, //maximizable
              false);//iconifiable
        p.setPreferredSize(new Dimension(640, 58));
        p.setBackground(new Color(153,153,203));
     //   p.setLayout(BorderLayout.NORTH); 
        p.add(toolBar,BorderLayout.NORTH);

        //...Create the GUI and put it in the window...

	getContentPane().add(p);

        //...Then set the window size or call pack...
        pack();

        //Set the window's location.
        setLocation(0,0);
    }
    public ToolFrame(String title, JToolBar toolBar, int x, int y) {
        super(title, 
              true, //resizable
              false, //closable
              false, //maximizable
              false);//iconifiable
        p.setPreferredSize(new Dimension(230, 50));
        p.setBackground(new Color(153,153,203));
     //   p.setLayout(BorderLayout.NORTH); 
        p.add(toolBar,BorderLayout.NORTH);

        //...Create the GUI and put it in the window...

	getContentPane().add(p);

        //...Then set the window size or call pack...
        pack();

        //Set the window's location.
        setLocation(x,y);
    }
}
