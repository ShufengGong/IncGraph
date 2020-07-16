package Algo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import Util.DynamicGraph;
import Util.DynamicVertex;

public class DynamicPageRank {

	DynamicGraph graph;

	public DynamicPageRank(DynamicGraph graph) {
		this.graph = graph;
	}

	public void iterate(int time, String path) throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(path));
		ArrayList<DynamicVertex> vertices = graph.vertices;
		for (int i = 0; i < time; i++) {
			System.out.println(i);
			double[] pr = new double[vertices.size()];
			for (DynamicVertex v : vertices) {
				if (v.isValid) {
					HashSet<Integer> neighbors = v.neighbor;
					int size = neighbors.size();
					if (size == 0) {
						double prs = v.value / graph.vertexNum;
						for (int nid = 0; nid < vertices.size(); nid++) {
							if (vertices.get(nid).isValid) {
								pr[nid] += prs;
							}
						}
					} else {
						double prs = v.value / size;
						for (int nid : neighbors) {
							pr[nid] += prs;
							if (!vertices.get(nid).isValid) {
								System.out.println("neighbor is invalid");
							}
						}
					}

				}
			}
			for (int j = 0; j < graph.capacity; j++) {
				vertices.get(j).value = pr[j] * 0.8 + 0.2;
			}
			double value = 0;
			for (DynamicVertex v : vertices) {
				if (v.isValid) {
					value += Math.abs(v.value - v.finalValue);
				}
			}
			bw.write(i + " " + (value / graph.vertexNum) + "\n");
		}
		bw.close();
	}

	public void adjustGraph(String incPath, DynamicGraph graph, double delta)
			throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(incPath));
		String line = null;
		while ((line = br.readLine()) != null) {
			String[] ss = line.split("\\s+");
			if (ss[0].equals("AE")) {
				int source = Integer.parseInt(ss[1]);
				int dest = Integer.parseInt(ss[2]);
				graph.addEdge(source, dest, delta);
			} else if (ss[0].equals("DE")) {
				int source = Integer.parseInt(ss[1]);
				int dest = Integer.parseInt(ss[2]);
				graph.delEdge(source, dest);
			} else if (ss[0].equals("AV")) {
				int vertexId = Integer.parseInt(ss[1]);
				graph.addVertex(vertexId, delta);
			} else if (ss[0].equals("DV")) {
				int vertexId = Integer.parseInt(ss[1]);
				graph.delVertex(vertexId);
			} else {
				System.out.println("no change " + line);
			}
		}
		br.close();

		ArrayList<DynamicVertex> vertices = graph.vertices;
		for (DynamicVertex v : vertices) {
			if (v.edgeChanged) {
				if (!v.addNeighbors.isEmpty()) {
					v.neighbor.addAll(v.addNeighbors);
					if (!v.isValid) {
						v.isValid = true;
					}
					v.addNeighbors.clear();
				}
				if (!v.delNeighbors.isEmpty()) {
					v.delNeighbors.clear();
				}
				v.edgeChanged = false;
			}
		}
	}

	public static void main(String[] args) throws IOException {

		int capacity = 0;
		String input = null;
		String incPath = null;
		String intermediatePath = null;
		String intermediateResultPath = null;

		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-input")) {
				input = args[++i];
			}

			if (args[i].equals("-vertexNum")) {
				capacity = Integer.parseInt(args[++i]);
			}

			if (args[i].equals("-interPath")) {
				intermediatePath = args[++i];
			}

			if (args[i].equals("-interResultPath")) {
				intermediateResultPath = args[++i];
			}

			if (args[i].equals("-incPath")) {
				incPath = args[++i];
			}
		}

		input = "E:/Mypaper/experiment/IncGraph/dataSet/google_90w_edge.txt";
		capacity = 1000000;
		intermediatePath = "E:/Mypaper/experiment/IncGraph/dataSet/google_90w_edge_inter.txt";
		intermediateResultPath = "E:/Mypaper/experiment/IncGraph/dataSet/google_90w_interresult.txt";
		incPath = "E:/Mypaper/experiment/IncGraph/dataSet/google_90w_change.txt";
		String result = "E:/Mypaper/experiment/IncGraph/dataSet/google_90w_edge_result.txt";

		DynamicGraph graph = new DynamicGraph(capacity);
		DynamicPageRank pagerank = new DynamicPageRank(graph);

		graph.init(input, 0.2);
		graph.setValue();
		String miniDataFinalResult = "E:/Mypaper/experiment/IncGraph/dataSet/google_90w_edge_result_final.txt";
		graph.setFinalValue(miniDataFinalResult);
		pagerank.iterate(100, intermediateResultPath + 1);
		pagerank.adjustGraph(incPath, graph, 0.2);
		miniDataFinalResult = "E:/Mypaper/experiment/IncGraph/dataSet/google_90w_edge_result_final_change.txt";
		graph.setFinalValue(miniDataFinalResult);
		pagerank.iterate(100, intermediateResultPath + 2);
		graph.dump(intermediatePath);
		graph.dumpResult(result);
	}

}
