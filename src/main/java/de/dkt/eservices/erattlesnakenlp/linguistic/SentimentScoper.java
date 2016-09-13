package de.dkt.eservices.erattlesnakenlp.linguistic;

import java.io.StringReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import de.dkt.eservices.ecorenlp.modules.Tagger;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.parser.nndep.DependencyParser;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.trees.GrammaticalRelation;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.TypedDependency;
import eu.freme.common.exception.BadRequestException;

public class SentimentScoper {
	
	static DependencyParser parser;
	
	public static void initParser(String language){

		String parserModel = null;
		if (language.equalsIgnoreCase("en")){
			parserModel = "edu/stanford/nlp/models/parser/nndep/english_UD.gz";  
		}
			
		else if (language.equalsIgnoreCase("de")){
			parserModel = "edu/stanford/nlp/models/parser/nndep/UD_German.gz";
		}
		else {
			throw new BadRequestException("Unsupported language: "+ language);
		}
		parser = DependencyParser.loadFromModelFile(parserModel);
				
	}
	
	public static HashMap<String, List<IndexedWord>> getScopeForSentiment(String text, List<String> sentimentWords){
		
		HashMap<String, List<IndexedWord>> sMap = new HashMap<String, List<IndexedWord>>();
		DocumentPreprocessor tokenizer = new DocumentPreprocessor(new StringReader(text));
		for (List<HasWord> sentence : tokenizer) {
			List<TaggedWord> tagged = Tagger.tagger.tagSentence(sentence);
			GrammaticalStructure gs = parser.predict(tagged);
			for (String sw : sentimentWords){
				List<IndexedWord> swdeps = new LinkedList<IndexedWord>();
				for (TypedDependency td : gs.typedDependencies()){
					List<IndexedWord> deps = new LinkedList<IndexedWord>();
					if (td.gov().word() != null && td.gov().word().equalsIgnoreCase(sw)){ // td.gov().word() equals null if node is root...
						deps = getDeps(gs, td.gov(), deps);
						for (IndexedWord id : deps){
							if (!swdeps.contains(id)){
								swdeps.add(id);
							}
						}
						// we could include the relation type here to find out the most likely candidate which the sentiment word has scope over (e.g. object or subject), but that requires some more work
						if (td.reln().toString().equals("neg")){
							sw = "negated_" + sw;
						}
						// TODO: tried a bit with German, and the TypedDependencies seem to be of lower quality, not working in the same way as for English
					}
				}
				sMap.put(sw,  swdeps);
			
			}
		}
		
		
		return sMap;
		
	}
	

	
	public static List<IndexedWord> getDeps(GrammaticalStructure gs, IndexedWord iw, List<IndexedWord> deps){
		for (TypedDependency td : gs.typedDependencies()){
			if (td.gov().index() == iw.index()){
				if (!td.reln().toString().equals("neg")){
					deps.add(td.dep());
				}
			}
		}
		return deps;
		
	}
	
	
	public static void main(String args[]){
		
		String language = "en";
		Tagger.initTagger(language);
		initParser(language);
		
		List<String> sw = new LinkedList<>();
		sw.add("admire");
		HashMap<String, List<IndexedWord>> sMap = getScopeForSentiment("I admire this bass guitar", sw);
		System.out.println(sMap);
		List<String> sw2 = new LinkedList<>();
		sw2.add("happy");
		HashMap<String, List<IndexedWord>>  sMap2 = getScopeForSentiment("I am happy with this bass guitar", sw2);
		System.out.println(sMap2);
		List<String> sw3 = new LinkedList<>();
		sw3.add("happy");
		HashMap<String, List<IndexedWord>>  sMap3 = getScopeForSentiment("I am not happy with this bass guitar", sw3);
		System.out.println(sMap3);
		List<String> sw4 = new LinkedList<>();
		sw4.add("admire");
		HashMap<String, List<IndexedWord>> sMap4 = getScopeForSentiment("I do not admire this bass guitar", sw4);
		System.out.println(sMap4);
		
		
		Tagger.initTagger("de");
		initParser("de");
		List<String> sw5 = new LinkedList<>();
		sw5.add("gut");
		HashMap<String, List<IndexedWord>> sMap5 = getScopeForSentiment("Ich find es nicht gut", sw5);
		System.out.println(sMap5);
		
		
	}

}
