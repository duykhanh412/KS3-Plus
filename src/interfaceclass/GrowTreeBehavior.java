package interfaceclass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;

import elements.Node;
import elements.OptimalSteinerTree;
import elements.QualitySteinerTree;
import elements.SteinerTree;

public interface GrowTreeBehavior<T,Q> {
	
	public List<Long> checkResult(PriorityQueue<SteinerTree> resultQueue, T firstOrder, long startTime);
	
	public boolean growNormalTree(T firstOrder, HashMap<Node, ArrayList<Node>> adjIndex,
			HashMap<String, Integer> keywordsMapping, List<String> keywords,
			HashMap<T, T> numberOfNodesInfo,
			HashMap<T, HashMap<Integer, T>> removedSubTreeQueue,
			PriorityQueue<T> subTreeQueue, HashMap<Node, ArrayList<T>> invertedRootSteinerTree,
			PriorityQueue<T> resultQueue);

	public void growQualityTree(T firstOrder, HashMap<Node, ArrayList<Node>> adjIndex,
			HashMap<String, Integer> keywordsMapping, List<String> keywords,
			HashMap<T, HashMap<Q, Q>> numberOfNodesInfo,
			HashMap<Q, Q> removedSubTreeQueue,
			PriorityQueue<T> subTreeQueue,
			HashMap<Node, ArrayList<Q>> invertedRootSteinerTree,
			PriorityQueue<T> resultQueue, int[] QoS);
}
