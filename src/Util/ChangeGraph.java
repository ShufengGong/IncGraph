package Util;

import java.io.BufferedReader;
import java.io.IOException;

public class ChangeGraph {
	
	public static void main(String[] args) throws NumberFormatException, IOException {
		double rate = 0.2;
		String input = "/home/gongsf/dataSet/Google_90/vertex.txt";
		String output = "/home/gongsf/dataSet/Google_90/"+rate+"/vertex_inc_data.txt";
		DynamicGraph graph = new DynamicGraph(1000000);
		graph.init(input, 0.2);
		graph.randomChange(output, rate);
		System.out.println("change finished!!");
	}
}
