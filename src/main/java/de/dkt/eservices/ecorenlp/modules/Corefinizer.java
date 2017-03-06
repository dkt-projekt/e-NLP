package de.dkt.eservices.ecorenlp.modules;

import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.trees.LabeledScoredTreeNode;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.trees.tregex.TregexPattern;
import edu.stanford.nlp.util.Pair;
import edu.stanford.nlp.dcoref.MentionExtractor;
import eu.freme.common.conversion.rdf.RDFConstants.RDFSerialization;
import opennlp.tools.util.Span;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.io.IOUtils;

import com.hp.hpl.jena.rdf.model.Model;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

import de.dkt.common.niftools.NIFReader;
import de.dkt.eservices.eopennlp.modules.SentenceDetector;
import de.dkt.eservices.erattlesnakenlp.linguistic.SpanWord;
import edu.stanford.nlp.ling.Label;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.trees.Dependency;
import edu.stanford.nlp.trees.LabeledScoredTreeNode;
import edu.stanford.nlp.trees.Tree;
import eu.freme.common.conversion.rdf.RDFConstants.RDFSerialization;
import opennlp.tools.util.Span;

@SuppressWarnings("deprecation")
public class Corefinizer {
	
	 public static TreeMap<Integer, CorefCluster> clusterIdMap = new TreeMap<Integer, CorefCluster>();
	 public static TreeMap<Integer,LinkedHashSet<CorefMention>> sentenceOrderMap = new TreeMap<Integer,LinkedHashSet <CorefMention>>();
	 public static TreeMap<Integer,SpanWord> sentenceMap = new TreeMap<Integer,SpanWord>();
	 public static TreeMap<Integer,LinkedHashSet<SpanWord>> wordSpanMap = new TreeMap<>();
	 
	//start Lexical Parser out of the loop so it won't start for every sentence  
	 public static LexicalizedParser lexParser = LexicalizedParser.loadModel("edu/stanford/nlp/models/lexparser/germanPCFG.ser.gz","-maxLength", "70");
	 
	
	 public static void main(String[] args) throws Exception {
		 
//		 LexicalizedParser lexParser = LexicalizedParser.loadModel("edu/stanford/nlp/models/lexparser/germanPCFG.ser.gz","-maxLength", "70");
//		 Tree tree = lexParser.parse("Tina Turner, eine Sängerin, besuchte am Montag Berlin.");
//		 tree.pennPrint();
		 
//		 for (Dependency<Label,Label,Object> dep : tree.dep){
//			 System.out.println(dep.dependent());
//		 }
		 
		 findCoreferences("C:\\Users\\Sabine\\Desktop\\WörkWörk\\relative.txt");
		 //findCoreferences("C:\\Users\\Sabine\\Desktop\\WörkWörk\\14cleaned.txt");
		 //findCoreferences("C:\\Users\\Sabine\\Desktop\\WörkWörk\\annotations\\7.txt");
		 //dummy("Im letzten Moment gibt es noch Hoffnung für die Männer und Frauen");
		 //otherDummy();
		 //getNamedEntityIndexes("Harry Potter. Tina Turner. Die Vereinten Nationen. Der FC Bayern München. Barack Obama. Berlin.");
		 
	  }
	 
	 public static void dummy(String string){
		 LexicalizedParser lexParser = LexicalizedParser.loadModel("edu/stanford/nlp/models/lexparser/germanPCFG.ser.gz","-maxLength", "70");
		 Tree tree = lexParser.parse(string);
		 HashMap<Span, String> npHash = new HashMap<>();
		 
		 Object[] a = tree.toArray();
     	ArrayList<ArrayList<String>> nps = new ArrayList<ArrayList<String>>();
     	for (Object s : a){
     		LabeledScoredTreeNode t = (LabeledScoredTreeNode)s;
     		if (t.label().toString().equalsIgnoreCase("np")||t.label().toString().equalsIgnoreCase("pper")){
     			//if (t.label().toString().equalsIgnoreCase("np")){
     			ArrayList<String> npAsList = new ArrayList<String>();
     			for (Tree it : t.flatten()){
     				if ((it.isLeaf())){
     					npAsList.add(it.pennString().trim());
     					System.out.println("Word: "+it.pennString().trim());
     					System.out.println("Span label: "+it.getSpan());
     				}
     			}
     		
     			nps.add(npAsList);
     		}
     	}
		 
//		 npHash = CorefUtils.traverseTreeForNPs(tree, npHash);
//		 npHash.forEach((k,v)->System.out.println("DEBUG traverseTreeForNPs key :"+k.getStart()+"value: "+v));
//		 System.out.println("------------------------------------------------------");
//		 String a = CorefUtils.determineHead(tree);
//		 System.out.println("DEBUG determineHead: "+a);
	 }
	 
	 public static void otherDummy() throws Exception{
//	 Model nifModel = EOpenNLPService.analyze("Hello World", "de", "ner", "<aij_wikiner_de>",  RDFSerialization.TURTLE, "spot", "http:www.dfki.de/sabine");
//
//				 String isstr = NIFReader.extractIsString(nifModel);
				
//				 //[<3, 7>, <15, 18>]
		 String inputString = "Harry Potter. Tina Turner. Die Vereinten Nationen. Der FC Bayern München. Barack Obama. Berlin.";
	
		 HttpResponse<String> response = Unirest.post("https://dev.digitale-kuratierung.de/api/e-nlp/namedEntityRecognition")
//			     .queryString("input", inputString)
			     .queryString("models", "ner-de_aij-wikinerTrainLOC;ner-de_aij-wikinerTrainPER;ner-de_aij-wikinerTrainORG" )
			     .queryString("analysis", "ner")
			     .queryString("language", "de")
			     .queryString("mode", "spot")
			     .body(inputString)
			     .asString();
//			   if(response.getStatus()!=200){
//			    logger.error("ERROR storing interaction for "+objectId);
//
//			   }
			   String nifString = response.getBody();
			   System.out.println(nifString);
			   Model nifModel = NIFReader.extractModelFromFormatString(nifString, RDFSerialization.TURTLE);
			   List<String[]> entityMap = NIFReader.extractEntityIndices(nifModel);
			   
			   for(String[] index: entityMap){
				   System.out.println("Entity indexes: "+Arrays.deepToString(index));
			   }
	 }
	 
