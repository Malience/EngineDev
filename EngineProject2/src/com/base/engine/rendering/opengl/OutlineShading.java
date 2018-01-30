package com.base.engine.rendering.opengl;

import static org.lwjgl.opengl.GL11.GL_ALWAYS;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_KEEP;
import static org.lwjgl.opengl.GL11.GL_NOTEQUAL;
import static org.lwjgl.opengl.GL11.GL_REPLACE;
import static org.lwjgl.opengl.GL11.GL_STENCIL_TEST;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glStencilFunc;
import static org.lwjgl.opengl.GL11.glStencilMask;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import math.Matrix4f;

public class OutlineShading {
	private static int NUM_MESHES = 10;
	int shader, blankshader;
	int[] meshes;
	Matrix4f[] transforms;
	
	public OutlineShading(int shader, int blankshader){
		this.shader = shader;
		this.blankshader = blankshader;
		meshes = new int[NUM_MESHES * 2];
		for(int i = 0; i < meshes.length; i++){
			meshes[i] = -1;
		}
		transforms = new Matrix4f[NUM_MESHES];
	}
	
	public void put(int index, int mesh, int meshvertices, Matrix4f transform){
		meshes[index * 2] = mesh;
		meshes[index * 2 + 1] = meshvertices;
		transforms[index] = transform;
	}
	
	public void remove(int index){
		meshes[index * 2] = -1;
		meshes[index * 2 + 1] = -1;
		transforms[index] = null;
	}
	
	public void renderFirstPass(Matrix4f view, Matrix4f projection){
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		glDisable(GL_DEPTH_TEST);
		glEnable(GL_STENCIL_TEST);
		

		glStencilMask(0x00);
		GL11.glStencilOp(GL_KEEP, GL_KEEP, GL_REPLACE);
		glStencilFunc(GL_ALWAYS, 1, 0xFF);
		glStencilMask(0xFF);
		GLShader.bindProgram(blankshader);
		GLShader.setUniformMat4(blankshader, "view", view);
		GLShader.setUniformMat4(blankshader, "projection", projection);
		for(int i = 0; i < transforms.length; i++){
			if(meshes[i * 2] == -1) continue;
			GLShader.setUniformMat4(blankshader, "model", transforms[i]);
			GLRendering.renderMesh(meshes[i * 2], meshes[i * 2 + 1]);
		}
		glStencilFunc(GL_NOTEQUAL, 1, 0xFF);
		glStencilMask(0x00);
		
		glDisable(GL_STENCIL_TEST);
		glEnable(GL_DEPTH_TEST);
	}
	
	public void renderSecondPass(Matrix4f view, Matrix4f projection){
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		glDisable(GL_DEPTH_TEST);
		glEnable(GL_STENCIL_TEST);
		
		GLShader.bindProgram(shader);
		GLShader.setUniformMat4(shader, "view", view);
		GLShader.setUniformMat4(shader, "projection", projection);
		
		for(int i = 0; i < transforms.length; i++){
			if(meshes[i * 2] == -1) continue;
			GLShader.setUniformMat4(shader, "model", transforms[i]);
			GLRendering.renderMesh(meshes[i * 2], meshes[i * 2 + 1]);
		}
		
		glStencilMask(0xFF);
		glEnable(GL_DEPTH_TEST);  
	}
}
