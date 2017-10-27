/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.java.controller;

import main.java.model.Node;
import main.java.model.SearchQuery;
import main.java.model.SearchResult;
import main.java.model.config;

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

    final private MovieController movieController = MovieController.getInstance("../../resources/File Names.txt");

    private final List<Node> neighbours = new ArrayList<>();
    private final List<SearchQuery> searchQueryList = new ArrayList<>();
    private final Node myNode = new Node();

    private int noOfLocalResults = 0;
    private String localQuerry = "";
    private List<String> LocalQueries = new ArrayList<>();
    private int queryPointer = 0;
    private int noOfNodesInTheNetwork = 0;

    public network() {
        BasicConfigurator.configure();

    }

    public void run() throws IOException {
    	
        this.addObserver(config.APP);
        boolean done = true;
        while (true) {
            if (done) {
                socket = new DatagramSocket(config.PORT);
                String msg = config.REG + " " + config.IP + " " + config.PORT + " " + config.USERNAME;

                this.myNode.setIP_address(config.IP);
                this.myNode.setPort_no(config.PORT);
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
                UpdateTheCMD(message);
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

    public synchronized void startSearch(String queryText) {
        noOfLocalResults = 0;
        localQuerry = queryText;

        SearchQuery query = new SearchQuery();
        query.setOriginNode(myNode);
        query.setQueryText(queryText);
        query.setHops(0);
        query.setSenderNode(myNode);
        query.setTimestamp(System.currentTimeMillis());

        searchQueryList.add(query);

        List<String> movies = this.movieController.searchMovies(query.getQueryText());

        SearchResult result = new SearchResult();
        result.setOrginNode(myNode);
        result.setMovies(movies);
        result.setHops(0);
        result.setTimestamp(query.getTimestamp());

        for (Node peer : neighbours) {
            searchRequest(peer, query);
        }

        searchResponce(query.getOriginNode(), result);

    }

    public boolean searchRequest(Node peer, SearchQuery query) {
        String msg = config.SER + " " + config.IP + " " + config.PORT + " " + query.getQueryText() + " " + query.getHops();
        sender(msg,new Node(peer.getIP_address(),peer.getPort_no()));
        return true;
    }

    public boolean searchResponce(Node originNode, SearchResult result) {
        String msg = config.SEROK + " " + result.getMovies().size() + " " + config.IP + " " + config.PORT + " " + result.getHops();
        for (String m : result.getMovies()) {
            msg += " " + m;
        }
        sender(msg,new Node(result.getOrginNode().getIP_address(),result.getOrginNode().getPort_no()));
        return true;
    }

    synchronized private void search(SearchQuery query) {

        if (searchQueryList.contains(query)) {
            unAnsweredMessages++;
            return;
        } else {
            searchQueryList.add(query);
        }

        // Increase the number of hops by one
        query.setHops(query.getHops() + 1);
        query.setSenderNode(myNode);

        Node sender = query.getSenderNode();

        List<String> results = movieController.searchMovies(query.getQueryText());

        SearchResult result = new SearchResult();
        result.setOrginNode(myNode);
        result.setMovies(results);
        result.setHops(query.getHops());
        result.setTimestamp(query.getTimestamp());

        neighbours.stream().filter(peer -> !peer.equals(sender)).forEach(peer -> {
            searchRequest(peer, query);
        });
        logger.info("Result sent to "+query.getOriginNode());
        searchResponce(query.getOriginNode(), result);
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

    private void sender(String msg, Node node) {
        String length_final = formatter.format(msg.length() + 5);
        String msg_final = length_final + " " + msg;
        try {
            DatagramPacket packet = new DatagramPacket(msg_final.getBytes(), msg_final.getBytes().length,
                    InetAddress.getByName(node.getIP_address()), node.getPort_no());
            socket.send(packet);
            sentMessages++;
        } catch (IOException e) {
            logger.error(e);
        }
    }

    //will be invoked when a response is received
    private void receiver(String message) {
        UpdateTheCMD(message);
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
                    String msg = config.JOIN + " " + config.IP + " " + config.PORT;
                    sender(msg,node);
                    addNeighbour(node);
                    break;

                case 2:

                    for (int i = 0; i < no_nodes; i++) {
                        String host = tokenizer.nextToken();
                        String hostport = tokenizer.nextToken();

                        Node temp = new Node(host, Integer.parseInt(hostport));
                        String joinMsg = config.JOIN + " " + config.IP + " " + config.PORT;
                        sender(joinMsg, temp);
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
            String ip = tokenizer.nextToken();
            int port = Integer.parseInt(tokenizer.nextToken());
            Node node = new Node(ip, port);

            String joinokMsg = config.JOINOK + " 0";
            sender(joinokMsg, node);
            addNeighbour(node);

        } else if (config.JOINOK.equals(command)) {
            int result = Integer.parseInt(tokenizer.nextToken());
            switch (result) {
                case 0:
                    logger.info("join successful");
                    break;

                case 9999:
                    logger.error("Error while adding new node to routing table");
                    break;
            }

        } else if (config.SER.equals(command)) {
            String ip = tokenizer.nextToken();
            int port = Integer.parseInt(tokenizer.nextToken());
            String query= tokenizer.nextToken();
            int hops = Integer.parseInt(tokenizer.nextToken());

            search(new SearchQuery(new Node(ip,port),query,hops));

        } else if (config.SEROK.equals(command)) {
            int no_files = Integer.parseInt(tokenizer.nextToken());
            String ip = tokenizer.nextToken();
            int port = Integer.parseInt(tokenizer.nextToken());
            int hops = Integer.parseInt(tokenizer.nextToken());

            List<String> movies = new ArrayList<>();

            for (int i = 0; i < no_files; i++)
                movies.add(tokenizer.nextToken());

            SearchResult result = new SearchResult(new Node(ip, port), movies, hops);
            int moviesCount = no_files;

            logger.info(" Result : " + ++noOfLocalResults + "  [ Query = " + localQuerry + "]");
            String output = String.format("Number of movies: %d\nMovies: %s\nHops: %d\nSender %s:%d\n",
                    moviesCount, result.getMovies().toString(), result.getHops(), result.getOrginNode().getIP_address(), result.getOrginNode().getPort_no());
            UpdateTheCMD(output);

        } else {
            unAnsweredMessages++;
        }

    }

    private void addNeighbour(Node node) {
        if (!neighbours.contains(node)) {
            neighbours.add(node);
        }
    }
    
    public void printNeighbors() {
        neighbours.forEach((a)->System.out.println(a.getIP_address() + ": " + a.getPort_no()));
        String msg = "\n***********************\nNeighbous\n***********************\n" ;
        printOnCMD(msg);
        neighbours.forEach((a)->printOnCMD(a.getIP_address() + ": " + a.getPort_no() + "\n"));
        printOnCMD("***********************\n");
    }

    @Override
    public void update(Observable o, Object arg) {
        // TODO Auto-generated method stub

    }

    public void UpdateTheCMD(String msg) {
        setChanged();
        msg = config.USERNAME + "> " + msg + " [" + Utils.getTimeStamp() + "]\n";
        notifyObservers(msg);
        clearChanged();
    }
    
    public void printOnCMD(String msg) {
        setChanged();
        notifyObservers(msg);
        clearChanged();
    }


}
