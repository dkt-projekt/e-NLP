package de.dkt.eservices.ecorenlp.modules;


import java.util.LinkedHashSet;
import java.util.Set;


public class CorefCluster {
	
private Set<CorefMention> corefMentions = new LinkedHashSet<CorefMention>();
private int clusterID;
private CorefMention firstMention;
	 
// primitive functions

public Set<CorefMention> getCorefMentions(){
	return corefMentions;
}

public CorefCluster(int ID, Set<CorefMention> mentions, CorefMention firstMent){
    clusterID = ID;
    corefMentions.addAll(mentions);
    firstMention = firstMent;}

public int getClusterID(){
	return clusterID;
}
public void setClusterID(int ID){
	clusterID = ID;
}

public CorefMention getFirstMention() {
	return firstMention;
}

public void setFirstMention(CorefMention firstMention) {
	this.firstMention = firstMention;
}

public String ClusterToString(){
	String foo = "";
	for(CorefMention mention : corefMentions){
		foo = foo + " "+(mention.getContents());
	}
	return foo;
}

}
