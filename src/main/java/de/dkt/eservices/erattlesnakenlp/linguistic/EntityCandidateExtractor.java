package de.dkt.eservices.erattlesnakenlp.linguistic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.hp.hpl.jena.rdf.model.Model;

import de.dkt.common.filemanagement.FileFactory;
import de.dkt.common.niftools.NIFReader;
import de.dkt.common.niftools.NIFWriter;
import de.dkt.eservices.ecorenlp.modules.Tagger;
import de.dkt.eservices.eopennlp.modules.SentenceDetector;
import de.dkt.eservices.eopennlp.modules.Tokenizer;
import eu.freme.common.exception.BadRequestException;





public class EntityCandidateExtractor {

	
	public static String serializedHashMapDirectory = "referenceCorpora";
	public static String serializedStopListDirectory = "stopwords";
	static int minNgramSize = 2;
	static int maxNgramSize = 5;
	
	public static HashMap<String, Double> entitySuggest(Model nifModel, String language, String referenceMap, double threshold, ArrayList<String> classificationModels){
		
		ArrayList<String> candidates = new ArrayList<String>();				
		Tagger.initTagger(language);
		
		String str = NIFReader.extractIsString(nifModel);
		HashMap<String, HashMap<String, Integer>> tagMap = Tagger.getTagMap(str, language);
		String[] sentences = SentenceDetector.detectSentences(str, language + "-sent.bin");
		HashMap<String, Integer> docCount = new HashMap<String, Integer>();
		double totalWordsInDoc = 0;
		for (String sentence : sentences){
			String[] tokens = Tokenizer.simpleTokenizeInput(sentence);
			for (String token : tokens){
				totalWordsInDoc += 1;
				int count = docCount.containsKey(token) ? docCount.get(token) : 0;
				docCount.put(token, count + 1);
			}
		}
		
		HashMap<String, Integer> refMap = null;
		File serializedFile = null;
		try{
			//System.out.println(serializedHashMapDirectory + File.separator + referenceMap + ".ser");
			serializedFile = FileFactory.generateFileInstance(serializedHashMapDirectory + File.separator + referenceMap + ".ser");
			FileInputStream fis = new FileInputStream(serializedFile);
			//System.out.println("DEBUGGING ref file:" + serializedFile.getAbsolutePath());
			//FileInputStream fis = new FileInputStream("C:\\Users\\pebo01\\workspace\\e-NLP\\src\\main\\resources\\referenceCorpora" + File.separator + referenceMap + ".ser");
			ObjectInputStream ois = new ObjectInputStream(fis);
			refMap = (HashMap) ois.readObject();
			ois.close();
			fis.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw new BadRequestException("ERROR: could not find reference corpus:" + serializedFile.getAbsolutePath());
		}

		double maxValue = 0;
		for (String token : refMap.keySet()){
			if (refMap.get(token) > maxValue){
				maxValue = refMap.get(token);
			}
		}
		//TODO: get total number of docs in reference corpus. the above is suboptimal, should be able to get the correct number from elsewhere...
		
		HashMap<String, Double> tokenScores = new HashMap<String, Double>();
		double maxTfIdf = 0;
		for (String token : docCount.keySet()){
			double termFreq = docCount.get(token) / totalWordsInDoc;
			// guess I can allow for this kind of smoothing (otherwise get a zero-division error)
			double refCount = refMap.containsKey(token) ? refMap.get(token) : 1;
			double idFreq = Math.log(maxValue / refCount);
			double tfidf = termFreq * idFreq;
			tokenScores.put(token, tfidf);
			if (tfidf > maxTfIdf){
				maxTfIdf = tfidf;
			}
		}
		  
		
		ArrayList<String> stoplist = null;
		try{
			String s = null;
			if (language.equalsIgnoreCase("en")){
				s = "english";
			}
			else if (language.equalsIgnoreCase("de")){
				s = "german";
			}
			else{
				throw new BadRequestException("No stoplist available for language: "+language+". Please create one first.");
			}
			serializedFile = FileFactory.generateFileInstance(serializedStopListDirectory + File.separator + s + ".ser");
			FileInputStream fis = new FileInputStream(serializedFile);
			//FileInputStream fis = new FileInputStream("C:\\Users\\pebo01\\workspace\\e-NLP\\src\\main\\resources\\stopwords" + File.separator + s + ".ser");
			ObjectInputStream ois = new ObjectInputStream(fis);
			stoplist = (ArrayList<String>) ois.readObject();
			ois.close();
			fis.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw new BadRequestException("ERROR: could not load stoplist for language:" + language);
		}

		for (String token : tokenScores.keySet()){
			if (tokenScores.get(token) / maxTfIdf > threshold){
				if (!(stoplist.contains(token.toLowerCase()))){
					if (tagMap.containsKey(token)){
						boolean exclude = true;
						for (String tag : tagMap.get(token).keySet()){
							if (tag.startsWith("N")){
								exclude = false;
							}
						}
						if (!exclude){
							if (!(token.matches("^\\W+$"))){
								if (!(token.matches("^\\d+$"))){
									candidates.add(token);
								}
							}
						}
					}
				}
			}
		}
		HashMap<String, Double> resultMap = new HashMap<String, Double>();
		for (String c : candidates){
			resultMap.put(c, tokenScores.get(c));
		}
		
		//System.out.println(candidates);
		if (classificationModels != null){
			HashMap<String, Double> resultMap2 = new HashMap<String, Double>();
			HashMap<String, String> classScores = classifySuggestions(classificationModels, candidates);
			// a bit ugly, but for now this will do as I don't want to change return type depending on if models are specified or not:
			//ArrayList<String> candidatesWithClasses = new ArrayList<String>();
			for (String c : classScores.keySet()){
				//candidatesWithClasses.add(c + "\t" + classScores.get(c));
				resultMap2.put(c + "\t" + classScores.get(c), tokenScores.get(c));
			}
			//candidates = candidatesWithClasses;
			resultMap = resultMap2;
		}
		
		return resultMap;
		
	}
	
	
	public static HashMap<String, String> classifySuggestions(ArrayList<String> languageModels, ArrayList<String> candidates){
		
		HashMap<String, HashMap<String, Double>> ngrampMapDict = new HashMap<String, HashMap<String, Double>>();
		for (String model : languageModels) {
			FileInputStream fis;
			try {
				File serializedFile = FileFactory.generateFileInstance(serializedHashMapDirectory + File.separator + model + ".ser");
				fis = new FileInputStream(serializedFile);
				//fis = new FileInputStream("C:\\Users\\pebo01\\workspace\\e-NLP\\src\\main\\resources\\referenceCorpora"	+ File.separator + model + ".ser");
				ObjectInputStream ois = new ObjectInputStream(fis);
				HashMap<String, Double> mapInFile = (HashMap<String, Double>) ois.readObject();
				ngrampMapDict.put(model, mapInFile);
				ois.close();
				fis.close();
			} catch (Exception e) {
				//e.printStackTrace();
				throw new BadRequestException("File for model: " + model + " could not be found. Please create this model first.");
			}

		}
		
		// now we have the reference models, get the score for every candidate
		Iterator it = ngrampMapDict.entrySet().iterator();
		HashMap<String, HashMap<String, Double>> candidateClassificationScores = new HashMap<String, HashMap<String, Double>>();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			String model = (String) pair.getKey();
			HashMap<String, Double> lm = (HashMap<String, Double>) pair.getValue();
			HashMap<String, Double> innerMap = new HashMap<String, Double>();
			Double maxValue = 0.0;
			for (String  k : lm.keySet()){
				maxValue += lm.get(k);
			}
			for (String candidate : candidates) {
				Double classScore = 0.0;
				for (int i = minNgramSize; i <= maxNgramSize; i++) {
					for (int j = 0; j <= candidate.length() - i; j++) {
						String ngram = candidate.substring(j, j + i);
						if (lm.containsKey(ngram)) {
							classScore += (lm.get(ngram) / maxValue);
						}
					}
				}
				innerMap.put(candidate, classScore);
			}
			candidateClassificationScores.put(model, innerMap);
			//TODO: continue here. Debug why it's always generalEnglishLm in my example... Should I do some more normalization somehow to correct for large difference in LM sizes?
			
		}
		
