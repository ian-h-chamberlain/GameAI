package ch.idsia.agents.controllers;

import ch.idsia.agents.Agent;

public class QLearningAgent extends BasicMarioAIAgent implements Agent {
	
	static QTable table;

	public QLearningAgent() {
		super("QLearningAgent");
		reset();
	}
	
	public static void setQTable(QTable t) {
		table = t;
	}
	
	public void reset() {
		
	}
}
