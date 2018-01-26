package com.base.engine.core;

public class Time 
{
	private static double last;
	private static float delta;
	
	public static void init() {
		last = getTime();
	}
	
	public static float getDelta() {
		return delta;
	}
	
	public static float updateDelta() {
		double time = getTime();
		delta = (float) (time - last);
		last = time;
		return delta;
	}
	
	private static final long SECOND = 1000000000L;

	public static double getTime()
	{
		return (double)System.nanoTime()/(double)SECOND;
	}
}
