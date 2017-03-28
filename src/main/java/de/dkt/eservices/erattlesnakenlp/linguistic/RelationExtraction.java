package de.dkt.eservices.erattlesnakenlp.linguistic;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;
import org.json.JSONObject;

import com.google.common.base.Objects;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.hp.hpl.jena.graph.compose.Union;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import de.dkt.common.filemanagement.FileFactory;
import de.dkt.common.niftools.NIFReader;
import de.dkt.common.niftools.NIFWriter;
import de.dkt.eservices.ecorenlp.modules.Tagger;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.Annotator;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.time.SUTime.Duration;
import edu.stanford.nlp.time.SUTime.Range;
import edu.stanford.nlp.time.SUTime.Temporal;
import edu.stanford.nlp.time.TimeAnnotator;
import edu.stanford.nlp.time.TimeExpression;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TypedDependency;
import edu.stanford.nlp.trees.WordStemmer;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.TypesafeMap;
import eu.freme.common.conversion.rdf.RDFConstants.RDFSerialization;
import net.didion.jwnl.JWNL;
import net.didion.jwnl.JWNLException;
import net.didion.jwnl.data.IndexWord;
import net.didion.jwnl.data.POS;
import net.didion.jwnl.data.Synset;
import net.didion.jwnl.dictionary.Dictionary;
import opennlp.tools.lemmatizer.SimpleLemmatizer;
import edu.stanford.nlp.ling.Word; 
import edu.stanford.nlp.ling.WordLemmaTag;
import edu.mit.jverbnet.data.FrameType;
import edu.mit.jverbnet.data.IFrame;
import edu.mit.jverbnet.data.IMember;
import edu.mit.jverbnet.data.IThematicRole;
import edu.mit.jverbnet.data.IVerbClass;
import edu.mit.jverbnet.data.IWordnetKey;
import edu.mit.jverbnet.data.syntax.ISyntaxArgDesc;
import edu.mit.jverbnet.index.IVerbIndex;
import edu.mit.jverbnet.index.VerbIndex;




/**
 * @author Julian Moreno Schneider julian.moreno_schneider@dfki.de, Peter Bourgonje peter.bourgonje@dfki.de
 *
 */
public class RelationExtraction {
	static HashMap <String, HashMap<LinkedList<String>, LinkedList<String>>> ListOfAllVerbs = new HashMap ();
	static ArrayList <String> LSAmatrix = new ArrayList <String> ();

	static String readFile(String path, Charset encoding) 
			throws IOException 
	{
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}

