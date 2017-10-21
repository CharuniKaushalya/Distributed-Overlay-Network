/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.java;

/**
 *
 * @author Charuni
 */
public class config {
	// Current Node Details
	static String IP = "127.0.0.1";
	static int PORT = 5007;
	static String USERNAME = "Test";
	static int id = 0;

	// Bootstrap Server Details
	static String BOOTSTRAP_IP = "127.0.0.1";
	static int BOOTSTRAP_PORT = 55555;

	static int noOfNodes = 1;
	
	//command
	public static final String REG = "REG";
    public static final String UNREG = "UNREG";
    public static final String ECHO = "ECHO";
    public static final String REGOK = "REGOK";
    public static final String UNROK = "UNROK";
    public static final String JOIN = "JOIN";
    public static final String JOINOK = "JOINOK";
    public static final String LEAVE = "LEAVE";
    public static final String LEAVEOK = "LEAVEOK";
    public static final String DISCON = "DISCON";
    public static final String DISOK = "DISOK";
    public static final String SER = "SER";
    public static final String SEROK = "SEROK";
    public static final String ERROR = "ERROR";
    public static final String STAT = "STAT";
    public static final String STATOK = "STATOK";
    public static final String QUERY = "QUERY";
    public static final String CLEAR = "CLEAR";

}
