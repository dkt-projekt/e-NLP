package de.dkt.eservices.ecorenlp.modules;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

public class CorefScorerSandbox {
	public static void main(String[] args) throws IOException {
		
		//-------------------extract gold mentions from document--------------------------------
		TreeMap<String, String> goldMap= new TreeMap<String,String>();
		String file = "C:\\Users\\Sabine\\Desktop\\WörkWörk\\CorefEval\\tubaCorefScore.tsv";
		//String file = "C:\\Users\\Sabine\\Desktop\\WörkWörk\\CorefEval\\tubaDummy.txt";
		BufferedReader br = new BufferedReader(new FileReader(file));  
		String line; 
		while ((line = br.readLine()) != null) {  
			
			// process the line. 
			String lastWord = line.substring(line.lastIndexOf("	")+1);
			//System.out.println("Last Word: "+lastWord);
			String sentAndWordNum = new String();
			
			Matcher matcher = Pattern.compile("\\d+\\t\\d+").matcher(line);
			if (matcher.find()){
				sentAndWordNum = matcher.group();
				//System.out.println("SentAndWordNum: "+sentAndWordNum);
				}
			
			if (!lastWord.equals("-")&&!lastWord.equals(" ")){
				//put sentenceNumber+WordNumber as key and clusterIdentifier as value
				goldMap.put(sentAndWordNum, lastWord);
				}
  
			}    
			br.close(); 
			
			//goldMap.forEach((k,v)->System.out.println("Key : " + k + " Value : " + v));
		
		//-------------------save each gold mention cluster with sentence numbers and word numbers------------------------
		TreeMap<String, Set<String>> goldClusters= new TreeMap<String,Set<String>>();
		
		for(Entry<String, String> entry : goldMap.entrySet()){
			if (goldClusters.containsKey(entry.getValue())){
				Set<String> values = goldClusters.get(entry.getValue());
				values.add(entry.getKey());
				goldClusters.put(entry.getValue(), values);
			}else{
				Set<String> values = new HashSet<String>();
				values.add(entry.getKey());
				goldClusters.put(entry.getValue(), values);
			}
		}
		
//		goldClusters.forEach((k,v)->System.out.println("Key : " + k + " Value : " + v.toString()));
			
		//-------------------extract our mentions from document------------------------------------------
		TreeMap<String, String> ourMap= new TreeMap<String,String>();
		String file2 = "C:\\Users\\Sabine\\Desktop\\WörkWörk\\CorefConll.txt";
		//String file2 = "C:\\Users\\Sabine\\Desktop\\WörkWörk\\CorefEval\\ourDummy.txt";
		BufferedReader br2 = new BufferedReader(new FileReader(file2));  
		String line2; 
			while ((line2 = br2.readLine()) != null) {  
				
				// process the line. 
				String lastWord = line2.substring(line2.lastIndexOf("	")+1);
				//System.out.println("Last Word: "+lastWord);
				String sentAndWordNum = new String();
				
				Matcher matcher = Pattern.compile("\\d+\\t\\d+").matcher(line2);
				if (matcher.find()){
					sentAndWordNum = matcher.group();
					//System.out.println("SentAndWordNum: "+sentAndWordNum);
					}
				
				if (!lastWord.equals("-")&&!lastWord.equals(" ")){
					//put sentenceNumber+WordNumber as key and clusterIdentifier as value
					ourMap.put(sentAndWordNum, lastWord);
					}
	  
				}    
				br2.close(); 
				
				//ourMap.forEach((k,v)->out.println("Key : " + k + " Value : " + v));
				
		//-------------------save each of our mention clusters with sentence numbers and word numbers-----------------------
		TreeMap<String, Set<String>> ourClusters= new TreeMap<String,Set<String>>();
				
		for(Entry<String, String> entry : ourMap.entrySet()){
			if (ourClusters.containsKey(entry.getValue())){
						Set<String> values = ourClusters.get(entry.getValue());
						values.add(entry.getKey());
						ourClusters.put(entry.getValue(), values);
			}else{
						Set<String> values = new HashSet<String>();
						values.add(entry.getKey());
						ourClusters.put(entry.getValue(), values);
			}
		}
				
				//ourClusters.forEach((k,v)->System.out.println("Key : " + k + " Value : " + v.toString()));
		
		//-------------------Implement different mentrics--------------------------------
		//MUC
		//Recall
		double counterSum = 0;
		double denominatorSum = 0;
		
		for(Entry<String, Set<String>> entryGold : goldClusters.entrySet()){
			int wordNumInGold = entryGold.getValue().size();
			int partitions = 0;
			Set<String> goldSet = entryGold.getValue();

			for(Entry<String, Set<String>> entryOur : ourClusters.entrySet()){

				Set<String> ourSet = entryOur.getValue();
				//System.out.println("ourSet: "+ourSet.toString());
				Set<String> overlapSet = new HashSet<String>();
				overlapSet.addAll(goldSet);
//				System.out.println("goldSet in Loop: "+overlapSet.toString());
//				System.out.println("ourSet in Loop: "+ourSet);
				overlapSet.retainAll(ourSet);

				if(!overlapSet.isEmpty()){
					partitions++;
//					System.out.println("PARTIAL OVERLAP");
//					System.out.println("overlapSet: "+overlapSet.toString());
				}
			}
			
			if (partitions>wordNumInGold){
				partitions=wordNumInGold;
			}
			
			if (partitions==0){
				partitions=wordNumInGold;
			}
			
//			if (partitions!=0){
				counterSum+=(wordNumInGold-partitions);
				denominatorSum+=(wordNumInGold-1);
//				System.out.println("counterSum: "+counterSum);
//				System.out.println("denominatorSum: "+denominatorSum);
//			}
			
		}
//		System.out.println("Final counterSum: "+counterSum);
//		System.out.println("Final denominatorSum: "+denominatorSum);
		double MUCRecall = counterSum/denominatorSum;
		System.out.println("MUCRecall: "+MUCRecall);
		
		//Precision
		double counterSum2 = 0;
		double denominatorSum2 = 0;
		
		for(Entry<String, Set<String>> entryOur : ourClusters.entrySet()){
			int wordNumInOur = entryOur.getValue().size();
			int partitions = 0;
			Set<String> ourSet = entryOur.getValue();

			for(Entry<String, Set<String>> entryGold : goldClusters.entrySet()){

				Set<String> goldSet = entryGold.getValue();
				//System.out.println("ourSet: "+ourSet.toString());
				Set<String> overlapSet = new HashSet<String>();
				overlapSet.addAll(ourSet);
//				System.out.println("goldSet in Loop: "+overlapSet.toString());
//				System.out.println("ourSet in Loop: "+ourSet);
				overlapSet.retainAll(goldSet);

				if(!overlapSet.isEmpty()){
					partitions++;
//					System.out.println("PARTIAL OVERLAP");
//					System.out.println("overlapSet: "+overlapSet.toString());
				}
			}
			
			if (partitions>wordNumInOur){
				partitions=wordNumInOur;
			}
			
			if (partitions==0){
				partitions=wordNumInOur;
			}
			
//			if (partitions!=0){
				counterSum2+=(wordNumInOur-partitions);
				denominatorSum2+=(wordNumInOur-1);
//				System.out.println("counterSum: "+counterSum);
//				System.out.println("denominatorSum: "+denominatorSum);
//			}
			
		}
//		System.out.println("Final counterSum: "+counterSum);
//		System.out.println("Final denominatorSum: "+denominatorSum);
		double MUCPrecision = counterSum2/denominatorSum2;
		System.out.println("MUCPrecision: "+MUCPrecision);
		double MUCF1 = (2*MUCPrecision*MUCRecall)/(MUCPrecision+MUCRecall);
		System.out.println("MUCF1 :"+MUCF1);
		
		//B^3 
		//Recall
		//Precision
		
		//CEAF
		//Recall
		//Precision
		
		//BLANC
		//Recall
		//Precision
		
	}
}
