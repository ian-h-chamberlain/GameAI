package ch.idsia.agents.controllers;

import ch.idsia.agents.Agent;

public class QLearningAgent extends BasicMarioAIAgent implements Agent {
	
	static QTable table;
	int stateSize = 18;
	
	// variables for check if we're stuck
	boolean isStuck = false;
	float stuckThreshold = 0.1f;
	int stayStuckFrames = 250;
	int stuckCounter = 0;
	float[] previousFloatPos = new float[]{0.0f, 0.0f};

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
}
