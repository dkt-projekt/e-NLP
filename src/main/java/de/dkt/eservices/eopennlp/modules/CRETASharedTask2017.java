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
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;

import com.hp.hpl.jena.query.ParameterizedSparqlString;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.ResultSet;
import de.dkt.eservices.erattlesnakenlp.modules.Sparqler;
import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.trees.HeadFinder;
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
	//static Integer NPLenghtThreshold = 8;
	static Integer NPLenghtThresholdForMinisterCases = 6;
	static Double learningThreshold = 5.0;
	
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
	
	
	public static ArrayList<String> appendToNameList(ArrayList<String> l, String filePath){
		String content;
		try {
			content = readFile(filePath, StandardCharsets.UTF_8);
			for (String line : content.split("\n")){
				l.add(line.trim());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return l;
	}
	
	public static ArrayList<String> getFullNameList(String filePath){
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
	
	
	public static HashMap<ArrayList, HashMap<String, Double>> findTypedTokensIncludeChunking(ArrayList<String> typeTokens, Span[] sentenceSpans, String content, HashMap<ArrayList, HashMap<String, Double>> entityMap, String checkType, HashMap<String, HashMap<String, HashMap<String, HashMap<String, Double>>>> qnh){

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
					if (qnh.containsKey(token)){
						HashMap<String, HashMap<String, Double>> ilm = qnh.get(token).get("LEFT");
						HashMap<String, HashMap<String, Double>> irm = qnh.get(token).get("RIGHT");
						// this is just looking one step back. If this works, go further back
						if (i > 1){
							String leftToken = content.substring(tokenSpans[i-1].getStart() + s.getStart(), tokenSpans[i-1].getEnd() + s.getStart());
							String preLeftToken = content.substring(tokenSpans[i-2].getStart() + s.getStart(), tokenSpans[i-2].getEnd() + s.getStart());
							if (ilm.containsKey(leftToken) && irm.containsKey(leftToken)){
								if(ilm.get(leftToken).containsKey(preLeftToken) && ilm.get(leftToken).containsKey(token) && irm.get(leftToken).containsKey(preLeftToken) && irm.get(leftToken).containsKey(token)){
									System.out.println("GETTING HERE");
								}
							}
						}
					}
					entityMap.put(tl, im);
				}
			}
		}
		
		return entityMap;
	}
	
	
	public static HashMap<ArrayList, HashMap<String, Double>> findTypedTokensAugmented(ArrayList<String> typeTokens, Span[] sentenceSpans, String content, HashMap<ArrayList, HashMap<String, Double>> entityMap, String checkType, ArrayList<String> excludeTokens){
		
		ArrayList<String> tokenListwithoutExcludeTokens = new ArrayList<String>();
		for (String s : typeTokens){
			if (!(excludeTokens.contains(s))){
				tokenListwithoutExcludeTokens.add(s);
			}
		}
		
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
					// check if it is in excludetokens, and if so, if there are other words around
					boolean b = false;
					if (excludeTokens.contains(token)){
						// taking a window size of 3, experiment with making it smaller or bigger
						int startOfWindow = tokenSpans[Math.max(0,  i-3)].getStart() + s.getStart();
						int endOfWindow = tokenSpans[Math.min(tokenSpans.length-1, i+3)].getEnd() + s.getStart();
						String windowStr = content.substring(startOfWindow, endOfWindow);
						System.out.println("DEBUGGING windowString:" + windowStr);
						for (String str : tokenListwithoutExcludeTokens){
							if (windowStr.contains(str)){
								System.out.println("REALNAME FOUND:" + str);
								b = true;
							}
						}
					}
					if (b){
						HashMap<String, Double> im = new HashMap<String, Double>();
						im.put(checkType,  1.0);
						entityMap.put(tl, im);
					}
				}
			}
			
		}
		
		return entityMap;
	}
	
	public static HashMap<ArrayList, HashMap<String, Double>> findTypedTokensWithExcludeList(ArrayList<String> typeTokens, Span[] sentenceSpans, String content, HashMap<ArrayList, HashMap<String, Double>> entityMap, String checkType, ArrayList<String> excludeTokens){
		
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
				String token = content.substring(tokenStartIndex, tokenEndIndex);
				if (typeTokens.contains(token)){
					ArrayList<Integer> tl = new ArrayList<Integer>();
					for (String ex : excludeTokens){
						// this below is an ugly hack to also include some determiners (or words that otherwise would not make it) in the annotated part. Don't really agree with this procedure, but it happened in the training data all over the place, so I added it.
						if (tokenStartIndex > ex.length()+1){
							if (content.substring(tokenStartIndex - ex.length() - 1, tokenEndIndex).toLowerCase().startsWith(String.format("%s ", ex.toLowerCase()))){
								tokenStartIndex = tokenStartIndex - ex.length() - 1;
							}
						}
					}
					tl.add(tokenStartIndex);
					tl.add(tokenEndIndex);
					HashMap<String, Double> im = new HashMap<String, Double>();
					im.put(checkType,  1.0);
					if (!(token.toLowerCase().startsWith("letzte"))){
						if (!(excludeTokens.contains(token))){ // I added this one for Goethe, since it kept annotating "meinem", even though I put it in the excludeList before. DEBUG!
							entityMap.put(tl, im);
						}
					}
				}
			}
			
		}
		
		return entityMap;
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
				//typeTokens = lowercaseList(typeTokens);
				if (typeTokens.contains(token)){
				//if (typeTokens.contains(token.toLowerCase())){
					HashMap<String, Double> im = new HashMap<String, Double>();
					im.put(checkType,  1.0);
					entityMap.put(tl, im);
				}
			}
			
		}
		
		return entityMap;
	}
	
	public static ArrayList<String> lowercaseList(ArrayList<String> l){
		
		ArrayList<String> retList = new ArrayList<String>();
		for (String s : l){
			retList.add(s.toLowerCase());
		}
		return retList;
	}
	
	
	public static ArrayList<String> getUppercaseTokens(Span[] sentenceSpans, String content){
		
		ArrayList<String> uct = new ArrayList<String>();
		
		for (Span s : sentenceSpans){
			String sent = content.substring(s.getStart(), s.getEnd());
			Pattern pattern = Pattern.compile("[A-Z]\\S+");
		    Matcher matcher = pattern.matcher(sent);
		    while (matcher.find()) {
		    	int startInd = matcher.start() + s.getStart();
		    	int endInd = matcher.end() + s.getStart();
		    	if (startInd != s.getStart()){ // to correct for sentence-initial words
		    		String token = content.substring(startInd, endInd);
		    		uct.add(token);
		    	}
		    }
			
		}
		
		return uct;
		
	}
	
	public static HashMap<ArrayList, HashMap<String, Double>> findUppercaseTokens(Span[] sentenceSpans, String content, HashMap<ArrayList, HashMap<String, Double>> entityMap, String type){
		
		
		for (Span s : sentenceSpans){
			String sent = content.substring(s.getStart(), s.getEnd());
			Pattern pattern = Pattern.compile("[A-Z]\\S+");
		    Matcher matcher = pattern.matcher(sent);
		    while (matcher.find()) {
		    	int startInd = matcher.start() + s.getStart();
		    	int endInd = matcher.end() + s.getStart();
		    	ArrayList<Integer> temp = new ArrayList<Integer>();
		    	temp.add(startInd);
		    	temp.add(endInd);
		    	HashMap<String, Double> tempMap = new HashMap<String, Double>();
		    	tempMap.put(type, 1.0);
		    	if (startInd != s.getStart()){ // to correct for sentence-initial words
		    		//System.out.println("ADDING SOMETHING HERE:" + sent + "\t" + content.substring(startInd, endInd));
		    		entityMap.put(temp,  tempMap);
		    	}
		    }
			
		}
		
		return entityMap;
	}
	
	public static HashMap<ArrayList, HashMap<String, Double>> findAbbrevs(HashMap<ArrayList, HashMap<String, Double>> entityMap, String content, Span[] sentenceSpans, String type){
		
		
		for (Span s : sentenceSpans){
			String sent = content.substring(s.getStart(), s.getEnd());
			Pattern pattern = Pattern.compile("[A-Z]{2,}");
		    Matcher matcher = pattern.matcher(sent);
		    while (matcher.find()) {
		    	int startInd = matcher.start() + s.getStart();
		    	int endInd = matcher.end() + s.getStart();
		    	ArrayList<Integer> temp = new ArrayList<Integer>();
		    	temp.add(startInd);
		    	temp.add(endInd);
		    	HashMap<String, Double> tempMap = new HashMap<String, Double>();
		    	tempMap.put(type, 1.0);
		    	entityMap.put(temp,  tempMap);
		    	//System.out.println("ADDING:" + content.substring(startInd, endInd));
		    }
			
		}
		
		return entityMap;
	}
	
	public static ArrayList<Span> chunkInputText(Span[] sentenceSpans, String content, ArrayList<HashMap> chunkProbs){
	
		ArrayList<Span> returnSpans = new ArrayList<Span>();
		
		HashMap<String, HashMap<String, Double>> leftMap = chunkProbs.get(0);
		HashMap<String, HashMap<String, Double>> rightMap = chunkProbs.get(1);
		
		for (Span s : sentenceSpans){
			String sent = content.substring(s.getStart(), s.getEnd());
			//System.out.println("DEBUGGING sent:" + sent);
			String[] tokens = sent.split(" ");
			Span[] tokenSpans = new Span[tokens.length];
			int k = 0;
			for (int m = 0; m < tokens.length; m++){
				String token = tokens[m];
				Span sp = new Span(k, k + token.length());
				k = k + token.length() + 1;
				tokenSpans[m] = sp;
			}
			int spanStart = s.getStart();
			int spanEnd = s.getStart();
			for (int i = 1; i < tokenSpans.length-1; i++) {
				String token = content.substring(tokenSpans[i].getStart() + s.getStart(), tokenSpans[i].getEnd() + s.getStart());
				String leftToken = content.substring(tokenSpans[i-1].getStart() + s.getStart(), tokenSpans[i-1].getEnd() + s.getStart());
				String rightToken = content.substring(tokenSpans[i+1].getStart() + s.getStart(), tokenSpans[i+1].getEnd() + s.getStart());
				Double leftProb = 0.0;
				Double rightProb = 0.0;
				if (leftMap.containsKey(token)){
					if (leftMap.get(token).containsKey(leftToken)){
						leftProb = leftMap.get(token).get(leftToken);
					}
				}
				if (rightMap.containsKey(token)){
					if (rightMap.get(token).containsKey(rightToken)){
						rightProb = rightMap.get(token).get(rightToken);
					}
				}
				
				if (leftProb < rightProb || token.matches("[\\.,:;\\?\'\"!]")){
					spanEnd = tokenSpans[i].getEnd() + s.getStart();
					Span newSpan = new Span(spanStart, spanEnd);
					//System.out.println("Chunk found:" + content.substring(newSpan.getStart(), newSpan.getEnd()));
					returnSpans.add(newSpan);
					spanStart = spanEnd + 1;
				}
				
				else{
					// do nothing
				}
				
			}
		}
		
		return returnSpans;
		
	}
	
	public static HashMap<ArrayList, HashMap<String, Double>> findNamesInChunks(ArrayList<String> typeTokens, ArrayList<Span> chunks, String content, HashMap<ArrayList, HashMap<String, Double>> entityMap, String type){
		
		for (Span c : chunks){
			String chunk = content.substring(c.getStart(), c.getEnd());
			boolean b = false;
			for (String name : typeTokens){
				if (chunk.toLowerCase().contains(name.toLowerCase())){
					b = true;
				}
			}
			if (b){
				String[] tokens = chunk.split(" ");
				Span[] tokenSpans = new Span[tokens.length];
				int k = 0;
				for (int m = 0; m < tokens.length; m++){
					String token = tokens[m];
					Span sp = new Span(k, k + token.length());
					k = k + token.length() + 1;
					tokenSpans[m] = sp;
				}
				for (int i = 0; i < tokenSpans.length; i++) {
					ArrayList<Integer> temp = new ArrayList<Integer>();
			    	temp.add(tokenSpans[i].getStart() + c.getStart());
			    	temp.add(tokenSpans[i].getEnd() + c.getStart());
			    	HashMap<String, Double> tempMap = new HashMap<String, Double>();
			    	tempMap.put(type, 1.0);
			    	entityMap.put(temp,  tempMap);
				}
				
			}
		}
		
		return entityMap;
	}
	

	// only difference with the other one (findFullNamesWithList) is that here I'm also considering an 's' after the name. Perhaps I should include that in the default case....
	public static HashMap<ArrayList, HashMap<String, Double>> findFullNamesWithListGenitiveCases(HashMap<ArrayList, HashMap<String, Double>> entityMap, ArrayList<String> fullNames, String content, Span[] sentenceSpans, String type){
		
		// using sentenceSpans here, but could do it directly on content as well, I think....
		for (Span s : sentenceSpans){
			String sent = content.substring(s.getStart(), s.getEnd());
			for (String name : fullNames){
				name = name + "s";
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
				    	tempMap.put(type, 1.0);
				    	entityMap.put(temp,  tempMap);
				    	//System.out.println("DEBUGGING: adding literal name here: " + content.substring(start, end));
				    }
				}
			}
		}
		
		return entityMap;
	}
	
	
	public static HashMap<ArrayList, HashMap<String, Double>> findFullNamesWithList(HashMap<ArrayList, HashMap<String, Double>> entityMap, ArrayList<String> fullNames, String content, Span[] sentenceSpans, String type){
		
		// using sentenceSpans here, but could do it directly on content as well, I think....
		for (Span s : sentenceSpans){
			String sent = content.substring(s.getStart(), s.getEnd());
			for (String name : fullNames){
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
				    	tempMap.put(type, 1.0);
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
	
	public static HashMap<ArrayList, HashMap<String, Double>> findAbbrevsInNPs(HashMap<ArrayList, HashMap<String, Double>> entityMap, String content, Span[] sentenceSpans, String type){
		
		LexicalizedParser lexParser = LexicalizedParser.loadModel("edu/stanford/nlp/models/lexparser/germanPCFG.ser.gz","-maxLength", "70");
		ArrayList<String> cTypes = new ArrayList<String>();
		cTypes.add("NP");
//		cTypes.add("PP"); // maybe exclude this is precision is getting too low
//		cTypes.add("CNP"); // maybe exclude this is precision is getting too low
		for (Span s : sentenceSpans){
			String sent = content.substring(s.getStart(), s.getEnd());
			Tree tree = lexParser.parse(sent);
			HashMap<Span, String> npHash = traverseTreeForCTypes(tree, new HashMap<Span, String>(), cTypes);
			for (Span sp : npHash.keySet()) {
				String npString = sent.substring(sp.getStart(), sp.getEnd());
				int realEnd = sp.getEnd();
				if (npString.matches("^.*\\s\\W$")){ // if the NP ends on punctuation, do not consider that
					realEnd = sp.getEnd()-2;
					npString = npString.substring(0,  npString.length()-2);
				}
				Pattern pattern = Pattern.compile("[A-Z]{2,}$"); // try with other word characters (to also capture EU-Rat)
			    Matcher matcher = pattern.matcher(npString);
			    while (matcher.find()) {
//			    	int startInd = matcher.start() + sp.getStart() + s.getStart();
//			    	int endInd = matcher.end() + sp.getStart() + s.getStart();
			    	ArrayList<Integer> temp = new ArrayList<Integer>();
			    	temp.add(s.getStart() + sp.getStart());
			    	temp.add(s.getStart() + realEnd);
			    	HashMap<String, Double> tempMap = new HashMap<String, Double>();
			    	tempMap.put(type, 1.0);
			    	if (npString.split(" ").length < NPLenghtThresholdForMinisterCases) {
			    		//System.out.println("DEBUGIN adding:" + content.substring(temp.get(0), temp.get(1)));
						entityMap.put(temp, tempMap);
					}
			    }
			}
		}
		
		return entityMap;
	}
	
	
	
	public static HashMap<ArrayList, HashMap<String, Double>> findSubstringsInNPs(ArrayList<String> nameIndicatorSubstrings, Span[] sentenceSpans, String content, HashMap<ArrayList, HashMap<String, Double>> entityMap, String type){
		
		LexicalizedParser lexParser = LexicalizedParser.loadModel("edu/stanford/nlp/models/lexparser/germanPCFG.ser.gz","-maxLength", "70");
		ArrayList<String> cTypes = new ArrayList<String>();
		cTypes.add("NP");
		cTypes.add("PP"); // maybe exclude this is precision is getting too low
		cTypes.add("CNP"); // maybe exclude this is precision is getting too low
		for (Span s : sentenceSpans){
			String sent = content.substring(s.getStart(), s.getEnd());
			Tree tree = lexParser.parse(sent);
			HashMap<Span, String> npHash = traverseTreeForCTypes(tree, new HashMap<Span, String>(), cTypes);
			for (String substr : nameIndicatorSubstrings) {
				for (Span sp : npHash.keySet()) {
					String npString = sent.substring(sp.getStart(), sp.getEnd());
					int realEnd = sp.getEnd();
					if (npString.matches("^.*\\s\\W$")){ // if the NP ends on punctuation, do not consider that
						realEnd = sp.getEnd()-2;
						npString = npString.substring(0,  npString.length()-2);
					}
					if (npString.toLowerCase().contains(substr.toLowerCase())) {
						HashMap<String, Double> tempMap = new HashMap<String, Double>();
						tempMap.put(type, 1.0);
						ArrayList<Integer> tl = new ArrayList<Integer>();
						tl.add(sp.getStart() + s.getStart());
						tl.add(realEnd + s.getStart());
						if (!(npString.contains(substr + "schaft"))){ //to exclude presidentschaft-cases
							if (npString.split(" ").length < NPLenghtThresholdForMinisterCases) { // this check to exclude too long nounphrases. Hopefully, the lexparser will have found the smaller, embedded nounphrase as well. And in nay case, these very long ones are not likely to be a person (as a whole)
								entityMap.put(tl, tempMap);
								//System.out.println("DEBUGGING: adding substringbased match here: " + content.substring(tl.get(0), tl.get(1)));
							}
						}
					}
				}
			}
		}
		
		return entityMap;
	}
	
	public static boolean customSparqlr(String infoURL, String sparqlService){
		ParameterizedSparqlString sQuery = new ParameterizedSparqlString(
				"select ?uri ?info where {\n" +
				" ?uri ?infoURL ?info.\n" + 
				" FILTER (?uri = ?dbpediaURI)" + "}");
		sQuery.setIri("dbpediaURI", infoURL);
		sQuery.setIri("infoURL", "http://www.georss.org/georss/point");
		//System.out.println("DEBUGGING qyuery:" + sQuery.toString());
		QueryExecution exec = QueryExecutionFactory.sparqlService(sparqlService, sQuery.asQuery());
		// System.out.println("DEBUGGING final sparql query:" +
		exec.setTimeout(3000, TimeUnit.MILLISECONDS);
		try {
			ResultSet res = exec.execSelect();
			while (res.hasNext()) {
				return true;

			}
		} catch (Exception e) {
			//TODO
		}
		exec.close();
		return false;
		
	}

	public static HashMap<ArrayList, HashMap<String, Double>> findPPsStartingWithIn(HashMap<ArrayList, HashMap<String, Double>> entityMap, String content, Span[] sentenceSpans, String type){
		
		LexicalizedParser lexParser = LexicalizedParser.loadModel("edu/stanford/nlp/models/lexparser/germanPCFG.ser.gz","-maxLength", "70");
		ArrayList<String> cTypes = new ArrayList<String>();
		cTypes.add("PP");
		for (Span s : sentenceSpans){
			String sent = content.substring(s.getStart(), s.getEnd());
			Tree tree = lexParser.parse(sent);
			HashMap<Span, String> ppHash = traverseTreeForCTypes(tree, new HashMap<Span, String>(), cTypes);
			for (Span sp : ppHash.keySet()){
				String ppString = sent.substring(sp.getStart(), sp.getEnd());
				if (ppString.toLowerCase().startsWith("in ")){
					if (ppString.split(" ").length < 4){
						int realEnd = sp.getEnd();
						if (ppString.matches("^.*\\s\\W$")){ // if the NP ends on punctuation, do not consider that
							realEnd = sp.getEnd()-2;
						}
						HashMap<String, Double> tempMap = new HashMap<String, Double>();
						tempMap.put(type, 1.0);
						ArrayList<Integer> tl = new ArrayList<Integer>();
						tl.add(sp.getStart() + 3 + s.getStart()); // NOTE: this is very ugly and dangerous; hardcoding the +3, because I do not want to that the preceding "in" itself. Breaks if there are, for some reasons, two whitespaces, a punctuation mark, or whatever else.
						tl.add(realEnd + s.getStart());
						String entURI = Sparqler.getDBPediaURI(sent.substring(sp.getStart() + 3, realEnd), "de", "http://de.dbpedia.org/sparql", "http://de.dbpedia.org");
						//Modify this so I get return type or something that I can use
						if (entURI != null){
							boolean location = customSparqlr(entURI, "http://de.dbpedia.org/sparql");
							if (location == true){
								entityMap.put(tl,  tempMap);
								//System.out.println("DEBUG adding:" + ppString);
							}
							
						}
						//TODO combine to make more sense! This one gets high P and low R. Fix!
						
//						if (location == true){
//							entityMap.put(tl,  tempMap);
//							System.out.println("DEBUG adding:" + ppString);
//						}
					}

				}
				
			}
		}
		
		return entityMap;
	}
	
	
	public static HashMap<ArrayList, HashMap<String, Double>> findNamesInNPs(ArrayList<String> names, Span[] sentenceSpans, String content, HashMap<ArrayList, HashMap<String, Double>> entityMap, String type){
		
		LexicalizedParser lexParser = LexicalizedParser.loadModel("edu/stanford/nlp/models/lexparser/germanPCFG.ser.gz","-maxLength", "70");
		ArrayList<String> cTypes = new ArrayList<String>();
		cTypes.add("NP");
		cTypes.add("PP"); // maybe exclude this is precision is getting too low
		cTypes.add("CNP"); // maybe exclude this is precision is getting too low
		for (Span s : sentenceSpans){
			String sent = content.substring(s.getStart(), s.getEnd());
			Tree tree = lexParser.parse(sent);
			HashMap<Span, String> npHash = traverseTreeForCTypes(tree, new HashMap<Span, String>(), cTypes);
			for (String name : names){
				for (Span sp : npHash.keySet()){
					String npString = sent.substring(sp.getStart(), sp.getEnd());
					int realEnd = sp.getEnd();
					if (npString.matches("^.*\\s\\W$")){ // if the NP ends on punctuation, do not consider that
						realEnd = sp.getEnd()-2;
						npString = npString.substring(0,  npString.length()-2);
					}
					if (npString.toLowerCase().matches(String.format(".*\\b%s\\b.*", name.toLowerCase()))){
						HashMap<String, Double> tempMap = new HashMap<String, Double>();
						tempMap.put(type, 1.0);
						ArrayList<Integer> tl = new ArrayList<Integer>();
						tl.add(sp.getStart() + s.getStart());
						tl.add(realEnd + s.getStart());
						if (npString.split(" ").length < NPLenghtThreshold){ // this check is here to exclude too long nounphrases. Hopefully, the lexparser will have found the smaller, embedded nounphrase as well. And in nay case, these very long ones are not likely to be a person (as a whole)
							entityMap.put(tl,  tempMap);
//							System.out.println("DEBUGGING: adding NPNamebased match here: " + content.substring(tl.get(0), tl.get(1)));
//							System.out.println("DEBUG culprit:" + name);
						}
					}
				}
			}
		}
		
		
		return entityMap;
	}
	
	public static ArrayList<String> cleanList(ArrayList<String> ml, ArrayList<String> excludeList){
		
		ArrayList<String> retList = new ArrayList<String>();
		for (String s : ml){
			if (!(excludeList.contains(s.toLowerCase()))){
				retList.add(s);
			}
		}
		return retList;
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
			System.err.println(String.format("ERROR: test and gold sets are not of same length (%s != %s). Exiting now.\n", testLines.size(), goldLines.size()));
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
					System.out.println("FALSE POSITIVE\ttest type: " + Arrays.toString(testparts).replaceAll("\n", "").trim() + "\t gold type: " + Arrays.toString(goldparts).replaceAll("\n", "").trim());
				}
				else if (!(testType.equalsIgnoreCase(entityType)) && goldType.equalsIgnoreCase(entityType)){
					fn += 1;
					System.out.println("FALSE NEGATIVE\ttest type: " + Arrays.toString(testparts).replaceAll("\n", "").trim() + "\t gold type: " + Arrays.toString(goldparts).replaceAll("\n", "").trim());
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
		System.out.println("Scores for type:" + entityType);
		System.out.println("P: " + p);
		System.out.println("R: " + r);
		System.out.println("F: " + f);
	}
	
	public static ArrayList<String> learnFromCONLLData(String filePath, String checkType, ArrayList<String> extraData){
		
		ArrayList<String> perTokens = new ArrayList<String>(); // this ArrayList<String> structure only allows for one definitive type. If I want to include probabilities, have to go for a nested map (i.e. just return probMap below)
		
		try {
			ArrayList<String> trainingLines = (ArrayList)IOUtils.readLines(new FileInputStream(filePath));
			HashMap<String, HashMap<String, Double>> probMap = new HashMap<String, HashMap<String, Double>>();
			ArrayList<String> trainingVocab = new ArrayList<String>();
			for (String s : trainingLines){
				if (s.contains("\t")){
					String[] parts = s.split("\t");
					String word = parts[0];
					trainingVocab.add(word);
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
					if (perScore > oScore * learningThreshold){
						perTokens.add(word);
					}
				}
				else if(im.containsKey(checkType) && !(im.containsKey("O"))){
					perTokens.add(word);					
				}
			}
			if (!extraData.isEmpty()){
				for (String str : extraData){
					if (!trainingVocab.contains(str)){
						perTokens.add(str);
					}
				}
			}
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return perTokens;
		
	}
	
	public static ArrayList<HashMap> getChunkProbs(String filepath){
		
		HashMap<String, HashMap<String, Double>> leftMap = new HashMap<String, HashMap<String, Double>>();
		HashMap<String, HashMap<String, Double>> rightMap = new HashMap<String, HashMap<String, Double>>();
		ArrayList<HashMap> retList = new ArrayList<HashMap>();
		try {
			HashMap<String, Integer> tokenCount = new HashMap<String, Integer>();
			ArrayList<String> trainingLines = (ArrayList)IOUtils.readLines(new FileInputStream(filepath));
			for (String line : trainingLines){
				String[] tokens = line.split(" ");
				for (int i = 0; i < tokens.length; i++){
					String token = tokens[i];
					int tc = 1;
					if (tokenCount.containsKey(token)){
						tc += tokenCount.get(token);
					}
					tokenCount.put(token, tc);
					String leftToken = ((i > 0) ? tokens[i-1] : "SOS");
					HashMap<String, Double> ilm = new HashMap<String, Double>();
					Double v = 1.0;
					if (leftMap.containsKey(token)){
						ilm = leftMap.get(token);
						if (ilm.containsKey(leftToken)){
							v += ilm.get(leftToken);
						}
					}
					ilm.put(leftToken, v);
					leftMap.put(token,  ilm);
					
					String rightToken = ((i < tokens.length-1) ? tokens[i+1] : "EOS");
					HashMap<String, Double> irm = new HashMap<String, Double>();
					Double w = 1.0;
					if (rightMap.containsKey(token)){
						irm = rightMap.get(token);
						if (irm.containsKey(rightToken)){
							w += irm.get(rightToken);
						}
					}
					irm.put(rightToken,  w);
					rightMap.put(token, irm);
					
				}
			}
			
			// normalize
			for (String token : leftMap.keySet()){
				HashMap<String, Double> lim = leftMap.get(token);
				for (String st : lim.keySet()){
					lim.put(st,  lim.get(st) / tokenCount.get(token));
				}
			}
			
			for (String token : rightMap.keySet()){
				HashMap<String, Double> rim = rightMap.get(token);
				for (String st : rim.keySet()){
					rim.put(st,  rim.get(st) / tokenCount.get(token));
				}
			}
			
			retList.add(leftMap);
			retList.add(rightMap);
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return retList;
	}
	
	public static void bundestagDebattenExperiment(HashMap<ArrayList, HashMap<String, Double>> entityMap, String content, Span[] sentenceSpans){
		
		/*****************************
		 BUNDESTAGDEBATTEN EXPERIMENTS
		 *****************************/
		
		
		/*
		 * PER SECTION
		 */
		
		
		//First Sieve: find names based on nameList
//		ArrayList<String> personFullNames = getFullNameList("C:\\Users\\pebo01\\Desktop\\data\\CRETA2017\\Bundestagdebatten\\BundestagmitgliederListeManuallyFiltered.txt");
//		entityMap = findFullNamesWithList(entityMap, personFullNames, content, sentenceSpans, "PER");
//		for (ArrayList<Integer> tl : entityMap.keySet()){
//			System.out.println("Added in sieve 1:" + content.substring(tl.get(0), tl.get(1)));
//		}
//		System.out.println("INFO: Size after strict name spotting: " + entityMap.keySet().size());
//		//Second Sieve: split fullname list into firstname lastname, extract NPs, any NP with a name in it, tag that whole NP. (probably will only find stuff based on lastname, but there may be chinese/other names in there for which it is not immediately obvious which is the first and last name (at least to me), middle names, etc.
//		ArrayList<String> personNames = splitFullNames(personFullNames);
//		// doing some cleaning up of personNames to exclude stopwords, middle name abbrevs (A., which, due to the dot, are especially nasty for regex matching), etc.
//		personNames = cleanList(personNames);
//		entityMap = findNamesInNPs(personNames, sentenceSpans, content, entityMap, "PER");
//		for (ArrayList<Integer> tl : entityMap.keySet()){
//			System.out.println("Added in sieve 2:" + content.substring(tl.get(0), tl.get(1)));
//		}
//		System.out.println("INFO: Size after NP-based name spotting: " + entityMap.keySet().size());
//		//Third Sieve: use some keywords that indicate a person in the bundestag-domain specifically
//		ArrayList<String> personIndicatorSubstrings = new ArrayList<String>();
//		personIndicatorSubstrings.add("minister");
//		personIndicatorSubstrings.add("chef");
//		personIndicatorSubstrings.add("kanzler");
//		personIndicatorSubstrings.add("minister");
//		personIndicatorSubstrings.add("präsident");
//		personIndicatorSubstrings.add("kommisar");
//		personIndicatorSubstrings.add("richter");
//		entityMap = findSubstringsInNPs(personIndicatorSubstrings, sentenceSpans, content, entityMap, "PER");
//		for (ArrayList<Integer> tl : entityMap.keySet()){
//			System.out.println("Added in sieve 3:" + content.substring(tl.get(0), tl.get(1)));
//		}
//		System.out.println("INFO: Size after NP-based substring spotting: " + entityMap.keySet().size());
//		
//		ArrayList<String> personPrefixes = new ArrayList<String>();
//		personPrefixes.add("dr");
//		personPrefixes.add("prof");
//		personPrefixes.add("professor");
//		personPrefixes.add("herr");
//		personPrefixes.add("frau");
//		
//		// Fourth Sieve: using prefixes and tag the whole NP as PER 
//		entityMap = findNamesInNPs(personPrefixes, sentenceSpans, content, entityMap, "PER"); // this is an abuse of the findPersonsInNPs method, because I'm not feeding it names but prefixes, but procedure is the same
//		System.out.println("INFO: Size after NP-based prefix spotting: " + entityMap.keySet().size());
//		for (ArrayList<Integer> tl : entityMap.keySet()){
//			System.out.println("Added in sieve 4:" + content.substring(tl.get(0), tl.get(1)));
//		}
//		System.out.println("entityMap:" + entityMap);
//		for (ArrayList<Integer> entSpan : entityMap.keySet()){
//			System.out.println("entSpan:" + content.substring(entSpan.get(0), entSpan.get(1)));
//		}
//		
//		
//		// sort of Fifth Sieve: using the annotated data to extract word frequencies and consider any word of which the likelihood of it being a PER is x times higher than it being O (or any other type?) as entity.
//		// This means (unless I split the data) that I'm testing on seen data. But doing it here just as an indication of how much it would improve upon the current stand)
//		ArrayList<String> typeTokens = learnFromCONLLData("C:\\Users\\pebo01\\Desktop\\data\\CRETA2017\\Bundestagdebatten\\conll\\gold.conll", "PER", new ArrayList<String>());
//		entityMap = findTypedTokens(typeTokens, sentenceSpans, content, entityMap, "PER");

		
		/*
		 * END OF PER SECTION
		 */
		
		/*
		 * LOC SECTION
		 */
		
//		ArrayList<String> locationFullNames = getFullNameList("C:\\Users\\pebo01\\Desktop\\data\\CRETA2017\\Bundestagdebatten\\worldCountriesAugmented.txt");
//		
//		ArrayList<String> locationNames = splitFullNames(locationFullNames);
//		
//		entityMap = findPPsStartingWithIn(entityMap, content, sentenceSpans, "LOC");
//		for (ArrayList<Integer> tl : entityMap.keySet()){
//			System.out.println("Added in sieve 1:" + content.substring(tl.get(0), tl.get(1)));
//		}
//		System.out.println("INFO: Size after in-initial NPs: " + entityMap.keySet().size());
//		
//		// semi-cheat sieve: take all names in locationfullNames, then filter out stuff that can be discarded with the cheat-stuff (like Turkey, which is often an organisation instead of a location)
//		ArrayList<String> typeTokens = learnFromCONLLData("C:\\Users\\pebo01\\Desktop\\data\\CRETA2017\\Bundestagdebatten\\conll\\gold.conll", "LOC", locationFullNames);
//		ArrayList<String> excludeTokens = new ArrayList<String>();
//		excludeTokens.add("kein");
//		typeTokens = cleanList(typeTokens, excludeTokens);
//		typeTokens.add("Türkei"); // these tokens that are added here, in the training data appear mostly as organisation, hence they are not considered locations. For the final version however, just given them econdary type, should be better.
//		// find a strategy to decide on primary and secondary type. 
//		// update: f-score drops a lot when these are considered locations. Proposed solution: keep them in, but give them ORG type as primary always
//		typeTokens.add("Europa");
//		typeTokens.add("Deutschland");
//		typeTokens.add("EU");
//		entityMap = findTypedTokens(typeTokens, sentenceSpans, content, entityMap, "LOC");
//		for (ArrayList<Integer> tl : entityMap.keySet()){
//			System.out.println("Added in sieve 2:" + content.substring(tl.get(0), tl.get(1)));
//		}
//		String[] modelNames = {"goethe_PER.bin", "goethe_LOC.bin"};
//		ArrayList<NameFinderME> nfList = populateNameFinderList(modelNames);
	
 
		 
		/*
		 * END OF LOC SECTION
		 */
		
		
		/*
		 * ORG SECTION
		 */
		
//		entityMap = findAbbrevs(entityMap, content, sentenceSpans, "ORG");
//		System.out.println("INFO: Size after abbrev spotting: " + entityMap.keySet().size());
//		for (ArrayList<Integer> tl : entityMap.keySet()){
//			System.out.println("Added in sieve 1:" + content.substring(tl.get(0), tl.get(1)));
//		}
//		
//		// sort of Fifth Sieve: using the annotated data to extract word frequencies and consider any word of which the likelihood of it being a PER is x times higher than it being O (or any other type?) as entity.
//		// This means (unless I split the data) that I'm testing on seen data. But doing it here just as an indication of how much it would improve upon the current stand)
//		ArrayList<String> typeTokens = learnFromCONLLData("C:\\Users\\pebo01\\Desktop\\data\\CRETA2017\\Bundestagdebatten\\conll\\gold.conll", "ORG", new ArrayList<String>());
//		typeTokens = cleanList(typeTokens);
//		//entityMap = findNamesInNPs(typeTokens, sentenceSpans, content, entityMap, "ORG"); // this increases recall a lot, but drops precision a lot
//		
//		entityMap = findTypedTokens(typeTokens, sentenceSpans, content, entityMap, "ORG");
//		for (ArrayList<Integer> tl : entityMap.keySet()){
//			System.out.println("Added in sieve 2:" + content.substring(tl.get(0), tl.get(1)));
//		}
//		System.out.println("INFO: Size after cheat sieve: " + entityMap.keySet().size());
		
		
		/*
		 * END OF ORG SECTION
		 */
		
		/*
		 * WRK SECTION
		 */
		
		ArrayList<String> werkIndicatorSubstrings = new ArrayList<String>();
		werkIndicatorSubstrings.add("vertrag");
		werkIndicatorSubstrings.add("verträge");
		werkIndicatorSubstrings.add("charta");
		werkIndicatorSubstrings.add("verfassung");
		werkIndicatorSubstrings.add("kriterien");
		entityMap = findSubstringsInNPs(werkIndicatorSubstrings, sentenceSpans, content, entityMap, "WRK");
		System.out.println("INFO: Size after NP-based substring spotting: " + entityMap.keySet().size());
		for (ArrayList<Integer> tl : entityMap.keySet()){
			System.out.println("Added in sieve 1:" + content.substring(tl.get(0), tl.get(1)));
		}
		
		// sort of Fifth Sieve: using the annotated data to extract word frequencies and consider any word of which the likelihood of it being a PER is x times higher than it being O (or any other type?) as entity.
		// This means (unless I split the data) that I'm testing on seen data. But doing it here just as an indication of how much it would improve upon the current stand)
		ArrayList<String> typeTokens = learnFromCONLLData("C:\\Users\\pebo01\\Desktop\\data\\CRETA2017\\Bundestagdebatten\\conll\\gold.conll", "WRK", new ArrayList<String>());
		typeTokens = cleanList(typeTokens);
		ArrayList<String> excludeTokens = new ArrayList<String>();
		excludeTokens.add("europäischen");
		typeTokens = cleanList(typeTokens, excludeTokens);
		entityMap = findTypedTokens(typeTokens, sentenceSpans, content, entityMap, "WRK");
		System.out.println("INFO: Size after cheat sieve: " + entityMap.keySet().size());
		for (ArrayList<Integer> tl : entityMap.keySet()){
			System.out.println("Added in sieve 2:" + content.substring(tl.get(0), tl.get(1)));
		}
		
		/*
		 * END OF WRK SECTION
		 */
		
		
		
//		
		// now do the conll style printing
		ArrayList<String> testLines = populateTestLines(sentenceSpans, content, entityMap);
		
		//System.out.println("Done.");
//		ArrayList<String> goldLines = new ArrayList<String>();
		
//		ArrayList<String> goldLines;
//		try {
//			goldLines = (ArrayList)IOUtils.readLines(new FileInputStream("C:\\Users\\pebo01\\Desktop\\data\\CRETA2017\\Bundestagdebatten\\conll\\gold.conll"));
//			
//			
//			evalNER(testLines, goldLines, false, "LOC");
//			
//			
//			
//			
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		

		
	}
	
	
	public static void parzivalExperiment(HashMap<ArrayList, HashMap<String, Double>> entityMap, String content, Span[] sentenceSpans){
		
		// only types in training data here are LOC and PER
		
		/*
		 * PER SECTION
		 */
		
//		ArrayList<String> typeTokens = learnFromCONLLData("C:\\Users\\pebo01\\Desktop\\ubuntuShare\\parzival\\all.conll", "PER", new ArrayList<String>());
//		// cleaning list of typeTokens because of too many false poasitives.
//		ArrayList<String> excludeTokens = new ArrayList<String>();
//		excludeTokens.add("des");
//		excludeTokens.add("der");
//		excludeTokens.add("dem");
//		excludeTokens.add("den");
//		excludeTokens.add("man");
//		excludeTokens.add("mal");
//		excludeTokens.add("mîn");
//		excludeTokens.add("diu");
//		excludeTokens.add("sîn");
//		excludeTokens.add("sîne");
//		excludeTokens.add("sîner");
//		excludeTokens.add("de");
//		excludeTokens.add("la");
//		excludeTokens.add("rôt");
//		excludeTokens.add("ein");
//		excludeTokens.add("âne");
//		
//		
//		
//		typeTokens = cleanList(typeTokens, excludeTokens);
//		
//		entityMap = findTypedTokensWithExcludeList(typeTokens, sentenceSpans, content, entityMap, "PER", excludeTokens);
//		System.out.println("INFO: Size after non-cheat sieve: " + entityMap.keySet().size());
//		
////		entityMap = findNamesInNPs(typeTokens, sentenceSpans, content, entityMap, "PER");
////		System.out.println("INFO: Size after NP-based spotting: " + entityMap.keySet().size());
//		
//		//NOTE: maybe for this I want to cleanList on typeTokens before!
////		ArrayList<HashMap> chunkProbs = getChunkProbs("C:\\Users\\pebo01\\Desktop\\ubuntuShare\\parzival\\all.txt");
////		ArrayList<Span> chunks = chunkInputText(sentenceSpans, content, chunkProbs);
////		ArrayList<String> uppercaseTokens = getUppercaseTokens(sentenceSpans, content);
////		entityMap = findNamesInChunks(uppercaseTokens, chunks, content, entityMap, "PER");
//		
//		
//		// simple uppercase spotting (since in mittelhochdeutsch, it seems that nouns are not capitalized by default)
//		entityMap = findUppercaseTokens(sentenceSpans, content, entityMap, "PER");
//		System.out.println("INFO: size after uppercase spotting: " + entityMap.keySet().size());
		
		
		/*
		 * END OF PER SECTION
		 */
		
		/*
		 * LOC SECTION
		 */
		
		ArrayList<String> typeTokens = learnFromCONLLData("C:\\Users\\pebo01\\Desktop\\ubuntuShare\\parzival\\all.conll", "LOC", new ArrayList<String>());
		ArrayList<String> excludeTokens = new ArrayList<String>();
		excludeTokens.add("des");
		excludeTokens.add("der");
		excludeTokens.add("dem");
		excludeTokens.add("den");
		excludeTokens.add("man");
		excludeTokens.add("mîn");
		excludeTokens.add("diu");
		excludeTokens.add("sîn");
		excludeTokens.add("sîner");
		excludeTokens.add("de");
		excludeTokens.add("la");
		excludeTokens.add("rôt");
		excludeTokens.add("ein");
		typeTokens = cleanList(typeTokens, excludeTokens);
		entityMap = findTypedTokensWithExcludeList(typeTokens, sentenceSpans, content, entityMap, "LOC", excludeTokens);
		System.out.println("INFO: Size after non-cheat sieve: " + entityMap.keySet().size());
		
//		entityMap = findUppercaseTokens(sentenceSpans, content, entityMap, "LOC");
//		System.out.println("INFO: size after uppercase spotting: " + entityMap.keySet().size());
		
		
		/*
		 * END OF LOC SECTION
		 */
		
		
		
		// now do the conll style printing
		ArrayList<String> testLines = populateTestLines(sentenceSpans, content, entityMap);
		
		//System.out.println("Done.");
//		ArrayList<String> goldLines = new ArrayList<String>();
		
//		ArrayList<String> goldLines;
//		try {
//			goldLines = (ArrayList)IOUtils.readLines(new FileInputStream("C:\\Users\\pebo01\\Desktop\\ubuntuShare\\parzival\\all.conll"));
//			
//			
//			evalNER(testLines, goldLines, false, "LOC");
//			
//			
//			
//			
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

		
	}
	
	
	
	
	
	public static void adornoExperiment(HashMap<ArrayList, HashMap<String, Double>> entityMap, String content, Span[] sentenceSpans){
		
		
		/*
		 * PER SECTION
		 */
		
//		//First Sieve: find names based on nameList
//		ArrayList<String> personFullNames = getFullNameList("C:\\Users\\pebo01\\Desktop\\data\\CRETA2017\\adorno\\listOfFullNames.txt");
//		entityMap = findFullNamesWithList(entityMap, personFullNames, content, sentenceSpans, "PER");
//		System.out.println("INFO: Size after strict name spotting: " + entityMap.keySet().size());
//		
//		
//		//Second Sieve: split fullname list into firstname lastname, extract NPs, any NP with a name in it, tag that whole NP. (probably will only find stuff based on lastname, but there may be chinese/other names in there for which it is not immediately obvious which is the first and last name (at least to me), middle names, etc.
//		ArrayList<String> personNames = splitFullNames(personFullNames);
//		// doing some cleaning up of personNames to exclude stopwords, middle name abbrevs (A., which, due to the dot, are especially nasty for regex matching), etc.
//		personNames = cleanList(personNames);
//		ArrayList<String> excludeTokens = new ArrayList<String>();
//		excludeTokens.add("ernst");
//		excludeTokens.add("de"); // may not be in german stopwords, but is in list of names plenty of times
//		excludeTokens.add("la");
//		personNames = cleanList(personNames, excludeTokens);
//		
//		//entityMap = findNamesInNPs(personNames, sentenceSpans, content, entityMap, "PER");
//		entityMap = findFullNamesWithList(entityMap, personNames, content, sentenceSpans, "PER");
//		System.out.println("INFO: Size after second try with splitted name spotting: " + entityMap.keySet().size());
//		
//		
//		entityMap = findFullNamesWithListGenitiveCases(entityMap, personNames, content, sentenceSpans, "PER");
//		System.out.println("INFO: Size after genitive cases: " + entityMap.keySet().size());
//		
//		// adding a few more people
//		personFullNames = appendToNameList(personFullNames, "C:\\Users\\pebo01\\Desktop\\ubuntuShare\\adorno\\famousAuthors.txt");
//		personFullNames = appendToNameList(personFullNames, "C:\\Users\\pebo01\\Desktop\\ubuntuShare\\adorno\\famousComposers.txt");
//		personFullNames = appendToNameList(personFullNames, "C:\\Users\\pebo01\\Desktop\\ubuntuShare\\adorno\\famousPhilosophers.txt");
//		personFullNames = appendToNameList(personFullNames, "C:\\Users\\pebo01\\Desktop\\ubuntuShare\\adorno\\famousPainters.txt");
//		personNames = splitFullNames(personFullNames);
//		personNames = cleanList(personNames);
//		personNames = cleanList(personNames, excludeTokens);
//		
//		entityMap = findFullNamesWithList(entityMap, personNames, content, sentenceSpans, "PER");
//		System.out.println("INFO: Size after adding some more names: " + entityMap.keySet().size());
//		
//		entityMap = findFullNamesWithListGenitiveCases(entityMap, personNames, content, sentenceSpans, "PER");
//		System.out.println("INFO: Size after genitive cases with extra names: " + entityMap.keySet().size());
//		
//		
//		// sort of Fifth Sieve: using the annotated data to extract word frequencies and consider any word of which the likelihood of it being a PER is x times higher than it being O (or any other type?) as entity.
//		// This means (unless I split the data) that I'm testing on seen data. But doing it here just as an indication of how much it would improve upon the current stand)
//		ArrayList<String> typeTokens = learnFromCONLLData("C:\\Users\\pebo01\\Desktop\\ubuntuShare\\adorno\\gold.conll", "PER", new ArrayList<String>());
//		entityMap = findTypedTokens(typeTokens, sentenceSpans, content, entityMap, "PER");
//		System.out.println("INFO: Size after cheat sieve: " + entityMap.keySet().size());
	
		
		/*
		 * END OF PER SECTION
		 */
		
		
		
		/*
		 * WRK SECTION
		 */
		ArrayList<String> werkIndicatorSubstrings = new ArrayList<String>();
		werkIndicatorSubstrings.add("gedicht");
		werkIndicatorSubstrings.add("appasionata");
		//werkIndicatorSubstrings.add("kritik");
		werkIndicatorSubstrings.add("oper");
		werkIndicatorSubstrings.add("erzählung");
		werkIndicatorSubstrings.add("sonate");
		werkIndicatorSubstrings.add("quartett");
		werkIndicatorSubstrings.add("symphonie");
		//werkIndicatorSubstrings.add("werk");
		werkIndicatorSubstrings.add("orchesterwerk");
		entityMap = findSubstringsInNPs(werkIndicatorSubstrings, sentenceSpans, content, entityMap, "WRK");
		System.out.println("INFO: Size after NP-based substring spotting: " + entityMap.keySet().size());
		
		//cheat sieve:
		// sort of Fifth Sieve: using the annotated data to extract word frequencies and consider any word of which the likelihood of it being a PER is x times higher than it being O (or any other type?) as entity.
		// This means (unless I split the data) that I'm testing on seen data. But doing it here just as an indication of how much it would improve upon the current stand)
		ArrayList<String> typeTokens = learnFromCONLLData("C:\\Users\\pebo01\\Desktop\\ubuntuShare\\adorno\\gold.conll", "WRK", new ArrayList<String>());
		typeTokens = cleanList(typeTokens);
		ArrayList<String> excludeList = new ArrayList<String>();
		excludeList.add("den");
		excludeList.add("der");
		excludeList.add("von");
		excludeList.add("die");
		excludeList.add("ein");
		excludeList.add("einer");
		excludeList.add("einem");
		excludeList.add("einen");
		entityMap = findTypedTokensWithExcludeList(typeTokens, sentenceSpans, content, entityMap, "WRK", excludeList);
		System.out.println("INFO: Size after cheat sieve: " + entityMap.keySet().size());
		
		
		/*
		 * END OF WRK SECTION
		 */
		
		
		
		
		// now do the conll style printing
		ArrayList<String> testLines = populateTestLines(sentenceSpans, content, entityMap);

		// System.out.println("Done.");
		// ArrayList<String> goldLines = new ArrayList<String>();

//		ArrayList<String> goldLines;
//		try {
//			goldLines = (ArrayList) IOUtils.readLines(new FileInputStream("C:\\Users\\pebo01\\Desktop\\ubuntuShare\\adorno\\gold.conll"));
//
//			evalNER(testLines, goldLines, false, "WRK");
//
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		
		
	}
	
	
	public static void wertherExperiment(HashMap<ArrayList, HashMap<String, Double>> entityMap, String content, Span[] sentenceSpans){
		
		/*
		 * PER SECTION
		 */
		
//		//First Sieve: find names based on nameList
//		ArrayList<String> personFullNames = getFullNameList("C:\\Users\\pebo01\\Desktop\\ubuntuShare\\goethe\\sortOfNames.txt");
//		entityMap = findFullNamesWithList(entityMap, personFullNames, content, sentenceSpans, "PER");
//		for (ArrayList<Integer> tl : entityMap.keySet()){
//			System.out.println("Added in sieve 1:" + content.substring(tl.get(0), tl.get(1)));
//		}
//		System.out.println("INFO: Size after strict name spotting: " + entityMap.keySet().size());
//		
//		// Fourth Sieve: using prefixes and tag the whole NP as PER 
//		entityMap = findNamesInNPs(personFullNames, sentenceSpans, content, entityMap, "PER");
//		for (ArrayList<Integer> tl : entityMap.keySet()){
//			System.out.println("Added in sieve 2:" + content.substring(tl.get(0), tl.get(1)));
//		}
//		System.out.println("INFO: Size after NP-based prefix spotting: " + entityMap.keySet().size());
//		
//		
//		//cheat sieve:
//		// sort of Fifth Sieve: using the annotated data to extract word frequencies and consider any word of which the likelihood of it being a PER is x times higher than it being O (or any other type?) as entity.
//		// This means (unless I split the data) that I'm testing on seen data. But doing it here just as an indication of how much it would improve upon the current stand)
//		ArrayList<String> typeTokens = learnFromCONLLData("C:\\Users\\pebo01\\Desktop\\ubuntuShare\\goethe\\gold.conll", "PER", new ArrayList<String>());
//		typeTokens = cleanList(typeTokens);
//		ArrayList<String> excludeList = new ArrayList<String>();
//		excludeList.add("die");
//		excludeList.add("der");
//		excludeList.add("das");
//		excludeList.add("den");
//		excludeList.add("dem");
//		excludeList.add("ein");
//		excludeList.add("eine");
//		excludeList.add("einer");
//		excludeList.add("einen");
//		excludeList.add("einem");
//		excludeList.add("seine");
//		excludeList.add("seiner");
//		excludeList.add("seinen");
//		excludeList.add("seinem");
//		excludeList.add("meiner");
//		excludeList.add("meinen");
//		excludeList.add("meinem");
//		excludeList.add("meine");
//		excludeList.add("von");
//		excludeList.add("aus");
//		excludeList.add("ihr");
//		excludeList.add("ihre");
//		
//		entityMap = findTypedTokensWithExcludeList(typeTokens, sentenceSpans, content, entityMap, "PER", excludeList);
//		for (ArrayList<Integer> tl : entityMap.keySet()){
//			System.out.println("Added in sieve 3:" + content.substring(tl.get(0), tl.get(1)));
//		}
//		//entityMap = findTypedTokens(typeTokens, sentenceSpans, content, entityMap, "PER");
//		System.out.println("INFO: Size after cheat sieve: " + entityMap.keySet().size());
	
		
		/*
		 * END OF PER SECTION
		 */

		/*
		 * LOC SECTION
		 */
		
		//cheat sieve:
		// sort of Fifth Sieve: using the annotated data to extract word frequencies and consider any word of which the likelihood of it being a PER is x times higher than it being O (or any other type?) as entity.
		// This means (unless I split the data) that I'm testing on seen data. But doing it here just as an indication of how much it would improve upon the current stand)
//		ArrayList<String> typeTokens = learnFromCONLLData("C:\\Users\\pebo01\\Desktop\\ubuntuShare\\goethe\\gold.conll", "LOC", new ArrayList<String>());
//		typeTokens = cleanList(typeTokens);
//		ArrayList<String> excludeList = new ArrayList<String>();
//		excludeList.add("die");
//		excludeList.add("dieser");
//		excludeList.add("der");
//		excludeList.add("das");
//		excludeList.add("den");
//		excludeList.add("dem");
//		excludeList.add("ein");
//		excludeList.add("eine");
//		excludeList.add("einer");
//		excludeList.add("einen");
//		excludeList.add("einem");
//		excludeList.add("meines");
//		excludeList.add("von");
//		excludeList.add("vom");
//		excludeList.add("aus");
//		excludeList.add("ihr");
//		excludeList.add("ihre");
//		excludeList.add("liebe");
//		entityMap = findTypedTokensWithExcludeList(typeTokens, sentenceSpans, content, entityMap, "LOC", excludeList);
//		for (ArrayList<Integer> tl : entityMap.keySet()){
//			System.out.println("Added in sieve 1:" + content.substring(tl.get(0), tl.get(1)));
//		}
//		System.out.println("INFO: Size after cheat sieve: " + entityMap.keySet().size());
		
		/*
		 * END OF LOC SECTION
		 */
		
		
		
		
		/*
		 * WRK SECTION
		 */
		
		//cheat sieve:
		// sort of Fifth Sieve: using the annotated data to extract word frequencies and consider any word of which the likelihood of it being a PER is x times higher than it being O (or any other type?) as entity.
		// This means (unless I split the data) that I'm testing on seen data. But doing it here just as an indication of how much it would improve upon the current stand)
		ArrayList<String> typeTokens = learnFromCONLLData("C:\\Users\\pebo01\\Desktop\\ubuntuShare\\goethe\\gold.conll", "WRK", new ArrayList<String>());
		typeTokens = cleanList(typeTokens);
		ArrayList<String> excludeList = new ArrayList<String>();
		excludeList.add("die");
		excludeList.add("dieser");
		excludeList.add("der");
		excludeList.add("das");
		excludeList.add("den");
		excludeList.add("dem");
		excludeList.add("ein");
		excludeList.add("eine");
		excludeList.add("einer");
		excludeList.add("einen");
		excludeList.add("einem");
		excludeList.add("von");
		excludeList.add("vom");
		excludeList.add("aus");
		excludeList.add("ihr");
		excludeList.add("ihre");
		excludeList.add("ihren");
		excludeList.add("meine");
		excludeList.add("meiner");
		excludeList.add("meinen");
		excludeList.add("meinem");
		excludeList.add("seine");
		excludeList.add("seiner");
		excludeList.add("seinen");
		excludeList.add("seinem");
		entityMap = findTypedTokensWithExcludeList(typeTokens, sentenceSpans, content, entityMap, "WRK", excludeList);
		System.out.println("INFO: Size after cheat sieve: " + entityMap.keySet().size());
 
		/*
		 * END OF WRK SECTION
		 */
		// now do the conll style printing
		ArrayList<String> testLines = populateTestLines(sentenceSpans, content, entityMap);

		// System.out.println("Done.");
		// ArrayList<String> goldLines = new ArrayList<String>();

//		ArrayList<String> goldLines;
//		try {
//			goldLines = (ArrayList<String>) IOUtils.readLines(new FileInputStream("C:\\Users\\pebo01\\Desktop\\ubuntuShare\\goethe\\gold.conll"));
//
//			evalNER(testLines, goldLines, false, "WRK");
//
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		
		
	}
	
	
	public static ArrayList<String> combineAnnotatedFiles(String fp1, String fp2){
		
		ArrayList<String> newlines=  new ArrayList<String>();
		
		try {
			String content1 = readFile(fp1, StandardCharsets.UTF_8);
			ArrayList<String> content1Lines = new ArrayList<String>();
			for (String line : content1.split("\n")){
				content1Lines.add(line.trim());
			}
			String content2 = readFile(fp2, StandardCharsets.UTF_8);
			ArrayList<String> content2Lines = new ArrayList<String>();
			for (String line : content2.split("\n")){
				content2Lines.add(line.trim());
			}
			
			if (content1Lines.size() != content2Lines.size()){
				System.out.println("ERROR: files do not have the same length. Dying now.");
				System.exit(1);
			}
			
			for (int i = 0; i < content1Lines.size(); i++){
				String[] leftParts = content1Lines.get(i).split("\t");
				String[] rightParts = content2Lines.get(i).split("\t");
				String leftType = leftParts[leftParts.length-1];
				String rightType = rightParts[rightParts.length-1];
				if (leftType.equalsIgnoreCase("o") && rightType.equalsIgnoreCase("o")){
					// both are not annotated. Do nothing.
					newlines.add(content1Lines.get(i));
				}
				else if (leftType.equalsIgnoreCase("o") && !(rightType.equalsIgnoreCase("o"))){
					// left is not annotated but right is. Take the type from right.
					newlines.add(content2Lines.get(i));
				}
				else if (!(leftType.equalsIgnoreCase("o")) && rightType.equalsIgnoreCase("o")){
					// left is annotated but right is not. Take the type from left.
					newlines.add(content1Lines.get(i));
				}
				else if (leftType.startsWith("B") && !(rightType.startsWith("B"))){
					// now we have a conflict. The entity type that had already started gets precedence.
					String newline = content2Lines.get(i) + "\t" + leftType;
					newlines.add(newline);
				}
				else if (!(leftType.startsWith("B")) && rightType.startsWith("B")){
					// now we have a conflict. The entity type that had already started gets precedence.
					String newline = content1Lines.get(i) + "\t" + rightType;
					newlines.add(newline);
				}
				else if((leftType.startsWith("B") && rightType.startsWith("B")) || (leftType.startsWith("I") && rightType.startsWith("I"))){
					// both are starting at the same point (or; not here, and both are already going on). In this case, just taking the left one first. Maybe this should be a bit more sophisticated. (Now it depends on which is fp1 and which is fp1, so it is left to the user, when providing the args)
					String newline = content1Lines.get(i) + "\t" + rightType;
					newlines.add(newline);
				}
				else{
					newlines.add("");
				}
				
			}
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return newlines;
	}

	
	public static void main(String[] args) {

					
		try {
			PrintWriter out = new PrintWriter(new File("C:\\Users\\pebo01\\Desktop\\debug.txt"));
			//String content = readFile("C:\\Users\\pebo01\\Desktop\\ubuntuShare\\goethe\\test.txt", StandardCharsets.UTF_8);
			//String content = readFile("C:\\Users\\pebo01\\Desktop\\data\\CRETA2017\\Bundestagdebatten\\test.plaintext", StandardCharsets.UTF_8);
			//String content = readFile("C:\\Users\\pebo01\\Desktop\\ubuntuShare\\parzival\\all.txt", StandardCharsets.UTF_8);
			//String content = readFile("C:\\Users\\pebo01\\Desktop\\ubuntuShare\\adorno\\test.txt", StandardCharsets.UTF_8);
			String content = readFile("C:\\Users\\pebo01\\Desktop\\data\\CRETA2017\\evaluationData\\goethe\\3_34_12_eval.conll", StandardCharsets.UTF_8);
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

			//bundestagDebattenExperiment(entityMap, content, sentenceSpans);
			
			//parzivalExperiment(entityMap, content, sentenceSpans);
			
			//adornoExperiment(entityMap, content, sentenceSpans);
			
			//wertherExperiment(entityMap, content, sentenceSpans);
			
			
			
			
			/*
			 * This section is to combine two annotated files. Whenever a token has more than one annotation type, add extra column and define which one has precedence.
			 * 
			 */
			
			ArrayList<String> combinedLines = combineAnnotatedFiles("C:\\Users\\pebo01\\Desktop\\workingFile.txt", "C:\\Users\\pebo01\\Desktop\\data\\CRETA2017\\evaluationData\\goethe\\annotated\\3_34_12_eval_WRK.txt");
			for (String l : combinedLines){
				out.write(l + "\n");
			}
			out.close(); // TODO: line numbers of 
			// 3_27_26 from bundestag
			// 3_34_12_eval from goethe
			// had to be fixed manually.
			 
			System.out.println("done.");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		
	}
	

}
