package de.dkt.eservices.ecorenlp.modules;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.nio.ShortBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.hibernate.engine.transaction.jta.platform.internal.SynchronizationRegistryBasedSynchronizationStrategy;

import com.hp.hpl.jena.rdf.model.Model;

import de.dkt.common.niftools.NIFReader;
import de.dkt.eservices.eopennlp.modules.SentenceDetector;
//import de.dkt.eservices.erattlesnakenlp.linguistic.SpanText;
import de.dkt.eservices.erattlesnakenlp.linguistic.SpanWord;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.Tokenizer;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.international.negra.NegraHeadFinder;
import edu.stanford.nlp.trees.LabeledScoredTreeNode;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import opennlp.tools.util.Span;

class LexParser {
	
	//private final static String inputFile = "C:\\Users\\Sabine\\Desktop\\WörkWörk\\14cleaned.txt";
	private final static String inputFile = "C:\\Users\\Sabine\\Desktop\\WörkWörk\\ConLLde.txt";
	
	private final static String EN_PCG_MODEL = "edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz";        
    private final static String DE_PCG_MODEL = "edu/stanford/nlp/models/lexparser/germanPCFG.ser.gz";

    private final TokenizerFactory<CoreLabel> tokenizerFactory = PTBTokenizer.factory(new CoreLabelTokenFactory(), "invertible=true");

    //private final LexicalizedParser parser = LexicalizedParser.loadModel(PCG_MODEL);
    public static LexicalizedParser parser = null;

    
    private static void initLexParser(String lang){
    	if (lang.equalsIgnoreCase("en")){
    		parser = LexicalizedParser.loadModel(EN_PCG_MODEL);
    	}
    	else if (lang.equalsIgnoreCase("de")){
    		parser = LexicalizedParser.loadModel(DE_PCG_MODEL,"-maxLength", "70");
    	}
    	
    }

    public Tree parse(String str) {                
        List<CoreLabel> tokens = tokenize(str);
        Tree tree = parser.apply(tokens);
        return tree;
    }

    private List<CoreLabel> tokenize(String str) {
        Tokenizer<CoreLabel> tokenizer =
            tokenizerFactory.getTokenizer(
                new StringReader(str));    
        return tokenizer.tokenize();
    }

    
    public LexParser initLexParser(){
    	return new LexParser();
    }
    //not sure if this is the best place for this, but if I do it in rattlesnake, the circular dependencies are flying all over the place...
    public Model addVerbsToNIF(LexParser parser, Model nifModel, String language, String opennlpSentModel){
    	
    	String isstr = NIFReader.extractIsString(nifModel);
    	
    	Span[] sentenceSpans = SentenceDetector.detectSentenceSpans(isstr, opennlpSentModel);
    	for (Span sentenceSpan : sentenceSpans){
			String sentence = isstr.substring(sentenceSpan.getStart(), sentenceSpan.getEnd());
			Tree tree = parser.parse(sentence);
			
    	}
    	
    	return nifModel;
    	
    }
    
    
    
