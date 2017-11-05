package main.java.model;

/**
 * Created by Thilini on 10/21/2017.
 */
public class Node {
    private String IP_address;
    private int port_no;
    private String username;
    private String status;
    private String update_time;
    public Node() {

    }

    public Node(String IP_address, int port_no) {
        this.IP_address = IP_address;
        this.port_no = port_no;
    }
    
    public Node(String IP_address, int port_no, String status, String update_time) {
        this.IP_address = IP_address;
        this.port_no = port_no;
        this.update_time = update_time;
        this.status = status;
        
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
    
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getUpdateTime() {
        return update_time;
    }

    public void setUpdateTime(String update_time) {
        this.update_time = update_time;
    }
}
