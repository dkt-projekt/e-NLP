package de.dkt.eservices.erattlesnakenlp.modules;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.springframework.core.io.ClassPathResource;

import com.hp.hpl.jena.rdf.model.Model;

import de.dkt.common.filemanagement.FileFactory;
import de.dkt.common.niftools.NIFReader;
import de.dkt.common.niftools.NIFWriter;
import eu.freme.common.conversion.rdf.RDFConstants.RDFSerialization;
import eu.freme.common.exception.ExternalServiceFailedException;

public class LanguageIdentificator {
	
	static int minNgramSize = 2;
	static int maxNgramsize = 5;
	
	public static String languageModelsDirectory = "languageModels";
	public static HashMap<String, HashMap<String,Double>> ngrampMapDict = new HashMap<String, HashMap<String,Double>>(); 
	
	public static void initializeNgramMaps(){
		
		try {
			ClassPathResource cprDir = new ClassPathResource(languageModelsDirectory);
			File fModelsDirectory;
			fModelsDirectory = cprDir.getFile();
			for (File lm : fModelsDirectory.listFiles()){
				String language = lm.getName().substring(0,lm.getName().indexOf(".ngram"));
				//System.out.println("debugging lang:" + language);
				FileInputStream fis;
		        fis = new FileInputStream(lm);
				ObjectInputStream ois = new ObjectInputStream(fis);
			    HashMap<String, Double> mapInFile = (HashMap<String, Double>)ois.readObject();
			    ngrampMapDict.put(language, mapInFile);
			    ois.close();
			    fis.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static Model detectLanguageNIF(Model nifModel) throws ExternalServiceFailedException {		
		
		String isString = NIFReader.extractIsString(nifModel);
		
		Double highestScore = 0.0;
		String highestScoringLanguage = null;
		// list all available ngram maps
		Iterator it = ngrampMapDict.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry)it.next();
		    String language = (String) pair.getKey();
		    HashMap<String,Double> lm = (HashMap<String, Double>) pair.getValue();
		    Double languageScore = 0.0;
			for (int i = minNgramSize; i <= maxNgramsize; i++){
			    for (int j = 0; j <= isString.length() - i; j++){
			    	String ngram = isString.substring(j, j+i);
			    	// since it's already normalized, I can just sum here
			    	if (lm.containsKey(ngram)){
			    		languageScore += lm.get(ngram);
			    	}
				}
			}
			//System.out.println("Score for:" + language + " : " + languageScore);
			//NOTE that this is just a plain threshold. May want to do something with confidence scores, or otherwise provide multiple if scores are too close together
			if (languageScore > highestScore){
				highestScoringLanguage = language;
				highestScore = languageScore;
			}
		}
		
		String docURI = NIFReader.extractDocumentURI(nifModel);
		NIFWriter.addLanguageAnnotation(nifModel, isString, docURI, highestScoringLanguage);
		return nifModel;
	}
	
	
	public static void createLanguageNgramModel(String corpusFilePath, String inputLanguage){
		
		File cp = new File(corpusFilePath);
		HashMap<String, Double> ngrams = new HashMap<String, Double>();
		HashMap<Integer, Double> ngramCounts = new HashMap<Integer, Double>();
		try {
			BufferedReader br = FileFactory.generateBufferedReaderInstance(cp.getAbsolutePath(), "utf-8");
			String line = br.readLine();
			while(line!=null){
				
				for (int i = minNgramSize; i <= maxNgramsize; i++){
					ngramCounts.put(i, 0.0);
					for (int j = 0; j <= line.length() - i; j++){
						String ngram = line.substring(j, j+i);
						ngramCounts.put(i, ngramCounts.get(i)+1);
						if (!ngrams.containsKey(ngram)){
							ngrams.put(ngram, 1.0);
						}
						else{
							ngrams.put(ngram, ngrams.get(ngram)+1);
						}
					}
				}
				line = br.readLine();
			}
			br.close();
			
			HashMap<String, Double> normalizedNgrams = new HashMap<String, Double>();
			for (String ngram : ngrams.keySet()){
				normalizedNgrams.put(ngram, (double) (ngrams.get(ngram)/ngramCounts.get(ngram.length())));
			}
			//System.out.println(normalizedNgrams);

			// write hashmap to file
			ClassPathResource cprDir = new ClassPathResource(languageModelsDirectory);
			File fModelsDirectory = cprDir.getFile();
			File mapFile = new File(fModelsDirectory, inputLanguage + ".ngrams");
			mapFile.createNewFile();
		    FileOutputStream fos = new FileOutputStream(mapFile);
		    ObjectOutputStream oos = new ObjectOutputStream(fos);

		    oos.writeObject(normalizedNgrams);
		    oos.flush();
		    oos.close();
		    fos.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	

	public static void main(String[] args) throws Exception {

		/*
		long start = System.currentTimeMillis();
		createLanguageNgramModel("C:\\Users\\pebo01\\Desktop\\ubuntuShare\\englishSentences.txt", "en");
		long time = System.currentTimeMillis() - start;
		System.out.println("Done creating ngramModel. Took " + time + " milliseconds.\n");
		*/
		
		//detectLanguageNIF("aapje");
		long startInit = System.currentTimeMillis();
		initializeNgramMaps();
		System.out.println("done initializing in " + (System.currentTimeMillis() - startInit));
		
		
		String turtleInput3 = 
				"@prefix rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n" +
				"@prefix xsd:   <http://www.w3.org/2001/XMLSchema#> .\n" +
				"@prefix itsrdf: <http://www.w3.org/2005/11/its/rdf#> .\n" +
				"@prefix nif:   <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#> .\n" +
				"@prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#> .\n" +
				"\n" +
				"<http://dkt.dfki.de/documents/#char=0,81>\n" +
				"        a                    nif:RFC5147String , nif:String , nif:Context ;\n" +
				"        nif:beginIndex       \"0\"^^xsd:nonNegativeInteger ;\n" +
				"        nif:endIndex         \"81\"^^xsd:nonNegativeInteger ;\n" +
				"        nif:isString         \"Aber ich versprach, daï¿½wir uns vorstellen werden, gemeinsam, einmal in Indien.\"^^xsd:string ;\n" +
				"";
		Model nifModel = NIFReader.extractModelFromFormatString(turtleInput3, RDFSerialization.TURTLE);
		
		
		long startRecog = System.currentTimeMillis();
		nifModel = detectLanguageNIF(nifModel);
		System.out.println("done recognizing in " + (System.currentTimeMillis() - startRecog));
		System.out.println("model:" + NIFReader.model2String(nifModel, RDFSerialization.TURTLE));
	}
	
	
}
