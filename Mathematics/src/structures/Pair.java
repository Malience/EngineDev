package structures;

import java.io.Serializable;

public class Pair <A,B> implements Serializable
{
	private static final long serialVersionUID = 181410687146320835L;
	
	public A first;
	public B second;
	
	public Pair(A first, B second)
	{
		this.first = first;
		this.second = second;
	}
	
	public void delete()
	{
		first = null;
		second = null;
	}
}
