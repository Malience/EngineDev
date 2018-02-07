package com.base.engine.data;

public class Material {
	public Texture diffuse;
	public Texture specular;
	public Texture normal;
	public float shininess;
	
	public Material(Texture diffuse) {
		this.diffuse = diffuse;
	}
}
