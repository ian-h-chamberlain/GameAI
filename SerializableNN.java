package ch.idsia.agents.controllers;

import java.util.ArrayList;
import java.util.List;

import ch.idsia.agents.controllers.NeuralNetwork.BasicNode;
import ch.idsia.agents.controllers.NeuralNetwork.Link;

public class SerializableNN {

	public class SerializableLink{
		SerializableNode origin;
		double weight;
	}
	
	public class SerializableNode{
		public List<Link> parents;
		public SerializableNode (NeuralNetwork.Node n){
			
		}
	}
	
	
	
	public List<SerializableNode> hiddenNodes;
	//public List<BasicNode> inputNodes;
	public List<SerializableNode> outputNodes;
	
	
	
	
	public SerializableNN(NeuralNetwork n){
		hiddenNodes = new ArrayList<SerializableNode>();
		outputNodes = new ArrayList<SerializableNode>();
		
	}
	
	
	public NeuralNetwork toNeuralNetwork(){
		
	}
	
}
