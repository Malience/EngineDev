package com.base.engine.input;

import static org.lwjgl.glfw.GLFW.*;

import com.base.engine.rendering.GLFWWindow;

public class GLFWKeyboard extends InputDevice{
	private GLFWWindow current_window;
	
	public GLFWKeyboard(GLFWWindow window) {
		this.device_id = InputContext.KEYBOARD;
		this.current_window = window;
		setGLFWCallbacks();
	}
	
	void setGLFWCallbacks(){
		glfwSetKeyCallback(current_window.getGLFWHandle(), (window, button, scancode, action, mods) -> {
			CallbackHolder.addButtonCallback(this.device_id, button, action, mods);
		});
	}
}
