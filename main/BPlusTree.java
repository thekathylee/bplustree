package main;

import java.util.ArrayList;
import java.util.LinkedList;


import java.lang.Math;

public class BPlusTree
{
    //variables
    int order;
    Node root;
    LinkedList<LeafNode> LeafList =new LinkedList<LeafNode>();  

    //constructors
    public BPlusTree()
    {
        root = new LeafNode();
        root.setParent(null);
    }

    //methods
    public void Initialize(int m)
    {
        order=m;
    }
    
    void deleteFromLeafList(int index) {
    	LeafList.remove(index);
    	updateLeafPointers();
    }

    /**
     * This method inserts a new leafNode associated with a specific key
     * @param key: the key associated with the value
     * @param value: the data to be inserted
     * @return none
     */
    public void Insert (int key, double value)
    {
        LeafNode currLeaf= LeafList.get(locateLeaf(key));
        if(currLeaf.isFull()){
            currLeaf.overfill(key, value);
            if(currLeaf.keys.size() >order){               //if the key already existed, overfill() would have replaced the element and there's nothing left to be done
                System.out.println("***** Insert( "+ key+" , "+ value+" ); called *****");
                IndexNode mom =currLeaf.getParent();
                IndexNode midNode = currLeaf.split();
                if(mom==null){                                  //if the root is overfilled, then set remaining tree as L child of created split tree and update root
                    System.out.println("mom is null");
                    if(midNode.children.get(0)==null) midNode.children.remove(0);
                    currLeaf.setParent(midNode);
                    midNode.children.add(0,currLeaf);
                    root=midNode;
                }else {
                    if(midNode.children.get(0)!=null) midNode.children.get(0).setParent(mom);
                    if(midNode.children.get(1)!=null) midNode.children.get(1).setParent(mom);
                    mom.merge(midNode);
                    while(mom.keys.size()>order){
                        midNode=mom.split();
                        if (midNode.getParent() == null){
                            root = midNode;
                            return;
                        }else {
                            mom = mom.getParent();
                            System.out.println("mom's new key : " +mom.keys.get(0));
                            mom.merge(midNode);
                        }
                        System.out.println("mid node root: " + midNode.keys.get(0) + "\nnum children: "+midNode.children.size());
                    }
                }
            }
            
        }else {
                LeafList.get(locateLeaf(key)).overfill(key,value);
        }
    }
    
    public LeafNode fromLeafList(int key) {
    	return LeafList.get(locateLeaf(key));
    }

