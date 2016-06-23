package de.dkt.eservices.erattlesnakenlp.linguistic;

import java.util.LinkedList;
import java.util.List;

import edu.stanford.nlp.ling.IndexedWord;

public class DPTreeNode {

	public DPTreeNode parent;
	public String relation;
	public String value;
	public IndexedWord indexedWord;
	public List<DPTreeNode> childs;
	
	public DPTreeNode(String value) {
		this.value = value;
		childs = new LinkedList<DPTreeNode>();
	}

	public DPTreeNode(DPTreeNode parent, String relationsValue, String value, List<DPTreeNode> childs) {
		super();
		this.parent = parent;
		this.relation = relationsValue;
		this.value = value;
		this.childs = childs;
	}

	public DPTreeNode(DPTreeNode parent, String relationsValue, String value) {
		super();
		this.parent = parent;
		this.relation = relationsValue;
		this.value = value;
		this.childs = new LinkedList<DPTreeNode>();
	}
	
	public DPTreeNode(DPTreeNode parent, String relationsValue, String word, IndexedWord iw) {
		super();
		this.parent = parent;
		this.relation = relationsValue;
		this.value = word;
		this.indexedWord = iw;
		this.childs = new LinkedList<DPTreeNode>();
	}
	
	public void printByLevel(String indent1,String indent){
		System.out.println(indent1+value+" ["+relation+"]");
		for (DPTreeNode treeNode : childs) {
			treeNode.printByLevel(indent1+indent,indent);
		}
	}
	
	
	
	
	public DPTreeNode getShortestPath(String s1, String s2, List<DPTreeNode> list){
//		System.out.println("Checking: "+value+" ["+relation+"]");
			List<DPTreeNode> listnew = new LinkedList<DPTreeNode>();
			for (DPTreeNode treeNode : childs) {
				listnew = new LinkedList<DPTreeNode>();
				DPTreeNode aux = treeNode.getShortestPath(s1,s2,listnew);
				if(aux!=null){
//					System.out.println("\tBack: "+aux.value+" ["+aux.relation+"]");
					return aux;
				}
//				if(checklist(s1,s2,listnew)){
//					return treeNode;
//				}
				list.addAll(listnew);
//				System.out.println("\tBack: NULL");
			}
			if(value.equalsIgnoreCase(s1) || value.equalsIgnoreCase(s2)){
//				System.out.println("\tAdding: "+value+" ["+relation+"]");
				list.add(this);
			}
			if(checklist(s1,s2,list)){
				return this;
			}
			return null;
	}

	public boolean checklist(String s1,String s2,List<DPTreeNode> list){
		boolean has1 = false;
		boolean has2 = false;
		for (DPTreeNode dpTreeNode : list) {
			if(dpTreeNode.value.equalsIgnoreCase(s1)){
				has1=true;
			}
			if(dpTreeNode.value.equalsIgnoreCase(s2)){
				has2=true;
			}
		}
		return (has1 && has2);
	}
	
	public DPTreeNode getPath(String s){
		if(value.equalsIgnoreCase(s)){
			return this;
		}
		else{
			for (DPTreeNode treeNode : childs) {
				DPTreeNode aux = treeNode.getPath(s);
				if(aux!=null){
					return aux;
				}
			}
			return null;
		}
	}
	
	
	
}
