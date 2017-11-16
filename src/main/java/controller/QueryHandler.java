package main.java.controller;
import javax.sound.midi.Soundbank;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.ExecutionException;

import main.java.model.Node;
import main.java.model.Statistics;
import main.java.model.config;

public class QueryHandler {
	
	private DatagramSocket socket;
    List<Statistics> statList = new ArrayList<>();
    
    public void addtoist(String stat){
    	statList.add(new Statistics(stat));
    }
    
    public  List<Statistics> getStatList(){
    	return statList;
    }

    public void getSummery() throws IOException{
		int option = 0;
        String reply = null;
        int query_position = 0;
        int node_position = 0;
        int fileNumber = 0;

        int max_rec = 0;
        int max_ans = 0;
        int max_sent = 0;
        int max_ndgree = 0;

        int min_rec = 0;
        int min_ans = 0;
        int min_sent = 0;
        int min_ndgree = 0;

        double avg_rec = 0;
        double avg_sent = 0;
        double avg_ans = 0;
        double avg_ndgree = 0;

        double sd_rec = 0;
        double sd_ans = 0;
        double sd_sent = 0;
        double sd_ndgree = 0;

        int hop_min = 9999999;
        int hop_max = 0;
        double hop_sd = 0;
        double hop_average = 0;
        double latency_min = 9999999;
        double latency_max = 0;
        double latency_sd = 0;
        double latency_average = 0;
        

        List<Integer> received_stat = new ArrayList<>();
        List<Integer> answered_stat = new ArrayList<>();
        List<Integer> sent_stat = new ArrayList<>();
        List<Integer> node_degree = new ArrayList<>();
		List<Statistics> statlist = getStatList();
		String hops = "";
        String latencies = "";
		for(Statistics stat :statlist){
			System.out.println(stat.toString());
			hops += stat.getHops();
            latencies += stat.getLatencies();
            received_stat.add(stat.getReceivedMessages());
            sent_stat.add(stat.getSentMessages());
            answered_stat.add(stat.getAnsweredMessages());
            node_degree.add(stat.getNodeDegree());

            if (stat.getLatencyMax() > 0) {
                if (hop_min > stat.getHopsMin()) {
                    hop_min = stat.getHopsMin();
                }
                if (hop_max < stat.getHopsMax()) {
                    hop_max = stat.getHopsMax();
                }
                if (latency_min > stat.getLatencyMin()) {
                    latency_min = stat.getLatencyMin();
                }
                if (latency_max < stat.getLatencyMax()) {
                    latency_max = stat.getLatencyMax();
                }

            }     
		}
		max_rec = Collections.max(received_stat);
        min_rec = Collections.min(received_stat);

        max_ans = Collections.max(answered_stat);
        min_ans = Collections.min(answered_stat);

        max_sent = Collections.max(sent_stat);
        min_sent = Collections.min(sent_stat);

        max_ndgree = Collections.max(node_degree);
        min_ndgree = Collections.min(node_degree);

        avg_rec = calculateAverage(received_stat);
        avg_sent = calculateAverage(sent_stat);
        avg_ans = calculateAverage(answered_stat);
        avg_ndgree = calculateAverage(node_degree);

        sd_rec = getSD(received_stat.toArray(), QueryHandler.calculateAverage(received_stat));
        sd_ans = getSD(answered_stat.toArray(), QueryHandler.calculateAverage(answered_stat));
        sd_sent = getSD(sent_stat.toArray(), QueryHandler.calculateAverage(sent_stat));
        sd_ndgree = getSD(node_degree.toArray(), avg_ndgree);
		StdCalculator stdgen = new StdCalculator(statlist);
        hop_average = stdgen.getSampleMeanOfHops();
        hop_sd = stdgen.calculateStdDevOfHops();
        latency_average = stdgen.getSampleMeanOfLatencies();
        latency_sd = stdgen.calculateStdDevOfLatencies();

        System.out.println("hop_average" + hop_average);
        System.out.println("hop sd" + hop_sd);
        System.out.println("latency average" + latency_average);
        System.out.println("latancy std" + latency_sd);
        
        File file = new File("stat-summary.csv");
        FileWriter fw = new FileWriter(file);
        fw.write(" ,received,answered,sent,node degree,latency,hops\n");
        fw.write("Min ," + min_rec + "," + min_ans + "," + min_sent + "," + min_ndgree + "," + latency_min + "," + hop_min + "\n");
        fw.write("Max ," + max_rec + "," + max_ans + "," + max_sent + "," + max_ndgree + "," + latency_max + "," + hop_max + "\n");
        fw.write("Avg ," + avg_rec + "," + avg_ans + "," + avg_sent + "," + avg_ndgree + "," + latency_average + "," + hop_average + "\n");
        fw.write("SD ," + sd_rec + "," + sd_ans + "," + sd_sent + "," + sd_ndgree + "," + latency_sd + "," + hop_sd);
        fw.flush();
        fw.close();
        file = new File("stat-hops" + fileNumber + ".csv");
        fw = new FileWriter(file);
        fw.write(hops.replace(",,", ",").replace(",", "\n"));
        fw.flush();
        fw.close();

        file = new File("stat-latency" + fileNumber + ".csv");
        fw = new FileWriter(file);
        fw.write(latencies.replace(",,", ",").replace(",", "\n"));
        fw.flush();
        fw.close();


        file = new File("stats-all" + fileNumber + ".csv");
        fw = new FileWriter(file);
        String fout = "sent,received,answered,node degree\n";
        for (int y = 0; y < received_stat.size(); y++) {
            fout += sent_stat.get(y) + "," + received_stat.get(y) + "," + answered_stat.get(y) + "," + node_degree.get(y) + "\n";
        }
        fw.write(fout);
        fw.flush();
        fw.close();
	}

    public static double getSD(Object[] latency, double mean) {
        double variance = 0, sd = 0;
        double[] temp = new double[latency.length];
        for (int i = 0; i < latency.length; i++) {
            temp[i] = (double) (Integer) latency[i] - mean;
            temp[i] = Math.pow(temp[i], 2.0); //to get the (x-average)……2
            variance += temp[i];
        }
        variance = variance / (latency.length - 1); // sample variance
        sd = Math.sqrt(variance);
        return sd;
    }

    public static double calculateAverage(List<Integer> marks) {
        Integer sum = 0;
        if (!marks.isEmpty()) {
            for (Integer mark : marks) {
                sum += mark;
            }
            return sum.doubleValue() / marks.size();
        }
        return sum;
    }
}
