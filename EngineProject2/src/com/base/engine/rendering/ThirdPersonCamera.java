package com.base.engine.rendering;

import math.Matrix4f;
import com.base.math.Transform;
import math.Vector3f;

public class ThirdPersonCamera {
	Transform target;
	private Vector3f pos;
	private Vector3f forward;
	private Vector3f right;
	private Vector3f up;
	
	float distance;
	private float yaw;
	private float pitch;
	
	private Matrix4f view_matrix = new Matrix4f();
	private boolean hasRotated;
	private boolean hasMoved;
	
	private static float cameraSpeed = .1f;
	private static float cameraYawSpeed = 30f;
	private static float cameraPitchSpeed = 20f;
	
	public void zoom(float amount){
		distance -= amount;
	}
	
	public void pitch(float amount){
		pitch -= amount;
	}
	
	public void yaw(float amount){
		yaw -= amount;
	}
}
