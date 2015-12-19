//package vpubsub1;

/**
 * This class defines the GUI of the subscriber and starts the subscriber
 */
import java.awt.EventQueue;
import javax.swing.*;
import java.io.*;
import java.awt.event.ActionEvent;
import java.util.Scanner;

public class Sub extends Thread {

    JFrame Frame;
    JComboBox cb, cb1;
    JTextPane tp;
    JLabel label, subnamelab, subname;
    Subscriber subobj;

    /**
     * The following list is to simulate a small scale publisher-subscriber
     * messaging pattern
     */
    public static int[] portlist = {6001, 6002, 6003, 6004, 6005}; //list of possible ports
    public static String[] subnames = {"Vegetables", "Fruit", "Meat", "Poultry", "Chocolate"};//List of possible subscriber names
    public static String[] subimages = {"sub2.jpg", "sub1.jpg", "sub3.jpg", "sub4.jpg", "sub5.jpg"};//list of images on the subscriber GUI

    /**
     * Main class to start the subscriber application
     *
     * @param args
     */
    public static void main(String[] args) {

        System.out.println("How many subscribers are in the network?");
        Scanner in = new Scanner(System.in);
        int n = in.nextInt();
        for (int i = 0; i < n; i++) {
            int port = portlist[i];
            String name1 = subnames[i];
            int ind = i;

            EventQueue.invokeLater(() -> {
                try {
                    Sub window = new Sub(port, name1, ind);
                    window.Frame.setVisible(true);
                } catch (Exception e) {
                }
            });

        }

    }

    /**
     * Method to add a new subscriber and start it
     * @param port Port at which subscriber starts
     * @param name Name of the subscriber
     * @param i    Index of the entity
     * @throws IOException 
     */
    public Sub(int port, String name, int i) throws IOException {
        subobj = new Subscriber(this, port);
        subimp sobj = new subimp(subobj, port);
        sobj.start();
        createsub(name, i);
    }

    /**
     * This method updates the list on the subscriber GUI
     */
    public void resettopics() {
        cb.removeAllItems();
        subobj.pubtopics.stream().forEach(cb::addItem);
    }

    /**
     * Set the published feed to the text pane 
     * @param pfeed Published feed
     */
    public void resetfeed(String pfeed) {
        String feed = tp.getText() + "\n" + pfeed;
        tp.setText(feed);

    }

    /**
     * Update the Unsubscribed topic list
     */
    public void resetunsub() {
        cb1.removeAllItems();
        subobj.topicssub.stream().forEach(cb1::addItem);
    }

    /**
     * Method to create GUI of the subscriber
     * @param name  Name of the subscriber
     * @param i     Index of sub
     */
    private void createsub(String name, int i) {
        Frame = new JFrame("Subscriber");
        Frame.setBounds(100, 100, 1000, 500);
        Frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Frame.getContentPane().setLayout(null);

        addelements(Frame, name, i);

    }

    /**
     * Method to add elements on the GUI
     * @param Frame JFrame
     * @param name  Name of the subscriber
     * @param i     Index
     */
    private void addelements(JFrame Frame, String name, int i) {
        subnamelab = new JLabel("Subscriber Name: " + portlist[i]);
        subname = new JLabel(name);

        cb = new JComboBox();
        subobj.pubtopics.stream().forEach(cb::addItem);

        JButton btnNewButton = new JButton("Subscribe To Topic");
        btnNewButton.addActionListener((ActionEvent e) -> {

            String selectedTopic = (String) cb.getSelectedItem();
            subobj.subtotopic(selectedTopic);
        });

        label = new JLabel("Published Feed:");

        tp = new JTextPane();

        label = new JLabel("Select Topic to Unsubscribe");

        cb1 = new JComboBox();

        JButton btnNewButton_1 = new JButton("Unsubscribe From Topic");
        btnNewButton_1.addActionListener((ActionEvent e) -> {
            String selectedTopic = (String) cb1.getSelectedItem();
            //System.out.println("remove " + selectedTopic);
            subobj.unsbtp(selectedTopic);
        });

        ImageIcon icon = new ImageIcon(subimages[i]);
        JLabel thumb = new JLabel();
        thumb.setIcon(icon);

        subnamelab.setBounds(30, 23, 110, 27);
        subname.setBounds(176, 22, 117, 29);
        cb.setBounds(30, 98, 110, 27);
        btnNewButton.setBounds(176, 97, 187, 29);
        label.setBounds(30, 147, 200, 27);
        tp.setBounds(176, 147, 246, 120);
        label.setBounds(30, 267, 200, 27);
        cb1.setBounds(30, 297, 110, 27);
        thumb.setBounds(600, 10, 300, 168);
        btnNewButton_1.setBounds(176, 297, 187, 29);
        Frame.getContentPane().add(subnamelab);
        Frame.getContentPane().add(subname);
        Frame.getContentPane().add(cb);
        Frame.getContentPane().add(thumb);
        Frame.getContentPane().add(cb1);
        Frame.getContentPane().add(label);
        Frame.getContentPane().add(tp);
        Frame.getContentPane().add(label);
        Frame.getContentPane().add(btnNewButton);
        Frame.getContentPane().add(btnNewButton_1);

    }
}
