package com.base.engine.rendering.opengl;

import static org.lwjgl.opengl.GL11.glClear;

import math.Matrix4f;

public class GLPass2 {
	protected static final String DEFAULT_PROJECTION = "projection", DEFAULT_VIEW = "view", DEFUALT_MODEL = "model";
	int framebuffer;
	int clearcode;
	int shader;
	int projection, view, model, material;
	
	public GLPass2(int framebuffer, int clearcode, int shader){this(framebuffer, clearcode, shader, DEFAULT_PROJECTION, DEFAULT_VIEW);}
	public GLPass2(int shader, String projection, String view){this(0, 0, shader, projection, view);}
	public GLPass2(int framebuffer, int shader, String projection, String view){this(framebuffer, 0, shader, projection, view);}
	public GLPass2(int framebuffer, int clearcode, int shader, String projection, String view){
		this.framebuffer = framebuffer;
		this.clearcode = clearcode;
		this.shader = shader;
		this.projection = GL20.glGetUniformLocation(shader, projection);
		this.view = GL20.glGetUniformLocation(shader, view);
		this.model = GL20.glGetUniformLocation(shader, "model");
	}
	
	public void render(Matrix4f view){
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebuffer);
	    if(clearcode > 0) glClear(clearcode);
	    GLShader.bindProgram(shader);
	    GLShader.setUniformMat4(this.view, view);
	    GLRendering.renderQuad();
	}
	
	public void render(int[] meshes, int[] indices){
		
	}
}
