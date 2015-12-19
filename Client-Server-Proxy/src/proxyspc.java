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
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class proxyspc implements Runnable {

	public static String host;
	public int ptport;
	public int puport;
	private DatagramSocket socket = null;
	private static Socket sockettcp = null;
	private static DatagramPacket request;
	public static int tptosp;
	public static int uptosp;
	private String returnMessage;
	private String msg1;
	//private int hop = 0;

	public proxyspc(int ptport, int puport) {
		this.ptport = ptport;
		this.puport = puport;
	}

	@Override
	public void run() {
		if (tsapp.proxyclienttcp && !tsapp.proxyclientudp && tsapp.both == 1) {
			try {

				socket = new DatagramSocket(puport);
				byte[] buffer = new byte[256];
				request = new DatagramPacket(buffer, buffer.length);
				while (true) {
					// Reading the message from the client
					socket.receive(request);
					byte[] message = request.getData();
					String number = new String(message, 0, request.getLength());
					//String n = number.substring(0, number.indexOf("hop:")).trim();
					System.out.println("Message received from udp client is " + number);
					//////////////////////////////////////////////////////////////////

					Socket ssocket;
					InetAddress address = InetAddress.getByName(host);

					ssocket = new Socket(address, tptosp);
					calcrtt rttobj = new calcrtt();

					// Send the message to the server
					//hop = hops.extracthop(number);
					//hop++;
					OutputStream os = ssocket.getOutputStream();
					OutputStreamWriter osw = new OutputStreamWriter(os);
					BufferedWriter bw = new BufferedWriter(osw);
					String sendMessage = number + "\n";
					rttobj.setRtt_start(System.currentTimeMillis());

					bw.write(sendMessage);
					bw.flush();
					//sendMessage = sendMessage.substring(0, sendMessage.indexOf("hop:")).trim();
					System.out.println("Message sent to the server : " + sendMessage);

					// Get the return message from the server
					InputStream is = ssocket.getInputStream();
					rttobj.setRtt_end(System.currentTimeMillis());
					InputStreamReader isr = new InputStreamReader(is);
					BufferedReader br = new BufferedReader(isr);
					msg1 = br.readLine();
					//String successmsg = msg1.substring(0, msg1.indexOf("hop:")).trim();
					System.out.println("Message received from the server : " + msg1);
					ssocket.close();
					System.out.println("The round trip time (Proxy-Server-Proxy) is: " + rttobj.calrtt() + "ms");

					//////////////////////////////////////////////////////////////////////////
					//hop = hops.extracthop(msg1);
					//successmsg = successmsg + "hop:" + hop;
					byte[] send = msg1.getBytes();
					DatagramPacket dp = new DatagramPacket(send, send.length, request.getAddress(), request.getPort());
					socket.send(dp);
					System.out.println("Message sent to the udp client : " + msg1);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					socket.close();
				} catch (Exception e) {
				}
			}
		} else if (!tsapp.proxyclienttcp && tsapp.proxyclientudp && tsapp.both == 1) {
			ServerSocket serverSocket = null;
			try {
				serverSocket = new ServerSocket(ptport);
			} catch (IOException e) {
				e.printStackTrace();
			}
			while (true) {
				// Reading the message from the client
				try {
					sockettcp = serverSocket.accept();
				} catch (IOException e) {
					e.printStackTrace();
				}
				InputStream is = null;
				try {
					is = sockettcp.getInputStream();
				} catch (IOException e) {
					e.printStackTrace();
				}
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				String number = null;
				try {
					number = br.readLine();
				} catch (IOException e) {
					e.printStackTrace();
				}
				//String n = number.substring(0, number.indexOf("hop:"));
				System.out.println("Message received from tcp client is " + number);
				///////////////////////////////////////////////////////
				{
					DatagramPacket response = null;

					calcrtt rttobj = new calcrtt();

					try {
						socket = new DatagramSocket();
					} catch (SocketException e) {
						e.printStackTrace();
					}
					InetAddress address = null;
					try {
						address = InetAddress.getByName(host);
					} catch (UnknownHostException e) {
						e.printStackTrace();
					}

					//hop = hops.extracthop(number);
					//hop++;
					//String sendMessage = n + "hop:" + hop;
					//System.out.println(sendMessage);
					// byte[] buffer = new byte[256];
					DatagramPacket dp = new DatagramPacket(number.getBytes(), number.getBytes().length,
							address, uptosp);
					rttobj.setRtt_start(System.currentTimeMillis());
					try {
						socket.send(dp);
						//sendMessage = sendMessage.substring(0, sendMessage.indexOf("hop:"));
						System.out.println("Message sent to the udp server : " + number);
					} catch (IOException e) {
						e.printStackTrace();
					}

					// Get the message from the server
					byte[] resp = new byte[256];
					response = new DatagramPacket(resp, resp.length);
					try {
						socket.receive(response);
					} catch (IOException e) {
						e.printStackTrace();
					}
					rttobj.setRtt_end(System.currentTimeMillis());
					byte[] messages = response.getData();
					returnMessage = new String(messages, 0, response.getLength());
					//String successmsg = returnMessage.substring(0, returnMessage.indexOf("hop:"));
					System.out.println("Message received from the udp server : " + returnMessage);

					System.out.println("The round trip time (Proxy-Server-Proxy) is: " + rttobj.calrtt() + "ms");

				} ///////////////////////////////////////////////////////

				// success message to client
				OutputStream ops = null;
				try {
					ops = sockettcp.getOutputStream();
				} catch (IOException e) {
					e.printStackTrace();
				}
				OutputStreamWriter opsw = new OutputStreamWriter(ops);
				BufferedWriter bpw = new BufferedWriter(opsw);
				String successmsg = returnMessage + "\n";
				try {
					bpw.write(successmsg);
					System.out.println("Message sent to the client : " + successmsg);

				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					bpw.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
	}
}
