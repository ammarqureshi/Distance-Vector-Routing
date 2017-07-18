package cs.tcd.ie.copy2;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.InputMismatchException;
import java.util.Scanner;

import tcdIO.Terminal;

public class Router extends Node implements Runnable{

	static final String DST_ADDRESS = "localhost";
	static final int INFINITE = 9999;
	static final int BLANK = -1;
	public boolean shortestPathFound = false;
	Terminal terminal;
	private boolean haveCosts;
	private boolean communicationEstablished;
	public int[][] costsArray;   //Pre made 2d array of costs between routers
	public int[] portNumbers;	//list of port numbers of each router, index corresponds to router number
	public int[] routingTable;	// routers routing table
	public int[] nextHopTable;
	public int routerNumber;	
	private int[] myNeighbours;
	private boolean[] established;
	private int phonePort;
	int[] neighbourRoutingTable;
	public Terminal destTerminal;
	public Router(int routerNumber, int port, int[][] costsArray, int[] portNumbers, int[] neighbours, 
			Startpoint smartPhone)
	{
		try 
		{

			this.routerNumber = routerNumber;
			this.destTerminal = smartPhone.terminal;
			this.phonePort = smartPhone.portNumber;
			this.haveCosts=false;
			communicationEstablished = false;
			this.myNeighbours = neighbours;
			this.established = new boolean[portNumbers.length];		//first establish comm. with neighbours to check if they are alive.
			terminal = new Terminal("Router " + routerNumber);
			this.costsArray = costsArray;
			this.portNumbers = portNumbers;
			routingTable = new int[costsArray.length];
			nextHopTable = new int[costsArray.length];



			socket  = new DatagramSocket(port);
			listener.go();
		} 
		catch (java.lang.Exception e)
		{
			e.printStackTrace();
		}
	}



	public Router(int routerNumber, int port, int[][] costsArray, int[] portNumbers, int[] neighbours)
	{
		try 
		{
			//this.phonePort = phonePort;
			this.haveCosts=false;
			communicationEstablished = false;
			this.myNeighbours = neighbours;
			this.established = new boolean[portNumbers.length];		//first establish comm. with neighbours to check if they are alive.
			this.routerNumber = routerNumber;
			terminal = new Terminal("Router " + routerNumber);
			this.costsArray = costsArray;
			this.portNumbers = portNumbers;
			routingTable = new int[costsArray.length];
			nextHopTable = new int[costsArray.length];



			socket  = new DatagramSocket(port);
			listener.go();
		} 
		catch (java.lang.Exception e)
		{
			e.printStackTrace();
		}
	}


	public void check() {

		for(int i = 0; i < routingTable.length; i++)
		{
			int cost = costsArray[routerNumber][i];

			if(cost != INFINITE && cost != 0) // checks whether the router is a neighbour or not
			{			
				//portNumbers[i]
				updateRoutingTable2(this.neighbourRoutingTable, i);
			}

		}

		//printTableToConsole();
		//terminal.println("ROUTING TABLES UPDATED");
		printRoutingTable();
		//System.out.println("\n\n");

	}





