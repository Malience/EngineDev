package com.base.engine.rendering.opengl;

import static org.lwjgl.opengl.GL11.*;

import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL11;

import com.base.engine.core.CoreEngine;
import com.base.engine.core.GameObject;
import com.base.engine.core.util.Util;
import com.base.engine.data.Mesh;
import com.base.engine.data.Resources;
import com.base.engine.physics.Frustum;
import com.base.engine.physics.Octree;
import com.base.engine.physics.PhysicsEngine;
import com.base.engine.rendering.Camera;
import com.base.engine.rendering.DirectionalLight;
import com.base.engine.rendering.GLFWWindow;
import com.base.engine.rendering.MaterialMap;
import com.base.engine.rendering.PointLight;
import com.base.engine.rendering.Projection;
import com.base.engine.rendering.SpotLight;
import com.base.engine.rendering.TContainer;
import math.Matrix4f;
import com.base.math.Transform;
import math.Vector3f;

public class GLFWRenderingEngine2 {
	public Camera camera;
	GLFWWindow window;
	GLContext context;
	Transform transform1 = new Transform();
	Mesh mesh, box;
	static int[] tex;
	Matrix4f ortho;
	Projection persp;
	MaterialMap material;
	DirectionalLight dlight;
	static DeferredRenderer drenderer;
	static Terrain terrain;
	
	public void start() {
		window = CoreEngine.window;
		context = new GLContext(33);
		context.viewport(window);
		window.setSizeCallback(context);
		
		glClearColor(0f, 1f, 1f, 1f);

		glFrontFace(GL_CCW);
		glEnable(GL_CULL_FACE);
		glCullFace(GL_BACK);
		
		glEnable(GL_DEPTH_TEST);

		//glEnable(GL_DEPTH_CLAMP);

		glEnable(GL_TEXTURE_2D); 
		
		tex = GLTexture.genTextures(1);
		GLTexture.createTextures(new String[]{"bricks.png"}, tex, 3);
		
		mesh = Resources.loadMesh("dragon.obj");
		
		box = Resources.loadMesh("Basic Cube.obj");
		//box = Resources.loadMesh("capsule.obj");
		
		Vector3f dlightpos = new Vector3f(-2f, 8f,-4f);
		float size = 20f;
		//ortho = Matrix4f.createPerspective(45f, 1f, -1.0f, 20f);
		ortho = new Matrix4f().orthographic(-size, size, -size, size, 1.0f, 20f);
		Matrix4f lightView = new Matrix4f().setLookAt(dlightpos);//new Matrix4f(new org.joml.Matrix4f().lookAt(2,10f,-10f,0,0,0,0,1,0));//Matrix4f.createLookAtMatrix(dlightpos, new Vector3f( 0.0f, 0.0f,  0.0f));
		ortho.mul(lightView);
		
		camera = new Camera();
		camera.translate(-5,0,5);
		//camera.lookAt(new Vector3f());
		//transform1.translate(0, 1,5);
		transform1.rotate(0, 45, 45);
		
		material = new MaterialMap(tex[0], 16f);
		
		dlight = new DirectionalLight(new Vector3f().sub(dlightpos), new Vector3f(0.3f,0.3f,0.3f), new Vector3f(0f,1f,1f), new Vector3f(1.0f, 1.0f, 1.0f));
		
		persp = new Projection(fov, window.getWidth(), window.getHeight(), .1f, 1000f);//Matrix4f.createPerspective(fov, aspectRatio, .1f, 1000f);
		
		terrain = new Terrain(-0.5f,-0.5f, GLTexture.createTextures(new String[]{"bricks.png", "mud.png", "gauge.png", "grassTexture.png", "blendMap.png"}, 3));
		
		//org.joml.Matrix4f jm = new org.joml.Matrix4f().ortho(-400, 400, -300, 300, 0.1f, 1000f, false);//.orthographic(fov, window.getWidth() / window.getHeight(), .1f, 1000f);
	    drenderer = new DeferredRenderer(window.getWidth(), window.getHeight(), true, new Matrix4f().perspective(fov, window.getWidth() / window.getHeight(), .1f, 1000f));
		//drenderer = new DeferredRenderer(window.getWidth(), window.getHeight(), true, jm.get(Util.createFloatBuffer(16 * 4)));
	    drenderer.toggleShadows();
	    drenderer.toggleSSAO();
	    
	    proj = new Matrix4f().perspective(fov, window.getWidth() / window.getHeight(), .1f, 1000f);
	    view = new Matrix4f().setLookAt(new Vector3f(-10,4,-10f), new Vector3f());
	    
	    transforms = new TContainer(10);
	    
	    shader = Resources.loadShader("shader.glsl");
//	    Octree octree = new Octree(60,60,60);
//	    float step = 5;
//	    transforms = new TContainer(1000);
//	    for(int i = 0; i < 10; i++) for(int j = 0; j < 10; j++) for(int k = 0; k < 10; k++)
//	    	octree.add(new GameObject(step * i, step * j, step * k));
//	    octree.update(1.0f);
//	    
//	    Frustum f = new Frustum(camera, persp);
//	    
//	    octree.get(f, transforms);
	    
	    System.out.println(camera.getViewMatrix());
	    
	    Transform one = new Transform();
	    transforms.add(transform1);
	}
	int shader;
	Matrix4f view;
	Matrix4f proj;
	TContainer transforms;
	static float fov = 45f, exposure = 1.0f;
	public void run() {
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
	    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
		
	    Matrix4f view = camera.getViewMatrix();
	    
	    
	    GLShader.bindProgram(shader);
		GLShader.setUniformMat4(GL20.glGetUniformLocation(shader, "projection"), proj);
		GLShader.setUniformMat4(GL20.glGetUniformLocation(shader, "view"), view);
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, material.diffuse);
		GLShader.setUniformMat4(GL20.glGetUniformLocation(shader, "model"), PhysicsEngine.bodies[0].transform.getTransformation());
		GLRendering.renderMesh(box.vao, box.indices);
		
		//System.out.println(PhysicsEngine.bodies[0].transform.getTransformation());
	    //drenderer.prepare(view);
	    //drenderer.render(mesh, count, material, transform1);
	    //drenderer.render(terrain);
	    //drenderer.render(box.vao, box.indices, transforms.next, material, transforms.transforms);
	    //drenderer.renderLighting(view, camera.pos, dlight, 0);
		
	    
		GLVertexArray.unbindVertexArray();
		window.swapBuffers();
	}
	
	public static void shadows(){drenderer.toggleShadows();}
	public static void ssao(){drenderer.toggleSSAO();}
	
}