    @SuppressWarnings("unchecked")
	public static void main(String[] args) throws IOException { 

      

       SpanWord span = getDocumentSpan(inputFile);
       
    	  //find out the number of words (just for debugging)
       		String everything = span.getText();
    	  String modEv = everything.replaceAll("\\."," .");
    	  List<String> list = Arrays.asList(modEv.split(" |\n"));
    	  int numberOfWords = list.size();
    	
          
    	  // Create a map to match word number and word index. We will need that later, when we match gender and number info from RFTagger
    	  //output.txt contains the tokenized text. I feed the output.txt to the RFTagger, to keep the tokenization as consistent as possible
    	   TreeMap<Integer, Integer> map = new TreeMap<Integer, Integer>();
    	   TreeMap<Integer, Integer> theOtherMap = new TreeMap<Integer,Integer>();
           int value = 1;
           map.put(-1, 0);
           PrintWriter out = new PrintWriter("C:\\Users\\Sabine\\Desktop\\WörkWörk\\output.txt");
     	   PTBTokenizer<CoreLabel> ptbt = new PTBTokenizer<>(new FileReader(inputFile),
                   new CoreLabelTokenFactory(), "");
           while (ptbt.hasNext()) {
             CoreLabel label = ptbt.next(); 

             int newIndex = label.beginPosition();
       	   map.put(newIndex,value);
       	   theOtherMap.put(value, newIndex);
       	   
       	   out.println(label);
       	   value++;
           }
           out.close();
           
           //other tokenization; unused right now
           ArrayList<HasWord> hwl = new ArrayList<HasWord>();
           String[] tokens = "I can't do that .".split(" ");
           for (String t : tokens){
            HasWord hw = new Word();
            hw.setWord(t);
            hwl.add(hw);
           }
           LexicalizedParser lexParser = LexicalizedParser.loadModel("edu/stanford/nlp/models/lexparser/germanPCFG.ser.gz","-maxLength", "70");
           Tree tree2 = lexParser.parse(hwl);
           ////System.out.println("tree2:" + tree2);
    	  
    	  
    	 //start Lexical Parser out of the loop so it won't start for every sentence  
        initLexParser("de");
        
        //initializing some sentence maps for later use
        //genderNumberMap contains the Noun Phrase as SpanWord and the gender and number information as String Array. 
        //sentenceMap maps each sentence number to the genderNumberMap of the sentence
        //sentenceMap2 maps the sentence number to the sentence as String
        //sentenceMap3 maps the sentence number to the sentence Indexes
        String sent = new String();
        HashMap<SpanWord,String[]> genderNumberMap= new HashMap<SpanWord,String[]>();
        LinkedHashMap<Integer,HashMap<SpanWord,String[]>> sentenceMap = new LinkedHashMap<Integer,HashMap<SpanWord,String[]>>();
        LinkedHashMap<Integer,String> sentenceMap2 = new LinkedHashMap<Integer,String>();
        LinkedHashMap<Integer, int[]> sentenceMap3 = new LinkedHashMap<Integer,int[]>();
        
        int sentenceCounter = 0;
        
        //do sentence splitting. Fill the sentence Maps. 
        Span[] sentenceSpans = SentenceDetector.detectSentenceSpans(everything, "en-sent.bin");
        
        for (Span sentenceSpan : sentenceSpans){
        	int[] sentenceSpans2 = new int[2];
        int sentenceStart = sentenceSpan.getStart();
        int sentenceEnd = sentenceSpan.getEnd();
       sent = everything.substring(sentenceStart, sentenceEnd);
       
      sentenceMap2.put(sentenceCounter,sent);
      sentenceSpans2[0]=sentenceStart;
      sentenceSpans2[1]=sentenceEnd;
      sentenceMap3.put(sentenceCounter, sentenceSpans2);
      sentenceCounter++;


            
      //do parsing on the split sentence
      Tree tree = getTreeFromSentence(sent);
       
        
        //Extract NPs and PPERs and add them to list nps
       ArrayList<ArrayList<String>> nps = extractNPs(tree);
        
         //iterate trough nps and extract the phrase spans
        List<SpanWord> wordSpans = new ArrayList<SpanWord>();
        
        
        //////////////////////////////////////////////////////////
       // !!DONT DELETE THAT, ITS JUST TURNED OFF FOR TESTING!!///
        /////////////////////////////////////////////////////////
        
        
        
        /*for (ArrayList<String> subl : nps){
        	////System.out.println("DEBUG subl: "+subl); 
        	String word = new String(); 
        	word =(subl.get(0));
        	for(int x=1; x<subl.size(); x++){
        		word = word + " " +(subl.get(x));
        	    
        	}
        
        	
        	List<Integer> pos = new ArrayList<Integer>();
        	int counter = 0;
        	  if (sent.toLowerCase().contains(word.toLowerCase()) && sent.toLowerCase().indexOf(word.toLowerCase()) != sent.toLowerCase().lastIndexOf(word.toLowerCase())){
        		    Matcher m = Pattern.compile("(?i)\\b"+word+"\\b").matcher(sent);
        		    counter++;
        		    while (m.find())
        		    {
        		        pos.add(m.start());
        		    
        		    }
        		    
        		    }
        	
        	
        	SpanWord d = new SpanWord("",0,0);
        	
        	if(pos.size()>1){
        		
        		int begin = sentenceStart + pos.get(counter-1);
            	int end = begin +word.length();
        		d = new SpanWord(word,begin,end);
        		wordSpans.add(d);
        		////System.out.println("DEBUG word span: "+d.getText()+" "+d.getStartSpan()+" "+d.getEndSpan());
        		
        	}else{
        		String inputStr = sent;
        	    String patternStr = word;
        	    Pattern pattern = Pattern.compile("\\b"+patternStr+"\\b");
        	    Matcher matcher = pattern.matcher(inputStr);
        	    if(matcher.find()){
        	    	int begin = sentenceStart + matcher.start();
        	    	int end = begin +word.length();
        	    	d = new SpanWord(word,begin,end);
        	    	wordSpans.add(d);}
        	    else{
        	    	////System.out.println("Something went wrong with the matching!");
        	    	}
        		////System.out.println("DEBUG word span: "+d.getText()+" "+d.getStartSpan()+" "+d.getEndSpan());
        	}
        	
        	//get number and gender of single words in Noun Phrases from RFTagger tagged document
        	List<String> wordTags = getWordTags(d, map, word);
        	
        	// compute gender and number for Noun Phrases, put them in the empty genderNumberMap
        	genderNumberMap = fillGenderNumberMap(genderNumberMap, wordTags, d);        
        	
        	
        }*/
        
        //sentenceMap.put(sentenceCounter, genderNumberMap);
        
        }
        
        
        
        //Initialize the coreferenceChain structure here:
    
        
        HashMap<SpanWord, ArrayList<SpanWord>> mastermap = new HashMap<SpanWord, ArrayList<SpanWord>>();
        List<SpanWord> alreadyTested = new ArrayList<SpanWord>();
        
        
        //Here is where the matching begins
        // To match the Noun Phrases put a map of the number of the sentence and the sentence indexes in here
        //This can count as first sieve
        for( Entry<Integer, int[]> a :sentenceMap3.entrySet()){
        	int[] indexes= a.getValue();
        	int key = a.getKey();
        	
        	
        	//get the nps from the sentences and put them in mnps
        	ArrayList<SpanWord> mnps = stringToNpListwIndex(indexes);
        	
        	
        	
        	//loop per next 5 sentences, get nps for next sentence and put them in mnps
        	for (int j = key+1; j < Math.min(sentenceMap3.size(), key+5); j++){
        		int[] indexes2 = sentenceMap3.get(j);
        		
        		for (SpanWord abc : stringToNpListwIndex(indexes2)){
        			mnps.add(abc);
        		}
        	}
        	
//        	PrintWriter out2 = new PrintWriter("C:\\Users\\Sabine\\Desktop\\WörkWörk\\outputNPS.txt");
//        	for (SpanWord b : mnps){
//        		System.out.println(b.getText());
//        		out2.println(b.getText());
//        	}
//        	out2.close();
//        	
        	//the nps of six consecutive sentences are in mnps now
        	// loop through mnps and compare the single items
        	for (int k = 0; k < mnps.size(); k++){
        		for (int l = k+1; l < mnps.size(); l++){
        		
        			SpanWord r = mnps.get(k);
        			SpanWord s = mnps.get(l);
        			
        			
        			if (compareListsSpan(r,s)){
        				
        				if (mastermap.containsKey(r)&& !alreadyTested.contains(s)){
        				
        					ArrayList<SpanWord> innerList = mastermap.get(r);
        					innerList.add(s);
        					if (!innerList.contains(r)){
        					innerList.add(r);}
        					mastermap.put(r,  sortByOrderOfAppearance(innerList));
        				}
        				else if ( !mastermap.containsKey(r)&& !alreadyTested.contains(s)) {
        				
        					ArrayList<SpanWord> templist = new ArrayList<SpanWord>();
        					templist.add(s);
        					templist.add(r);
        					mastermap.put(r, sortByOrderOfAppearance(templist));
        				}
        				alreadyTested.add(r);
        				alreadyTested.add(s);       			
        				
        			}
        		}
        	}
        	
        	
        	
        	
        }
        
    
        
        //ANKIT
        /*//System.out.println("==========DEBUG NP HEAD PERCOLATION========");
        String sentence = new String("Barack Obama, Präsident der U.S.A, besuchte heute Berlin.");
        HashMap<Span, String> npHash = getNPHeads(sentence);
        for (Span sp : npHash.keySet()){
      	  //System.out.println("NP:" + sentence.substring(sp.getStart(), sp.getEnd()));
      	  //System.out.println("Indices:" + sp.getStart() + "|" + sp.getEnd());
      	  //System.out.println("HEAD:" + npHash.get(sp));
      	}*/
       
        
        //This can count as second sieve, its the strict matching of heads and matching of stemmed heads
        for( Entry<Integer, int[]> a :sentenceMap3.entrySet()){
        	int[] indexes= a.getValue();
        	int key = a.getKey();
        	
        	
        	//get the heads from the sentences and put them in mnps
        	 String sentence = everything.substring(indexes[0],indexes[1]);
        	 
        	 //System.out.println("DEBUG sentence in second sieve: "+sentence);
        	 
        	HashMap<Span, String> npHash1 = getNPHeads(sentence);
        	ArrayList<SpanWord> mnps = new ArrayList<SpanWord>();
            for (Span sp : npHash1.keySet()){
            	String np = sentence.substring(sp.getStart(), sp.getEnd());
            	int startIndexOfHead = indexes[0]+np.indexOf(npHash1.get(sp));
            	int endIndexOfHead = startIndexOfHead + npHash1.get(sp).length();
				SpanWord r = new SpanWord(npHash1.get(sp),startIndexOfHead,endIndexOfHead);
          	  mnps.add(r);}
            
        	
        	//loop per next 5 sentences, get heads of nps for next sentence and put them in mnps
        	for (int j = key+1; j < Math.min(sentenceMap3.size(), key+5); j++){
        		int[] indexes2 = sentenceMap3.get(j);
        		
        		String sentence2 = everything.substring(indexes2[0],indexes2[1]);
        		
        		HashMap<Span, String> npHash2 = getNPHeads(sentence2);
        		 for (Span sp : npHash2.keySet()){
        			String np = everything.substring(sp.getStart(), sp.getEnd());
                 	int startIndexOfHead = indexes2[0]+np.indexOf(npHash2.get(sp));
                 	int endIndexOfHead = startIndexOfHead + npHash2.get(sp).length();
     				SpanWord r = new SpanWord(npHash2.get(sp),startIndexOfHead,endIndexOfHead);
               	  mnps.add(r);}
        	}
        	
        	
        	//the heads of nps of six consecutive sentences are in mnps now
        	// loop through mnps and compare the single items
        	for (int k = 0; k < mnps.size(); k++){
        		for (int l = k+1; l < mnps.size(); l++){
        		
        			SpanWord r = mnps.get(k);
        			SpanWord s = mnps.get(l);
        			
        			
        			if (compareListsSpan(r,s)){
        				
        				if (mastermap.containsKey(r)&& !alreadyTested.contains(s)){
        				
        					ArrayList<SpanWord> innerList = mastermap.get(r);
        					innerList.add(s);
        					if (!innerList.contains(r)){
        					innerList.add(r);}
        					mastermap.put(r,  sortByOrderOfAppearance(innerList));
        				}
        				else if ( !mastermap.containsKey(r)&& !alreadyTested.contains(s)) {
        				
        					ArrayList<SpanWord> templist = new ArrayList<SpanWord>();
        					templist.add(s);
        					templist.add(r);
        					mastermap.put(r, sortByOrderOfAppearance(templist));
        				}
        				alreadyTested.add(r);
        				alreadyTested.add(s);
        				
        			}
        		}
        	}
        	
        }
        
        mastermap = deleteDuplicatesInCorefChain(mastermap);
        ArrayList<SpanWord> deleteSpans = new ArrayList<SpanWord>();
    	Set<SpanWord> setkeys = mastermap.keySet();
    	SpanWord[] mapkeys = setkeys.toArray(new SpanWord[setkeys.size()]);
    	for (int q = 0; q < mapkeys.length; q++){
    		for (int w = q+1; w < mapkeys.length; w++){
    			if (mastermap.get(mapkeys[q]).equals(mastermap.get(mapkeys[w]))){
    				deleteSpans.add(mapkeys[q]);
    			}
    		}
    	}
    	
    	for (SpanWord q : deleteSpans){
    		mastermap.remove(q);
    	}
    	
    	
    	
    	for (SpanWord sw : mastermap.keySet()){
    		System.out.println("-----------------------------------------------------------------");
    		for (SpanWord sw2 : mastermap.get(sw)){
    			System.out.println(String.format("is in the chain: %s\twith index (%s,%s)", sw2.getText(), sw2.getStartSpan(), sw2.getEndSpan()));
    		}
    	}
    	//ArrayList<SpanWord> allTheNPs = getAllTheNPs();
    	
//    	for(SpanWord f : allTheNPs){
//    		System.out.println(f.getText()+"\t"+f.getStartSpan()+"\t"+f.getEndSpan());
//    		
//    	}
    	
   	HashMap<String,String> corefChainsForConLL = corefChainsForConLL (mastermap, sentenceMap3, map);
   	
   	SortedSet<String> keys = new TreeSet<String>(corefChainsForConLL.keySet());
    	int wordCounter = 1;
//    	   	
   	PrintWriter out2 = new PrintWriter(new FileWriter("C:\\Users\\Sabine\\Desktop\\WörkWörk\\outputConLL.txt", false), false);
   	for (String key : keys) { 
   	   String value2 = corefChainsForConLL.get(key); 
   	 out2.write(key+"- - - - - - - - - - - - - - - - "+value2+"\n");
    	 wordCounter++;
   	}
       out2.close();


    }
    
