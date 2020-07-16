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

public class DynamicGraph {

	public ArrayList<DynamicVertex> vertices;
	public HashSet<Integer> deadEnds;
	public int vertexNum;
	public int lastVertexNum;
	public int capacity;

	public DynamicGraph(int capacity) {

		// this.vertexNum = vertexNum;
		this.capacity = capacity;
		deadEnds = new HashSet<Integer>();
		vertices = new ArrayList<DynamicVertex>(capacity);
		for (int i = 0; i < capacity; i++) {
			vertices.add(new DynamicVertex(i));
		}
	}

	public void init(String input, double delta) throws NumberFormatException,
			IOException {
		BufferedReader br = new BufferedReader(new FileReader(input));
		String line = null;
		for (int i = 0; (line = br.readLine()) != null; i++) {
			StringTokenizer st = new StringTokenizer(line);
			int source = Integer.parseInt(st.nextToken());
			while (st.hasMoreTokens()) {
				int dest = Integer.parseInt(st.nextToken());
				addEdge(source, dest, delta);
				if (vertices.get(source).neighbor.contains(dest)) {
					System.out.println("repeat in out neighbors");
				}
				vertices.get(source).add(dest);
			}
		}
		br.close();

		for (int i = 0; i < vertexNum; i++) {
			DynamicVertex v = vertices.get(i);
			v.edgeChanged = false;
			v.vertexChanged = false;
			v.addNeighbors.clear();
			v.delNeighbors.clear();
			if (v.isValid && v.neighbor.size() == 0) {
				deadEnds.add(v.id);
			}
		}
	}

	public void setValueAndDelta(String deltaAndValue) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(deltaAndValue));
		String line = null;
		while ((line = br.readLine()) != null) {
			String[] ss = line.split("\\s+");
			if (ss.length == 3) {
				int id = Integer.parseInt(ss[0]);
				double value = Double.parseDouble(ss[1]);
				double delta = Double.parseDouble(ss[2]);
				vertices.get(id).value = value;
				vertices.get(id).delta = delta;
			} else {
				System.out.println("not have value or delta");
			}
		}
		br.close();
	}

	public void setValue() {
		for (DynamicVertex v : vertices) {
			v.value = Math.random();
		}
	}

	public void setFinalValue(String path) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(path));
		String line = null;
		while ((line = br.readLine()) != null) {
			String[] ss = line.split("\\s+");
			int id = Integer.parseInt(ss[0]);
			double value = Double.parseDouble(ss[1]);
			vertices.get(id).finalValue = value;
		}
		br.close();
	}

	public void addEdge(int source, int dest, double delta) {
		DynamicVertex v = vertices.get(source);
		if (!v.isValid) {
			addVertex(source, delta);
		}
		if (v.neighbor.size() == 0) {
			if (!deadEnds.remove(v.id)) {
				System.out.println("the dead end not in deadEnds");
				System.exit(1);
			}
		}
		if (!vertices.get(dest).isValid) {
			addVertex(dest, delta);
		}
		// if(v.n)
		v.addNeighbors.add(dest);
		v.edgeChanged = true;
		if (vertices.get(dest).inNeighbors.contains(source)) {
			System.out.println("repeat in neighbors");
			System.out.println(dest + " " + source);
			System.exit(1);
		} else {
			vertices.get(dest).numInNeighbor++;
			vertices.get(dest).inNeighbors.add(source);
		}
	}

	public void delEdge(int source, int dest) {
		DynamicVertex v = vertices.get(source);
		if (v.del(new Integer(dest))) {
			if (v.neighbor.size() == 0) {
				deadEnds.add(v.id);
			}
			v.delNeighbors.add(dest);
			v.edgeChanged = true;
			vertices.get(dest).numInNeighbor--;
			vertices.get(dest).inNeighbors.remove(source);
		} else {
			System.out.println("there is no edge : " + source + " " + dest);
		}
	}

	public void addVertex(int id, double delta) {
		DynamicVertex v = vertices.get(id);
		if (v.isValid) {
			System.out.println("Vertex " + id + " exists!");
		} else {
			v.isValid = true;
			v.vertexChanged = true;
			v.delta = delta;
			vertexNum++;
			if (vertexNum > capacity) {
				System.out.println("vertexNum is larger than capacity");
				System.exit(0);
			}
			if (!deadEnds.add(v.id)) {
				System.out.println("the invalid vertex in deadEnds");
			}
		}
	}

	public void delVertex(int id) {
		DynamicVertex v = vertices.get(id);
		if (!v.isValid) {
			System.out.println("Vertex " + id + " has been deleted!");
		} else {
			if (!v.inNeighbors.isEmpty()) {
				HashSet<Integer> set = new HashSet<Integer>(v.inNeighbors);
				for (int vid : set) {
					delEdge(vid, id);
				}
			}
			if (!v.neighbor.isEmpty()) {
				HashSet<Integer> set = new HashSet<Integer>(v.neighbor);
				for (int vid : set) {
					delEdge(id, vid);
				}
			}

			v.isValid = false;
			v.vertexChanged = true;
			vertexNum--;
			if (vertexNum < 0) {
				System.out.println("vertexNum is smaller than 0");
				System.exit(0);
			}
			if (!deadEnds.remove(v.id)) {
				System.out.println("the dead end not in deadEnds");
			}
		}
	}

	public void randomChange(String path, double rate) throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(path));
		for (DynamicVertex vertex : vertices) {
			if (vertex.isValid) {
				HashSet<Integer> set = new HashSet<Integer>(vertex.neighbor);
				for (int vid : set) {
					if (Math.random() > 0.5) {
						if (Math.random() < rate) {
							bw.write("DE " + vertex.id + " " + vid + "\n");
							vertex.neighbor.remove(vid);
						}
					} else {
						if (Math.random() < rate) {
							int dest = (int) (Math.random() * vertexNum);
							while (set.contains(dest)) {
								dest = (int) (Math.random() * vertexNum);
							}
							bw.write("AE " + vertex.id + " " + dest + "\n");
							vertex.neighbor.add(vid);
						}
					}
				}
				if (vertex.neighbor.isEmpty()) {
					bw.write("AE " + vertex.id + " "
							+ ((int) (Math.random() * vertexNum)) + "\n");
				}
			}
		}
		bw.close();
	}

	public void dump(String path) throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(path));
		for (DynamicVertex v : vertices) {
			if (v.isValid) {
				bw.write(v.id + "\t");
				if (!v.neighbor.isEmpty()) {
					for (int id : v.neighbor) {
						bw.write(id + " ");
					}
				}else{
					bw.write(v.id);
				}
				bw.write("\n");
			}
		}
		bw.close();
	}

	public void dumpResult(String path) throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(path));
		for (DynamicVertex v : vertices) {
			if (v.isValid) {
				bw.write(v.id + " " + v.value + " " + v.delta + "\n");
			}
		}
		bw.close();
	}

	public static void main(String[] args) throws NumberFormatException,
			IOException {
		int capacity = 1000000;
		String input = "/home/gongsf/dataSet/Google_90/vertex.txt";

		String result = "/home/gongsf/dataSet/Google_90/vertex_delta_result.txt";

		DynamicGraph graph = new DynamicGraph(capacity);
		graph.init(input, 0.2);
		graph.dump(result);
	}
}
