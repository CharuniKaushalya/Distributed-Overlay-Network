/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.java.controller;

import main.java.model.config;
import main.java.model.Node;
import main.java.view.app;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;
import java.text.DecimalFormat;

/**
 * @author Charuni
 */
public class network extends Observable implements Observer {

    final static private Logger logger = Logger.getLogger(network.class);

    private DatagramSocket socket;
    private int receivedMessages, sentMessages, unAnsweredMessages;
    private DecimalFormat formatter = new DecimalFormat("0000");
    private final List<Node> neighbours = new ArrayList<>();

    public network() {
        BasicConfigurator.configure();
    }

    public void run() throws IOException {

        boolean done = true;
        while (true) {
            if (done) {
                socket = new DatagramSocket(config.PORT);
                String msg = config.REG + " " + config.IP + " " + config.PORT + " " + config.USERNAME;
                sender(msg);
                done = false;
            }
            byte[] buffer = new byte[65536];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            try {
                socket.receive(packet);
                byte[] data = packet.getData();
                String message = new String(data, 0, packet.getLength());

                logger.info("receiving ; " + message);
                receiver(message);
                //    onResponseReceived(message,new Node(packet.getAddress().getHostName(),packet.getPort()));

            } catch (IOException e) {
                logger.error(e);
            }

        }
    }

    public boolean leave() {
        String msg = config.UNREG + " " + config.IP + " " + config.PORT + " " + config.USERNAME;
        sender(msg);
        return true;
    }

    private void sender(String msg) {
        String length_final = formatter.format(msg.length() + 5);
        String msg_final = length_final + " " + msg;
        try {
            DatagramPacket packet = new DatagramPacket(msg_final.getBytes(), msg_final.getBytes().length,
                    InetAddress.getByName(config.BOOTSTRAP_IP), config.BOOTSTRAP_PORT);
            socket.send(packet);
            sentMessages++;
        } catch (IOException e) {
            logger.error(e);
        }
    }

    //will be invoked when a response is received
    private void receiver(String message) {

        receivedMessages++;
        StringTokenizer tokenizer = new StringTokenizer(message, " ");
        String length = tokenizer.nextToken();
        String command = tokenizer.nextToken();

        if (config.REGOK.equals(command)) {
            int no_nodes = Integer.parseInt(tokenizer.nextToken());

            switch (no_nodes) {
                case 0:
                    break;

                case 1:
                    logger.info("registration is successful, 1 nodes contacts is returned");
                    String ip = tokenizer.nextToken();
                    int port = Integer.parseInt(tokenizer.nextToken());


                    Node node = new Node(ip, port);
                    String msg = config.JOIN + " " + ip + " " + port;
                    sender(msg);
                    addNeighbour(node);
                    break;

                case 2:
                    for (int i = 0; i < no_nodes; i++) {
                        String host = tokenizer.nextToken();
                        String hostport = tokenizer.nextToken();

                        Node temp = new Node(host, Integer.parseInt(hostport));
                        String joinMsg = config.JOIN + " " + host + " " + hostport;
                        sender(joinMsg);
                        addNeighbour(temp);
                    }
                    logger.info("registration is successful, 2 nodes' contacts are returned");
                    break;
                case 9996:
                    logger.info("Failed to register. BootstrapServer is full.");
                    break;

                case 9997:
                    logger.info("Failed to register. This ip and port is already used by another App.");
                    //closeSocket();
                    break;

                case 9998:
                    logger.info("You are already registered. Please unregister first.");
                    break;

                case 9999:
                    logger.info("Error in the command. Please fix the error");
                    // closeSocket();
                    break;
            }

        } else if (config.UNROK.equals(command)) {
            logger.info("Successfully unregistered this Node from the boostrap server");

        } else if (config.JOIN.equals(command)) {
        	System.out.println("call join " + config.PORT);
        	//addNeighbour(new Node(tokenizer.nextToken()));

        } else if (config.JOINOK.equals(command)) {

        } else {
            unAnsweredMessages++;
        }
    }
    
    private void addNeighbour(Node node) {
        if (!neighbours.contains(node)) {
            neighbours.add(node);
        }
    }

	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		
	}
	
	public void UpdateTheCMD(String msg) {
        setChanged();
        notifyObservers(msg);
        clearChanged();
    }


}
