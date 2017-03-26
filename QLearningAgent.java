package ch.idsia.agents.controllers;

import java.util.Random;

import ch.idsia.agents.Agent;

public class QLearningAgent extends BasicMarioAIAgent implements Agent {
	
	static QTable table;

	public float epsilon;
	
	Random rand = new Random();
	
	public boolean[] lastAction;
	public boolean[] lastState;
	
	public QLearningAgent() {
		super("QLearningAgent");
		reset();
	}
	boolean[] getState(){
		return null;
	}
	
	@Override
	public boolean[] getAction() {
		boolean[] ret;
		boolean[] state = getState();
		if(rand.nextFloat()> epsilon){
			
		}else{
			ret = getRandomAction();
		}
		lastState = state;
		lastAction = ret;
		return ret;
	}
	
	
	boolean[] getRandomAction(){
		boolean[] ret = new boolean[5];
		for(int i = 0; i< ret.length; i++){
			ret[i] = rand.nextBoolean();
		}
		return ret;
	}
	
	public static void setQTable(QTable t) {
		table = t;
	}
	
	public void reset() {
		
	}
}
