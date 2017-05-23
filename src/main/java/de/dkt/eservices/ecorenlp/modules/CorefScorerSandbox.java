package de.dkt.eservices.ecorenlp.modules;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.bag.SynchronizedSortedBag;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

public class CorefScorerSandbox {
	public static void main(String[] args) throws IOException {
		
		//-------------------extract gold mentions from document--------------------------------
		HashMap<String, String> goldMap= new HashMap<String,String>();
		//String file = "C:\\Users\\Sabine\\Desktop\\WörkWörk\\CorefEval\\tubaCorefScore.tsv";
		//String file = "C:\\Users\\Sabine\\Desktop\\WörkWörk\\CorefEval\\tubaDummy.txt";
		String file = "C:\\Users\\Sabine\\Desktop\\WörkWörk\\CorefEval\\tubaDummy2.txt";
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
				if(lastWord.contains(",")){
					String[] parts = lastWord.split(",");
					for(String part:parts){
						goldMap.put(sentAndWordNum, part);
					}
				}else{
				//put sentenceNumber+WordNumber as key and clusterIdentifier as value
				goldMap.put(sentAndWordNum, lastWord);
				}
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
		
		//goldClusters.forEach((k,v)->System.out.println("Key : " + k + " Value : " + v.toString()));
			
		//-------------------extract our mentions from document------------------------------------------
		TreeMap<String, String> ourMap= new TreeMap<String,String>();
		//String file2 = "C:\\Users\\Sabine\\Desktop\\WörkWörk\\CorefConll.txt";
		//String file2 = "C:\\Users\\Sabine\\Desktop\\WörkWörk\\corefConllS2.txt";
		String file2 = "C:\\Users\\Sabine\\Desktop\\WörkWörk\\CorefEval\\tubaDummy2.txt";
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
					if(lastWord.contains(",")){
						String[] parts = lastWord.split(",");
						for(String part:parts){
							ourMap.put(sentAndWordNum, part);
						}
					}else{
					//put sentenceNumber+WordNumber as key and clusterIdentifier as value
					ourMap.put(sentAndWordNum, lastWord);
					}
					}
	  
				}    
				br2.close(); 
				
				//ourMap.forEach((k,v)->System.out.println("Key : " + k + " Value : " + v));
				
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
		System.out.println("-----------------------------------------------------------------------------------");
		
		//B^3 
		//Recall
		double counter =0;
		double denominator=0;
		
	
		
		for(Entry<String, Set<String>> entryGold : goldClusters.entrySet()){
			denominator += entryGold.getValue().size();
			double maxOverlap=0;
			for(Entry<String, Set<String>> entryOur : ourClusters.entrySet()){
				
				Set<String> goldSet = entryGold.getValue();
				//System.out.println("ourSet: "+entryOur.getValue().toString());
				Set<String> overlapSet = new HashSet<String>();
				overlapSet.addAll(entryOur.getValue());
				//System.out.println("goldSet in Loop: "+overlapSet.toString());
				//System.out.println("ourSet in Loop: "+entryOur.getValue().toString());
				overlapSet.retainAll(goldSet);
				
				if(overlapSet.size()==entryOur.getValue().size()){
					double f = (overlapSet.size()*overlapSet.size());
					double e = goldSet.size();
					counter += f/e;
					break;
				}

				if(!overlapSet.isEmpty()&&overlapSet.size()>maxOverlap){
					double f = (overlapSet.size()*overlapSet.size());
					double e = goldSet.size();
					counter += f/e;
					maxOverlap+=overlapSet.size();
				}
			}
		}
		double B3Recall = counter/denominator;
		System.out.println("B3 Recall: "+B3Recall);
		//Precision
		
		double counter2 =0;
		double denominator2=0;
	
		
		for(Entry<String, Set<String>> entryOur : ourClusters.entrySet()){
			denominator2 += entryOur.getValue().size();
			double maxOverlap=0;
			for(Entry<String, Set<String>> entryGold : goldClusters.entrySet()){
				
				Set<String> goldSet = entryGold.getValue();
				//System.out.println("ourSet: "+entryOur.getValue().toString());
				Set<String> overlapSet = new HashSet<String>();
				overlapSet.addAll(entryOur.getValue());
				//System.out.println("goldSet in Loop: "+overlapSet.toString());
				//System.out.println("ourSet in Loop: "+entryOur.getValue().toString());
				overlapSet.retainAll(goldSet);

				if(overlapSet.size()==entryOur.getValue().size()){
					double f = (overlapSet.size()*overlapSet.size());
					double e = goldSet.size();
					counter2 += f/e;
					break;
				}

				if(!overlapSet.isEmpty()&&overlapSet.size()>maxOverlap){
					double f = (overlapSet.size()*overlapSet.size());
					double e = goldSet.size();
					counter2 += f/e;
					maxOverlap+=overlapSet.size();
				}
			}
		}
		
		double B3Precision = counter2/denominator2;
		System.out.println("B3 Precsion: "+B3Precision);
		double B3F1 = (2*B3Precision*B3Recall)/(B3Precision+B3Recall);
		System.out.println("B3F1 :"+B3F1);
		System.out.println("--------------------------------------------------------------------------");
		
		//CEAF
		//Recall
		double overlapSize=0;
		double keySetSize = 0;
	
		for(Entry<String, Set<String>> entryGold : goldClusters.entrySet()){
			keySetSize+=entryGold.getValue().size();
			int maxAlignment = 0;
			for(Entry<String, Set<String>> entryOur : ourClusters.entrySet()){
				//walk trough all the response sets and find the one with perfect alignment
				//if found, save overlap size. save key set size seperatly

				Set<String> goldSet = entryGold.getValue();
				//System.out.println("ourSet: "+entryOur.getValue().toString());
				Set<String> overlapSet = new HashSet<String>();
				overlapSet.addAll(entryOur.getValue());
				//System.out.println("goldSet in Loop: "+overlapSet.toString());
				//System.out.println("ourSet in Loop: "+entryOur.getValue().toString());
				overlapSet.retainAll(goldSet);

				if(!overlapSet.isEmpty()&&(overlapSet.size()>maxAlignment)){
					maxAlignment=overlapSet.size();
				}
			}if (maxAlignment!=0){
				overlapSize+=maxAlignment;
//				overlapSize+=1;
			}
		}
		double CEAFRecall=overlapSize/keySetSize;
		System.out.println("CEAF Recall: "+CEAFRecall);
		
		//Precision
		double overlapSize2=0;
		double keySetSize2 = 0;
	
		
		for(Entry<String, Set<String>> entryGold : goldClusters.entrySet()){
			int maxAlignment2 = 0;
			for(Entry<String, Set<String>> entryOur : ourClusters.entrySet()){
				//walk trough all the respose sets and find the one with perfect alignment
				//if found, save overlap size. save key set size seperatly

				Set<String> goldSet = entryGold.getValue();
				//System.out.println("ourSet: "+entryOur.getValue().toString());
				Set<String> overlapSet = new HashSet<String>();
				overlapSet.addAll(entryOur.getValue());
				//System.out.println("goldSet in Loop: "+overlapSet.toString());
				//System.out.println("ourSet in Loop: "+entryOur.getValue().toString());
				overlapSet.retainAll(goldSet);

				if(!overlapSet.isEmpty()&&(overlapSet.size()>maxAlignment2)){
					maxAlignment2=overlapSet.size();
				}
			}if (maxAlignment2!=0){
				overlapSize2+=maxAlignment2;
				//overlapSize2+=1;
			}
		}  
		
		for(Entry<String, Set<String>> entryOur : ourClusters.entrySet()){
			keySetSize2+=entryOur.getValue().size();
		}
		
		
		double CEAFPrecision=overlapSize2/keySetSize2;
		System.out.println("CEAF Precision: "+CEAFPrecision);
		double CEAFF1 = (2*CEAFPrecision*CEAFRecall)/(CEAFPrecision+CEAFRecall);
		System.out.println("CEAFF1 :"+CEAFF1);
		System.out.println("-----------------------------------------------------------------------------------------");
		
		
		//BLANC
		//creating the set of gold linked/unlinked mentions
		// to solve the out of memory error try sliding window
