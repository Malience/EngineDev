package com.base.engine.rendering;

public class Light {
	public float[] pos;
	
	public float[] ambient;
	public float[] diffuse;
	public float[] specular;
	
	public Light(float[] pos, float[] ambient, float[] diffuse, float[] specular) {
		this.pos = pos;
		this.ambient = ambient;
		this.diffuse = diffuse;
		this.specular = specular;
	}
	
	
}
