package com.base.engine.input;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import com.base.engine.core.CoreEngine;

public class GLFWInputEngine extends InputEngine {
	private static final String config = "./config/keybindings.cfg";
	private static boolean joysticks_enabled = false;
	private static boolean debug = false;
	
	InputDevice devices[];
	InputContext context;
	
	public void start() {
		devices = new InputDevice[]{new GLFWMouse(CoreEngine.window), new GLFWKeyboard(CoreEngine.window)};
		if(joysticks_enabled) GLFWJoystick.init();
		InputMapping.init();
		
		try { 
			Scanner k = new Scanner(new File(config));
			if(debug) System.out.println("Config loading!");
			ArrayList<String> bindings = new ArrayList<String>();
			while(k.hasNextLine()){
				String line = k.nextLine().trim().replaceAll("\\s","");
				String[] splits = line.split("=");
				if(splits[0].isEmpty() || splits[1].isEmpty()) continue;
				bindings.add(splits[1]); bindings.add(splits[0]);
			}
			k.close();
			context = new InputContext(bindings);
			if(debug) System.out.println("Config loaded!");
		} catch (FileNotFoundException e) {System.out.println("Config not found!");}
		
		if(context == null) context = new InputContext();
	}
	
	public void run() {
		if(joysticks_enabled) GLFWJoystick.pollJoysticks();
		//System.out.println(glfwGetJoystickButtons(0).get());
		
		context.invoke();
		
		CallbackHolder.clear();
		InputMapping.invokeCallbacks();
	}
	
	public void enableJoysticks(){joysticks_enabled = true; GLFWJoystick.init();}
	public void disableJoysticks(){joysticks_enabled = false;}
}
