package cs.tcd.ie.copy;


import java.util.Timer;	
import java.util.TimerTask;

public class ACKTimer2 {
	
	Timer t;
	Router c;
	
	public ACKTimer2(int msec, Router c) {
		
		this.c = c;
		t = new Timer();
		t.scheduleAtFixedRate(new ACKTaskTimer2(c), msec, msec);	
	}

	

}