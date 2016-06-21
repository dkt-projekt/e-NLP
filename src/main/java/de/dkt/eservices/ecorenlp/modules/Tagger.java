package de.dkt.eservices.ecorenlp.modules;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.ListIterator;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.rdf.model.Model;

import de.dkt.common.niftools.NIFReader;
import de.dkt.common.niftools.NIFWriter;
import de.dkt.eservices.eopennlp.modules.NameFinder;
import de.dkt.eservices.eopennlp.modules.SentenceDetector;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import eu.freme.common.exception.BadRequestException;
import opennlp.tools.util.Span;

public class Tagger {

	
	
	public static MaxentTagger tagger;
	static Logger logger = Logger.getLogger(Tagger.class);
	
	public static void initTagger(String language){

		String taggersDirectory = "taggers" + File.separator;
		if (language.equalsIgnoreCase("en")){
			logger.info("Loading model: " + System.getProperty("user.dir") + File.separator + taggersDirectory + "english-left3words-distsim.tagger");
			System.out.println("Loading model: " + System.getProperty("user.dir") + File.separator + taggersDirectory + "english-left3words-distsim.tagger");
			tagger = new MaxentTagger(taggersDirectory + "english-left3words-distsim.tagger");
			
		}
		else if (language.equalsIgnoreCase("de")){
			//tagger = new MaxentTagger(taggersDirectory + "german-hgc.tagger");
			logger.info("Loading model: " + System.getProperty("user.dir") + File.separator + taggersDirectory + "german-fast.tagger");
			tagger = new MaxentTagger(taggersDirectory + "german-fast.tagger");
			
		}
		else {
			throw new BadRequestException("Unsupported language: "+ language);
		}
				
	}
	
	public static HashMap<String, HashMap<String, Integer>> getEntitytCandidateMap(String str, String language){
		
		ArrayList<String> englishEntityCandidateTagSet = new ArrayList<>(Arrays.asList("NNP")); // NOTE: this is coupled to the stanford postagger...
		ArrayList<String> germanEntityCandidateTagSet = new ArrayList<>(Arrays.asList("NE")); // NOTE: this is coupled to the stanford postagger...
		//TODO: create a general official stopwords list somewhere for all languages we support
		ArrayList<String> candidateTagSet = new ArrayList<String>();
	
				if (language.equals("en")){
					candidateTagSet = englishEntityCandidateTagSet;

				}
				else if (language.equals("de")){
					candidateTagSet = germanEntityCandidateTagSet;
				}
		
		HashMap<String, HashMap<String, Integer>> entityCandidateMap = new HashMap<String, HashMap<String, Integer>>();
		String taggedString = tagger.tagString(str);
		String[] tagList = taggedString.split(" ");
		String word = "";
		for (int i = 0; i < tagList.length; i++){
			String parts[] = tagList[i].split("_");
			String w = parts[0];
			String tag = parts[1];
			if (candidateTagSet.contains(tag)){
				word += " " + w;
				//check if next word is also of properNoun type to capture multi word units
				if (i < tagList.length && candidateTagSet.contains(tagList[i+1].split("_")[1])){
					// do nothing so that in next increment, the next word gets added
				}
				else{
					int c = 1;
					HashMap<String, Integer> innerMap = new HashMap<String, Integer>();
					word = word.trim();
					if (entityCandidateMap.containsKey(word)){
						if (entityCandidateMap.get(word).containsKey(tag)){
							c = entityCandidateMap.get(word).get(tag) + 1;
						}
					}
					innerMap.put(tag, c);
					entityCandidateMap.put(word, innerMap);
					// and clear word, since it's not assumed to be part of multi word unit
					word = "";
				}
			}
		}
//		try {
//			PrintWriter out = new PrintWriter(new File("C:\\Users\\pebo01\\Desktop\\temp.txt.txt"));
//			out.write(taggedString);
//			out.close();
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		return entityCandidateMap;
	}
	
//	public static HashMap<String, HashMap<String, Integer>> getWord2Tag2Count(String str){
//		
//		HashMap<String, HashMap<String, Integer>> tagMap = new HashMap<String, HashMap<String, Integer>>();
//		String taggedString = tagger.tagString(str);
//		//TODO: take out NNP items that are misrecognized, that are actually mostly Prepositions, Adjectives, Verbs?
//		
//		
//		for (String t: taggedString.split(" ")){
//			String parts[] = t.split("_");
//			String word = parts[0];
//			String tag = parts[1];
//			int c = 1;
//			HashMap<String, Integer> innerMap = new HashMap<String, Integer>();
//			if (tagMap.containsKey(word)){
//				if (tagMap.get(word).containsKey(tag)){
//					c = tagMap.get(word).get(tag) + 1;
//				}
//			}
//			innerMap.put(tag, c);
//			tagMap.put(word, innerMap);
//		}
//		
//		return tagMap;
//	}
	
	
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
		
		
//		initTagger("en");
//		String sentence = "This isn't this is a test sentence.";
//		Span tokenSpans[] = Tokenizer.simpleTokenizeIndices(sentence);
//		//String taggedSentence = tagger.tagTokenizedString(tokenString);
//		
//		String taggedSentence = tagger.tagString(sentence);
//		System.out.println("DEBUGGINg tokenSpans here:" + Arrays.toString(tokenSpans));		
//		System.out.println("DEBUGGING taggedSentence:" + taggedSentence);
//
//		//String taggedSentence = tagger.tagTokenizedString(sentence);
//		int counter=0;
//		String taggedTokens[] = taggedSentence.split(" ");
//		for (String t : taggedTokens){
//			String parts[] = t.split("_");
//			int aux = sentence.indexOf(parts[0], counter);
//			counter = aux + parts[0].length();
//			System.out.println(parts[0]+"  "+aux+"  "+counter );
//			int start = aux;
//			int end = counter;
//		}

	//MaxentTagger nlTagger = initTagger("nl");
	
	}
	
}

