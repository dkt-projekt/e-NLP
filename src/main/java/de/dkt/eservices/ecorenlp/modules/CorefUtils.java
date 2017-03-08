package de.dkt.eservices.ecorenlp.modules;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import de.dkt.eservices.eopennlp.modules.SentenceDetector;
import de.dkt.eservices.erattlesnakenlp.linguistic.SpanWord;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.parser.common.ParserGrammar;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.Tokenizer;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.trees.HeadFinder;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.international.negra.NegraHeadFinder;
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.trees.tregex.TregexPattern;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.Pair;
import opennlp.tools.util.Span;

public class CorefUtils {
	
 public static void main(String[] args) throws Exception {
	 
	 getWordNumbers("C:\\Users\\Sabine\\Desktop\\WörkWörk\\de.trial(2).txt");
//	 boolean val = isAcronym("SPD","Sozialdemokartische Partei Deutschlands");
//	 System.out.println("VAL: "+val);
//		 
//
//	 LexicalizedParser lexParser = LexicalizedParser.loadModel("edu/stanford/nlp/models/lexparser/germanPCFG.ser.gz","-maxLength", "70");
//	 Tree tree = lexParser.parse("Vielleicht die Frage was von Bremen zu halten ist: \"\"Wunderbar gemütlich\"\" sagt Alexander in perfektem Deutsch;");
//	 traverseBreadthFirst(tree);
 }
	 //Tree tree = lexParser.parse("Vielleicht die Frage was von Bremen zu halten ist: \"\"Wunderbar gemütlich\"\" sagt Alexander in perfektem Deutsch;");
	
