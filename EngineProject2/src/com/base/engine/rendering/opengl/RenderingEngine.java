package com.base.engine.rendering.opengl;

import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import com.base.engine.core.CoreEngine;
import com.base.engine.core.Engine;
import com.base.engine.core.Time;
import com.base.engine.data.Material;
import com.base.engine.data.Mesh;
import com.base.engine.data.Resources;
import com.base.engine.rendering.Camera;
import com.base.engine.rendering.DirectionalLight;
import com.base.engine.rendering.GLFWWindow;
import com.base.engine.data.Shader;
import com.base.engine.data.Texture;
import com.base.engine.rendering.water.WaterTile;

import math.MathUtil;
import math.Matrix4f;
import math.Quaternion;

import com.base.math.Transform;
import math.Vector3f;

public class RenderingEngine implements Engine {
	public Camera camera;
	GLFWWindow window;
	GLContext context;
	DirectionalLight dlight;
	static DeferredRenderer gbuffer;
	static Terrain terrain;
	Shader watershader;
	
	RenderingStructure structure;
	Texture dudv;
	
	@Override
	public void start() {
		window = CoreEngine.window;
		context = new GLContext(33);
		context.viewport(window);
		window.setSizeCallback(this::resize);
		
		glClearColor(0f, 1f, 1f, 1f);

		glFrontFace(GL_CCW);
		glEnable(GL_CULL_FACE);
		glCullFace(GL_BACK);
		
		glEnable(GL_DEPTH_TEST);

		//glEnable(GL_DEPTH_CLAMP);

		glEnable(GL_TEXTURE_2D); 
		
		watershader = Resources.loadShader("water.glsl");
		
		camera = new Camera();
		camera.translate(-5,0,5);
		
		Vector3f dlightpos = new Vector3f(-2f, 8f,-4f);
		dlight = new DirectionalLight(new Vector3f().sub(dlightpos), new Vector3f(0.3f,0.3f,0.3f), new Vector3f(0f,1f,1f), new Vector3f(1.0f, 1.0f, 1.0f));
		
		terrain = new Terrain(80, 10, -0.5f,-0.5f, GLTexture.createTextures(new String[]{"./res/textures/bricks.png", "./res/textures/mud.png", "./res/textures/gauge.png", "./res/textures/grassTexture.png", "./res/textures/blendMap.png"}, 3));
		
		proj = new Matrix4f().perspective(fov, (float) window.getWidth() / (float) window.getHeight(), .1f, 1000f);
		//org.joml.Matrix4f jm = new org.joml.Matrix4f().ortho(-400, 400, -300, 300, 0.1f, 1000f, false);//.orthographic(fov, window.getWidth() / window.getHeight(), .1f, 1000f);
	    gbuffer = new DeferredRenderer(window.getWidth(), window.getHeight());
	    gbuffer.initShaders("gbuffer.glsl", "terrain.glsl", "gbufferlight2.glsl", "ssao.glsl", "ssaoblur.glsl");
	    gbuffer.setProjection(proj);
	    gbuffer.init();
	    
	    structure = new RenderingStructure(10, 10);
		
		Material bricks = new Material(Resources.loadTexture("bricks.png"));
		
		
		//Mesh dragon = Resources.loadMesh("dragon.obj");
		
		Mesh box = Resources.loadMesh("Basic Cube.obj");
		//box = Resources.loadMesh("capsule.obj");
		
		MeshRenderer mesh = new MeshRenderer(box, bricks);
		
	    structure.add(mesh, new Transform(0, 5, 0));
	    structure.add(mesh, new Transform(0, 5, 10));
	    structure.add(mesh, new Transform(10, 5, 0));
	    structure.add(mesh, new Transform(0, 7, 6));
	    structure.add(mesh, new Transform(1, 1, 6));
	    structure.add(mesh, new Transform(8, 5, 8));
	    
	    structure.terrain = terrain;
	    
	    waterbuffer = new WaterRenderer(320, 180, window.getWidth(), window.getHeight());
	    waterbuffer.initShader("water.glsl", "projection", "view", "model", proj);
	    
	    water = new WaterTile(0, 0, 0, 80);
	    Quaternion q = new Quaternion().rotate(0, 0, 180 * MathUtil.RAD);
	    waterTransform = new Matrix4f().translationRotateScale(water.x, water.y, water.z, q.x, q.y, q.z, q.w, water.size, water.size, water.size);
	    
	    dudv = Resources.loadTexture("dudv_old.png");
	    
	    GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
	}
	WaterRenderer waterbuffer;
	WaterTile water;
	Matrix4f waterTransform;
	Camera camera2 = new Camera();
	Matrix4f proj;
	static float fov = 45f, exposure = 1.0f;
	@Override
	public void run() {
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
	    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
	    
	    Matrix4f view = camera.getViewMatrix();
	    float distance = 2 * (camera.pos.y - water.y);
	    camera2.pos.set(camera.pos).y -= distance;
	    camera2.pitch = -camera.pitch; camera2.yaw = camera.yaw;
	    camera2.hasRotated = true; camera2.hasMoved = true;
	    Matrix4f view2 = camera2.getViewMatrix();
	    
	    //Render scene
	    GLShader.bindProgram(gbuffer.terrain_shader.program);
	    GLShader.setUniformVec4(gbuffer.terrain_shader.program, "clip_plane", 0, 0, 0, 0);
	    GLShader.bindProgram(gbuffer.gbuffer_shader.program);
	    GLShader.setUniformVec4(gbuffer.gbuffer_shader.program, "clip_plane", 0, 0, 0, 0);
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, gbuffer.gbuffer);
	    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	    structure.render(gbuffer.gbuffer_shader, view, null);
	    structure.renderTerrain(gbuffer.terrain_shader, view, null);
	    gbuffer.renderLighting(view, camera.pos, dlight, 0, window.getWidth(), window.getHeight());
	    
