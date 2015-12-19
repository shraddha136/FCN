
//package FCN_PROJ;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;

/**
 *
 * @author Shraddha Atrawalkar
 */
public class clientt implements Runnable {

	private static Socket socket;
	public static String host;
	public static int tcpport;
	public static int reqt;
	public static String userid;
	public static String password;
	public static int times;
	public static boolean utc = false;

	@Override
	public void run() {
		if (times == 0) {
			times = 1;
		}
		for (int i = 1; i <= times; i++) {
			try {

				InetAddress address = InetAddress.getByName(host);

				socket = new Socket(address, tcpport);
				calcrtt rttobj = new calcrtt();

				// Send the message to the server
				OutputStream os = socket.getOutputStream();
				OutputStreamWriter osw = new OutputStreamWriter(os);
				BufferedWriter bw = new BufferedWriter(osw);

				String number = "";
				if (reqt != 0) {
					// String number =
					// "modify10:useridssa293:passwordpass1234:hop";
					number = "modify" + reqt + ":userid" + userid + ":password" + password;
				} else if (utc) {
					number = "UTC";
				} else {
					number = "CAL";
				}
				//String sendMessage = number + "hop:" + hop + "\n";
				String sendMessage = number + "\n";
				rttobj.setRtt_start(System.currentTimeMillis());

				bw.write(sendMessage);
				bw.flush();
				//sendMessage = sendMessage.substring(0,sendMessage.indexOf("hop:"));
				System.out.println("Message sent to the server : " + sendMessage);

				// Get the return message from the server
				InputStream is = socket.getInputStream();
				rttobj.setRtt_end(System.currentTimeMillis());
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				String message = br.readLine();
				//hop = hops.extracthop(message);
				//message = message.substring(0,message.indexOf("hop:"));
				System.out.println("Message received from the server : " + message);
				socket.close();
				System.out.println("The round trip time (Client-Server/Proxy) is: " + rttobj.calrtt() + "ms");
			} catch (Exception exception) {
				exception.printStackTrace();
			} finally {
				// Closing the socket
				try {
					socket.close();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}
	}
}
