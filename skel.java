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

    private abstract class Node 
    {
        //variables

        //methods   
    }




    private class IndexNode extends Node 
    {
        //variables
        ArrayList<Node> children;

        //constructors
        IndexNode(){

        }

    }



    private class LeafNode extends Node
    {
        //variables
        LeafNode prev;
        LeafNode next;

        //constructors
        LeafNode()
        {

        }

    }
}