package primitive_stacks;
//Please explain to me why you need this
public class BooleanStack {
	private static final int DEFAULT_SIZE = 5;
	private static final int DEFAULT_EXPANSION_SIZE = 5;
	
	private boolean[] stack;
	private int expansion_size;
	private volatile int top = -1;
	
	public BooleanStack() {this(DEFAULT_SIZE, DEFAULT_EXPANSION_SIZE);}
	public BooleanStack(int size) {this(size, DEFAULT_EXPANSION_SIZE);}
	public BooleanStack(int size, int expansion_size) {stack = new boolean[size]; this.expansion_size = expansion_size;}
	
	public synchronized boolean pop() {if(stack.length - top > expansion_size << 1) shrink(); return stack[top--];}
	public synchronized void push(boolean x) {if(++top >= stack.length) expand(); stack[top] = x;}
	public boolean peek() {return stack[top];}
	public boolean isEmpty() {return top < 0;}
	public String toString() {String s = "["; for(int i = 0; i < top; i++) s += stack[i] + ", "; s += top < 0 ? "]" : stack[top] + "]"; return s;}
	
	public void setExpansionSize(int expansion_size) {this.expansion_size = expansion_size;}
	private void expand() {boolean[] copy = new boolean[stack.length + expansion_size];System.arraycopy(stack, 0, copy, 0, stack.length); stack = copy;}
	private void shrink() {boolean[] copy = new boolean[stack.length - expansion_size];System.arraycopy(stack, 0, copy, 0, copy.length); stack = copy;}
}
