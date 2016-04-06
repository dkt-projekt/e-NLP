package de.dkt.eservices.erattlesnakenlp.linguistic;

public class Relation {

	public Entity subject;
	public Entity object;
	
	public Action action;

	public Relation(Entity subject, Entity object, Action action) {
		super();
		this.subject = subject;
		this.object = object;
		this.action = action;
	}
	
	
}
