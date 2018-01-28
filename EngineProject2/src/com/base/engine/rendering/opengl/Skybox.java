package com.base.engine.rendering.opengl;

import static org.lwjgl.opengl.GL11.GL_LEQUAL;
import static org.lwjgl.opengl.GL11.GL_LESS;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glDepthFunc;
import static org.lwjgl.opengl.GL11.glDrawArrays;

import org.lwjgl.opengl.GL11;

import math.Matrix4f;

public class Skybox extends GLPass{
	int texture, mesh;
	
	public Skybox(int shader, int texture) {
		super(0, 0, shader);
		this.texture = texture;
		mesh = GLVertexArray.genVertexArray();
		GLVertexArray.setUpVertexArray(mesh, skyboxVertices);
	}
	
	public void render(Matrix4f view, int framebuffer){
		glDepthFunc(GL_LEQUAL);
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebuffer);
		GLShader.bindProgram(shader);
		GLShader.setUniformMat4(this.view, view);
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texture);
		GLVertexArray.bindVertexArray(mesh);
		glDrawArrays(GL_TRIANGLES, 0, 36);
		glDepthFunc(GL_LESS);
	}
	
	public void finalize(){
		GLVertexArray.deleteVertexArray(mesh);
	}
	int[] indices = {0,1,2,
					2,3,0,
					
					4,1,0,
					0,5,4,
					
					2,6,7,
					7,3,2,
					
					4,5,7,7,6,4,
					0,3,7,7,5,0,
					1,4,2,2,4,6
					};
	private static final float skyboxVertices[] = {
		    // positions          
		    -1.0f,  1.0f, -1.0f, //0//
		    -1.0f, -1.0f, -1.0f, //1//
		     1.0f, -1.0f, -1.0f, //2//
		     1.0f, -1.0f, -1.0f, //2
		     1.0f,  1.0f, -1.0f, //3//
		    -1.0f,  1.0f, -1.0f, //0

		    -1.0f, -1.0f,  1.0f, //4//
		    -1.0f, -1.0f, -1.0f, //1
		    -1.0f,  1.0f, -1.0f, //0
		    -1.0f,  1.0f, -1.0f, //0
		    -1.0f,  1.0f,  1.0f, //5//
		    -1.0f, -1.0f,  1.0f, //4

		     1.0f, -1.0f, -1.0f, //2
		     1.0f, -1.0f,  1.0f, //6//
		     1.0f,  1.0f,  1.0f, //7//
		     1.0f,  1.0f,  1.0f, //7
		     1.0f,  1.0f, -1.0f, //3
		     1.0f, -1.0f, -1.0f, //2

		    -1.0f, -1.0f,  1.0f, //4
		    -1.0f,  1.0f,  1.0f, //5
		     1.0f,  1.0f,  1.0f, //7
		     1.0f,  1.0f,  1.0f, //7
		     1.0f, -1.0f,  1.0f, //6
		    -1.0f, -1.0f,  1.0f, //4

		    -1.0f,  1.0f, -1.0f, //0
		     1.0f,  1.0f, -1.0f, //3
		     1.0f,  1.0f,  1.0f, //7
		     1.0f,  1.0f,  1.0f, //7
		    -1.0f,  1.0f,  1.0f, //5
		    -1.0f,  1.0f, -1.0f, //0

		    -1.0f, -1.0f, -1.0f, //1
		    -1.0f, -1.0f,  1.0f, //4
		     1.0f, -1.0f, -1.0f, //2
		     1.0f, -1.0f, -1.0f, //2
		    -1.0f, -1.0f,  1.0f, //4
		     1.0f, -1.0f,  1.0f  //6
		};
	
}
