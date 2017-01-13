package simulation;

import java.io.File;

import tools.StdOut;
import elements.DataGraph;

public class InstancePW {
	DataGraph dg;
	QueryPW q;
	Setting setting;
	public InstanceResult result;
	
	public InstancePW(DataGraph dg, int mashupID, int numNodes, double edgeCoefficient, double pCoefficient, int numKeywords, int keywordDistance, String queryType, int numQoS, int diffCoeff) {
		this.setting = new Setting(numNodes, edgeCoefficient, pCoefficient, numKeywords, keywordDistance, queryType, numQoS, diffCoeff);
		this.result = new InstanceResult(numNodes, edgeCoefficient, pCoefficient, numKeywords, keywordDistance, queryType);
		
		this.setting.numNodes = numNodes;
		this.setting.edgeCoefficient = edgeCoefficient;
		this.setting.pCoefficient = pCoefficient;
		this.setting.numKeywords = numKeywords;
		this.setting.keywordDistance = keywordDistance;
		this.setting.queryType = queryType;
		
		this.setting.numQoS = numQoS;
		this.setting.diffCoeff = diffCoeff;
		
		this.dg = dg;
		
//		StdOut.println(dg);
//		dg.printNodeKeywords();
//		dg.printNodeIndex();
//		dg.printEdgeIndex();
//		displayGraph(dg);
		
		q = new QueryPW(dg, this.setting, mashupID);
		//q.printKeywords();
	}
	
	public InstanceResult run(){
		//try to answer a normal query
		if(Config.RUNNING_KS3_NORMAL) {
			if(this.dg.answerNormalPW(this.q) != null) {
				this.result.mashupID = this.q.mashupID;
				this.result.numKeywordsInMashup = this.q.numKeywordsInMashup;
				
				this.result.isSuccessfulKS3Normal = true;
				this.result.timeConsumptionSuccessfulKS3Normal = q.timeConsumptionSuccessfulKS3Normal;
				this.result.objectiveValueKS3Normal += q.objectiveValueKS3Normal;
				this.result.isSuccessfulIndividualNormal = q.isSuccessfulIndividualNormal;
				
				this.result.newSolution = q.newSolution;
			} else{
				this.result.isSuccessfulKS3Normal = false;
			}
		}
 		return this.result;
	}
}
