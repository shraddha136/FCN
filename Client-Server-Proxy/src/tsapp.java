//package fcnshraddha;

/**
 *
 * @author Shraddha Atrawalkar This is the main class which will parse the
 *         commandline arguments and call appropriate classes
 */
public class tsapp {

	public static boolean server = false;
	public static boolean client = false;
	public static boolean proxy = false;
	private static String reqt = null;
	public static boolean proxyclientudp = false;
	public static boolean proxyclienttcp = false;
	public static String protocol = "udp";
	public static String serveruser;
	public static String serverpswd;
	public static int times;
	private static int tport;
	private static int uport;
	public static int both = 0;

	public static void main(String[] args) {

		int len = args.length;
		for (int i = 0; i < args.length; i++) {

			switch (args[i]) {

			case "-s":
				System.out.println("Server");
				server = true;
				// parsing server commands
				for (int j = i; j < args.length; j++) {
					switch (args[j]) {
					case "-T":
						int pos = j + 1;
						reqt = args[pos];
						j++;
						break;
					case "--user":
						pos = j + 1;
						serveruser = servert.userid = serveru.userid = args[pos];
						j++;
						break;
					case "--pass":
						pos = j + 1;
						serverpswd = servert.password = serveru.password = args[pos];
						j++;
						break;
					}
				}
				break;

			case "-p":
				System.out.println("Proxy");
				proxy = true;
				// tsapp p server [options] UDP_Port TCP_Port
				// tsapp p 127.0.0.5 ddproxydudp 5000 ddproxydtcp 5001 4000 4001
				proxyspc.host = proxyt.host = proxyu.host = args[1];
				for (int j = 2; j < args.length; j++) {
					int pos;
					// parsing proxy commands
					switch (args[j]) {
					case "--proxy-udp":
						pos = j + 1;
						proxyspc.uptosp = proxyu.uptosp = Integer.parseInt(args[pos]);
						j++;
						proxyclientudp = true;
						both++;
						break;
					case "--proxy-tcp":
						pos = j + 1;
						proxyspc.tptosp = proxyt.tptosp = Integer.parseInt(args[pos]);
						j++;
						proxyclienttcp = true;
						both++;
						break;
					}
				}
				break;
			case "-c":

				// tsapp dc 127.0.0.5 5000
				// tsapp dc 127.0.0.5 dT 99 dduser usr ddpass pw dt 5001
				// tsapp dc 127.0.0.4 dn 10 du 4000
				// tsapp dc 127.0.0.4 dz du 4000
				// tsapp dc server [options] port
				System.out.println("Client");
				client = true;
				clientt.host = clientu.host = args[i + 1];
				for (int j = i + 2; j < args.length; j++) {
					// parsing client commands
					switch (args[j]) {
					case "-T":
						int pos = j + 1;
						clientt.reqt = clientu.reqt = Integer.parseInt(args[pos]);
						j++;
						break;
					case "--user":
						pos = j + 1;
						clientt.userid = clientu.userid = args[pos];
						j++;
						break;
					case "--pass":
						pos = j + 1;
						clientt.password = clientu.password = args[pos];
						j++;
						break;
					case "-t":
						protocol = "TCP";
						break;
					case "-n":
						clientu.times = clientt.times = Integer.parseInt(args[j + 1]);
						break;
					case "-z":
						clientt.utc = clientu.utc = true;
						break;
					}
				} // I have not included -u here because the protocol will be
					// UDP by default if nothing is specified
				break;
			}
		}

		// call the appropriate classes to start communication
		if (server == true) {

			// setting the date at the TCP server for the first time
			servert.reqt = Long.valueOf(reqt);// value to set the
												// server at
			tport = servert.port = Integer.parseInt(args[len - 1]);// tcpserverport
			// setting the date at the UDP server for the first time
			serveru.reqt = Long.valueOf(reqt);// value to set the
												// server at
			uport = serveru.port = Integer.parseInt(args[len - 2]);
			System.out.println("Starting servers");
			(new Thread(new servert())).start();
			(new Thread(new serveru())).start();

		} else if (proxy == true) {
			proxyu.udpserveruser = proxyt.tcpserveruser = serveruser;
			proxyu.udpserverpswd = proxyt.tcpserverpswd = serverpswd;
			// Starting UDP and Proxy servers
			if (proxyclienttcp && !proxyclientudp && both == 1) {
				System.out.println("Starting proxy tcp at " + Integer.parseInt(args[len - 1]));
				int ptport = Integer.parseInt(args[len - 1]); // 5001
				System.out.println("Starting proxy udp at " + Integer.parseInt(args[len - 2]));
				int puport = Integer.parseInt(args[len - 2]); // 5000
				(new Thread(new proxyspc(ptport, puport))).start();
				int tp = Integer.parseInt(args[len - 1]);
				(new Thread(new proxyt(tp))).start();
			}
			if (!proxyclienttcp && proxyclientudp && both == 1) {

				System.out.println("Starting proxy tcp at " + Integer.parseInt(args[len - 1]));
				int ptport = Integer.parseInt(args[len - 1]); // 5001
				System.out.println("Starting proxy udp at " + Integer.parseInt(args[len - 2]));
				int puport = Integer.parseInt(args[len - 2]); // 5000
				(new Thread(new proxyspc(ptport, puport))).start();
				int up = Integer.parseInt(args[len - 2]);
				(new Thread(new proxyu(up))).start();
			}
			if (proxyclienttcp == true && both == 2) {
				int tp = Integer.parseInt(args[len - 1]);
				(new Thread(new proxyt(tp))).start();
			}
			if (proxyclientudp == true && both == 2) {
				int up = Integer.parseInt(args[len - 2]);
				(new Thread(new proxyu(up))).start();
			}

		} else if (client == true) {
			if (protocol == "TCP") {
				clientt.tcpport = Integer.parseInt(args[len - 1]);
				// Start the TCP
				(new Thread(new clientt())).start();
			} else {
				clientu.udpport = Integer.parseInt(args[len - 1]);
				(new Thread(new clientu())).start();
			}
		}
	}
}
