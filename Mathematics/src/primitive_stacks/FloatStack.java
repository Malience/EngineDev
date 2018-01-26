package primitive_stacks;

public class FloatStack {
	private static final int DEFAULT_SIZE = 5;
	private static final int DEFAULT_EXPANSION_SIZE = 5;
	
	private float[] stack;
	private int expansion_size;
	private volatile int top = -1;
	
	public FloatStack() {this(DEFAULT_SIZE, DEFAULT_EXPANSION_SIZE);}
	public FloatStack(int size) {this(size, DEFAULT_EXPANSION_SIZE);}
	public FloatStack(int size, int expansion_size) {stack = new float[size]; this.expansion_size = expansion_size;}
	
	public synchronized float pop() {if(stack.length - top > expansion_size << 1) shrink(); return stack[top--];}
	public synchronized void push(float x) {if(++top >= stack.length) expand(); stack[top] = x;}
	public float peek() {return stack[top];}
	public boolean isEmpty() {return top < 0;}
	public String toString() {String s = "["; for(int i = 0; i < top; i++) s += stack[i] + ", "; s += top < 0 ? "]" : stack[top] + "]"; return s;}
	
	public void setExpansionSize(int expansion_size) {this.expansion_size = expansion_size;}
	private void expand() {float[] copy = new float[stack.length + expansion_size];System.arraycopy(stack, 0, copy, 0, stack.length); stack = copy;}
	private void shrink() {float[] copy = new float[stack.length - expansion_size];System.arraycopy(stack, 0, copy, 0, copy.length); stack = copy;}
}
