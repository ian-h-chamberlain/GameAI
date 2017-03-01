package ch.idsia.agents.controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import ch.idsia.agents.controllers.NeuralNetwork.Link;
import ch.idsia.agents.controllers.NeuralNetwork.LinkIterator;
import ch.idsia.benchmark.tasks.BasicTask;
import ch.idsia.benchmark.tasks.MarioCustomSystemOfValues;
import ch.idsia.tools.CmdLineOptions;
import java.util.Calendar;

/**
 * Created by IntelliJ IDEA. User: Sergey Karakovskiy, sergey at idsia dot ch Date: Mar 17, 2010 Time: 8:28:00 AM
 * Package: ch.idsia.scenarios
 */
public final class learningMain
{
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
	public static void playSingleGame(NeuralNetwork nn, boolean vizualize, int difficulty, int levelSeed) {

		LearningAgent.useNeuralNetwork(nn);
		final String argsString = "-vis off -ag ch.idsia.agents.controllers.LearningAgent";
		CmdLineOptions cmdLineOptions = new CmdLineOptions(argsString);
		
		cmdLineOptions.setVisualization(vizualize);
		cmdLineOptions.setLevelDifficulty(difficulty);
		cmdLineOptions.setLevelRandSeed(levelSeed);
		
		final BasicTask task = new BasicTask(cmdLineOptions);
		
		task.reset(cmdLineOptions);
		task.runOneEpisode();
		
		final MarioCustomSystemOfValues sov = new MarioCustomSystemOfValues();
		double fitness = task.getEnvironment().getEvaluationInfo().computeWeightedFitness(sov);
		System.out.println("Fitness: " + fitness);
	}
	
