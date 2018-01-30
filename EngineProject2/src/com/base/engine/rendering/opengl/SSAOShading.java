package com.base.engine.rendering.opengl;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glClear;

import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;

import math.Matrix4f;
import math.Vector3f;

public class SSAOShading {
	int shader, buffer, gPosition, gNormal, noise,  blurshader, blurbuffer, colorbuffer;
	Vector3f[] samples;
	
	public SSAOShading(int shader, int buffer, int gPosition, int gNormal, int noise, Vector3f[] samples){this(shader, buffer, gPosition, gNormal, noise, samples, -1, -1, -1);}
	public SSAOShading(int shader, int buffer, int gPosition, int gNormal, int noise, Vector3f[] samples, int blurshader, int blurbuffer, int colorbuffer){
		this.shader = shader;
		this.buffer = buffer;
		this.gPosition = gPosition;
		this.gNormal = gNormal;
		this.noise = noise;
		this.samples = samples;
		this.blurshader = blurshader;
		this.blurbuffer = blurbuffer;
		this.colorbuffer = colorbuffer;
	}
	
	public void renderFirstPass(Matrix4f view, Matrix4f projection){
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, buffer);
	    glClear(GL_COLOR_BUFFER_BIT);
	    GLShader.bindProgram(shader);
	    GL13.glActiveTexture(GL13.GL_TEXTURE0);
	    glBindTexture(GL_TEXTURE_2D, gPosition);
	    GLShader.setUniform(shader, "gPosition", 0);
	    GL13.glActiveTexture(GL13.GL_TEXTURE1);
	    glBindTexture(GL_TEXTURE_2D, gNormal);
	    GLShader.setUniform(shader, "gNormal", 1);
	    GL13.glActiveTexture(GL13.GL_TEXTURE2);
	    glBindTexture(GL_TEXTURE_2D, noise);
	    GLShader.setUniform(shader, "texNoise", 2);
	    GLShader.setUniformVec3(shader, "samples", samples);
	    GLShader.setUniformMat4(shader, "view", view);
	    GLShader.setUniformMat4(shader, "projection", projection);
	    GLRendering.renderQuad();
		
	    
	  //SSAO Blur
	    if(blurshader >= 0){
		    GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, blurbuffer);
	        glClear(GL_COLOR_BUFFER_BIT);
	        GLShader.bindProgram(blurshader);
	        GL13.glActiveTexture(GL13.GL_TEXTURE0);
	        glBindTexture(GL_TEXTURE_2D, colorbuffer);
	        GLShader.setUniform(blurshader, "ssaoInput", 0);
	        GLRendering.renderQuad();
	    }
        
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
	}
}
