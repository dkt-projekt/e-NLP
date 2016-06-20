package de.dkt.eservices.erattlesnakenlp.linguistic;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Set;

import com.hp.hpl.jena.rdf.model.Model;

import de.dkt.common.niftools.NIFReader;
import de.dkt.common.niftools.NIFWriter;
import de.dkt.eservices.ecorenlp.modules.Tagger;



public class EntityCandidateExtractor {

	 
	public static ArrayList<String> suggestCandidates(Model nifModel, String language, double thresholdValue){
		
		ArrayList<String> candidates = new ArrayList<String>();
		 
		Tagger.initTagger(language);

		
		String str = NIFReader.extractIsString(nifModel);
		HashMap<String, HashMap<String, Integer>> entityCandidateMap = Tagger.getEntitytCandidateMap(str, language);
		HashMap<String, Integer> preMap = new HashMap<String, Integer>();
		HashMap<String, Integer> candidateMap = new HashMap<String, Integer>();
		int totalCount = 0;
		for (String word : entityCandidateMap.keySet()){
			HashMap<String, Integer> innerMap = entityCandidateMap.get(word);
			for (String tag : innerMap.keySet()){
				int c = innerMap.get(tag);
				preMap.put(word, c);
				totalCount += 1;
			}
		}
		
		int avg = totalCount / preMap.size();
		for (String w : preMap.keySet()){
			if (preMap.get(w) > avg * (1/thresholdValue)){
				candidateMap.put(w, preMap.get(w));
			}
		}
		
		// sort them to display frequency (descending)
		Object[] a = candidateMap.entrySet().toArray();
		Arrays.sort(a, new Comparator<Object>() {
			public int compare(Object o1, Object o2) {
				return ((HashMap.Entry<String, Integer>) o2).getValue()
						.compareTo(((HashMap.Entry<String, Integer>) o1).getValue());
			}
		});
		
		ArrayList<String> stopwordSet = new ArrayList<String>();
		ArrayList<String> englishStopwords = new ArrayList<>(Arrays.asList("a", "an", "and", "are", "as", "at", "be", "but", "by", "for", "if", "in", "into", "is", "it", "no", "not", "of", "on", "or", "such", "that", "the", "their", "then", "there", "these", "they", "this", "to", "was", "will", "with", "start", "starts", "period", "periods", "a", "an", "and", "are", "as", "at", "be", "but", "by", "for", "if", "in", "into", "is", "it", "no", "not", "of", "on", "or", "such", "that", "the", "their", "then", "there", "these", "they", "this", "to", "was", "will", "with"));
		ArrayList<String> germanStopwords = new ArrayList<>(Arrays.asList("aber", "als", "am", "an", "auch", "auf", "aus", "bei", "bin", "bis", "bist", "da", "dadurch", "daher", "darum", "das", "daß", "dass", "dein", "deine", "dem", "den", "der", "des", "dessen", "deshalb", "die", "dies", "dieser", "dieses", "doch", "dort", "du", "durch", "ein", "eine", "einem", "einen", "einer", "eines", "er", "es", "euer", "eure", "für", "hatte", "hatten", "hattest", "hattet", "hier", "hinter", "ich", "ihr", "ihre", "im", "in", "ist", "ja", "jede", "jedem", "jeden", "jeder", "jedes", "jener", "jenes", "jetzt", "kann", "kannst", "können", "könnt", "machen", "mein", "meine", "mit", "muß", "mußt", "musst", "müssen", "müßt", "nach", "nachdem", "nein", "nicht", "nun", "oder", "seid", "sein", "seine", "sich", "sie", "sind", "soll", "sollen", "sollst", "sollt", "sonst", "soweit", "sowie", "und", "unser", "unsere", "unter", "vom", "von", "vor", "wann", "warum", "was", "weiter", "weitere", "wenn", "wer", "werde", "werden", "werdet", "weshalb", "wie", "wieder", "wieso", "wir", "wird", "wirst", "wo", "woher", "wohin", "zu", "zum", "zur", "über"));
		if (language.equals("de")){
			stopwordSet = germanStopwords;
		}
		else if(language.equals("en")){
			stopwordSet = englishStopwords;
		}
		
		for (Object e : a) {
			if (!(stopwordSet.contains(((HashMap.Entry<String, Integer>) e).getKey().toLowerCase()))){
				candidates.add(((HashMap.Entry<String, Integer>) e).getKey() + "\t"
						+ ((HashMap.Entry<String, Integer>) e).getValue());
			}
		}
		
		return candidates;
	}
	
	
	static String readFile(String path, Charset encoding) 
			  throws IOException 
			{
			  byte[] encoded = Files.readAllBytes(Paths.get(path));
			  return new String(encoded, encoding);
			}
	
	public static void main(String args[]){
		
		String testSent = "Pierre Vinken , 61 years old , will join the board as a nonexecutive director Nov. 29 ." +
				"Mr. Vinken is chairman of Elsevier N.V. , the Dutch publishing group .";
		Model nifModel = NIFWriter.initializeOutputModel();
		//NIFWriter.addInitialString(nifModel, testSent, "http://dkt.dfki.de/documents/#");
		
		try {
			NIFWriter.addInitialString(nifModel, readFile("C:\\Users\\pebo01\\Desktop\\data\\artComSampleFilesDBPediaTimeouts\\pg28497.txt", StandardCharsets.UTF_8), "http://dkt.dfki.de/documents/#");
			suggestCandidates(nifModel, "en", 0.1);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		//System.out.println(suggestCandidates(nifModel, "en", 0.5));
	}
	
}
