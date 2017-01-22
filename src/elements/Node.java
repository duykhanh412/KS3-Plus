package elements;
import java.util.ArrayList;
import java.util.Random;

import simulation.Config;
import simulation.Setting;

public class Node {
	
	public Node(){
		
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ID;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Node other = (Node) obj;
		if (ID != other.ID)
			return false;
		return true;
	}

	public int ID;
	public String kw;
	ArrayList<Edge> edges;

	public Setting setting;

	public int[] QoS;

//	public double[] doubleQoS;

	public Node(int ID, String kw) {
		this.ID = ID;
		this.kw = kw;
	}

	public Node(int ID, String kw, int[] QoS) {
		this.ID = ID;
		this.kw = kw;
		this.QoS = new int[QoS.length];
		for(int i = 0; i < QoS.length; i++){
			this.QoS[i] = QoS[i];
		}
//		this.QoS = QoS;
	}

	public Node(String kw, Setting setting) {
		this.ID = -1;
		this.kw = kw;
		this.setting = setting;
		this.edges = new ArrayList<Edge>();

		QoS = new int[this.setting.numQoS];

		Random rand = new Random();
		for (int p = 0; p < setting.numQoS; ++p) {
			QoS[p] = rand.nextInt(Config.QOS_UPPER_BOUND - Config.QOS_LOWER_BOUND) + Config.QOS_LOWER_BOUND;
		}
	}
}
