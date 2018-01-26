package com.base.engine.rendering;

import java.util.HashMap;

public class RenderStructure2 {
	private static final int NUM_TYPES = 10;
	
	private HashMap<Integer, RenderStructure3> map;
	
	private float[] camera_pos;
	
	public RenderStructure2(){this(NUM_TYPES);}
	public RenderStructure2(int types){map = new HashMap<Integer, RenderStructure3>(types);}
	
	public void setCamera(float[] camera_pos){this.camera_pos = camera_pos;}
	public void clear(){map.clear();}
	
	public void addInstance(int render){if(!map.containsKey(render)){map.put(render, new RenderStructure3(render));}}
	
	public void addInstance(int render, float[] transform){
		if(!map.containsKey(render)) return;
		map.get(render).addInstance(camera_pos, transform);
	}
}