    public static HashMap<String,String> corefChainsForConLL (HashMap<SpanWord,ArrayList<SpanWord>> mastermap, LinkedHashMap<Integer, int[]> sentenceMap3, TreeMap <Integer,Integer> map) throws FileNotFoundException{
    	HashMap<Integer,String> index2PosMap = new HashMap<Integer, String>();
    	HashMap<String,String> corefChainMap = new HashMap<String,String>();
    	
    	for( Entry<Integer, int[]> a :sentenceMap3.entrySet()){
    		int wordNumber = 1;
        	int[] indexes= a.getValue();
        	int sentenceNumber = a.getKey();
        
        	//here goes the tokenization of the sentence into words
        	for (int i=indexes[0]; i<=indexes[1]; i++){
        		
        	
        		if (map.containsKey(i)){
        			if(sentenceNumber<10&&wordNumber<10){
        				index2PosMap.put(i,"0"+sentenceNumber+"_"+"0"+wordNumber);
        				wordNumber++;
        			}
        			else if(sentenceNumber>=10&&wordNumber<10){
        				index2PosMap.put(i,sentenceNumber+"_"+"0"+wordNumber);
        				wordNumber++;
        			}
        			else if(sentenceNumber<10&&wordNumber>=10){
        				index2PosMap.put(i,"0"+sentenceNumber+"_"+wordNumber);
        				wordNumber++;
        			}
        			else if(sentenceNumber>=10&&wordNumber>=10){
        				index2PosMap.put(i,sentenceNumber+"_"+wordNumber);
        				wordNumber++;
        				}
        			
        		
        		}
        	}
        	
        	}
    	
    	for (String s : index2PosMap.values()){
    		corefChainMap.put(s, "-");
    	}
    	
//    	for (Entry<Integer,String> s : index2PosMap.entrySet()){
//    		System.out.println("DEBUG index2PosMap: "+s.getKey()+"\t"+s.getValue());
//    	}
    	
    	int counterOfCorefChains = 1;
    	for(Entry<SpanWord,ArrayList<SpanWord>> k : mastermap.entrySet()){
    		for ( SpanWord j : k.getValue()){
    			String position=index2PosMap.get((j.getStartSpan()+1));
    			if(corefChainMap.get(position)==null){
    				System.out.println("Oh no, index " + j.getStartSpan() +" is missing in index2PosMap!");
    			}
    			else if(corefChainMap.get(position).equals("-")){
    				corefChainMap.put(position, Integer.toString(counterOfCorefChains));
    			}else{
    				String whatsInThere = corefChainMap.get(position);
    				corefChainMap.put(position, whatsInThere+"|"+Integer.toString(counterOfCorefChains));
    			}
    		}
    		counterOfCorefChains++;
    		
    	}
    	
    	return corefChainMap;
    }
    
    
    public static HashMap<SpanWord, ArrayList<SpanWord>> deleteDuplicatesInCorefChain(HashMap<SpanWord, ArrayList<SpanWord>> mm){
    	
    	
    	
    	for (SpanWord sw : mm.keySet()){
    		ArrayList<SpanWord> newlist = new ArrayList<SpanWord>();
    		ArrayList<SpanWord> innerList = mm.get(sw);
    		HashMap<SpanWord, String> tempmap = new HashMap<SpanWord, String>();
    		for (SpanWord sw2 : innerList){
    			tempmap.put(sw2, "dummy");
    		}
    		for (SpanWord sss : tempmap.keySet()){
    			newlist.add(sss);
    		}
    		mm.put(sw, newlist);
    	}
    	
    	return mm;
    }
    
