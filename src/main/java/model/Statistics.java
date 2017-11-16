package main.java.model;

/**
 * Created by Charuni on 5/11/2017.
 */
public class Statistics {
	private int receivedMessages;
    private int sentMessages;
    private int answeredMessages;
    private int nodeDegree;
    private int latencyMin=0;
    private int latencyMax=0;
    private double latencySD=0;
    private double latencyAverage=0;
    private int hopsMin=0;
    private int hopsMax=0;
    private double hopsSD=0;
    private double hopsAverage=0;
    private int numberOfHope=0;
    private int numberOfLatencies =0;
    private String sep = "#";
    private String hops=",";
    private String latencies =",";
    
    public Statistics() {

    }
    
    public Statistics(String encodedStat){
        String [] str = encodedStat.split(sep);
        receivedMessages = Integer.parseInt(str[0]);
        sentMessages = Integer.parseInt(str[1]);
        answeredMessages = Integer.parseInt(str[2]);
        latencyMin = Integer.parseInt(str[3]);
        latencyMax = Integer.parseInt(str[4]);
        latencySD = Double.parseDouble(str[5]);
        latencyAverage = Double.parseDouble(str[6]);
        numberOfLatencies = Integer.parseInt(str[7]);
        hopsMin = Integer.parseInt(str[8]);
        hopsMax = Integer.parseInt(str[9]);
        hopsSD = Double.parseDouble(str[10]);
        hopsAverage = Double.parseDouble(str[11]);
        nodeDegree = Integer.parseInt(str[12]);
        numberOfHope = Integer.parseInt(str[13]);
        hops=str[14];
        latencies=str[15];
    }
    
    public int getReceivedMessages() {
        return receivedMessages;
    }

    public void setReceivedMessages(int receivedMessages) {
        this.receivedMessages = receivedMessages;
    }

    public int getSentMessages() {
        return sentMessages;
    }

    public void setSentMessages(int sentMessages) {
        this.sentMessages = sentMessages;
    }

    public int getAnsweredMessages() {
        return answeredMessages;
    }

    public void setAnsweredMessages(int answeredMessages) {
        this.answeredMessages = answeredMessages;
    }
    
    public int getNodeDegree() {
        return nodeDegree;
    }

    public void setNodeDegree(int nodeDegree) {
        this.nodeDegree = nodeDegree;
    }

    public int getLatencyMin() {
        return latencyMin;
    }

    public void setLatencyMin(int latencyMin) {
        this.latencyMin = latencyMin;
    }

    public int getLatencyMax() {
        return latencyMax;
    }

    public void setLatencyMax(int latencyMax) {
        this.latencyMax = latencyMax;
    }
    

    public double getLatencyAverage() {
        return latencyAverage;
    }

    public void setLatencyAverage(double latencyAverage) {
    	 this.latencyAverage = latencyAverage;
    }

    public double getLatencySD() {
        return latencySD;
    }

    public void setLatencySD(double latencySD) {
        this.latencySD = latencySD;
    }

    public int getNumberOfLatencies() {
        return numberOfLatencies;
    }

    public void setNumberOfLatencies(int numberOfLatencies) {
        this.numberOfLatencies = numberOfLatencies;
    }
    
    public int getNumberOfHope() {
        return numberOfHope;
    }

    public void setNumberOfHope(int numberOfHope) {
        this.numberOfHope = numberOfHope;
    }

    public int getHopsMin() {
        return hopsMin;
    }

    public void setHopsMin(int hopsMin) {
        this.hopsMin = hopsMin;
    }

    public int getHopsMax() {
        return hopsMax;
    }

    public void setHopsMax(int hopsMax) {
        this.hopsMax = hopsMax;
    }  

    public double getHopsAverage() {
        return hopsAverage;
    }

    public void setHopsAverage(double hopsAverage) {
        this.hopsAverage = hopsAverage;
    }

    public double getHopsSD() {
        return hopsSD;
    }

    public void setHopsSD(double hopsSD) {
        this.hopsSD = hopsSD;
    }
    
    public String getHops() {
        return hops;
    }

    public void setHops(String hops) {
        this.hops = hops;
    }

    public String getLatencies() {
        return latencies;
    }

    public void setLatencies(String latencies) {
        this.latencies = latencies;
    }
    
    public String toString() {
    	StringBuilder sb = new StringBuilder();
        sb.append("Forwarded Messages\t:"+sentMessages+"\n");
        sb.append("Received Messages\t:"+receivedMessages+"\n");
        sb.append("Answered Messages\t:"+answeredMessages+"\n");
        sb.append("Node degree\t\t:"+nodeDegree+"\n\n");
        sb.append("No of Latencies\t:"+numberOfLatencies+"\n");
        sb.append("Latency Min\t\t:"+latencyMin+"\n");
        sb.append("Latency Max\t\t:"+latencyMax+"\n");
        sb.append("Latency Average\t:"+latencyAverage+"\n");
        sb.append("Latency SD\t\t:"+latencySD+"\n\n");
        sb.append("No of hops\t\t:"+numberOfHope+"\n");
        sb.append("Hops Min\t\t:"+hopsMin+"\n");
        sb.append("Hops Max\t\t:"+hopsMax+"\n");
        sb.append("Hops Average\t:"+hopsAverage+"\n");
        sb.append("Hops SD\t\t:"+hopsSD+"\n");
    	return sb.toString();
    	
    }
    
    public String toEncoded(){
        return receivedMessages+sep+
                sentMessages+sep+
                answeredMessages+sep
                +latencyMin+sep+
                latencyMax+sep+
                latencySD+sep+
                latencyAverage+sep+
                numberOfLatencies+sep
                +hopsMin+sep+
                hopsMax+sep+
                hopsSD+sep+
                hopsAverage+sep+
                nodeDegree+sep+
                numberOfHope+sep+
                hops+sep+
                latencies;
    }
    

}