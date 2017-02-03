package de.dkt.eservices.erattlesnakenlp.linguistic;

import java.util.LinkedList;

import edu.stanford.nlp.ling.IndexedWord;

public class EntityRelationTripleList {

	private String subject;
	private LinkedList<String> object;
	private String relation;

	public EntityRelationTripleList() {
	}

	public EntityRelationTripleList(String subject, String relation, LinkedList<String> object) {
		super();
		this.subject = subject;
		this.relation = relation;
		this.object = object;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getRelation() {
		return relation;
	}

	public void setRelation(String connectingElement) {
		this.relation = connectingElement;
	}

	public LinkedList<String> getObject() {
		return object;
	}

}
