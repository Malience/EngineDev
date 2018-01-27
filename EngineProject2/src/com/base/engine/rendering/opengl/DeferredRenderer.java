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

import com.base.engine.data.Resources;
import com.base.engine.rendering.DirectionalLight;
import com.base.engine.rendering.Material;
import com.base.engine.rendering.MaterialMap;
import com.base.engine.rendering.PointLight;
import math.Matrix4f;
import com.base.math.Transform;
import math.Vector3f;

public class DeferredRenderer {
	
	
	//Shaders
	public static final int GBUFFER_SHADER = Resources.loadShader("gbuffer.glsl"), SSAO_SHADER = Resources.loadShader("ssao.glsl"), BLUR_SHADER = Resources.loadShader("ssaoblur.glsl");
	private static final int GBUFFER_COLOR_SHADER = Resources.loadShader("gbuffercolor.glsl"), LIGHTING_SHADER = Resources.loadShader("gbufferlight2.glsl");
	public static final int TERRAIN_SHADER = Resources.loadShader("terrain.glsl");
	private static final int TERRAIN_VIEW_UNIFORM = GL20.glGetUniformLocation(TERRAIN_SHADER, "view"), TERRAIN_MODEL_UNIFORM = GL20.glGetUniformLocation(TERRAIN_SHADER, "model");
			
	private static final int GBUFFER_VIEW_UNIFORM = GL20.glGetUniformLocation(GBUFFER_SHADER, "view"), GBUFFER_PROJ_UNIFORM = GL20.glGetUniformLocation(GBUFFER_SHADER, "projection");
	private static final int GBUFFER_MODEL_UNIFORM = GL20.glGetUniformLocation(GBUFFER_SHADER, "model");
	private static final int LIGHTING_VIEW_UNIFORM = GL20.glGetUniformLocation(LIGHTING_SHADER, "view"), LIGHTING_PROJ_UNIFORM = GL20.glGetUniformLocation(LIGHTING_SHADER, "projection");
	int width, height;
	//MSAA stuff TODO
	
	public DeferredRenderer(int width, int height, boolean ssao, Matrix4f proj){
		this.width = width; this.height = height; this.ssao = ssao;
		GLShader.bindProgram(GBUFFER_SHADER);
		GLShader.setUniformMat4(GBUFFER_PROJ_UNIFORM, proj);
		GLShader.bindProgram(LIGHTING_SHADER);
		GLShader.setUniformMat4(LIGHTING_PROJ_UNIFORM, proj);
		GLShader.bindProgram(TERRAIN_SHADER);
		GLShader.setUniformMat4(TERRAIN_SHADER, "projection", proj);
		GLShader.bindProgram(SSAO_SHADER);
		GLShader.setUniformMat4(SSAO_SHADER, "projection", proj);
		initGBuffer();
		if(ssao)initSSAO();
		initLighting();
	}
	
	public DeferredRenderer(int width, int height, boolean ssao, FloatBuffer proj){
		this.width = width; this.height = height; this.ssao = ssao;
		GLShader.bindProgram(GBUFFER_SHADER);
		GLShader.setUniformMat4(GBUFFER_PROJ_UNIFORM, proj);
		GLShader.bindProgram(LIGHTING_SHADER);
		GLShader.setUniformMat4(LIGHTING_PROJ_UNIFORM, proj);
		GLShader.bindProgram(TERRAIN_SHADER);
		GLShader.setUniformMat4(TERRAIN_SHADER, "projection", proj);
		GLShader.bindProgram(SSAO_SHADER);
		GLShader.setUniformMat4(SSAO_SHADER, "projection", proj);
		initGBuffer();
		if(ssao)initSSAO();
		initLighting();
	}
	
	public void setProjection(Matrix4f proj) {
		GLShader.bindProgram(GBUFFER_SHADER);
		GLShader.setUniformMat4(GBUFFER_PROJ_UNIFORM, proj);
		GLShader.bindProgram(LIGHTING_SHADER);
		GLShader.setUniformMat4(LIGHTING_PROJ_UNIFORM, proj);
		GLShader.bindProgram(TERRAIN_SHADER);
		GLShader.setUniformMat4(TERRAIN_SHADER, "projection", proj);
		GLShader.bindProgram(SSAO_SHADER);
		GLShader.setUniformMat4(SSAO_SHADER, "projection", proj);
		GLShader.bindProgram(GBUFFER_SHADER);
	}
	
	public void setProjection(FloatBuffer proj) {
		GLShader.bindProgram(GBUFFER_SHADER);
		GLShader.setUniformMat4(GBUFFER_PROJ_UNIFORM, proj);
		GLShader.bindProgram(LIGHTING_SHADER);
		GLShader.setUniformMat4(LIGHTING_PROJ_UNIFORM, proj);
		GLShader.bindProgram(TERRAIN_SHADER);
		GLShader.setUniformMat4(TERRAIN_SHADER, "projection", proj);
		GLShader.bindProgram(SSAO_SHADER);
		GLShader.setUniformMat4(SSAO_SHADER, "projection", proj);
		GLShader.bindProgram(GBUFFER_SHADER);
	}
	
