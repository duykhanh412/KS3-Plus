package queries;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;

import other.OptimalQueueComparator;
import elements.*;

public class OptimalQuery {
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

		Comparator<SteinerTree> comparator = new OptimalQueueComparator();

		// The first tree taken from the priority queue
		// SteinerTree firstOrder = new SteinerTree();

		// The priority queue sorted in the increasing order of costs of trees
		PriorityQueue<SteinerTree> subTreeQueue = new PriorityQueue<SteinerTree>(keywords.size(), comparator);

		PriorityQueue<SteinerTree> resultQueue = new PriorityQueue<SteinerTree>(keywords.size(), comparator);

		HashMap<SteinerTree, HashMap<Integer, SteinerTree>> removedSubTreeQueue = new HashMap<SteinerTree, HashMap<Integer, SteinerTree>>();
		// Intermediate hash map to save the information of number of nodes in a
		// tree
		HashMap<SteinerTree, SteinerTree> numberOfNodesInfo = new HashMap<SteinerTree, SteinerTree>();

		HashMap<String, Integer> keywordsMapping = new HashMap<String, Integer>();

		HashMap<Node, ArrayList<SteinerTree>> invertedRootSteinerTree = new HashMap<Node, ArrayList<SteinerTree>>();

		long startTime = System.currentTimeMillis();

