package com.base.engine.rendering;

import java.util.HashMap;

public class RenderStructure {
	HashMap<Integer, Integer> indexMap;
	private static final int NUM_TYPES = 40;
	private static final int NUM_INSTANCES = 20;
	
	float[] camera_pos;
	
	int[] array;
	int[][] textures;
	float[][][] transforms;
	float[][] distances;
	
	int current_type;
	int[] current_instance;
	
	public RenderStructure(){this(NUM_TYPES, NUM_INSTANCES);}
	public RenderStructure(int types, int instances){
		indexMap = new HashMap<Integer, Integer>(types);
		
		array = new int[types];
		textures = new int[types][];
		transforms = new float[types][instances+1][];
		distances = new float[types][instances+1];
		
		current_type = 0;
		current_instance = new int[types];
	}
	
	public void addInstance(int array, float[] transform, int... textures){
		if(!indexMap.containsKey(array)){
			if(current_type >= this.array.length) return;
			this.array[current_type] = array;
			this.textures[current_type] = textures;
			indexMap.put(array, current_type++);
		}
		addInstance(indexMap.get(array), transform);
	}
	
	//TODO: Fix this
	private void addInstance(int array, float[] transform){
		float distance = 0;//VectorMath.distanceSquared(camera_pos, transform[3], transform[7], transform[11]);
		if(current_instance[array] < transforms[array].length) {
			transforms[array][current_instance[array]] = transform;
			distances[array][current_instance[array]++] = distance;
			bubbleUp(array);
		} else if(distances[array][1] > distance){
			transforms[array][1] = transform;
			distances[array][1] = distance;
			bubbleDown(array);
		}
	}
	
	private void bubbleDown(int array){
		int index = 1;
		int child = index*2;
		child = distances[array][child] > distances[array][child+1] ? child : child + 1;
		float[] transform_swap;
		float distance_swap;
		while(child < distances[array].length && distances[array][index] < distances[array][child]){
			transform_swap = transforms[array][index];
			distance_swap = distances[array][index];
			transforms[array][index] = transforms[array][child];
			distances[array][index] = distances[array][child];
			transforms[array][child] = transform_swap;
			distances[array][child] = distance_swap;
			index = child;
			child = index*2;
			child = distances[array][child] > distances[array][child+1] ? child : child + 1;
		}
	}
	
	private void bubbleUp(int array){
		int index = current_instance[array] - 1;
		int parent = index/2;
		if(index <= 1) return;
		float[] transform_swap;
		float distance_swap;
		while(index > 1 && distances[array][index] > distances[array][parent]){
			transform_swap = transforms[array][index];
			distance_swap = distances[array][index];
			transforms[array][index] = transforms[array][parent];
			distances[array][index] = distances[array][parent];
			transforms[array][parent] = transform_swap;
			distances[array][parent] = distance_swap;
			index = parent;
			parent = index/2;
		}
	}
}
