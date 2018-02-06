import math.Vector3f;
import primitives.Plane;

public class PlaneTest {
	public static void main(String [] args) {
		Vector3f eye = new Vector3f(1,0,2);
		Vector3f a = new Vector3f(-1,1,2);
		Vector3f b = new Vector3f(5,0,3);
		
		Plane p = new Plane(eye, a, b);
		
		System.out.println(p);
	}
}