		// boolean answer = false;
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
					SteinerTree initialTrees = new SteinerTree(root, new HashMap<String, Node>(), keywordsInput, 1,
							QoS.length);
					// double[] initialNodesQuality = new
					// double[listOfKeywordNode.get(j).QoS.length];
					// for (int check = 0; check <
					// listOfKeywordNode.get(j).QoS.length; check++) {
					// initialNodesQuality[check] = (double)
					// listOfKeywordNode.get(j).QoS[check] / 100;
					// }
					initialTrees.setQoS(listOfKeywordNode.get(j).QoS);
					int qualityCheck = 0;
					for (int check = 0; check < QoS.length; check++) {
						if (initialTrees.getQoS()[check] <= QoS[check])
							qualityCheck++;
					}
					if (qualityCheck == QoS.length) {
						subTreeQueue.add(initialTrees);
						numberOfNodesInfo.put(initialTrees, initialTrees);

						ArrayList<SteinerTree> steinerTrees = new ArrayList<SteinerTree>();
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

			SteinerTree firstOrder = subTreeQueue.remove();
			SteinerTree testRemove = numberOfNodesInfo.remove(firstOrder);

			if (removedSubTreeQueue.containsKey(firstOrder)) {
				if (removedSubTreeQueue.get(firstOrder).size() == 0) {
					removedSubTreeQueue.remove(firstOrder);
				} else if (removedSubTreeQueue.get(firstOrder).containsKey(firstOrder.getQoS()[0])) {
					removedSubTreeQueue.get(firstOrder).remove(firstOrder.getQoS()[0]);
					continue;
				}
			}

			// if (resultQueue.size() > 0) {
			// SteinerTree firstResult = resultQueue.peek();
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
				for (String n : firstOrder.getNodes().keySet()) {
					System.out.println(firstOrder.getNodes().get(n).ID + "-" + firstOrder.getNodes().get(n).kw);
				}
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

			// Grow tree
			for (int i = 0; i < adjacentNodes.size(); i++) {
				HashMap<String, Node> copyNodes = new HashMap<String, Node>();
				copyNodes.putAll(firstOrder.getNodes());
				HashMap<Node, Node> copyKeywords = new HashMap<Node, Node>();
				copyKeywords.putAll(firstOrder.getKeywords());

				SteinerTree intermediateTree = new SteinerTree(
						new Node(firstOrder.getRoot().ID, firstOrder.getRoot().kw, firstOrder.getRoot().QoS), copyNodes,
						copyKeywords, firstOrder.getNumberOfNodes(), firstOrder.getQoS());

				if (intermediateTree.growTree(adjacentNodes.get(i), keywordsMapping)) {
//					if ((intermediateTree.getNumberOfNodes()
//							+ (keywords.size() - intermediateTree.getKeywords().size())) <= 2 * keywords.size()) {
						intermediateTree.setQoS(adjacentNodes.get(i).QoS);
						// Check quality constraints
						int qualityCheck = 0;
						for (int check = 0; check < QoS.length; check++) {
							if (intermediateTree.getQoS()[check] <= QoS[check])
								qualityCheck++;
						}
						if (qualityCheck == QoS.length) {
							if (numberOfNodesInfo.containsKey(intermediateTree)) {
								SteinerTree test = new SteinerTree(
										new Node(numberOfNodesInfo.get(intermediateTree).getRoot().ID,
												numberOfNodesInfo.get(intermediateTree).getRoot().kw,
												numberOfNodesInfo.get(intermediateTree).getRoot().QoS),
										numberOfNodesInfo.get(intermediateTree).getNodes(),
										numberOfNodesInfo.get(intermediateTree).getKeywords(),
										numberOfNodesInfo.get(intermediateTree).getNumberOfNodes(),
										numberOfNodesInfo.get(intermediateTree).getQoS());

								if (intermediateTree.getQoS()[0] < test.getQoS()[0]
										|| ((intermediateTree.getQoS()[0] == test.getQoS()[0])
												&& (intermediateTree.getNumberOfNodes() < test.getNumberOfNodes()))) {
									if (removedSubTreeQueue.containsKey(test)) {
										removedSubTreeQueue.get(test).put(test.getQoS()[0], test);
									} else {
										HashMap<Integer, SteinerTree> numberOfNodesMap = new HashMap<Integer, SteinerTree>();
										numberOfNodesMap.put(test.getQoS()[0], test);
										removedSubTreeQueue.put(test, numberOfNodesMap);
									}
									// subTreeQueue.remove(test);
									subTreeQueue.add(intermediateTree);

									numberOfNodesInfo.put(intermediateTree, intermediateTree);

									if (invertedRootSteinerTree.containsKey(adjacentNodes.get(i))) {
										invertedRootSteinerTree.get(adjacentNodes.get(i)).remove(test);
										invertedRootSteinerTree.get(adjacentNodes.get(i)).add(intermediateTree);
									} else {
										ArrayList<SteinerTree> steinerTrees = new ArrayList<SteinerTree>();
										steinerTrees.add(intermediateTree);
										invertedRootSteinerTree.put(adjacentNodes.get(i), steinerTrees);
									}
								}
							} else {
								subTreeQueue.add(intermediateTree);
								numberOfNodesInfo.put(intermediateTree, intermediateTree);

								if (invertedRootSteinerTree.containsKey(adjacentNodes.get(i))) {
									invertedRootSteinerTree.get(adjacentNodes.get(i)).add(intermediateTree);
								} else {
									ArrayList<SteinerTree> steinerTrees = new ArrayList<SteinerTree>();
									steinerTrees.add(intermediateTree);
									invertedRootSteinerTree.put(adjacentNodes.get(i), steinerTrees);
								}
							}
							if (intermediateTree.minimumSteinerTree(keywords)) {
								resultQueue.add(intermediateTree);
							}
						}
//					}
				}
			}

			// Merge Tree
			if (invertedRootSteinerTree.containsKey(firstOrder.getRoot())) {
				ArrayList<SteinerTree> test = new ArrayList<SteinerTree>();
				test = invertedRootSteinerTree.get(firstOrder.getRoot());

				int testSize = test.size();

				for (int i = 0; i < testSize; i++) {
					if (test.get(i).equals(firstOrder)) {
						test.remove(i);
						testSize--;
						i--;
						continue;
					}
					if (test.size() == 0) {
						invertedRootSteinerTree.remove(firstOrder.getRoot());
						break;
					}
					HashMap<String, Node> copyNodes = new HashMap<String, Node>();
					copyNodes.putAll(firstOrder.getNodes());
					HashMap<Node, Node> copyKeywords = new HashMap<Node, Node>();
					copyKeywords.putAll(firstOrder.getKeywords());

					SteinerTree intermediateTreeMerge = new SteinerTree(
							new Node(firstOrder.getRoot().ID, firstOrder.getRoot().kw, firstOrder.getRoot().QoS),
							copyNodes, copyKeywords, firstOrder.getNumberOfNodes(), firstOrder.getQoS());

					SteinerTree elementsInSameRootTreeQueue = test.get(i);

					HashMap<String, Node> copyNodesForMergedTree = new HashMap<String, Node>();
					copyNodesForMergedTree.putAll(elementsInSameRootTreeQueue.getNodes());
					HashMap<Node, Node> copyKeywordsForMergedTree = new HashMap<Node, Node>();
					copyKeywordsForMergedTree.putAll(elementsInSameRootTreeQueue.getKeywords());

					SteinerTree mergedTree = new SteinerTree(
							new Node(elementsInSameRootTreeQueue.getRoot().ID, elementsInSameRootTreeQueue.getRoot().kw,
									elementsInSameRootTreeQueue.getRoot().QoS),
							copyNodesForMergedTree, copyKeywordsForMergedTree,
							elementsInSameRootTreeQueue.getNumberOfNodes(), elementsInSameRootTreeQueue.getQoS());

					if (!intermediateTreeMerge.equals(mergedTree)) {
						if (intermediateTreeMerge.mergeTreeWithQualityConstraints(mergedTree)) {
//							if ((intermediateTreeMerge.getNumberOfNodes()
//									+ (keywords.size() - intermediateTreeMerge.getKeywords().size())) <= 2
//											* keywords.size()) {
								// Check quality constraints
								int qualityCheck = 0;
								for (int check = 0; check < QoS.length; check++) {
									if (intermediateTreeMerge.getQoS()[check] <= QoS[check])
										qualityCheck++;
								}
								if (qualityCheck == QoS.length) {
									if (numberOfNodesInfo.containsKey(intermediateTreeMerge)) {
										if (intermediateTreeMerge.getQoS()[0] < numberOfNodesInfo
												.get(intermediateTreeMerge).getQoS()[0]
												|| ((intermediateTreeMerge.getQoS()[0] == numberOfNodesInfo
														.get(intermediateTreeMerge).getQoS()[0])
														&& (intermediateTreeMerge.getNumberOfNodes() < numberOfNodesInfo
																.get(intermediateTreeMerge).getNumberOfNodes()))) {

											if (removedSubTreeQueue.containsKey(intermediateTreeMerge)) {
												removedSubTreeQueue.get(intermediateTreeMerge).put(
														intermediateTreeMerge.getNumberOfNodes(),
														intermediateTreeMerge);
											} else {
												HashMap<Integer, SteinerTree> numberOfNodesMap = new HashMap<Integer, SteinerTree>();
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

									} else {
										subTreeQueue.add(intermediateTreeMerge);
										numberOfNodesInfo.put(intermediateTreeMerge, intermediateTreeMerge);

										if (invertedRootSteinerTree.containsKey(firstOrder.getRoot())) {
											invertedRootSteinerTree.get(firstOrder.getRoot())
													.add(intermediateTreeMerge);
										} else {
											ArrayList<SteinerTree> steinerTrees = new ArrayList<SteinerTree>();
											steinerTrees.add(intermediateTreeMerge);
											invertedRootSteinerTree.put(intermediateTreeMerge.getRoot(), steinerTrees);
										}
									}
									if (intermediateTreeMerge.minimumSteinerTree(keywords)) {
										resultQueue.add(intermediateTreeMerge);
									}
								}
//							}
						}
					}
				}
			}
		}
		System.out.println("Optimal search solution status: Infeasible");
		return false;
	}
}
