package ch.idsia.agents.controllers;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;

import ch.idsia.benchmark.tasks.BasicTask;
import ch.idsia.benchmark.tasks.MarioCustomSystemOfValues;
import ch.idsia.tools.CmdLineOptions;

public final class QLearningMain {
	public static void WriteCSV(String filename, String contents){
		try {
			Calendar.getInstance();
			PrintWriter writer = new PrintWriter(filename + Calendar.getInstance().getTimeInMillis() + ".csv","UTF-8");
			writer.write(contents);
			writer.close();
		}catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		final String argsString = "-vis off -ag ch.idsia.agents.controllers.QLearningAgent";
		CmdLineOptions cmdLineOptions = new CmdLineOptions(argsString);

		int numEpisodes = 5000;
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
		String csvstr = "";
		for (int i=0; i<numEpisodes; i++) {

			cmdLineOptions.setVisualization(false);
			basicTask.reset(cmdLineOptions);
			basicTask.runOneEpisode();
			
			final MarioCustomSystemOfValues sov = new MarioCustomSystemOfValues();
			double fitness = basicTask.getEnvironment().getEvaluationInfo().computeWeightedFitness(sov);
			
			if (maxFitness < fitness) {
				maxFitness = fitness;
			}
			System.out.println(fitness);
			// System.out.println("Total Q: " + QLearningAgent.totalQ);
			csvstr += i + "," + fitness + "," + QLearningAgent.totalQ + "\n";
			
			QLearningAgent.runFinalReward(basicTask.getEnvironment().getMarioStatus());
		}
		
		String filename = QLearningAgent.learningRate + "a-" + QLearningAgent.discount + "g-" + QLearningAgent.epsilon + "e-";
		// disable random choices
		QLearningAgent.epsilon = 0.0f;
		
		
		System.out.println("QTable has " + table.size() + " entries");
		
		// visualize the final result
		cmdLineOptions.setVisualization(true);
		basicTask.reset(cmdLineOptions);
		basicTask.runOneEpisode();
		
		System.out.println("Max fitness was " + maxFitness);
		final MarioCustomSystemOfValues sov = new MarioCustomSystemOfValues();
		double fitness = basicTask.getEnvironment().getEvaluationInfo().computeWeightedFitness(sov);
		System.out.println("Final was " + fitness);
		
		csvstr += numEpisodes + "," + fitness + "," + QLearningAgent.totalQ + "\n";
		
		WriteCSV(filename, csvstr);
		
		System.exit(0);
	}
}
