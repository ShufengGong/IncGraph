package Test;

import java.util.HashSet;

public class TestHashSet {
	
	public static void main(String[] args) {
		HashSet<Integer> set = new HashSet<Integer>();
		System.out.println(set.add(1));
		System.out.println(set.add(2));
		System.out.println(set.add(1));
		System.out.println(set.remove(0));
		System.out.println(set.remove(1));
	}

}
