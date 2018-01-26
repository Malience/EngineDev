package com.base.engine.rendering;

import java.nio.FloatBuffer;

import com.base.engine.core.util.Util;

public class Material{
	public FloatBuffer ambient;
	public FloatBuffer diffuse;
	public FloatBuffer specular;
	public float shininess;
	public int[] textures;
	//private byte flags;
	
	public Material(float[] ambient, float[] diffuse, float[] specular, float shininess, int[] textures){
		this.ambient = Util.arrayToBuffer(ambient);
		this.diffuse = Util.arrayToBuffer(diffuse);
		this.specular = Util.arrayToBuffer(specular);
		this.shininess = shininess;
		this.textures = textures;
	}
	
	public Material(FloatBuffer ambient, FloatBuffer diffuse, FloatBuffer specular, float shininess, int[] textures){
		this.ambient = ambient;
		this.diffuse = diffuse;
		this.specular = specular;
		this.shininess = shininess;
		this.textures = textures;
	}
	
	public int numTextures(){return textures.length;}
}
