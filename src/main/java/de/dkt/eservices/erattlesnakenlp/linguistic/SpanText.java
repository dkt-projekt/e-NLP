package de.dkt.eservices.erattlesnakenlp.linguistic;

import java.util.List;

public class SpanText implements LinguisticUnit {

	protected List<LinguisticUnit> childs;
	protected int startSpan;
	protected int endSpan;

	public SpanText() {
	}
	
	public SpanText(String s) {
		this(s,0);
	}	
	
//	public Text(String s, int startSpan, int endSpan) {
//		childs = TextSplitter.splitText(s);
//		this.startSpan = startSpan;
//		this.endSpan = endSpan;
//	}
	
	public SpanText(String s, int offset) {
		childs = TextSplitter.splitText(s,offset);
		startSpan = offset;
		endSpan = offset + s.length();
	}	
	
	public List<LinguisticUnit> getChilds() {
		return childs;
	}

	public void setChilds(List<LinguisticUnit> childs) {
		this.childs = childs;
	}
	
	public void indentedPrintToScreen(String indent){
		if(childs!=null){
			for (LinguisticUnit lu : childs) {
				if(lu instanceof SpanText){
					System.out.println(indent+"--");
				}
				lu.indentedPrintToScreen(indent+indent);
			}
		}
	}
	
	public static void main(String[] args) {
		SpanText t1 = new SpanText("There is something inside the text that can help me do my staff. This is simple, but difficult at the same time.\n I do not know what to do.\n Greetings");
		t1.indentedPrintToScreen("\t");
	}

	public int getStartSpan() {
		return startSpan;
	}

	public void setStartSpan(int startSpan) {
		this.startSpan = startSpan;
	}

	public int getEndSpan() {
		return endSpan;
	}

	public void setEndSpan(int endSpan) {
		this.endSpan = endSpan;
	}
	
	
}
