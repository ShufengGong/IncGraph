package PageRank;

import java.util.ArrayList;
import java.util.List;

public class Page {
	
	public int id;
	public double lastPr;
	public double prValue;
	public ArrayList<Integer> neighbor;
//	public int inDirect;
	public boolean isValid;
	
	public Page(int id, ArrayList<Integer> neighbor){
		this.id = id;
		this.neighbor = neighbor;
		this.isValid = false;
	}
}
