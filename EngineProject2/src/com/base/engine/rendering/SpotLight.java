package com.base.engine.rendering;

import math.Vector3f;

public class SpotLight {
	public Vector3f position;
	public Vector3f direction;
	
	public float cutoff;
	public float outerCutoff;
	
	public Vector3f ambient;
	public Vector3f diffuse;
	public Vector3f specular;
	
	public SpotLight(Vector3f position, Vector3f direction, float cutoff, float outerCutoff, Vector3f ambient, Vector3f diffuse, Vector3f specular) {
		this.position = position;
		this.direction = direction;
		this.cutoff = cutoff;
		this.outerCutoff = outerCutoff;
		this.ambient = ambient;
		this.diffuse = diffuse;
		this.specular = specular;
	}
}
