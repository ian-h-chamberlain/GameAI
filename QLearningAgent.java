package ch.idsia.agents.controllers;

import ch.idsia.agents.Agent;

public class QLearningAgent extends BasicMarioAIAgent implements Agent {
	
	static QTable table;
	int stateSize = 19;
	boolean isStuck;
	int framesSinceMoved = 0;

	public QLearningAgent() {
		super("QLearningAgent");
		reset();
	}
	
	public static void setQTable(QTable t) {
		table = t;
	}
	
	public void reset() {
		
	}
	
	boolean[] getState() {
		boolean[] state = new boolean[stateSize];
		
		// state[0] is stuck?
		
		// state[1] is on the ground
		
		// state[2-4] detect small, medium, and large gaps in the level
		
		// state[5-8] are for obstacles directly in front of mario
		
		// state[9-18] are for a 3x3 grid of enemy detection around mario
		
		// POSSIBLE IDEAS
		// state[19-21] for platforms above Mario
		
		
		return state;
	}
}
