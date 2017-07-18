package cs.tcd.ie.copy2;

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
	static final int E5 = 5;
	static final int E4 = 4;
	Terminal terminal;
	int[] portNumbers;
	int connectedTo;
	int	 phoneConnections[];
	String name;
	boolean msgRecv;
	public int portNumber;
	public Startpoint(String name, int port, int[] portNumbers,int connectedTo,int[] sp1Connections)
	{
		try 
		{
			this.portNumber = port;
			this.name = name;
			this.msgRecv = false;
			this.portNumbers = portNumbers;
			this.connectedTo = connectedTo;
			this.phoneConnections = sp1Connections;
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
			//GET ADDRESS OF ROUTER THIS SMARTPHONE IS CONNECTED TO


			terminal.println("Connections of " + this.name +":");

			for(int i=0;i<phoneConnections.length;i++){
				terminal.println("E" +phoneConnections[i]);
			}
			int destinationPhone = terminal.readInt("Which phone to connect To?(enter digit only):");

			boolean correct = false;
			while(!correct){

			for(int i=0;i<phoneConnections.length;i++){
				
				if(destinationPhone == phoneConnections[i]){
					correct = true;
					break;
				}
			}
			if(!correct){
			destinationPhone = terminal.readInt("Invalid connection_RETRY:" );
			}
			}
		
			terminal.println("\n connected to  " + "E" + destinationPhone);
			InetSocketAddress dstAddress = new InetSocketAddress(DST_ADDRESS, portNumbers[this.connectedTo]);
			terminal.println("\n\n");
			//int destPhone;
			while(true )
			{
				String message = terminal.readString("Enter message:  ");
				String msg = "msg";
				int hopCount=0;

				System.out.println(destinationPhone);
				//message = message + "," +this.phoneConnections[0] + "," + this.name + ","+ msg ;
				message = message + "," + destinationPhone + "," + this.name + ","+ msg ;

				data = message.getBytes();
				packet= new DatagramPacket(data, data.length, dstAddress);
				//	StringContent content = new StringContent(packet); // receives packet from neighbour router, which contains neighbour's
				//String c = content.toString();                     // routing table
				//	String[] m = c.split(",");
				Thread.sleep(1000);
				socket.send(packet);




			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}


}
