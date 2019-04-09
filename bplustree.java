import java.util.ArrayList;
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

    }

    /**
     * This method deletes the leafNode associated with a specific key
     * @param key: the key associated with the value
     * @return none
     */
    public void Delete(int key)
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

    public void updateLeafList(Node currNode){
        if(currNode.isLeaf()){

        }else {
            for(int i=0; i < ((IndexNode)currNode).children.size(); i++){
                updateLeafList(((IndexNode)currNode).children.get(i));
            }
        }
    }

    private abstract class Node 
    {
        //variables
        ArrayList<Integer> keys;

        //methods   
        IndexNode split(){

        }
        boolean isFull(){
            
        }
        boolean isLeaf(){

        }
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

        IndexNode(int key){
            keys = new ArrayList<Integer>();
            children = new ArrayList<Node>();
            keys.add(key);
        }

        //methods
        IndexNode split(IndexNode fullNode){
            int midIndex = (int)Math.ceil(order/2);
            IndexNode midNode = new IndexNode(fullNode.keys.get(midIndex));
            IndexNode childNode = new IndexNode();
            for (int i= midIndex+1; i< order; i++){
                childNode.keys.add(fullNode.keys.get(i));
                childNode.children.add(fullNode.children.get(i));
                fullNode.keys.remove(i);
                fullNode.children.remove(i);
            }
            fullNode.keys.remove(midIndex);                 //need to deal w reallocating children of midNode when you combine trees
            midNode.children.add(1, childNode);
            return midNode;
        }

        void merge(IndexNode insertNode){
            int indexInsert=-1;
            for(int i=0; i< keys.size(); i++){
                if(insertNode.keys.get(0) < keys.get(i)){                       //if the key < current element, indexInsert is updated to current key
                    indexInsert=i;
                }else if (insertNode.keys.get(0) > keys.get(i)){                // if the key > current element, add key in proper location in index node
                    keys.add(indexInsert+1, insertNode.keys.get(0));            //!!!still need to adjust children
                    if(insertNode.children.size()==2){
                        children.add(indexInsert, insertNode.children.get(0));
                        children.add(indexInsert+1, insertNode.children.get(1));
                    }else if (insertNode.children.size()==1){
                        children.add(indexInsert, insertNode.children.get(0));
                    }
                    break;
                }else {                                                         // if the key exists, replace the element
                    keys.remove(indexInsert+1);
                    keys.add(indexInsert+1, insertNode.keys.get(0));
                    children.remove(indexInsert);
                    children.remove(indexInsert+1);
                    if(insertNode.children.size()==2){
                        children.add(indexInsert, insertNode.children.get(0));
                        children.add(indexInsert+1, insertNode.children.get(1));
                    }else if (insertNode.children.size()==1){
                        children.add(indexInsert, insertNode.children.get(0));
                    }
                }
            }
        }


        boolean isFull(){
            if(keys.size() >= (order-1)){
                return true;
            }
            else return false;
        }

        boolean isLeaf(){
            return false;
        }

    }



    private class LeafNode extends Node
    {
        //variables
        LeafNode prev;
        LeafNode next;
        ArrayList<Double> values;
        IndexNode parent;

        //constructors
        LeafNode()
        {
            keys = new ArrayList<Integer>();
            values = new ArrayList<Double>();
        }

        LeafNode(int key, double value)
        {
            keys = new ArrayList<Integer>();
            values = new ArrayList<Double>();
            keys.add(key);
            values.add(value);
        }

        //methods
        boolean isFull(){
            if(keys.size() >= (order-1)){
                return true;
            }
            else return false;
        }

        IndexNode split(LeafNode fullNode){
            int midIndex = (int)Math.ceil(order/2);
            IndexNode midNode = new IndexNode(fullNode.keys.get(midIndex));
            LeafNode childNode = new LeafNode();
            for (int i= midIndex; i< order; i++){
                childNode.keys.add(fullNode.keys.get(i));
                childNode.values.add(fullNode.values.get(i));
                fullNode.keys.remove(i);
                fullNode.values.remove(i);
            }
            midNode.children.add(1, childNode);
            return midNode;
        }

        boolean isLeaf(){
            return true;
        }

    }
    private class LeafList
    {
        //variables
        LeafNode head;
        LeafNode tail;

        //constructors
        LeafList(int key, double value)
        {
            head = new LeafNode(key, value);
            tail = new LeafNode(key, value);
        }

        LeafNode get(int index)
        {
            LeafNode temp = head;
            for(int i=0 ; i < index; i++)
            {
                temp=temp.next;
            }
            return temp;
        }

        void addLeaf(){
            LeafNode temp = head;
            while(temp.next!=null){
                
            }
        }

        //methods
        LeafNode locateLeaf(int key){
            LeafNode temp = head;
            while(key > temp.keys.get(temp.keys.size()-1)){
                if(key >= temp.next.keys.get(0)){
                    temp=temp.next;
                }
            }
            return temp;
        }



    }
}