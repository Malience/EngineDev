package com.base.engine.rendering.opengl;

import static org.lwjgl.opengl.GL11.*;

import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;

import com.base.engine.core.CoreEngine;
import com.base.engine.core.util.Util;
import com.base.engine.data.Mesh;
import com.base.engine.rendering.Camera;
import com.base.engine.rendering.DirectionalLight;
import com.base.engine.rendering.GLFWWindow;
import com.base.engine.rendering.MaterialMap;
import com.base.engine.rendering.PointLight;
import com.base.engine.rendering.Projection;
import com.base.engine.rendering.SpotLight;
import math.Matrix4f;
import com.base.math.Transform;
import math.Vector3f;

public class GLFWRenderingEngine {
	public Camera camera;
	GLFWWindow window;
	GLContext context;
	int shader, outlineshader, skyboxshader, reflectshader, refractshader, shadowshader, debugshader, 
		gaussshader, bloomshader, blankshader, invisibleshader, invisibleshadowshader, fogshader, terrainshader;
	static final int both = 7000;
	static final int DEPTH_WIDTH = both, DEPTH_HEIGHT = both;
	int shadowFramebuffer, shadowTexture;
	int bloombuffer, bloombuffers[], pingbuffer[], pingbuffers[], outlinebuffer;
	float vertices[] = {
		    // Positions          // Texture Coords
		     1f,  1f, 0.0f,  1.0f, 1.0f,   // Top Right
		     1f, -1f, 0.0f,  1.0f, 0.0f,   // Bottom Right
		     -1f, -1f, 0.0f, 0.0f, 0.0f,   // Bottom Left
		     -1f,  1f, 0.0f, 0.0f, 1.0f    // Top Left 
		};
	int indices[] = {
			0, 1, 3,
			1, 3, 2
	};
	
