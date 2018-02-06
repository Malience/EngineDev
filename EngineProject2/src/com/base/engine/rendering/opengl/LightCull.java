package com.base.engine.rendering.opengl;

import math.Matrix4f;
import math.Vector4f;

public class LightCull {
	
	public static Vector4f clipToView(Vector4f clip, Matrix4f invProj) {
		Vector4f view = invProj.transform(clip, new Vector4f());
		float invW = 1f / view.w;
		view.x *= invW; view.y *= invW; view.z *= invW; view.w = 1f;
		return view;
	}
	
	public static Vector4f screenToView(Vector4f screen, Matrix4f invProj, int screenWidth, int screenHeight) {
		return clipToView(new Vector4f(
				screen.x * 2.0f / (float) screenWidth - 1.0f, 
				screen.y * 2.0f / (float) screenHeight - 1.0f, //May not be correct
				screen.z, screen.w), invProj);
	}
}
