import javax.swing.JInternalFrame;
import javax.swing.JDesktopPane;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JMenuBar;
import javax.swing.JFrame;
import javax.swing.JFileChooser;
import javax.swing.JComponent;
import java.awt.image.*;
import java.io.*;
import javax.imageio.ImageIO;

import java.awt.event.*;
import java.awt.*;
import javax.swing.JOptionPane;



public class ImageCompositing extends JFrame {

    JFileChooser fileChooser; // when we want to load/save a file
    JDesktopPane desktop;
    ICInternalFrame internalForegroundFrame=null;
    ICInternalFrame internalBackgroundFrame=null;
    BackgroundImagePanel backgroundImagePanel;
    JFrame frame2 = new JFrame("ColorChooser");
    

    public ImageCompositing() {
        super("Image Compositing");

	fileChooser = new JFileChooser();

        //Make the big window be indented 50 pixels from each edge
        //of the screen.
        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
                  screenSize.width  - inset*2,
                  screenSize.height - inset*2);

        //Set up the GUI.
        desktop = new JDesktopPane(); //a specialized layered pane
        setContentPane(desktop);
        setJMenuBar(createMenuBar());


        //Make dragging a little faster but perhaps uglier.
        //desktop.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
    }

	private JMenuBar createMenuBar(){
		JMenuBar menuBar = new JMenuBar();
		JMenu menu;
		JMenuItem menuItem;

		menu = new JMenu("File");
			//a group of JMenuItems
			menuItem = new JMenuItem("Open Background");
			// Below we declare an instance of an anonymous inner class
			// it implements the ActionListener interface
			menuItem.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					int returnValue=fileChooser.showOpenDialog(ImageCompositing.this);
            				if (returnValue == JFileChooser.APPROVE_OPTION) {
						String fileName=fileChooser.getSelectedFile().getAbsolutePath(); 
						backgroundImagePanel=new BackgroundImagePanel(fileName, ImageCompositing.this);
						internalBackgroundFrame=createInternalFrame("Background", backgroundImagePanel, internalBackgroundFrame);
            				} 
				}
			});
			menu.add(menuItem);

			menuItem = new JMenuItem("Open Foreground");
			menuItem.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					frame2.dispose();
					int returnValue=fileChooser.showOpenDialog(ImageCompositing.this);
            				if (returnValue == JFileChooser.APPROVE_OPTION) {
						String fileName=fileChooser.getSelectedFile().getAbsolutePath(); 
						ForegroundImagePanel foregroundImagePanel=new ForegroundImagePanel(fileName, ImageCompositing.this);
						internalForegroundFrame=createInternalFrame("Foreground", foregroundImagePanel, internalForegroundFrame);
            				} 
				}
			});
			menu.add(menuItem);
	
			menu.addSeparator();// -------------
			


			menuItem = new JMenuItem("Save Background");
			menuItem.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){ 
					int returnValue=fileChooser.showSaveDialog(ImageCompositing.this);
					if (returnValue == JFileChooser.APPROVE_OPTION) {
						String fileName=fileChooser.getSelectedFile().getAbsolutePath();
						
						SaveBackGround(fileName);
					}
				}
			});
			
			menu.add(menuItem);

			menuItem = new JMenuItem("Save Foreground");
			menuItem.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){ 
					int returnValue=fileChooser.showSaveDialog(ImageCompositing.this);
					if (returnValue == JFileChooser.APPROVE_OPTION) {
						String fileName=fileChooser.getSelectedFile().getAbsolutePath();
						
						SaveForeGround(fileName);
					}
				}
			});
			
			menu.add(menuItem);

			menu.addSeparator();// -------------
			
			menuItem = new JMenuItem("Quit");
			menuItem.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){ 
					System.exit(0);
				}
			});
			
			menu.add(menuItem);
			
			menuBar.add(menu);
			
			menu = new JMenu("Edit");
			
			menuItem = new JMenuItem("Color Chooser Foreground");
			menuItem.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					
					GUI();

				}
			});
			menu.add(menuItem);

			menuItem = new JMenuItem("Color Chooser Background");
			menuItem.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					
					GUIBack();

				}
			});
			menu.add(menuItem);
	
	
			menu.addSeparator();// -------------
			menuItem = new JMenuItem("Undo Background");
			menuItem.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					
					undoBack();

				}
			});
			menu.add(menuItem);
			menuItem = new JMenuItem("Undo Foreground");
			menuItem.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					
					undo();

				}
			});
			menu.add(menuItem);
			menu.addSeparator();// -------------
			menuItem = new JMenuItem("Redo Background");
			menuItem.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					
					redoBack();

				}
			});
			menu.add(menuItem);
			menuItem = new JMenuItem("Redo Foreground");
			menuItem.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					
					redo();

				}
			});
			menu.add(menuItem);

		menuBar.add(menu);

		return menuBar;
	}

	private ICInternalFrame createInternalFrame(String frameTitle, ImagePanel ip, ICInternalFrame previousInternalFrame){
		if(ip.getImage()==null)return previousInternalFrame;

		ICInternalFrame icif=null;
		if(previousInternalFrame!=null){
			desktop.remove(previousInternalFrame);
			previousInternalFrame.dispose();
		}
		icif=new ICInternalFrame(frameTitle, ip);
       		icif.setVisible(true); //necessary as of 1.3
       		desktop.add(icif);
       		try { icif.setSelected(true); } 
		catch (java.beans.PropertyVetoException e) {}
		return icif;
	}

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
        //Make sure we have nice window decorations.
        JFrame.setDefaultLookAndFeelDecorated(true);

        //Create and set up the window.
        ImageCompositing frame = new ImageCompositing();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	
        //Display the window.
        frame.setVisible(true);
    }
        private void GUI() {
		if ((internalForegroundFrame!=null)&&(internalForegroundFrame.ip2.press!=-1)){
        //Make sure we have nice window decorations.
       			 JFrame.setDefaultLookAndFeelDecorated(true);

        //Create and set up the window.
      			  frame2 = new JFrame("ColorChooser For Foreground");
			 // frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create and set up the content pane.
        		JComponent newContentPane = new Demo(internalForegroundFrame);

			newContentPane.setOpaque(true); //content panes must be opaque
			frame2.setContentPane(newContentPane);

        //Display the window.
       			 frame2.pack();
		 	frame2.setVisible(true);
		}else if(internalForegroundFrame.ip2.press==-1){
			 JFrame frame2 = new JFrame("Please Select matte");
		     JOptionPane.showMessageDialog(frame2,
		    "Please identify the the matte by clicking on the Foreground picture first then choose Color Chooser ",
		    "Inane warning",
		    JOptionPane.WARNING_MESSAGE);

			}
    }

        private void GUIBack() {
		if ((internalBackgroundFrame!=null)&&(internalBackgroundFrame.ip2.press!=-1)){
        //Make sure we have nice window decorations.
       			 JFrame.setDefaultLookAndFeelDecorated(true);

        //Create and set up the window.
      			  frame2 = new JFrame("ColorChooser For Background");
			 // frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create and set up the content pane.
        		JComponent newContentPane = new Demo(internalBackgroundFrame);

			newContentPane.setOpaque(true); //content panes must be opaque
			frame2.setContentPane(newContentPane);

        //Display the window.
       			 frame2.pack();
		 	frame2.setVisible(true);
			}else if(internalBackgroundFrame.ip2.press==-1){
			 JFrame frame2 = new JFrame("Please Select matte");
		     JOptionPane.showMessageDialog(frame2,
		    "Please identify the the matte by clicking on the Background picture first then choose Color Chooser ",
		    "Inane warning",
		    JOptionPane.WARNING_MESSAGE);

			}
    }

    private void SaveBackGround(String file){
	    if (internalBackgroundFrame!=null){
	        boolean success = (new File(file)).delete();
		if (!success) {
			// Deletion failed
		}
		try{
			ImageIO.write((internalBackgroundFrame.ip2.copyImage2()), "jpg", new File(file));
		}catch (Exception e){}
	    }
		
    }

    private void SaveForeGround(String file){
	    if (internalForegroundFrame!=null){
	        boolean success = (new File(file)).delete();
		if (!success) {
			// Deletion failed
		}
		try{
			ImageIO.write((internalForegroundFrame.ip2.copyImage2()), "jpg", new File(file));
		}catch (Exception e){}
	    }
		
    }
    private void undo(){
	    if ((internalForegroundFrame!=null)&&(internalForegroundFrame.ip2.pre.size()!=0)){
	    	internalForegroundFrame.ip2.next.addElement(internalForegroundFrame.ip2.copyImage());
	    	BufferedImage temp=(BufferedImage) internalForegroundFrame.ip2.pre.lastElement();
	    	internalForegroundFrame.ip2.pre.remove(internalForegroundFrame.ip2.pre.size()-1);
	    	
	    	internalForegroundFrame.ip2.setImage(temp);
	    }
	
    }
    private void undoBack(){
	    if ((internalBackgroundFrame!=null)&&(internalBackgroundFrame.ip2.pre.size()!=0)){
	    	internalBackgroundFrame.ip2.next.addElement(internalBackgroundFrame.ip2.copyImage());
	    	BufferedImage temp=(BufferedImage) internalBackgroundFrame.ip2.pre.lastElement();

	    	internalBackgroundFrame.ip2.pre.remove(internalBackgroundFrame.ip2.pre.size()-1);
	    	
	    	internalBackgroundFrame.ip2.setImage(temp);
	    }
	
    }
    private void redoBack(){
	    if ((internalBackgroundFrame!=null)&&(internalBackgroundFrame.ip2.next.size()!=0)){
	    	internalBackgroundFrame.ip2.pre.addElement(internalBackgroundFrame.ip2.copyImage());
	    	BufferedImage temp=(BufferedImage) internalBackgroundFrame.ip2.next.lastElement();
	    	internalBackgroundFrame.ip2.next.remove(internalBackgroundFrame.ip2.next.size()-1);
	    	
	    	internalBackgroundFrame.ip2.setImage(temp);
	    }
	
    }
    private void redo(){
	    if ((internalForegroundFrame!=null)&&(internalForegroundFrame.ip2.next.size()!=0)){
	       	internalForegroundFrame.ip2.pre.addElement(internalForegroundFrame.ip2.copyImage());
	    	BufferedImage temp=(BufferedImage) internalForegroundFrame.ip2.next.lastElement();
	    	internalForegroundFrame.ip2.next.remove(internalForegroundFrame.ip2.next.size()-1);
	    	
	    	internalForegroundFrame.ip2.setImage(temp);
	    }
	
    }
    
    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}
