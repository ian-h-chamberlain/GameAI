package ch.idsia.agents.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class QTable {

	HashMap<Long, float[]> table;
	float initialValue = 0.0f;
	static int numActions;
	
	public QTable(int actions) {
		table = new HashMap<>();
		numActions = actions;
	}
	
	public float getQ(long state, boolean[] action) {
		int index = (int) longFromBoolArray(action);
		
		// long stateID = longFromBoolArray(state);
		initializeState(state);
		return table.get(state)[index];
	}
	
	public boolean[] maxQAction(long state) {
		initializeState(state);
		
		// long stateID = longFromBoolArray(state);
		float[] values = table.get(state);
		
		// find the max q-value
		ArrayList<Integer> possibleActions = new ArrayList<>();

		int maxIndex = 0;
		for (int i=0; i<values.length; i++) {
			if (values[i] > values[maxIndex]) {
				maxIndex = i;
			}
		}
		
		// and return the action set that results in that value
		return boolArrayFromLong(maxIndex);
	}
	
	public void setQ(long state, boolean[] action, float newValue) {
		// long stateID = longFromBoolArray(state);
		initializeState(state);
		int index = (int) longFromBoolArray(action);
		table.get(state)[index] = newValue;
	}
	
	void initializeState(long state) {
		// if we've seen the state before, do nothing
		if (table.containsKey(state)) {
			return;
		}
		
		int totalActions = (int) Math.pow(2, numActions);

		// otherwise set all its action values
		table.put(state, new float[totalActions]);
		for (int i=0; i<totalActions; i++) {
			table.get(state)[i] = initialValue;
		}
	}
	
	public int size() {
		return table.size();
	}
	
	// Helper function to form an int out of an action boolean array
	public static long longFromBoolArray(boolean[] action) {
		long result = 0;
		for (int i=0; i < action.length; i++) {
			if (action[i]) {
				result |= 1 << i;
			}
		}
		return result;
	}
	
	// and a helper function for the reverse operation
	public static boolean[] boolArrayFromLong(long index) {
		boolean[] result = new boolean[numActions];
		for (int i=0; i<numActions; i++) {
			result[i] = ((index >> i) % 2 == 1);
		}
		
		return result;
	}
}