	 public static LinkedHashSet<String[]> getNamedEntityIndexes(String inputString) throws Exception{
			
		 HttpResponse<String> response = Unirest.post("https://dev.digitale-kuratierung.de/api/e-nlp/namedEntityRecognition")
			     .queryString("models", "ner-de_aij-wikinerTrainLOC;ner-de_aij-wikinerTrainPER;ner-de_aij-wikinerTrainORG" )
			     .queryString("analysis", "ner")
			     .queryString("language", "de")
			     .queryString("mode", "spot")
			     .body(inputString)
			     .asString();

			   String nifString = response.getBody();
			   System.out.println(nifString);
			   Model nifModel = NIFReader.extractModelFromFormatString(nifString, RDFSerialization.TURTLE);
			   List<String[]> entityMap = NIFReader.extractEntityIndices(nifModel);
			   
			   LinkedHashSet<String[]> entityIndexes = new LinkedHashSet<>(); 

			   if (!(entityMap==null)){
				   for(String[] index: entityMap){
					   String[]	indexes = new String[3];
					   indexes[0]=index[3];
					   indexes[1]=index[4];
				   
					   	if(index[2].contains("Organisation")){
					   		indexes[2]="org";
					   	}if(index[2].contains("Person")){
					   		indexes[2]="per";
					   	}if(index[2].contains("Location")){
					   		indexes[2]="loc";
					   	}
					   entityIndexes.add(indexes);
				   }
			   }
			   
		 return entityIndexes;
	 }
	 
