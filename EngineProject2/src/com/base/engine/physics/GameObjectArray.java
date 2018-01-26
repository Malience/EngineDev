package com.base.engine.physics;

import java.util.Queue;

import com.base.engine.core.GameObject;

public class GameObjectArray extends ListArray<GameObject> {
	public GameObjectArray(){super();}
	//public GameObjectArray(GameObject o){super(o);}
	//public GameObjectArray(Queue<GameObject> queue){super(queue);}
	public GameObjectArray(GameObject element){this.array = new GameObject[1]; this.array[0] = element; end = 0;}
	public GameObjectArray(Queue<GameObject> queue){this.array = new GameObject[queue.size()]; queue.toArray(array); end = queue.size() - 1;}
	
	@Override
	protected void expand(){GameObject[] copy = new GameObject[array.length + EXPAND_SIZE]; System.arraycopy(array, 0, copy, 0, array.length); array = copy;}
	public void trim(){GameObject[] copy = end < EXPAND_SIZE ? new GameObject[EXPAND_SIZE + 1] : new GameObject[end + 1];System.arraycopy(array, 0, copy, 0, end + 1);array = copy;}
	public void clear(){this.array = new GameObject[EXPAND_SIZE]; end = -1;}
	
	public void set(GameObject element){this.array = new GameObject[EXPAND_SIZE]; this.array[0] = element; end = 0;}
	
}
