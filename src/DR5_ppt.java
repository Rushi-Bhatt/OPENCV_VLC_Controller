import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.*;
import java.awt.event.*;

import javax.imageio.ImageIO;
import javax.swing.*;

import org.opencv.core.*;
import org.opencv.highgui.*;

public class DR5_ppt implements ItemListener{
	
	boolean flag=false,flip;
	BufferedImage image_old,image_new;
	int old_x=0,old_y=0,new_x=0,new_y=0,mx,my;
    float s_count=1,count=1;
    Robot r = null;
    JFrame frm;
    JLabel label_1;
    
	DR5_ppt()throws InterruptedException, IOException
	{
		frm=new JFrame();
    	label_1 = new JLabel("",null,JLabel.CENTER);
        frm.add(label_1,BorderLayout.CENTER);
        JToggleButton jtb=new JToggleButton("Start Mot Det");
        frm.add(jtb,BorderLayout.SOUTH);
        
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

    VideoCapture camera = new VideoCapture(0);
    Thread.sleep(500);
    
    if(!camera.isOpened()){
        System.out.println("Camera Error");
    }
    else{
        System.out.println("Camera OK?");
    }

   
    Mat frame = new Mat();
    frm.setSize(800, 620);
    frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frm.setVisible(true);
    
    
  //  frm.setLocation(350, 100);
    jtb.addItemListener(this);
    
   
		
	
       
    	do
	    {
		    camera.grab();
		    camera.retrieve(frame);
		    camera.read(frame);
		 
		    Highgui.imwrite("camera.png", frame);
		    
		    File file = new File("camera.png");  
		    
		    image_new = ImageIO.read(file);
		    
		    flip=false;
		    flipImg();
		    flip=true;
		    
		    label_1.setIcon(new ImageIcon(image_new));	
		    
		    if(flag)
		    {
				int[] pixels = image_new.getRGB(0,0, image_new.getWidth(), image_new.getHeight(), null, 0, image_new.getWidth());
				int x=0,y=0;
				count=1;//,count2=0;
				int k=1;//so no divide by 0 error
				new_x=0;new_y=0;
				for(int i=0;i<pixels.length;i++)
				{			
					if(x==image_new.getWidth())
		        	{
		        		y++;
		        		x=0;
		        	}
		        	
		        	if(y==image_new.getHeight())
		        	{
		        		break;
		        	}
					int red=(pixels[i] & 0x00FF0000)>> 16;        		    		
		            int green=(pixels[i] & 0x0000FF00)>> 8;
		       	    int blue=(pixels[i] & 0x000000FF);
				
		       	    
		       	    if(red>green+55 && red>blue+55)
		       	    {
		       	    	//image_new.setRGB(x, y, -1);  
		       	    	new_x+=x;
		       	    	new_y+=y;
		       	    	k++;
		       	    	count++;
		       	    }
		       	 x++;
				}   
			
				if(count>500)///////newly added
				{
					new_x/=k;
					new_y/=k;
				}
				else
				{
					new_x=old_x;
					new_y=old_y;
				}
		
				System.out.println(new_x-old_x+" "+new_x+" "+old_x);
				if(((new_x-old_x)>175))
				{
					r.keyPress(KeyEvent.VK_DOWN);
					r.keyRelease(KeyEvent.VK_DOWN);
				}
				
				old_x=new_x;
				old_y=new_y;
		    }
		    
	    }while(true);
	}
	
	public void itemStateChanged(ItemEvent ie) 
	{
		try 
		{
			r = new Robot();
		}
		catch (AWTException e1) {}
		
		JToggleButton jtb2;
		jtb2=(JToggleButton) ie.getItem();
		if(jtb2.isSelected()==true)
		{
			flag=true;
			if(flip==false)
				try {
					flipImg();
					flip=true;
				} catch (IOException e) {}
			
			int[] pixels = image_new.getRGB(0,0, image_new.getWidth(), image_new.getHeight(), null, 0, image_new.getWidth());
			int x=0,y=0;
			
			int k=1;
			for(int i=0;i<pixels.length;i++)
			{			
				if(x==image_new.getWidth())
	        	{
	        		y++;
	        		x=0;
	        	}
	        	
	        	if(y==image_new.getHeight())
	        	{
	        		break;
	        	}
				
	        	int red=(pixels[i] & 0x00FF0000)>> 16;        		    		
	            int green=(pixels[i] & 0x0000FF00)>> 8;
	       	    int blue=(pixels[i] & 0x000000FF);
			
	       	    if(red>green+55 && red>blue+55)
	       	    {
	       	    	old_x+=x;
	       	    	old_y+=y;
	       	    	k++;
	       	    	s_count++;
	       	    }
	       	 x++;
			}   
		
			if(s_count>500)/////////newly added
			{
				old_x/=k;
				old_y/=k;
			}
			else
			{
				old_x=0;
				old_y=0;
			}
		}
		
		else
		{
			flag=false;
		}
		System.out.println("*"+old_x+" "+old_y);
	
		r.mouseMove((int) (60+old_x+frm.getLocation().getX()),(int) (60+old_y+frm.getLocation().getY()));
		mx=old_x+60;
		my=old_y+60;
	}
	
	void flipImg() throws IOException
	{
		File file_temp = new File("camera.png");  
		BufferedImage image_temp;
		image_temp = ImageIO.read(file_temp);
		
		int[] pixels = image_temp.getRGB(0,0, image_temp.getWidth(), image_temp.getHeight(), null, 0, image_temp.getWidth());
		int x=0,y=0;
		
		for(int i=0;i<pixels.length;i++)
		{			
			if(x==image_temp.getWidth())
        	{
        		y++;
        		x=0;
        	}
        	
        	if(y==image_new.getHeight())
        	{
        		break;
        	}
			
           	image_new.setRGB(image_new.getWidth()-x-1, y, pixels[i]);  
           	x++;
		}   		
	}
	
	public static void main (String args[]) throws InterruptedException, IOException
	{
    	new DR5_ppt();
    }	
}
