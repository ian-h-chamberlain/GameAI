package ch.idsia.agents.controllers;

import ch.idsia.benchmark.tasks.BasicTask;
import ch.idsia.benchmark.tasks.MarioCustomSystemOfValues;
import ch.idsia.tools.CmdLineOptions;

public final class QLearningMain {

	public static void main(String[] args) {
		final String argsString = "-vis off -ag ch.idsia.agents.controllers.QLearningAgent";
		CmdLineOptions cmdLineOptions = new CmdLineOptions(argsString);

		int numEpisodes = 2000;
		int difficulty = 0;
		int seed = 1;
		// initialize the level paramaters
		cmdLineOptions.setLevelDifficulty(difficulty);
		cmdLineOptions.setLevelRandSeed(seed);
		
		final BasicTask basicTask = new BasicTask(cmdLineOptions);
		
		// set up the Q table for use with the agent
		QTable table = new QTable(5);	// action space of 4 possible actions
		QLearningAgent.setQTable(table);
		
		double maxFitness = 0.0;
		
		for (int i=0; i<numEpisodes; i++) {

			basicTask.reset(cmdLineOptions);
			basicTask.runOneEpisode();
			
			final MarioCustomSystemOfValues sov = new MarioCustomSystemOfValues();
			double fitness = basicTask.getEnvironment().getEvaluationInfo().computeWeightedFitness(sov);
			
			if (maxFitness < fitness) {
				maxFitness = fitness;
			}
			System.out.println(fitness);
			
			QLearningAgent.runFinalReward(basicTask.getEnvironment().getMarioStatus());
		}
		
		// disable random choices
		QLearningAgent.epsilon = 0.0f;
		
		System.out.println("QTable has " + table.size() + " entries");
		
		// visualize the final result
		cmdLineOptions.setVisualization(true);
		basicTask.reset(cmdLineOptions);
		basicTask.runOneEpisode();
		
		System.out.println("Max fitness was " + maxFitness);
		final MarioCustomSystemOfValues sov = new MarioCustomSystemOfValues();
		System.out.println("Final was " + basicTask.getEnvironment().getEvaluationInfo().computeWeightedFitness(sov));
		
		System.exit(0);
	}
}
