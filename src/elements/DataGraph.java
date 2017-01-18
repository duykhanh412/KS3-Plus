package elements;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import simulation.Config;
import simulation.QueryPW;
import simulation.Setting;
import tools.Digraph;
import tools.StdOut;
import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloLinearNumExpr;
import ilog.cplex.IloCplex;

public class DataGraph {
	private static final String NEWLINE = System.getProperty("line.separator");

	public int count = 0;
	public Setting setting;

	public ArrayList<Node> nodes;
	public ArrayList<Edge> edges;

	public ArrayList<String> keywords;
	public ArrayList<String> candidateKeywords;

	Digraph dig;

	public ArrayList<String> APIs;
	public HashMap<String, ArrayList<Node>> invertedIndex; // index of nodes
															// that contain
															// specific keywords
	public HashMap<Node, ArrayList<Edge>> edgeIndex; // save the edges that are
														// connected to each
														// node
	public HashMap<Edge, ArrayList<Node>> nodeIndex; // save the nodes that are
														// connected to each
														// edge

	public HashMap<Node, ArrayList<Node>> adjIndex; // save the neighbour nodes
													// of each node

	public HashMap<Node, ArrayList<Node>> undirectedAdjIndex; // save the
																// neighbour
																// nodes of each
																// node
																// (Undirected)

	public String[][] all_mashup_api = new String[13185][3]; // [mashup_id][mashup_name][api_name]
	public HashMap<Integer, ArrayList<String>> hm_mashups_apis = new HashMap<Integer, ArrayList<String>>();
	HashMap<String, ArrayList<String>> API_API_Strings = new HashMap<String, ArrayList<String>>();
	HashMap<Integer, ArrayList<Integer>> API_API_Integers = new HashMap<Integer, ArrayList<Integer>>();

	long timeConsumptionLight;

	double numberOfServicesLight;

	public DataGraph() {
		this.keywords = new ArrayList<String>();

		this.nodes = new ArrayList<Node>();
		this.edges = new ArrayList<Edge>();

	}

	public DataGraph(Digraph dig, Setting setting) {
		this.dig = dig;
		this.setting = setting;

		this.keywords = new ArrayList<String>();

		// construct a DataGraph based on a Digraph
		this.nodes = new ArrayList<Node>();
		this.edges = new ArrayList<Edge>();

		// initialise the node index and edge index and adjIndex
		edgeIndex = new HashMap<Node, ArrayList<Edge>>();
		nodeIndex = new HashMap<Edge, ArrayList<Node>>();
		adjIndex = new HashMap<Node, ArrayList<Node>>();
		undirectedAdjIndex = new HashMap<Node, ArrayList<Node>>();

		File fin;
		BufferedReader br = null;
		PrintWriter writer;

		// select candidate keywords from all available keywords based on the
		// number of nodes in the data graph
		this.candidateKeywords = new ArrayList<String>();
		Random r = new Random();
		int loopCounter = 0;
		int numKeywords = this.setting.numNodes / 10; // determines how many
														// nodes per keyword
		for (int i = 0; i < numKeywords; ++i) {
			this.candidateKeywords.add(Config.ALL_KEYWORDS[i]);
		}
		String ks;
		/*
		 * for(int i=0; i<numKeywords; ++i) {
		 * 
		 * do { ks=Config.ALL_KEYWORDS[r.nextInt(Config.NUM_ALL_KEYWORDS)];
		 * StdOut.println("Trying to prepare keywords ..."); ++loopCounter;
		 * if(loopCounter > numKeywords + 100) { //System.exit(-1); } }
		 * while(this.candidateKeywords.indexOf(ks) != -1);
		 * this.candidateKeywords.add(ks); System.out.println(ks +
		 * " is selected as a valid keyword."); }
		 */

		// initialise the inverted index
		invertedIndex = new HashMap<String, ArrayList<Node>>();
		for (int i = 0; i < this.candidateKeywords.size(); ++i) {
			ArrayList<Node> al = new ArrayList<Node>();
			invertedIndex.put(this.candidateKeywords.get(i), al);
		}

		String keyword;
		Node node;

		// create the nodes
		int index;
		for (int i = 0; i < dig.V(); ++i) {
			index = r.nextInt(this.candidateKeywords.size());
			keyword = this.candidateKeywords.get(index);
			node = new Node(keyword, setting);
			node.ID = i;
			nodes.add(node);

			// add node to inverted index as created
			invertedIndex.get(node.kw).add(node);

			// create edge index for node created
			this.edgeIndex.put(node, new ArrayList<Edge>());

			// intialise ArrayList in adjIndex for node created
			this.adjIndex.put(node, new ArrayList<Node>());

			this.undirectedAdjIndex.put(node, new ArrayList<Node>());

			// insert the keyword into the set of keywords in graph
			if (this.keywords.indexOf(keyword) == -1) {
				this.keywords.add(keyword);
			}
		}

		// create the edges and link them to nodes
		Edge edge;
		int edgeIDCounter = 0;
		for (int i = 0; i < dig.V(); ++i) {
			for (int j : dig.adj[i]) {
				edge = new Edge();
				edge.ID = edgeIDCounter++;
				edge.fromNode = nodes.get(i);
				edge.toNode = nodes.get(j);
				edges.add(edge);

				// create node index for edge created
				this.nodeIndex.put(edge, new ArrayList<Node>());

				// add to and from nodes to edge index as created
				this.nodeIndex.get(edge).add(nodes.get(i));
				this.nodeIndex.get(edge).add(nodes.get(j));

				// add edge to node index as created
				this.edgeIndex.get(nodes.get(i)).add(edge);
				this.edgeIndex.get(nodes.get(j)).add(edge);

				// add node to adjIndex
				this.adjIndex.get(edge.fromNode).add(edge.toNode);

				// add node to undirectedAdjIndex
				this.undirectedAdjIndex.get(edge.fromNode).add(edge.toNode);
				this.undirectedAdjIndex.get(edge.toNode).add(edge.fromNode);
			}
		}

		// make sure all nodes are connected to at least two other nodes
		for (int i = 0; i < this.nodes.size(); ++i) {
			node = this.nodes.get(i);
			if (this.adjIndex.get(node).size() < 2) {
				int nodeIndex[] = new int[2];
				do {
					nodeIndex[0] = r.nextInt(this.nodes.size());
					nodeIndex[1] = r.nextInt(this.nodes.size());
				} while (nodeIndex[0] == node.ID || nodeIndex[1] == node.ID || nodeIndex[0] == nodeIndex[1]);

				edge = new Edge();
				edge.ID = edgeIDCounter++;
				edge.fromNode = node;
				edge.toNode = this.nodes.get(nodeIndex[0]);
				edges.add(edge);

				// create node index for edge created
				this.nodeIndex.put(edge, new ArrayList<Node>());

				// add to and from nodes to edge index as created
				this.nodeIndex.get(edge).add(edge.fromNode);
				this.nodeIndex.get(edge).add(edge.toNode);

				// add edge to node index as created
				this.edgeIndex.get(edge.fromNode).add(edge);
				this.edgeIndex.get(edge.toNode).add(edge);

				// add node to adjIndex
				this.adjIndex.get(edge.fromNode).add(edge.toNode);

				// add node to undirectedAdjIndex
				this.undirectedAdjIndex.get(edge.fromNode).add(edge.toNode);
				this.undirectedAdjIndex.get(edge.toNode).add(edge.fromNode);

				// generate the second added edge
				edge = new Edge();
				edge.ID = edgeIDCounter++;
				edge.fromNode = this.nodes.get(nodeIndex[1]);
				edge.toNode = node;
				edges.add(edge);

				// create node index for edge created
				this.nodeIndex.put(edge, new ArrayList<Node>());

				// add to and from nodes to edge index as created
				this.nodeIndex.get(edge).add(edge.fromNode);
				this.nodeIndex.get(edge).add(edge.toNode);

				// add edge to node index as created
				this.edgeIndex.get(edge.fromNode).add(edge);
				this.edgeIndex.get(edge.toNode).add(edge);

				// add node to adjIndex
				this.adjIndex.get(edge.fromNode).add(edge.toNode);

				// add node to undirectedAdjIndex
				this.undirectedAdjIndex.get(edge.fromNode).add(edge.toNode);
				this.undirectedAdjIndex.get(edge.toNode).add(edge.fromNode);
			}
		}

		// link multiple apis to corresponding mashups based on
		// all_mashup_api[][]
		// ArrayList<String> apiList;
		// for (int i = 0; i < all_mashup_api.length; ++i) {
		// apiList = hm_mashups_apis.get(Integer
		// .parseInt(all_mashup_api[i][0]));
		// if (apiList != null) {
		// apiList.add(all_mashup_api[i][2]);
		// // System.out.println("Adding " + all_mashup_api[i][2] +
		// // " to API #" + all_mashup_api[i][0]);
		// hm_mashups_apis.put(Integer.parseInt(all_mashup_api[i][0]),
		// apiList);
		// } else {
		// apiList = new ArrayList<String>();
		// apiList.add(all_mashup_api[i][2]);
		// // System.out.println("Adding " + all_mashup_api[i][2] +
		// // " to API #" + all_mashup_api[i][0]);
		// hm_mashups_apis.put(Integer.parseInt(all_mashup_api[i][0]),
		// apiList);
		// }
		// }
	}

