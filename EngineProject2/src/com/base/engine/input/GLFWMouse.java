package com.base.engine.input;

import static org.lwjgl.glfw.GLFW.*;

import com.base.engine.rendering.GLFWWindow;

public class GLFWMouse extends InputDevice {
	private static final int CURSOR_X = 0;
	private static final int CURSOR_Y = 1;
	private static final int SCROLL_X = 2;
	private static final int SCROLL_Y = 3;
	
	private static boolean flip_x_axis = false;
	private static boolean flip_y_axis = true;
	
	private GLFWWindow current_window;
	
	public GLFWMouse(GLFWWindow window) {
		this.device_id = InputContext.MOUSE;
		this.current_window = window;
		setGLFWCallbacks();
	}
	
	void setGLFWCallbacks(){
		glfwSetMouseButtonCallback(current_window.getGLFWHandle(), (window, button, action, mods) -> {
			if(action != GLFW_REPEAT) CallbackHolder.addButtonCallback(this.device_id, button, action, mods);
		});
		glfwSetCursorPosCallback(current_window.getGLFWHandle(), (window, xpos, ypos) -> {
			
			xpos = (xpos / current_window.getWidth());
			ypos = (ypos / current_window.getHeight());
			
			if(flip_x_axis) xpos = 1 - xpos;
			if(flip_y_axis) ypos = 1 - ypos;
			
			CallbackHolder.addAxisCallback(this.device_id, CURSOR_X, (float) xpos);
			CallbackHolder.addAxisCallback(this.device_id, CURSOR_Y, (float) ypos);
		});
		glfwSetScrollCallback(current_window.getGLFWHandle(), (window, xoffset, yoffset) -> {
			if(xoffset != 0) CallbackHolder.addAxisCallback(this.device_id, SCROLL_X, (float) xoffset);
			if(yoffset != 0) CallbackHolder.addAxisCallback(this.device_id, SCROLL_Y, (float) yoffset);
		});
	}
}