	Transform transform0 = new Transform();
	Transform transform1 = new Transform();
	Transform transform2 = new Transform();
	Transform transform3 = new Transform();
	Transform transform4 = new Transform();
	Transform transform5 = new Transform();
	Mesh mesh, box, plane, grass;
	int quad;
	int skyboxtexture;
	static int[] tex;
	Matrix4f ortho;
	Projection persp;
	MaterialMap material, grassmat;
	DirectionalLight dlight;
	PointLight[] plights;
	SpotLight slight;
	static DeferredRenderer drenderer;
	public void start() {
		window = CoreEngine.window;
		context = new GLContext(33);
		context.viewport(window);
		window.setSizeCallback(context);
		
		glClearColor(0f, 0f, 0f, 1f);

		glFrontFace(GL_CCW);
		glEnable(GL_CULL_FACE);
		glCullFace(GL_BACK);
		
		glEnable(GL_DEPTH_TEST);

		//glEnable(GL_DEPTH_CLAMP);

		//glEnable(GL_TEXTURE_2D); 
		
//		shader = ShaderManager.loadShader("shader");
//		outlineshader = ShaderManager.loadShader("outline");
//		skyboxshader = ShaderManager.loadShader("skyboxshader");
//		reflectshader = ShaderManager.loadShader("reflect");
//		refractshader = ShaderManager.loadShader("refract");
//		debugshader = ShaderManager.loadShader("debug");
//		shadowshader = ShaderManager.loadShader("shadow");
//		gaussshader = ShaderManager.loadShader("gaussblur");
//		bloomshader = ShaderManager.loadShader("bloom");
//		blankshader = ShaderManager.loadShader("blank");
//		invisibleshader = ShaderManager.loadShader("invisible");
//		invisibleshadowshader = ShaderManager.loadShader("invisibleshadows");
//		fogshader = ShaderManager.loadShader("fog");
//		terrainshader = ShaderManager.loadShader("terrain");
		
//		tex = GLTexture.genTextures(1);
//		GLTexture.createTextures(new String[]{"bricks.png"}, tex, filter);
//		
//		int[] grasstex = GLTexture.genTextures(1);
//		GLTexture.createTextures(new String[]{"grassTexture.png"}, grasstex, filter);
//		grassmat = new MaterialMap(grasstex[0]);
//		
//		terrainTextures = GLTexture.genTextures(5);
//		GLTexture.createTextures(new String[]{"bricks.png", "mud.png", "gauge.png", "path.png", "blendMap.png"}, terrainTextures, filter);
		
		mesh = new Mesh("./res/meshes/dragon.obj");
		mesh.load();
		
		box = new Mesh("./res/meshes/Basic Cube.obj");
		box.load();
		
		plane = new Mesh("./res/meshes/plane3.obj");
		plane.load();
		
		grass = new Mesh("./res/meshes/grassModel.obj");
		grass.load();
		
		quad = GLVertexArray.setUpVertexArray(vertices, indices, true, 3,2);
		
		shadowFramebuffer = GLFramebuffer.genFramebuffers();
		shadowTexture = GLTexture.createDepthbuffer(DEPTH_WIDTH, DEPTH_HEIGHT);
		GLFramebuffer.bindDepthbuffer(shadowFramebuffer, shadowTexture);
		
		Vector3f dlightpos = new Vector3f(-2f, 8f,-5f);
		float size = 20f;
		//ortho = Matrix4f.createPerspective(45f, 1f, -1.0f, 20f);
		ortho = new Matrix4f().orthographic(-size, size, -size, size, 1.0f, 20f);
		Matrix4f lightView = new Matrix4f().setLookAt(dlightpos, new Vector3f( 0.0f, 0.0f,  0.0f));//new Matrix4f(new org.joml.Matrix4f().lookAt(2,10f,-10f,0,0,0,0,1,0));//Matrix4f.createLookAtMatrix(dlightpos, new Vector3f( 0.0f, 0.0f,  0.0f));
		ortho.mul(lightView);
		
		bloombuffer = GLFramebuffer.genFramebuffers();
		bloombuffers = new int[2];
		GLFramebuffer.setUpBloomBuffer(bloombuffer, bloombuffers, window.getWidth(), window.getHeight());
		
		pingbuffer = new int[2];
		GLFramebuffer.genFramebuffers(pingbuffer);
		pingbuffers = new int[2];
		GLFramebuffer.setUpPingBuffer(pingbuffer, pingbuffers, window.getWidth(), window.getHeight());
		
		outlinebuffer = GLFramebuffer.createStencilBuffer(window.getWidth(), window.getHeight())[0];
		
		//DataManager.setModelTextures(model, tex);
		
		camera = new Camera();
		//camera.translate(0, 5, 5);
		//transform0.rotateX((float)Math.toRadians(-55));
		transform0.translate(8,2,0);
		transform1.translate(0,0,0);
		transform2.translate(0,0,0);
		transform3.translate(-16,2,0);
		transform4.translate(-16,8,0);
		transform5.translate(5,0,0);
		
		material = new MaterialMap(tex[0], 16f);
		Vector3f ambient = new Vector3f(0.1f, 0.1f, 0.1f);
		//Vector3f diffuse = new Vector3f(0.1f, 0.1f, 0.1f);
		Vector3f specular = new Vector3f(1.0f, 1.0f, 1.0f);
		
		dlight = new DirectionalLight(new Vector3f().sub(dlightpos), new Vector3f(0.4f,0.4f,0.4f), new Vector3f(1f,1f,1f), new Vector3f(1.0f, 1.0f, 1.0f));
		plights = new PointLight[]{
			new PointLight(new Vector3f(-1f, 3f, 0f), 1.0f, 0.22f, .20f, ambient, new Vector3f(1f,0,0), specular),
			new PointLight(new Vector3f(-5f, 6f, 1f), 1.0f, 0.22f, .20f, ambient, new Vector3f(0,0,1f), specular),
			new PointLight(new Vector3f(4f, 4f, 2f), 1.0f, 0.22f, .20f, ambient, new Vector3f(0,1f,1f), specular),
			new PointLight(new Vector3f(0f, 0f, 0f), 1.0f, 0.22f, .20f, ambient, new Vector3f(0,1f,0), specular),
			new PointLight(new Vector3f(8f, 6f, 0f), 1.0f, 0.2f, .20f, ambient, new Vector3f(1f,0f,1f), specular)
			//new PointLight(new float[]{-5f, -5f, 0f}, 1.0f, 0.7f, 1.8f, ambient, diffuse, specular),
			//new PointLight(new float[]{-4f, -3f, 0f}, 1.0f, 0.7f, 1.8f, ambient, diffuse, specular),
			//new PointLight(new float[]{-2f, -1f, 0f}, 1.0f, 0.7f, 1.8f, ambient, diffuse, specular),
			//new PointLight(new float[]{8f, 8f, 8f}, 1.0f, 0.7f, 1.8f, ambient, diffuse, specular),
			//new PointLight(new float[]{12f, -1f, 0f}, 1.0f, 0.7f, 1.8f, ambient, diffuse, specular)	
		};
		slight = new SpotLight(new Vector3f(-1f, -1f, 0f), new Vector3f(1f, 1f, 0f), 0.91f, 0.82f, new Vector3f(1f,0f,0f), new Vector3f(1f,1f,1f), new Vector3f(1.0f, 1.0f, 1.0f));
		
		//MathTest.outputMatrix(transform.getTransformation());
		aspectRatio = window.getWidth()/(float)window.getHeight();
		//projection = new Matrix4f(new org.joml.Matrix4f().ortho(-10f, 10f, -10f, 10f, -1f, 7.5f));
		persp = new Projection(fov, window.getWidth(), window.getHeight(), .1f, 1000f);//Matrix4f.createPerspective(fov, aspectRatio, .1f, 1000f);
		//fb = Util.createFloatBuffer(16);
		
		
		
		skyboxtexture = GLTexture.createCubeMap(new String[]{"right.jpg","left.jpg","top.jpg","bottom.jpg","back.jpg","front.jpg"});
		
		skybox = new Skybox(skyboxshader, skyboxtexture);
		GLShader.bindProgram(skybox.shader);
	    GLShader.setUniformMat4(skybox.projection, persp.matrix);
		
		
		
//		fogPass = new GLPass(gbuffer.framebuffer, 0, fogshader);
//	    GLShader.bindProgram(fogPass.shader);
//	    GLShader.setUniformMat4(fogPass.projection, persp.matrix);
//	    GLShader.setUniform(fogPass.shader, "gPosition", 0);
//	    GLShader.setUniform(fogPass.shader, "gAlbedoSpec", 1);
//	    GLShader.setUniformVec3(fogshader, "skycolor", new float[]{1.0f,1.0f,1.0f});
		
		outlinePass1 = new GLPassMesh(0, 0, blankshader);
		outlinePass1.put(0, box.vao, box.indices, transform0.getTransformation());
		GLShader.bindProgram(outlinePass1.shader);
	    GLShader.setUniformMat4(outlinePass1.projection, persp.matrix);
		
	    outlinePass2 = new GLPassMesh(0, 0, outlineshader);
	    outlinePass2.meshes = outlinePass1.meshes;
	    outlinePass2.transforms = outlinePass1.transforms;
		GLShader.bindProgram(outlinePass2.shader);
	    GLShader.setUniformMat4(outlinePass2.projection, persp.matrix);
	    
//		geometryPass = new GLPassMeshMat(gbuffer.framebuffer, GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT, gbuffershader);
//		geometryPass.put(0, box, boxcount, transform0.getTransformation(), material);
//		//geometryPass.put(1, plane, planecount, transform2.getTransformation());
//		geometryPass.put(2, mesh, icount, transform1.getTransformation());
//		//geometryPass.put(3, terrain.mesh, terrain.getIndices(), transform2.getTransformation());
//		GLShader.bindProgram(geometryPass.shader);
//	    GLShader.setUniformMat4(geometryPass.projection, persp.matrix);
	    
//	    geometryColorPass = new GLPassMesh(0, 0, outlineshader);
//	    geometryPass.put(0, box, boxcount, transform0.getTransformation(), material);
//		//geometryPass.put(1, plane, planecount, transform2.getTransformation());
//		//geometryPass.put(3, terrain.mesh, terrain.getIndices(), transform2.getTransformation());
//		GLShader.bindProgram(geometryPass.shader);
//	    GLShader.setUniformMat4(geometryPass.projection, persp.matrix);
	    
	    
	    invisiblePass = new GLPassMeshMat(0, 0, invisibleshader);
	    invisiblePass.put(0, grass.vao, grass.indices, transform5.getTransformation(), grassmat);
	    GLShader.bindProgram(invisiblePass.shader);
	    GLShader.setUniformMat4(invisiblePass.projection, persp.matrix);
	    
	    
	    GLShader.bindProgram(terrainshader);
	    GLShader.setUniformMat4(terrainshader, "projection", persp.matrix);
	    GLShader.setUniform(terrainshader, "backgroundTexture", 0);
	    GLShader.setUniform(terrainshader, "rTexture", 1);
	    GLShader.setUniform(terrainshader, "gTexture", 2);
	    GLShader.setUniform(terrainshader, "bTexture", 3);
	    GLShader.setUniform(terrainshader, "blendMap", 4);
	    
	    //drenderer= new DeferredRenderer(window.getWidth(), window.getHeight(), true, true, persp.matrix);
	}
	int[] terrainTextures;
	public Terrain terrain;
	Skybox skybox;
	Framebuffer gbuffer, ssaobuffer, ssaoblurbuffer;
	int noiseTexture;
	Vector3f[] ssaokernel;
	static float aspectRatio;
	static float fov = 45f;
	static float exposure = 1.0f;
	SSAOShading ssaoshading1;
	GLPass fogPass;
	GLPassMesh outlinePass1, outlinePass2, geometryColorPass;
	GLPassMeshMat geometryPass, invisiblePass;
	public void run() {
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
	    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
		
	    Matrix4f view = camera.getViewMatrix();
	    
//		glDisable(GL_DEPTH_TEST);
//		glEnable(GL_STENCIL_TEST);
//		GL11.glStencilOp(GL_KEEP, GL_KEEP, GL_REPLACE);
//		glStencilFunc(GL_ALWAYS, 1, 0xFF);
//		glStencilMask(0xFF);
//		outlinePass1.render(view);
//		glStencilFunc(GL_NOTEQUAL, 1, 0xFF);
//		glStencilMask(0x00);
//		glDisable(GL_STENCIL_TEST);
//		glEnable(GL_DEPTH_TEST);
//	    
//		GLShader.bindProgram(shadowshader);
//		GLShader.setUniformMat4(shadowshader, "lightView", ortho);
//		glViewport(0, 0, DEPTH_WIDTH, DEPTH_HEIGHT);
//		
//		GLFramebuffer.bindFramebuffer(shadowFramebuffer);
//	    glClear(GL_DEPTH_BUFFER_BIT);
//		
//	    glCullFace(GL_FRONT);
	    
//		GLShader.setUniformMat4(shadowshader, "model", transform2.getTransformation());
		//GLRendering.renderMesh(plane, planecount);
		GLShader.setUniformMat4(shadowshader, "model", transform1.getTransformation());
		GLRendering.renderMesh(mesh.vao, mesh.indices);
//		GLShader.setUniformMat4(shadowshader, "model", transform0.getTransformation());
//		GLRendering.renderMesh(box, boxcount);
//		GLShader.setUniformMat4(shadowshader, "model", transform2.getTransformation());
//		GLRendering.renderMesh(terrain.mesh, terrain.indices);
//		
//		//TODO: Shadows for transparent things
//		glDisable(GL_CULL_FACE);
//		GLShader.bindProgram(invisibleshadowshader);
//		GLShader.setUniformMat4(invisibleshadowshader, "lightView", ortho);
//		GLShader.setUniformMat4(invisibleshadowshader, "model", transform5.getTransformation());
//		GL13.glActiveTexture(GL13.GL_TEXTURE0);
//	    glBindTexture(GL_TEXTURE_2D, grassmat.diffuse);
//	    GLRendering.renderMesh(grass, grasscount);
//		glEnable(GL_CULL_FACE);
//		
		glCullFace(GL_BACK);
//		
		GLFramebuffer.unbindFramebuffer();
//		
		glViewport(0, 0, window.getWidth(), window.getHeight());
		
		// 1. geometry pass: render all geometric/color data to g-buffer 
		//gbufferShading.renderFirstPass(view, projection);
//		geometryPass.render(view);
//	   
//	    GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, gbuffer.framebuffer);
//	    GLShader.bindProgram(terrainshader);
//	    GLShader.setUniformMat4(terrainshader, "view", view);
//	    GLShader.setUniformMat4(terrainshader, "model", terrain.transform.getTransformation());
//	    GL13.glActiveTexture(GL13.GL_TEXTURE0);
//	    glBindTexture(GL_TEXTURE_2D, terrainTextures[0]);
//	    GL13.glActiveTexture(GL13.GL_TEXTURE1);
//	    glBindTexture(GL_TEXTURE_2D, terrainTextures[1]);
//	    GL13.glActiveTexture(GL13.GL_TEXTURE2);
//	    glBindTexture(GL_TEXTURE_2D, terrainTextures[2]);
//	    GL13.glActiveTexture(GL13.GL_TEXTURE3);
//	    glBindTexture(GL_TEXTURE_2D, terrainTextures[3]);
//	    GL13.glActiveTexture(GL13.GL_TEXTURE4);
//	    glBindTexture(GL_TEXTURE_2D, terrainTextures[4]);
//	    GLRendering.renderMesh(terrain.mesh, terrain.getIndices());
	    
//	    GLShader.bindProgram(gbuffercolorshader);
//	    GLShader.setUniformMat4(gbuffercolorshader, "view", view);
//	    GLShader.setUniformMat4(gbuffercolorshader, "projection", persp.matrix);
//	    if(CoreEngine.player != null){
//	    	GLShader.setUniformMat4(gbuffercolorshader, "model", CoreEngine.player.transform.getTransformation());
//		    GLShader.setUniformVec3(gbuffercolorshader, "color", CoreEngine.player.color);
//		    GLRendering.renderMesh(box, boxcount);
//	    }
//	    for(int i = 0; i < CoreEngine.players.length; i++){
//	    	if(CoreEngine.players[i] == null) continue;
//		    GLShader.setUniformMat4(gbuffercolorshader, "model", CoreEngine.players[i].transform.getTransformation());
//		    GLShader.setUniformVec3(gbuffercolorshader, "color", CoreEngine.players[i].color);
//		    GLRendering.renderMesh(box, boxcount);
//	    }
	    
		
		// SSAO
//	    ssaoshading1.renderFirstPass(view, persp.matrix);
        
	    // 2. lighting pass: use g-buffer to calculate the scene's lighting
	    
	    
//		drenderer.prepare(view);
//		drenderer.render(mesh, icount, material, transform1);
//		drenderer.renderSSAO();
//		drenderer.prepareLighting(view, camera.getPos(), 0);
//		drenderer.renderLighting(dlight, plights, ortho, shadowTexture);
        
        
        
        
//	    GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, pingbuffer[0]);
//	    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
//        //debugshader = ShaderManager.loadShader("debug");
//        GLShader.bindProgram(gaussshader);
//        GL13.glActiveTexture(GL13.GL_TEXTURE0);
//	    glBindTexture(GL_TEXTURE_2D, bloombuffers[1]);
//	    GLShader.setUniform(gaussshader, "image", 0);
//	    GLShader.setUniform(gaussshader, "horizontal", 1);
//	    GLRendering.renderQuad();
//	    
//	    GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, pingbuffer[1]);
//	    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
//        //debugshader = ShaderManager.loadShader("debug");
//        GLShader.bindProgram(gaussshader);
//        GL13.glActiveTexture(GL13.GL_TEXTURE0);
//	    glBindTexture(GL_TEXTURE_2D, pingbuffers[0]);
//	    GLShader.setUniform(gaussshader, "image", 0);
//	    GLShader.setUniform(gaussshader, "horizontal", 0);
//	    GLRendering.renderQuad();
//	    
//	    
//	    
//	    
//	    GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
//	    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
//        //debugshader = ShaderManager.loadShader("debug");
//        GLShader.bindProgram(bloomshader);
//        GL13.glActiveTexture(GL13.GL_TEXTURE0);
//	    glBindTexture(GL_TEXTURE_2D, bloombuffers[0]);
//	    GLShader.setUniform(bloomshader, "scene", 0);
//	    GL13.glActiveTexture(GL13.GL_TEXTURE1);
//	    glBindTexture(GL_TEXTURE_2D, pingbuffers[0]);
//	    GLShader.setUniform(bloomshader, "bloomBlur", 1);
//	    GLShader.setUniform(bloomshader, "exposure", exposure);
//	    GLShader.setUniform(bloomshader, "gamma", gamma);
//	    GLShader.setUniform(bloomshader, "bloom", bloom);
//	    GLRendering.renderQuad();
	    
		
	    
//	    GLFramebuffer.copyDepthBuffer(drenderer.gbuffer, 0, window.getWidth(), window.getHeight());
	    

	    
//		glEnable(GL_DEPTH_TEST);
//		GLShader.bindProgram(shadowshader);
//		GLShader.setUniformMat4(shadowshader, "lightView", ortho);
//		glViewport(0, 0, DEPTH_WIDTH, DEPTH_HEIGHT);
//		
//		GLFramebuffer.bindFramebuffer(shadowFramebuffer);
//	    glClear(GL_DEPTH_BUFFER_BIT);
//		
//	    glCullFace(GL_FRONT);
//	    
//		GLShader.setUniformMat4(shadowshader, "model", transform2.getTransformation());
//		GLRendering.renderMesh(plane, planecount);
//		GLShader.setUniformMat4(shadowshader, "model", transform1.getTransformation());
//		GLRendering.renderMesh(mesh, icount);
//		GLShader.setUniformMat4(shadowshader, "model", transform0.getTransformation());
//		GLRendering.renderMesh(box, boxcount);
//		
//		glCullFace(GL_BACK);
//		
//		GLFramebuffer.unbindFramebuffer();
//		
//		glViewport(0, 0, window.getWidth(), window.getHeight());
//		glClear(clearcode);
//		
//		//GLShader.bindProgram(shader);
//		// ... draw rest of the scene
//		
//		//GLRendering.renderModelFromManager(model, shader);
//		
//		GLShader.bindProgram(lightshader);
//		
//		//GLShader.setMaterial(lightshader, material);
//		GLShader.setLight(lightshader, dlight);
//		GLShader.setLight(lightshader, plights);
//		GLShader.setLight(lightshader, slight);
//		
//		GLShader.setUniform(lightshader, "lights", 0);
//		GLShader.setUniformVec3(lightshader, "viewPos", camera.getPos());
//		GLShader.setUniformMat4(lightshader, "view", camera.getViewMatrix());
//		GLShader.setUniformMat4(lightshader, "projection", projection);
//		GLShader.setUniformMat4(lightshader, "lightSpaceMatrix", ortho);
//		GLShader.setUniform(lightshader, "shadowSamples", 1);
//		
//		GL13.glActiveTexture(GL13.GL_TEXTURE14);
//        glBindTexture(GL_TEXTURE_2D, shadowTexture);
//        GLShader.setUniform(lightshader, "shadowMap", 14);
//        
//		//GLShader.setUniformVec4(lightshader, "lightColor", 1,0,0,1);
//		glStencilMask(0x00);
//		GLShader.setUniformMat4(lightshader, "model", transform2.getTransformation());
//		GLRendering.renderMesh(plane, planecount, lightshader, material);
//		
//		
//		

//		
//		//GL11.glDisable(GL11.GL_BLEND);

//	    GL13.glActiveTexture(GL13.GL_TEXTURE0);
//	    
//		GLShader.bindProgram(reflectshader);
//		GLShader.setUniformMat4(reflectshader, "view", camera.getViewMatrix());
//		GLShader.setUniformMat4(reflectshader, "model", transform3.getTransformation());
//		GLShader.setUniformMat4(reflectshader, "projection", persp.matrix);
//		GLShader.setUniformVec3(reflectshader, "cameraPos", camera.getPos());
//		//GLShader.setUniform(skyboxshader, "cubemap", 0);
//		// ... set view and projection matrix
//		GLRendering.renderMesh(box, boxcount);
//		
//		GLShader.bindProgram(refractshader);
//		GLShader.setUniformMat4(refractshader, "view", camera.getViewMatrix());
//		GLShader.setUniformMat4(refractshader, "model", transform4.getTransformation());
//		GLShader.setUniformMat4(refractshader, "projection", persp.matrix);
//		GLShader.setUniformVec3(refractshader, "cameraPos", camera.getPos());
//		//GLShader.setUniform(skyboxshader, "cubemap", 0);
//		// ... set view and projection matrix
//		GLRendering.renderMesh(box, boxcount);

	    //glClear(GL_DEPTH_BUFFER_BIT);
	    
	    
		//skybox.render(view);
		
		
//		
//		glEnable(GL_STENCIL_TEST);
//		GL11.glStencilOp(GL_KEEP, GL_KEEP, GL_REPLACE);
//		glStencilFunc(GL_ALWAYS, 1, 0xFF);
//		glStencilMask(0xFF);
//		GLShader.setUniformMat4(lightshader, "model", transform1.getTransformation());
//		GLRendering.renderMesh(mesh, icount, lightshader, material);
//		GLShader.setUniformMat4(lightshader, "model", transform0.getTransformation());
//		GLRendering.renderMesh(box, boxcount, lightshader, material);
//		
//		glStencilFunc(GL_NOTEQUAL, 1, 0xFF);
//		glStencilMask(0x00); // disable writing to the stencil buffer
//		
		//GLFramebuffer.copyStencilBuffer(gbuffer, 0, window.getWidth(), window.getHeight());
		
//		glDisable(GL_DEPTH_TEST);
//		glEnable(GL_STENCIL_TEST);
//		outlinePass2.render(view);
//		glStencilMask(0xFF);
//		glDisable(GL_STENCIL_TEST);
//		glEnable(GL_DEPTH_TEST); 
//		
//		
//		glDisable(GL_CULL_FACE);
//		invisiblePass.render(view);
//		glEnable(GL_CULL_FACE);
		
	    if(debug){
		    GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	        //debugshader = ShaderManager.loadShader("debug");
	        GLShader.bindProgram(debugshader);
	        GL13.glActiveTexture(GL13.GL_TEXTURE0);
		    glBindTexture(GL_TEXTURE_2D, drenderer.gnormal);
		    GLShader.setUniform(debugshader, "texture0", 0);
		    GLShader.setUniformMat4(debugshader, "view", camera.getViewMatrix());
		    GLShader.setUniform(debugshader, "useView", useView);
		    GLRendering.renderQuad();
	    }
		
		
		GLVertexArray.unbindVertexArray();
		window.swapBuffers();
	}
	
