package com.base.engine.physics;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

import com.base.engine.core.GameObject;
import com.base.engine.core.util.Util;
import com.base.engine.rendering.RStructure;
import primitives.AABB;

public class Octree {
	private static final int MIN_SIZE = 1;
	
	private OctreeNode root;
	
	private Queue<GameObject> queue = new LinkedList<GameObject>();
	private GameObjectArray[] object_array = new GameObjectArray[5];
	private int next = 0;
	
	private boolean built = false;
	
	public Octree(int x, int y, int z){root = new OctreeNode(new AABB(x,y,z, -x,-y,-z));}
	public Octree(int x, int y, int z, GameObject... o){
		root = new OctreeNode(new AABB(x,y,z, -x,-y,-z));
		for(int i = 0; i < o.length; i++) queue.add(o[i]);
	}
	
	public void add(GameObject o){queue.add(o);}
	
	//Static methods
	public void update(float delta){
		if(!built){
			if(!queue.isEmpty()) {object_array[0] = new GameObjectArray(queue); queue.clear(); root.objects = 0;}
			root.buildTree(); built = true;
		} else while(!queue.isEmpty()) root.insert(queue.poll());
		this.root.update(delta);
	}
	
	//Assumes that object is inside bounding box 
	public static byte getOctant(GameObject o, float centerx, float centery, float centerz){
//		byte octant = 0b0;
//		if(o.collider.minx + o.transform.pos.x >= centerx) octant |= 0b1;
//		else if(o.collider.maxx + o.transform.pos.x > centerx) return -1;	//x-axis
//		if(o.collider.miny + o.transform.pos.y >= centery) octant |= 0b10;
//		else if(o.collider.maxy + o.transform.pos.y > centery) return -1;	//y-axis
//		if(o.collider.minz + o.transform.pos.z >= centerz) octant |= 0b100;
//		else if(o.collider.maxz + o.transform.pos.z > centerz) return -1;	//z-axis
//		return octant;
		return 0;
	}
	
	public int getNextArray(){
		while(next < object_array.length){
			if(object_array[next] == null){object_array[next] = new GameObjectArray(); return next++;}
			else if(object_array[next].end < 0) return next++;
			next++;
		}
		expand();
		return next++;
	}
	public int getNextArray(GameObject o){
		while(next < object_array.length){
			if(object_array[next] == null){object_array[next] = new GameObjectArray(o); return next++;}
			else if(object_array[next].end < 0){object_array[next].add(o); return next++;}
			next++;
		}
		expand();
		object_array[next] = new GameObjectArray(o);
		return next++;
	}
	
//	public void getAllMeshes(RStructure meshes){
//		for(GameObjectArray array : object_array){
//			if(array != null) 
//		}
//	}
	
	public void get(Frustum f, TContainer t){get(root, f, t);}
	
	public boolean get(OctreeNode n, Frustum f, TContainer t){
		int check = 0;//f.contains(n.region);
		if(check == 1) {if(!getAll(n, t)) return false;}
		else if(check == 0){
			if(n.active_nodes != 0) for(int i = 0; i < 8; i++) 
				if (n.nodes[i] != null) if(!get(n.nodes[i], f, t)) return false;
			if(!getCheck(f, t, n.objects)) return false;
		}
		return true;
	}
	
	public boolean get(TContainer t, int i){for(GameObject o : object_array[i].array) if(o != null) if(!t.add(o.transform)) return false; return true;}
	public boolean getCheck(Frustum f, TContainer t, int i){
		if(i < 0) return true;
//		for(GameObject o : object_array[i].array) 
//			if(o != null && f.intersects(o.collider)) 
//				if(!t.add(o.transform)) 
//					return false; 
		return true;}
	
	public void getAll(TContainer t){for(int i = 0; i < next; i++) for(GameObject o : object_array[i].array) if(o != null) if(!t.add(o.transform)) return;}
	public boolean getAll(OctreeNode n, TContainer t){
		if(n.active_nodes != 0) for(int i = 0; i < 8; i++) 
			if(n.nodes[i] != null) if(!getAll(n.nodes[i], t)) return false;
		return get(t, n.objects);
	}
	
	private void expand(){GameObjectArray[] copy  = new GameObjectArray[object_array.length + 5];System.arraycopy(object_array, 0, copy, 0, object_array.length);object_array = copy;}
	public String toString(){return root.region.toString() + "\n" + object_array.toString();}
	
	private class OctreeNode {
		private AABB region;

		private OctreeNode parent;
		private OctreeNode[] nodes;
		
		private int objects;
		private int max_lifespan = 8;
		private int current_life = -1;
		
		private byte active_nodes = 0b00000000;
		
		//Constructors
		@SuppressWarnings("unused")
		public OctreeNode(){}
		public OctreeNode(AABB region){this.region = region;}
		private OctreeNode(AABB region, int objects){this.region = region; this.objects = objects;}
		
