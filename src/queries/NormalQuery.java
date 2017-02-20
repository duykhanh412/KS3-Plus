package queries;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;

import other.TreeCostComparator;
import elements.Node;
import elements.SteinerTree;
public class NormalQuery {
	public long timeConsumptionSuccessfulKS3Normal;
	public int numberOfNodes;

	public boolean normalQuerySeriesB(ArrayList<String> keywords, HashMap<String, ArrayList<Node>> invertedIndexAPIName,
			HashMap<Node, ArrayList<Node>> adjIndex, int testNumberOfNodes) {
//		System.out.println(keywords);

		System.out.println("NORMAL QUERY");
		
		Comparator<SteinerTree> comparator = new TreeCostComparator();

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

		// Put nodes containing keywords in the queue
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

		while (subTreeQueue.size() != 0) {

			SteinerTree firstOrder = subTreeQueue.remove();
			SteinerTree testRemove = numberOfNodesInfo.remove(firstOrder);
			
			if (removedSubTreeQueue.containsKey(firstOrder)) {
				if (removedSubTreeQueue.get(firstOrder).size() == 0) {
					removedSubTreeQueue.remove(firstOrder);
				} else if (removedSubTreeQueue.get(firstOrder).containsKey(firstOrder.getNumberOfNodes())) {
					removedSubTreeQueue.get(firstOrder).remove(firstOrder.getNumberOfNodes());
					continue;
				}
			}

			// Check if the current tree includes all the keywords then return
			// the result

			if (resultQueue.size() > 0) {
				SteinerTree firstResult = resultQueue.peek();
				if (firstOrder.getNumberOfNodes() >= firstResult.getNumberOfNodes()) {
					long endTime = System.currentTimeMillis();
//					System.out.println("The root of the result tree: " + firstResult.getRoot().ID + "-"
//							+ firstResult.getRoot().kw);
//					System.out.println("The nodes of the tree: ");
//					for (Node n : firstResult.getNodes().keySet()) {
//						System.out.println(firstResult.getNodes().get(n).ID + "-" + firstResult.getNodes().get(n).kw);
//					}
					this.timeConsumptionSuccessfulKS3Normal = endTime - startTime;
					this.numberOfNodes = firstResult.getNumberOfNodes();

//					System.out.println("The total computation time: " + (endTime - startTime));
//					if (this.numberOfNodes != testNumberOfNodes) {
//						System.out.println("One of the result is wrong");
//					}
					return true;
				}

			}
			
			ArrayList<Node> adjacentNodes = adjIndex.get(firstOrder.getRoot());

			// Grow tree
			for (int i = 0; i < adjacentNodes.size(); i++) {
				HashMap<Node, Node> copyNodes = new HashMap<Node, Node>();
				copyNodes.putAll(firstOrder.getNodes());
				HashMap<Node, Node> copyKeywords = new HashMap<Node, Node>();
				copyKeywords.putAll(firstOrder.getKeywords());

				SteinerTree intermediateTree = new SteinerTree(
						new Node(firstOrder.getRoot().ID, firstOrder.getRoot().kw), copyNodes, copyKeywords,
						firstOrder.getNumberOfNodes());

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
						if (intermediateTree.minimumSteinerTree(keywords)) {
							resultQueue.add(intermediateTree);
						}
					}
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
							new Node(elementsInSameRootTreeQueue.getRoot().ID,
									elementsInSameRootTreeQueue.getRoot().kw),
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
											removedSubTreeQueue.get(intermediateTreeMerge).put(
													intermediateTreeMerge.getNumberOfNodes(), intermediateTreeMerge);
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
										if((endBlock - startBlock) > 100){
											System.out.println("Computation time for the merge process block code is: " + (endBlock - startBlock));	
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
								if (intermediateTreeMerge.minimumSteinerTree(keywords)) {
									resultQueue.add(intermediateTreeMerge);
								}
							}
						}
					}
				}
			}
		}
		return false;
	}

	public boolean normalQueryPW(ArrayList<String> keywords, HashMap<String, ArrayList<Node>> invertedIndexAPIName,
			HashMap<Node, ArrayList<Node>> adjIndex, ArrayList<String> orignialSBS) {

		if (keywords.size() == 0) {
			System.out.println("The input keywords are nod valid");
			return false;
		}
		System.out.println(keywords);

		Comparator<SteinerTree> comparator = new TreeCostComparator();

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

//		for(int i = 0; i < orignialSBS.size(); i++){
//			keywordsMapping.put(orignialSBS.get(i), 1);
//		}
		// Put nodes containing keywords in the queue
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

		while (subTreeQueue.size() != 0) {

			SteinerTree firstOrder = subTreeQueue.remove();
			SteinerTree testRemove = numberOfNodesInfo.remove(firstOrder);
			
			if (removedSubTreeQueue.containsKey(firstOrder)) {
				if (removedSubTreeQueue.get(firstOrder).size() == 0) {
					removedSubTreeQueue.remove(firstOrder);
				} else if (removedSubTreeQueue.get(firstOrder).containsKey(firstOrder.getNumberOfNodes())) {
					removedSubTreeQueue.get(firstOrder).remove(firstOrder.getNumberOfNodes());
					continue;
				}
			}

			// Check if the current tree includes all the keywords then return
			// the result

			if (resultQueue.size() > 0) {
				SteinerTree firstResult = resultQueue.peek();
				if (firstOrder.getNumberOfNodes() >= firstResult.getNumberOfNodes()) {
					long endTime = System.currentTimeMillis();
//					System.out.println("The root of the result tree: " + firstResult.getRoot().ID + "-"
//							+ firstResult.getRoot().kw);
//					System.out.println("The nodes of the tree: ");
//					for (Node n : firstResult.getNodes().keySet()) {
//						System.out.println(firstResult.getNodes().get(n).ID + "-" + firstResult.getNodes().get(n).kw);
//					}
					this.timeConsumptionSuccessfulKS3Normal = endTime - startTime;
					this.numberOfNodes = firstResult.getNumberOfNodes();

//					System.out.println("The total computation time: " + (endTime - startTime));
//					if (this.numberOfNodes != testNumberOfNodes) {
//						System.out.println("One of the result is wrong");
//					}
					return true;
				}

			}
			
			ArrayList<Node> adjacentNodes = adjIndex.get(firstOrder.getRoot());

			// Grow tree
			for (int i = 0; i < adjacentNodes.size(); i++) {
				HashMap<Node, Node> copyNodes = new HashMap<Node, Node>();
				copyNodes.putAll(firstOrder.getNodes());
				HashMap<Node, Node> copyKeywords = new HashMap<Node, Node>();
				copyKeywords.putAll(firstOrder.getKeywords());

				SteinerTree intermediateTree = new SteinerTree(
						new Node(firstOrder.getRoot().ID, firstOrder.getRoot().kw), copyNodes, copyKeywords,
						firstOrder.getNumberOfNodes());

				if (intermediateTree.growTree(adjacentNodes.get(i), keywordsMapping)) {
//					if ((intermediateTree.getNumberOfNodes()
//							+ (keywords.size() - intermediateTree.getKeywords().size())) <= 2 * keywords.size()) {
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
						if (intermediateTree.minimumSteinerTree(keywords)) {
							resultQueue.add(intermediateTree);
						}
					}
//				}
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
							new Node(elementsInSameRootTreeQueue.getRoot().ID,
									elementsInSameRootTreeQueue.getRoot().kw),
							copyNodesForMergedTree, copyKeywordsForMergedTree,
							elementsInSameRootTreeQueue.getNumberOfNodes());

					if (!intermediateTreeMerge.equals(mergedTree)) {
						if (intermediateTreeMerge.mergeTree(mergedTree)) {
//							if ((intermediateTreeMerge.getNumberOfNodes()
//									+ (keywords.size() - intermediateTreeMerge.getKeywords().size())) <= 2
//											* keywords.size()) {
								if (numberOfNodesInfo.containsKey(intermediateTreeMerge)) {
									if (intermediateTreeMerge.getNumberOfNodes() < numberOfNodesInfo
											.get(intermediateTreeMerge).getNumberOfNodes()) {
										
										if (removedSubTreeQueue.containsKey(intermediateTreeMerge)) {
											removedSubTreeQueue.get(intermediateTreeMerge).put(
													intermediateTreeMerge.getNumberOfNodes(), intermediateTreeMerge);
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
										if((endBlock - startBlock) > 100){
											System.out.println("Computation time for the merge process block code is: " + (endBlock - startBlock));	
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
								if (intermediateTreeMerge.minimumSteinerTree(keywords)) {
									resultQueue.add(intermediateTreeMerge);
								}
							}
						}
					}
//				}
			}
		}
		return false;
	}

	public boolean checkOriginalSBS(ArrayList<String> keywords, HashMap<String, ArrayList<Node>> invertedIndexAPIName,
			HashMap<Node, ArrayList<Node>> adjIndex) {

		if (keywords.size() == 0) {
			System.out.println("The input keywords are nod valid");
			return false;
		}
		System.out.println(keywords);

		Comparator<SteinerTree> comparator = (Comparator<SteinerTree>) new TreeCostComparator();

		// The first tree taken from the priority queue
		// SteinerTree firstOrder = new SteinerTree();

		// The priority queue sorted in the increasing order of costs of trees
		PriorityQueue<SteinerTree> subTreeQueue = new PriorityQueue<SteinerTree>(keywords.size(), comparator);

		PriorityQueue<SteinerTree> resultQueue = new PriorityQueue<SteinerTree>(keywords.size(), comparator);
		
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

		while (subTreeQueue.size() != 0) {

			SteinerTree firstOrder = subTreeQueue.remove();
			SteinerTree testRemove = numberOfNodesInfo.remove(firstOrder);
			// invertedRootSteinerTree.get(firstOrder.getRoot()).remove(firstOrder);

			ArrayList<Node> adjacentNodes = adjIndex.get(firstOrder.getRoot());

			// Grow tree
			for (int i = 0; i < adjacentNodes.size(); i++) {
				HashMap<Node, Node> copyNodes = new HashMap<Node, Node>();
				copyNodes.putAll(firstOrder.getNodes());
				HashMap<Node, Node> copyKeywords = new HashMap<Node, Node>();
				copyKeywords.putAll(firstOrder.getKeywords());

				SteinerTree intermediateTree = new SteinerTree(
						new Node(firstOrder.getRoot().ID, firstOrder.getRoot().kw), copyNodes, copyKeywords,
						firstOrder.getNumberOfNodes());

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
								subTreeQueue.remove(test);
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
						if (intermediateTree.checkOriginalSBS(keywords)) {
							long endTime = System.currentTimeMillis();
							System.out.println("The root of the result tree: " + intermediateTree.getRoot().ID + "-"
									+ intermediateTree.getRoot().kw);
							System.out.println("The nodes of the tree: ");
							for (Node n : intermediateTree.getNodes().keySet()) {
								System.out.println(intermediateTree.getNodes().get(n).ID + "-" + intermediateTree.getNodes().get(n).kw);
							}
							this.timeConsumptionSuccessfulKS3Normal = endTime - startTime;
							this.numberOfNodes = intermediateTree.getNumberOfNodes();

							System.out.println("The total computation time: " + (endTime - startTime));
							return true;
						}
					}
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
							new Node(elementsInSameRootTreeQueue.getRoot().ID,
									elementsInSameRootTreeQueue.getRoot().kw),
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
										subTreeQueue.remove(intermediateTreeMerge);
										subTreeQueue.add(intermediateTreeMerge);

										numberOfNodesInfo.put(intermediateTreeMerge, intermediateTreeMerge);

										invertedRootSteinerTree.get(firstOrder.getRoot())
												.remove(numberOfNodesInfo.get(intermediateTreeMerge));
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
								if (intermediateTreeMerge.checkOriginalSBS(keywords)) {
									long endTime = System.currentTimeMillis();
									System.out.println("The root of the result tree: " + intermediateTreeMerge.getRoot().ID + "-"
											+ intermediateTreeMerge.getRoot().kw);
									System.out.println("The nodes of the tree: ");
									for (Node n : intermediateTreeMerge.getNodes().keySet()) {
										System.out.println(intermediateTreeMerge.getNodes().get(n).ID + "-" + intermediateTreeMerge.getNodes().get(n).kw);
									}
									this.timeConsumptionSuccessfulKS3Normal = endTime - startTime;
									this.numberOfNodes = intermediateTreeMerge.getNumberOfNodes();

									System.out.println("The total computation time: " + (endTime - startTime));
									return true;
								}
							}
						}
					}
				}
			}
		}
		return false;
	}
}
