package de.dkt.eservices.erattlesnakenlp.linguistic;

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

import de.dkt.common.niftools.DFKINIF;
import de.dkt.common.niftools.NIFReader;
import de.dkt.common.niftools.NIFWriter;
import de.dkt.eservices.ecorenlp.modules.Tagger;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.parser.nndep.DependencyParser;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;
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
		
		String isstr = NIFReader.extractIsString(nifModel);
		List<String[]> entityMap = NIFReader.extractEntityIndices(nifModel);
		ArrayList<EntityRelationTriple> ert = new ArrayList<EntityRelationTriple>();
		if (!(entityMap == null)){
			
			DocumentPreprocessor tokenizer = new DocumentPreprocessor(new StringReader(isstr));

			for (List<HasWord> sentence : tokenizer) {
				List<TaggedWord> tagged = Tagger.tagger.tagSentence(sentence);
				GrammaticalStructure gs = parser.predict(tagged);
				HashMap<IndexedWord, IndexedWordTuple> relMap = new HashMap<IndexedWord, IndexedWordTuple>();
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
						relMap.put(td.gov(), t);
						//word2lemma.put(td.gov().word(), td.gov().lemma()); // TODO: check is lemmatisation is context-sensitive
					//}
				}

				// now do another loop to find object of the main verb/root
				// thing. This may also appear before the subject was
				// encountered, hence the need for two loops.
				for (TypedDependency td : gs.typedDependencies()) {
					if (relMap.containsKey(td.gov()) && !(relMap.get(td.gov()).getFirst() == td.dep())) {
						IndexedWordTuple t = relMap.get(td.gov());
						t.setSecond(td.dep());
						relMap.put(td.gov(), t);
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
							t.setSubject(relMap.get(w).getFirst().word());
							t.setRelation(w.word());
							//t.setRelation(word2lemma.get(w.word()));
							t.setObject(relMap.get(w).getSecond().word());
							if (!(s == null)){
								if (Integer.parseInt(l[3]) <= tagged.get(f.index() - 1).beginPosition()
										&& Integer.parseInt(l[4]) >= tagged.get(f.index() - 1).endPosition()) {
									String replacedSubject = l[0];
									subjectReplaced = true;
									//System.out.println("NEW TUPLE:" + replacedSubject + "|" + w.word() + "|" + relMap.get(w).getSecond().word());
									t.setSubject(replacedSubject);
								} else if (Integer.parseInt(l[3]) <= tagged.get(s.index() - 1).beginPosition()
										&& Integer.parseInt(l[4]) >= tagged.get(s.index() - 1).endPosition()) {
									String replacedObject = l[0];
									//System.out.println("NEW TUPLE Obj:" + relMap.get(w).getFirst().word() + "|" + w.word() + "|" + replacedObject);
									t.setObject(replacedObject);
								}
							}
							//if (!(t.getSubject() == null)){// && !(t.getObject() == null)){ // having both replaced may be too strict
							if (subjectReplaced){
								
								ert.add(t);
							}
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
		
		
//		String text = "This is a sample sentence. Eric Mendelsohn did design the Einstein tower.";
//		DocumentPreprocessor tokenizer = new DocumentPreprocessor(new StringReader(text));
//	    for (List<HasWord> sentence : tokenizer) {
//	      List<TaggedWord> tagged = Tagger.tagger.tagSentence(sentence);
//	      System.out.println("Sentence:" + sentence);
//	      GrammaticalStructure gs = parser.predict(tagged);
//
//	      // Print typed dependencies
//	      System.out.println("OUT:" + gs);
//	      
//	      for (TypedDependency td : gs.typedDependencies()){
//	    	  IndexedWord dep = td.dep();
//	    	  if (td.reln().toString() == "nsubj"){
//	    		  System.out.println("SUBJECT FOUND!");
//	    		  int IndexOfHighestNodeInSubject = td.dep().index();
//	    		  //gs.getGrammaticalRelation(td.gov().index(), td.dep().index());
//	    		  td.gov();
//	    		  //System.out.println(gs.getGrammaticalRelation(td.gov().index(), td.dep().index()));
//	    		  //System.out.println(gs.getGrammaticalRelation(td.gov().index(), td.dep().index()).getParent());
//	    	  }
//	    	  
//	    	  
//	    	  int depStart = tagged.get(td.dep().index()-1).beginPosition();
//	    	  int depEnd = tagged.get(td.dep().index()-1).endPosition();
//	    	  // NOTE that this approach only works for single-word entities, which is a serious drawback. Use NP subject phrase to fix multi-word entities?
//	    	  
//	    	  //String subjectEntityURI = NIFReader.extractDocumentURI(nifModel) + "#char=" + depStart + "," + depEnd;
//	    	  //String taIdentRef = extractTaIdentRefWithEntityURI(nifModel, subjectEntityURI);
//	    	  
//	    	  System.out.println("DEBUG td;" + td);
//	    	  System.out.println("dep:" + td.dep());
//	    	  System.out.println(td.dep().index());//TODO: get index of element in string, so that I can trace it back to possible entityURI, and do the whole thing...
//	    	  String word = tagged.get(td.dep().index()-1).word();
//	    	  System.out.println(tagged.get(td.dep().index()-1).beginPosition());
//	    	  System.out.println(tagged.get(td.dep().index()-1).endPosition());
//	    	  System.out.println("Word at index of dep:" + word);
//	    	  
//	    	  System.out.println("reln:" + td.reln());
//	    	  
//	    	  System.out.println("gov:" + td.gov());
//	    	  System.out.println(td.gov().index());//TODO: get index of element in string, so that I can trace it back to possible entityURI, and do the whole thing...
//	    	  System.out.println("\n");
//	    	  
//	      }
//	      
	      String testNIF = 
	    		  "@prefix dbo:   <http://dbpedia.org/ontology/> .\n" +
	    				  "@prefix rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n" +
	    				  "@prefix xsd:   <http://www.w3.org/2001/XMLSchema#> .\n" +
	    				  "@prefix itsrdf: <http://www.w3.org/2005/11/its/rdf#> .\n" +
	    				  "@prefix nif:   <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#> .\n" +
	    				  "@prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#> .\n" +
	    				  "\n" +
	    				  "<http://dkt.dfki.de/documents/#char=136,150>\n" +
	    				  "        a                     nif:RFC5147String , nif:String ;\n" +
	    				  "        nif:anchorOf          \"Edward Seymour\"^^xsd:string ;\n" +
	    				  "        nif:beginIndex        \"136\"^^xsd:nonNegativeInteger ;\n" +
	    				  "        nif:endIndex          \"150\"^^xsd:nonNegativeInteger ;\n" +
	    				  "        nif:entity            <http://dkt.dfki.de/ontologies/nif#person> ;\n" +
	    				  "        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,1519> ;\n" +
	    				  "        itsrdf:taIdentRef     <http://de.dbpedia.org/resource/Edward_Seymour> .\n" +
	    				  "\n" +
	    				  "<http://dkt.dfki.de/documents/#char=0,5>\n" +
	    				  "        a                     nif:RFC5147String , nif:String ;\n" +
	    				  "        nif:anchorOf          \"Henry\"^^xsd:string ;\n" +
	    				  "        nif:beginIndex        \"0\"^^xsd:nonNegativeInteger ;\n" +
	    				  "        nif:endIndex          \"5\"^^xsd:nonNegativeInteger ;\n" +
	    				  "        nif:entity            <http://dkt.dfki.de/ontologies/nif#person> ;\n" +
	    				  "        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,1519> ;\n" +
	    				  "        itsrdf:taIdentRef     <http://de.dbpedia.org/resource/Henry> .\n" +
	    				  "\n" +
	    				  "<http://dkt.dfki.de/documents/#char=651,657>\n" +
	    				  "        a                     nif:RFC5147String , nif:String ;\n" +
	    				  "        nif:anchorOf          \"Alfred\"^^xsd:string ;\n" +
	    				  "        nif:beginIndex        \"651\"^^xsd:nonNegativeInteger ;\n" +
	    				  "        nif:endIndex          \"657\"^^xsd:nonNegativeInteger ;\n" +
	    				  "        nif:entity            <http://dkt.dfki.de/ontologies/nif#person> ;\n" +
	    				  "        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,1519> ;\n" +
	    				  "        itsrdf:taIdentRef     <http://de.dbpedia.org/resource/Alfred> .\n" +
	    				  "\n" +
	    				  "<http://dkt.dfki.de/documents/#char=912,921>\n" +
	    				  "        a                     nif:RFC5147String , nif:String ;\n" +
	    				  "        nif:anchorOf          \"Mary Rose\"^^xsd:string ;\n" +
	    				  "        nif:beginIndex        \"912\"^^xsd:nonNegativeInteger ;\n" +
	    				  "        nif:endIndex          \"921\"^^xsd:nonNegativeInteger ;\n" +
	    				  "        nif:entity            <http://dkt.dfki.de/ontologies/nif#person> ;\n" +
	    				  "        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,1519> ;\n" +
	    				  "        itsrdf:taIdentRef     <http://de.dbpedia.org/resource/Mary_Rose> .\n" +
	    				  "\n" +
	    				  "<http://dkt.dfki.de/documents/#char=1491,1501>\n" +
	    				  "        a                     nif:RFC5147String , nif:String ;\n" +
	    				  "        nif:anchorOf          \"Henry VIII\"^^xsd:string ;\n" +
	    				  "        nif:beginIndex        \"1491\"^^xsd:nonNegativeInteger ;\n" +
	    				  "        nif:endIndex          \"1501\"^^xsd:nonNegativeInteger ;\n" +
	    				  "        nif:entity            <http://dkt.dfki.de/ontologies/nif#person> ;\n" +
	    				  "        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,1519> ;\n" +
	    				  "        itsrdf:taIdentRef     <http://de.dbpedia.org/resource/Henry_VIII> .\n" +
	    				  "\n" +
	    				  "<http://dkt.dfki.de/documents/#char=52,58>\n" +
	    				  "        a                     nif:RFC5147String , nif:String ;\n" +
	    				  "        nif:anchorOf          \"Edward\"^^xsd:string ;\n" +
	    				  "        nif:beginIndex        \"52\"^^xsd:nonNegativeInteger ;\n" +
	    				  "        nif:endIndex          \"58\"^^xsd:nonNegativeInteger ;\n" +
	    				  "        nif:entity            <http://dkt.dfki.de/ontologies/nif#person> ;\n" +
	    				  "        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,1519> ;\n" +
	    				  "        itsrdf:taIdentRef     <http://de.dbpedia.org/resource/Edward> .\n" +
	    				  "\n" +
	    				  "<http://dkt.dfki.de/documents/#char=946,961>\n" +
	    				  "        a                     nif:RFC5147String , nif:String ;\n" +
	    				  "        nif:anchorOf          \"HMNB Portsmouth\"^^xsd:string ;\n" +
	    				  "        nif:beginIndex        \"946\"^^xsd:nonNegativeInteger ;\n" +
	    				  "        nif:endIndex          \"961\"^^xsd:nonNegativeInteger ;\n" +
	    				  "        nif:entity            <http://dkt.dfki.de/ontologies/nif#organization> ;\n" +
	    				  "        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,1519> ;\n" +
	    				  "        itsrdf:taIdentRef     <http://de.dbpedia.org/resource/HMNB_Portsmouth> .\n" +
	    				  "\n" +
	    				  "<http://dkt.dfki.de/documents/#char=744,754>\n" +
	    				  "        a                     nif:RFC5147String , nif:String ;\n" +
	    				  "        nif:anchorOf          \"Royal Navy\"^^xsd:string ;\n" +
	    				  "        nif:beginIndex        \"744\"^^xsd:nonNegativeInteger ;\n" +
	    				  "        nif:endIndex          \"754\"^^xsd:nonNegativeInteger ;\n" +
	    				  "        nif:entity            <http://dkt.dfki.de/ontologies/nif#organization> ;\n" +
	    				  "        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,1519> ;\n" +
	    				  "        itsrdf:taIdentRef     <http://de.dbpedia.org/resource/Royal_Navy> .\n" +
	    				  "\n" +
	    				  "<http://dkt.dfki.de/documents/#char=1259,1264>\n" +
	    				  "        a                     nif:RFC5147String , nif:String ;\n" +
	    				  "        nif:anchorOf          \"Henry\"^^xsd:string ;\n" +
	    				  "        nif:beginIndex        \"1259\"^^xsd:nonNegativeInteger ;\n" +
	    				  "        nif:endIndex          \"1264\"^^xsd:nonNegativeInteger ;\n" +
	    				  "        nif:entity            <http://dkt.dfki.de/ontologies/nif#person> ;\n" +
	    				  "        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,1519> ;\n" +
	    				  "        itsrdf:taIdentRef     <http://de.dbpedia.org/resource/Henry> .\n" +
	    				  "\n" +
	    				  "<http://dkt.dfki.de/documents/#char=685,690>\n" +
	    				  "        a                     nif:RFC5147String , nif:String ;\n" +
	    				  "        nif:anchorOf          \"Henry\"^^xsd:string ;\n" +
	    				  "        nif:beginIndex        \"685\"^^xsd:nonNegativeInteger ;\n" +
	    				  "        nif:endIndex          \"690\"^^xsd:nonNegativeInteger ;\n" +
	    				  "        nif:entity            <http://dkt.dfki.de/ontologies/nif#person> ;\n" +
	    				  "        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,1519> ;\n" +
	    				  "        itsrdf:taIdentRef     <http://de.dbpedia.org/resource/Henry> .\n" +
	    				  "\n" +
	    				  "<http://dkt.dfki.de/documents/#char=546,551>\n" +
	    				  "        a                     nif:RFC5147String , nif:String ;\n" +
	    				  "        nif:anchorOf          \"Henry\"^^xsd:string ;\n" +
	    				  "        nif:beginIndex        \"546\"^^xsd:nonNegativeInteger ;\n" +
	    				  "        nif:endIndex          \"551\"^^xsd:nonNegativeInteger ;\n" +
	    				  "        nif:entity            <http://dkt.dfki.de/ontologies/nif#person> ;\n" +
	    				  "        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,1519> ;\n" +
	    				  "        itsrdf:taIdentRef     <http://de.dbpedia.org/resource/Henry> .\n" +
	    				  "\n" +
	    				  "<http://dkt.dfki.de/documents/#char=0,1519>\n" +
	    				  "        a               nif:RFC5147String , nif:String , nif:Context ;\n" +
	    				  "        nif:beginIndex  \"0\"^^xsd:nonNegativeInteger ;\n" +
	    				  "        nif:endIndex    \"1519\"^^xsd:nonNegativeInteger ;\n" +
	    				  "        nif:isString    \"Henry 's decision to entrust the regency of his son Edward 's minor years to a decidedly reform-oriented regency council , dominated by Edward Seymour , most likely for the simple tactical reason that Seymour seemed likely to provide the strongest leadership for the kingdom , ensured that the English Reformation would be consolidated and even furthered during his son 's reign .During the reign of Henry VIII , as many as 72,000 people are estimated to have been executed .The power of the state was magnified , yet so too ( at least after Henry 's death ) were demands for increased political participation by the middle class .Together with Alfred the Great and Charles II , Henry is traditionally cited as one of the founders of the Royal Navy .His reign featured some naval warfare and , more significantly , large royal investment in shipbuilding ( including a few spectacular great ships such as Mary Rose ) , dockyards ( such as HMNB Portsmouth ) and naval innovations ( such as the use of cannon on board ship -- although archers were still deployed on medieval-style forecastles and bowcastles as the ship 's primary armament on large ships , or co-armament where cannons were used ) .However , in some ways this is a misconception since Henry did not bequeath to his immediate successors a navy in the sense of a formalised organisation with structures , ranks , and formalised munitioning structures but only in the sense of a set of ships .These were also known as Henry VIII 's Device Forts .\"^^xsd:string .\n" +
	    				  "\n" +
	    				  "<http://dkt.dfki.de/documents/#char=672,682>\n" +
	    				  "        a                     nif:RFC5147String , nif:String ;\n" +
	    				  "        nif:anchorOf          \"Charles II\"^^xsd:string ;\n" +
	    				  "        nif:beginIndex        \"672\"^^xsd:nonNegativeInteger ;\n" +
	    				  "        nif:endIndex          \"682\"^^xsd:nonNegativeInteger ;\n" +
	    				  "        nif:entity            <http://dkt.dfki.de/ontologies/nif#person> ;\n" +
	    				  "        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,1519> .\n" +
	    				  "";	      

	      //Model nifModel2 = NIFWriter.initializeOutputModel();
	      
//	      Model nifModel2;
//		try {
//			nifModel2 = NIFReader.extractModelFromFormatString(testNIF, RDFSerialization.TURTLE);
//			System.out.println("RESULT entities:" + NIFReader.extractEntityIndices(nifModel2));
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	      
		Date d1 = new Date();

		HashMap<String,Integer> subjectCount = new HashMap<String,Integer>();
		HashMap<String,HashMap<String,Integer>> subject2RelationCount = new HashMap<String,HashMap<String,Integer>>();
		String docFolder = "C:\\Users\\pebo01\\Desktop\\ubuntuShare\\out\\out\\english";
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
				System.out.println("FileName:" + f.getName());
				System.out.println("DEBUGGINg result:");
				for (EntityRelationTriple t : ert) {
					System.out.println("subject---relation---object: " + t.getSubject() + "---" + t.getRelation() + "---" + t.getObject());
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
				System.out.println("\n");
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
			
	      
	    

		
	}
	
    
	
	
	
}
