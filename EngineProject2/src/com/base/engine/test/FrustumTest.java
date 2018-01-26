package com.base.engine.test;

import com.base.engine.core.GameObject;
import com.base.engine.physics.Frustum;
import com.base.engine.rendering.Camera;
import com.base.engine.rendering.Projection;
import math.Vector3f;

import primitives.BoundingBox;

public class FrustumTest {
	public static void main(String [] args){
		Camera c = new Camera(new Vector3f(0,0,0), 0, 0);
		Projection p = new Projection(70, 100, 100, 1, 20);
		
		Frustum f = new Frustum(c, p);
		
		GameObject tinybox = new GameObject(0f,0f,2f);
		tinybox.collider = new BoundingBox(0.1f,0.1f,0.1f);
		
		boolean b = f.top.contains(tinybox);
		
		System.out.println(b);
	}
}
