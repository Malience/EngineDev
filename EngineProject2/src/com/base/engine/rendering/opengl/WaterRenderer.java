package com.base.engine.rendering.opengl;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glFramebufferTexture2D;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;

import com.base.engine.data.Resources;
import com.base.engine.data.Shader;

import math.Matrix4f;

public class WaterRenderer {
	
	public int refl_width, refl_height, refr_width, refr_height;
	
	public int refl_framebuffer, refr_framebuffer;		//Framebuffer
	private int refl_renderbuffer;						//Renderbuffer
	private int refl_tex, refr_tex, refr_depth;	//Textures
	
	private Shader shader;
	private int moveFactor;
	
	public WaterRenderer(int refl_width, int refl_height, int refr_width, int refr_height) {
		this.refl_width = refl_width; this.refl_height = refl_height; this.refr_width = refr_width; this.refr_height = refr_height;
		// Water Reflection
	    refl_framebuffer = GLFramebuffer.genFramebuffers();
		
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, refl_framebuffer);
		refl_tex = GLTexture.createColorbufferf(refl_width, refl_height);
		glBindTexture(GL_TEXTURE_2D, refl_tex);
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, refl_tex, 0);
		  
		GL20.glDrawBuffers(GL_COLOR_ATTACHMENT0);
		
		refl_renderbuffer = GLFramebuffer.genRenderbuffer();
		GLFramebuffer.setUpDepthStencilBuffer(refl_framebuffer, refl_renderbuffer, refl_width, refl_height);
	    
		if(GLFramebuffer.checkFramebufferStatus(refl_framebuffer)) System.err.println("Reflection buffer not complete!");
		
		// Water Refraction
		refr_framebuffer = GLFramebuffer.genFramebuffers();
		
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, refr_framebuffer);
		refr_tex = GLTexture.createColorbufferf(refr_width, refr_height);
		glBindTexture(GL_TEXTURE_2D, refr_tex);
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, refr_tex, 0);
		  
		GL20.glDrawBuffers(GL_COLOR_ATTACHMENT0);
		
		refr_depth = GLTexture.genTextures();
		glBindTexture(GL_TEXTURE_2D, refr_depth);
		glTexImage2D(GL_TEXTURE_2D, 0, GL14.GL_DEPTH_COMPONENT32, refr_width, refr_height, 0, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, (ByteBuffer) null);
		GL11.glTexParameteri(GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL32.glFramebufferTexture(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, refr_depth, 0);
		
		if(GLFramebuffer.checkFramebufferStatus(refr_framebuffer)) System.err.println("Refraction buffer not complete!");
	}
	
	public void initShader(String filename, String proj, String view, String model, Matrix4f projection) {
		shader = Resources.loadShader(filename);
		shader.setUniformLocations(proj, view, model);
		int program = shader.program;
		moveFactor = GL20.glGetUniformLocation(program, "moveFactor");
		GLShader.bindProgram(program);
		GLShader.setUniformMat4(shader.proj, projection);
		GLShader.setUniform(program, "reflection", 0);
		GLShader.setUniform(program, "refraction", 1);
		GLShader.setUniform(program, "dudv", 2);
	}
	
	public void render(int framebuffer, int width, int height, int dudv, Matrix4f view, Matrix4f transform, float motion) {
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebuffer);
	    GL11.glViewport(0, 0, width, height);
	    GLShader.bindProgram(shader.program);
		GLShader.setUniformMat4(shader.view, view);
		GLShader.setUniformMat4(shader.model, transform);
		GLShader.setUniform(moveFactor, motion);
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, refl_tex);
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, refr_tex);
		GL13.glActiveTexture(GL13.GL_TEXTURE2);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, dudv);
		GLRendering.renderQuad();
	}
	
	public void dispose() {
		GLFramebuffer.deleteFramebuffers(refl_framebuffer);
		GLFramebuffer.deleteFramebuffers(refr_framebuffer);
		GLFramebuffer.deleteRenderbuffers(refl_renderbuffer);
		
		GLTexture.deleteTexture(refl_tex);
		GLTexture.deleteTexture(refr_depth);
		GLTexture.deleteTexture(refr_tex);
	}
}
