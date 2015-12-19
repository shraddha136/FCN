//package vpubsub1;

/**
 * Class to implement the Subscriber working
 */
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class subimp extends Thread {

    Subscriber sobj; //subscriber object
    ServerSocket socket; //socket for the subscriber

    /**
     * Constructor for this class
     *
     * @param sobj Subscriber object
     * @param port Subscriber port
     * @throws IOException
     */
    public subimp(Subscriber sobj, int port) throws IOException {
        socket = new ServerSocket(port);
        this.sobj = sobj;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Socket servsocket = socket.accept();
                ObjectInputStream ois = new ObjectInputStream(servsocket.getInputStream());

                String command = (String) ois.readObject();
                if (command.equalsIgnoreCase("feed")) {
                    Message mobj = (Message) ois.readObject();
                    sobj.pbfd(mobj.topic + ": " + mobj.payload);
                } else if (command.equalsIgnoreCase("reset")) {
                    ArrayList<String> pubtopics = (ArrayList<String>) ois.readObject();
                    sobj.restop(pubtopics);
                }

            } catch (IOException | ClassNotFoundException e) {
            }
        }
    }
}
