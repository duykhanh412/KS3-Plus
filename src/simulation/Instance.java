package simulation;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import queries.*;
import tools.Digraph;
import tools.DigraphGenerator;
import tools.StdOut;
import elements.DataGraph;
import elements.Node;
import elements.Query;
import interfaceclass.QueryType;

public class Instance {
	DataGraph dg;
	Query q;
	Setting setting;
	// public InstanceResult result;
	// The result for running the old algorithm
	public InstanceResult oldResult;
	// The result for running the new algorithm
	public InstanceResult newResult;
	public int numberOfNodesInOldAlgorithms = 0;
	public int numberOfNodesInNewAlgorithms = 0;

	public Instance(int numNodes, double edgeCoefficient, double pCoefficient, int numKeywords, int keywordDistance,
			String queryType, int numQoS, int diffCoeff, int numRun) {
		this.setting = new Setting(numNodes, edgeCoefficient, pCoefficient, numKeywords, keywordDistance, queryType,
				numQoS, diffCoeff);
		// this.result = new InstanceResult(numNodes, edgeCoefficient,
		// pCoefficient, numKeywords, keywordDistance, queryType);
		this.oldResult = new InstanceResult(numNodes, edgeCoefficient, pCoefficient, numKeywords, keywordDistance,
				queryType);
		this.newResult = new InstanceResult(numNodes, edgeCoefficient, pCoefficient, numKeywords, keywordDistance,
				queryType);

		this.setting.numNodes = numNodes;
		this.setting.edgeCoefficient = edgeCoefficient;
		this.setting.pCoefficient = pCoefficient;
		this.setting.numKeywords = numKeywords;
		this.setting.keywordDistance = keywordDistance;
		this.setting.queryType = queryType;

		this.setting.numQoS = numQoS;
		this.setting.diffCoeff = diffCoeff;

		Digraph dig = DigraphGenerator.defaultDigraph(this.setting);
		dg = new DataGraph(dig, this.setting);
		System.out.println(
				"==============================Verify Number of Nodes and Edges==============================");
		System.out.println("Number of Edges: " + dg.nodeIndex.size());
		System.out.println("Number of Nodes: " + dg.edgeIndex.size());

		// Write the data graph to intermediate files
//		PrintWriter writer;
//		try {
//			writer = new PrintWriter(Config.pathToSecondSerieDatasets + "Numnode-" + numNodes + "-Numedge-"
//					+ edgeCoefficient + "-Numkeyword-" + numKeywords + "-Keyworddistance-" + keywordDistance
//					+ "-numRun-" + numRun + ".txt", "UTF-8");
//			for (Node n : dg.adjIndex.keySet()) {
//				writer.print(n.ID + "{" + n.kw);
//				writer.print(":");
//				String prefix = "";
//				for (int i = 0; i < dg.adjIndex.get(n).size(); i++) {
//					writer.print(prefix);
//					writer.print(dg.adjIndex.get(n).get(i).ID + "{" + dg.adjIndex.get(n).get(i).kw + "{");
//					String prefix2 = "";
//					for (int j = 0; j < dg.adjIndex.get(n).get(i).QoS.length; j++) {
//						writer.print(prefix2);
//						writer.print(dg.adjIndex.get(n).get(i).QoS[j]);
//						prefix2 = ";";
//					}
//					prefix = ",";
//				}
//				writer.println();
//			}
//			writer.close();
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		PrintWriter writer2;
//		try {
//			writer2 = new PrintWriter(Config.pathToSecondSerieDatasets + "Numnode-" + numNodes + "-Numedge-"
//					+ edgeCoefficient + "-Numkeyword-" + numKeywords + "-Keyworddistance-" + keywordDistance
//					+ "-Undirected-numRun-" + numRun + ".txt", "UTF-8");
//			for (Node n : dg.adjIndex.keySet()) {
//				writer2.print(n.ID + "{" + n.kw);
//				writer2.print(":");
//				String prefix = "";
//				for (int i = 0; i < dg.undirectedAdjIndex.get(n).size(); i++) {
//					writer2.print(prefix);
//					writer2.print(dg.undirectedAdjIndex.get(n).get(i).ID + "{" + dg.undirectedAdjIndex.get(n).get(i).kw
//							+ "{");
//					String prefix2 = "";
//					for (int j = 0; j < dg.undirectedAdjIndex.get(n).get(i).QoS.length; j++) {
//						writer2.print(prefix2);
//						writer2.print(dg.undirectedAdjIndex.get(n).get(i).QoS[j]);
//						prefix2 = ";";
//					}
//					prefix = ",";
//				}
//				writer2.println();
//			}
//			writer2.close();
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		PrintWriter writer1;
//		try {
//			writer1 = new PrintWriter(Config.pathToSecondSerieDatasets + "IntertedIndexAPI" + "-Numnode-" + numNodes + "-Numedge-"
//					+ edgeCoefficient + "-Numkeyword-" + numKeywords + "-Keyworddistance-" + keywordDistance
//					+ "-numRun-" + numRun + ".txt", "UTF-8");
//			for (String n : dg.invertedIndex.keySet()) {
//				writer1.print(n);
//				writer1.print(":");
//				String prefix = "";
//				ArrayList<Node> test = dg.invertedIndex.get(n);
//				for (int i = 0; i < test.size(); i++) {
//					writer1.print(prefix);
//					writer1.print(test.get(i).ID + "{" + test.get(i).kw + "{");
//					String prefix2 = "";
//					for (int j = 0; j < test.get(i).QoS.length; j++) {
//						writer1.print(prefix2);
//						writer1.print(test.get(i).QoS[j]);
//						prefix2 = ";";
//					}
//					prefix = ",";
//				}
//				writer1.println();
//			}
//			writer1.close();
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		q = new Query(dg, this.setting);
		// q.printKeywords();
	}