    /**
     * This method deletes the leafNode associated with a specific key
     * @param key: the key associated with the value
     * @return none
     */
    public void Delete(int key)
    {
        System.out.println("***** Delete( "+ key+" ); called *****");
    	int tempIndex=locateLeaf(key);
        LeafNode temp = LeafList.get(tempIndex);
        int index = getChildIndex(temp, temp.getParent());
        temp.values.remove(getIndexOf(key, temp));
        temp.keys.remove(getIndexOf(key, temp));
        if(temp.keys.size()>0) {
        	return;
        }
        
        /**
         * Removing the key
         * 3 Main Cases:
         * 1. The right sibling has more than 1 key in which we borrow that key and remove in-between indexNode key at the parent level
         * 2. The left sibling has more than 1 key in which we borrow that key and remove in-between indexNode key at the parent level
         * 3. Neither siblings have extra keys in which we delete the desired key ("merge with sibling") and also the in-between key at the parent level
         */
    	if(temp.getParent().children.size() > 1){					//at least 1 sibling
    		if (rsib(temp) && abundant(temp.next)){						//case 1: right sibling is abundant
	        	System.out.println("Case 1: Abundant right sibling starting w key: "+ temp.next.keys.get(0));
	            temp.keys.add(temp.next.keys.get(0));
	            temp.values.add(temp.next.values.get(0));
	            index = getChildIndex(temp, temp.getParent());
	            temp.getParent().keys.remove(index);
	            temp.getParent().keys.add(index, temp.next.keys.get(1));
	            temp.next.keys.remove(0);
	            temp.next.values.remove(0);
	        } else if(lsib(temp) && abundant(temp.prev)){				//case 2: left sibling is abundant
	        	System.out.println("Case 2: Abundant left sibling starting w key: "+ temp.prev.keys.get(0));
	            int prevSize=temp.prev.keys.size();
	            temp.keys.add(temp.prev.keys.get(prevSize-1));
	            temp.values.add(temp.prev.values.get(prevSize-1));
	            temp.prev.keys.remove(prevSize-1);
	            temp.prev.values.remove(prevSize-1);
	            index = getChildIndex(temp, temp.getParent());
	            if(index!=0) {
		            temp.getParent().keys.remove(index-1);
		            temp.getParent().keys.add(index-1, temp.keys.get(0));
	            }

	        } else {       //if Rsib/Lsib == 1, delete key and remove in-between key in parent (index i-1, unless i=0 then index 0)
	            if(index==0) {
	                System.out.println("Removing this key from parent: "+ temp.getParent().keys.get(0));
	            	temp.getParent().keys.remove(0);
	            }
	            else {
	                System.out.println("Removing this key from parent: "+ temp.getParent().keys.get(index-1));
	            	temp.getParent().keys.remove(index-1);
	            }
	            deleteFromLeafList(tempIndex);
	            temp.getParent().children.remove(index);
	        }
	        System.out.println("index: "+ index);

	        fixDeficit(temp.getParent(), index);
    	}
    }
        public void fixDeficit(IndexNode tempParent, int removedIndex) {
        	
        if(tempParent.getParent()==null) {
        	System.out.println("Deficient node with no parent node"); 	//deal with this later
        }
        else if (tempParent.keys.size()<=0) {							//if parent is deficient
        	int parentIndex=getChildIndex(tempParent, tempParent.getParent());
        	if(tempParent.getParent().children.size() > 1){					//at least 1 uncle
        		Node uncle = null;
        		int uncleIndex=0;
            	if(rsib(tempParent) && tempParent.getParent().children.get(parentIndex+1).keys.size() > 1) {		//if there is an abundant right uncle, we replace parent with parent.children.get(parentIndex);
            			System.out.println("In fixDeficit function, case 1: There's an abundant right uncle");
                		int modifyIndex=0;
                		if(parentIndex!=0) modifyIndex=parentIndex-1;
                		int key =LMostofRTree(tempParent.getParent(), parentIndex);
						LeafNode newChild = LeafList.get(locateLeaf(key));
						System.out.println("LeafList index of newChild: "+ locateLeaf(key));
						tempParent.getParent().keys.remove(parentIndex);
                		tempParent.getParent().keys.add(parentIndex,newChild.keys.get(0));				//set parents key to that of the Left most child of right subtree
						newChild.getParent().children.remove(0);							//remove newChild from it's parent
                		newChild.setParent(tempParent);													//set replacements parent as deficient parent
                		tempParent.children.add(newChild);												//add replacement to parent's childlist
                		System.out.println("added "+ newChild.keys.get(0) + " to tempParent");
                		if(newChild.keys.size()==1) {
                			System.out.println("newchild's key: "+ key);
                    		deleteFromLeafList(locateLeaf(key));
                    	}else {
							newChild.keys.remove(0);
							newChild.values.remove(01);
						}
                		uncle = tempParent.getParent().children.get(parentIndex+1);
                		if(removedIndex==0) {
                        	tempParent.keys.add(tempParent.children.get(removedIndex+1).keys.get(0));			//If the first key was removed, replace parent key w right sibling
                		}else {
                        	tempParent.keys.add(tempParent.getParent().keys.get(modifyIndex));			//else, replace parent key with grandparent
                		}
						uncle.keys.remove(uncleIndex);
						while (tempParent.getParent()!=null && removedIndex==0) {
							removedIndex=getChildIndex(tempParent, tempParent.getParent());
							if(removedIndex==0) {
	                        	tempParent.getParent().keys.remove(0);
	                        	tempParent.getParent().keys.add(0,tempParent.getParent().children.get(removedIndex+1).keys.get(0));			//If the first key was removed, replace parent key w right sibling
							}
							tempParent=tempParent.getParent();
						}
            	}
            	else if (lsib(tempParent) && tempParent.getParent().children.get(parentIndex-1).keys.size() > 1) {		//if there is an abundant left uncle, we replace parent with parent.children.get(parentIndex-1);
            			System.out.println("In fixDeficit function, case 2: There's an abundant left uncle");
                		int modifyIndex=0;
                		if(parentIndex!=0) modifyIndex=parentIndex-1;
                		int key =RMostofLTree(tempParent.getParent(), parentIndex);
                		LeafNode newChild = LeafList.get(locateLeaf(key));
                		tempParent.getParent().keys.remove(tempParent.getParent().keys.size()-1);
                		tempParent.getParent().keys.add(newChild.keys.get(newChild.keys.size()-1));
                		newChild.getParent().children.remove(newChild.getParent().children.size()-1);							//remove newLChild from it's parent
                		newChild.setParent(tempParent);
                		tempParent.children.add(0,newChild);
                		if(newChild.keys.size()==1) {
                    		deleteFromLeafList(locateLeaf(key));
                    	}else {
							newChild.keys.remove(newChild.keys.size()-1);
							newChild.values.remove(newChild.keys.size()-1);
						}
                		uncle = tempParent.getParent().children.get(parentIndex-1);
                		uncleIndex=(uncle.keys.size()-1);
//                		if(removedIndex==0) {
//                        	tempParent.keys.add(tempParent.children.get(removedIndex+1).keys.get(0));			//If the first key was removed, replace parent key w right sibling
//                		}else {
                        	tempParent.keys.add(tempParent.children.get(removedIndex).keys.get(0));			//else, replace parent key with grandparent
//                		}
                    	uncle.keys.remove(uncleIndex);
						while (tempParent.getParent()!=null && removedIndex==0) {
							removedIndex=getChildIndex(tempParent, tempParent.getParent());
							if(removedIndex==0) {
	                        	tempParent.getParent().keys.remove(0);
	                        	tempParent.getParent().keys.add(0,tempParent.getParent().children.get(removedIndex+1).keys.get(0));			//If the first key was removed, replace parent key w right sibling
							}
							tempParent=tempParent.getParent();
						}
            	}else {																			//if there's at least 1 uncle but there is no abundant uncle, merge deficient node, in-between parent key, and uncle
        			System.out.println("In fixDeficit function, case 3: there are no abundant uncles");
        			System.out.println("tempParent currently has "+tempParent.children.size()+"child(ren)");
            		int modifyIndex =0;
        			if(rsib(tempParent)) {
                		uncle = tempParent.getParent().children.get(parentIndex+1);
                		uncleIndex=parentIndex+1;
                		for(Node n: ((IndexNode)uncle).children) {
                			n.setParent(tempParent);
                			tempParent.children.add(n);
                		}
                		tempParent.keys.add(tempParent.getParent().keys.get(modifyIndex));
                		for(int k: uncle.keys) {
                			tempParent.keys.add(k);
                		}
        			}
        			else if(lsib(tempParent)) {
                		if(parentIndex!=0) modifyIndex=parentIndex-1;
                		uncle = tempParent.getParent().children.get(parentIndex-1);
                		System.out.println("key of uncle: "+ uncle.keys.get(0));
                		uncleIndex=parentIndex-1;
                		for(int i=0; i< ((IndexNode)uncle).children.size();i++) {
                			((IndexNode)uncle).children.get(i).setParent(tempParent);
                			tempParent.children.add(i,((IndexNode)uncle).children.get(i));
                		}
                		for(int k: uncle.keys) {
                			tempParent.keys.add(k);
                		}
                		tempParent.keys.add(tempParent.getParent().keys.get(modifyIndex));
            		}
            		tempParent.getParent().keys.remove(modifyIndex);
            		tempParent.getParent().children.remove(uncleIndex);
        			System.out.println("size of tempParent.parent: "+tempParent.getParent().keys.size());
            		if(tempParent.getParent().keys.size()<=0) {
            			fixDeficit(tempParent.getParent(), modifyIndex);
            		}          		
            	}
        	}
        }
    }
    
