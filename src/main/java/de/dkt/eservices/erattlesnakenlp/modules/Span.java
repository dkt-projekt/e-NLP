package de.dkt.eservices.erattlesnakenlp.modules;

public class Span {

	private int begin;
	private int end;
	
	public Span() {
	}

	public Span(int begin, int end) {
		super();
		this.begin = begin;
		this.end = end;
	}

	public int getBegin() {
		return begin;
	}

	public void setBegin(int begin) {
		this.begin = begin;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}
	
}
