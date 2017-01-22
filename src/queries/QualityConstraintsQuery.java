package queries;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;

import other.*;
import elements.*;

public class QualityConstraintsQuery {
	public long timeConsumptionSuccessfulKS3Constraint;
	public int numberOfNodes;

	public boolean qualityConstraintsQuerySeriesB(ArrayList<String> keywords,
			HashMap<String, ArrayList<Node>> invertedIndexAPIName, HashMap<Node, ArrayList<Node>> adjIndex, int[] QoS,
			int testNumberOfNodes) {
		// System.out.println(keywords);
		// String prefix = "";
		// for (int i = 0; i < QoS.length; i++) {
		// System.out.print(prefix + " " + QoS[i]);
		// prefix = ",";
		// }
		// System.out.println();

		System.out.println("QUALITY CONSTRAINTS QUERY");

		Comparator<OptimalSteinerTree> comparator = new QualityQueueComparator();

		// The priority queue sorted in the increasing order of costs of trees
		PriorityQueue<OptimalSteinerTree> subTreeQueue = new PriorityQueue<OptimalSteinerTree>(keywords.size(),
				comparator);

		PriorityQueue<OptimalSteinerTree> resultQueue = new PriorityQueue<OptimalSteinerTree>(keywords.size(),
				comparator);

		HashMap<OptimalSteinerTree, HashMap<Integer, OptimalSteinerTree>> removedSubTreeQueue = new HashMap<OptimalSteinerTree, HashMap<Integer, OptimalSteinerTree>>();

		// Intermediate hash map to save the information of number of nodes in a
		// tree
		HashMap<OptimalSteinerTree, OptimalSteinerTree> numberOfNodesInfo = new HashMap<OptimalSteinerTree, OptimalSteinerTree>();

		HashMap<String, Integer> keywordsMapping = new HashMap<String, Integer>();

		HashMap<Node, ArrayList<OptimalSteinerTree>> invertedRootSteinerTree = new HashMap<Node, ArrayList<OptimalSteinerTree>>();

		long startTime = System.currentTimeMillis();

		// Put nodes containing keywords in the queue
		for (int i = 0; i < keywords.size(); i++) {
			if (invertedIndexAPIName.containsKey(keywords.get(i))) {
				ArrayList<Node> listOfKeywordNode = invertedIndexAPIName.get(keywords.get(i));
				for (int j = 0; j < listOfKeywordNode.size(); j++) {
					HashMap<Node, Node> keywordsInput = new HashMap<Node, Node>();
					keywordsInput.put(
							new Node(listOfKeywordNode.get(j).ID, listOfKeywordNode.get(j).kw,
									listOfKeywordNode.get(j).QoS),
							new Node(listOfKeywordNode.get(j).ID, listOfKeywordNode.get(j).kw,
									listOfKeywordNode.get(j).QoS));
					Node root = new Node(listOfKeywordNode.get(j).ID, listOfKeywordNode.get(j).kw,
							listOfKeywordNode.get(j).QoS);
					OptimalSteinerTree initialTrees = new OptimalSteinerTree(root, new HashMap<Node, Node>(),
							keywordsInput, 1, QoS.length);
					initialTrees.setQoS(listOfKeywordNode.get(j).QoS);
					int qualityCheck = 0;
					for (int check = 0; check < QoS.length; check++) {
						if (initialTrees.getQoS()[check] <= QoS[check])
							qualityCheck++;
					}
					if (qualityCheck == QoS.length) {
						subTreeQueue.add(initialTrees);
						numberOfNodesInfo.put(initialTrees, initialTrees);

						ArrayList<OptimalSteinerTree> steinerTrees = new ArrayList<OptimalSteinerTree>();
						steinerTrees.add(initialTrees);
						invertedRootSteinerTree.put(root, steinerTrees);
					}
				}

				keywordsMapping.put(keywords.get(i), 1);
			} else {
				System.out.println("The graph does not contain the keyword " + keywords.get(i));
				return false;
			}
		}

		while (subTreeQueue.size() != 0) {

			OptimalSteinerTree firstOrder = subTreeQueue.remove();
			OptimalSteinerTree testRemove = numberOfNodesInfo.remove(firstOrder);

			if (removedSubTreeQueue.containsKey(firstOrder)) {
				if (removedSubTreeQueue.get(firstOrder).size() == 0) {
					removedSubTreeQueue.remove(firstOrder);
				} else if (removedSubTreeQueue.get(firstOrder).containsKey(firstOrder.getNumberOfNodes())) {
					removedSubTreeQueue.get(firstOrder).remove(firstOrder.getNumberOfNodes());
					continue;
				}
			}

			// if (resultQueue.size() > 0) {
			// OptimalSteinerTree firstResult = resultQueue.peek();
			// if (firstOrder.getNumberOfNodes() >=
			// firstResult.getNumberOfNodes()) {
			// long endTime = System.currentTimeMillis();
			// System.out.println("The root of the result tree: " +
			// firstResult.getRoot().ID + "-"
			// + firstResult.getRoot().kw);
			// System.out.println("The nodes of the tree: ");
			// for (String n : firstResult.getNodes().keySet()) {
			// System.out.println(firstResult.getNodes().get(n).ID + "-" +
			// firstResult.getNodes().get(n).kw);
			// }
			// System.out.println("Number of nodes, quality constraints: " +
			// firstResult.getNumberOfNodes());
			// this.timeConsumptionSuccessfulKS3Constraint = endTime -
			// startTime;
			// this.numberOfNodes = firstResult.getNumberOfNodes();
			//
			// System.out.println("The total computation time: " + (endTime -
			// startTime));
			// if (this.numberOfNodes != testNumberOfNodes) {
			// System.out.println("One of the result is wrong");
			// }
			// String subprefix = "";
			// System.out.println("The final quality of the tree: ");
			// for (int i = 0; i < firstResult.getQoS().length; i++) {
			// System.out.print(subprefix + " " + firstResult.getQoS()[i]);
			// subprefix = ",";
			// }
			// System.out.println();
			// return true;
			// }
			// }

			// Check if the current tree includes all the keywords then return
			// the result
			if (firstOrder.minimumSteinerTree(keywords)) {
				long endTime = System.currentTimeMillis();
				System.out.println(
						"The root of the result tree: " + firstOrder.getRoot().ID + "-" + firstOrder.getRoot().kw);
				System.out.println("The nodes of the tree: ");
				for (Node n : firstOrder.getNodes().keySet()) {
					System.out.println(firstOrder.getNodes().get(n).ID + "-" + firstOrder.getNodes().get(n).kw);
				}
				this.timeConsumptionSuccessfulKS3Constraint = endTime - startTime;
				this.numberOfNodes = firstOrder.getNumberOfNodes();
				System.out.println("The total computation time: " + (endTime - startTime));
				// if (this.numberOfNodes != testNumberOfNodes) {
				// System.out.println("Test");
				// }
				return true;
			}

			ArrayList<Node> adjacentNodes = adjIndex.get(firstOrder.getRoot());

			// Grow tree
			for (int i = 0; i < adjacentNodes.size(); i++) {
				HashMap<Node, Node> copyNodes = new HashMap<Node, Node>();
				copyNodes.putAll(firstOrder.getNodes());
				HashMap<Node, Node> copyKeywords = new HashMap<Node, Node>();
				copyKeywords.putAll(firstOrder.getKeywords());

				OptimalSteinerTree intermediateTree = new OptimalSteinerTree(
						new Node(firstOrder.getRoot().ID, firstOrder.getRoot().kw, firstOrder.getRoot().QoS), copyNodes,
						copyKeywords, firstOrder.getNumberOfNodes(), firstOrder.getQoS());

				if (intermediateTree.growTree(adjacentNodes.get(i), keywordsMapping)) {
					if ((intermediateTree.getNumberOfNodes()
							+ (keywords.size() - intermediateTree.getKeywords().size())) <= 2 * keywords.size()) {
						intermediateTree.setQoS(adjacentNodes.get(i).QoS);
						// Check quality constraints
						int qualityCheck = 0;
						for (int check = 0; check < QoS.length; check++) {
							if (intermediateTree.getQoS()[check] <= QoS[check])
								qualityCheck++;
						}
						if (qualityCheck == QoS.length) {
							if (numberOfNodesInfo.containsKey(intermediateTree)) {
								OptimalSteinerTree test = new OptimalSteinerTree(
										new Node(numberOfNodesInfo.get(intermediateTree).getRoot().ID,
												numberOfNodesInfo.get(intermediateTree).getRoot().kw,
												numberOfNodesInfo.get(intermediateTree).getRoot().QoS),
										numberOfNodesInfo.get(intermediateTree).getNodes(),
										numberOfNodesInfo.get(intermediateTree).getKeywords(),
										numberOfNodesInfo.get(intermediateTree).getNumberOfNodes(),
										numberOfNodesInfo.get(intermediateTree).getQoS());

								if (intermediateTree.getNumberOfNodes() <= test.getNumberOfNodes()) {
									int secondQualityCheck = 0;
									for (int check = 0; check < QoS.length; check++) {
										if (intermediateTree.getQoS()[check] <= test.getQoS()[check])
											secondQualityCheck++;
									}
									if (secondQualityCheck == QoS.length) {
										if (removedSubTreeQueue.containsKey(test)) {
											removedSubTreeQueue.get(test).put(test.getNumberOfNodes(), test);
										} else {
											HashMap<Integer, OptimalSteinerTree> numberOfNodesMap = new HashMap<Integer, OptimalSteinerTree>();
											numberOfNodesMap.put(test.getNumberOfNodes(), test);
											removedSubTreeQueue.put(test, numberOfNodesMap);
										}
										// subTreeQueue.remove(test);
										subTreeQueue.add(intermediateTree);

										numberOfNodesInfo.put(intermediateTree, intermediateTree);

										if (invertedRootSteinerTree.containsKey(adjacentNodes.get(i))) {
											invertedRootSteinerTree.get(adjacentNodes.get(i)).remove(test);
											invertedRootSteinerTree.get(adjacentNodes.get(i)).add(intermediateTree);
										} else {
											ArrayList<OptimalSteinerTree> steinerTrees = new ArrayList<OptimalSteinerTree>();
											steinerTrees.add(intermediateTree);
											invertedRootSteinerTree.put(adjacentNodes.get(i), steinerTrees);
										}
									}
								}
							} else {
								subTreeQueue.add(intermediateTree);
								numberOfNodesInfo.put(intermediateTree, intermediateTree);

								if (invertedRootSteinerTree.containsKey(adjacentNodes.get(i))) {
									invertedRootSteinerTree.get(adjacentNodes.get(i)).add(intermediateTree);
								} else {
									ArrayList<OptimalSteinerTree> steinerTrees = new ArrayList<OptimalSteinerTree>();
									steinerTrees.add(intermediateTree);
									invertedRootSteinerTree.put(adjacentNodes.get(i), steinerTrees);
								}
							}
							if (intermediateTree.minimumSteinerTree(keywords)) {
								resultQueue.add(intermediateTree);
							}
						}
					}
				}
			}

			// Merge Tree
			if (invertedRootSteinerTree.containsKey(firstOrder.getRoot())) {
				ArrayList<OptimalSteinerTree> test = new ArrayList<OptimalSteinerTree>();
				test = invertedRootSteinerTree.get(firstOrder.getRoot());

				int testSize = test.size();

				for (int i = 0; i < testSize; i++) {
					if (test.get(i).equals(firstOrder)) {
						test.remove(i);
						testSize--;
						i--;
						continue;
					}
					if (test.get(i).getKeywordsString().equals(firstOrder.getKeywordsString())) {
						if (firstOrder.getNumberOfNodes() <= test.get(i).getNumberOfNodes()) {
							int check = 0;
							for (int k = 0; k < firstOrder.getQoS().length; k++) {
								if (firstOrder.getQoS()[k] <= test.get(i).getQoS()[k]) {
									check++;
								}
							}
							if (check == firstOrder.getQoS().length) {
								if (removedSubTreeQueue.containsKey(test.get(i))) {
									removedSubTreeQueue.get(test.get(i)).put(test.get(i).getNumberOfNodes(),
											test.get(i));
								} else {
									HashMap<Integer, OptimalSteinerTree> numberOfNodesMap = new HashMap<Integer, OptimalSteinerTree>();
									numberOfNodesMap.put(test.get(i).getNumberOfNodes(), test.get(i));
									removedSubTreeQueue.put(test.get(i), numberOfNodesMap);
								}

								numberOfNodesInfo.remove(test.get(i));

								test.remove(i);
								testSize--;
								i--;
							}
						}
						continue;
					}
					if (test.size() == 0) {
						invertedRootSteinerTree.remove(firstOrder.getRoot());
						break;
					}
					HashMap<Node, Node> copyNodes = new HashMap<Node, Node>();
					copyNodes.putAll(firstOrder.getNodes());
					HashMap<Node, Node> copyKeywords = new HashMap<Node, Node>();
					copyKeywords.putAll(firstOrder.getKeywords());

					OptimalSteinerTree intermediateTreeMerge = new OptimalSteinerTree(
							new Node(firstOrder.getRoot().ID, firstOrder.getRoot().kw, firstOrder.getRoot().QoS),
							copyNodes, copyKeywords, firstOrder.getNumberOfNodes(), firstOrder.getQoS());

					OptimalSteinerTree elementsInSameRootTreeQueue = test.get(i);

					HashMap<Node, Node> copyNodesForMergedTree = new HashMap<Node, Node>();
					copyNodesForMergedTree.putAll(elementsInSameRootTreeQueue.getNodes());
					HashMap<Node, Node> copyKeywordsForMergedTree = new HashMap<Node, Node>();
					copyKeywordsForMergedTree.putAll(elementsInSameRootTreeQueue.getKeywords());

					OptimalSteinerTree mergedTree = new OptimalSteinerTree(
							new Node(elementsInSameRootTreeQueue.getRoot().ID, elementsInSameRootTreeQueue.getRoot().kw,
									elementsInSameRootTreeQueue.getRoot().QoS),
							copyNodesForMergedTree, copyKeywordsForMergedTree,
							elementsInSameRootTreeQueue.getNumberOfNodes(), elementsInSameRootTreeQueue.getQoS());

					if (!intermediateTreeMerge.equals(mergedTree)) {
						if (intermediateTreeMerge.mergeTreeWithQualityConstraints(mergedTree)) {
							if ((intermediateTreeMerge.getNumberOfNodes()
									+ (keywords.size() - intermediateTreeMerge.getKeywords().size())) <= 2
											* keywords.size()) {
								// // Check quality constraints
								int qualityCheck = 0;
								for (int check = 0; check < QoS.length; check++) {
									if (intermediateTreeMerge.getQoS()[check] <= QoS[check])
										qualityCheck++;
								}
								if (qualityCheck == QoS.length) {
									if (numberOfNodesInfo.containsKey(intermediateTreeMerge)) {
										if (intermediateTreeMerge.getNumberOfNodes() <= numberOfNodesInfo
												.get(intermediateTreeMerge).getNumberOfNodes()) {

											int secondQualityCheck = 0;
											for (int check = 0; check < QoS.length; check++) {
												if (intermediateTreeMerge.getQoS()[check] <= numberOfNodesInfo
														.get(intermediateTreeMerge).getQoS()[check])
													secondQualityCheck++;
											}
											if (secondQualityCheck == QoS.length) {
												if (removedSubTreeQueue.containsKey(intermediateTreeMerge)) {
													removedSubTreeQueue.get(intermediateTreeMerge).put(
															intermediateTreeMerge.getNumberOfNodes(),
															intermediateTreeMerge);
												} else {
													HashMap<Integer, OptimalSteinerTree> numberOfNodesMap = new HashMap<Integer, OptimalSteinerTree>();
													numberOfNodesMap.put(intermediateTreeMerge.getNumberOfNodes(),
															intermediateTreeMerge);
													removedSubTreeQueue.put(intermediateTreeMerge, numberOfNodesMap);
												}

												// subTreeQueue.remove(intermediateTreeMerge);
												subTreeQueue.add(intermediateTreeMerge);

												numberOfNodesInfo.put(intermediateTreeMerge, intermediateTreeMerge);

												invertedRootSteinerTree.get(firstOrder.getRoot())
														.remove(numberOfNodesInfo.get(intermediateTreeMerge));

												invertedRootSteinerTree.get(firstOrder.getRoot())
														.add(intermediateTreeMerge);
												i--;
												testSize--;
											}
										}

									} else {
										subTreeQueue.add(intermediateTreeMerge);
										numberOfNodesInfo.put(intermediateTreeMerge, intermediateTreeMerge);

										if (invertedRootSteinerTree.containsKey(firstOrder.getRoot())) {
											invertedRootSteinerTree.get(firstOrder.getRoot())
													.add(intermediateTreeMerge);
										} else {
											ArrayList<OptimalSteinerTree> steinerTrees = new ArrayList<OptimalSteinerTree>();
											steinerTrees.add(intermediateTreeMerge);
											invertedRootSteinerTree.put(intermediateTreeMerge.getRoot(), steinerTrees);
										}
									}
									if (intermediateTreeMerge.minimumSteinerTree(keywords)) {
										resultQueue.add(intermediateTreeMerge);
									}
								}
							}
						}
					}
				}
			}
		}
		long endTime = System.currentTimeMillis();
		this.timeConsumptionSuccessfulKS3Constraint = endTime - startTime;
		System.out.println("Constraint search solution status: Infeasible");
		return false;
	}
}
