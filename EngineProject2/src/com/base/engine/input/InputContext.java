package com.base.engine.input;

import java.util.ArrayList;
import java.util.HashMap;
import static org.lwjgl.glfw.GLFW.*;

public class InputContext {
	public static boolean multiple_joysticks = false;
	public static final int ALL_JOYSTICK = 16;
	public static final int MOUSE = 17;
	public static final int KEYBOARD = 18;
	protected int priority;
	private static String[] keyBindings = new String[]{

	};
	
	private HashMap<Integer, Integer> actionBindings = new HashMap<Integer, Integer>();
	private HashMap<Integer, Integer> stateBindings = new HashMap<Integer, Integer>();
	private HashMap<Integer, Integer> rangeBindings = new HashMap<Integer, Integer>();
	
	public InputContext(){this(keyBindings);}
	public InputContext(ArrayList<String> bindings){this(bindings.toArray(new String[bindings.size()]));}
	public InputContext(String[] bindings){
		for(int i = 0; i < bindings.length; i+=2){
			int binding = parseInputCode(bindings[i]);
			int action = InputMapping.getActionIndex(bindings[i+1]);
			if(action >= 0) actionBindings.put(binding, action);
			else {
				action = InputMapping.getStateIndex(bindings[i+1]);
				if(action >= 0) stateBindings.put(binding, action);
				else{
					action = InputMapping.getRangeIndex(bindings[i+1]);
					if(action >= 0) rangeBindings.put(binding, action);
					else System.out.println("Action doesn't exist!");
				}
			}
		}
		
	}
	
	public void invoke(){
		if(!actionBindings.isEmpty() || !stateBindings.isEmpty()) CallbackHolder.checkButtons(actionBindings, stateBindings);
		if(!rangeBindings.isEmpty()) CallbackHolder.checkAxes(rangeBindings);
	}
	
	static int parseInputCode(String s){
		String[] splits = s.toUpperCase().split("_");
		if(splits[0].equals("MOUSE")){
			try {return (MOUSE << 24) | (Integer.parseInt(splits[1]) << 16);}
			catch(NumberFormatException e){
				switch(splits[1]){
				case "LEFT": return (MOUSE << 24);
				case "RIGHT": return (MOUSE << 24) | (1 << 16);
				case "MIDDLE": return (MOUSE << 24) | (2 << 16);
				case "CURSORX": return (MOUSE << 24);
				case "CURSORY": return (MOUSE << 24) | (1 << 16);
				case "SCROLLX": return (MOUSE << 24) | (2 << 16);
				case "SCROLLY": return (MOUSE << 24) | (3 << 16);
				}
				System.err.println("Mouse Axis Unknown: " + splits[1]);
				return -1;
			}
		}if(splits[0].equals("KEYBOARD") || splits[0].equals("KEY")){
			if(splits.length > 2){
				int k = 0;
				for(int i = 1; i < splits.length; i++){
					String upper = splits[i];
					k |= upper.equals("SHIFT") ? GLFW_MOD_SHIFT : 
						(upper.equals("CTRL") || upper.equals("CONTROL") ? GLFW_MOD_CONTROL : 
						(upper.equals("ALT") ? GLFW_MOD_ALT : 
						(upper.equals("SUPER") ? GLFW_MOD_SUPER : (parseInputKey(upper) << 16))));
				}
				return (KEYBOARD << 24) | k;
			} else return (KEYBOARD << 24) | (parseInputKey(splits[1]) << 16);
		}if(splits[0].equals("JOYSTICK")){
			if(splits[2].equals("HAT")) return (Integer.parseInt(splits[1]) << 24) | (parseHat(splits[4]) << 20) | (Integer.parseInt(splits[3]) << 16); //JOYSTICK_1_HAT_1_UP
			if(splits[1].equals("HAT")) return (ALL_JOYSTICK << 24) | (parseHat(splits[3]) << 20) | (Integer.parseInt(splits[2]) << 16);
			if(splits.length == 3) return (Integer.parseInt(splits[1]) << 24) | (Integer.parseInt(splits[2]) << 16);
			if(splits.length == 2) return (ALL_JOYSTICK << 24) | (Integer.parseInt(splits[1]) << 16);
		}
		return -1;
	}
	
	static int parseHat(String s){
		switch(s){
		case "UP": return 1;
		case "RIGHT": return 2;
		case "DOWN": return 4;
		case "LEFT": return 8;
		case "UPRIGHT":
		case "RIGHTUP": return 2 | 1;
		case "DOWNRIGHT":
		case "RIGHTDOWN": return 2 | 4;
		case "UPLEFT":
		case "LEFTUP": return 8 | 1;
		case "DOWNLEFT":
		case "LEFTDOWN": return 8 | 4;
		}
		System.err.println("Hat Unknown: " + s);
		return Integer.parseInt(s);
	}
	
	static int parseInputKey(String s){
		if(s.length() == 1) return s.charAt(0);												//Basic key
		if(s.charAt(0) == 'F') return 289 | Integer.parseInt(s.substring(1,s.length())); 	//Function keys
		if(s.charAt(0) == 'K' && s.length()  < 4) return 320 | s.charAt(1); //TODO: Check accuracy
		switch(s){
		case "SPACE": return 32;
		case "APOSTROPHE": return 39;
		case "COMMA": return 44;
		case "MINUS": return 45;
		case "PERIOD": return 46;
		case "SLASH": return 47;
		case "SEMICOLON": return 59;
		case "EQUAL": return 61;
		case "LEFTBRACKET": return 91;
		case "BACKSLASH": return 92;
		case "RIGHTBRACKET": return 93;
		case "GRAVEACCENT": return 96;
		case "WORLD1": return 161;
		case "WORLD2": return 162;
		case "ESC": return 256;
		case "ESCAPE": return 256;
		case "ENTER": return 257;
		case "TAB": return 258;
		case "BACKSPACE": return 259;
		case "INSERT": return 260;
		case "DELETE": return 261;
		case "RIGHT": return 262;
		case "LEFT": return 263;
		case "DOWN": return 264;
		case "UP": return 265;
		case "PAGEUP": return 266;
		case "PAGEDOWN": return 267;
		case "HOME": return 268;
		case "END": return 269;
		case "CAPSLOCK": return 280;
		case "SCROLLLOCK": return 281;
		case "NUMLOCK": return 282;
		case "PRINTSCREEN": return 283;
		case "PAUSE": return 284;
		
		case "KPDIVIDE": return 331;
		case "KPMULTIPLY": return 332;
		case "KPSUBTRACT": return 333;
		case "KPADD": return 334;
		case "KPENTER": return 335;
		case "KPEQUAL": return 336;
		
		case "LEFTSHIFT": return 340;
		case "LEFTCONTROL": return 341;
		case "LEFTALT": return 342;
		case "LEFTSUPER": return 343;
		case "RIGHTSHIFT": return 344;
		case "RIGHTCONTROL": return 345;
		case "RIGHTALT": return 346;
		case "RIGHTSUPER": return 347;
		case "MENU": return 348;
		}
		System.err.println("Key Unknown: " + s);
		return -1;
	}
}
