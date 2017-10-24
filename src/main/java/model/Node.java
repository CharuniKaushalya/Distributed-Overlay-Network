package main.java.model;

/**
 * Created by Thilini on 10/21/2017.
 */
public class Node {
    private String IP_address;
    private int port_no;
    private String username;

    public Node() {

    }

    public Node(String IP_address, int port_no) {
        this.IP_address = IP_address;
        this.port_no = port_no;
    }

    public Node(String IP_address, int port_no, String username) {
        this.IP_address = IP_address;
        this.port_no = port_no;
        this.username = username;
    }

    public String getIP_address() {
        return IP_address;
    }

    public void setIP_address(String IP_address) {
        this.IP_address = IP_address;
    }

    public int getPort_no() {
        return port_no;
    }

    public void setPort_no(int port_no) {
        this.port_no = port_no;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}