import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.*;

// vertex structure of the graph
class Node {
   int label;
   java.util.ArrayList<AdjacentNode> adjlist; //stores arraylist of neighbouring nodes
   public Node(int label, ArrayList<AdjacentNode> list) {
       this.label = label;
       this.adjlist = list;
   }
}

// fibonacci node structure
class FibHeapNode {
    int key;
    int value;
    int degree;
    FibHeapNode parent;
    FibHeapNode child,left,right;
    boolean cut;
    }
//class for adjacent nodes to be stored in the adjacency list of each node
class AdjacentNode {
    int label;
    int weight;
    public AdjacentNode(int label, int weight) {
        this.label = label;
        this.weight = weight;
    }
}

//main class
public class ssp {
      public    Node[] Nodes;
     private  boolean connected = false;
       boolean[] visited;//for dfs
     //private File fileInput;
     private   int nodesCount;
     private   int edgesCount;
     private   int cost = 1000;
     private   int[] distance;
     private   int[] path;
       FibHeapNode head;   //pointer to minimum node
       FibHeapNode[] treeMin;
       FibHeapNode temp1;
           static File fileInput;
public   int sourceNode;

     public static void main(String[] args) throws FileNotFoundException
     {
            if(args.length==0)
            {
                System.out.println("Argument required:\nExample: java dijkstra <filename>.txt\n");
            	return;

            }

            ssp obj =new ssp();
            String filename= args[0];
            //System.out.println(filename);
            fileInput = new File(filename);
            obj.FileInputGraph();
            //fibonacci heap scheme
            System.out.println("Executing Dijkshtra using Fibonacci heap!!!");

            String []result=new String[2];
            result=obj.ShortestPathFibonacciHeap(Integer.parseInt(args[1]),Integer.parseInt(args[2]));
            obj.printResult(result);
            }
public  void printResult(String []a)
    {
        int weight=Integer.parseInt(a[0]);
        System.out.println("Shortest Path="+weight);
                System.out.println(a[1]);


        }
public   void FileInputGraph() throws FileNotFoundException {  //to take input from a file

        Scanner scan = new Scanner(fileInput);
            sourceNode = 0;//scan.nextInt();
            nodesCount = scan.nextInt();
            int edgesno = scan.nextInt();

            Nodes = new Node[nodesCount];    //create an array equal to number of nodes
            for (int i = 0; i < nodesCount; i++) {
                Nodes[i] = new Node(i, new ArrayList<AdjacentNode>());
            }
            int count = 0;
            while (count < edgesno) {     //read all edges and add them to the adjacency lists
                int edge1, edge2, c;
                edge1 = scan.nextInt();
                edge2 = scan.nextInt();
                c = scan.nextInt();
                Nodes[edge1].adjlist.add(new AdjacentNode(edge2, c));
                Nodes[edge2].adjlist.add(new AdjacentNode(edge1, c));
                count++;
            }

    }


    public    String[] ShortestPathFibonacciHeap(int source, int destNode) {  //Shortest path using fibonacci heap scheme

    	if((source<0)||(destNode>(nodesCount-1))){

    		String []result=new String[2];
    		result[0]=String.valueOf(0);
    		result[1]=String.valueOf(0);
    		return result;
    	}
    	int c=0,z=0;
    	distance = new int[nodesCount];
    	path= new int[nodesCount];
    	path[source]=0;
        visited = new boolean[Nodes.length];
        FibHeapNode[] f = new FibHeapNode[nodesCount];  //points to the fibonacci heap
        for (int k = 0; k < nodesCount; k++) {
            distance[k] = Integer.MAX_VALUE;
        }
        distance[source] = 0;
        for (int i = 0; i < Nodes.length; i++) {
            f[i] = Insert(distance[Nodes[i].label], Nodes[i].label);
        }
        while (head != null) {
            FibHeapNode minNode = DeleteMin();   //to get next minimum node
            visited[minNode.value] = true;
            distance[minNode.value] = minNode.key;
            f[minNode.value] = null;


            ArrayList<AdjacentNode> adjNodes = Nodes[minNode.value].adjlist;
            for (AdjacentNode thisNode : adjNodes) {   //update distances of neighbours using Decrease_Key function
                int newDist = minNode.key + thisNode.weight;
                if (f[thisNode.label] != null && newDist < f[thisNode.label].key) {
                    Decrease_Key(f[thisNode.label], newDist);
                    path[thisNode.label]=minNode.value;
                }

            }
            if(minNode.value==destNode)
        	{
        	   temp1=minNode;
        	   break;
        	}

        }
        String []result=new String[2];
        result[0]=String.valueOf(distance[destNode]);
        c=destNode;
        String p="";
        p=String.valueOf(destNode);
        while(path[c]!=source)
        {
        	p=String.valueOf(path[c])+" "+p;
        	c=path[c];
        }
        p=String.valueOf(source)+" "+p;
        result[1]=p;
        return result;
  }

//Fibonacci heap Functions

