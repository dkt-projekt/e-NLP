package de.dkt.eservices.erattlesnakenlp.linguistic;

import edu.stanford.nlp.ling.IndexedWord;

public class EntityRelationTriple {
	
	private String subject;
	private String object;
	private String relation;
	private int start;
	private int end;
	
	public EntityRelationTriple() {
	}

	public EntityRelationTriple(String subject, String relation, String object, int start, int end) {
		super();
		this.subject = subject;
		this.relation = relation;
		this.object = object;
		this.start = start;
		this.end = end;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	public void setStartIndex(int start) {
		this.start = start;
	}
	public void setEndIndex(int end) {
		this.end = end;
	}
	
	public int getStartIndex() {
		return start;
	}
	public int getEndIndex() {
		return end;
	}

	
	public String getRelation() {
		return relation;
	}

	public void setRelation(String connectingElement) {
		this.relation = connectingElement;
	}
	
	public String getObject() {
		return object;
	}

	public void setObject(String object) {
		this.object = object;
	}

}
