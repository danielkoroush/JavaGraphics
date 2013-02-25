import java.awt.*;
import java.util.Vector;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.colorchooser.*;

/* ColorChooserDemo.java is a 1.4 application that requires no other files. */
public class Demo extends JPanel
                              implements ChangeListener {

    protected JColorChooser tcc;
    protected JLabel banner;
    public ICInternalFrame image=null;

    public void setForeGround(ICInternalFrame pic){
	    image=pic;
	    
    }
    public Demo(ICInternalFrame pic) {
        super(new BorderLayout());
	image=pic;

        //Set up the banner at the top of the window
      /*  banner = new JLabel("Welcome to the Tutorial Zone!",
                            JLabel.CENTER);
        banner.setForeground(Color.yellow);
        banner.setBackground(Color.blue);
        banner.setOpaque(true);
        banner.setFont(new Font("SansSerif", Font.BOLD, 24));
        banner.setPreferredSize(new Dimension(100, 65));

        JPanel bannerPanel = new JPanel(new BorderLayout());
        bannerPanel.add(banner, BorderLayout.CENTER);
        bannerPanel.setBorder(BorderFactory.createTitledBorder("Banner")); */

        //Set up color chooser for setting text color
        tcc = new JColorChooser();
        tcc.getSelectionModel().addChangeListener(this);
       // tcc.setBorder(BorderFactory.createTitledBorder(
         //                                    "Choose Text Color"));

       // add(bannerPanel, BorderLayout.CENTER);
        add(tcc, BorderLayout.PAGE_END);
    }

    public void stateChanged(ChangeEvent e) {
	   // System.out.println(tcc.getColor());
	    if (image!=null){
		    image.ip2.pre.addElement(image.ip2.copyImage());
		    image.ip2.cut1.addElement(new Vector());
		    int red=tcc.getColor().getRed();
		    int blue=tcc.getColor().getBlue();
		    int green=tcc.getColor().getGreen();
		   
		   image.ip2.setImage((image.ip2.setColor(image.ip2.copyImage(),image.ip2.press,blue,red,green)));
		   image.ip2.next.addElement(image.ip2.copyImage());
		   image.ip2.setpress(image.ip2.getPixel(image.ip2.copyImage(),image.ip2.x,image.ip2.y),image.ip2.x,image.ip2.y);
		   image.ip2.cut3.addElement(new Vector());
		   
		  
	    }
	   
       // Color newColor = tcc.getColor();
       // banner.setForeground(newColor);
    }




}
    
