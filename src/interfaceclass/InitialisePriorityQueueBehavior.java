package interfaceclass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;

import elements.Node;

public interface InitialisePriorityQueueBehavior<T, Q> {
	public boolean initialiseNormalPriorityQueueBehavior(List<String> keywords,
			HashMap<String, ArrayList<Node>> invertedIndexAPIName, PriorityQueue<T> subTreeQueue,
			HashMap<String, Integer> keywordsMapping, HashMap<T, T> numberOfNodesInfo,
			HashMap<Node, ArrayList<T>> invertedRootSteinerTree);

	public boolean initialiseQualityPriorityQueueBehavior(List<String> keywords,
			HashMap<String, ArrayList<Node>> invertedIndexAPIName, PriorityQueue<T> subTreeQueue,
			HashMap<String, Integer> keywordsMapping, int[] QoS, HashMap<T, HashMap<Q, Q>> numberOfNodesInfo,
			HashMap<Node, ArrayList<Q>> invertedRootSteinerTreeWithQuality);
}