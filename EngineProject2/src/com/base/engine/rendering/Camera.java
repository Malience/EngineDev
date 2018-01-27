package com.base.engine.rendering;

import com.base.engine.core.CoreEngine;
import com.base.engine.core.Time;

import math.Matrix4f;
import math.Vector3f;


public class Camera {
	private static Camera camera;
	public Vector3f pos;
	public Vector3f forward, right, up;
	
	public float yaw;
	public float pitch;
	
	private Matrix4f view_matrix = new Matrix4f();
	
	public boolean hasRotated;
	public boolean hasMoved;
	
	private static float cameraSpeed = 20f, cameraYawSpeed = 16f, cameraPitchSpeed = 9f;
	
	public Camera(){pos = new Vector3f(); yaw = -90; calculateRotate(); camera = this;}
	public Camera(Vector3f pos, float yaw, float pitch){this.pos = pos; this.yaw = yaw; this.pitch = pitch; calculateRotate(); camera = this;}
	
	public Matrix4f getViewMatrix(){if(hasRotated) calculateRotate(); else if(hasMoved) calculateMove(); return view_matrix;}
	
	public void translate(Vector3f r){pos.add(r); hasMoved = true;}
	public void translate(float x, float y, float z){pos.add(x, y, z); hasMoved = true;}
	public void lookAt(Vector3f loc){pos.sub(loc, forward).normalize(); hasRotated = true;}
	
	private void calculateRotate(){
		if(forward == null) forward = new Vector3f(); right = new Vector3f(); up = new Vector3f();
		forward.rotate(yaw, pitch).normalize();
		Vector3f.UP.cross(forward, right).normalize(); 
		forward.cross(right, up);
		hasRotated = false;
		calculateMove();
	}
	private void calculateMove(){
		view_matrix.setLookAt(pos, forward, up, right);
		hasMoved = false;
	}
	
	public Vector3f getPos(){return pos;}
	
	public static void moveForward(){if(camera != null) camera.translate(camera.forward.x * -cameraSpeed * Time.getDelta(), camera.forward.y * -cameraSpeed * Time.getDelta(), camera.forward.z * -cameraSpeed * Time.getDelta());}
	public static void moveBackward(){if(camera != null) camera.translate(camera.forward.x * cameraSpeed * Time.getDelta(), camera.forward.y * cameraSpeed * Time.getDelta(), camera.forward.z * cameraSpeed * Time.getDelta());}
	public static void moveLeft(){if(camera != null) camera.translate(camera.right.x * -cameraSpeed * Time.getDelta(), camera.right.y * -cameraSpeed * Time.getDelta(), camera.right.z * -cameraSpeed * Time.getDelta());}
	public static void moveRight(){if(camera != null) camera.translate(camera.right.x * cameraSpeed * Time.getDelta(), camera.right.y * cameraSpeed * Time.getDelta(), camera.right.z * cameraSpeed * Time.getDelta());}
	
	static boolean locked = false;
	public static void rotateYaw(float x){if(!locked) return; camera.yaw += (.5 - x) * cameraYawSpeed; camera.calculateRotate(); CoreEngine.window.setCursorMiddle();}
	public static void rotatePitch(float x){if(!locked) return; camera.pitch += (.5 - x) * cameraPitchSpeed; if(camera.pitch > 80) camera.pitch = 80; else if(camera.pitch < -80) camera.pitch = -80; camera.calculateRotate(); CoreEngine.window.setCursorPos(0, 0);}
	public static void lockCursor(){CoreEngine.window.lockCursor(); locked = true;}
	public static void unlockCursor(){CoreEngine.window.unlockCursor(); locked = false;}
}