    public  void Add(FibHeapNode f) {  //to add a fibonacci node to the heap
        f.parent = null;
        if (head == null) {
            head = f.left = f.right = f;
            return;
        }
        f.left = head.left;
        f.right = head;
        head.left.right = f;
        head.left = f;
        if (head.key > f.key) {
            head = f;
        }
    }

    public  FibHeapNode Insert(int key, int value) {  //creates a new fibonacci heap node and calls the Add function
        FibHeapNode temp = new FibHeapNode();
        temp.value = value;
        temp.key = key;
        Add(temp);
        return temp;
    }

    public  FibHeapNode DeleteMin() {         //Deletes the minimum element from the heap
        treeMin = new FibHeapNode[Nodes.length];
        if (head == null) {
            return null;
        }
        FibHeapNode headNode = head;
        FibHeapNode present, previous;
        if ((present = head.child) != null) {
            do {
                previous = present;
                present = present.right;
                Join(previous);
            } while (present != head.child);
        }
        for (present = head.right; present != head;) {
            previous = present;
            present = present.right;
            Join(previous);
        }
        head = null;
        for (int i = 0; i < treeMin.length; i++) {
            if (treeMin[i] != null) {
                Add(treeMin[i]);
                treeMin[i] = null;
            }
        }
        return headNode;  //returns new minimum element
    }

    public  void Join(FibHeapNode node) {  //joins two trees by making one the child of another
        while (treeMin[node.degree] != null) {
            FibHeapNode newhead, newChild;
            if (treeMin[node.degree].key >= node.key) {

                newhead = node;
                newChild = treeMin[node.degree];
            }
            else { //treeMin stores trees of different degrees upto max degree
                newhead = treeMin[node.degree];
                newChild = node;
            }
            newChild.parent = newhead;
            newChild.cut = false;
            if (newhead.child != null)
            {
                newChild.left = newhead.child.left;
                newChild.right = newhead.child;
                newhead.child.left.right = newChild;
                newhead.child.left = newChild;

            }
            else
            {
                newhead.child = newChild.left = newChild.right = newChild;
            }
            node = newhead;
            treeMin[node.degree] = null;
        }
        treeMin[node.degree] = node;
    }
    public  void Decrease_Key(FibHeapNode node, int newKey) {   //decreases key of a given fibonacci node
        FibHeapNode parent = node.parent;
        node.key = newKey;
        if (parent == null) {
            if (head.key > node.key) {
                head = node;
            }
            return;
        }
        if (node.key < parent.key) {
            Remove(node);
            Add(node);
            for (node = parent, parent = node.parent; node.cut == true && parent != null; node = parent, parent = parent.parent) {  //cascading cut
                Remove(node);
                Add(node);
            }
            node.cut = true;
        }
    }

    public  void Remove(FibHeapNode n) {   //removes an arbitrary node from the heap
        FibHeapNode parent = n.parent;
        if (n.right == n) {
            parent.child = null;
        } else {
            n.left.right = n.right;
            n.right.left = n.left;
            if (parent.child == n) {
                parent.child = n.right;
            }
        }
    }
 //Result Printing

}