	public static LexicalizedParser parser = null;
	private final static String EN_PCFG_MODEL = "edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz";        
    private final static String DE_PCFG_MODEL = "edu/stanford/nlp/models/lexparser/germanPCFG.ser.gz";

    
 static  int i = 1;
public static  TreeMap<Integer,CorefMention> traverseBreadthFirst(Tree tree, SpanWord sentence) throws FileNotFoundException{
		
//		tree.pennPrint();
	TreeMap<Integer,CorefMention> leafNumberMap = new TreeMap<Integer, CorefMention>();
		Queue<Tree> queue = new LinkedList<Tree>() ;
		    if (tree == null)
		        return null;
		    
			 

		    queue.add(tree);
		    while(!queue.isEmpty()){
		    	
		        Tree node = queue.remove();
		        
		        String s = "NP<PPER";
		        TregexPattern p = TregexPattern.compile(s);
		        TregexMatcher m = p.matcher(node);
		        
		        m.find();
		        
		        ArrayList<ArrayList<String>> nps = new ArrayList<ArrayList<String>>();
		        if ((node.label().value().equals("NP")||node.label().value().equals("PPER"))&&(m.getMatch()== null)){
		        	
		        //if (node.label().value().equals("NP")){
		        	ArrayList<String> npAsList = new ArrayList<String>();
		        	String modifiers = "";
		        	for (Tree it : node.flatten()){
        				if ((it.isLeaf())){
        					npAsList.add(it.pennString().trim());
        				}
        				
        				if(it.label().value().equals("ADJA")||it.label().value().equals("PDAT")||it.label().value().equals("PIAT")||
        						it.label().value().equals("PIDAT")||it.label().value().equals("PPOSAT")||it.label().value().equals("PRELAT")||
        						it.label().value().equals("PWAT")){
        					for (Tree et : it.flatten()){
        						if ((et.isLeaf())){
            					modifiers = modifiers+" "+et.pennString().trim();
            				}}
        				}
        			}
		        	
        		
		        	String word = new String(); 
	            	word =(npAsList.get(0));
	            	for(int x=1; x<npAsList.size(); x++){
	            		word = word + " " +(npAsList.get(x));
	            	    
	            	}
	            	
	            	String nodeHead = "";
	            	nodeHead = determineHead(node);
	            	List<Word> wl = node.yieldWords();
	            	int begin = wl.get(0).beginPosition()+sentence.getStartSpan();
	            	int end = wl.get(wl.size()-1).endPosition()+sentence.getStartSpan();
	            	Span sp = new Span(begin, end);
	            	
   	
	            	
	       		  
		        			
//		        	leafNumberMap.put(i, new CorefMention(i, word, sp.getStart(), sp.getEnd(), nodeHead, modifiers, tree,
//		        			determineGender(word), determineNumber(word), determinePerson(word)));
		        	leafNumberMap.put(i, new CorefMention(i, word, sp.getStart(), sp.getEnd(), nodeHead, modifiers, tree));
		        	
		        	
		        }
		        i++;
		        if(node.children() != null) 
		        	for(Tree kid : node.children()){
		        		queue.add(kid);}
		    

		}
	

			return leafNumberMap;
	}

public static LinkedHashSet<SpanWord> getWordSpans(LinkedHashSet<CorefMention> mentions, SpanWord sentence){
//	for (CorefMention m : mentions){
//		System.out.println("all the mentions: "+m.getContents()+" "+m.getMentionID());
//	}
//	System.out.println("--------------------------------------------");
	LinkedHashSet<SpanWord>	wordSpans = new LinkedHashSet<>();
	int counter = 0;
	
	for (CorefMention mention : mentions){
//	String word = mention.getContents().replace("''", "").replace("``","").replace(" :",":").replace(" .",".").trim();
//	word = word.replace("\\s+", "").replace("\t", "").trim();
	//System.out.println("Original Sentence: "+sentence.getText());
	String word = mention.getContents().replaceAll("\\p{Punct}", "").replaceAll("\"", "").replaceAll("„", "").replaceAll("“", "").replaceAll("-", "").replaceAll("\\s+", " ");
	//stem.out.println("WORD: "+word);
 	List<Integer> pos = new ArrayList<>();

	String sent = sentence.getText().replaceAll("\\p{Punct}", "").replaceAll("\\s+", " ").replaceAll("„", "").replaceAll("“", "").replace("-", "");
	//System.out.println("SENT: "+sent);
	int sentenceStart = sentence.getStartSpan();
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
//		pos.forEach(k->System.out.println("pos: "+k));
//		System.out.println("Counter: "+counter);
		int begin = sentenceStart + pos.get(counter-1);

    	int end = begin +word.length();
		d = new SpanWord(word,begin,end);
		wordSpans.add(d);

		
	}else{
		String inputStr = sent;
	    String patternStr = word;

	    Pattern pattern = Pattern.compile("\\b"+patternStr+"\\b");
	    Matcher matcher = pattern.matcher(inputStr.trim());
	    if(matcher.find()){

	    	int begin = sentenceStart + matcher.start();
	    	int end = begin +word.length();
	    	d = new SpanWord(word,begin,end);
	    	wordSpans.add(d);}
	    else{ 
	    	

	    	}

	   
	}
	}
	return wordSpans;
	
}

public static HashMap<Span, String> traverseTreeForNPs(Tree tree, HashMap<Span, String> npHash){
//	  System.out.println("DEBUG traverseTreeForNPs input tree: ");
//	  tree.pennPrint();
  	  
  	  for (Tree subtree : tree.getChildrenAsList()){
  	   
  	   if (subtree.label().toString().equals("NP") || subtree.label().toString().equals("MPN") || subtree.label().toString().equals("CNP")){ // conjoined NPs have the label CNP. Do I want to include these here too? And I probably want to include PPs and some other stuff as well
  	    List<Word> wl = subtree.yieldWords();
  	    int begin = wl.get(0).beginPosition();
  	    int end = wl.get(wl.size()-1).endPosition();
  	    Span sp = new Span(begin, end);
  	    //HeadFinder hf = new SemanticHeadFinder(); // may want to experiment here with different types of headfinders (http://nlp.stanford.edu/nlp/javadoc/javanlp/edu/stanford/nlp/trees/HeadFinder.html)
  	    HeadFinder hf = new NegraHeadFinder();
  	    npHash.put(sp, subtree.headTerminal(hf).toString());
//  	    System.out.println("DEBUG traverseTreeForNPs intermediate: "+subtree.headTerminal(hf).toString());
  	   }
  	   if (!(subtree.isPreTerminal())){
  	    traverseTreeForNPs(subtree, npHash);
  	   }
  	  }
  	  
  	  return npHash;
  	 }

