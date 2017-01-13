package simulation;

import java.util.ArrayList;

public class SetResult {
	public int numNodes;
	public double edgeCoefficient;
	public double pCoefficient;
	public int numKeywords;
	public int keywordDistance;
	public int numQoS;
	public int qdCoefficient;
	
	public int queryType;
	
	public boolean isSuccessful;
	public int numNodesSelected;
	public int numEdgesSelected;
	public double time;
	
	public ArrayList<InstanceResult> instancesResults;
	
	public int numberSuccessfulRunsIndividualNormal;
	public int numberSuccessfulRunsIndividualConstraint;
	public int numberSuccessfulRunsIndividualOptimal;
	
	public int numberSuccessfulRunsKS3Normal;
	public int numberSuccessfulRunsKS3Constraint;
	public int numberSuccessfulRunsKS3Optimal;
	
	public int numberFailedRunsIndividualNormal;
	public int numberFailedRunsIndividualConstraint;
	public int numberFailedRunsIndividualOptimal;
	
	public int numberFailedRunsKS3Normal;
	public int numberFailedRunsKS3Constraint;
	public int numberFailedRunsKS3Optimal;
	
	public double successRateIndividualNormal = 0.0;
	public double successRateIndividualConstraint = 0.0;
	public double successRateIndividualOptimal = 0.0;
	public double successRateKS3Normal = 0.0;
	public double successRateKS3Constraint = 0.0;
	public double successRateKS3Optimal = 0.0;
	
	//public double averageTimeKS3Normal = 0.0;
	//public double averageTimeKS3Constraint = 0.0;
	//public double averageTimeKS3Optimal = 0.0;
	
	public double totalTimeConsumptionSuccessfulRunsIndividualNormal = 0.0;
	public double totalTimeConsumptionSuccessfulRunsIndividualConstraint = 0.0;
	public double totalTimeConsumptionSuccessfulRunsIndividualOptimal = 0.0;
	
	public double totalTimeConsumptionSuccessfulRunsKS3Normal = 0.0;
	public double totalTimeConsumptionSuccessfulRunsKS3Constraint = 0.0;
	public double totalTimeConsumptionSuccessfulRunsKS3Optimal = 0.0;
	
	public double averageTimeConsumptionSuccessfulRunsIndividual = 0.0;
	public double averageTimeConsumptionSuccessfulRunsKS3Normal = 0.0;
	public double averageTimeConsumptionSuccessfulRunsKS3Constraint = 0.0;
	public double averageTimeConsumptionSuccessfulRunsKS3Optimal = 0.0;
	
	public double totalTimeConsumptionFailedRunsIndividualNormal = 0.0;
	public double totalTimeConsumptionFailedRunsIndividualConstraint = 0.0;
	public double totalTimeConsumptionFailedRunsIndividualOptimal = 0.0;
	public double totalTimeConsumptionFailedRunsKS3Normal = 0.0;
	public double totalTimeConsumptionFailedRunsKS3Constraint = 0.0;
	public double totalTimeConsumptionFailedRunsKS3Optimal = 0.0;
	
	public double averageTimeConsumptionFailedRunsIndividual = 0.0;
	public double averageTimeConsumptionFailedRunsKS3Normal = 0.0;
	public double averageTimeConsumptionFailedRunsKS3Constraint = 0.0;
	public double averageTimeConsumpitionFailedRunsKS3Optimal = 0.0;
	
	public double totalObjectiveValueIndividual = 0.0;
	public double totalObjectiveValueKS3Normal = 0.0;
	public double totalObjectiveValueKS3Constraint = 0.0;
	public double totalObjectiveValueKS3Optimal = 0.0;
	
	public double averageObjectiveValueKS3Normal = 0.0;
	public double averageObjectiveValueKS3Constraint = 0.0;
	public double averageObjectiveValueKS3Optimal = 0.0;
	
	public ArrayList<String> changingFactors;
	
	public SetResult(int numNodes, double edgeCoefficient, double pCoefficient, int numKeywords, int keywordDistance, int numQoS, int qdCoefficient) {
		this.numNodes = numNodes;
		this.edgeCoefficient = edgeCoefficient;
		this.pCoefficient = pCoefficient;
		this.numKeywords = numKeywords;
		this.keywordDistance = keywordDistance;
		this.numQoS = numQoS;
		this.qdCoefficient = qdCoefficient;
		
		this.instancesResults = new ArrayList<InstanceResult>();
	}
	
