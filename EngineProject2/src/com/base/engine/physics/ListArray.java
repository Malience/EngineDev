package com.base.engine.physics;

import java.util.Queue;

import com.base.engine.core.util.Util;

@SuppressWarnings("unchecked")
public class ListArray<E> {
	protected static final int EXPAND_SIZE = 1;
	
	public E[] array;
	public int end;
	
	public ListArray(){this(EXPAND_SIZE);}
	public ListArray(int size){this.array = (E[])new Object[size]; end = -1;}
	public ListArray(E... array){this.array = array; end = array.length - 1;}
	public ListArray(Queue<E> queue){this.array = (E[])queue.toArray(); end = queue.size() - 1;}
	public ListArray(E element){this.array = (E[])new Object[1]; this.array[0] = element; end = 0;}
	
	//O(1) remove is unchecked
	public void add(E element){if(++end == array.length) expand(); array[end] = element;}
	public void remove(int i){array[i] = array[end]; array[end--] = null;}
	//O(n)
	public void remove(E element){for(int i = 0; i <= end; i++)if(array[i] == element){this.remove(i); return;}}
	
	protected void expand(){this.array = Util.expandArray(array, EXPAND_SIZE);}
	public void trim(){E[] copy =(E[]) (end < EXPAND_SIZE ? new Object[EXPAND_SIZE + 1] : new Object[end + 1]);System.arraycopy(array, 0, copy, 0, end + 1);array = copy;}
	public void clear(){this.array = (E[])new Object[EXPAND_SIZE]; end = -1;}
	
	public void set(E element){this.array = (E[])new Object[EXPAND_SIZE]; this.array[0] = element; end = 0;}
	public void set(E... array){this.array = array; end = array.length - 1;}
	
	public String toString(){if(end < 0) return "[]"; String s = "[" + array[0]; for(int i = 1; i <= end; i++) s += ", " + array[i]; s += "]"; return s;}
}