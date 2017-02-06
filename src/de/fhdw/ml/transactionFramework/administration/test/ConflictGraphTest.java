package de.fhdw.ml.transactionFramework.administration.test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import de.fhdw.ml.transactionFramework.administration.ConflictGraph;

public class ConflictGraphTest {
	
	private ConflictGraph<Integer> conflictGraph;
	

	@Before
	public void createConflictGraph(){
		this.conflictGraph = new ConflictGraph<Integer>();
	}

	@Test
	public void testWitoutCycles() {
		try {
			this.conflictGraph.putSuccessor(2, 3);
// 2 --> 3
			assertEquals(true, this.conflictGraph.isMinimal(2));
			assertEquals(true, this.conflictGraph.isMaximal(3));
			this.conflictGraph.putSuccessor(4, 5);
// 2 --> 3    4 --> 5
			this.conflictGraph.putSuccessor(3, 4);
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
			this.conflictGraph.putSuccessor(1, 5);
// 2 --> 3 --> 4 --> 5     1 --> 5
			assertEquals(true, this.conflictGraph.isMinimal(1));
			assertEquals(true, this.conflictGraph.isMinimal(2));
			this.conflictGraph.putSuccessor(1, 2);
// 1 --> 2 --> 3 --> 4 --> 5
			assertEquals(true, this.conflictGraph.isMinimal(1));
			assertEquals(false, this.conflictGraph.isMinimal(2));	
			assertEquals(true, this.conflictGraph.hasPath(1, 5));
			assertEquals(4, this.conflictGraph.getSuccessors(1).size());
			assertEquals(3, this.conflictGraph.getSuccessors(2).size());
			assertEquals(2, this.conflictGraph.getSuccessors(3).size());
			assertEquals(1, this.conflictGraph.getSuccessors(4).size());
			assertEquals(0, this.conflictGraph.getSuccessors(5).size());
			this.conflictGraph.putSuccessor(2, 4);
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
			this.conflictGraph.putSuccessor(2, 6);
// 1 --> 2 --> 3 --> 4 --> 5    2 --> 6
			this.conflictGraph.putSuccessor(6, 4);
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
		} catch (Exception e) {
			fail();
		}
	}
	@Test
	public void testWithCycles(){
		try {
			this.conflictGraph.putSuccessor(1, 2);
			this.conflictGraph.putSuccessor(2, 3);
			this.conflictGraph.putSuccessor(4, 5);
			this.conflictGraph.putSuccessor(5, 6);
			this.conflictGraph.putSuccessor(6, 7);
			this.conflictGraph.putSuccessor(3, 4);
		} catch (Exception e) {
			fail();
		}
// 1 --> 2 --> 3 --> 4 --> 5 --> 6 --> 7 
		try{
			this.conflictGraph.putSuccessor(8, 8);
			fail();
		} catch (Exception e){}
		try{
			this.conflictGraph.putSuccessor(7, 1);
			fail();
		} catch (Exception e){}
		try{
			this.conflictGraph.putSuccessor(5, 3);
			fail();
		} catch (Exception e){}
		try{
			this.conflictGraph.putSuccessor(7, 8);
			this.conflictGraph.putSuccessor(7, 9);
			this.conflictGraph.putSuccessor(7, 10);
			this.conflictGraph.putSuccessor(7, 11);
		} catch (Exception e){
			fail();
		}
// 1 --> 2 --> 3 --> 4 --> 5 --> 6 --> 7 --> 8
//                                       --> 9
//                                       --> 10
//                                       --> 11
		try{
			this.conflictGraph.putSuccessor(11, 1);
			fail();
		} catch (Exception e){}
		try{
			this.conflictGraph.putSuccessor(10, 2);
			fail();
		} catch (Exception e){}
		try{
			this.conflictGraph.putSuccessor(9, 3);
			fail();
		} catch (Exception e){}
	}
}
