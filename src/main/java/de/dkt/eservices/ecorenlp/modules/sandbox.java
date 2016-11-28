package de.dkt.eservices.ecorenlp.modules;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hp.hpl.jena.rdf.model.Model;

import de.dkt.common.niftools.NIFReader;
import de.dkt.eservices.eopennlp.modules.SentenceDetector;
import de.dkt.eservices.erattlesnakenlp.linguistic.SpanWord;
import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.trees.HeadFinder;
import edu.stanford.nlp.trees.LabeledScoredTreeNode;
import edu.stanford.nlp.trees.SemanticHeadFinder;
import edu.stanford.nlp.trees.Tree;
import opennlp.tools.util.Span;

public class sandbox {
	public static LexicalizedParser parser = null;
	private final static String EN_PCG_MODEL = "edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz";        
    private final static String DE_PCG_MODEL = "edu/stanford/nlp/models/lexparser/germanPCFG.ser.gz";
	
    public static void main(String[] args) throws IOException{
    	
    	//And then you call it like this:
           LexicalizedParser lexParser = LexicalizedParser.loadModel("edu/stanford/nlp/models/lexparser/germanPCFG.ser.gz","-maxLength", "70");
        String sentence = "Der Hund und sein Freund sind hier und die Katze. Barack Obama, Präsident der U.S.A, besuchte heute Berlin.";
           //String sentence = "Barack Obama, Präsident der U.S.A, besuchte heute Berlin.";
        	Tree tree = lexParser.parse(sentence);

        	HashMap<Span, String> npHash = traverseTreeForNPs(tree, new HashMap<Span, String>());
        	for (Span sp : npHash.keySet()){
        	 //System.out.println("NP:" + sentence.substring(sp.getBegin(), sp.getEnd()));
        	 //System.out.println("Indices:" + sp.getBegin() + "|" + sp.getEnd());
        	 System.out.println("HEAD:" + npHash.get(sp));
        	}
   
    /*String sent = "er er.";
    String newWord = "er";
    String word = "er";
//    if (sent.matches(newWord) && sent.indexOf(newWord) != sent.lastIndexOf(newWord)){
		//what happens if a word is in the sentence more than once?
		/*int index = sent.indexOf(word);
		while (index >= 0) {
		    System.out.println("---------------------------------------DEBUG yamyam: ----------------------------\n"+index);
		    System.out.println(word);
		    index = sent.indexOf(word, index + 1);
		}
//	}
    
    //Str = "Er gilt als der Mann dem die schwierigsten Missionen gelingen: Blitzschnell erobert er Gebiete zurück die das syrische Regime schon verloren glaubt.";
    String str = "Er er er Tiger";
    int res = 0;
    Pattern p = Pattern.compile("(?i)\\b"+word+"\\b");
    Matcher m = p.matcher(str);
    while (m.find()) {
      res= m.groupCount();
    }
    System.out.println(res);
    
    if (str.contains(word) && str.indexOf(word) != str.lastIndexOf(word)){
    Matcher n = Pattern.compile("(?i)\\b"+word+"\\b").matcher(str);
    List<Integer> pos = new ArrayList<Integer>();
    while (n.find())
    {
        pos.add(n.start());
    }
    System.out.println(pos);
    
    sent = "Der 49-Jährige weiß warum er warnt.";
    word = "er";
    int begin = 0 + sent.indexOf(word);
	int end = begin +word.length();
	SpanWord d = new SpanWord(word,begin,end);
	
	System.out.println("DEBUG word span: "+d.getText()+" "+d.getStartSpan()+" "+d.getEndSpan());
	
	 String inputStr = sent;
	    String patternStr = word;
	    Pattern pattern = Pattern.compile("\\b"+patternStr+"\\b");
	    Matcher matcher = pattern.matcher(inputStr);
	    if(matcher.find()){

	    System.out.println(matcher.start());//this will give you index
	    }
    
    }*/
    //boolean a = compareListsSpan("ein Labrador","der Labrador");
    //System.out.println(a);
    	
   /* initLexParser("de");
    tree = parser.parse("Barack Obama, Präsident der vereinigten Staaten, besuchte heute Berlin.");
    parser.setOptionFlags();
   System.out.println("DEBUG tree: ");
   tree.pennPrint();
   
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
   	
     for (ArrayList<String> subl : nps){
   	//System.out.println("DEBUG subl: "+subl); 
   	String word = new String(); 
   	word =(subl.get(0));
   	for(int x=1; x<subl.size(); x++){
   		word = word + " " +(subl.get(x));
   	System.out.println("word: "+word);    
   	}}}*/
 
   
    }
  public static HashMap<Span, String> traverseTreeForNPs(Tree tree, HashMap<Span, String> npHash){

    	  
    	  for (Tree subtree : tree.getChildrenAsList()){
    	   
    	   if (subtree.label().toString().equals("NP")){ // conjoined NPs have the label CNP. Do I want to include these here too? And I probably want to include PPs and some other stuff as well
    	    List<Word> wl = subtree.yieldWords();
    	    int begin = wl.get(0).beginPosition();
    	    int end = wl.get(wl.size()-1).endPosition();
    	    Span sp = new Span(begin, end);
    	    HeadFinder hf = new SemanticHeadFinder(); // may want to experiment here with different types of headfinders (http://nlp.stanford.edu/nlp/javadoc/javanlp/edu/stanford/nlp/trees/HeadFinder.html)
    	    npHash.put(sp, subtree.headTerminal(hf).toString());
    	   }
    	   if (!(subtree.isPreTerminal())){
    	    traverseTreeForNPs(subtree, npHash);
    	   }
    	  }
    	  
    	  return npHash;
    	 }
    
    	
    
    private static void detectAppositive(String sent){
    	
    }
    
    private static void initLexParser(String lang){
    	if (lang.equalsIgnoreCase("en")){
    		parser = LexicalizedParser.loadModel(EN_PCG_MODEL);
    	}
    	else if (lang.equalsIgnoreCase("de")){
    		parser = LexicalizedParser.loadModel(DE_PCG_MODEL,"-maxLength", "70");
    	}
    	
    }

    
public static boolean compareListsSpan(String w1, String w2){
    	
    	//String w1 = s1.getText();
    	//String w2 = s2.getText();
    	boolean retValue = false;
    	if (w1.equalsIgnoreCase(w2)){
    		retValue = true;
    	}

    	else if (!w1.contains(" ") && !w2.contains(" ")&&w1.length() > 4 && w2.length()> 4){
    		String naiveStemW1 = (w1.substring(0, (Integer)w1.length() - w1.length()/5)).toLowerCase();
    		String naiveStemW2 = (w2.substring(0, (Integer)w2.length() - w2.length()/5)).toLowerCase();
    		if(naiveStemW1.contains(naiveStemW2) || naiveStemW2.contains(naiveStemW1)){
    			System.out.println("DEBUGGING STEMMED APPROACH:" + naiveStemW1 + "\t" + naiveStemW2);
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
        			System.out.println("DEBUGGING STEMMED APPROACH2a:" + naiveStemW1 + "\t" + naiveStemW2);
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
        			System.out.println("DEBUGGING STEMMED APPROACH2b:" + naiveStemW1 + "\t" + naiveStemW2);
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
        			System.out.println("DEBUGGING STEMMED APPROACH2c:" + naiveStemW1 + "\t" + naiveStemW2);
        			retValue = true;
        		}
    	} }
    	
    	return retValue;
    }
}