//		Set<Map<Integer,Integer>> linkedGoldPairs = new LinkedHashSet<Map<Integer, Integer>>();
//		Set<Map<Integer,Integer>> unlinkedGoldPairs = new HashSet<Map<Integer, Integer>>();
		
		@SuppressWarnings("unchecked")
		//Entry<String,String>[] values =  (Entry<String, String>[]) goldMap.entrySet().toArray(); // returns a Map.Entry<K,V>[]
//		String[] looplist = new String[goldMap.size()];
//		int counter = 0;
//		for (String entry : goldMap.keySet()){
//			looplist[counter]=entry;
//			counter++;
//		}
//		for (int i = 0; i < looplist.length; i++) {
//			  for (int j = i+1; j<looplist.length; j++) {
//			    if (goldMap.get(looplist[i]).equals(goldMap.get(looplist[j]))) {
//			    	
//			    	TreeMap<String,String> linkedPair = new TreeMap<String,String>();
//					linkedPair.put(looplist[i], looplist[j]);
//					linkedGoldPairs.add(linkedPair);
//			    }else{
//			    	TreeMap<String,String> linkedPair = new TreeMap<String,String>();
//			    	linkedPair.put(looplist[i], looplist[j]);
//					unlinkedGoldPairs.add(linkedPair);
//			    }
//			  }
//			}
		
		Set<Float[]> linkedGoldPairs = new LinkedHashSet<Float[]>();
		Set<Float[]> unlinkedGoldPairs = new HashSet<Float[]>();
		
		String[] looplist = new String[goldMap.size()];
		int counterx = 0;
		for (String entry : goldMap.keySet()){
			looplist[counterx]=entry;
			counterx++;
		}

		
		
		for (int i = 0; i < looplist.length; i++) {
			int key = i;
			  for (int j = i+1; j< Math.min(looplist.length, key+100); j++) {
			    if (goldMap.get(looplist[i]).equals(goldMap.get(looplist[j]))&&!looplist[i].equals("")&&!looplist[j].equals("")) {
			    	Float[] linkedPair = new Float[2];
					linkedPair[0]=Float.parseFloat(looplist[i].replaceAll("[^0-9]", "."));
					linkedPair[1]=Float.parseFloat(looplist[j].replaceAll("[^0-9]", "."));
					//System.out.println("Linked Pair: "+linkedPair.toString());
					linkedGoldPairs.add(linkedPair);
					//System.out.println("linkedGoldpairs: "+linkedGoldPairs.toString());
			    }if (!goldMap.get(looplist[i]).equals(goldMap.get(looplist[j]))&&!looplist[i].equals("")&&!looplist[j].equals("")){
			    	Float[] linkedPair = new Float[2];
					linkedPair[0]=Float.parseFloat(looplist[i].replaceAll("[^0-9]", "."));
					linkedPair[1]=Float.parseFloat(looplist[j].replaceAll("[^0-9]", "."));
					unlinkedGoldPairs.add(linkedPair);
			    }
			  }
			}