	 public static void findCoreferences(String inputFile) throws Exception{
		 //get the input file and make it a string
		 SpanWord span = getDocumentSpan(inputFile);
		 String everything = span.getText();
		 
		 
		
		 //do sentence splitting here. SentenceMap maps a sentence to its number 
		
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
		 
		 TreeMap<Integer,LinkedHashSet<CorefCluster>> sentenceOrderMapCluster = new TreeMap<Integer, LinkedHashSet<CorefCluster>>();

		 int IdCounter = 1;
		 
		 //everything that happens in this loop works per sentence
		 for (Map.Entry<Integer, SpanWord> entry : sentenceMap.entrySet()){
	 
			 //clean text before giving it to the parser to avoid errors with quotation marks and brackets later
			 //String sentence = entry.getValue().getText().replace("\"", "").replace("(", "").replace(")", "").replace("/", "").replaceAll("-", "");
			 String sentence = entry.getValue().getText();
			 Tree tree = lexParser.parse(sentence);
			 tree.pennPrint();

			 
			 //this is where the mentions get all the mention information 
			 //TODO: breadth first traversal is not exactly the same as in Standford NLP, check that again!
			 TreeMap<Integer,CorefMention> rightOrderMap = CorefUtils.traverseBreadthFirst(tree, entry.getValue());
			 LinkedHashSet<CorefMention> orderedValues = new LinkedHashSet<CorefMention>(rightOrderMap.values());
//			 orderedValues.forEach(k->System.out.println("DEBUG INDEXES:"+k.getContents()+"|"+k.getStartIndex()+"|"+k.getEndIndex()+
//					 k.getGender()+"|"+k.getNumber()));
			 
			//here the word spans are added
//			 LinkedHashSet<SpanWord> indexedWords = CorefUtils.getWordSpans(orderedValues, entry.getValue());
//			 LinkedHashSet<CorefMention> newOrderedValues = new LinkedHashSet<>();
//			 
//			 if(orderedValues.size()==indexedWords.size()){
//				 CorefMention[] corefArray = new CorefMention[orderedValues.size()];
//				 SpanWord[]	spanArray = new SpanWord[orderedValues.size()];
//				 orderedValues.toArray(corefArray);
//				 indexedWords.toArray(spanArray);
//				 for (int i = 0; i<orderedValues.size(); i++){
//					 corefArray[i].setStartIndex(spanArray[i].getStartSpan());
//					 corefArray[i].setEndIndex(spanArray[i].getEndSpan());
//					 newOrderedValues.add(corefArray[i]);
//				 }
//			 }else{
//				 System.out.println("Something went wrong with the indexing!!!");
//				 System.out.println("ordered Values size: "+orderedValues.size()+" indexedWords size: "+indexedWords.size());
//				 System.out.println("Input sentence: "+entry.getValue().getText());
//				 orderedValues.forEach((k)->System.out.println("input : " +k.getContents()));
//				 indexedWords.forEach((k)->System.out.println("output : " +k.getText()));
//			 }
			 
			 //put sentence numbers and indexes into the mentions
			 for (CorefMention ment : orderedValues){
				ment.setSentenceNumber(entry.getKey());
			 }
			
			 
			 sentenceOrderMap.put(entry.getKey(),orderedValues);
			
			 
			 TreeMap<Integer,CorefCluster> rightOrderMapCluster = new TreeMap<Integer,CorefCluster>();
			 LinkedHashSet<CorefMention> mentionSet = new LinkedHashSet<>();
			 
			 for(Entry<Integer, CorefMention> mention : rightOrderMap.entrySet()){
				 mention.getValue().setClusterID(IdCounter);
				 
				 Set<CorefMention> a = new LinkedHashSet<CorefMention>();
				 Set<String> genders = new HashSet<>();
				 Set<String> numbers = new HashSet<>();
				 genders.add(mention.getValue().getGender());
				 numbers.add(mention.getValue().getNumber());
				 a.add(mention.getValue());
				 mentionSet.add(mention.getValue());
				 CorefCluster newCluster = new CorefCluster(IdCounter,a,mention.getValue(), genders, numbers, mention.getValue().getPerson());
				 rightOrderMapCluster.put(mention.getKey(), newCluster);
				 
				 //this map is important because this is where the clusters will merge!
				 clusterIdMap.put(IdCounter,newCluster);
				 
				 

				 IdCounter++;
			 }
			 LinkedHashSet<CorefCluster> orderedClusters = new LinkedHashSet<CorefCluster>(rightOrderMapCluster.values());
			 sentenceOrderMapCluster.put(entry.getKey(), orderedClusters);
			 wordSpanMap.put(entry.getKey(), CorefUtils.getWordSpans(mentionSet, entry.getValue()));
			 
//			 for (Entry<Integer,LinkedHashSet<SpanWord>> f : wordSpanMap.entrySet()){
//				 System.out.println("word spans: ");
//				 for (SpanWord word : f.getValue()){
//					 System.out.println(word.getText()+ " "+word.getStartSpan()+" "+ word.getEndSpan());
//				 }
//			 }
			 
//			 for(Entry<Integer,LinkedHashSet<CorefMention>> ment : sentenceOrderMap.entrySet()){
//				 System.out.println("----------------------------------------------------------");
//				 for(CorefMention c : ment.getValue()){
//					 System.out.println(c.getContents()+" index: "+c.getStartIndex()+"-"+c.getEndIndex());
//				 }
//			 }
		 } 
		 
		 //Put the NER tags into the Coref Mentions
		 LinkedHashSet<String[]> indexList = getNamedEntityIndexes(everything);
		 
		 for(Entry<Integer,LinkedHashSet<CorefMention>> entry : sentenceOrderMap.entrySet()){
			 	for(CorefMention ment : entry.getValue()){
			 		for(String[] indexes : indexList){
			 			int nerStartIndex = Integer.parseInt(indexes[0]);
			 			int nerEndIndex = Integer.parseInt(indexes[1]);
			 			if(nerStartIndex>=ment.getStartIndex()&&nerEndIndex<=ment.getEndIndex()){
			 				ment.setNerTags(indexes);
			 			}
			 		}
			 	}
		 }
		 

		 
		 //Put a cluster in the first sieve. Let the sieve walk trough the antecedent mentions. Clusters may merge.
		
//		 TreeMap<Integer, LinkedHashSet<CorefCluster>> sentenceOrderMapCluster2 = new TreeMap<Integer, LinkedHashSet<CorefCluster>>();
//		 
//		 for(Map.Entry<Integer, LinkedHashSet<CorefCluster>> entry : sentenceOrderMapCluster.entrySet()){
//			 //look in same sentence
//			 LinkedHashSet<CorefCluster> newSet = mergeClustersWithinSentence(entry.getValue());
//			 
//
//			 if (!newSet.equals(entry.getValue())){
//				 sentenceOrderMapCluster2.put(entry.getKey(), newSet);
//
//			 }else{
//				 
//				 sentenceOrderMapCluster2.put(entry.getKey(), entry.getValue());
//
//			 } 
//		 }
//		 
//		 		 
//		 //look in the antecedent sentence
//		 LinkedHashSet<CorefCluster> noDuplicatesSet = new LinkedHashSet<CorefCluster>();
//		 LinkedHashSet<CorefCluster> newSet = new LinkedHashSet<CorefCluster>();
//		 
//		 for(Map.Entry<Integer, LinkedHashSet<CorefCluster>> entry : sentenceOrderMapCluster2.entrySet()){
//			
//				LinkedHashSet<CorefCluster> prevSentenceClusters = new LinkedHashSet<CorefCluster>();
//				if (!entry.getKey().equals(1)){
//				int k =	sentenceOrderMapCluster2.lowerKey(entry.getKey());
//				prevSentenceClusters = sentenceOrderMapCluster2.get(k);
//				
//				prevSentenceClusters.addAll(entry.getValue());
//			
//				newSet = mergeClustersWithinSentence(prevSentenceClusters);
//				noDuplicatesSet.addAll(newSet);
//				
//
//				}
//				
//			 
//		}
//		 
//		 noDuplicatesSet = filterDuplicates(noDuplicatesSet);
		 
		 
		 
//////////// THE SIEVES START HERE //////////////////////////////////////////////////////////////////////////////////////// 		 
		 //better version of sieve one 
		 // first look in the same sentence
		 for  (Entry<Integer, LinkedHashSet<CorefMention>> a : sentenceOrderMap.entrySet()){
			  //compare mentions within one sentence, merge clusters when needed

			  compareMentionsWithinSentence(a.getValue(), 1);
			  
		
		  }
		  
		  //first sieve, look in antecedent sentence

		  for(Map.Entry<Integer, LinkedHashSet<CorefMention>> entry : sentenceOrderMap.entrySet()){
				
				LinkedHashSet<CorefMention> prevSentenceMentions = new LinkedHashSet<CorefMention>();
				if (!entry.getKey().equals(1)){
				int k =	sentenceOrderMap.lowerKey(entry.getKey());
				prevSentenceMentions = sentenceOrderMap.get(k);
				
				prevSentenceMentions.addAll(entry.getValue());
			
				compareMentionsWithinSentence(prevSentenceMentions, 1);

				}
				
		} 
		 

		 //Second sieve, look in same sentence
		  for  (Entry<Integer, LinkedHashSet<CorefMention>> a : sentenceOrderMap.entrySet()){
			  //compare mentions within one sentence, merge clusters when needed

			  compareMentionsWithinSentence(a.getValue(), 2);
			  
		
		  }
		  
		  //second sieve, look in antecedent sentence

		  for(Map.Entry<Integer, LinkedHashSet<CorefMention>> entry : sentenceOrderMap.entrySet()){
				
				LinkedHashSet<CorefMention> prevSentenceMentions = new LinkedHashSet<CorefMention>();
				if (!entry.getKey().equals(1)){
				int k =	sentenceOrderMap.lowerKey(entry.getKey());
				prevSentenceMentions = sentenceOrderMap.get(k);
				
				prevSentenceMentions.addAll(entry.getValue());
			
				compareMentionsWithinSentence(prevSentenceMentions, 2);

				}
				
		} 
		  
		  //Third sieve, look in same sentence
		  for  (Entry<Integer, LinkedHashSet<CorefMention>> a : sentenceOrderMap.entrySet()){
			  //compare mentions within one sentence, merge clusters when needed

			  compareMentionsWithinSentence(a.getValue(), 3);
			  
		
		  }
		  
		  //third sieve, look in antecedent sentence

		  for(Map.Entry<Integer, LinkedHashSet<CorefMention>> entry : sentenceOrderMap.entrySet()){
				
				LinkedHashSet<CorefMention> prevSentenceMentions = new LinkedHashSet<CorefMention>();
				if (!entry.getKey().equals(1)){
				int k =	sentenceOrderMap.lowerKey(entry.getKey());
				prevSentenceMentions = sentenceOrderMap.get(k);
				
				prevSentenceMentions.addAll(entry.getValue());
			
				compareMentionsWithinSentence(prevSentenceMentions, 3);

				}
				
		}
		  
		  //Fourth sieve, look in same sentence
		  for  (Entry<Integer, LinkedHashSet<CorefMention>> a : sentenceOrderMap.entrySet()){
			  //compare mentions within one sentence, merge clusters when needed

			  compareMentionsWithinSentence(a.getValue(), 4); 
			  
		
		  }
		  
		  //fourth sieve, look in antecedent sentence

		  for(Map.Entry<Integer, LinkedHashSet<CorefMention>> entry : sentenceOrderMap.entrySet()){
				
				LinkedHashSet<CorefMention> prevSentenceMentions = new LinkedHashSet<CorefMention>();
				if (!entry.getKey().equals(1)){
				int k =	sentenceOrderMap.lowerKey(entry.getKey());
				prevSentenceMentions = sentenceOrderMap.get(k);
				
				prevSentenceMentions.addAll(entry.getValue());
			
				compareMentionsWithinSentence(prevSentenceMentions, 4);

				}
				
		}
		  
		//Fifth sieve, look in same sentence
		  for  (Entry<Integer, LinkedHashSet<CorefMention>> a : sentenceOrderMap.entrySet()){
			  //compare mentions within one sentence, merge clusters when needed

			  compareMentionsWithinSentence(a.getValue(), 5); 
			  
		
		  }
		  
		  //Fifth sieve, look in antecedent sentence

		  for(Map.Entry<Integer, LinkedHashSet<CorefMention>> entry : sentenceOrderMap.entrySet()){
				
				LinkedHashSet<CorefMention> prevSentenceMentions = new LinkedHashSet<CorefMention>();
				if (!entry.getKey().equals(1)){
				int k =	sentenceOrderMap.lowerKey(entry.getKey());
				prevSentenceMentions = sentenceOrderMap.get(k);
				
				prevSentenceMentions.addAll(entry.getValue());
			
				compareMentionsWithinSentence(prevSentenceMentions, 5);

				}
				
		}
		  
		//sixth sieve, look in same sentence
		  for  (Entry<Integer, LinkedHashSet<CorefMention>> a : sentenceOrderMap.entrySet()){
			  //compare mentions within one sentence, merge clusters when needed

			  compareMentionsWithinSentence(a.getValue(), 6); 
			  
		
		  }
		  
		  //sixth sieve, look in antecedent sentence

		  for(Map.Entry<Integer, LinkedHashSet<CorefMention>> entry : sentenceOrderMap.entrySet()){
				
				LinkedHashSet<CorefMention> prevSentenceMentions = new LinkedHashSet<CorefMention>();
				if (!entry.getKey().equals(1)){
				int k =	sentenceOrderMap.lowerKey(entry.getKey());
				prevSentenceMentions = sentenceOrderMap.get(k);
				
				prevSentenceMentions.addAll(entry.getValue());
			
				compareMentionsWithinSentence(prevSentenceMentions, 6);

				}
				
		}
		  
//		  for (Entry<Integer,LinkedHashSet<CorefMention>> entry : sentenceOrderMap.entrySet()){
//			  for (CorefMention mention : entry.getValue()){
//				  System.out.println("mention: "+mention.getContents()+" index: "+mention.getStartIndex()+"-"+mention.getEndIndex());
//			  }
//		  }
		  
		  for (Entry<Integer,CorefCluster> a : clusterIdMap.entrySet()){
			System.out.println(a.toString());
			Set<CorefMention> z = a.getValue().getCorefMentions();
			for (CorefMention f : z){
				System.out.println("MentionId: "+f.getMentionID()+"; Mention: "+f.getContents()+"; head: "+f.getHead()
				+" Index: "+f.getStartIndex
				()+"-"+f.getEndIndex());
			}
		}
		 

	 }
	 
		 
	
