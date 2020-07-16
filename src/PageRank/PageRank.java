package PageRank;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class PageRank {

//	public int num;
	ArrayList<Page> pageList;
	public String input;
//	public double q;
	public int vertexNum = 0;

	public PageRank() {

	}

	public void init(String input, int capacity) {

		pageList = new ArrayList<Page>(capacity);
		
		for(int i = 0; i < capacity; i++){
			pageList.add(new Page(i, new ArrayList<Integer>()));
		}
		try {
			BufferedReader br = new BufferedReader(new FileReader(input));
			String line = null;
			while ((line = br.readLine()) != null) {
				StringTokenizer st = new StringTokenizer(line);
				int source = Integer.parseInt(st.nextToken());
				while(st.hasMoreTokens()){
					int dest = Integer.parseInt(st.nextToken());
					pageList.get(source).neighbor.add(dest);
					pageList.get(source).isValid = true;
					pageList.get(dest).isValid = true;
				}
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		for(int i = 0; i < capacity; i++){
			if(pageList.get(i).isValid){
				pageList.get(i).prValue = Math.random();
				vertexNum++;
			}
		}

	}

	public void itrComp(int itrNum) {
		int length = pageList.size();
		System.out.println(length);
		for (int i = 0; i < itrNum; i++) {
			System.out.println(i);
			double[] pr = new double[length];
			for (int j = 0; j < length; j++) {
				if(pageList.get(j).isValid){
					ArrayList<Integer> list = pageList.get(j).neighbor;
					int size = list.size();
					if(size == 0){
						double prs = pageList.get(j).prValue / vertexNum;
						for (int k = 0; k < pageList.size(); k++) {
							if(pageList.get(k).isValid){
								pr[k] += prs;
							}
						}
						System.out.println("has dead ends");
					}else{
						double prs = pageList.get(j).prValue / size;
						for (int k = 0; k < size; k++) {
							pr[list.get(k)] += prs;
						}
					}
				}
			}
			for (int j = 0; j < length; j++) {
				pageList.get(j).prValue = pr[j] * 0.8 + 0.2;
			}
		}
	}


	public void print(String output) throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(output));
		double sumPr = 0;
		for (int i = 0; i < pageList.size(); i++) {
			if(pageList.get(i).isValid){
				sumPr += pageList.get(i).prValue;
				bw.write(i + "\t" + pageList.get(i).prValue + "\n");
			}
		}
		bw.close();
		System.out.println(sumPr);
	}

	public static void main(String[] args) throws IOException {
		double rate = 0.2;
		String input = "/home/gongsf/dataSet/Google_90/"+rate+"/vertex_changed_data.txt";
		String output = "/home/gongsf/dataSet/Google_90/"+rate+"/vertex_changed_result.txt";
		PageRank pr = new PageRank();
		pr.init(input, 1000000);
		
		pr.itrComp(400);
		pr.print(output);
	}
}