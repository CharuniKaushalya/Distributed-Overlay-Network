/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.java;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;
import java.text.DecimalFormat;

/**
 *
 * @author Charuni
 */
public class network{
	private DatagramSocket socket;
    private int receivedMessages, sentMessages, unAnsweredMessages;
    private DecimalFormat formatter = new DecimalFormat("0000");

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

	                System.out.println("receiving ; " + message);
	                receiver(message);
	            //    onResponseReceived(message,new Node(packet.getAddress().getHostName(),packet.getPort()));

	            } catch (IOException e) {
	                e.printStackTrace();
	            }

	        }
	    }
	 
	    private void sender(String msg) {
            String length_final = formatter.format(msg.length() + 5);
    		String msg_final = length_final + " " + msg;
	        try {
	            DatagramPacket packet = new DatagramPacket(msg_final.getBytes(), msg_final.getBytes().length,
	            		 InetAddress.getByName(config.BOOTSTRAP_IP),config.BOOTSTRAP_PORT);
	            socket.send(packet);
	            sentMessages++;
	        } catch (IOException e) {
	            System.out.println(e);
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

	                    System.out.println("Case 1");
	                    break;

	                case 2:
	                    System.out.println("Case 2");
	                    break;
	                case 9996:
	                    System.out.println("Failed to register. BootstrapServer is full.");
	                    break;

	                case 9997:
	                    System.out.println("Failed to register. This ip and port is already used by another App.");
	                    //closeSocket();
	                    break;

	                case 9998:
	                    System.out.println("You are already registered. Please unregister first.");
	                    break;

	                case 9999:
	                    System.out.println("Error in the command. Please fix the error");
	                   // closeSocket();
	                    break;
	            }

	        } else if (config.UNROK.equals(command)) {
	            System.out.println("Successfully unregistered this node from the boostrap server");

	        }else {
	            unAnsweredMessages++;
	        }
	    }
	
    


}
