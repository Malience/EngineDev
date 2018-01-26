package structures;
import primitive_stacks.IntStack;

public class ArrayObjectTable {
	private static final int DEFAULT_SIZE = 5;
	private static final int DEFAULT_EXPANSION_SIZE = 5;
	
	private GameObject[] object_table;
	private IntStack free_list;
	private int expansion_size;
	private volatile int next = 0;
	
	public ArrayObjectTable() {this(DEFAULT_SIZE, DEFAULT_EXPANSION_SIZE);}
	public ArrayObjectTable(int size) {this(size, DEFAULT_EXPANSION_SIZE);}
	public ArrayObjectTable(int size, int expansion_size) {object_table = new GameObject[size]; free_list = new IntStack(size, expansion_size); this.expansion_size = expansion_size;}
	
	public int createObject() {
		if(!free_list.isEmpty()) {
			int free = free_list.pop();
			object_table[free].id = free;
			return free;
		}
		if(next >= object_table.length) expand();
		object_table[next] = new GameObject(next);
		return next++;
	}
	
	public GameObject get(int id) {return object_table[id].id < 0 ? null : object_table[id];}
	public void destroyObject(int id) {object_table[id].id = -1; free_list.push(id);}
	
	public void setExpansionSize(int expansion_size) {this.expansion_size = expansion_size;}
	private void expand() {expand(expansion_size);}
	private void expand(int amount) {GameObject[] copy = new GameObject[object_table.length + amount];System.arraycopy(object_table, 0, copy, 0, object_table.length); object_table = copy;}
}