	public synchronized void onReceipt(DatagramPacket packet)
	{
		try 
		{
			StringContent content = new StringContent(packet); // receives packet from neighbour router, which contains neighbour's
			String c = content.toString();                     // routing table
			String[] m = c.split(",");
			if(m[0].contains("HELLO")){

				int routerNum = Integer.parseInt(m[1].trim());
				terminal.println("got a message! from " + routerNum + "to " + this.routerNumber );
				established[routerNum] = true;
				int neighbours=0;

				for(int i=0;i<established.length;i++){

					if(established[i] ==true){

						for(int j=0;j<myNeighbours.length;j++){

							if(i==myNeighbours[j]){
								neighbours++;
								terminal.println("tick message from " + i + " to " + this.routerNumber);
							}
						}
					}

					for(int k=0;k<established.length;k++){
						terminal.println(k + "" + established[k]);
					}
				}
				//has marked all fo its neighbours
				if(neighbours == myNeighbours.length - 1){
					this.communicationEstablished = true;
				}

				this.notify();
			}

			else if(c.contains("msg"))
			{

				Thread.sleep(2000);
				int dst = Integer.parseInt(m[1].trim());
				int next = nextHopTable[dst];

				if(next != routerNumber)
				{
					terminal.println("sending to "+next+ " -->" +m[0]);
					InetSocketAddress dstAddress = new InetSocketAddress(DST_ADDRESS, portNumbers[next]);
					packet.setSocketAddress(dstAddress);
					socket.send(packet);
				}

			}


			else if(!shortestPathFound)findShortestPath(c);
			//			else{
			//				findShortestPath(c);
			//			}

		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

	public synchronized void run()
	{
		while(!this.communicationEstablished){
			try {
				establishCommunication();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}	//say hi to my neighbours


		}

		getCosts();


		if(this.communicationEstablished && haveCosts){
			terminal.println("\n\nCommunication Established b/w neighbours");
			//	terminal.println("in here");
			printRoutingTable();

			int x = 0;
			while(x < this.routingTable.length)//loop a certain number of times to resend updated routing tables
			{			// could be a loop that terminates when the subsequent routing tables does not change anymore.
				// the very last output printed will be the shortest path from that node to another node
				sendRoutingTable();
				x++;
			}
			terminal.println();
			shortestPathFound = true;
		}

	}

	private void getCosts() {


		for(int i = 0; i < routingTable.length; i++)
		{
			routingTable[i] = costsArray[routerNumber][i]; //initialise routing table for the router
			if(routingTable[i] == INFINITE) nextHopTable[i] = BLANK;
			else nextHopTable[i] = i;
		}


		haveCosts = true;

	}

	private void establishCommunication() throws IOException, InterruptedException {
		byte[] data= null;
		DatagramPacket packet= null;


		//sending message to all my neighbours
		for(int i=0;i<this.myNeighbours.length;i++){
			String msg = "HELLO," + this.routerNumber;
			data = msg.getBytes();
			InetSocketAddress dstAddress = new InetSocketAddress(DST_ADDRESS, portNumbers[myNeighbours[i]]);
			packet = new DatagramPacket(data,data.length,dstAddress);
			terminal.println("sendng to " + myNeighbours[i] + " from " + this.routerNumber);
			socket.send(packet);
			this.wait();

		}

	}



	public void findShortestPath(String c)
	{
		this.neighbourRoutingTable = new int[routingTable.length];

		Scanner scanner = new Scanner(c);
		try{

			for(int i = 0; i < this.neighbourRoutingTable.length; i++) // convert the routing table string back to an array
			{
				if(scanner.hasNextInt())
				{
					int val = scanner.nextInt();
					this.neighbourRoutingTable[i] = val;
				}
			}
			int neighbourRouterNumber = scanner.nextInt();
			scanner.close();
			updateRoutingTable(neighbourRoutingTable, neighbourRouterNumber);
		}

		catch(InputMismatchException e){
			scanner.nextLine();		//clears the buffer
			System.err.println("ERROR");
		}

		printRoutingTable();
		this.notify();

	}

	public void sendRoutingTable()
	{
		try
		{
			byte[] data= null;
			DatagramPacket packet= null;

			for(int i = 0; i < routingTable.length; i++)
			{
				int cost = costsArray[routerNumber][i];
				if(cost != INFINITE && cost != 0) // checks whether the router is a neighbour or not
				{								  // send the routing table if it is a neighbour or not itself
					String s = arrayToString();	  // convert the routing table array into string for sending
					data = s.getBytes();		  // convert the string to bytes for sending	
					InetSocketAddress dstAddress = new InetSocketAddress(DST_ADDRESS, portNumbers[i]);
					packet= new DatagramPacket(data, data.length, dstAddress);
					socket.send(packet);
					this.wait();
				}
			}

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public String arrayToString()
	{
		String table = "";
		for(int i = 0; i < routingTable.length; i++)
		{
			table = table + routingTable[i] + " ";
		}
		table = table + routerNumber + " "; //add a routerNumber at the end to determine where this routing table is coming from
		return table;
	}

	public void updateRoutingTable(int[] neighbourRoutingTable, int neighbourRouterNumber)//Bellman Ford's algorithm
	{
		int cost = costsArray[routerNumber][neighbourRouterNumber]; 
		for(int i = 0; i < routingTable.length; i++)
		{
			int min = Math.min(routingTable[i], neighbourRoutingTable[i] + cost);
			routingTable[i] = min;

			if(min == neighbourRoutingTable[i] + cost) nextHopTable[i] = neighbourRouterNumber;
		}
	}


	public void updateRoutingTable2(int[] neighbourRoutingTable, int neighbourRouterNumber)//Bellman Ford's algorithm
	{
		int cost = costsArray[routerNumber][neighbourRouterNumber]; 
		for(int i = 0; i < routingTable.length; i++)
		{
			int min = Math.min(routingTable[i], neighbourRoutingTable[i] + cost);
			routingTable[i] = min;

			if(min == neighbourRoutingTable[i] + cost) nextHopTable[i] = neighbourRouterNumber;
		}
	}




	public void printRoutingTable()
	{
		terminal.println();
		terminal.println("Destination" + "\tCost" + "\tNext");
		System.out.println(routingTable.length);

		for(int i = 0; i < routingTable.length; i++)
		{

			String cost = (routingTable[i] == INFINITE) ? "INF":"" + routingTable[i];
			//String next = (nextHopTable[i] == BLANK) ? "--":"" + nextHopTable[i];
			String next = "";
			String next2 = "";
			String x = "";
			if(nextHopTable[i]==BLANK){next = "--";}
			else	
			{
				next = ""+nextHopTable[i];
				if(nextHopTable[i]>3){
					next2 = "E" + next; 
				}
				else{
					next2 = "R" + next;
				}
			}
			
			if(i>3){
				x = "E" + i	;
			}
			else{
				x = "R" + i	;
			}
			terminal.println(x + "\t\t" + cost + "\t" + next2);
		}
	}

	public void forwardMessage(int destinationRouter, String message)
	{
		try
		{
			byte[] data= null;
			DatagramPacket packet= null;
			int next = nextHopTable[destinationRouter];
			if(next != routerNumber)
			{
				data = message.getBytes();
				InetSocketAddress dstAddress = new InetSocketAddress(DST_ADDRESS, portNumbers[next]);

				packet= new DatagramPacket(data, data.length, dstAddress);
				socket.send(packet);
			}

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}


	public void check2() {
		if(!this.communicationEstablished){
			System.err.println("ERROR:LINK BROKEN ");	
			System.exit(1);
		}	

		else{
			System.out.println("LINK STILL ESTABLISHED");
		}
	}

}
