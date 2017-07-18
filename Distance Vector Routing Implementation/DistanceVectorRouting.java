package cs.tcd.ie.copy2;

public class DistanceVectorRouting {
	static final int INF = 9999;
	static final int phone1=4;
	static final int phone2=5;
	static final int phone3=6;
	static ACKTimer at;
	public static Router router0,router1,router2,router3;
	public static Startpoint sp1,sp2,sp3;
	public static void main(String[] args) {


		int routingTables[][] = new int[][]{
			//	0	1	2	3	4	5	
				{0, 5, 10, 1,1,INF,INF},					//router 0
				{5, 0, 2,  1,INF,INF,INF},					//router 1
				{10,2, 0,  INF,INF,INF,INF},				//router 2
				{1, 1, INF,0,INF,1,1},						//router 3
				{1,INF,INF,INF,0,INF,INF},					//E4 connected to rout 0
				{INF,INF,INF,1,INF,0,INF},					//E5 connected to router 3
				{INF,INF,INF,1,INF,INF,0}					//E6 connected to router 3
		};

		int neigbourR0[] = {1,2,3};
		int neighbourR1[] = {0,2,3};
		int neighbourR2[] = {0,1};
		int neighbourR3[] = {0,1};

		int SP1port = 90;
		int SP2port = 140;
		int SP3port = 145;
		
		
		int portNumbers[] = {5555, 4444, 50001, 50002,SP1port,SP2port,SP3port};
		
		int sp1Connections[] = {phone2,phone3};			
		int sp2Connections[]=	{phone1,phone3};
		int sp3Connections[] = {phone1,phone2};
		sp1 = new Startpoint("SmartPhone1/E4", SP1port, portNumbers,0,sp1Connections);	//connected to router 0 
		sp2 = new Startpoint("SmartPhone2/E5",SP2port,portNumbers,3,sp2Connections);	// to router 3
		sp3 = new Startpoint("SmartPhone3/E6",SP3port,portNumbers,3,sp3Connections);	//to router 3
		Thread StartPoint1 = new Thread(sp1);
		Thread StartPoint2 = new Thread(sp2);
		Thread StartPoint3 = new Thread(sp3);
		
		router0 = new Router(0, portNumbers[0],routingTables, portNumbers,neigbourR0);
		router1 = new Router(1, portNumbers[1],routingTables, portNumbers,neighbourR1 );
		router2 = new Router(2, portNumbers[2],routingTables, portNumbers,neighbourR2);
		router3 = new Router(3, portNumbers[3],routingTables, portNumbers,neighbourR3);

		Thread Router0 = new Thread(router0);
		Thread Router1 = new Thread(router1);
		Thread Router2 = new Thread(router2);
		Thread Router3 = new Thread(router3);

		StartPoint1.start();
		StartPoint2.start();
		StartPoint3.start();
		Router0.start();
		Router1.start();
		Router2.start();
		Router3.start();


		
	}

}
