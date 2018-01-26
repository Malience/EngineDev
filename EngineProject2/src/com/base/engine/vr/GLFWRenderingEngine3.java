package com.base.engine.vr;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;

import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.openvr.Texture;
import org.lwjgl.openvr.TrackedDevicePose;
import org.lwjgl.openvr.VR;
import org.lwjgl.openvr.VRCompositor;
import org.lwjgl.openvr.VRSystem;
import org.lwjgl.openvr.VRTextureBounds;
import org.lwjgl.system.MemoryStack;

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
import com.base.engine.rendering.opengl.DeferredRenderer;
import com.base.engine.rendering.opengl.Framebuffer;
import com.base.engine.rendering.opengl.GLContext;
import com.base.engine.rendering.opengl.GLTexture;
import com.base.engine.rendering.opengl.GLVertexArray;
import com.base.engine.rendering.opengl.Terrain;
import math.Matrix4f;
import com.base.math.Transform;
import math.Vector3f;

public class GLFWRenderingEngine3 {
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
		
		glClearColor(0f, 1f, 0f, 1f);

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
		Matrix4f lightView = new Matrix4f().setLookAt(dlightpos, new Vector3f( 0.0f, 0.0f,  0.0f));//new Matrix4f(new org.joml.Matrix4f().lookAt(2,10f,-10f,0,0,0,0,1,0));//Matrix4f.createLookAtMatrix(dlightpos, new Vector3f( 0.0f, 0.0f,  0.0f));
		ortho.mul(lightView);
		
		camera = new Camera();
		//camera.translate(0, 5, 5);
		transform1.translate(0,0,0);
		
		material = new MaterialMap(tex[0], 16f);
		
		dlight = new DirectionalLight(new Vector3f().sub(dlightpos), new Vector3f(0.3f,0.3f,0.3f), new Vector3f(0f,1f,1f), new Vector3f(1.0f, 1.0f, 1.0f));
		
		persp = new Projection(fov, window.getWidth(), window.getHeight(), .1f, 1000f);//Matrix4f.createPerspective(fov, aspectRatio, .1f, 1000f);
		
		terrain = new Terrain(-0.5f,-0.5f, GLTexture.createTextures(new String[]{"bricks.png", "mud.png", "gauge.png", "grassTexture.png", "blendMap.png"}, 3));
		
	    drenderer = new DeferredRenderer(window.getWidth(), window.getHeight(), true, persp.matrix);
	    
//	    Octree octree = new Octree(60,60,60);
	    float step = 5;
	    transforms = new TContainer(1000);
//	    for(int i = 0; i < 10; i++) for(int j = 0; j < 10; j++) for(int k = 0; k < 10; k++)
//	    	octree.add(new GameObject(step * i, step * j, step * k));
//	    octree.update(1.0f);
//	    
//	    Frustum f = new Frustum(camera, persp);
//	    
//	    octree.get(f, transforms);
	    
	    Transform one = new Transform();
	    transforms.add(one);
	    
	    VR2.init();
	    
