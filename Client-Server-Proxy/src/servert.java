
//package FCN_PROJ;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author Shraddha Atrawalkar
 */
public class servert implements Runnable {

	private static Socket socket;
	private String returnMessage;
	public static long reqt;
	public static String userid;
	public static String password;
	public static int port;
	private String data = null;
	private String username = null;
	private String pass = null;
	// private int hop = 0;

	public static void main(String[] args) {
		(new Thread(new servert())).start();

	}

	@Override
	public void run() {
		try {

			ServerSocket serverSocket = new ServerSocket(port);
			handletime htobj = new handletime();
			while (true) {
				// Reading the message from the client
				socket = serverSocket.accept();
				InputStream is = socket.getInputStream();
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				String number = br.readLine();
				// String n = number.substring(0, number.indexOf("hop:"));
				System.out.println("Message received from client is " + number);
				// hop = hops.extracthop(number);
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
					OutputStream ops = socket.getOutputStream();
					OutputStreamWriter opsw = new OutputStreamWriter(ops);
					BufferedWriter bpw = new BufferedWriter(opsw);
					// hop++;
					String successmsg = returnMessage + "\n";
					bpw.write(successmsg);
					bpw.flush();
					// String s = successmsg.substring(0,
					// successmsg.indexOf("hop:"));
					System.out.println("Message sent to the client : " + successmsg);

				} else {
					returnMessage = htobj.reqdtime(number);
					// Sending the response back to the client.
					// hop++;
					String rep = String.valueOf(returnMessage) + "\n";
					OutputStream os = socket.getOutputStream();
					OutputStreamWriter osw = new OutputStreamWriter(os);
					BufferedWriter bw = new BufferedWriter(osw);
					bw.write(rep);
					// String s = rep.substring(0, rep.indexOf("hop:"));
					System.out.println("Message sent to the client is " + rep);
					bw.flush();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				socket.close();
				System.out.println("TCP socket closed");
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

			/*
			 * String hopss = newss.substring(e + 1); int g =
			 * newss.indexOf("hop") + 1; hop =
			 * Integer.parseInt(newss.substring(g, hopss.length()));
			 * System.out.println(hop);
			 */

		} catch (Exception e) {
			// Client needs to send message in a proper format
			OutputStream ops = socket.getOutputStream();
			OutputStreamWriter opsw = new OutputStreamWriter(ops);
			BufferedWriter bpw = new BufferedWriter(opsw);
			String rejectmsg = "usage 'modify(data_you_want_to_set):userid(your_username):password(your_password)'"
					+ "\n";
			bpw.write(rejectmsg);
			bpw.flush();
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