	public void prepare(Matrix4f view){
		glEnable(GL_CULL_FACE);
	    glCullFace(GL_BACK);
	    glEnable(GL11.GL_DEPTH_TEST);
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, gbuffer);
	    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	    GLShader.bindProgram(TERRAIN_SHADER);
		GLShader.setUniformMat4(TERRAIN_VIEW_UNIFORM, view);
	    GLShader.bindProgram(GBUFFER_SHADER);
	    GLShader.setUniformMat4(GBUFFER_VIEW_UNIFORM, view);
	}
	
	public void render(int mesh, int indices, MaterialMap mat, Transform transform){
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, mat.diffuse);
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, mat.specular >= 0 ? mat.specular : mat.diffuse);
		GLShader.setUniformMat4(GBUFFER_MODEL_UNIFORM, transform.getTransformation());
		GLRendering.renderMesh(mesh, indices);
	}
	
	public void render(int[] meshes, MaterialMap[] mats, Transform[] transforms){
		for(int i = 0; i < mats.length; i++){
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, mats[i].diffuse);
			GL13.glActiveTexture(GL13.GL_TEXTURE1);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, mats[i].specular >= 0 ? mats[i].specular : mats[i].diffuse);
			GLShader.setUniformMat4(GBUFFER_MODEL_UNIFORM, transforms[i].getTransformation());
			GLRendering.renderMesh(meshes[i * 2], meshes[i * 2 + 1]);
		}
	}
	
	public void render(int mesh, int indices, MaterialMap mat, Transform[] transforms){
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, mat.diffuse);
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, mat.specular >= 0 ? mat.specular : mat.diffuse);
		for(int i = 0; i < transforms.length; i++){
			GLShader.setUniformMat4(GBUFFER_MODEL_UNIFORM, transforms[i].getTransformation());
			GLRendering.renderMesh(mesh, indices);
		}
	}
	
	public void render(int mesh, int indices, int end, MaterialMap mat, Transform[] transforms){
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, mat.diffuse);
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, mat.specular >= 0 ? mat.specular : mat.diffuse);
		for(int i = 0; i < end; i++){
			GLShader.setUniformMat4(GBUFFER_MODEL_UNIFORM, transforms[i].getTransformation());
			GLRendering.renderMesh(mesh, indices);
		}
	}
	
	public void render(Terrain mesh){
		GLShader.bindProgram(TERRAIN_SHADER);
	    GLShader.setUniformMat4(TERRAIN_MODEL_UNIFORM, mesh.transform.getTransformation());
	    GL13.glActiveTexture(GL13.GL_TEXTURE0);
	    glBindTexture(GL_TEXTURE_2D, mesh.textures[0]);
	    GL13.glActiveTexture(GL13.GL_TEXTURE1);
	    glBindTexture(GL_TEXTURE_2D, mesh.textures[1]);
	    GL13.glActiveTexture(GL13.GL_TEXTURE2);
	    glBindTexture(GL_TEXTURE_2D, mesh.textures[2]);
	    GL13.glActiveTexture(GL13.GL_TEXTURE3);
	    glBindTexture(GL_TEXTURE_2D, mesh.textures[3]);
	    GL13.glActiveTexture(GL13.GL_TEXTURE4);
	    glBindTexture(GL_TEXTURE_2D, mesh.textures[4]);
	    GLRendering.renderMesh(mesh.mesh, mesh.getIndices());
		GLShader.bindProgram(GBUFFER_SHADER);
	}
	
	public void renderLighting(Matrix4f view, Vector3f viewpos, DirectionalLight dlight, int buffer, int width, int height){
		if(ssao){
			//SSAO
			GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, ssaoframebuffer);
		    glClear(GL_COLOR_BUFFER_BIT);
		    GLShader.bindProgram(SSAO_SHADER);
		    GL13.glActiveTexture(GL13.GL_TEXTURE0);
		    glBindTexture(GL_TEXTURE_2D, gposition);
		    GL13.glActiveTexture(GL13.GL_TEXTURE1);
		    glBindTexture(GL_TEXTURE_2D, gnormal);
		    GL13.glActiveTexture(GL13.GL_TEXTURE2);
		    glBindTexture(GL_TEXTURE_2D, noiseTexture);
		    GLShader.setUniformMat4(SSAO_SHADER, "view", view);
		    GLRendering.renderQuad();
		    //SSAO BLUR
	    	GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, ssaoblurframebuffer);
	    	glClear(GL_COLOR_BUFFER_BIT);
	    	GLShader.bindProgram(BLUR_SHADER);
	    	GL13.glActiveTexture(GL13.GL_TEXTURE0);
	        glBindTexture(GL_TEXTURE_2D, ssaobuffer);
	    	GLRendering.renderQuad();
		}
	    //LIGHTING
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, buffer);
		GL11.glViewport(0, 0, width, height);
	    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	    GLShader.bindProgram(LIGHTING_SHADER);
	    GLShader.setUniformMat4(LIGHTING_SHADER, "view", view);
	    GLShader.setUniformVec3(LIGHTING_SHADER, "viewPos", viewpos);
	    GLShader.setLight(LIGHTING_SHADER, dlight);
	    GL13.glActiveTexture(GL13.GL_TEXTURE0);
	    glBindTexture(GL_TEXTURE_2D, gposition);
	    GL13.glActiveTexture(GL13.GL_TEXTURE1);
	    glBindTexture(GL_TEXTURE_2D, gnormal);
	    GL13.glActiveTexture(GL13.GL_TEXTURE2);
	    glBindTexture(GL_TEXTURE_2D, galbedospec);
	    if(ssao){GL13.glActiveTexture(GL13.GL_TEXTURE3);
	    glBindTexture(GL_TEXTURE_2D, ssaoblurbuffer);}
		GLRendering.renderQuad();
	}
	
	//GBuffer
	int gbuffershader, gbuffer, gposition, gnormal, galbedospec, gdepth;
	public void initGBuffer(){
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
	boolean ssao;
	int ssaoframebuffer, ssaobuffer, noiseTexture, ssaoblurframebuffer, ssaoblurbuffer;
	public void initSSAO(){initSSAO(8, 4);}
	public void initSSAO(int kernel_size, int noise_size){
		initSSAOKernel(kernel_size);
		initSSAONoise(noise_size);
		
		ssaoframebuffer = GLFramebuffer.genFramebuffers();
		
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, ssaoframebuffer);
		ssaobuffer = GLTexture.createRedBuffer(width, height);
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, ssaobuffer, 0);
		
	    GLShader.setUniform(SSAO_SHADER, "gPosition", 0);
	    GLShader.setUniform(SSAO_SHADER, "gNormal", 1);
	    GLShader.setUniform(SSAO_SHADER, "texNoise", 2);
	    
    	ssaoblurframebuffer = GLFramebuffer.genFramebuffers();
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, ssaoblurframebuffer);
		ssaoblurbuffer = GLTexture.createRedBuffer(width, height);
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, ssaoblurbuffer, 0);
        GLShader.bindProgram(BLUR_SHADER);
        GLShader.setUniform(BLUR_SHADER, "ssaoInput", 0);
        
        
	}
	
	private static final int KERNEL_SIZE_UNIFORM = GL20.glGetUniformLocation(SSAO_SHADER, "kernelSize");
	public void initSSAOKernel(int kernel_size){
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
		
		GLShader.setUniform(KERNEL_SIZE_UNIFORM, kernel_size);
	    GLShader.setUniformVec3(SSAO_SHADER, "samples", ssaokernel);
	}
	
	public void initSSAONoise(int noise_size){noiseTexture = GLTexture.createNoiseTexture(noise_size, noise_size);}
	
	//Lighting
	boolean shadows = true;
	private void initLighting(){
		GLShader.bindProgram(LIGHTING_SHADER);
	    GLShader.setUniform(LIGHTING_SHADER, "gPosition", 0);
	    GLShader.setUniform(LIGHTING_SHADER, "gNormal", 1);
	    GLShader.setUniform(LIGHTING_SHADER, "gAlbedoSpec", 2);
        GLShader.setUniform(LIGHTING_SHADER, "ssao", 3);
        GLShader.setUniform(LIGHTING_SHADER, "shadowMap", 4);
        GLShader.setUniform(SHADOWS_SAMPLES_UNIFORM, 1);
        GLShader.setUniform(SSAO_UNIFORM, ssao);
		GLShader.setUniform(SHADOWS_UNIFORM, shadows);
	}
	private static final int SSAO_UNIFORM = GL20.glGetUniformLocation(LIGHTING_SHADER, "ssaoEnabled");
	private static final int SHADOWS_UNIFORM = GL20.glGetUniformLocation(LIGHTING_SHADER, "shadowsEnabled");
	private static final int SHADOWS_SAMPLES_UNIFORM = GL20.glGetUniformLocation(LIGHTING_SHADER, "shadowsEnabled");
	
	public void toggleSSAO(){ssao = !ssao; GLShader.setUniform(SSAO_UNIFORM, ssao);}
	public void toggleShadows(){shadows = !shadows; GLShader.setUniform(SHADOWS_UNIFORM, shadows);}
}