    //Setting booleans Rsib and Lsib to check existence of siblings
    public boolean rsib(Node temp) {

    	if(temp.isLeaf()) {
	    	if(((LeafNode)temp).next==null) {
	    		return false;
	    	}
	        if(temp.getParent() == ((LeafNode)temp).next.getParent()) {
	        	return true;
	        }
    	}else {
    		int index = getChildIndex(temp,temp.getParent());
    		if(index < 0) {
    			System.out.println("parent linking is corrupted. within rsib(), temp wasn't found within temp.parent");
    		}
    		else if(temp.getParent().children.size()>= index+2) {
    			return true;
    		}
    	}
    	return false;
    }
    
    public boolean abundant(Node temp) {
    	if(temp==null) {
    		return false;
    	}else if (temp.keys.size()>1) {
    		return true;
    	}
    	return false;
    }
    
    public boolean lsib(Node temp) {
    	if(temp.isLeaf()) {
	    	if(((LeafNode)temp).prev==null) {
	    		return false;
	    	}
	        if(temp.getParent() == ((LeafNode)temp).prev.getParent()) {
	        	return true;
	        }
    	}else {
    		int index = getChildIndex(temp,temp.getParent());
    		if(index < 0) {
    			System.out.println("parent linking is corrupted. within lsib(), temp wasn't found within temp.parent");
    		}
    		else if(index > 0) {
    			return true;
    		}
    	}
    	return false;
    }