	public static void WriteNetwork(NeuralNetwork n,double fitness){
		//System.out.println("_____________________WRITING________________");
		String finalNetwork = n.toString();
		String[] netparams = finalNetwork.split("~");
		String filename = netparams[0] + "l" +netparams[1] + "l" + netparams[2]; 
		try {
			File dir = new File(filename);
			if(!dir.exists()){
				dir.mkdirs();
			}
			ArrayList<String> s = new ArrayList<String>();
			s.add(finalNetwork);
			PrintWriter writer = new PrintWriter(filename + "/" + Double.valueOf(fitness).toString(),"UTF-8");
			writer.write(finalNetwork);
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	//}
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	public static NeuralNetwork ReadNetwork(String dir){
		String ret = "";
		try {
			File f = new File(dir);
			FileReader read = new FileReader(f);
			char[] exp = new char[(int) f.length()];
			read.read(exp);
			ret = new String(exp);
			read.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return NeuralNetwork.Parse(ret);
	}
	public static void main(String[] args)
	{
		final String argsString = "-vis off -ag ch.idsia.agents.controllers.LearningAgent";
		CmdLineOptions cmdLineOptions = new CmdLineOptions(argsString);
		int difficulty = 0;
		int seed = 0;
		// initialize the level paramaters
		cmdLineOptions.setLevelDifficulty(difficulty);
		cmdLineOptions.setLevelRandSeed(seed);
		
		int numGenerations = 5;
		int numParents = 10;
		int numChildren = 10;
		
		int inputX = 3;
		int inputY = 3;
		
		LearningAgent.setInputFieldSize(inputX, inputY);

		/*
		for (int i=0; i<10; i++) {
			NeuralNetwork cur = ReadNetwork("20l10l6/1566.9957275390625");
			playSingleGame(cur,true,difficulty,0);
		}
		System.exit(0);
		*/

		
		int inputNodes = inputX * inputY * 2 + 2;
		int outputNodes = 6;
		int hiddenNodes = 10;
		
		double mutationChance = 1.0;
		
		Random rn = new Random();
		
		TreeMap<Double, ArrayList<NeuralNetwork>> networkFitnesses = new TreeMap<>();
		ArrayList<NeuralNetwork> curGeneration = new ArrayList<>();
		
		ArrayList<NeuralNetwork> bestParents = new ArrayList<>();

		String csvOut = "";

		for (int m=0; m <= numParents; m++) {
			
			for (int i=0; i<numParents; i++) {
				// set up the meta generation
				if (m == numParents) {
					curGeneration.add(bestParents.get(i));
				}
				else {
					// randomly initialize parents
					NeuralNetwork nn = NeuralNetwork.MakeFullyConnected(inputNodes, hiddenNodes, outputNodes);
					nn.Randomize(-3, 3);
					curGeneration.add(nn);
				}
			}
			
			final BasicTask basicTask = new BasicTask(cmdLineOptions);

			// start running the generations
			for (int i=0; i<numGenerations; i++) {
				
				System.out.println("Generation " + i + ", " + curGeneration.size() + " members");
				
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
					NeuralNetwork child = NeuralNetwork.MakeFullyConnected(inputNodes, hiddenNodes, outputNodes);
					LinkIterator childItr = child.getIterator();
					NeuralNetwork inverseChild = NeuralNetwork.MakeFullyConnected(inputNodes, hiddenNodes, outputNodes);
					LinkIterator inverseItr = inverseChild.getIterator();
					
					// first we need to combine sigma
					double alpha = rn.nextDouble();
					double firstSig = curGeneration.get(first).getSigma();
					double secondSig = curGeneration.get(second).getSigma();
					
					child.setSigma(firstSig * alpha + secondSig * (1 - alpha));
					inverseChild.setSigma(firstSig * (1 - alpha) + secondSig * alpha);
					
					child.setSigma(child.getSigma() + rn.nextGaussian() * child.getSigma());
					inverseChild.setSigma(inverseChild.getSigma() + rn.nextGaussian() * inverseChild.getSigma());
					
					// recombine all weights randomly
					while (firstItr.hasNext() && secondItr.hasNext() && childItr.hasNext() && inverseItr.hasNext()) {
						alpha = rn.nextDouble();
						
						double firstWeight = firstItr.next().weight;
						double secondWeight = secondItr.next().weight;
						
						Link childLink = childItr.next();
						Link inverseLink = inverseItr.next();
						
						childLink.weight = firstWeight * alpha + secondWeight * (1 - alpha);
						inverseLink.weight = firstWeight * (1 - alpha) + secondWeight * alpha;

						if (rn.nextDouble() < mutationChance) {
							double mutation = rn.nextGaussian() * child.getSigma();
							childLink.weight += mutation;
							
							mutation = rn.nextGaussian() * inverseChild.getSigma();
							inverseLink.weight += mutation;
						}
					}
					
					curGeneration.add(child);
					curGeneration.add(inverseChild);
				}
				
				networkFitnesses.clear();

				for (int j=0; j<curGeneration.size(); j++) {
					
					// tell the learning agent to use this neural net
					LearningAgent.useNeuralNetwork(curGeneration.get(j));
					
					// cmdLineOptions.setLevelRandSeed((j + rn.nextInt()) % 10);
					
					basicTask.reset(cmdLineOptions);
					basicTask.runOneEpisode();
					
					final MarioCustomSystemOfValues sov = new MarioCustomSystemOfValues();
					double fitness = basicTask.getEnvironment().getEvaluationInfo().computeWeightedFitness(sov);
					
					WriteNetwork(curGeneration.get(j),fitness);
					if (!networkFitnesses.containsKey(fitness)) {
						networkFitnesses.put(fitness, new ArrayList<>());
					}

					networkFitnesses.get(fitness).add(curGeneration.get(j)); // TODO actually store network
				}
				
				Iterator<Map.Entry<Double, ArrayList<NeuralNetwork>>> itr = networkFitnesses.descendingMap().entrySet().iterator();
				
				NeuralNetwork[] nextCandidates = new NeuralNetwork[numParents + numChildren];

				double[] candidateFitness = new double[numParents + numChildren];
				double[] choice_weights = new double[numParents + numChildren];
				
				// collect the fitness of candidates and weights
				String currentRow = "";
				int j = 0;
				double total_fitness = 0.0f;
				while (itr.hasNext() && j < numParents + numChildren) {
					Map.Entry<Double, ArrayList<NeuralNetwork>> entry = itr.next();
					for (int k=0; k < entry.getValue().size() && j < numParents + numChildren; k++) {
						nextCandidates[j] = entry.getValue().get(k);
						candidateFitness[j] = entry.getKey();
						total_fitness += entry.getKey();
						currentRow += entry.getKey() + ",";
						j++;
					}
				}
				
				// now sum all the previous weights so we get a useful array
				for (j=0; j < numParents + numChildren; j++) {
					choice_weights[j] = candidateFitness[j] / total_fitness;
					for (int k=0; k<j; k++) {
						choice_weights[j] += candidateFitness[k] / total_fitness;
					}
				}
				
				// Get the next generation parents with weighting based on fitness
				curGeneration.clear();
				while (curGeneration.size() < numParents) {

					double choice = rn.nextDouble();

					int k=0;
					while(choice > choice_weights[k] && k < numParents + numChildren - 1) {
						k++;
					}

					curGeneration.add(nextCandidates[k]);

					Double sigma = nextCandidates[k].getSigma();
					sigma = BigDecimal.valueOf(sigma).setScale(3, RoundingMode.HALF_UP).doubleValue();
					Double fitness = candidateFitness[k];

					System.out.print(nextCandidates[k].id + ",");
					System.out.print(sigma.toString() + ",");
					System.out.print(fitness.intValue() + ";");
					
				}

				System.out.println("");
				System.out.println(curGeneration.size() + " selected");
				
				
				csvOut += currentRow + "\n";
			}

			if (m < numParents) {
				Iterator<Map.Entry<Double, ArrayList<NeuralNetwork>>> itr = networkFitnesses.descendingMap().entrySet().iterator();
				
				bestParents.add(itr.next().getValue().get(0));
				curGeneration.clear();
			}
			System.out.println("");
			System.out.println(curGeneration.size() + " selected");
			
		}
		WriteCSV("data",csvOut);
		
		Iterator<Map.Entry<Double, ArrayList<NeuralNetwork>>> itr = networkFitnesses.descendingMap().entrySet().iterator();
		playSingleGame(itr.next().getValue().get(0), true, cmdLineOptions.getLevelDifficulty(), cmdLineOptions.getLevelRandSeed());

		System.exit(0);
	}
}