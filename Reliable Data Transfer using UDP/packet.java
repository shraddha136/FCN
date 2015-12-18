
import java.io.Serializable;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//package fcnprojfile;
/**
 * Class to define structure of the packet
 * 
 * @author shraddha
 */
public class packet implements Serializable, Comparable<packet> {

	public int sourceport;
	public int destport;
	public int seqno;
	public int ackno;
	public int controlfield;
	public static final int SYN = 1;
	public static final int ACK = 2;
	public static final int SYNACK = 3;
	public Long chksum;
	public boolean eof = false;
	public byte[] data = new byte[512];

	public packet(byte[] packet, int seqno) {
		this.data = packet;
		this.seqno = seqno;
	}

	@Override
	public int compareTo(packet t) {
		if (this.seqno < t.seqno) {
			return -1;
		} else if (this.seqno > t.seqno) {
			return 1;
		} else {
			return 0;
		}
	}

}
