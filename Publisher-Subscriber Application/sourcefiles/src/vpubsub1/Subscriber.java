//package vpubsub1;

/**
 * This class defines the working of the Subscriber
 */
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Subscriber {

    Socket middleware; //Socket for subscriber to connect to middleware
    public ArrayList<String> pubtopics; //list of topics
    public ArrayList<String> topicssub;//list of topics subscriber subscribed to
    private Publisher pobj = new Publisher(); //publisher obj
    public static int Pubport = 5000;//publisher port
    public static String Pubip = "localhost";//hostname
    Sub sbr;//subscriber object
    public int port; //port

    /**
     * Define the constructor of this class
     */
    Subscriber() {

    }

    /**
     * Define custom Constructor
     *
     * @param sbr Subscriber object
     * @param port Port of Subscriber
     */
    Subscriber(Sub sbr, int port) {
        try {
            this.sbr = sbr;
            this.port = port;
            topicssub = new ArrayList<>();
            pubtopics = new ArrayList<>();

            middleware = new Socket(Pubip, Pubport);
            ObjectInputStream ois;
            ObjectOutputStream oboust = new ObjectOutputStream(
                    middleware.getOutputStream());
            oboust.writeObject("pubtopicsRequest");
            oboust.flush();
            ois = new ObjectInputStream(
                    middleware.getInputStream());
            pubtopics = (ArrayList<String>) ois.readObject();

            ois.close();
        } catch (UnknownHostException e) {
        } catch (ClassNotFoundException | IOException e) {
        }
    }

    public void restop(ArrayList<String> alltops) {
        sbr.resettopics();
        pubtopics = alltops;
    }

    public void subtotopic(String topic) {
        try {
            middleware = new Socket(Pubip,
                    Pubport);
            try (ObjectOutputStream oboust = new ObjectOutputStream(
                    middleware.getOutputStream())) {
                oboust.writeObject("subscribe");
                oboust.writeObject(topic);
                oboust.writeObject(String.valueOf(port));
                oboust.flush();
            }
            topicssub.add(topic);
            sbr.resetunsub();
        } catch (IOException e) {
        }
    }

    public void pbfd(String feed) {
        sbr.resetfeed(feed);
    }

    public void unsbtp(String tp) {
        try {
            middleware = new Socket(Pubip,
                    Pubport);
            try (ObjectOutputStream oboust = new ObjectOutputStream(
                    middleware.getOutputStream())) {
                oboust.writeObject("unsubscribe");
                oboust.writeObject(tp);
                oboust.writeObject(String.valueOf(port));
                oboust.flush();
            }
            topicssub.remove(tp);

            sbr.resetunsub();
        } catch (IOException e) {
        }
    }

}
