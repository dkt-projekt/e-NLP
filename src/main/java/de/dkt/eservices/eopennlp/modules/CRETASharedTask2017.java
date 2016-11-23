package de.dkt.eservices.eopennlp.modules;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.event.PopupMenuListener;

import org.springframework.core.io.ClassPathResource;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.util.Span;

public class CRETASharedTask2017 {
	
	public static String modelsDirectory = "trainedModels" + File.separator + "ner" + File.separator;
	
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
	
	
	public static void main(String[] args) {
		

				
		try {
			PrintWriter out = new PrintWriter(new File("C:\\Users\\pebo01\\Desktop\\debug.txt"));
			String content = readFile("C:\\Users\\pebo01\\Desktop\\ubuntuShare\\cleanTest.txt", StandardCharsets.UTF_8);
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
			String[] modelNames = {"parzival_PER.bin", "parzival_LOC.bin"};
			ArrayList<NameFinderME> nfList = populateNameFinderList(modelNames);
			
			for (NameFinderME nfModel : nfList){
				for (Span sentenceSpan : sentenceSpans) {
					String sentence = content.substring(sentenceSpan.getStart(), sentenceSpan.getEnd());
					//Span tokenSpans[] = Tokenizer.simpleTokenizeIndices(sentence);
					//String tokens[] = Span.spansToStrings(tokenSpans, sentence);
					String[] tokens = sentence.split(" ");
					Span[] tokenSpans = new Span[tokens.length];
					int k = 0;
					for (int m = 0; m < tokens.length; m++){
						String token = tokens[m];
						Span s = new Span(k, k + token.length());
						k = k + token.length() + 1;
						tokenSpans[m] = s;
					}
					
					Span nameSpans[];
					synchronized (nameFinder) {
						nameSpans = nfModel.find(tokens);
					}
					for (Span s : nameSpans) {
						int nameStartIndex = 0;
						int nameEndIndex = 0;
						for (int i = 0; i <= tokenSpans.length; i++) {
							if (i == s.getStart()) {
								nameStartIndex = tokenSpans[i].getStart() + sentenceSpan.getStart();
							} else if (i == s.getEnd()) {
								nameEndIndex = tokenSpans[i - 1].getEnd() + sentenceSpan.getStart();
							}
						}
						ArrayList<Integer> se = new ArrayList<Integer>();
						se.add(nameStartIndex);
						se.add(nameEndIndex);
						HashMap<String, Double> spanMap = entityMap.get(se);
						if (spanMap == null) {
							spanMap = new HashMap<String, Double>();
						}
						spanMap.put(s.getType(), s.getProb());
						entityMap.put(se, spanMap);
					}
				}

			}
			
			
			// now do the conll style printing
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
					if (entityMap.containsKey(tl)){
						if (entityMap.get(tl).keySet().size() == 1){
							for (String key : entityMap.get(tl).keySet()){
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
							// for now, just taking the one with the highest probability, not considering secondary types. (currently, this is essentially the same as what happens above, but keeping it separate for when I want to assign secondary types)
							Double highestProb = 0.0;
						    String finalType = null;
						    HashMap<String, Double> innerMap = entityMap.get(tl); 
						    for (String key : innerMap.keySet()) {
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
					else{
						prevType = "O";
						}
					String token = content.substring(tokenStartIndex, tokenEndIndex);
					out.write(token + "\t" + type + "\n");
					//System.out.println(token + "\t" + type);
				}
				out.write("\n");
				
				//System.out.println("\n");
			}
			System.out.println("Done.");
			out.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		
	}
	

}
