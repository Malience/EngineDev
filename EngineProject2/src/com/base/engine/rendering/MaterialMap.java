package com.base.engine.rendering;

public class MaterialMap{
	private static final float DEFAULT_SHININESS = 0;
	
	public int diffuse;
	public int specular;
	public float shininess;
	
	public MaterialMap(){this(-1, -1, DEFAULT_SHININESS);}
	public MaterialMap(int diffuse){this(diffuse, -1, DEFAULT_SHININESS);}
	public MaterialMap(float shininess){this(-1, -1, shininess);}
	public MaterialMap(int diffuse, int specular){this(diffuse, specular, DEFAULT_SHININESS);}
	public MaterialMap(int diffuse, float shininess){this(diffuse, -1, shininess);}
	public MaterialMap(int diffuse, int specular, float shininess) {
		this.diffuse = diffuse;
		this.specular = specular;
		this.shininess = shininess;
	}
}
