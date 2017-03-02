package de.dkt.eservices.ecorenlp.modules;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.dkt.eservices.erattlesnakenlp.linguistic.SpanWord;
import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.trees.HeadFinder;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.international.negra.NegraHeadFinder;
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.trees.tregex.TregexPattern;
import opennlp.tools.util.Span;

public class CorefUtils {
	
 public static void main(String[] args) throws Exception {
	 boolean val = isAcronym("SPD","Sozialdemokartische Partei Deutschlands");
	 System.out.println("VAL: "+val);
		 
	  }
	
	public static LexicalizedParser parser = null;
	private final static String EN_PCFG_MODEL = "edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz";        
    private final static String DE_PCFG_MODEL = "edu/stanford/nlp/models/lexparser/germanPCFG.ser.gz";

    
 static  int i = 1;
public static  TreeMap<Integer,CorefMention> traverseBreadthFirst(Tree tree){
		
//		System.out.println("DEBUG Tree: ");
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
	            	
	            	
	            	
	       		 
		        	leafNumberMap.put(i, new CorefMention(i, word, 1, 1, nodeHead, modifiers, tree));
		        	
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

public static String determineGender(String word) throws FileNotFoundException{
	Scanner txtscan = new Scanner(new File("C:\\Users\\Sabine\\Downloads\\german-pos-dict-1.1\\german-pos-dict-1.1\\dictionary.txt"));
	String ret = "NOG";
	
	while(txtscan.hasNextLine()){
	    String str = txtscan.nextLine();
	    if(str.indexOf(word) != -1){
	    	if(str.contains("NEU")){
	    		ret="NEU";
	    	}if(str.contains("MAS")){
	    		ret="MAS";
	    	}if(str.contains("FEM")){
	    		ret="FEM";
	    	}
	    }
	}
	return ret;
}

public static String determineNumber(String word) throws FileNotFoundException{
	Scanner txtscan = new Scanner(new File("C:\\Users\\Sabine\\Downloads\\german-pos-dict-1.1\\german-pos-dict-1.1\\dictionary.txt"));
	String ret = "NON";
	
	while(txtscan.hasNextLine()){
	    String str = txtscan.nextLine();
	    if(str.indexOf(word) != -1){
	    	if(str.contains("PLU")){
	    		ret="PLU";
	    	}if(str.contains("SIN")){
	    		ret="SIN";
	    	}
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



}
