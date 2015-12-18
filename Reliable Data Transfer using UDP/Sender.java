//package fcnprojfile;

//import fcnprojfile.getchecksum;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import javax.swing.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

/**
 * Class which will break the file into chunks and send over the network It will
 * also implement TCP TAHOE & RENO
 * 
 * @author shraddha
 */
public class Sender {

	// private static int R = 0;
	// private static Long timeout = Long.parseLong("100");
	// Create the socket, set the address and create the file to be sent
	public DatagramSocket socket;

	public static ArrayList<windowpacket> filebits = new ArrayList<>();
	private int ssthresh = 8;
	private int start = 0;
	private int end = 1;
	private float cwnd = 1.0f;
	private int seqno = 0;
	private InetAddress destip;
	private InetAddress srcip;
	public static String sendfile;
	public static long timeout;
	private int sourceport;
	public static int destport;
	public static boolean quiet = false;
	public static Checksum checksumobj;
	ActionListener task;
	Timer time;

	public void initializetimer() {
		task = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent ae) {
				for (int i = start; i < end; i++) {
					if ((System.currentTimeMillis() - filebits.get(i).timestamp) > timeout && !filebits.get(i).ack) {
						try {

							filebits.get(i).timestamp = System.currentTimeMillis();
							packet sendpack = filebits.get(i).p;
							sendpack.sourceport = sourceport;
							sendpack.destport = destport;
							// checksumobj.update(sendpack.data, 0,
							// sendpack.data.length);
							sendpack.chksum = getchecksum.datachecksum(sendpack.data);
							// if (!quiet) {
							System.out.println("resending packet " + sendpack.seqno);
							// }
							sendpack(sendpack);
						} catch (IOException ex) {
							Logger.getLogger(Sender.class.getName()).log(Level.SEVERE, null, ex);
						}
					}
				}
			}
		};
	}

	public void starttimer() {
		int delay = 1000;
		time = new Timer(delay, task);
		time.start();
	}

	public Sender(int port, String dest) throws SocketException, UnknownHostException {
		socket = new DatagramSocket(port);
		sourceport = port;
		destport = port;
		destip = InetAddress.getByName(dest);// "129.21.30.37"
		srcip = InetAddress.getByName("localhost");
		checksumobj = new CRC32();

	}

	public void Send() throws IOException {
		// System.out.println("Sending the file");
		for (int i = start; i < end && i < filebits.size(); i++) {
			filebits.get(i).timestamp = System.currentTimeMillis();
			packet sendpack = filebits.get(i).p;
			sendpack.sourceport = sourceport;
			sendpack.destport = destport;
			sendpack.chksum = getchecksum.datachecksum(sendpack.data);

			sendpack(sendpack);
		}

	}

	public void splitfile(String filename) throws FileNotFoundException, IOException {
		// Create a byte array to store the filestream
		File file = new File(filename);
		Long check = (new getchecksum()).computechecksum(filename);
		System.out.println("Hash code of the file sent " + check);
		InputStream inFromFile = new FileInputStream(file);
		byte[] filebytes = new byte[512];
		int n = inFromFile.read(filebytes);
		while (n != -1) {
			if (n < (512)) {
				byte[] newdata = new byte[n];
				for (int i = 0; i < n; i++) {
					newdata[i] = filebytes[i];
				}
				filebytes = newdata;
			}

			windowpacket p = new windowpacket(new packet(filebytes.clone(), seqno++));
			filebits.add(p);
			n = inFromFile.read(filebytes);
		}
	}

	private void sendpack(packet sendpack) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(sendpack);
		oos.flush();
		byte[] send = new byte[1024 * 2];
		send = baos.toByteArray();
		DatagramPacket dp = new DatagramPacket(send, send.length, destip, destport);
		// System.out.println("Sending to " + destip + "" + destport);
		if (!quiet) {
			// System.out.println("sending chksum " + sendpack.chksum + " seq "
			// + sendpack.seqno);
			System.out.println("Sending seq: " + sendpack.seqno);
		}
		socket.send(dp);
	}

	public void recvack() throws IOException, ClassNotFoundException {
		byte[] recvdata = new byte[1024];
		DatagramPacket dp = new DatagramPacket(recvdata, recvdata.length);
		int newend;
		socket.receive(dp);
		recvdata = dp.getData();
		ByteArrayInputStream bais = new ByteArrayInputStream(recvdata);
		ObjectInputStream ois = new ObjectInputStream(bais);
		packet recvpack = (packet) ois.readObject();
		if (!quiet) {
			System.out.println("Ack received: " + recvpack.ackno);
		}
		if (updateacks(recvpack.ackno)) {// not to resend

			incrcwnd();
			if (start != recvpack.ackno) {// dupack
				if (!quiet) {
					System.out.println("No dup acks");
				}
				start = recvpack.ackno;
			} else {
				if (!quiet) {
					System.out.println("1/2 dupack");
				}
			}

			newend = start + (int) cwnd;
			for (int i = end; i < newend && i < filebits.size(); i++) {
				filebits.get(i).timestamp = System.currentTimeMillis();
				packet sendpack = filebits.get(i).p;
				sendpack.sourceport = sourceport;
				sendpack.destport = destport;
				sendpack.chksum = getchecksum.datachecksum(sendpack.data);
				sendpack(sendpack);
			}
		} else {// resend the lost packet

			if (fcntcp.algo == fcntcp.TAHOE) {
				ssthresh = (int) (cwnd / 2);
				cwnd = 1;
			} else if (fcntcp.algo == fcntcp.RENO) {
				ssthresh = (int) cwnd;
				cwnd = (int) (cwnd / 2);
			}

			if (!quiet) {
				System.out.println("ssthresh = " + ssthresh);
			}

			filebits.get(recvpack.ackno).timestamp = System.currentTimeMillis();
			packet sendpack = filebits.get(recvpack.ackno).p;
			sendpack.sourceport = sourceport;
			sendpack.destport = destport;
			sendpack.chksum = getchecksum.datachecksum(sendpack.data);
			sendpack(sendpack);
			newend = end;
		}
		if (newend > filebits.size()) {
			newend = filebits.size();
		}
		end = newend;
		ois.close();
		bais.close();

	}

	private boolean updateacks(int ack) {
		// System.out.println("strt in update " + start);
		for (int i = start; i < end; i++) {
			if (filebits.get(i).p.seqno < ack) {
				filebits.get(i).ack = true;
			}
			if (filebits.get(i).p.seqno == ack) {
				filebits.get(i).dupack++;
				if (!filebits.get(i).ack) {
					filebits.get(i).ack = true;
				} else {
					filebits.get(i).dupack++;
				}
				// System.out.println(filebits.get(i).p.seqno + " " +
				// filebits.get(i).dupack);
				if (filebits.get(i).dupack > 3) {
					return false;
				}
			}
		}

		return true;
	}

	private void incrcwnd() {
		if (cwnd <= ssthresh) {
			cwnd++;
		} else {
			cwnd += 1.0f / cwnd;
		}
		if (!quiet) {
			System.out.println("Congestion window: " + cwnd);
		}
	}

	public boolean tostop() {
		for (int i = 0; i < filebits.size(); i++) {
			if (!filebits.get(i).ack) {
				return true;
			}
		}
		return false;
	}
}
