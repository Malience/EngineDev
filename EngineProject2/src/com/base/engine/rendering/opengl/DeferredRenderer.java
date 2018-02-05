package com.base.engine.rendering.opengl;

import static org.lwjgl.opengl.GL11.GL_BACK;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glCullFace;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT1;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT2;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.GL_RGBA16F;
import static org.lwjgl.opengl.GL30.glFramebufferTexture2D;
import static org.lwjgl.opengl.GL30.glGenRenderbuffers;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.lwjgl.*;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import com.base.engine.data.Resources;
import com.base.engine.data.Shader;
import com.base.engine.rendering.DirectionalLight;
import com.base.engine.rendering.Material;
import com.base.engine.rendering.MaterialMap;
import com.base.engine.rendering.PointLight;
import math.Matrix4f;
import com.base.math.Transform;
import math.Vector3f;

public class DeferredRenderer {
	
	
	
	int width, height;
	//MSAA stuff TODO
	
	//Shaders
	public Shader gbuffer_shader, terrain_shader, lighting_shader, ssao_shader, blur_shader;
	//private int ssao_uniform, shadows_uniforms, shadows_samples_uniform;
	
	public void initShaders(String gbuffer, String terrain, String lighting, String ssao, String blur) {
		gbuffer_shader = Resources.loadShader(gbuffer);
		terrain_shader = Resources.loadShader(terrain);
		lighting_shader = Resources.loadShader(lighting);
		ssao_shader = Resources.loadShader(ssao);
		blur_shader = Resources.loadShader(blur);
		
		gbuffer_shader.setUniformLocations("projection", "view", "model");
		terrain_shader.setUniformLocations("projection", "view", "model");
		lighting_shader.setUniformLocations("projection", "view", "model");
		ssao_shader.setUniformLocations("projection", "view", "model");
		
		
		int lighting_program = lighting_shader.program;
		GLShader.bindProgram(lighting_program);
	    GLShader.setUniform(lighting_program, "gPosition", 0);
	    GLShader.setUniform(lighting_program, "gNormal", 1);
	    GLShader.setUniform(lighting_program, "gAlbedoSpec", 2);
        GLShader.setUniform(lighting_program, "ssao", 3);
        GLShader.setUniform(lighting_program, "shadowMap", 4);
        GLShader.setUniform(lighting_program, "shadowSamples", 1);
        GLShader.setUniform(lighting_program, "ssaoEnabled", 0);
		GLShader.setUniform(lighting_program, "shadowsEnabled", 0);
		
		int ssao_program = ssao_shader.program;
		GLShader.bindProgram(ssao_program);
		GLShader.setUniform(ssao_program, "gPosition", 0);
	    GLShader.setUniform(ssao_program, "gNormal", 1);
	    GLShader.setUniform(ssao_program, "texNoise", 2);
	    
	    int blur_program = blur_shader.program;
	    GLShader.bindProgram(blur_program);
        GLShader.setUniform(blur_program, "ssaoInput", 0);
	}
	
	public void setProjection(Matrix4f proj) {
		GLShader.bindProgram(gbuffer_shader.program);
		GLShader.setUniformMat4(gbuffer_shader.proj, proj);
		GLShader.bindProgram(lighting_shader.program);
		GLShader.setUniformMat4(lighting_shader.proj, proj);
		GLShader.bindProgram(terrain_shader.program);
		GLShader.setUniformMat4(terrain_shader.proj, proj);
		GLShader.bindProgram(ssao_shader.program);
		GLShader.setUniformMat4(ssao_shader.proj, proj);
	}
	
	
	public DeferredRenderer(int width, int height){
		this.width = width; this.height = height;;
	}
	
	public void init() {
		initGBuffer();
		if(ssao)initSSAO();
		initLighting();
	}
	
