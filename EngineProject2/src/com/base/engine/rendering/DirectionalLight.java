package com.base.engine.rendering;

import math.Vector3f;

public class DirectionalLight {
	public Vector3f direction;
	
	public Vector3f ambient;
	public Vector3f diffuse;
	public Vector3f specular;
	
	public DirectionalLight(Vector3f direction, Vector3f ambient, Vector3f diffuse, Vector3f specular) {
		this.direction = direction;
		this.ambient = ambient;
		this.diffuse = diffuse;
		this.specular = specular;
	}
	
	
}