    public static ArrayList<SpanWord> sortByOrderOfAppearance(ArrayList<SpanWord> spanlist){
    	
    	ArrayList<SpanWord> sortedList = new ArrayList<SpanWord>();
    	
    	ArrayList<Integer> intlist = new ArrayList<Integer>();
    	for (SpanWord  sw : spanlist){
    		intlist.add(sw.getStartSpan());
    	}
    	Collections.sort(intlist);
    	for (int j : intlist){
    		for (SpanWord sw : spanlist){
    			if (sw.getStartSpan() == j){
    				sortedList.add(sw);
    			}
    		}
    	}
    	 
    	return sortedList; 
    	
    }
    
    public static HashMap<SpanWord,String[]> fillGenderNumberMap (HashMap<SpanWord,String[]> genderNumberMap, List<String> wordTags, SpanWord d){
    	
    	int masc = 0;
    	int fem = 0;
    	int neut = 0;
    	int sg = 0;
    	int pl = 0;
    	String[] genNum = new String[2];
    	genNum[0] = "unkn";
    	genNum[1] = "unkn";
    	
    	for(int i=0; i<wordTags.size(); i++){
    		if (wordTags.get(i).contains("Sg")){
    			sg++;
    		}
    		if (wordTags.get(i).contains("Pl")){
    			pl++;
    		}
    		if (wordTags.get(i).contains("Masc")){
    			masc++;
    		}
    		if (wordTags.get(i).contains("Fem")){
    			fem++;
    		}
    		if (wordTags.get(i).contains("Neut")){
    			neut++;
    		}
    	}
    	if (masc>0 && fem<=0 && neut<=0){
    		
    		genNum[0] = "masc";
    	}if (fem>0 && masc<=0&& neut<=0){
    	
    		genNum[0] = "fem";
    	}if (neut>0 && masc<=0&& fem<=0){
    		
    		genNum[0] = "neut";
    	}
    	if (sg>0 && pl<=0){
    		
    		genNum[1] = "sg";
    	}if (pl>0 && sg<=0){
    	
    		genNum[1] = "pl";
    	}
    	
    	genderNumberMap.put(d, genNum);
    	return genderNumberMap;
    
    }
    
