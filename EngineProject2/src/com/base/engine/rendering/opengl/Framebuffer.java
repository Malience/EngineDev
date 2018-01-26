package com.base.engine.rendering.opengl;

import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0;
import static org.lwjgl.opengl.GL30.GL_DRAW_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.GL_READ_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;
import static org.lwjgl.opengl.GL30.glBlitFramebuffer;
import static org.lwjgl.opengl.GL30.glFramebufferTexture2D;
import static org.lwjgl.opengl.GL30.glGenRenderbuffers;


public class Framebuffer {
	public static final int 
	RED 			= 0x10000000,
	GREEN 			= 0x20000000,
	BLUE 			= 0x40000000,
	COLOR 			= RED | GREEN | BLUE,
	ALPHA 			= 0x00000010,
	FLOAT 			= 0x00000100,
	DEPTH 			= 0x00001000,
	STENCIL 		= 0x00010000,
	RED_BUFFER		= RED,
	RGB_BUFFER 		= COLOR,
	RGBF_BUFFER 	= COLOR | FLOAT,
	RGBA_BUFFER 	= COLOR | ALPHA,
	RGBAF_BUFFER 	= COLOR | ALPHA | FLOAT,
	DEPTH_BUFFER 	= DEPTH,
	STENCIL_BUFFER 	= STENCIL;
	
	public int framebuffer, buffers[], renderbuffer = -1;
	
	public Framebuffer(){framebuffer = GLFramebuffer.genFramebuffers();}
	public Framebuffer(int buffers){
		framebuffer = GLFramebuffer.genFramebuffers(); 
		setBuffers(buffers);
	}
	
	public void setBuffers(int buffers){
		if(this.buffers != null) GLTexture.deleteTextures(this.buffers);
		this.buffers = GLTexture.genTextures(new int[buffers]);
		int[] attachments = new int[buffers];
		for(int i = 0; i < attachments.length; i++) attachments[i] = GL_COLOR_ATTACHMENT0 + i;
		org.lwjgl.opengl.GL30.glBindFramebuffer(org.lwjgl.opengl.GL30.GL_FRAMEBUFFER, framebuffer);
		org.lwjgl.opengl.GL20.glDrawBuffers(attachments);
		org.lwjgl.opengl.GL30.glBindFramebuffer(org.lwjgl.opengl.GL30.GL_FRAMEBUFFER, 0);
	}
	
	public void setBuffer(int buffer, int width, int height, int buffercode){
		org.lwjgl.opengl.GL30.glBindFramebuffer(org.lwjgl.opengl.GL30.GL_FRAMEBUFFER, framebuffer);
		setUpTexture(buffers[buffer], width, height, buffercode);
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0 + buffer, GL_TEXTURE_2D, buffers[buffer], 0);
		org.lwjgl.opengl.GL30.glBindFramebuffer(org.lwjgl.opengl.GL30.GL_FRAMEBUFFER, 0);
	}
	
	//TODO: Multiple types of renderbuffers
	public void setRenderbuffer(int width, int height, int buffercode){
		org.lwjgl.opengl.GL30.glBindFramebuffer(org.lwjgl.opengl.GL30.GL_FRAMEBUFFER, framebuffer);
		renderbuffer = glGenRenderbuffers();
		org.lwjgl.opengl.GL30.glBindRenderbuffer(org.lwjgl.opengl.GL30.GL_RENDERBUFFER, renderbuffer);
		org.lwjgl.opengl.GL30.glRenderbufferStorage(org.lwjgl.opengl.GL30.GL_RENDERBUFFER, org.lwjgl.opengl.GL11.GL_RGB16, width, height);
		org.lwjgl.opengl.GL30.glFramebufferRenderbuffer(org.lwjgl.opengl.GL30.GL_FRAMEBUFFER, org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0, org.lwjgl.opengl.GL30.GL_RENDERBUFFER, renderbuffer);
		org.lwjgl.opengl.GL30.glBindFramebuffer(org.lwjgl.opengl.GL30.GL_FRAMEBUFFER, 0);
	}
	
	public void copyDepthBuffer(Framebuffer buffer, int width, int height){copyDepthBuffer(buffer.framebuffer, width, height);}
	public void copyDepthBuffer(int buffer, int width, int height){
		glBindFramebuffer(GL_READ_FRAMEBUFFER, this.framebuffer);
		glBindFramebuffer(GL_DRAW_FRAMEBUFFER, buffer);
		glBlitFramebuffer(0, 0, width, height, 0, 0, width, height, GL_DEPTH_BUFFER_BIT, GL_NEAREST);
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
	}
	
	public static void setUpTexture(int texture, int width, int height, int buffercode){
		switch(buffercode){
		case RED_BUFFER: 	GLTexture.createRedBuffer(texture, width, height);			break;
		case RGB_BUFFER: 	GLTexture.createColorbuffer(texture, width, height);		break;
		case RGBF_BUFFER: 	GLTexture.createColorbufferf(texture, width, height);		break;
		case RGBA_BUFFER: 	GLTexture.createColorbuffer(texture, width, height, true);	break;
		case RGBAF_BUFFER: 	GLTexture.createColorbufferf(texture, width, height, true);	break;
		case DEPTH_BUFFER: 	GLTexture.createDepthbuffer(texture, width, height);		break;
		}
	}
	
	public void dispose(){
		if(renderbuffer >= 0) GLFramebuffer.deleteRenderbuffers(renderbuffer);
		if(buffers != null) GLTexture.deleteTextures(buffers);
		GLFramebuffer.deleteFramebuffers(framebuffer);
	}
	
	@Override
	public void finalize(){dispose();}
}
