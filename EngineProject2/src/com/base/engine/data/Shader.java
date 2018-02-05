package com.base.engine.data;

import org.lwjgl.opengl.GL20;

import com.base.engine.rendering.opengl.GLShader;

public class Shader extends Resource {
	public int program = -1;
	public int proj = -1, view = -1, model = -1;
	
	public Shader(String filename) {
		super(filename);
		// TODO Auto-generated constructor stub
	}
	
	public void setUniformLocations(String proj, String view, String model) {
		this.proj = GL20.glGetUniformLocation(program, proj);
		this.view = GL20.glGetUniformLocation(program, view);
		this.model = GL20.glGetUniformLocation(program, model);
	}
	
	@Override
	public void load() {
		program = GLShader.compileProgram(filename);
	}

	@Override
	public void unload() {
		// TODO Auto-generated method stub
		GLShader.deleteProgram(program);
	}

}
