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

public class GLFWRenderingEngine4 {
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
		
		glClearColor(0f, 0f, 0f, 1f);

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
		
		Vector3f dlightpos = new Vector3f(-2f, 8f,-4f);
		float size = 20f;
		//ortho = Matrix4f.createPerspective(45f, 1f, -1.0f, 20f);
		ortho = new Matrix4f().orthographic(-size, size, -size, size, 1.0f, 20f);
		Matrix4f lightView = new Matrix4f().setLookAt(dlightpos);//new Matrix4f(new org.joml.Matrix4f().lookAt(2,10f,-10f,0,0,0,0,1,0));//Matrix4f.createLookAtMatrix(dlightpos, new Vector3f( 0.0f, 0.0f,  0.0f));
		ortho.mul(lightView);
		
		camera = new Camera();
		//camera.translate(0, 5, 5);
		transform1.translate(0,0,0);
		
		material = new MaterialMap(tex[0], 16f);
		
		dlight = new DirectionalLight(new Vector3f().sub(dlightpos), new Vector3f(0.3f,0.3f,0.3f), new Vector3f(0f,1f,1f), new Vector3f(1.0f, 1.0f, 1.0f));
		
		persp = new Projection(fov, window.getWidth(), window.getHeight(), .1f, 1000f);//Matrix4f.createPerspective(fov, aspectRatio, .1f, 1000f);
		
		terrain = new Terrain(-0.5f,-0.5f, GLTexture.createTextures(new String[]{"bricks.png", "mud.png", "gauge.png", "grassTexture.png", "blendMap.png"}, 3));
		
	    drenderer = new DeferredRenderer(window.getWidth(), window.getHeight(), true, persp.matrix);
	    
	    Octree octree = new Octree(60,60,60);
	    float step = 5;
	    transforms = new TContainer(1000);
	    for(int i = 0; i < 10; i++) for(int j = 0; j < 10; j++) for(int k = 0; k < 10; k++)
	    	octree.add(new GameObject(step * i, step * j, step * k));
	    octree.update(1.0f);
	    
	    Frustum f = new Frustum(camera, persp);
	    
	    octree.get(f, transforms);
	    
	    Transform one = new Transform();
	    transforms.add(one);
	}
	TContainer transforms;
	static float fov = 45f, exposure = 1.0f;
	public void run() {
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
	    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
		
	    Matrix4f view = camera.getViewMatrix();
	    
	    
	    
	    
	    drenderer.prepare(view);
	    //drenderer.render(mesh, count, material, transform1);
	    //drenderer.render(terrain);
	    drenderer.render(box.vao, box.indices, transforms.next, material, transforms.transforms);
	    drenderer.renderLighting(view, camera.pos, dlight, 0);
		
	    
		GLVertexArray.unbindVertexArray();
		window.swapBuffers();
	}
	
	public static void shadows(){drenderer.toggleShadows();}
	public static void ssao(){drenderer.toggleSSAO();}
	
}