public static String head = "";
//adaption of the traverseTreeForNPs to extract the head of a mention
public static String determineHead(Tree tree){
	 
	//TODO add some preprocessing, because the heads are not always helpful for coreference
	  
	  for (Tree subtree : tree.getChildrenAsList()){
 	    HeadFinder hf = new NegraHeadFinder();
 	    head = subtree.headTerminal(hf).toString();
 	 //System.out.println("DEBUG intermediate: "+head);
 	   if (!(subtree.isPreTerminal())){
 	    determineHead(subtree);
 	   }
 	  }
	  
	  //System.out.println("Debug head: "+head);
	  return head;
}

private static void initLexParser(String lang){
	if (lang.equalsIgnoreCase("en")){
		parser = LexicalizedParser.loadModel(EN_PCFG_MODEL);
	}
	else if (lang.equalsIgnoreCase("de")){
		parser = LexicalizedParser.loadModel(DE_PCFG_MODEL,"-maxLength", "70");
	}
	
}

public static void traverse (Tree tree, Tree wholet){
	//System.out.println("---------------------------------------------");
	for (Tree kid : tree.children()) {
        //kid.pennPrint();
        //System.out.println("DEBUG nodeNUmbers:"+kid.nodeNumber(wholet));
        //System.out.println("DEBUG nodeNUmbers:"+kid.getNodeNumber(1));
        //System.out.println("label :"+tree.label());
        if (!kid.isLeaf()){
        	traverse(kid,wholet);
        }
      }
	
//    if (!tree.isLeaf()){
//        for(Tree kid : tree.children()){
//        	traverse(kid);
//        };
//    }else{
//    	;
//    }
//    //tree.pennPrint();
   
}
public static String lexiconPath = "C:\\Users\\Sabine\\Downloads\\german-pos-dict-1.1\\german-pos-dict-1.1\\dictionary.txt";
public static String determineGender(String word) throws FileNotFoundException{
	Scanner txtscan = new Scanner(new File("C:\\Users\\Sabine\\Downloads\\german-pos-dict-1.1\\german-pos-dict-1.1\\dictionary.txt"));
	String ret = "NOG";
	int masc = 0;
	int fem = 0;
	int neut = 0;
	String[] words = word.split(" ");

	
	for (String w : words){
		while(txtscan.hasNextLine()){
			String str = txtscan.nextLine();
			if(str.matches("^(?i)"+w+"\\b.*")){
				if(str.contains("NEU")){
	    		neut++;
				}if(str.contains("MAS")){
	    		masc++;
				}if(str.contains("FEM")){
	    		fem++;
				}
			}
		}
		
//        char c = Character.toLowerCase(w.charAt(0)); //get the first char.
//        switch(c){
//            case 'a': searchLexicon()
//            case 'b': System.out.println("a");
//            case 'c': System.out.println("a");
//            case 'd': System.out.println("a");
//            case 'e': System.out.println("a");
//            case 'f': System.out.println("a");
//            case 'g': System.out.println("a");
//            case 'h': System.out.println("a");
//            case 'i': System.out.println("a");
//            case 'j': System.out.println("a");
//            case 'k': System.out.println("a");
//            case 'l': System.out.println("a");
//            case 'm': System.out.println("a");
//            case 'n': System.out.println("a");
//            case 'o': System.out.println("a");
//            case 'p': System.out.println("a");
//            case 'q': System.out.println("a");
//            case 'r': System.out.println("a");
//            case 's': System.out.println("a");
//            case 't': System.out.println("a");
//            case 'u': System.out.println("a");
//            case 'v': System.out.println("a");
//            case 'w': System.out.println("a");
//            case 'x': System.out.println("a");
//            case 'y': System.out.println("a");
//            case 'z': System.out.println("a");
//        }
	}
	if (masc>0 && fem<=0 && neut<=0){
		
		ret="MAS";
	}if (fem>0 && masc<=0&& neut<=0){
	
		ret="FEM";
	}if (neut>0 && masc<=0&& fem<=0){
		
		ret="NEU";
	}
	return ret;
}

