package other;

import java.util.Comparator;

import elements.SteinerTree;

public class TreeCostComparator implements Comparator<SteinerTree>{
	public int compare(SteinerTree arg0, SteinerTree arg1) {
		// TODO Auto-generated method stub
		if(arg0.getNumberOfNodes() < arg1.getNumberOfNodes())
			return -1;
		if(arg0.getNumberOfNodes() >= arg1.getNumberOfNodes())
			return 1;
		return 0;
	}
}
