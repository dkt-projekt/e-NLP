package de.dkt.eservices.erattlesnakenlp.linguistic;

import java.util.HashMap;
import java.util.Map;

public class Entity {

	public String text;
	public int startSpan;
	public int endSpan;
	Map<String,String> properties;
	
	public Entity(String text, int startSpan, int endSpan) {
		super();
		this.text = text;
		this.startSpan = startSpan;
		this.endSpan = endSpan;
		this.properties = new HashMap<String,String>();
	}
	
	public Entity(String text, int startSpan, int endSpan, Map<String,String> properties) {
		super();
		this.text = text;
		this.startSpan = startSpan;
		this.endSpan = endSpan;
		this.properties = properties;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Entity){
			Entity e = (Entity) obj;
			if(this.text.equalsIgnoreCase(e.text) &&
					this.startSpan==e.startSpan &&
					this.endSpan==e.endSpan){
				return true;
			}
		}
		return false;
	}
	
}
