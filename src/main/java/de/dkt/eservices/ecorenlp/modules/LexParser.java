package de.dkt.eservices.ecorenlp.modules;

import java.io.FileInputStream;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;

import com.hp.hpl.jena.rdf.model.Model;

import de.dkt.common.niftools.NIFReader;
import de.dkt.eservices.eopennlp.modules.SentenceDetector;
import de.dkt.eservices.erattlesnakenlp.linguistic.SpanText;
import de.dkt.eservices.erattlesnakenlp.linguistic.SpanWord;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.Tokenizer;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.LabeledScoredTreeNode;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import opennlp.tools.util.Span;

class LexParser {
	
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
    
    
    
    public static void main(String[] args) throws IOException { 

       
    	String everything = new String();
        
        //Find out the span of the document
        FileInputStream inputStream = new FileInputStream("C:\\Users\\Sabine\\Desktop\\WörkWörk\\14cleaned.txt");
        try {
            everything = IOUtils.toString(inputStream);
            int docLength = everything.length();
            System.out.println("document length :"+docLength);
        } finally {
            inputStream.close();
        }
        
          SpanText span = new SpanText(everything, 0);
    	  System.out.println("DEBUG document span: "+span.getStartSpan()+" "+span.getEndSpan());
    	  
    	  //find out the number of words
    	  String modEv = everything.replaceAll("\\."," .");
    	  List<String> list = Arrays.asList(modEv.split(" |\n"));
    	  int numberOfWords = list.size();
    	
          
    	  // Map to match word number and word span
    	   HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
           int value = 1;
           map.put(-1, 0);
           PrintWriter out = new PrintWriter("C:\\Users\\Sabine\\Desktop\\WörkWörk\\output.txt");
     	   PTBTokenizer<CoreLabel> ptbt = new PTBTokenizer<>(new FileReader("C:\\Users\\Sabine\\Desktop\\WörkWörk\\14cleaned.txt"),
                   new CoreLabelTokenFactory(), "");
           while (ptbt.hasNext()) {
             CoreLabel label = ptbt.next(); 

             int newIndex = label.beginPosition();
       	   map.put(newIndex,value);
       	   
       	   out.println(label);
       	   //System.out.println("DEBUG map: "+label+" Value: "+value+" Key: "+newIndex);
       	   value++;
           }
           out.close();
    	  
    	  
    	  //start Lexical Parser out of the loop so it won't start for every sentence  
        initLexParser("de");
        String sent = new String();
        HashMap<SpanWord,String[]> genderNumberMap= new HashMap<SpanWord,String[]>();
        LinkedHashMap<Integer,HashMap<SpanWord,String[]>> sentenceMap = new LinkedHashMap<Integer,HashMap<SpanWord,String[]>>();
        LinkedHashMap<Integer,String> sentenceMap2 = new LinkedHashMap<Integer,String>();
        LinkedHashMap<Integer, Integer> sentenceMap3 = new LinkedHashMap<Integer,Integer>();
        int sentenceCounter = 0;
        
        //do sentence splitting in for loop 
       
        
        Span[] sentenceSpans = SentenceDetector.detectSentenceSpans(everything, "en-sent.bin");
        
        for (Span sentenceSpan : sentenceSpans){
        int sentenceStart = sentenceSpan.getStart();
        int sentenceEnd = sentenceSpan.getEnd();
       sent = everything.substring(sentenceStart, sentenceEnd);
       System.out.println("--------------------------------------------------------------------");
      System.out.println("DEBUG sent: "+sent);
      sentenceMap2.put(sentenceCounter,sent);
      sentenceMap3.put(sentenceCounter, sentenceStart);
      sentenceCounter++;
      //System.out.println("begins at:" + sentenceStart);
      //System.out.println("ends at:" + sentenceEnd);
     
            
      //do parsing on the splitted sentence, this is where the magic happens
        Tree tree = parser.parse(sent);
        parser.setOptionFlags();
       // System.out.println("DEBUG tree: ");
       // tree.pennPrint();
        
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
        
         //iterates trough nps and extracts the phrase spans, matches them with gender and number info from RFTagger, puts them in genderNumberMap
        
        int counter = 0;
       
        List<SpanWord> wordSpans = new ArrayList<SpanWord>();
       
        for (ArrayList<String> subl : nps){
        	//System.out.println("DEBUG subl: "+subl); 
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
        	
        	
        	SpanWord d = new SpanWord("",0,0);
        	
        	if(pos.size()>1){
        		
        		int begin = sentenceStart + pos.get(counter-1);
            	int end = begin +word.length();
        		d = new SpanWord(word,begin,end);
        		wordSpans.add(d);
        		System.out.println("DEBUG word span: "+d.getText()+" "+d.getStartSpan()+" "+d.getEndSpan());
        		
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
        	    	System.out.println("Something went wrong with the matching!");
        	    	}
        		System.out.println("DEBUG word span: "+d.getText()+" "+d.getStartSpan()+" "+d.getEndSpan());
        	}
        	//get number and gender of nps and pps from RFTagger tagged document
        	
        	int y = d.getStartSpan();
        	int wordNumber = map.get(y);
        	System.out.println("DEBUG wordNumber: "+wordNumber);
        	
        	List<String> list2 = Arrays.asList(word.split(" "));
        	List<String> wordTags = new ArrayList<String>();
      	  	int numberOfWords2 = list2.size();
        	String line32 = Files.readAllLines(Paths.get("C:\\Users\\Sabine\\Desktop\\WörkWörk\\15.txt")).get(wordNumber-1);
        	for(int i=0; i<numberOfWords2; i++){
        		line32 = Files.readAllLines(Paths.get("C:\\Users\\Sabine\\Desktop\\WörkWörk\\15.txt")).get(wordNumber-1+i);
        		System.out.println("DEBUG wordTag: "+line32);
        		wordTags.add(line32);
        	}
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
        	
        }
        
        sentenceMap.put(sentenceCounter, genderNumberMap);
        
        }
        
