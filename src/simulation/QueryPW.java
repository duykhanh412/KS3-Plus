package simulation;

import java.util.ArrayList;
import java.util.Random;

import elements.DataGraph;
import elements.Node;
import simulation.Config;
import simulation.Setting;
import tools.StdOut;

public class QueryPW {
	public String type;
	public Integer mashupID;
	public Integer numKeywordsInMashup;
	
	public boolean newSolution; //records whether a new mashup is found different from the programmableweb mashup
	
	public ArrayList<String> keywords;
	public DataGraph dg;
	
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
	
	public QueryPW(DataGraph dg, Setting setting, Integer mashupID) {  //For data graph dg, get a specific number of keywords, with each pair of keywords within a specific distance 
		this.keywords = new ArrayList<String>();
		this.dg = dg;
		this.mashupID = mashupID;
		this.numKeywordsInMashup= this.dg.hm_mashups_apis.get(mashupID).size();
		
		this.setting = setting;
		
		//get random keywords from 
		//this.keywords = this.getRandomKeywordsFromAMashup(this.setting.numKeywords, 0); //!!!
		this.keywords = this.getFirstAndLastKeywordsFromAMashup(mashupID);
	}
	
	private ArrayList<String> getRandomKeywordsFromAMashup(Integer numKeywords, Integer mashupID) {	//randomly get a specific number of keywords WITHOUT a specific keywordDistance
		ArrayList<String> keywords = null;
		if(numKeywords < 2) {
			StdOut.println("Only " + numKeywords + " are specified! Not valid!");
		} else {
			keywords = new ArrayList<String>();
			ArrayList<String> candidateKeywords = this.dg.hm_mashups_apis.get(mashupID);
			if(candidateKeywords == null) {
				StdOut.println("Cannot retrieve any keywords from Mashup #" + mashupID + "!");
			} else {
				String keyword;
				Random r = new Random();
				while(keywords.size() < numKeywords) {
					keyword = candidateKeywords.get(r.nextInt(candidateKeywords.size()));
					if(!keywords.contains(keyword)) {
						keywords.add(keyword);
					}
				}
			}
		}
		return keywords;
	}
	
	private ArrayList<String> getRandomKeywordsFromAMashup(Integer numKeywords, Integer mashupID, Integer keywordDistance) {	//randomly get a specific number of keywords WITH a specific keywordDistance
		ArrayList<String> keywords = null;
		if(numKeywords < 2) {
			StdOut.println("Only " + numKeywords + " are specified! Not valid!");
		} else if(keywordDistance > this.dg.hm_mashups_apis.get(mashupID).size()) {
			StdOut.println("Keyword distance is too large and not suitable for extracting keywords from mashup #" + mashupID + "!!!");
		} else {
			keywords = new ArrayList<String>();
			ArrayList<String> candidateKeywords = this.dg.hm_mashups_apis.get(mashupID);
			String keyword;
			keyword = candidateKeywords.get(0);
			int i = 1;
			while(keywords.size() < numKeywords) {
				if(i*keywordDistance < candidateKeywords.size()) {
					keyword = candidateKeywords.get(i*keywordDistance);
					if(!keywords.contains(keyword)) {
						keywords.add(keyword);
					}
				} else {
					StdOut.println("Index out of bounds when extracting keywords from mashup #" + mashupID + "!!!");
				}
				++i;
			}
		}
		return keywords;
	}
	
	private ArrayList<String> getFirstAndLastKeywordsFromAMashup(Integer mashupID) {
		ArrayList<String> keywords = new ArrayList<String>();
		if(this.dg.hm_mashups_apis.get(mashupID).size() < 2) {
			StdOut.println("A maximum of one API can be extracted from Mashup #" + mashupID);
		} else {
			keywords.add(this.dg.hm_mashups_apis.get(mashupID).get(0));
			keywords.add(this.dg.hm_mashups_apis.get(mashupID).get(this.dg.hm_mashups_apis.get(mashupID).size()-1));
		}
		
		return keywords;
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
