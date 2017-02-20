package mergetree.implementation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;

import elements.Node;
import elements.OptimalSteinerTree;
import elements.QualitySteinerTree;
import interfaceclass.MergeTreeBehavior;

public class MergeQualityTreeBehavior implements MergeTreeBehavior<OptimalSteinerTree, QualitySteinerTree> {

	public void mergeQualityTree(OptimalSteinerTree firstOrder, List<Node> adjacentNodes,
			HashMap<String, Integer> keywordsMapping, List<String> keywords,
			HashMap<OptimalSteinerTree, HashMap<QualitySteinerTree, QualitySteinerTree>> numberOfNodesInfo,
			HashMap<QualitySteinerTree, QualitySteinerTree> removedSubTreeQueue,
			PriorityQueue<OptimalSteinerTree> subTreeQueue,
			HashMap<Node, ArrayList<QualitySteinerTree>> invertedRootSteinerTree,
			PriorityQueue<OptimalSteinerTree> resultQueue, int[] QoS) {
		// TODO Auto-generated method stub

		QualitySteinerTree copyOfFirstOrder = new QualitySteinerTree(firstOrder.getRoot(), firstOrder.getNodes(),
				firstOrder.getKeywords(), firstOrder.getNumberOfNodes(), firstOrder.getQoS());

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
									listOfSameRootTrees.get(i).getNumberOfNodes(), listOfSameRootTrees.get(i).getQoS());

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
						new Node(firstOrder.getRoot().ID, firstOrder.getRoot().kw, firstOrder.getRoot().QoS), copyNodes,
						copyKeywords, firstOrder.getNumberOfNodes(), firstOrder.getQoS());

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

									for (QualitySteinerTree candidateTree : numberOfNodesInfo.get(intermediateTreeMerge)
											.keySet()) {
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
										numberOfNodesInfo.get(intermediateTreeMerge).put(copyOfIntermediateTreeMerge,
												copyOfIntermediateTreeMerge);
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
									subIntermediateTrees.put(copyOfIntermediateTreeMerge, copyOfIntermediateTreeMerge);
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
								if (intermediateTreeMerge.minimumSteinerTree((ArrayList<String>) keywords)) {
									resultQueue.add(intermediateTreeMerge);
								}
							}
						}
					}
				}
			}
		}
	}

	public void mergeNormalTree(OptimalSteinerTree firstOrder, HashMap<Node, ArrayList<Node>> adjIndex,
			HashMap<String, Integer> keywordsMapping, List<String> keywords,
			HashMap<OptimalSteinerTree, OptimalSteinerTree> numberOfNodesInfo,
			HashMap<OptimalSteinerTree, HashMap<Integer, OptimalSteinerTree>> removedSubTreeQueue,
			PriorityQueue<OptimalSteinerTree> subTreeQueue,
			HashMap<Node, ArrayList<OptimalSteinerTree>> invertedRootSteinerTree,
			PriorityQueue<OptimalSteinerTree> resultQueue) {
		// TODO Auto-generated method stub
		
	}

}
