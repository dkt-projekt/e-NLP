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
	
	
	public static ArrayList<EntityRelationTriple> getRelationsNIF(Model nifModel){
		
		/*
		 * The code below parses for dependencies. With this we can extract the subject of a sentence and fill the EntityRelationTriple. The problem is that in the TypedDependencies, only the head of the subject noun phrase
		 * is listed. This is problematic for multi-word entities. I could not find a way to get all children of this subject noun phrase head. Current workaround is to get all entities from the nif first by entityURI.
		 * This included the start and end index of the entity in the string. If the start and end of the head of the subject noun is entailed by the entity offsets, let's assume this (whole) entity is the subject.
		 * Then the EntityRelationTriple can be filled. 
		 * TODO: think of a good way of filtering this. Currently it returns all relations found for which an entity was either the subject or the object (e.g. the entity is governed by the root/verb).
		 * TODO: take negation into account!!! 
		 * Biggest TODO: parse a lot of sentences, make inventory of the kind of relations that are in there, and filter on reln a bit more (now I'm only using nsubj and then taking all dependencies. Will probably want to filter to get only the useful ones
		 */
		
//		String isstr2 = "Mendelsohn has designed the Einstein Tower";
//		
//			
//			DocumentPreprocessor tokenizer2 = new DocumentPreprocessor(new StringReader(isstr2));
//
//			for (List<HasWord> sentence2 : tokenizer2) {
//				List<TaggedWord> tagged = Tagger.tagger.tagSentence(sentence2);
//				GrammaticalStructure gs = parser.predict(tagged);
//				HashMap<IndexedWord, IndexedWordTuple> relMap = new HashMap<IndexedWord, IndexedWordTuple>();
//				// System.out.println(sentence);
//				// System.out.println("all:" + gs.typedDependencies());
//				for (TypedDependency td : gs.typedDependencies()) {
//					System.out.println(td);
//				}
//			}
//		
		
		String isstr = NIFReader.extractIsString(nifModel);
		List<String[]> entityMap = NIFReader.extractEntityIndices(nifModel);
		ArrayList<EntityRelationTriple> ert = new ArrayList<EntityRelationTriple>();
		
		List<String> subjectRelationTypes = new ArrayList<>(Arrays.asList("nsubj", "nsubjpass"));
		
		List<String> objectRelationTypes = new ArrayList<>(Arrays.asList("dobj", "cop", "nmod"));
		List<String> indirectObjectRelationTypes = new ArrayList<>(Arrays.asList("case"));
		
		if (!(entityMap == null)){
			
			DocumentPreprocessor tokenizer = new DocumentPreprocessor(new StringReader(isstr));

			for (List<HasWord> sentence : tokenizer) {
				List<TaggedWord> tagged = Tagger.tagger.tagSentence(sentence);
				GrammaticalStructure gs = parser.predict(tagged);
				HashMap<IndexedWord, IndexedWordTuple> relMap = new HashMap<IndexedWord, IndexedWordTuple>();
				IndexedWord subject = null;
				IndexedWord connectingElement = null;
				IndexedWord object = null;
//				System.out.println("sentence:" + sentence);
//				System.out.println("DEBUGGING gs:" + gs);
//				System.out.println("\n");
				// System.out.println(sentence);
				// System.out.println("all:" + gs.typedDependencies());
				for (TypedDependency td : gs.typedDependencies()) {
					// NOTE TO SELF: by commenting out the line below, I'm taking all relations in which there is a node that governs an entity and them some object. Let's see if this makes sense...
					//if (td.reln().toString() == "nsubj") { // TODO: this is the
															// most basic
															// subject relation.
															// Check more
															// dependency trees
															// to include more
															// subject relation
															// names here!
						IndexedWordTuple t = new IndexedWordTuple();
						t.setFirst(td.dep());
						if (subjectRelationTypes.contains(td.reln().toString())){
							connectingElement = td.gov();
							subject = td.dep();
							///////relMap.put(td.gov(), t);
						}
				}

				// now do another loop to find object of the main verb/root
				// thing. This may also appear before the subject was
				// encountered, hence the need for two loops.
				if (!(connectingElement == null)){
					for (TypedDependency td : gs.typedDependencies()) {
						if (objectRelationTypes.contains(td.reln().toString())) {
							if (td.gov().beginPosition() == connectingElement.beginPosition()
									&& td.gov().endPosition() == connectingElement.endPosition()) {
								object = td.dep();
								
							}
						}
						else if (indirectObjectRelationTypes.contains(td.reln().toString())){
							object = td.gov(); // NOTE: bit tricky; taking case (usually indirect object) relation as object here if nothing founr for object. Could be interesting...
						}
					}
//					if (relMap.containsKey(td.gov()) && !(relMap.get(td.gov()).getFirst() == td.dep())) {
//						IndexedWordTuple t = relMap.get(td.gov());
//						t.setSecond(td.dep());
//						if (td.reln().toString() == "dobj"){
//						
//						///////relMap.put(td.gov(), t);
//						}
//					}
				}
//				System.out.println("Subject:" + subject);
//				System.out.println("relation:" + connectingElement);
//				System.out.println("object:" + object);
//				System.out.println("\n");
				
//TODO: change I for Erich Mendelsohn
				
				if (!(subject == null) && !(connectingElement == null) && !(object == null)){
					String subjectURI = null;
					String objectURI = null;
					for (String[] l : entityMap){
						System.out.println("subject;" + subject);
						System.out.println("subjectWord:" + subject.word());
						System.out.println("beingPos:" + subject.beginPosition());
						System.out.println("endpos:" + subject.endPosition());
						System.out.println("entity:" + isstr.substring(Integer.parseInt(l[3], Integer.parseInt(l[4]))));
						System.out.println("indices:" + l[3] + "," + l[4]);
						if (subject.beginPosition() >= Integer.parseInt(l[3]) && subject.endPosition() <= Integer.parseInt(l[4])){
							System.out.println("GETTIN GG HERE!!!!!!!!!!!!!!!!!!!!!!!!!"); // TODO: get here!
							subjectURI = l[0];
						}
						if (object.beginPosition() >= Integer.parseInt(l[3]) && object.endPosition() <= Integer.parseInt(l[4])){
							objectURI = l[0];
						}
						if (subject.word().equalsIgnoreCase("I")){
							subjectURI = "http://d-nb.info/gnd/11858071X"; // replacing I for Erich URI
						}
						if(object.word().equalsIgnoreCase("you")){
							objectURI = "http://d-nb.info/gnd/128830751"; // replacing you for Luise URI
						}
					}
					if (!(subjectURI == null)){//  && !(objectURI == null)){
						//System.out.println("Relation:" + subjectURI + " === " + connectingElement + " === " + objectURI);
						System.out.println("Relation:" + subjectURI + " === " + connectingElement + " === " + object);
					}
					

				}
				// this feels quite elaborate (lot of looping), think about a
				// better way
				for (String[] l : entityMap) {
					for (IndexedWord w : relMap.keySet()) {
						if (!(relMap.get(w).getFirst() == null) && !(relMap.get(w).getSecond() == null)) {// maybe take out the second not null check
							IndexedWord f = relMap.get(w).getFirst();
							IndexedWord s = relMap.get(w).getSecond();
							//if (!(tagged.get(s.index()) == null)){
							EntityRelationTriple t = new EntityRelationTriple();
							boolean subjectReplaced = false;
							boolean relationIsVerb = false;
							t.setSubject(relMap.get(w).getFirst().word());
							//if (w.tag().startsWith("V") && relMap.get(w).getFirst().tag().startsWith("N") && relMap.get(w).getSecond().tag().startsWith("N")){
//								relationIsVerb = true;
//							}
							t.setRelation(w.word());
							//t.setRelation(word2lemma.get(w.word()));
							t.setObject(relMap.get(w).getSecond().word());
							//ert.add(t);
							if (!(s == null)){
								if (Integer.parseInt(l[3]) <= tagged.get(f.index() - 1).beginPosition()
										&& Integer.parseInt(l[4]) >= tagged.get(f.index() - 1).endPosition()) {
									String replacedSubject = l[0];
									subjectReplaced = true;
									//System.out.println("NEW TUPLE:" + replacedSubject + "|" + w.word() + "|" + relMap.get(w).getSecond().word());
									t.setSubject(replacedSubject);
									//t.setSubject(t.getSubject() + "|" + replacedSubject);
								} else if (Integer.parseInt(l[3]) <= tagged.get(s.index() - 1).beginPosition()
										&& Integer.parseInt(l[4]) >= tagged.get(s.index() - 1).endPosition()) {
									String replacedObject = l[0];
									//System.out.println("NEW TUPLE Obj:" + relMap.get(w).getFirst().word() + "|" + w.word() + "|" + replacedObject);
									t.setObject(replacedObject);
									//t.setSubject(t.getObject() + "|" + replacedObject);
								}
							}
							//if (!(t.getSubject() == null)){// && !(t.getObject() == null)){ // having both replaced may be too strict
							//if (subjectReplaced){
							//if (relationIsVerb){
								ert.add(t);
							//}
							//}
						}
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
		initParser("en");

		
	      
		Date d1 = new Date();

		HashMap<String,Integer> subjectCount = new HashMap<String,Integer>();
		HashMap<String,HashMap<String,Integer>> subject2RelationCount = new HashMap<String,HashMap<String,Integer>>();
		String docFolder = "C:\\Users\\pebo01\\Desktop\\ubuntuShare\\out\\out\\english";
		String debugOut = "C:\\Users\\pebo01\\Desktop\\debug.txt";
		BufferedWriter brDebug = null;
		try {
			brDebug = FileFactory.generateOrCreateBufferedWriterInstance(debugOut, "utf-8", false);
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
				Model nifModel = NIFReader.extractModelFromFormatString(fileContent, RDFSerialization.TURTLE);
				ArrayList<EntityRelationTriple> ert = getRelationsNIF(nifModel);
				//System.out.println("FileName:" + f.getName());
				brDebug.write("FileName:" + f.getName());
				//System.out.println("DEBUGGINg result:");
				for (EntityRelationTriple t : ert) {
					//System.out.println("subject---relation---object: " + t.getSubject() + "---" + t.getRelation() + "---" + t.getObject());
					brDebug.write("subject---relation---object: " + t.getSubject() + "---" + t.getRelation() + "---" + t.getObject() + "\n");
					int currentSubjectCount = 1;
					if (subjectCount.containsKey(t.getSubject())){
						currentSubjectCount += subjectCount.get(t.getSubject());
					}
					subjectCount.put(t.getSubject(), currentSubjectCount);

					
					HashMap<String,Integer> innerMap = new HashMap<String,Integer>();
					if (subject2RelationCount.containsKey(t.getSubject())){
						innerMap = subject2RelationCount.get(t.getSubject());
						int currentSubject2Relationcount = 1;
						if (innerMap.containsKey(t.getRelation())){
							currentSubject2Relationcount += innerMap.get(t.getRelation());
						}
						innerMap.put(t.getRelation(), currentSubject2Relationcount);						
					}
					subject2RelationCount.put(t.getSubject(), innerMap);
					
				}
				//System.out.println("\n");
				brDebug.write("\n");
				
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		
		List<Map.Entry<String, Integer>> list = new LinkedList<Map.Entry<String, Integer>>(subjectCount.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
			public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
				return (o2.getValue()).compareTo(o1.getValue());
			}
		});
		Map<String, Integer> result = new LinkedHashMap<String, Integer>();
		for (Map.Entry<String, Integer> entry : list) {
			result.put(entry.getKey(), entry.getValue());
		}
		System.out.println(result);
		System.out.println(subject2RelationCount);
		
		//get objects of relations also in my maps
		
		System.out.println("Done."); 
			
	    try {
			brDebug.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    

		
	}
	
    
	
	
	
}
