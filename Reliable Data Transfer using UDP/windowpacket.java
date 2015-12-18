/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//package fcnprojfile;

/**
 * Class to define structure of each packet sent over the network
 * 
 * @author shraddha
 */
public class windowpacket {

	public packet p;
	public boolean ack = false;
	public int dupack = 0;
	public long timestamp;

	public windowpacket(packet p) {
		this.p = p;

	}

}
