package interfaceclass;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;

import elements.Node;
import elements.OptimalSteinerTree;
import elements.QualitySteinerTree;
import elements.SteinerTree;
import other.TreeCostComparator;

public abstract class QueryType<T, Q> {
	protected InitialisePriorityQueueBehavior initialisePriorityQueueBehavior;
	protected GrowTreeBehavior growTreeBehavior;
	protected MergeTreeBehavior mergeTreeBehavior;
	protected int queryType;
	protected long timeConsumptionSuccessfulKS3;
	protected long numberOfNodes;
	protected Comparator<T> comparator;
	
	public long getTimeConsumptionSuccessfulKS3() {
		return timeConsumptionSuccessfulKS3;
	}

	public void setTimeConsumptionSuccessfulKS3(long timeConsumptionSuccessfulKS3) {
		this.timeConsumptionSuccessfulKS3 = timeConsumptionSuccessfulKS3;
	}

	public long getNumberOfNodes() {
		return numberOfNodes;
	}

	public void setNumberOfNodes(long numberOfNodes) {
		this.numberOfNodes = numberOfNodes;
	}


	public GrowTreeBehavior getGrowTreeBehavior() {
		return growTreeBehavior;
	}

	public void setGrowTreeBehavior(GrowTreeBehavior growTreeBehavior) {
		this.growTreeBehavior = growTreeBehavior;
	}

	public MergeTreeBehavior getMergeTreeBehavior() {
		return mergeTreeBehavior;
	}

	public void setMergeTreeBehavior(MergeTreeBehavior mergeTreeBehavior) {
		this.mergeTreeBehavior = mergeTreeBehavior;
	}

	public int getQueryType() {
		return queryType;
	}

	public void setQueryType(int queryType) {
		this.queryType = queryType;
	}

	public InitialisePriorityQueueBehavior getInitialiseNormalPriorityQueueBehavior() {
		return initialisePriorityQueueBehavior;
	}

	public void setInitialiseNormalPriorityQueueBehavior(
			InitialisePriorityQueueBehavior initialisePriorityQueueBehavior) {
		this.initialisePriorityQueueBehavior = initialisePriorityQueueBehavior;
	}

	public boolean performInitialiseNormalPriorityQueue(List<String> keywords,
			HashMap<String, ArrayList<Node>> invertedIndexAPIName, PriorityQueue<T> subTreeQueue,
			HashMap<String, Integer> keywordsMapping, HashMap<T, T> numberOfNodesInfo,
			HashMap<Node, ArrayList<T>> invertedRootSteinerTree) {
		return initialisePriorityQueueBehavior.initialiseNormalPriorityQueueBehavior(keywords, invertedIndexAPIName,
				subTreeQueue, keywordsMapping, numberOfNodesInfo, invertedRootSteinerTree);
	}

	public boolean performInitialiseQualityPriorityQueue(ArrayList<String> keywords,
			HashMap<String, ArrayList<Node>> invertedIndexAPIName, PriorityQueue<T> subTreeQueue,
			HashMap<String, Integer> keywordsMapping, int[] QoS,
			HashMap<T, HashMap<Q, Q>> numberOfNodesInfo,
			HashMap<Node, ArrayList<Q>> invertedRootSteinerTreeWithQuality) {
		return initialisePriorityQueueBehavior.initialiseQualityPriorityQueueBehavior(keywords, invertedIndexAPIName,
				subTreeQueue, keywordsMapping, QoS, numberOfNodesInfo, invertedRootSteinerTreeWithQuality);
	}

	public boolean performGrowNormalTree(T firstOrder, HashMap<Node, ArrayList<Node>> adjIndex,
			HashMap<String, Integer> keywordsMapping, List<String> keywords,
			HashMap<T, T> numberOfNodesInfo,
			HashMap<T, HashMap<Integer, T>> removedSubTreeQueue,
			PriorityQueue<T> subTreeQueue, HashMap<Node, ArrayList<T>> invertedRootSteinerTree,
			PriorityQueue<T> resultQueue) {
		return growTreeBehavior.growNormalTree(firstOrder, adjIndex, keywordsMapping, keywords, numberOfNodesInfo,
				removedSubTreeQueue, subTreeQueue, invertedRootSteinerTree, resultQueue);
	}