	public void renderLighting(Matrix4f view, Vector3f viewpos, DirectionalLight dlight, int buffer, int width, int height){
		if(ssao){
			//SSAO
			GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, ssaoframebuffer);
		    glClear(GL_COLOR_BUFFER_BIT);
		    GLShader.bindProgram(ssao_shader.program);
		    GL13.glActiveTexture(GL13.GL_TEXTURE0);
		    glBindTexture(GL_TEXTURE_2D, gposition);
		    GL13.glActiveTexture(GL13.GL_TEXTURE1);
		    glBindTexture(GL_TEXTURE_2D, gnormal);
		    GL13.glActiveTexture(GL13.GL_TEXTURE2);
		    glBindTexture(GL_TEXTURE_2D, noiseTexture);
		    GLShader.setUniformMat4(ssao_shader.view, view);
		    GLRendering.renderQuad();
		    //SSAO BLUR
	    	GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, ssaoblurframebuffer);
	    	glClear(GL_COLOR_BUFFER_BIT);
	    	GLShader.bindProgram(blur_shader.program);
	    	GL13.glActiveTexture(GL13.GL_TEXTURE0);
	        glBindTexture(GL_TEXTURE_2D, ssaobuffer);
	    	GLRendering.renderQuad();
		}
	    //LIGHTING
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, buffer);
		GL11.glViewport(0, 0, width, height);
	    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	    GLShader.bindProgram(lighting_shader.program);
	    GLShader.setUniformMat4(lighting_shader.view, view);
	    GLShader.setUniformVec3(lighting_shader.program, "viewPos", viewpos);
	    GLShader.setLight(lighting_shader.program, dlight);
	    GL13.glActiveTexture(GL13.GL_TEXTURE0);
	    glBindTexture(GL_TEXTURE_2D, gposition);
	    GL13.glActiveTexture(GL13.GL_TEXTURE1);
	    glBindTexture(GL_TEXTURE_2D, gnormal);
	    GL13.glActiveTexture(GL13.GL_TEXTURE2);
	    glBindTexture(GL_TEXTURE_2D, galbedospec);
	    GL13.glActiveTexture(GL13.GL_TEXTURE3);
	    glBindTexture(GL_TEXTURE_2D, ssaoblurbuffer);
		GLRendering.renderQuad();
	}
	
	//GBuffer
	public int gbuffer;
	private int gposition, gnormal, galbedospec;

	int gdepth;
	private void initGBuffer(){
		gbuffer = GLFramebuffer.genFramebuffers();
		
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, gbuffer);
		
		// - Position
		gposition = GLTexture.createColorbufferf(width, height);
		glBindTexture(GL_TEXTURE_2D, gposition);
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, gposition, 0);
		  
		// - Normal
		gnormal = GLTexture.createColorbufferf(width, height);
		glBindTexture(GL_TEXTURE_2D, gnormal);
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT1, GL_TEXTURE_2D, gnormal, 0);
		
		// - AlbedoSpec
		galbedospec = GLTexture.createColorbufferf(width, height, true);
		glBindTexture(GL_TEXTURE_2D, galbedospec);
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT2, GL_TEXTURE_2D, galbedospec, 0);
		  
		GL20.glDrawBuffers(new int[]{GL_COLOR_ATTACHMENT0, GL_COLOR_ATTACHMENT1, GL_COLOR_ATTACHMENT2});
		
		gdepth = GLFramebuffer.genRenderbuffer();
		GLFramebuffer.setUpDepthStencilBuffer(gbuffer, gdepth, width, height);
		
		if(GLFramebuffer.checkFramebufferStatus(gbuffer)) System.err.println("GBuffer not complete!");
	}
	
	//SSAO
	boolean ssao = false;
	private int ssaoframebuffer, ssaobuffer, noiseTexture, ssaoblurframebuffer, ssaoblurbuffer;
	private void initSSAO(){initSSAO(8, 4);}
	private void initSSAO(int kernel_size, int noise_size){
		initSSAOKernel(kernel_size);
		initSSAONoise(noise_size);
		
		ssaoframebuffer = GLFramebuffer.genFramebuffers();
		
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, ssaoframebuffer);
		ssaobuffer = GLTexture.createRedBuffer(width, height);
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, ssaobuffer, 0);
		
	    
	    
    	ssaoblurframebuffer = GLFramebuffer.genFramebuffers();
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, ssaoblurframebuffer);
		ssaoblurbuffer = GLTexture.createRedBuffer(width, height);
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, ssaoblurbuffer, 0);
	}
	
	private void initSSAOKernel(int kernel_size){
		kernel_size *= kernel_size;
		
		Vector3f[] ssaokernel = new Vector3f[kernel_size];
		
		for(int i = 0; i < kernel_size; i++){
			float scale = ((float) i) / kernel_size;
			scale = 0.1f + scale * scale * (0.9f);
			ssaokernel[i] = new Vector3f(
					((float)Math.random()) * 2.0f - 1.0f,
					((float)Math.random()) * 2.0f - 1.0f,
					((float)Math.random())
					).normalize().mul(((float)Math.random()) * scale);
		}
		GLShader.bindProgram(ssao_shader.program);
		GLShader.setUniform(ssao_shader.program, "kernelSize", kernel_size);
	    GLShader.setUniformVec3(ssao_shader.program, "samples", ssaokernel);
	}
	
	private void initSSAONoise(int noise_size){noiseTexture = GLTexture.createNoiseTexture(noise_size, noise_size);}
	
	public void dispose() {
		GLFramebuffer.deleteFramebuffers(gbuffer);
		GLFramebuffer.deleteFramebuffers(ssaoframebuffer);
		GLFramebuffer.deleteFramebuffers(ssaoblurframebuffer);
		
		GLTexture.deleteTexture(gposition);
		GLTexture.deleteTexture(gnormal);
		GLTexture.deleteTexture(galbedospec);
		GLTexture.deleteTexture(gdepth);
		GLTexture.deleteTexture(ssaobuffer);
		GLTexture.deleteTexture(noiseTexture);
		GLTexture.deleteTexture(ssaoblurbuffer);
	}
	
	
	//Lighting
	boolean shadows = false;
	private void initLighting(){
		
	}
//	public void toggleSSAO(){ssao = !ssao; GLShader.setUniform(SSAO_UNIFORM, ssao);}
//	public void toggleShadows(){shadows = !shadows; GLShader.setUniform(SHADOWS_UNIFORM, shadows);}
}
