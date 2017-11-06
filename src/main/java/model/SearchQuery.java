package main.java.model;

/**
 * Created by Thilini on 10/24/2017.
 */
public class SearchQuery {

    private Node originNode;
    private Node senderNode;
    private String queryText;
    private int hops;
    private long timestamp;


    public SearchQuery(Node originNode, String queryText, int hops) {
        this.originNode = originNode;
        this.queryText = queryText;
        this.hops = hops;
    }
    
    public SearchQuery(Node originNode, String queryText, int hops, long timestamp) {
        this.originNode = originNode;
        this.queryText = queryText;
        this.hops = hops;
        this.timestamp = timestamp;
    }

    public SearchQuery() {
    }

    public Node getOriginNode() {
        return originNode;
    }

    public void setOriginNode(Node originNode) {
        this.originNode = originNode;
    }

    public Node getSenderNode() {
        return senderNode;
    }

    public void setSenderNode(Node senderNode) {
        this.senderNode = senderNode;
    }

    public String getQueryText() {
        return queryText;
    }

    public void setQueryText(String queryText) {
        this.queryText = queryText;
    }

    public int getHops() {
        return hops;
    }

    public void setHops(int hops) {
        this.hops = hops;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
