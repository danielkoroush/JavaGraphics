//Youssef Aboul-Naja  (Student#: 991489073)
//Hoa Nguyen  (Student#: 991043116)

import javax.swing.*; 
import java.awt.*; 
import java.awt.image.*;

public class ImageLoader extends JPanel { 
	private static ImageLoader il=new ImageLoader(); 
	/** 
	* @param fileName the name of a file containing an image JPEG, GIF etc. 
	* @return a BufferedImage holding the corresponding image, null if unable to load the image 
	*/ 
	public static BufferedImage loadImage(String fileName){ return il.load(fileName); } 

	/** 
	* @param fileName the name of a file containing an image JPEG, GIF etc. 
	* @return a BufferedImage holding the corresponding image, null if unable to load the image 
	*/ 
	private BufferedImage load(String fileName){ 
		// Algorithm: Load the image into an image icon 
		// create an appropriate BufferedImage (same size as the given image) 
		// paint the image icon into the buffered image 
		ImageIcon imgi = new ImageIcon(fileName); 
		Image img = imgi.getImage(); 
		int width = img.getWidth(this); 
		int height = img.getHeight(this); 
		if(width==-1 || height==-1) return null; 
		BufferedImage bimage = new BufferedImage(width, height,BufferedImage.TYPE_INT_ARGB); 
		Graphics2D bimageContext = bimage.createGraphics(); 
		bimageContext.drawImage(img, 0, 0, null); 
		return bimage; 
	} 
}
