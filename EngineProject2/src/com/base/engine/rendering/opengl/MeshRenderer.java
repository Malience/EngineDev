package com.base.engine.rendering.opengl;

import com.base.engine.data.Material;
import com.base.engine.data.Mesh;

public class MeshRenderer {
	public Mesh mesh;
	public Material material;
	
	public MeshRenderer(Mesh mesh, Material material) {this.mesh = mesh; this.material = material;}
}
