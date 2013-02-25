import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.util.*;

public class Paint {



	//This method extract the foreground by comparing the picture to the background and setting background
	// pixels alpha to 0 and foreground pixels alpha to 255 and return the final result as a bufferedImage
	public BufferedImage ForeGround(BufferedImage back, BufferedImage fore){
			if ((back.getWidth(null)!=fore.getWidth(null))||(back.getHeight(null)!=fore.getHeight(null))){
				
				return null;
			}
			
			int w=back.getWidth(null), h=back.getHeight(null);
			BufferedImage result= new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
			for(int x=0;x<w;x++){
				for(int y=0;y<h;y++){
					int pixel=back.getRGB(x,y);

					int red   = (pixel >> 16) & 0xff;
					int green = (pixel >> 8 ) & 0xff;
					int blue  = (pixel      ) & 0xff;
					int pixel2=fore.getRGB(x,y);

					int red2  = (pixel2 >> 16) & 0xff;
					int green2 = (pixel2 >> 8 ) & 0xff;
					int blue2  = (pixel2      ) & 0xff;
						 if (!(((red<red2+15)&&(red>red2-15))
							&&
						((green<green2+15)&&(green>green2-15))
							&&
						((blue<blue2+15)&&(blue>blue2-15)))) {
							pixel=(255<<24)+(red2<<16)+(green2<<8)+blue2;
							result.setRGB(x,y,pixel);
							
						}else{
							pixel=(0<<24)+(red2<<16)+(green2<<8)+blue2;
							result.setRGB(x,y,pixel);
						}
					
				}
			}
	return result;

	}

	// This method creats the background from the file names given in args [] and send
	// the final result as a BufferedImage
	public BufferedImage BackGround(String args []){						
		Vector Images=new Vector();
		ImagePanel ipBackground;
		for (int i=0;i<args.length;i++){
			ipBackground=new ImagePanel(args[i]);
			BufferedImage biForeground=ipBackground.copyImage();
			Images.addElement(biForeground);
		}
		Vector Result=setAlpha(Images,args.length);
		return (finish(Result,(BufferedImage)Result.elementAt(0)));


	}

	// this method gets the background from the series of images in the Vectore Images by
	// comparing each pixel and taking the average of each pixel in all images.
	public static BufferedImage finish(Vector Images, BufferedImage start){
		BufferedImage result= new BufferedImage(start.getWidth(null),start.getHeight(null),BufferedImage.TYPE_INT_ARGB);
		BufferedImage temp;
		int FRed=0;
		int FBlue=0;
		int FGreen=0;
		int k=0;
		Double temp2;
		int pixel=0;
		int w=result.getWidth(null), h=result.getHeight(null);
			for(int x=0;x<w;x++){
				for(int y=0;y<h;y++){
					for (int i=0;i<Images.size();i++){
						temp=(BufferedImage) Images.elementAt(i);
						pixel=temp.getRGB(x,y);
						int alpha = (pixel >> 24) & 0xff;
						if (alpha!=0){
							FRed=FRed+ ((pixel >> 16) & 0xff);
							FGreen =FGreen+ ((pixel >> 8 ) & 0xff);
							FBlue = FBlue+((pixel      ) & 0xff);
							k++;
							
						}
				
					
					}
				
				if (k!=0){
					FRed=(int) FRed/k;
					FBlue=(int) FBlue/k;
					FGreen=(int) FGreen/k;
					pixel=(255<<24)+(FRed<<16)+(FGreen<<8)+FBlue;
				}else{
					pixel=(0<<24)+(FRed<<16)+(FGreen<<8)+FBlue;
				}
				k=0;
				result.setRGB(x,y,pixel);
				FRed=0;
				FGreen=0;
				FBlue=0;

				}
			}


		return result;
		
	}

	// Set the alpha component of the given image to 0 by comparing it to other images
	// to extract the background
	public static Vector setAlpha(Vector Images, int num){
		Vector Result=new Vector();
		BufferedImage bimg;
		BufferedImage temp;
		int k=0;
		for (int i=0;i<Images.size();i++){
			bimg=(BufferedImage) Images.elementAt(i);
			int w=bimg.getWidth(null), h=bimg.getHeight(null);
			for(int x=0;x<w;x++){
				for(int y=0;y<h;y++){
					int pixel=bimg.getRGB(x,y);
					int alpha = (pixel >> 24) & 0xff;
					int red   = (pixel >> 16) & 0xff;
					int green = (pixel >> 8 ) & 0xff;
					int blue  = (pixel      ) & 0xff;
					for (int j=0;j<Images.size();j++){
						temp=(BufferedImage) Images.elementAt(j);
						int pixel2=temp.getRGB(x,y);
						int alpha2 = (pixel2 >> 24) & 0xff;
						int red2   = (pixel2 >> 16) & 0xff;
						int green2 = (pixel2 >> 8 ) & 0xff;
						int blue2  = (pixel2      ) & 0xff;
						if (((red<red2+45)&&(red>red2-45))
							&&
						((green<green2+45)&&(green>green2-45))
							&&
						((blue<blue2+45)&&(blue>blue2-45))){
							k++;
						}
					}
					if (k>(int)num/2){
						alpha=255;
					}else{
						alpha=0;
					}
					pixel=(alpha<<24)+(red<<16)+(green<<8)+blue;
					bimg.setRGB(x,y,pixel);
					k=0;

				}
			}
			Result.addElement(bimg);	
	}

	return Result;
	}


}
