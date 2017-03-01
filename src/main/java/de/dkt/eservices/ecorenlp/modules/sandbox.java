package de.dkt.eservices.ecorenlp.modules;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import java.util.LinkedList;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;

import java.util.Map.Entry;
import java.util.Properties;
import java.util.Queue;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import de.dkt.common.niftools.NIFReader;
import de.dkt.eservices.erattlesnakenlp.linguistic.SpanWord;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.trees.Constituent;
import edu.stanford.nlp.trees.HeadFinder;
import edu.stanford.nlp.trees.international.negra.NegraHeadFinder;
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.trees.tregex.TregexPattern;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.PropertiesUtils;
import eu.freme.common.conversion.rdf.RDFConstants.RDFSerialization;
import edu.stanford.nlp.trees.Tree;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.util.Span;

import edu.stanford.nlp.naturalli.ClauseSplitter;
import edu.stanford.nlp.naturalli.OpenIE;
import edu.stanford.nlp.naturalli.SentenceFragment;

public class sandbox {
	public static LexicalizedParser parser = null;
	private final static String EN_PCFG_MODEL = "edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz";        
    private final static String DE_PCFG_MODEL = "edu/stanford/nlp/models/lexparser/germanPCFG.ser.gz";
    
    public static <K, V> void addToLinkedHash(LinkedHashMap<K, V> map, int index, K key, V value) {
    	  assert (map != null);
    	  assert !map.containsKey(key);
    	  assert (index >= 0) && (index < map.size());

    	  int i = 0;
    	  List<Entry<K, V>> rest = new ArrayList<Entry<K, V>>();
    	  for (Entry<K, V> entry : map.entrySet()) {
    	    if (i++ >= index) {
    	      rest.add(entry);
    	    }
    	  }
    	  map.put(key, value);
    	  for (int j = 0; j < rest.size(); j++) {
    	    Entry<K, V> entry = rest.get(j);
    	    map.remove(entry.getKey());
    	    map.put(entry.getKey(), entry.getValue());
    	  }
    	}
	
    
    
