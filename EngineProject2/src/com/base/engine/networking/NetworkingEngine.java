package com.base.engine.networking;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Scanner;

import com.base.engine.core.CoreEngine;
import com.base.engine.core.Player;
import com.base.engine.core.Time;
import com.base.engine.core.util.Util;
import math.Vector3f;

public class NetworkingEngine {
	static final int PACKET_SIZE = 13;
	static final int PLAYERS = 20;
	static NetworkListener listener;
	static final String config = "./config/networking.cfg";
	static InetAddress ip;
	static String hostaddress;
	static boolean host;
	static boolean networking = true;
	static int id;
	static int port, hostport;
	static Vector3f[] playerpos;
	static Vector3f[] networkpos;
	static boolean debug = false;
	public static boolean hasChanged = false;
	public static long lastSend;
	public static int sendDelay = 30;
	
	
	public void start(){
		if(!networking) return;
		Scanner k = null;
		try { k = new Scanner(new File(config));
		} catch (FileNotFoundException e) {e.printStackTrace();}
		
		if(debug) System.out.println("Config loading!");
		while(k.hasNextLine()){
			String line = k.nextLine().trim().replaceAll("\\s","");
			String[] splits = line.split("=");
			switch(splits[0]){
			case "host": hostaddress = splits[1]; break;
			case "port": port = Integer.parseInt(splits[1]); break;
			case "hostport": hostport = Integer.parseInt(splits[1]); break;
			case "networking": networking = Boolean.parseBoolean(splits[1]); break;
			case "network_delay": sendDelay = Integer.parseInt(splits[1]); break;
			}
		}
		k.close();
		if(debug) System.out.println("Config loaded!");
		if(!networking) return;
		playerpos = new Vector3f[PLAYERS];
		networkpos = new Vector3f[PLAYERS];
		
		converter = Util.createByteBuffer(PACKET_SIZE);
		bytes = new byte[PACKET_SIZE];
		if(debug) System.out.println("Client set up!");
		
		try { 	server = new DatagramSocket(port);
				if(debug) System.out.println("Socket loaded!");
				send = new DatagramPacket(bytes, PACKET_SIZE, InetAddress.getByName(hostaddress), hostport);
		} catch (UnknownHostException e1) {e1.printStackTrace(); dispose();} 
		catch (SocketException e) {e.printStackTrace(); dispose();}
		
		listener = new NetworkListener(PLAYERS, server, this);
		if(debug) System.out.println("Listener loaded!");
		bytes[12] = -1;
		converter.putInt(port);
		converter.flip();
		bytes[0] = (byte) (port >>> 24);
		bytes[1] = (byte) (port >>> 16);
		bytes[2] = (byte) (port >>> 8);
		bytes[3] = (byte) (port);
		try {
			server.send(send);
			if(debug) System.out.println("ID request sent!");
			DatagramPacket rec = new DatagramPacket(new byte[PACKET_SIZE], PACKET_SIZE);
			server.receive(rec);
			if(debug) System.out.println("ID response recieved!");
			id = rec.getData()[12];
			if(debug) System.out.println("ID: " + id);
		} catch (IOException e) {
			e.printStackTrace();
			dispose();
		}
		listener.start();
		System.out.println("Listener started!");
	}
	
	public void setPlayer(Player player){
		if(networking) {
			playerpos[id] = player.transform.pos;
			player.transform.changeCallback = this::hasChanged;
		}
	}
	
	public void run(){
		if(!networking) return;
		if(hasChanged && playerpos[id] != null && System.currentTimeMillis() - lastSend > sendDelay) {hasChanged = false; send(playerpos[id]); lastSend = System.currentTimeMillis();}
		for(int i = 0; i < playerpos.length; i++){
			if(i == id || playerpos[i] == null) continue;
			if(listener.getVector(i, networkpos[i])){
				playerpos[i].lerp(networkpos[i], Time.getDelta() * 10.0f, playerpos[i]);
				CoreEngine.players[i].transform.applyChanges();
			}
		}
	}
	
	public void instantiatePlayer(int id){
		playerpos[id] = new Vector3f();
		listener.getVector(id, playerpos[id]);
		networkpos[id] = new Vector3f(playerpos[id]);
		CoreEngine.players[id] = new Player(playerpos[id], getColor(id));
		System.out.println("Player " + id + " instantiated!");
		
	}
	
	public void dispose(){
		if(listener != null) listener.dispose();
		Util.free(converter);
		networking = false;
		NetworkListener.run = false;
		if(listener != null)listener.interrupt();
	}
	
	DatagramSocket server;
	byte[] bytes = new byte[PACKET_SIZE];
	ByteBuffer converter;
	private DatagramPacket send;
	
	public void hasChanged(){hasChanged = true;}
	
	public void send(Vector3f pos){
		int x = Float.floatToIntBits(pos.x);
		bytes[3] = (byte)(x & 0xff);
		bytes[2] = (byte)((x >> 8) & 0xff);
		bytes[1] = (byte)((x >> 16) & 0xff);
		bytes[0] = (byte)((x >> 24) & 0xff);
		int y = Float.floatToIntBits(pos.y);
		bytes[7] = (byte)(y & 0xff);
		bytes[6] = (byte)((y >> 8) & 0xff);
		bytes[5] = (byte)((y >> 16) & 0xff);
		bytes[4] = (byte)((y >> 24) & 0xff);
		int z = Float.floatToIntBits(pos.z);
		bytes[11] = (byte)(z & 0xff);
		bytes[10] = (byte)((z >> 8) & 0xff);
		bytes[9] = (byte)((z >> 16) & 0xff);
		bytes[8] = (byte)((z >> 24) & 0xff);
		//for(int i = 0; i < bytes.length - 1; i++) bytes[i] = converter.get(i);
		bytes[12] = (byte) id;
		try {
			server.send(send);
		} catch (IOException e) {
			e.printStackTrace();
			dispose();
		}
	}
	
	public Vector3f getColor(){
		switch(id){
		case 0: return new Vector3f(1,0,0);
		case 1: return new Vector3f(0,1,0);
		case 2: return new Vector3f(0,0,1);
		case 3: return new Vector3f(1,1,0);
		case 4: return new Vector3f(0,1,1);
		case 5: return new Vector3f(1,1,1);
		case 6: return new Vector3f(0,0,0);
		case 7: return new Vector3f(1,0,1);
		}
		return new Vector3f(0,0,0);
	}
	
	public Vector3f getColor(int id){
		switch(id){
		case 0: return new Vector3f(1,0,0);
		case 1: return new Vector3f(0,1,0);
		case 2: return new Vector3f(0,0,1);
		case 3: return new Vector3f(1,1,0);
		case 4: return new Vector3f(0,1,1);
		case 5: return new Vector3f(1,1,1);
		case 6: return new Vector3f(0,0,0);
		case 7: return new Vector3f(1,0,1);
		}
		return new Vector3f(0,0,0);
	}
}
