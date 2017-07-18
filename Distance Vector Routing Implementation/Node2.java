package cs.tcd.ie.copy2;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.CountDownLatch;

import tcdIO.Terminal;

public abstract class Node2 {
	public boolean msgRecv;
	static final int PACKETSIZE = 65536;
	Terminal terminal;
	Terminal destTerminal;
	DatagramSocket socket;
	Listener listener;
	CountDownLatch latch;
	
	Node2() {
		latch= new CountDownLatch(1);
		listener= new Listener();
		listener.setDaemon(true);
		listener.start();
		terminal = new Terminal("Response");

	}
	
	
	public abstract void onReceipt(DatagramPacket packet);
	
	/**
	 *
	 * Listener thread
	 * 
	 * Listens for incoming packets on a datagram socket and informs registered receivers about incoming packets.
	 */
	class Listener extends Thread {
		
		/*
		 *  Telling the listener that the socket has been initialized 
		 */
		public void go() {
			latch.countDown();
		}
		
		/*
		 * Listen for incoming packets and inform receivers
		 */
		
		public void run() {
			try {
				latch.await();
				// Endless loop: attempt to receive packet, notify receivers, etc
				while(true) {
					
					
					DatagramPacket packet = new DatagramPacket(new byte[PACKETSIZE], PACKETSIZE);
					socket.receive(packet);
					
					StringContent content = new StringContent(packet); // receives packet from neighbour router, which contains neighbour's
					String c = content.toString();                     // routing table
					if(c.contains("msg")){
					String[] m = c.split(",");
					
					terminal.println(m[2]+" says "+": "+ m[0]);
					terminal.println();
					}
					
					
				}
			} catch (Exception e) {if (!(e instanceof SocketException)) e.printStackTrace();}
				
		}
	}
}