	 public static void compareMentionsWithinSentence(LinkedHashSet<CorefMention> a, int sieveNumber) throws FileNotFoundException{


		 CorefMention[] array = new CorefMention[a.size()];
		 if (!(a.size()==1)){
			 //only one cluster, nothing to merge
			 //return a;
		 //}else{
		 a.toArray(array);
		 if(sieveNumber==1){
		 for(int i=1; i<a.size();i++){
			  for (int j=i-1; j>=0;j--){
				  if (sieveOne(array[i],array[j])){
					  mergeClusters(array[j],array[i]); 
					  System.out.println("SIEVE ONE: "+array[i].getContents()+" "+array[j].getContents());
				  }
			  }
		  }
		 }
		 else if(sieveNumber==2){
			 for(int i=1; i<a.size();i++){
				  for (int j=i-1; j>=0;j--){
					  if (sieveTwo(array[i],array[j])){
//						  System.out.println("one: "+array[i].getClusterID()+" "+array[i].getContents()+"two :"+array[j].getClusterID()+" "+array[j].getContents());
						  mergeClusters(array[j],array[i]);
						  System.out.println("SIEVE TWO: "+array[i].getContents()+" "+array[j].getContents());
 
					  }
				  }
			  }
			 }
		 else if(sieveNumber==3){
			 for(int i=1; i<a.size();i++){
				  for (int j=i-1; j>=0;j--){
					  if (sieveThree(array[i],array[j])){
						  mergeClusters(array[j],array[i]);
						  System.out.println("SIEVE THREE: "+array[i].getContents()+" "+array[j].getContents());
					  }
				  }
			  }
			 }
		 
		 else if(sieveNumber==4){
			 for(int i=1; i<a.size();i++){
				  for (int j=i-1; j>=0;j--){
					  if (sieveFour(array[i],array[j])){
						  mergeClusters(array[j],array[i]);
						  System.out.println("SIEVE FOUR: "+array[i].getContents()+" "+array[j].getContents());
						  
 
					  }
				  }
			  }
			 }
		 else if(sieveNumber==5){
			 for(int i=1; i<a.size();i++){
				  for (int j=i-1; j>=0;j--){
					  if (sieveFive(array[i],array[j])){
						  mergeClusters(array[j],array[i]);
						  System.out.println("SIEVE FIVE: "+array[i].getContents()+" "+array[j].getContents());
 
					  }
				  }
			  }
			 }
		 
		 else if(sieveNumber==6){
			 for(int i=1; i<a.size();i++){
				  for (int j=i-1; j>=0;j--){
					  if (sieveSix(array[i],array[j])){
						  mergeClusters(array[j],array[i]);
						  System.out.println("SIEVE SIX: "+array[i].getContents()+" "+array[j].getContents());
 
					  }
				  }
			  }
			 }
			 }
//		 LinkedHashSet<CorefMention> newCorefMentionSet = new LinkedHashSet<CorefMention>();
//		 for (CorefMention m : array){
//			 newCorefMentionSet.add(m);
//		 }
//		return newCorefMentionSet;
		 
	 }
	 
	
	 