//		for (Integer[] in :linkedGoldPairs){
//			System.out.println(Arrays.deepToString(in));
//		}
		
//		for (int i = 0; i < values.length; i++) {
//		  for (int j = i+1; j<values.length; j++) {
//		    if (values[i].getValue().equals(values[j].getValue())) {
//		    	TreeMap<String,String> linkedPair = new TreeMap<String,String>();
//				linkedPair.put(values[i].getKey(), values[j].getKey());
//				linkedGoldPairs.add(linkedPair);
//		    }else{
//		    	TreeMap<String,String> linkedPair = new TreeMap<String,String>();
//				linkedPair.put(values[i].getKey(), values[j].getKey());
//				unlinkedGoldPairs.add(linkedPair);
//		    }
//		  }
//		}
		
//		for(Map.Entry<String, String> entry1: goldMap.entrySet()) {
//			   String key1 = entry1.getKey();
//			   int hash1 = System.identityHashCode(key1);
//			   String value1 = entry1.getValue();
//			   for(Map.Entry<String, String> entry2: goldMap.entrySet()) {
//			       String key2 = entry2.getKey();
//			       if (hash1> System.identityHashCode(key2)) continue;
//
//			       String value2 = entry2.getValue();
//			       if(value1.equals(value2)){
//			    	   TreeMap<String,String> linkedPair = new TreeMap<String,String>();
//						linkedPair.put(key1, key2);
//						linkedGoldPairs.add(linkedPair);
//			       }else{
//				    	TreeMap<String,String> linkedPair = new TreeMap<String,String>();
//						linkedPair.put(key1, key2);
//						unlinkedGoldPairs.add(linkedPair);
//				    }
//			   }
//			}
		
		
		//creating the set of gold linked/unlinked mentions
	Set<Float[]> linkedOurPairs = new HashSet<Float[]>();
	Set<Float[]> unlinkedOurPairs = new HashSet<Float[]>();
		
		
		String[] looplist2 = new String[ourMap.size()];
		int counter2x = 0;
		for (String entry : ourMap.keySet()){
			looplist2[counter2x]=entry;
			counter2x++;
		}

		for (int i = 0; i < looplist2.length; i++) {
			int key = i;
			  for (int j = i+1; j< Math.min(looplist2.length, key+100); j++) {

			    if (ourMap.get(looplist2[i]).equals(ourMap.get(looplist2[j]))&&!looplist2[i].equals("")&&!looplist2[j].equals("")) {
			    	Float[] linkedPair = new Float[2];
					linkedPair[0]=Float.parseFloat(looplist2[i].replaceAll("[^0-9]", "."));
					linkedPair[1]=Float.parseFloat(looplist2[j].replaceAll("[^0-9]", "."));
					linkedOurPairs.add(linkedPair);
			    }if (!ourMap.get(looplist2[i]).equals(ourMap.get(looplist2[j]))&&!looplist2[i].equals("")&&!looplist2[j].equals("")) {
			    	Float[] linkedPair = new Float[2];
					linkedPair[0]=Float.parseFloat(looplist2[i].replaceAll("[^0-9]", "."));
					linkedPair[1]=Float.parseFloat(looplist2[j].replaceAll("[^0-9]", "."));
					unlinkedOurPairs.add(linkedPair);
			    }
			  }
			}
