package ch.idsia.agents.controllers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class NeuralNetwork {
	//Iterator code at bottom.
	
	public static class Link{
		public BasicNode owner;
		public double weight;
		public Link(BasicNode o, double w){
			owner = o; 
			weight = w;
		}
	}
	
	public interface BasicNode{
		public double getVal();
	}
	
	public static class Node implements BasicNode{
		public List<Link> parents;

		public double val;
		
		public Node(){
			parents = new ArrayList<Link>();
		}
		
		public double getVal(){
			return val;
		}
		
		public void update(){
			double total = 0;
			for(int i = 0; i < parents.size(); i++){
				total += parents.get(i).owner.getVal() * parents.get(i).weight;
			}
			total = sigmoid(total);
			val = total;
		}
	}
	
	public static class InputNode implements BasicNode{
		public double val;
		public double getVal(){
			return val;
		}
	}
	
	
	public List<BasicNode> hiddenNodes;
	public List<BasicNode> inputNodes;
	public List<BasicNode> outputNodes;
	
	public int id;
	public static int maxid = 0; 
	
	public static double sigmoid(double x){
		return 1/(1+Math.pow(Math.E, -x));
	}
	
	public NeuralNetwork(int input, int hidden, int output){
		hiddenNodes = new ArrayList<BasicNode>();
		inputNodes = new ArrayList<BasicNode>();
		outputNodes = new ArrayList<BasicNode>();
		for(int i = 0; i < input; i++){
			inputNodes.add(new InputNode());
		}
		for(int i = 0; i < hidden; i++){
			hiddenNodes.add(new Node());
		}
		for(int i = 0; i < output; i++){
			outputNodes.add(new Node());
		}
		id = maxid;
		maxid++;
	}
	
	
	void runInputs(double[] inputs){
		if(inputs.length != inputNodes.size()){
			throw new RuntimeException("Error inputs and input nodes do not match.");
		}
		for(int i = 0; i < inputs.length; i++){
			((InputNode)inputNodes.get(i)).val = inputs[i];
		}
	}
	
	
	public double[] getOutputs(double[] inputs){
		runInputs(inputs);
		for(int i = 0; i < hiddenNodes.size();i++){
			((Node)hiddenNodes.get(i)).update();
		}
		double[] ret = new double[outputNodes.size()];
		for(int i = 0; i < outputNodes.size();i++){
			((Node)outputNodes.get(i)).update();
			ret[i] = outputNodes.get(i).getVal();
		}
		return ret;
	}
	
	public LinkIterator getIterator(){
		return new LinkIterator(this);
	}
	
	
	public static NeuralNetwork MakeFullyConnected(int input, int hidden, int output){
		NeuralNetwork ret = new NeuralNetwork(input,hidden,output);
		for(int i = 0; i < ret.hiddenNodes.size();i++){
			for(int j = 0; j < ret.inputNodes.size(); j++){
				((Node)ret.hiddenNodes.get(i)).parents.add(new Link(ret.inputNodes.get(j),0));
			}
		}
		for(int i = 0; i < ret.outputNodes.size();i++){
			for(int j = 0; j < ret.hiddenNodes.size(); j++){
				((Node)ret.outputNodes.get(i)).parents.add(new Link(ret.hiddenNodes.get(j),0));
			}
		}
		return ret;
	}
	
	public void Randomize(double min, double max){
		LinkIterator itr = getIterator();
		while(itr.hasNext()){
			Link t = itr.next();
			double val = Math.random() * (max-min) + min;
			t.weight = val;
		}
	}
	
	public static class LinkIterator implements Iterator<Link>{
		public static class LILoc{
			boolean onOutputs = false;
			int i = 0;
			int j = -1;
			boolean done = false;
			@Override
			public String toString(){
				return "i: " + i + ", j: " + j + (onOutputs ?  " on outputs " : "") + (done ? " done " : "");
			}
			public LILoc next(NeuralNetwork n){
				List<BasicNode> l = n.hiddenNodes;
				LILoc ret = new LILoc(this);
				if(ret.onOutputs){
					l = n.outputNodes;
				}
				ret.j++;
				List<Link> links = ((Node)l.get(ret.i)).parents;
				if(ret.j >= links.size()){
					ret.i++;
					ret.j = 0;
					if(ret.i >= l.size()){
						if(ret.onOutputs){
							ret.done = true;
							return ret;
						}else{
							ret.onOutputs = true;
							ret.i = 0;
						}
					}
				}
				return ret;
			}
			
			public Link eval(NeuralNetwork n){
				List<BasicNode> l = n.hiddenNodes;
				if(onOutputs)
				{
					l = n.outputNodes;
				}
				return ((Node)l.get(i)).parents.get(j);
			}
			
			public LILoc(){}
			public LILoc(LILoc original){
				i = original.i;
				j = original.j;
				done = original.done;
				onOutputs = original.onOutputs;
			}
		}
		LILoc location;
		NeuralNetwork net;
		
		public LinkIterator(NeuralNetwork n){
			location = new LILoc();
			net = n;
		}
		
		@Override
		public boolean hasNext() {
			LILoc n = location.next(net);
			if(n.done){
				return false;
			}
			return true;
		}

		@Override
		public Link next() {
			LILoc n = location.next(net);
			location = n;
			return location.eval(net);
		}
		
	}
}
