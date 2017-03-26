package ch.idsia.agents.controllers;

import ch.idsia.benchmark.tasks.BasicTask;
import ch.idsia.benchmark.tasks.MarioCustomSystemOfValues;
import ch.idsia.tools.CmdLineOptions;

public final class QLearningMain {

	public static void main(String[] args) {
		final String argsString = "-vis off -ag ch.idsia.agents.controllers.QLearningAgent";
		CmdLineOptions cmdLineOptions = new CmdLineOptions(argsString);

		int numEpisodes = 5000;
		int difficulty = 0;
		int seed = 0;
		// initialize the level paramaters
		cmdLineOptions.setLevelDifficulty(difficulty);
		cmdLineOptions.setLevelRandSeed(seed);
		
		final BasicTask basicTask = new BasicTask(cmdLineOptions);
		
		// set up the Q table for use with the agent
		QTable table = new QTable(4);	// action space of 4 possible actions
		QLearningAgent.setQTable(table);
		
		for (int i=0; i<numEpisodes; i++) {

			basicTask.reset(cmdLineOptions);
			basicTask.runOneEpisode();
			
			final MarioCustomSystemOfValues sov = new MarioCustomSystemOfValues();
			double fitness = basicTask.getEnvironment().getEvaluationInfo().computeWeightedFitness(sov);
			
			// TODO use the reward function to update the q-table
		}
		
	}

}