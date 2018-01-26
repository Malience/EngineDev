package com.base.engine.data;

import com.base.engine.core.util.AssimpUtil;
import com.base.engine.rendering.opengl.GLVertexArray;

public class Mesh extends Resource {
	public int vao = -1;
	public int[] buffers = null;
	public int indices = -1;
	
	
	public Mesh(String filename) {
		super(filename);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void load() {
		AssimpUtil.loadMesh(this);
	}

	@Override
	public void unload() {
		// TODO Auto-generated method stub
		GLVertexArray.deleteVertexArray(vao);
		GLVertexArray.deleteBuffers(buffers);
		vao = -1;
		buffers = null;
		indices = -1;
	}

}
