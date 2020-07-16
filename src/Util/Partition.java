package Util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.StringTokenizer;

public class Partition {
	
	public static void main(String[] args) throws IOException {
		String rate = "0.2";
		String input = "/home/gongsf/dataSet/Google_90/vertex_inited.txt";
		String output = "/home/gongsf/dataSet/Google_90/part";
		int partNum = 2;
		
		BufferedReader br = new BufferedReader(new FileReader(input));
		BufferedWriter[] bw = new BufferedWriter[partNum];
		for(int i = 0; i < partNum; i++){
			bw[i] = new BufferedWriter(new FileWriter(output +i));
		}
		
		String line = null;
		while((line = br.readLine()) != null){
			StringTokenizer st = new StringTokenizer(line);
			int id = Integer.parseInt(st.nextToken());
			bw[id % partNum].write(line + "\n");
		}
		for(int i = 0; i < partNum; i++){
			bw[i].close();
		}
		
		br.close();
	}

}
