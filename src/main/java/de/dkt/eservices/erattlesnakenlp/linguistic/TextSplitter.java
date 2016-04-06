package de.dkt.eservices.erattlesnakenlp.linguistic;

import java.util.LinkedList;
import java.util.List;

public class TextSplitter {
	
	private static final String paragraphRegex = "\n";
	private static final String sentenceRegex = "\\.";
	private static final String wordRegex = " ";
	
	public static List<LinguisticUnit> splitText(String text, int initialOffset){
		List<LinguisticUnit> list = new LinkedList<LinguisticUnit>();		
		String paragraphs[] = text.split(paragraphRegex);
		int offset=initialOffset;
		for (String p : paragraphs) {
			SpanText p1 = new SpanText();
			p1.setChilds(splitParagraphs(p,offset));
			p1.setStartSpan(offset);
			p1.setEndSpan(offset+p.length());
			list.add(p1);
			offset += p.length() + 1;
		}
		return list;
	}
	
	public static List<LinguisticUnit> splitParagraphs(String text, int initialOffset){
		List<LinguisticUnit> list = new LinkedList<LinguisticUnit>();
		String sentences[] = text.split(sentenceRegex);		
		int offset=initialOffset;
		for (String p : sentences) {
			SpanText p1 = new SpanText();
			p1.setChilds(splitSentences(p,offset));
			p1.setStartSpan(offset);
			p1.setEndSpan(offset+p.length());
			list.add(p1);
		}
		return list;
	}

	public static List<LinguisticUnit> splitSentences(String text, int initialOffset){
		List<LinguisticUnit> list = new LinkedList<LinguisticUnit>();
		String words[] = text.split(wordRegex);		
		int offset=initialOffset;
		for (String p : words) {
			list.add(new SpanWord(p,offset,offset+p.length()));
		}
		return list;
	}
	
}
