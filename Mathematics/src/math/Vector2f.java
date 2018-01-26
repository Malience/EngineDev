package math;

import java.io.Serializable;

public class Vector2f implements Serializable
{
	private static final long serialVersionUID = 3808306732265514410L;
	
	public float x;
	public float y;
	//Constructors
	public Vector2f(float x, float y){this.x = x; this.y = y;}
	public Vector2f(Vector2f r){this.x = r.x; this.y = r.y;}

	//Basic Vector Functions
	public Vector2f normal(){float length = length(); return new Vector2f(x / length, y / length);}
	public float distance(Vector2f r){return (float) Math.sqrt(Math.pow(x - r.x, 2) + Math.pow(y - r.y, 2));}
	public float length(){return (float)Math.sqrt(x * x + y * y);}
	public float max(){return Math.max(x, y);}
	public float dot(Vector2f r){return x * r.getX() + y * r.getY();}
	public float cross(Vector2f r){return x * r.getY() - y * r.getX();}
	
	//Basic Arithmetic Operations (Addition, Subtraction, Multiplication, Division)
	public Vector2f add(Vector2f r){return new Vector2f(x + r.getX(), y + r.getY());}
	public Vector2f add(float r){return new Vector2f(x + r, y + r);}
	public Vector2f sub(Vector2f r){return new Vector2f(x - r.getX(), y - r.getY());}
	public Vector2f sub(float r){return new Vector2f(x - r, y - r);}
	public Vector2f mul(Vector2f r){return new Vector2f(x * r.getX(), y * r.getY());}
	public Vector2f mul(float r){return new Vector2f(x * r, y * r);}
	public Vector2f div(Vector2f r){return new Vector2f(x / r.getX(), y / r.getY());}
	public Vector2f div(float r){return new Vector2f(x / r, y / r);}
	
	//Basic Algebra Operations
	public Vector2f abs(){return new Vector2f(Math.abs(x), Math.abs(y));}
	
	//Rotations
	public Vector2f rotate(float angle)
	{
		double rad = Math.toRadians(angle);
		double cos = Math.cos(rad);
		double sin = Math.sin(rad);
		return new Vector2f((float)(x * cos - y * sin),(float)(x * sin + y * cos));
	}
	
	//Basic Game Operations
	public Vector2f lerp(Vector2f dest, float lerpFactor){return dest.sub(this).mul(lerpFactor).add(this);}
	
	//Getters/Setters for values
	public float getX() {return x;}
	public void setX(float x) {this.x = x;}
	public float getY() {return y;}
	public void setY(float y){this.y = y;}
	public Vector2f set(float x, float y) { this.x = x; this.y = y; return this; }
	public Vector2f set(Vector2f r) { set(r.getX(), r.getY()); return this; }

	//Compare Methods
	public boolean equals(Vector2f r){return x == r.getX() && y == r.getY();}
	public int compareTo(Vector2f r)
	{
		if(x < r.x && y < r.y)return -1;
		else if(x > r.x && y > r.y)return 1;
		return 0; //CAREFUL, this does not mean they are equal!!!!!
	}
	//Basic Outputs
	public String toString(){return "(" + x + " " + y + ")";}
}