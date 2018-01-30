package com.base.engine.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;

import com.base.engine.input.GLFWInputEngine;
import com.base.engine.input.InputContext;
import com.base.engine.input.InputMapping;
import com.base.engine.input.InputMapping.ActionPair;
import com.base.engine.input.InputMapping.RangePair;
import com.base.engine.networking.NetworkingEngine;
import com.base.engine.physics.PhysicsEngine;
import com.base.engine.physics.PhysicsEngineNew;
import com.base.engine.rendering.Camera;
import com.base.engine.rendering.GLFWWindow;
import com.base.engine.rendering.opengl.GLFWRenderingEngine;
import com.base.engine.rendering.opengl.GLFWRenderingEngine2;
import com.base.engine.vr.GLFWRenderingEngine3;
import com.base.engine.vr.VR2;

public class CoreEngine implements Engine {
	private boolean running = false;
	private int framesPerSecond = 60;
	public static GLFWWindow window;
	private static Engine engines[];
	
	private static final String config = "./config/config.cfg";
	
	
	public CoreEngine(Engine engines[]) {this.engines = engines;}
	
	public void start() {
		int width = -1;
		int height = -1;
		try { 
			Scanner k = new Scanner(new File(config));
			ArrayList<String> bindings = new ArrayList<String>();
			while(k.hasNextLine()){
				String line = k.nextLine().trim().replaceAll("\\s","");
				String[] splits = line.split("=");
				if(splits[0].isEmpty() || splits[1].isEmpty()) continue;
				switch(splits[0]) {
				case "Window_Width": width = Integer.parseInt(splits[1]); continue;
				case "Window_Height": height = Integer.parseInt(splits[1]); continue;	
				}
			}
			k.close();
		} catch (FileNotFoundException e) {System.out.println("Config not found!");}
		
		if(width < 0) width = 800;
		if(height < 0) height = 600;
		window = new GLFWWindow("test",width,height);
		
		for(int i = 0; i < engines.length; i++) {
			engines[i].start();
		}
		
	}
	
	public void run() {
		running = true;
		int frames = 0;
		float frameTime = 1.0f / framesPerSecond;
		float delta, unprocessedTime = 0f, frameCounter = 0f;;
		//glfwFreeCallbacks(window);
		float current, last = Time.getTime();
		
		while(running && !window.shouldClose()) {
			//frame start
			
			boolean render = false;
			
			current = Time.getTime();
			delta = current - last;
			last = current;
			
			unprocessedTime += delta;
			frameCounter += delta;
			
			//frame end
			while(unprocessedTime >= frameTime)
			{
				render = true;
				
				unprocessedTime -= frameTime;
				
				
				//Network
				
				
				
				if(frameCounter >= 1.0)
				{
					//System.out.println(frames);
					frames = 0;
					frameCounter = 0;
				}	
			}
			if(render) {
				Time.updateDelta();
				
				
				//Poll window events
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
	}
}
