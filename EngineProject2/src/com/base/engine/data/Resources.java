package com.base.engine.data;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;

public class Resources {
	private static HashMap<String, Mesh> meshes = new HashMap<String, Mesh>();
	private static HashMap<String, Shader> shaders = new HashMap<String, Shader>();
	private static HashMap<String, Texture> textures = new HashMap<String, Texture>();
	
	public static final String MESH_PATH = "./res/meshes/";
	public static Mesh loadMesh(String filename) {
		filename = MESH_PATH + filename;
		System.out.println(new File(filename).getAbsolutePath());
		Mesh mesh;
		if(!meshes.containsKey(filename)) {
			mesh = new Mesh(filename);
			meshes.put(filename, mesh);
			mesh.load();
		} else mesh = meshes.get(filename);
		mesh.increment(); 
		return mesh;
	}
	
	public static final String SHADER_PATH = "./res/shaders/";
	public static int loadShader(String filename) {
		filename = SHADER_PATH + filename;
		Shader shader;
		if(!shaders.containsKey(filename)) {
			shader = new Shader(filename);
			shaders.put(filename, shader);
			shader.load();
		} else shader = shaders.get(filename);
		shader.increment(); 
		return shader.program;
	}
	
	public static final String TEXTURE_PATH = "";//./res/textures/";
	public static Texture loadTexture(String filename) {
		filename = TEXTURE_PATH + filename;
		Texture texture;
		if(!textures.containsKey(filename)) {
			texture = new Texture(filename);
			textures.put(filename, texture);
			texture.load();
		} else texture = textures.get(filename);
		texture.increment(); 
		return texture;
	}
	
	public static void dispose() {
		Collection<Mesh> meshcol = meshes.values();
		for(Mesh m : meshcol) m.unload();
	}
}
