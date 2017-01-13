package simulation;

public class Setting {
	public int numNodes;
	public double edgeCoefficient;
	public double pCoefficient;
	public int numKeywords;
	public int keywordDistance;
	
	public int numQoS;
	public int diffCoeff;
	public int qosConstraintLowerBound;
	public int qosConstraintUpperBound;
	
	public String queryType;
	
	public Setting(int numNodes, double edgeCoefficient, double pCoefficient, int numKeywords, int keywordDistance, String queryType, int numQoS, int diffCoeff) {
		this.numNodes = numNodes;
		this.edgeCoefficient = edgeCoefficient;
		this.pCoefficient = pCoefficient;
		this.numKeywords = numKeywords;
		this.keywordDistance = keywordDistance;
		this.queryType = queryType;
		
		this.numQoS = numQoS;
		this.diffCoeff = diffCoeff;
		
		this.qosConstraintLowerBound = (Config.QOS_LOWER_BOUND + this.diffCoeff) * this.numKeywords;
		this.qosConstraintUpperBound = Config.QOS_UPPER_BOUND * this.numKeywords;
	}
}