        for( Entry<Integer, String> a :sentenceMap2.entrySet()){
        	String sent1 = a.getValue();
        	int key = a.getKey();
        	System.out.println("LEVEL 1: sent1 "+sent1);
        	//get the nps from the sentences and put them in mnps
        	ArrayList<ArrayList<String>> mnps = new ArrayList<ArrayList<String>>();
        	ArrayList<ArrayList<String>> nps = stringToNpList(sent1);
        	for (ArrayList<String> ksdfgh : nps){
        		mnps.add(ksdfgh);
        	}
        	//loop per sentence, get nps for next sentence and put them in mnps
        	for (int j = key+1; j < Math.min(sentenceMap2.size(), key+5); j++){
        		String sent2 = sentenceMap2.get(j);
        		System.out.println("LEVEL 2: sent2" + sent2);
        		for (ArrayList<String> abc : stringToNpList(sent2)){
        			mnps.add(abc);
        		}
        	}
        	// loop through mnps and compare the single items
        	for (int k = 0; k < mnps.size(); k++){
        		for (int l = k+1; l < mnps.size(); l++){
        			System.out.println("np1:"+mnps.get(k));
        			System.out.println("np2:"+mnps.get(l));
        			if (compareLists(mnps.get(k),mnps.get(l))){
        				System.out.println("FOUND MATCHING ITEMS:"+"ITEMS GO HERE");
        			}
        			//if edit distance is smaller than X, also print/consider as referring to the same thing
        		}
        	}
        	
        }
        
        //Sliding window walk trough sentences and match
       /* int i = 0;
        int windowSize = 3;
        
        
        for (int x=0; x<sentenceMap.size(); x++){
        	List<SpanWord> npsInWindow = new ArrayList<SpanWord>();
        	
        	for(int y=0; y<windowSize; y++){
        	
        		HashMap<SpanWord,String[]> bla = sentenceMap.get(x+y);
        		List<SpanWord> npsInSentence = new ArrayList<SpanWord>();
        		
        		for(Entry<SpanWord, String[]> entry : genderNumberMap.entrySet()){
        			
        			SpanWord key = entry.getKey();
        			npsInSentence.add(key);
        			System.out.println(key.getText());
        		}
        		npsInWindow.addAll(npsInSentence);
        			
        		}
        	}*/
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
        
        
    // do it with indexes only, not with the texts, okay?
    
    public static  ArrayList<ArrayList<String>> stringToNpList (String sent){
    Tree tree = parser.parse(sent);
    parser.setOptionFlags();
   // System.out.println("DEBUG tree: ");
   // tree.pennPrint();
    
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
                          
    }
