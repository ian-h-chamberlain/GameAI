package ch.idsia.agents.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
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
		
		Random rn = new Random();
		
		TreeMap<Float, ArrayList<Integer>> networkFitnesses = new TreeMap<>();
		ArrayList<Integer> curGeneration = new ArrayList<>();
		
		HashMap<Integer, Float> fitnesses = new HashMap<>();

		int id = 0;
		// randomly initialize parents
		for (int i=0; i<numParents; i++) {
			curGeneration.add(id);
			fitnesses.put(id, rn.nextFloat() * 100);
			id++;
		}

		final BasicTask basicTask = new BasicTask(cmdLineOptions);

		// start running the generations
		for (int i=0; i<numGenerations; i++) {
			
			System.out.println("Generation " + i);
			
			// Generate children randomly from parent list
			for (int j=0; j<numChildren; j++) {
				int first = rn.nextInt(numParents);
				int second = first;
				while (second == first) {
					second = rn.nextInt(numParents);
				}
				
				// recombine second and first
				curGeneration.add(id);
				fitnesses.put(id, rn.nextFloat() * 100);
				id++;
				
				// TODO apply random mutation to child
			}
			
			networkFitnesses.clear();

			for (int j=0; j<curGeneration.size(); j++) {
				
				// TODO set the current neural network
				/*
				basicTask.reset(cmdLineOptions);
				basicTask.runOneEpisode();
				
				final MarioCustomSystemOfValues sov = new MarioCustomSystemOfValues();
				float fitness = basicTask.getEnvironment().getEvaluationInfo().computeWeightedFitness(sov);
				*/
				float fitness = fitnesses.get(curGeneration.get(j));
				
				if (!networkFitnesses.containsKey(fitness)) {
					networkFitnesses.put(fitness, new ArrayList<>());
				}

				networkFitnesses.get(fitness).add(curGeneration.get(j)); // TODO actually store network
			}
			
			System.out.println("Next parents, from " + networkFitnesses.size() + " pool");
			
			Iterator<Map.Entry<Float, ArrayList<Integer>>> itr = networkFitnesses.descendingMap().entrySet().iterator();
			
			// Get the next generation parents with highest fitnesses
			curGeneration.clear();
			while (curGeneration.size() < numParents) {
				if (!itr.hasNext()) {
					// this probably shouldn't happen
					System.err.println("Ran out of candidates!");
					break;
				}

				Map.Entry<Float, ArrayList<Integer>> entry = itr.next();
				int k = 0;
				while (k < entry.getValue().size() && curGeneration.size() < numParents) {
					// System.out.print(entry.getValue().get(k) +":" + entry.getKey().toString() + ",");
					curGeneration.add(entry.getValue().get(k));
					System.out.print(entry.getValue().get(k) + ":" + entry.getKey() + ",");
					k++;
				}
			}
			System.out.println("");
			System.out.println(curGeneration.size() + " selected");
			
			// TODO: calculate std. devation and use it for evolving
		}

		System.exit(0);
	}
}