	/*
	 * public Node getRandomNeighbourWithinKSteps(Node node, int K) { //get
	 * random neighbour WITHIN k steps Node currentNode=node;
	 * if(this.invertedIndex.get(node.kw).size() == 0) { StdOut.println(
	 * "Exception occurred in getRandomNeighbourWithinKSteps! The given node is not in the graph!"
	 * ); return null; } else { Random r = new Random(); int k; int loopCounter
	 * = 0; do { ++loopCounter; if(loopCounter > 20) { System.exit(-1); } k =
	 * r.nextInt(K)+1; for(int i=0; i<k; ++i) { ArrayList<Node> neighbours =
	 * this.adjIndex.get(currentNode); //get the neighbours of the current node
	 * int size = neighbours.size(); //get the number of neighbours of the
	 * current node if(size == 0) {
	 * StdOut.println("This node does not have a neighbour!"); } else { int t =
	 * r.nextInt(size);
	 * StdOut.println("There is a number of "+this.adjIndex.get(
	 * currentNode).size()+" adjacent nodes of node "+currentNode.ID+"."); Node
	 * neighbour = neighbours.get(t); if(this.adjIndex.get(neighbour).size() !=
	 * 0) { //to ensure the selected neighbour has at least one neighbour
	 * currentNode = this.adjIndex.get(currentNode).get(t); } } }
	 * StdOut.println("Attempting to get random neighbourWithinKSteps ..."); }
	 * while(currentNode == node); return currentNode; } }
	 */

