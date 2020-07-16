package Util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class TotalAbsDiff {

	public static void main(String[] args) throws IOException {
		double rate = 0.05;
		String finalPath = "/home/gongsf/dataSet/Google_90/" + rate
				+ "/vertex_changed_result.txt";
		String currentPath = "/home/gongsf/dataSet/Google_90/" + rate
				+ "/vertex_changed_value_delta.txt";

		ArrayList<Double> finalList = new ArrayList<Double>();

		BufferedReader finalReader = new BufferedReader(new FileReader(
				finalPath));
		String line = null;
		while ((line = finalReader.readLine()) != null) {
			StringTokenizer st = new StringTokenizer(line);
			int id = Integer.parseInt(st.nextToken());
			double value = Double.parseDouble(st.nextToken());
			finalList.add(value);
		}
		finalReader.close();
		
		double total = 0;
		BufferedReader currReader = new BufferedReader(new FileReader(currentPath));
		line = null;
		while((line = currReader.readLine()) != null){
			StringTokenizer st = new StringTokenizer(line);
			int id = Integer.parseInt(st.nextToken());
			double value = Double.parseDouble(st.nextToken());
			total += Math.abs(value - finalList.get(id));
		}
		currReader.close();
		System.out.println(total);
	}

}
