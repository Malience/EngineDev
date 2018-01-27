package com.base.engine.rendering;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.system.MemoryUtil.NULL;

import com.base.engine.rendering.opengl.GLContext;

public class GLFWWindow extends Window{
	GLContext context;
	private long glfw_handle;
	private int samples = 1;
	
	public long getGLFWHandle(){return glfw_handle;}
	
	public GLFWWindow(String title, int width, int height) {this(title, width, height, 1);}
	public GLFWWindow(String title, int width, int height, int samples){
		super(title, width, height);
		this.samples = samples;
		
		glfwInit();
		glfwSetErrorCallback((int error, long description) -> {System.out.println(error + "\n" + org.lwjgl.system.MemoryUtil.memASCII(description));});
		
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
		glfwWindowHint(GLFW_VISIBLE, GL_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, GL_TRUE);
		glfwWindowHint(GLFW_SAMPLES, samples);
		
		glfw_handle = glfwCreateWindow(width, height, title, NULL, NULL);
		
		glfwSetWindowSizeCallback(glfw_handle, (long window, int w, int h) -> {this.width = w; this.height = h; updateContext();});
		
		
		
		glfwMakeContextCurrent(glfw_handle);
		glfwSwapInterval(1);		
		
		glfwShowWindow(glfw_handle);
	}
	
	public void setSizeCallback(GLContext context){this.context = context;}
	public void setCursorPos(float x, float y){glfwSetCursorPos(this.glfw_handle, x, y);}
	public void setCursorMiddle() {
		glfwSetCursorPos(this.glfw_handle, this.width/2, this.height/2);
	}
	public void lockCursor(){glfwSetInputMode(glfw_handle, GLFW_CURSOR, GLFW_CURSOR_DISABLED);}
	public void unlockCursor(){glfwSetInputMode(glfw_handle, GLFW_CURSOR, GLFW_CURSOR_NORMAL);}
	
	private void updateContext(){if(context != null) context.viewport(this);}
	
	public void swapBuffers(){glfwSwapBuffers(this.glfw_handle);}
	public static void pollEvents(){glfwPollEvents();}
	
	public void setSamples(int samples){
		if(this.samples == samples) return;
		glfwHideWindow(glfw_handle);
		glfwDestroyWindow(glfw_handle);
		this.samples = samples;
		glfwWindowHint(GLFW_SAMPLES, samples);
		
		glfw_handle = glfwCreateWindow(getWidth(), getHeight(), getTitle(), NULL, NULL);
		
		glfwMakeContextCurrent(glfw_handle);
		glfwSwapInterval(1);		
		
		glfwShowWindow(glfw_handle);
	}
	
	public boolean shouldClose() {
		return glfwWindowShouldClose(glfw_handle);
	}
	
	public void dispose() {
		System.out.println("Disposing of Window!");
		glfwDestroyWindow(glfw_handle);
		glfwTerminate();
	}
	
	@Override
	public void finalize() {
		dispose();
	}
}
