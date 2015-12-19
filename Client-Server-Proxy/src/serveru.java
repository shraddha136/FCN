
//package FCN_PROJ;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author Shraddha Atrawalkar
 */
public class serveru implements Runnable {

	private static DatagramSocket socket = null;
	private static DatagramPacket request;
	private String returnMessage;
	public static long reqt;
	public static String userid;
	public static String password;
	public static int port;
	private String data = null;
	private String username = null;
	private String pass = null;
	//private int hop = 0;

	public static void main(String[] args) {
		(new Thread(new serveru())).start();
	}

	@Override
	public void run() {
		try {

			socket = new DatagramSocket(port);
			handletime htobj = new handletime();

			byte[] buffer = new byte[256];
			request = new DatagramPacket(buffer, buffer.length);
			while (true) {
				// Reading the message from the client
				socket.receive(request);
				byte[] message = request.getData();
				String number = new String(message, 0, request.getLength());
				System.out.println(number);
				//String n = number.substring(0, number.indexOf("hop:"));
				System.out.println("Message received from udp client is " + number);
				//hop = hops.extracthop(number);
				//hop++;
				// returnMessage = htobj.reqdtime(number);
				if (number.contains("modify") && number.contains("userid") && number.contains("password")) {
					// client wants to modify value at server
					extractinfo(number);
					if (verifyuser()) {
						// send the data to the server
						String tomodify = data;
						returnMessage = htobj.reqdtime(tomodify);
					} else {
						returnMessage = "Client  not authorized";
					}
					// success message to client

					
					String successmsg = returnMessage ;
					byte[] send = successmsg.getBytes();
					DatagramPacket dp = new DatagramPacket(send, send.length, request.getAddress(), request.getPort());
					socket.send(dp);
					//String s = successmsg.substring(0, successmsg.indexOf("hop:"));
					System.out.println("Message sent to the udp client : " + successmsg);

				} else {
					returnMessage = htobj.reqdtime(number);

					String successmsg = returnMessage ;
					byte[] send = successmsg.getBytes();
					DatagramPacket dp = new DatagramPacket(send, send.length, request.getAddress(), request.getPort());
					socket.send(dp);
					//String s = successmsg.substring(0, successmsg.indexOf("hop:"));
					System.out.println("Message sent to udp client is " + successmsg);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				socket.close();
			} catch (Exception e) {
			}
		}

	}

	private void extractinfo(String number) throws IOException {
		try {
			System.out.println(number.length());

			int a = number.lastIndexOf("modify") + 6;
			int b = number.indexOf(":");
			data = number.substring(a, b);
			System.out.println(number.substring(a, b));

			String news = number.substring(b + 1);
			int c = news.indexOf("userid") + 6;
			int d = news.indexOf(":");
			username = news.substring(c, d);
			if (username.equals("null")) {
				username = "0";
			}
			System.out.println(username);

			String newss = news.substring(d + 1);
            int e = newss.indexOf("password") + 8;
            pass = newss.substring(e, newss.length());
            if (pass.equals("null")) {
                pass = "0";
            }
            System.out.println(pass);
		} catch (Exception e) {
			// Client needs to send message in a proper format
			String rejectmsg = "usage 'modify(data_you_want_to_set):userid(your_username):password(your_password)'"
					+ "\n";
			byte[] send = rejectmsg.getBytes();
			DatagramPacket dp = new DatagramPacket(send, send.length, request.getAddress(), request.getPort());
			socket.send(dp);

			System.out.println("Message sent to the client : " + rejectmsg);
		}

	}

	private boolean verifyuser() {

		if (userid == null) {
			userid = "0";
		}
		if (password == null) {
			password = "0";
		}
		System.out.println(userid.trim() + " and " + password.trim());
		System.out.println(username + " and " + pass);
		return username.equals(userid.trim()) && pass.equals(password.trim());
	}

}
