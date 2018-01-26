package com.base.engine.rendering;

import java.nio.FloatBuffer;

import com.base.engine.core.util.Util;

public class RenderStructure3 {
	private static final int NUM_INSTANCES = 20;
	
	private FloatBuffer buffer;
	
	private float[][] transforms;
	private float[] distances;
	
	private int current_instance;
	private int instances;
	
	public RenderStructure3(){this(NUM_INSTANCES);}
	public RenderStructure3(int num_instances){
		this.instances = num_instances + 1;
		transforms = new float[instances][];
		distances = new float[instances];
		current_instance = 1;
		buffer = Util.createFloatBuffer(num_instances * 16);
	}
	
	public void clear(){
		transforms = new float[instances][];
		distances = new float[instances];
		current_instance = 1;
	}
	
	public int getInstances(){return instances;}
	public FloatBuffer getRender(){return Util.arrayToBuffer(buffer, transforms);}
	
	//TODO: Fix this
	public void addInstance(float[] camera_pos, float[] transform){
		float distance = 0;//VectorMath.distanceSquared(camera_pos, transform[3], transform[7], transform[11]);
		if(current_instance < transforms.length) {
			transforms[current_instance] = transform;
			distances[current_instance++] = distance;
			bubbleUp();
		} else if(distances[1] > distance){
			transforms[1] = transform;
			distances[1] = distance;
			bubbleDown();
		}
	}
	
	private void bubbleDown(){
		int index = 1;
		int child = index*2;
		child = distances[child] > distances[child+1] ? child : child + 1;
		float[] transform_swap;
		float distance_swap;
		while(child < distances.length && distances[index] < distances[child]){
			transform_swap = transforms[index];
			distance_swap = distances[index];
			transforms[index] = transforms[child];
			distances[index] = distances[child];
			transforms[child] = transform_swap;
			distances[child] = distance_swap;
			index = child;
			child = index*2;
			child = distances[child] > distances[child+1] ? child : child + 1;
		}
	}
	
	private void bubbleUp(){
		int index = current_instance - 1;
		int parent = index/2;
		if(index <= 1) return;
		float[] transform_swap;
		float distance_swap;
		while(index > 1 && distances[index] > distances[parent]){
			transform_swap = transforms[index];
			distance_swap = distances[index];
			transforms[index] = transforms[parent];
			distances[index] = distances[parent];
			transforms[parent] = transform_swap;
			distances[parent] = distance_swap;
			index = parent;
			parent = index/2;
		}
	}
}
