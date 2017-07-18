package cs.tcd.ie.copy;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import tcdIO.Terminal;

public  class Startpoint extends Node2 implements Runnable{

	static final String DST_ADDRESS = "localhost";
	Terminal terminal;
	int[] portNumbers;
	int root;
	int dst;
	String name;
	boolean msgRecv;
	public int portNumber;
	public Startpoint(String name, int port, int[] portNumbers,int connectedTo,int destRouter)
	{
		try 
		{
			this.portNumber = port;
			this.name = name;
			this.msgRecv = false;
			this.portNumbers = portNumbers;
			this.root = connectedTo;
			this.dst = destRouter;
			terminal = new Terminal(name);
			socket  = new DatagramSocket(port);
			listener.go();
		} 
		catch (java.lang.Exception e)
		{
			e.printStackTrace();
		}
	}

	public synchronized void onReceipt(DatagramPacket packet) 
	{

				StringContent content = new StringContent(packet); // receives packet from neighbour router, which contains neighbour's
				String c = content.toString();                     // routing table
				String[] m = c.split(",");
				terminal.println(m[2]+"says "+": "+ m[0]);
				terminal.println();
				msgRecv = true;
				this.notify();	


	}


	public synchronized void run()
	{
		try
		{
			byte[] data= null;
			DatagramPacket packet= null;

			//	int root = terminal.readInt("Enter Root router: ");
			//	int dst = terminal.readInt("Enter Destination router: ");
			InetSocketAddress dstAddress = new InetSocketAddress(DST_ADDRESS, portNumbers[this.root]);

			while(true )
			{

			//	while(!msgRecv){
				String message = terminal.readString("Enter message:  ");
				String msg = "msg";
				int hopCount=0;
				message = message + "," +this.dst + "," + this.name + ","+ msg ;
				data = message.getBytes();
				packet= new DatagramPacket(data, data.length, dstAddress);
				StringContent content = new StringContent(packet); // receives packet from neighbour router, which contains neighbour's
				String c = content.toString();                     // routing table
				//	String[] m = c.split(",");
				Thread.sleep(1000);
				//socket.send(packet);
				
				
				
				File fnew=new File("/tmp/rose.jpg");
				BufferedImage originalImage=ImageIO.read(fnew);
				ByteArrayOutputStream baos=new ByteArrayOutputStream();
				ImageIO.write(originalImage, "jpg", baos );
				byte[] imageInByte=baos.toByteArray();
				
				//ImageIcon(imageInByte);
				
//				InputStream in = new ByteArrayInputStream(imageInByte);
//				BufferedImage bImageFromConvert = ImageIO.read(in);
//
//				ImageIO.write(bImageFromConvert, "jpg", new File(
//						"c:/newRose.jpg"));
				
				JLabel jlabel = new JLabel();
				jlabel.setIcon(ImageIcon(imageInByte));
				
				
				
				//}
			//		this.wait();
//msgRecv = false;
				
				//Thread.sleep(3000);
				///msgRecv = false;

			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}
	
	
	
	private javax.swing.ImageIcon ImageIcon(byte[] imageInByte) {

return new ImageIcon(imageInByte);
		
	}

	
	
	

}
