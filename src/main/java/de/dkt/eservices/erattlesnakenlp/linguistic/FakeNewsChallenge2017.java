package de.dkt.eservices.erattlesnakenlp.linguistic;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

import org.omg.Messaging.SyncScopeHelper;

import cc.mallet.classify.Classifier;
import de.dkt.common.filemanagement.FileFactory;
import de.dkt.eservices.ecorenlp.modules.Lemmatizer;
import de.dkt.eservices.ecorenlp.modules.Tagger;
import de.dkt.eservices.emallet.modules.DocumentClassification;
import de.dkt.eservices.eopennlp.modules.Tokenizer;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.ling.WordLemmaTag;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.TypedDependency;
import opennlp.tools.tokenize.SimpleTokenizer;

public class FakeNewsChallenge2017 {
	
	final static int MIN_NGRAM_LENGTH = 1;
	final static int MAX_NGRAM_LENGTH = 6;
	
	static HashMap<String, Double> ngramIDF = new HashMap<String, Double>();
	static HashMap<String, HashMap<String, Integer>> ngramMemoryMap = new HashMap<String, HashMap<String, Integer>>();
	static List<String> enStopwords = new ArrayList<String>();;
	
	private ArrayList<FakeNewsChallengeObject> parseTsv(String[] flines, boolean lemmatise){
		Lemmatizer.initLemmatizer();
		ArrayList<FakeNewsChallengeObject> l = new ArrayList<FakeNewsChallengeObject>();
		HashMap<String, String> memoryMap = new HashMap<String, String>();
		int hi = 0;
		int ai = 0;
		for (String s : flines){
			String[] parts = s.split("\t");
			int id = Integer.parseInt(parts[0]);
			String stance = parts[1].replaceAll("\"", ""); // TODO: may want to consider doing the same (stripping trailing double quotes) for articles and headlines
			int articleId = Integer.parseInt(parts[2]);
			String article = parts[3];
			String header = parts[4];
			FakeNewsChallengeObject fnco = null;
			if (lemmatise){
				String headerLemmas = "";
				if (memoryMap.containsKey(header)){
					headerLemmas = memoryMap.get(header);
				}
				else{
					headerLemmas = getLemmas(header);
					memoryMap.put(header,  headerLemmas);
				}
				//String articleLemmas = getLemmas(article);
				// because lemmatising takes a bit long, using a memory hashmap to only lemmatise an article once
				String articleLemmas = "";
				if (memoryMap.containsKey(article)){
					articleLemmas = memoryMap.get(article);
				}
				else{
					articleLemmas = getLemmas(article);
					memoryMap.put(article,  articleLemmas);
				}
				// lemmatising fails sometimes, due to untokenizable chars
				if (headerLemmas.equalsIgnoreCase("")){
					headerLemmas = header;
					hi++;
				}
				if (articleLemmas.equalsIgnoreCase("")){
					articleLemmas = article;
					ai++;
				}
				fnco = new FakeNewsChallengeObject(id, articleId, stance, article, header, headerLemmas, articleLemmas);
				
			}
			else{
				fnco = new FakeNewsChallengeObject(id, articleId, stance, article, header);
			}
			l.add(fnco);
			
		}
//		System.out.println(String.format("INFO: %s out of %s headers using raw instead of lemmas.", Integer.toString(hi), Integer.toString(flines.length)));
//		System.out.println(String.format("INFO: %s out of %s articles using raw instead of lemmas.", Integer.toString(ai), Integer.toString(flines.length)));
		return l;
	}
	
	private String getLemmas(String text){
		List<String> lemmas = Lemmatizer.lemmatize(text, "elvish"); // language param is not used...
		String lemmaStrings = String.join(" ", lemmas);
		return lemmaStrings;
	}
	
	private HashMap<String, Integer> getNgrams(String text){
		HashMap<String, Integer> hm = new HashMap<String, Integer>();
		String[] tokens = text.toLowerCase().split("\\s");
		for (int i = MIN_NGRAM_LENGTH; i <= MAX_NGRAM_LENGTH; i++) {
			for (int j = 0; j < tokens.length + 1 - i; j++) {
				String ngram = String.join(" ", Arrays.copyOfRange(tokens, j, j + i));
				int v = (hm.containsKey(ngram) ? hm.get(ngram) + 1 : 1);
				hm.put(ngram, v);
			}
		}
		return hm;
	}
	
	 
	
	public String[] readLines(String filename) throws IOException {
		FileReader fileReader = new FileReader(filename);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		List<String> lines = new ArrayList<String>();
		String line = null;
		while ((line = bufferedReader.readLine()) != null) {
			lines.add(line);
		}
		bufferedReader.close();
		return lines.toArray(new String[lines.size()]);
	}
	
	private ArrayList<FakeNewsChallengeObject> getRandomPercentage(double fraction, ArrayList<FakeNewsChallengeObject> l){
		Collections.shuffle(l);
		ArrayList<FakeNewsChallengeObject> rl = new ArrayList<FakeNewsChallengeObject>();
		double d = l.size() * fraction;
		for (int i = 0; i < d; i++){
			rl.add(l.get(i));
		}
		return rl;
	}
	
