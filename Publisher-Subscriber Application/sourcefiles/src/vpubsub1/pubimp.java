//package vpubsub1;


/**
 * Class to implement the Publisher working
 */
import java.io.IOException;
import java.net.ServerSocket;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class pubimp extends Thread {

    ServerSocket socket; //Socket for the publisher
    Publisher pobj; //publisher object
    public static int Pubport = 5000;//publisher port

    /**
     * Constructor for this class
     * @param pobj Publisher object
     * @throws IOException 
     */
    public pubimp(Publisher pobj) throws IOException {
        this.pobj = pobj;
        socket = new ServerSocket(Pubport);
    }

    @Override
    public void run() {
        while (true) {
            try {
                Socket soc = socket.accept();
                try (ObjectOutputStream oboust = new ObjectOutputStream(soc.getOutputStream())) {
                    ObjectInputStream ois = new ObjectInputStream(soc.getInputStream());
                    String command = (String) ois.readObject();
                    
                    if (command.equalsIgnoreCase("pubtopicsRequest")) {
                        oboust.writeObject(pobj.pubtopics);
                        oboust.flush();
                    } else if (command.equalsIgnoreCase("subscribe")) {
                        pobj.addsubport((String) ois.readObject(), Integer.parseInt((String) ois.readObject()));
                    } else if (command.equalsIgnoreCase("unsubscribe")) {
                        pobj.unsubport((String) ois.readObject(), Integer.parseInt((String) ois.readObject()));
                    }
                    ois.close();
                }
            } catch (IOException | ClassNotFoundException e) {
            }
        }
    }
}