    public static List<String> getWordTags(SpanWord d, HashMap<Integer, Integer> map, String word) throws IOException{
    	int y = d.getStartSpan();
    	int wordNumber = map.get(y);
    	////System.out.println("DEBUG wordNumber: "+wordNumber);
    	
    	List<String> list2 = Arrays.asList(word.split(" "));
    	List<String> wordTags = new ArrayList<String>();
  	  	int numberOfWords2 = list2.size();
  	  	
  	  	//This is the tokenized file created by RFTagger
  	  	String line32 = Files.readAllLines(Paths.get("C:\\Users\\Sabine\\Desktop\\WörkWörk\\15_old3.txt")).get(wordNumber-1);
  	  	
  	  	//cleanup of the result of the RFTagger
  	  	/*FileWriter fw = new FileWriter("C:\\Users\\Sabine\\Desktop\\WörkWörk\\16.txt"); 
    	for(int i=0; i<numberOfWords2; i++){
    		if (!line32.equals("")) // don't write out blank lines
    	    {
    	        fw.write(line32, 0, line32.length());
    	    }
    	}
    	fw.close();*/
    	
    	
    	for(int i=0; i<numberOfWords2; i++){
    		line32 = Files.readAllLines(Paths.get("C:\\Users\\Sabine\\Desktop\\WörkWörk\\15_old3.txt")).get(wordNumber-1+i);
    		////System.out.println("DEBUG wordTag: "+line32);
    		wordTags.add(line32);
    	}
    	return wordTags;
    }
    
    
    
