package de.dkt.eservices.ecorenlp.modules;

import edu.stanford.nlp.hcoref.CorefCoreAnnotations;
import edu.stanford.nlp.hcoref.data.CorefChain;
import edu.stanford.nlp.hcoref.data.Mention;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;
import opennlp.tools.util.Span;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.io.IOUtils;

import de.dkt.eservices.eopennlp.modules.SentenceDetector;
import de.dkt.eservices.erattlesnakenlp.linguistic.SpanWord;

public class Corefinizer {
	
	 public static void main(String[] args) throws Exception {
		 findCoreferences("C:\\Users\\Sabine\\Desktop\\WörkWörk\\14cleaned.txt");
	  }
	 
	 public static void findCoreferences(String inputFile) throws IOException{
		 
		 //get the input file and make it a string
		 SpanWord span = getDocumentSpan(inputFile);
		 String everything = span.getText();
		 
		//start Lexical Parser out of the loop so it won't start for every sentence  
		 LexicalizedParser lexParser = LexicalizedParser.loadModel("edu/stanford/nlp/models/lexparser/germanPCFG.ser.gz","-maxLength", "70");
		 
		 //do sentence splitting here. SentenceMap maps a sentence to its number 
		 TreeMap<Integer,SpanWord> sentenceMap = new TreeMap<Integer,SpanWord>();
		 String sent = new String();
		 int sentenceCounter = 1;
		 
		 Span[] sentenceSpans = SentenceDetector.detectSentenceSpans(everything, "en-sent.bin");
		 for (Span sentenceSpan : sentenceSpans){
			 int sentenceStart = sentenceSpan.getStart();
		     int sentenceEnd = sentenceSpan.getEnd();
		     sent = everything.substring(sentenceStart, sentenceEnd);
		     SpanWord sentence = new SpanWord(sent,sentenceStart,sentenceEnd);
		     sentenceMap.put(sentenceCounter, sentence);
		     sentenceCounter++;
		 }
		 //get all the NPs and PPERs and make them into mentions. Pack the mentions in sentence-packages in the right order. 
		 //(Walk trough the mention and make each mention its own cluster)
		 TreeMap<Integer,LinkedHashSet<CorefMention>> sentenceOrderMap = new TreeMap<Integer,LinkedHashSet <CorefMention>>();
		 TreeMap<Integer,LinkedHashSet<CorefCluster>> sentenceOrderMapCluster = new TreeMap<Integer, LinkedHashSet<CorefCluster>>();
		 int IdCounter = 1;
		 for (Map.Entry<Integer, SpanWord> entry : sentenceMap.entrySet()){
	 
			 String sentence = entry.getValue().getText();
			 Tree tree = lexParser.parse(sentence);
			 TreeMap<Integer,CorefMention> rightOrderMap = sandbox.traverseBreadthFirst(tree);
			 LinkedHashSet<CorefMention> orderedValues = new LinkedHashSet<CorefMention>(rightOrderMap.values());
			 sentenceOrderMap.put(entry.getKey(),orderedValues);
			
			 
			 TreeMap<Integer,CorefCluster> rightOrderMapCluster = new TreeMap<Integer,CorefCluster>();
			 
			 for(Entry<Integer, CorefMention> mention : rightOrderMap.entrySet()){
				 Set<CorefMention> a = new LinkedHashSet<CorefMention>();
				 a.add(mention.getValue());
				 CorefCluster newCluster = new CorefCluster(IdCounter,a,mention.getValue());
				 rightOrderMapCluster.put(mention.getKey(), newCluster);
				 IdCounter++;
			 }
			 LinkedHashSet<CorefCluster> orderedClusters = new LinkedHashSet<CorefCluster>(rightOrderMapCluster.values());
			 sentenceOrderMapCluster.put(entry.getKey(), orderedClusters);
		 } 
		 
		 //Put a cluster in the first sieve. Let the sieve walk trough the antecedent mentions. Clusters may merge.
		
		 Iterator<Entry<Integer, LinkedHashSet<CorefCluster>>> it = sentenceOrderMapCluster.entrySet().iterator();
		 TreeMap<Integer, LinkedHashSet<CorefCluster>> sentenceOrderMapCluster2 = new TreeMap<Integer, LinkedHashSet<CorefCluster>>();
		 TreeMap<Integer, LinkedHashSet<CorefCluster>> sentenceOrderMapCluster3 = new TreeMap<Integer, LinkedHashSet<CorefCluster>>();
		 
		 for(Map.Entry<Integer, LinkedHashSet<CorefCluster>> entry : sentenceOrderMapCluster.entrySet()){
			 //look in same sentence
			 LinkedHashSet<CorefCluster> newSet = mergeClustersWithinSentence(entry.getValue());
			 

			 if (!newSet.equals(entry.getValue())){
				 sentenceOrderMapCluster2.put(entry.getKey(), newSet);

			 }else{
				 sentenceOrderMapCluster2.put(entry.getKey(), entry.getValue());

			 } 
		 }
		 for (Entry<Integer,LinkedHashSet<CorefCluster>> e : sentenceOrderMapCluster2.entrySet()){
			 System.out.println(e.getKey());
			 for(CorefCluster a : e.getValue()){
				 System.out.println(a.getClusterID()+" "+a.ClusterToString());
			 }
		 }
		 
		 //look in the antecedent sentence
//		 for(Map.Entry<Integer, LinkedHashSet<CorefCluster>> entry : sentenceOrderMapCluster2.entrySet()){
//			 LinkedHashSet<CorefCluster> newSet = new LinkedHashSet<CorefCluster>();
//
//			
//				LinkedHashSet<CorefCluster> nextSentenceClusters = sentenceOrderMapCluster2.lowerKey(key)
//				
//				nextSentenceClusters.addAll(entry.getValue());
//			
//				newSet = mergeClustersWithinSentence(nextSentenceClusters);
////				for (CorefCluster a : newSet){
////					System.out.println("DEBUG prevSent: "+a.ClusterToString());
////				}
//			
//
//			 if (newSet.equals(entry.getValue())){
//				 sentenceOrderMapCluster3.put(entry.getKey(), newSet);
//			 }else{
//				 sentenceOrderMapCluster3.put(entry.getKey(), entry.getValue());
//			 } 
//		 }
//		 
		 //Put the first cluster in the second sieve. Let the sieve walk trough antecedent mentions. And so on... 
		 
	 }
	 
