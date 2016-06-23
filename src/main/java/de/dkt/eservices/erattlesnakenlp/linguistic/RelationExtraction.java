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
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONObject;

import com.hp.hpl.jena.rdf.model.Model;

import de.dkt.common.filemanagement.FileFactory;
import de.dkt.common.niftools.NIFReader;
import de.dkt.eservices.ecorenlp.modules.Tagger;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.TypedDependency;
import eu.freme.common.conversion.rdf.RDFConstants.RDFSerialization;

/**
 * @author Julian Moreno Schneider julian.moreno_schneider@dfki.de, Peter Bourgonje peter.bourgonje@dfki.de
 *
 */
public class RelationExtraction {
	
	
	static String readFile(String path, Charset encoding) 
			  throws IOException 
			{
			  byte[] encoded = Files.readAllBytes(Paths.get(path));
			  return new String(encoded, encoding);
			}

	public static ArrayList<EntityRelationTriple> getRelationsNIF(Model nifModel){
		
		String isstr = NIFReader.extractIsString(nifModel);
		List<String[]> entityMap = NIFReader.extractEntityIndices(nifModel);
		ArrayList<EntityRelationTriple> ert = new ArrayList<EntityRelationTriple>();
		
		// if we have entities in the sentence, start parsing
		if (!(entityMap == null)) {
			DocumentPreprocessor tokenizer = new DocumentPreprocessor(new StringReader(isstr));
			for (List<HasWord> sentence : tokenizer) {
				System.out.println("Sentence: " + sentence);
				List<TaggedWord> tagged = Tagger.tagger.tagSentence(sentence);
				GrammaticalStructure gs = DepParserTree.parser.predict(tagged);
				Collection<TypedDependency> list = gs.allTypedDependencies();
				HashMap<IndexedWord, Integer> swm = new HashMap<IndexedWord, Integer>();
				for (TypedDependency td : list){
					swm.put(td.dep(), td.dep().index()-1);
				}
				DPTreeNode tree =  DepParserTree.generateTreeFromList(list);
				List<DPTreeNode> list2 = new LinkedList<DPTreeNode>();
				List<SpanWord> sentenceEntities = new ArrayList<SpanWord>();
				for (TaggedWord tw : tagged){
					for (String[] sa : entityMap){
						if (Integer.parseInt(sa[3]) >= tagged.get(0).beginPosition() && Integer.parseInt(sa[4]) <= tagged.get(tagged.size()-1).endPosition()){ // if the entity is inside this sentence
							SpanWord sw = new SpanWord(sa[1], sa[0], tagged.indexOf(tw)); // SpanWord is now entity label (anchorOf), URI (taIdentRef) and index in tagged TODO: edit name, because it's a gross abuse of the original idea
							sentenceEntities.add(sw);
						}
					}
				}
				for (int i = 0; i < sentenceEntities.size(); i++){ // compare every entity in this sentence to every other
					for (int j = i+1; j < sentenceEntities.size(); j++){
						String leftWord = sentenceEntities.get(i).getText();
						String rightWord = sentenceEntities.get(j).getText();
						if (sentenceEntities.get(i).getURI() != null
								&& sentenceEntities.get(j).getURI() != null) { // only proceed if we have a URI for both entities
							DPTreeNode result = tree.getShortestPath(leftWord, rightWord, list2); //TODO: now the leftWord and rightWord are still string based. Only the result.value now has IndexedWord. Fis this so that I am accessing getShortestPath with tagged indices rather than strings. Because currecntly this is not happening, right?
							if (result != null){
								if (result.indexedWord.tag() != null && result.indexedWord.tag().startsWith("V")) {
									// this is a really stupid and naive assumption, but the best we have currently: the entity that appears first in the sentence is supposed to be the subject of the relation triple
									// this is the problem when we take the lowest common verb node; you lose track of which is the real subject and object because there is no direct relation between them (or at least not necessarily.
									// perhaps it would be possible to trace the path and get this information there.
									String subject = null;
									String object = null;
								
									if (sentenceEntities.get(i).getTaggedWordsIndex() < sentenceEntities.get(j).getTaggedWordsIndex()) {
										subject = String.format("%s(%s)", sentenceEntities.get(i).getText(), sentenceEntities.get(i).getURI());
										object = String.format("%s(%s)", sentenceEntities.get(j).getText(), sentenceEntities.get(j).getURI());
									} else {
										subject = String.format("%s(%s)", sentenceEntities.get(j).getText(), sentenceEntities.get(j).getURI());
										object = String.format("%s(%s)", sentenceEntities.get(i).getText(), sentenceEntities.get(i).getURI());
									}
									String relation = result.value;
									EntityRelationTriple t = new EntityRelationTriple();
									t.setSubject(subject);
									t.setRelation(relation);
									t.setObject(object);
									ert.add(t);
								}
								// System.out.println(leftWord + "+++" +
								// result.value + "+++" + rightWord);
							}
						}
					}
				}
				

			}
		}
		
		return ert;
		
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
	
	public static void main(String args[]){
		
		Tagger.initTagger("en");
		DepParserTree.initParser("en");

		String docFolder = "C:\\Users\\pebo01\\Desktop\\ubuntuShare\\WikiWars_20120218_v104\\nifs";

		File df = new File(docFolder);
		ArrayList<EntityRelationTriple> masterList = new ArrayList<EntityRelationTriple>();
		Date d3 = new Date();
		int c = 0;
		for (File f : df.listFiles()) {
			c += 1;
			String fileContent;
			System.out.println("Processing file: " + f.getName());
			try {
				fileContent = readFile(f.getAbsolutePath(), StandardCharsets.UTF_8);
				Model nifModel = NIFReader.extractModelFromFormatString(fileContent, RDFSerialization.TURTLE);
				ArrayList<EntityRelationTriple> ert = getRelationsNIF(nifModel);
				for (EntityRelationTriple t : ert) {
					masterList.add(t);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		
		
		String debugOut = "C:\\Users\\pebo01\\Desktop\\debug.txt";
		BufferedWriter brDebug = null;
		try {
			brDebug = FileFactory.generateOrCreateBufferedWriterInstance(debugOut, "utf-8", false);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		HashMap<String,HashMap<String,HashMap<String,Integer>>> m = convertRelationTripleListToHashMap(masterList);
		JSONObject jsonMap = new JSONObject(m);
		try {
			//brDebug.write(jsonMap.toString());
			// pretty print:
			brDebug.write(jsonMap.toString(4));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		System.out.println("Done."); 
			
	    try {
			brDebug.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
}
