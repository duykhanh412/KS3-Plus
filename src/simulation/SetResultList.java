package simulation;

import java.util.ArrayList;

public class SetResultList extends ArrayList<SetResult> {
	public static ArrayList<String> getChangingFactors() {
		ArrayList<String> changingFactors = new ArrayList<String>();
		
		if(Config.MIN_NODES != Config.MAX_NODES) {
			changingFactors.add("N");
		}
		if(Config.MIN_EDGE_COEFFICIENT != Config.MAX_EDGE_COEFFICIENT) {
			changingFactors.add("EC");
		}
		if(Config.MIN_P_COEFFICIENT != Config.MAX_P_COEFFICIENT) {
			changingFactors.add("PC");
		}
		if(Config.MIN_KEYWORDS != Config.MAX_KEYWORDS) {
			changingFactors.add("K");
		}
		if(Config.MIN_KEYWORD_DISTANCE != Config.MAX_KEYWORD_DISTANCE) {
			changingFactors.add("KD");
		}
		
		return changingFactors;
	}
}
