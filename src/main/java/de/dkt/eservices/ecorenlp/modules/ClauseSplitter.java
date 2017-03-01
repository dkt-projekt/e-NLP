package de.dkt.eservices.ecorenlp.modules;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.naturalli.OpenIE;
import edu.stanford.nlp.naturalli.SentenceFragment;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.PropertiesUtils;

public class ClauseSplitter {

	static StanfordCoreNLP pipeline = null;
	static Properties props = PropertiesUtils.asProperties("annotators", "tokenize,ssplit,pos,lemma,depparse,natlog,openie");
	
	public void initPipeline(){
		pipeline = new StanfordCoreNLP(props);
	}
	
	public ArrayList<String> getClauses(String text){
		
		ArrayList<String> clauses = new ArrayList<String>();
		Annotation ann = new Annotation(text);
		pipeline.annotate(ann);
		for (CoreMap sent : ann.get(CoreAnnotations.SentencesAnnotation.class)) {
			List<SentenceFragment> cl = new OpenIE(props).clausesInSentence(sent);
			filterSuperClauses(cl);
		}
		return clauses;
		
	}
	
	public ArrayList<String> filterSuperClauses(List<SentenceFragment> clauses){
		ArrayList<String> cl = new ArrayList<String>();
		// I'm assuming that the first item is the complete sentence and from there on it's traversing breath first. TODO: debug this approach with more data!
		String fullSent = clauses.get(0).toString();
		ArrayList<String> processed = new ArrayList<String>();
		for (int i = clauses.size()-1; i > 0; i--){
			String temp = clauses.get(i).toString();
			// stripping off every bit that I have seen already
			for (String p : processed){
				temp = temp.replaceAll(p,  "");
			}
			cl.add(temp);
			processed.add(temp);
			for (String p : processed){
				fullSent = fullSent.replaceAll(p, "");
			}
		}
		cl.add(fullSent); // or what is left of this...
		return cl;
	}
	
	
	public static void main(String[] args){
//		initPipeline();
//		String sentence = "This computer is great, but the keyboard is lousy and the processor sucks.";
//		getClauses(sentence);
	}
		
}
