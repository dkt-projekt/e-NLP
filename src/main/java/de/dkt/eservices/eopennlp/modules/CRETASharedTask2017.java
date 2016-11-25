package de.dkt.eservices.eopennlp.modules;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.swing.event.PopupMenuListener;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;

//import de.dkt.eservices.erattlesnakenlp.modules.Span;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Label;
import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.trees.Constituent;
import edu.stanford.nlp.trees.Dependency;
import edu.stanford.nlp.trees.HeadFinder;
import edu.stanford.nlp.trees.LeftHeadFinder;
import edu.stanford.nlp.trees.SemanticHeadFinder;
import edu.stanford.nlp.trees.Tree;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
//import opennlp.tools.util.Span;
import opennlp.tools.util.Span;



/*
 * NOTE that this method is created only for the CUTE 2017 shared task. It's committed for backup purposes, but can be deleted after the shared task has been done. (It contains plenty of script-like, very inefficient code.
 */

public class CRETASharedTask2017 {
	
	public static String modelsDirectory = "trainedModels" + File.separator + "ner" + File.separator;
	static Integer NPLenghtThreshold = 10;
	static Integer NPLenghtThresholdForMinisterCases = 6;
	
	static String readFile(String path, Charset encoding) 
			  throws IOException 
			{
			  byte[] encoded = Files.readAllBytes(Paths.get(path));
			  return new String(encoded, encoding);
			}
	
	
	
