package de.dkt.eservices.erattlesnakenlp.linguistic;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import de.dkt.eservices.ecorenlp.modules.Lemmatizer;

public class FakeNewsChallenge2017 {
	
	final static int MIN_NGRAM_LENGTH = 1;
	final static int MAX_NGRAM_LENGTH = 6;
	
	private ArrayList<FakeNewsChallengeItem> parseTsv(String[] flines){
		Lemmatizer.initLemmatizer();
		ArrayList<FakeNewsChallengeItem> l = new ArrayList<FakeNewsChallengeItem>();
		for (String s : flines){
			String[] parts = s.split("\t");
			int id = Integer.parseInt(parts[0]);
			String stance = parts[1];
			int articleId = Integer.parseInt(parts[2]);
			String article = parts[3];
			String header = parts[4];
			FakeNewsChallengeItem fnci = new FakeNewsChallengeItem(id, articleId, stance, article, header); // TODO: once code is in place, try with and without lemmatizing
//			String headerLemmas = getLemmas(header);
//			String articleLemmas = getLemmas(article);
//			FakeNewsChallengeItem fnci = new FakeNewsChallengeItem(id, articleId, stance, article, header, headerLemmas, articleLemmas);
			l.add(fnci);
		}
		return l;
	}
	
	private String getLemmas(String text){
		List<String> lemmas = Lemmatizer.lemmatize(text, "elvish"); // language param is not used...
		String lemmaStrings = String.join(" ", lemmas);
		return lemmaStrings;
	}
	
	private HashMap<String, Integer> getNgrams(String text){
		HashMap<String, Integer> hm = new HashMap<String, Integer>();
		String[] tokens = text.split("\\s");
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
	
	public static void main (String[] args){
		
		FakeNewsChallenge2017 fnc = new FakeNewsChallenge2017();
		try {
			System.out.println("INFO: Parsing tsv...");
			ArrayList<FakeNewsChallengeItem> l = fnc.parseTsv(fnc.readLines("C:\\Users\\pebo01\\Desktop\\ubuntuShare\\fncData.tsv"));
			System.out.println("INFO: Done parsing tsv.");
			for (FakeNewsChallengeItem fnci : l){
//				HashMap<String, Integer> headerNgrams = fnc.getNgrams(fnci.getHeaderLemmas());
//				HashMap<String, Integer> articleNgrams = fnc.getNgrams(fnci.getArticleLemmas());
				HashMap<String, Integer> headerNgrams = fnc.getNgrams(fnci.getHeader());
				HashMap<String, Integer> articleNgrams = fnc.getNgrams(fnci.getArticle());
				double score = 0.0;
				for (String ngram : headerNgrams.keySet()){
					if (articleNgrams.containsKey(ngram)){
						score += (headerNgrams.get(ngram) + articleNgrams.get(ngram)) * ngram.split("\\s").length; // last part is to give longer ngram more weight (maybe should be not linear but even exponential)
					}
				}
				score = score / (headerNgrams.size() + articleNgrams.size());
				//TODO: DEBUG THIS WHOLE THING!!!
				System.out.println("STANCE:" + fnci.getStance());
				System.out.println("SCORE:" + score);
				System.out.println("HEADER:" + fnci.getHeader());
				System.out.println("ARTICLE:" + fnci.getArticle());
				System.out.println("\n\n");
				
				
			}
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

}