public static int[] searchLexicon ( int beginNumber, int endNumber, String w) throws IOException{

	int[] output = new int[3];
	
	for(int i = 0; beginNumber<=i&& i<=endNumber;i++){
		try (Stream<String> lines = Files.lines(Paths.get(lexiconPath))) {
		    String line = lines.skip(i).findFirst().get();
		    if(line.matches("^(?i)"+w+"\\b.*")){
				if(line.contains("NEU")){
	    		output[0]++;
				}if(line.contains("MAS")){
				output[1]++;
				}if(line.contains("FEM")){
				output[2]++;
				}
			}
		}
	}
	return output;
	
}

public static String determineNumber(String word) throws FileNotFoundException{
	Scanner txtscan = new Scanner(new File("C:\\Users\\Sabine\\Downloads\\german-pos-dict-1.1\\german-pos-dict-1.1\\dictionary.txt"));
	int sg = 0;
	int pl = 0;
	String ret = "NON";
	String[] words = word.split(" ");
	
	for (String w : words){
		while(txtscan.hasNextLine()){
			String str = txtscan.nextLine();
			if(str.matches("^(?i)"+w+"\\b.*")){
				if(str.contains("PLU")){
	    		pl++;
				}if(str.contains("SIN")){
	    		sg++;
				}
			}
		}
	}
	if (sg>0 && pl<=0){
		
		ret="SIN";
	}if (pl>0 && sg<=0){
	
		ret="PLU";
	}
	return ret;
}

public static Set<String> determinePerson(String word) throws FileNotFoundException{
	Set<String> ret = new HashSet<>();
	if (isPronoun(word)){
		if (word.equalsIgnoreCase("ich")||word.equalsIgnoreCase("meiner")||word.equalsIgnoreCase("mir")||word.equalsIgnoreCase("mich")){
			ret.add("1SI");
		}if (word.equalsIgnoreCase("du")||word.equalsIgnoreCase("deiner")||word.equalsIgnoreCase("dir")||word.equalsIgnoreCase("dich")){
			ret.add("2SI");
		}if (word.equalsIgnoreCase("er")||word.equalsIgnoreCase("seiner")||word.equalsIgnoreCase("ihn")||word.equalsIgnoreCase("ihm")){
			ret.add("3SI");
		}if (word.equalsIgnoreCase("sie")||word.equalsIgnoreCase("ihrer")||word.equalsIgnoreCase("ihr")){
			ret.add("3SI");
		}if (word.equalsIgnoreCase("es")||word.equalsIgnoreCase("seiner")||word.equalsIgnoreCase("ihm")){
			ret.add("3SI");
		}if (word.equalsIgnoreCase("wir")||word.equalsIgnoreCase("unser")||word.equalsIgnoreCase("uns")){
			ret.add("1PL");
		}if (word.equalsIgnoreCase("ihr")||word.equalsIgnoreCase("euer")||word.equalsIgnoreCase("euch")){
			ret.add("2PL");
		}if (word.equalsIgnoreCase("sie")||word.equalsIgnoreCase("ihrer")||word.equalsIgnoreCase("ihnen")){
			ret.add("3PL");
		}
	}
	
	
	return ret;
}


