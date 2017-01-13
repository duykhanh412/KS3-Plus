package simulation;

import java.util.ArrayList;

import queries.*;
import tools.Digraph;
import tools.DigraphGenerator;
import tools.StdOut;
import elements.DataGraph;
import elements.Query;

public class Instance {
	DataGraph dg;
	Query q;
	Setting setting;
//	public InstanceResult result;
	// The result for running the old algorithm
	public InstanceResult oldResult;
	// The result for running the new algorithm
	public InstanceResult newResult;
	public int numberOfNodesInOldAlgorithms = 0;
	public int numberOfNodesInNewAlgorithms = 0;

	public Instance(int numNodes, double edgeCoefficient, double pCoefficient,
			int numKeywords, int keywordDistance, String queryType, int numQoS,
			int diffCoeff) {
		this.setting = new Setting(numNodes, edgeCoefficient, pCoefficient,
				numKeywords, keywordDistance, queryType, numQoS, diffCoeff);
//		this.result = new InstanceResult(numNodes, edgeCoefficient,
//				pCoefficient, numKeywords, keywordDistance, queryType);
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

		// StdOut.println(dg);
		// dg.printNodeKeywords();
		// dg.printNodeIndex();
		// dg.printEdgeIndex();
		// displayGraph(dg);

		q = new Query(dg, this.setting);
		// q.printKeywords();
	}

	// public InstanceResult run(){
	// //try to answer a normal query
	// if(Config.RUNNING_KS3_NORMAL) {
	// if(this.dg.answerNormal(this.q) != null) {
	// this.result.isSuccessfulKS3Normal = true;
	// this.result.timeConsumptionSuccessfulKS3Normal =
	// q.timeConsumptionSuccessfulKS3Normal;
	// this.result.objectiveValueKS3Normal += q.objectiveValueKS3Normal;
	// this.result.isSuccessfulIndividualNormal =
	// q.isSuccessfulIndividualNormal;
	// } else{
	// this.result.isSuccessfulKS3Normal = false;
	// }
	// }
	// //try to answer a constraint query
	// if(Config.RUNNING_KS3_CONSTRAINT) {
	// if(this.dg.answerConstraint(this.q) != null) {
	// this.result.isSuccessfulKS3Constraint = true;
	// this.result.timeConsumptionSuccessfulKS3Constraint =
	// q.timeConsumptionSuccessfulKS3Constraint;
	// this.result.objectiveValueKS3Constraint += q.objectiveValueKS3Constraint;
	// this.result.isSuccessfulIndividualConstraint =
	// q.isSuccessfulIndividualConstraint;
	// } else{
	// this.result.isSuccessfulKS3Constraint = false;
	// }
	// }
	// //try to answer an optimal query
	// if(Config.RUNNING_KS3_OPTIMAL) {
	// if(this.dg.answerOptimal(this.q) != null) {
	// this.result.isSuccessfulKS3Optimal = true;
	// this.result.timeConsumptionSuccessfulKS3Optimal =
	// q.timeConsumptionSuccessfulKS3Optimal;
	// this.result.objectiveValueKS3Optimal += q.objectiveValueKS3Optimal;
	// this.result.isSuccessfulIndividualOptimal =
	// q.isSuccessfulIndividualOptimal;
	// } else{
	// this.result.isSuccessfulKS3Optimal = false;
	// }
	// }
	// return this.result;
	// }
	public InstanceResult runOldAlgorithm() {
		// try to answer a normal query
		if (Config.RUNNING_KS3_NORMAL) {
			if (this.dg.answerNormal(this.q) != null) {
				this.oldResult.isSuccessfulKS3Normal = true;
				this.oldResult.timeConsumptionSuccessfulKS3Normal = q.timeConsumptionSuccessfulKS3Normal;
				this.oldResult.objectiveValueKS3Normal += q.objectiveValueKS3Normal;
				this.oldResult.isSuccessfulIndividualNormal = q.isSuccessfulIndividualNormal;
//				this.numberOfNodesInOldAlgorithms = dg.numberOfNodes;
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

		// nq.NormalQuerySeriesB(keywords, this.dg.invertedIndex,
		// this.dg.adjIndex);
		if (Config.RUNNING_KS3_NORMAL) {
			NormalQuery nq = new NormalQuery();
			if (nq.normalQuerySeriesB(keywords, this.dg.invertedIndex,
					this.dg.undirectedAdjIndex,
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
			QualityConstraintsQuery QoSQ = new QualityConstraintsQuery();
			if (QoSQ.qualityConstraintsQuerySeriesB(keywords,
					this.dg.invertedIndex, this.dg.undirectedAdjIndex,
					this.q.QoS, this.oldResult.objectiveValueKS3Constraint)) {
				this.newResult.isSuccessfulKS3Constraint = true;
				this.newResult.timeConsumptionSuccessfulKS3Constraint = QoSQ.timeConsumptionSuccessfulKS3Constraint;
				this.newResult.objectiveValueKS3Constraint += q.objectiveValueKS3Constraint;
				this.newResult.isSuccessfulIndividualConstraint = q.isSuccessfulIndividualConstraint;
				this.numberOfNodesInNewAlgorithms = QoSQ.numberOfNodes;
			} else {
				this.newResult.isSuccessfulKS3Constraint = false;
			}
		}
		if (Config.RUNNING_KS3_OPTIMAL) {
			OptimalQuery oQ = new OptimalQuery();
			if (oQ.optimalQuerySeriesB(keywords, this.dg.invertedIndex,
					this.dg.undirectedAdjIndex, this.q.QoS,
					this.oldResult.objectiveValueKS3Optimal)) {
				this.newResult.isSuccessfulKS3Optimal = true;
				this.newResult.timeConsumptionSuccessfulKS3Optimal = oQ.timeConsumptionSuccessfulKS3Optimal;
				this.newResult.objectiveValueKS3Optimal += q.objectiveValueKS3Optimal;
				this.newResult.isSuccessfulIndividualOptimal = q.isSuccessfulIndividualOptimal;

			} else {
				this.newResult.isSuccessfulKS3Optimal = false;
			}
		}
		return this.newResult;
	}
}
