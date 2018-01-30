package com.base.engine.core;

public class Time {
	private static final long SECOND = 1000000000L;
	private static final float INV_SECOND = 1.0f / (float) SECOND;
	
	private static float last, delta;
	
	static { last = getTime(); }
	
	public static float getDelta() { return delta; }
	public static float getTime() { return System.nanoTime() * INV_SECOND; }
	
	public static float updateDelta() {
		float time = getTime();
		delta = time - last;
		last = time;
		return delta;
	}
}
