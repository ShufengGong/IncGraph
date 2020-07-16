package Util;

import java.io.*;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class InitDeltaAndValue {

    public static void main(String[] args) throws IOException {
    	double rate = 0.01;
        String input = "/home/gongsf/dataSet/Google_90/"+rate+"/vertex_changed_data.txt";
        String finalResult = "/home/gongsf/dataSet/Google_90/"+rate+"/vertex_changed_result.txt";
        String valueAndDelta = "/home/gongsf/dataSet/Google_90/"+rate+"/vertex_changed_value_delta_round.txt";
        String output = "/home/gongsf/dataSet/Google_90/"+rate+"/vertex_changed_inited_round.txt";

        int capacity = 1000000;
        ArrayList<Double> finalValueList = new ArrayList<Double>(capacity);
        for(int i = 0; i < capacity; i++){
        	finalValueList.add((double) -1);
        }
        BufferedReader resultBr = new BufferedReader(new FileReader(finalResult));
        String line = null;
        while((line = resultBr.readLine()) != null){
            String[] ss = line.split("\\s+");
            int source = Integer.parseInt(ss[0]);
            double value = Double.parseDouble(ss[1]);
//            System.out.println(source);
            finalValueList.set(source, value);
        }
        resultBr.close();
        
        ArrayList<Double> valueList = new ArrayList<Double>();
        ArrayList<Double> deltaList = new ArrayList<Double>();
        BufferedReader valueDeltaBr = new BufferedReader(new FileReader(valueAndDelta));
        line = null;
        while((line = valueDeltaBr.readLine()) != null){
        	String[] ss = line.split("\\s+");
        	if(ss.length != 3){
        		System.out.println("not have value or delta");
        	}else{
        		int source = Integer.parseInt(ss[0]);
        		if(valueList.size() == source){
        			valueList.add(Double.parseDouble(ss[1]));
        			deltaList.add(Double.parseDouble(ss[2]));
        		}else{
        			System.out.println("what fuck");
        		}
        	}
        }
        valueDeltaBr.close();

        BufferedWriter bw = new BufferedWriter(new FileWriter(output));
        BufferedReader br = new BufferedReader(new FileReader(input));
        line = null;
        while((line = br.readLine()) != null){
            StringTokenizer st = new StringTokenizer(line);
            String id = st.nextToken();
            bw.write(id + "\t");
            int idInt = Integer.parseInt(id);
            bw.write(finalValueList.get(idInt) + "\t");
            bw.write(valueList.get(idInt) + "\t");
            bw.write(deltaList.get(idInt) + "\t");
//            bw.write(0 + "\t");
//            bw.write(0.2 + "\t");
            while(st.hasMoreTokens()){
                bw.write(st.nextToken() + " ");
            }
            bw.write("\n");
        }
        br.close();
        bw.close();
    }
}
