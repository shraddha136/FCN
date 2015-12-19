
//package FCN_PROJ;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Shraddha Atrawalkar
 */
public class proxyu implements Runnable {

	private static DatagramSocket proxysoc = null;
	private static DatagramPacket proxypack = null;
	private static DatagramPacket packet = null;
	public static int upport;
	public static String host;
	public static int uptosp;
	private String data = null;
	private String username = null;
	private String pass = null;
	public static String udpserveruser;
	public static String udpserverpswd;
	//private int hop = 0;

	public proxyu(int upport) {
		proxyu.upport = upport;
	}

	@Override
	public void run() {

		InetAddress address = null;
		try {
			address = InetAddress.getByName(host);
		} catch (UnknownHostException ex) {
			Logger.getLogger(proxyu.class.getName()).log(Level.SEVERE, null, ex);
		}

		byte[] buffer = new byte[256];
		proxypack = new DatagramPacket(buffer, buffer.length);
		try {
			proxysoc = new DatagramSocket(upport);

		} catch (SocketException ex) {
			System.out.println("cannot open udp at " + upport);
		}
		while (true) {
			try {
				// Reading the message from the client
				// from client to proxy
				proxysoc.receive(proxypack);
			} catch (IOException ex) {
				Logger.getLogger(proxyu.class.getName()).log(Level.SEVERE, null, ex);
			}
			byte[] message = proxypack.getData();
			String number = new String(message, 0, proxypack.getLength());
			//hop = hops.extracthop(number);
			//String n = number.substring(0, number.indexOf("hop:"));
			System.out.println("Message received from udp client is " + number);

			// Send the message to the server
			//hop++;
			String sendMessage = number;
			packet = new DatagramPacket(sendMessage.getBytes(), sendMessage.getBytes().length, address, uptosp);
			try {
				proxysoc.send(packet);
				//String s = number.substring(0, number.indexOf("hop:"));
				System.out.println("Message sent to the udp server : " + sendMessage);
			} catch (IOException ex) {
				Logger.getLogger(proxyu.class.getName()).log(Level.SEVERE, null, ex);
			}

			// from server
			byte[] resp = new byte[256];
			DatagramPacket response = new DatagramPacket(resp, resp.length);
			try {
				proxysoc.receive(response);
			} catch (IOException ex) {
				Logger.getLogger(proxyu.class.getName()).log(Level.SEVERE, null, ex);
			}
			byte[] messages = response.getData();
			number = new String(messages, 0, response.getLength());
			//hop = hops.extracthop(number);
			//number = number.substring(0, number.indexOf("hop:"));
			System.out.println("Message received from the udp server : " + number);

			// message to client

			String successmsg = number ;
			System.out.println("Message sent to the udp client : " + successmsg);
			// byte[] buffer = new byte[256];
			packet = new DatagramPacket(successmsg.getBytes(), successmsg.getBytes().length, proxypack.getAddress(),
					proxypack.getPort());
			try {
				proxysoc.send(packet);
			} catch (IOException ex) {
				Logger.getLogger(proxyu.class.getName()).log(Level.SEVERE, null, ex);
			}
			//String ss = successmsg.substring(0, successmsg.indexOf("hop:"));
			System.out.println("Message sent to the client : " + successmsg);
		}
	}

}
