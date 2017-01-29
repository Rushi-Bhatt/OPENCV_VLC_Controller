import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.*;
import java.awt.event.*;

import javax.imageio.ImageIO;
import javax.swing.*;

import org.opencv.core.*;
import org.opencv.highgui.*;


public class vlc implements ItemListener,MouseListener
{	
	boolean flag=false;
	BufferedImage image_new;
	int old_x=0,old_y=0,new_x=0,new_y=0,X=0,Y=0;
    float s_count=0,count=0;
    JLabel label_1;
    
    int RED=0;      		    		
    int GREEN=0;
	int BLUE=0;
	 
	@SuppressWarnings("unused")
	vlc()throws InterruptedException, IOException
	{
		JFrame frm=new JFrame();
		
		label_1 = new JLabel("",null,JLabel.CENTER);
        frm.add(label_1,BorderLayout.CENTER);
        JToggleButton jtb=new JToggleButton("Start Mot Det");
        frm.add(jtb,BorderLayout.SOUTH);
        
        frm.setLayout(new FlowLayout());
        label_1.addMouseListener(this);
        
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
	    frm.setSize(665, 560);
	    frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frm.setVisible(true);
	    //frm.setLocation(350, 100);
	    jtb.addItemListener(this);
		frm.setLocationRelativeTo(null);
       
		Robot r = null;
		try 
		{
			r = new Robot();
		}
		catch (AWTException e1) {}
		int no_change=0;
    	do
	    {
		    camera.grab();
		    camera.retrieve(frame); 
		    camera.read(frame);
		 
		    Highgui.imwrite("camera.png", frame);
		    
		    File file = new File("camera.png");  
		    
		    image_new = ImageIO.read(file);
		       
		    flipImg();
		    
		    if(flag==false && no_change>3)
		    {
			    int[] pixels = image_new.getRGB(0,0, image_new.getWidth(), image_new.getHeight(), null, 0, image_new.getWidth());
				int x=0,y=0,count=1;
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
		        	
		       	   // if(pixels[i]==pixels[I])
		       	    if(Math.abs(RED-red)<25 && Math.abs(GREEN-green)<25 && Math.abs(BLUE-blue)<25)
		       	    {
		       	    	
		       	    	count++;
		       	    	old_x+=x;
		       	    	old_y+=y;
		       	    }
		       	   
		       
		       	 x++;
				}   
				old_x/=count;
				old_y/=count;
				
				if(count>1500)
				{
					flag=true;
					no_change=0;
				}
				
		    }
		    
		    if(flag==true)
		    { 	
		    	int[] pixels = image_new.getRGB(0,0, image_new.getWidth(), image_new.getHeight(), null, 0, image_new.getWidth());
		    	int x=0,y=0, k=1;
				new_x=0;
				new_y=0;
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
		        	
		       	   // if(pixels[i]==pixels[I])
		       	    if(Math.abs(RED-red)<25 && Math.abs(GREEN-green)<25 && Math.abs(BLUE-blue)<25)
		       	    {
		       	    	image_new.setRGB(x, y, -1); 
		       	    	k++;
		       	    	new_x+=x;
		       	    	new_y+=y;    
		       	    }
		       	    else
		       	    {
		       	    	image_new.setRGB(x, y, 0);
		       	    }
		       
		       	 x++;
				}   
				if(k>1500)
				{
					new_x/=k;
					new_y/=k;
				}
				
				else
				{
					
					new_x=old_x;
					new_y=old_y;
				}
				System.out.println(new_x-old_x+" "+" "+k+" "+no_change);
				/*if((Math.abs(new_x-old_x)>5 || Math.abs(new_y-old_y)>5)&& k>1500)
				{			
					java.awt.Point p;
					p=frm.getLocation();
				//	label_1.setLocation(p.x-(new_x-old_x)*7/2, p.y+(new_y-old_y)*2);
				//	p=label_1.getLocation();
					frm.setLocation((int) (p.getX()+(new_x-old_x)*2), (int) (p.getY()+(new_y-old_y)*2));
					no_change=0;
				}*/
				
				if((new_x-old_x)>10)
				{			
					r.keyPress(KeyEvent.VK_CONTROL);		
					r.keyPress(KeyEvent.VK_RIGHT);
                    r.keyPress(KeyEvent.VK_RIGHT);
                    r.keyPress(KeyEvent.VK_CONTROL);		
					
					no_change=0;
				}
			    
				else if((new_x-old_x)<-20)
				{

					r.keyPress(KeyEvent.VK_CONTROL);		
					r.keyPress(KeyEvent.VK_LEFT);
                    r.keyPress(KeyEvent.VK_LEFT);
                    r.keyPress(KeyEvent.VK_CONTROL);		
					no_change=0;
					
				}
				else if((new_y-old_y)>10)
				{			
					r.keyPress(KeyEvent.VK_CONTROL);		
					r.keyPress(KeyEvent.VK_UP);
					r.keyPress(KeyEvent.VK_UP);
					r.keyPress(KeyEvent.VK_CONTROL);		
					
					no_change=0;
			    
				}
				else if((new_y-old_y)<-20)
				{

					r.keyPress(KeyEvent.VK_CONTROL);		
					r.keyPress(KeyEvent.VK_DOWN);
                    r.keyPress(KeyEvent.VK_DOWN);
                    r.keyPress(KeyEvent.VK_CONTROL);		
					no_change=0;
					
				}
				else
				{
					no_change++;
					if(no_change>3)
					flag=false;
				}
				
				old_x=new_x;
				old_y=new_y;
			}
		    
		    label_1.setIcon(new ImageIcon(image_new));	 
		   
	    }while(true);
	}
	
	public void itemStateChanged(ItemEvent ie) 
	{
		JToggleButton jtb2;
		jtb2=(JToggleButton) ie.getItem();
		if(jtb2.isSelected()==true)
		{
			flag=true;
		}
		
		else
		{
			flag=false;
		}
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
    	new vlc();
    }

	@Override
	public void mouseClicked(MouseEvent arg0) 
	{	
		X=arg0.getX();
		Y=arg0.getY();
		
		System.out.println(X+" "+Y+" ");
		
		
		RED=(image_new.getRGB(X, Y) & 0x00FF0000)>> 16;        		    		
	    GREEN=(image_new.getRGB(X, Y) & 0x0000FF00)>> 8;
		BLUE=(image_new.getRGB(X, Y) & 0x000000FF);
		
		
		int[] pixels = image_new.getRGB(0,0, image_new.getWidth(), image_new.getHeight(), null, 0, image_new.getWidth());
		int x=0,y=0,k=1;
		old_x=0;
		old_y=0;
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
        	
       	   // if(pixels[i]==pixels[I])
       	    if(Math.abs(RED-red)<25 && Math.abs(GREEN-green)<25 && Math.abs(BLUE-blue)<25)
       	    {
       	    	k++;
       	    	old_x+=x;
       	    	old_y+=y;    
       	    }
       	 x++;
		}   
		old_x/=k;
		old_y/=k;
	}
	
	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
