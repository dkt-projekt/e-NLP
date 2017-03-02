package de.dkt.eservices.ecorenlp.modules;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ie.machinereading.structure.Span;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;


public class Tokenizer {

    protected StanfordCoreNLP pipeline;
    
    public Tokenizer() {
        Properties props;
        props = new Properties();
        props.put("annotators", "tokenize, ssplit");
        
        this.pipeline = new StanfordCoreNLP(props);
    }
    
    public List<String> tokenize(String documentText)
    {
    	List<String> tokens = new LinkedList<String>();
        Annotation document = new Annotation(documentText);

        this.pipeline.annotate(document);
        List<CoreMap> sentences = document.get(SentencesAnnotation.class);
        for(CoreMap sentence: sentences) {
            for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
                tokens.add(token.toString());
            }
        }
        return tokens;
    }
    

    public ArrayList<Span> tokenizeIndices(String documentText){
    	ArrayList<Span> tokenSpans = new ArrayList<Span>();
    	Annotation document = new Annotation(documentText);

        this.pipeline.annotate(document);
        List<CoreMap> sentences = document.get(SentencesAnnotation.class);
        for(CoreMap sentence: sentences) {
            for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
            	Span sp = new Span();
                sp.setStart(token.beginPosition());
                sp.setEnd(token.endPosition());
                tokenSpans.add(sp);
            }
        }
        return tokenSpans;
    }


    

    public static void main(String[] args) {
    	Date before = new Date();
    	String testSent = "This is just a test sentence.";
    	Tokenizer tok = new Tokenizer();
    	for (int i = 0; i < 1000; i++){
    		tok.tokenizeIndices(testSent);
    	}
    	Date after = new Date();
    	System.out.println((after.getTime()-before.getTime()) +" miliseconds for tokenization.");
    	
    	}
    
}