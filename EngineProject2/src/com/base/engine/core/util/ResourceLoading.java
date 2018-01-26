package com.base.engine.core.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Scanner;

public class ResourceLoading {
	private static final String MODEL_LOCATION = "/models/";
	
	private static HashMap<String, FileType> filetypes = new HashMap<String, FileType>();
	
	public void load(String filename) {
		String[] splits = filename.split(".");
		if(splits.length < 2) {System.err.println("Invalid Filename: " + filename); return;}
		filetypes.get(splits[1]).run(filename);
	}
	
	static {
		filetypes.put("obj", ResourceLoading::OBJ);
	}
	
	private static void OBJ(String filename) {
		try {
			Scanner k = null;
			File file = new File("./res" + MODEL_LOCATION + filename);
			InputStream in = null;
			if(file.exists()) k = new Scanner(file);
			else {
				in= Class.class.getResourceAsStream(MODEL_LOCATION + filename);
				k = new Scanner(in);
			}
			
			String s;
			while(k.hasNextLine()) {
				s = k.next(" ");
				switch(s) {
				case "v":
					break;
				case "f":
					
				}
			}
			
			
			try {
				if(!file.exists()) in.close();
				k.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@FunctionalInterface
	private interface FileType{void run(String filename);}
}
