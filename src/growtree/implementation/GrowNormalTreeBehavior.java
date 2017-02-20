package growtree.implementation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;

import elements.Node;
import elements.OptimalSteinerTree;
import elements.QualitySteinerTree;
import elements.SteinerTree;
import interfaceclass.GrowTreeBehavior;

public class GrowNormalTreeBehavior implements GrowTreeBehavior<SteinerTree, QualitySteinerTree> {
	public long timeConsumptionSuccessfulKS3;
	public long numberOfNodes;

	public boolean growNormalTree(SteinerTree firstOrder, HashMap<Node, ArrayList<Node>> adjIndex,
			HashMap<String, Integer> keywordsMapping, List<String> keywords,
			HashMap<SteinerTree, SteinerTree> numberOfNodesInfo,
			HashMap<SteinerTree, HashMap<Integer, SteinerTree>> removedSubTreeQueue,
			PriorityQueue<SteinerTree> subTreeQueue, HashMap<Node, ArrayList<SteinerTree>> invertedRootSteinerTree,
			PriorityQueue<SteinerTree> resultQueue) {
		// TODO Auto-generated method stub

		SteinerTree testRemove = numberOfNodesInfo.remove(firstOrder);
		List<Node> adjacentNodes = adjIndex.get(firstOrder.getRoot());
		
		if (removedSubTreeQueue.containsKey(firstOrder)) {
			if (removedSubTreeQueue.get(firstOrder).size() == 0) {
				removedSubTreeQueue.remove(firstOrder);
			} else if (removedSubTreeQueue.get(firstOrder).containsKey(firstOrder.getNumberOfNodes())) {
				removedSubTreeQueue.get(firstOrder).remove(firstOrder.getNumberOfNodes());
				return false;
			}
		}

		for (int i = 0; i < adjacentNodes.size(); i++) {
			HashMap<Node, Node> copyNodes = new HashMap<Node, Node>();
			copyNodes.putAll(firstOrder.getNodes());
			HashMap<Node, Node> copyKeywords = new HashMap<Node, Node>();
			copyKeywords.putAll(firstOrder.getKeywords());

			SteinerTree intermediateTree = new SteinerTree(new Node(firstOrder.getRoot().ID, firstOrder.getRoot().kw),
					copyNodes, copyKeywords, firstOrder.getNumberOfNodes());

			if (intermediateTree.growTree(adjacentNodes.get(i), keywordsMapping)) {
				if ((intermediateTree.getNumberOfNodes()
						+ (keywords.size() - intermediateTree.getKeywords().size())) <= 2 * keywords.size()) {
					if (numberOfNodesInfo.containsKey(intermediateTree)) {
						SteinerTree test = new SteinerTree(numberOfNodesInfo.get(intermediateTree).getRoot(),
								numberOfNodesInfo.get(intermediateTree).getNodes(),
								numberOfNodesInfo.get(intermediateTree).getKeywords(),
								numberOfNodesInfo.get(intermediateTree).getNumberOfNodes());

						if (intermediateTree.getNumberOfNodes() < numberOfNodesInfo.get(intermediateTree)
								.getNumberOfNodes()) {
							if (removedSubTreeQueue.containsKey(test)) {
								removedSubTreeQueue.get(test).put(test.getNumberOfNodes(), test);
							} else {
								HashMap<Integer, SteinerTree> numberOfNodesMap = new HashMap<Integer, SteinerTree>();
								numberOfNodesMap.put(test.getNumberOfNodes(), test);
								removedSubTreeQueue.put(test, numberOfNodesMap);
							}
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
					if (intermediateTree.minimumSteinerTree((ArrayList<String>) keywords)) {
						resultQueue.add(intermediateTree);
					}
				}
			}
		}
		return true;
	}

	public List<Long> checkResult(PriorityQueue<SteinerTree> resultQueue, SteinerTree firstOrder, long startTime) {
		// TODO Auto-generated method stub
		if (resultQueue.size() > 0) {
			SteinerTree firstResult = resultQueue.peek();
			if (firstOrder.getNumberOfNodes() >= firstResult.getNumberOfNodes()) {
				long endTime = System.currentTimeMillis();
				this.timeConsumptionSuccessfulKS3 = endTime - startTime;
				this.numberOfNodes = firstResult.getNumberOfNodes();
				List<Long> result = new ArrayList<Long>();
				result.add(timeConsumptionSuccessfulKS3);
				result.add((long) this.numberOfNodes);
				return result;
			}

		}
		return null;
	}

	public void growQualityTree(SteinerTree firstOrder, HashMap<Node, ArrayList<Node>> adjIndex,
			HashMap<String, Integer> keywordsMapping, List<String> keywords,
			HashMap<SteinerTree, HashMap<QualitySteinerTree, QualitySteinerTree>> numberOfNodesInfo,
			HashMap<QualitySteinerTree, QualitySteinerTree> removedSubTreeQueue,
			PriorityQueue<SteinerTree> subTreeQueue,
			HashMap<Node, ArrayList<QualitySteinerTree>> invertedRootSteinerTree,
			PriorityQueue<SteinerTree> resultQueue, int[] QoS) {
		// TODO Auto-generated method stub
		
	}

}