//		for (Integer[] in :linkedOurPairs){
//			System.out.println(Arrays.deepToString(in));
//		}
		
//		@SuppressWarnings("unchecked")
//		Entry<String,String>[] values2 =  (Entry<String, String>[]) ourMap.entrySet().toArray(); // returns a Map.Entry<K,V>[]
//		for (int i = 0; i < values2.length; i++) {
//		  for (int j = i+1; j<values2.length; j++) {
//		    if (values2[i].getValue().equals(values2[j].getValue())) {
//		    	TreeMap<String,String> linkedPair = new TreeMap<String,String>();
//				linkedPair.put(values2[i].getKey(), values2[j].getKey());
//				linkedOurPairs.add(linkedPair);
//		    }else{
//		    	TreeMap<String,String> linkedPair = new TreeMap<String,String>();
//				linkedPair.put(values2[i].getKey(), values2[j].getKey());
//				unlinkedOurPairs.add(linkedPair);
//		    }
//		  }
//		}
		
//		for(Map.Entry<String, String> entry1: ourMap.entrySet()) {
//			   String key1 = entry1.getKey();
//			   int hash1 = System.identityHashCode(key1);
//			   String value1 = entry1.getValue();
//			   for(Map.Entry<String, String> entry2: ourMap.entrySet()) {
//			       String key2 = entry2.getKey();
//			       if (hash1> System.identityHashCode(key2)) continue;
//
//			       String value2 = entry2.getValue();
//			       if(value1.equals(value2)){
//			    	   TreeMap<String,String> linkedPair = new TreeMap<String,String>();
//						linkedPair.put(key1, key2);
//						linkedOurPairs.add(linkedPair);
//			       }else{
//				    	TreeMap<String,String> linkedPair = new TreeMap<String,String>();
//						linkedPair.put(key1, key2);
//						unlinkedOurPairs.add(linkedPair);
//				    }
//			   }
//			}
		
		//Recall Coreference
	float sharedPairs  = 0;
		for (Float[] entry1:linkedGoldPairs){
			for (Float[] entry2:linkedOurPairs){
				if((Math.abs(entry1[0] - entry2[0]) < 0.00000001)&&(Math.abs(entry1[1] - entry2[1]) < 0.00000001)){
				sharedPairs++;

			}
			}
		}
		
//		float unsharedPairs  = 0;
//		for (Float[] entry1:unlinkedGoldPairs){
//			for (Float[] entry2:unlinkedOurPairs){
//				if((Math.abs(entry1[0] - entry2[0]) < 0.00000001)&&(Math.abs(entry1[1] - entry2[1]) < 0.00000001)){
//				unsharedPairs++;
//
//			}
//			}
//		}
		System.out.println("Shared Pairs: "+sharedPairs);
		//System.out.println("Unshared Pairs: "+unsharedPairs);
		
		double recallCoreference = sharedPairs/linkedGoldPairs.size();
		System.out.println("Recall BLANC Coreference:"+recallCoreference);
		//double recallUncoreference = unsharedPairs/unlinkedGoldPairs.size();
		//System.out.println("Recall BLANC Uncoreference:"+recallUncoreference);
		//Precision
		double precisionCoreference = sharedPairs/linkedOurPairs.size();
		System.out.println("Precision BLANC Coreference: "+precisionCoreference);
		//double precisionUncoreference = unsharedPairs/unlinkedOurPairs.size();
		//System.out.println("Precision BLANC Uncoreference: "+precisionUncoreference);
		double BLANCF1 = ((2*precisionCoreference * recallCoreference)/precisionCoreference+ recallCoreference);
		System.out.println("BLANCF1 :"+BLANCF1);
	}
}
