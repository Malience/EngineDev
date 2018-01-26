package com.base.engine.core;

import com.base.engine.input.GLFWInputEngine;
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
	public static GLFWRenderingEngine2 renderingEngine;
	public static GLFWInputEngine input;
	public static NetworkingEngine networkEngine;
	public static PhysicsEngine physicsEngine;
	public static Player[] players;
	public static Player player;
	
	
	public void start() {
		window = new GLFWWindow("test",800,600);
		
		players = new Player[20];
		
		networkEngine = new NetworkingEngine();
		networkEngine.start();
		
		renderingEngine = new GLFWRenderingEngine2();
		renderingEngine.start();
		
		player = new Player(networkEngine.getColor());
		networkEngine.setPlayer(player);
		renderingEngine.camera.translate(player.transform.pos.x, player.transform.pos.y + 5, player.transform.pos.z + 5);
		renderingEngine.camera.lookAt(player.transform.pos);
		
		physicsEngine = new PhysicsEngine();
		
		//physicsEngine.terrain = renderingEngine.terrain;
		//physicsEngine.physicsActivated = CoreEngine.player.transform.pos;
		
		input = new GLFWInputEngine();
		input.start();
		
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
			input.run();
			
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
		CoreEngine engine = new CoreEngine();
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
