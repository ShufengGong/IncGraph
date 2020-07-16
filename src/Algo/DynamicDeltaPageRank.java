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

public class DynamicDeltaPageRank {

	DynamicGraph graph;

	public DynamicDeltaPageRank(DynamicGraph graph) {
		this.graph = graph;
		ArrayList<DynamicVertex> vertices = graph.vertices;
		for (DynamicVertex v : vertices) {
			v.isValid = false;
			v.edgeChanged = false;
			v.setDelta(0);
		}
	}

	public void iterate(int time, String path) throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(path));
		ArrayList<DynamicVertex> vertices = graph.vertices;
		for (int i = 0; i < time; i++) {
			System.out.println(i);
			for (DynamicVertex v : vertices) {
				if (v.isValid && v.delta != 0) {
					int size = v.neighbor.size();
					v.value += v.delta;
					if (size == 0) {
						double delta = v.delta * 0.8 / graph.vertexNum;
						v.delta = 0;
						for (DynamicVertex v2 : vertices) {
							if (v2.isValid)
								v2.delta += delta;
						}
					} else {
						double delta = v.delta * 0.8 / size;
						v.delta = 0;
						for (int j : v.neighbor) {
							vertices.get(j).delta += delta;
						}
					}

				}
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
		graph.lastVertexNum = graph.vertexNum;
		HashSet<Integer> deadEnds = new HashSet<Integer>(graph.deadEnds);
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

		adjustDelta(deadEnds);

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

	public void adjustDelta(HashSet<Integer> deadEnds) {

		ArrayList<DynamicVertex> vertices = graph.vertices;
		for (DynamicVertex v : vertices) {
			if (v.edgeChanged) {
				
				int numAddNeighbor = v.addNeighbors.size();
				int numDelNeighbor = v.delNeighbors.size();
				int numOriNeighbor = v.neighbor.size() + numDelNeighbor;
				int numNeighbor = v.neighbor.size() + numAddNeighbor;
				if (numOriNeighbor != 0 && numNeighbor == 0) {
					// the out edges become empty
					double priorDelta = (v.value / numOriNeighbor) * 0.8;
					double nowDelta = (v.value / graph.vertexNum) * 0.8;
					for (int i = 0; i < graph.capacity; i++) {
						DynamicVertex neighbor = vertices.get(i);
						if (neighbor.isValid) {
							neighbor.delta += nowDelta;
						}
					}
					for (int i = 0; i < v.delNeighbors.size(); i++) {
						DynamicVertex neighbor = vertices.get(v.delNeighbors.get(i));
						neighbor.delta -= priorDelta;
					}
				} else if (numOriNeighbor == 0 && numNeighbor != 0) {
					// the out edges become not empty
					double priorDelta = (v.value / graph.lastVertexNum) * 0.8;
					double nowDelta = (v.value / numNeighbor) * 0.8;
					for (int i = 0; i < v.addNeighbors.size(); i++) {
						DynamicVertex neighbor = vertices.get(v.addNeighbors.get(i));
						neighbor.delta += nowDelta;
					}
					for (int i = 0; i < graph.capacity; i++) {
						DynamicVertex neighbor = vertices.get(i);
						if (neighbor.isValid) {
							neighbor.delta -= priorDelta;
						}
					}
				} else {
					double priorDelta = (v.value / numOriNeighbor) * 0.8;
					double nowDelta = (v.value / numNeighbor) * 0.8;
					double changeDelta = nowDelta - priorDelta;
					for (int i = 0; i < v.addNeighbors.size(); i++) {
						DynamicVertex neighbor = vertices.get(v.addNeighbors.get(i));
						neighbor.delta += nowDelta;
					}
					for (int i : v.neighbor) {
						DynamicVertex neighbor = vertices.get(i);
						neighbor.delta += changeDelta;
					}
					for (int i = 0; i < v.delNeighbors.size(); i++) {
						DynamicVertex neighbor = vertices.get(v.delNeighbors.get(i));
						neighbor.delta -= priorDelta;
					}
				}

			}

			if (v.vertexChanged) {
				if (v.isValid) {
					// new added vertex

				} else {
					// deleted vertex
					if(v.delNeighbors.isEmpty()){
						double priorDelta = (v.value / graph.lastVertexNum) * 0.8;
						for (int i = 0; i < graph.capacity; i++) {
							DynamicVertex neighbor = vertices.get(i);
							if (neighbor.isValid) {
								neighbor.delta -= priorDelta;
							}
						}
					}else{
						double priorDelta = (v.value / graph.vertexNum) * 0.8;
						for (int i = 0; i < graph.capacity; i++) {
							DynamicVertex neighbor = vertices.get(i);
							if (neighbor.isValid) {
								neighbor.delta -= priorDelta;
							}
						}
					}
					

				}
				v.vertexChanged = false;
			}
		}
		
		for (int i : deadEnds) {
			DynamicVertex vertex = vertices.get(i);
			if(vertex.isValid){
				double priorDelta = (vertex.value / graph.lastVertexNum) * 0.8;
				double nowDelta = (vertex.value / graph.vertexNum) * 0.8;
				double delta = nowDelta - priorDelta;
				for (DynamicVertex v : vertices) {
					if (v.isValid) {
						if (v.vertexChanged) {
							v.delta += nowDelta;
						} else {
							v.delta += delta;
						}
					}
				}
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

		capacity = 1000000;
		String rate = "0.01";
		input = "/home/gongsf/dataSet/Google_90/vertex.txt";
		String valueAndDeltaPath = "/home/gongsf/dataSet/Google_90/vertex_value_delta_round.txt";
		incPath = "/home/gongsf/dataSet/Google_90/"+rate+"/vertex_inc_data.txt";
//		intermediatePath = "E:/Mypaper/experiment/IncGraph/dataSet/google_90w_edge_inter.txt";
//		intermediateResultPath = "E:/Mypaper/experiment/IncGraph/dataSet/google_90w_edge_iterResult.txt";
//		incPath = "E:/Mypaper/experiment/IncGraph/dataSet/google_90w_change.txt";
		String changedGraph = "/home/gongsf/dataSet/Google_90/"+rate+"/vertex_changed_data.txt";
		String adjustedValueDelta = "/home/gongsf/dataSet/Google_90/"+rate+"/vertex_changed_value_delta_round.txt";

		DynamicGraph graph = new DynamicGraph(capacity);
		DynamicDeltaPageRank pagerank = new DynamicDeltaPageRank(graph);

		graph.init(input, 0.2);
		graph.setValueAndDelta(valueAndDeltaPath);
		pagerank.adjustGraph(incPath, graph, 0.2);
		graph.dump(changedGraph);
		graph.dumpResult(adjustedValueDelta);
		System.out.println("finished");
	}

}
