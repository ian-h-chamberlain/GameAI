package ch.idsia.agents.controllers;

import java.util.HashMap;

public class QTable {

	HashMap<boolean[], Float> table;
	float initialValue = 0.0f;
	int numStates = 1;
	
	public QTable() {
		table = new HashMap<>();
	}
	
	public float getQ(boolean[] state) {
		if (table.containsKey(state)) {
			return table.get(state);
		}
		else {
			// TODO possibly add state to the table?
			return initialValue;
		}
	}
	
	public void setQ(boolean[] state, float newValue) {
		table.put(state, newValue);
	}
	
	public float getReward(boolean[] state) {
		// TODO evaluate state for reward
		return 0.0f;
	}
	
}
