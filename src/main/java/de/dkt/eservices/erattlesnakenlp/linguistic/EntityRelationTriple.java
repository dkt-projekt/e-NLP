package de.dkt.eservices.erattlesnakenlp.linguistic;

import edu.stanford.nlp.ling.IndexedWord;

public class EntityRelationTriple {

	private String subject;
	private String object;
	private String relation;
	private String themRoleSubj;
	private String themRoleObj;
	private String lemma;
	private int start;
	private int end;

	public EntityRelationTriple() {
	}

	public EntityRelationTriple(String subject, String relation, String object, String themRoleSubj, String themRoleObj, int start, int end) {
		super();
		this.subject = subject;
		this.relation = relation;
		this.object = object;
		this.themRoleSubj = themRoleSubj;
		this.themRoleObj = themRoleObj;
		this.lemma = lemma;
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
	public void setThemRoleSubj(String themRoleSubj){
		this.themRoleSubj = themRoleSubj;
	}
	public void setThemRoleObj(String themRoleObj){
		this.themRoleObj = themRoleObj;
	}
	public void setLemma(String lemma){
		this.lemma = lemma;
	}
	public String getThemRoleSubj(){
		return themRoleSubj;
	}
	
	public String getLemma(){
		return lemma;
	}
	public String getThemRoleObj(){
		return themRoleObj;
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
