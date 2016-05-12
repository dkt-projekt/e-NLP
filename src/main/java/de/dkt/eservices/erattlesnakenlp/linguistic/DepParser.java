package de.dkt.eservices.erattlesnakenlp.linguistic;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.persistence.Tuple;

import org.h2.engine.SysProperties;
import org.json.JSONObject;
import org.omg.Messaging.SyncScopeHelper;

import com.hp.hpl.jena.rdf.model.Model;
//import com.hp.hpl.jena.tdb.base.file.FileFactory;

import de.dkt.common.filemanagement.FileFactory;
import de.dkt.common.niftools.NIFReader;
import de.dkt.eservices.ecorenlp.modules.Tagger;
//import de.dkt.eservices.ecorenlp.modules.Tagger;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.parser.nndep.DependencyParser;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.TypedDependency;
import eu.freme.common.conversion.rdf.RDFConstants.RDFSerialization;
import eu.freme.common.exception.BadRequestException;


public class DepParser {

	
	static DependencyParser parser;
	
	public static void initParser(String language){

		String parserModel = null;
		if (language.equalsIgnoreCase("en")){
			parserModel = "edu/stanford/nlp/models/parser/nndep/english_UD.gz";  
		}
			
		else if (language.equalsIgnoreCase("de")){
			parserModel = "edu/stanford/nlp/models/parser/nndep/UD_German.gz";
		}
		else {
			throw new BadRequestException("Unsupported language: "+ language);
		}
		parser = DependencyParser.loadFromModelFile(parserModel);
				
	}
	
	public static HashMap<String,HashMap<String,HashMap<String,Integer>>> convertRelationTripleListToHashMap(ArrayList<EntityRelationTriple> ertList){
		
		HashMap<String,HashMap<String,HashMap<String,Integer>>> m = new HashMap<String,HashMap<String,HashMap<String,Integer>>>();
		for (EntityRelationTriple t : ertList){
			String subjectElem = t.getSubject() ;
			String relation = t.getRelation();
			String objectElem = t.getObject();
			if (m.containsKey(subjectElem)){
				HashMap<String, HashMap<String, Integer>> relMap = m.get(subjectElem);
				if (relMap.containsKey(relation)) {
					HashMap<String, Integer> objectMap = relMap.get(relation);
					if (objectMap.containsKey(objectElem)) {
						Integer currentObjectCount = objectMap.get(objectElem);
						objectMap.put(objectElem, currentObjectCount + 1);
					} else {
						objectMap.put(objectElem, 1);
					}
					relMap.put(relation, objectMap);
				} else {
					HashMap<String, Integer> objectMap = new HashMap<String, Integer>();
					objectMap.put(objectElem, 1);
					relMap.put(relation, objectMap);
				}
				m.put(subjectElem, relMap);
			}
			else{
				HashMap<String, HashMap<String, Integer>> relMap = new HashMap<String, HashMap<String, Integer>>();
				HashMap<String, Integer> objectMap = new HashMap<String, Integer>();
				objectMap.put(objectElem, 1);
				relMap.put(relation, objectMap);
				m.put(subjectElem, relMap);
			}
		}
		
		return m;
	}
	

	/*
	 *action plan: find all entities in a sentence. If more than one, compare each to every other, find the lowest/first connecting/governing node/dependency, if this is a verb, take that as the relation.
	 *this should make it less dependent on the actual dep parser used, since the possibility of traversing a tree should be pretty standard for every tree-like output.
	 *it should also return more results, since not restricting on the type of dependency (but only on if there is a governing verb found. 
	 */
	
