package primitive_stacks;

public class ShortStack {
	private static final int DEFAULT_SIZE = 5;
	private static final int DEFAULT_EXPANSION_SIZE = 5;
	
	private short[] stack;
	private int expansion_size;
	private volatile int top = -1;
	
	public ShortStack() {this(DEFAULT_SIZE, DEFAULT_EXPANSION_SIZE);}
	public ShortStack(int size) {this(size, DEFAULT_EXPANSION_SIZE);}
	public ShortStack(int size, int expansion_size) {stack = new short[size]; this.expansion_size = expansion_size;}
	
	public synchronized short pop() {if(stack.length - top > expansion_size << 1) shrink(); return stack[top--];}
	public synchronized void push(short x) {if(++top >= stack.length) expand(); stack[top] = x;}
	public short peek() {return stack[top];}
	public boolean isEmpty() {return top < 0;}
	public String toString() {String s = "["; for(int i = 0; i < top; i++) s += stack[i] + ", "; s += top < 0 ? "]" : stack[top] + "]"; return s;}
	
	public void setExpansionSize(int expansion_size) {this.expansion_size = expansion_size;}
	private void expand() {short[] copy = new short[stack.length + expansion_size];System.arraycopy(stack, 0, copy, 0, stack.length); stack = copy;}
	private void shrink() {short[] copy = new short[stack.length - expansion_size];System.arraycopy(stack, 0, copy, 0, copy.length); stack = copy;}
}
