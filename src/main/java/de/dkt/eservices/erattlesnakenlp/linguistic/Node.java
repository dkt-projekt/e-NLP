package de.dkt.eservices.erattlesnakenlp.linguistic;

import java.util.ArrayList;
import java.util.List;

public class Node {

	    private List<Node> children = null;
	    private String value;

	    public Node(String value)
	    {
	        this.children = new ArrayList<>();
	        this.value = value;
	    }

	    public void addChild(Node child)
	    {
	        children.add(child);
	    }

	
}
