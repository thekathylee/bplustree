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
        root.parent=null;
    }

    //methods
    public void Initialize(int m)
    {
        order=m;
    }

    /**
     * This method inserts a new leafNode associated with a specific key
     * @param key: the key associated with the value
     * @param value: the data to be inserted
     * @return none
     */
    public void Insert (int key, double value)
    {
        if(locateLeaf(key).isFull()){
            LeafNode currLeaf= locateLeaf(key);
            currLeaf.overfill(key, value);
            System.out.println("currLeaf key 1: "+ currLeaf.keys.get(0));
            System.out.println("currLeaf key 1 should now be 1: "+ currLeaf.keys.get(1));
            if(currLeaf.keys.size() >order){               //if the key already existed, overfill() would have replaced the element and there's nothing left to be done
                System.out.println("init, key val inserted is: "+ key);
                IndexNode mom =currLeaf.parent;
                IndexNode midNode = currLeaf.split();
                if(mom==null){                                  //if the root is overfilled, then set remaining tree as L child of created split tree and update root
                    System.out.println("mom is null");
                    if(midNode.children.get(0)==null) midNode.children.remove(0);
                    midNode.children.add(0,currLeaf);
                    root=midNode;
                }else {
                    if(midNode.children.get(0)!=null) midNode.children.get(0).setParent(mom);
                    if(midNode.children.get(1)!=null) midNode.children.get(1).setParent(mom);
                    mom.merge(midNode);
                    System.out.println("mom key: "+ mom.keys.get(0)+"\nmom keys size: "+mom.keys.size()+"\nmom key2: "+ mom.keys.get(1));
                    while(mom.keys.size()>order){
                        midNode=mom.split();
                        mom.setParent(midNode);
                        if (midNode.parent == null){
                            root = midNode;
                            return;
                        }else {
                            mom = mom.parent;
                            System.out.println("size of midNode: " +midNode.keys.size() );
                            mom.merge(midNode);
                        }
                        System.out.println("mid node root: " + midNode.keys.get(0) + "\nnum children: "+midNode.children.size());
                    }
                    System.out.println("mid node root: " + midNode.keys.get(0) + "\nnum children: "+midNode.children.size());
                    //parent.merge(midNode);
                    System.out.println("new root count "+ root.keys.size()+"\n key1: "+ root.keys.get(0));
                }
            }
            
        }else {
//             if(root.isLeaf() && root.keys.size()==0){
//            	 ((LeafNode)root).overfill(key,value);
//            	 System.out.println("HAPPEN ONCE, key value should be 3: "+key);
//             }else if(root.isLeaf()){
//                 IndexNode newRoot = new IndexNode(root.keys.get(0));
//                 LeafNode firstValue = new LeafNode(newRoot);
//                 LeafList.removeFirst();
//                 firstValue.keys.add(key);
//                 firstValue.values.add(value);
//                 newRoot.children.add(firstValue);
//                 root = newRoot;
//            	 System.out.println("HAPPEN ONCE, key value should be 4: "+key);
//
//             }else {
                locateLeaf(key).overfill(key,value);
   //          }

        }
    }

    /**
     * This method deletes the leafNode associated with a specific key
     * @param key: the key associated with the value
     * @return none
     */
    public void Delete(int key)
    {
        LeafNode temp = locateLeaf(key);
        int index= getIndexOf(key, temp);
        temp.keys.remove(index);
        temp.values.remove(index);
        if(temp.next.keys.size()>1 && temp.parent == temp.next.parent){
            temp.keys.add(temp.next.keys.get(0));
            temp.values.add(temp.next.values.get(0));
            temp.next.keys.remove(0);
            temp.next.values.remove(0);
            index = getIndexOf(temp.keys.get(0), temp.parent);
            temp.parent.keys.remove(index);
            temp.parent.keys.add(index, temp.next.keys.get(0));
        } else if(temp.parent == temp.prev.parent && temp.prev.keys.size()>1){
            int prevSize=temp.prev.keys.size();
            temp.keys.add(temp.prev.keys.get(prevSize-1));
            temp.values.add(temp.prev.values.get(prevSize-1));
            temp.prev.keys.remove(prevSize-1);
            temp.prev.values.remove(prevSize-1);
            index = getIndexOf(temp.keys.get(0), temp.parent);
            temp.parent.keys.remove(index);
            temp.parent.keys.add(index, temp.keys.get(0));

        } else if(temp.next.keys.size()==1){                 //if Rsib/Lsib == 1, delete key and remove in-between key in parent (index i-1, unless i=0 then index 0)
            if(index==0) temp.parent.keys.remove(0);
            else temp.parent.keys.remove(index-1);
        }













        // 
        // if(!parent.isFull()){
        //     parent.key = gparent.key;
        //     if(uncle.size() > 1){
        //         gparent.key = uncle.key;
        //         //while loop to handle when cousin is index node
        //         LeafNode temp = uncle.children.get(0);
        //         uncle.children.remove(0);
        //         parent.children.add(temp);
        //     } else {
        //         parent.keys.add(gparent.keys.get(0));
        //         parent.keys.add(uncle.keys.get(0));
        //         for(Node n: uncle.children){
        //             parent.children.add(n);
        //         }
        //         uncle.children.removeAll();
        //         //loop to remove app children from gparent except parent
        //         parent.prev=null;
        //         root=parent;

        //     }
        // }


    }

    /**
     * This method searches the tree for a specific key
     * @param key: the key associated with the value
     * @return the value associated with the key
     */
    public double Search(int key)
    {
        LeafNode temp = locateLeaf(key);
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
        LeafNode temp = locateLeaf(key1);
        String str="";
        str+=extract(key1, key2, temp);
        int currVal=temp.keys.get(temp.keys.size()-1);
        while(currVal < key2 && temp.next!=null){
            temp=temp.next;
            str+=", "+ extract(key1, key2, temp);
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
            if(node.keys.get(i) == key1 || i==0){
                str= str + node.values.get(i);
            }else if (node.keys.get(i) > key1 && node.keys.get(i) <= key2){
                str= str + ", " + node.values.get(i);
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
    public LeafNode locateLeaf(int key){
        if(LeafList.size()==0){
            root = new LeafNode();
            return (LeafNode)root;
        }else {
            LeafNode temp = LeafList.getFirst();
            for(int i=0; i< LeafList.size()-1;i ++ ){
                temp=LeafList.get(i);
                if(key > temp.keys.get(temp.keys.size()-1)){ //if key > last element of ith element of leaflist
                    if(key >= LeafList.get(i+1).keys.get(0)){           //if key >= first element of i+1 th element of leaflist
                        continue;
                    }else {
                        return LeafList.get(i);
                    }
                }else {                                                //if key <= last element of ith element
                    return LeafList.get(i);
                }
            }
            return LeafList.get(LeafList.size()-1);
        }
    }


    public int getIndexOf(int key, Node node){
        int index = -1;
        for(int i = 0; i < node.keys.size(); i++) {
            if(node.keys.get(i) == key){
                index = i;
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
        ArrayList<Integer> keys;
        IndexNode parent;
        

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
    }



    /** ----------------------------------------------------------------------------------------------------------------------------------------------
     * 
     * 
     * 
     *  INDEX NODE CLASS
     * 
     * 
     */

    private class IndexNode extends Node 
    {
        //variables
        ArrayList<Node> children;

        //constructors
        IndexNode(){
            keys = new ArrayList<Integer>();
            children = new ArrayList<Node>();
            parent=null;
        }

        IndexNode(int key){
            keys = new ArrayList<Integer>();
            children = new ArrayList<Node>();
            keys.add(key);
            parent=null;
        }

        //methods

        /**
         *  This function will return the newly "split" node and expects that it is working with an "overfilled" node
         *  This function also removes split values from original node
         */
        IndexNode split(){
            int midIndex = (int)Math.ceil(order/2);
            IndexNode midNode = new IndexNode(keys.get(midIndex));
            IndexNode childNode = new IndexNode();
            for (int i= midIndex+1; i<= order; i++){                     //midNode will move elements after midIndex to childNode
                childNode.keys.add(keys.get(i));
                children.get(i).setParent(childNode);
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
            if(this.parent!=null){
                midNode.setParent(this.parent);
            } 
            this.setParent(midNode);
            System.out.println("this.key.0(should be 3): "+ this.keys.get(0)+"\nparent key"+ midNode.keys.get(0));
            midNode.children.add(0, this);                              //placeholder for L child
            childNode.setParent(midNode);
            midNode.children.add(1, childNode);
            return midNode;
        }

        /**
         * @param insertNode the Node that we wish to merge with the calling node
         *  This function merges the key and corresponding children of insertNode with the calling node
         */
        void merge(IndexNode insertNode){
            System.out.println("insertNode size: "+ insertNode.keys.size());
            if(keys.size()==0){
                keys.add(insertNode.keys.get(0));
            }else{
                int indexInsert=-1;
                for(int i=0; i< keys.size();i++){
                    indexInsert=i;
                    if(insertNode.keys.get(0) > keys.get(i)){
                        System.out.println("init, index insert is: " + indexInsert);

                    }else if(insertNode.keys.get(0) == keys.get(i)){
                        keys.remove(i);
                        System.out.println("insertNode size: "+ insertNode.keys.size());
                        keys.add(i, insertNode.keys.get(0));
                        children.remove(i);
                        children.remove(i+1);
                        for(int j=0; j<insertNode.children.size(); j++){
                            children.add(i+j, insertNode.children.get(i));
                        }   
                        return;
                    }else {                 //if insertNode < current key
                        indexInsert=i-1;
                        break;
                    }
                }
                keys.add(indexInsert+1, insertNode.keys.get(0));
                System.out.println("key inserted:" +keys.get(indexInsert+1));
                for(int i=0; i<insertNode.children.size();i++){
                    if(insertNode.children.get(i)==null){
                    }else {
                        children.add(indexInsert+1+i, insertNode.children.get(i));
                        System.out.println("Children count of temp: "+ children.size());
                        System.out.println("key count of temp: "+ keys.size());

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

    private class LeafNode extends Node
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
            updateLeafPointers();
            parent=null;

        }
        LeafNode(IndexNode parent)
        {
            keys = new ArrayList<Integer>();
            values = new ArrayList<Double>();
            LeafList.add(this);
            updateLeafPointers();
            setParent(parent);
        }

        //methods

        IndexNode split(){
            int midIndex = (int)Math.ceil(order/2);
            IndexNode midNode = new IndexNode(keys.get(midIndex));
            LeafNode childNode = new LeafNode(midNode);
            for (int i= midIndex; i<= order; i++){
                childNode.keys.add(keys.get(midIndex));
                childNode.values.add(values.get(midIndex));
                keys.remove(midIndex);
                values.remove(midIndex);
            }
            this.setParent(midNode);
            System.out.println("this.key.0(should be 3): "+ this.keys.get(0)+"\nparent key"+ midNode.keys.get(0));
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
                        continue;
                    }else if(key == keys.get(i)){
                        keys.remove(i);
                        keys.add(i,key);
                        values.remove(i);
                        values.add(i,value);
                        return;
                    }else {
                        keys.add(0,key);
                        values.add(0,value);
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
        // System.out.println("root: "+(btree.root).keys.get(0));
        // System.out.println("root[b]: "+(btree.root).keys.get(1)+"\n\n");

        btree.Insert(5,17.0);
        // System.out.println("num children: "+(((IndexNode)btree.root).children.size()));
        // System.out.println("root: "+(btree.root).keys.get(0));
        // System.out.println("child[0]: "+((IndexNode)btree.root).children.get(0).keys.get(0));
        // // System.out.println("child[1]: "+((IndexNode)btree.root).children.get(1).keys.get(0));
        // // System.out.println("child[1b]: "+((IndexNode)btree.root).children.get(1).keys.get(1));
        btree.toString();
         System.out.println("\n\n");

        btree.Insert(6,12.0);
        // System.out.println("root size: "+(btree.root).keys.size());
        // System.out.println("num children: "+(((IndexNode)btree.root).children.size()));
        // System.out.println("root: "+(btree.root).keys.get(0));
        // System.out.println("root[b]: "+(btree.root).keys.get(1));
        // System.out.println("child[0]: "+((IndexNode)btree.root).children.get(0).keys.get(0));
        // System.out.println("child[1]: "+((IndexNode)btree.root).children.get(1).keys.get(0));
        // System.out.println("child[2]: "+((IndexNode)btree.root).children.get(2).keys.get(0));
        // System.out.println("child[2b]: "+((IndexNode)btree.root).children.get(2).keys.get(1));
        // btree.toString();
        // System.out.println("\n\n");

        btree.toString();
         System.out.println("\n\n");
        btree.Insert(7,22.0);
        btree.toString();
//        System.out.println("root size: "+(btree.root).keys.size());
//        System.out.println("num children: "+((IndexNode)btree.root).children.size());
//        System.out.println("root: "+(btree.root).keys.get(0));
//     //   System.out.println("child[0]: "+((IndexNode)btree.root).children.get(0).keys.get(0));
//        // System.out.println("child[1]: "+((IndexNode)btree.root).children.get(1).keys.get(0));
//        //System.out.println("num children of child[0]: "+((IndexNode)((IndexNode)btree.root).children.get(0)).children.size());
//    //    System.out.println("gchild[0:0]: "+((IndexNode)((IndexNode)btree.root).children.get(0)).children.get(0).keys.get(0));
//     //   System.out.println("gchild[0:1]: "+((IndexNode)((IndexNode)btree.root).children.get(0)).children.get(1).keys.get(0));
//        System.out.println("num children of child[0]: "+((IndexNode)((IndexNode)btree.root).children.get(1)).children.size());
//        System.out.println("gchild[1:0]: "+((IndexNode)((IndexNode)btree.root).children.get(1)).children.get(0).keys.get(0));
//        System.out.println("num children: "+((IndexNode)((IndexNode)btree.root).children.get(1)).children.size());
//        System.out.println("gchild[1:1]: "+((IndexNode)((IndexNode)btree.root).children.get(1)).children.get(1).keys.get(0));
        btree.toString();
        System.out.println("\n\n");

        // System.out.println(btree.Search(7));
        // System.out.println(btree.Search(3,5));
        // LeafNode temp = btree.locateLeaf(3);
        // System.out.println("key of temp.next: "+temp.next.keys.get(0));
        // System.out.println("Search [3:3]: "+btree.Search(3,3));
        // System.out.println("Search [3:4]: "+btree.Search(3,4));
        // System.out.println("Search [3:5]: "+btree.Search(3,5));
        // System.out.println("Search [3:6]: "+btree.Search(3,6));
        // System.out.println("Search [3:7]: "+btree.Search(3,7));
        // System.out.println("Search [3:9]: "+btree.Search(3,9));
        // btree.Insert(7,62.0);
        // btree.toString();
        // System.out.println(btree.Search(7));
        // System.out.println("value of 3 prior to insert (should be 10.0):"+btree.Search(3));
        // btree.Insert(3,102.0);
        // btree.toString();
        // System.out.println(btree.Search(3));
     //   btree.Insert(8,12.0);
       // btree.toString();
        btree.Insert(2,12.0);
        btree.toString();
        btree.Insert(9,15.0);
        System.out.println("adding 9 now: ");
        btree.toString();
        System.out.println("\n\n");
        System.out.println("child[1b]: "+((IndexNode)btree.root).children.get(1).keys.size());
     //   System.out.println("child[1b]: "+((IndexNode)btree.root).children.get(1).keys.get(1));
        // System.out.println("plz be 7: "+ ((IndexNode)btree.root).children.get(1).keys.get(1));
        // System.out.println("plz be 2: "+ ((IndexNode)((IndexNode)btree.root).children.get(1)).children.size());
        System.out.println("plz be 1: "+ (btree.root.keys.size()));
        btree.Insert(1,12.0);
        btree.toString();

       
       // System.out.println("size of child 1: "+((IndexNode)((IndexNode)btree.root).children.get(1)).children.size());
    //    System.out.println("gchild[0:0]: "+((IndexNode)((IndexNode)btree.root).children.get(1)).children.size());

    }
}