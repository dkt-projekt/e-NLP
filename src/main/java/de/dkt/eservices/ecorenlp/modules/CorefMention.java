package de.dkt.eservices.ecorenlp.modules;

public class CorefMention {
	
private int mentionID;
private String contents;
private int startIndex;
private int endIndex; 
private int sentenceNumber;
private int clusterID;
private String head;

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

}
