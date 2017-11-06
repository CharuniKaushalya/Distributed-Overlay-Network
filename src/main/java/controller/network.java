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
import main.java.model.Statistics;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

/**
 * @author Charuni
 */
public class network extends Observable implements Observer {

	final static private Logger logger = Logger.getLogger(network.class);

	private DatagramSocket socket;
	@SuppressWarnings("unused")
	private int receivedMessages, sentMessages, unAnsweredMessages;
    private List<Integer> latencyArray = new ArrayList<>();
    private List<Integer> hopArray = new ArrayList<>();
	private DecimalFormat formatter = new DecimalFormat("0000");

	final private MovieController movieController = MovieController.getInstance("../../resources/File Names.txt");

	private final List<Node> neighbours = new ArrayList<>();
	private final List<SearchQuery> searchQueryList = new ArrayList<>();
	private List<SearchResult> searchResultList = new ArrayList<>();
	private final Node myNode = new Node();

	private int noOfLocalResults = 0;
	private String localQuerry = "";

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
				receiver(message);
				// onResponseReceived(message,new
				// Node(packet.getAddress().getHostName(),packet.getPort()));

			} catch (IOException e) {
				logger.error(e);
			}

		}
	}

	public boolean unRegister() {
		String msg = config.UNREG + " " + config.IP + " " + config.PORT + " " + config.USERNAME;
		sender(msg);
		return true;
	}

	public boolean send_leave() {
		for (Node peer : neighbours) {
			String msg = config.LEAVE + " " + config.IP + " " + config.PORT;
			sender(msg, new Node(peer.getIP_address(), peer.getPort_no()));
		}
		return true;
	}
	
	public boolean leave_network(Node node) {
		this.removeNeighbour(node);
		String msg = config.LEAVEOK + " 0";
		sender(msg, new Node(node.getIP_address(), node.getPort_no()));
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
		String msg = config.SER + " " + config.IP + " " + config.PORT + " " + query.getQueryText() + " "
				+ query.getHops() +  " " + query.getTimestamp();
		sender(msg, new Node(peer.getIP_address(), peer.getPort_no()));
		return true;
	}

	public boolean searchResponce(Node originNode, SearchResult result) {
		String msg = config.SEROK + " " + result.getMovies().size() + " " + config.IP + " " + config.PORT + " "
				+ result.getHops() + " " + result.getTimestamp();
		for (String m : result.getMovies()) {
			msg += " " + m;
		}
		sender(msg, new Node(result.getOrginNode().getIP_address(), result.getOrginNode().getPort_no()));
		return true;
	}
	
	private boolean checkQueryList(SearchQuery query){
		for ( SearchQuery q: searchQueryList ){
			if(q.getQueryText().equals(query.getQueryText())){
				return true;
			}
		}
		return false;
	}

	synchronized private void search(SearchQuery query) {

		if (this.checkQueryList(query)) {
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
		result.setOrginNode(query.getOriginNode());
		result.setMovies(results);
		result.setHops(query.getHops());
		result.setTimestamp(query.getTimestamp());

		neighbours.stream().filter(peer -> !peer.equals(sender)).forEach(peer -> {
			searchRequest(peer, query);
		});
		logger.info("Result sent to " + query.getOriginNode());
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

	// will be invoked when a response is received
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
				String status = "Active";
				String timeStamp = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new Date());

				Node node = new Node(ip, port, status, timeStamp);
				String msg = config.JOIN + " " + config.IP + " " + config.PORT;
				sender(msg, node);
				addNeighbour(node);
				break;

			case 2:

				for (int i = 0; i < no_nodes; i++) {
					String host = tokenizer.nextToken();
					String hostport = tokenizer.nextToken();
					Node temp = new Node(host, Integer.parseInt(hostport));					
					String pingMsg = config.PING + " " + config.IP + " " + config.PORT;
					sender(pingMsg,temp);
				}
				logger.info("registration is successful, 2 nodes' contacts are returned");
				break;
			case 9996:
				logger.info("Failed to register. BootstrapServer is full.");
				break;

			case 9997:
				logger.info("Failed to register. This ip and port is already used by another App.");
				// closeSocket();
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
			String status = "Active";
			String timeStamp = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new Date());
			Node node = new Node(ip, port);
			node.setStatus(status);
			node.setUpdateTime(timeStamp);
		
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
			
		} else if (config.LEAVE.equals(command)) {
			String ip = tokenizer.nextToken();
			int port = Integer.parseInt(tokenizer.nextToken());
			Node node = new Node(ip, port);
			this.leave_network(node);

		} else if (config.LEAVEOK.equals(command)) {
			int result = Integer.parseInt(tokenizer.nextToken());
			switch (result) {
			case 0:
				logger.info("Leaving network successful");
				break;

			case 9999:
				logger.error("Error while leaving the network");
				break;
			}

		} else if (config.SER.equals(command)) {
			String ip = tokenizer.nextToken();
			int port = Integer.parseInt(tokenizer.nextToken());
			String query = tokenizer.nextToken();
			int hops = Integer.parseInt(tokenizer.nextToken());
			long timestamp 	= Long.parseLong(tokenizer.nextToken());
			System.out.println("here come serch");
			System.out.println(timestamp);

			search(new SearchQuery(new Node(ip, port), query, hops, timestamp));

		} else if (config.SEROK.equals(command)) {
			int no_files = Integer.parseInt(tokenizer.nextToken());
			String ip = tokenizer.nextToken();
			int port = Integer.parseInt(tokenizer.nextToken());
			int hops = Integer.parseInt(tokenizer.nextToken());
			long timestamp = Long.parseLong(tokenizer.nextToken());
			long latency = (System.currentTimeMillis() -timestamp);


            latencyArray.add((int) latency);
            hopArray.add(hops);

			List<String> movies = new ArrayList<>();

			for (int i = 0; i < no_files; i++)
				movies.add(tokenizer.nextToken());

			SearchResult result = new SearchResult(new Node(ip, port), movies, hops);
			int moviesCount = no_files;
			result.setMoviesCount(moviesCount);
			if(moviesCount >0){
				this.searchResultList.add(result);
				
				logger.info(" Result : " + ++noOfLocalResults + "  [ Query = " + localQuerry + "]");
				this.printSearchResults();
			}
//			String output = String.format("Number of movies: %d\nMovies: %s\nHops: %d\nSender %s:%d\n", moviesCount,
//					result.getMovies().toString(), result.getHops(), result.getOrginNode().getIP_address(),
//					result.getOrginNode().getPort_no());
//			UpdateTheCMD(output);

		} else if (config.PING.equals(command)) {
			String host = tokenizer.nextToken();
			String hostport = tokenizer.nextToken();
			Node temp = new Node(host, Integer.parseInt(hostport));					
			String pingMsg = config.PINGOK + " " + config.IP + " " + config.PORT;
			sender(pingMsg,temp);
			
		}else if (config.PINGOK.equals(command)) {
			String host = tokenizer.nextToken();
			String hostport = tokenizer.nextToken();
			String hoststatus = "Active";
			String hosttimeStamp = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new Date());
			Node temp = new Node(host, Integer.parseInt(hostport), hoststatus, hosttimeStamp);
			String joinMsg = config.JOIN + " " + config.IP + " " + config.PORT;
			sender(joinMsg, temp);
			addNeighbour(temp);
			
		}
		else {
			unAnsweredMessages++;
		}

	}

	private void addNeighbour(Node node) {
		if (!neighbours.contains(node)) {
			neighbours.add(node);
		}
	}
	
	private void removeNeighbour(Node node) {
		for(Node neighbour : neighbours){
			if (neighbour.getIP_address().equals(node.getIP_address()) && (neighbour.getPort_no() == node.getPort_no())){
				neighbours.remove(neighbour);
				break;
			}
		}
	}
	
	public void clearSearchResults(){
		this.searchResultList = new ArrayList<SearchResult>();
	}

	public void printNeighbors() {
		String msg = "\n***********************\nNeighbous\n***********************\n";
		printOnCMD(msg);
		neighbours.forEach((a) -> printOnCMD(a.getIP_address() + ": " + a.getPort_no() + "\n"));
		printOnCMD("***********************\n");
	}
	
	public void printSearchResults() {
		printOnCMD("\n***********************\nSearch Results\n***********************\n");
		printOnCMD("Origin" + "\t\t" +"Hops" + "\t"+"MovieCount" + "\t"+ "Movies" +"\n");
		searchResultList.forEach((a) -> printOnCMD(a.getOrginNode().getIP_address()+":"+a.getOrginNode().getPort_no() + "\t"+ a.getHops()+"\t" +a.getMoviesCount()+ "\t" +a.getMovies().toString() + "\n"));
		printOnCMD("***********************\n");
	}

	public void routingTable() {
		ArrayList<String> headers = new ArrayList<String>();
		headers.add("IPADDRESS");
		headers.add("PORT");
		headers.add("LASTUPDATE");
		headers.add("STATUS");

		ArrayList<ArrayList<String>> content = new ArrayList<ArrayList<String>>();
		for (Node node : neighbours) {
			ArrayList<String> row1 = new ArrayList<String>();
			row1.add(node.getIP_address());
			row1.add(Integer.toString(node.getPort_no()));
			try {
				String timeStamp = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new Date());
				SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
				System.out.println(timeStamp);
				Date d1 = format.parse(node.getUpdateTime());
				Date d2 = format.parse(timeStamp);
				long diff = d2.getTime() - d1.getTime();
				long diffSeconds = diff / 1000 % 60;
				long diffMinutes = diff / (60 * 1000) % 60;
				long diffHours = diff / (60 * 60 * 1000) % 24;
				long diffDays = diff / (24 * 60 * 60 * 1000);
				row1.add(diffMinutes + " Min " + diffSeconds + " Sec");
			} catch (Exception e) {
				e.printStackTrace();
			}

			row1.add(node.getStatus());
			content.add(row1);
		}

		ConsoleTable ct = new ConsoleTable(headers, content);
		printOnCMD("\n" + ct.printTable() + "\n");
	}
	public Statistics getStatistics() {
		Statistics stat = new Statistics();
		stat.setAnsweredMessages(receivedMessages- unAnsweredMessages);
        stat.setSentMessages(sentMessages);
        stat.setReceivedMessages(receivedMessages);
        stat.setNodeDegree(neighbours.size());
        if(latencyArray.size()>0){
            double avg = latencyArray.stream().mapToLong(val -> val).average().getAsDouble();
            stat.setLatencyMax(Collections.max(latencyArray));
            stat.setLatencyMin(Collections.min(latencyArray));
            stat.setLatencyAverage(avg);
            stat.setLatencySD(Utils.getStandardDeviation(latencyArray.toArray(), avg));
            stat.setNumberOfLatencies(latencyArray.size());

            avg = hopArray.stream().mapToLong(val -> val).average().getAsDouble();
            stat.setHopsMax(Collections.max(hopArray));
            stat.setHopsMin(Collections.min(hopArray));
            stat.setHopsAverage(avg);
            stat.setHopsSD(Utils.getStandardDeviation(hopArray.toArray(), avg));
            stat.setNumberOfHope(hopArray.size());

        }
        return stat;
	}

	public void printStatistics(Statistics stat) {
		String msg = "\n***********************\nStatistics\n***********************\n";
		printOnCMD(msg);
		printOnCMD(stat.toString());
		printOnCMD("***********************\n");

		
	}
	
    public void clearStats(){
        receivedMessages=0;
        sentMessages= 0;
        unAnsweredMessages = 0;
        latencyArray= new ArrayList<>();
        hopArray = new ArrayList<>();
        UpdateTheCMD("Statistics are cleared. ");
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
