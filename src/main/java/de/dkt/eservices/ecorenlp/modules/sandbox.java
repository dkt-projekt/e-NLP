package de.dkt.eservices.ecorenlp.modules;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.ClassPathResource;

import com.hp.hpl.jena.rdf.model.Model;

import de.dkt.common.niftools.NIFManagement;
import de.dkt.common.niftools.NIFReader;
import de.dkt.eservices.eopennlp.modules.SentenceDetector;
import de.dkt.eservices.eopennlp.modules.Tokenizer;
import de.dkt.eservices.erattlesnakenlp.linguistic.SpanWord;
import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.trees.HeadFinder;
import edu.stanford.nlp.trees.LabeledScoredTreeNode;
import edu.stanford.nlp.trees.SemanticHeadFinder;
import edu.stanford.nlp.trees.international.negra.NegraHeadFinder;
import eu.freme.common.conversion.rdf.RDFConstants.RDFSerialization;
import eu.freme.common.exception.BadRequestException;
import edu.stanford.nlp.trees.Tree;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.util.Span;

public class sandbox {
	public static LexicalizedParser parser = null;
	private final static String EN_PCFG_MODEL = "edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz";        
    private final static String DE_PCFG_MODEL = "edu/stanford/nlp/models/lexparser/germanPCFG.ser.gz";
	
    public static void main(String[] args) throws IOException{
    	
    	
    	LexicalizedParser lexParser = LexicalizedParser.loadModel("edu/stanford/nlp/models/lexparser/germanPCFG.ser.gz","-maxLength", "70");
  	String sentence = "Barack Obama, Präsident der U.S.A, besuchte heute Berlin.";
//  	String sentence = "Sein Blick ist im vorübergehn der Stäbe so müde geworden, dass er nichts mehr hält. Ihm ist als ob es tausend Stäbe gäbe und hinter tausen Stäben keine Welt.";
    	;
 //   	getOrderFromTree(tree);
 //       traverse(tree,tree);
    	String secondSentence = "Angela Merkel, Kanzlerin der Herzen, war auch mit von der Partie";
    	String[] array = {sentence, secondSentence};
    	for (String a :array){
    		Tree tree = lexParser.parse(a);
    		traverseBreadthFirst(tree);
    	}
    	
    	
        
    	
    
//           LexicalizedParser lexParser = LexicalizedParser.loadModel("edu/stanford/nlp/models/lexparser/germanPCFG.ser.gz","-maxLength", "70");
//           //String sentence = "Der Hund und sein Freund sind hier und die Katze. Barack Obama, Präsident der U.S.A, besuchte heute Berlin.";
//           String sentence = "Barack Obama, Präsident der U.S.A, besuchte heute Berlin.";
//           Tree tree = lexParser.parse(sentence);
//           
//           ///** debugging
//           System.out.println("The Parse Tree for " + sentence + " is: ");
//           tree.pennPrint();
//           //**/
//        	
//
//        	HashMap<Span, String> npHash = traverseTreeForNPs(tree, new HashMap<Span, String>());
//        	for (Span sp : npHash.keySet()){
//        	  System.out.println("NP:" + sentence.substring(sp.getStart(), sp.getEnd()));
//        	  System.out.println("Indices:" + sp.getStart() + "|" + sp.getEnd());
//        	  System.out.println("HEAD:" + npHash.get(sp));
//        	}
    	
//    	String modelsDirectory = "trainedModels/";
//		InputStream modelIn = null;
//		ClassPathResource cpr;
//		POSModel POSModel = null;
//		try{
//			//cpr = new ClassPathResource("C:\\Users\\pebo01\\workspace\\DktBroker-standalone\\target\\classes\\taggers\\english-left3words-distsim.tagger");
//			File f = new File("C:\\Users\\pebo01\\workspace\\DktBroker-standalone\\target\\classes\\taggers\\english-left3words-distsim.tagger");
//			modelIn = new FileInputStream(f);
//			POSModel = new POSModel(modelIn);
//		}
//		catch(IOException e){
//			throw new BadRequestException(e.getMessage());
//		}
//    	
    	
    	
//    	String nifFileMap = "C:\\Users\\pebo01\\Desktop\\data\\IAFL2017\\nifs";
//    	File df = new File(nifFileMap);
//    	ArrayList<Model> modelsList = new ArrayList<Model>();
//    	//String prefix = null;
//    	for (File f : df.listFiles()){
//			String fileContent = readFile(f.getAbsolutePath(), StandardCharsets.UTF_8);
//			try {
//				Model nifModel = NIFReader.extractModelFromFormatString(fileContent, RDFSerialization.TURTLE);
//				modelsList.add(nifModel);
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//    	}
//    	String prefix = "http://dkt.dfki.de/clintonCorpus";
//    	//Model nifCollection = NIFManagement.createCollectionFromDocuments(prefix, modelsList);
//
//    	PrintWriter out = new PrintWriter(new File("C:\\Users\\pebo01\\Desktop\\debug.txt"));
//		//out.println(NIFReader.model2String(nifCollection, RDFSerialization.TURTLE));
//		out.close();
//    	
    }
    
    
    static String readFile(String path, Charset encoding) 
			  throws IOException 
			{
			  byte[] encoded = Files.readAllBytes(Paths.get(path));
			  return new String(encoded, encoding);
			}
    