	public DataGraph(File fin, Setting setting) {
		this.setting = setting;

		ArrayList<String> APIs;
		Boolean[][] API_API;

		APIs = new ArrayList<String>();
		API_API = new Boolean[1496][1496];
		for (int i = 0; i < API_API.length; ++i) {
			for (int j = 0; j < API_API[i].length; ++j) {
				API_API[i][j] = false;
			}
		}

		String line = null;
		String[][] mashup_id_name = new String[6295][2];
		Integer index = 0;
		String[] parts;
		String[] mashupID;
		String[] mashupName;

		String[][] all_mashup_api = new String[13248][3]; // [mashup_id][mashup_name][api_name]
		HashMap<String, Integer> uiqueAPIs = new HashMap<String, Integer>();
		HashMap<String, Integer> uniqueAPIs = new HashMap<String, Integer>();

		BufferedReader br;
		PrintWriter writer;

		// read mashups-apis
		try {
			br = new BufferedReader(new FileReader(fin));

			while ((line = br.readLine()) != null) {
				parts = line.split("VALUES");
				// System.out.println(parts[1]);

				parts = parts[1].split(",");
				parts[0] = parts[0].split("'")[1];
				parts[1] = parts[1].split("'")[1];
				parts[2] = parts[2].split("'")[1];
				// System.out.println(parts[0] + " - " + parts[1] + " - " +
				// parts[2]);
				all_mashup_api[index][0] = parts[0];
				all_mashup_api[index][1] = parts[1];
				all_mashup_api[index][2] = parts[2];
				++index;
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// output all_mashup_api[][]
		/*
		 * for(int i=0; i<all_mashup_api.length; ++i) {
		 * System.out.println(all_mashup_api[i][0] + " - " +
		 * all_mashup_api[i][1] + " - " + all_mashup_api[i][2]); }
		 */

		// link multiple apis to corresponding mashups based on
		// all_mashup_api[][]
		// HashMap<Integer, ArrayList<String>> hm_mashups_apis = new
		// HashMap<Integer, ArrayList<String>>();
		ArrayList<String> apiList;
		for (int i = 0; i < all_mashup_api.length; ++i) {
			apiList = hm_mashups_apis.get(Integer.parseInt(all_mashup_api[i][0]));
			if (apiList != null) {
				apiList.add(all_mashup_api[i][2]);
				// System.out.println("Adding " + all_mashup_api[i][2] +
				// " to API #" + all_mashup_api[i][0]);
				hm_mashups_apis.put(Integer.parseInt(all_mashup_api[i][0]), apiList);
			} else {
				apiList = new ArrayList<String>();
				apiList.add(all_mashup_api[i][2]);
				// System.out.println("Adding " + all_mashup_api[i][2] +
				// " to API #" + all_mashup_api[i][0]);
				hm_mashups_apis.put(Integer.parseInt(all_mashup_api[i][0]), apiList);
			}
		}
		// output links between mashups and apis in hm_mashups_apis
		/*
		 * Set<HashMap.Entry<Integer, ArrayList<String>>> entrySet =
		 * hm_mashups_apis.entrySet(); for (Entry<Integer, ArrayList<String>>
		 * entry : entrySet) { System.out.print("Mashup #" + entry.getKey() +
		 * ": "); for(int i=0; i<entry.getValue().size(); ++i) {
		 * System.out.print(entry.getValue().get(i) + ", "); }
		 * System.out.println(""); }
		 */
		// write hm_mashups_apis to file mashups_apis.txt
		try {
			writer = new PrintWriter("d:\\mashups_apis.txt", "UTF-8");

			for (Integer key : hm_mashups_apis.keySet()) {
				writer.print(key + "#");
				for (int i = 0; i < hm_mashups_apis.get(key).size(); ++i) {
					writer.print(hm_mashups_apis.get(key).get(i));
					if (i != hm_mashups_apis.get(key).size() - 1) {
						writer.print("#");
					}
				}
				writer.println("");
			}
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// extract all unique apis from hm_mashups_apis
		uniqueAPIs = new HashMap<String, Integer>();
		Set<HashMap.Entry<Integer, ArrayList<String>>> mashup_APIs_Set = hm_mashups_apis.entrySet();
		Integer apiIndex = 0;
		for (Entry<Integer, ArrayList<String>> entry : mashup_APIs_Set) {
			for (int i = 0; i < entry.getValue().size(); ++i) {
				if (uniqueAPIs.get(entry.getValue().get(i)) == null) {
					uniqueAPIs.put(entry.getValue().get(i), apiIndex);
					++apiIndex;
				}
			}
		}
		// output all unique apis in uniqueAPIs
		// Set<HashMap.Entry<String, Integer>> apiSet = uniqueAPIs.entrySet();
		// for(Entry<String, Integer> entry : apiSet) {
		// System.out.println("API #" + entry.getValue() + ":\t" +
		// entry.getKey());
		// }

		// insert all unique APIs into the ArrayList named APIs
		String[] APIArray = new String[uniqueAPIs.size()];
		Set<HashMap.Entry<String, Integer>> APISet = uniqueAPIs.entrySet();
		// into an array first
		for (Entry<String, Integer> entry : APISet) {
			APIArray[entry.getValue()] = entry.getKey();
		}
		// then into the ArrayList named APIs
		for (int i = 0; i < APIArray.length; ++i) {
			APIs.add(APIArray[i]);
		}
		// output the ArrayList named APIs
		/*
		 * for(int i=0; i<APIs.size(); ++i) { System.out.println("API #" + i +
		 * ": " + APIs.get(i)); }
		 */
		// write APIs to file APIs.txt
		try {
			writer = new PrintWriter("d:\\APIs.txt", "UTF-8");

			for (int i = 0; i < APIs.size(); ++i) {
				writer.println("API #" + i + ": " + APIs.get(i));
			}
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// link API to API based on mashup_APIs_Set extracted from
		// HashMap<Integer, ArrayList<String>> hm_mashups_apis
		String[] apiArray;
		for (Entry<Integer, ArrayList<String>> entry : mashup_APIs_Set) {
			if (entry.getValue().size() > 1) { // only process mashups that use
												// more than 1 API
				apiArray = entry.getValue().toArray(new String[entry.getValue().size()]);
				// for(int i=0; i<apiArray.length; ++i)
				// {System.out.print(apiArray[i] + ", ");}
				// System.out.println("");
				for (int i = 0; i < entry.getValue().size() - 1; ++i) {
					String service1 = entry.getValue().get(i);
					String service2 = entry.getValue().get(i + 1);

					Integer service1Index = uniqueAPIs.get(service1);
					Integer service2Index = uniqueAPIs.get(service2);

					// add to API_API
					API_API[service1Index][service2Index] = true;
					API_API[service2Index][service1Index] = true;

					// add to API_API_Integers
					if (API_API_Integers.get(service1Index) == null) {
						ArrayList<Integer> al = new ArrayList<Integer>();
						al.add(service2Index);
						API_API_Integers.put(service1Index, al);
						// System.out.println(service1Index);
					} else {
						if (API_API_Integers.get(service1Index).contains(service2Index)) {

						} else {
							API_API_Integers.get(service1Index).add(service2Index);
						}
					}

					// add to API_API_Strings
					if (API_API_Strings.get(service1) == null) {
						ArrayList<String> al = new ArrayList<String>();
						al.add(service2);
						API_API_Strings.put(service1, al);
						// System.out.println(service1Index);
					} else {
						if (API_API_Strings.get(service1).contains(service2)) {

						} else {
							API_API_Strings.get(service1).add(service2);
						}
					}
				}
			}
		}
		// output API_API
		/*
		 * for(int i=0; i<API_API.length; ++i) { for(int j=0;
		 * j<API_API[i].length; ++j) { if(API_API[i][j] == true) {
		 * System.out.print("1"); } else { System.out.print("0"); } }
		 * System.out.println(""); }
		 */
		// output API_API_Integers
		/*
		 * Set<HashMap.Entry<Integer, ArrayList<Integer>>> entrySet =
		 * this.API_API_Integers.entrySet(); for (Entry<Integer,
		 * ArrayList<Integer>> entry : entrySet) { System.out.print("API #" +
		 * entry.getKey() + ": "); for(int i=0; i<entry.getValue().size(); ++i)
		 * { System.out.print(entry.getValue().get(i)); if(i !=
		 * entry.getValue().size() - 1) { System.out.print(", "); } }
		 * System.out.println(""); }
		 */
		// output API_API_Strings
		/*
		 * Set<HashMap.Entry<String, ArrayList<String>>> entrySet =
		 * API_API_Strings.entrySet(); for (Entry<String, ArrayList<String>>
		 * entry : entrySet) { System.out.print("API " + entry.getKey() + ": ");
		 * for(int i=0; i<entry.getValue().size(); ++i) {
		 * System.out.print(entry.getValue().get(i)); if(i !=
		 * entry.getValue().size() - 1) { System.out.print(", "); } }
		 * System.out.println(""); }
		 */
		// grabbing data from mashups_apis.txt finishes here

		// initialise DataGraph to be used in experiments
		this.keywords = new ArrayList<String>();

		// construct a DataGraph based on the data extracted from
		// mashups_apis.txt
		this.nodes = new ArrayList<Node>();
		this.edges = new ArrayList<Edge>();

		// initialise the node index and edge index and adjIndex
		edgeIndex = new HashMap<Node, ArrayList<Edge>>();
		nodeIndex = new HashMap<Edge, ArrayList<Node>>();
		adjIndex = new HashMap<Node, ArrayList<Node>>();

		// select candidate keywords from all available keywords based on the
		// number of nodes in the data graph
		this.candidateKeywords = new ArrayList<String>();
		Random r = new Random();
		int numKeywords = APIs.size(); // determines how many keywords
		for (int i = 0; i < numKeywords; ++i) {
			this.candidateKeywords.add(APIs.get(i));
		}

		// initialise the inverted index
		invertedIndex = new HashMap<String, ArrayList<Node>>();
		for (int i = 0; i < this.candidateKeywords.size(); ++i) {
			ArrayList<Node> al = new ArrayList<Node>();
			invertedIndex.put(this.candidateKeywords.get(i), al);
		}

		String keyword;
		Node node;

		// create the nodes
		int numNodes = this.candidateKeywords.size(); // determine the number of
														// nodes in the graph,
														// in this case, it
														// equals to the number
														// of keywords/APIs
		for (int i = 0; i < numNodes; ++i) {
			keyword = this.candidateKeywords.get(i);
			node = new Node(keyword, setting);
			node.ID = i;
			nodes.add(node);

			// add node to inverted index as created
			invertedIndex.get(node.kw).add(node);

			// create edge index for node created
			this.edgeIndex.put(node, new ArrayList<Edge>());

			// initialise ArrayList in adjIndex for node created
			this.adjIndex.put(node, new ArrayList<Node>());

			// insert the keyword into the set of keywords in graph
			if (this.keywords.indexOf(keyword) == -1) {
				this.keywords.add(keyword);
			}
		}

		// create the edges and link them to nodes
		Edge edge;
		int edgeIDCounter = 0;
		// for(int i=0; i<numNodes; ++i) {
		for (Entry<Integer, ArrayList<Integer>> entry : API_API_Integers.entrySet()) {
			for (int j = 0; j < entry.getValue().size(); ++j) {
				edge = new Edge();
				edge.ID = edgeIDCounter++;
				edge.fromNode = nodes.get(entry.getKey()); // for now,
															// API1->API2 is
															// generated only,
															// not API1<-API2
				edge.toNode = nodes.get(entry.getValue().get(j));
				edges.add(edge);

				// create node index for edge created
				this.nodeIndex.put(edge, new ArrayList<Node>());

				// add to and from nodes to edge index as created
				this.nodeIndex.get(edge).add(nodes.get(entry.getKey()));
				this.nodeIndex.get(edge).add(nodes.get(entry.getValue().get(j)));

				// add edge to node index as created
				this.edgeIndex.get(nodes.get(entry.getKey())).add(edge);
				this.edgeIndex.get(nodes.get(entry.getValue().get(j))).add(edge);

				// add node to adjIndex
				this.adjIndex.get(edge.fromNode).add(edge.toNode);
			}
		}

		// make sure all nodes are connected to at least two other nodes
		/*
		 * for(int i=0; i<this.nodes.size(); ++i) { node = this.nodes.get(i);
		 * if(this.adjIndex.get(node).size() < 2) { int nodeIndex[] = new
		 * int[2]; do { nodeIndex[0] = r.nextInt(this.nodes.size());
		 * nodeIndex[1] = r.nextInt(this.nodes.size()); } while(nodeIndex[0] ==
		 * node.ID || nodeIndex[1] == node.ID || nodeIndex[0] == nodeIndex[1]);
		 * 
		 * edge = new Edge(); edge.ID = edgeIDCounter++; edge.fromNode = node;
		 * edge.toNode = this.nodes.get(nodeIndex[0]); edges.add(edge);
		 * 
		 * //create node index for edge created this.nodeIndex.put(edge, new
		 * ArrayList<Node>());
		 * 
		 * //add to and from nodes to edge index as created
		 * this.nodeIndex.get(edge).add(edge.fromNode);
		 * this.nodeIndex.get(edge).add(edge.toNode);
		 * 
		 * //add edge to node index as created
		 * this.edgeIndex.get(edge.fromNode).add(edge);
		 * this.edgeIndex.get(edge.toNode).add(edge);
		 * 
		 * //add node to adjIndex
		 * this.adjIndex.get(edge.fromNode).add(edge.toNode);
		 * 
		 * //generate the second added edge edge = new Edge(); edge.ID =
		 * edgeIDCounter++; edge.fromNode = this.nodes.get(nodeIndex[1]);
		 * edge.toNode = node; edges.add(edge);
		 * 
		 * //create node index for edge created this.nodeIndex.put(edge, new
		 * ArrayList<Node>());
		 * 
		 * //add to and from nodes to edge index as created
		 * this.nodeIndex.get(edge).add(edge.fromNode);
		 * this.nodeIndex.get(edge).add(edge.toNode);
		 * 
		 * //add edge to node index as created
		 * this.edgeIndex.get(edge.fromNode).add(edge);
		 * this.edgeIndex.get(edge.toNode).add(edge);
		 * 
		 * //add node to adjIndex
		 * this.adjIndex.get(edge.fromNode).add(edge.toNode); } }
		 */

//		this.printAPIs(20);
//		this.printAPIs(21);
//		this.printAPIs(22);
//		this.printAPIs(23);
//
//		this.printAdjacentNodes(20);
//		this.printAdjacentNodes(21);
//		this.printAdjacentNodes(22);
//		this.printAdjacentNodes(23);
	}

	public Node getRandomNeighbourWithinKSteps(Node node, int K) { // get random
																	// neighbour
																	// AT k
																	// steps
		Node previousNode = node;
		;
		Node currentNode = node;

		Random r = new Random();

		if (this.invertedIndex.get(node.kw).size() == 0) {
			StdOut.println("Exception occurred in getRandomNeighbourWithinKSteps! The given node is not in the graph!");
			return null;
		} else {

			int k;
			int loopCounter = 0;
			// do {
			++loopCounter;
			if (loopCounter > 20) {
				System.exit(-1);
			}
			k = r.nextInt(K) + 1;
			// k = K;//r.nextInt(K)+1;
			for (int i = 0; i < k; ++i) {
				ArrayList<Node> neighbours = this.adjIndex.get(currentNode); // get
																				// the
																				// neighbours
																				// of
																				// the
																				// current
																				// node
				int size = neighbours.size(); // get the number of neighbours of
												// the current node
				if (size == 0) {
					StdOut.println("This node does not have a neighbour!");
				} else {
					int t = r.nextInt(size);
					StdOut.println("There is a number of " + this.adjIndex.get(currentNode).size()
							+ " adjacent nodes of node " + currentNode.ID + ".");
					Node neighbour = neighbours.get(t);
					if (this.adjIndex.get(neighbour).size() != 0 && neighbour != previousNode) { // to
																									// ensure
																									// the
																									// selected
																									// neighbour
																									// has
																									// at
																									// least
																									// one
																									// neighbour
						previousNode = currentNode;
						currentNode = this.adjIndex.get(currentNode).get(t);
					}
				}
			}
			StdOut.println("Attempting to get random neighbourWithinKSteps ...");
			// } while(currentNode == node);
			return currentNode;
		}
	}

	public DataGraph answerNormal(Query q) {
		DataGraph answer = new DataGraph();
		;

		long startTimeNormal, endTimeNormal;
		try {
			// initialise matrice X and Y
			IloIntVar[] x = new IloIntVar[this.nodes.size()];
			IloIntVar[] y = new IloIntVar[this.edges.size()];

			IloCplex model = new IloCplex();

			// set the domain for x and y
			for (int i = 0; i < x.length; ++i) {
				x[i] = model.intVar(0, 1, "x" + i);
			}
			for (int i = 0; i < y.length; ++i) {
				y[i] = model.intVar(0, 1, "y" + i);
			}

			// constraints: exactly one node is selected to cover each keyword
			// in the query
			for (int i = 0; i < q.keywords.size(); ++i) {
				IloLinearNumExpr expr = model.linearNumExpr();
				ArrayList<Node> al = invertedIndex.get(q.keywords.get(i));
				for (int j = 0; j < al.size(); ++j) {
					expr.addTerm(1, x[al.get(j).ID]);
				}
				model.addEq(expr, 1, "G" + i);
			}

			/*
			 * node constraints: 1) if a node is selected, at least one edge
			 * connected to it is selected 2) if a node is not selected, none of
			 * the edge(s) connected to it are selected
			 */
			for (int i = 0; i < nodes.size(); ++i) {
				IloLinearNumExpr expr = model.linearNumExpr();
				ArrayList<Edge> al = this.edgeIndex.get(nodes.get(i));
				if (al == null) {
					StdOut.println("!!!");
				}
				; // Debugging code
				for (int j = 0; j < al.size(); ++j) {
					expr.addTerm(1, y[al.get(j).ID]);
				}
				model.addGe(expr, x[i], "N" + nodes.get(i).ID);
			}

			// edge constraint: if an edge is selected, both nodes connected to
			// it must be selected
			/*
			 * solution 1: create two constraints for two nodes connected to one
			 * edge
			 */
			/*
			 * for(int i=0; i<edges.size(); ++i) { ArrayList<Node> al =
			 * this.nodeIndex.get(edges.get(i)); for(int j=0; j<al.size(); ++j)
			 * { IloLinearNumExpr expr = model.linearNumExpr(); expr.addTerm(1,
			 * x[al.get(j).ID]); model.addGe(expr, y[i],
			 * ("E"+edges.get(i).ID)+"N"+al.get(j).ID); } }
			 */
			/*
			 * solution 2: create one constraints for two nodes connected to one
			 * edge
			 */
			for (int i = 0; i < edges.size(); ++i) {
				ArrayList<Node> al = this.nodeIndex.get(edges.get(i));
				IloLinearNumExpr expr = model.linearNumExpr();
				for (int j = 0; j < al.size(); ++j) {
					expr.addTerm(1, x[al.get(j).ID]);
				}
				model.addGe(expr, model.prod(2, y[i]), ("E" + edges.get(i).ID));
			}

			// connectedness constraint
			IloLinearNumExpr expr = model.linearNumExpr();
			for (int i = 0; i < nodes.size(); ++i) {
				expr.addTerm(-1, x[i]);
			}
			for (int i = 0; i < edges.size(); ++i) {
				expr.addTerm(1, y[i]);
			}
			model.addGe(expr, -1, "C");

			// the total number of nodes selected must not be more than 2 times
			// the number of keywords in the query
			expr = model.linearNumExpr();
			for (int i = 0; i < nodes.size(); ++i) {
				expr.addTerm(1, x[i]);
			}
			int nk = this.setting.numKeywords;
			StdOut.println(nk);
			int kd = this.setting.keywordDistance;
			StdOut.println(kd);
			model.addLe(expr, nk * kd * 2);

			// objective function: minimise the number of services in solution
			IloLinearNumExpr obj = model.linearNumExpr();
			for (int i = 0; i < nodes.size(); ++i) {
				obj.addTerm(1, x[i]);
			}
			model.add(model.minimize(obj));

			startTimeNormal = System.nanoTime();
			if (model.solve()) {
				q.isSuccessfulNormal = true;
				endTimeNormal = System.nanoTime();
				q.timeConsumptionSuccessfulKS3Normal = (endTimeNormal - startTimeNormal) / 1000000;
				q.objectiveValueKS3Normal = (int) model.getObjValue();

				// calculate whether the individualNormal method would succeed
				if (q.objectiveValueKS3Normal != this.setting.numKeywords) {
					q.isSuccessfulIndividualNormal = false;
				} else {
					q.isSuccessfulIndividualNormal = true;
				}

				System.out.println("Normal search solution status = " + model.getStatus());
				StdOut.print("Objective value: " + model.getObjValue());

				// calculate the number of nodes selected
				int xcount = 0;
				ArrayList<Node> xal = new ArrayList<Node>();
				for (int i = 0; i < this.nodes.size(); ++i) {
					if ((int) (model.getValue(x[i])) == 1) {
						++xcount;
						xal.add(nodes.get(i));
						answer.nodes.add(nodes.get(i)); // add selected nodes to
														// answer
					}
				}
				StdOut.println("\nNumber of nodes selected: " + xcount);
				StdOut.print("Selected nodes:");
				for (int i = 0; i < xal.size(); ++i) {
					StdOut.print(xal.get(i).ID + ", ");
				}
				StdOut.println();

				// calculate the number of edges selected
				int ycount = 0;
				ArrayList<Edge> yal = new ArrayList<Edge>();
				for (int i = 0; i < this.edges.size(); ++i) {
					if ((int) (model.getValue(y[i])) == 1) {
						++ycount;
						yal.add(edges.get(i));
						answer.edges.add(edges.get(i)); // //add selected edges
														// to answer
					}
				}
				StdOut.println("\nNumber of edges selected: " + ycount);
				StdOut.print("Selected edges:");
				for (int i = 0; i < yal.size(); ++i) {
					StdOut.print(yal.get(i).ID + "-" + "FromNode/" + yal.get(i).fromNode.ID + ":" + yal.get(i).fromNode.kw +">" + "ToNode/"
							+ yal.get(i).toNode.ID + ":" + yal.get(i).toNode.kw + ", ");
				}
				StdOut.println();

				model.exportModel("light_result.lp");
			} else {
				q.isSuccessfulNormal = false;
				endTimeNormal = System.nanoTime();
				q.timeConsumptionFailedKS3Normal = (endTimeNormal - startTimeNormal) / 1000000;

				this.numberOfServicesLight = -1.0;
				System.out.println("Normal search solution status = " + model.getStatus());
				model.exportModel("Normal_result.lp");
			}

		} catch (IloException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (q.isSuccessfulNormal == true) {
			return answer;
		} else {
			return null;
		}
	}

	public DataGraph answerConstraint(Query q) {
		DataGraph answer = new DataGraph();

		IloLinearNumExpr expr;

		long startTimeConstraint, endTimeConstraint;
		try {
			// initialise matrice X and Y
			IloIntVar[] x = new IloIntVar[this.nodes.size()];
			IloIntVar[] y = new IloIntVar[this.edges.size()];

			IloCplex model = new IloCplex();

			// set the domain for x and y
			for (int i = 0; i < x.length; ++i) {
				x[i] = model.intVar(0, 1, "x" + i);
			}
			for (int i = 0; i < y.length; ++i) {
				y[i] = model.intVar(0, 1, "y" + i);
			}

			// constraints: exactly one node is selected to cover each keyword
			// in the query
			for (int i = 0; i < q.keywords.size(); ++i) {
				expr = model.linearNumExpr();
				ArrayList<Node> al = invertedIndex.get(q.keywords.get(i));
				for (int j = 0; j < al.size(); ++j) {
					expr.addTerm(1, x[al.get(j).ID]);
				}
				model.addEq(expr, 1, "G" + i);
			}

			/*
			 * node constraints: 1) if a node is selected, at least one edge
			 * connected to it is selected 2) if a node is not selected, none of
			 * the edge(s) connected to it are selected
			 */
			for (int i = 0; i < nodes.size(); ++i) {
				expr = model.linearNumExpr();
				ArrayList<Edge> al = this.edgeIndex.get(nodes.get(i));
				if (al == null) {
					StdOut.println("!!!");
				}
				; // Debugging code
				for (int j = 0; j < al.size(); ++j) {
					expr.addTerm(1, y[al.get(j).ID]);
				}
				model.addGe(expr, x[i], "N" + nodes.get(i).ID);
			}

			// edge constraint: if an edge is selected, both nodes connected to
			// it must be selected
			/*
			 * solution 1: create two constraints for two nodes connected to one
			 * edge
			 */
			/*
			 * for(int i=0; i<edges.size(); ++i) { ArrayList<Node> al =
			 * this.nodeIndex.get(edges.get(i)); for(int j=0; j<al.size(); ++j)
			 * { IloLinearNumExpr expr = model.linearNumExpr(); expr.addTerm(1,
			 * x[al.get(j).ID]); model.addGe(expr, y[i],
			 * ("E"+edges.get(i).ID)+"N"+al.get(j).ID); } }
			 */
			/*
			 * solution 2: create one constraints for two nodes connected to one
			 * edge
			 */
			for (int i = 0; i < edges.size(); ++i) {
				ArrayList<Node> al = this.nodeIndex.get(edges.get(i));
				expr = model.linearNumExpr();
				for (int j = 0; j < al.size(); ++j) {
					expr.addTerm(1, x[al.get(j).ID]);
				}
				model.addGe(expr, model.prod(2, y[i]), ("E" + edges.get(i).ID));
			}

			// connectedness constraint
			expr = model.linearNumExpr();
			for (int i = 0; i < nodes.size(); ++i) {
				expr.addTerm(-1, x[i]);
			}
			for (int i = 0; i < edges.size(); ++i) {
				expr.addTerm(1, y[i]);
			}
			model.addGe(expr, -1, "C");

			// add constraints: quality constraints
			for (int p = 0; p < this.setting.numQoS; ++p) {
				expr = model.linearNumExpr();
				for (int i = 0; i < this.nodes.size(); ++i) {
					expr.addTerm(this.nodes.get(i).QoS[p], x[i]);
				}
				model.addLe(expr, q.QoS[p], "Q" + p);
			}

			// the total number of nodes selected must not be more than 2 times
			// the number of keywords in the query
			expr = model.linearNumExpr();
			for (int i = 0; i < nodes.size(); ++i) {
				expr.addTerm(1, x[i]);
			}
			model.addLe(expr, (this.setting.numKeywords) * this.setting.keywordDistance * 2);

			// objective function: minimise the number of services in solution
			IloLinearNumExpr obj = model.linearNumExpr();
			for (int i = 0; i < nodes.size(); ++i) {
				obj.addTerm(1, x[i]);
			}
			model.add(model.minimize(obj));

			startTimeConstraint = System.nanoTime();
			if (model.solve()) {
				q.isSuccessfulConstraint = true;
				endTimeConstraint = System.nanoTime();
				q.timeConsumptionSuccessfulKS3Constraint = (endTimeConstraint - startTimeConstraint) / 1000000;
				q.objectiveValueKS3Constraint = (int) model.getObjValue();

				// calculate whether the individualConstraint method would
				// succeed
				if (q.objectiveValueKS3Constraint != this.setting.numKeywords) {
					q.isSuccessfulIndividualConstraint = false;
				} else {
					q.isSuccessfulIndividualConstraint = true;
				}

				System.out.println("Constraint search solution status = " + model.getStatus());
				StdOut.print("Objective value: " + model.getObjValue());

				// calculate the number of nodes selected
				int xcount = 0;
				ArrayList<Node> xal = new ArrayList<Node>();
				for (int i = 0; i < this.nodes.size(); ++i) {
					if ((int) (model.getValue(x[i])) == 1) {
						++xcount;
						xal.add(nodes.get(i));
						answer.nodes.add(nodes.get(i)); // add selected nodes to
														// answer
					}
				}
				StdOut.println("\nNumber of nodes selected: " + xcount);
				StdOut.print("Selected nodes:");
				for (int i = 0; i < xal.size(); ++i) {
					StdOut.print(xal.get(i).ID + ", ");
				}
				StdOut.println();

				// calculate the number of edges selected
				int ycount = 0;
				ArrayList<Edge> yal = new ArrayList<Edge>();
				for (int i = 0; i < this.edges.size(); ++i) {
					if ((int) (model.getValue(y[i])) == 1) {
						++ycount;
						yal.add(edges.get(i));
						answer.edges.add(edges.get(i)); // //add selected edges
														// to answer
					}
				}
				StdOut.println("\nNumber of edges selected: " + ycount);
				StdOut.print("Selected edges:");
				for (int i = 0; i < yal.size(); ++i) {
					StdOut.print(yal.get(i).ID + "-" + "FromNode/" + yal.get(i).fromNode.ID + ":" + yal.get(i).fromNode.kw +">" + "ToNode/"
							+ yal.get(i).toNode.ID + ":" + yal.get(i).toNode.kw + ", ");
				}
				StdOut.println();

				model.exportModel("constraint_result.lp");
			} else {
				q.isSuccessfulConstraint = false;
				endTimeConstraint = System.nanoTime();
				q.timeConsumptionFailedKS3Constraint = (endTimeConstraint - startTimeConstraint) / 1000000;

				this.numberOfServicesLight = -1.0;
				System.out.println("Constraint search solution status = " + model.getStatus());
				model.exportModel("Constraint_result.lp");
			}

		} catch (IloException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (q.isSuccessfulConstraint == true) {
			return answer;
		} else {
			return null;
		}
	}

	public DataGraph answerOptimal(Query q) {
		DataGraph answer = new DataGraph();

		IloLinearNumExpr expr;

		long startTimeOptimal, endTimeOptimal;
		try {
			// initialise matrice X and Y
			IloIntVar[] x = new IloIntVar[this.nodes.size()];
			IloIntVar[] y = new IloIntVar[this.edges.size()];

			IloCplex model = new IloCplex();

			// set the domain for x and y
			for (int i = 0; i < x.length; ++i) {
				x[i] = model.intVar(0, 1, "x" + i);
			}
			for (int i = 0; i < y.length; ++i) {
				y[i] = model.intVar(0, 1, "y" + i);
			}

			// constraints: exactly one node is selected to cover each keyword
			// in the query
			for (int i = 0; i < q.keywords.size(); ++i) {
				expr = model.linearNumExpr();
				ArrayList<Node> al = invertedIndex.get(q.keywords.get(i));
				for (int j = 0; j < al.size(); ++j) {
					expr.addTerm(1, x[al.get(j).ID]);
				}
				model.addEq(expr, 1, "G" + i);
			}

			/*
			 * node constraints: 1) if a node is selected, at least one edge
			 * connected to it is selected 2) if a node is not selected, none of
			 * the edge(s) connected to it are selected
			 */
			for (int i = 0; i < nodes.size(); ++i) {
				expr = model.linearNumExpr();
				ArrayList<Edge> al = this.edgeIndex.get(nodes.get(i));
				if (al == null) {
					StdOut.println("!!!");
				}
				; // Debugging code
				for (int j = 0; j < al.size(); ++j) {
					expr.addTerm(1, y[al.get(j).ID]);
				}
				model.addGe(expr, x[i], "N" + nodes.get(i).ID);
			}

			// edge constraint: if an edge is selected, both nodes connected to
			// it must be selected
			/*
			 * solution 1: create two constraints for two nodes connected to one
			 * edge
			 */
			/*
			 * for(int i=0; i<edges.size(); ++i) { ArrayList<Node> al =
			 * this.nodeIndex.get(edges.get(i)); for(int j=0; j<al.size(); ++j)
			 * { IloLinearNumExpr expr = model.linearNumExpr(); expr.addTerm(1,
			 * x[al.get(j).ID]); model.addGe(expr, y[i],
			 * ("E"+edges.get(i).ID)+"N"+al.get(j).ID); } }
			 */
			/*
			 * solution 2: create one constraints for two nodes connected to one
			 * edge
			 */
			for (int i = 0; i < edges.size(); ++i) {
				ArrayList<Node> al = this.nodeIndex.get(edges.get(i));
				expr = model.linearNumExpr();
				for (int j = 0; j < al.size(); ++j) {
					expr.addTerm(1, x[al.get(j).ID]);
				}
				model.addGe(expr, model.prod(2, y[i]), ("E" + edges.get(i).ID));
			}

			// connectedness constraint
			expr = model.linearNumExpr();
			for (int i = 0; i < nodes.size(); ++i) {
				expr.addTerm(-1, x[i]);
			}
			for (int i = 0; i < edges.size(); ++i) {
				expr.addTerm(1, y[i]);
			}
			model.addGe(expr, -1, "C");

			// add constraints: quality constraints
			for (int p = 0; p < this.setting.numQoS; ++p) {
				expr = model.linearNumExpr();
				for (int i = 0; i < this.nodes.size(); ++i) {
					expr.addTerm(this.nodes.get(i).QoS[p], x[i]);
				}
				model.addLe(expr, q.QoS[p], "Q" + p);
			}

			// the total number of nodes selected must not be more than 2 times
			// the number of keywords in the query
			expr = model.linearNumExpr();
			for (int i = 0; i < nodes.size(); ++i) {
				expr.addTerm(1, x[i]);
			}
			model.addLe(expr, (this.setting.numKeywords) * this.setting.keywordDistance * 2);

			// objective function: minimise the first qos dimension
			IloLinearNumExpr obj = model.linearNumExpr();
			for (int i = 0; i < nodes.size(); ++i) {
				obj.addTerm(this.nodes.get(i).QoS[0], x[i]);
			}
			model.add(model.minimize(obj));

			startTimeOptimal = System.nanoTime();
			if (model.solve()) {
				q.isSuccessfulOptimal = true;
				endTimeOptimal = System.nanoTime();
				q.timeConsumptionSuccessfulKS3Optimal = (endTimeOptimal - startTimeOptimal) / 1000000;
				q.objectiveValueKS3Optimal = model.getObjValue();

				System.out.println("Optimal search solution status = " + model.getStatus());
				StdOut.print("Objective value: " + model.getObjValue());

				// calculate the number of nodes selected
				int xcount = 0;
				ArrayList<Node> xal = new ArrayList<Node>();
				for (int i = 0; i < this.nodes.size(); ++i) {
					if ((int) (model.getValue(x[i])) == 1) {
						++xcount;
						xal.add(nodes.get(i));
						answer.nodes.add(nodes.get(i)); // add selected nodes to
														// answer
					}
				}
				StdOut.println("\nNumber of nodes selected: " + xcount);
				StdOut.print("Selected nodes:");
				for (int i = 0; i < xal.size(); ++i) {
					StdOut.print(xal.get(i).ID + ", ");
				}
				StdOut.println();

				// calculate whether the individual method would succeed
				if (xcount != this.setting.numKeywords) {
					q.isSuccessfulIndividualOptimal = false;
				} else {
					q.isSuccessfulIndividualOptimal = true;
				}

				// calculate the number of edges selected
				int ycount = 0;
				ArrayList<Edge> yal = new ArrayList<Edge>();
				for (int i = 0; i < this.edges.size(); ++i) {
					if ((int) (model.getValue(y[i])) == 1) {
						++ycount;
						yal.add(edges.get(i));
						answer.edges.add(edges.get(i)); // //add selected edges
														// to answer
					}
				}
				StdOut.println("\nNumber of edges selected: " + ycount);
				StdOut.print("Selected edges:");
				for (int i = 0; i < yal.size(); ++i) {
					StdOut.print(yal.get(i).ID + "-" + "FromNode/" + yal.get(i).fromNode.ID + ":" + yal.get(i).fromNode.kw +">" + "ToNode/"
							+ yal.get(i).toNode.ID + ":" + yal.get(i).toNode.kw + ", ");
				}
				StdOut.println();

				model.exportModel("optimal_result.lp");
			} else {
				q.isSuccessfulOptimal = false;
				endTimeOptimal = System.nanoTime();
				q.timeConsumptionFailedKS3Optimal = (endTimeOptimal - startTimeOptimal) / 1000000;

				this.numberOfServicesLight = -1.0;
				System.out.println("Optimal search solution status = " + model.getStatus());
				model.exportModel("optimal_result.lp");
			}

		} catch (IloException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (q.isSuccessfulOptimal == true) {
			return answer;
		} else {
			return null;
		}
	}

	public DataGraph answerNormalPW(QueryPW q) {
		DataGraph answer = new DataGraph();
		;

		long startTimeNormal, endTimeNormal;
		try {
			// initialise matrice X and Y
			IloIntVar[] x = new IloIntVar[this.nodes.size()];
			IloIntVar[] y = new IloIntVar[this.edges.size()];

			IloCplex model = new IloCplex();

			// set the domain for x and y
			for (int i = 0; i < x.length; ++i) {
				x[i] = model.intVar(0, 1, "x" + i);
			}
			for (int i = 0; i < y.length; ++i) {
				y[i] = model.intVar(0, 1, "y" + i);
			}

			// constraints: exactly one node is selected to cover each keyword
			// in the query
			for (int i = 0; i < q.keywords.size(); ++i) {
				IloLinearNumExpr expr = model.linearNumExpr();
				ArrayList<Node> al = invertedIndex.get(q.keywords.get(i));
				for (int j = 0; j < al.size(); ++j) {
					expr.addTerm(1, x[al.get(j).ID]);
				}
				model.addEq(expr, 1, "G" + i);
			}

			/*
			 * node constraints: 1) if a node is selected, at least one edge
			 * connected to it is selected 2) if a node is not selected, none of
			 * the edge(s) connected to it are selected
			 */
			for (int i = 0; i < nodes.size(); ++i) {
				IloLinearNumExpr expr = model.linearNumExpr();
				ArrayList<Edge> al = this.edgeIndex.get(nodes.get(i));
				if (al == null) {
					StdOut.println("!!!");
				}
				; // Debugging code
				for (int j = 0; j < al.size(); ++j) {
					expr.addTerm(1, y[al.get(j).ID]);
				}
				model.addGe(expr, x[i], "N" + nodes.get(i).ID);
			}

			// edge constraint: if an edge is selected, both nodes connected to
			// it must be selected
			/*
			 * solution 1: create two constraints for two nodes connected to one
			 * edge
			 */
			/*
			 * for(int i=0; i<edges.size(); ++i) { ArrayList<Node> al =
			 * this.nodeIndex.get(edges.get(i)); for(int j=0; j<al.size(); ++j)
			 * { IloLinearNumExpr expr = model.linearNumExpr(); expr.addTerm(1,
			 * x[al.get(j).ID]); model.addGe(expr, y[i],
			 * ("E"+edges.get(i).ID)+"N"+al.get(j).ID); } }
			 */
			/*
			 * solution 2: create one constraints for two nodes connected to one
			 * edge
			 */
			for (int i = 0; i < edges.size(); ++i) {
				ArrayList<Node> al = this.nodeIndex.get(edges.get(i));
				IloLinearNumExpr expr = model.linearNumExpr();
				for (int j = 0; j < al.size(); ++j) {
					expr.addTerm(1, x[al.get(j).ID]);
				}
				model.addGe(expr, model.prod(2, y[i]), ("E" + edges.get(i).ID));
			}

			// connectedness constraint
			IloLinearNumExpr expr = model.linearNumExpr();
			for (int i = 0; i < nodes.size(); ++i) {
				expr.addTerm(-1, x[i]);
			}
			for (int i = 0; i < edges.size(); ++i) {
				expr.addTerm(1, y[i]);
			}
			model.addGe(expr, -1, "C");

			// the total number of nodes selected must not be more than 2 times
			// the number of keywords in the query
			// this constraint does not apply to experiments with PW dataset
			/*
			 * expr = model.linearNumExpr(); for(int i=0; i<nodes.size(); ++i) {
			 * expr.addTerm(1, x[i]); } int nk = this.setting.numKeywords; int
			 * kd = this.setting.keywordDistance; model.addLe(expr, nk*kd*2);
			 */

			// objective function: minimise the number of services in solution
			IloLinearNumExpr obj = model.linearNumExpr();
			for (int i = 0; i < nodes.size(); ++i) {
				obj.addTerm(1, x[i]);
			}
			model.add(model.minimize(obj));

			startTimeNormal = System.nanoTime();
			if (model.solve()) {
				q.isSuccessfulNormal = true;
				endTimeNormal = System.nanoTime();
				q.timeConsumptionSuccessfulKS3Normal = (endTimeNormal - startTimeNormal) / 1000000;
				q.objectiveValueKS3Normal = (int) model.getObjValue();

				// calculate whether the individualNormal method would succeed
				if (q.objectiveValueKS3Normal != this.setting.numKeywords) {
					q.isSuccessfulIndividualNormal = false;
				} else {
					q.isSuccessfulIndividualNormal = true;
				}

				System.out.println("Normal search solution status = " + model.getStatus());
				StdOut.print("Objective value: " + model.getObjValue());

				// calculate the number of nodes selected
				int xcount = 0;
				ArrayList<Node> xal = new ArrayList<Node>();
				for (int i = 0; i < this.nodes.size(); ++i) {
					if ((int) (model.getValue(x[i])) == 1) {
						++xcount;
						xal.add(nodes.get(i));
						answer.nodes.add(nodes.get(i)); // add selected nodes to
														// answer
					}
				}
				StdOut.println("\nNumber of nodes selected: " + xcount);
				StdOut.print("Selected nodes:");
				for (int i = 0; i < xal.size(); ++i) {
					StdOut.print(xal.get(i).ID + ", ");
				}
				StdOut.println();

				// determine whether the found solution is a new mashup
				// different from the programmableweb mashup
				if (xal.size() != q.dg.hm_mashups_apis.get(q.mashupID).size()) {
					q.newSolution = true;
				} else {
					q.newSolution = false;
				}

				// calculate the number of edges selected
				int ycount = 0;
				ArrayList<Edge> yal = new ArrayList<Edge>();
				for (int i = 0; i < this.edges.size(); ++i) {
					if ((int) (model.getValue(y[i])) == 1) {
						++ycount;
						yal.add(edges.get(i));
						answer.edges.add(edges.get(i)); // //add selected edges
														// to answer
					}
				}
				StdOut.println("\nNumber of edges selected: " + ycount);
				StdOut.print("Selected edges:");
				for (int i = 0; i < yal.size(); ++i) {
					StdOut.print(yal.get(i).ID + "-" + "FromNode/" + yal.get(i).fromNode.ID + ":" + yal.get(i).fromNode.kw +">" + "ToNode/"
							+ yal.get(i).toNode.ID + ":" + yal.get(i).toNode.kw + ", ");
				}
				StdOut.println();

				model.exportModel("light_result.lp");
			} else {
				q.isSuccessfulNormal = false;
				endTimeNormal = System.nanoTime();
				q.timeConsumptionFailedKS3Normal = (endTimeNormal - startTimeNormal) / 1000000;

				this.numberOfServicesLight = -1.0;
				System.out.println("Normal search solution status = " + model.getStatus());
				model.exportModel("Normal_result.lp");
			}
		} catch (IloException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (q.isSuccessfulNormal == true) {
			return answer;
		} else {
			return null;
		}
	}

	public DataGraph answerConstraintPW(QueryPW q) {
		StdOut.println("This answerConstraintPW method is under construction.");
		return null;
	}

	public DataGraph answerOptimalPW(QueryPW q) {
		StdOut.println("This answerOptimalPW method is under construction.");
		return null;
	}

	// output the keyword of each node
	public void printKeywords() {
		StringBuilder s = new StringBuilder();
		s.append(nodes.size() + " vertices, " + edges.size() + " edges " + NEWLINE);
		for (int v = 0; v < nodes.size(); v++) {
			s.append(String.format("%d: ", v));
			s.append(nodes.get(v).kw);
			s.append(NEWLINE);
		}
		System.out.print(s);
	}

	// output the inverted index
	public void printInvertedIndex() {
		StdOut.println();
		StdOut.println("Printing Inverted Index:");
		Iterator it = this.invertedIndex.entrySet().iterator();
		ArrayList<Node> al;
		Node node;
		while (it.hasNext()) {
			Map.Entry<String, ArrayList<Node>> pair = (Map.Entry<String, ArrayList<Node>>) it.next();
			StdOut.print(pair.getKey() + ": ");
			al = pair.getValue();
			for (int i = 0; i < al.size(); ++i) {
				node = (Node) al.get(i);
				StdOut.print(node.ID + ", ");
			}
			StdOut.println();
		}
	}

	// output the node index
	public void printNodeIndex() {
		StdOut.println();
		StdOut.println("Printing Node Index:");
		Iterator it = this.nodeIndex.entrySet().iterator();
		ArrayList<Node> al;
		Node node;
		while (it.hasNext()) {
			Map.Entry<Edge, ArrayList<Node>> pair = (Map.Entry<Edge, ArrayList<Node>>) it.next();
			StdOut.print("Edge " + pair.getKey().ID + ": ");
			al = pair.getValue();
			for (int i = 0; i < al.size(); ++i) {
				node = (Node) al.get(i);
				StdOut.print(node.ID + ", ");
			}
			StdOut.println();
		}
	}

	// output the edge index
	public void printEdgeIndex() {
		StdOut.println();
		StdOut.println("Printing Edge Index:");
		Iterator it = this.edgeIndex.entrySet().iterator();
		ArrayList<Edge> al;
		Edge edge;
		while (it.hasNext()) {
			Map.Entry<Node, ArrayList<Edge>> pair = (Map.Entry<Node, ArrayList<Edge>>) it.next();
			StdOut.print("Node " + pair.getKey().ID + ": ");
			al = pair.getValue();
			for (int i = 0; i < al.size(); ++i) {
				edge = (Edge) al.get(i);
				StdOut.print(edge.ID + ", ");
			}
			StdOut.println();
		}
	}

	// output the adjacent nodes of each node
	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append(nodes.size() + " vertices, " + edges.size() + " edges " + NEWLINE);
		for (int v = 0; v < nodes.size(); v++) {
			s.append(String.format("%d: ", v));
			for (int i = 0; i < this.adjIndex.get(nodes.get(v)).size(); ++i) {
				s.append(String.format("%d ", this.adjIndex.get(nodes.get(v)).get(i).ID));
			}
			s.append(NEWLINE);
		}
		return s.toString();
	}

	// output the keyword contained by each node
	public void printNodeKeywords() {
		StdOut.println();
		StdOut.println("Printing Keywords of Nodes:");
		for (int i = 0; i < this.nodes.size(); ++i) {
			StdOut.println("Node " + this.nodes.get(i).ID + ": " + this.nodes.get(i).kw);
		}
	}

	public boolean allNodesHaveNeighbour() {
		Node node;
		for (int i = 0; i < this.nodes.size(); ++i) {
			node = this.nodes.get(i);
			if (this.adjIndex.get(node).size() == 0) {
				return false;
			}
		}
		return true;
	}

	public void printMashupsWithAPIs() {
		Set<HashMap.Entry<Integer, ArrayList<String>>> entrySet = hm_mashups_apis.entrySet();
		for (Entry<Integer, ArrayList<String>> entry : entrySet) {
			System.out.print("Mashup #" + entry.getKey() + ": ");
			for (int i = 0; i < entry.getValue().size(); ++i) {
				System.out.print(entry.getValue().get(i) + ", ");
			}
			System.out.println("");
		}
	}

	public void printMashupWithAPIs(Integer mashupID) {
		System.out.print("Mashup #" + mashupID + ": ");
		ArrayList<String> apis = this.hm_mashups_apis.get(mashupID);
		for (int i = 0; i < apis.size(); ++i) {
			System.out.print(apis.get(i) + ", ");
		}
		System.out.println("");
	}

	public void printAPIs(Integer sourceAPIID) { // given an API, print all APIs
													// that it links to
		if (this.API_API_Integers.get(sourceAPIID) == null) {
			StdOut.println("API #" + sourceAPIID + " does not exist!");
		} else {
			System.out.print("API #" + sourceAPIID + ": ");
			for (int i = 0; i < this.API_API_Integers.get(sourceAPIID).size(); ++i) {
				System.out.print(this.API_API_Integers.get(sourceAPIID).get(i));
				if (i != this.API_API_Integers.get(sourceAPIID).size() - 1) {
					System.out.print(", ");
				}
			}
			System.out.println("");
		}
	}

	public void printAdjacentNodes(Integer nodeID) {
		Node node = this.nodes.get(nodeID);
		if (node == null) {
			StdOut.println("Node #" + nodeID + " does not exist!");
		} else {
			System.out.print("Node # " + nodeID + " links to: ");
			for (int i = 0; i < this.adjIndex.get(node).size(); ++i) {
				System.out.print(this.adjIndex.get(node).get(i).ID);
				if (i != this.adjIndex.get(node).size() - 1) {
					System.out.print(", ");
				}
			}
			System.out.println("");
		}
	}

	/**
	 * Unit tests the <tt>DataGraph</tt> data type.
	 */
	/*
	 * public static void main(String[] args) { Digraph dig =
	 * DigraphGenerator.defaultDigraph(10, 2, 2); DataGraph dg = new
	 * DataGraph(dig);
	 * 
	 * StdOut.println("Testing..."); StdOut.println(dg);
	 * 
	 * Random r = new Random(); int n = r.nextInt(dg.nodes.size()); Node
	 * currentNode = dg.nodes.get(n); Node neighbourNode =
	 * dg.getRandomNeighbourWithinKSteps(currentNode, 2);
	 * 
	 * StdOut.println("The current node is " + currentNode.ID);
	 * StdOut.println("Its selected neighbour node is " + neighbourNode.ID);
	 * 
	 * //SimulationProcess.displayGraph(dg); //dg.printInvertedIndex(); //
	 * //StdOut.println();
	 * 
	 * //dg.printKeywords(); }
	 */
}
