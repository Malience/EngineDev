package com.base.engine.input;

import static org.lwjgl.glfw.GLFW.*;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public class GLFWJoystick extends InputDevice{
	private static final int MAX_JOYSTICKS = GLFW_JOYSTICK_LAST + 1;
	static GLFWJoystick[] current_joysticks = new GLFWJoystick[MAX_JOYSTICKS];
	private static int num_joysticks = 0;
	
	//Buttons
	private boolean[] prev_buttons;
	private boolean[] current_buttons;
	//Axes
	private float[] prev_axes;
	private float[] current_axes;
	//Hats
	private byte[] prev_hats;
	private byte[] current_hats;
	//Constructor
	private GLFWJoystick(int id) {device_id = id;}
	
	protected static void init(){
		checkJoysticks();
		glfwSetJoystickCallback((int jid, int event) -> {
			
			if(event == GLFW_DISCONNECTED) destroyJoystick(jid);
			else createJoystick(jid);
			System.out.println("Joystick " + (event == GLFW_CONNECTED ? "connected: " : "disconnected: ") + jid);
		});
	}
	
	public boolean getBoolean(int i) {return current_buttons[i];}
	public float getFloat(int i) {return current_axes[i];}
	
	protected static int getNumJoysticks() {return num_joysticks;}
	
	//Values that determine the cutoff point for axes values
	private static final float MAX_EPSILON = .99f;
	private static final float MIN_EPSILON = .01f;
	
	public void poll() {
		if(!glfwJoystickPresent(device_id)) {destroyJoystick(device_id); return;}
		//Buttons
		ByteBuffer buffer = glfwGetJoystickButtons(device_id);
		if(current_buttons == null) {int cap = buffer.capacity();prev_buttons = new boolean[cap];current_buttons = new boolean[cap];}
		int i = 0;
		while(buffer.hasRemaining()) {
			prev_buttons[i] = current_buttons[i]; current_buttons[i] = buffer.get() == 1;
			if(current_buttons[i] || prev_buttons[i]) 
				CallbackHolder.addButtonCallback(this.device_id, i, current_buttons[i] ? (prev_buttons[i] ? GLFW_REPEAT : GLFW_PRESS) : GLFW_RELEASE);
			i++;
			}
		
		//Axes
		FloatBuffer abuffer = glfwGetJoystickAxes(device_id);
		if(current_axes == null) {int cap = abuffer.capacity();prev_axes = new float[cap];current_axes = new float[cap];}
		i = 0;
		while(abuffer.hasRemaining()) {
			prev_axes[i] = current_axes[i];
			float state = abuffer.get();
			if(state > MAX_EPSILON) current_axes[i] = 1.0f;
			else if(state < -MAX_EPSILON) current_axes[i] = -1.0f;
			else if(state < MIN_EPSILON && state > -MIN_EPSILON) current_axes[i] = 0.0f;
			else current_axes[i] = state;
			if(current_axes[i] != prev_axes[i]) CallbackHolder.addAxisCallback(this.device_id, i, current_axes[i]);
			i++;
		}
		
		//Hats
		buffer = glfwGetJoystickHats(device_id);
		if(current_hats == null) {int cap = buffer.capacity()+1;prev_hats = new byte[cap];current_hats = new byte[cap];}
		i = 0;
		while(buffer.hasRemaining()) {
			prev_hats[i] = current_hats[i]; current_hats[i] = buffer.get();
			if(current_hats[i] != prev_hats[i]){
				if(current_hats[i] == 0) CallbackHolder.addButtonCallback(this.device_id, (prev_hats[i] << 2) + i, GLFW_RELEASE);
				else{
					CallbackHolder.addButtonCallback(this.device_id, (prev_hats[i] << 2) + i, GLFW_RELEASE);
					CallbackHolder.addButtonCallback(this.device_id, (current_hats[i] << 2) + i, GLFW_PRESS);
				}
			}else CallbackHolder.addButtonCallback(this.device_id, (current_hats[i] << 2) + i, GLFW_REPEAT);
			i++;
			}
	}
	@Deprecated
	protected static void updateAll() {for(int i = 0; i < current_joysticks.length; i++) current_joysticks[i].poll();}
	@Deprecated
	protected static void updateJoysticks() {checkJoysticks();updateAll();}
	protected static int checkJoysticks() {
		num_joysticks = 0;
		for(int i = 0; i < MAX_JOYSTICKS; i++){
			boolean current_joystick = glfwJoystickPresent(i);
			if(current_joysticks[i] == null && current_joystick) createJoystick(i);
			else if(current_joysticks[i] != null) {
				if(!current_joystick) destroyJoystick(i);
				else num_joysticks++;
			}
		}
		return num_joysticks;
	}
	
	protected static void pollJoysticks(){for(int i = 0; i < current_joysticks.length; i++) if(current_joysticks[i] != null)current_joysticks[i].poll();}
	
	private static void createJoystick(int i) {current_joysticks[i] = new GLFWJoystick(i);}
	private static void destroyJoystick(int i) {current_joysticks[i] = null;}
	protected String getName() {return glfwJoystickPresent(device_id) ? glfwGetJoystickName(device_id) : "Disconnected Joystick";}
	protected static String getName(int id) {return glfwJoystickPresent(id) ? glfwGetJoystickName(id) : "Disconnected Joystick";}
	protected int getID(){return device_id;}
}