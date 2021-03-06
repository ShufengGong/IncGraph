package Util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class DynamicVertex {
	
	public int id;
	public HashSet<Integer> neighbor;
	public boolean isValid = false;
	public boolean edgeChanged = false;
	public boolean vertexChanged = false;
	public double delta;
	public double value;
	public int numInNeighbor = 0;
	public HashSet<Integer> inNeighbors;
	public double finalValue;
	
	public ArrayList<Integer> addNeighbors;
	public ArrayList<Integer> delNeighbors;
	
	public DynamicVertex(int id){
		this.id = id;
		neighbor = new HashSet<Integer>();
		addNeighbors = new ArrayList<Integer>();
		delNeighbors = new ArrayList<Integer>();
		inNeighbors = new HashSet<Integer>();
		isValid = false;
		edgeChanged = false;
		vertexChanged = false;
		delta = 0;
		value = 0;
	}
	
	public void add(int id){
		neighbor.add(id);
	}
	
	public boolean del(int id){
		return neighbor.remove(new Integer(id));
	}
	
	public double getDelta(){
		return delta;
	}
	
	public void setDelta(double delta){
		this.delta = delta;
	}

}
