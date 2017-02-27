package ch.idsia.agents.controllers;

import java.util.Random;

import ch.idsia.agents.Agent;
import ch.idsia.benchmark.mario.engine.sprites.Mario;
import ch.idsia.benchmark.mario.environments.Environment;

public class LearningAgent extends BasicMarioAIAgent implements Agent {
	
	static NeuralNetwork net;
	static int inputFieldWidth = 3;
	static int inputFieldHeight = 3;
	
	int trueJumpCounter = 0;
	Random gen;

	public LearningAgent() {
		super("LearningAgent");
		reset();
	}
	
	public static void useNeuralNetwork(NeuralNetwork nn) {
		net = nn;
	}
	
	public static void setInputFieldSize(int x, int y) {
		inputFieldWidth = x;
		inputFieldHeight = y;
	}

	@Override
	public boolean[] getAction() {
		double inputs[] = new double[2 * inputFieldWidth * inputFieldHeight + 2];
		
		int startX = marioCenter[0] - inputFieldWidth / 2;
		int startY = marioCenter[1] - inputFieldHeight / 2;
		
		for (int i=0; i<inputFieldWidth; i++) {
			for (int j=0; j<inputFieldHeight; j++) {
				inputs[j * inputFieldHeight + i] = levelScene[startX + i][startY + j];
				inputs[j * inputFieldHeight + i + (inputFieldWidth * inputFieldHeight)] = enemies[startX + i][startY + j];
			}
		}
		
		inputs[inputs.length - 1] = gen.nextDouble();
		inputs[inputs.length - 2] = 1.0;
		
		double[] results = net.getOutputs(inputs);
		
		for (int i=0; i<action.length; i++) {
			action[i] = (results[i] > 0.5); 
		}
		
		// implement long jumps
		if ((isMarioAbleToJump || !isMarioOnGround) && results[Mario.KEY_JUMP] > 0.5) {
			action[Mario.KEY_JUMP] = true;
			++trueJumpCounter;
		} else {
			action[Mario.KEY_JUMP] = false;
			trueJumpCounter = 0;
		}

		if (trueJumpCounter > 16)
		{
			trueJumpCounter = 0;
			action[Mario.KEY_JUMP] = false;
		}
		
		return action;
	}

	@Override
	public void reset() {
		gen = new Random(0);
		action = new boolean[Environment.numberOfButtons];
		trueJumpCounter = 0;
	}
}
