package de.dkt.eservices.erattlesnakenlp.linguistic;

public class SpanWord implements LinguisticUnit {

	protected String text;
	protected int startSpan;
	protected int endSpan;
	protected int taggedWordsIndex;
	protected String uri;
	
	public SpanWord(String text) {
		super();
		this.text = text;
	}
	
	public SpanWord(String label, String URI, int taggedWordsIndex){
		super();
		this.text = label;
		this.uri = URI;
		this.taggedWordsIndex = taggedWordsIndex;
	}
	
	public SpanWord(String text, int startSpan, int endSpan) {
		super();
		this.text = text;
		this.startSpan = startSpan;
		this.endSpan = endSpan;
	}

	public String getText() {
		return text;
	}
	public String getURI() {
		return uri;
	}
	public void setURI(String uri) {
		this.uri = uri;
	}
	public int getTaggedWordsIndex() {
		return taggedWordsIndex;
	}
	public void setTaggedWordsIndex(int taggedWordsIndex) {
		this.taggedWordsIndex = taggedWordsIndex;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	public void indentedPrintToScreen(String indent){
		System.out.println(indent+text);
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
