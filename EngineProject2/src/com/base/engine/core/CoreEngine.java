package com.base.engine.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import com.base.engine.data.Resources;
import com.base.engine.rendering.GLFWWindow;

public class CoreEngine implements Engine {
	private boolean running = false;
	private int framesPerSecond = 60;
	public static GLFWWindow window;
	private Engine engines[];
	
	private static final String config = "./config/config.cfg";
	public CoreEngine(Engine engines[]) {this.engines = engines;}
	
	public void start() {
		int width = -1;
		int height = -1;
		try { 
			Scanner k = new Scanner(new File(config));
			while(k.hasNextLine()){
				String line = k.nextLine().trim().replaceAll("\\s","");
				String[] splits = line.split("=");
				if(splits[0].isEmpty() || splits[1].isEmpty()) continue;
				switch(splits[0]) {
				case "Window_Width": width = Integer.parseInt(splits[1]); continue;
				case "Window_Height": height = Integer.parseInt(splits[1]); continue;
				case "FPS":
				case "FramesPerSecond": framesPerSecond = Integer.parseInt(splits[1]); continue;
				}
			}
			k.close();
		} catch (FileNotFoundException e) {System.out.println("Config not found!");}
		
		if(width < 0) width = 800; if(height < 0) height = 600;
		window = new GLFWWindow("test",width,height);
		
		for(int i = 0; i < engines.length; i++) engines[i].start();
	}
	
	public void run() {
		running = true;
		int frames = 0;
		float frameTime = 1.0f / framesPerSecond;
		float delta, unprocessedTime = 0f, frameCounter = 0f;;
		//glfwFreeCallbacks(window);
		float current, last = Time.getTime();
		boolean render;
		while(running && !window.shouldClose()) {
			render = false;
			current = Time.getTime();
			delta = current - last;
			last = current;
			unprocessedTime += delta;
			frameCounter += delta;
			while(unprocessedTime >= frameTime) {
				render = true;
				unprocessedTime -= frameTime;
				if(frameCounter >= 1.0) {
					//System.out.println(frames);
					frames = 0;
					frameCounter = 0;
				}	
			}
			if(render) {
				Time.updateDelta();
				GLFWWindow.pollEvents();
				
				for(int i = 0; i < engines.length; i++) engines[i].run();
				frames++;}
			else{try{Thread.sleep(1);}catch(InterruptedException e){e.printStackTrace();}}
		}
		this.dispose();
	}
	
	public void dispose() {
		for(int i = 0; i < engines.length; i++) engines[i].dispose();
		window.dispose();
		Resources.dispose();
	}
}
