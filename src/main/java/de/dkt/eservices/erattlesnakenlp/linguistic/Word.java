package de.dkt.eservices.erattlesnakenlp.linguistic;

public class Word implements LinguisticUnit {

	protected String text;
	
	public Word(String text) {
		super();
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	public void indentedPrintToScreen(String indent){
		System.out.println(indent+text);
	}
}
