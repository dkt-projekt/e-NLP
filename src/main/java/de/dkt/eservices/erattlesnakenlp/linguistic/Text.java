package de.dkt.eservices.erattlesnakenlp.linguistic;

import java.util.List;

public class Text implements LinguisticUnit {

	protected List<LinguisticUnit> childs;

	public Text() {
	}
	
	public Text(String s) {
		childs = TextSplitter.splitText(s,0);
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
				if(lu instanceof Text){
					System.out.println(indent+"--");
				}
				lu.indentedPrintToScreen(indent+indent);
			}
		}
	}
	
	public static void main(String[] args) {
		
		Text t1 = new Text("There is something inside the text that can help me do my staff. This is simple, but difficult at the same time.\n I do not know what to do.\n Greetings");
		t1.indentedPrintToScreen("\t");
		
	}
}