    public int RMostofLTree(Node parent, int rootIndex) {
    	Node temp = ((IndexNode)parent).children.get(rootIndex-1);
    	System.out.println("in RMostofLTree, key of L uncle is: " + temp.keys.get(0));
    	System.out.println("in RMostofLTree, size of L uncle is: " + temp.keys.size());
    	while(!temp.isLeaf()) {
    		temp = ((IndexNode)temp).children.get(((IndexNode)temp).children.size()-1);
    	}
    	return temp.keys.get(temp.keys.size()-1);
    }
    public int LMostofRTree(Node parent, int rootIndex) {
//    	Node temp = ((IndexNode)parent).children.get(rootIndex+1);
//    	while(!temp.isLeaf()) {
//    		temp = ((IndexNode)temp).children.get(0);
//    	}
//    	int key=temp.keys.get(0);
//    	if(temp.keys.size()==1) {
//    		deleteFromLeafList(locateLeaf(key));
//        	temp.keys.remove(0);
//    	}
//    	((LeafNode)temp).values.remove(0);
//    	return key;
//    	
    	Node temp = ((IndexNode)parent).children.get(rootIndex+1);
    	System.out.println("in LMostofRTree, key of L uncle is: " + temp.keys.get(0));
    	System.out.println("in LMostofRTree, size of L uncle is: " + temp.keys.size());
    	while(!temp.isLeaf()) {
    		temp = ((IndexNode)temp).children.get(0);
    	}
    	return temp.keys.get(0);
    }

    /**
     * This method searches the tree for a specific key
     * @param key: the key associated with the value
     * @return the value associated with the key
     */
    public double Search(int key)
    {
        LeafNode temp = LeafList.get(locateLeaf(key));
        int index=-1;
        for(int i=0; i < temp.keys.size();i++){
            if(temp.keys.get(i)==key){
                index=i;
                break;
            }
        }
        return temp.values.get(index);
    }

    /**
     * This method searches the tree within a range
     * @param key1: the starting value of the range, inclusive
     * @param key2: the ending value of the range, inclusive
     * @return values such that in the range between key1 and key2
     */
    public String Search(int key1, int key2)
    {
        LeafNode temp = LeafList.get(locateLeaf(key1));
        String str="";
        str+=extract(key1, key2, temp);
        int currVal=temp.keys.get(temp.keys.size()-1);
        while(currVal < key2 && temp.next!=null){
            temp=temp.next;
            str+=", "+ extract(key1, key2, temp);
            if(temp.keys.size()<=0) break;
            currVal=temp.keys.get(temp.keys.size()-1);
        }
        return str;
    }

    /**
     * @param key1  the lower bound for the key range (inclusive)
     * @param key2  the upper bound for the key range (inclusive)
     * @param node  the LeafNode in which we want to extract the values from
     * @return  a comma seperated string containing the values corresponding to keys
     *          in which key1 <= key <= key2
     */
    public String extract(int key1, int key2, LeafNode node){
        String str="";
        for(int i=0; i < node.keys.size();i++){
            if(node.keys.get(i) == key1){
            	if (str=="") str= str + node.values.get(i);
            	else str= str + ", " + node.values.get(i);
            }else if (node.keys.get(i) > key1 && node.keys.get(i) <= key2){
            	if (str=="") str= str + node.values.get(i);
            	else str= str + ", " + node.values.get(i);
            }
        }
        return str;
    }
    
