package de.dkt.eservices.erattlesnakenlp.linguistic;

public class EntityRelationTriple {
	
	private String subject;
	private String object;
	private String relation;
	
	public EntityRelationTriple() {
	}

	public EntityRelationTriple(String subject, String relation, String object) {
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

	public void setRelation(String relation) {
		this.relation = relation;
	}
	
	public String getObject() {
		return object;
	}

	public void setObject(String object) {
		this.object = object;
	}

}
