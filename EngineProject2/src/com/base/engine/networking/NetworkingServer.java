package com.base.engine.networking;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.Scanner;

public class NetworkingServer {
	byte[] bytes;
	DatagramSocket server;
	DatagramPacket data;
	ByteBuffer buffer;
	private static final String config = "./config/server.cfg";
	static final int PACKET_SIZE = 13;
	private static final int DEFAULT_PORT = 9970, DEFUALT_MAX_PLAYERS = 20;
	private int port = -1, max_players = -1;
	private static boolean run = true;
	static boolean debug = false;
	
	InetAddress[] addresses;
	int[] ports;
	
	int nextPlayer = 0;
	
	public static void main(String [] args){
		NetworkingServer server = new NetworkingServer();
	}
	
	public NetworkingServer(){
		try { 
			Scanner k = new Scanner(new File(config));
			if(debug) System.out.println("Config loading!");
			while(k.hasNextLine()){
				String line = k.nextLine().trim().replaceAll("\\s","");
				String[] splits = line.split("=");
				switch(splits[0]){
				case "port": port = Integer.parseInt(splits[1]); break;
				case "max_players": max_players = Integer.parseInt(splits[1]); break;
				}
			}
			k.close();
			if(debug) System.out.println("Config loaded!");
		} catch (FileNotFoundException e) {System.out.println("Config not found!");}
		
		if(port < 0) port = DEFAULT_PORT;
		if(max_players < 0) max_players = DEFUALT_MAX_PLAYERS;
		
		bytes = new byte[PACKET_SIZE];
		data = new DatagramPacket(bytes, PACKET_SIZE);
		buffer = ByteBuffer.wrap(bytes);
		
		addresses = new InetAddress[max_players];
		ports = new int[max_players];
		System.out.println("Server set up!");
		try {
			server = new DatagramSocket(port);
		} catch (SocketException e) {
			e.printStackTrace();
			dispose();
		}
		System.out.println("Socket connected on port " + DEFAULT_PORT + "!");
		run();
	}
	
	public void run(){
		System.out.println("Server online!");
		try{
		while(run){
			server.receive(data);
			System.out.println("Recieved!");
			if(bytes[12] < 0){
				int connectionID = connect(data.getAddress(), buffer.getInt(0));
				System.out.println("Player " + connectionID + " at " + addresses[connectionID] + ":" + ports[connectionID] + " connected!");
				sendID(connectionID);
			} else {
				if(debug) System.out.println("Position: " + buffer.getFloat(0) + " " + buffer.getFloat(4) + " " + buffer.getFloat(8));
				
				forwardPosition(bytes[12]);
			}
		}
		} catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public void sendID(int id) throws IOException{
		bytes[12] = (byte) id;
		data.setAddress(addresses[id]);
		data.setPort(ports[id]);
		server.send(data);
	}
	
	public void forwardPosition(int id) throws IOException{
		for(int i = 0; i < nextPlayer; i++){
			if(i == id || addresses[i] == null) continue;
			data.setAddress(addresses[i]);
			data.setPort(ports[i]);
			server.send(data);
		}
	}
	
	public int connect(InetAddress address, int port){
		addresses[nextPlayer] = address;
		ports[nextPlayer] = port;
		int out = nextPlayer;
		nextPlayer++;
		return out;
	}
	
	public void clear(){
		nextPlayer = 0;
		addresses = new InetAddress[max_players];
		ports = new int[max_players];
		System.out.println("Server cleared!");
	}
	
	public void dispose(){
		run = false;
	}
}
