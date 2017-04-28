package de.dkt.eservices.erattlesnakenlp.linguistic;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import de.dkt.eservices.ecorenlp.modules.Lemmatizer;

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
			String stance = parts[1].replaceAll("\"", "");
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
	
	private HashMap<FakeNewsChallengeObject, String> classifyInstances(ArrayList<FakeNewsChallengeObject> l, double threshold){
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
				// do fine-grained classification of related ones here (agree, disagree or discuss)
				
				hm.put(fnco, "discuss"); // baseline (popular vote...)
			}
		}
		
		return hm;
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
	
	private void evaluate(HashMap<FakeNewsChallengeObject, String> results, ArrayList<FakeNewsChallengeObject> l){
		
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
		System.out.println("debug relatednessScore:" + rs);
		System.out.println("debug finegrainedScore:" + fs);
		double finalScore = ((fs * 3) + (rs * 1)) / 4;
		System.out.println("INFO: Weighted score:" + finalScore);
		
	}
	
	public static void main (String[] args){
		
		FakeNewsChallenge2017 fnc = new FakeNewsChallenge2017();
		String data = "C:\\Users\\pebo01\\Desktop\\ubuntuShare\\fncData.tsv";
		System.out.println("INFO: Parsing tsv...");
		ArrayList<FakeNewsChallengeObject> l;
		try {
			l = fnc.parseTsv(fnc.readLines(data), true);
			System.out.println("INFO: Done parsing tsv.");

			
			//+++++++++++++++++ experiments to get optimal threshold value ++++++++++++++++++++++++++++++++++++++++++
//			fnc.classifyRelatedness(l, data);
			//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			
			//+++++++++++++++++ classification experiments with threshold for relatedness of headline/article +++++++
//			System.out.println("INFO: Starting classification.");
//			double threshold = 0.0096;
//			HashMap<FakeNewsChallengeObject, String> results = fnc.classifyInstances(l, threshold);
//			System.out.println("INFO: Done with classification. Evaluating now...");
//			fnc.evaluate(results, l);
			//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			
			//+++++++++++++++++ general playing around ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			for (FakeNewsChallengeObject fnco : l){
				if (fnco.getStance().equalsIgnoreCase("discuss")){
					System.out.println("DEBUG: Discuss: " + fnco.getHeader());
				}
				else if (fnco.getStance().equalsIgnoreCase("disagree")){
					System.out.println("DEBUG: Disagree: " + fnco.getHeader());
				}
				else if (fnco.getStance().equalsIgnoreCase("agree")){
					System.out.println("DEBUG: Agree: " + fnco.getHeader());
				}
			}
			
			//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			
			System.out.println("INFO: Done.");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
					
	}

}
