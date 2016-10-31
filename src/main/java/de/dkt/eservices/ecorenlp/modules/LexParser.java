package de.dkt.eservices.ecorenlp.modules;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.jws.soap.InitParam;

import com.hp.hpl.jena.rdf.model.Model;

import de.dkt.common.niftools.NIFReader;
import de.dkt.eservices.eopennlp.modules.SentenceDetector;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.Label;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.Tokenizer;
import edu.stanford.nlp.trees.Constituent;
import edu.stanford.nlp.trees.Dependency;
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
    		parser = LexicalizedParser.loadModel(DE_PCG_MODEL);
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
    
    
    
    public static void main(String[] args) { 
        String str = "Zwei Wochen haben sie sich dort islamistischen gelbe Rebellen und Waffen verschanzt.";
        //LexParser parser = new LexParser();
        initLexParser("de");
        Tree tree = parser.parse(str);
        
        tree.pennPrint();
        
        
        Object[] a = tree.toArray();
        ArrayList<ArrayList<String>> nps = new ArrayList<ArrayList<String>>();
        for (Object s : a){
        	LabeledScoredTreeNode t = (LabeledScoredTreeNode)s;
        	if (t.label().toString().equalsIgnoreCase("np")){
        		//System.out.println("cons:" +t.constituents());
        		ArrayList<String> npAsList = new ArrayList<String>();
        		for (Tree it : t.flatten()){
        			if ((it.isLeaf())){
        				npAsList.add(it.pennString().trim());
        			}
        		}
        		
        		nps.add(npAsList);
        	}
        }
        
        
        for (ArrayList<String> subl : nps){
        	System.out.println(subl);
        }
        
        List<Tree> leaves = tree.getLeaves();
        // Print words and Pos Tags
        for (Tree leaf : leaves) {
//        	System.out.println("leaf:" + leaf);
//        	System.out.println("isPhrasal:" + leaf.isPhrasal());
//        	System.out.println("isPreTerminal:" + leaf.isPreTerminal());
//        	System.out.println("isPrePreTerminal:" + leaf.isPrePreTerminal());
//        	System.out.println("nodeString:" +leaf.nodeString());
//        	System.out.println("pennString:" +leaf.pennString());
//        	System.out.println("children:" + leaf.children());
//        	System.out.println("NODESTRING:" + leaf.nodeString());
//        	System.out.println("CLASS:" + leaf.value());
//        	//System.out.println("CLASS:" + leaf.valueOf());
//        	//leaf.pennString();
//        	System.out.println("DEBUG:" + leaf.toString());
//        	System.out.println("LABEL:" + leaf.label());
//        	System.out.println(leaf.value());
//            Tree parent = leaf.parent(tree);
//            
//            System.out.print(leaf.label().value() + "-" + parent.label().value() + " ");
        }
        System.out.println();
        
        /*
        TreebankLanguagePack tlp = new PennTreebankLanguagePack();
        GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
        GrammaticalStructure gs = gsf.newGrammaticalStructure(tree);
        Collection tdl = gs.typedDependenciesCCprocessed(true);
        System.out.println(tdl);
        System.out.println();
        */
                          
    }
}