package de.dkt.eservices.ecorenlp.modules;

import java.io.File;
import java.util.Arrays;

import com.hp.hpl.jena.rdf.model.Model;

import de.dkt.common.niftools.NIFReader;
import de.dkt.common.niftools.NIFWriter;
import de.dkt.eservices.eopennlp.modules.SentenceDetector;
import de.dkt.eservices.eopennlp.modules.Tokenizer;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import eu.freme.common.exception.BadRequestException;
import opennlp.tools.util.Span;

public class Tagger {

	public static MaxentTagger tagger;
	
	public static void initTagger(String language){

		String taggersDirectory = "taggers" + File.separator;
		if (language.equalsIgnoreCase("en")){
			tagger = new MaxentTagger(taggersDirectory + "english-left3words-distsim.tagger");
		}
		else if (language.equalsIgnoreCase("de")){
			tagger = new MaxentTagger(taggersDirectory + "german-hgc.tagger");
		}
		else {
			throw new BadRequestException("Unsupported language: "+ language);
		}
				
	}
	
	public static Model tagNIF(Model nifModel, String informat, String sentenceModel){
		
		String text = NIFReader.extractIsString(nifModel);
    	// this may seem a bit verbose, but first get spans, then reconstruct sentence to have words and their proper indices, then tag and assign char indices in nif annotations
		Span[] sentenceSpans = SentenceDetector.detectSentenceSpans(text, sentenceModel);
		for (Span sentenceSpan : sentenceSpans){
			String sentence = text.substring(sentenceSpan.getStart(), sentenceSpan.getEnd());
			//System.out.println("DEBUGGING sent:" + sentence + " " + sentenceSpan.getStart() + " " + sentenceSpan.getEnd());
			String taggedSentence = tagger.tagString(sentence); // note that stanford tokenizes isn't in another (better) way than opennlp. corenlp -> is n't. opennlp -> isn t. If we want to guarantee identical tokenization, we can use opennlp for tokenizing and use tagger.tagTokenizedString(sentence) here
			int counter=sentenceSpan.getStart();
			String taggedTokens[] = taggedSentence.split(" ");
			for (String t : taggedTokens){
				String parts[] = t.split("_");
				int aux = text.indexOf(parts[0], counter);
				counter = aux + parts[0].length();
				//System.out.println(parts[0]+"  "+aux+"  "+counter );
				int tokenStart = aux;
				int tokenEnd = counter;
				NIFWriter.addPosTagAnnotation(nifModel, tokenStart, tokenEnd, parts[0], parts[1]);
			}
			
		}

		
		return nifModel;
	}
	
	
	public static void main(String[] args) throws Exception{
	
		/*
	MaxentTagger tagger = initTagger("en");
	String testSent = "Pierre Vinken , 61 years old , will join the board as a nonexecutive director Nov. 29 ." +
			"Mr. Vinken is chairman of Elsevier N.V. , the Dutch publishing group .";
	
	String tagged = tagger.tagString(testSent);
	
	System.out.println("RESULT:" + (tagged));
	
	MaxentTagger deTagger = initTagger("de");
	String testSentDe = "Lilia Skala drehte bis ins hohe Alter Filme und stand auf der Bühne.";
	String taggedDe = deTagger.tagString(testSentDe);
	System.out.println("RESULT DE:" + taggedDe);
	*/
		//String sentence = "This is    a test sentence.";
		//String tokens[] = Span.spansToStrings(tokenSpans, sentence);
		
		
//		
		initTagger("en");
		String sentence = "This isn't this is a test sentence.";
		Span tokenSpans[] = Tokenizer.simpleTokenizeIndices(sentence);
		//String taggedSentence = tagger.tagTokenizedString(tokenString);
		
		String taggedSentence = tagger.tagString(sentence);
		System.out.println("DEBUGGINg tokenSpans here:" + Arrays.toString(tokenSpans));		
		System.out.println("DEBUGGING taggedSentence:" + taggedSentence);

		//String taggedSentence = tagger.tagTokenizedString(sentence);
		int counter=0;
		String taggedTokens[] = taggedSentence.split(" ");
		for (String t : taggedTokens){
			String parts[] = t.split("_");
			int aux = sentence.indexOf(parts[0], counter);
			counter = aux + parts[0].length();
			System.out.println(parts[0]+"  "+aux+"  "+counter );
			int start = aux;
			int end = counter;
		}

	//MaxentTagger nlTagger = initTagger("nl");
	
	}
	
}

