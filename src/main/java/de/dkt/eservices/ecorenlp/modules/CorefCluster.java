package de.dkt.eservices.ecorenlp.modules;


import java.util.LinkedHashSet;
import java.util.Set;


public class CorefCluster {
	
private Set<CorefMention> corefMentions = new LinkedHashSet<CorefMention>();
private int clusterID;
private CorefMention firstMention;
private Set<String> genders;
private Set<String> numbers;
private Set<String> persons;


	 
// primitive functions

public Set<CorefMention> getCorefMentions(){
	return corefMentions;
}

public void setCorefMentions(Set<CorefMention> mentions){
	corefMentions = mentions;
}

public CorefCluster(int ID, Set<CorefMention> mentions, CorefMention firstMent){
    clusterID = ID;
    corefMentions.addAll(mentions);
    firstMention = firstMent;}

public CorefCluster(int ID, Set<CorefMention> mentions, CorefMention firstMent, Set<String> gend, Set<String>num, Set<String>pers){
    clusterID = ID;
    corefMentions.addAll(mentions);
    firstMention = firstMent;
    genders =gend;
    numbers = num;
    persons = pers;}

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

public String toString(){
	String foo = ""+this.clusterID;
	for(CorefMention mention : corefMentions){
		foo = foo + "; "+(mention.getContents());
	}
	return foo;
}

//complex functions

public String getContentsOfClusterAsString(){
	Set<CorefMention> set = this.getCorefMentions();
	String string = "";
	for (CorefMention a : set){
		string = string + " "+ a.getContents();
	}
	return string;
	
}

public Set<String> getGenders() {
	return genders;
}

public void setGenders(Set<String> genders) {
	this.genders = genders;
}

public Set<String> getNumbers() {
	return numbers;
}

public void setNumbers(Set<String> numbers) {
	this.numbers = numbers;
}

public Set<String> getPersons() {
	return persons;
}

public void setPersons(Set<String> persons) {
	this.persons = persons;
}

}
