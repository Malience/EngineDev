package com.base.engine.rendering.opengl;

import static org.lwjgl.opengl.GL11.glClear;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;

import com.base.engine.rendering.MaterialMap;
import math.Matrix4f;

public class GLPassMeshMat extends GLPassMesh{
	MaterialMap[] materials;
	
	public GLPassMeshMat(int framebuffer, int clearcode, int shader){this(framebuffer, clearcode, shader, DEFAULT_PROJECTION, DEFAULT_VIEW, DEFAULT_MESH_MAX);}
	public GLPassMeshMat(int shader, String projection, String view){this(0, 0, shader, projection, view, DEFAULT_MESH_MAX);}
	public GLPassMeshMat(int framebuffer, int shader, String projection, String view){this(framebuffer, 0, shader, projection, view, DEFAULT_MESH_MAX);}
	public GLPassMeshMat(int framebuffer, int clearcode, int shader, String projection, String view){this(framebuffer, clearcode, shader, projection, view, DEFAULT_MESH_MAX);}
	public GLPassMeshMat(int framebuffer, int clearcode, int shader, String projection, String view, int meshes){
		super(framebuffer, clearcode, shader, projection, view);
		this.meshes = new int[meshes * 2];
		for(int i = 0; i < this.meshes.length; i+=2) this.meshes[i] = -1;
		transforms = new Matrix4f[meshes];
		materials = new MaterialMap[meshes];
		GLShader.bindProgram(shader);
		GLShader.setUniform(shader, "diffuse0", 0);
		GLShader.setUniform(shader, "specular0", 1);
	}
	
	//Will use material of previous mesh if null
	public void put(int index, int mesh, int meshvertices, Matrix4f transform, MaterialMap material){
		meshes[index * 2] = mesh;
		meshes[index * 2 + 1] = meshvertices;
		transforms[index] = transform;
		materials[index] = material;
	}
	
	@Override
	public void remove(int index){
		meshes[index * 2] = -1;
		meshes[index * 2 + 1] = -1;
		transforms[index] = null;
		materials[index] = null;
	}
	
	@Override
	public void render(Matrix4f view){
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebuffer);
	    if(clearcode > 0) glClear(clearcode);
	    GLShader.bindProgram(shader);
	    GLShader.setUniformMat4(this.view, view);
	    for(int i = 0; i < transforms.length; i++){
			if(meshes[i * 2] == -1) continue;
			if(materials[i] != null && materials[i].diffuse >= 0){
				GL13.glActiveTexture(GL13.GL_TEXTURE0);
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, materials[i].diffuse);
				GL13.glActiveTexture(GL13.GL_TEXTURE1);
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, materials[i].specular >= 0 ? materials[i].specular : materials[i].diffuse);
			}
			GLShader.setUniformMat4(model, transforms[i]);
			GLRendering.renderMesh(meshes[i * 2], meshes[i * 2 + 1]);
		}
	}
}
