/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.java.controller;

import main.java.model.config;
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
public class network {

    final static private Logger logger = Logger.getLogger(network.class);

    private DatagramSocket socket;
    private int receivedMessages, sentMessages, unAnsweredMessages;
    private DecimalFormat formatter = new DecimalFormat("0000");

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

                    logger.info("Case 1");
                    break;

                case 2:
                    logger.info("Case 2");
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
            logger.info("Successfully unregistered this node from the boostrap server");

        } else {
            unAnsweredMessages++;
        }
    }


}