	public static ArrayList<EntityRelationTriple> getRelationsNIF2(Model nifModel, BufferedWriter br2Debug){
		
		String isstr = NIFReader.extractIsString(nifModel);
		List<String[]> entityMap = NIFReader.extractEntityIndices(nifModel);
		ArrayList<EntityRelationTriple> ert = new ArrayList<EntityRelationTriple>();
		
		if (!(entityMap == null)){
			
			DocumentPreprocessor tokenizer = new DocumentPreprocessor(new StringReader(isstr));

			for (List<HasWord> sentence : tokenizer) {
				List<TaggedWord> tagged = Tagger.tagger.tagSentence(sentence);
				ArrayList<ArrayList<Integer>> entityWordIndices = new ArrayList<ArrayList<Integer>>();
				for (String[] e : entityMap){
					int entityStart = Integer.parseInt(e[3]);
					int entityEnd = Integer.parseInt(e[4]);
					ArrayList<Integer> tuple = new ArrayList<Integer>();
					for (int i = 0; i < tagged.size() ; i++){
						TaggedWord t = tagged.get(i);
						if (t.beginPosition() == entityStart){
							tuple.add(i);
						}
						if (t.endPosition() == entityEnd){
							tuple.add(i);
						}
					}
					if (tuple.size() == 2){ // it does not always fill in the endposition. I'm assuming that this is because of punctuation at the end of an entity, which is seen as part of the entity by NER, but not by the tagger
						// if this approach results in anything useful, dive into this.
						entityWordIndices.add(tuple);
					}
				}
//				System.out.println("Sentence:" + sentence);
//				System.out.println("DEBUGGING entity word indices:" + entityWordIndices);
//				for (ArrayList<Integer> l : entityWordIndices){
//					ArrayList<String> reconstruct = new ArrayList<String>();
//					for (int j = l.get(0); j < l.get(1)+1 ; j++){
//						reconstruct.add(tagged.get(j).word());
//					}
//					System.out.println("DEBUGGING l:" + l);
//					System.out.println("REC:" + reconstruct);
//				}
				
				// now I have the word indices of all entities for this sentence
				// if there is more than one entity, proceed to parse				
				if (entityWordIndices.size() > 1){
					GrammaticalStructure gs = parser.predict(tagged);
					// the loop through dependency graph, if element in graph is part of an entity, find the shortest path in the graph to another node containing/being part of an entity
					System.out.println("zsentence:" + sentence);
					System.out.println("DEBUGGINg gs:" + gs.typedDependencies());
					System.out.println("DEBUGGING entityWordIndices:" + entityWordIndices);
					for (int q = 0; q < entityWordIndices.size(); q ++){
						for (int r = q +1; r < entityWordIndices.size(); r ++){
							TypedDependency qgs = gs.typedDependencies(false).get(entityWordIndices.get(q).get(0));
							TypedDependency rgs = gs.typedDependencies(false).get(entityWordIndices.get(r).get(0));
							
							System.out.println("qgs:" + qgs);
							System.out.println("tgs:" + rgs);
							createTreeFromGrammaticalStructure(gs);
						}	
					}
				}
			}
			
		}
		
		return ert;
		
	}
	
	public static void traverseGrammaticalStructure(Node node, GrammaticalStructure gs, IndexedWord iw){
		
		while (!(getTypedDependenciesbyDep(gs, iw).isEmpty())){
			System.out.println("RESULT:" + getTypedDependenciesbyDep(gs, iw));
			for (TypedDependency td : getTypedDependenciesbyDep(gs, iw)){
				//node.addChild(new Node(td.dep().toString())); // add to subnode here instead
				System.out.println("DEBUGGINg td:" + td);
				traverseGrammaticalStructure(node, gs, td.dep());
				
			}
		}
		
	}
	
	public static Node createTreeFromGrammaticalStructure(GrammaticalStructure gs){
		Node root = new Node("root");
		
		for (TypedDependency td : gs.typedDependencies()){
			if (td.reln().toString().equals("root")){
				System.out.println("DEBUGGING tds:" + td);
				System.out.println(td.dep());
				System.out.println(td.gov());
				System.out.println(td.reln());
				root.addChild(new Node(td.dep().toString()));
				System.out.println("all deps governed by sub:" + getTypedDependenciesbyDep(gs, td.dep()));			
				traverseGrammaticalStructure(root, gs, td.dep());
				
				
	
				
			}
		}
		//root.addChild(new Node("child1"));
		//root.addChild(new Node("child2")); //etc.
		
		return root;
	}
	