public static List<String> stopwordsAsList = Arrays.asList("aber","als","am","an","auch","auf","aus","bei","bin","bis","bist","da","dadurch","daher","darum","das","daß","dass","dein","deine","dem","den","der","des","dessen","deshalb","die","dies","dieser","dieses","doch","dort","du","durch","ein","eine","einem","einen","einer","eines","er","es","euer","eure","für","hatte","hatten","hattest","hattet","hier	hinter","ich","ihr","ihre","im","in","ist","ja","jede","jedem","jeden","jeder","jedes","jener","jenes","jetzt","kann","kannst","können","könnt","machen","mein","meine","mit","muß","mußt","musst","müssen","müßt","nach","nachdem","nein","nicht","nun","oder","seid","sein","seine","sich","sie","sind","soll","sollen","sollst","sollt","sonst","soweit","sowie","und","unser	unsere","unter","vom","von","vor","wann","warum","was","weiter","weitere","wenn","wer","werde","werden","werdet","weshalb","wie","wieder","wieso","wir","wird","wirst","wo","woher","wohin","zu","zum","zur","über");
public static String stopwords = "aber "+ "als "+ "am "+ "an "+ "auch "+ "auf "+ "aus "+ "bei "+ "bin "+ "bis "+"bist "+"da "+"dadurch "+"daher "+"darum "+"das "+"daß "+"dass "+"dein "+"deine "+"dem "+"den "+"der "+"des "+"dessen "+"deshalb "+"die "+"dies "+"dieser "+"dieses "+"doch "+"dort "+"du "+"durch "+"ein "+"eine "+"einem "+"einen "+"einer "+"eines "+"er "+"es "+"euer "+"eure "+"für "+"hatte "+"hatten "+"hattest "+"hattet "+"hier 	hinter "+"ich "+"ihr "+"ihre "+"im "+"in "+"ist "+"ja "+"jede "+"jedem "+"jeden "+"jeder "+"jedes "+"jener "+"jenes "+"jetzt "+"kann "+"kannst "+"können "+"könnt "+"machen "+"mein "+"meine "+"mit "+"muß "+"mußt "+"musst "+"müssen "+"müßt "+"nach "+"nachdem "+"nein "+"nicht "+"nun "+"oder "+"seid "+"sein "+"seine "+"sich "+"sie "+"sind "+"soll "+"sollen "+"sollst "+"sollt "+"sonst "+"soweit "+"sowie "+"und "+"unser 	unsere "+"unter "+"vom "+"von "+"vor "+"wann "+"warum "+"was "+"weiter "+"weitere "+"wenn "+"wer "+"werde "+"werden "+"werdet "+"weshalb "+"wie "+"wieder "+"wieso "+"wir "+"wird "+"wirst "+"wo "+"woher "+"wohin "+"zu "+"zum "+"zur "+"über  ";

public static boolean isStopWord(String word){
	if(stopwords.matches(".*\\b"+word+"\\b.*")){
		return true;
	}else{
		return false;
	}
}

public static String filterStopWordsFromString(String string){
	 for ( String stopWord : stopwordsAsList) {
	        string = string.replaceAll("(?i)\\b[^\\w -]*" + stopWord + "[^\\w -]*\\b", "");
	    }
	return string;
}

public static String pronouns = "ich "+"mich "+"mir "+"mich "+"du "+"dich "+"dir "+"er "+"ihn "+"ihm "+"sie "+"ihr "+"es "+"wir "+"uns "+"ihr "+"euch "+"sie "+"ihnen "+"unser "+"euer "+"meiner "+"deiner "+"seiner "+"ihrer";

public static boolean isPronoun(String word){
	if(pronouns.matches(".*\\b"+word+"\\b.*")){
		return true;
	}else{
		return false;
	}
}

public static String findModifiers(CorefMention Mention){
	
	return null;
}

