package com.base.engine.rendering;

import math.Vector3f;

public class PointLight {
	public Vector3f position;
	
	public float constant;
	public float linear;
	public float quadratic;
	
	public Vector3f ambient;
	public Vector3f diffuse;
	public Vector3f specular;
	
	public PointLight(Vector3f position, float constant, float linear, float quadratic, Vector3f ambient, Vector3f diffuse, Vector3f specular) {
		this.position = position;
		this.constant = constant;
		this.linear = linear;
		this.quadratic = quadratic;
		this.ambient = ambient;
		this.diffuse = diffuse;
		this.specular = specular;
	}

}
