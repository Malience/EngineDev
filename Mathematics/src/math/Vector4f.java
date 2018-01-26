package math;

import java.io.Serializable;

public class Vector4f implements Serializable
{
	private static final long serialVersionUID = -8882673770183831145L;
	
	public float x;
	public float y;
	public float z;
	public float w;
	//Constructor
	public Vector4f(){};
	public Vector4f(float x, float y, float z){this.x = x; this.y = y; this.z = z;}
	public Vector4f(float x, float y, float z, float w){this.x = x; this.y = y; this.z = z; this.w = w;}
	public Vector4f(Vector4f r) {x = r.x; y = r.y; z = r.z; w = r.w;}
	
	//Getters/Setters for values
	public Vector4f set(float x, float y, float z, float w) { this.x = x; this.y = y; this.z = z; this.w = w; return this; }
	public Vector4f set(Vector4f r) { set(r.x, r.y, r.z, r.w); return this; }
	
	//Basic Outputs
	public String toString(){return "(" + x + " " + y + " " + z + " " + w + ")";}
}