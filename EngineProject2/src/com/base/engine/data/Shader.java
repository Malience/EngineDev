package com.base.engine.data;

import com.base.engine.rendering.opengl.GLShader;

public class Shader extends Resource {
	public int program = -1;
	
	public Shader(String filename) {
		super(filename);
		// TODO Auto-generated constructor stub
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