    public static ArrayList<ArrayList<String>> extractNPs(Tree tree){
    	Object[] a = tree.toArray();
        ArrayList<ArrayList<String>> nps = new ArrayList<ArrayList<String>>();
        for (Object s : a){
        	LabeledScoredTreeNode t = (LabeledScoredTreeNode)s;
        	if (t.label().toString().equalsIgnoreCase("np")||t.label().toString().equalsIgnoreCase("pper")||t.label().toString().equalsIgnoreCase("cnp")){
        	//if (t.label().toString().equalsIgnoreCase("np")){
        		ArrayList<String> npAsList = new ArrayList<String>();
        		for (Tree it : t.flatten()){
        			if ((it.isLeaf())){
        				npAsList.add(it.pennString().trim());
        			}
        		}
        		
        		nps.add(npAsList);
        	}
        }
		return nps;
    	
    }
    
    public static Tree getTreeFromSentence(String sent){
        Tree tree = parser.parse(sent);
        parser.setOptionFlags();
       ////System.out.println("DEBUG tree: ");
       //tree.pennPrint();
		return tree;
    	
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
    
    
    
    public static boolean compareLists(ArrayList<String> list1, ArrayList<String> list2){
    	if(list1.size()!=list2.size()){
    		return false;
    	}
    	for (int i = 0; i < list1.size(); i++) {
			if(!list1.get(i).equalsIgnoreCase(list2.get(i))){
				return false;
			}
		}
    	return true;
    }
    
    // recognizes exact match, nearly exact match if only one word, differences in case and articles if two words
    public static boolean compareListsSpan(SpanWord s1, SpanWord s2){
    	
    	String w1 = s1.getText();
    	String w2 = s2.getText();
    	int i1 = s1.getStartSpan();
    	int i2 = s2.getStartSpan();
    	boolean retValue = false;
    	if (i1 == i2){
    		return false;
    	}
    	else if (w1.equalsIgnoreCase(w2)){
    		retValue = true;
    		////System.out.println("FOUND IT: "+w1+i1+"\t"+w2+i2);
    	}

    	else if (!w1.contains(" ") && !w2.contains(" ")&&w1.length() > 4 && w2.length()> 4){
    		String naiveStemW1 = (w1.substring(0, (Integer)w1.length() - w1.length()/5)).toLowerCase();
    		String naiveStemW2 = (w2.substring(0, (Integer)w2.length() - w2.length()/5)).toLowerCase();
    		if(naiveStemW1.contains(naiveStemW2) || naiveStemW2.contains(naiveStemW1)){
    			////System.out.println("DEBUGGING STEMMED APPROACH:" + naiveStemW1 + "\t" + naiveStemW2);
    			retValue = true;
    		}
    	}
    	
    	else if ((w2.split(" ").length!=2 && w1.split(" ").length==2 && w1.matches("(?i)(.*)\\b(der|die|das|dem|den|des|ein|eine|einen|einem|eines|einer)\\b(.*)"))){
    		String [] w1AsArray = w1.split(" ");
    		
    		w1 = w1AsArray[1];
    		
    		if (w1.length() > 4 && w2.length()> 4){
        		String naiveStemW1 = (w1.substring(0, (Integer)w1.length() - w1.length()/5)).toLowerCase();
        		String naiveStemW2 = (w2.substring(0, (Integer)w2.length() - w2.length()/5)).toLowerCase();
        		if(naiveStemW1.contains(naiveStemW2) || naiveStemW2.contains(naiveStemW1)){
        			////System.out.println("DEBUGGING STEMMED APPROACH2a:" + naiveStemW1 + "\t" + naiveStemW2);
        			retValue = true;
        		}
    	} }
    	else if ((w1.split(" ").length!=2 && w2.split(" ").length==2 && w2.matches("(?i)(.*)\\b(der|die|das|dem|den|des|ein|eine|einen|einem|eines|einer)\\b(.*)"))){
    		
    		String [] w2AsArray = w2.split(" ");
    		
    		w2 = w2AsArray[1];
    		if (w1.length() > 4 && w2.length()> 4){
        		String naiveStemW1 = (w1.substring(0, (Integer)w1.length() - w1.length()/5)).toLowerCase();
        		String naiveStemW2 = (w2.substring(0, (Integer)w2.length() - w2.length()/5)).toLowerCase();
        		if(naiveStemW1.contains(naiveStemW2) || naiveStemW2.contains(naiveStemW1)){
        			////System.out.println("DEBUGGING STEMMED APPROACH2b:" + naiveStemW1 + "\t" + naiveStemW2);
        			retValue = true;
        		}
    	} }
    	else if ((w1.split(" ").length==2 && w2.split(" ").length==2 && w2.matches("(?i)(.*)\\b(der|die|das|dem|den|des|ein|eine|einen|einem|eines|einer)\\b(.*)")&& w1.matches("(?i)(.*)\\b(der|die|das|dem|den|des|ein|eine|einen|einem|eines|einer)\\b(.*)"))){
    		String [] w1AsArray = w1.split(" ");
    		String [] w2AsArray = w2.split(" ");
    		w1 = w1AsArray[1];
    		w2 = w2AsArray[1];
    		if (w1.length() > 4 && w2.length()> 4){
        		String naiveStemW1 = (w1.substring(0, (Integer)w1.length() - w1.length()/5)).toLowerCase();
        		String naiveStemW2 = (w2.substring(0, (Integer)w2.length() - w2.length()/5)).toLowerCase();
        		if(naiveStemW1.contains(naiveStemW2) || naiveStemW2.contains(naiveStemW1)){
        			////System.out.println("DEBUGGING STEMMED APPROACH2c:" + naiveStemW1 + "\t" + naiveStemW2);
        			retValue = true;
        		}
    	} }
    	
    	return retValue;
    }
        
        
    public static  ArrayList<ArrayList<String>> stringToNpList (String sent){
    Tree tree = parser.parse(sent);
    parser.setOptionFlags();
   //System.out.println("DEBUG tree: ");
   //tree.flatten().pennPrint();
    
    //Extract NPs and PPERs and adds them to list nps
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
    			}
    		}
    		
