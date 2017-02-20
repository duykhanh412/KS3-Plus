package queries;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;

import elements.Node;
import elements.OptimalSteinerTree;
import other.OptimalQueueComparator;

public class OptimalQuery_NewVersion {
	public long timeConsumptionSuccessfulKS3Optimal;
	public double optimalQualityValue;

	public boolean optimalQuerySeriesB(ArrayList<String> keywords,
			HashMap<String, ArrayList<Node>> invertedIndexAPIName, HashMap<Node, ArrayList<Node>> adjIndex, int[] QoS,
			double optimalQualityValue) {
		System.out.println(keywords);
		String prefix = "";
		for (int i = 0; i < QoS.length; i++) {
			System.out.print(prefix + " " + QoS[i]);
			prefix = ",";
		}
		System.out.println();

		System.out.println("OPTIMAL QUERY");

		Comparator<OptimalSteinerTree> comparator = new OptimalQueueComparator();

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

		HashMap<Node, ArrayList<OptimalSteinerTree>> invertedRootSteinerTreeWithQuality = new HashMap<Node, ArrayList<OptimalSteinerTree>>();

		long startTime = System.currentTimeMillis();

		// Put nodes containing keywords in the queue
		for (int i = 0; i < keywords.size(); i++) {
			if (invertedIndexAPIName.containsKey(keywords.get(i))) {
				ArrayList<Node> listOfKeywordNode = invertedIndexAPIName.get(keywords.get(i));
				for (int j = 0; j < listOfKeywordNode.size(); j++) {
					HashMap<Node, Node> keywordsInput = new HashMap<Node, Node>();
					keywordsInput.put(new Node(listOfKeywordNode.get(j).ID, listOfKeywordNode.get(j).kw),
							new Node(listOfKeywordNode.get(j).ID, listOfKeywordNode.get(j).kw));
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

						ArrayList<OptimalSteinerTree> SteinerTreeWithQualitys = new ArrayList<OptimalSteinerTree>();
						SteinerTreeWithQualitys.add(initialTrees);
						invertedRootSteinerTreeWithQuality.put(root, SteinerTreeWithQualitys);
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
				} else if (removedSubTreeQueue.get(firstOrder).containsKey(firstOrder.getQoS()[0])) {
					removedSubTreeQueue.get(firstOrder).remove(firstOrder.getQoS()[0]);
					continue;
				}
			}

			// if (resultQueue.size() > 0) {
			// SteinerTreeWithQuality firstResult = resultQueue.peek();
			// if (firstOrder.getQoS()[0] >= firstResult.getQoS()[0]) {
			// long endTime = System.currentTimeMillis();
			// System.out.println("The root of the result tree: " +
			// firstResult.getRoot().ID + "-"
			// + firstResult.getRoot().kw);
			// System.out.println("The nodes of the tree: ");
			// for (String n : firstResult.getNodes().keySet()) {
			// System.out.println(firstResult.getNodes().get(n).ID + "-" +
			// firstResult.getNodes().get(n).kw);
			// }
			// this.timeConsumptionSuccessfulKS3Optimal = endTime - startTime;
			// this.optimalQualityValue = (double) firstResult.getQoS()[0];
			//
			// System.out.println("The total computation time: " + (endTime -
			// startTime));
			// System.out.println("The optimal objective value: " +
			// this.optimalQualityValue);
			// if (this.optimalQualityValue != optimalQualityValue) {
			// System.out.println("One of the result is wrong");
			// }
			// return true;
			// }
			//
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
				System.out.println("Number of nodes, optimal: " + firstOrder.getNumberOfNodes());
				this.timeConsumptionSuccessfulKS3Optimal = endTime - startTime;
				this.optimalQualityValue = (double) firstOrder.getQoS()[0];
				System.out.println("The total computation time: " + (endTime - startTime));
				System.out.println("The optimal objective value: " + this.optimalQualityValue);
				if (this.optimalQualityValue != optimalQualityValue) {
					System.out.println("One of the result is wrong");
				}
				String subprefix = "";
				System.out.println("The final quality of the tree: ");
				for (int i = 0; i < firstOrder.getQoS().length; i++) {
					System.out.print(subprefix + " " + firstOrder.getQoS()[i]);
					subprefix = ",";
				}
				return true;
			}

			ArrayList<Node> adjacentNodes = adjIndex.get(firstOrder.getRoot());

			// Grow tree process
			for (int i = 0; i < adjacentNodes.size(); i++) {
				HashMap<Node, Node> copyNodes = new HashMap<Node, Node>();
				copyNodes.putAll(firstOrder.getNodes());
				HashMap<Node, Node> copyKeywords = new HashMap<Node, Node>();
				copyKeywords.putAll(firstOrder.getKeywords());

				OptimalSteinerTree intermediateTree = new OptimalSteinerTree(
						new Node(firstOrder.getRoot().ID, firstOrder.getRoot().kw, firstOrder.getRoot().QoS), copyNodes,
						copyKeywords, firstOrder.getNumberOfNodes(), firstOrder.getQoS());

				// Grow tree to the neighbours
				if (intermediateTree.growTree(adjacentNodes.get(i), keywordsMapping)) {
					// Limit number of nodes in the optimal tree which must be
					// less than twice of the number of keywords in the query

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

								// Check if the quality value of the current
								// tree is
								// less than the quality value of the existing
								// tree
								// in the priority then updates it
								int secondQualityCheck = 0;
								for (int check = 0; check < QoS.length; check++) {
									if (intermediateTree.getQoS()[check] <= test.getQoS()[check])
										secondQualityCheck++;
								}

								if (((intermediateTree.getQoS()[0] < test.getQoS()[0])
										&& (secondQualityCheck == QoS.length))
										|| ((intermediateTree.getQoS()[0] == test.getQoS()[0])
												&& (intermediateTree.getNumberOfNodes() < test.getNumberOfNodes())
												&& (secondQualityCheck == QoS.length))) {

									// Updating the priority queue
									// Put the must-removed tree to a hashmap
									// and
									// remove later in the next loops
									if (removedSubTreeQueue.containsKey(test)) {
										removedSubTreeQueue.get(test).put(test.getQoS()[0], test);
									} else {
										HashMap<Integer, OptimalSteinerTree> numberOfNodesMap = new HashMap<Integer, OptimalSteinerTree>();
										numberOfNodesMap.put(test.getQoS()[0], test);
										removedSubTreeQueue.put(test, numberOfNodesMap);
									}
									// subTreeQueue.remove(test);
									subTreeQueue.add(intermediateTree);

									numberOfNodesInfo.put(intermediateTree, intermediateTree);

									if (invertedRootSteinerTreeWithQuality.containsKey(adjacentNodes.get(i))) {
										invertedRootSteinerTreeWithQuality.get(adjacentNodes.get(i)).remove(test);
										invertedRootSteinerTreeWithQuality.get(adjacentNodes.get(i))
												.add(intermediateTree);
									} else {
										ArrayList<OptimalSteinerTree> SteinerTreeWithQualitys = new ArrayList<OptimalSteinerTree>();
										SteinerTreeWithQualitys.add(intermediateTree);
										invertedRootSteinerTreeWithQuality.put(adjacentNodes.get(i),
												SteinerTreeWithQualitys);
									}
								}
							} else {
								subTreeQueue.add(intermediateTree);
								numberOfNodesInfo.put(intermediateTree, intermediateTree);

								if (invertedRootSteinerTreeWithQuality.containsKey(adjacentNodes.get(i))) {
									invertedRootSteinerTreeWithQuality.get(adjacentNodes.get(i)).add(intermediateTree);
								} else {
									ArrayList<OptimalSteinerTree> SteinerTreeWithQualitys = new ArrayList<OptimalSteinerTree>();
									SteinerTreeWithQualitys.add(intermediateTree);
									invertedRootSteinerTreeWithQuality.put(adjacentNodes.get(i),
											SteinerTreeWithQualitys);
								}
							}
							if (intermediateTree.minimumSteinerTree(keywords)) {
								resultQueue.add(intermediateTree);
							}
						}
					}
				}
			}

			// Merge Tree process
			if (invertedRootSteinerTreeWithQuality.containsKey(firstOrder.getRoot())) {
				ArrayList<OptimalSteinerTree> test = new ArrayList<OptimalSteinerTree>();
				test = invertedRootSteinerTreeWithQuality.get(firstOrder.getRoot());

				int testSize = test.size();

				for (int i = 0; i < testSize; i++) {
					if (test.get(i).equals(firstOrder)) {
						test.remove(i);
						testSize--;
						i--;
						continue;
					}

					// Check if the current tree from the priority queue is
					// having the quality value less than the other tree which
					// have the same keywords but different nodes
					if (test.get(i).getKeywordsString().equals(firstOrder.getKeywordsString())) {
						if (firstOrder.getQoS()[0] < test.get(i).getQoS()[0]) {
							int check = 0;

							// If the current tree have less quality values than
							// the other tree in the priority queue, then remove
							// the other tree
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
						invertedRootSteinerTreeWithQuality.remove(firstOrder.getRoot());
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

						// Merge tree
						if (intermediateTreeMerge.mergeTreeWithQualityConstraints(mergedTree)) {
							if ((intermediateTreeMerge.getNumberOfNodes()
									+ (keywords.size() - intermediateTreeMerge.getKeywords().size())) <= 2
											* keywords.size()) {
								// Check quality constraints
								int qualityCheck = 0;
								for (int check = 0; check < QoS.length; check++) {
									if (intermediateTreeMerge.getQoS()[check] <= QoS[check])
										qualityCheck++;
								}
								if (qualityCheck == QoS.length) {

									// Check if the priority queue contains the
									// current tree after merging
									if (numberOfNodesInfo.containsKey(intermediateTreeMerge)) {
										int secondQualityCheck = 0;
										for (int check = 0; check < QoS.length; check++) {
											if (intermediateTreeMerge.getQoS()[check] <= numberOfNodesInfo
													.get(intermediateTreeMerge).getQoS()[check])
												secondQualityCheck++;
										}

										if (((intermediateTreeMerge.getQoS()[0] < numberOfNodesInfo
												.get(intermediateTreeMerge).getQoS()[0])
												&& (secondQualityCheck == QoS.length))
												|| ((intermediateTreeMerge.getQoS()[0] == numberOfNodesInfo
														.get(intermediateTreeMerge).getQoS()[0])
														&& (intermediateTreeMerge.getNumberOfNodes() < numberOfNodesInfo
																.get(intermediateTreeMerge).getNumberOfNodes())
														&& (secondQualityCheck == QoS.length))) {

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

											invertedRootSteinerTreeWithQuality.get(firstOrder.getRoot())
													.remove(numberOfNodesInfo.get(intermediateTreeMerge));

											invertedRootSteinerTreeWithQuality.get(firstOrder.getRoot())
													.add(intermediateTreeMerge);
											i--;
											testSize--;
										}

									} else {
										subTreeQueue.add(intermediateTreeMerge);
										numberOfNodesInfo.put(intermediateTreeMerge, intermediateTreeMerge);

										if (invertedRootSteinerTreeWithQuality.containsKey(firstOrder.getRoot())) {
											invertedRootSteinerTreeWithQuality.get(firstOrder.getRoot())
													.add(intermediateTreeMerge);
										} else {
											ArrayList<OptimalSteinerTree> SteinerTreeWithQualitys = new ArrayList<OptimalSteinerTree>();
											SteinerTreeWithQualitys.add(intermediateTreeMerge);
											invertedRootSteinerTreeWithQuality.put(firstOrder.getRoot(),
													SteinerTreeWithQualitys);
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
		System.out.println("Optimal search solution status: Infeasible");
		return false;
	}
}
