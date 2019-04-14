package test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Rule;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import main.BPlusTree;
import main.BPlusTree.IndexNode;
import main.BPlusTree.LeafNode;



class MyTests {
	private main.BPlusTree btree;
	ArrayList<Integer> results = new ArrayList<Integer>();

	@Before
	public void setUp() throws Exception {
		BPlusTree btree = new BPlusTree();
        btree.Initialize(2);
        btree.Insert(3,10.0);
        btree.Insert(4,11.0);
        btree.Insert(5,17.0);
        btree.Insert(6,12.0);
        btree.Insert(7,22.0);
        btree.Insert(2,12.0);
        btree.Insert(9,15.0);
        btree.Insert(1,12.0);
        btree.Insert(11,32.0);
        btree.Insert(8,27.0);
        btree.Insert(13,27.0);
        btree.Insert(15,5127.0);
        btree.Insert(16,9.0);
        btree.Insert(18,0.0);
//        btree.Insert(19,56.0);
//        btree.Insert(84,1.0);
//        btree.Insert(34,5.0);
//        btree.Insert(20,2.0);
//        btree.Insert(21,52.0);
//        btree.Insert(28,26.0);
//        btree.Insert(17,29.0);
//        btree.Insert(24,7.0);
//        btree.Insert(23,65.0);
//        btree.Insert(64,44.0);
//        btree.Insert(100,76.0);
        btree.toString();
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@Test
	void testBPlusTree() {
		fail("Not yet implemented");
	}

	@Test
	void testInsert() {
		fail("Not yet implemented");
	}

	@Test
	void testDelete() {
		fail("Not yet implemented");
	}

	@Test
	void testFixDeficit() {
		fail("Not yet implemented");
	}

	@Test
	void testRMostofLTree() {
		fail("Not yet implemented");
	}

	@Test
	void testLMostofRTree() {
		fail("Not yet implemented");
	}

	@Test
	void testSearchInt() {
		fail("Not yet implemented");
	}

	@Test
	void testSearchIntInt() {
		fail("Not yet implemented");
	}

	@Test
	void testUpdateLeafPointers() {
		fail("Not yet implemented");
	}
	
	@Test
	void testParentPointers() {
	    results.add(2);
//		assertEquals(13, (int)btree.fromLeafList(11).getParent().keys.get(0), "13 is 11's parent");
//		assertEquals(13, (int)btree.fromLeafList(13).getParent().keys.get(0), "13 is 13's parent");
//		assertEquals(16, (int)btree.fromLeafList(15).getParent().keys.get(0), "13 is 15's parent");
//		assertEquals(16, (int)btree.fromLeafList(16).getParent().keys.get(0), "13 is 16's parent");
		assertEquals(results.get(0), btree.fromLeafList(1).getParent().keys.get(0));
		assertEquals(results.get(0), btree.fromLeafList(2).getParent().keys.get(0), "13 is 13's parent");
		assertEquals(results.get(0), btree.fromLeafList(3).getParent().keys.get(0), "13 is 15's parent");
		assertEquals(results.get(0), btree.fromLeafList(4).getParent().keys.get(0), "13 is 16's parent");

	}

}
