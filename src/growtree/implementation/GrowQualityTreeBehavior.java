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

public class GrowQualityTreeBehavior implements GrowTreeBehavior<OptimalSteinerTree, QualitySteinerTree> {

	public void growQualityTree(OptimalSteinerTree firstOrder, HashMap<Node, ArrayList<Node>> adjIndex,
			HashMap<String, Integer> keywordsMapping, List<String> keywords,
			HashMap<OptimalSteinerTree, HashMap<QualitySteinerTree, QualitySteinerTree>> numberOfNodesInfo,
			HashMap<QualitySteinerTree, QualitySteinerTree> removedSubTreeQueue,
			PriorityQueue<OptimalSteinerTree> subTreeQueue,
			HashMap<Node, ArrayList<QualitySteinerTree>> invertedRootSteinerTree,
			PriorityQueue<OptimalSteinerTree> resultQueue, int[] QoS) {
		// TODO Auto-generated method stub
		List<Node> adjacentNodes = adjIndex.get(firstOrder.getRoot());
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

						QualitySteinerTree copyofIntermediateTree = new QualitySteinerTree(intermediateTree.getRoot(),
								intermediateTree.getNodes(), intermediateTree.getKeywords(),
								intermediateTree.getNumberOfNodes(), intermediateTree.getQoS());

						if (numberOfNodesInfo.containsKey(intermediateTree)
								&& numberOfNodesInfo.get(intermediateTree).size() > 0) {

							boolean putIntermediateTree = false;
							ArrayList<QualitySteinerTree> neededToRemoveSteinerTree = new ArrayList<QualitySteinerTree>();
							for (QualitySteinerTree candidateTree : numberOfNodesInfo.get(intermediateTree).keySet()) {

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
						if (intermediateTree.minimumSteinerTree((ArrayList<String>) keywords)) {
							resultQueue.add(intermediateTree);
						}
					}
				}
			}
		}
	}

	public List<Long> checkResult(PriorityQueue<SteinerTree> resultQueue, OptimalSteinerTree firstOrder,
			long startTime) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean growNormalTree(OptimalSteinerTree firstOrder, HashMap<Node, ArrayList<Node>> adjIndex,
			HashMap<String, Integer> keywordsMapping, List<String> keywords,
			HashMap<OptimalSteinerTree, OptimalSteinerTree> numberOfNodesInfo,
			HashMap<OptimalSteinerTree, HashMap<Integer, OptimalSteinerTree>> removedSubTreeQueue,
			PriorityQueue<OptimalSteinerTree> subTreeQueue,
			HashMap<Node, ArrayList<OptimalSteinerTree>> invertedRootSteinerTree,
			PriorityQueue<OptimalSteinerTree> resultQueue) {
		// TODO Auto-generated method stub
		return false;
	}
}