	    GLFramebuffer.copyDepthBuffer(gbuffer.gdepth, 0, window.getWidth(), window.getHeight());
	    
	    //Render water refraction
	    GLShader.bindProgram(gbuffer.terrain_shader.program);
	    GLShader.setUniformVec4(gbuffer.terrain_shader.program, "clip_plane", 0, -1, 0, water.y);
	    GLShader.bindProgram(gbuffer.gbuffer_shader.program);
	    GLShader.setUniformVec4(gbuffer.gbuffer_shader.program, "clip_plane", 0, -1, 0, water.y);
	    GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, gbuffer.gbuffer);
	    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	    structure.render(gbuffer.gbuffer_shader, view, null);
	    structure.renderTerrain(gbuffer.terrain_shader, view, null);
	    gbuffer.renderLighting(view, camera.pos, dlight, waterbuffer.refr_framebuffer, waterbuffer.refr_width, waterbuffer.refr_height);
	    
	    //Render water reflection
	    GLShader.bindProgram(gbuffer.terrain_shader.program);
	    GLShader.setUniformVec4(gbuffer.terrain_shader.program, "clip_plane", 0, 1, 0, water.y);
	    GLShader.bindProgram(gbuffer.gbuffer_shader.program);
	    GLShader.setUniformVec4(gbuffer.gbuffer_shader.program, "clip_plane", 0, 1, 0, water.y);
	    GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, gbuffer.gbuffer);
	    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	    structure.render(gbuffer.gbuffer_shader, view2, null);
	    structure.renderTerrain(gbuffer.terrain_shader, view2, null);
	    gbuffer.renderLighting(view2, camera2.pos, dlight, waterbuffer.refl_framebuffer, waterbuffer.refl_width, waterbuffer.refl_height);
	    
	    
	    //Render water
	    move += wave_speed * Time.getDelta(); move %= 1;
	    waterbuffer.render(0, window.getWidth(), window.getHeight(), dudv.texture, view, waterTransform, move);
	    
		GLVertexArray.unbindVertexArray();
		window.swapBuffers();
	}
	
	public void resize() {
//		proj.perspective(fov, (float) window.getWidth() / (float) window.getHeight(), .1f, 1000f);
//		drenderer = new DeferredRenderer(window.getWidth(), window.getHeight(), true, proj);
//		GLShader.bindProgram(watershader);
//		GLShader.setUniformMat4(GL20.glGetUniformLocation(watershader, "projection"), proj);
	}
	
	@Override
	public void dispose() {
		gbuffer.dispose();
		waterbuffer.dispose();
	}
	
	public static float move = 0;
	public static final float wave_speed = 0.03f;
	
//	public static void shadows(){drenderer.toggleShadows();}
//	public static void ssao(){drenderer.toggleSSAO();}
	
}