	 // sieve two is the strict head match:
	 //(one is the candidate and two is its antecedent)
	 //1. the mention head matches any mention in the antecedent cluster. 
	 //2. all  the  non-stop words  in  the mention cluster are included in the set of non-stop words  in  
	 //the  cluster  of  the  antecedent  candidate
	 //3.The mention’s modifiers  are  all  included  in  the  modifiers  of  the  antecedent  candidate
	 //4.the two mentions are not in an i-within-i  construct,  i.e.,  one  cannot  be  a  child  NP in the other’s NP constituent
	 public static boolean sieveTwo(CorefMention one, CorefMention two){
		 
		 boolean first = false;
		 boolean second = false;
		 boolean third = false;
		 boolean fourth = false;
		 boolean retVal = false;
		
		 
		 //first constraint, the mention head matches any mention in the antecedent cluster. 
		 String oneHead = one.getHead();
		 CorefCluster clusterOfTwo = clusterIdMap.get(two.getClusterID());
		 String twoClusterContents = clusterOfTwo.getContentsOfClusterAsString();
		 
		 if(twoClusterContents.matches(".*\\b"+oneHead+"\\b.*")){
			 first = true;
		 }
		 
		 //second constraint,all  the  non-stop words  in  the mention cluster are included in the set of non-stop words  in  
		 //the  cluster  of  the  antecedent  candidate
		 CorefCluster clusterOfOne = clusterIdMap.get(one.getClusterID());
		 String oneClusterContents = clusterOfOne.getContentsOfClusterAsString();
		 String oneWithout = CorefUtils.filterStopWordsFromString(oneClusterContents);
		 String twoWithout = CorefUtils.filterStopWordsFromString(twoClusterContents);
		
		 if((oneWithout.trim().isEmpty()&&twoWithout.trim().isEmpty())|| 
				 !oneWithout.trim().isEmpty()&&!twoWithout.trim().isEmpty()&&twoWithout.contains(oneWithout)){
			 second = true;
		 }
		 
		 //third constraint, the mention’s modifiers  are  all  included  in  the  modifiers  of  the  antecedent  candidate
		 String oneModi = one.getModifier();
		 String twoModi = two.getModifier();
		 if ((oneModi.trim().isEmpty()&&twoModi.trim().isEmpty())||
				 !oneModi.trim().isEmpty()&&!twoModi.trim().isEmpty()&&twoModi.contains(oneModi)){
			 third = true;
		 }
		 
		 //fourth constraint
//		 if(one.getSentenceNumber()==two.getSentenceNumber()){
//			 
//			 Tree tree = one.getSentenceAsTree();
//			 
//			 String s = "NP<NP";
//			 TregexPattern p = TregexPattern.compile(s);
//			 TregexMatcher m = p.matcher(tree);
//			
//			 
//			 String particle = "";
//			 LinkedList<Tree> treeList = new LinkedList<>();
//			 LinkedList<String> particles = new LinkedList<>();
//			 LinkedList<Boolean> bools = new LinkedList<>();
//			 
//			 while(m.find()){
//			    Tree it = m.getMatch();
//			    treeList.add(it);	    	
//			 }
////			 System.out.println("-------------------------");
////			 treeList.forEach(treee->treee.pennPrint());
//			 
//			 for(Tree a : treeList){
//				 for (Tree et : a.flatten()){
//		    		if ((et.isLeaf())){
//		    			particle = particle+" "+et.pennString().trim();
//		    		}
//		    		
//				 }
//				 particles.add(particle);
//			 }
//			 
//			 //particles.forEach(part->System.out.println("Particle: "+part));
//			 for (String particle2 : particles){
//				 if(!particle2.trim().isEmpty()&&!(particle2.trim().contains(one.getContents())&&particle2.trim().contains(two.getContents()))){
//					 boolean bool = true;
//					 bools.add(bool);
////				 	System.out.println("one: "+one.getContents()+" two: "+two.getContents());
////				 	System.out.println("particle: "+particle);
//				 }
//			 }
//			 
//			 if(bools.size()==particles.size()){
//				 fourth = true;
//			 }
//
//		 }
		 
		 //fourth constraint without Tgrep, but with indexes instead
		 //the two mentions are not in an i-within-i  construct,  i.e.,  one  cannot  be  a  child  NP in the other’s NP constituent
		 if(one.getSentenceNumber()==two.getSentenceNumber()){

			 if(!((two.getStartIndex()<=one.getStartIndex()&&two.getEndIndex()>=one.getEndIndex())
					 ||(one.getStartIndex()<=two.getStartIndex()&&one.getEndIndex()>=two.getEndIndex()))){
				 fourth = true;
			 }
		 }
		 
		 
		 //test whether all constraints are fulfilled
		 retVal = first&&second&&third&&fourth;
		 
		 
		 return retVal;
	 }
	 
