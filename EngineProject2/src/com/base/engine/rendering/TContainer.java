package com.base.engine.rendering;

import com.base.math.Transform;

public class TContainer {
	public Transform[] transforms;
	public int next, last;
	
	public TContainer(int size){transforms = new Transform[size]; last = size;}
	
	public boolean add(Transform t){if(next >= last) return false; transforms[next++] = t; return true;}
}