    public static void main(String[] args) throws IOException{
    	
    	
    	
//    	String sentence = "This computer is great, but the keyboard is lousy.";
//    	Properties props = PropertiesUtils.asProperties("annotators", "tokenize,ssplit,pos,lemma,depparse,natlog,openie");
//    	StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
//
//        Annotation doc = new Annotation(sentence);
//        pipeline.annotate(doc);
//
//		for (CoreMap sent : doc.get(CoreAnnotations.SentencesAnnotation.class)) {
//			System.out.println(sent.get(CoreAnnotations.TextAnnotation.class));
//			List<SentenceFragment> clauses = new OpenIE(props).clausesInSentence(sent);
//			for (SentenceFragment clause : clauses) {
//				System.out.println(clause);
//				//System.out.println(clause.parseTree.toString(SemanticGraph.OutputFormat.LIST));
//			}
//		}
    	
    }
    	
    	
    	
//    	String filePath = "C:\\Users\\pebo01\\AppData\\Local\\Temp\\Temp1_NIFfiles.zip\\NIFfiles\\CliffBurton.nif";   	
//    	String jsonLdString = readFile(filePath, StandardCharsets.UTF_8);
//    	try {
//			Model nifModel = NIFReader.extractModelFromFormatString(jsonLdString, RDFSerialization.JSON_LD);
//			List<String[]> events = new LinkedList<String[]>();
//			ResIterator iterEntities = nifModel.listSubjects();
//	        while (iterEntities.hasNext()) {
//	            Resource r = iterEntities.nextResource();
//	            if (r.toString().startsWith("http://dkt.dfki.de/documents/#event")){
//	            	System.out.println(r);
//	            	StmtIterator si = r.listProperties();
//	            	while(si.hasNext()){
//	            		Statement r2 = si.nextStatement();
//	            		System.out.println(r2);
//	            	}
//	            }
//	        }
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//    	
//    	
//    }
//    	
//    	
//    	LexicalizedParser lexParser = LexicalizedParser.loadModel("edu/stanford/nlp/models/lexparser/germanPCFG.ser.gz","-maxLength", "70");
//    	String sentence = "Barack Obama, Präsident der U.S.A, besuchte heute Berlin.";
////  	String sentence = "Sein Blick ist im vorübergehn der Stäbe so müde geworden, dass er nichts mehr hält. Ihm ist als ob es tausend Stäbe gäbe und hinter tausen Stäben keine Welt.";
//    	
// //   	getOrderFromTree(tree);
// //       traverse(tree,tree);
//    	String secondSentence = "Angela Merkel, Kanzlerin der Herzen, war auch mit von der Partie";
//    	String[] array = {sentence, secondSentence};
//    	for (String a :array){
//    		Tree tree = lexParser.parse(a);
//    		traverseBreadthFirst(tree);
//    	}
    	
//    	
//        
//    	
//    
////           LexicalizedParser lexParser = LexicalizedParser.loadModel("edu/stanford/nlp/models/lexparser/germanPCFG.ser.gz","-maxLength", "70");
////           //String sentence = "Der Hund und sein Freund sind hier und die Katze. Barack Obama, Präsident der U.S.A, besuchte heute Berlin.";
////           String sentence = "Barack Obama, Präsident der U.S.A, besuchte heute Berlin.";
////           Tree tree = lexParser.parse(sentence);
////           
////           ///** debugging
////           System.out.println("The Parse Tree for " + sentence + " is: ");
////           tree.pennPrint();
////           //**/
////        	
////
////        	HashMap<Span, String> npHash = traverseTreeForNPs(tree, new HashMap<Span, String>());
////        	for (Span sp : npHash.keySet()){
////        	  System.out.println("NP:" + sentence.substring(sp.getStart(), sp.getEnd()));
////        	  System.out.println("Indices:" + sp.getStart() + "|" + sp.getEnd());
////        	  System.out.println("HEAD:" + npHash.get(sp));
////        	}
//    	
////    	String modelsDirectory = "trainedModels/";
////		InputStream modelIn = null;
////		ClassPathResource cpr;
////		POSModel POSModel = null;
////		try{
////			//cpr = new ClassPathResource("C:\\Users\\pebo01\\workspace\\DktBroker-standalone\\target\\classes\\taggers\\english-left3words-distsim.tagger");
////			File f = new File("C:\\Users\\pebo01\\workspace\\DktBroker-standalone\\target\\classes\\taggers\\english-left3words-distsim.tagger");
////			modelIn = new FileInputStream(f);
////			POSModel = new POSModel(modelIn);
////		}
////		catch(IOException e){
////			throw new BadRequestException(e.getMessage());
////		}
////    	
////    	String aapje = "\\aapje";
////    	System.out.println("String aapje:" + aapje);
////    	System.out.println("Escaped:" + aapje.replace("\\", "\\\\"));
////    	System.exit(1);
//    	
//
//    	
////    	String nifFileMap = "C:\\Users\\pebo01\\Desktop\\data\\IAFL2017\\nifs";
////    	File df = new File(nifFileMap);
////    	ArrayList<Model> modelsList = new ArrayList<Model>();
////    	//String prefix = null;
////    	for (File f : df.listFiles()){
////			String fileContent = readFile(f.getAbsolutePath(), StandardCharsets.UTF_8);
////			try {
////				Model nifModel = NIFReader.extractModelFromFormatString(fileContent, RDFSerialization.TURTLE);
////				modelsList.add(nifModel);
////			} catch (Exception e) {
////				// TODO Auto-generated catch block
////				e.printStackTrace();
////			}
////    	}
////    	String prefix = "http://dkt.dfki.de/clintonCorpus";
////    	//Model nifCollection = NIFManagement.createCollectionFromDocuments(prefix, modelsList);
////
////    	PrintWriter out = new PrintWriter(new File("C:\\Users\\pebo01\\Desktop\\debug.txt"));
////		//out.println(NIFReader.model2String(nifCollection, RDFSerialization.TURTLE));
////		out.close();
////    	
//
//    	String nifFileMap = "C:\\Users\\pebo01\\Desktop\\data\\IAFL2017\\corefNIFs";
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
//    	System.out.println("Populated list.");
//    	String prefix = "http://dkt.dfki.de/clintonCorpus";
//    	Model nifCollection = NIFManagement.createCollectionFromDocuments(prefix, modelsList);
//    	System.out.println("Collection created.");
//    	PrintWriter out = new PrintWriter(new File("C:\\Users\\pebo01\\Desktop\\data\\IAFL2017\\nifCollectionCustomMade\\clintonCollection.nif"));
//    	// the normal model2string ran into memory limitations, so doing it manually here...
//		//out.println(NIFReader.model2String(nifCollection, RDFSerialization.TURTLE));
//    	// list the statements in the Model
//    	StmtIterator iter = nifCollection.listStatements();
//
//    	// print out the predicate, subject and object of each statement
//    	HashMap<String, LinkedHashMap<String, String>> nifStringHash = new HashMap<String, LinkedHashMap<String, String>>(); 
//    	while (iter.hasNext()) {
//    	    Statement stmt      = iter.nextStatement();
//    	    Resource  subject   = stmt.getSubject();
//    	    Property  predicate = stmt.getPredicate();
//    	    RDFNode   object    = stmt.getObject();
//    	    
//    	    LinkedHashMap<String, String> innerMap = nifStringHash.containsKey(subject.toString()) ? nifStringHash.get(subject.toString()) : new LinkedHashMap<String, String>();
//    	    // for some reason, nif:isString should always be last item...
//    	    if (predicate.toString().equals("http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#isString")){
//    	    	addToLinkedHash(innerMap, innerMap.keySet().size(), predicate.toString(), object.toString());
//    	    }
//    	    else{
//    	    	addToLinkedHash(innerMap, 0, predicate.toString(), object.toString());
//    	    }
//    	    //innerMap.put(predicate.toString(), object.toString());
//    	    nifStringHash.put(subject.toString(), innerMap);
//    	    
//    	    
////    	    out.write(subject.toString() + "\n");
////    	    out.write(" " + predicate.toString() + " " + "\n");
////    	    if (object instanceof Resource) {
////    	       out.write(object.toString() + "\n");
////    	    } else {
////    	        out.write(" \"" + object.toString() + "\"" + "\n");
////    	    }
////
////    	    out.write(" ." + "\n");
//    	}
//    	// fix prefixes for readability (and to make the file a bit smaller)...
//    	
//    	HashMap<String, String> prefixMap = new HashMap<String, String>();
//    	prefixMap.put("http://dkt.dfki.de/ontologies/nif#", "dktnif:");
//    	prefixMap.put("http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-annotation#", "nif-ann:");
//    	prefixMap.put("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "rdf:");
//    	prefixMap.put("http://www.w3.org/2001/XMLSchema#", "xsd:");
//    	prefixMap.put("http://www.w3.org/2005/11/its/rdf#", "itsrdf:");
//    	prefixMap.put("http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#", "nif:");
//    	prefixMap.put("http://www.w3.org/2000/01/rdf-schema#", "rdfs:");
//    	prefixMap.put("http://www.w3.org/2006/time#", "time:");
//    	prefixMap.put("http://www.w3.org/2003/01/geo/wgs84_pos/", "geo:");
//    	prefixMap.put("http://dbpedia.org/ontology/", "dbo:");
//    	prefixMap.put("http://www.w3.org/2002/07/owl#", "owl:");
//    	
//    	
//    	out.write("@prefix dktnif: <http://dkt.dfki.de/ontologies/nif#> .\n");
//    	out.write("@prefix nif-ann: <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-annotation#> .\n");
//    	out.write("@prefix rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n");
//    	out.write("@prefix xsd:   <http://www.w3.org/2001/XMLSchema#> .\n");
//    	out.write("@prefix itsrdf: <http://www.w3.org/2005/11/its/rdf#> .\n");
//    	out.write("@prefix nif:   <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#> .\n");
//    	out.write("@prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#> .\n");
//    	out.write("@prefix time:  <http://www.w3.org/2006/time#> .\n");
//    	out.write("@prefix geo:  <http://www.w3.org/2003/01/geo/wgs84_pos/> .\n");
//    	out.write("@prefix dbo:  <http://dbpedia.org/ontology/> .\n");
//    	out.write("@prefix owl:  <http://www.w3.org/2002/07/owl#> .\n");
//    	out.write("\n");
//    	
//    	for (String subject : nifStringHash.keySet()){
//    		boolean bs = false;
//    		for (String p : prefixMap.keySet()){
//    			if (subject.contains(p)){
//    				subject = subject.replaceAll(p, prefixMap.get(p));
//    				bs = true;
//    			}
//    		}
//    		if (!bs){
//    			out.write("<" + subject + ">\n");
//    		}
//    		else{
//    			out.write(subject + "\n");
//    		}
//    		
//    		int i = 1;
//    		for (String predicate : nifStringHash.get(subject).keySet()){
//    			String object = nifStringHash.get(subject).get(predicate);
//    			boolean bp = false;
//    			boolean bo = false;
//    			boolean quoteObject = false;
//    			for (String p : prefixMap.keySet()){
//    				if (predicate.contains(p)){
//    					predicate = predicate.replaceAll(p, prefixMap.get(p));
//    					bp = true;
//    				}
//        			if (object.contains(p)){
//        				object = object.replaceAll(p,  prefixMap.get(p));
//        				bo = true;
//        				if (p.equals("http://www.w3.org/2001/XMLSchema#")){
//        					quoteObject = true;
//        				}
//        			}
//        		}
//    			String printPredicate = null;
//    			if (!bp){
//    				printPredicate = "<" + predicate + ">";
//    			}
//    			else{
//    				printPredicate = predicate;
//    			}
//    			String printObject = null;
//    			if (!bo){
//    				printObject = "<" + object + ">";
//    			}
//    			//TODO; CHECK validation, thi sis where I left off...
//    			else{
//    				printObject = object.replace("\\", "\\\\");
//    				printObject = printObject.replaceAll("\"", "\\\\\"");
//    				printObject = printObject.replaceAll("\n", "\\\\n");
//    				printObject = printObject.replaceAll("\r", "\\\\r");
//    				printObject = printObject.replaceAll("\f", "\\\\f");
//    				
//    				if (quoteObject){
//    					String[] parts = printObject.split("\\^\\^");
//    					printObject = "\"" + parts[0] + "\"^^" + parts[1];
//    				}
//    			}
//    			if (i < nifStringHash.get(subject).keySet().size()){
//    				out.write(String.format("\t%s\t%s ;\n", printPredicate, printObject));
//    			}
//    			else{
//    				out.write(String.format("\t%s\t%s .\n\n", printPredicate, printObject));
//    			}
//    			i ++;
//    		}
//    	}
//		out.close();
//		//System.out.println("Done.");
//		
//		System.out.println("Validating collection now.");
//		String fileContent = readFile(new File("C:\\Users\\pebo01\\Desktop\\data\\IAFL2017\\nifCollectionCustomMade\\clintonCollection.nif").getAbsolutePath(), StandardCharsets.UTF_8);
//		try {
//			Model nifCollectionModel = NIFReader.extractModelFromFormatString(fileContent, RDFSerialization.TURTLE);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		System.out.println("Validated. Done.");}
		
