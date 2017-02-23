package ch.idsia.agents.controllers;

import ch.idsia.agents.Agent;
import ch.idsia.benchmark.mario.environments.Environment;

public class LearningAgent extends BasicMarioAIAgent implements Agent {
	
	static NeuralNetwork net;
	static int inputFieldWidth = 3;
	static int inputFieldHeight = 3;

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
		double inputs[] = new double[9];
		
		int startX = marioCenter[0] - inputFieldWidth / 2;
		int startY = marioCenter[1] - inputFieldHeight / 2;
		
		for (int i=0; i<inputFieldWidth; i++) {
			for (int j=0; j<inputFieldHeight; j++) {
				inputs[j * inputFieldHeight + i] = getReceptiveFieldCellValue(startX + i, startY + j);
			}
		}
		
		double[] results = net.getOutputs(inputs);
		
		// TODO: actually use neural net results to take actions
		
		return null;
	}

	@Override
	public void reset() {
		action = new boolean[Environment.numberOfButtons];
	}
}