	public void performMergeNormalTree(T firstOrder, HashMap<Node, ArrayList<Node>> adjIndex,
			HashMap<String, Integer> keywordsMapping, List<String> keywords,
			HashMap<T, T> numberOfNodesInfo,
			HashMap<T, HashMap<Integer, T>> removedSubTreeQueue,
			PriorityQueue<T> subTreeQueue, HashMap<Node, ArrayList<T>> invertedRootSteinerTree,
			PriorityQueue<T> resultQueue) {
		mergeTreeBehavior.mergeNormalTree(firstOrder, adjIndex, keywordsMapping, keywords, numberOfNodesInfo,
				removedSubTreeQueue, subTreeQueue, invertedRootSteinerTree, resultQueue);
	}

	public void performGrowQualityTree(T firstOrder, HashMap<Node, ArrayList<Node>> adjIndex,
			HashMap<String, Integer> keywordsMapping, List<String> keywords,
			HashMap<T, HashMap<Q, Q>> numberOfNodesInfo,
			HashMap<Q, Q> removedSubTreeQueue,
			PriorityQueue<T> subTreeQueue,
			HashMap<Node, ArrayList<Q>> invertedRootSteinerTree,
			PriorityQueue<T> resultQueue, int[] QoS) {
		growTreeBehavior.growQualityTree(firstOrder, adjIndex, keywordsMapping, keywords, numberOfNodesInfo,
				removedSubTreeQueue, subTreeQueue, invertedRootSteinerTree, resultQueue, QoS);
	}

	public void performMergeQualityTree(T firstOrder, List<Node> adjacentNodes,
			HashMap<String, Integer> keywordsMapping, List<String> keywords,
			HashMap<T, HashMap<Q, Q>> numberOfNodesInfo,
			HashMap<Q, Q> removedSubTreeQueue,
			PriorityQueue<T> subTreeQueue,
			HashMap<Node, ArrayList<Q>> invertedRootSteinerTree,
			PriorityQueue<T> resultQueue, int[] QoS) {
		mergeTreeBehavior.mergeQualityTree(firstOrder, adjacentNodes, keywordsMapping, keywords, numberOfNodesInfo,
				removedSubTreeQueue, subTreeQueue, invertedRootSteinerTree, resultQueue, QoS);
	}

	public boolean queryWithQuality() {

		return false;
	}

	public boolean queryWithoutQuality(List<String> keywords, HashMap<String, ArrayList<Node>> invertedIndexAPIName,
			HashMap<Node, ArrayList<Node>> adjIndex) {
		System.out.println("NORMAL QUERY");

		// The priority queue sorted in the increasing order of costs of trees
		PriorityQueue<T> subTreeQueue = new PriorityQueue<T>(10, comparator);

		PriorityQueue<T> resultQueue = new PriorityQueue<T>(10, comparator);

		HashMap<T, HashMap<Integer, T>> removedSubTreeQueue = new HashMap<T, HashMap<Integer, T>>();

		// Intermediate hash map to save the information of number of nodes in a
		// tree
		HashMap<T, T> numberOfNodesInfo = new HashMap<T, T>();

		HashMap<String, Integer> keywordsMapping = new HashMap<String, Integer>();

		HashMap<Node, ArrayList<T>> invertedRootSteinerTree = new HashMap<Node, ArrayList<T>>();

		long startTime = System.currentTimeMillis();

		if (performInitialiseNormalPriorityQueue(keywords, invertedIndexAPIName, subTreeQueue, keywordsMapping,
				numberOfNodesInfo, invertedRootSteinerTree)) {
			while (subTreeQueue.size() != 0) {
				T firstOrder = subTreeQueue.remove();
				
				List<Long> result = growTreeBehavior.checkResult(resultQueue, firstOrder, startTime);
				if (result != null) {
					this.timeConsumptionSuccessfulKS3 = result.get(0);
					this.numberOfNodes = result.get(1);
					return true;
				}
				
				if (!performGrowNormalTree(firstOrder, adjIndex, keywordsMapping, keywords, numberOfNodesInfo,
						removedSubTreeQueue, subTreeQueue, invertedRootSteinerTree, resultQueue)){
					continue;
				}
				performMergeNormalTree(firstOrder, adjIndex, keywordsMapping, keywords, numberOfNodesInfo,
						removedSubTreeQueue, subTreeQueue, invertedRootSteinerTree, resultQueue);
			}
		}
		return false;
	}
}
