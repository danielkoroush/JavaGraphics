//import javax.swing.JInternalFrame;
import javax.swing.JDesktopPane;
import javax.swing.JPanel;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JMenuBar;
import javax.swing.JFrame;
import javax.swing.JFileChooser;
import javax.swing.JComponent;
import java.awt.image.*;
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.JToolBar;
import javax.swing.JTextField;
import java.awt.event.*;
import java.awt.*;
import javax.swing.JLabel;
import cern.colt.matrix.impl.*;
import cern.colt.matrix.*;
import cern.colt.matrix.linalg.*;
import javax.swing.JOptionPane;
import java.net.URL;
import java.util.Vector;
import javax.swing.JCheckBox;

import javax.swing.JButton;
import javax.swing.ImageIcon;

public class ImageCompositing extends JFrame {

    JFileChooser fileChooser; // when we want to load/save a file
    JDesktopPane desktop;
    ICInternalFrame internalForegroundFrame=null;
    ICInternalFrame internalBackgroundFrame=null;
    ICInternalFrame internalForegroundFrameFilter=null;
    Vector frames=new Vector();
    Vector pre=new Vector();
    Vector next=new Vector();
    
    ICInternalFrame icif2=null;
    ICInternalFrame Mo=null;
    boolean alignment=false;
    boolean blending=false;
    boolean linear=false;
    boolean q1option=false;
    ICInternalFrame temp=null;
    static ImageCompositing frame;
    ICInternalFrame cutFrame=null;
    ICInternalFrame textureFrame=null;
    BackgroundImagePanel backgroundImagePanel;
    JFrame frame2 = new JFrame("ColorChooser");
    BufferedImage cut2;
    JDialog hi;
    JDialog hi2;
    

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
        JToolBar toolBar = new JToolBar("Graphical Tools");
        toolBar.setBackground(new Color(153,153,203));
        //toolBar.setPreferredSize(new Dimension(20,150));
       URL imageURL = ImageCompositing.class.getResource("new.gif");
        JButton button17 = new JButton();
        button17.setToolTipText("Open a new Frame");
        button17.setIcon(new ImageIcon(imageURL, "IS"));
        button17.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){			
				int returnValue=fileChooser.showOpenDialog(ImageCompositing.this);
				if (returnValue == JFileChooser.APPROVE_OPTION) {
			String fileName=fileChooser.getSelectedFile().getAbsolutePath(); 
			backgroundImagePanel=new BackgroundImagePanel(fileName, ImageCompositing.this);
			internalBackgroundFrame=createInternalFrame("Working Frame", backgroundImagePanel, internalBackgroundFrame);
				} 
			}
		});
        toolBar.add(button17);
        imageURL = ImageCompositing.class.getResource("cut5.gif");
        JButton button = new JButton();
        button.setIcon(new ImageIcon(imageURL, "IS"));
        button.setToolTipText("Start Intelligent Sicssors");
        button.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){			
				IS();
			}
		});
        toolBar.add(button);
        imageURL = ImageCompositing.class.getResource("cut7.gif");
        JButton button2 = new JButton();
        button2.setIcon(new ImageIcon(imageURL, "IS"));
        button2.setToolTipText("Cut the Selected Area");
        button2.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if ((internalBackgroundFrame!=null)){
					BufferedImage cut=CutPic();
					//cut=internalForegroundFrame.ip2.cutImage;
				ImagePanel cutImagePanel=new ImagePanel(cut);
				cutImagePanel.setImage(cut);
				
				if (icif2!=null){
					icif2.setVisible(false);
					desktop.remove(icif2);
					
					icif2.dispose();
				}
				//internalBackgroundFrame=createInternalFrame("Cut", cutImagePanel, cutFrame);
				icif2=new ICInternalFrame("Cut",cutImagePanel,200,0);
	       		icif2.setVisible(true); //necessary as of 1.3
	       		desktop.add(icif2);
				
				}

			}
		});
        toolBar.add(button2);
        imageURL = ImageCompositing.class.getResource("paste.gif");
        JButton button3 = new JButton();
        button3.setIcon(new ImageIcon(imageURL, "IS"));
        button3.setToolTipText("Paste the selcted Area");
        button3.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){		
				if (icif2!=null){
			JDialog.setDefaultLookAndFeelDecorated(true);
			//JFrame dialog=new JFrame("Paste");
			JPanel dpanel=new JPanel();
			dpanel.setLayout(new GridLayout(3,1)); 
			dpanel.add(new JLabel("Please Enter X,Y Cordinates"));
			hi= new JDialog(frame,"Paste",true);
			JButton okButton=new JButton("Ok");

			JPanel p=new JPanel(); // Comes with a default Flow Layout
			p.setLayout(new GridLayout(2,2));
			p.add(new JLabel("            X= "));
			final JTextField x=new JTextField();
			p.add(x);
			p.add(new JLabel("            Y= "));
			final JTextField y=new JTextField();
			p.add(y);
		
		
			
			dpanel.add(p);
			okButton.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){

					if (internalBackgroundFrame!=null){
						internalBackgroundFrame.ip2.pre.addElement(internalBackgroundFrame.ip2.copyImage());
					internalBackgroundFrame.ip2.paintImage(cut2,new Integer(x.getText()).intValue(),new Integer(y.getText()).intValue());
					//	internalBackgroundFrame.ip2.paintImage(cut2,internalBackgroundFrame.ip2.x,internalBackgroundFrame.ip2.y);
					internalBackgroundFrame.ip2.next.addElement(internalBackgroundFrame.ip2.copyImage());
					}else{
						
							 JFrame frame2 = new JFrame("Please Open A Background First");
						     JOptionPane.showMessageDialog(frame2,
						    "Please Open A Background First",
						    "Inane warning",
						    JOptionPane.WARNING_MESSAGE);
						
					}
					hi.setVisible(false);
					
				}
				
		});
			dpanel.add(okButton);
			hi.getContentPane().add(dpanel);
			hi.pack(); 	
	        hi.setLocationRelativeTo(frame);
	        hi.setVisible(true);
			}else{
				
					 JFrame frame2 = new JFrame("Please Cut First");
				     JOptionPane.showMessageDialog(frame2,
				    "Please Cut First",
				    "Inane warning",
				    JOptionPane.WARNING_MESSAGE);
			
			}
        }
		});
        toolBar.add(button3);

        imageURL = ImageCompositing.class.getResource("save.gif");
        JButton button5 = new JButton();
        button5.setToolTipText("Save Foreground Image");
        button5.setIcon(new ImageIcon(imageURL, "IS"));
        button5.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){			
				int returnValue=fileChooser.showSaveDialog(ImageCompositing.this);
				if (returnValue == JFileChooser.APPROVE_OPTION) {
					String fileName=fileChooser.getSelectedFile().getAbsolutePath();
					
					SaveBackGround(fileName);
				}
			}
		});
        toolBar.add(button5);
        
        imageURL = ImageCompositing.class.getResource("color.gif");
        JButton button6 = new JButton();
        button6.setToolTipText("Color Chooser for Foreground Image");
        button6.setIcon(new ImageIcon(imageURL, "IS"));
        button6.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){			

				GUIBack();
			
			}
		});
        toolBar.add(button6);
        
        imageURL = ImageCompositing.class.getResource("undo.gif");
        JButton button7 = new JButton();
        button7.setToolTipText("Undo Foreground Image");
        button7.setIcon(new ImageIcon(imageURL, "IS"));
        button7.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){			
				
				undo();
			
			}
		});
        toolBar.add(button7);
        imageURL = ImageCompositing.class.getResource("redo.gif");
        JButton button8 = new JButton();
        button8.setToolTipText("Redo Foreground Image");
        button8.setIcon(new ImageIcon(imageURL, "IS"));
        button8.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){			

				redo();
			
			}
		});
        toolBar.add(button8);
        imageURL = ImageCompositing.class.getResource("mosaic.gif");
        JButton button15 = new JButton();
        button15.setToolTipText("Open a Texture Frame for the Image Mosaic");
        button15.setIcon(new ImageIcon(imageURL, "IS"));
        button15.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){			
				int returnValue=fileChooser.showOpenDialog(ImageCompositing.this);
				if (returnValue == JFileChooser.APPROVE_OPTION) {
					String fileName=fileChooser.getSelectedFile().getAbsolutePath(); 
					backgroundImagePanel=new BackgroundImagePanel(fileName, ImageCompositing.this);
					textureFrame=createInternalFrame("Texture Frame", backgroundImagePanel, textureFrame);
					ForegroundImagePanel foregroundImagePanelMask=new ForegroundImagePanel(fileName, ImageCompositing.this);
					internalForegroundFrameFilter=createInternalFrame2("ForegroundMask", foregroundImagePanelMask, internalForegroundFrameFilter);
				}
			

			
			}
		});
        toolBar.add(button15);
        imageURL = ImageCompositing.class.getResource("m2.gif");
        JButton button22 = new JButton();
        button22.setToolTipText("Compute the Image Mosaic");
        button22.setIcon(new ImageIcon(imageURL, "IS"));
        button22.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){			

				
				if ((internalBackgroundFrame.ip2.points.size()==4)&&(textureFrame.ip2.points.size()==4)){
					internalBackgroundFrame.ip2.pre.addElement(internalBackgroundFrame.ip2.copyImage());
					System.out.println(internalBackgroundFrame.ip2.pre.size());
					createCanvas();

				}else{
					
					 JFrame frame2 = new JFrame("Please Open A Background First");
				     JOptionPane.showMessageDialog(frame2,
				    "Please Select 4 points in the Working Frame as well as 4 points in the TextureFrame",
				    "Inane warning",
				    JOptionPane.WARNING_MESSAGE);
				}
				
			
			}
		});
        toolBar.add(button22);
        
        imageURL = ImageCompositing.class.getResource("options.gif");
        JButton button18 = new JButton();
        button18.setToolTipText("Mosaic Options (Question 3)");
        button18.setIcon(new ImageIcon(imageURL, "IS"));
        button18.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){			
				final JDialog option= new JDialog(frame,"Mosaic Options",true);
				JDialog.setDefaultLookAndFeelDecorated(true);
				JPanel optPanel = new JPanel();
				optPanel.setLayout(new GridLayout(3,1));

				JCheckBox align=new JCheckBox("Image Alignment      ");
				align.setSelected(alignment);
				align.addItemListener(new ItemListener(){
					public void itemStateChanged(ItemEvent e){			

				        if (e.getStateChange() == ItemEvent.DESELECTED) {
				            alignment=false;
				           
				        }else{
				        	alignment=true;
				        }
				        
					
					}
				});

				JCheckBox blend=new JCheckBox("Pixel Blending       ");
				blend.setSelected(blending);
				blend.addItemListener(new ItemListener(){
					public void itemStateChanged(ItemEvent e){			

				        if (e.getStateChange() == ItemEvent.DESELECTED) {
				            blending=false;
				           
				        }else{
				        	blending=true;
				        }
				   
					
					}
				});
				//createCanvas();
				JButton okButton=new JButton("Ok");
				okButton.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e){
						option.setVisible(false);
						
					}
			});

				optPanel.add(align);

				optPanel.add(blend);
				optPanel.add(okButton);
				option.getContentPane().add(optPanel);
				option.pack(); 	
		        option.setLocationRelativeTo(frame);
		        option.setVisible(true);
			}
		});
        
        toolBar.add(button18);
        
        imageURL = ImageCompositing.class.getResource("q1.gif");
        JButton button16 = new JButton();
        button16.setToolTipText("linear/projective transformation (Question 1)");
        button16.setIcon(new ImageIcon(imageURL, "IS"));
        button16.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){			

				if (textureFrame!=null){
					linear=true;
					final JDialog option= new JDialog(frame,"linear/projective transformation Options",true);
					JDialog.setDefaultLookAndFeelDecorated(true);
					JPanel optPanel = new JPanel();
					optPanel.setLayout(new GridLayout(3,1));

					JCheckBox align=new JCheckBox("linear/projective transformation Option ");
					align.setSelected(q1option);
					align.addItemListener(new ItemListener(){
						public void itemStateChanged(ItemEvent e){			

					        if (e.getStateChange() == ItemEvent.DESELECTED) {
					            q1option=false;
					           
					        }else{
					        	q1option=true;
					        }
					        
						
						}
					});

					JLabel l=new JLabel("Please click on 4 points on the Working fame to see the result!");

					JButton okButton=new JButton("Ok");
					okButton.addActionListener(new ActionListener(){
						public void actionPerformed(ActionEvent e){
							option.setVisible(false);
							
						}
				});

					optPanel.add(align);

					optPanel.add(l);
					optPanel.add(okButton);
					option.getContentPane().add(optPanel);
					option.pack(); 	
			        option.setLocationRelativeTo(frame);
			        option.setVisible(true);
				
					

				}else{
					 JFrame frame2 = new JFrame("Please Open A Texture Frame First");
				     JOptionPane.showMessageDialog(frame2,
				    "Please Open a texture Frame first",
				    "Inane warning",
				    JOptionPane.WARNING_MESSAGE);
				}
			
			}
		});
        toolBar.add(button16);



        
        imageURL = ImageCompositing.class.getResource("credit.gif");
        JButton button19 = new JButton();
        button19.setToolTipText("Credits");
        button19.setIcon(new ImageIcon(imageURL, "IS"));
        button19.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){			
				JDialog option= new JDialog(frame,"Credits",true);
				JDialog.setDefaultLookAndFeelDecorated(true);
				URL imageURL = ImageCompositing.class.getResource("c.jpg");
		    	ImageIcon icon2 = new ImageIcon(imageURL);
		    	JLabel label = new JLabel(icon2);
		        label.setHorizontalAlignment(JLabel.CENTER);
		        JPanel p=new JPanel();
		        p.add(label);
				option.getContentPane().add(p);
				option.pack(); 	
		        option.setLocationRelativeTo(frame);
		        option.setVisible(true);
			
			}
		});
        toolBar.add(button19);
        
                ToolFrame frame3 = new ToolFrame("Graphical Tools",toolBar);
                JToolBar toolBar2 = new JToolBar("Still draggable");
                ToolFrame frame2 = new ToolFrame("TOOLS for BACKGROUND",toolBar2,382,0);
                frame3.setVisible(true);
                desktop.add(frame3);
                
 
              
        setContentPane(desktop);
          
        setJMenuBar(createMenuBar());


    }

	private JMenuBar createMenuBar(){
		JMenuBar menuBar = new JMenuBar();
		JMenu menu;
		JMenuItem menuItem;

		menu = new JMenu("File");
			//a group of JMenuItems
			menuItem = new JMenuItem("Open Working Frame");
			// Below we declare an instance of an anonymous inner class
			// it implements the ActionListener interface
			menuItem.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					int returnValue=fileChooser.showOpenDialog(ImageCompositing.this);
            				if (returnValue == JFileChooser.APPROVE_OPTION) {
						String fileName=fileChooser.getSelectedFile().getAbsolutePath(); 
						backgroundImagePanel=new BackgroundImagePanel(fileName, ImageCompositing.this);
						internalBackgroundFrame=createInternalFrame("Working Frame", backgroundImagePanel, internalBackgroundFrame);
            				} 
				}
			});
			menu.add(menuItem);

			menuItem = new JMenuItem("Open texture Frame");
			menuItem.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					frame2.dispose();
					int returnValue=fileChooser.showOpenDialog(ImageCompositing.this);
					if (returnValue == JFileChooser.APPROVE_OPTION) {
						String fileName=fileChooser.getSelectedFile().getAbsolutePath(); 
						backgroundImagePanel=new BackgroundImagePanel(fileName, ImageCompositing.this);
						textureFrame=createInternalFrame("Texture Frame", backgroundImagePanel, textureFrame);
						ForegroundImagePanel foregroundImagePanelMask=new ForegroundImagePanel(fileName, ImageCompositing.this);
						internalForegroundFrameFilter=createInternalFrame2("ForegroundMask", foregroundImagePanelMask, internalForegroundFrameFilter);
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


			
			menuItem = new JMenuItem("Save Cut");
			menuItem.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){ 
					int returnValue=fileChooser.showSaveDialog(ImageCompositing.this);
					if (returnValue == JFileChooser.APPROVE_OPTION) {
						String fileName=fileChooser.getSelectedFile().getAbsolutePath();
						
						SaveCut(fileName);
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
					
					undo();

				}
			});

			menu.add(menuItem);
			menu.addSeparator();// -------------
			menuItem = new JMenuItem("Redo Background");
			menuItem.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					
					redo();

				}
			});
			menu.add(menuItem);

			menu.addSeparator();// -------------
			menuItem = new JMenuItem("Black & White");
			menuItem.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					internalForegroundFrame.ip2.pre.addElement((internalForegroundFrame.ip2.copyImage()));
					grayFore();

				}
			});
			menu.add(menuItem);
			menuItem = new JMenuItem("Red Channel");
			menuItem.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					internalForegroundFrame.ip2.pre.addElement((internalForegroundFrame.ip2.copyImage()));
					setChannel("RED");

				}
			});
			menu.add(menuItem);
			//menu.add(menuItem);
			menuItem = new JMenuItem("Green Channel");
			menuItem.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					internalForegroundFrame.ip2.pre.addElement((internalForegroundFrame.ip2.copyImage()));
					setChannel("GREEN");

				}
			});
			menu.add(menuItem);
			
			menuItem = new JMenuItem("Blue Channel");
			menuItem.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					internalForegroundFrame.ip2.pre.addElement((internalForegroundFrame.ip2.copyImage()));
					setChannel("BLUE");

				}
			});
			menu.add(menuItem);

			menuItem = new JMenuItem("Guassian");
			menuItem.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					internalForegroundFrame.ip2.pre.addElement((internalForegroundFrame.ip2.copyImage()));
					gauss();

				}
			});
			menu.add(menuItem);

			menuItem = new JMenuItem("LOG");
			menuItem.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					internalForegroundFrame.ip2.pre.addElement((internalForegroundFrame.ip2.copyImage()));
					LOG();

				}
			});
			menu.add(menuItem);
			
			menuItem = new JMenuItem("Zero Crossing");
			menuItem.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					internalForegroundFrame.ip2.pre.addElement((internalForegroundFrame.ip2.copyImage()));
					ZeroCrossing();

				}
			});
			menu.add(menuItem);
			
			/*!!!!1*/
			menuItem = new JMenuItem("Image Gradient");
			menuItem.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					
					Gradient();

				}
			});
			menu.add(menuItem);
			/*!!!!2*/
			
			/*!!!!1*/
			menuItem = new JMenuItem("Magnitude of Image Gradient");
			menuItem.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					
					magnitudeOfGradient();

				}
			});
			menu.add(menuItem);
			/*!!!!2*/
			
			menuItem = new JMenuItem("IS");
			menuItem.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){

					IS();

				}
			});
			menu.add(menuItem);
			menuItem = new JMenuItem("CUT");
			menuItem.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					if ((internalForegroundFrame!=null)){
						BufferedImage cut=CutPic();
						//cut=internalForegroundFrame.ip2.cutImage;
					ImagePanel cutImagePanel=new ImagePanel(cut);
					cutImagePanel.setImage(cut);
					
					if (icif2!=null){
						desktop.remove(icif2);
						icif2.dispose();
					}
					//internalBackgroundFrame=createInternalFrame("Cut", cutImagePanel, cutFrame);
					icif2=new ICInternalFrame("Cut",cutImagePanel,200,0);
		       		icif2.setVisible(true); //necessary as of 1.3
		       		desktop.add(icif2);
					
					}

				}
			});


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
	private ICInternalFrame createInternalFrame2(String frameTitle, ImagePanel ip, ICInternalFrame previousInternalFrame){
		if(ip.getImage()==null)return previousInternalFrame;

		ICInternalFrame icif=null;
		if(previousInternalFrame!=null){
			desktop.remove(previousInternalFrame);
			previousInternalFrame.dispose();
		}
		icif=new ICInternalFrame(frameTitle, ip);
       		icif.setVisible(false); //necessary as of 1.3
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
        frame = new ImageCompositing();

       

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
	private void set(){
		BufferedImage temp=internalForegroundFrame.ip2.copyImage();
		int pixel=(255<<24)+(255<<16)+(0<<8)+0;
		temp.setRGB(12,8,pixel);
		temp.setRGB(60,17,pixel);
		temp.setRGB(8,65,pixel);
		temp.setRGB(57,79,pixel);
		internalForegroundFrame.ip2.setImage(temp);
		temp=internalBackgroundFrame.ip2.copyImage();
		temp.setRGB(130,79,pixel);
		temp.setRGB(179,88,pixel);
		temp.setRGB(127,136,pixel);
		temp.setRGB(176,151,pixel);
		internalBackgroundFrame.ip2.setImage(temp);
		

	}
	  private void createCanvas()
      {
              //get the 4 clicked points of one image (in this code'internalForeGroundFrame')
              Point p1;
              Point p2;
              Point p3;
              Point p4;
              Point p5;
              Point p6;
              Point p7;
              Point p8;

			  p1=(Point) (textureFrame.ip2.points.elementAt(0));
              p2=(Point) (textureFrame.ip2.points.elementAt(1));
              p3=(Point) (textureFrame.ip2.points.elementAt(2));
              p4=(Point) (textureFrame.ip2.points.elementAt(3));
              Vector temp=new Vector();

              p5=(Point) (internalBackgroundFrame.ip2.points.elementAt(0));
             
              
              p6=(Point) (internalBackgroundFrame.ip2.points.elementAt(1));
              
             
              p7=(Point) (internalBackgroundFrame.ip2.points.elementAt(2));
              
              
              p8=(Point) (internalBackgroundFrame.ip2.points.elementAt(3));
              if (alignment){
              	p1=allignment(internalBackgroundFrame.ip2.bimg.getRGB((int)p5.getX(),(int)p5.getY()),p1,temp);
              	temp.addElement(p1);
              	p2=allignment(internalBackgroundFrame.ip2.bimg.getRGB((int)p6.getX(),(int)p6.getY()),p2,temp);
              	temp.addElement(p2);
              	p3=allignment(internalBackgroundFrame.ip2.bimg.getRGB((int)p7.getX(),(int)p7.getY()),p3,temp);
              	temp.addElement(p3);
              	p4=allignment(internalBackgroundFrame.ip2.bimg.getRGB((int)p8.getX(),(int)p8.getY()),p4,temp);
              }

              //Create 2 texture maps for forwards and backwards mapping
              TextureMap t1=new TextureMap((int)p1.getX(),(int)p1.getY(),(int)p2.getX(),(int)p2.getY(),(int)p3.getX(),(int)p3.getY(),
                             (int)p4.getX(),(int)p4.getY(),(int)p5.getX(),(int)p5.getY(),(int)p6.getX(),(int)p6.getY(),
                             (int)p7.getX(),(int)p7.getY(),(int)p8.getX(),(int)p8.getY());

              TextureMap t2=new TextureMap((int)p5.getX(),(int)p5.getY(),(int)p6.getX(),(int)p6.getY(),(int)p7.getX(),(int)p7.getY(),
                             (int)p8.getX(),(int)p8.getY(),(int)p1.getX(),(int)p1.getY(),(int)p2.getX(),(int)p2.getY(),
                             (int)p3.getX(),(int)p3.getY(),(int)p4.getX(),(int)p4.getY());

              //int declare the highest and lowest points for our final canvas to be those from just the backgroundImageFrame
              double sX=0;
              double lX=internalBackgroundFrame.ip2.getWidth();
              double sY=0;
              double lY=internalBackgroundFrame.ip2.getHeight();

              //Check which of the points from foregroundImageFrame mapped tobackGroundImageFrame
              //will go over the present size of the canvas.
              if (t2.texU(0,0)<sX) sX=t2.texU(0,0);
              //if (p1.getX()>lX) lX=p1.getX();
              if (t2.texV(0,0)<sY) sY=t2.texV(0,0);
              //if (p1.getY()>lY) lY=p1.getY();

              if (t2.texU(textureFrame.ip2.getWidth(),0)>lX)lX=t2.texU(textureFrame.ip2.getWidth(),0);
              if (t2.texV(textureFrame.ip2.getWidth(),0)<sY)sY=t2.texV(textureFrame.ip2.getWidth(),0);

              if(t2.texU(textureFrame.ip2.getWidth(),textureFrame.ip2.getHeight())>lX)lX=t2.texU(textureFrame.ip2.getWidth(),textureFrame.ip2.getWidth());
              if(t2.texV(textureFrame.ip2.getWidth(),textureFrame.ip2.getHeight())>lY)lY=t2.texV(textureFrame.ip2.getWidth(),textureFrame.ip2.getWidth());

              if (t2.texU(0,textureFrame.ip2.getHeight())<sX)sX=t2.texU(0,textureFrame.ip2.getHeight());
              if (t2.texV(0,textureFrame.ip2.getHeight())>lY)lY=t2.texV(0,textureFrame.ip2.getHeight());

              //calculate, given the smallest and largest x and y, the width and height of the canvas
              int width=(int)(lX-sX);
              int height=(int)(lY-sY);
              int edgex1;
              int edgex2;
              int edgex3=0;
              int edgex4;
              int edgey1;
              int edgey2;
              int edgey3=0;
              int edgey4;
              
              //where to place the top-left corner of the foregroundImage
              int xPlacement=(int)Math.abs(sX);
              int yPlacement=(int)Math.abs(sY);
              edgex1=xPlacement;
              edgey1=yPlacement;
              
              double x,y;
              x=xPlacement;
              y=yPlacement;
              BufferedImage Mos=new BufferedImage(width, height,BufferedImage.TYPE_INT_ARGB);
             
              for (int i=0;i<internalBackgroundFrame.ip2.getWidth();i++){
              	for (int j=0;j<internalBackgroundFrame.ip2.getHeight();j++){

              		Mos.setRGB((int)x, (int)y,internalBackgroundFrame.ip2.bimg.getRGB(i,j));
              		y++;
              		
              	}
              	x++;
              	y=yPlacement;
              }
              edgex2=edgex1+internalBackgroundFrame.ip2.getWidth();
              edgey2=edgey1+internalBackgroundFrame.ip2.getHeight();

			/*	double weight1=0;
				double weight2=1;
				try{ 
					Mos.getRGB((int)x,(int)y);
					System.out.println("NOT EMPOTY");
					weight1=1;
					weight2=0;
				
				}catch (Exception e){
					System.out.println("EMPTY");
				}*/
				
	       		int k=0;
	       		int k1=0;
	       		int k2=0;
				for (int i=0;i<width;i++){
					for (int j=0;j<height;j++){
						x=  t2.texU(i,j);
						y=  t2.texV(i,j);

							try{
								if (k==0){
									edgex3=(int) x+xPlacement;
									edgey4=(int) y+yPlacement;
									k++;
								}
								k1=(int) x+xPlacement;
								k2=(int)y+yPlacement;
						//Mos.setRGB((int) Math.floor(x+xPlacement),(int) Math.floor(y+yPlacement),internalForegroundFrame.ip2.bimg.getRGB(i,j));
						//	Mos.setRGB((int) Math.ceil(x+xPlacement),(int) Math.floor(y+yPlacement),internalForegroundFrame.ip2.bimg.getRGB(i,j));
							//Mos.setRGB((int) Math.floor(x+xPlacement),(int) Math.ceil(y+yPlacement),internalForegroundFrame.ip2.bimg.getRGB(i,j));
							//Mos.setRGB((int) Math.ceil(x+xPlacement),(int) Math.ceil(y+yPlacement),internalForegroundFrame.ip2.bimg.getRGB(i,j));
							}catch(ArrayIndexOutOfBoundsException e){}//System.out.println("x="+x+" y="+y);}	//}
						
					} 
				}
				edgex4=k1;
				edgey4=k2;
				double d1,d2;
				for (int i=0;i<width;i++){
					for (int j=0;j<height;j++){
						x=  t2.texU(i,j);
						y=  t2.texV(i,j);

							try{
								k=Mos.getRGB((int)x+xPlacement,(int)y+yPlacement);
	
	
		
		
	//	System.out.println((Math.pow(d1,3)/(Math.pow(d1,3)+(Math.pow(d2,3))))+"    "+(Math.pow(d2,3)/(Math.pow(d1,3)+(Math.pow(d2,3)))));
		k=Mos.getRGB((int)Math.floor(x+xPlacement),(int) Math.floor(y+yPlacement));
		if ((k==0)&&(blending)) {
			Mos.setRGB((int) Math.floor(x+xPlacement),(int) Math.floor(y+yPlacement),textureFrame.ip2.bimg.getRGB(i,j));
		}else{
			d1=(findEdge(new Point((int)Math.floor(x+xPlacement),(int)Math.floor(y+yPlacement)),new Point(edgex1,edgey1),new Point (edgex2,edgey2)));
			d2=(findEdge(new Point((int)Math.floor(x+xPlacement),(int)Math.floor(y+yPlacement)),new Point(edgex3,edgey3),new Point (edgex4,edgey4)));
			int k3= BlendPix(k,textureFrame.ip2.bimg.getRGB(i,j),(Math.pow(d1,3)/(Math.pow(d1,3)+(Math.pow(d2,3)))),(Math.pow(d2,3)/(Math.pow(d1,3)+(Math.pow(d2,3)))));
			Mos.setRGB((int) Math.floor(x+xPlacement),(int) Math.floor(y+yPlacement),k3);
		}
		k=Mos.getRGB((int)Math.ceil(x+xPlacement),(int) Math.floor(y+yPlacement));
		if ((k==0)&&(blending)){
			d1=(findEdge(new Point((int)Math.ceil(x+xPlacement),(int) Math.floor(y+yPlacement)),new Point(edgex1,edgey1),new Point (edgex2,edgey2)));
			d2=(findEdge(new Point((int)Math.ceil(x+xPlacement),(int) Math.floor(y+yPlacement)),new Point(edgex3,edgey3),new Point (edgex4,edgey4)));
			int k3= BlendPix(k,textureFrame.ip2.bimg.getRGB(i,j),(Math.pow(d1,3)/(Math.pow(d1,3)+(Math.pow(d2,3)))),(Math.pow(d2,3)/(Math.pow(d1,3)+(Math.pow(d2,3)))));
			Mos.setRGB((int)Math.ceil(x+xPlacement),(int) Math.floor(y+yPlacement),k3);
		}else{
			Mos.setRGB((int) Math.ceil(x+xPlacement),(int) Math.floor(y+yPlacement),textureFrame.ip2.bimg.getRGB(i,j));
		}
		k=Mos.getRGB((int) Math.floor(x+xPlacement),(int) Math.ceil(y+yPlacement));
		if ((k==0)&&(blending)){
			d1=(findEdge(new Point((int) Math.floor(x+xPlacement),(int) Math.ceil(y+yPlacement)),new Point(edgex1,edgey1),new Point (edgex2,edgey2)));
			d2=(findEdge(new Point((int) Math.floor(x+xPlacement),(int) Math.ceil(y+yPlacement)),new Point(edgex3,edgey3),new Point (edgex4,edgey4)));
			int k3= BlendPix(k,textureFrame.ip2.bimg.getRGB(i,j),(Math.pow(d1,3)/(Math.pow(d1,3)+(Math.pow(d2,3)))),(Math.pow(d2,3)/(Math.pow(d1,3)+(Math.pow(d2,3)))));
			Mos.setRGB((int) Math.floor(x+xPlacement),(int) Math.ceil(y+yPlacement),k3);
		}else{
			Mos.setRGB((int) Math.floor(x+xPlacement),(int) Math.ceil(y+yPlacement),textureFrame.ip2.bimg.getRGB(i,j));
		}
		k=Mos.getRGB((int) Math.ceil(x+xPlacement),(int) Math.ceil(y+yPlacement));
		if ((k==0)&&(blending)){
			d1=(findEdge(new Point((int) Math.ceil(x+xPlacement),(int) Math.ceil(y+yPlacement)),new Point(edgex1,edgey1),new Point (edgex2,edgey2)));
			d2=(findEdge(new Point((int) Math.ceil(x+xPlacement),(int) Math.ceil(y+yPlacement)),new Point(edgex3,edgey3),new Point (edgex4,edgey4)));
			int k3= BlendPix(k,textureFrame.ip2.bimg.getRGB(i,j),(Math.pow(d1,3)/(Math.pow(d1,3)+(Math.pow(d2,3)))),(Math.pow(d2,3)/(Math.pow(d1,3)+(Math.pow(d2,3)))));
			Mos.setRGB((int) Math.ceil(x+xPlacement),(int) Math.ceil(y+yPlacement),k3);
		}else{
			Mos.setRGB((int) Math.ceil(x+xPlacement),(int) Math.ceil(y+yPlacement),textureFrame.ip2.bimg.getRGB(i,j));
		}
		
			}catch(Exception e){}//System.out.println("x="+x+" y="+y);}	//}
					
					} 
				}
             Vector t5=internalBackgroundFrame.ip2.pre;
             Vector t6=internalBackgroundFrame.ip2.next;
              BackgroundImagePanel MosPanel=new BackgroundImagePanel(Mos,ImageCompositing.this);
              MosPanel.setImage(Mos);
              internalBackgroundFrame=createInternalFrame("Working Frame",MosPanel,internalBackgroundFrame);
              internalBackgroundFrame.ip2.pre=t5;
              internalBackgroundFrame.ip2.next=t6;
              //Mo.setVisible(true); //necessary as of 1.3
              //desktop.add(Mo);

      }

	private Point allignment(int pix1,Point start,Vector temp){
		Point result=null;
		int diff=200;
		int tempdif;
		boolean b=false;
		for (int i=-20;i<20;i++){
			for (int j=-20;j<20;j++){
				if (((start.getX()+i)>=0)&&((start.getX()+i)<textureFrame.ip2.getWidth())&&(start.getY()+j>=0)&&((start.getY()+j)<textureFrame.ip2.getHeight())){
					tempdif=Math.abs(pix1-textureFrame.ip2.bimg.getRGB((int)start.getX()+i,(int) start.getY()+j));
					if (tempdif<diff){
						for (int k=0;k<temp.size();k++){
							Point p=(Point)temp.elementAt(k);
							if (p.equals(new Point((int)start.getX()+i,(int)start.getY()+j))) b=true;
						}
						if (!b){
						diff=tempdif;
						result=new Point((int)start.getX()+i,(int)start.getY()+j);
						}
						b=false;
					}
				}
			}
		}

		return result;
	}
	
	private double findEdge(Point p,Point start,Point end){
		double a=Math.abs((p.getX()-start.getX()));
		double b=Math.abs((p.getY()-start.getY()));
		double c=Math.abs((p.getX()- end.getX()));
		double d=Math.abs(p.getY()-end.getY());
		return Math.min(Math.min(a,b),Math.min(c,d));
	}
    private void getpoints(){

		Point p1;
		Point p2;
		Point p3;
		Point p4;
		Point p5;
		Point p6;
		Point p7;
		Point p8;
		DoubleMatrix2D mapMatrix;
		BufferedImage Mos=new BufferedImage(internalBackgroundFrame.ip2.getWidth()+internalForegroundFrame.ip2.getWidth()
				, internalForegroundFrame.ip2.getHeight()+internalBackgroundFrame.ip2.getHeight(), BufferedImage.TYPE_INT_ARGB);
		p1=(Point) (internalForegroundFrame.ip2.points.elementAt(0));
		p2=(Point) (internalForegroundFrame.ip2.points.elementAt(1));
		p3=(Point) (internalForegroundFrame.ip2.points.elementAt(2));
		p4=(Point) (internalForegroundFrame.ip2.points.elementAt(3));
		
		p5=(Point) (internalBackgroundFrame.ip2.points.elementAt(0));
		
		p6=(Point) (internalBackgroundFrame.ip2.points.elementAt(1));
		
		p7=(Point) (internalBackgroundFrame.ip2.points.elementAt(2));
		p8=(Point) (internalBackgroundFrame.ip2.points.elementAt(3));
		
		p1=checkPixels(p5,p1);
		p2=checkPixels(p6,p2);
		p3=checkPixels(p7,p3);
		p4=checkPixels(p8,p4);
			//TextureMap t=new TextureMap((int) p1.getX(),(int)p1.getY(),(int)p2.getX(),(int)p2.getY(),(int)p3.getX(),(int)p3.getY(),
				//	(int)p4.getX(),(int)p4.getY(),(int)p5.getX(),(int)p5.getY(),(int)p6.getX(),(int)p6.getY(),
					//(int)p7.getX(),(int)p7.getY(),(int)p8.getX(),(int)p8.getY()); 
			
			TextureMap t=new TextureMap((int) p5.getX(),(int)p5.getY(),(int)p6.getX(),(int)p6.getY(),(int)p7.getX(),(int)p7.getY(),
				(int)p8.getX(),(int)p8.getY(),(int)p1.getX(),(int)p1.getY(),(int)p2.getX(),(int)p2.getY(),
					(int)p3.getX(),(int)p3.getY(),(int)p4.getX(),(int)p4.getY()); 
				int k=internalBackgroundFrame.ip2.getWidth()+internalForegroundFrame.ip2.getWidth();
				int k2=internalForegroundFrame.ip2.getHeight()+internalBackgroundFrame.ip2.getHeight();
			//TextureMap t=new TextureMap(130,79,179,88,127,136,176,151,12,8,60,17,8,65,57,79);
				
				//130  79    12   8
				 //179  88    60  17
				 //127 136     8  65
				 //176 151    57  79

	       		double x,y;
				for (int i=0;i<k;i++){
					for (int j=0;j<k2;j++){
						x=  t.texU(i,j);
						y=  t.texV(i,j);
						if((i<internalBackgroundFrame.ip2.getWidth())&&(j<internalBackgroundFrame.ip2.getHeight())){
							Mos.setRGB(i, j,internalBackgroundFrame.ip2.bimg.getRGB(i,j));
						}
						if ((Math.ceil(x)<k)&&(Math.ceil(y)<k2)&&(x>=0)&&(y>=0)&&(i<internalForegroundFrame.ip2.getWidth())&&(j<internalForegroundFrame.ip2.getHeight())){
							//Mos.setRGB((int) x,(int)y,internalForegroundFrame.ip2.bimg.getRGB(i,j));
							Mos.setRGB((int) Math.floor(x),(int) Math.floor(y),internalForegroundFrame.ip2.bimg.getRGB(i,j));
							 Mos.setRGB((int) Math.ceil(x),(int) Math.floor(y),internalForegroundFrame.ip2.bimg.getRGB(i,j));
							 Mos.setRGB((int) Math.floor(x),(int) Math.ceil(y),internalForegroundFrame.ip2.bimg.getRGB(i,j));
							Mos.setRGB((int) Math.ceil(x),(int) Math.ceil(y),internalForegroundFrame.ip2.bimg.getRGB(i,j));
						}
					} 
				}
				/*
	       		 for (int i=0;i<internalBackgroundFrame.ip2.getWidth();i++){
	       			for (int j=0;j<internalBackgroundFrame.ip2.getHeight();j++){
	       				//x=(int) t.texU(i,j);			
						//y=(int) t.texV(i,j);
						//System.out.println(x+"       "+y);
						//if ((x>0)&&(y>0)&&(x<k)&&(y<k2)){
						Mos.setRGB(i, j,internalBackgroundFrame.ip2.bimg.getRGB(i,j));
						//}
	       			}
	       		}
	       		for (int i=0;i<internalForegroundFrame.ip2.getWidth();i++){
	       			for (int j=0;j<internalForegroundFrame.ip2.getHeight();j++){
	       				x=t.texU(i,j);			
						y= t.texV(i,j);
						//System.out.println(x+"       "+y);
						if ((x>=0)&&(y>=0)&&(x<k)&&(y<k2)){
						Mos.setRGB((int) Math.floor(x),(int) Math.floor(y),internalForegroundFrame.ip2.bimg.getRGB(i,j));
						Mos.setRGB((int) Math.ceil(x),(int) Math.ceil(y),internalForegroundFrame.ip2.bimg.getRGB(i,j));
						Mos.setRGB((int) x,(int) y,internalForegroundFrame.ip2.bimg.getRGB(i,j));
						}
	       			}
	       		}*/
				ImagePanel MosPanel=new ImagePanel(Mos);
				MosPanel.setImage(Mos);
				Mo=new ICInternalFrame("Cut",MosPanel,200,0);
	       		Mo.setVisible(true); //necessary as of 1.3
	       		desktop.add(Mo); 
	}
    private int BlendPix(int pix1,int pix2,double w1,double w2){
		int red   = (pix1 >> 16) & 0xff;
		int green = (pix1 >> 8 ) & 0xff;
		int blue  = (pix1      ) & 0xff;
		
		int red2   = (pix2 >> 16) & 0xff;
		int green2 = (pix2 >> 8 ) & 0xff;
		int blue2  = (pix2      ) & 0xff;
		
		return (255<<24)+((int)((w1*red)+(w2*red2))<<16)+((int)((w1*green)+(w2*green2))<<8)+(int)((w1*blue)+(w2*blue2));
    }
   private Point checkPixels(Point pixel1,Point pixel2){
    	Point p;
    	p=pixel2;
    	int pix=internalBackgroundFrame.ip2.bimg.getRGB((int)pixel1.getX(),(int)pixel1.getY());
    	int pix2=internalForegroundFrame.ip2.bimg.getRGB((int)pixel2.getX(),(int)pixel2.getY());
    	int min=Math.abs(pix-pix2);
    	for (int i=-3;i<4;i++){
    		for (int j=-3;j<4;j++){
    			if (((i+pixel2.getX()>=0)&&(i+pixel2.getX()>internalForegroundFrame.ip2.getWidth()))&&(j+pixel2.getY()>=0)&&(j+pixel2.getX()<internalBackgroundFrame.ip2.getHeight())){
    				pix2=Math.abs(pix-internalForegroundFrame.ip2.bimg.getRGB(i+internalForegroundFrame.ip2.getWidth(),j+internalForegroundFrame.ip2.getHeight()));
    				if (min>pix2){
    					min=pix2;
    					p=new Point(i+i+internalForegroundFrame.ip2.getWidth(),j+i+internalForegroundFrame.ip2.getHeight());
    				}
    			}
    		}
    	}
    	return p;
    }

    private void SaveCut(String file){
	    if (icif2!=null){
	        boolean success = (new File(file)).delete();
		if (!success) {
			// Deletion failed
		}
		try{
			ImageIO.write((icif2.ip2.copyImage3()), "jpg", new File(file));
		}catch (Exception e){System.out.println(e);}
	    }else{
	    	
	    }
		
    }

    private void undo(){
    	System.out.println(internalBackgroundFrame.ip2.pre.size());
	    if ((internalBackgroundFrame!=null)&&(internalBackgroundFrame.ip2.pre.size()!=0)){
	    	internalBackgroundFrame.ip2.next.addElement(internalBackgroundFrame.ip2.copyImage());
	    	BufferedImage temp=(BufferedImage) internalBackgroundFrame.ip2.pre.lastElement();

	    	internalBackgroundFrame.ip2.pre.remove(internalBackgroundFrame.ip2.pre.size()-1);
	    	if (internalBackgroundFrame.ip2.points.size()!=0) internalBackgroundFrame.ip2.points.removeElementAt(internalBackgroundFrame.ip2.points.size()-1); 
	    	internalBackgroundFrame.ip2.setImage(temp);
	    	System.out.println(internalBackgroundFrame.ip2.points.size());
	    }
	
    }
    private void redo(){
	    if ((internalBackgroundFrame!=null)&&(internalBackgroundFrame.ip2.next.size()!=0)){
	    	internalBackgroundFrame.ip2.pre.addElement(internalBackgroundFrame.ip2.copyImage());
	    	BufferedImage temp=(BufferedImage) internalBackgroundFrame.ip2.next.lastElement();
	    	internalBackgroundFrame.ip2.next.remove(internalBackgroundFrame.ip2.next.size()-1);
	    	
	    	internalBackgroundFrame.ip2.setImage(temp);
	    }
	
    }

	private void grayFore(){
		if ((internalBackgroundFrame!=null)){
	       	internalBackgroundFrame.ip2.setImage(internalBackgroundFrame.ip2.setGray(internalBackgroundFrame.ip2.copyImage()));
	    }
	}
	private void setChannel(String s){
		if ((internalBackgroundFrame!=null)){
	       	internalBackgroundFrame.ip2.setImage(internalBackgroundFrame.ip2.Channel(internalBackgroundFrame.ip2.copyImage(),s));
	    }
	}


	private void gauss(){
		if ((internalBackgroundFrame!=null)){
//internalForegroundFrame.ip2.createLOGMask2();
			double sigma=1.0;
			internalBackgroundFrame.ip2.setImage(internalBackgroundFrame.ip2.applyGaussianSmoothing(sigma));
	    }
	}
	private void LOG(){
		if ((internalBackgroundFrame!=null)){
//internalForegroundFrame.ip2.createMask();
			double sigma=1.0;
			internalBackgroundFrame.ip2.setImage(internalBackgroundFrame.ip2.applyLOG2(sigma));
	       
	    }
	}
	private void ZeroCrossing(){
		if ((internalBackgroundFrame!=null)){
//internalForegroundFrame.ip2.createMask();
			internalBackgroundFrame.ip2.setImage(internalBackgroundFrame.ip2.ZeroCrossing(internalBackgroundFrame.ip2.copyImage()));
	

	    }
	}
	private void IS(){
		internalBackgroundFrame.ip2.cost(internalBackgroundFrame.ip2.setGray(internalBackgroundFrame.ip2.copyImage()));
	}
	private void cut(){
		cut2=internalBackgroundFrame.ip2.cut();
	}

	
	private void Gradient(){
		if ((internalForegroundFrame!=null)){
			//internalForegroundFrame.ip2.createMask();
			internalBackgroundFrame.ip2.setImage(internalBackgroundFrame.ip2.applyGradient(internalBackgroundFrame.ip2.copyImage()));
				    }
	}
	

	private void magnitudeOfGradient(){
		if ((internalForegroundFrame!=null)){
			//internalForegroundFrame.ip2.createMask();
			internalBackgroundFrame.ip2.setImage(internalBackgroundFrame.ip2.applyMagnitudeOfGradient(internalBackgroundFrame.ip2.copyImage()));
				    }
	}
	
	
	public BufferedImage CutPic(){
		BufferedImage cutImage=new BufferedImage(internalBackgroundFrame.ip2.getWidth(),internalBackgroundFrame.ip2.getHeight(),BufferedImage.TYPE_INT_ARGB);
		Vector cut=internalBackgroundFrame.ip2.cut;
		QuickSort q=new QuickSort();
		Vector y=new Vector();
		if (cut.size()>1){
			cut=q.quickSortX(cut);
			Point temp;
			Point temp2;
			for (int i=0;i<cut.size()-1;i++){
				temp=(Point) cut.elementAt(i);
				temp2=(Point) cut.elementAt(i+1);
				if (temp.x==temp2.x){
					y.add(temp);
				}else{
					y.add(temp);
					y=q.quickSortY(y);
					temp=(Point) y.elementAt(0);
					temp2=(Point) y.elementAt(y.size()-1);
					for (int j=temp.y;j<temp2.y;j++){
						
						cutImage.setRGB(temp2.x,j,(internalBackgroundFrame.ip2.Origninalbimg.getRGB(temp2.x,j)));
					}
					y=new Vector();
				}
			}
		}
		/*JFrame jBackground=new JFrame("CUT");
		jBackground.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ImagePanel ipBackground =new ImagePanel(New);
		jBackground.getContentPane().add(ipBackground,BorderLayout.CENTER);
		jBackground.pack();
		jBackground.setVisible(true); */
		cut2=cutImage;
		return cutImage;
	}
	
    public void setMapped(int x,int y)
    {
    	linear=false;
    	boolean distanceShade=q1option;
    	int red=0;
    	int green=0;
    	int blue=0;
    	double gradientSize=0.5;
    	
        if(distanceShade)
        {
        	internalForegroundFrameFilter.ip2.paintImage(textureFrame.ip2.copyImage(),0,0);
            BufferedImage b=internalBackgroundFrame.ip2.setDistImage(textureFrame.ip2.copyImage(),red,green,blue,gradientSize);//createMappedImage(internalForegroundFrame.ip.copyImage(),true,0,0,0);//internalBackgroundFrame.ip.createMappedImage(internalForegroundFrame.ip.copyImage(),true,0,0,0);
            internalForegroundFrameFilter.ip2.paintImage(b,0,0);//internalBackgroundFrame.ip.createMappedImage(b,false,0,0,0),x,y);
            internalBackgroundFrame.ip2.paintImage(internalBackgroundFrame.ip2.createMappedImage(internalForegroundFrameFilter.ip2.copyImage(),false,0,0,0),x,y);
            //internalForegroundFrameFilter.ip.paintImage(internalForegroundFrameFilter.ip.createMappedImage(internalForegroundFrame.ip.copyImage(),false,0,0,0),x,y);
        }
        else
        {
            internalBackgroundFrame.ip2.paintImage(internalBackgroundFrame.ip2.createMappedImage(textureFrame.ip2.copyImage(),false,0,0,0),x,y);
        }
    	//createMappedImage(BufferedImage texture)   paintImage(BufferedImage b, int x, int y)
    }
    protected static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = Menu.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
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