	OutlineShading outline;
	
	
	public static void changeFov(float amt){
		if(fov >= 1.0f && fov <= 45.0f)
		  	fov -= amt;
		  if(fov <= 1.0f)
		  	fov = 1.0f;
		  if(fov >= 45.0f)
		  	fov = 45.0f;
		//projection.updatePerspectiveProjection(fov, aspectRatio);
	}
	
	public static boolean debug, useView, bloom, useSSAO = true, useShadows = true;
	public static float exposureStep = 0.1f;
	public static float gamma = 1.1f;
	public static int filter = 2;
	
	public static void useView(){useView = !useView;}
	public static void debug(){debug = !debug;}
	public static void bloom(){bloom = !bloom;}
	public static void shadows(){drenderer.toggleShadows();}
	public static void ssao(){drenderer.toggleSSAO();}
	
	public static void raiseFilter(){filter += 1; if(filter > GLContext.context.anisotropic_filtering) filter = (int)GLContext.context.anisotropic_filtering; setFilter();}
	public static void lowerFilter(){filter -= 1; if(filter < 0) filter = 0; setFilter();}
	private static void setFilter(){
		System.out.println("Filter: " + filter);
		for(int i = 0; i < tex.length; i++){
			GLTexture.setFiltering(tex[i], filter);
		}
	}
	
	public static void raiseExposure(){exposure += exposureStep; System.out.println("Exposure: " + exposure);}
	public static void lowerExposure(){exposure -= exposureStep; System.out.println("Exposure: " + exposure);}
	
	public static void raiseGamma(){gamma += exposureStep; System.out.println("Gamma: " + gamma);}
	public static void lowerGamma(){gamma -= exposureStep; System.out.println("Gamma: " + gamma);}
}