    	// bypassing the printing, going to clustering straight away
    	// had to add e-clustering to pom and make the clusteRNIF static for this to work... (temporarily did so and then undid)
//    	JSONObject outObject = de.dkt.eservices.eweka.EWekaService.clusterNIF(nifCollection, "kmeans", "en", "entityfrequencyappearance", "clintonCorpus");
//    	System.out.println("Clustering done.");
//        String result = outObject.toString(1);
//        PrintWriter out = new PrintWriter(new File("C:\\Users\\pebo01\\Desktop\\debug.txt"));
//        out.println(result);
//        out.close();
//        System.out.println("output written to file.");

    
    
    
    static String readFile(String path, Charset encoding) 
			  throws IOException 
			{
			  byte[] encoded = Files.readAllBytes(Paths.get(path));
			  return new String(encoded, encoding);
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
//    	    System.out.println("DEBUG traverseTreeForNPs intermediate: "+subtree.headTerminal(hf).toString());
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
//		for (CorefMention m : mentions){
//			System.out.println("all the mentions: "+m.getContents()+" "+m.getMentionID());
//		}
//		System.out.println("--------------------------------------------");
		LinkedHashSet<SpanWord>	wordSpans = new LinkedHashSet<>();
		int counter = 0;
		
		for (CorefMention mention : mentions){
//		String word = mention.getContents().replace("''", "").replace("``","").replace(" :",":").replace(" .",".").trim();
//		word = word.replace("\\s+", "").replace("\t", "").trim();
		//System.out.println("Original Sentence: "+sentence.getText());
		String word = mention.getContents().replaceAll("\\p{Punct}", "").replaceAll("\"", "").replaceAll("„", "").replaceAll("“", "").replaceAll("\\s+", " ");
		//stem.out.println("WORD: "+word);
	 	List<Integer> pos = new ArrayList<>();

    	String sent = sentence.getText().replaceAll("\\p{Punct}", "").replaceAll("\\s+", " ").replaceAll("„", "").replaceAll("“", "");
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
    		
    		int begin = sentenceStart + pos.get(counter-1);
   
        	int end = begin +word.length();
    		d = new SpanWord(word,begin,end);
    		wordSpans.add(d);

    		
    	}else{
    		String inputStr = sent;
    	    String patternStr = word;
    
    	    Pattern pattern = Pattern.compile("\\b"+patternStr+"\\b");
    	    
    	    Pattern pattern2 = Pattern.compile("\\^"+patternStr+"\\b");
    	    Matcher matcher2 = pattern2.matcher(inputStr.trim());
    	    
    	    Matcher matcher = pattern.matcher(inputStr.trim());
    	    if(matcher.find()){

    	    	int begin = sentenceStart + matcher.start();
    	    	int end = begin +word.length();
    	    	d = new SpanWord(word,begin,end);
    	    	wordSpans.add(d);}
    	    else{ 
    	    	if (matcher2.find()){
    	    		int begin = sentenceStart + matcher.start();
        	    	int end = begin +word.length();
        	    	d = new SpanWord(word,begin,end);
        	    	wordSpans.add(d);
    	    	}

    	    	}
    
    	   
    	}
    	}
		return wordSpans;
		
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

}