		// because of the structure of the hashmap (should be reversed), this is
		// a bit elaborate now...
		HashMap<String, String> classificationScores = new HashMap<String, String>();
		for (String candidate : candidates){
			Double highestScore = 0.0;
			String cl = null;
			for (String model : candidateClassificationScores.keySet()){
				if (candidateClassificationScores.get(model).get(candidate) > highestScore){
					highestScore = candidateClassificationScores.get(model).get(candidate);
					cl = model;
				}
			}
			classificationScores.put(candidate, cl);
		}
		
		return classificationScores;
		
	}
	
	
	// This has to be done only once, hence there is no endpoint attached to it. Just do it locally, create the serialized hash, upload to github and done.
	public static void serializeTfIdfHashMap(String inputFolder, String outmapName){
		
		FileReader fileReader;
		HashMap<String, Integer> token2docs = new HashMap<String, Integer>();
		try {
			File df = FileFactory.generateOrCreateDirectoryInstance(inputFolder);
			for (File f : df.listFiles()) {
				fileReader = new FileReader(f);
				BufferedReader bufferedReader = new BufferedReader(fileReader);
				String line = null;
				HashMap<String, Integer> tokenCount = new HashMap<String, Integer>();
				while ((line = bufferedReader.readLine()) != null) {
					String[] tokens = Tokenizer.simpleTokenizeInput(line);
					for (String token: tokens){
						int count = tokenCount.containsKey(token) ? tokenCount.get(token) : 0;
						tokenCount.put(token, count + 1);
					}
				}
				for (String token : tokenCount.keySet()){
					int count = token2docs.containsKey(token) ? token2docs.get(token) : 0;
					token2docs.put(token, count + 1);
				}
				bufferedReader.close();
			}
			
			String serializedMapPath = serializedHashMapDirectory + File.separator + outmapName + ".ser";
			//serializedMapPath = "C:\\Users\\pebo01\\workspace\\e-NLP\\src\\main\\resources\\referenceCorpora" + File.separator + outmapName + ".ser";
			File serializedFile = FileFactory.generateOrCreateFileInstance(serializedMapPath);
			FileOutputStream fos = new FileOutputStream(serializedFile);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(token2docs);
			oos.close();
			fos.close();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// like the above, only done once, best locally.
	public static void serializeStoplist(String inputFile, String stoplistName){
		
		
		try {
			ArrayList<String> stopwords = new ArrayList<String>();
			FileReader fileReader = new FileReader(inputFile);
			BufferedReader br = new BufferedReader(fileReader);
			String line = null;
			while ((line = br.readLine()) != null){
				stopwords.add(line);
			}
			br.close();
			String serializedListPath = serializedStopListDirectory + File.separator + stoplistName + ".ser";
			File serializedFile = FileFactory.generateOrCreateFileInstance(serializedListPath);
			//serializedListPath = "C:\\Users\\pebo01\\workspace\\e-NLP\\src\\main\\resources\\stopwords" + File.separator + stoplistName + ".ser";
			FileOutputStream fos = new FileOutputStream(serializedFile);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(stopwords);
			oos.close();
			fos.close();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	public static void serializeClassLanguageModel(String data, String modelName){
		

		HashMap<String, Double> ngrams = new HashMap<String, Double>();
		HashMap<Integer, Double> ngramCounts = new HashMap<Integer, Double>();
		// the below is file based (i.e. file as input) in case I want to run it locally again at some point
//		try{
//			FileReader fr = new FileReader(inputFile);
//			BufferedReader br = new BufferedReader(fr);
//			String line = null;
//			while ((line = br.readLine()) != null){
//				for (int i = minNgramSize; i < maxNgramSize; i++){
//					ngramCounts.put(i, 0.0);
//					for (int j = 0; j < line.length()-i; j++){
//						String ngram = line.substring(j, j+i);
//						ngramCounts.put(i, ngramCounts.get(i)+1);
//						if (!ngrams.containsKey(ngram)){
//							ngrams.put(ngram, 1.0);
//						}
//						else{
//							ngrams.put(ngram, ngrams.get(ngram)+1);
//						}
//					}
//				}
//			}
//			br.close();

		try{

			for (int i = minNgramSize; i < maxNgramSize; i++) {
				ngramCounts.put(i, 0.0);
				for (int j = 0; j < data.length() - i; j++) {
					String ngram = data.substring(j, j + i);
					ngramCounts.put(i, ngramCounts.get(i) + 1);
					if (!ngrams.containsKey(ngram)) {
						ngrams.put(ngram, 1.0);
					} else {
						ngrams.put(ngram, ngrams.get(ngram) + 1);
					}
				}
			}
		
			HashMap<String, Double> normalizedNgrams = new HashMap<String, Double>();
			for (String ngram : ngrams.keySet()){
				normalizedNgrams.put(ngram, (double) (ngrams.get(ngram)/ngramCounts.get(ngram.length())));
			}
			String serializedLMPath = serializedHashMapDirectory + File.separator + modelName + ".ser";
			//serializedLMPath = "C:\\Users\\pebo01\\workspace\\e-NLP\\src\\main\\resources\\referenceCorpora" + File.separator + modelName + ".ser";
			File serializedFile = FileFactory.generateOrCreateFileInstance(serializedLMPath);
			//System.out.println(serializedFile.getAbsolutePath());
			FileOutputStream fos = new FileOutputStream(serializedFile);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(normalizedNgrams);
			oos.close();
			fos.close();
			
		}
		catch (Exception e){
			//TODO
			e.printStackTrace();
		}
		
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
			NIFWriter.addInitialString(nifModel, readFile("C:\\Users\\pebo01\\Desktop\\data\\ARTCOM\\DKT_Texte_AC\\DKT_Texte_AC\\Micropia\\Wikipedia\\TXT\\Fungus.txt", StandardCharsets.UTF_8), "http://dkt.dfki.de/documents/#");
			//suggestCandidates(nifModel, "en", 0.1);
			entitySuggest(nifModel, "en", "englishReuters", 0.02, null);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//serializeClassLanguageModel("C:\\Users\\pebo01\\Desktop\\data\\ARTCOM\\listOfUNCountriesModified.txt", "countriesLM");
		
		//serializeTfIdfHashMap("C:\\Users\\pebo01\\Desktop\\data\\tfidDocumentDollections\\reuters\\docCollection", "englishReuters");
		
		//serializeStoplist("C:\\Users\\pebo01\\Desktop\\englishStoplist.txt", "english");
		
		
		//System.out.println(suggestCandidates(nifModel, "en", 0.5));
	}
	
}
