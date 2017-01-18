package other;

import java.util.Comparator;

import elements.OptimalSteinerTree;

public class OptimalQueueComparator implements Comparator<OptimalSteinerTree>{
	public int compare(OptimalSteinerTree arg0, OptimalSteinerTree arg1) {
		// TODO Auto-generated method stub
		if (arg0.getQoS()[0] < arg1.getQoS()[0])
			return -1;
		if (arg0.getQoS()[0] >= arg1.getQoS()[0])
			return 1;
		return 0;
	}
}
