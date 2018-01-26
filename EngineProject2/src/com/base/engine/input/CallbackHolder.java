package com.base.engine.input;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_REPEAT;

import java.util.HashMap;

public class CallbackHolder {
	private static final int DEFAULT_MAX_BUTTON_CALLBACKS = 20;
	private static final int DEFAULT_MAX_AXIS_CALLBACKS = 10;
	
	private static int[] button_callbacks = new int[DEFAULT_MAX_BUTTON_CALLBACKS];;
	private static int[] axis_callbacks = new int[DEFAULT_MAX_AXIS_CALLBACKS];
	private static float[] axis_values = new float[DEFAULT_MAX_AXIS_CALLBACKS];
	
	private static int current_code;
	private static int current_code_no_mod;
	private static int current_action;
	
	private static int current_button = 0;
	private static int buttons_length = 0;
	
	private static int current_axis = 0;
	private static int axes_length = 0;
	
	public static void addButtonCallback(int device_id, int button, int action, int mods){
		if(current_button >= button_callbacks.length) return;
		button_callbacks[current_button] = 
				((device_id < InputContext.ALL_JOYSTICK && !InputContext.multiple_joysticks ? InputContext.ALL_JOYSTICK : device_id) << 24) | (button << 16) | (action << 8) | mods;
		current_button++;
	}
	public static void addButtonCallback(int device_id, int button, int action){
		if(current_button >= button_callbacks.length) return;
		button_callbacks[current_button] = 
				((device_id < InputContext.ALL_JOYSTICK && !InputContext.multiple_joysticks ? InputContext.ALL_JOYSTICK : device_id) << 24) | (button << 16) | (action << 8);
		current_button++;
	}
	
	public static void addAxisCallback(int device_id, int axis, float value){
		if(current_axis >= axis_callbacks.length) return;
		axis_callbacks[current_axis] = ((device_id < InputContext.ALL_JOYSTICK && !InputContext.multiple_joysticks ? InputContext.ALL_JOYSTICK : device_id) << 24) | (axis << 16);
		axis_values[current_axis] = value;
		current_axis++;
	}
	
	public static void clear() {current_button = 0; current_axis = 0;}
	
	public static void checkButtons(HashMap<Integer, Integer> actionBindings, HashMap<Integer, Integer> stateBindings){
		if(current_button == 0 || (actionBindings == null && stateBindings == null)) return;
		buttons_length = current_button;
		current_button = 0;
		if(actionBindings != null && stateBindings != null) for(int i = 0; i < buttons_length; i++){
			current_action = (button_callbacks[i] & 0x0000FF00) >> 8;
			if(current_action == GLFW_REPEAT) continue;
			current_code = button_callbacks[i] & 0xFFFF00FF;
			current_code_no_mod = current_code & 0xFFFF0000;
			if(stateBindings.containsKey(current_code)) InputMapping.invokeState(stateBindings.get(current_code), current_action == GLFW_PRESS);
			else if(stateBindings.containsKey(current_code_no_mod)) InputMapping.invokeState(stateBindings.get(current_code_no_mod), current_action == GLFW_PRESS);
			else if(current_action == GLFW_PRESS) {
				if(actionBindings.containsKey(current_code)) InputMapping.invokeAction(actionBindings.get(current_code));
				else if(actionBindings.containsKey(current_code_no_mod)) InputMapping.invokeAction(actionBindings.get(current_code_no_mod));
			} else button_callbacks[current_button] = button_callbacks[i]; current_button++;
		} else if(actionBindings != null) for(int i = 0; i < buttons_length; i++){
			current_action = (button_callbacks[i] & 0x0000FF00) >> 8;
			if(current_action != GLFW_PRESS) continue;
			current_code = button_callbacks[i] & 0xFFFF00FF;
			current_code_no_mod = current_code & 0xFFFF0000;
			if(actionBindings.containsKey(current_code)) InputMapping.invokeAction(actionBindings.get(current_code));
			else if(actionBindings.containsKey(current_code_no_mod)) InputMapping.invokeAction(actionBindings.get(current_code_no_mod));
			else button_callbacks[current_button] = button_callbacks[i]; current_button++;
		} else if(stateBindings != null) for(int i = 0; i < buttons_length; i++){
			current_action = (button_callbacks[i] & 0x0000FF00) >> 8;
			if(current_action == GLFW_REPEAT) continue;
			current_code = button_callbacks[i] & 0xFFFF00FF;
			current_code_no_mod = current_code & 0xFFFF0000;
			if(stateBindings.containsKey(current_code)) InputMapping.invokeState(stateBindings.get(current_code), current_action == GLFW_PRESS);
			else if(stateBindings.containsKey(current_code_no_mod)) InputMapping.invokeState(stateBindings.get(current_code_no_mod), current_action == GLFW_PRESS);
			else button_callbacks[current_button] = button_callbacks[i]; current_button++;
		}
	}
	
	public static void checkAxes(HashMap<Integer, Integer> rangeBindings){
		if(current_axis == 0 || rangeBindings == null) return;
		axes_length = current_axis;
		current_axis = 0;
		for(int i = 0; i < axes_length; i++){
			current_code = axis_callbacks[i] & 0xFFFF00FF;
			if(rangeBindings.containsKey(current_code)) InputMapping.invokeRange(rangeBindings.get(current_code), axis_values[i]);
			else axis_callbacks[current_axis] = axis_callbacks[i]; current_axis++;
		}
	}
	
	public static void setButtonCallbacksSize(int size){button_callbacks = new int[size];}
	public static void setAxisCallbacksSize(int size){axis_callbacks = new int[size]; axis_values = new float[size];}
}
