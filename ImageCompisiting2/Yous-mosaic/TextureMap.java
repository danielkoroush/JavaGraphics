//Youssef Aboul-Naja  (Student#: 991489073)
//Hoa Nguyen  (Student#: 991043116)

import javax.swing.JInternalFrame;
import javax.swing.JDesktopPane;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JMenuBar;
import javax.swing.JFrame;
import javax.swing.JFileChooser;
import java.awt.event.*;
import java.awt.*;
import javax.swing.JOptionPane;

/**
 * This class will initialize the desktop for the program TextureMap
 */ 
public class TextureMap extends JFrame {

    JFileChooser fileChooser; // when we want to load/save a file
    JDesktopPane desktop;
    ICInternalFrame internalTextureFrame;
    ICInternalFrame internalImageFrame;
	
	ICInternalFrame internalThumbnailFrame; // the frame to hold thumnail image panels
	
    
    JMenu imageSelector;
    JMenuItem openImageJMenu;
	//////////////// open images for mosaic op.
	JMenuItem mosaicImages;
	JMenuItem textureImage;
	////////////////////////
    ResultantImagePanel resultantImagePanel;//the resultant image
    TextureImagePanel texturePanel;//the current texture

	boolean AUTO_WARP = false;
	boolean BLENDING = false;
	public static int TOOL_IN_USE;
	public final static int NORMAL_TEXTURE_MAP=0;
	public final static int BASIC_MOSAIC=1;
    // keep track of the number of images has been open in thumbnail frame. 15 is maximum number allowed
	int maxNumImages = 0;
	// for mosaic operation, action is taken only when there have been 2 images chosen
	int totalImages=0;

    public TextureMap() {
        super("TextureMap");

		fileChooser = new JFileChooser();

        //Make the big window be indented 50 pixels from each edge
        //of the screen.
        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset, screenSize.width - inset*2, screenSize.height - inset*2);

        //Set up the GUI.
        desktop = new JDesktopPane(); //a specialized layered pane
        setContentPane(desktop);
        setJMenuBar(createMenuBar());

