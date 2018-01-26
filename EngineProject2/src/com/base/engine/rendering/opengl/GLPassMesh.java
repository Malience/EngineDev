package com.base.engine.rendering.opengl;

import static org.lwjgl.opengl.GL11.glClear;

import math.Matrix4f;

public class GLPassMesh extends GLPass{
	protected static final int DEFAULT_MESH_MAX = 10;
	int model;
	int[] meshes;
	Matrix4f[] transforms;
	
	public GLPassMesh(int framebuffer, int clearcode, int shader){this(framebuffer, clearcode, shader, DEFAULT_PROJECTION, DEFAULT_VIEW, DEFAULT_MESH_MAX);}
	public GLPassMesh(int shader, String projection, String view){this(0, 0, shader, projection, view, DEFAULT_MESH_MAX);}
	public GLPassMesh(int framebuffer, int shader, String projection, String view){this(framebuffer, 0, shader, projection, view, DEFAULT_MESH_MAX);}
	public GLPassMesh(int framebuffer, int clearcode, int shader, String projection, String view){this(framebuffer, clearcode, shader, projection, view, DEFAULT_MESH_MAX);}
	public GLPassMesh(int framebuffer, int clearcode, int shader, String projection, String view, int meshes){
		super(framebuffer, clearcode, shader, projection, view);
		this.meshes = new int[meshes * 2];
		for(int i = 0; i < this.meshes.length; i+=2) this.meshes[i] = -1;
		transforms = new Matrix4f[meshes];
		model = GL20.glGetUniformLocation(shader, "model");
	}
	
	public void put(int index, int mesh, int meshvertices, Matrix4f transform){
		meshes[index * 2] = mesh;
		meshes[index * 2 + 1] = meshvertices;
		transforms[index] = transform;
	}
	
	public void remove(int index){
		meshes[index * 2] = -1;
		meshes[index * 2 + 1] = -1;
		transforms[index] = null;
	}
	
	@Override
	public void render(Matrix4f view){
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebuffer);
	    if(clearcode > 0) glClear(clearcode);
	    GLShader.bindProgram(shader);
	    GLShader.setUniformMat4(this.view, view);
	    for(int i = 0; i < transforms.length; i++){
			if(meshes[i * 2] == -1) continue;
			GLShader.setUniformMat4(model, transforms[i]);
			GLRendering.renderMesh(meshes[i * 2], meshes[i * 2 + 1]);
		}
	}
}