    		nps.add(npAsList);
    	}
    }
    
    	
    
	return nps;}
    
    //gets all the NPs from a text
    public static ArrayList<SpanWord> getAllTheNPs() throws IOException{
    	ArrayList<SpanWord> allNPs = new ArrayList<SpanWord>();
    	SpanWord span = getDocumentSpan(inputFile);
    	String everything = span.getText();
    	Span[] sentenceSpans = SentenceDetector.detectSentenceSpans(everything, "en-sent.bin");
    	LinkedHashMap<Integer, int[]> sentenceMap3 = new LinkedHashMap<Integer,int[]>(); 
    	int sentenceCounter = 0;
    	
         for (Span sentenceSpan : sentenceSpans){
        	 int[] sentenceSpans2 = new int[2];
        	 int sentenceStart = sentenceSpan.getStart();
        	 int sentenceEnd = sentenceSpan.getEnd();
        	 sentenceSpans2[0]=sentenceStart;
        	 sentenceSpans2[1]=sentenceEnd;
        	 sentenceMap3.put(sentenceCounter, sentenceSpans2);
        	 sentenceCounter++;}
       
    	 for( Entry<Integer, int[]> a :sentenceMap3.entrySet()){
         	int[] indexes= a.getValue();
         	allNPs.addAll(stringToNpListwIndex(indexes));
         	}
  	
     	
     	
		return allNPs;
    	
    }
    
    
    //gets the sentence indexes, returns the NPs with indexes
    public static  ArrayList<SpanWord> stringToNpListwIndex(int[] indexes) throws IOException{
    	ArrayList<SpanWord> bla = new ArrayList<SpanWord>();
    	String everything = new String();
        
        FileInputStream inputStream = new FileInputStream(inputFile);
        try {
            everything = IOUtils.toString(inputStream);
           
        } finally {
            inputStream.close();
        }
        
        
        	String sent = everything.substring(indexes[0], indexes[1]);
        	//System.out.println("DEBUG sentence in first sieve: "+sent);
        
        	Tree tree = parser.parse(sent);
        	parser.setOptionFlags();
        	//System.out.println("DEBUG tree: ");
        	//System.out.println(tree.flatten().toString());
        
        	//Extract NPs and PPERs and adds them to list nps
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
        				}
        			}
        		
        			nps.add(npAsList);
        		}
        	}
        	
        	int counter = 0;
            
            ArrayList<SpanWord> wordSpans = new ArrayList<SpanWord>();
           
            for (ArrayList<String> subl : nps){
            	////System.out.println("DEBUG subl: "+subl); 
            	String word = new String(); 
            	word =(subl.get(0));
            	for(int x=1; x<subl.size(); x++){
            		word = word + " " +(subl.get(x));
            	    
            	}
        
        
        	
            List<Integer> pos = new ArrayList<Integer>();
        	
      	  if (sent.toLowerCase().contains(word.toLowerCase()) && sent.toLowerCase().indexOf(word.toLowerCase()) != sent.toLowerCase().lastIndexOf(word.toLowerCase())){
      		    Matcher m = Pattern.compile("(?i)\\b"+word+"\\b").matcher(sent);
      		    counter++;
      		    while (m.find())
      		    {
      		        pos.add(m.start());
      		    
      		    }
      		    
      		    }
      	
      	//System.out.println("Counter :"+counter+"\nPosSize :"+pos.size());
      	SpanWord d = new SpanWord("",0,0);
      	
      	if(pos.size()>1){
      		
      		int begin = indexes[0] + pos.get(counter-1);
          	int end = begin +word.length();
      		d = new SpanWord(word,begin,end);
      		wordSpans.add(d);
      		////System.out.println("DEBUG word span: "+d.getText()+" "+d.getStartSpan()+" "+d.getEndSpan());
      		
      	}else{
      		String inputStr = sent;
      	    String patternStr = word;
      	    Pattern pattern = Pattern.compile("\\b"+patternStr+"\\b");
      	    Matcher matcher = pattern.matcher(inputStr);
      	    if(matcher.find()){
      	    	int begin = indexes[0] + matcher.start();
      	    	int end = begin +word.length();
      	    	d = new SpanWord(word,begin,end);
      	    	wordSpans.add(d);}
      	    else{
      	    	////System.out.println("Something went wrong with the matching!");
      	    	}
      		////System.out.println("DEBUG word span: "+d.getText()+" "+d.getStartSpan()+" "+d.getEndSpan());
      	}
      	
    	}
//            bla.add(wordSpans);
//		return bla;
            return wordSpans;
                       
    }
    
    
    /**
     * ANKIT
     * Method to label head words in a tree using Stanford NLP NegraHeadFinder
     * Help from de.dkt.eservices.ecorenlp.modules.sandbox
     * pre: Input Tree
     * post: Output list of head NPs
     */
    public static HashMap<Span, String> getNPHeads(String sentence){
       	// Get constituency parse tree for the sentence
    	Tree t = getTreeFromSentence(sentence);
    	////System.out.println("DEBUG: Sentence:");
    	////System.out.println(sentence);
    	////System.out.println("DEBUG: Tree:");
    	//t.pennPrint();
    	
    	// Get a list of NPs and head of each NP using methods from sandbox.java
    	//TODO: Merge the associated methods from sandbox.java herein
    	HashMap<Span, String> npHash = sandbox.traverseTreeForNPs(t, new HashMap<Span, String>());
    	
    	
    	return npHash;
    }
}
