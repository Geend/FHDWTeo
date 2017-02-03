package de.fhdw.ml.transactionFramework.administration;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

public class ConflictGraph<T> {
	
	private final Map<T,TreeSet<T>> directSuccessors;
	private final Map<T,TreeSet<T>> directPredecessors;
	
	private final Map<T,TreeSet<T>> successors;
	private final Map<T,TreeSet<T>> predecessors;
	
	public ConflictGraph(Comparator<T> comparator) {
		this.directSuccessors = new TreeMap<T,TreeSet<T>>();
		this.directPredecessors = new TreeMap<T,TreeSet<T>>();
		this.successors = new TreeMap<T,TreeSet<T>>();
		this.predecessors = new TreeMap<T,TreeSet<T>>();
	}
	public boolean hasPath(T start, T end){
		return this.getSuccessors(start).contains(end);
	}
	public TreeSet<T> getSuccessors(T t) {
		TreeSet<T> result = this.successors.get(t);
		if (result == null) {
			result = new TreeSet<T>();
			this.successors.put(t, result);
		}
		return result;
	}
	public TreeSet<T> getPredecessors(T t) {
		TreeSet<T> result = this.predecessors.get(t);
		if (result == null) {
			result = new TreeSet<T>();
			this.predecessors.put(t, result);
		}
		return result;
	}
	public TreeSet<T> getDirectSuccessors(T t) {
		TreeSet<T> result = this.directSuccessors.get(t);
		if (result == null) {
			result = new TreeSet<T>();
			this.directSuccessors.put(t, result);
		}
		return result;
	}
	public TreeSet<T> getDirectPredecessors(T t) {
		TreeSet<T> result = this.directPredecessors.get(t);
		if (result == null) {
			result = new TreeSet<T>();
			this.directPredecessors.put(t, result);
		}
		return result;
	}
	private TreeSet<T> getPredecessorsAndMeAsCopy(T t) {
		@SuppressWarnings("unchecked")
		TreeSet<T> result = (TreeSet<T>) this.getPredecessors(t).clone();
		result.add(t);
		return result;
	}
	private TreeSet<T> getSuccessorsAndMeAsCopy(T t) {
		@SuppressWarnings("unchecked")
		TreeSet<T> result = (TreeSet<T>) this.getSuccessors(t).clone();
		result.add(t);
		return result;
	}
	synchronized public void putSuccessor(T predecessor, T successor, OnCycle<T> onCycle) {
		if (this.hasPath(predecessor, successor)) return;
		if (predecessor.equals(successor) || this.hasPath(successor, predecessor)) {
			onCycle.handleCycle(successor);
			return;
		}
		this.getDirectSuccessors(predecessor).add(successor);
		this.getDirectPredecessors(successor).add(predecessor);
		TreeSet<T> beforePredecessor = this.getPredecessorsAndMeAsCopy(predecessor);
		TreeSet<T> afterSuccessor = this.getSuccessorsAndMeAsCopy(successor);
		for (T current : beforePredecessor) {
			TreeSet<T> afterCurrent = this.getSuccessors(current);
			afterCurrent.addAll(afterSuccessor);
		}
		for (T current : afterSuccessor) {
			TreeSet<T> beforeCurrent = this.getPredecessors(current);
			beforeCurrent.addAll(beforePredecessor);
		}
	}
	public boolean isMinimal(T t) {
		return this.getDirectPredecessors(t).size() == 0;
	}
	public boolean isMaximal(T t) {
		return this.getDirectSuccessors(t).size() == 0;
	}
}
