package cs.tcd.ie.copy;


import java.io.IOException;
import java.util.TimerTask;

public class ACKTaskTimer extends TimerTask {
	
	Router c;
	
	public ACKTaskTimer(Router c) {
		super();
		this.c = c;
	}
	
	public void run() {
		
		((Router) c).check(); 
	}
	
	

}