	private HashMap<FakeNewsChallengeObject, String> classifyInstances(ArrayList<FakeNewsChallengeObject> l, double threshold, Classifier fallbackHeadlineClassifier, int numClasses , Classifier binaryAgreeOrDisagreeClassifier, Classifier binaryAgreeOrDiscussClassifier, Classifier binaryDiscussOrDisagreeClassifier){
		HashMap<FakeNewsChallengeObject, String> hm = new HashMap<FakeNewsChallengeObject, String>();
		
		// prerequisites...
		deserializeStopwords("C:\\Users\\pebo01\\workspace\\e-NLP\\src\\main\\resources\\stopwords\\english.ser");
		populateHashMaps(l);
		
		
		//actual classification...
		for (FakeNewsChallengeObject fnco : l) {
			String val = binaryClassifyInstanceForRelatedness(fnco, threshold);
			if (val.equalsIgnoreCase("unrelated")){
				hm.put(fnco,  val);
			}
			else{
				String cl = "";

				// figures on question mark... not very helpful, given that discuss is the fallback anyway
//				Discuss headlines ending with question mark: 555 of 8909 (0.06229655404646986).
//				Disagree headlines ending with question mark: 21 of 840 (0.025).
//				Agree headlines ending with question mark: 39 of 3678 (0.010603588907014683).
				
				// first separating discuss vs. other
//				String binaryDiscussOther = DocumentClassification.classifyString(fnco.getHeader(), binaryDiscussOtherClassifier);
//				if (binaryDiscussOther.equalsIgnoreCase("discuss")){
//					hm.put(fnco, "discuss");
//				}
//				else{
//					// do binary classification between agree and disagree
//					String binaryAgreeOrDisagree = DocumentClassification.classifyString(fnco.getHeader(), binaryAgreeOrDisagreeClassifier);
//					hm.put(fnco, binaryAgreeOrDisagree);
//				}
				
				// negation/disagree classifier (parse headline and get distance from negation to root (stolen from Frreira & Vlachos, 2016))
//				double articleDenialScore = negationScore(fnco, "article");
//				double headerDenialScore = negationScore(fnco, "header");
				
//				if (fnco.getStance().equalsIgnoreCase("agree")){
//					System.out.println("DEBUGGING_agree\t" + headerDenialScore + "\t" + articleDenialScore + "\t" + (articleDenialScore - headerDenialScore));
//				}
//				else if (fnco.getStance().equalsIgnoreCase("disagree")){
//					System.out.println("DEBUGGING_disagree\t" + headerDenialScore + "\t" + articleDenialScore + "\t" + (articleDenialScore - headerDenialScore));
//				}
//				else if (fnco.getStance().equalsIgnoreCase("discuss")){
//					System.out.println("DEBUGGING_discuss\t" + headerDenialScore + "\t" + articleDenialScore + "\t" + (articleDenialScore - headerDenialScore));
//				}
//				if (articleDenialScore - headerDenialScore < 0.1){
//					cl = "agree"; // this decreases score dramatically...
//				}
//				else{
				HashMap<String, Double> classScores = DocumentClassification.classifyStringToScores(fnco.getHeader(), fallbackHeadlineClassifier, numClasses); // TODO: fix here! (see below)
				
				
				
//				String debugLine = "";
//				for (String c : classScores.keySet()){
//					debugLine += String.format("%s(%s)\t", c, classScores.get(c));
//				}
				
				String[] sorted = new String[classScores.size()];
				int p = 0;
				for (String scl : sortByValueReverse(classScores).keySet()){
					sorted[p] = scl;
					p++;
				}
//				for (int i = 0; i < sorted.length; i++){
//					System.out.println("DEBUGGING SORTING:" + sorted[i] + "|" + classScores.get(sorted[i]));
//				}
				double highestScore = getHighestScore(classScores);
				double diffBetween1and2 = getDiffInScores(classScores, highestScore);
				if (diffBetween1and2 > 0.7){ // re-visit optimal threshold value when other classification method is implemented
					cl = sorted[0];
				}
				else{
					String first = sorted[0];
					String second = sorted[1];
					// making a list to decrease number of if checks...
					List<String> _12 = new ArrayList<>(Arrays.asList(first.toLowerCase(), second.toLowerCase()));
					if (_12.contains("agree") && _12.contains("disagree")){
						cl = DocumentClassification.classifyString(fnco.getHeader(), binaryAgreeOrDisagreeClassifier); //TODO: fix here tha\t I'm not only feeding the header, but also the article!!!!!
					}
					else if (_12.contains("agree") && _12.contains("discuss")){
						cl = DocumentClassification.classifyString(fnco.getHeader(), binaryAgreeOrDiscussClassifier);
					}
					else if (_12.contains("discuss") && _12.contains("disagree")){
						cl = DocumentClassification.classifyString(fnco.getHeader(), binaryDiscussOrDisagreeClassifier);
					}
				}
				
//				}
				
				///////cl = DocumentClassification.classifyString(fnco.getHeader(), fallbackHeadlineClassifier); // this was the original (and up to now still best scoring) line/procedure!!!!
//				debugLine += "actual class:" + fnco.getStance() + "(" + cl + ")";
//				System.out.println("GREP:" + debugLine);
				
				hm.put(fnco,  cl);
				
				
				
				//hm.put(fnco, "discuss"); // baseline (popular vote...)
				
				
				
			}
		}
		
		return hm;
	}
	
	private double getDiffInScores(HashMap<String, Double> hm, double hs){
		double smallestDiff = 1;
		for (String cl : hm.keySet()){
			if (Math.abs(hs - hm.get(cl)) < smallestDiff){
				smallestDiff = hm.get(cl);
			}
		}
		return smallestDiff;
	}
	
	private double getHighestScore(HashMap<String, Double> hm){
		double highestScore = 0;
		for (String cl : hm.keySet()){
			if (hm.get(cl) > highestScore){
				highestScore = hm.get(cl);
			}
		}
		return highestScore;
	}
	
