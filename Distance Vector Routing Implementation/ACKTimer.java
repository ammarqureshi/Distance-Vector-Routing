package cs.tcd.ie.copy2;


import java.util.Timer;	
import java.util.TimerTask;

public class ACKTimer {
	
	Timer t;
	Router c;
	
	public ACKTimer(int msec, Router c) {
		
		this.c = c;
		t = new Timer();
		t.scheduleAtFixedRate(new ACKTaskTimer(c), msec, msec);	
	}

	

}