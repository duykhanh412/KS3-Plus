package simulation;

import java.io.File;
import java.util.ArrayList;

import tools.StdOut;
import elements.DataGraph;
import queries.NormalQuery;

public class InstancePW {
	
	DataGraph dg;
	QueryPW q;
	Setting setting;
	// public InstanceResult result;
	public InstanceResult oldResult;
	public InstanceResult newResult;

	public InstancePW(DataGraph dg, int mashupID, int numNodes, double edgeCoefficient, double pCoefficient,
			int numKeywords, int keywordDistance, String queryType, int numQoS, int diffCoeff) {
		this.setting = new Setting(numNodes, edgeCoefficient, pCoefficient, numKeywords, keywordDistance, queryType,
				numQoS, diffCoeff);
		// this.result = new InstanceResult(numNodes, edgeCoefficient,
		// pCoefficient, numKeywords, keywordDistance, queryType);
		this.newResult = new InstanceResult(numNodes, edgeCoefficient, pCoefficient, numKeywords, keywordDistance,
				queryType);
		this.oldResult = new InstanceResult(numNodes, edgeCoefficient, pCoefficient, numKeywords, keywordDistance,
				queryType);

		this.setting.numNodes = numNodes;
		this.setting.edgeCoefficient = edgeCoefficient;
		this.setting.pCoefficient = pCoefficient;
		this.setting.numKeywords = numKeywords;
		this.setting.keywordDistance = keywordDistance;
		this.setting.queryType = queryType;

		this.setting.numQoS = numQoS;
		this.setting.diffCoeff = diffCoeff;

		this.dg = dg;

		// StdOut.println(dg);
		// dg.printNodeKeywords();
		// dg.printNodeIndex();
		// dg.printEdgeIndex();
		// displayGraph(dg);

		q = new QueryPW(dg, this.setting, mashupID);
		// q.printKeywords();
	}

	// public InstanceResult run(){
	// //try to answer a normal query
	// if(Config.RUNNING_KS3_NORMAL) {
	// if(this.dg.answerNormalPW(this.q) != null) {
	// this.result.mashupID = this.q.mashupID;
	// this.result.numKeywordsInMashup = this.q.numKeywordsInMashup;
	//
	// this.result.isSuccessfulKS3Normal = true;
	// this.result.timeConsumptionSuccessfulKS3Normal =
	// q.timeConsumptionSuccessfulKS3Normal;
	// this.result.objectiveValueKS3Normal += q.objectiveValueKS3Normal;
	// this.result.isSuccessfulIndividualNormal =
	// q.isSuccessfulIndividualNormal;
	//
	// this.result.newSolution = q.newSolution;
	// } else{
	// this.result.isSuccessfulKS3Normal = false;
	// }
	// }
	// return this.result;
	// }
	public InstanceResult runOldAlgorithm(int mashupID) {
		// try to answer a normal query
		// ArrayList<String> keywords = this.dg.hm_mashups_apis.get(mashupID);
		// if (keywords.size() == 9 || keywords.size() == 7 || keywords.size()
		// == 6 || keywords.size() == 5) {
		if (Config.RUNNING_KS3_NORMAL) {
			if (this.dg.answerNormalPW(this.q) != null) {
				this.oldResult.mashupID = this.q.mashupID;
				this.oldResult.numKeywordsInMashup = this.q.numKeywordsInMashup;
//				if (mashupID == 7) {
//					System.out.println("test");
//				}

				this.oldResult.isSuccessfulKS3Normal = true;
				this.oldResult.timeConsumptionSuccessfulKS3Normal = q.timeConsumptionSuccessfulKS3Normal;
				this.oldResult.objectiveValueKS3Normal += q.objectiveValueKS3Normal;
				this.oldResult.isSuccessfulIndividualNormal = q.isSuccessfulIndividualNormal;

				this.oldResult.newSolution = q.newSolution;
			} else {
				this.oldResult.isSuccessfulKS3Normal = false;
			}
		}
		// }
		return this.oldResult;
	}

	public InstanceResult runNewAlgorithm(int mashupID) {
		ArrayList<String> keywords = this.q.keywords;
		// if (keywords.size() == 9 || keywords.size() == 7 || keywords.size()
		// == 6 || keywords.size() == 5) {
		NormalQuery nq = new NormalQuery();
		if (nq.normalQueryPW(keywords, this.dg.invertedIndex, this.dg.adjIndex)) {
			this.newResult.mashupID = mashupID;
			this.newResult.numKeywordsInMashup = this.q.numKeywordsInMashup;
//			if (this.q.numKeywordsInMashup == 10 && nq.timeConsumptionSuccessfulKS3Normal > 1500) {
//				System.out.println("test");
//			}

			this.newResult.isSuccessfulKS3Normal = true;
			this.newResult.timeConsumptionSuccessfulKS3Normal = nq.timeConsumptionSuccessfulKS3Normal;
			this.newResult.objectiveValueKS3Normal += nq.numberOfNodes;
			this.newResult.isSuccessfulIndividualNormal = q.isSuccessfulIndividualNormal;

			if (nq.numberOfNodes < this.q.numKeywordsInMashup) {
				this.newResult.newSolution = true;
			} else {
				this.newResult.newSolution = false;
			}
			dg.count++;
		} else {
			this.newResult.isSuccessfulKS3Normal = false;
		}
		// }
		return this.newResult;
	}

	public InstanceResult checkOriginalSBS(int mashupID) {
		ArrayList<String> keywords = this.dg.hm_mashups_apis.get(mashupID);
		if (keywords.size() >= 9) {
			NormalQuery nq = new NormalQuery();
			if (nq.checkOriginalSBS(keywords, this.dg.invertedIndex, this.dg.adjIndex)) {
				this.newResult.mashupID = mashupID;
				this.newResult.numKeywordsInMashup = this.q.numKeywordsInMashup;

				this.newResult.isSuccessfulKS3Normal = true;
				this.newResult.timeConsumptionSuccessfulKS3Normal = nq.timeConsumptionSuccessfulKS3Normal;
				this.newResult.objectiveValueKS3Normal += q.objectiveValueKS3Normal;
				this.newResult.isSuccessfulIndividualNormal = q.isSuccessfulIndividualNormal;

				dg.count++;
			} else {
				this.newResult.isSuccessfulKS3Normal = false;
			}
			// return this.newResult;
		}
		return this.newResult;
	}
}
