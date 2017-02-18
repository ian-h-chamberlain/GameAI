package ch.idsia.agents.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

import ch.idsia.agents.Agent;
import ch.idsia.agents.controllers.NeuralNetwork.Link;
import ch.idsia.agents.controllers.NeuralNetwork.LinkIterator;
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
		
		
//		NeuralNetwork n =  NeuralNetwork.MakeFullyConnected(10,10,10);
//		n.Randomize(0, 1);
//		if(1==1){
//			return;
//		}
		// initialize the level paramaters
		cmdLineOptions.setLevelDifficulty(0);
		cmdLineOptions.setLevelRandSeed(0);
		
		int numGenerations = 1000;
		int numParents = 50;
		int numChildren = 50;
		
		int inputNodes = 10;
		int outputNodes = 1;
		int hiddenNodes = 10;
		
		Random rn = new Random();
		
		TreeMap<Double, ArrayList<NeuralNetwork>> networkFitnesses = new TreeMap<>();
		ArrayList<NeuralNetwork> curGeneration = new ArrayList<>();

		// randomly initialize parents
		for (int i=0; i<numParents; i++) {
			NeuralNetwork nn = new NeuralNetwork(inputNodes, hiddenNodes, outputNodes);
			nn.Randomize(-3, 3);
			curGeneration.add(nn);
		}

		final BasicTask basicTask = new BasicTask(cmdLineOptions);

		// start running the generations
		for (int i=0; i<numGenerations; i++) {
			
			System.out.println("Generation " + i);
			
			// Generate children randomly from parent list
			for (int j=0; j<numChildren / 2; j++) {
				int first = rn.nextInt(numParents);
				int second = first;
				while (second == first) {
					second = rn.nextInt(numParents);
				}
				
				LinkIterator firstItr = curGeneration.get(first).getIterator();
				LinkIterator secondItr = curGeneration.get(second).getIterator();
				
				// recombine second and first
				NeuralNetwork child = new NeuralNetwork(inputNodes, hiddenNodes, outputNodes);
				LinkIterator childItr = child.getIterator();
				NeuralNetwork inverseChild = new NeuralNetwork(inputNodes, hiddenNodes, outputNodes);
				LinkIterator inverseItr = inverseChild.getIterator();
				
				// recombine all weights randomly
				while (firstItr.hasNext() && secondItr.hasNext() && childItr.hasNext() && inverseItr.hasNext()) {
					double alpha = rn.nextDouble();
					
					double firstWeight = firstItr.next().weight;
					double secondWeight = secondItr.next().weight;
					
					Link childLink = childItr.next();
					Link inverseLink = inverseItr.next();
					
					childLink.weight = firstWeight * alpha + secondWeight * (1 - alpha);
					inverseLink.weight = firstWeight * (1 - alpha) + secondWeight * alpha;

					// TODO use std. deviation for this
					double mutation = (rn.nextDouble() - 0.5) * 0.01 * (childLink.weight + inverseLink.weight);
					childLink.weight += mutation;

					mutation = (rn.nextDouble() - 0.5) * 0.01 * (childLink.weight + inverseLink.weight);
					inverseLink.weight += mutation;
				}
				
				curGeneration.add(child);
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
				double[] inputs = {1, 1, 1, 1, 1, 1, 1, 1, 1, 1};

				double fitness = curGeneration.get(j).getOutputs(inputs)[0];
				
				if (!networkFitnesses.containsKey(fitness)) {
					networkFitnesses.put(fitness, new ArrayList<>());
				}

				networkFitnesses.get(fitness).add(curGeneration.get(j)); // TODO actually store network
			}
			
			System.out.println("Next parents, from " + networkFitnesses.size() + " pool");
			
			Iterator<Map.Entry<Double, ArrayList<NeuralNetwork>>> itr = networkFitnesses.descendingMap().entrySet().iterator();
			
			// Get the next generation parents with highest fitnesses
			curGeneration.clear();
			while (curGeneration.size() < numParents) {
				if (!itr.hasNext()) {
					// this probably shouldn't happen
					System.err.println("Ran out of candidates!");
					break;
				}

				Map.Entry<Double, ArrayList<NeuralNetwork>> entry = itr.next();
				int k = 0;
				while (k < entry.getValue().size() && curGeneration.size() < numParents) {
					// System.out.print(entry.getValue().get(k) +":" + entry.getKey().toString() + ",");
					curGeneration.add(entry.getValue().get(k));
					System.out.print(entry.getKey() + ",");
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