	public InstanceResult runOldAlgorithm() {
		// try to answer a normal query
		if (Config.RUNNING_KS3_NORMAL) {
			if (this.dg.answerNormal(this.q) != null) {
				this.oldResult.isSuccessfulKS3Normal = true;
				this.oldResult.timeConsumptionSuccessfulKS3Normal = q.timeConsumptionSuccessfulKS3Normal;
				this.oldResult.objectiveValueKS3Normal += q.objectiveValueKS3Normal;
				this.oldResult.isSuccessfulIndividualNormal = q.isSuccessfulIndividualNormal;
				// this.numberOfNodesInOldAlgorithms = dg.numberOfNodes;
			} else {
				this.oldResult.isSuccessfulKS3Normal = false;
			}
		}
		// try to answer a constraint query
		if (Config.RUNNING_KS3_CONSTRAINT) {
			if (this.dg.answerConstraint(this.q) != null) {
				this.oldResult.isSuccessfulKS3Constraint = true;
				this.oldResult.timeConsumptionSuccessfulKS3Constraint = q.timeConsumptionSuccessfulKS3Constraint;
				this.oldResult.objectiveValueKS3Constraint += q.objectiveValueKS3Constraint;
				this.oldResult.isSuccessfulIndividualConstraint = q.isSuccessfulIndividualConstraint;
			} else {
				this.oldResult.isSuccessfulKS3Constraint = false;
			}
		}
		// try to answer an optimal query
		if (Config.RUNNING_KS3_OPTIMAL) {
			if (this.dg.answerOptimal(this.q) != null) {
				this.oldResult.isSuccessfulKS3Optimal = true;
				this.oldResult.timeConsumptionSuccessfulKS3Optimal = q.timeConsumptionSuccessfulKS3Optimal;
				this.oldResult.objectiveValueKS3Optimal += q.objectiveValueKS3Optimal;
				this.oldResult.isSuccessfulIndividualOptimal = q.isSuccessfulIndividualOptimal;
			} else {
				this.oldResult.isSuccessfulKS3Optimal = false;
			}
		}
		return this.oldResult;
	}

	public InstanceResult runNewAlgorithm() {
		ArrayList<String> keywords = this.q.keywords;
		if (Config.RUNNING_KS3_NORMAL) {
			NormalQuery nq = new NormalQuery();
			if (nq.normalQuerySeriesB(keywords, this.dg.invertedIndex, this.dg.undirectedAdjIndex,
					this.oldResult.objectiveValueKS3Normal)) {
				this.newResult.isSuccessfulKS3Normal = true;
				this.newResult.timeConsumptionSuccessfulKS3Normal = nq.timeConsumptionSuccessfulKS3Normal;
				this.newResult.objectiveValueKS3Normal += nq.numberOfNodes;
				this.newResult.isSuccessfulIndividualNormal = q.isSuccessfulIndividualNormal;
				this.numberOfNodesInNewAlgorithms = nq.numberOfNodes;
			} else {
				this.newResult.isSuccessfulKS3Normal = false;
			}
		}
		if (Config.RUNNING_KS3_CONSTRAINT) {
			QualityConstraintsQuery_NewVersion QoSQ = new QualityConstraintsQuery_NewVersion();
			if (QoSQ.qualityConstraintsQuerySeriesB(keywords, this.dg.invertedIndex, this.dg.undirectedAdjIndex,
					this.q.QoS, this.oldResult.objectiveValueKS3Constraint)) {
				this.newResult.isSuccessfulKS3Constraint = true;
				this.newResult.timeConsumptionSuccessfulKS3Constraint = QoSQ.timeConsumptionSuccessfulKS3Constraint;
				this.newResult.objectiveValueKS3Constraint += QoSQ.numberOfNodes;
				this.newResult.isSuccessfulIndividualConstraint = QoSQ.isSuccessfulIndividualConstraint;
				this.numberOfNodesInNewAlgorithms = QoSQ.numberOfNodes;
			} else {
				this.newResult.isSuccessfulKS3Constraint = false;
			}
		}
		if (Config.RUNNING_KS3_OPTIMAL) {
			OptimalQuery oQ = new OptimalQuery();
			if (oQ.optimalQuerySeriesB(keywords, this.dg.invertedIndex, this.dg.undirectedAdjIndex, this.q.QoS,
					this.oldResult.objectiveValueKS3Optimal)) {
				this.newResult.isSuccessfulKS3Optimal = true;
				this.newResult.timeConsumptionSuccessfulKS3Optimal = oQ.timeConsumptionSuccessfulKS3Optimal;
				this.newResult.objectiveValueKS3Optimal += oQ.optimalQualityValue;
				this.newResult.isSuccessfulIndividualOptimal = oQ.isSuccessfulIndividualOptimal;

			} else {
				this.newResult.isSuccessfulKS3Optimal = false;
			}
		}
		return this.newResult;
	}
}
