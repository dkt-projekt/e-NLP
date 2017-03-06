package de.dkt.eservices.ecorenlp.modules;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.stanford.nlp.trees.Tree;

public class CorefMention {
	
private int mentionID;
private String contents;
private int startIndex;
private int endIndex; 
private int sentenceNumber;
private int clusterID;
private String head;
private String modifier = null;
private Tree sentenceAsTree;
private String[] nerTags = null;
private String gender = "";
private String number = "";
private Set<String> person = new HashSet<>();

public String getHead(){
	return this.head;
}

public void setHead(String s){
	head = s;
}

public CorefMention(int i, String word, int j, int k) {
	// TODO Auto-generated constructor stub
	mentionID = i;
	contents = word;
	startIndex = j;
	endIndex = k;
}

public CorefMention(int i, String word, int j, int k, String l) {
	// TODO Auto-generated constructor stub
	mentionID = i;
	contents = word;
	startIndex = j;
	endIndex = k;
	head = l;
	
}

public CorefMention(int i, String word, int j, int k, String l, String m, String[] n) {
	// TODO Auto-generated constructor stub
	mentionID = i;
	contents = word;
	startIndex = j;
	endIndex = k;
	head = l;
	modifier = m;
	nerTags = n;
	
}

public CorefMention(int i, String word, int j, int k, String l, String m, Tree n) {
	// TODO Auto-generated constructor stub
	mentionID = i;
	contents = word;
	startIndex = j;
	endIndex = k;
	head = l;
	modifier = m;
	sentenceAsTree = n;
	
}

public CorefMention(int mentID, String word, int j, int k, String hea, String mod, Tree tree,String gen, String num, Set<String> pers) {
	// TODO Auto-generated constructor stub
	mentionID = mentID;
	contents = word;
	startIndex = j;
	endIndex = k;
	head = hea;
	modifier = mod;
	sentenceAsTree = tree;
	gender = gen;
	number = num;
	person = pers;
	
}
// primitive methods
public int getMentionID(){
	return mentionID;
}
public void setMentionID(int id){
	mentionID = id;
}
public String getContents(){
	return contents;
}
public void setContents(String contents1){
	contents=contents1;
}
public void setStartIndex(int index){
	startIndex = index;
}
public int getStartIndex(){
	return startIndex;
}
public void	setEndIndex(int index){
	endIndex = index;
}
public int getEndIndex(){
	return endIndex;
}
@Override
public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((contents == null) ? 0 : contents.hashCode());
	result = prime * result + endIndex;
	result = prime * result + mentionID;
	result = prime * result + startIndex;
	return result;
}
@Override
public boolean equals(Object obj) {
	if (this == obj)
		return true;
	if (obj == null)
		return false;
	if (getClass() != obj.getClass())
		return false;
	CorefMention other = (CorefMention) obj;
	if (contents == null) {
		if (other.contents != null)
			return false;
	} else if (!contents.equals(other.contents))
		return false;
	if (endIndex != other.endIndex)
		return false;
	if (mentionID != other.mentionID)
		return false;
	if (startIndex != other.startIndex)
		return false;
	return true;
}
public int getSentenceNumber() {
	return sentenceNumber;
}
public void setSentenceNumber(int sentenceNumber) {
	this.sentenceNumber = sentenceNumber;
}
public int getClusterID() {
	return clusterID;}

public void setClusterID(int clusterID) {
	this.clusterID = clusterID;
}

public String toString(CorefMention mention){
	return mention.getContents();
}

public String getModifier() {
	return modifier;
}

public void setModifier(String modifier) {
	this.modifier = modifier;
}

public Tree getSentenceAsTree() {
	return sentenceAsTree;
}

public void setSentenceAsTree(Tree sentenceAsTree) {
	this.sentenceAsTree = sentenceAsTree;
}

public String[] getNerTags() {
	return nerTags;
}

public void setNerTags(String[] nerTags) {
	this.nerTags = nerTags;
}

public String getGender() {
	return gender;
}

public void setGender(String gender) {
	this.gender = gender;
}

public String getNumber() {
	return number;
}

public void setNumber(String number) {
	this.number = number;
}

public Set<String> getPerson() {
	return person;
}

public void setPerson(Set<String> person) {
	this.person = person;
}

}
