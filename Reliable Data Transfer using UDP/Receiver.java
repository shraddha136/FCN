
//package fcnprojfile;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import static java.util.Collections.sort;
import java.util.Comparator;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

/**
 * Class will will receive the file
 * 
 * @author shraddha
 */
public class Receiver extends Thread {

	public boolean skip = true;
	public boolean corrupt = true;
	private static int R;
	private int recvseqno = 0;
	// private static InetAddress address;
	private static InetAddress destip;
	private InetAddress srcip;
	public static int sourceport;
	public static int destport;
	private DatagramSocket socket;
	public static boolean quiet = false;
	private static ArrayList<packet> recvdpackets = new ArrayList<>();

	public Receiver(int port, String recvip) throws SocketException, UnknownHostException {
		destip = InetAddress.getByName(recvip);// "129.21.22.196"
		srcip = InetAddress.getByName("localhost");
		socket = new DatagramSocket(port);
		sourceport = port;
		destport = port;
	}

	public boolean receive() throws IOException, ClassNotFoundException {
		byte[] recvdata = new byte[1024];
		DatagramPacket dp = new DatagramPacket(recvdata, recvdata.length);
		socket.receive(dp);
		recvdata = dp.getData();
		ByteArrayInputStream bais = new ByteArrayInputStream(recvdata);
		ObjectInputStream ois = new ObjectInputStream(bais);
		packet recvpack = (packet) ois.readObject();
		if (!quiet) {
			System.out.println("Received seq: " + recvpack.seqno);
		}
		/*
		 * if (skip && recvpack.seqno == 4) { System.out.println("Packet " +
		 * recvpack.seqno + " lost"); skip = false; return true; }
		 */

		/*if (recvpack.seqno == 7 && corrupt) {
			System.out.println("Packet " + recvpack.seqno + " was corrupted");
			corrupt = false;
			byte[] tocorrupt;
			String x = "hello";
			tocorrupt = x.getBytes();
			recvpack.data = tocorrupt;
		}*/

		// System.out.println("chk recv" + chk);
		addpacket(recvpack);

		recvdpackets.sort(new compareseq());
		if (!quiet) {
			printseq();
		}

		ois.close();

		bais.close();
		// System.out.println("get eof " + recvpack.eof);
		// if (recvpack.eof && (recvpack.seqno == getacktosend() - 1)) {

		if (readytowrite(recvpack)) {
			writetofile();
			return false;
		} else {
			return true;
		}

	}

	public void sendack() throws IOException {
		packet sendack = new packet(null, recvseqno++);
		sendack.ackno = getacktosend();
		sendack.controlfield = packet.ACK;
		sendack.sourceport = sourceport;
		sendrecvack(sendack);

	}

	private void sendrecvack(packet sendpack) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(sendpack);
		oos.flush();
		byte[] send = new byte[1024];
		send = baos.toByteArray();
		DatagramPacket dp = new DatagramPacket(send, send.length, destip, destport);
		if (!quiet) {
			System.out.println("Sending ack: " + sendpack.ackno);
		}
		socket.send(dp);

	}

	private int getacktosend() {
		int startseq = recvdpackets.get(0).seqno;
		for (int i = 0; i < recvdpackets.size(); i++) {
			if (recvdpackets.get(i).seqno != startseq) {
				break;
			}
			startseq++;
		}
		return startseq;
	}

	private void printseq() {
		for (int i = 0; i < recvdpackets.size(); i++) {
			System.out.print(recvdpackets.get(i).seqno);
		}
		System.out.println("");
	}

	private void addpacket(packet recvpack) {
		boolean contains = false;
		for (int i = 0; i < recvdpackets.size(); i++) {
			if (recvdpackets.get(i).seqno == recvpack.seqno) {
				contains = true;
				break;
			}
		}
		boolean corrupt = iscorrupt(recvpack);

		// System.out.println("before add " + recvdpackets.size());
		if (!contains && !corrupt) {
			// System.out.println("not corr");
			recvdpackets.add(recvpack);
			// System.out.println("after add " + recvdpackets.size());
		}
	}

	private void writetofile() {
		OutputStream os = null;
		try {
			String newfile = "newtest.txt";
			os = new FileOutputStream(newfile);
			for (int i = 0; i < recvdpackets.size(); i++) {
				os.write(recvdpackets.get(i).data);
				os.flush();
			}
			Long check = (new getchecksum()).computechecksum(newfile);
			System.out.println("Hash code of the file received " + check);

			// System.out.println("Hash of the received file is: " + (new
			// File("test.txt")).hashCode());
		} catch (FileNotFoundException ex) {
			Logger.getLogger(Receiver.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
			Logger.getLogger(Receiver.class.getName()).log(Level.SEVERE, null, ex);
		} finally {
			try {
				os.close();

			} catch (IOException ex) {
				Logger.getLogger(Receiver.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}

	private boolean readytowrite(packet recvpack) {
		int order = recvdpackets.get(0).seqno;
		// System.out.println("in ready " + recvdpackets.get(recvdpackets.size()
		// - 1).eof);
		if (true == recvdpackets.get(recvdpackets.size() - 1).eof) {
			// System.out.println("inside if");
			for (int i = 0; i < recvdpackets.size(); i++) {
				if (recvdpackets.get(i).seqno == order) {
					order++;
				} else {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	private boolean iscorrupt(packet recvpack) {
		if (getchecksum.datachecksum(recvpack.data) == recvpack.chksum) {
			return false;
		}
		return true;

	}

	public static class compareseq implements Comparator<packet> {

		@Override
		public int compare(packet t, packet t1) {
			if (t.seqno < t1.seqno) {
				return -1;
			} else if (t.seqno > t1.seqno) {
				return 1;
			} else {
				return 0;
			}
		}

	}
}
