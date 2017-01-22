package elements;

import java.util.ArrayList;
import java.util.HashMap;

public class OptimalSteinerTree {
	private Node root;
	private HashMap<Node, Node> nodes;
	private int numberOfNodes;
	private HashMap<Node, Node> keywords;
	private HashMap<String, Integer> keywordsString;
	private int[] qualityConstraints;

	public OptimalSteinerTree(Node root, HashMap<Node, Node> nodes, HashMap<Node, Node> keywords,
			int newNumberOfNodes) {
		this.root = root;
		this.nodes = nodes;
		this.keywords = keywords;
		this.numberOfNodes = newNumberOfNodes;
		this.keywordsString = new HashMap<String, Integer>();
		for (Node keyword : keywords.keySet()) {
			this.keywordsString.put(keyword.kw, 1);
		}
	}

	public OptimalSteinerTree(Node root, HashMap<Node, Node> nodes, HashMap<Node, Node> keywords, int newNumberOfNodes,
			int numQoS) {
		this.root = root;
		this.nodes = nodes;
		this.keywords = keywords;
		this.numberOfNodes = newNumberOfNodes;
		this.keywordsString = new HashMap<String, Integer>();
		for (Node keyword : keywords.keySet()) {
			this.keywordsString.put(keyword.kw, 1);
		}
		this.qualityConstraints = new int[numQoS];
		for (int i = 0; i < this.qualityConstraints.length; i++) {
			this.qualityConstraints[i] = 0;
		}
	}

	public OptimalSteinerTree(Node root, HashMap<Node, Node> nodes, HashMap<Node, Node> keywords, int newNumberOfNodes,
			int[] QoS) {
		this.root = root;
		this.nodes = nodes;
		this.keywords = keywords;
		this.numberOfNodes = newNumberOfNodes;
		this.keywordsString = new HashMap<String, Integer>();
		for (Node keyword : keywords.keySet()) {
			this.keywordsString.put(keyword.kw, 1);
		}
		this.qualityConstraints = new int[QoS.length];
		for (int i = 0; i < this.qualityConstraints.length; i++) {
			this.qualityConstraints[i] = QoS[i];
		}
	}

	public OptimalSteinerTree() {

	}

	public void setRoot(Node root) {
		this.root = root;
	}

	public Node getRoot() {
		return this.root;
	}

	public void setNodes(HashMap<Node, Node> nodes) {
		this.nodes = nodes;
	}

	public HashMap<Node, Node> getNodes() {
		return this.nodes;
	}

	public void setKeywords(HashMap<Node, Node> keywords) {
		this.keywords = keywords;
		for (Node keyword : keywords.keySet()) {
			this.keywordsString.put(keyword.kw, 1);
		}
	}

	public HashMap<String, Integer> getKeywordsString() {
		return this.keywordsString;
	}

	public HashMap<Node, Node> getKeywords() {
		return this.keywords;
	}

	public void setNumberOfNodes() {
		this.numberOfNodes = this.nodes.size() + 1;
	}

	public int getNumberOfNodes() {
		return this.numberOfNodes;
	}

	public void setQoS(int[] addedQoS) {
		for (int i = 0; i < this.qualityConstraints.length; i++) {
			this.qualityConstraints[i] = this.qualityConstraints[i] + addedQoS[i];
		}
	}

	public void setQoSWithMergeTree(int[] addedQoS, int[] rootQoS) {
		for (int i = 0; i < this.qualityConstraints.length; i++) {
			this.qualityConstraints[i] = this.qualityConstraints[i] + addedQoS[i] - rootQoS[i];
		}
	}

