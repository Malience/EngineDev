package com.base.engine.rendering;

import com.base.engine.rendering.opengl.GLShader;

public class InstanceShader implements Shader {
	private int program = -1;
	
	public InstanceShader(String fileName){this.program = GLShader.compileProgram(fileName);}
	
	@Override
	public int getProgram() {return program;}
	
	@Override
	public void finalize() {GLShader.deleteProgram(program);}
	
	public void bind(){GLShader.bindProgram(this.program);}
	public void setUniform(String uniformName, int value){GLShader.setUniform(program, uniformName, value);}
	public void setUniform(String uniformName, float value){GLShader.setUniform(program, uniformName, value);}
	public void setUniformVec3(String uniformName, float... value){GLShader.setUniformVec3(program, uniformName, value);}
	public void setUniformMat4(String uniformName, float... value){GLShader.setUniformMat4(program, uniformName, value);}
}
