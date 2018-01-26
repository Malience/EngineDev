package com.base.engine.data;

import com.base.engine.rendering.opengl.GLTexture;

public class Texture extends Resource{
	public int texture = -1;
	
	public Texture(String filename) {super(filename);}

	public void setTextureFiltering(int filter) {GLTexture.setFiltering(texture, filter);}
	
	@Override
	public void load() {texture = GLTexture.createTexture(filename);}

	@Override
	public void unload() {GLTexture.deleteTexture(texture); texture = -1;}
	
}
