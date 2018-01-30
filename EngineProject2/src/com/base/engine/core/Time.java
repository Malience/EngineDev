package com.base.engine.core;

public class Time 
{
	private static float last;
	private static float delta;
	
	static {
		last = getTime();
	}
	
	public static float getDelta() {
		return delta;
	}
	
	public static float updateDelta() {
		float time = getTime();
		delta = time - last;
		last = time;
		return delta;
	}
	
	private static final long SECOND = 1000000000L;

	public static float getTime()
	{
		return System.nanoTime()/(float)SECOND;
	}
}
