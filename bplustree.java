import java.util.ArrayList;
import java.util.LinkedList;
import java.lang.Math;

public class BPlusTree //Comparable must be a superclass of K, which is extended by K
{
    //variables
    int order;
    Node root;
    LinkedList<LeafNode> LeafList =new LinkedList<LeafNode>();  

    //constructors
    public BPlusTree()
    {
        root();
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
            IndexNode midNode=currLeaf.split();
            IndexNode temp = currLeaf.parent;
            if(temp==null){                                  //if the root is overfilled, then set remaining tree as L child of created split tree and update root
                // System.out.println("here");
                if(midNode.children.get(0)==null) midNode.children.remove(0);
                midNode.children.add(0,currLeaf);
                root=midNode;
            }else {
                // System.out.println("is temp full?" + temp.isFull());
                // System.out.println("order:" + order);
                // System.out.println("temp key size: "+temp.keys.size());
                // System.out.println("temp key: "+temp.keys.get(0));
                while(temp.isFull()){
                    temp.merge(midNode);
                    midNode=temp.split();
                    if(temp.parent==null){                  //if the root is overfilled, then set remaining tree as L child of created split tree and update root
                        //midNode.children.add(0,temp);
                        root=midNode;
                        return;
                    }else {
                        temp=temp.parent;
                    }
                }
                if(midNode.children.get(0)!=null) midNode.children.get(0).setParent(temp);
                midNode.children.get(1).setParent(temp);
                temp.merge(midNode);
            }
            
        }else {
            locateLeaf(key).overfill(key,value);
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
        return 0.0;
    }

    /**
     * This method searches the tree within a range
     * @param key1: the starting value of the range, inclusive
     * @param key2: the ending value of the range, inclusive
     * @return values such that in the range between key1 and key2
     */
    public String Search(int key1, int key2)
    {
        return "";
    }

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

    static void writeNode(Node node, String prefix, boolean hasmoresibs) {
        System.out.println();        
        if (hasmoresibs) {
            System.out.print(prefix + "├── ");
        } else {
            System.out.print(prefix + "└── ");
        }
        for(int k: node.keys) {
            System.out.print(k+"  ");
        }

        String newprefix = prefix;
        if (hasmoresibs) {
            newprefix += "│   ";
        } else {
            newprefix += "    ";
        }
        if(!node.isLeaf()){
            for (int i = 0; i < ((IndexNode)node).children.size(); i++) {
                writeNode(((IndexNode)node).children.get(i), newprefix, i < (((IndexNode)node).children.size() - 1));
            }
        }
    }


