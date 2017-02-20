package mergetree.implementation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;

import elements.Node;
import elements.OptimalSteinerTree;
import elements.QualitySteinerTree;
import elements.SteinerTree;
import interfaceclass.MergeTreeBehavior;

public class MergeNormalTreeBehavior implements MergeTreeBehavior<SteinerTree, QualitySteinerTree> {

	public void mergeNormalTree(SteinerTree firstOrder, HashMap<Node, ArrayList<Node>> adjIndex,
			HashMap<String, Integer> keywordsMapping, List<String> keywords,
			HashMap<SteinerTree, SteinerTree> numberOfNodesInfo,
			HashMap<SteinerTree, HashMap<Integer, SteinerTree>> removedSubTreeQueue,
			PriorityQueue<SteinerTree> subTreeQueue, HashMap<Node, ArrayList<SteinerTree>> invertedRootSteinerTree,
			PriorityQueue<SteinerTree> resultQueue) {
		// TODO Auto-generated method stub

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
				HashMap<Node, Node> copyNodes = new HashMap<Node, Node>();
				copyNodes.putAll(firstOrder.getNodes());
				HashMap<Node, Node> copyKeywords = new HashMap<Node, Node>();
				copyKeywords.putAll(firstOrder.getKeywords());

				SteinerTree intermediateTreeMerge = new SteinerTree(
						new Node(firstOrder.getRoot().ID, firstOrder.getRoot().kw), copyNodes, copyKeywords,
						firstOrder.getNumberOfNodes());

				SteinerTree elementsInSameRootTreeQueue = test.get(i);

				HashMap<Node, Node> copyNodesForMergedTree = new HashMap<Node, Node>();
				copyNodesForMergedTree.putAll(elementsInSameRootTreeQueue.getNodes());
				HashMap<Node, Node> copyKeywordsForMergedTree = new HashMap<Node, Node>();
				copyKeywordsForMergedTree.putAll(elementsInSameRootTreeQueue.getKeywords());

				SteinerTree mergedTree = new SteinerTree(
						new Node(elementsInSameRootTreeQueue.getRoot().ID, elementsInSameRootTreeQueue.getRoot().kw),
						copyNodesForMergedTree, copyKeywordsForMergedTree,
						elementsInSameRootTreeQueue.getNumberOfNodes());

				if (!intermediateTreeMerge.equals(mergedTree)) {
					if (intermediateTreeMerge.mergeTree(mergedTree)) {
						if ((intermediateTreeMerge.getNumberOfNodes()
								+ (keywords.size() - intermediateTreeMerge.getKeywords().size())) <= 2
										* keywords.size()) {
							if (numberOfNodesInfo.containsKey(intermediateTreeMerge)) {
								if (intermediateTreeMerge.getNumberOfNodes() < numberOfNodesInfo
										.get(intermediateTreeMerge).getNumberOfNodes()) {

									if (removedSubTreeQueue.containsKey(intermediateTreeMerge)) {
										removedSubTreeQueue.get(intermediateTreeMerge)
												.put(intermediateTreeMerge.getNumberOfNodes(), intermediateTreeMerge);
									} else {
										HashMap<Integer, SteinerTree> numberOfNodesMap = new HashMap<Integer, SteinerTree>();
										numberOfNodesMap.put(intermediateTreeMerge.getNumberOfNodes(),
												intermediateTreeMerge);
										removedSubTreeQueue.put(intermediateTreeMerge, numberOfNodesMap);
									}

									subTreeQueue.add(intermediateTreeMerge);

									numberOfNodesInfo.put(intermediateTreeMerge, intermediateTreeMerge);

									long startBlock = System.currentTimeMillis();
									invertedRootSteinerTree.get(firstOrder.getRoot())
											.remove(numberOfNodesInfo.get(intermediateTreeMerge));
									long endBlock = System.currentTimeMillis();
									if ((endBlock - startBlock) > 100) {
										System.out.println("Computation time for the merge process block code is: "
												+ (endBlock - startBlock));
									}
									invertedRootSteinerTree.get(firstOrder.getRoot()).add(intermediateTreeMerge);
									i--;
									testSize--;
								}

							} else {
								subTreeQueue.add(intermediateTreeMerge);
								numberOfNodesInfo.put(intermediateTreeMerge, intermediateTreeMerge);

								if (invertedRootSteinerTree.containsKey(firstOrder.getRoot())) {
									invertedRootSteinerTree.get(firstOrder.getRoot()).add(intermediateTreeMerge);
								} else {
									ArrayList<SteinerTree> steinerTrees = new ArrayList<SteinerTree>();
									steinerTrees.add(intermediateTreeMerge);
									invertedRootSteinerTree.put(intermediateTreeMerge.getRoot(), steinerTrees);
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

	public void mergeQualityTree(SteinerTree firstOrder, List<Node> adjacentNodes,
			HashMap<String, Integer> keywordsMapping, List<String> keywords,
			HashMap<SteinerTree, HashMap<QualitySteinerTree, QualitySteinerTree>> numberOfNodesInfo,
			HashMap<QualitySteinerTree, QualitySteinerTree> removedSubTreeQueue,
			PriorityQueue<SteinerTree> subTreeQueue,
			HashMap<Node, ArrayList<QualitySteinerTree>> invertedRootSteinerTree,
			PriorityQueue<SteinerTree> resultQueue, int[] QoS) {
		// TODO Auto-generated method stub
		
	}
}
