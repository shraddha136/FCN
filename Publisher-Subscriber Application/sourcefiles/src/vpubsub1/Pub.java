//package vpubsub1;

/**
 * This class defines the GUI of the publisher and starts the publisher 
 */

import java.awt.EventQueue;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import java.awt.event.ActionEvent;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextField;

public class Pub extends Thread {

    /**
     * Define the contents of the GUI
     */
    JFrame f;
    JTextField tf1;
    JTextField tf12;
    JLabel jl1;
    JComboBox comboBox;
    JButton btnAddTopic;
    JLabel jl2;
    JList list;
    JLabel jl3;
    JButton pubfeed;
    ImageIcon image;

    private static final String PUB = "PUBLISHER"; //text for title

    Publisher pobj;

    /**
     * Main class to start the publisher application
     * @param args 
     */
    public static void main(String[] args) {
        Thread t = new Thread(() -> {
            EventQueue.invokeLater(() -> {
                try {
                    new Pub().f.setVisible(true);
                } catch (Exception e) {
                }
            });
        });
        t.start();

    }

    /**
     * Define the constructor and start the Publisher
     * @throws IOException 
     */
    public Pub() throws IOException {
        pobj = new Publisher();
        pubimp pubobj = new pubimp(pobj);
        pubobj.start();
        create_pub();
    }

    /**
     * Create the GUI frame
     */
    private void create_pub() {
        f = new JFrame(PUB);
        f.getContentPane().setLayout(null);
        f.setBounds(100, 100, 1000, 500);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addelements(f);

    }

    /**
     * Add elements to the Frame
     * @param Frame 
     */
    private void addelements(JFrame Frame) {

        jl1 = new JLabel("Enter Topic");
        tf1 = new JTextField();
        comboBox = new JComboBox();
        btnAddTopic = new JButton("Add Topic");
        btnAddTopic.addActionListener((ActionEvent e) -> {
            String topic = tf1.getText();
            tf1.setText("");
            if (!topic.isEmpty() && !topic.contentEquals("")) {
                comboBox.addItem(topic);
                pobj.publishertopics(topic);
            }
        });

        jl2 = new JLabel("Available Topics");
        list = new JList();
        jl3 = new JLabel("Publish Feed:");
        tf12 = new JTextField();

        pubfeed = new JButton("Publish");
        pubfeed.addActionListener((ActionEvent e) -> {
            String topic = (String) comboBox.getSelectedItem();
            String msg = tf12.getText();
            if (msg.equalsIgnoreCase("")) {
            } else {
                pobj.publishfeed(topic, msg, PUB);
            }
        });

        jl1.setBounds(67, 37, 134, 28);
        tf1.setBounds(167, 37, 134, 28);
        btnAddTopic.setBounds(332, 37, 117, 29);
        tf12.setBounds(167, 157, 200, 200);
        jl3.setBounds(67, 157, 134, 27);
        comboBox.setBounds(167, 97, 134, 27);
        list.setBounds(84, 118, 1, 1);
        jl2.setBounds(67, 97, 134, 27);
        pubfeed.setBounds(400, 157, 117, 29);

        ImageIcon icon = new ImageIcon("pub.jpg");
        JLabel thumb = new JLabel();
        thumb.setIcon(icon);
        thumb.setBounds(600, 10, 300, 168);

        Frame.getContentPane().add(jl1);
        Frame.getContentPane().add(tf1);
        Frame.getContentPane().add(btnAddTopic);
        Frame.getContentPane().add(tf12);
        Frame.getContentPane().add(jl3);
        Frame.getContentPane().add(jl2);
        Frame.getContentPane().add(list);
        Frame.getContentPane().add(comboBox);
        Frame.getContentPane().add(pubfeed);
        Frame.getContentPane().add(thumb);
    }
}
