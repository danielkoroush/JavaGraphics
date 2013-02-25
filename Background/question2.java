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
import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.event.*;
import java.awt.*;
import javax.swing.JOptionPane;

public class question2 extends JFrame{
	static String images [];
	JFileChooser fileChooser;
	JDesktopPane desktop;
	JInternalFrame result;
	//ImagePanel for the fore and the back grounds
	ImagePanel back=null;
	ImagePanel fore=null;

	//Creating the menu bar for the frame
	public  question2(){
	super("Question 2");
	fileChooser = new JFileChooser();

	JPanel panel=new JPanel();
	JLabel label1 = new JLabel("Text-Only Label");
	panel.add(label1);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(50, 50,
                  screenSize.width  - 50*2,
                  screenSize.height - 50*2);
  


   	desktop = new JDesktopPane();
	JMenuBar menuBar = new JMenuBar();
	JMenu menu;
	JMenuItem menuItem;
	menu = new JMenu("File");
	menuItem = new JMenuItem("Compile Background");
	menuItem.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent e){
			if (images.length!=0){
			JFrame result=new JFrame("Background Image");
			
			Paint p=new Paint();
			BufferedImage temp=p.BackGround(images);
			back=new ImagePanel(temp);

			result.getContentPane().add(back,BorderLayout.CENTER);
			result.pack();
			result.setVisible(true);
			}else{
				JFrame frame2 = new JFrame("No Files Submited");
		  	 	JOptionPane.showMessageDialog(frame2,
		    		"Sorry no file names were found on the command lines",
		    		"Inane error",
		   		 JOptionPane.ERROR_MESSAGE);
			}

		}
	});
	menu.add(menuItem);

	menuItem = new JMenuItem("Compile Foreground from ...");
	menuItem.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent e){
				int returnValue=fileChooser.showOpenDialog(question2.this);
            			if (returnValue == JFileChooser.APPROVE_OPTION) {
					String fileName=fileChooser.getSelectedFile().getAbsolutePath(); 
					System.out.println(fileName);
					fore=new ImagePanel(fileName);
					if (back!=null){
						JFrame result=new JFrame("Foreground Image");
						Paint p=new Paint();
						BufferedImage temp=p.ForeGround(back.copyImage(),fore.copyImage());
						if (temp!=null){
							ImagePanel temp2=new ImagePanel(temp);
							result.getContentPane().add(temp2,BorderLayout.CENTER);
							result.pack();
							result.setVisible(true);
							fore=temp2;
						}else{
							JFrame frame2 = new JFrame("Size mismatch");
		  	 				JOptionPane.showMessageDialog(frame2,
		    					"Foreground and Background picture sizes are different!",
		    					"Inane error",
		   		 			JOptionPane.ERROR_MESSAGE);
						}

					}else{
						JFrame frame2 = new JFrame("No Background");
		  	 			JOptionPane.showMessageDialog(frame2,
		    				"Please Compile a Background first",
		    				"Inane error",
		   		 		JOptionPane.ERROR_MESSAGE);
					}

						
            				} 

		}
	});
	menu.add(menuItem);

	menuItem = new JMenuItem("Save Background");
	menuItem.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent e){
			if (back!=null){
				int returnValue=fileChooser.showSaveDialog(question2.this);
				if (returnValue == JFileChooser.APPROVE_OPTION) {
				String fileName=fileChooser.getSelectedFile().getAbsolutePath();				
					Save(fileName,back);
					}
			}

		}
	});
	menu.add(menuItem);

	menuItem = new JMenuItem("Save Foreground");
	menuItem.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent e){
			if (fore!=null){
				int returnValue=fileChooser.showSaveDialog(question2.this);
				if (returnValue == JFileChooser.APPROVE_OPTION) {
				String fileName=fileChooser.getSelectedFile().getAbsolutePath();				
					Save(fileName,fore);
					}
			}


		}
	});
	menu.add(menuItem);

	menuBar.add(menu);
	

	setContentPane(desktop);
	setJMenuBar(menuBar);
	
     
      
    }



     // This method saves the image into a file 
     private static void Save(String file, ImagePanel image){
	 if (image!=null){
	        boolean success = (new File(file)).delete();
		try{
			ImageIO.write(image.copyImage2(image.copyImage()), "jpg", new File(file));
		}catch (Exception e){}
	    }

	}
	
    //creating the GUI Frame
    private static void ShowGUI() {
        //Make sure we have nice window decorations.
        JFrame.setDefaultLookAndFeelDecorated(true);

        //Create and set up the window.
        question2 frame = new question2();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	
        //Display the window.
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
	images=args;
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ShowGUI();
            }
        });
    }

}