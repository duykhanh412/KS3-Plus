package queries;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;

import elements.Node;
import elements.OptimalSteinerTree;
import elements.QualitySteinerTree;
import other.QualityQueueComparator;

public class QualityConstraintsQuery_NewVersion {
	public long timeConsumptionSuccessfulKS3Constraint;
	public int numberOfNodes;
	public boolean isSuccessfulIndividualConstraint;

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

		HashMap<QualitySteinerTree, QualitySteinerTree> removedSubTreeQueue = new HashMap<QualitySteinerTree, QualitySteinerTree>();

		// Intermediate hash map to save the information of number of nodes in a
		// tree
		HashMap<OptimalSteinerTree, HashMap<QualitySteinerTree, QualitySteinerTree>> numberOfNodesInfo = new HashMap<OptimalSteinerTree, HashMap<QualitySteinerTree, QualitySteinerTree>>();

		HashMap<String, Integer> keywordsMapping = new HashMap<String, Integer>();

		HashMap<Node, ArrayList<QualitySteinerTree>> invertedRootSteinerTree = new HashMap<Node, ArrayList<QualitySteinerTree>>();

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
						HashMap<QualitySteinerTree, QualitySteinerTree> subNumberOfNodesInfo = new HashMap<QualitySteinerTree, QualitySteinerTree>();
						QualitySteinerTree subInitialTrees = new QualitySteinerTree(root, new HashMap<Node, Node>(),
								keywordsInput, 1, QoS);
						subNumberOfNodesInfo.put(subInitialTrees, subInitialTrees);
						numberOfNodesInfo.put(initialTrees, subNumberOfNodesInfo);

						ArrayList<QualitySteinerTree> steinerTrees = new ArrayList<QualitySteinerTree>();
						steinerTrees.add(subInitialTrees);
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
			QualitySteinerTree copyOfFirstOrder = new QualitySteinerTree(firstOrder.getRoot(), firstOrder.getNodes(),
					firstOrder.getKeywords(), firstOrder.getNumberOfNodes(), firstOrder.getQoS());
			if (numberOfNodesInfo.containsKey(firstOrder)) {
				if (numberOfNodesInfo.get(firstOrder).containsKey(copyOfFirstOrder)) {
					numberOfNodesInfo.get(firstOrder).remove(copyOfFirstOrder);
				}
			}
			// OptimalSteinerTree testRemove =
			// numberOfNodesInfo.remove(firstOrder);

