package de.dkt.eservices.erattlesnakenlp.linguistic;

public class SpanRelation extends Relation{

	public int startSpan;
	public int endSpan;
	
	public SpanRelation(Entity subject, Entity object, Action action) {
		super(subject, object, action);
		int start = Math.min(subject.startSpan, object.startSpan);
		this.startSpan = start;
		int end = Math.max(subject.endSpan, object.endSpan);
		this.endSpan = end;
	}

	public SpanRelation(Entity subject, Entity object, Action action, int startSpan, int endSpan) {
		super(subject, object, action);
		this.startSpan = startSpan;
		this.endSpan = endSpan;
	}

	public String getSubject(){
		if(subject!=null){
			return subject.text;
		}
		return "http://dkt.dfki.de/entities/NULL";
	}
	public String getAction(){
		if(action!=null){
			return action.text;
		}
		return "http://dkt.dfki.de/entities/NULL";
	}
	
	public String getObject(){
		if(object!=null){
			return object.text;
		}
		return "http://dkt.dfki.de/entities/NULL";
	}
}
