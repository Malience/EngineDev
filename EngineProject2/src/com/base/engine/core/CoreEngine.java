package com.base.engine.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import com.base.engine.input.GLFWInputEngine;
import com.base.engine.input.InputContext;
import com.base.engine.networking.NetworkingEngine;
import com.base.engine.physics.PhysicsEngine;
import com.base.engine.physics.PhysicsEngineNew;
import com.base.engine.rendering.GLFWWindow;
import com.base.engine.rendering.opengl.GLFWRenderingEngine;
import com.base.engine.rendering.opengl.GLFWRenderingEngine2;
import com.base.engine.vr.GLFWRenderingEngine3;
import com.base.engine.vr.VR2;

public class CoreEngine implements Engine{
	boolean running = false;
	int framesPerSecond = 60;
	public static GLFWWindow window;
	public static Engine renderingEngine;
	public static Engine inputEngine;
	public static NetworkingEngine networkEngine;
	public static Engine physicsEngine;
	public static Player[] players;
	public static Player player;
	
	private static final String config = "./config/config.cfg";
	
	
	public CoreEngine(Engine renderingEngine, Engine inputEngine, Engine physicsEngine) {
		CoreEngine.renderingEngine = renderingEngine;
		CoreEngine.inputEngine = inputEngine;
		CoreEngine.physicsEngine = physicsEngine;
	}
	
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
		
		players = new Player[20];
		
		networkEngine = new NetworkingEngine();
		
		networkEngine.start();
		
		renderingEngine.start();
		
		player = new Player(networkEngine.getColor());
		networkEngine.setPlayer(player);
		
		//physicsEngine = new PhysicsEngine();
		physicsEngine.start();
		
		//physicsEngine.terrain = renderingEngine.terrain;
		//physicsEngine.physicsActivated = CoreEngine.player.transform.pos;
		
		inputEngine.start();
		
		running = true;
		this.run();
		this.dispose();
	}
	
	public void run() {
		Time.init();
		int frames = 0;
		float frameTime = 1.0f / framesPerSecond;
		float delta;
		float unprocessedTime = 0f;
		float frameCounter = 0f;
		//glfwFreeCallbacks(window);
		
		while(running && !window.shouldClose()) {
			//frame start
			
			boolean render = false;
			
			delta = Time.updateDelta();
			
			unprocessedTime += delta;
			frameCounter += delta;
			
			//Poll window events
			GLFWWindow.pollEvents();
			
			//Input
			inputEngine.run();
			
			physicsEngine.run();
			
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
				
				//networkEngine.run();
				renderingEngine.run();
				frames++;}
			else{try{Thread.sleep(1);}catch(InterruptedException e){e.printStackTrace();}}
		}
	}
	
	public void dispose() {
		networkEngine.dispose();
		window.dispose();
		//VR2.stop();
	}
	
	public static void main(String [] args) {
		CoreEngine engine = new CoreEngine(new GLFWRenderingEngine2(), new GLFWInputEngine(), new PhysicsEngine());
		engine.start();
	}
	
	static float moveSpeed = 15.0f;
	
	public static void movePlayerForward(){
		player.transform.translate(0, 0, -moveSpeed * Time.getDelta());
	}
	
	public static void movePlayerBackward(){
		player.transform.translate(0, 0, moveSpeed * Time.getDelta());
	}
	
	public static void movePlayerLeft(){
		player.transform.translate(-moveSpeed * Time.getDelta(), 0, 0);
	}
	
	public static void movePlayerRight(){
		player.transform.translate(moveSpeed * Time.getDelta(), 0, 0);
	}
}
