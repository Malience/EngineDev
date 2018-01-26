package com.base.engine.rendering;

import com.base.engine.physics.Frustum;

public class LightCull {
	
	static int tiles, length, width, height;
	
	static Frustum[] frustums;
	
	//Tiles must be a power of 2
	public static void initialize(int tiles, int width, int height) {
		LightCull.tiles = tiles;
		LightCull.length = (int)Math.sqrt(tiles);
		LightCull.width = width / length;
		LightCull.height = height / length;
		generateFrustums();
	}
	
	public static void generateFrustums() {
		frustums = new Frustum[tiles];
		
		
	}

}