	public int[] getQoS() {
		return this.qualityConstraints;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((keywords == null) ? 0 : keywords.hashCode());
		result = prime * result + ((root == null) ? 0 : root.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OptimalSteinerTree other = (OptimalSteinerTree) obj;
		if (keywords == null) {
			if (other.keywords != null)
				return false;
		} else if (!keywords.equals(other.keywords))
			return false;
		if (root == null) {
			if (other.root != null)
				return false;
		} else if (!root.equals(other.root))
			return false;
		return true;
	}

	// Check whether two sets of keywords are distinct
	public boolean checkKeywords(HashMap<Node, Node> keywords) {
		int test = 0;
		for (Node keyword : keywords.keySet()) {
			if (this.keywordsString.containsKey(keyword.kw) && !keyword.kw.equals(this.root.kw))
				test++;
		}
		if (test > 0)
			return false;
		else
			return true;
	}

	public boolean growTree(Node newRoot, HashMap<String, Integer> keywordsMapping) {
		if (this.keywordsString.containsKey(newRoot.kw)) {
			return false;
		}
		if (this.nodes.containsKey(newRoot.kw)) {
			return false;
		} else
			this.nodes.put(this.root, this.root);
		this.numberOfNodes++;
		this.root = null;
		this.root = new Node(newRoot.ID, newRoot.kw, newRoot.QoS);
		// for(int i = 0; i < this.root.QoS.length; i++){
		// System.out.println(newRoot.QoS[i]);
		// }
		// if(this.root.QoS == null){
		// System.out.println("Fuck");
		// }

		if (keywordsMapping.containsKey(newRoot.kw)) {
			this.keywords.put(this.root, this.root);
			this.keywordsString.put(this.root.kw, 1);
		}
		return true;
	}

	public boolean mergeTree(OptimalSteinerTree mergedTree) {

		if (!checkKeywords(mergedTree.getKeywords())) {
			return false;
		}

		// Merge two sets of nodes
		for (Node key : mergedTree.getNodes().keySet()) {
			if (!this.nodes.containsKey(key)) {
				this.nodes.put(key, mergedTree.getNodes().get(key));
			}
		}

		// Merge two sets of keywords
		for (Node keyword : mergedTree.getKeywords().keySet()) {
			// if (!this.keywords.containsKey(key)) {
			this.keywords.put(keyword, keyword);
			this.keywordsString.put(keyword.kw, 1);
			// }
		}
		this.numberOfNodes = this.nodes.size() + 1;
		return true;
	}

	public boolean mergeTreeWithQualityConstraints(OptimalSteinerTree mergedTree) {

		if (!checkKeywords(mergedTree.getKeywords())) {
			return false;
		}

		// Merge two sets of nodes
		for (Node key : mergedTree.getNodes().keySet()) {

			if (!this.nodes.containsKey(key)) {
				this.nodes.put(key, mergedTree.getNodes().get(key));
				for (int i = 0; i < this.qualityConstraints.length; i++) {
					this.qualityConstraints[i] = this.qualityConstraints[i] + mergedTree.getNodes().get(key).QoS[i];
				}
			}
		}

		// Merge two sets of keywords
		for (Node keyword : mergedTree.getKeywords().keySet()) {
			// if (!this.keywords.containsKey(key)) {
			this.keywords.put(keyword, keyword);
			this.keywordsString.put(keyword.kw, 1);
			// }
		}
		this.numberOfNodes = this.nodes.size() + 1;
		return true;
	}

	// Check if the current tree includes all the keywords input
	public boolean minimumSteinerTree(ArrayList<String> keywords) {
		int i = 0;
		int numberOfKeywords = keywords.size();
		for (int j = 0; j < keywords.size(); j++) {
			if (this.keywordsString.containsKey(keywords.get(j))) {
				i++;
			}
			if (i == numberOfKeywords)
				return true;
		}
		return false;
	}

	public boolean checkOriginalSBS(ArrayList<String> keywords) {
		int i = 0;
		int numberOfKeywords = keywords.size();
		for (int j = 0; j < keywords.size(); j++) {
			if (this.keywordsString.containsKey(keywords.get(j))) {
				i++;
			}
			if (i == numberOfKeywords && this.keywords.size() == this.numberOfNodes
					&& numberOfKeywords == this.keywords.size())
				return true;
		}
		return false;
	}
}
