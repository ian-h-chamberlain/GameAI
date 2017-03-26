package ch.idsia.agents.controllers;

import java.util.HashMap;

public class QTable {

	HashMap<boolean[], float[]> table;
	float initialValue = 0.0f;
	int numActions;
	
	public QTable(int actions) {
		table = new HashMap<>();
		numActions = actions;
	}
	
	public float getQ(boolean[] state, boolean[] action) {
		initializeState(state);
		int index = getActionIndex(action);
		return table.get(state)[index];
	}
	
	public void setQ(boolean[] state, boolean[] action, float newValue) {
		initializeState(state);
		int index = getActionIndex(action);
		table.get(state)[index] = newValue;
	}
	
	public float getReward(boolean[] state) {
		// TODO evaluate state for reward
		return 0.0f;
	}
	
	void initializeState(boolean[] state) {
		// if we've seen the state before, do nothing
		if (table.containsKey(state)) {
			return;
		}

		// otherwise set all its action values
		table.put(state, new float[numActions]);
		for (int i=0; i<numActions; i++) {
			table.get(state)[i] = initialValue;
		}
	}
	
	// Helper function to form an int out of an action boolean array
	int getActionIndex(boolean[] action) {
		int result = 0;
		for (int i=0; i < action.length; i++) {
			if (action[i]) {
				result |= 1 << i;
			}
		}
		return result;
	}
	
}
