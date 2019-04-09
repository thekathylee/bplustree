import java.util.ArrayList;
import java.util.Iterator;
import java.lang.Math;

public class BPlusTree <K extends Comparable<? super K>, V> //Comparable must be a superclass of K, which is extended by K
{
    //variables
    int order;
    Node root;

    //constructors
    public BPlusTree()
    {

    }

    //methods
    public void Initialize(int m)
    {
        this.order=m;
        root=new LeafNode();
    }

    /**
     * This method insets a new leafNode associated with a specific key
     * @param key: the key associated with the value
     * @param value: the data to be inserted
     * @return none
     */
    public void Insert (int key, double value)
    {
        if (locate(key)!=null){
            
        }
    }

    /**
     * This method deletes the leafNode associated with a specific key
     * @param key: the key associated with the value
     * @return none
     */
    public void Delete(int key)
    {

    }

    public Node locate(int key)
    {

    }

    /**
     * This method searches the tree for a specific key
     * @param key: the key associated with the value
     * @return the value associated with the key
     */
    public double Search(int key)
    {
        
    }

    /**
     * This method searches the tree within a range
     * @param key1: the starting value of the range, inclusive
     * @param key2: the ending value of the range, inclusive
     * @return values such that in the range between key1 and key2
     */
    public String Search(int key1, int key2)
    {

    }

    public String toString()
    {

    }

    private abstract class Node 
    {
        //variables
        ArrayList<Integer> keys;

        //methods
        void insertData(int key, double value);
        void deleteData(int key);
        boolean isFull();   
        boolean isLeaf();    
    }




    private class IndexNode extends Node 
    {
        //variables
        ArrayList<Node> children;

        //constructors
        IndexNode(){
            keys = new ArrayList<Integer>();
            children = new ArrayList<Node>();
        }
        IndexNode(int key, Node child){
            keys = new ArrayList<Integer>();
            children = new ArrayList<Node>();
            children.add(child);
        }

        //methods
        // void insertData(int key, double value)
        // {
        //     Node childNode = children.get(getChild(key)+1);
        //     childNode.insertData(key,value,this);
        // }

        void insertData(int key, double value)
        {
            Node childNode = getChild(key);
            while(!childNode.isLeaf()){
                childNode = childNode.getChild(key);
            }
            if (childNode.isFull()){              
                merge(key, value, childNode);
            }else {                             //if the node has space for new data
                deleteData(key);                //delete any entry that has the same key (no duplicates)
                insertData(key, value);
            }
        }

        void insertSibling(Node sibling)
        {
            if (this.isFull()){              
                this.merge(key, value, parent);
            }else {                             //if the node has space for new data
                deleteData(key);                //delete any entry that has the same key (no duplicates)
                int i = getChild(key);          //retrieve index to insert the values
                keys.add(i-1, key);               //update keys
                children.addAll(i, sibling.children);           //update associated values
            }
        }
        
        void deleteData(int key)
        {
            if(keys.contains(key)){
                int i=getChild(key);
                keys.remove(i-1);
                children.remove(i);
            }    
        }

        /**
         * This method searches for the index associated with the key
         * @param key: the target key value
         * @return the index of an existing value with the key, or the index in which the value would be if it DNE
         */
        Node getChild(int key){
            for(int i=0; i< keys.size(); i++){
                if (keys.get(i) < key) {             //if the element is less than the key, move to next element
                    continue; 
                }else return children.get(i+1);                 //If the element is in the list or the element is larger than the key, return the index
            }
        }

        /**
         * This method searches for the index associated with the key
         * @param key: the target key value
         * @return the index of an existing value with the key, or the index in which the value would be if it DNE
         */
        Node getChild(int key){
            for(int i=0; i< keys.size(); i++){
                if (keys.get(i) < key) {             //if the element is less than the key, move to next element
                    continue; 
                }else return children.get(i+1);                 //If the element is in the list or the element is larger than the key, return the index
            }
        }
        int locate(int key){
            for(int i=0; i< keys.size(); i++){
                if (keys.get(i) < key) {             //if the element is less than the key, move to next element
                    continue; 
                }else return i+1;                 //If the element is in the list or the element is larger than the key, return the index
            }
        }
        boolean isFull()
        {
            return (keys.size() >= order);
        }
        boolean isLeaf()
        {
            return false;
        }
        void merge(int key, double value, LeafNode childNode){
            int middleIndex = (int)(Math.ceil(order/2))-1;
            Node splitNode = new LeafNode();
            for(int i=middleIndex; i< keys.size();i++){
                if(childNode.isLeaf())
                splitNode.insertData(childNode.keys.get(i), childNode.values.get(i));
                childNode.deleteData(childNode.keys.get(i));
            }
            Node keyPointer = new IndexNode(splitNode.keys.get(0), splitNode);   //create new internal node with key and children(0) pointing to created leafnode
            this.insertSibling(keyPointer);                                       //insert internal node into parent
        }
        
        /** */
        void merge(int key, double value, IndexNode childNode){
            int middleIndex = (int)(Math.ceil(order/2))-1;
            Node splitNode = new IndexNode();
            for(int i=middleIndex; i< keys.size();i++){
                if(childNode.isLeaf())
                splitNode.insertData(childNode.keys.get(i), childNode.children.get(i));
                childNode.deleteData(childNode.keys.get(i));
            }
            Node keyPointer = new IndexNode(splitNode.keys.get(0), splitNode);   //create new internal node with key and children(0) pointing to created leafnode
            this.insertSibling(keyPointer);                                       //insert internal node into parent
        }

    }







    private class LeafNode extends Node
    {
        //variables
        LeafNode prev;
        LeafNode next;
        ArrayList<Double> values;
        // List<K> keys;

        //constructors
        LeafNode()
        {
            values = new ArrayList<Double>();
            keys = new ArrayList<Integer>();

        }

        boolean isLeaf()
        {
            return true;
        }

        //methods
        void insertData(int key, double value)
        {
            deleteData(key);                //delete any entry that has the same key (no duplicates)
            int i = locate(key);            //retrieve index to insert the values
            keys.add(i, key);               //update keys
            values.add(i, value);           //update associated values
        }


        /**
         * This method searches for the index associated with the key
         * @param key: the target key value
         * @return the index of an existing value with the key, or the index in which the value would be if it DNE
         */
        int locate(int key){
            for(int i=0; i< keys.size(); i++){
                if (keys.get(i) < key) {             //if the element is less than the key, move to next element
                    continue; 
                }else return i;                 //If the element is in the list or the element is larger than the key, return the index
            }
        }
        
        /**
         * This method deletes the data if it exists, and does nothing otherwise
         * @param key: the target key value
         * @return nothing
         */
        void deleteData(int key)
        {
            if(keys.contains(key)){
                int i=locate(key);
                keys.remove(i);
                values.remove(i);
            }            
        }

        boolean isFull()
        {
            return (keys.size() >= order);
        }

        void merge(int key, double value, IndexNode parent){
            int middleIndex = (int)(Math.ceil(order/2))-1;
            Node splitNode = new LeafNode();
            for(int i=middleIndex; i< keys.size();i++){
                splitNode.insertData(keys.get(i), values.get(i));
                this.deleteData(keys.get(i));
            }
            Node keyPointer = new IndexNode(splitNode.keys.get(0), splitNode);   //create new internal node with key and children(0) pointing to created leafnode
            parent.insertSibling(keyPointer);                                       //insert internal node into parent
        }
    }
}