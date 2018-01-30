package com.base.engine.rendering.opengl;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public abstract class GLFramebuffer {
	public static int genFramebuffers(){return GL30.glGenFramebuffers();}
	public static int[] genFramebuffers(int[] buffers){GL30.glGenFramebuffers(buffers); return buffers;}
	public static void bindFramebuffer(int buffer){GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, buffer);}
	public static void unbindFramebuffer(){GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);}
	
	public static void deleteFramebuffers(int buffer){GL30.glDeleteFramebuffers(buffer);}
	public static void deleteFramebuffers(int[] buffer){GL30.glDeleteFramebuffers(buffer);}
	
	public static void deleteRenderbuffers(int buffer){GL30.glDeleteRenderbuffers(buffer);}
	public static void deleteRenderbuffers(int[] buffer){GL30.glDeleteRenderbuffers(buffer);}
	
	public static void attachColor(int texture){attachTexture(texture, GL30.GL_COLOR_ATTACHMENT0);}
	public static void attachDepth(int texture){attachTexture(texture, GL30.GL_DEPTH_ATTACHMENT);}
	public static void attachStencil(int texture){attachTexture(texture, GL30.GL_STENCIL_ATTACHMENT);}
	public static void attachDepthStencil(int texture){attachTexture(texture, GL30.GL_DEPTH_STENCIL_ATTACHMENT);}
	
	private static void attachTexture(int texture, int attachment){GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, attachment, GL11.GL_TEXTURE_2D, texture, 0);}
	
	public static boolean checkFramebufferStatus(int buffer){return GL30.glCheckFramebufferStatus(buffer) == GL30.GL_FRAMEBUFFER_COMPLETE;}
	
	public static int genRenderbuffer(){return GL30.glGenRenderbuffers();}
	public static int[] genRenderbuffer(int[] buffers){GL30.glGenRenderbuffers(buffers); return buffers;}
	
	public static void bindRenderbuffer(int buffer){GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, buffer);}
	public static void unbindRenderbuffer(){GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, 0);}
	
	public static int setUpDepthStencilBuffer(int width, int height)
	{return setUpBuffer(GL30.glGenFramebuffers(), GL30.glGenRenderbuffers(), width, height, GL30.GL_DEPTH24_STENCIL8, GL30.GL_DEPTH_STENCIL_ATTACHMENT);}
	public static int setUpDepthStencilBuffer(int framebuffer, int renderbuffer, int width, int height)
	{return setUpBuffer(framebuffer, renderbuffer, width, height, GL30.GL_DEPTH24_STENCIL8, GL30.GL_DEPTH_STENCIL_ATTACHMENT);}
	public static int setUpBuffer(int width, int height, int format, int attachment){return setUpBuffer(GL30.glGenFramebuffers(), GL30.glGenRenderbuffers(), width, height, format, attachment);}
	public static int setUpBuffer(int framebuffer, int renderbuffer, int width, int height, int format, int attachment){
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebuffer);
		GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, renderbuffer);
		GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, format, width, height);
		GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, attachment, GL30.GL_RENDERBUFFER, renderbuffer);
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, 0);
		return framebuffer;
	}
	
	public static void bindDepthbuffer(int framebuffer, int depthbuffer){
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebuffer);
		GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL11.GL_TEXTURE_2D, depthbuffer, 0);
		GL11.glDrawBuffer(GL11.GL_NONE);
		GL11.glReadBuffer(GL11.GL_NONE);
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);  
	}
	
	public static int[] createStencilBuffer(int width, int height){
		int[] buffers = new int[2];
		
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, buffers[0]);
		
		buffers[1] = GL30.glGenRenderbuffers();
		GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, buffers[1]);
		GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL11.GL_DEPTH_COMPONENT, width, height);
		GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL30.GL_RENDERBUFFER, buffers[1]);
		//GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, 0);
		
		if(GLFramebuffer.checkFramebufferStatus(buffers[0])) System.err.println("Framebuffer not complete!");
		
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		return buffers;
	}
	
	public static int setUpBloomBuffer(int framebuffer, int[] buffers, int width, int height)
	{
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebuffer);
		
		// - FRAG
		buffers[0] = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, buffers[0]);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F, width, height, 0, GL_RGBA, GL_FLOAT, (ByteBuffer)null);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
	    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, buffers[0], 0);
		  
		// - BRIGHT
		buffers[1] = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, buffers[1]);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F, width, height, 0, GL_RGBA, GL_FLOAT, (ByteBuffer)null);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
	    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT1, GL_TEXTURE_2D, buffers[1], 0);
		  
		int[] attachments = {GL_COLOR_ATTACHMENT0, GL_COLOR_ATTACHMENT1};
		GL20.glDrawBuffers(attachments);
		
		//GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, 0);
		
		if(GLFramebuffer.checkFramebufferStatus(framebuffer)) System.err.println("Framebuffer not complete!");
		
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);  
		return framebuffer;
	}
	
	public static int[] setUpPingBuffer(int[] framebuffers, int[] buffers, int width, int height)
	{
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebuffers[0]);
		
		// - FRAG
		buffers[0] = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, buffers[0]);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F, width, height, 0, GL_RGBA, GL_FLOAT, (ByteBuffer)null);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
	    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, buffers[0], 0);
		
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebuffers[1]);
		// - BRIGHT
		buffers[1] = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, buffers[1]);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F, width, height, 0, GL_RGBA, GL_FLOAT, (ByteBuffer)null);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
	    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT1, GL_TEXTURE_2D, buffers[1], 0);
		 //TODO: FIX ^^^^
		
		//GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, 0);
		
		if(GLFramebuffer.checkFramebufferStatus(framebuffers[0])) System.err.println("Framebuffer not complete!");
		
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);  
		return framebuffers;
	}
	
	public static void copyDepthBuffer(int read, int draw, int width, int height){
		glBindFramebuffer(GL_READ_FRAMEBUFFER, read);
		glBindFramebuffer(GL_DRAW_FRAMEBUFFER, draw);
		glBlitFramebuffer(0, 0, width, height, 0, 0, width, height, GL_DEPTH_BUFFER_BIT, GL_NEAREST);
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
	}
	public static void copyStencilBuffer(int read, int draw, int width, int height){
		glBindFramebuffer(GL_READ_FRAMEBUFFER, read);
		glBindFramebuffer(GL_DRAW_FRAMEBUFFER, draw);
		glBlitFramebuffer(0, 0, width, height, 0, 0, width, height, GL_STENCIL_BUFFER_BIT, GL_NEAREST);
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
	}
}
