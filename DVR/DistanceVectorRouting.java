package cs.tcd.ie.copy;

public class DistanceVectorRouting {
	static final int INF = 9999;
	static ACKTimer at;
	static ACKTimer2 at2;
	public static Router router0,router1,router2,router3;
	public static Startpoint sp1,sp2,sp3;
	public static void main(String[] args) {


		int routingTables[][] = new int[][]{
				//		0	1	2	3	
				{0, 5, 10, 1},
				{5, 0, 2, 1},
				{10, 2, 0, INF},
				{1, 1, INF, 0},

		};

		int neigbourR0[] = {1,2,3};
		int neighbourR1[] = {0,2,3};
		int neighbourR2[] = {0,1};
		int neighbourR3[] = {0,1};


		int portNumbers[] = {5555, 4444, 50001, 50002,90,140};
		int SP1port = 90;
		int SP2port = 140;
		int SP3port = 145;
		
		sp1 = new Startpoint("SmartPhone1", SP1port, portNumbers,0,3);
		sp2 = new Startpoint("SmartPhone2",SP2port,portNumbers,3,0);
		sp3 = new Startpoint("SmartPhone3",SP3port,portNumbers,3,2);
		Thread StartPoint1 = new Thread(sp1);
		Thread StartPoint2 = new Thread(sp2);
		Thread StartPoint3 = new Thread(sp3);
//Thread StartPoint3 = new Thread(new Startpoint("SmartPhone3",SP3port,portNumbers,2,3));
//Thread StartPoint4 = new Thread(new Startpoint("SmartPhone4",200,portNumbers,3,2));

		
		router0 = new Router(0, portNumbers[0],routingTables, portNumbers,neigbourR0,sp2);
		router1 = new Router(1, portNumbers[1],routingTables, portNumbers,neighbourR1 );
		router2 = new Router(2, portNumbers[2],routingTables, portNumbers,neighbourR2,sp3 );
		router3 = new Router(3, portNumbers[3],routingTables, portNumbers,neighbourR3,sp1 );


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

//		int time = 35000;
//		at = new ACKTimer(time, router0);
//		at = new ACKTimer(time,router1);
//		at = new ACKTimer(time,router2);
//		at = new ACKTimer(time,router3);


//		int time2= 5000;
//		at2 = new ACKTimer2(time2, router0);
//		at2 = new ACKTimer2(time2,router1);
//		at2 = new ACKTimer2(time2,router2);
//		at2 = new ACKTimer2(time2,router3);

		
	}

}