	public static ArrayList<TypedDependency> getTypedDependenciesbyDep(GrammaticalStructure gs, IndexedWord iw){
		ArrayList<TypedDependency> al = new ArrayList<TypedDependency>();
		for (TypedDependency td : gs.typedDependencies()){
			if (td.gov().equals(iw)){
				al.add(td);
				}
			}
		return al;
	}
	
	public static ArrayList<EntityRelationTriple> getRelationsNIF(Model nifModel, BufferedWriter br2Debug){
		
		/*
		 * The code below parses for dependencies. With this we can extract the subject of a sentence and fill the EntityRelationTriple. The problem is that in the TypedDependencies, only the head of the subject noun phrase
		 * is listed. This is problematic for multi-word entities. I could not find a way to get all children of this subject noun phrase head. Current workaround is to get all entities from the nif first by entityURI.
		 * This included the start and end index of the entity in the string. If the start and end of the head of the subject noun is entailed by the entity offsets, let's assume this (whole) entity is the subject.
		 * Then the EntityRelationTriple can be filled. 
		 * TODO: think of a good way of filtering this. Currently it returns all relations found for which an entity was either the subject or the object (e.g. the entity is governed by the root/verb).
		 * TODO: take negation into account!!! 
		 * Biggest TODO: parse a lot of sentences, make inventory of the kind of relations that are in there, and filter on reln a bit more (now I'm only using nsubj and then taking all dependencies. Will probably want to filter to get only the useful ones
		 */
		
		String isstr = NIFReader.extractIsString(nifModel);
		List<String[]> entityMap = NIFReader.extractEntityIndices(nifModel);
		ArrayList<EntityRelationTriple> ert = new ArrayList<EntityRelationTriple>();
		
		//TODO: complete the following
		List<String> englishSubjectRelationTypes = new ArrayList<>(Arrays.asList("nsubj", "nsubjpass"));
		List<String> englishObjectRelationTypes = new ArrayList<>(Arrays.asList("dobj", "cop", "nmod")); // advmod, 
		List<String> englishIndirectObjectRelationTypes = new ArrayList<>(Arrays.asList("case"));
		
		List<String> germanSubjectRelationTypes = new ArrayList<>(Arrays.asList("nsubj", "nsubjpass"));
		List<String> germanObjectRelationTypes = new ArrayList<>(Arrays.asList("dobj", "cop", "nmod"));
		List<String> germanIndirectObjectRelationTypes = new ArrayList<>(Arrays.asList("case"));
		
		if (!(entityMap == null)){
			
			DocumentPreprocessor tokenizer = new DocumentPreprocessor(new StringReader(isstr));

			for (List<HasWord> sentence : tokenizer) {
				List<TaggedWord> tagged = Tagger.tagger.tagSentence(sentence);
				GrammaticalStructure gs = parser.predict(tagged);
				try {
					br2Debug.write("DEBUGGING sentence:" + sentence + "\n");
					br2Debug.write("DEBUGGING grammaticalStructure:" + gs + "\n\n");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				HashMap<IndexedWord, IndexedWordTuple> relMap = new HashMap<IndexedWord, IndexedWordTuple>();
				IndexedWord subject = null;
				IndexedWord connectingElement = null;
				IndexedWord object = null;
				for (TypedDependency td : gs.typedDependencies()) {
						IndexedWordTuple t = new IndexedWordTuple();
						t.setFirst(td.dep());
						if (englishSubjectRelationTypes.contains(td.reln().toString())){
							connectingElement = td.gov();
							subject = td.dep();
						}
				}

				// now do another loop to find object of the main verb/root
				// thing. This may also appear before the subject was
				// encountered, hence the need for two loops.
				if (!(connectingElement == null)){
					for (TypedDependency td : gs.typedDependencies()) {
						if (englishObjectRelationTypes.contains(td.reln().toString())) {
							if (td.gov().beginPosition() == connectingElement.beginPosition()
									&& td.gov().endPosition() == connectingElement.endPosition()) {
								object = td.dep();
								
							}
						}
						else if (englishIndirectObjectRelationTypes.contains(td.reln().toString())){
							object = td.gov(); // NOTE: bit tricky; taking case (usually indirect object) relation as object here if nothing found for object. Could be interesting...
						}
					}
				}

				if (!(subject == null) && !(connectingElement == null) && !(object == null)){
					String subjectURI = null;
					String objectURI = null;
					//NOTE subject.beginPosition() seems to return something else that what I would expect (e.g. in any case not the starting position in the entire string), so do this in a bit more sophisticated way
					int subjectStart = tagged.get(subject.index()-1).beginPosition();
					int subjectEnd = tagged.get(subject.index()-1).endPosition();
					int objectStart = tagged.get(object.index()-1).beginPosition();
					int objectEnd = tagged.get(object.index()-1).endPosition();
					for (String[] l : entityMap){
						if (subjectStart >= Integer.parseInt(l[3]) && subjectEnd <= Integer.parseInt(l[4])){ // the >= and <= are because the dependency parser is likely to cut up multi-word entities and make the head of the MWE the subject
							subjectURI = l[0];
						}
						if (objectStart >= Integer.parseInt(l[3]) && objectEnd <= Integer.parseInt(l[4])){ // idem here (regarding the >= and <=)
							objectURI = l[0];
						}
					}
					if (!(subjectURI == null) && !(objectURI == null)){
						EntityRelationTriple t = new EntityRelationTriple();
						t.setSubject(String.format("%s(%s)", subject, subjectURI));
						t.setRelation(connectingElement.word());
						//if (!(objectURI == null)){
							//t.setObject(String.format("%s(%s)", object, objectURI));
						//}
						//else {
							//t.setObject(object.word());
						//}
						t.setObject(String.format("%s(%s)", object, objectURI));
						ert.add(t);
					}
				}
			}		
		}
		
		return ert;
		
	}
	
	
	static String readFile(String path, Charset encoding) 
			  throws IOException 
			{
			  byte[] encoded = Files.readAllBytes(Paths.get(path));
			  return new String(encoded, encoding);
			}
	
	
	public static void main(String args[]){
		
		Tagger.initTagger("en");
		//Tagger.initTagger("de");
		initParser("en");
		//initParser("de");

		
	      
		Date d1 = new Date();

		HashMap<String,Integer> subjectCount = new HashMap<String,Integer>();
		HashMap<String,HashMap<String,Integer>> subject2RelationCount = new HashMap<String,HashMap<String,Integer>>();
		//String docFolder = "C:\\Users\\pebo01\\Desktop\\ubuntuShare\\out\\out\\english";
		//String docFolder = "C:\\Users\\pebo01\\Desktop\\ubuntuShare\\tempOut\\out";
		String docFolder = "C:\\Users\\pebo01\\Desktop\\RelationExtractionPlayground\\artComData\\nif\\small";
		//String docFolder = "C:\\Users\\pebo01\\Desktop\\data\\Condat_Data\\smallTestSetNIFS";
		
		String debugOut = "C:\\Users\\pebo01\\Desktop\\debug.txt";
		BufferedWriter brDebug = null;
		try {
			brDebug = FileFactory.generateOrCreateBufferedWriterInstance(debugOut, "utf-8", false);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		String debug2Out = "C:\\Users\\pebo01\\Desktop\\debug2.txt";
		BufferedWriter br2Debug = null;
		try {
			br2Debug = FileFactory.generateOrCreateBufferedWriterInstance(debug2Out, "utf-8", false);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		// String outputFolder =
		// "C:\\Users\\pebo01\\Desktop\\mendelsohnDocs\\out";
		
		File df = new File(docFolder);
		ArrayList<EntityRelationTriple> masterList = new ArrayList<EntityRelationTriple>();
		Date d3 = new Date();
		int c = 0;
		for (File f : df.listFiles()) {
			c += 1;
			String fileContent;
			try {
				fileContent = readFile(f.getAbsolutePath(), StandardCharsets.UTF_8);
				//System.out.println("Trying for file:" + f);
				Model nifModel = NIFReader.extractModelFromFormatString(fileContent, RDFSerialization.TURTLE);
				ArrayList<EntityRelationTriple> ert = getRelationsNIF2(nifModel, br2Debug);
				
				// Action plan for mockup: ert is now per NIF. Add to masterList. Then convert this into double nested hashmap (subject > relation > object > count) and output JSON for Julian to eat in his mockup.
				for (EntityRelationTriple t : ert){
					masterList.add(t);
				}
				
				//System.out.println("FileName:" + f.getName());
//				brDebug.write("FileName:" + f.getName());
//				//System.out.println("DEBUGGINg result:");
//				for (EntityRelationTriple t : ert) {
//					//System.out.println("subject---relation---object: " + t.getSubject() + "---" + t.getRelation() + "---" + t.getObject());
//					brDebug.write("subject---relation---object: " + t.getSubject() + "---" + t.getRelation() + "---" + t.getObject() + "\n");
//					int currentSubjectCount = 1;
//					if (subjectCount.containsKey(t.getSubject())){
//						currentSubjectCount += subjectCount.get(t.getSubject());
//					}
//					subjectCount.put(t.getSubject(), currentSubjectCount);
//
//					
//					HashMap<String,Integer> innerMap = new HashMap<String,Integer>();
//					if (subject2RelationCount.containsKey(t.getSubject())){
//						innerMap = subject2RelationCount.get(t.getSubject());
//						int currentSubject2Relationcount = 1;
//						if (innerMap.containsKey(t.getRelation())){
//							currentSubject2Relationcount += innerMap.get(t.getRelation());
//						}
//						innerMap.put(t.getRelation(), currentSubject2Relationcount);						
//					}
//					subject2RelationCount.put(t.getSubject(), innerMap);
//					
//				}
//				//System.out.println("\n");
//				brDebug.write("\n");
				
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		
		// DEBUGGING masterList and convert procedure:
//		masterList.clear();
//		EntityRelationTriple td = new EntityRelationTriple();
//		td.setSubject("aapje");
//		td.setRelation("boompje");
//		td.setObject("beestje");
//		EntityRelationTriple tda = new EntityRelationTriple();
//		tda.setSubject("aapje");
//		tda.setRelation("boompje");
//		tda.setObject("kalfje");
//		EntityRelationTriple tdb = new EntityRelationTriple();
//		tdb.setSubject("aapje");
//		tdb.setRelation("schaapje");
//		tdb.setObject("veulentje");
//		EntityRelationTriple tdc = new EntityRelationTriple();
//		tdc.setSubject("huisje");
//		tdc.setRelation("schaapje");
//		tdc.setObject("veulentje");
//		masterList.add(td);
//		masterList.add(td);
//		masterList.add(tda);
//		masterList.add(tdb);
//		masterList.add(tdc);
//		
		HashMap<String,HashMap<String,HashMap<String,Integer>>> m = convertRelationTripleListToHashMap(masterList);
		JSONObject jsonMap = new JSONObject(m);
		try {
			brDebug.write(jsonMap.toString());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} // write to JSON here
		
//		List<Map.Entry<String, Integer>> list = new LinkedList<Map.Entry<String, Integer>>(subjectCount.entrySet());
//		Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
//			public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
//				return (o2.getValue()).compareTo(o1.getValue());
//			}
//		});
//		Map<String, Integer> result = new LinkedHashMap<String, Integer>();
//		for (Map.Entry<String, Integer> entry : list) {
//			result.put(entry.getKey(), entry.getValue());
//		}
//		System.out.println(result);
//		System.out.println(subject2RelationCount);
		
		//get objects of relations also in my maps
		
		System.out.println("Done."); 
			
	    try {
			brDebug.close();
			br2Debug.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    

		
	}
	
    
	
	
	
}
