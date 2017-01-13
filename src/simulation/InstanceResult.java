package simulation;

public class InstanceResult {
	public boolean newSolution;
	
	public int numNodes;
	public double edgeCoefficient;
	public double pCoefficient;
	public int numKeywords;
	public int keywordDistance;
	
	public int mashupID;
	public int numKeywordsInMashup;
	
	public String queryType;
	
	public boolean isSuccessfulIndividualNormal;
	public boolean isSuccessfulIndividualConstraint;
	public boolean isSuccessfulIndividualOptimal;
	public boolean isSuccessfulKS3Normal;
	public boolean isSuccessfulKS3Constraint;
	public boolean isSuccessfulKS3Optimal;
	
	public double timeConsumptionSuccessfulIndividualNormal;
	public double timeConsumptionSuccessfulIndividualConstraint;
	public double timeConsumptionSuccessfulIndividualOptimal;
	public double timeConsumptionSuccessfulKS3Normal;
	public double timeConsumptionSuccessfulKS3Constraint;
	public double timeConsumptionSuccessfulKS3Optimal;
	
	public double timeConsumptionFailedIndividualNormal;
	public double timeConsumptionFailedIndividualConstraint;
	public double timeConsumptionFailedIndividualOptimal;
	public double timeConsumptionFailedKS3Normal;
	public double timeConsumptionFailedKS3Constraint;
	public double timeConsumptionFailedKS3Optimal;
	
	public int objectiveValueKS3Normal;
	public int objectiveValueKS3Constraint;
	public double objectiveValueKS3Optimal;
	
	public InstanceResult(int numNodes, double edgeCoefficient, double pCoefficient, int numKeywords, int keywordDistance, String queryType) {
		this.numNodes = numNodes;
		this.edgeCoefficient = edgeCoefficient;
		this.pCoefficient = pCoefficient;
		this.numKeywords = numKeywords;
		this.keywordDistance = keywordDistance;
		
		this.queryType = queryType;
	}
}
