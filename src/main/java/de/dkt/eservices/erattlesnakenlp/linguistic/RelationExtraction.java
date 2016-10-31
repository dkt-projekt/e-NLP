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
	
	
	
	
	public static ArrayList<EntityRelationTriple> getDirectRelationsNIF(Model nifModel, String language){
		
		
		//TODO discuss if we want to do this at broker startup instead
		Tagger.initTagger(language);
		DepParserTree.initParser(language);
		
		List<String> englishSubjectRelationTypes = new ArrayList<>(Arrays.asList("nsubj", "nsubjpass", "csubj", "csubjpass"));
		List<String> englishObjectRelationTypes = new ArrayList<>(Arrays.asList("dobj", "cop", "nmod", "iobj", "advmod", "case")); 
		List<String> englishIndirectObjectRelationTypes = new ArrayList<>(Arrays.asList("iobj", "case"));
		
		String isstr = NIFReader.extractIsString(nifModel);
		List<String[]> entityMap = NIFReader.extractEntityIndices(nifModel);
		// extract sameAs annotations and add them to the entityMap
		List<String[]> sameAsMentions = NIFReader.extractSameAsAnnotations(nifModel);
		
		if (entityMap != null && sameAsMentions != null) {
			entityMap.addAll(sameAsMentions);
		}
		
		ArrayList<EntityRelationTriple> ert = new ArrayList<EntityRelationTriple>();
		if (entityMap != null) {
			DocumentPreprocessor tokenizer = new DocumentPreprocessor(new StringReader(isstr));
			for (List<HasWord> sentence : tokenizer) {
				System.out.println("Sentence: " + sentence);
				List<TaggedWord> tagged = Tagger.tagger.tagSentence(sentence);
				GrammaticalStructure gs = DepParserTree.parser.predict(tagged);
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
					//System.out.println("DEBUGGING relation found:" + subject + "___" + connectingElement + "___" + object);
					String subjectURI = null;
					String objectURI = null;
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
//					// Mendelsohn exception:
//					if (subject.word().equalsIgnoreCase("i")){
//						subjectURI = "Eric";
//					}
					
					if (!(subjectURI == null) && !(objectURI == null)){
						EntityRelationTriple t = new EntityRelationTriple();
						//t.setSubject(String.format("%s(%s)", subject, subjectURI));
						t.setSubject(String.format("%s", subjectURI));
						t.setRelation(connectingElement.word());
						//t.setObject(String.format("%s(%s)", object, objectURI));
						t.setObject(String.format("%s", objectURI));
						ert.add(t);
					}
					
				}
				
				
				
			}
		}
		
		return ert;
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
				ArrayList<String> indicesAdded = new ArrayList<String>();
				
				for (String[] sa : entityMap){
					int entityStart = Integer.parseInt(sa[3]);
					int entityEnd = Integer.parseInt(sa[4]);
					if ((entityStart >= tagged.get(0).beginPosition()) && (entityEnd <= tagged.get(tagged.size()-1).beginPosition())){
						for (int k = 0; k < tagged.size(); k++){
							if ((tagged.get(k).beginPosition() >= entityStart) && (tagged.get(k).endPosition() <= entityEnd)){
								SpanWord sw = new SpanWord(sa[1], sa[0], k); // for multi word units, this gets overwritten everytime until the last word of the MWU, but that shouldn't really be an issue
								String index = sa[3] + "_" + sa[4];
								if (!(indicesAdded.contains(index))){
									sentenceEntities.add(sw);
									indicesAdded.add(index);
								}
							}
						}
					}
				}
				
				
				//System.out.println("length of sent: " + sentence.size());
				int distanceThreshold = 5 + (sentence.size() / 4);
				for (int i = 0; i < sentenceEntities.size(); i++){ // compare every entity in this sentence to every other
					for (int j = i+1; j < sentenceEntities.size(); j++){
						// splitting on whitespace because the entity can be multi word unit
						String leftWord = sentenceEntities.get(i).getText().split(" ")[0];
						String rightWord = sentenceEntities.get(j).getText().split(" ")[0];
						int leftTaggedIndex = sentenceEntities.get(i).getTaggedWordsIndex();
						int rightTaggedIndex = sentenceEntities.get(j).getTaggedWordsIndex();
						int leftBeginPosition = Integer.parseInt(indicesAdded.get(i).split("_")[0]);
						int rightBeginPosition = Integer.parseInt(indicesAdded.get(j).split("_")[0]);
						
						// here comes a set of limitations, to hopefully increase accuracy without losing too much recall
						//System.out.println("leftWord: " + leftWord);
						//System.out.println("index:" + leftTaggedIndex);
						//System.out.println("rightWord: " + rightWord);
						//System.out.println("index:" + rightTaggedIndex);
						if (sentenceEntities.get(i).getURI() != null && sentenceEntities.get(j).getURI() != null) { // only proceed if we have a URI for both entities
							DPTreeNode result = tree.getShortestPath(leftWord, rightWord, list2); //TODO: now the leftWord and rightWord are still string based. Only the result.value now has IndexedWord. Fis this so that I am accessing getShortestPath with tagged indices rather than strings. Because currecntly this is not happening, right?
							
							if (result != null){
								int iPosition = sentenceEntities.get(i).getTaggedWordsIndex();
								int jPosition = sentenceEntities.get(j).getTaggedWordsIndex();
								int connBegin = result.indexedWord.beginPosition();
								if (Math.abs(iPosition - jPosition) <= distanceThreshold) {// check if the two entities are not too far away from eachother. Quite naive, but let's see if it works
									//if ((connBegin < leftBeginPosition && connBegin > rightBeginPosition)
											//|| (connBegin > leftBeginPosition && connBegin < rightBeginPosition)) {
										if (result.indexedWord.tag() != null
												&& result.indexedWord.tag().startsWith("V")) {
											// this is a really stupid and naive assumption, but the best we have currently: the entity that appears first in the sentence is supposed to be the subject of the relation triple this is the problem when we take the lowest common verb node; you lose track of which is the real
											// subject and object because there is no relation between them (or at least not necessarily. perhaps it would be possible to
											// trace the path and get this information there.s
											String subject = null;
											String object = null;

											if (iPosition < jPosition) {
												subject = String.format("%s(%s)", sentenceEntities.get(i).getText(), sentenceEntities.get(i).getURI());
												object = String.format("%s(%s)", sentenceEntities.get(j).getText(), sentenceEntities.get(j).getURI());
											} else {
												subject = String.format("%s(%s)", sentenceEntities.get(j).getText(), sentenceEntities.get(j).getURI());
												object = String.format("%s(%s)", sentenceEntities.get(i).getText(), sentenceEntities.get(i).getURI());
											}
											//System.out.println(
													//"result: " + leftWord + "===" + result.value + "===" + rightWord);
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
							//}
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
		
		
		
		
		Tagger.initTagger("de");
		DepParserTree.initParser("de");
		
		DocumentPreprocessor tokenizer = new DocumentPreprocessor(new StringReader("Seit zwei Wochen haben sie sich dort vor islamistischen Rebellen verschanzt."));
		for (List<HasWord> sentence : tokenizer) {
			System.out.println("Sentence: " + sentence);
			List<TaggedWord> tagged = Tagger.tagger.tagSentence(sentence);
			GrammaticalStructure gs = DepParserTree.parser.predict(tagged);
			Collection<TypedDependency> list = gs.allTypedDependencies();
			
		}
		System.exit(1);
		
		//String tempString = "This is a sentence; with a semi-colon in it.";
		String tempString = 
				"\n" +
						"[Datumsstempel: MAR 16 1942]\n" +
						" \n" +
						"Mrs. Luise Mendelsohn\n" +
						"The Alden\n" +
						"225 Central Park West\n" +
						"New York City\n" +
						"[Absender]\n" +
						"The Drake\n" +
						"Lake Shore Drive\n" +
						"Chicago\n" +
						"[Anmerkung: 1b, 21/III 42.]\n" +
						"\n" +
						"The Drake\n" +
						"Chicago\n" +
						"III.21.42.\n" +
						"\n" +
						"The flowers came and your\n" +
						"letter, and I feel not totally\n" +
						"cut off from where I came and\n" +
						"shall go back to. They deserve a\n" +
						"special reply tomorrow. -\n" +
						"After a very lively luncheon\n" +
						"with Prof. Lorch and family at\n" +
						"Ann Arbor I streamlined to\n" +
						"Chicago into the Drake fossil. The\n" +
						"technical details I admired 18\n" +
						"years ago have become either\n" +
						"loose or dusty and the seclusive\n" +
						"refinement has made place a\n" +
						"\n" +
						"humdrum of incentives for\n" +
						"lady-battleships or pre-war jeunesse-\n" +
						"chromée. \n" +
						"I had oyster dinner with Mies\n" +
						"and later at his flat much serious\n" +
						"talk and laughter. Hilbersheimer\n" +
						"came joining us.\n" +
						"My lecture at Mies&apos;s department,\n" +
						"for students only, was tremendously\n" +
						"cheered and Mies regretted not having\n" +
						"invited the architects and the public.\n" +
						"Dinner at a Greek Restaurant, invited\n" +
						"by Hilbersh. \n" +
						"Today, I went to the Arts Club, where\n" +
						"in the \"Wrigley\" Bldg. my work refuses\n" +
						"to be chewed, a posthumous sight for\n" +
						"me and an unborn for Chicago.\n" +
						"Met a French (Baronesse of course)\n" +
						"infected by Jenny de Margerie with\n" +
						"Mendelsohnitis,\n" +
						"\n" +
						"and the Lady President of the Club\n" +
						"looking like a cluster of overpainted\n" +
						"too late autumn apples.\n" +
						"Badly hung and no critics. Sealed\n" +
						"lips because of unopened brass-heads. \n" +
						"Then, the Sulzbergers came. Too rich as\n" +
						"not to be conceated. They don&apos;t think\n" +
						"that modern Architecture and World Wars\n" +
						"have any connection or George III any\n" +
						"with war reverses!\n" +
						"I think to cancel their invitation for\n" +
						"supper tomorrow night - 7 miles bus-\n" +
						"ride for, certainly, cold meat.\n" +
						"In an hour&apos;s time, Cocktail Party\n" +
						"at Mies - apparently for Bach, no\n" +
						"Mass or remembrance visible.\n" +
						"After that my counter invitation for\n" +
						"dinner - Mies and friend. Did&apos;nt see\n" +
						"her yet:\n" +
						"\n" +
						"Monday night - Marc gentlemen\n" +
						"Dinner.\n" +
						"Tuesday night - Dinner &amp; Lecture\n" +
						"of the Institute within my exhibition.\n" +
						"An embar[r]assing background.\n" +
						"Wednesday to Ann Arbor - 2nd lecture\n" +
						"on Thursday.\n" +
						"Thursday night, may be Dinner with\n" +
						"Albert Kahn&apos;s in Detroit and Friday\n" +
						"morning to New York. There is, of course,\n" +
						"a day train and Plant&apos;s office has\n" +
						"just wasted money. Nevertheless,\n" +
						"your spring costume is certain.\n" +
						"Weather in Chicago changes from\n" +
						"late spring to deep winter. I am\n" +
						"transpiring and caughing.\n" +
						"An impossible climate, a depressing\n" +
						"vista to my sensitive eyes - a world\n" +
						"untouched by a 30 years fight! Mr. benedicted\n" +
						"by\n" +
						"Benedictus\n";
//		
//		DocumentPreprocessor tokenizer = new DocumentPreprocessor(new StringReader(tempString));
//		for (List<HasWord> sentence : tokenizer) {
//			System.out.println("Sentence: " + sentence);
//		}
//		System.exit(1);
		Tagger.initTagger("en");
		DepParserTree.initParser("en");

//		String ts = "Later she moved to Brandenburg with her family.";
//		DocumentPreprocessor tokenizer = new DocumentPreprocessor(new StringReader(ts));
//		for (List<HasWord> sentence : tokenizer) {
//			List<TaggedWord> tagged = Tagger.tagger.tagSentence(sentence);
//			GrammaticalStructure gs = DepParserTree.parser.predict(tagged);
//			System.out.println("DEBUGGING GS:" + gs);
//		}
		//System.exit(1);
		//String docFolder = "C:\\Users\\pebo01\\Desktop\\ubuntuShare\\WikiWars_20120218_v104\\nifs";
		//String docFolder = "C:\\Users\\pebo01\\Desktop\\ubuntuShare\\WikiWars_20120218_v104\\nifs";
		
		//String docFolder = "C:\\Users\\pebo01\\Desktop\\data\\artComSampleFilesDBPediaTimeouts\\outputNifs";
		//String docFolder = "C:\\Users\\pebo01\\Desktop\\data\\3pc_Data\\enLetters\\nif";
		//String docFolder = "C:\\Users\\pebo01\\Desktop\\data\\ARTCOM\\artComSampleFilesDBPediaTimeouts\\nifsCorefinized";
		String docFolder = "C:\\Users\\pebo01\\Desktop\\data\\mendelsohnDocs\\englishNifsCorefinized";
		

		File df = new File(docFolder);
		ArrayList<EntityRelationTriple> masterList = new ArrayList<EntityRelationTriple>();
		Date d3 = new Date();
		String debugOut = "C:\\Users\\pebo01\\Desktop\\debug.txt";
		BufferedWriter brDebug = null;
		int c = 0;
		for (File f : df.listFiles()) {
			c += 1;
			String fileContent;
			System.out.println("Processing file: " + f.getName());
			try {
				fileContent = readFile(f.getAbsolutePath(), StandardCharsets.UTF_8);
				Model nifModel = NIFReader.extractModelFromFormatString(fileContent, RDFSerialization.TURTLE);
				ArrayList<EntityRelationTriple> ert = getRelationsNIF(nifModel);
				//ArrayList<EntityRelationTriple> ert = getDirectRelationsNIF(nifModel, "en");
				
				for (EntityRelationTriple t : ert) {
					masterList.add(t);
				}
		
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		try {
			brDebug = FileFactory.generateOrCreateBufferedWriterInstance(debugOut, "utf-8", false);
			HashMap<String,HashMap<String,HashMap<String,Integer>>> m = convertRelationTripleListToHashMap(masterList);
			JSONObject jsonMap = new JSONObject(m);
			brDebug.write(jsonMap.toString(4));
			brDebug.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		System.out.println("Done."); 
		System.exit(1);
	    
		
		String nifString = 
				"@prefix dktnif: <http://dkt.dfki.de/ontologies/nif#> .\n" +
						"@prefix dbo:   <http://dbpedia.org/ontology/> .\n" +
						"@prefix geo:   <http://www.w3.org/2003/01/geo/wgs84_pos/> .\n" +
						"@prefix nif-ann: <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-annotation#> .\n" +
						"@prefix rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n" +
						"@prefix owl:   <http://www.w3.org/2002/07/owl#> .\n" +
						"@prefix xsd:   <http://www.w3.org/2001/XMLSchema#> .\n" +
						"@prefix itsrdf: <http://www.w3.org/2005/11/its/rdf#> .\n" +
						"@prefix nif:   <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#> .\n" +
						"@prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#> .\n" +
						"\n" +
						"<http://dkt.dfki.de/documents/#char=0,13>\n" +
						"        a                     nif:String , nif:RFC5147String ;\n" +
						"        dbo:birthDate         \"1954-07-17\"^^xsd:date ;\n" +
						"        nif:anchorOf          \"Angela Merkel\"^^xsd:string ;\n" +
						"        nif:beginIndex        \"0\"^^xsd:nonNegativeInteger ;\n" +
						"        nif:endIndex          \"13\"^^xsd:nonNegativeInteger ;\n" +
						"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,86> ;\n" +
						"        itsrdf:taClassRef     dbo:Person ;\n" +
						"        itsrdf:taIdentRef     <http://dbpedia.org/resource/Angela_Merkel> .\n" +
						"\n" +
						"<http://dkt.dfki.de/documents/#char=21,29>\n" +
						"        a                     nif:RFC5147String , nif:String ;\n" +
						"        nif:anchorOf          \"an alien\"^^xsd:string ;\n" +
						"        nif:beginIndex        \"21\"^^xsd:nonNegativeInteger ;\n" +
						"        nif:endIndex          \"29\"^^xsd:nonNegativeInteger ;\n" +
						"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,86> ;\n" +
						"        owl:sameAs            \"http://dkt.dfki.de/documents/#char=0,13\"^^xsd:string .\n" +
						"\n" +
						"<http://dkt.dfki.de/documents/#char=56,59>\n" +
						"        a                     nif:RFC5147String , nif:String ;\n" +
						"        nif:anchorOf          \"She\"^^xsd:string ;\n" +
						"        nif:beginIndex        \"56\"^^xsd:nonNegativeInteger ;\n" +
						"        nif:endIndex          \"59\"^^xsd:nonNegativeInteger ;\n" +
						"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,86> ;\n" +
						"        owl:sameAs            \"http://dkt.dfki.de/documents/#char=0,13\"^^xsd:string .\n" +
						"\n" +
						"<http://dkt.dfki.de/documents/#char=0,86>\n" +
						"        a                        nif:RFC5147String , nif:String , nif:Context ;\n" +
						"        dktnif:averageLatitude   \"52.96361111111111\"^^xsd:double ;\n" +
						"        dktnif:averageLongitude  \"11.504722222222222\"^^xsd:double ;\n" +
						"        dktnif:standardDeviationLatitude\n" +
						"                \"0.6016666666666666\"^^xsd:double ;\n" +
						"        dktnif:standardDeviationLongitude\n" +
						"                \"1.5033333333333339\"^^xsd:double ;\n" +
						"        nif:beginIndex           \"0\"^^xsd:nonNegativeInteger ;\n" +
						"        nif:endIndex             \"86\"^^xsd:nonNegativeInteger ;\n" +
						"        nif:isString             \"Angela Merkel is not an alien. She was born in Hamburg. She then moved to Brandenburg.\"^^xsd:string .\n" +
						"\n" +
						"<http://dkt.dfki.de/documents/#char=47,54>\n" +
						"        a                     nif:String , nif:RFC5147String ;\n" +
						"        nif:anchorOf          \"Hamburg\"^^xsd:string ;\n" +
						"        nif:beginIndex        \"47\"^^xsd:nonNegativeInteger ;\n" +
						"        nif:endIndex          \"54\"^^xsd:nonNegativeInteger ;\n" +
						"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,86> ;\n" +
						"        geo:lat               \"53.56527777777778\"^^xsd:double ;\n" +
						"        geo:long              \"10.001388888888888\"^^xsd:double ;\n" +
						"        itsrdf:taClassRef     dbo:Location ;\n" +
						"        itsrdf:taIdentRef     <http://dbpedia.org/resource/Hamburg> .\n" +
						"\n" +
						"<http://dkt.dfki.de/documents/#char=31,34>\n" +
						"        a                     nif:RFC5147String , nif:String ;\n" +
						"        nif:anchorOf          \"She\"^^xsd:string ;\n" +
						"        nif:beginIndex        \"31\"^^xsd:nonNegativeInteger ;\n" +
						"        nif:endIndex          \"34\"^^xsd:nonNegativeInteger ;\n" +
						"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,86> ;\n" +
						"        owl:sameAs            \"http://dkt.dfki.de/documents/#char=0,13\"^^xsd:string .\n" +
						"\n" +
						"<http://dkt.dfki.de/documents/#char=74,85>\n" +
						"        a                     nif:RFC5147String , nif:String ;\n" +
						"        nif:anchorOf          \"Brandenburg\"^^xsd:string ;\n" +
						"        nif:beginIndex        \"74\"^^xsd:nonNegativeInteger ;\n" +
						"        nif:endIndex          \"85\"^^xsd:nonNegativeInteger ;\n" +
						"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,86> ;\n" +
						"        geo:lat               \"52.36194444444445\"^^xsd:double ;\n" +
						"        geo:long              \"13.008055555555556\"^^xsd:double ;\n" +
						"        itsrdf:taClassRef     dbo:Location ;\n" +
						"        itsrdf:taIdentRef     <http://dbpedia.org/resource/Brandenburg> .\n" +
						"\n";
//		ArrayList<EntityRelationTriple> collectionRelationList = new ArrayList<EntityRelationTriple>();
//		String debugOut = "C:\\Users\\pebo01\\Desktop\\debug.txt";
//		BufferedWriter brDebug = null;
//		try {
//			Model nifModel = NIFReader.extractModelFromFormatString(nifString, RDFSerialization.TURTLE);
//			ArrayList<EntityRelationTriple> ert = getDirectRelationsNIF(nifModel, "en");
//			for (EntityRelationTriple t : ert) {
//				collectionRelationList.add(t);
//			}
//			
//			System.out.println("DEBUGGING erT:" + ert);
//			brDebug = FileFactory.generateOrCreateBufferedWriterInstance(debugOut, "utf-8", false);
//			HashMap<String,HashMap<String,HashMap<String,Integer>>> m = convertRelationTripleListToHashMap(collectionRelationList);
//			JSONObject jsonMap = new JSONObject(m);
//			brDebug.write(jsonMap.toString(4));
//			brDebug.close();
//			
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
		
		

	}
	
}
