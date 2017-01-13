package other;

import java.util.Comparator;

import elements.SteinerTree;

public class OptimalQueueComparator implements Comparator<SteinerTree>{
	public int compare(SteinerTree arg0, SteinerTree arg1) {
		// TODO Auto-generated method stub
		if (arg0.getQoS()[0] < arg1.getQoS()[0])
			return -1;
		if (arg0.getQoS()[0] >= arg1.getQoS()[0])
			return 1;
		return 0;
	}
}
