
//package FCN_PROJ;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 *
 * @author Shraddha Atrawalkar
 */
public class clientu implements Runnable {

	private static DatagramSocket socket = null;
	private static DatagramPacket response = null;
	public static String host;
	public static int udpport;
	public static int reqt;
	public static String userid;
	public static String password;
	public static int times;
//	private int hop = 0;
	public static boolean utc = false;

	@Override
	public void run() {
		if (times == 0) {
			times = 1;
		}
		for (int i = 1; i <= times; i++) {
			try {

				calcrtt rttobj = new calcrtt();

				socket = new DatagramSocket();
				InetAddress address = InetAddress.getByName(host);
				// Send the message to the server
				String number = "";
				if (reqt != 0) {
					// String number = "modify10:useridssa293:passwordpass1234";
					number = "modify" + reqt + ":userid" + userid + ":password" + password;
				} else if (utc) {
					number = "UTC";
				} else {
					number = "CAL";
				}
				String sendmsg = number;
				System.out.println(sendmsg);
				DatagramPacket dp = new DatagramPacket(sendmsg.getBytes(), sendmsg.getBytes().length, address, udpport);
				rttobj.setRtt_start(System.currentTimeMillis());
				socket.send(dp);
				//sendmsg = sendmsg.substring(0, sendmsg.indexOf("hop:"));
				System.out.println("Message sent to the udp server : " + sendmsg);

				// Get the message from the server
				byte[] resp = new byte[256];
				response = new DatagramPacket(resp, resp.length);
				socket.receive(response);
				rttobj.setRtt_end(System.currentTimeMillis());
				byte[] messages = response.getData();
				String recv = new String(messages, 0, response.getLength());
				//hop = hops.extracthop(recv);
				//recv = recv.substring(0, recv.indexOf("hop:"));
				System.out.println("Message received from the server : " + recv );

				System.out.println("The round trip time (Client-Proxy/Server) is: " + rttobj.calrtt() + "ms");
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