public static boolean isDemonym(String one, String two) throws FileNotFoundException{
	Scanner txtscan = new Scanner(new File("C:\\Users\\Sabine\\Desktop\\WörkWörk\\demonyms.txt"));
	boolean ret = false;
	
	
	while(txtscan.hasNextLine()){
	    String str = txtscan.nextLine();
	    if(str.indexOf(one) != -1){
	    	if(str.matches(".*\\b"+one+"\\b.*")&&str.matches(".*\\b"+two+"\\b.*")){
	    		ret=true;
	    	}
	    }
	}
	return ret;
}

public static boolean isAcronym(String one, String two){
	boolean ret = false;
	String acro1 = "";
	String acro2 = "";
	for (int i = 0; i < one.length(); i++) {
	    char c = one.charAt(i);
	    acro1 += Character.isUpperCase(c) ? c : ""; 
	}
	for (int i = 0; i < two.length(); i++) {
	    char c = two.charAt(i);
	    acro2 += Character.isUpperCase(c) ? c : ""; 
	}
	if (one.equals(acro2)||two.equals(acro1)){
		ret=true;
	}
	return ret;
}

public static void findTreePattern(Tree tree, TregexPattern tgrepPattern) {
    try {
      TregexMatcher m = tgrepPattern.matcher(tree);
      while (m.find()) {
        Tree t = m.getMatch();
        Tree np1 = m.getNode("m1");
        Tree np2 = m.getNode("m2");
        Tree np3 = null;
        if(tgrepPattern.pattern().contains("m3")) np3 = m.getNode("m3");
        System.out.println("BINGO");
        if(np3!=null) System.out.println("BINGO2");;
      }
    } catch (Exception e) {
      // shouldn't happen....
      throw new RuntimeException(e);
    }
  }

public static TreeMap<Integer,SpanWord> sentenceMap = new TreeMap<Integer,SpanWord>();
public static LexicalizedParser lexParser = LexicalizedParser.loadModel("edu/stanford/nlp/models/lexparser/germanPCFG.ser.gz","-maxLength", "70");
public static Map<Integer,Integer[]> getWordNumbers(String inputFile) throws IOException{
	 
	SpanWord span = Corefinizer.getDocumentSpan(inputFile);
	String everything = span.getText();

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

	 	 //everything that happens in this loop works per sentence
	 Map<Integer,Integer[]> wordIndexList = new TreeMap<>();
	 int i = 1;
	 
	 for (Map.Entry<Integer, SpanWord> entry : sentenceMap.entrySet()){
 
		 String sentence = entry.getValue().getText();
		
//		 @SuppressWarnings("unchecked")
//		List<CoreLabel> what = (List<CoreLabel>) lexParser.tokenize(sentence);
	
		 
		 
		 List<CoreLabel> what = tokenize(sentence);
	// System.out.println("Tokenize "+what.toString());

		 
		
		 for (CoreLabel w : what){
			 //System.out.println(w.toString()+" "+w.beginPosition()+"|"+w.endPosition()+" "+entry.getValue().getStartSpan());
         	int begin =  w.beginPosition()+entry.getValue().getStartSpan();
         	int end = w.endPosition()+entry.getValue().getStartSpan();
//         	System.out.println("Begin: "+begin+" End: "+end);
         	Integer[] arr = new Integer[2];
         	arr[0]= begin;
         	arr[1]= end;
//         	System.out.println(Arrays.toString(arr));
         	wordIndexList.put(i,arr);
         	i++;
	 }
	}
//	 for (Entry<Integer, Integer[]> p : wordIndexList.entrySet()){
//		 Integer[] array = p.getValue();
//		 System.out.println(Arrays.toString(array));
//	 }
	 return wordIndexList;
}

private final static TokenizerFactory<CoreLabel> tokenizerFactory = PTBTokenizer.factory(new CoreLabelTokenFactory(), "invertible=true");

public static List<CoreLabel> tokenize(String str) {
    Tokenizer<CoreLabel> tokenizer =
        tokenizerFactory.getTokenizer(
            new StringReader(str));    
    return tokenizer.tokenize();
}




}