    /**
     * @return a "pretty printed" visual representation of tree structure
     */
    public String toString()
    {
        System.out.print("(ROOT): ");
        for(int k: root.keys) {
            System.out.print(k+"  ");
        }
        for (int i = 0; i < ((IndexNode)root).children.size(); i++) {
            writeNode(((IndexNode)root).children.get(i), " ", i < ((IndexNode)root).children.size() - 1);
        }

        return "";
    }

    /**
     *  a function for internal use within toString()
     */
    static void writeNode(Node node, String prefix, boolean hasmoresibs) {
        System.out.println();        
        if (hasmoresibs) {
            System.out.print(prefix + "|-- ");
        } else {
            System.out.print(prefix + "\\-- ");
        }
        for(int k: node.keys) {
            System.out.print(k+"  ");
        }

        String newprefix = prefix;
        if (hasmoresibs) {
            newprefix += "|   ";
        } else {
            newprefix += "    ";
        }
        if(!node.isLeaf()){
            for (int i = 0; i < ((IndexNode)node).children.size(); i++) {
                writeNode(((IndexNode)node).children.get(i), newprefix, i < (((IndexNode)node).children.size() - 1));
            }
        }
    }

    /**
     * @param key The target key
     * @return The LeafNode in which the key exists (or would exist if the key is not contained)
     */
    public int locateLeaf(int key){
        if(LeafList.size()==0){
            root = new LeafNode();
            return 0;
        }else {
            LeafNode temp = LeafList.getFirst();
            int i=0;
            for(i=0; i< LeafList.size()-1;i ++ ){
                temp=LeafList.get(i);
                if(key > temp.keys.get(temp.keys.size()-1)){ //if key > last element of ith element of leaflist
                    if(key >= LeafList.get(i+1).keys.get(0)){           //if key >= first element of i+1 th element of leaflist
                        if(i+1 == LeafList.size()-1) {
                        	return i+1;
                        }else {
                        	continue;
                        }
                    }else {
                        return i;
                    }
                }else {                                                //if key <= last element of ith element
                    return i;
                }
            }
            return LeafList.size()-1;
        }
    }


    public int getIndexOf(int key, Node node){
        int index = -1;
        for(int i = 0; i < node.keys.size(); i++) {
            if(node.keys.get(i) == key){
                index = i;
                return index;
            }
        }
        return index;
    }
    public int getChildIndex(Node child, IndexNode parent){
        int index = -1;
        for(int i = 0; i < parent.children.size(); i++) {
            if(parent.children.get(i) == child){
                index = i;
                return index;
            }
        }
        return index;
    }
    
    /**
     *  This function updates the prev and next LeafNode pointers within the LeafNode class
     */
    void updateLeafPointers(){
        for(int i=0; i< LeafList.size()-1;i ++ ){
            LeafList.get(i).setNext(LeafList.get(i+1));
            LeafList.get(i+1).setPrev(LeafList.get(i));
        }
    }
    

    /** ----------------------------------------------------------------------------------------------------------------------------------------------
     * 
     * 
     * 
     *  NODE CLASS
     * 
     * 
     */

    private abstract class Node 
    {
        //variables
        public ArrayList<Integer> keys;
        private IndexNode parent;
        

        //methods   
        abstract IndexNode split();
        boolean isFull(){
            if(keys.size() >= (order)){
                return true;
            }
            else return false;
        }
        abstract boolean isLeaf();
        void setParent(IndexNode parent){
            this.parent=parent;
        }
		public IndexNode getParent() {
			return parent;
		}
    }


    /** ----------------------------------------------------------------------------------------------------------------------------------------------
     * 
     * 
     * 
     *  INDEX NODE CLASS
     * 
     * 
     */

    public class IndexNode extends Node 
    {
        //variables
        ArrayList<Node> children;

