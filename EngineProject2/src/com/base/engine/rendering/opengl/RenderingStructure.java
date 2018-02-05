package com.base.engine.rendering.opengl;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;

import java.nio.FloatBuffer;
import java.util.HashMap;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;

import com.base.engine.data.Mesh;
import com.base.engine.data.Shader;
import com.base.math.Transform;

import math.Matrix4f;

public class RenderingStructure {
	private HashMap<Material, Batch> matmap;
	private Batch[] batchstack;
	private int next, size;
	public Terrain terrain;
	
	public RenderingStructure(int size, int batch_size) {
		matmap = new HashMap<Material, Batch>(size);
		batchstack = new Batch[size];
		for(int i = 0; i < size; i++) batchstack[i] = new Batch(batch_size);
		this.size = size;
	}
	
	public void add(MeshRenderer meshrenderer, Transform transform) {add(meshrenderer.material, meshrenderer.mesh, transform);}
	public void add(Material mat, Mesh mesh, Transform transform) {
		Batch batch = matmap.get(mat);
		if(batch == null) {
			if(next >= size) return;
			batch = batchstack[next++];
			batch.setMaterial(mat);
			matmap.put(mat, batch);
		}
		batch.addMesh(mesh, transform);
	}
	
	public void render(Shader shader, Matrix4f view, Matrix4f projection) {
		int program = shader.program;
		GL20.glUseProgram(program);
		int model = shader.model < 0 ? GL20.glGetUniformLocation(program, "model") : shader.model;
		int view_loc = shader.model < 0 ? GL20.glGetUniformLocation(program, "view") : shader.view;
		if(projection != null) {
			int proj = shader.proj < 0 ? GL20.glGetUniformLocation(program, "projection") : shader.proj;
			GLShader.setUniformMat4(proj, projection);
		}
		GLShader.setUniformMat4(view_loc, view);
		for(int i = 0; i < next; i++) batchstack[i].render(program, model);
	}
	
	public void renderTerrain(Shader shader, Matrix4f view, Matrix4f projection) {
		int program = shader.program;
		GL20.glUseProgram(program);
		int model = shader.model < 0 ? GL20.glGetUniformLocation(program, "model") : shader.model;
		int view_loc = shader.model < 0 ? GL20.glGetUniformLocation(program, "view") : shader.view;
		if(projection != null) {
			int proj = shader.proj < 0 ? GL20.glGetUniformLocation(program, "projection") : shader.proj;
			GLShader.setUniformMat4(proj, projection);
		}
		GLShader.setUniformMat4(view_loc, view);
		GLShader.setUniformMat4(model, terrain.transform.getTransformation());
	    GL13.glActiveTexture(GL13.GL_TEXTURE0);
	    glBindTexture(GL_TEXTURE_2D, terrain.textures[0]);
	    GL13.glActiveTexture(GL13.GL_TEXTURE1);
	    glBindTexture(GL_TEXTURE_2D, terrain.textures[1]);
	    GL13.glActiveTexture(GL13.GL_TEXTURE2);
	    glBindTexture(GL_TEXTURE_2D, terrain.textures[2]);
	    GL13.glActiveTexture(GL13.GL_TEXTURE3);
	    glBindTexture(GL_TEXTURE_2D, terrain.textures[3]);
	    GL13.glActiveTexture(GL13.GL_TEXTURE4);
	    glBindTexture(GL_TEXTURE_2D, terrain.textures[4]);
	    GLRendering.renderMesh(terrain.mesh, terrain.getIndices());
	}
	
	public void dispose() {
		for(Batch batch : matmap.values()) batch.dispose();
		matmap.clear();
		next = 0;
	}
	
	private static class Batch {
		int diffuse;
		int[] meshes;
		FloatBuffer[] buffers;
		int next = 0;
		
		Batch(int size){
			meshes = new int[size << 1];
			buffers = new FloatBuffer[size];
		}
		
		void setMaterial(Material mat) {this.diffuse = mat.diffuse.texture;}
		
		void addMesh(Mesh mesh, Transform transform) {
			buffers[next] = transform.getBuffer();
			meshes[next << 1] = mesh.vao;
			meshes[(next << 1) + 1] = mesh.indices;
			next++;
		}
		
		void render(int shader, int model_loc) {
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, diffuse);
			GL13.glActiveTexture(GL13.GL_TEXTURE1);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, diffuse);
			for(int i = 0; i < next; i++) {
				GLShader.setUniformMat4(model_loc, buffers[i]);
				GLRendering.renderMesh(meshes[i << 1], meshes[(i << 1) + 1]);
			}
		}
		
		void dispose() {
			for(int i = 0; i < next; i++) buffers[i] = null;
			next = 0;
		}
		
	}
}