    public LeafNode locateLeaf(int key){
        if(LeafList.size()==0){
            root = new LeafNode();
            return (LeafNode)root;
        }else {
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
    }
    
    public void root(){         //initializes root
        root = new LeafNode();
        root.parent=null;
    }

    private abstract class Node 
    {
        //variables
        ArrayList<Integer> keys;
        IndexNode parent;

        //methods   
        abstract IndexNode split();
        abstract boolean isFull();
        abstract boolean isLeaf();
        void setParent(IndexNode parent){
            this.parent=parent;
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
            IndexNode midNode = new IndexNode(keys.get(midIndex));
            IndexNode childNode = new IndexNode();
            for (int i= midIndex+1; i<= order; i++){                     //midNode will move elements after midIndex to childNode
                childNode.keys.add(keys.get(i));
                childNode.children.add(0,children.get(i));
                keys.remove(i);
                children.remove(i);
            }
            if(children.get(order)!=null){
                childNode.children.add(children.get(order));
                children.remove(order);
            }
            keys.remove(midIndex);    
            midNode.children.add(0, this);                              //placeholder for L child
            childNode.setParent(midNode);
            midNode.children.add(1, childNode);
            return midNode;
        }


        void merge(IndexNode insertNode){
            if(keys.size()==0){
                keys.add(insertNode.keys.get(0));
            }else{
                int indexInsert=-1;
                for(int i=0; i< keys.size();i++){
                    indexInsert=i;
                    if(insertNode.keys.get(0) > keys.get(i)){
                        continue;
                    }else if(insertNode.keys.get(0) == keys.get(i)){
                        keys.remove(i);
                        keys.add(indexInsert+1, insertNode.keys.get(0));
                        children.remove(indexInsert+1);
                        children.remove(indexInsert+2);
                        for(int j=0; j<insertNode.children.size(); j++){
                            children.add(indexInsert+1+j, insertNode.children.get(i));
                        }   
                        return;
                    }else {
                        break;
                    }
                }
                keys.add(indexInsert+1, insertNode.keys.get(0));
                for(int i=0; i<insertNode.children.size();i++){
                    if(insertNode.children.get(i)==null){
                    }else {
                        children.add(indexInsert+1+i, insertNode.children.get(i));
                    }
                }        
            }
        }

        


        boolean isFull(){
            if(keys.size() >= (order)){
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
            LeafList.add(this);
        }

        //methods
        boolean isFull(){
            if(keys.size() >= (order)){
                return true;
            }
            else return false;
        }

        IndexNode split(){
            int midIndex = (int)Math.ceil(order/2);
            IndexNode midNode = new IndexNode(keys.get(midIndex));
            LeafNode childNode = new LeafNode();
            for (int i= midIndex; i<= order; i++){
                childNode.keys.add(keys.get(midIndex));
                childNode.values.add(values.get(midIndex));
                keys.remove(midIndex);
                values.remove(midIndex);
            }
            midNode.children.add(0,null);
         //   this.parent.children.remove(this.parent.keys.size()+1);
            childNode.setParent(midNode);
            midNode.children.add(1, childNode);
            return midNode;
        }

        boolean isLeaf(){
            return true;
        }

        void overfill(int key, double value){
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
                        break;
                    }
                }
                keys.add(index+1,key);
                values.add(index+1,value);
            }
        }

    }

    public static void main(String[] args){
        BPlusTree btree = new BPlusTree();
        btree.Initialize(2);
        btree.Insert(3,10.0);
        btree.Insert(4,11.0);
        System.out.println("root: "+(btree.root).keys.get(0));
        System.out.println("root[b]: "+(btree.root).keys.get(1)+"\n\n");

        btree.Insert(5,17.0);
        System.out.println("num children: "+(((IndexNode)btree.root).children.size()));
        System.out.println("root: "+(btree.root).keys.get(0));
        System.out.println("child[0]: "+((IndexNode)btree.root).children.get(0).keys.get(0));
        System.out.println("child[1]: "+((IndexNode)btree.root).children.get(1).keys.get(0));
        System.out.println("child[1b]: "+((IndexNode)btree.root).children.get(1).keys.get(1));
        btree.toString();
        System.out.println("\n\n");

        btree.Insert(6,12.0);
        System.out.println("root size: "+(btree.root).keys.size());
        System.out.println("num children: "+(((IndexNode)btree.root).children.size()));
        System.out.println("root: "+(btree.root).keys.get(0));
        System.out.println("root[b]: "+(btree.root).keys.get(1));
        System.out.println("child[0]: "+((IndexNode)btree.root).children.get(0).keys.get(0));
        System.out.println("child[1]: "+((IndexNode)btree.root).children.get(1).keys.get(0));
        System.out.println("child[2]: "+((IndexNode)btree.root).children.get(2).keys.get(0));
        System.out.println("child[2b]: "+((IndexNode)btree.root).children.get(2).keys.get(1));
  //      System.out.println("child[3]: "+((IndexNode)btree.root).children.get(3).keys.get(0));
        btree.toString();
        System.out.println("\n\n");


        btree.Insert(7,22.0);
        System.out.println("root size: "+(btree.root).keys.size());
        System.out.println("num children: "+((IndexNode)btree.root).children.size());
        System.out.println("root: "+(btree.root).keys.get(0));
        System.out.println("child[0]: "+((IndexNode)btree.root).children.get(0).keys.get(0));
        System.out.println("child[1]: "+((IndexNode)btree.root).children.get(1).keys.get(0));
        System.out.println("num children of child[0]: "+((IndexNode)((IndexNode)btree.root).children.get(0)).children.size());
        System.out.println("gchild[0:0]: "+((IndexNode)((IndexNode)btree.root).children.get(0)).children.get(0).keys.get(0));
        System.out.println("gchild[0:1]: "+((IndexNode)((IndexNode)btree.root).children.get(0)).children.get(1).keys.get(0));
        System.out.println("gchild[1:0]: "+((IndexNode)((IndexNode)btree.root).children.get(1)).children.get(0).keys.get(0));
        System.out.println("num children: "+((IndexNode)((IndexNode)btree.root).children.get(1)).children.size());
        System.out.println("gchild[1:1]: "+((IndexNode)((IndexNode)btree.root).children.get(1)).children.get(1).keys.get(0));
   //     System.out.println("gchild[1:1b]: "+((IndexNode)((IndexNode)btree.root).children.get(1)).children.get(1).keys.get(1)+"\n\n");
        btree.toString();
    }
}