package ch.idsia.agents.controllers;

import java.util.Random;

import ch.idsia.agents.Agent;
import ch.idsia.benchmark.mario.engine.sprites.Mario;

public class QLearningAgent extends BasicMarioAIAgent implements Agent {
	
	static QTable table;
	int stateSize = 19;
	boolean isStuck;
	int framesSinceMoved = 0;

	public float epsilon;
	public float learningRate;
	
	Random rand = new Random();
	
	public boolean[] lastAction;
	public boolean[] lastState;
	public float lastPos = 0;
	
	public QLearningAgent() {
		super("QLearningAgent");
		reset();
	}
	
	public float getReward(){
		//returns the reward for being in the current state as the agent.
		float ret = marioFloatPos[0] - lastPos;
		lastPos = marioFloatPos[0];
		return ret;
	}
	
	public void runFinalReward(){
		float reward = 0;
		if(Mario.STATUS_DEAD == this.marioStatus){
			reward -= 1000;
		}
		if(Mario.STATUS_WIN == this.marioStatus){
			reward += 1000;
		}
		float oldQ = table.getQ(lastState, lastAction);
		float newQ = oldQ + learningRate * (reward - oldQ); 
		table.setQ(lastState, lastAction, newQ);
	}
	
	@Override
	public boolean[] getAction() {
		boolean[] ret;
		boolean[] state = getState();
		//update the q value for the last action.
		float currentActionQ = table.getQ(state, table.maxQAction(state));
		float oldQ = table.getQ(lastState, lastAction);
		float newQ = oldQ + learningRate * (getReward() + currentActionQ - oldQ); 
		table.setQ(lastState, lastAction, newQ);
		if(rand.nextFloat()> epsilon){
			ret = table.maxQAction(state);
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