        //Make dragging a little faster but perhaps uglier.
        //desktop.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
    }
	
	/**
	 * create an menu bar for GUI
	 * @return an menu bar for GUI
	 */
	private JMenuBar createMenuBar(){
		JMenuBar menuBar = new JMenuBar();
		JMenu menu;
		JMenuItem menuItem;

		imageSelector = new JMenu("Selecting Images");
	  
		//select texture menu item ----------
		textureImage = new JMenuItem("Open Texture");

		textureImage.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				int returnValue=fileChooser.showOpenDialog(TextureMap.this);
				if (returnValue == JFileChooser.APPROVE_OPTION) {
					openImageJMenu.setEnabled(true);
					mosaicImages.setEnabled(false);
					String fileName=fileChooser.getSelectedFile().getAbsolutePath(); 
					texturePanel=new TextureImagePanel(fileName, TextureMap.this);
					internalTextureFrame=createInternalFrame("Texture", texturePanel, internalTextureFrame);
				} 
			}
		});
   
		imageSelector.add(textureImage);
  
		//Open Image menu Item------------------
		openImageJMenu = new JMenuItem("Open Resultant Image");
		// Below we declare an instance of an anonymous inner class
		// it implements the ActionListener interface
		openImageJMenu.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				int returnValue=fileChooser.showOpenDialog(TextureMap.this);
				    if (returnValue == JFileChooser.APPROVE_OPTION) {
                  
					String fileName=fileChooser.getSelectedFile().getAbsolutePath(); 
					resultantImagePanel=new ResultantImagePanel(fileName, TextureMap.this);
					internalImageFrame=createInternalFrame("Image", resultantImagePanel, internalImageFrame);
                } 
			}
		});
   
		//you cant select an image until you select a texture
		openImageJMenu.setEnabled(false);
		imageSelector.add(openImageJMenu);
 
		imageSelector.addSeparator();// -------------
		
		///////////////open image for mosaic operation /////////////////
	  
		//select texture menu item ----------
		mosaicImages = new JMenuItem("Open images for mosaic op.");

		mosaicImages.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(maxNumImages == 15){
					JOptionPane.showMessageDialog(TextureMap.this,"Error: We allow maximum 15 images","Error", JOptionPane.ERROR_MESSAGE);
				}
				else {
					int returnValue=fileChooser.showOpenDialog(TextureMap.this);
					if (returnValue == JFileChooser.APPROVE_OPTION) {
						maxNumImages++;
						String fileName=fileChooser.getSelectedFile().getAbsolutePath(); 
						ThumbnailImagePanel tip=new ThumbnailImagePanel(fileName, TextureMap.this);
						createOrUpdateThumbnailFrame(tip);
					} 
				}
			}
		});
   		mosaicImages.setEnabled(false);
		imageSelector.add(mosaicImages);
		//////////////////////////////////////////////////////////
		
		

		menuBar.add(imageSelector);
  
		imageSelector.setEnabled(false);
  
		menu = new JMenu("Tools");
  
		menuItem = new JMenuItem("Normal Texture map");
		menuItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				TOOL_IN_USE = NORMAL_TEXTURE_MAP;
				ICInternalFrame.reset();
				totalImages = 0;
				maxNumImages = 0;
				if(internalTextureFrame!=null){
					desktop.remove(internalTextureFrame);
					internalTextureFrame.dispose();
				}
				if(internalImageFrame!=null){
					desktop.remove(internalImageFrame);
					internalImageFrame.dispose();
				} 
				if(internalThumbnailFrame!=null){
					desktop.remove(internalThumbnailFrame);
					internalThumbnailFrame.dispose();
					internalThumbnailFrame=null;
				} 
				TextureMap.this.update(TextureMap.this.getGraphics());
				
				imageSelector.setEnabled(true);
				textureImage.setEnabled(true);
				mosaicImages.setEnabled(false);
			}
		});
		menu.add(menuItem);
		
		///////////////// basic mosaic option ///////////////////
		menuItem = new JMenuItem("Basic Mosaic");
		menuItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){ 
				TOOL_IN_USE = BASIC_MOSAIC;
				ICInternalFrame.reset();
				totalImages = 0;
				maxNumImages = 0;
				AUTO_WARP = false;
				BLENDING = false;
				if(internalTextureFrame!=null){
					desktop.remove(internalTextureFrame);
					internalTextureFrame.dispose();
				}
				if(internalImageFrame!=null){
					desktop.remove(internalImageFrame);
					internalImageFrame.dispose();
				}
				if(internalThumbnailFrame!=null){
					desktop.remove(internalThumbnailFrame);
					internalThumbnailFrame.dispose();
					internalThumbnailFrame=null;
				} 
				TextureMap.this.update(TextureMap.this.getGraphics());
				
				imageSelector.setEnabled(true);
				textureImage.setEnabled(false);
				openImageJMenu.setEnabled(false);
				mosaicImages.setEnabled(true);
			}
		});
		menu.add(menuItem);
		//////////////////////////////////

		///////////////// advanced mosaic option ///////////////////
		menuItem = new JMenuItem("Advanced Mosaic (With only Warping)");
		menuItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{ 
				TOOL_IN_USE = BASIC_MOSAIC;
				ICInternalFrame.reset();
				totalImages = 0;
				maxNumImages = 0;
				AUTO_WARP = true;
				BLENDING = false;
				if(internalTextureFrame!=null)
				{
					desktop.remove(internalTextureFrame);
					internalTextureFrame.dispose();
				}
				if(internalImageFrame!=null)
				{
					desktop.remove(internalImageFrame);
					internalImageFrame.dispose();
				}
				if(internalThumbnailFrame!=null)
				{
					desktop.remove(internalThumbnailFrame);
					internalThumbnailFrame.dispose();
					internalThumbnailFrame=null;
				} 
				TextureMap.this.update(TextureMap.this.getGraphics());
				
				imageSelector.setEnabled(true);
				textureImage.setEnabled(false);
				openImageJMenu.setEnabled(false);
				mosaicImages.setEnabled(true);
			}
		});
		menu.add(menuItem);
		//////////////////////////////////

		///////////////// advanced mosaic option ///////////////////
		menuItem = new JMenuItem("Advanced Mosaic (With only Color Blending)");
		menuItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{ 
				TOOL_IN_USE = BASIC_MOSAIC;
				ICInternalFrame.reset();
				totalImages = 0;
				maxNumImages = 0;
				AUTO_WARP = false;
				BLENDING =  true;
				if(internalTextureFrame!=null)
				{
					desktop.remove(internalTextureFrame);
					internalTextureFrame.dispose();
				}
				if(internalImageFrame!=null)
				{
					desktop.remove(internalImageFrame);
					internalImageFrame.dispose();
				}
				if(internalThumbnailFrame!=null)
				{
					desktop.remove(internalThumbnailFrame);
					internalThumbnailFrame.dispose();
					internalThumbnailFrame=null;
				} 
				TextureMap.this.update(TextureMap.this.getGraphics());
				
				imageSelector.setEnabled(true);
				textureImage.setEnabled(false);
				openImageJMenu.setEnabled(false);
				mosaicImages.setEnabled(true);
			}
		});
		menu.add(menuItem);
		//////////////////////////////////
		
		///////////////// advanced mosaic option ///////////////////
		menuItem = new JMenuItem("Super Advanced Mosaic (With Warping & Blending)");
		menuItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{ 
				TOOL_IN_USE = BASIC_MOSAIC;
				ICInternalFrame.reset();
				totalImages = 0;
				maxNumImages = 0;
				AUTO_WARP = true;
				BLENDING =  true;
				if(internalTextureFrame!=null)
				{
					desktop.remove(internalTextureFrame);
					internalTextureFrame.dispose();
				}
				if(internalImageFrame!=null)
				{
					desktop.remove(internalImageFrame);
					internalImageFrame.dispose();
				}
				if(internalThumbnailFrame!=null)
				{
					desktop.remove(internalThumbnailFrame);
					internalThumbnailFrame.dispose();
					internalThumbnailFrame=null;
				} 
				TextureMap.this.update(TextureMap.this.getGraphics());
				
				imageSelector.setEnabled(true);
				textureImage.setEnabled(false);
				openImageJMenu.setEnabled(false);
				mosaicImages.setEnabled(true);
			}
		});
		menu.add(menuItem);
		//////////////////////////////////

		menuBar.add(menu);

		return menuBar;
	}
	
	/**
	 * if the internal frame is null, create a new one and put an image panel into it
	 * otherwise put an image panel into it
	 * @param ip the next immage panel to be put in the thumbnail frame
	 **/
	private void createOrUpdateThumbnailFrame(ImagePanel ip){
		if(internalThumbnailFrame == null){
			internalThumbnailFrame = new ICInternalFrame("Thumbnail frame");
			internalThumbnailFrame.setVisible(true); //necessary as of 1.3
        	desktop.add(internalThumbnailFrame);
		}
		internalThumbnailFrame.getContentPane().add(ip);
		internalThumbnailFrame.pack();
		try { internalThumbnailFrame.setSelected(true); } 
		catch (java.beans.PropertyVetoException e) {}
	}

	/**
	 * create a new internal frame with a title and an image panel
	 * @return an internal frame with a title and an image panel
	 * @param frameTitle the title of this frame
	 * @param ip the image panel to be put in this frame
	 **/
	public ICInternalFrame createInternalFrame(String frameTitle, ImagePanel ip){
		ICInternalFrame icif=null;
		
		icif=new ICInternalFrame(frameTitle, ip);
        icif.setVisible(true); //necessary as of 1.3
        desktop.add(icif);
        try { icif.setSelected(true); } 
		catch (java.beans.PropertyVetoException e) {}
		return icif;
	}
	
	/**
	 * update the graphical content of this frame
	 */
	public void update()
	{
		update(this.getGraphics());
	}
	
	/**
	 * remove the texture frame and its contents and set the 
	 * pointer to the texture frame to null
	 */
	public void destroyTextureFrame()
	{
		if(internalTextureFrame!=null)
		{
			desktop.remove(internalTextureFrame);
			internalTextureFrame.dispose();
			totalImages--;
			internalTextureFrame = null;
			texturePanel = null;
		}
	}
	
	/**
	 * remove the thumbnail frame and its contents and set the 
	 * pointer to the thumbnail frame to null
	 */
	public void destroyThumbnailFrame(){
		if(internalThumbnailFrame != null){
			desktop.remove(internalThumbnailFrame);
			internalThumbnailFrame.dispose();
			internalThumbnailFrame = null;
		}
	}
	
	//Internal frame creator.  Adds an internal frame to the 
	//desktop
	public ICInternalFrame createInternalFrame(String frameTitle, ImagePanel ip, ICInternalFrame previousInternalFrame){
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
        TextureMap frame = new TextureMap();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Display the window.
        frame.setVisible(true);
    }

	/**
	 * main method
	 */
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
