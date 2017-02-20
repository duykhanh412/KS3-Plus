package query.implementation;

import elements.QualitySteinerTree;
import elements.SteinerTree;
import growtree.implementation.GrowNormalTreeBehavior;
import initialisepriorityqueue.implementation.InitialiseNormalPriorityQueue;
import interfaceclass.QueryType;
import mergetree.implementation.MergeNormalTreeBehavior;
import other.TreeCostComparator;

public class NormalQuery extends QueryType<SteinerTree, QualitySteinerTree>{
	public NormalQuery() {
		// TODO Auto-generated constructor stub
		initialisePriorityQueueBehavior = new InitialiseNormalPriorityQueue();
		growTreeBehavior = new GrowNormalTreeBehavior();
		mergeTreeBehavior = new MergeNormalTreeBehavior();
		comparator = new TreeCostComparator();
	}
}