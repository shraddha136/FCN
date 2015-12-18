
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//package fcnprojfile;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main class which will call all other classes
 * 
 * @author shraddha
 */
public class fcntcp {

	private static boolean client = false;
	private static boolean server = false;
	private static String sendfile;
	public static int algo;
	public static final int TAHOE = 1;
	public static final int RENO = 2;

	public static void main(String[] args) throws SocketException, UnknownHostException, IOException {
		int len = args.length;

		for (int i = 0; i < args.length; i++) {
			switch (args[i]) {
			case "-c":
				// System.out.println("Running as Client");
				client = true;
				break;
			case "-s":
				// System.out.println("Running as Server");
				server = true;
				break;
			case "-f":
				int pos = i + 1;
				sendfile = args[pos];
				i++;
				break;
			case "-a":
				pos = i + 1;
				if ("1".equals(args[pos])) {// tahoe
					System.out.println("");
					algo = 1;
				} else if ("2".equals(args[pos])) {// reno
					algo = 2;
				}
				i++;
				break;
			case "-t":
				pos = i + 1;
				Sender.timeout = Long.parseLong(args[pos]);
				i++;
				break;
			case "-q":
				Sender.quiet = Receiver.quiet = true;
				break;
			}
		}

		String alg;
		if (algo == RENO) {
			alg = "RENO";
		} else {
			alg = "TAHOE";
		}

		if (client) {
			int senderport = Integer.parseInt(args[len - 1]);
			String desthostaddress = args[len - 2];
			// Sender.destport = Integer.parseInt(args[len - 2]);
			Sender s = new Sender(senderport, desthostaddress);
			final String fileName = sendfile;
			s.splitfile(fileName);
			// System.out.println("sending");
			Sender.filebits.get(Sender.filebits.size() - 1).p.eof = true;
			s.Send();
			if (Sender.timeout == 0) {
				Sender.timeout = 1000;
			}
			s.initializetimer();
			Thread t = new Thread(new Runnable() {

				@Override
				public void run() {
					boolean stoprecv = true;
					// if (!Sender.quiet) {
					System.out.println("Implementing Algorithm: " + alg);
					// System.out.println("starting to send");
					// }
					while (stoprecv) {
						try {
							s.recvack();
						} catch (IOException ex) {
							Logger.getLogger(fcntcp.class.getName()).log(Level.SEVERE, null, ex);
						} catch (ClassNotFoundException ex) {
							Logger.getLogger(fcntcp.class.getName()).log(Level.SEVERE, null, ex);
						}
						stoprecv = s.tostop();
						// System.out.println("stop recv " + stoprecv);
					}

				}
			});
			t.start();
			s.starttimer();
		} else if (server) {// receiver
			int receiverport = Integer.parseInt(args[len - 1]);
			String destaddressforrecv = args[len - 2];
			// Receiver.destport = Integer.parseInt(args[len - 2]);
			Receiver r = new Receiver(receiverport, destaddressforrecv);
			Thread t = new Thread(new Runnable() {

				@Override
				public void run() {
					boolean eofflag = true;
					while (eofflag) {
						// System.out.println("inside run");
						try {
							eofflag = r.receive();
						} catch (IOException ex) {
							Logger.getLogger(fcntcp.class.getName()).log(Level.SEVERE, null, ex);
						} catch (ClassNotFoundException ex) {
							Logger.getLogger(fcntcp.class.getName()).log(Level.SEVERE, null, ex);
						}
						try {
							r.sendack();
						} catch (IOException ex) {
							Logger.getLogger(fcntcp.class.getName()).log(Level.SEVERE, null, ex);
						}
					}
				}
			});
			// System.out.println("startingclec");
			t.start();

		}
	}

}