	 //	 Pass 4 removes the compatible modifiers only feature
	 public static boolean sieveThree (CorefMention one, CorefMention two){
		 boolean first = false;
		 boolean second = false;
	
		 boolean fourth = false;
		 boolean retVal = false;
		 
		 //first constraint
		 String oneHead = one.getHead();
		 CorefCluster clusterOfTwo = clusterIdMap.get(two.getClusterID());
		 String twoClusterContents = clusterOfTwo.getContentsOfClusterAsString();
		 
		 if(twoClusterContents.matches(".*\\b"+oneHead+"\\b.*")){
			 first = true;
		 }
		 
		//second constraint,all  the  non-stop words  in  the mention cluster are included in the set of non-stop words  in  
		 //the  cluster  of  the  antecedent  candidate
		 CorefCluster clusterOfOne = clusterIdMap.get(one.getClusterID());
		 String oneClusterContents = clusterOfOne.getContentsOfClusterAsString();
		 String oneWithout = CorefUtils.filterStopWordsFromString(oneClusterContents);
		 String twoWithout = CorefUtils.filterStopWordsFromString(twoClusterContents);
		
		 if((oneWithout.trim().isEmpty()&&twoWithout.trim().isEmpty())|| 
				 !oneWithout.trim().isEmpty()&&!twoWithout.trim().isEmpty()&&twoWithout.contains(oneWithout)){
			 second = true;
		 }
		 
		//fourth constraint
		 if(one.getSentenceNumber()==two.getSentenceNumber()){

			 if(!((two.getStartIndex()<=one.getStartIndex()&&two.getEndIndex()>=one.getEndIndex())
					 ||(one.getStartIndex()<=two.getStartIndex()&&one.getEndIndex()>=two.getEndIndex()))){
				 fourth = true;
			 }
		 }
		 
		//test whether all constraints are fulfilled
		 retVal = first&&second&&fourth;
		 
		 
		 return retVal;
	 }
	 
	 
//	 Pass 5 removes the word inclusion constraint.
	 public static boolean sieveFour(CorefMention one, CorefMention two){
		 boolean first = false;

		 boolean third = false;
		 boolean fourth = false;
		 boolean retVal = false;
		 
		 //first constraint
		 String oneHead = one.getHead();
		 CorefCluster clusterOfTwo = clusterIdMap.get(two.getClusterID());
		 String twoClusterContents = clusterOfTwo.getContentsOfClusterAsString();
		 
		 if(twoClusterContents.matches(".*\\b"+oneHead+"\\b.*")){
			 first = true;
		 }
		 
		 //third constraint, the mention’s modifiers  are  all  included  in  the  modifiers  of  the  antecedent  candidate
		 String oneModi = one.getModifier();
		 String twoModi = two.getModifier();
		 if ((oneModi.trim().isEmpty()&&twoModi.trim().isEmpty())||
				 !oneModi.trim().isEmpty()&&!twoModi.trim().isEmpty()&&twoModi.contains(oneModi)){
			 third = true;
		 }
		 
		 
		 //fourth constraint
		 if(one.getSentenceNumber()==two.getSentenceNumber()){

			 if(!((two.getStartIndex()<=one.getStartIndex()&&two.getEndIndex()>=one.getEndIndex())
					 ||(one.getStartIndex()<=two.getStartIndex()&&one.getEndIndex()>=two.getEndIndex()))){
				 fourth = true;
			 }
		 }
		 
		 
		 //test whether all constraints are fulfilled
		 retVal = first&&third&&fourth;
		 
		 
		 return retVal;
	 }
	 
//	 Relaxed head matching. mention head matches any word in the cluster of the candidate antecedent.
//	Both mention and antecedent have to be marked as named entities and the tag has to be equal. 
//	Furthermore, this pass implements a conjunction of the above features with word inclusion and not i-within-i.
	 public static boolean sieveFive(CorefMention one, CorefMention two){
		 boolean first = false;
		 boolean second = false;
		 boolean fourth = false;
		 boolean retVal = false;
		 
		 //first constraint, match any word in the cluster of the candidate antecedent, but only if
		 //both are named entities and the markers match
		 String oneContents = one.getContents();
		 CorefCluster clusterOfTwo = clusterIdMap.get(two.getClusterID());
		 String twoClusterContents = clusterOfTwo.getContentsOfClusterAsString();
		 
		 if(!(one.getNerTags()==null)&&!(two.getNerTags()==null)&&
				 one.getNerTags()[2].equals(two.getNerTags()[2])){
		 	if(twoClusterContents.matches(".*\\b"+oneContents+"\\b.*")){
			 first = true;
		 	}
		 }
		 
		//second constraint,all  the  non-stop words  in  the mention cluster are included in the set of non-stop words  in  
		 //the  cluster  of  the  antecedent  candidate
		 CorefCluster clusterOfOne = clusterIdMap.get(one.getClusterID());
		 String oneClusterContents = clusterOfOne.getContentsOfClusterAsString();
		 String oneWithout = CorefUtils.filterStopWordsFromString(oneClusterContents);
		 String twoWithout = CorefUtils.filterStopWordsFromString(twoClusterContents);
		
		 if((oneWithout.trim().isEmpty()&&twoWithout.trim().isEmpty())|| 
				 !oneWithout.trim().isEmpty()&&!twoWithout.trim().isEmpty()&&twoWithout.contains(oneWithout)){
			 second = true;
		 }
		 
		 //fourth constraint
		 if(one.getSentenceNumber()==two.getSentenceNumber()){

			 if(!((two.getStartIndex()<=one.getStartIndex()&&two.getEndIndex()>=one.getEndIndex())
					 ||(one.getStartIndex()<=two.getStartIndex()&&one.getEndIndex()>=two.getEndIndex()))){
				 fourth = true;
			 }
		 }
		 
		 
		 //test whether all constraints are fulfilled
		 retVal = first&&second&&fourth;
		 
		 
		 return retVal;
	 }
	 
	 
	 //exact match
	 public static boolean sieveOne (CorefMention one, CorefMention two){
		 
		if(one.getContents().equalsIgnoreCase(two.getContents())){
			return true;
					
				
			 
		 }
		return false;
		 
	 }
	 
	 
//	  private static final TregexPattern appositionPattern = TregexPattern.compile("NP=m1 < (NP=m2 $.. (/,/ $.. NP=m3))");
//	  private static final TregexPattern appositionPattern2 = TregexPattern.compile("NP=m1 < (NP=m2 $.. (/,/ $.. (SBAR < (WHNP < WP|WDT=m3))))");
//	  private static final TregexPattern appositionPattern3 = TregexPattern.compile("/^NP(?:-TMP|-ADV)?$/=m1 < (NP=m2 $- /^,$/ $-- NP=m3 !$ CC|CONJP)");
//	  private static final TregexPattern appositionPattern4 = TregexPattern.compile("/^NP(?:-TMP|-ADV)?$/=m1 < (PRN=m2 < (NP < /^NNS?|CD$/ $-- /^-LRB-$/ $+ /^-RRB-$/))");
//	 
	 