	private void hashGrammaticalStructures(ArrayList<FakeNewsChallengeObject> l){
		HashMap<String, ArrayList<GrammaticalStructure>> memoryMap = new HashMap<String, ArrayList<GrammaticalStructure>>(); // TODO: maybe make this global, to not have to do it again for every iteration (in case of x-fold validation)
		for (FakeNewsChallengeObject fnco : l){
			if (memoryMap.containsKey(fnco.getHeader())){
				fnco.setHeaderGS(memoryMap.get(fnco.getHeader()));
			}
			else{
				ArrayList<GrammaticalStructure> headerGSList = new ArrayList<GrammaticalStructure>();
				DocumentPreprocessor tokenizer = new DocumentPreprocessor(new StringReader(fnco.getHeader()));
				for (List<HasWord> sentence : tokenizer) {
					List<TaggedWord> tagged = Tagger.tagger.tagSentence(sentence);
					GrammaticalStructure gs = DepParserTree.parser.predict(tagged);
					headerGSList.add(gs);
				}
				fnco.setHeaderGS(headerGSList);
				memoryMap.put(fnco.getHeader(), headerGSList);
			}
			if (memoryMap.containsKey(fnco.getArticle())){
				fnco.setArticleGS(memoryMap.get(fnco.getArticle()));
			}
			else{
				ArrayList<GrammaticalStructure> articleGSList = new ArrayList<GrammaticalStructure>();
				DocumentPreprocessor tokenizer2 = new DocumentPreprocessor(new StringReader(fnco.getArticle()));
				for (List<HasWord> sentence : tokenizer2) {
					List<TaggedWord> tagged = Tagger.tagger.tagSentence(sentence);
					GrammaticalStructure gs = DepParserTree.parser.predict(tagged);
					articleGSList.add(gs);
				}
				fnco.setArticleGS(articleGSList);
				memoryMap.put(fnco.getArticle(), articleGSList);
			}
		}
		
	}
	
	
	private Double negationScore(FakeNewsChallengeObject fnco, String t){
		
		ArrayList<Double> rbDistances = new ArrayList<Double>();
		int sentcount = 0;
		
		ArrayList<GrammaticalStructure> gsList = new ArrayList<GrammaticalStructure>();
		if (t.equalsIgnoreCase("header")){
			gsList = fnco.getHeaderGS();
		}
		else if (t.equalsIgnoreCase("article")){
			gsList = fnco.getArticleGS();
		}
		for (GrammaticalStructure gs : gsList) {
			sentcount++;
			double maxdist = 0;
			for (TypedDependency td : gs.typedDependencies()){
				double rootdist = distanceToRoot(td, gs);
				if (rootdist > maxdist){
					maxdist = rootdist;
				}
				if (td.reln().toString().equalsIgnoreCase("neg")){ // TODO: may also want to include negation verbs (that do not have neg type reln, like deny, reject, etc.) 
					rbDistances.add(rootdist);
				}
			}
			for (int i = 0; i < rbDistances.size(); i++){
				rbDistances.set(i, 1 - (rbDistances.get(i) / maxdist));
			}
			// this is now normalized for sentence level
		}
		// now combine to doc level
		double total = 0;
		for (double d : rbDistances){
			total += d;
		}
		double docScore = total / sentcount;
		
		return docScore;
	}
	
	private Double discussScore(FakeNewsChallengeObject fnco, String t){
		
		ArrayList<Double> rbDistances = new ArrayList<Double>();
		int sentcount = 0;
		
		ArrayList<GrammaticalStructure> gsList = new ArrayList<GrammaticalStructure>();
		if (t.equalsIgnoreCase("header")){
			gsList = fnco.getHeaderGS();
		}
		else if (t.equalsIgnoreCase("article")){
			gsList = fnco.getArticleGS();
		}
		for (GrammaticalStructure gs : gsList) {
			sentcount++;
			double maxdist = 0;
			for (TypedDependency td : gs.typedDependencies()){
				double rootdist = distanceToRoot(td, gs);
				if (rootdist > maxdist){
					maxdist = rootdist;
				}
				if (td.dep().tag().equals("RB")){
					rbDistances.add(rootdist);
				}
			}
			for (int i = 0; i < rbDistances.size(); i++){
				rbDistances.set(i, 1 - (rbDistances.get(i) / maxdist));
			}
			// this is now normalized for sentence level
		}
		// now combine to doc level
		double total = 0;
		for (double d : rbDistances){
			total += d;
		}
		double docScore = total / sentcount;
		
		return docScore;
	}
	
	
	private String binaryClassifyInstanceForRelatedness(FakeNewsChallengeObject fnco, double threshold){
		
		HashMap<String, Integer> headerNgrams = ngramMemoryMap.get(fnco.getHeaderLemmas());
		HashMap<String, Integer> articleNgrams = ngramMemoryMap.get(fnco.getArticleLemmas());
		double score = 0.0;
		for (String ngram : headerNgrams.keySet()) {
			boolean b = false;
			for (String n : ngram.split("\\s")) {
				if ((!enStopwords.contains(n)) && !n.matches("\\W+")) {
					b = true; // there has to be at least one word that is not a stopword and that is not only punctuation in the ngram
				}
			}
			if (b) {
				if (articleNgrams.containsKey(ngram)) {
					score += (headerNgrams.get(ngram) + articleNgrams.get(ngram)) * ngram.split("\\s").length * ngramIDF.get(ngram); 
				}
			}
		}
		score = score / (headerNgrams.size() + articleNgrams.size());
		String val = null;
		if (score > threshold) {
			val = "related";
		} else {
			val = "unrelated";
		}
		return val;
	}
	