	public static ArrayList<NameFinderME> populateNameFinderList(String[] modelNames){
		
		ArrayList<NameFinderME> nfList = new ArrayList<NameFinderME>();
		try{
			for (String modelName : modelNames) {
				ClassPathResource cprNERModel = new ClassPathResource(modelsDirectory + modelName);
				InputStream tnfNERModel = new FileInputStream(cprNERModel.getFile());
				TokenNameFinderModel tnfModel = new TokenNameFinderModel(tnfNERModel);
				NameFinderME nameFinder = new NameFinderME(tnfModel);
				nfList.add(nameFinder);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return nfList;
	}
	

	
	public static HashMap<Span, String> traverseTreeForCTypes(Tree tree, HashMap<Span, String> npHash, ArrayList<String> cTypes){

		
		for (Tree subtree : tree.getChildrenAsList()){
			if (cTypes.contains(subtree.label().toString())){
				List<Word> wl = subtree.yieldWords();
				int begin = wl.get(0).beginPosition();
				int end = wl.get(wl.size()-1).endPosition();
				Span sp = new Span(begin, end);
				HeadFinder hf = new SemanticHeadFinder(); // may want to experiment here with different types of headfinders (http://nlp.stanford.edu/nlp/javadoc/javanlp/edu/stanford/nlp/trees/HeadFinder.html
				try{
					npHash.put(sp, subtree.headTerminal(hf).toString());
				}catch(IllegalArgumentException e){
					//System.err.println("WARNING: could not find head for: " + subtree + ".\n");
					npHash.put(sp, "HEADNOTFOUND");
				}
			}
			if (!(subtree.isPreTerminal())){
				traverseTreeForCTypes(subtree, npHash, cTypes);
			}
		}
		
		return npHash;
	}
	
	public static ArrayList<String> getPersonFullNameList(String filePath){
		ArrayList<String> personNames = new ArrayList<String>();
		String content;
		try {
			content = readFile(filePath, StandardCharsets.UTF_8);
			for (String line : content.split("\n")){
				personNames.add(line.trim());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return personNames;
	}
	
	public static HashMap<ArrayList, HashMap<String, Double>> findTypedTokens(ArrayList<String> typeTokens, Span[] sentenceSpans, String content, HashMap<ArrayList, HashMap<String, Double>> entityMap, String checkType){
		
		for (Span s : sentenceSpans){
			String sent = content.substring(s.getStart(), s.getEnd());
			String[] tokens = sent.split(" ");
			Span[] tokenSpans = new Span[tokens.length];
			int k = 0;
			for (int m = 0; m < tokens.length; m++){
				String token = tokens[m];
				Span sp = new Span(k, k + token.length());
				k = k + token.length() + 1;
				tokenSpans[m] = sp;
			}
			for (int i = 0; i < tokenSpans.length; i++) {
				int tokenStartIndex = tokenSpans[i].getStart() + s.getStart();
				int tokenEndIndex = tokenSpans[i].getEnd() + s.getStart(); 
				ArrayList<Integer> tl = new ArrayList<Integer>();
				tl.add(tokenStartIndex);
				tl.add(tokenEndIndex);
				String token = content.substring(tokenStartIndex, tokenEndIndex);
				if (typeTokens.contains(token)){
					HashMap<String, Double> im = new HashMap<String, Double>();
					im.put(checkType,  1.0);
					entityMap.put(tl, im);
				}
			}
			
		}
		
		return entityMap;
	}
	
	
	public static HashMap<ArrayList, HashMap<String, Double>> findPersonNamesWithList(HashMap<ArrayList, HashMap<String, Double>> entityMap, ArrayList<String> personFullNames, String content, Span[] sentenceSpans){
		
		// using sentenceSpans here, but could do it directly on content as well, I think....
		for (Span s : sentenceSpans){
			String sent = content.substring(s.getStart(), s.getEnd());
			for (String name : personFullNames){
				int lastIndex = 0;
				while(lastIndex != -1){
				    lastIndex = sent.indexOf(name,lastIndex);
				    if(lastIndex != -1){
				    	int start = lastIndex + s.getStart();
				    	int end = lastIndex + name.length() + s.getStart();
				    	lastIndex += name.length();
				    	ArrayList<Integer> temp = new ArrayList<Integer>(); // hrrr, this is why I don't like Java
				    	temp.add(start);
				    	temp.add(end);
				    	HashMap<String, Double> tempMap = new HashMap<String, Double>();
				    	tempMap.put("PER", 1.0); // putting dummy value, TODO: check type (PER or Person?)
				    	entityMap.put(temp,  tempMap);
				    	//System.out.println("DEBUGGING: adding literal name here: " + content.substring(start, end));
				    }
				}
			}
		}
		
		return entityMap;
	}
	
	public static ArrayList<String> splitFullNames(ArrayList<String> fullNames){
		ArrayList<String> retList = new ArrayList<String>();
		for (String name : fullNames){
			String[] names = name.split(" ");
			for (String n : names){
				retList.add(n);
			}
		}
		return retList;
	}
	
	public static HashMap<ArrayList, HashMap<String, Double>> findSubstringsInNPs(ArrayList<String> personIndicatorSubstrings, Span[] sentenceSpans, String content, HashMap<ArrayList, HashMap<String, Double>> entityMap){
		
		LexicalizedParser lexParser = LexicalizedParser.loadModel("edu/stanford/nlp/models/lexparser/germanPCFG.ser.gz","-maxLength", "70");
		ArrayList<String> cTypes = new ArrayList<String>();
		cTypes.add("NP");
		cTypes.add("PP"); // maybe exclude this is precision is getting too low
		cTypes.add("CNP"); // maybe exclude this is precision is getting too low
		for (Span s : sentenceSpans){
			String sent = content.substring(s.getStart(), s.getEnd());
			Tree tree = lexParser.parse(sent);
			HashMap<Span, String> npHash = traverseTreeForCTypes(tree, new HashMap<Span, String>(), cTypes);
			for (String substr : personIndicatorSubstrings) {
				for (Span sp : npHash.keySet()) {
					String npString = sent.substring(sp.getStart(), sp.getEnd());
					if (npString.toLowerCase().contains(substr)) {
						HashMap<String, Double> tempMap = new HashMap<String, Double>();
						tempMap.put("PER", 1.0);
						ArrayList<Integer> tl = new ArrayList<Integer>();
						tl.add(sp.getStart() + s.getStart());
						tl.add(sp.getEnd() + s.getStart());
						if (npString.split(" ").length < NPLenghtThresholdForMinisterCases) { // this check to exclude too long nounphrases. Hopefully, the lexparser will have found the smaller, embedded nounphrase as well. And in nay case, these very long ones are not likely to be a person (as a whole)
							entityMap.put(tl, tempMap);
							//System.out.println("DEBUGGING: adding substringbased match here: " + content.substring(tl.get(0), tl.get(1)));
						}
					}
				}
			}
		}
		
		return entityMap;
	}
	

	
	public static HashMap<ArrayList, HashMap<String, Double>> findPersonsInNPs(ArrayList<String> personNames, Span[] sentenceSpans, String content, HashMap<ArrayList, HashMap<String, Double>> entityMap){
		
		LexicalizedParser lexParser = LexicalizedParser.loadModel("edu/stanford/nlp/models/lexparser/germanPCFG.ser.gz","-maxLength", "70");
		ArrayList<String> cTypes = new ArrayList<String>();
		cTypes.add("NP");
		cTypes.add("PP"); // maybe exclude this is precision is getting too low
		cTypes.add("CNP"); // maybe exclude this is precision is getting too low
		for (Span s : sentenceSpans){
			String sent = content.substring(s.getStart(), s.getEnd());
			Tree tree = lexParser.parse(sent);
			HashMap<Span, String> npHash = traverseTreeForCTypes(tree, new HashMap<Span, String>(), cTypes);
			for (String name : personNames){
				for (Span sp : npHash.keySet()){
					String npString = sent.substring(sp.getStart(), sp.getEnd());
					if (npString.toLowerCase().matches(String.format(".*\\b%s\\b.*", name.toLowerCase()))){
						HashMap<String, Double> tempMap = new HashMap<String, Double>();
						tempMap.put("PER", 1.0);
						ArrayList<Integer> tl = new ArrayList<Integer>();
						tl.add(sp.getStart() + s.getStart());
						tl.add(sp.getEnd() + s.getStart());
						if (npString.split(" ").length < NPLenghtThreshold){ // this check is here to exclude too long nounphrases. Hopefully, the lexparser will have found the smaller, embedded nounphrase as well. And in nay case, these very long ones are not likely to be a person (as a whole)
							entityMap.put(tl,  tempMap);
							//System.out.println("DEBUGGING: adding NPNamebased match here: " + content.substring(tl.get(0), tl.get(1)));
						}
					}
				}
			}
		}
		
		
		return entityMap;
	}
	
	public static ArrayList<String> cleanList(ArrayList<String> l){
		ArrayList<String> rl = new ArrayList<String>();
		// un-serialize stopword list
		ArrayList<String> swl = new ArrayList<String>();
		try {
			FileInputStream fileIn = new FileInputStream("C:\\Users\\pebo01\\workspace\\e-NLP\\src\\main\\resources\\stopwords\\german.ser");
			ObjectInputStream in = new ObjectInputStream(fileIn);
			swl = (ArrayList<String>) in.readObject();
			in.close();
			fileIn.close();
		} catch (Exception i) {
			i.printStackTrace();
		}
		
		for (String i : l){
			if (i.length() > 2){
				if (!(swl.contains(i.toLowerCase()))){
					rl.add(i);					
				}
			}
		}
		return rl;
	}
	
	public static ArrayList<String> populateTestLines(Span[] sentenceSpans, String content, HashMap<ArrayList, HashMap<String, Double>> entityMap){
		
		ArrayList<String> testLines = new ArrayList<String>();
		PrintWriter out = null;
		try {
			out = new PrintWriter(new File("C:\\Users\\pebo01\\Desktop\\debug.txt"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for (Span sentenceSpan : sentenceSpans) {
			String sentence = content.substring(sentenceSpan.getStart(), sentenceSpan.getEnd());
			String[] tokens = sentence.split(" ");
			Span[] tokenSpans = new Span[tokens.length];
			int k = 0;
			for (int m = 0; m < tokens.length; m++){
				String token = tokens[m];
				Span s = new Span(k, k + token.length());
				k = k + token.length() + 1;
				tokenSpans[m] = s;
			}
			String prevType = "O";
			for (int i = 0; i < tokenSpans.length; i++) {
				int tokenStartIndex = tokenSpans[i].getStart() + sentenceSpan.getStart();
				int tokenEndIndex = tokenSpans[i].getEnd() + sentenceSpan.getStart(); 
				ArrayList<Integer> tl = new ArrayList<Integer>();
				tl.add(tokenStartIndex);
				tl.add(tokenEndIndex);
				String type = "O";
				boolean entityFound = false;
				for (ArrayList<Integer> entSpan : entityMap.keySet()){
					if ((tokenStartIndex >= entSpan.get(0)) && (tokenEndIndex <= entSpan.get(1))){
						//System.out.println(String.format("Token: '%s' with index '%s, %s' was found in '%s' with index '%s, %s'", content.substring(tokenStartIndex, tokenEndIndex), Integer.toString(tokenStartIndex), Integer.toString(tokenEndIndex), content.substring(entSpan.get(0), entSpan.get(1)), Integer.toString(entSpan.get(0)), Integer.toString(entSpan.get(1))));
						entityFound = true;
						if (entityMap.get(entSpan).size() == 1){
							for (String key : entityMap.get(entSpan).keySet()){
								if (prevType.equals(key)){
									type = "I-" + key;
								}
								else{
									type = "B-" + key;
								}
								prevType = key;
							}
						}
						else{
							Double highestProb = 0.0; // currently not working with probabilities really, so this is a bit pointless now, but if I add it, it's already in place...
							String finalType = null;
							HashMap<String, Double> innerMap = entityMap.get(entSpan);
							for (String key : innerMap.keySet()){
								String sType = key;
								Double prob = innerMap.get(key);
							    if (prob > highestProb){
						        	finalType = sType;
						        	highestProb = prob;
						        }
							}
							if (prevType.equals(finalType)){
						    	type = "I-" + finalType;
						    }
						    else{
						    	type = "B-" + finalType;
						    }
						    prevType = finalType;
							
						}
					}
				}
				
				if (!entityFound){
					prevType = "O";
					}
				String token = content.substring(tokenStartIndex, tokenEndIndex);
				out.write(token + "\t" + type + "\n");
				testLines.add(token + "\t" + type + "\n");
				//System.out.println(token + "\t" + type);
			}
			out.write("\n");
			testLines.add("\n");
			
			//System.out.println("\n");
		}
		out.close();
		
		return testLines;
		
	}
	
	public static void evalNER(ArrayList<String> testLines, ArrayList<String> goldLines, boolean strict, String entityType){
		
		if (testLines.size() != goldLines.size()){
			System.err.println("ERROR: test and gold sets are not of same length. Exiting now.\n");
			return;
		}
		
		double tp = 0.0;
		double tn = 0.0;
		double fp = 0.0;
		double fn = 0.0;
				
		for (int i = 0; i < testLines.size(); i++){
			String testline = testLines.get(i);
			String goldline = goldLines.get(i);
			if (testline.contains("\t") && goldline.contains("\t")){
				String[] testparts = testline.split("\t");
				String[] goldparts = goldline.split("\t");
				String testType = testparts[testparts.length-1].trim();
				String goldType = goldparts[goldparts.length-1].trim();
				//System.out.println(String.format("Test word and type: '%s', '%s'\tGold word and type: '%s', '%s'", testparts[0], testType, goldparts[0], goldType));
				if (strict == false){ //  TODO: note that this doesn't work with entityType. Fix if I want to do strict evaluation!
					testType = testType.replaceAll("^[BI]\\-", "");
					goldType = goldType.replaceAll("^[BI]\\-", "");
				}
				//System.out.println(String.format("Comparing: '%s' and '%s' to argType '%s'", testType, goldType, entityType));
				if (testType.equalsIgnoreCase(entityType) && goldType.equalsIgnoreCase(entityType)){
					tp += 1;
				}
				else if(testType.equalsIgnoreCase(entityType) && !(goldType.equalsIgnoreCase(entityType))){
					fp += 1;
					System.out.println("FALSE POSITIVE: " + Arrays.toString(testparts).replaceAll("\n", "").trim());
				}
				else if (!(testType.equalsIgnoreCase(entityType)) && goldType.equalsIgnoreCase(entityType)){
					fn += 1;
				}
				else if (!(testType.equalsIgnoreCase(entityType)) && !(goldType.equalsIgnoreCase(entityType))){
					tn += 1;
				}
			}
		}
		
		System.out.println("tp:" + tp);
		System.out.println("tn:" + tn);
		System.out.println("fp:" + fp);
		System.out.println("fn:" + fn);
		
		double p = 0.0;
		double r = 0.0;
		if (tp + fp != 0){
			p = tp / (tp + fp);
		}
		if (tp + fn != 0){
			r = tp / (tp + fn);
		}
		
		double f = 0.0;
		if (p + r != 0){
			f = 2 * ((p * r) / (p + r));
		}
		System.out.println("P: " + p);
		System.out.println("R: " + r);
		System.out.println("F: " + f);
	}
	
	public static ArrayList<String> learnFromCONLLData(String filePath, String checkType){
		
		ArrayList<String> perTokens = new ArrayList<String>(); // this ArrayList<String> structure only allows for one definitive type. If I want to include probabilities, have to go for a nested map (i.e. just return probMap below)
		Double threshold = 5.0;
		try {
			ArrayList<String> trainingLines = (ArrayList)IOUtils.readLines(new FileInputStream(filePath));
			HashMap<String, HashMap<String, Double>> probMap = new HashMap<String, HashMap<String, Double>>(); 
			for (String s : trainingLines){
				if (s.contains("\t")){
					String[] parts = s.split("\t");
					String word = parts[0];
					String type = parts[1].replaceAll("^[BI]\\-", "");
					HashMap<String, Double> tm = new HashMap<String, Double>();
					Double score = 1.0;
					if (probMap.containsKey(word)){
						HashMap<String, Double> im = probMap.get(word);
						if (im.containsKey(type)){
							score += im.get(type); 
						}
					}
					tm.put(type,  score);
					probMap.put(word, tm);
				}
			}
			for (String word : probMap.keySet()){
				HashMap<String, Double> im = probMap.get(word);
				if (im.containsKey(checkType) && im.containsKey("O")){
					Double perScore = im.get(checkType);
					Double oScore = im.get("O");
					if (perScore > oScore * threshold){
						perTokens.add(word);
					}
				}
				else if(im.containsKey(checkType) && !(im.containsKey("O"))){
					perTokens.add(word);					
				}
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return perTokens;
		
	}
	
	
	public static void bundestagDebattenExperiment(HashMap<ArrayList, HashMap<String, Double>> entityMap, String content, Span[] sentenceSpans){
		
		/*****************************
		 BUNDESTAGDEBATTEN EXPERIMENTS
		 *****************************/
		
		//First Sieve: find names based on nameList
		ArrayList<String> personFullNames = getPersonFullNameList("C:\\Users\\pebo01\\Desktop\\data\\CRETA2017\\Bundestagdebatten\\BundestagmitgliederListeManuallyFiltered.txt");
		entityMap = findPersonNamesWithList(entityMap, personFullNames, content, sentenceSpans);
		System.out.println("INFO: Size after strict name spotting: " + entityMap.keySet().size());
		//Second Sieve: split fullname list into firstname lastname, extract NPs, any NP with a name in it, tag that whole NP. (probably will only find stuff based on lastname, but there may be chinese/other names in there for which it is not immediately obvious which is the first and last name (at least to me), middle names, etc.
		ArrayList<String> personNames = splitFullNames(personFullNames);
		// doing some cleaning up of personNames to exclude stopwords, middle name abbrevs (A., which, due to the dot, are especially nasty for regex matching), etc.
		personNames = cleanList(personNames);
		entityMap = findPersonsInNPs(personNames, sentenceSpans, content, entityMap);
		System.out.println("INFO: Size after NP-based name spotting: " + entityMap.keySet().size());
		//Third Sieve: use some keywords that indicate a person in the bundestag-domain specifically
		ArrayList<String> personIndicatorSubstrings = new ArrayList<String>();
		personIndicatorSubstrings.add("minister");
		personIndicatorSubstrings.add("chef");
		personIndicatorSubstrings.add("kanzler");
		personIndicatorSubstrings.add("minister");
		personIndicatorSubstrings.add("präsident");
		personIndicatorSubstrings.add("kommisar");
		personIndicatorSubstrings.add("richter");
		entityMap = findSubstringsInNPs(personIndicatorSubstrings, sentenceSpans, content, entityMap);
		System.out.println("INFO: Size after NP-based substring spotting: " + entityMap.keySet().size());
		
		ArrayList<String> personPrefixes = new ArrayList<String>();
		personPrefixes.add("dr");
		personPrefixes.add("prof");
		personPrefixes.add("professor");
		personPrefixes.add("herr");
		personPrefixes.add("frau");
		
		// Fourth Sieve: using prefixes and tag the whole NP as PER 
		entityMap = findPersonsInNPs(personPrefixes, sentenceSpans, content, entityMap); // this is an abuse of the findPersonsInNPs method, because I'm not feeding it names but prefixes, but procedure is the same
		System.out.println("INFO: Size after NP-based prefix spotting: " + entityMap.keySet().size());
		
		System.out.println("entityMap:" + entityMap);
		for (ArrayList<Integer> entSpan : entityMap.keySet()){
			System.out.println("entSpan:" + content.substring(entSpan.get(0), entSpan.get(1)));
		}
		
		
		// sort of Fifth Sieve: using the annotated data to extract word frequencies and consider any word of which the likelihood of it being a PER is x times higher than it being O (or any other type?) as entity.
		// This means (unless I split the data) that I'm testing on seen data. But doing it here just as an indication of how much it would improve upon the current stand)
		ArrayList<String> typeTokens = learnFromCONLLData("C:\\Users\\pebo01\\Desktop\\data\\CRETA2017\\Bundestagdebatten\\conll", "PER");
		entityMap = findTypedTokens(typeTokens, sentenceSpans, content, entityMap, "PER");
		//TODO on monday: debug this learnFromCONLLData and findTypedTokens. Then check how much f increases (or decreases :). 	
		// then move on with parzival
		
		
		
//		String[] modelNames = {"goethe_PER.bin", "goethe_LOC.bin"};
//		ArrayList<NameFinderME> nfList = populateNameFinderList(modelNames);
//		
//		for (NameFinderME nfModel : nfList){
//			for (Span sentenceSpan : sentenceSpans) {
//				String sentence = content.substring(sentenceSpan.getStart(), sentenceSpan.getEnd());
//				//Span tokenSpans[] = Tokenizer.simpleTokenizeIndices(sentence);
//				//String tokens[] = Span.spansToStrings(tokenSpans, sentence);
//				String[] tokens = sentence.split(" ");
//				Span[] tokenSpans = new Span[tokens.length];
//				int k = 0;
//				for (int m = 0; m < tokens.length; m++){
//					String token = tokens[m];
//					Span s = new Span(k, k + token.length());
//					k = k + token.length() + 1;
//					tokenSpans[m] = s;
//				}
//				
//				Span nameSpans[];
//				synchronized (nameFinder) {
//					nameSpans = nfModel.find(tokens);
//				}
//				for (Span s : nameSpans) {
//					int nameStartIndex = 0;
//					int nameEndIndex = 0;
//					for (int i = 0; i <= tokenSpans.length; i++) {
//						if (i == s.getStart()) {
//							nameStartIndex = tokenSpans[i].getStart() + sentenceSpan.getStart();
//						} else if (i == s.getEnd()) {
//							nameEndIndex = tokenSpans[i - 1].getEnd() + sentenceSpan.getStart();
//						}
//					}
//					ArrayList<Integer> se = new ArrayList<Integer>();
//					se.add(nameStartIndex);
//					se.add(nameEndIndex);
//					HashMap<String, Double> spanMap = entityMap.get(se);
//					if (spanMap == null) {
//						spanMap = new HashMap<String, Double>();
//					}
//					spanMap.put(s.getType(), s.getProb());
//					entityMap.put(se, spanMap);
//				}
//			}
//
//		}
		
		
//		
		// now do the conll style printing
		ArrayList<String> testLines = populateTestLines(sentenceSpans, content, entityMap);
		
		//System.out.println("Done.");
//		ArrayList<String> goldLines = new ArrayList<String>();
		
		ArrayList<String> goldLines;
		try {
			goldLines = (ArrayList)IOUtils.readLines(new FileInputStream("C:\\Users\\pebo01\\Desktop\\data\\CRETA2017\\Bundestagdebatten\\conll\\gold.conll"));
			
					
			evalNER(testLines, goldLines, false, "PER");
			
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		
	}
	
	
	public static void parzivalExperiment(HashMap<ArrayList, HashMap<String, Double>> entityMap, String content, Span[] sentenceSpans){
		
		
		
	}
	
	
	public static void main(String[] args) {
		
						
		try {
			PrintWriter out = new PrintWriter(new File("C:\\Users\\pebo01\\Desktop\\debug.txt"));
			String content = readFile("C:\\Users\\pebo01\\Desktop\\data\\CRETA2017\\Bundestagdebatten\\test.plaintext", StandardCharsets.UTF_8);
			String modelsDirectory = "trainedModels" + File.separator + "ner" + File.separator;
			NameFinderME nameFinder = null;
			HashMap<ArrayList, HashMap<String, Double>> entityMap = new HashMap<>();
			//Span[] sentenceSpans = SentenceDetector.detectSentenceSpans(content, "de-sent.bin");
			ArrayList<Span> ss = new ArrayList<Span>();
			String[] sentenceList = content.split("\n");
			Span[] sentenceSpans = new Span[sentenceList.length];
			int j = 0;
			for (int l = 0; l < sentenceList.length; l++){
				String sent = sentenceList[l];
				Span s = new Span(j, j + sent.length());
				j = j + sent.length() + 1;
				sentenceSpans[l] = s;
			}

			bundestagDebattenExperiment(entityMap, content, sentenceSpans);
			
			//parzivalExperiment(entityMap, content, sentenceSpans);
			
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		
	}
	

}