			if (removedSubTreeQueue.containsKey(copyOfFirstOrder)) {
				// if (removedSubTreeQueue.get(copyOfFirstOrder).size() == 0) {
				removedSubTreeQueue.remove(copyOfFirstOrder);
				// } else if
				// (removedSubTreeQueue.get(firstOrder).containsKey(firstOrder.getNumberOfNodes()))
				// {
				// removedSubTreeQueue.get(firstOrder).remove(firstOrder.getNumberOfNodes());
				continue;
				// }
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
				// System.out.println(
				// "The root of the result tree: " + firstOrder.getRoot().ID +
				// "-" + firstOrder.getRoot().kw);
				// System.out.println("The nodes of the tree: ");
				// for (Node n : firstOrder.getNodes().keySet()) {
				// System.out.println(firstOrder.getNodes().get(n).ID + "-" +
				// firstOrder.getNodes().get(n).kw);
				// }
				this.timeConsumptionSuccessfulKS3Constraint = endTime - startTime;
				this.numberOfNodes = firstOrder.getNumberOfNodes();
				if (firstOrder.getNumberOfNodes() != 2)
					this.isSuccessfulIndividualConstraint = false;
				else
					this.isSuccessfulIndividualConstraint = true;
				// System.out.println("The total computation time: " + (endTime
				// - startTime));
				// String subprefix = "";
				// System.out.println("The final quality of the tree: ");
				// for (int i = 0; i < firstOrder.getQoS().length; i++) {
				// System.out.print(subprefix + " " + firstOrder.getQoS()[i]);
				// subprefix = ",";
				// }
				// System.out.println();
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

							QualitySteinerTree copyofIntermediateTree = new QualitySteinerTree(
									intermediateTree.getRoot(), intermediateTree.getNodes(),
									intermediateTree.getKeywords(), intermediateTree.getNumberOfNodes(),
									intermediateTree.getQoS());

							if (numberOfNodesInfo.containsKey(intermediateTree)
									&& numberOfNodesInfo.get(intermediateTree).size() > 0) {

								boolean putIntermediateTree = false;
								ArrayList<QualitySteinerTree> neededToRemoveSteinerTree = new ArrayList<QualitySteinerTree>();
								for (QualitySteinerTree candidateTree : numberOfNodesInfo.get(intermediateTree)
										.keySet()) {

									HashMap<Node, Node> copyNodesOfCandidateTree = new HashMap<Node, Node>();
									copyNodesOfCandidateTree.putAll(candidateTree.getNodes());
									HashMap<Node, Node> copyKeywordsOfCandidateTree = new HashMap<Node, Node>();
									copyKeywordsOfCandidateTree.putAll(candidateTree.getKeywords());

									OptimalSteinerTree optimalSteinerTreeOfCandidateTree = new OptimalSteinerTree(
											new Node(candidateTree.getRoot().ID, candidateTree.getRoot().kw,
													candidateTree.getRoot().QoS),
											copyNodesOfCandidateTree, copyKeywordsOfCandidateTree,
											candidateTree.getNumberOfNodes(), candidateTree.getQoS());

									QualitySteinerTree copyOfCandidateTree = new QualitySteinerTree(
											new Node(candidateTree.getRoot().ID, candidateTree.getRoot().kw,
													candidateTree.getRoot().QoS),
											copyNodesOfCandidateTree, copyKeywordsOfCandidateTree,
											candidateTree.getNumberOfNodes(), candidateTree.getQoS());

									int secondQualityCheck = 0;
									for (int check = 0; check < QoS.length; check++) {
										if (intermediateTree.getQoS()[check] <= copyOfCandidateTree.getQoS()[check])
											secondQualityCheck++;
									}

									if (intermediateTree.getNumberOfNodes() <= copyOfCandidateTree.getNumberOfNodes()) {
										if (secondQualityCheck == QoS.length) {
											removedSubTreeQueue.put(copyOfCandidateTree, copyOfCandidateTree);

											subTreeQueue.add(intermediateTree);

											// numberOfNodesInfo.put(intermediateTree,
											// intermediateTree);
											neededToRemoveSteinerTree.add(candidateTree);
											putIntermediateTree = true;

											if (invertedRootSteinerTree.containsKey(adjacentNodes.get(i))) {
												invertedRootSteinerTree.get(adjacentNodes.get(i)).remove(candidateTree);
												invertedRootSteinerTree.get(adjacentNodes.get(i))
														.add(copyofIntermediateTree);
											} else {
												ArrayList<QualitySteinerTree> steinerTrees = new ArrayList<QualitySteinerTree>();
												steinerTrees.add(copyofIntermediateTree);
												invertedRootSteinerTree.put(adjacentNodes.get(i), steinerTrees);
											}
										} else {
											subTreeQueue.add(intermediateTree);
											HashMap<QualitySteinerTree, QualitySteinerTree> subIntermediateTrees = new HashMap<QualitySteinerTree, QualitySteinerTree>();
											subIntermediateTrees.put(copyofIntermediateTree, copyofIntermediateTree);
											numberOfNodesInfo.put(intermediateTree, subIntermediateTrees);

											if (invertedRootSteinerTree.containsKey(adjacentNodes.get(i))) {
												invertedRootSteinerTree.get(adjacentNodes.get(i))
														.add(copyofIntermediateTree);
											} else {
												ArrayList<QualitySteinerTree> steinerTrees = new ArrayList<QualitySteinerTree>();
												steinerTrees.add(copyofIntermediateTree);
												invertedRootSteinerTree.put(adjacentNodes.get(i), steinerTrees);
											}
										}
									}
								}
								if (putIntermediateTree) {
									numberOfNodesInfo.get(intermediateTree).put(copyofIntermediateTree,
											copyofIntermediateTree);
								}
								if (neededToRemoveSteinerTree.size() > 0) {
									for (int removedStree = 0; removedStree < neededToRemoveSteinerTree
											.size(); removedStree++) {
										numberOfNodesInfo.get(intermediateTree)
												.remove(neededToRemoveSteinerTree.get(removedStree));
									}
								}
							} else {
								subTreeQueue.add(intermediateTree);
								HashMap<QualitySteinerTree, QualitySteinerTree> subIntermediateTrees = new HashMap<QualitySteinerTree, QualitySteinerTree>();
								subIntermediateTrees.put(copyofIntermediateTree, copyofIntermediateTree);
								numberOfNodesInfo.put(intermediateTree, subIntermediateTrees);

								if (invertedRootSteinerTree.containsKey(adjacentNodes.get(i))) {
									invertedRootSteinerTree.get(adjacentNodes.get(i)).add(copyofIntermediateTree);
								} else {
									ArrayList<QualitySteinerTree> steinerTrees = new ArrayList<QualitySteinerTree>();
									steinerTrees.add(copyofIntermediateTree);
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
				ArrayList<QualitySteinerTree> listOfSameRootTrees = new ArrayList<QualitySteinerTree>();
				listOfSameRootTrees = invertedRootSteinerTree.get(firstOrder.getRoot());

				int testSize = listOfSameRootTrees.size();

				for (int i = 0; i < testSize; i++) {
					if (listOfSameRootTrees.get(i).equals(copyOfFirstOrder)) {
						listOfSameRootTrees.remove(i);
						testSize--;
						i--;
						continue;
					}
					if (listOfSameRootTrees.get(i).getKeywordsString().equals(copyOfFirstOrder.getKeywordsString())) {
						if (copyOfFirstOrder.getNumberOfNodes() <= listOfSameRootTrees.get(i).getNumberOfNodes()) {
							int check = 0;
							for (int k = 0; k < firstOrder.getQoS().length; k++) {
								if (firstOrder.getQoS()[k] <= listOfSameRootTrees.get(i).getQoS()[k]) {
									check++;
								}
							}
							if (check == firstOrder.getQoS().length) {
								removedSubTreeQueue.put(listOfSameRootTrees.get(i), listOfSameRootTrees.get(i));

								OptimalSteinerTree copyOfSameRootTree = new OptimalSteinerTree(
										listOfSameRootTrees.get(i).getRoot(), listOfSameRootTrees.get(i).getNodes(),
										listOfSameRootTrees.get(i).getKeywords(),
										listOfSameRootTrees.get(i).getNumberOfNodes(),
										listOfSameRootTrees.get(i).getQoS());

								if (numberOfNodesInfo.containsKey(copyOfSameRootTree)) {
									if (numberOfNodesInfo.containsKey(copyOfSameRootTree)) {
										numberOfNodesInfo.get(copyOfSameRootTree).remove(listOfSameRootTrees.get(i));
									}
								}

								listOfSameRootTrees.remove(i);
								testSize--;
								i--;
							}
						}
						continue;
					}
					if (listOfSameRootTrees.size() == 0) {
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

					// OptimalSteinerTree elementsInSameRootTreeQueue =
					// listOfSameRootTrees.get(i);

					HashMap<Node, Node> copyNodesForMergedTree = new HashMap<Node, Node>();
					copyNodesForMergedTree.putAll(listOfSameRootTrees.get(i).getNodes());
					HashMap<Node, Node> copyKeywordsForMergedTree = new HashMap<Node, Node>();
					copyKeywordsForMergedTree.putAll(listOfSameRootTrees.get(i).getKeywords());

					OptimalSteinerTree mergedTree = new OptimalSteinerTree(
							new Node(listOfSameRootTrees.get(i).getRoot().ID, listOfSameRootTrees.get(i).getRoot().kw,
									listOfSameRootTrees.get(i).getRoot().QoS),
							copyNodesForMergedTree, copyKeywordsForMergedTree,
							listOfSameRootTrees.get(i).getNumberOfNodes(), listOfSameRootTrees.get(i).getQoS());

					if (!intermediateTreeMerge.equals(mergedTree)) {
						if (intermediateTreeMerge.mergeTreeWithQualityConstraints(mergedTree)) {
							QualitySteinerTree copyOfIntermediateTreeMerge = new QualitySteinerTree(
									intermediateTreeMerge.getRoot(), intermediateTreeMerge.getNodes(),
									intermediateTreeMerge.getKeywords(), intermediateTreeMerge.getNumberOfNodes(),
									intermediateTreeMerge.getQoS());

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
									if (numberOfNodesInfo.containsKey(intermediateTreeMerge)
											&& numberOfNodesInfo.get(intermediateTreeMerge).size() > 0) {

										boolean putIntermediateTree = false;
										ArrayList<QualitySteinerTree> neededToRemoveSteinerTree = new ArrayList<QualitySteinerTree>();

										for (QualitySteinerTree candidateTree : numberOfNodesInfo
												.get(intermediateTreeMerge).keySet()) {
											if (intermediateTreeMerge.getNumberOfNodes() <= candidateTree
													.getNumberOfNodes()) {

												int secondQualityCheck = 0;
												for (int check = 0; check < QoS.length; check++) {
													if (intermediateTreeMerge.getQoS()[check] <= candidateTree
															.getQoS()[check])
														secondQualityCheck++;
												}
												if (secondQualityCheck == QoS.length) {
													removedSubTreeQueue.put(candidateTree, candidateTree);
													subTreeQueue.add(intermediateTreeMerge);

													putIntermediateTree = true;
													neededToRemoveSteinerTree.add(candidateTree);

													if (invertedRootSteinerTree.containsKey(firstOrder.getRoot())) {
														invertedRootSteinerTree.get(firstOrder.getRoot())
																.remove(candidateTree);
														invertedRootSteinerTree.get(firstOrder.getRoot())
																.add(copyOfIntermediateTreeMerge);
													}

													i--;
													testSize--;
												} else {
													subTreeQueue.add(intermediateTreeMerge);
													HashMap<QualitySteinerTree, QualitySteinerTree> subIntermediateTrees = new HashMap<QualitySteinerTree, QualitySteinerTree>();
													subIntermediateTrees.put(copyOfIntermediateTreeMerge,
															copyOfIntermediateTreeMerge);
													numberOfNodesInfo.put(intermediateTreeMerge, subIntermediateTrees);

													if (invertedRootSteinerTree.containsKey(firstOrder.getRoot())) {
														invertedRootSteinerTree.get(firstOrder.getRoot())
																.add(copyOfIntermediateTreeMerge);
													} else {
														ArrayList<QualitySteinerTree> steinerTrees = new ArrayList<QualitySteinerTree>();
														steinerTrees.add(copyOfIntermediateTreeMerge);
														invertedRootSteinerTree.put(firstOrder.getRoot(), steinerTrees);
													}
												}

											}
										}

										if (putIntermediateTree) {
											numberOfNodesInfo.get(intermediateTreeMerge)
													.put(copyOfIntermediateTreeMerge, copyOfIntermediateTreeMerge);
										}
										if (neededToRemoveSteinerTree.size() > 0) {
											for (int removedStree = 0; removedStree < neededToRemoveSteinerTree
													.size(); removedStree++) {
												numberOfNodesInfo.get(intermediateTreeMerge)
														.remove(neededToRemoveSteinerTree.get(removedStree));
											}
										}

									} else {
										subTreeQueue.add(intermediateTreeMerge);
										HashMap<QualitySteinerTree, QualitySteinerTree> subIntermediateTrees = new HashMap<QualitySteinerTree, QualitySteinerTree>();
										subIntermediateTrees.put(copyOfIntermediateTreeMerge,
												copyOfIntermediateTreeMerge);
										numberOfNodesInfo.put(intermediateTreeMerge, subIntermediateTrees);

										if (invertedRootSteinerTree.containsKey(firstOrder.getRoot())) {
											invertedRootSteinerTree.get(firstOrder.getRoot())
													.add(copyOfIntermediateTreeMerge);
										} else {
											ArrayList<QualitySteinerTree> steinerTrees = new ArrayList<QualitySteinerTree>();
											steinerTrees.add(copyOfIntermediateTreeMerge);
											invertedRootSteinerTree.put(firstOrder.getRoot(), steinerTrees);
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