  public static HashMap<Span, String> traverseTreeForNPs(Tree tree, HashMap<Span, String> npHash){

    	  
    	  for (Tree subtree : tree.getChildrenAsList()){
    	   
    	   if (subtree.label().toString().equals("NP") || subtree.label().toString().equals("MPN") || subtree.label().toString().equals("CNP")){ // conjoined NPs have the label CNP. Do I want to include these here too? And I probably want to include PPs and some other stuff as well
    	    List<Word> wl = subtree.yieldWords();
    	    int begin = wl.get(0).beginPosition();
    	    int end = wl.get(wl.size()-1).endPosition();
    	    Span sp = new Span(begin, end);
    	    //HeadFinder hf = new SemanticHeadFinder(); // may want to experiment here with different types of headfinders (http://nlp.stanford.edu/nlp/javadoc/javanlp/edu/stanford/nlp/trees/HeadFinder.html)
    	    HeadFinder hf = new NegraHeadFinder();
    	    npHash.put(sp, subtree.headTerminal(hf).toString());
    	   }
    	   if (!(subtree.isPreTerminal())){
    	    traverseTreeForNPs(subtree, npHash);
    	   }
    	  }
    	  
    	  return npHash;
    	 }
    
    	
 
    
    private static void initLexParser(String lang){
    	if (lang.equalsIgnoreCase("en")){
    		parser = LexicalizedParser.loadModel(EN_PCFG_MODEL);
    	}
    	else if (lang.equalsIgnoreCase("de")){
    		parser = LexicalizedParser.loadModel(DE_PCFG_MODEL,"-maxLength", "70");
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

	public static void getOrderFromTree (Tree tree){
		//tree.indexSpans(4);
		System.out.println("TREE: ");
		tree.pennPrint();
		//System.out.println("INT I: "+i);
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
		
//	    if (!tree.isLeaf()){
//	        for(Tree kid : tree.children()){
//	        	traverse(kid);
//	        };
//	    }else{
//	    	;
//	    }
//	    //tree.pennPrint();
	   
	}
	
//	static Queue<Tree> queue = new LinkedList<Tree>() ;
	static int i = 1;
//	static TreeMap<Integer,CorefMention> leafNumberMap = new TreeMap<Integer, CorefMention>();
	
	public static  TreeMap<Integer,CorefMention> traverseBreadthFirst(Tree tree){
		TreeMap<Integer,CorefMention> leafNumberMap = new TreeMap<Integer, CorefMention>();
		Queue<Tree> queue = new LinkedList<Tree>() ;

		    if (tree == null)
		        return null;

		    queue.add(tree);
		    while(!queue.isEmpty()){
		    
		        Tree node = queue.remove();
		        
		        ArrayList<ArrayList<String>> nps = new ArrayList<ArrayList<String>>();
		        if (node.label().value().equals("NP")||node.label().value().equals("PPER")){
		        	ArrayList<String> npAsList = new ArrayList<String>();
		        	for (Tree it : node.flatten()){
        				if ((it.isLeaf())){
        					npAsList.add(it.pennString().trim());
        				}
        			}
        		
		        	String word = new String(); 
	            	word =(npAsList.get(0));
	            	for(int x=1; x<npAsList.size(); x++){
	            		word = word + " " +(npAsList.get(x));
	            	    
	            	}
	            	
		        	leafNumberMap.put(i, new CorefMention(i, word, 1, 1));
		        }
		        i++;
		        if(node.children() != null) 
		        	for(Tree kid : node.children()){
		        		queue.add(kid);}
		    

		}

			return leafNumberMap;
	}
}