        //constructors
        IndexNode(){
            keys = new ArrayList<Integer>();
            children = new ArrayList<Node>();
            setParent(null);
        }

        IndexNode(int key){
            keys = new ArrayList<Integer>();
            children = new ArrayList<Node>();
            keys.add(key);
            setParent(null);
        }

        //methods

        /**
         *  This function will return the newly "split" node and expects that it is working with an "overfilled" node
         *  This function also removes split values from original node
         */
        IndexNode split(){
        	System.out.println("\n*****Entered node class's split method with overfull node starting w key: " + this.keys.get(0));
            int midIndex = (int)Math.ceil(order/2);
            IndexNode midNode = new IndexNode(keys.get(midIndex));
            IndexNode childNode = new IndexNode();
            for (int i= midIndex+1; i<= order; i++){                     //midNode will move elements after midIndex to childNode
                childNode.keys.add(keys.get(i));
                System.out.println();
                children.get(i).setParent(childNode);
                childNode.children.add(children.get(i));
                keys.remove(i);
                children.remove(i);
            }
            if(children.get(order)!=null){
                children.get(order).setParent(childNode);
                childNode.children.add(children.get(order));
                children.remove(order);
            }
            keys.remove(midIndex);   
            if(getParent()!=null){
                midNode.setParent(getParent());
            } 
            midNode.children.add(0, this);                              //placeholder for L child
            if(this.getParent()!=null) this.getParent().children.remove(getChildIndex(this, this.getParent()));
            System.out.println("\n new parent key: "+ midNode.keys.get(0) + "\nparent key child count: "+ midNode.children.size());
            childNode.setParent(midNode);
            midNode.children.add(1, childNode);
            return midNode;
        }

        /**
         * @param insertNode the Node that we wish to merge with the calling node
         *  This function merges the key and corresponding children of insertNode with the calling node
         */
        void merge(IndexNode insertNode){
        	System.out.println("\n*****Entered node class's merge method with insert node starting w key: " + insertNode.keys.get(0)+"\n called be node starting w key: "+this.keys.get(0));
            System.out.println("insertNode size: "+ insertNode.keys.size());
            if(keys.size()==0){
                keys.add(insertNode.keys.get(0));
            }else{
                int indexInsert=-1;
                for(int i=0; i< keys.size();i++){
                    indexInsert=i;
                    if(insertNode.keys.get(0) > keys.get(i)){
                    }else if(insertNode.keys.get(0) == keys.get(i)){
                        keys.add(i, insertNode.keys.get(0));
                        keys.remove(i+1);
                        children.remove(i);
                        children.remove(i);
                        for(int j=0; j<insertNode.children.size(); j++){
                        	insertNode.children.get(i).setParent(this);
                            children.add(i+j, insertNode.children.get(i));
                        }   
                        return;
                    }else {                 //if insertNode < current key
                        indexInsert=i-1;
                        break;
                    }
                }
                keys.add(indexInsert+1, insertNode.keys.get(0));
                System.out.println("key inserted into calling node:" +keys.get(indexInsert+1));
                System.out.println("num children of insertNode: "+insertNode.children.size());
                for(int i=0; i<insertNode.children.size();i++){
                    if(insertNode.children.get(i)==null){
                    }else {
                    	insertNode.children.get(i).setParent(this);
                    	if(insertNode.children.get(i).keys.get(0) < insertNode.keys.get(0)) {
                        	insertNode.children.get(i).setParent(this);
                            children.add(indexInsert+1, insertNode.children.get(i));
                    	}else {
                        	insertNode.children.get(i).setParent(this);
                            children.add(indexInsert+2, insertNode.children.get(i));
                    	}
                    }
                }        
            }
        }

        /**
         *  This function helps identify this node as an IndexNode (for internal use/ checking conditions prior to casting Node type to IndexNode) 
         */
        boolean isLeaf(){
            return false;
        }

    }




    /** ----------------------------------------------------------------------------------------------------------------------------------------------
     * 
     * 
     * 
     *  LEAF NODE CLASS
     * 
     * 
     */

    public class LeafNode extends Node
    {
        //variables
        ArrayList<Double> values;
        LeafNode next;
        LeafNode prev;