	private void deserializeStopwords(String filePath){
		try {
			InputStream file = new FileInputStream(filePath);
			InputStream buffer = new BufferedInputStream(file);
			ObjectInput input = new ObjectInputStream(buffer);
			enStopwords = (List<String>)input.readObject();
			input.close();
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void populateHashMaps(ArrayList<FakeNewsChallengeObject> l){
		
		HashMap<String, Double> ngram2docs = new HashMap<String, Double>();
		HashMap<Integer, Integer> bodyId2count = new HashMap<Integer, Integer>();
		System.out.println("INFO: Getting IDF values for ngrams.");
		for (FakeNewsChallengeObject fnco : l){
			int i = (bodyId2count.containsKey(fnco.getArticleId()) ? bodyId2count.get(fnco.getArticleId()) + 1 : 1);
			bodyId2count.put(fnco.getArticleId(), i);
			HashMap<String, Integer> articleNgrams = null;
			if (ngramMemoryMap.containsKey(fnco.getArticleLemmas())){
				articleNgrams = ngramMemoryMap.get(fnco.getArticleLemmas());
			}
			else{
				articleNgrams = getNgrams(fnco.getArticleLemmas());
				ngramMemoryMap.put(fnco.getArticleLemmas(), articleNgrams);
				
			}
			ngramMemoryMap.put(fnco.getHeaderLemmas(), getNgrams(fnco.getHeaderLemmas()));
			for (String ngram : articleNgrams.keySet()){
				double d = (ngram2docs.containsKey(ngram) ? ngram2docs.get(ngram) + 1.0 : 1.0);
				ngram2docs.put(ngram, d);
			}
		}
		for (String ngram : ngram2docs.keySet()){
			ngramIDF.put(ngram, bodyId2count.size() / ngram2docs.get(ngram) + 1); // +1 for smoothing
		}
		System.out.println("INFO: Done getting IDF vals.");
		
	}
	
	private void classifyRelatedness(ArrayList<FakeNewsChallengeObject> l, String data){
		
		deserializeStopwords("C:\\Users\\pebo01\\workspace\\e-NLP\\src\\main\\resources\\stopwords\\english.ser");
		
		//taking a random sample, to get an idea of influence of distribution on optimal threshold value.
		int nrOfRuns = 1;
		for (int j = 0; j < nrOfRuns; j++){
		System.out.println(String.format("INFO: Run %s of %s\n\n\n", Integer.toString(j+1), Integer.toString(nrOfRuns)));
		l = getRandomPercentage(1.0, l);
		
		
		populateHashMaps(l);
		
		//possible TODO: take out check for stopwords in ngrams (as this is now partially dealt with through IDF), and use exponential boost for ngram length (rather than linear)
		// but currently accuracy ist 96.26. Continuing with related classification (agree, disagree or discuss) first, as this is 75% of the final score.
		
		for (double threshold = 0.005; threshold < 0.015; threshold+= 0.0001){
		//if(true){
		
			//double threshold = 0.0096;
			//double threshold = 3.0;
			double c = 0.0;
			for (FakeNewsChallengeObject fnco : l) {
				//HashMap<String, Integer> headerNgrams = fnc.getNgrams(fnci.getHeaderLemmas());
				//HashMap<String, Integer> articleNgrams = fnc.getNgrams(fnci.getArticleLemmas());
				HashMap<String, Integer> headerNgrams = ngramMemoryMap.get(fnco.getHeaderLemmas());
				HashMap<String, Integer> articleNgrams = ngramMemoryMap.get(fnco.getArticleLemmas());
				//HashMap<String, Integer> headerNgrams = fnc.getNgrams(fnci.getHeader());
				//HashMap<String, Integer> articleNgrams = fnc.getNgrams(fnci.getArticle());
				double score = 0.0;
//					System.out.println("STANCE:" + fnci.getStance());
//					System.out.println("HEADER:" + fnci.getHeader());
//					System.out.println("ARTICLE:" + fnci.getArticle());
				for (String ngram : headerNgrams.keySet()) {
					boolean b = false;
					for (String n : ngram.split("\\s")) {
						if ((!enStopwords.contains(n)) && !n.matches("\\W+")) {
							b = true; // there has to be at least one word that is not a stopword and that is not only punctuation in the ngram
						}
					}
					if (b) {
						if (articleNgrams.containsKey(ngram)) {
//								System.out.println("DEBUGGING matching ngrams:" + ngram);
							score += (headerNgrams.get(ngram) + articleNgrams.get(ngram)) * ngram.split("\\s").length * ngramIDF.get(ngram); 
							// last part is to give longer ngram more weight (maybe should be not linear but even exponential)
							// and including IDF for ngrams, to boost more salient ones
						}
					}
				}
				score = score / (headerNgrams.size() + articleNgrams.size());
//					System.out.println("SCORE:" + score);
//					System.out.println("\n\n");
				String val = null;
				if (score > threshold) {
					val = "related";
				} else {
					val = "unrelated";
				}
				if (val.equals("related") && (fnco.getStance().equals("discuss") || fnco.getStance().equals("agree")
						|| fnco.getStance().equals("disagree"))) {
					c++;
//						System.out.println("Correctly classified this one as related:" + fnci.getHeader());
//						System.out.println(fnci.getArticle());
//						System.out.println("\n\n");
				}
				else if (val.equals("unrelated") && fnco.getStance().equals("unrelated")){
					c++;
//						System.out.println("Correctly classified this one as unrelated:" + fnci.getHeader());
//						System.out.println(fnci.getArticle());
//						System.out.println("\n\n");
				}
				else{
//						System.out.println("Incorrectly classified this one (" + fnci.getStance() + " vs." + val + "): " + fnci.getHeader());
//						System.out.println(fnci.getArticle());
//						System.out.println("\n\n");
				}
				
			}
			
			double d = c / (double)l.size();
//				System.out.println("debugging c and total:" + c + "|" + l.size());
			System.out.println("INFO: Score for threshold:" + threshold + "\t" + d);
		}
		
		}	
		
	}
	
	private Double evaluate(HashMap<FakeNewsChallengeObject, String> results, ArrayList<FakeNewsChallengeObject> l){
		
		double relatednessScore = 0.0;
		double fineGrainedScore = 0.0;
		double totalRelated = 0.0;
		ArrayList<String> relatedClasses = new ArrayList<String>();
		relatedClasses.add("discuss");
		relatedClasses.add("disagree");
		relatedClasses.add("agree");
		
		for (FakeNewsChallengeObject fnco : l){
			if (results.get(fnco).equalsIgnoreCase("unrelated") && fnco.getStance().equalsIgnoreCase("unrelated")){
				relatednessScore++;
			}
			else if (relatedClasses.contains(fnco.getStance())){
				totalRelated++;
				if (relatedClasses.contains(results.get(fnco))){
					relatednessScore++;
					if (results.get(fnco).equalsIgnoreCase(fnco.getStance())){
						fineGrainedScore++;
					}
				}
			}
			else{
				// no points scored :(
			}
		}
		double rs = relatednessScore / l.size();
		double fs = fineGrainedScore / totalRelated;
//		System.out.println("debug relatednessScore:" + rs);
//		System.out.println("debug finegrainedScore:" + fs);
		double finalScore = ((fs * 3) + (rs * 1)) / 4;
//		System.out.println("INFO: Weighted score:" + finalScore);
		return finalScore;
		
	}
	
	private int distanceToRoot(TypedDependency td, GrammaticalStructure gs){
		int d = 1;
		if (td.gov().toString().equals("ROOT")){
			return d;
		}
		else{
			IndexedWord gov = td.gov();
			d = checkGovs(gov, gs, d+1);
		}
		return d;
	}
	
	private int checkGovs(IndexedWord gov, GrammaticalStructure gs, int d){
		for (TypedDependency td : gs.typedDependencies()){ // TODO; not sure if this is guaranteed to always find the shortest path, check this!
			if (td.dep().equals(gov)){
				if (td.gov().toString().equals("ROOT")){
					return d;
				}
				else{
					d = checkGovs(td.gov(), gs, d+1);
				}
			}
		}
		return d;
	}
	
	// taken from http://stackoverflow.com/questions/109383/sort-a-mapkey-value-by-values-java
	public <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
		List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
			public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
				return (o1.getValue()).compareTo(o2.getValue());
				//return (o2.getValue()).compareTo(o1.getValue());
			}
		});

		Map<K, V> result = new LinkedHashMap<K, V>();
		for (Map.Entry<K, V> entry : list) {
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}
	public <K, V extends Comparable<? super V>> Map<K, V> sortByValueReverse(Map<K, V> map) {
		List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
			public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
				//return (o1.getValue()).compareTo(o2.getValue());
				return (o2.getValue()).compareTo(o1.getValue());
			}
		});

		Map<K, V> result = new LinkedHashMap<K, V>();
		for (Map.Entry<K, V> entry : list) {
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}
	
	public static HashMap<String, List<FakeNewsChallengeObject>> splitListRandom(ArrayList<FakeNewsChallengeObject> l){
	    HashMap<String, List<FakeNewsChallengeObject>> returnMap = new HashMap<String, List<FakeNewsChallengeObject>>();
		int n = (int)(l.size() * 0.9);
		Collections.shuffle(l);
		List<FakeNewsChallengeObject> training = l.subList(0, n);
		List<FakeNewsChallengeObject> test = l.subList(n, l.size()); // probably -1
		returnMap.put("training", training);
		returnMap.put("test", test);
		
	    return returnMap;
	}
	
	private void printNegationStats(ArrayList<FakeNewsChallengeObject> l, PrintWriter debug){
		
		for (FakeNewsChallengeObject fnco : l){
			double articleDenialScore = negationScore(fnco, "article");
			double headerDenialScore = negationScore(fnco, "header");
		
			if (fnco.getStance().equalsIgnoreCase("agree")){
				debug.println("negation_agree\t" + headerDenialScore + "\t" + articleDenialScore + "\t" + (articleDenialScore - headerDenialScore));
			}
			else if (fnco.getStance().equalsIgnoreCase("disagree")){
				debug.println("negation_disagree\t" + headerDenialScore + "\t" + articleDenialScore + "\t" + (articleDenialScore - headerDenialScore));
			}
			else if (fnco.getStance().equalsIgnoreCase("discuss")){
				debug.println("negation_discuss\t" + headerDenialScore + "\t" + articleDenialScore + "\t" + (articleDenialScore - headerDenialScore));
			}
		}
	}
	
	private void printDiscussStats(ArrayList<FakeNewsChallengeObject> l, PrintWriter debug){
		
		for (FakeNewsChallengeObject fnco : l){
//			double articleDenialScore = discussScore(fnco, "article");
			double headerDenialScore = discussScore(fnco, "header");
		
			if (fnco.getStance().equalsIgnoreCase("agree")){
//				debug.println("discuss_agree\t" + headerDenialScore + "\t" + articleDenialScore + "\t" + (articleDenialScore - headerDenialScore));
				debug.println("discuss_agree\t" + headerDenialScore);
			}
			else if (fnco.getStance().equalsIgnoreCase("disagree")){
//				debug.println("discuss_disagree\t" + headerDenialScore + "\t" + articleDenialScore + "\t" + (articleDenialScore - headerDenialScore));
				debug.println("discuss_disagree\t" + headerDenialScore);
			}
			else if (fnco.getStance().equalsIgnoreCase("discuss")){
//				debug.println("discuss_discuss\t" + headerDenialScore + "\t" + articleDenialScore + "\t" + (articleDenialScore - headerDenialScore));
				debug.println("discuss_discuss\t" + headerDenialScore);
			}
		}
	}
	
	
	private String createTrainingDataDeleteClass(List<FakeNewsChallengeObject> trainInstances, String deleteCandidate){
		
		String p = null;
		if (deleteCandidate.equalsIgnoreCase("agree")){
			p = "C:\\Users\\pebo01\\Desktop\\FakeNewsChallenge2017\\binaryDiscussOrDisagreeTrainData.txt";
		}
		else if (deleteCandidate.equalsIgnoreCase("disagree")){
			p = "C:\\Users\\pebo01\\Desktop\\FakeNewsChallenge2017\\binaryAgreeOrDiscussTrainData.txt";
		}
		else if (deleteCandidate.equalsIgnoreCase("discuss")){
			p = "C:\\Users\\pebo01\\Desktop\\FakeNewsChallenge2017\\binaryAgreeOrDisagreeTrainData.txt";
		}
		
		int lineno = 1;
		try {
			ArrayList<String> targetlines = new ArrayList<String>();
			for (FakeNewsChallengeObject fnco : trainInstances){
				if (!fnco.getStance().equalsIgnoreCase("unrelated") && !fnco.getStance().equalsIgnoreCase(deleteCandidate)){
					targetlines.add(String.format("%s %s %s %s", Integer.toString(lineno), fnco.getStance(), fnco.getHeader(), fnco.getArticle())); // TODO: maybe multiply the header a couple of times to give it more weight?
					lineno++;
				}
			}
			PrintWriter pw = new PrintWriter(new File(p));
			for (String line : targetlines){
				pw.println(line);
			}
			pw.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return p;
	}
	
	public static void main (String[] args){
		
		FakeNewsChallenge2017 fnc = new FakeNewsChallenge2017();
		Tagger.initTagger("en");
		DepParserTree.initParser("en");
		
		int numIterations = 50;
		ArrayList<Double> scores = new ArrayList<Double>();
		
//		//String sentence = "The claim in this article is denied.";
//		//String sentence = "The is not a headline.";
//		String sentence = "Le Pen didn't win the elections.";
//		Tagger.initTagger("en");
//		DepParserTree.initParser("en");
//		fnc.denyScore(sentence);
//		System.exit(1);
		
		String data = "C:\\Users\\pebo01\\Desktop\\ubuntuShare\\fncData.tsv";
		
		System.out.println("INFO: Parsing tsv...");
		ArrayList<FakeNewsChallengeObject> l;
		
		PrintWriter debugOut = null;
		try {
			debugOut = new PrintWriter(new File("C:\\Users\\pebo01\\Desktop\\debug.txt"));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		//String alg = "maxent";
		String[] algs = {"maxent"};//, "mcmaxent", "bayesem", "bayes", "winnow"};
		try {
			//PrintWriter debug = new PrintWriter(new File("C:\\Users\\pebo01\\Desktop\\debug.txt"));
			for (String alg : algs){
				System.out.println("INFO: Currently using algorithm: " + alg);
			l = fnc.parseTsv(fnc.readLines(data), true);
			System.out.println("INFO: Done parsing tsv.");
			//System.out.println("INFO: Hashing GrammaticalStructures.");
			//fnc.hashGrammaticalStructures(l);
			//System.out.println("INFO: Done hashing GrammaticalStructures.");
			
			
//			fnc.printNegationStats(l, debugOut); // NOTE: try threshold 0.1 (if diff < 0.1, class is agree)
//			//fnc.printDiscussStats(l, debugOut);
//			System.exit(1);
			
			
			for (int k = 1; k < numIterations+1; k++){
				System.out.println("INFO: Running iteration: " + Integer.toString(k));

			//+++++++++++++++++ training a headline classifier ++++++++++++++++++++++++++++++++++++++++++++++++++++++
			HashMap<String, List<FakeNewsChallengeObject>> trainTestMap = splitListRandom(l);
			String tempTrainPath = "C:\\Users\\pebo01\\Desktop\\FakeNewsChallenge2017\\tempTrainData.txt";
//			String binaryDiscussOtherTrainPath = "C:\\Users\\pebo01\\Desktop\\FakeNewsChallenge2017\\tempTrainData2.txt";
//			String binaryAgreeOrDisagreeTrainPath = "C:\\Users\\pebo01\\Desktop\\FakeNewsChallenge2017\\tempTrainData3.txt";
			PrintWriter tempTrain = new PrintWriter(new File(tempTrainPath));
//			PrintWriter binaryDiscussOtherTrain = new PrintWriter(new File(binaryDiscussOtherTrainPath));
//			PrintWriter binaryAgreeOrDisagreeTrain = new PrintWriter(new File(binaryAgreeOrDisagreeTrainPath));
			List<FakeNewsChallengeObject> trainInstances = trainTestMap.get("training");
			ArrayList<FakeNewsChallengeObject> testList = new ArrayList<FakeNewsChallengeObject>();
			for (FakeNewsChallengeObject fn : trainTestMap.get("test")){
				testList.add(fn);
			}
			int q = 1;
			HashMap<String, Integer> tempMap = new HashMap<String, Integer>();
			for (FakeNewsChallengeObject fnco : trainInstances) {
				// filtering out unrelated cases, as this can be done pretty accurately with ngram overlap already
				if (!fnco.getStance().equalsIgnoreCase("unrelated")){
					// id class data
					tempTrain.println(String.format("%s %s %s", Integer.toString(q), fnco.getStance(), fnco.getHeader()));
					tempMap.put(fnco.getStance(), 1); // ugly way of getting set...
					q++;
					// to train a binary discuss/other (agree or disagree) classifier
//					if (fnco.getStance().equalsIgnoreCase("disagree") || fnco.getStance().equalsIgnoreCase("agree")){
//						binaryDiscussOtherTrain.println(String.format("%s %s %s", Integer.toString(q), "agreeOrDisagree", fnco.getHeader()));
//						binaryAgreeOrDisagreeTrain.println(String.format("%s %s %s", Integer.toString(q), fnco.getStance(), fnco.getHeader()));
//					}
//					else{
//						binaryDiscussOtherTrain.println(String.format("%s %s %s", Integer.toString(q), fnco.getStance(), fnco.getHeader())); //  stance is discuss
//					}
						
				}
			}
			int numClasses = tempMap.size();
			tempTrain.close();
			// ++++++++ general three-class classifier +++++++++++++
			DocumentClassification.trainClassifier("C:\\Users\\pebo01\\Desktop\\FakeNewsChallenge2017\\tempTrainData.txt", "C:\\Users\\pebo01\\workspace\\e-Clustering\\src\\main\\resources\\trainedModels\\documentClassification", "fakeNewsChallengeStanceModel", "en", alg);
			Classifier fallbackHeadlineClassifier;
			File fallbackModelFile = FileFactory.generateFileInstance("C:\\Users\\pebo01\\workspace\\e-Clustering\\src\\main\\resources\\trainedModels\\documentClassification\\en-fakeNewsChallengeStanceModel.EXT");
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fallbackModelFile));
			fallbackHeadlineClassifier = (Classifier) ois.readObject();
			ois.close();

//			DocumentClassification.trainClassifier("C:\\Users\\pebo01\\Desktop\\FakeNewsChallenge2017\\tempTrainData.txt", "C:\\Users\\pebo01\\workspace\\e-Clustering\\src\\main\\resources\\trainedModels\\documentClassification", "binaryDiscussOtherModel", "en", alg);
			
			// ++++++++ binary classifiers +++++++++++++
			Classifier binaryAgreeOrDisagreeClassifier;
			String binaryAgreeOrDisagreeTrainData = fnc.createTrainingDataDeleteClass(trainInstances, "discuss");
			DocumentClassification.trainClassifier(binaryAgreeOrDisagreeTrainData, "C:\\Users\\pebo01\\workspace\\e-Clustering\\src\\main\\resources\\trainedModels\\documentClassification", "binaryAgreeOrDisagreeModel", "en", alg);
			File binaryAgreeOrDisagreeModelFile = FileFactory.generateFileInstance("C:\\Users\\pebo01\\workspace\\e-Clustering\\src\\main\\resources\\trainedModels\\documentClassification\\en-binaryAgreeOrDisagreeModel.EXT");
			ObjectInputStream ois3 = new ObjectInputStream(new FileInputStream(binaryAgreeOrDisagreeModelFile));
			binaryAgreeOrDisagreeClassifier = (Classifier) ois3.readObject();
			ois3.close();
			
			Classifier binaryAgreeOrDiscussClassifier;
			String binaryAgreeOrDiscussTrainData = fnc.createTrainingDataDeleteClass(trainInstances, "disagree");
			DocumentClassification.trainClassifier(binaryAgreeOrDiscussTrainData, "C:\\Users\\pebo01\\workspace\\e-Clustering\\src\\main\\resources\\trainedModels\\documentClassification", "binaryAgreeOrDiscussModel", "en", alg);
			File binaryAgreeOrDiscussModelFile = FileFactory.generateFileInstance("C:\\Users\\pebo01\\workspace\\e-Clustering\\src\\main\\resources\\trainedModels\\documentClassification\\en-binaryAgreeOrDiscussModel.EXT");
			ObjectInputStream ois4 = new ObjectInputStream(new FileInputStream(binaryAgreeOrDiscussModelFile));
			binaryAgreeOrDiscussClassifier = (Classifier) ois4.readObject();
			ois4.close();
			
			Classifier binaryDiscussOrDisagreeClassifier;
			String binaryDiscussOrDisagreeTrainData = fnc.createTrainingDataDeleteClass(trainInstances, "agree");
			DocumentClassification.trainClassifier(binaryDiscussOrDisagreeTrainData, "C:\\Users\\pebo01\\workspace\\e-Clustering\\src\\main\\resources\\trainedModels\\documentClassification", "binaryDiscussOrDisagreeModel", "en", alg);
			File binaryDiscussOrDisagreeModelFile = FileFactory.generateFileInstance("C:\\Users\\pebo01\\workspace\\e-Clustering\\src\\main\\resources\\trainedModels\\documentClassification\\en-binaryDiscussOrDisagreeModel.EXT");
			ObjectInputStream ois5 = new ObjectInputStream(new FileInputStream(binaryDiscussOrDisagreeModelFile));
			binaryDiscussOrDisagreeClassifier = (Classifier) ois5.readObject();
			ois5.close();
			
//			Classifier binaryDiscussOtherClassifier;

//			File binaryDiscussOtherModelFile = FileFactory.generateFileInstance("C:\\Users\\pebo01\\workspace\\e-Clustering\\src\\main\\resources\\trainedModels\\documentClassification\\en-binaryDiscussOtherModel.EXT");
//			ObjectInputStream ois2 = new ObjectInputStream(new FileInputStream(binaryDiscussOtherModelFile));
//			binaryDiscussOtherClassifier = (Classifier) ois2.readObject();
//			ois2.close();
			
			
			
			
			//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			
			//+++++++++++++++++ experiments to get optimal threshold value ++++++++++++++++++++++++++++++++++++++++++
//			fnc.classifyRelatedness(l, data);
			//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			
			//+++++++++++++++++ classification experiments with threshold for relatedness of headline/article +++++++
			System.out.println("INFO: Starting classification.");
			double threshold = 0.0096;
			//HashMap<FakeNewsChallengeObject, String> results = fnc.classifyInstances(l, threshold, fallbackHeadlineClassifier);
			HashMap<FakeNewsChallengeObject, String> results = fnc.classifyInstances(testList, threshold, fallbackHeadlineClassifier, numClasses, binaryAgreeOrDisagreeClassifier, binaryAgreeOrDiscussClassifier, binaryDiscussOrDisagreeClassifier); // NOTE: using only testList (10% of all data) now to prevent classifying seen data
			System.out.println("INFO: Done with classification. Evaluating now...");
			//double weightedScore = fnc.evaluate(results, l);
			double weightedScore = fnc.evaluate(results, testList); // NOTE: same here (see comment above on testList)
			scores.add(weightedScore);
			//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			
			//+++++++++++++++++ general playing around ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//			HashMap<String, Double> generalWordCount = new HashMap<String, Double>();
//			HashMap<String, Double> discussWordCount = new HashMap<String, Double>();
//			HashMap<String, Double> disagreeWordCount = new HashMap<String, Double>();
//			HashMap<String, Double> agreeWordCount = new HashMap<String, Double>();
//			int a = 0;
//			int at = 0;
//			int b = 0;
//			int bt = 0;
//			int c = 0;
//			int ct = 0;
//			for (FakeNewsChallengeObject fnco : l){
//				if (fnco.getStance().equalsIgnoreCase("discuss")){
//					//System.out.println("DEBUG: Discuss: " + fnco.getHeader());
//					at++;
//					if (fnco.getHeader().trim().endsWith("?\"")){
//						a++;
//					}
//					for (String token : Tokenizer.simpleTokenizeInput(fnco.getArticle())){
//						double i = (generalWordCount.containsKey(token) ? generalWordCount.get(token) + 1 : 1);
//						generalWordCount.put(token,  i);
//						double j = (discussWordCount.containsKey(token) ? discussWordCount.get(token) + 1 : 1);
//						discussWordCount.put(token,  j);
//					}
//				}
//				else if (fnco.getStance().equalsIgnoreCase("disagree")){
//					bt++;
//					if (fnco.getHeader().trim().endsWith("?\"")){
//						b++;
//					}
//					//System.out.println("DEBUG: Disagree: " + fnco.getHeader());
//					for (String token : Tokenizer.simpleTokenizeInput(fnco.getArticle())){
//						double i = (generalWordCount.containsKey(token) ? generalWordCount.get(token) + 1 : 1);
//						generalWordCount.put(token,  i);
//						double j = (disagreeWordCount.containsKey(token) ? disagreeWordCount.get(token) + 1 : 1);
//						disagreeWordCount.put(token,  j);
//					}
//					
//				}
//				else if (fnco.getStance().equalsIgnoreCase("agree")){
//					ct++;
//					if (fnco.getHeader().trim().endsWith("?\"")){
//						c++;
//					}
//					for (String token : Tokenizer.simpleTokenizeInput(fnco.getArticle())){
//						double i = (generalWordCount.containsKey(token) ? generalWordCount.get(token) + 1 : 1);
//						generalWordCount.put(token,  i);
//						double j = (agreeWordCount.containsKey(token) ? agreeWordCount.get(token) + 1 : 1);
//						agreeWordCount.put(token,  j);
//					}
//				}
//			}
//			System.out.println(String.format("Discuss headlines ending with question mark: %s of %s (%s).", a, at, Double.toString((double)a/at)));
//			System.out.println(String.format("Disagree headlines ending with question mark: %s of %s (%s).", b, bt, Double.toString((double)b/bt)));
//			System.out.println(String.format("Agree headlines ending with question mark: %s of %s (%s).", c, ct, Double.toString((double)c/ct)));
//			for (String token : discussWordCount.keySet()){
//				double d = discussWordCount.get(token) / generalWordCount.get(token);
//				discussWordCount.put(token, d);
//			}
//			for (String token : disagreeWordCount.keySet()){
//				double d = disagreeWordCount.get(token) / generalWordCount.get(token);
//				disagreeWordCount.put(token, d);
//			}
//			for (String token : agreeWordCount.keySet()){
//				double d = agreeWordCount.get(token) / generalWordCount.get(token);
//				agreeWordCount.put(token, d);
//			}
//			Map<String, Double> sorted = fnc.sortByValue(discussWordCount);
//			for (String token : sorted.keySet()){
//				System.out.println(token + "\t" + discussWordCount.get(token));
//			}
			
			//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			}
			double t = 0.0;
			for (double d : scores){
				t += d;
			}
			System.out.println(String.format("INFO: Result of %s-fold cross-validation for algorithm: %s\t%s.", Integer.toString(numIterations), alg, Double.toString(t/scores.size())));
			//debug.println(String.format("INFO: Result of %s-fold cross-validation for algorithm: %s\t%s.", Integer.toString(numIterations), alg, Double.toString(t/scores.size())));
			}
			//debug.close();
			System.out.println("INFO: Done.");
			debugOut.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
					
	}

}
