package Util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.StringTokenizer;

import Algo.DynamicDeltaPageRank;

public class StaticGraph {
	
	public ArrayList<StaticVertex> vertices;
	public HashSet<Integer> deadEnds;
	public int vertexNum;
	public int lastVertexNum;
	public int capacity;
	
	public StaticGraph(int capacity){
		
//		this.vertexNum = vertexNum;
		this.capacity = capacity;
		deadEnds = new HashSet<Integer>();
		vertices = new ArrayList<StaticVertex>(capacity);
		for(int i = 0; i < capacity; i++){
			vertices.add(new StaticVertex(i));
		}
	}
	
	public void init(String input, double delta) throws NumberFormatException, IOException{
		BufferedReader br = new BufferedReader(new FileReader(input));
		String line = null;
		for(int i = 0; (line = br.readLine()) != null; i++){
			StringTokenizer st = new StringTokenizer(line);
			int source = Integer.parseInt(st.nextToken());
			while(st.hasMoreTokens()){
				int dest = Integer.parseInt(st.nextToken());
				addEdge(source, dest, delta);
				if(vertices.get(source).neighbor.contains(dest)){
					System.out.println("repeat in out neighbors");
				}
				vertices.get(source).add(dest);
			}
		}
		br.close();
		
		for(int i = 0; i < vertexNum; i++){
			StaticVertex v = vertices.get(i);
			if(v.isValid && v.inNeighbors.size() == 0){
				int id = (int)(Math.random() * vertexNum);
				addEdge(id, v.id, delta);
			}
		}
		
		for(int i = 0; i < vertexNum; i++){
			StaticVertex v = vertices.get(i);
			v.edgeChanged = false;
			v.vertexChanged = false;
			v.addNeighbors.clear();
			v.delNeighbors.clear();
			if(v.isValid && v.neighbor.size() == 0){
				deadEnds.add(v.id);
				System.out.println("had dead ends " + v.id);
			}
			if(v.isValid && v.inNeighbors.size() == 0){
				System.out.println("had dead ends ssss " + v.id);
			}
		}
		System.out.println(deadEnds.size());
	}
	
	public void setValue(){
		for(StaticVertex v : vertices){
			v.value = Math.random();
		}
	}
	
	public void setFinalValue(String path) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(path));
		String line = null;
		while((line = br.readLine()) != null){
			String[] ss = line.split("\\s+");
			int id = Integer.parseInt(ss[0]);
			double value = Double.parseDouble(ss[1]);
			vertices.get(id).finalValue = value;
		}
		br.close();
	}
	
	public void addEdge(int source, int dest, double delta){
		StaticVertex v = vertices.get(source);
		if(!v.isValid){
			addVertex(source, delta);
		}
		if(v.neighbor.size() == 0){
			if(!deadEnds.remove(v.id)){
				System.out.println("the dead end not in deadEnds");
				System.exit(1);
			}
		}
		v.addNeighbors.add(dest);
		v.edgeChanged = true;
		if(!vertices.get(dest).isValid){
			addVertex(dest, delta);
		}
		vertices.get(dest).numInNeighbor++;
		if(vertices.get(dest).inNeighbors.contains(source)){
			System.out.println("repeat in neighbors");
		}
		
		vertices.get(dest).inNeighbors.add(source);
	}
	
	public void delEdge(int source, int dest){
		StaticVertex v = vertices.get(source);
		if(v.del(new Integer(dest))){
			if(v.neighbor.size() == 0){
				deadEnds.add(v.id);
			}
			v.delNeighbors.add(dest);
			v.edgeChanged = true;
			vertices.get(dest).numInNeighbor--;
			vertices.get(dest).inNeighbors.remove(source);
		}else{
			System.out.println("there is no edge : " + source + " " + dest);
		}
	}
	
	public void addVertex(int id, double delta){
		StaticVertex v = vertices.get(id);
		if(v.isValid){
			System.out.println("Vertex " + id + " exists!");
		}else{
			v.isValid = true;
			v.vertexChanged = true;
			v.delta = delta;
			vertexNum++;
			if(vertexNum > capacity){
				System.out.println("vertexNum is larger than capacity");
				System.exit(0);
			}
			if(!deadEnds.add(v.id)){
				System.out.println("the invalid vertex in deadEnds");
			}
		}
	}
	
	public void delVertex(int id){
		StaticVertex v = vertices.get(id);
		if(!v.isValid){
			System.out.println("Vertex " + id + " has been deleted!");
		}else{
			if(!v.inNeighbors.isEmpty()){
				HashSet<Integer> set = new HashSet<Integer>(v.inNeighbors);
				for(int vid : set){
					delEdge(vid, id);
				}
			}
			if(!v.neighbor.isEmpty()){
				HashSet<Integer> set = new HashSet<Integer>(v.neighbor);
				for(int vid : set){
					delEdge(id, vid);
				}
			}
			
			v.isValid = false;
			v.vertexChanged = true;
			vertexNum--;
			if(vertexNum < 0){
				System.out.println("vertexNum is smaller than 0");
				System.exit(0);
			}
			if(!deadEnds.remove(v.id)){
				System.out.println("the dead end not in deadEnds");
			}
		}
	}
	
	public void randomChange(String path) throws IOException{
		BufferedWriter bw = new BufferedWriter(new FileWriter(path));
		for(StaticVertex vertex : vertices){
			if(vertex.isValid){
				HashSet<Integer> set = new HashSet<Integer>(vertex.neighbor);
				for(int vid : set){
					if(Math.random() > 0.8){
						bw.write("DE " + vertex.id + " " + vid + "\n");
						vertex.neighbor.remove(vid);
					}
				}
				if(vertex.neighbor.isEmpty()){
					bw.write("AE " + vertex.id + " " + ((int)(Math.random() * vertexNum)) + "\n");
				}
			}
		}
		bw.close();
	}
	
	public void dump(String path) throws IOException{
		BufferedWriter bw = new BufferedWriter(new FileWriter(path));
		for(StaticVertex v : vertices){
			if(v.isValid){
				if(!v.neighbor.isEmpty()){
					bw.write(v.id + "\t");
					for(int id : v.neighbor){
						bw.write(id + " ");
					}
				}else{
					System.out.println("no neighbors");
					bw.write(v.id);
				}
				bw.write("\n");
			}
		}
		bw.close();
	}
	
	public void dumpResult(String path) throws IOException{
		BufferedWriter bw = new BufferedWriter(new FileWriter(path));
		for(StaticVertex v : vertices){
			if(v.isValid){
				bw.write(v.id + " " + v.value + " " + v.delta + "\n");
			}
		}
		bw.close();
	}
	
	public static void main(String[] args) throws NumberFormatException, IOException {
		String input = "/home/gongsf/dataSet/Google_90/vertex.txt";
		int capacity = 1000000;
		String result = "/home/gongsf/dataSet/Google_90/vertex_format.txt";

		StaticGraph graph = new StaticGraph(capacity);
		graph.init(input, 0.2);
		graph.dump(result);
	}
}