	 public static SpanWord getDocumentSpan(String inputFile) throws IOException{
	    	
	    	String everything = new String();
	    	FileInputStream inputStream = new FileInputStream(inputFile);
	        try {
	            everything = IOUtils.toString(inputStream);
	            int docLength = everything.length();
	            ////System.out.println("document length :"+docLength);
	        } finally {
	            inputStream.close();
	        }
	        
	          SpanWord span = new SpanWord(everything, 0, everything.length());
	    	  ////System.out.println("DEBUG document span: "+span.getStartSpan()+" "+span.getEndSpan());
			return span;
	    	
	    }
	 
	 public static boolean sieveOne (CorefCluster one, CorefCluster two){
		
		 for(CorefMention a : one.getCorefMentions()){
			 for(CorefMention b : two.getCorefMentions()){
				if(a.getContents().equals(b.getContents())){
					return true;
					
				} 
			 }
		 }return false;
	 }
	 
	 public static CorefCluster mergeClusters (CorefCluster one, CorefCluster two){
//		 one.getCorefMentions().addAll(two.getCorefMentions());
		 Set<CorefMention> oneMention = one.getCorefMentions();
		 Set<CorefMention> twoMention = two.getCorefMentions();
		 Set<CorefMention> newSet = new LinkedHashSet<CorefMention>();
		 for (CorefMention m : oneMention){
			 newSet.add(m);
		 }
		 for (CorefMention n: twoMention){
			 newSet.add(n);
		 }
		 CorefCluster newCluster = new CorefCluster(one.getClusterID(),newSet ,one.getFirstMention());
		 return newCluster;
	 }
	 
	 public static LinkedHashSet<CorefCluster> mergeClustersWithinSentence(LinkedHashSet<CorefCluster> a){
		 if (a.size()==1){
			 //only one cluster, nothing to merge
			 return a;
		 }else{
		 CorefCluster[] array = new CorefCluster[a.size()];
		 CorefCluster[] otherArray = new CorefCluster[a.size()];
		 CorefCluster[] mergedArray = new CorefCluster[a.size()];
		 CorefMention empty =  new CorefMention(0, "empty", 0, 0);
		 CorefMention deleted = new CorefMention(0, "deleted", 0, 0);
		 Set<CorefMention> emptySet = new LinkedHashSet<CorefMention>();
		 emptySet.add(empty);
		 CorefCluster emptyCluster = new CorefCluster(0,emptySet,empty);
		 CorefCluster deletedCluster = new CorefCluster(0,emptySet,deleted);
		 for (int i=1; i<a.size();i++){
			mergedArray[i]= emptyCluster; 
		 }
		 a.toArray(array);
		  for(int i=1; i<a.size();i++){
			  for (int j=i-1; j>=0;j--){
				  if (sieveOne(array[i],array[j])){
					  CorefCluster newCluster = mergeClusters(array[j],array[i]);
					  mergedArray[j]=newCluster;
					  mergedArray[i]=deletedCluster;
//					  System.out.println(newCluster.ClusterToString()+" "+newCluster.getClusterID());
//					  System.out.println("if case, i: "+i+" j: "+j);
					  
				  }
				  else{
					  otherArray[j]=array[j];
					  otherArray[i]=array[i];
//					  System.out.println("else case, i: "+i+" j: "+j);
				  }
			  }
		  }
		LinkedHashSet<CorefCluster> newCorefClusterSet = new LinkedHashSet<CorefCluster>();
		for(int i=1; i<a.size();i++){
			if (mergedArray[i].equals(emptyCluster)){
				newCorefClusterSet.add(otherArray[i]);
//				System.out.println(otherArray[i].getClusterID()+" "+otherArray[i].ClusterToString());
			}else if (mergedArray[i].equals(deletedCluster)){
				//do nothing
			}
			else{
				newCorefClusterSet.add(mergedArray[i]);
//				System.out.println(mergedArray[i].getClusterID()+" "+mergedArray[i].ClusterToString());
			}
		}
 
		return newCorefClusterSet;
	 }
	 }

}