	//method with a Triple <S, V, ObjectsList>, instead of having one (String) object, it is a list of one or more objects
	public static ArrayList<EntityRelationTriple> getDirectRelationsNIFObjectsList(Model nifModel, String language) throws IOException{


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

				final StanfordLemmatizer lemmatizer = StanfordLemmatizer.getInstance(); 
				final List<WordLemmaTag> tlSentence = new ArrayList<WordLemmaTag>();

				for (TaggedWord tw : tagged) {
					tlSentence.add((lemmatizer).lemmatize(tw));
					//System.out.println((lemmatizer).lemmatize(tw));
				}

				GrammaticalStructure gs = DepParserTree.parser.predict(tagged);
				HashMap<IndexedWord, IndexedWordTuple> relMap = new HashMap<IndexedWord, IndexedWordTuple>();
				IndexedWord subject = null;
				IndexedWord connectingElement = null;
				IndexedWord object = null;
				String objectsDependency = null;
				String iobjectsDependency = null;

				/**
				 * 
				 */

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
								objectsDependency = td.reln().toString();

							}
						}
						else if (englishIndirectObjectRelationTypes.contains(td.reln().toString())){
							object = td.gov(); // NOTE: bit tricky; taking case (usually indirect object) relation as object here if nothing found for object. Could be interesting...
						}
					}
				}


				if (!(subject == null) && !(connectingElement == null) && !(object == null)){
					//	System.out.println("DEBUGGING relation found:" + subject + " TAG "+subject.tag() + "___" + connectingElement + "___" + object);

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

					//lemma of the connectingElement
					String relationLemma = null;
					LinkedList<String> wordnetInformationSet = null;
					String pathToVerbnet2 = "verbnet" + File.separator + "new_vn_3.2" + File.separator;
					File f = FileFactory.generateFileInstance(pathToVerbnet2);
					String pathToVerbnet = f.getAbsolutePath();
					String subjectThemRole = null;
					String objectThemRole = null;
					VerbnetConnector verbnetConn = new VerbnetConnector ();
					WordnetConnector wordnetConn = new WordnetConnector();



					for (WordLemmaTag SentenceList : tlSentence){
						if (SentenceList.word().equals(connectingElement.word())){
							relationLemma = SentenceList.lemma();
							System.out.println("data running " + relationLemma);
							wordnetInformationSet = wordnetConn.getWordnetInformation(relationLemma, pathToVerbnet);
							System.out.println("subject " + subject + " object " + object );
							verbnetConn.assignThetaRoles(subject, object, objectsDependency,  iobjectsDependency, relationLemma, pathToVerbnet);
						}
					}


					if (!(subjectURI == null) && !(objectURI == null)){
						EntityRelationTriple t = new EntityRelationTriple();
						//t.setSubject(String.format("%s(%s)", subject, subjectURI));
						t.setSubject(String.format("%s", subjectThemRole.concat(subjectURI)));
						t.setRelation(connectingElement.word().concat(" lemma: ").concat(relationLemma).concat(" "));
						//.concat(wordnetInformationSet));
						//t.setObject(String.format("%s(%s)", object, objectURI));
						t.setObject(String.format("%s", objectThemRole.concat(objectURI)));
						ert.add(t);
					}

				}



			}

		}

		return ert;
	}





	public static ArrayList<EntityRelationTriple> getDirectRelationsNIF(Model nifModel, String language, ArrayList<EntityRelationTriple> ert) throws IOException{

		//TODO discuss if we want to do this at broker startup instead
		Tagger.initTagger(language);
		DepParserTree.initParser(language);

		String isstr = NIFReader.extractIsString(nifModel);

		List<String[]> entityMap = NIFReader.extractEntityIndices(nifModel);
		// extract sameAs annotations and add them to the entityMap
		List<String[]> sameAsMentions = NIFReader.extractSameAsAnnotations(nifModel);

		if (entityMap != null && sameAsMentions != null) {
			entityMap.addAll(sameAsMentions);
		}

		
		if (entityMap != null) {
			DocumentPreprocessor tokenizer = new DocumentPreprocessor(new StringReader(isstr));
			for (List<HasWord> sentence : tokenizer) {
				System.out.println(sentence);
				List<TaggedWord> tagged = Tagger.tagger.tagSentence(sentence);

				final StanfordLemmatizer lemmatizer = StanfordLemmatizer.getInstance(); 
				final List<WordLemmaTag> tlSentence = new ArrayList<WordLemmaTag>();


				for (TaggedWord tw : tagged) {
					tlSentence.add((lemmatizer).lemmatize(tw));
				}

				GrammaticalStructure gs = DepParserTree.parser.predict(tagged);

				HashMap<IndexedWord, IndexedWordTuple> relMap = new HashMap<IndexedWord, IndexedWordTuple>();
				IndexedWord subject1 = SVOTripleAssignment.getSubject(gs);
				IndexedWord connectingElement1 = SVOTripleAssignment.getVerb(gs);
				IndexedWord object1 = SVOTripleAssignment.getObject(gs, SVO_Object.getIndirectObjectList(gs).get(0));
				int sentenceStart = tagged.get(0).beginPosition(); // TODO: debug this carefully!

//				//only the direct verb dependencies are relevant for extracting the arguments 
//				List<TypedDependency> allVerbDependenciesList = (List<TypedDependency>) DepParserTree.getAllDirectVerbDependencies(gs.allTypedDependencies());
//
//				//all dependencies to find the prepositions
//				Collection<TypedDependency> allDepenednciesList = gs.allTypedDependencies();
//				//	DPTreeNode tree =  DepParserTree.generateTreeFromList(allVerbDependenciesList);


				if (!(subject1 == null) && !(connectingElement1 == null) && !(object1 == null)){
					System.out.println();
					System.out.println("DEBUGGING relation found:" + subject1 + " TAG "+subject1.tag() + "___" + connectingElement1 + "___" + object1 + " " + object1.tag());
					
					String subjectURI = SVOTripleAssignment.getURI(subject1,tagged, entityMap);
					String objectURI = SVOTripleAssignment.getURI(object1, tagged, entityMap);

					System.out.println("subjectURI " + subjectURI + " objectRI " + objectURI + "--");

					// lemma of the connectingElement
					String relationLemma = null;
					LinkedList<String> wordnetInformationSet = null;

					String pathToVerbnet = "C:\\Users\\Sabine\\Downloads\\vn_3_14\\verbnet";

					String pathToVerbnet2 = "verbnet" + File.separator + "new_vn_3.2" + File.separator;
					File f = FileFactory.generateFileInstance(pathToVerbnet2);
					String pathToVerbnet = f.getAbsolutePath();
					
					String subjectThemRole = null;
					String objectThemRole = null;
					String objectsDependency = SVO_Object.getObjectRelationType(gs).reln().toString();
					String iobjectsDependency = SVO_Object.getSecondObjectRelationType(gs).reln().toString();

					for (WordLemmaTag SentenceList : tlSentence) {
						if (SentenceList.word().equals(connectingElement1.word())) {
							relationLemma = SentenceList.lemma();

							// THETA ROLES' ASSIGNMENT
							WordnetConnector wordnetConn = new WordnetConnector();
							VerbnetConnector verbnetConn = new VerbnetConnector();

							LinkedList<String> thetaRolesList = verbnetConn.assignThetaRoles(subject1, object1,
									objectsDependency, iobjectsDependency, relationLemma, pathToVerbnet);
							verbnetConn.getAssignedRolesList();

							// System.out.println("PROCESSING START");
							// wordEl.getSimplifiedPOSTagsList(gs);
							// System.out.println("PROCESSING STOP");

							System.out.println("size of theta roles " + thetaRolesList.size());
							// System.out.println("###relationLemma " +
							// relationLemma + "getWordByDep " +
							// WordElement.getWordByDependency("nmod", gs) +
							// "###");
							if (thetaRolesList.size() > 0) {
								subjectThemRole = thetaRolesList.get(0);
								objectThemRole = thetaRolesList.get(1);
								System.out.println("subject&object " + subjectThemRole + " obj " + objectThemRole);
							}
							SVO_Verb verb = new SVO_Verb();
							int listSize = verb.getDirectRootDependenciesList(gs).size();
							// System.out.println("the POS tag of the
							// object: " +
							// wordEl.getPOStagOfDependent(object1.word(),gs));
							System.out.println("all direct verbDep: " + listSize);

							/**
							 * 
							 * 
							 * //comparing the input verb to the list of verbs
							 * ArrayList<String> commonSynsetsVerbs = new
							 * ArrayList <String> (); for ( String element:
							 * ListOfAllVerbs.keySet()){ boolean similarVerb =
							 * WordnetConnector.compare2VerbsSynsets(
							 * relationLemma, element, pathToVerbnet);
							 * 
							 * //if the verb is similar, it is likely on the
							 * list there is a synonym //the list is used for
							 * building a LSA, where the synonym candidates are
							 * compared to each other using a cosinus similarity
							 * measure if (similarVerb){
							 * commonSynsetsVerbs.add(relationLemma); } }
							 * 
							 * HashMap <LinkedList <String>, LinkedList
							 * <String>> allVerbInfo = new HashMap();
							 * allVerbInfo.put(wordnetEntries, thetaRolesList);
							 * ListOfAllVerbs.put(relationLemma, allVerbInfo);
							 * LSAmatrix.add(subject.toString());
							 * LSAmatrix.add(relationLemma);
							 * LSAmatrix.add(object.toString());
							 * 
							 * 
							 * 
							 * System.out.println(ListOfAllVerbs.keySet().
							 * toString() + " ListOfAllVerbs ");
							 **/
						}

						// SimilarityMeasure.getSublists(LSAmatrix);
					}
					// TODO
					// extend the method to add the subject's and the
					// objects' thematic roles

					// EntityRelationTriple t =
					// SVOTripleAssignment.setEntityRelationTriple(subjectURI,
					// objectURI, gs);
					// System.out.println("assignment end " + t.getSubject()
					// + " obj " + t.getObject() + " rel "
					// +t.getRelation());
					ArrayList<EntityRelationTriple> ertripleList = SVOTripleAssignment
							.getEntityRelationTripleList(subjectURI, objectURI, gs, sentenceStart, tagged);
					// ert.addAll(ertripleList);
					// ert.add(t);
					for (EntityRelationTriple t : ertripleList){
						ert.add(t);
					}
//					ert = (ArrayList<EntityRelationTriple>) Stream.concat(ert.stream(), ertripleList.stream())
//							.collect(Collectors.toList());
				}
				
			}
		}
//		for (EntityRelationTriple t : ert){
//			System.out.println("debugging return vlaue of of tripe list:" + t.getStartIndex() +"|" + t.getEndIndex()+"|" + t.getRelation()+"|" + t.getSubject()+"|" + t.getLemma()+"|" + t.getObject()+"|" + t.getThemRoleSubj()+"|" + t.getThemRoleObj());
//		}
		return ert;
	}