	 //here go the percise constructs
	 public static boolean sieveSix(CorefMention one, CorefMention two) throws FileNotFoundException{
		 boolean appositive=false;
		 boolean predNom = false;
		 boolean roleApp = false;
		 boolean relPron = false;
		 boolean acronym = false;
		 boolean demonym = false;
		 boolean ret = false;
		 
		 
		 //check for appositive
		 //this gets some of them, but not all!		
		 String app1 = "NP$/,/<(MPN$/,/$NP)";
		 String app2 = "NP$/,/<(NP$/,/)";
		 String app3 = "PP$/,/$NP$/,/";
		 	TregexPattern p1 = TregexPattern.compile(app1);
		 	TregexPattern p2 = TregexPattern.compile(app2);
		 	TregexPattern p3 = TregexPattern.compile(app3);
	        TregexMatcher m1 = p1.matcher(one.getSentenceAsTree());
	        TregexMatcher m2 = p2.matcher(one.getSentenceAsTree());
	        TregexMatcher m3 = p3.matcher(one.getSentenceAsTree());
	        m1.find();
	        m2.find();
	        m3.find();
	        
	        if(!(m1.getMatch()== null)||!(m2.getMatch()==null||!(m3.getMatch()==null))){
	        	//System.out.println(one.getContents());
	        }
	               
	     //predicate nominative
	        String pred1 = "NP$(VAFIN<(ist|war))$NP";
	        String pred2 = "MPN$(VAFIN<(ist|war))$NP";

			 	TregexPattern pre1 = TregexPattern.compile(pred1);
			 	TregexPattern pre2 = TregexPattern.compile(pred2);
//			 	TregexPattern p3 = TregexPattern.compile(app3);
		        TregexMatcher mre1 = pre1.matcher(one.getSentenceAsTree());
		        TregexMatcher mre2 = pre2.matcher(one.getSentenceAsTree());
//		        TregexMatcher m3 = p3.matcher(one.getSentenceAsTree());
		        mre1.find();
		        mre2.find();
//		        m3.find();
		        
		        if(!(mre1.getMatch()== null)||!(mre2.getMatch()==null)){
		        	System.out.println(one.getContents());
		        }
			 
		 //relative pronoun
		 // for some reason the constructions with relative pronouns are considered as one mention. Deal with that later.
		        
		 //check if demonym
		 String oneContens = one.getContents();
		 String twoContents = two.getContents();
		 if(CorefUtils.isDemonym(oneContens, twoContents)){
			 demonym = true;
		 }
		 
		 //check if acronym
		 if(CorefUtils.isAcronym(oneContens, twoContents)){
			 acronym = true;
		 }
		 
		 //check if any constraint is true
		 if (appositive||predNom||roleApp||relPron||acronym||demonym){
			 ret=true;
		 }
		 return ret;
	 }
	 
