package com.base.engine.physics;

public class ListArrayTest {
	public static void main(String [] args){
		ListArray<Integer> array = new ListArray<>(0,1);
		Integer g = 3;
		
		
		array.add(2);
		array.add(g);
		array.add(4);
		array.add(5);
		array.add(6);
		
		array.remove(1);
		array.remove(g);
		
		array.trim();
		
		array.clear();
		
		array.set(1);
		array.set(1,2);
	}
}
