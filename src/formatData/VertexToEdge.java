package formatData;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.StringTokenizer;

public class VertexToEdge {
	
	public static void main(String[] args) throws IOException {
		String input = "E:/Mypaper/experiment/IncGraph/dataSet/google_90w.txt";
		String output = "E:/Mypaper/experiment/IncGraph/dataSet/google_90w_edge.txt";
		
		BufferedReader br = new BufferedReader(new FileReader(input));
		BufferedWriter bw = new BufferedWriter(new FileWriter(output));
		String line = null;
		while((line = br.readLine()) != null){
			StringTokenizer st = new StringTokenizer(line);
			String source = st.nextToken();
			while(st.hasMoreTokens()){
				bw.write(source + " " + st.nextToken() + "\n");
			}
		}
		br.close();
		bw.close();
//		BufferedReader br = new BufferedReader(new FileReader(output));
//		for(int i = 0; i < 10; i++){
//			System.out.println(i);
//			System.out.println(br.readLine());
//		}
//		br.close();
	}

}
