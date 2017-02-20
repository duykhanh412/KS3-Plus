package initialisepriorityqueue.implementation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;

import elements.Node;
import elements.QualitySteinerTree;
import elements.SteinerTree;
import interfaceclass.InitialisePriorityQueueBehavior;

public class InitialiseNormalPriorityQueue implements InitialisePriorityQueueBehavior<SteinerTree, QualitySteinerTree> {

	public boolean initialiseNormalPriorityQueueBehavior(List<String> keywords,
			HashMap<String, ArrayList<Node>> invertedIndexAPIName, PriorityQueue<SteinerTree> subTreeQueue,
			HashMap<String, Integer> keywordsMapping, HashMap<SteinerTree, SteinerTree> numberOfNodesInfo,
			HashMap<Node, ArrayList<SteinerTree>> invertedRootSteinerTree) {
		// TODO Auto-generated method stub
		for (int i = 0; i < keywords.size(); i++) {
			if (invertedIndexAPIName.containsKey(keywords.get(i))) {
				ArrayList<Node> listOfKeywordNode = invertedIndexAPIName.get(keywords.get(i));
				for (int j = 0; j < listOfKeywordNode.size(); j++) {
					HashMap<Node, Node> keywordsInput = new HashMap<Node, Node>();
					keywordsInput.put(new Node(listOfKeywordNode.get(j).ID, listOfKeywordNode.get(j).kw),
							new Node(listOfKeywordNode.get(j).ID, listOfKeywordNode.get(j).kw));
					Node root = new Node(listOfKeywordNode.get(j).ID, listOfKeywordNode.get(j).kw);
					SteinerTree initialTrees = new SteinerTree(root, new HashMap<Node, Node>(), keywordsInput, 1);
					subTreeQueue.add(initialTrees);
					numberOfNodesInfo.put(initialTrees, initialTrees);

					ArrayList<SteinerTree> steinerTrees = new ArrayList<SteinerTree>();
					steinerTrees.add(initialTrees);
					invertedRootSteinerTree.put(root, steinerTrees);
				}
				keywordsMapping.put(keywords.get(i), 1);
			} else {
				System.out.println("The graph does not contain the keyword " + keywords.get(i));
				return false;
			}
		}
		return true;
	}

	public boolean initialiseQualityPriorityQueueBehavior(List<String> keywords,
			HashMap<String, ArrayList<Node>> invertedIndexAPIName, PriorityQueue<SteinerTree> subTreeQueue,
			HashMap<String, Integer> keywordsMapping, int[] QoS,
			HashMap<SteinerTree, HashMap<QualitySteinerTree, QualitySteinerTree>> numberOfNodesInfo,
			HashMap<Node, ArrayList<QualitySteinerTree>> invertedRootSteinerTreeWithQuality) {
		// TODO Auto-generated method stub
		return false;
	}

}
