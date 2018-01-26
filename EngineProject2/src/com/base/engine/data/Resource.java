package com.base.engine.data;

public abstract class Resource {
	public final String filename;
	//0 Implies that it is unloaded
	private volatile int instances = 0;
	
	public Resource(String filename) {this.filename = filename; load();}
	
	public abstract void load();
	public abstract void unload();
	
	public void increment() {instances++;}
	public void decrement() {instances--; if(instances <= 0) unload();}
	
	@Override
	public void finalize() {this.unload();}
	public String toString() {return filename;}
}