		//Methods
		public void update(float delta){
			if(!built) return; //TODO: Handle unbuilt trees
			if(objects >= 0 && object_array[objects].end < 0){if(objects < next) next = objects; objects = -1;}
			if(objects < 0) { 
				if(active_nodes == 0){if(current_life == -1) current_life = max_lifespan;else if(current_life > 0) current_life--; return;}
				for(int i = 0; i < 8; i++) if (nodes[i] != null) nodes[i].update(delta);
				return;
			}
			else if(current_life != -1){if(max_lifespan <= 64) max_lifespan += max_lifespan; current_life = -1;}
			
			Stack<Integer> movedObjects = new Stack<>();//m_objects.Count);
			for(int i = 0; i <= object_array[objects].end; i++) if(object_array[objects].array[i].update(delta)) movedObjects.add(i);
			
			if(active_nodes != 0) for(int i = 0; i < 8; i++) 
				if (nodes[i] != null){if(nodes[i].current_life == 0){active_nodes ^= 1 << i; if(active_nodes == 0) nodes = null; else nodes[i] = null; } else nodes[i].update(delta);}
			
			for(int i : movedObjects){
				OctreeNode current = this;
				//TODO: Fix
//				while(!current.region.contains(object_array[objects].array[i])){
//						if(current.parent != null) current = current.parent;
//						else break; //Expand upwards
//				}
				
				if(current == this && this.insertSelf(object_array[objects].array[i])) object_array[objects].remove(i);
				else {current.insert(object_array[objects].array[i]); object_array[objects].remove(i);}
			}
		}
		
		private void insert(GameObject o){
//			if(active_nodes == 0 && this.size() < 1){this.add(o);return;}
//			if (region.maxx - region.minx <= MIN_SIZE && region.maxy - region.miny <= MIN_SIZE && region.maxz - region.minz <= MIN_SIZE){this.add(o); return;}
//			
//			float 	centerx = (region.maxx + region.minx) * 0.5f,
//					centery = (region.maxy + region.miny) * 0.5f,
//					centerz = (region.maxz + region.minz) * 0.5f;
//			
//			byte octant = getOctant(o, 	centerx, centery, centerz);
//			if(octant < 0) this.add(o);
//			else {
//				if(nodes == null) nodes = new OctreeNode[8];
//				if(nodes[octant] == null) nodes[octant] = createNode(getOctantBB(centerx, centery, centerz, octant), o);
//				else nodes[octant].insert(o);
//			}	
		}
		
		private boolean insertSelf(GameObject o){
//			if(active_nodes == 0 && this.size() < 1) return false;
//			if (region.maxx - region.minx <= MIN_SIZE && region.maxy - region.miny <= MIN_SIZE && region.maxz - region.minz <= MIN_SIZE) return false;
//			
//			float 	centerx = (region.maxx + region.minx) * 0.5f,
//					centery = (region.maxy + region.miny) * 0.5f,
//					centerz = (region.maxz + region.minz) * 0.5f;
//			
//			byte octant = getOctant(o, 	centerx, centery, centerz);
//			if(octant >= 0) {
//				if(nodes == null) nodes = new OctreeNode[8];
//				if(nodes[octant] == null) nodes[octant] = createNode(getOctantBB(centerx, centery, centerz, octant), o);
//				else nodes[octant].insert(o);
//				return true;
//			}
			return false;
		}
		
		private void buildTree(){
			if(objects < 0 || object_array[objects].end < 1) return;
//			
//			//check against min-size 
//			if(region.maxx - region.minx <= MIN_SIZE && region.maxy - region.miny <= MIN_SIZE && region.maxz - region.minz <= MIN_SIZE) return;
//			
//			float 	centerx = (region.maxx + region.minx) * 0.5f,
//					centery = (region.maxy + region.miny) * 0.5f,
//					centerz = (region.maxz + region.minz) * 0.5f;
//			
//			int[] octant_objects = new int[]{-1,-1,-1,-1,-1,-1,-1,-1};
//			int i = 0;
//			while(i <= object_array[objects].end){
//				byte octant = getOctant(object_array[objects].array[i], centerx, centery, centerz);
//				if(octant >= 0){
//					if(octant_objects[octant] < 0) octant_objects[octant] = getNextArray(object_array[objects].array[i]);
//					else object_array[octant_objects[octant]].add(object_array[objects].array[i]);
//					object_array[objects].remove(i);
//				}
//				else i++;
//			}
//			
//			for(i = 0; i < 8; i++){
//				if(octant_objects[i] >= 0){
//					if(nodes == null) nodes = new OctreeNode[8];
//					nodes[i] = createNode(getOctantBB(centerx, centery, centerz, i), octant_objects[i]);
//					active_nodes |= 1 << i;
//					nodes[i].buildTree();
//				}
//			}
			object_array[objects].trim();
		}
		
		private void add(GameObject o){if(objects < 0) objects = getNextArray(); object_array[objects].add(o);}
		private int size(){return objects < 0 ? 0 : object_array[objects].end + 1;}
		
		private OctreeNode createNode(AABB box, int i){
			if(i < 0) return null;
			
			OctreeNode node = new OctreeNode(box, i);
			node.parent = this;
			return node;
		}
		
		private OctreeNode createNode(AABB box, GameObject o){
			if(o == null) return null;
			OctreeNode node = new OctreeNode(box);
			node.objects = getNextArray(o);
			node.parent = this;
			return node;
		}
		
		private AABB getOctantBB(float centerx, float centery, float centerz, int octant){
			AABB box = new AABB();
//			if((octant & (0b1)) == 0){box.maxx = centerx; box.minx = region.minx;}
//			else{box.maxx = region.maxx; box.minx = centerx;}
//			if((octant & (0b10)) == 0){box.maxy = centery; box.miny = region.miny;}
//			else{box.maxy = region.maxy; box.miny = centery;}
//			if((octant & (0b100)) == 0){box.maxz = centery; box.minz = region.minz;}
//			else{box.maxz = region.maxz; box.minz = centerz;}
			return box;
		}
		
		public String toString(){return region.toString() + "\n" + object_array[objects];}
	}
}
