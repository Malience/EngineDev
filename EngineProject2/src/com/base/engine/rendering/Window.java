package com.base.engine.rendering;

public abstract class Window {
	protected int width;
	protected int height;
	private String title;
	
	public int getWidth(){return width;}
	public int getHeight(){return height;}
	public String getTitle(){return title;}
	
	public Window(String title, int width, int height) {
		this.width = width;
		this.height = height;
	}
}
