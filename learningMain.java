package ch.idsia.agents.controllers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import ch.idsia.agents.Agent;
import ch.idsia.benchmark.mario.environments.Environment;
import ch.idsia.benchmark.mario.environments.MarioEnvironment;
import ch.idsia.benchmark.tasks.BasicTask;
import ch.idsia.benchmark.tasks.MarioCustomSystemOfValues;
import ch.idsia.tools.CmdLineOptions;

/**
 * Created by IntelliJ IDEA. User: Sergey Karakovskiy, sergey at idsia dot ch Date: Mar 17, 2010 Time: 8:28:00 AM
 * Package: ch.idsia.scenarios
 */
public final class learningMain
{
	public static void main(String[] args)
	{
		final String argsString = "-vis off -ag ch.idsia.agents.controllers.LearningAgent";
		CmdLineOptions cmdLineOptions = new CmdLineOptions(argsString);
		
		// initialize the level paramaters
		cmdLineOptions.setLevelDifficulty(0);
		cmdLineOptions.setLevelRandSeed(0);
		
		int numGenerations = 1000;
		int numParents = 50;
		int numChildren = 50;
		
		TreeMap<Float, ArrayList<Integer>> networkFitnesses = new TreeMap<>();
		ArrayList<Integer> parents = new ArrayList<>();
		// TODO randomly initialize parents

		final BasicTask basicTask = new BasicTask(cmdLineOptions);

		for (int i=0; i<numGenerations; i++) {
			
			// TODO Generate children randomly from parent list
			
			for (int j=0; j<numParents + numChildren; j++) {
				// TODO set the current neural network
				basicTask.reset(cmdLineOptions);
				basicTask.runOneEpisode();
				
				final MarioCustomSystemOfValues sov = new MarioCustomSystemOfValues();
				float fitness = basicTask.getEnvironment().getEvaluationInfo().computeWeightedFitness(sov);
				
				if (!networkFitnesses.containsKey(fitness)) {
					networkFitnesses.put(fitness, new ArrayList<>());
				}

				networkFitnesses.get(fitness).add(j); // TODO actually store network
			}
			
			Iterator<Map.Entry<Float, ArrayList<Integer>>> itr = networkFitnesses.descendingMap().entrySet().iterator();
			
			// Get the next generation parents
			parents.clear();
			for (int j=0; j<numParents; j++) {
				if (!itr.hasNext()) {
					// this probably shouldn't happen
					System.err.println("Ran out of candidates!");
					break;
				}

				ArrayList<Integer> nextParents = itr.next().getValue();
				int k = 0;
				while (k < nextParents.size() && j < numParents) {
					parents.add(nextParents.get(k));
					j++;
					k++;
				}
			}
			
			// TODO: calculate std. devation and use it for evolving

		}

		System.exit(0);
	}
}