	    lefteye = new Framebuffer(1);
	    GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, lefteye.framebuffer);
	    lefteye.setBuffer(0, VR2.renderWidth, VR2.renderHeight, Framebuffer.RGB_BUFFER);
	    righteye = new Framebuffer(1);
	    GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, righteye.framebuffer);
	    righteye.setBuffer(0, VR2.renderWidth, VR2.renderHeight, Framebuffer.RGB_BUFFER);
	    
		VRSystem.VRSystem_CaptureInputFocus();
		
		vrdrenderer = new DeferredRenderer(VR2.renderWidth, VR2.renderHeight, true, persp.matrix);
	    
	}
	DeferredRenderer vrdrenderer;
	Framebuffer lefteye;
	Framebuffer righteye;
	TContainer transforms;
	static float fov = 45f, exposure = 1.0f;
	public void run() {
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
	    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
	    GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, lefteye.framebuffer);
	    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
	    GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, righteye.framebuffer);
	    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
	    
	    
	    
	    
	    //drenderer.render(mesh, count, material, transform1);
	    //drenderer.render(terrain);
	    
	    
	    
		
	    //VRCompositor.VRCompositor_CompositorBringToFront();
	    try(MemoryStack stack = stackPush()){
	    	
	    	TrackedDevicePose.Buffer poseb = TrackedDevicePose.mallocStack(5, stack);
	    	VRSystem.VRSystem_GetDeviceToAbsoluteTrackingPose(VR.ETrackingUniverseOrigin_TrackingUniverseStanding, 0, poseb);
	    	
	    	
	    	//System.out.println(camera.pos);
	    	//camera.pos = new Vector3f(poseb.get(0).vVelocity());
	    	//camera.translate(new Vector3f(poseb.get(0).vVelocity()));
	    	//Camera.rotatePitch(poseb.get(0).vAngularVelocity().v(0));
	    	//Camera.rotateYaw(poseb.get(0).vAngularVelocity().v(1));
	    	camera.pitch += poseb.get(0).vAngularVelocity().v(2);
	    	camera.yaw += poseb.get(0).vAngularVelocity().v(1);
	    	camera.hasMoved = true;
	    	camera.hasRotated = true;
	    	
	    	Matrix4f view = camera.getViewMatrix();
	    	
	    	GL11.glViewport(0, 0, window.getWidth(), window.getHeight());
	    	
		    drenderer.prepare(view);
		    drenderer.render(box.vao, box.indices, transforms.next, material, transforms.transforms);
		    drenderer.renderLighting(view, camera.pos, dlight, 0);
		    
		    window.swapBuffers();
		    VRCompositor.VRCompositor_PostPresentHandoff();
	    	
		    GL11.glViewport(0, 0, VR2.renderWidth, VR2.renderHeight);
	    	
		    vrdrenderer.setProjection(VR2.leftEyePerspective);
		    vrdrenderer.prepare(view);
		    vrdrenderer.render(box.vao, box.indices, transforms.next, material, transforms.transforms);
		    vrdrenderer.renderLighting(view, camera.pos, dlight, lefteye.framebuffer);
		    
		    vrdrenderer.setProjection(VR2.rightEyePerspective);
		    vrdrenderer.prepare(view);
		    vrdrenderer.render(box.vao, box.indices, transforms.next, material, transforms.transforms);
		    vrdrenderer.renderLighting(view, camera.pos, dlight, righteye.framebuffer);
	    	
	    	
	    	Texture lefttexture = Texture.mallocStack(stack);
	    	lefttexture.set(lefteye.buffers[0], VR.ETextureType_TextureType_OpenGL, VR.EColorSpace_ColorSpace_Auto);
	    	
	    	Texture righttexture = Texture.mallocStack(stack);
	    	righttexture.set(righteye.buffers[0], VR.ETextureType_TextureType_OpenGL, VR.EColorSpace_ColorSpace_Auto);
	    	
	    	
	    	VRTextureBounds leftbounds = VRTextureBounds.mallocStack(stack);
	    	leftbounds.uMin(0);
	    	leftbounds.uMax(1);
	    	leftbounds.vMin(0);
	    	leftbounds.vMax(1);
	    	
	    	VRTextureBounds rightbounds = VRTextureBounds.mallocStack(stack);
	    	rightbounds.uMin(0);
	    	rightbounds.uMax(1);
	    	rightbounds.vMin(0);
	    	rightbounds.vMax(1);
	    	
	    	VRCompositor.VRCompositor_Submit(VR.EVREye_Eye_Left, lefttexture, leftbounds, VR.EVRSubmitFlags_Submit_Default);
	    	VRCompositor.VRCompositor_Submit(VR.EVREye_Eye_Right, righttexture, rightbounds, VR.EVRSubmitFlags_Submit_Default);
	    	
	    	VRCompositor.VRCompositor_WaitGetPoses(poseb, poseb);
	    	
	    	
	    }
	    
	    
	    
	    
		GLVertexArray.unbindVertexArray();
		
	}
	
	public static void shadows(){drenderer.toggleShadows();}
	public static void ssao(){drenderer.toggleSSAO();}
	
}
