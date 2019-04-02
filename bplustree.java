public class BPlusTree 
{
    //variables
    int order;
    IndexNode root;

    //constructors
    public bplustree()
    {

    }

    //methods
    public void Initialize(int m)
    {
        this.order=m;
        root=new Node();
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
    public void Delete(int key){

    }

    /**
     * This method searches the tree for a specific key
     * @param key: the key associated with the value
     * @return the value associated with the key
     */
    public double Search(int key){

    }

    /**
     * This method searches the tree within a range
     * @param key1: the starting value of the range, inclusive
     * @param key2: the ending value of the range, inclusive
     * @return values such that in the range between key1 and key2
     */
    public String Search(int key1, int key2){

    }

    public String toString()
    {

    }

}

private abstract class Node 
{
    //variables


    //methods
}

private class IndexNode extends Node 
{
    //variables

    //constructors

    //methods

}
private class LeafNode extends Node
{
    //variables

    //constructors

    //methods

}