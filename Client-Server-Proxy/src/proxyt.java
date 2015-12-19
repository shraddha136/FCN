
//package FCN_PROJ;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

/**
 *
 * @author Shraddha Atrawalkar
 */
public class proxyt implements Runnable {

	private static Socket proxysocket;
	private static Socket proxyttoservert;
	private static ServerSocket proxySocket = null;
	public static int tpport;
	public static String host;
	public static int tptosp;
	private String data = null;
	public static String tcpserveruser;
	public static String tcpserverpswd;
	//private int hop = 0;

	public proxyt(int tpport) {
		proxyt.tpport = tpport;
	}

	@Override
	public void run() {
		try {

			InetAddress address = InetAddress.getByName(host);

			try {
				proxySocket = new ServerSocket(tpport);
			} catch (SocketException se) {
				System.out.println("cannot open tcp at " + tpport);
			}

			while (true) {
				// Reading the message from the client
				proxysocket = proxySocket.accept();
				InputStream is = proxysocket.getInputStream();
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				String number = br.readLine();
				//number.replaceAll("\\s","");
				//hop = hops.extracthop(number);
				//String n = number.substring(0, number.indexOf("hop:"));
				System.out.println("Message received from client is " + number);

				calcrtt rttobj = new calcrtt();
				// Send the message to the server to retrieve value
				proxyttoservert = new Socket(address, tptosp);
				OutputStream os3 = proxyttoservert.getOutputStream();
				OutputStreamWriter osw3 = new OutputStreamWriter(os3);
				BufferedWriter bw3 = new BufferedWriter(osw3);

				//hop++;
				String sendMessage = number + "\n";
				rttobj.setRtt_start(System.currentTimeMillis());
				bw3.write(sendMessage);
				bw3.flush();
				//String s = sendMessage.substring(0, sendMessage.indexOf("hop:"));
				System.out.println("Message sent to the server : " + sendMessage);

				// from server
				InputStream iss = proxyttoservert.getInputStream();
				rttobj.setRtt_end(System.currentTimeMillis());
				InputStreamReader issr = new InputStreamReader(iss);
				BufferedReader brs = new BufferedReader(issr);
				number = brs.readLine();
				//hop = hops.extracthop(number);
				//number = number.substring(0, number.indexOf("hop:"));
				System.out.println("Message received from server is " + number);
				proxyttoservert.close();

				System.out.println("The round trip time (Proxy-Server-Proxy) is: " + rttobj.calrtt() + "ms");

				// success message to client
				OutputStream ops = proxysocket.getOutputStream();
				OutputStreamWriter opsw = new OutputStreamWriter(ops);
				BufferedWriter bpw = new BufferedWriter(opsw);
				
				String successmsg = number + "\n";
				bpw.write(successmsg);
				bpw.flush();
				//String ss = successmsg.substring(0, successmsg.indexOf("hop:"));
				System.out.println("Message sent to the client : " + sendMessage);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}

	}

}