	 public static boolean SieveSeven(CorefMention one, CorefMention two){
		 boolean number = false;
		 boolean gender = false;
		 boolean person = false;
		 boolean ner = false;
		 String oneStr = one.getContents();
		 String twoStr = two.getContents();
		 
		 if (CorefUtils.isPronoun(oneStr)||CorefUtils.isPronoun(twoStr)){
			 //check number
			 if (one.getNumber().equals(two.getNumber())||one.getNumber().equals("NON")||two.getNumber().equals("NON")){
				 number = true;
			 }
			 //check gender
			 if(one.getGender().equals(two.getGender())||one.getGender().equals("NOG")||two.getGender().equals("NOG")){
				 gender = true;
			 }
			 //check person
			 if (one.getPerson().isEmpty()||two.getPerson().isEmpty()||one.getPerson().equals(two.getPerson())){
				 person = true;
			 }
//			 //check NER label (this seems useless to me, because this would link any pronoun to any named entity.)
//			 if (!(one.getNerTags().equals(null))||!(two.getNerTags().equals(null))){
//				 ner = true;
//			 }
		 }
		 return false;
	 }
	 
	 
	 public static void mergeClusters (CorefMention one, CorefMention two){
	//one + two = one 
		 
		 CorefCluster oneCluster = clusterIdMap.get(one.getClusterID());
		 CorefCluster twoCluster = clusterIdMap.get(two.getClusterID());
		 
		if (!(one.getClusterID()==two.getClusterID())){
		Set<CorefMention> newSet = oneCluster.getCorefMentions();
		Set<String>	newGenders = oneCluster.getGenders();
		Set<String> newNumbers = oneCluster.getGenders();
		Set<String> newPersons = oneCluster.getPersons();
		int oldClusterId = two.getClusterID();
		two.setClusterID(one.getClusterID());
		
		newSet.add(one);
		newSet.add(two);
		if(!(two.getGender().equals(null)||two.getNumber().equals(null)||two.getPerson().equals(null))){
			newGenders.add(two.getGender());
			newNumbers.add(two.getGender());
			newPersons.addAll(two.getPerson());
		}
	
			 
		oneCluster.setCorefMentions(newSet);
		oneCluster.setGenders(newGenders);
		oneCluster.setNumbers(newNumbers);
		oneCluster.setPersons(newPersons);
		clusterIdMap.put(oneCluster.getClusterID(), oneCluster);
		clusterIdMap.remove(oldClusterId);
		}	 	
			 
			 
			 
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
				if(a.getContents().equalsIgnoreCase(b.getContents())){
					return true;
					
				} 
			 }
		 }return false;
	 }
	 
	 public static CorefCluster mergeClusters (CorefCluster one, CorefCluster two){
 // one + two = one
		 Set<CorefMention> oneMention = one.getCorefMentions();
		 Set<CorefMention> twoMention = two.getCorefMentions();
		 Set<CorefMention> newSet = new LinkedHashSet<CorefMention>();
		 for (CorefMention m : oneMention){
			 newSet.add(m);
		 }
		 for (CorefMention n: twoMention){
			 newSet.add(n);
			 n.setClusterID(one.getClusterID());
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
	 
	 public static LinkedHashSet<CorefCluster> filterDuplicates(LinkedHashSet<CorefCluster> a){
		 CorefCluster[] array = new CorefCluster[a.size()];
		 CorefCluster[] otherArray = new CorefCluster[a.size()];
		 CorefCluster[] mergedArray = new CorefCluster[a.size()];
		 CorefMention empty =  new CorefMention(0, "empty", 0, 0);
		 CorefMention deleted = new CorefMention(0, "deleted", 0, 0);
		 CorefMention news = new CorefMention(0, "new", 0, 0);
		 Set<CorefMention> emptySet = new LinkedHashSet<CorefMention>();
		 emptySet.add(empty);
		 CorefCluster emptyCluster = new CorefCluster(0,emptySet,empty);
		 CorefCluster deletedCluster = new CorefCluster(0,emptySet,deleted);
		 CorefCluster newCluster = new CorefCluster(0,emptySet,news);
		
		 a.toArray(array);
		  for(int i=1; i<a.size();i++){
			  for (int j=1; j<a.size();j++){
				  if (array[i].getClusterID()==array[j].getClusterID()){
					  
					 if(array[i].getCorefMentions().size()>array[j].getCorefMentions().size()){
						 mergedArray[i]=array[i];
						 mergedArray[j]=deletedCluster;

					 }else if (array[i].getCorefMentions().size()<array[j].getCorefMentions().size()){
						 mergedArray[j]=array[i];
						 mergedArray[i]=deletedCluster;

					 }else{
						 mergedArray[i]=array[i];
						 mergedArray[j]=array[j];
					 }
					  
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