        //constructors
        LeafNode()
        {
            keys = new ArrayList<Integer>();
            values = new ArrayList<Double>();
            LeafList.add(this);
            prev=null;
            next=null;
            updateLeafPointers();
            setParent(null);
        }
        
        LeafNode(int leafIndex, IndexNode parent)
        {
            keys = new ArrayList<Integer>();
            values = new ArrayList<Double>();
            LeafList.add(leafIndex, this);
            prev=null;
            next=null;
            updateLeafPointers();
            setParent(parent);
        }
        
        LeafNode(int key, double value)				//this constructor doesn't add itself to LeafList or maintain relationships. This is for temporary storage of LeafNodes
        {
            keys = new ArrayList<Integer>();
            values = new ArrayList<Double>();
        }

        //methods
      
        IndexNode split(){
        	System.out.println("\n*****Entered leaf class's split method with overfull node starting w key: " + this.keys.get(0));
            int midIndex = (int)Math.ceil(order/2);
            int leafIndex = locateLeaf(keys.get(0));
            IndexNode midNode = new IndexNode(keys.get(midIndex));
            LeafNode childNode = new LeafNode(leafIndex + 1,midNode);
            for (int i= midIndex; i<= order; i++){
                childNode.keys.add(keys.get(midIndex));
                childNode.values.add(values.get(midIndex));
                keys.remove(midIndex);
                values.remove(midIndex);
            }
            System.out.println("\nnew parent key"+ midNode.keys.get(0));
            midNode.children.add(0,null);
            childNode.setParent(midNode);
            midNode.children.add(1, childNode);
            return midNode;
        }


        /**
         * @param key   the key to be inserted into the new node
         * @param value the value to be inserted into the new node
         * This function will create a node with keys.size() = order+1, this function is meant as a precursor to the function split()
         */
        void overfill( int key, double value){
            if(keys.size()==0){
                keys.add(key);
                values.add(value);
            }else{
                int index=-1;
                for(int i=0; i< keys.size();i++){
                    index=i;
                    if(key > keys.get(i)){
                    }else if(key == keys.get(i)){
                        keys.remove(i);
                        keys.add(i,key);
                        values.remove(i);
                        values.add(i,value);
                        return;
                    }else {
                        keys.add(index,key);
                        values.add(index,value);
                        return;
                    }
                }
                keys.add(index+1,key);
                values.add(index+1,value);
            }
        }

        /**
         * setter function for prev pointer
         */
        void setPrev(LeafNode prev){
            this.prev=prev;
        }

        /**
         * setter function for next pointer
         */
        void setNext(LeafNode next){
            this.next=next;
        }

        /**
         *  This function helps identify this node as an LeafNode (for internal use/ checking conditions prior to casting Node type to LeafNode) 
         */
        boolean isLeaf(){
            return true;
        }

    }


    public static void main(String[] args){
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
        btree.Insert(21,56.0);
        btree.Insert(84,1.0);
        btree.Insert(34,5.0);
        btree.Insert(20,2.0);
        btree.Insert(21,52.0);
        btree.Insert(28,26.0);
        btree.Insert(17,29.0);
        btree.Insert(20,29.0);
        btree.Insert(19,29.0);
        btree.Insert(24,7.0);
        btree.Insert(23,65.0);
        btree.Insert(64,44.0);
        btree.Insert(100,76.0);
        btree.toString();

        btree.Delete(21);
        btree.toString();
        System.out.println();

        btree.Delete(23);
        btree.toString();
        System.out.println();


        btree.Delete(24);
        btree.toString();
        System.out.println();


        btree.Delete(6);
        btree.toString();
        System.out.println();

        btree.Delete(9);
        btree.toString();
        System.out.println();

//        btree.Delete(100);
//        btree.toString();
//        System.out.println();
//
//
//        btree.Delete(64);
//        btree.toString();
//        System.out.println();
//
//
//        btree.Delete(21);
//        btree.toString();
//        System.out.println();
//
//        btree.Delete(23);
//        btree.toString();
//        System.out.println();
//
//        btree.Delete(24);
//        btree.toString();
//        System.out.println();
//
//
//        btree.Delete(84);
//        btree.toString();
//        System.out.println();
//
//
//        btree.Delete(28);
//        btree.toString();
//        System.out.println();
    }
}