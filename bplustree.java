import java.util.ArrayList;
import java.util.LinkedList;
import java.lang.Math;

public class BPlusTree <K extends Comparable<? super K>, V> //Comparable must be a superclass of K, which is extended by K
{
    //variables
    int order;
    Node root;
    LinkedList<LeafNode> LeafList =new LinkedList<LeafNode>();  

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
            IndexNode midNode=currLeaf.split();
            IndexNode temp = currLeaf.parent;
            while(temp.isFull()){
                temp.merge(midNode);
                midNode=temp.split();
                if(temp.parent==null){                  //if the root is overfilled, then set remaining tree as L child of created split tree and update root
                    midNode.children.add(0,temp);
                    root=midNode;
                    return;
                }else {
                    temp=temp.parent;
                }
            }
            temp.merge(midNode);
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

    // public void updateLeafList(Node currNode){
    //     if(currNode.isLeaf()){

    //     }else {
    //         for(int i=0; i < ((IndexNode)currNode).children.size(); i++){
    //             updateLeafList(((IndexNode)currNode).children.get(i));
    //         }
    //     }
    // }

    LeafNode locateLeaf(int key){
        LeafNode temp = LeafList.getFirst();
        for(int i=0; i< LeafList.size()-1;i ++ ){
            if(key > LeafList.get(i).keys.get(temp.keys.size()-1)){
                if(key >= LeafList.get(i+1).keys.get(0)){
                    continue;
                }else {
                    return LeafList.get(i);
                }
            }
        }
        return LeafList.get(LeafList.size()-1);
    }

    private abstract class Node 
    {
        //variables
        ArrayList<Integer> keys;
        IndexNode parent;

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
        IndexNode split(){
            int midIndex = (int)Math.ceil(order/2);
            IndexNode midNode = new IndexNode(this.keys.get(midIndex));
            IndexNode childNode = new IndexNode();
            for (int i= midIndex+1; i< order; i++){
                childNode.keys.add(this.keys.get(i));
                childNode.children.add(this.children.get(i));
                this.keys.remove(i);
                this.children.remove(i);
            }
            this.keys.remove(midIndex);                 
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
        ArrayList<Double> values;

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

        IndexNode split(){
            int midIndex = (int)Math.ceil(order/2);
            IndexNode midNode = new IndexNode(this.keys.get(midIndex));
            LeafNode childNode = new LeafNode();
            for (int i= midIndex; i< order; i++){
                childNode.keys.add(this.keys.get(i));
                childNode.values.add(this.values.get(i));
                this.keys.remove(i);
                this.values.remove(i);
            }
            midNode.children.add(1, childNode);
            return midNode;
        }

        boolean isLeaf(){
            return true;
        }

        void overfill(int key, double value){
            for(int i=0; i< keys.size();i++){
                if(key > keys.get(i)){
                    continue;
                }else if(key == keys.get(i)){
                    values.remove(i);
                    values.add(i,value);
                }else {
                    keys.add(i, key);
                    values.add(i, value);
                }
            }
        }
        

    }


    // private class LeafList
    // {
    //     //variables
    //     LeafNode head;
    //     LeafNode tail;

    //     //constructors
    //     LeafList(int key, double value)
    //     {
    //         head = new LeafNode(key, value);
    //         tail = new LeafNode(key, value);
    //     }

    //     LeafNode get(int index)
    //     {
    //         LeafNode temp = head;
    //         for(int i=0 ; i < index; i++)
    //         {
    //             temp=temp.next;
    //         }
    //         return temp;
    //     }

    //     void addLeaf(){
    //         LeafNode temp = head;
    //         while(temp.next!=null){

    //         }
    //     }

    //     //methods
    //     LeafNode locateLeaf(int key){
    //         LeafNode temp = head;
    //         while(key > temp.keys.get(temp.keys.size()-1)){
    //             if(key >= temp.next.keys.get(0)){
    //                 temp=temp.next;
    //             }
    //         }
    //         return temp;
    //     }



    // }
}