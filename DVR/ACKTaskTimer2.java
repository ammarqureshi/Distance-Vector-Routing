package cs.tcd.ie.copy;


import java.io.IOException;
import java.util.TimerTask;

public class ACKTaskTimer2 extends TimerTask {
	
	Router c;
	
	public ACKTaskTimer2(Router c) {
		super();
		this.c = c;
	}
	
	public void run() {
		
		((Router) c).check2(); 
	}
	
	

}
