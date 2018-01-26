package com.base.engine.rendering;

public class Model {
	public Mesh[] meshes;
	
	public Model(Mesh[] meshes){this.meshes = meshes;}
	//public void draw(GLShader shader){for(int i = 0; i < meshes.length; i++) meshes[i].draw(shader);}
}
