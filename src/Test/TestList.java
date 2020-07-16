package Test;

import java.util.ArrayList;
import java.util.LinkedList;

public class TestList {
	
	public static void main(String[] args) {
		ArrayList<Integer> list = new ArrayList<Integer>();
		list.add(0);
		list.add(21);
		list.add(13);
		list.add(23);
		list.add(1);
		list.add(2);
		
		System.out.println(list.get(1));
		
//		list.remove(new Integer(2));
//		
//		for(int v: list){
//			System.out.println(v);
//		}
	}

}
