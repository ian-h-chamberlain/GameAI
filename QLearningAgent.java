package ch.idsia.agents.controllers;

import java.util.Random;

import ch.idsia.agents.Agent;
import ch.idsia.benchmark.mario.engine.sprites.Mario;
import ch.idsia.benchmark.mario.environments.Environment;

public class QLearningAgent extends BasicMarioAIAgent implements Agent {
	
	static QTable table;
	int stateSize = 18;
	
	// variables for check if we're stuck
	boolean isStuck = false;
	float stuckThreshold = 0.1f;
	int stayStuckFrames = 250;
	int stuckCounter = 0;
	float[] previousFloatPos = new float[]{0.0f, 0.0f};
	public static boolean sarsa = false;
	public static float epsilon = .5f;
	public static float learningRate = .5f;
	public static float discount = .6f;
	
	Random rand = new Random();
	
	public static boolean[] lastAction;
	public static boolean[] lastState;
	public float lastPos = 0;
	
	int collisions = 0;
	int prevCollisions = 0;
	
	public static float totalQ; 
	
	public QLearningAgent() {
		super("QLearningAgent");
		reset();
	}
	
	public float getReward(){
		//returns the reward for being in the current state as the agent.
		float ret = marioFloatPos[0] - lastPos;
		if(ret < 0){
			ret = 0;
		}
		lastPos = marioFloatPos[0];
		
		ret -= 50000 * (collisions - prevCollisions);
		prevCollisions = collisions;
		//System.out.println(ret);
		return ret;
	}
	
	public static void runFinalReward(int status){
		float reward = 0;
		System.out.println("TotalQ: " + totalQ);
		totalQ = 0;
		if(Mario.STATUS_DEAD == status){
			reward -= 100000;
		}
		if(Mario.STATUS_WIN == status){
			reward += 100000;
		}
		float oldQ = table.getQ(lastState, lastAction);
		float newQ = oldQ + learningRate * (reward - oldQ); 
		table.setQ(lastState, lastAction, newQ);
	}
	 
	
	@Override
	public boolean[] getAction() {
		boolean[] ret;
		boolean[] state = getState();
		
		
		//Get the next action.
		if(rand.nextFloat()> epsilon){
			ret = table.maxQAction(state);
		}else{
			ret = getRandomAction();
		}
		

		float currentActionQ;
		//update the q value for the last action.
		if(sarsa){
			currentActionQ = table.getQ(state, ret);
		}else{
			currentActionQ = table.getQ(state, table.maxQAction(state));
		}
		totalQ += currentActionQ;
		System.out.print(currentActionQ + ",");
		float oldQ;
		if(lastState == null || lastAction == null){
			oldQ = 0;
		}else{
			oldQ = table.getQ(lastState, lastAction);	
		}
		float newQ = oldQ + learningRate * (getReward() + discount*currentActionQ - oldQ); 
		System.out.println(newQ);
		if (lastState != null && lastAction != null) {
			table.setQ(lastState, lastAction, newQ);
		}

		
		lastState = state;
		lastAction = ret;
		boolean[] actualRet = new boolean[6];
		for(int i = 0; i < ret.length; i++){
			actualRet[i] = ret[i];
		}
		ret[ret.length-1] = false;
		return actualRet;
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
		lastPos = 0;
	}
	
	@Override
	public void integrateObservation(Environment env) {
		super.integrateObservation(env);
		
		collisions = env.getEvaluationInfo().collisionsWithCreatures;
	}
	
	boolean[] getState() {
		boolean[] state = new boolean[stateSize];
		
		// state[0] is stuck?
		state[0] = isStuck;

		// reset position every so often
		stuckCounter++;
		if (stuckCounter >= stayStuckFrames) {
			// determine whether stuck or not
			isStuck = (Math.abs(marioFloatPos[0] - previousFloatPos[0]) < stuckThreshold);
			// reset counter
			stuckCounter = 0;
			previousFloatPos = marioFloatPos;
		}
		
		// state[1] is on the ground
		state[1] = isMarioOnGround;
		
		int marioX = marioCenter[0];
		int marioY = marioCenter[1];

		// state[2-4] detect small, medium, and large gaps in the level
		boolean[] gaps = detectGaps(marioX, marioY);
		state[2] = gaps[0];
		state[3] = gaps[1];
		state[4] = gaps[2];
		
		// state[5-8] are for obstacles directly in front of mario
		state[5] = (levelScene[marioX + 1][marioY - 1] != 0);
		state[6] = (levelScene[marioX + 1][marioY] != 0);
		state[7] = (levelScene[marioX + 1][marioY + 1] != 0);
		state[8] = (levelScene[marioX + 1][marioY + 2] != 0);

		// state[9-18] are for a 3x3 grid of enemy detection around mario
		state[9] = (enemies[marioX - 1][marioY - 1] != 0);
		state[10] = (enemies[marioX - 1][marioY] != 0);
		state[11] = (enemies[marioX - 1][marioY + 1] != 0);
		state[12] = (enemies[marioX][marioY - 1] != 0);
		state[13] = (enemies[marioX][marioY] != 0);
		state[14] = (enemies[marioX][marioY + 1] != 0);
		state[15] = (enemies[marioX + 1][marioY - 1] != 0);
		state[16] = (enemies[marioX + 1][marioY] != 0);
		state[17] = (enemies[marioX + 1][marioY + 1] != 0);
		
		// POSSIBLE IDEAS
		// state[19-21] for platforms above Mario
		
		return state;
	}
	
	boolean[] detectGaps(int marioX, int marioY) {
		boolean[] result = new boolean[]{false, false, false};
		
		// check for small gaps close to mario
		if (levelScene[marioX + 1][marioY - 2] == 0 ||
				levelScene[marioX + 2][marioY - 2] == 0) {
			result[0] = true;
		}
		
		// medium gaps slightly further away
		if (levelScene[marioX + 3][marioY - 2] == 0 ||
				levelScene[marioX + 4][marioY - 2] == 0 ||
				levelScene[marioX + 5][marioY - 2] == 0) {
			result[1] = true;
		}

		// and large gaps several squares away
		if (levelScene[marioX + 6][marioY - 2] == 0 ||
				levelScene[marioX + 7][marioY - 2] == 0 ||
				levelScene[marioX + 8][marioY - 2] == 0 ||
				levelScene[marioX + 9][marioY - 2] == 0) {
			result[2] = true;
		}
		
		return result;
	}
}
