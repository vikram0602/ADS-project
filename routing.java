
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/*
Main class for routing
*/
public class routing {

    private static int source=0;
	private static int dest=0;
	public static HashMap <Integer,String> vertexToIp=new HashMap<Integer, String>();
	public static List<Trie> nodesTrees=new ArrayList<Trie>();
    public static void main(String []args){
        routDijkstra myObj = null;
            if(args.length==0)
            {
                System.out.println("Argument required:\nExample: java Routing <filename1>.txt <filename2>.txt <source> <destination>\n");
            	return;

            }

            if(args.length!=4)
            {
            	System.out.println("Invalid Number of Parameters try again..");
            	return;
            }

        try{
        	BufferedReader infile=new BufferedReader(new FileReader(args[1]));
            source=Integer.parseInt(args[2]);
            dest=Integer.parseInt(args[3]);
        	int num=0;
        	String ip;
            /*
            Get the ip of all the nodes from the second file and
            put in the hash table in the form <int(nodenumber), ip>
            */
        	while((ip=infile.readLine())!=null)
            	{
            		if(ip.length()>1){
    	        		vertexToIp.put(num,ip);
    	        		num++;
            		}
            	}

            	//Make an object of routDijkstra class for retrieving the shortest path of
            	//each node from the rest
            	myObj=new routDijkstra(args[0]);
            	Trie tempTrie;
            	nodesTrees.clear();
            	for(int i=0;i<num;i++)
            	{
            		//construct binary trie for every vertex and insert all <ip,next_node> pairs to it
            		//
            		tempTrie=new Trie();

            		for(int j=0;j<num;j++)
            		{
            			if(i==j)
            				continue;
            			String []r=myObj.ShortestPathFibonacciHeap(i,j);
            			if(Integer.parseInt(r[0])==Integer.MAX_VALUE)
            				continue;
            			String []path=r[1].split(" ");
            			String ipValue=vertexToIp.get(j);
            			String binaryIP=ipToString(ipValue);
            			int nextNode=Integer.parseInt(path[1]);
            			tempTrie.add(binaryIP,nextNode);
            		}

            		tempTrie.postOrderRemoval();
            		nodesTrees.add(tempTrie);
            	}
                /*
                Rterieve the result of shortest path for
                source-dest either using dijsktra or
                using the tries of each and calculating the length from the input file
                we use the dijsktra for convinience
                */
            	String []result=myObj.ShortestPathFibonacciHeap(source, dest);
                int weight=Integer.parseInt(result[0]);

                /*The weight go here*/
                System.out.println(weight);
                /*Path in the form of prefixes of to next node*/
                String path="";
              	String destIp=ipToString(vertexToIp.get(dest));
              	Trie temp=nodesTrees.get(source);
              	String longPrefix=temp.longestPrefixOf(destIp);
              	int nodeValue=temp.get(longPrefix);
              	path+=longPrefix;
                /*Traverse through each trie i.e of next node
                and find the prefix to the dest getting
                the next node
                */
              	while(nodeValue!=dest){

              		temp=nodesTrees.get(nodeValue);
              		longPrefix=temp.longestPrefixOf(destIp);
              		nodeValue=temp.get(longPrefix);
              		path+=" "+longPrefix;
                }
                System.out.println(path);

            }catch(IOException e){
            	e.printStackTrace();
            }
    }

    /*Function to convert ip address to binary string
    later on it may be used to put data into the binary trie*/
    public static String ipToString(String ip){
    	ip=ip.replace('.', ' ');
    	String []tokens=ip.split(" ");
    	String binaryString="";
    	for(int i=0;i<tokens.length;i++)
    	{
    		String number=Integer.toBinaryString(Integer.parseInt(tokens[i]));
    		if(number.length()<8)
    		{
    			number=new String((new char[8-number.length()])).replace('\0','0')+number;
    		}
    		binaryString+=number;
    	}
		return binaryString;

    }

}
//Binary Trie Data structure full implementation
//
class Trie{
    private Node root;
    //
    //Basic element of tree
    private  class Node {
        private int val;
        public Node  left,right;
        public Node()
        {
        	this.val=-1;
        	this.left=null;
        	this.right=null;
        }
    }
    public Trie() {

    }

    /*
    Method for removing the nodes having the same
    next hope while traversing the tree in postorder
    */

    public void postOrderRemoval(){

    	postOrderRemoval(this.root);
    }
    /*
    */
    private void postOrderRemoval(Node head){

        if(head==null){
            return;
        }
        if(head.left!=null)
            postOrderRemoval(head.left);
        if(head.right!=null)
            postOrderRemoval(head.right);
        if(head.right!=null && head.left!=null ){

            if(head.right.val==head.left.val && head.right.val!=-1){
                int temp=head.right.val;
                head.val=temp;
                head.right=null;
                head.left=null;
            }
        }
       if((head.left!=null) && head.right==null)
        {
            if(head.left.val!=-1){
                head.val=head.left.val;
                head.left=null;
            }
        }

        if((head.right!=null) && head.left==null)
        {
            if(head.right.val!=-1){
                head.val=head.right.val;
                head.right=null;
            }
        }

    }



    /*Method to add new <ip,nextNode> pairs
    to trie
    */

    public void add(String ip, int vertex) {
         root = add(root, ip, vertex, 0);
    }
    /*
    Private method for adding new key.value pair
    */

    private Node add(Node head, String ip, int vertex, int d) {

        if (head == null) head = new Node();

        if (d == ip.length()) {
            head.val = vertex;
            return head;
        }

        char c = ip.charAt(d);
        String temp="";
        temp+=c;
        if(Integer.parseInt(temp)==0){

          head.left = add(head.left, ip, vertex, d+1);
        }

        else{
            head.right = add(head.right, ip, vertex, d+1);
        }
        return head;
    }

    /*Function to get the longest prefix in trie
    for an ip adress
    */
    public String longestPrefixOf(String ip) {

        int length = longestPrefixOf(root, ip, 0, 0);
        return ip.substring(0, length);
    }
    /*
    Private function called in public longestPrefixOF for
    getting the longest prefix match of a given ip address
    */

    private int longestPrefixOf(Node head, String ip, int d, int length) {

        if (head == null) return length;

        if (head.val != -1) length = d;

        if (d == ip.length()) return length;

        char c = ip.charAt(d);
        String temp="";
        temp+=c;
        if(Integer.parseInt(temp)==0)
        return longestPrefixOf(head.left, ip, d+1, length);
        else
        return longestPrefixOf(head.right, ip, d+1, length);
    }

    /*function to get the value for a given key
    i.e. next node for an ip
    */
     public int get(String key) {

        Node head = get(root, key, 0);
        if (head == null) return 0;
        return  head.val;
    }
    /*
    Private function called in get function for getting
    the value of given node given longest prefix match
    */

    private Node get(Node head, String key, int d) {

        if (head == null) return null;
        if (d == key.length()) return head;
        char c = key.charAt(d);
        String temp="";
        temp+=c;
        if(Integer.parseInt(temp)==0)
            return get(head.left, key, d+1);
        else
            return get(head.right,key,d+1);
    }


}