//	public static ArrayList <EntityRelationTriple> getSynonymRelationTriples (){
//		ArrayList<EntityRelationTriple> ert = new ArrayList ();
//
//		return ert;
//	}


//	public static ArrayList<EntityRelationTriple> getRelationsNIF(Model nifModel){
//
//		String isstr = NIFReader.extractIsString(nifModel);
//		List<String[]> entityMap = NIFReader.extractEntityIndices(nifModel);
//		ArrayList<EntityRelationTriple> ert = new ArrayList<EntityRelationTriple>();
//
//		// if we have entities in the sentence, start parsing
//		if (!(entityMap == null)) {
//			DocumentPreprocessor tokenizer = new DocumentPreprocessor(new StringReader(isstr));
//
//			for (List<HasWord> sentence : tokenizer) {
//
//				//System.out.println("Sentence: " + sentence);	
//
//
//				List<TaggedWord> tagged = Tagger.tagger.tagSentence(sentence);
//				/**
//				 * 
//				 */
//
//				final StanfordLemmatizer lemmatizer = StanfordLemmatizer.getInstance(); 
//				final List<WordLemmaTag> tlSentence = new ArrayList<WordLemmaTag>();
//
//				/**
//				 * 
//				 */
//
//				for (TaggedWord tw : tagged) {
//					tlSentence.add((lemmatizer).lemmatize(tw));
//					System.out.println((lemmatizer).lemmatize(tw));
//				}
//
//
//				GrammaticalStructure gs = DepParserTree.parser.predict(tagged);				
//				Collection<TypedDependency> list = gs.allTypedDependencies();
//
//
//				HashMap<IndexedWord, Integer> swm = new HashMap<IndexedWord, Integer>();
//				for (TypedDependency td : list){
//					swm.put(td.dep(), td.dep().index()-1);		
//				}
//
//
//				DPTreeNode tree =  DepParserTree.generateTreeFromList(list);
//				List<DPTreeNode> list2 = new LinkedList<DPTreeNode>();
//
//
//				List<SpanWord> sentenceEntities = new ArrayList<SpanWord>();
//				ArrayList<String> indicesAdded = new ArrayList<String>();
//
//				for (String[] sa : entityMap){
//					int entityStart = Integer.parseInt(sa[3]);
//					int entityEnd = Integer.parseInt(sa[4]);
//					if ((entityStart >= tagged.get(0).beginPosition()) && (entityEnd <= tagged.get(tagged.size()-1).beginPosition())){
//						for (int k = 0; k < tagged.size(); k++){
//							if ((tagged.get(k).beginPosition() >= entityStart) && (tagged.get(k).endPosition() <= entityEnd)){
//								SpanWord sw = new SpanWord(sa[1], sa[0], k); // for multi word units, this gets overwritten everytime until the last word of the MWU, but that shouldn't really be an issue
//								String index = sa[3] + "_" + sa[4];
//								if (!(indicesAdded.contains(index))){
//									sentenceEntities.add(sw);
//									indicesAdded.add(index);
//								}
//							}
//						}
//					}
//				}
//
//
//				//System.out.println("length of sent: " + sentence.size());
//				int distanceThreshold = 5 + (sentence.size() / 4);
//				for (int i = 0; i < sentenceEntities.size(); i++){ // compare every entity in this sentence to every other
//					for (int j = i+1; j < sentenceEntities.size(); j++){
//						// splitting on whitespace because the entity can be multi word unit
//						String leftWord = sentenceEntities.get(i).getText().split(" ")[0];
//						String rightWord = sentenceEntities.get(j).getText().split(" ")[0];
//						int leftTaggedIndex = sentenceEntities.get(i).getTaggedWordsIndex();
//						int rightTaggedIndex = sentenceEntities.get(j).getTaggedWordsIndex();
//						int leftBeginPosition = Integer.parseInt(indicesAdded.get(i).split("_")[0]);
//						int rightBeginPosition = Integer.parseInt(indicesAdded.get(j).split("_")[0]);
//
//						// here comes a set of limitations, to hopefully increase accuracy without losing too much recall
//						//System.out.println("leftWord: " + leftWord);
//						//System.out.println("index:" + leftTaggedIndex);
//						//System.out.println("rightWord: " + rightWord);
//						//System.out.println("index:" + rightTaggedIndex);
//						if (sentenceEntities.get(i).getURI() != null && sentenceEntities.get(j).getURI() != null) { // only proceed if we have a URI for both entities
//							DPTreeNode result = tree.getShortestPath(leftWord, rightWord, list2); //TODO: now the leftWord and rightWord are still string based. Only the result.value now has IndexedWord. Fis this so that I am accessing getShortestPath with tagged indices rather than strings. Because currecntly this is not happening, right?
//
//							if (result != null){
//								int iPosition = sentenceEntities.get(i).getTaggedWordsIndex();
//								int jPosition = sentenceEntities.get(j).getTaggedWordsIndex();
//								int connBegin = result.indexedWord.beginPosition();
//								if (Math.abs(iPosition - jPosition) <= distanceThreshold) {// check if the two entities are not too far away from eachother. Quite naive, but let's see if it works
//									//if ((connBegin < leftBeginPosition && connBegin > rightBeginPosition)
//									//|| (connBegin > leftBeginPosition && connBegin < rightBeginPosition)) {
//									if (result.indexedWord.tag() != null
//											&& result.indexedWord.tag().startsWith("V")) {
//										// this is a really stupid and naive assumption, but the best we have currently: the entity that appears first in the sentence is supposed to be the subject of the relation triple this is the problem when we take the lowest common verb node; you lose track of which is the real
//										// subject and object because there is no relation between them (or at least not necessarily. perhaps it would be possible to
//										// trace the path and get this information there.s
//										String subject = null;
//										String object = null;
//										String relation = null;
//										String relationLemma = null;
//
//
//
//										for (WordLemmaTag SentenceList : tlSentence){
//											if (SentenceList.word().equals(result.indexedWord.word()))
//												relationLemma = SentenceList.lemma();
//										}
//
//
//
//										relation = String.format("%s(%s)", "LEMMA: " + relationLemma, "http://wordnet...");
//
//										if (iPosition < jPosition) {
//											subject = String.format("%s(%s)", sentenceEntities.get(i).getText(), sentenceEntities.get(i).getURI());
//											object = String.format("%s(%s)", sentenceEntities.get(j).getText(), sentenceEntities.get(j).getURI());
//										} else {
//											subject = String.format("%s(%s)", sentenceEntities.get(j).getText(), sentenceEntities.get(j).getURI());
//											object = String.format("%s(%s)", sentenceEntities.get(i).getText(), sentenceEntities.get(i).getURI());
//										}
//										//System.out.println(
//										//"result: " + leftWord + "===" + result.value + "===" + rightWord);
//
//										EntityRelationTriple t = new EntityRelationTriple();
//										t.setSubject(subject);
//										t.setRelation(relation);
//										t.setObject(object);
//										
//
//										ert.add(t);
//									}
//									// System.out.println(leftWord + "+++" +
//									// result.value + "+++" + rightWord);
//								}
//							}
//							//}
//						}
//					}
//				}
//
//
//			}
//		}
//
//		return ert;
//
//	}

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

	//	public static void extractEventsFromTheSource(String filePath) throws IOException{
	//		String jsonLdString = readFile(filePath, StandardCharsets.UTF_8);
	//		try {
	//			Model nifModel = NIFReader.extractModelFromFormatString(jsonLdString, RDFSerialization.JSON_LD);
	//			List<String[]> events = new LinkedList<String[]>();
	//			ResIterator iterEntities = nifModel.listSubjects();
	//			while (iterEntities.hasNext()) {
	//				Resource r = iterEntities.nextResource();
	//				if (r.toString().startsWith("http://dkt.dfki.de/documents/#event")){
	//					System.out.println(r);
	//					StmtIterator si = r.listProperties();
	//					while(si.hasNext()){
	//						Statement r2 = si.nextStatement();
	//						System.out.println(r2);
	//					}
	//				}
	//			}
	//		} catch (Exception e) {
	//			// TODO Auto-generated catch block
	//			e.printStackTrace();
	//		}
	//	}





	public static void main(String args[]) throws JWNLException, IOException{

		Tagger.initTagger("en");
		DepParserTree.initParser("en");


		String docFolder = "C:\\Users\\Sabine\\Desktop\\WörkWörk\\testfiles";

		//String docFolder = "/home/agata/Documents/programming/files_relation_extraction/run_example";

		//String docFolder = "C:\\Users\\pebo01\\Desktop\\debugRelExtract\\nifs";
		//String docFolder = "/home/agata/Documents/programming/files_relation_extraction/englishNifsCorefinized";
		/**
		 * another examples
		 **/
		// CONJ in subject position
		//Sumner and his family moved to Tallahatchie County, Mississippi from Alabama around January, 1872.

		//	String filePath = "/home/agata/Documents/programming/files_relation_extraction/NIFfiles_biographies/AllenGinsberg.nif";   	

		//	extractEventsFromTheSource(filePath);

		File df = new File(docFolder);
		System.out.println(df.getAbsolutePath());
		ArrayList<EntityRelationTriple> masterList = new ArrayList<EntityRelationTriple>();
		Date d3 = new Date();

		String debugOut = "C:\\Users\\Sabine\\Desktop\\WörkWörk\\out.txt";

		//String debugOut = "/home/agata/Documents/programming/files_relation_extraction/debug.txt";

		//String debugOut = "C:\\Users\\pebo01\\Desktop\\debug.txt";
		BufferedWriter brDebug = null;
		//printWordNetInfo();
		int c = 0;
		for (File f : df.listFiles()) {
			c += 1;
			String fileContent;
			System.out.println("Processing file: " + f.getName());
			try {
				fileContent = readFile(f.getAbsolutePath(), StandardCharsets.UTF_8);
				Model nifModel = NIFReader.extractModelFromFormatString(fileContent, RDFSerialization.TURTLE);
				//ArrayList<EntityRelationTriple> ert = getRelationsNIF(nifModel);
				ArrayList<EntityRelationTriple> ert = new ArrayList<EntityRelationTriple>();
				ert = getDirectRelationsNIF(nifModel, "en", ert);
				// make the annotation in nif
				for (EntityRelationTriple t : ert){
					System.out.println("debugging the relation triple:" + t.getSubject() + t.getObject() + t.getRelation() + t.getLemma());
					NIFWriter.addAnnotationRelation(nifModel, t.getStartIndex(), t.getEndIndex(), t.getRelation(), t.getSubject(), t.getLemma(), t.getObject(), t.getThemRoleSubj(), t.getThemRoleObj());
				}
				System.out.println("DEBUGGING nif output:\n" + NIFReader.model2String(nifModel, RDFSerialization.TURTLE));
				
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

	}

}
