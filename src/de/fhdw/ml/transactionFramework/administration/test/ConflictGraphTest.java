package de.fhdw.ml.transactionFramework.administration.test;

import static org.junit.Assert.*;

import java.util.Comparator;

import org.junit.Before;
import org.junit.Test;

import de.fhdw.ml.transactionFramework.administration.ConflictGraph;

public class ConflictGraphTest {
	
	private ConflictGraph<Integer> conflictGraph;
	
	Integer causeOfCycle;

	@Before
	public void createConflictGraph(){
		this.conflictGraph = new ConflictGraph<Integer>(new Comparator<Integer>() {
			@Override
			public int compare(Integer o1, Integer o2) {
				return o1.compareTo(o2);
			}
		});
		this.causeOfCycle = null;
	}

	@Test
	public void testWitoutCycles() {
		this.conflictGraph.putSuccessor(2, 3, x -> {fail();});
// 2 --> 3
		assertEquals(true, this.conflictGraph.isMinimal(2));
		assertEquals(true, this.conflictGraph.isMaximal(3));
		this.conflictGraph.putSuccessor(4, 5,  x -> {fail();});
// 2 --> 3    4 --> 5
		this.conflictGraph.putSuccessor(3, 4, x -> {fail();});
// 2 --> 3 --> 4 --> 5
		assertEquals(3, this.conflictGraph.getSuccessors(2).size());
		assertEquals(2, this.conflictGraph.getSuccessors(3).size());
		assertEquals(1, this.conflictGraph.getSuccessors(4).size());
		assertEquals(0, this.conflictGraph.getSuccessors(5).size());
		assertEquals(true, this.conflictGraph.hasPath(2,3));
		assertEquals(true, this.conflictGraph.hasPath(2,4));
		assertEquals(true, this.conflictGraph.hasPath(2,5));
		assertEquals(true, this.conflictGraph.hasPath(3,4));
		assertEquals(true, this.conflictGraph.hasPath(3,5));
		assertEquals(true, this.conflictGraph.hasPath(4,5));
		assertEquals(true, this.conflictGraph.isMinimal(2));
		assertEquals(false, this.conflictGraph.isMinimal(3));
		assertEquals(false, this.conflictGraph.isMinimal(4));
		assertEquals(false, this.conflictGraph.isMinimal(5));
		assertEquals(true, this.conflictGraph.isMaximal(5));
		assertEquals(false, this.conflictGraph.isMaximal(4));
		assertEquals(false, this.conflictGraph.isMaximal(3));
		assertEquals(false, this.conflictGraph.isMaximal(2));
		this.conflictGraph.putSuccessor(1, 5, x -> {fail();});
// 2 --> 3 --> 4 --> 5     1 --> 5
		assertEquals(true, this.conflictGraph.isMinimal(1));
		assertEquals(true, this.conflictGraph.isMinimal(2));
		this.conflictGraph.putSuccessor(1, 2, x -> {fail();});
// 1 --> 2 --> 3 --> 4 --> 5
		assertEquals(true, this.conflictGraph.isMinimal(1));
		assertEquals(false, this.conflictGraph.isMinimal(2));	
		assertEquals(true, this.conflictGraph.hasPath(1, 5));
		assertEquals(4, this.conflictGraph.getSuccessors(1).size());
		assertEquals(3, this.conflictGraph.getSuccessors(2).size());
		assertEquals(2, this.conflictGraph.getSuccessors(3).size());
		assertEquals(1, this.conflictGraph.getSuccessors(4).size());
		assertEquals(0, this.conflictGraph.getSuccessors(5).size());
		this.conflictGraph.putSuccessor(2, 4, x -> {fail();});
// 1 --> 2 --> 3 --> 4 --> 5
		assertEquals(4, this.conflictGraph.getSuccessors(1).size());
		assertEquals(3, this.conflictGraph.getSuccessors(2).size());
		assertEquals(2, this.conflictGraph.getSuccessors(3).size());
		assertEquals(1, this.conflictGraph.getSuccessors(4).size());
		assertEquals(0, this.conflictGraph.getSuccessors(5).size());
		assertEquals(0, this.conflictGraph.getPredecessors(1).size());
		assertEquals(1, this.conflictGraph.getPredecessors(2).size());
		assertEquals(2, this.conflictGraph.getPredecessors(3).size());
		assertEquals(3, this.conflictGraph.getPredecessors(4).size());
		assertEquals(4, this.conflictGraph.getPredecessors(5).size());
		this.conflictGraph.putSuccessor(2, 6, x -> {fail();});
// 1 --> 2 --> 3 --> 4 --> 5    2 --> 6
		this.conflictGraph.putSuccessor(6, 4, x -> {fail();});
//         --> 6 -->
// 1 --> 2 --> 3 --> 4 --> 5 
		assertEquals(5, this.conflictGraph.getSuccessors(1).size());
		assertEquals(4, this.conflictGraph.getSuccessors(2).size());
		assertEquals(2, this.conflictGraph.getSuccessors(3).size());
		assertEquals(1, this.conflictGraph.getSuccessors(4).size());
		assertEquals(0, this.conflictGraph.getSuccessors(5).size());
		assertEquals(2, this.conflictGraph.getSuccessors(6).size());
		assertEquals(0, this.conflictGraph.getPredecessors(1).size());
		assertEquals(1, this.conflictGraph.getPredecessors(2).size());
		assertEquals(2, this.conflictGraph.getPredecessors(3).size());
		assertEquals(4, this.conflictGraph.getPredecessors(4).size());
		assertEquals(5, this.conflictGraph.getPredecessors(5).size());
		assertEquals(2, this.conflictGraph.getPredecessors(6).size());
		assertEquals(true, this.conflictGraph.isMinimal(1));
		assertEquals(false, this.conflictGraph.isMinimal(2));
		assertEquals(false, this.conflictGraph.isMinimal(3));
		assertEquals(false, this.conflictGraph.isMinimal(4));
		assertEquals(false, this.conflictGraph.isMinimal(5));
		assertEquals(false, this.conflictGraph.isMinimal(6));
		assertEquals(true, this.conflictGraph.isMaximal(5));
		assertEquals(false, this.conflictGraph.isMaximal(1));
		assertEquals(false, this.conflictGraph.isMaximal(2));
		assertEquals(false, this.conflictGraph.isMaximal(3));
		assertEquals(false, this.conflictGraph.isMaximal(4));
		assertEquals(false, this.conflictGraph.isMaximal(6));
	}
	@Test
	public void testWithCycles(){
		this.conflictGraph.putSuccessor(1, 2, x -> {fail();});
		this.conflictGraph.putSuccessor(2, 3, x -> {fail();});
		this.conflictGraph.putSuccessor(4, 5, x -> {fail();});
		this.conflictGraph.putSuccessor(5, 6, x -> {fail();});
		this.conflictGraph.putSuccessor(6, 7, x -> {fail();});
		this.conflictGraph.putSuccessor(3, 4, x -> {fail();});
// 1 --> 2 --> 3 --> 4 --> 5 --> 6 --> 7 
		this.conflictGraph.putSuccessor(8, 8, x -> {this.causeOfCycle = x;});
		assertEquals(new Integer(8), this.causeOfCycle);
		this.conflictGraph.putSuccessor(7, 1, x -> {this.causeOfCycle = x;});
		assertEquals(new Integer(1), this.causeOfCycle);
		this.conflictGraph.putSuccessor(5, 3, x -> {this.causeOfCycle = x;});
		assertEquals(new Integer(3), this.causeOfCycle);
		this.conflictGraph.putSuccessor(7, 8, x -> {fail();});
		this.conflictGraph.putSuccessor(7, 9, x -> {fail();});
		this.conflictGraph.putSuccessor(7, 10, x -> {fail();});
		this.conflictGraph.putSuccessor(7, 11, x -> {fail();});
// 1 --> 2 --> 3 --> 4 --> 5 --> 6 --> 7 --> 8
//                                       --> 9
//                                       --> 10
//                                       --> 11
		this.conflictGraph.putSuccessor(11, 1, x -> {this.causeOfCycle = x;});
		assertEquals(new Integer(1), this.causeOfCycle);
		this.conflictGraph.putSuccessor(10, 2, x -> {this.causeOfCycle = x;});
		assertEquals(new Integer(2), this.causeOfCycle);
		this.conflictGraph.putSuccessor(9, 3, x -> {this.causeOfCycle = x;});
		assertEquals(new Integer(3), this.causeOfCycle);		
	}
}
