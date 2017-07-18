package cs.tcd.ie.copy2;

import java.net.DatagramPacket;

public interface PacketContent {
	public String toString();
	public DatagramPacket toDatagramPacket();
}
