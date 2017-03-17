package de.dkt.eservices.erattlesnakenlp.linguistic;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class AbusiveLanguageACLWorkshop2017 {
	
	final static int MIN_NGRAM_LENGTH = 1;
	final static int MAX_NGRAM_LENGTH = 3;
	
	public static void main(String args[]) throws IOException{
		
		Path filePath = new File("C:\\Users\\pebo01\\Desktop\\ubuntuShare\\naughtySorted.txt").toPath();
		List<String> naughtyTweets = Files.readAllLines(filePath, StandardCharsets.UTF_8);
		ArrayList<String> cleanNaughtyTweets = new ArrayList<String>();
		for (String t : naughtyTweets){
			cleanNaughtyTweets.add(TweetCleaner.clean(t));
		}
		Path filePath2 = new File("C:\\Users\\pebo01\\Desktop\\ubuntuShare\\decentSorted.txt").toPath();
		List<String> niceTweets = Files.readAllLines(filePath2, StandardCharsets.UTF_8);
		ArrayList<String> cleanNiceTweets = new ArrayList<String>();
		for (String t : niceTweets){
			cleanNiceTweets.add(TweetCleaner.clean(t));
		}
		
		HashMap<String[], Double> naughtyNgramScores = getWordNgramScores(cleanNaughtyTweets);
		HashMap<String[], Double> niceNgramScores = getWordNgramScores(cleanNiceTweets);
		//TODO: also try out with getCharacterNgramScores here!
		
		// do some smoothing first (add +1 to every ngrams from map a to map b and other way round)
		naughtyNgramScores = smoothMap(naughtyNgramScores, niceNgramScores);
		niceNgramScores = smoothMap(niceNgramScores, naughtyNgramScores);
				
		Map<String[], Double> diffMap = getScoreDifference(naughtyNgramScores, niceNgramScores);
		
		
		//Map<String[], Double> diffMap = compareWithThreshold(naughtyNgramScores, niceNgramScores, 1);
		//diffMap = sortByValue(diffMap);
		
		
		try {
			PrintWriter out = new PrintWriter(new File("C:\\Users\\pebo01\\Desktop\\debug.txt"));
			for (String[] sa : diffMap.keySet()){
				out.println("Key: " + Arrays.toString(sa));
				out.println("val: " + diffMap.get(sa));
				out.println("\n");
			}
			out.close();
		
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static HashMap<String[], Double> compareWithThreshold(HashMap<String[], Double> hm1, HashMap<String[], Double> hm2, int threshold){
		
		HashMap<String[], Double> rm = new HashMap<String[], Double>();
		
		for (String[] sa : hm1.keySet()){
			if (hm1.get(sa) > hm2.get(sa) * threshold){
				rm.put(sa, hm1.get(sa));
			}
		}
		
		return rm;
	}
	
	public static Map<String[], Double> getScoreDifference(HashMap<String[], Double> hm1, HashMap<String[], Double> hm2){
		
		Map<String[], Double> diffMap = new HashMap<String[], Double>();
		
		// all ngrams are now in both, so doesn't matter which one I loop through here
		for (String[] sa : hm1.keySet()){
			Double scoreDiff = hm1.get(sa) - hm2.get(sa); // think I don't need an Abs() here, but check...
			diffMap.put(sa, scoreDiff);
		}
		
		//sort the map
		diffMap = sortByValue(diffMap);
		
		return diffMap;
	}
	
	// taken from http://stackoverflow.com/questions/109383/sort-a-mapkey-value-by-values-java
	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
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
	
	public static HashMap<String[], Double> smoothMap(HashMap<String[], Double> hm1, HashMap<String[], Double> hm2){
		
		double smallestValue = 10;
		for (String[] sa : hm1.keySet()){
			if (hm1.get(sa) < smallestValue){
				smallestValue = hm1.get(sa);
			}
		}
		for (String[] sa : hm2.keySet()){
			if (hm2.get(sa) < smallestValue){
				smallestValue = hm2.get(sa);
			}
		}
		
		for (String[] sa : hm2.keySet()){
			Double d = (hm1.containsKey(sa) ? hm1.get(sa) + smallestValue : smallestValue);
			hm1.put(sa, d);
		}
		return hm1;
		
	}
	
	public static HashMap<String, Double> getCharacterNgramScores(ArrayList<String> tweets){
		
		HashMap<String, Double> hm = new HashMap<String, Double>();
		HashMap<Integer, Integer> hm2 = new HashMap<Integer, Integer>();
		
		for (String t : tweets){
			for (int i = MIN_NGRAM_LENGTH; i <= MAX_NGRAM_LENGTH; i++){
				for (int j = 0; j < t.length()+1 - i; j++){
					String ngram = t.substring(j,  j+i);
					int v = (hm2.containsKey(ngram.length()) ? hm2.get(ngram.length()) + 1 : 1);
					hm2.put(ngram.length(), v);
					double c = (hm.containsKey(ngram) ? hm.get(ngram) + 1 : 1);
					hm.put(ngram, (double)c);
				}
			}
		}
		
		// now normalize the values
		for (String key : hm.keySet()){
			double normVal = hm.get(key) / (double) hm2.get(key.length());// not sure if the casting to double is necessary here...
			hm.put(key, normVal);
		}
		return hm;
		
	}
	
	
	public static HashMap<String[], Double> getWordNgramScores(ArrayList<String> tweets){
		
		HashMap<String[], Double> hm = new HashMap<String[], Double>();
		HashMap<Integer, Integer> hm2 = new HashMap<Integer, Integer>();
		
		for (String t : tweets){
			//String[] tokens = Tokenizer.simpleTokenizeInput(t);
			String[] tokens = t.split("\\s"); // the tokenizer also split up the <allCaps> etc. marks that cleanTweet adds...
			for (int i = MIN_NGRAM_LENGTH; i <= MAX_NGRAM_LENGTH; i++){
				for (int j = 0; j < tokens.length+1 - i; j++){
					String[] ngram = Arrays.copyOfRange(tokens, j, j+i);
					int v = (hm2.containsKey(ngram.length) ? hm2.get(ngram.length) + 1 : 1);
					hm2.put(ngram.length, v);
					double c = (hm.containsKey(ngram) ? hm.get(ngram) + 1 : 1);
					hm.put(ngram, (double)c);
				}
			}
		}
		
		// now normalize the values
		for (String[] key : hm.keySet()){
			double normVal = hm.get(key) / (double) hm2.get(key.length);// not sure if the casting to double is necessary here...
			hm.put(key, normVal);
		}
		
		return hm;
		
	}

}
