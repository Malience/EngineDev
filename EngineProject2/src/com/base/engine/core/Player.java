package com.base.engine.core;

import com.base.math.Transform;
import math.Vector3f;

public class Player {
	public Transform transform;
	public Vector3f color;
	
	public Player(Vector3f color){
		this.transform = new Transform();
		this.color = color;
		transform.translate((float) (Math.random() * 10), 0, (float) (Math.random() * 10));
	}
	
	public Player(Vector3f pos, Vector3f color){
		this.transform = new Transform();
		transform.pos = pos;
		transform.remakeTransformation();
		this.color = color;
	}
}
