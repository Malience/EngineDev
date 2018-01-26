package com.base.engine.rendering.opengl;

import org.lwjgl.opengl.*;

import com.base.engine.rendering.GLFWWindow;

public class GLContext {
	public static GLContext context;
	@SuppressWarnings("unused")
	private int version = 33;
	GLCapabilities glcapabilities;
	float anisotropic_filtering;
	
	public GLContext(int version){
		this.version = version;
		glcapabilities = GL.createCapabilities();
		checkExtensions();
		context = this;
	}
	
	public void setVersion(int version){
		if(!(version != 33 || version >= 40 || version <= 45)) System.err.println("OpenGL Version not supported!");
		this.version = version;
	}
	
	public void viewport(int[] sizes){GL11.glViewport(0, 0, sizes[0], sizes[1]);}
	public void viewport(GLFWWindow window){GL11.glViewport(0, 0, window.getWidth(), window.getHeight());}
	
	private static boolean print = false;
	
	public void checkExtensions(){
		int[] num_extensions = new int[1];
		GL11.glGetIntegerv(GL30.GL_NUM_EXTENSIONS, num_extensions);
		for(int i = 0; i < num_extensions[0]; i++) {
			String s = GL30.glGetStringi(GL11.GL_EXTENSIONS, i);
			if(print) System.out.println(s);
			if(s.equals("GL_EXT_texture_filter_anisotropic")) {
				float[] f = new float[1];
				GL11.glGetFloatv(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT, f);
				anisotropic_filtering = f[0];
			
			}
		}
	}
}
