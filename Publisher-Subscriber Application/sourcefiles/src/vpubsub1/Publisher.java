//package vpubsub1;

/**
 * This class defines the working of the Publisher
 */
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

public class Publisher {

    public ArrayList<String> pubtopics;  //list of topics publisher will publish
    public HashMap<String, ArrayList<Integer>> subscribertopics; //list of topics subscriber will subscribe to
    String ip = "localhost"; //define host
    public static int Pubport = 5000; //Port for the publisher

    /**
     * Define the constructor of this class
     */
    public Publisher() {
        pubtopics = new ArrayList<>();
        subscribertopics = new HashMap<>();
    }

    /**
     * Topics that publisher adds
     *
     * @param newtopic New topic publisher adds
     */
    public void publishertopics(String newtopic) {
        if (pubtopics.contains(newtopic) != true) {
            pubtopics.add(newtopic);
            subup();
        }
    }

    /**
     * Method to update topics
     */
    public void subup() {
        try {
            Socket sub = new Socket(ip, Pubport);
            try (ObjectOutputStream oboust = new ObjectOutputStream(
                    sub.getOutputStream())) {
                oboust.writeObject("reset");
                oboust.writeObject(pubtopics);
                oboust.flush();
            }
        } catch (IOException e) {

        }
    }

    /**
     * Create a mapping between subscriber port and topic
     *
     * @param topic Topic subscribed
     * @param port subscriber port
     */
    public void addsubport(String topic, int port) { //key topic, value list of ports
        if (subscribertopics.containsKey(topic)) {
            if (!subscribertopics.get(topic).contains(port)) {
                subscribertopics.get(topic).add(port);
            }
        } else {
            ArrayList<Integer> portlist = new ArrayList<>();
            portlist.add(port);
            subscribertopics.put(topic, portlist);
        }
    }

    /**
     * Delete mapping when subscriber unsubscribes to topic
     * @param topic Topic subscribed
     * @param port subscriber port
     */
    void unsubport(String topic, int port) {
        if (!subscribertopics.isEmpty() && topic != null) {
            if (subscribertopics.containsKey(topic)) {
                if (subscribertopics.get(topic).contains(port)) {
                    subscribertopics.get(topic).remove(subscribertopics.get(topic).indexOf(port));
                }
            }
        }
    }

    
    /**
     * Method to publish feed publisher wants to
     * @param topic   Topic for which feed is published
     * @param message Feed
     * @param actor   Publisher
     */
    public void publishfeed(String topic, String message, String actor) {
        try {

            if (!subscribertopics.isEmpty()) {
                if (subscribertopics.containsKey(topic)) {
                    ArrayList<Integer> ports = subscribertopics.get(topic);
                    for (int i = 0; i < ports.size(); i++) {
                        int destport = ports.get(i);
                        Socket sub = new Socket(ip, destport);
                        try (ObjectOutputStream oboust = new ObjectOutputStream(
                                sub.getOutputStream())) {
                            oboust.writeObject("feed");
                            oboust.writeObject(new Message(topic, message));
                            oboust.flush();
                        }
                    }
                }
            }

        } catch (UnknownHostException e) {
        } catch (IOException e) {
        }
    }
}
