package com.base.engine.rendering;

import math.Matrix4f;

public class Projection {
	public float fov, width, height, near, far;
	public Matrix4f matrix;
	public Matrix4f inverse;
	
	public Projection(float fov, float width, float height, float near, float far){
		this.fov = fov; this.width = width; this.height = height; this.near = near; this.far = far;
		matrix = new Matrix4f().perspective(fov, width/height, near, far);
		inverse = new Matrix4f();
	}
	
	public Matrix4f calculateInverse(Matrix4f view){
		view.mul(matrix, inverse);
		return inverse.invert();}
}
