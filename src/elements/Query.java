package elements;

import java.util.ArrayList;
import java.util.Random;

import simulation.Config;
import simulation.Setting;
import tools.StdOut;

public class Query {
	public String type;
	
	public ArrayList<String> keywords;
	public int[] QoS;
	
	public boolean isSuccessfulIndividualNormal;
	public boolean isSuccessfulIndividualConstraint;
	public boolean isSuccessfulIndividualOptimal;
	
	public boolean isSuccessfulNormal;
	public boolean isSuccessfulConstraint;
	public boolean isSuccessfulOptimal;
	
	public double timeConsumptionIndividual = 0.0;
	
	public double timeConsumptionSuccessfulKS3Normal = 0.0;
	public double timeConsumptionSuccessfulKS3Constraint = 0.0;
	public double timeConsumptionSuccessfulKS3Optimal = 0.0;
	
	public double timeConsumptionFailedKS3Normal = 0.0;
	public double timeConsumptionFailedKS3Constraint = 0.0;
	public double timeConsumptionFailedKS3Optimal = 0.0;
	
	public double objectiveValueIndividual = 0;
	public int objectiveValueKS3Normal = 0;
	public int objectiveValueKS3Constraint = 0;
	public double objectiveValueKS3Optimal = 0;
	
	public Setting setting;
	
	public Query(DataGraph dg, Setting setting) {  //For data graph dg, get a specific number of keywords, with each pair of keywords within a specific distance 
		this.keywords = new ArrayList<String>();
		
		this.setting = setting;
		Random r = new Random();
		
		int counter = 0;
		
		String kw;
		Node node;
		int n;
		do {
			kw = dg.keywords.get(r.nextInt(dg.keywords.size()));	//generate the first keyword
			n = r.nextInt(dg.invertedIndex.get(kw).size());

			node = dg.invertedIndex.get(kw).get(n);	//from the inverted index randomly fetch a node containing the first keyword
			StdOut.println("Attempting to ensure that node selected here has at least one neibhbour ...");
			StdOut.println("First keyword selected: " + node.kw + "(node " + node.ID + ")");

		}
		while (dg.adjIndex.get(node).size()==0); //ensures the node selected here has at least one neighbour
		
		this.keywords.add(kw); //add the keyword into this query
		++counter;
		
		if(this.setting.keywordDistance !=0) { //if there is a limit on keywordDistance
			int loopCounter = 0;
			while(counter<this.setting.numKeywords) {
				++loopCounter;
				if(loopCounter > 20) {
					System.exit(-1);
				}
				node = dg.getRandomNeighbourWithinKSteps(node, this.setting.keywordDistance); //get the keyword of a neighbour
				if(this.keywords.indexOf(node.kw) == -1) {
					this.keywords.add(node.kw);
					StdOut.println("Selected keyword: " + node.kw + "(node "+ node.ID + ")");
					++counter;
				}
				StdOut.println("Attempting to get " + this.setting.numKeywords + " keywords within keyword distance ...");
			}
		} else {
			while(counter<this.setting.numKeywords) {
				do {
					kw = dg.keywords.get(r.nextInt(dg.keywords.size()));
					if(this.keywords.indexOf(kw) == -1) {
						n = r.nextInt(dg.invertedIndex.get(kw).size());
						node = dg.invertedIndex.get(kw).get(n);	//from the inverted index randomly fetch a node containing keyword kw
						StdOut.println("Attempting to get a node with at least one neibhbour ...");
					}
				}
				while (dg.adjIndex.get(node).size()==0); //ensures the node selected here has at least one neighbour
				this.keywords.add(kw); //add the keyword into this query
				++counter;
			}
		}
		
		//initialise the QoS constraints for a query
		this.QoS = new int[setting.numQoS];
		for(int p=0; p<this.setting.numQoS; ++p) {
			//QoS[p] = (r.nextInt(10)+Config.QOS_UPPER_BOUND-setting.diffCoeff)*(this.setting.numKeywords*this.setting.keywordDistance);
			QoS[p] = (r.nextInt(10)+Config.QOS_UPPER_BOUND-setting.diffCoeff)*(this.setting.numKeywords);
		}	

//		StdOut.println("Keywords to cover:");
//		for(int i=0; i<this.keywords.size(); ++i) {
//			StdOut.println(this.keywords.get(i));
//		}
	}
	
	public void printKeywords() {
		StdOut.println();
		StdOut.println(this.keywords.size() + " keywords in Query:");
		for(int i=0; i<this.keywords.size(); ++i) {
			StdOut.print(this.keywords.get(i));
			if(i != this.keywords.size()-1) {
				StdOut.print(", ");
			}
		}
		StdOut.println();
		StdOut.println();
	}
}
