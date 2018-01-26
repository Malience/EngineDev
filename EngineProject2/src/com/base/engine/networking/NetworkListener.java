package com.base.engine.networking;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;

import math.Vector3f;


public class NetworkListener extends Thread{
	volatile NetworkingEngine engine;
	DatagramSocket server;
	byte[] bytes;
	DatagramPacket data;
	ByteBuffer converter;
	static final int PACKET_SIZE = 13;
	static final int timeout = 1000;
	public static Vector3f[] networkpos;
	public static boolean run = true;
	private static boolean[] hasChanged;
	
	public NetworkListener(int size, DatagramSocket server, NetworkingEngine engine){
		this.engine = engine;
		networkpos = new Vector3f[size];
		hasChanged = new boolean[size];
		bytes = new byte[PACKET_SIZE];
		converter = ByteBuffer.wrap(bytes);
		data = new DatagramPacket(bytes, PACKET_SIZE);
		this.server = server;
		try {
			server.setSoTimeout(timeout);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void run(){
		try{
		while(run){
			try{
				server.receive(data);
				int index = bytes[12];
				if(networkpos[index] == null){
					float x = converter.getFloat(0);
					float y = converter.getFloat(4);
					float z = converter.getFloat(8);
					networkpos[index] = new Vector3f(x,y,z);
					hasChanged[index] = true;
					engine.instantiatePlayer(index);
				} else {
					networkpos[index].x = converter.getFloat(0);
					networkpos[index].y = converter.getFloat(4);
					networkpos[index].z = converter.getFloat(8);
					hasChanged[index] = true;
				}
			} catch(SocketTimeoutException e){}
		}
		} catch(IOException e){
			e.printStackTrace();
			dispose();
		}
	}
	
	public boolean getVector(int id, Vector3f out){
		if(!hasChanged[id] || networkpos[id] == null) return false;
		out.x = networkpos[id].x;
		out.y = networkpos[id].y;
		out.z = networkpos[id].z;
		hasChanged[id] = false;
		return true;
		//System.out.println(out.toString());
	}
	
	public boolean check(int id){
		//System.out.println(networkpos[id] != null);
		return networkpos[id] != null;
	}
	
	//public boolean isEmpty(){return read == write;}
	
	public void dispose(){
		run = false;
		//Util.free(converter);
	}
}