	public void add(InstanceResult ir) {
		this.instancesResults.add(ir);
		//individualNormal
		if(ir.isSuccessfulIndividualNormal) {
			++this.numberSuccessfulRunsIndividualNormal;
			this.totalTimeConsumptionSuccessfulRunsIndividualNormal += ir.timeConsumptionSuccessfulIndividualNormal;
		} else {
			++this.numberFailedRunsIndividualNormal;
			this.totalTimeConsumptionFailedRunsIndividualNormal += ir.timeConsumptionFailedIndividualNormal;
		}
		//individualConstraint
		if(ir.isSuccessfulIndividualConstraint) {
			++this.numberSuccessfulRunsIndividualConstraint;
			this.totalTimeConsumptionSuccessfulRunsIndividualConstraint += ir.timeConsumptionSuccessfulIndividualConstraint;
		} else {
			++this.numberFailedRunsIndividualConstraint;
			this.totalTimeConsumptionFailedRunsIndividualConstraint += ir.timeConsumptionFailedIndividualConstraint;
		}
		//individualOptimal
		if(ir.isSuccessfulIndividualOptimal) {
			++this.numberSuccessfulRunsIndividualOptimal;
			this.totalTimeConsumptionSuccessfulRunsIndividualOptimal += ir.timeConsumptionSuccessfulIndividualOptimal;
		} else {
			++this.numberFailedRunsIndividualOptimal;
			this.totalTimeConsumptionFailedRunsIndividualOptimal += ir.timeConsumptionFailedIndividualOptimal;
		}
		//normal
		if(ir.isSuccessfulKS3Normal) {
			++this.numberSuccessfulRunsKS3Normal;
			this.totalTimeConsumptionSuccessfulRunsKS3Normal += ir.timeConsumptionSuccessfulKS3Normal;
			this.totalObjectiveValueKS3Normal += ir.objectiveValueKS3Normal;
		} else {
			++this.numberFailedRunsKS3Normal;
			this.totalTimeConsumptionFailedRunsKS3Normal += ir.timeConsumptionFailedKS3Normal;
		}
		
		//constraint
		if(ir.isSuccessfulKS3Constraint) {
			++this.numberSuccessfulRunsKS3Constraint;
			this.totalTimeConsumptionSuccessfulRunsKS3Constraint += ir.timeConsumptionSuccessfulKS3Constraint;
			this.totalObjectiveValueKS3Constraint += ir.objectiveValueKS3Constraint;
		} else {
			++this.numberFailedRunsKS3Constraint;
			this.totalTimeConsumptionFailedRunsKS3Constraint += ir.timeConsumptionFailedKS3Constraint;
		}
		
		//optimal
		if(ir.isSuccessfulKS3Optimal) {
			++this.numberSuccessfulRunsKS3Optimal;
			this.totalTimeConsumptionSuccessfulRunsKS3Optimal += ir.timeConsumptionSuccessfulKS3Optimal;
			this.totalObjectiveValueKS3Optimal += ir.objectiveValueKS3Optimal;
		} else {
			++this.numberFailedRunsKS3Optimal;
			this.totalTimeConsumptionFailedRunsKS3Optimal += ir.timeConsumptionFailedKS3Optimal;
		}
	}
		
	public void averageResultsAll() {
			averageResultsKS3();
	}
		
	public void averageResultsKS3() {
		
		//average success rate
		this.successRateIndividualNormal = 1.0 * this.numberSuccessfulRunsIndividualNormal / Config.NUM_RUNS;
		this.successRateIndividualConstraint = 1.0 * this.numberSuccessfulRunsIndividualConstraint / Config.NUM_RUNS;
		this.successRateIndividualOptimal = 1.0 * this.numberSuccessfulRunsIndividualOptimal / Config.NUM_RUNS;
		this.successRateKS3Normal = 1.0 * this.numberSuccessfulRunsKS3Normal / Config.NUM_RUNS;
		this.successRateKS3Constraint = 1.0 * this.numberSuccessfulRunsKS3Constraint / Config.NUM_RUNS;
		this.successRateKS3Optimal = 1.0 * this.numberSuccessfulRunsKS3Optimal / Config.NUM_RUNS;

		//average time consumption
		this.averageTimeConsumptionSuccessfulRunsKS3Normal = this.totalTimeConsumptionSuccessfulRunsKS3Normal / this.numberSuccessfulRunsKS3Normal;
		this.averageTimeConsumptionSuccessfulRunsKS3Constraint = this.totalTimeConsumptionSuccessfulRunsKS3Constraint / this.numberSuccessfulRunsKS3Constraint;
		this.averageTimeConsumptionSuccessfulRunsKS3Optimal = this.totalTimeConsumptionSuccessfulRunsKS3Optimal / this.numberSuccessfulRunsKS3Optimal;
		
		//average objective value
		this.averageObjectiveValueKS3Normal= this.totalObjectiveValueKS3Normal/ this.numberSuccessfulRunsKS3Normal;
		this.averageObjectiveValueKS3Constraint= this.totalObjectiveValueKS3Constraint / this.numberSuccessfulRunsKS3Constraint;
		this.averageObjectiveValueKS3Optimal= this.totalObjectiveValueKS3Optimal / this.numberSuccessfulRunsKS3Optimal;
	}
}
