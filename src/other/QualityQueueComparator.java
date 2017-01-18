package other;

import java.util.Comparator;

import elements.OptimalSteinerTree;;

public class QualityQueueComparator implements Comparator<OptimalSteinerTree>{

	public int compare(OptimalSteinerTree arg0, OptimalSteinerTree arg1) {
		// TODO Auto-generated method stub
		if(arg0.getNumberOfNodes() < arg1.getNumberOfNodes())
			return -1;
		if(arg0.getNumberOfNodes() >= arg1.getNumberOfNodes())
			return 1;
		return 0;
	}

}
