package ch.idsia.agents.controllers;

import java.util.Random;

import ch.idsia.agents.Agent;

public class QLearningAgent extends BasicMarioAIAgent implements Agent {
	
	static QTable table;

	public float epsilon;
	
	QTable table;
	
	Random rand = new Random();
	
	public QLearningAgent() {
		super("QLearningAgent");
		reset();
	}
	
	@Override
	public boolean[] getAction() {
		
	}
	public static void setQTable(QTable t) {
		table = t;
	}
	
	public void reset() {
		
	}
}
