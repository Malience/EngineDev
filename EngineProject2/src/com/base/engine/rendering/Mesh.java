package com.base.engine.rendering;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import com.base.engine.rendering.opengl.GLVertexArray;

public class Mesh {
	public int vao;
	public int indices;
	public Material mat;
	
	public Mesh(float[] vertices, int[] indices){this(vertices, true, indices, null);}
	public Mesh(float[] vertices, int[] indices, Material mat){this(vertices, true, indices, null);}
	public Mesh(float[] vertices, boolean interleaved, int[] indices, Material mat){
		vao = GLVertexArray.setUpVertexArray(vertices, indices, true, 3,3,2);
		this.indices = indices.length;
		this.mat = mat;
	}
	public Mesh(FloatBuffer vertices, boolean interleaved, int[] indices, Material mat){
		vao = GLVertexArray.setUpVertexArray(vertices, indices, true, 3,3,2);
		this.indices = indices.length;
		this.mat = mat;
	}
	public Mesh(FloatBuffer[] vertices, IntBuffer indices, Material mat, int... attribute_sizes){
		vao = GLVertexArray.setUpVertexArray(vertices, indices, attribute_sizes);
		this.indices = indices.limit();
		this.mat = mat;
	}
}
