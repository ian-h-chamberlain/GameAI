package ch.idsia.agents.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class QTable {

	HashMap<Long, float[]> table;
	float initialValue = 0.0f;
	int numActions;
	
	public QTable(int actions) {
		table = new HashMap<>();
		numActions = actions;
	}
	
	public float getQ(boolean[] state, boolean[] action) {
		initializeState(state);
		int index = (int) longFromBoolArray(action);
		
		long stateID = longFromBoolArray(state);
		return table.get(stateID)[index];
	}
	
	public boolean[] maxQAction(boolean[] state) {
		initializeState(state);
		
		long stateID = longFromBoolArray(state);
		float[] values = table.get(stateID);
		
		// find the max q-value
		ArrayList<Integer> possibleActions = new ArrayList<>();

		int maxIndex = 0;
		for (int i=0; i<values.length; i++) {
			if (values[i] > values[maxIndex]) {
				maxIndex = i;
				possibleActions.clear();
			}

			if (values[i] == values[maxIndex]) {
				possibleActions.add(i);
			}
		}
		
		Random rn = new Random(); 
		
		int index = possibleActions.get(rn.nextInt(possibleActions.size()));
		
		// and return the action set that results in that value
		return boolArrayFromLong(maxIndex);
	}
	
	public void setQ(boolean[] state, boolean[] action, float newValue) {
		initializeState(state);
		int index = (int) longFromBoolArray(action);
		long stateID = longFromBoolArray(state);
		table.get(stateID)[index] = newValue;
	}
	
	void initializeState(boolean[] state) {
		// if we've seen the state before, do nothing
		long stateID = longFromBoolArray(state);
		if (table.containsKey(stateID)) {
			return;
		}
		
		boolean[] possibleActions = new boolean[numActions];
		for (int i=0; i<numActions; i++) {
			possibleActions[i] = true;
		}
		
		int totalActions = (int) longFromBoolArray(possibleActions);

		// otherwise set all its action values
		table.put(stateID, new float[totalActions]);
		for (int i=0; i<numActions; i++) {
			table.get(stateID)[i] = initialValue;
		}
	}
	
	public int size() {
		return table.size();
	}
	
	// Helper function to form an int out of an action boolean array
	long longFromBoolArray(boolean[] action) {
		long result = 0;
		for (int i=0; i < action.length; i++) {
			if (action[i]) {
				result |= 1 << i;
			}
		}
		return result;
	}
	
	// and a helper function for the reverse operation
	boolean[] boolArrayFromLong(long index) {
		boolean[] result = new boolean[numActions];
		for (int i=0; i<numActions; i++) {
			result[i] = ((index >> i) % 2 == 1);
		}
		
		return result;
	}
}