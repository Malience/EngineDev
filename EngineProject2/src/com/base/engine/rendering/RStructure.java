package com.base.engine.rendering;

import com.base.engine.core.util.Util;

public class RStructure {
	public int[] meshes;
	public int next = 0;
	
	public RStructure(int size){meshes = new int[size];}
	
	public void add(int mesh){if(next >= meshes.length) expand(5); meshes[next++] = mesh;}
	
	protected void expand(int increase){meshes = Util.expandIntArray(meshes, increase);}
	protected void unsafeExpand(int increase){meshes = new int[meshes.length + increase]; next = 0;}
}
