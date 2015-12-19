//package vpubsub1;

/**
 * This class defines the format of the messages exchanged in the network
 */

import java.io.Serializable;

public class Message implements Serializable {

    public String topic;
    public String payload;

    public Message(String topic, String payload) {
        this.topic = topic;
        this.payload = payload;
    }
}
