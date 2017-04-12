package de.dkt.eservices.erattlesnakenlp.modules;

public class RattleSpan {

	private int begin;
	private int end;
	private String textContent;
	
	public RattleSpan() {
	}

	public RattleSpan(int begin, int end, String textContent) {
		super();
		this.begin = begin;
		this.end = end;
		this.textContent = textContent;
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
	public String getText() {
		return textContent;
	}

	public void setText(String textContent) {
		this.textContent = textContent;
	}
	
}
