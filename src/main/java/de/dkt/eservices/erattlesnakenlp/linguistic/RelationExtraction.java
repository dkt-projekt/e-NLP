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

import org.apache.commons.collections.CollectionUtils;
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
					String pathToVerbnet = "/home/agata/Documents/programming/verbnet";
					String subjectThemRole = null;
					String objectThemRole = null;



					for (WordLemmaTag SentenceList : tlSentence){
						if (SentenceList.word().equals(connectingElement.word())){
							relationLemma = SentenceList.lemma();

							wordnetInformationSet = WordnetConnector.getWordnetInformation(relationLemma, pathToVerbnet);
							System.out.println("subject " + subject + " object " + object );
							VerbnetConnector.assignThetaRoles(subject, object, objectsDependency, relationLemma, pathToVerbnet);
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





	public static ArrayList<EntityRelationTriple> getDirectRelationsNIF(Model nifModel, String language) throws IOException{

		//TODO discuss if we want to do this at broker startup instead
		Tagger.initTagger(language);
		DepParserTree.initParser(language);

		List<String> englishSubjectRelationTypes = new ArrayList<>(Arrays.asList("nsubj", "nsubjpass", "csubj", "csubjpass"));
		List<String> englishObjectRelationTypes = new ArrayList<>(Arrays.asList("dobj", "cop", "nmod", "iobj", "case", "nmod:poss")); 
		List<String> englishIndirectObjectRelationTypes = new ArrayList<>(Arrays.asList("iobj", "case"));
		List<String> englishIndirectObjectRelationTypesExtended = new ArrayList<>(Arrays.asList("iobj", "case", "nmod", "nmod:poss"));


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
				List<TaggedWord> tagged = Tagger.tagger.tagSentence(sentence);

				final StanfordLemmatizer lemmatizer = StanfordLemmatizer.getInstance(); 
				final List<WordLemmaTag> tlSentence = new ArrayList<WordLemmaTag>();


				for (TaggedWord tw : tagged) {
					tlSentence.add((lemmatizer).lemmatize(tw));
					System.out.println((lemmatizer).lemmatize(tw));
				}

				GrammaticalStructure gs = DepParserTree.parser.predict(tagged);

				HashMap<IndexedWord, IndexedWordTuple> relMap = new HashMap<IndexedWord, IndexedWordTuple>();
				IndexedWord subject = null;
				IndexedWord connectingElement = null;
				IndexedWord object = null;
				IndexedWord iobject = null;
				String objectsDependency = null;
				String iobjectsDependency = null;


				//only the direct verb dependencies are relevant for extracting the arguments 
				List<TypedDependency> allVerbDependenciesList = (List<TypedDependency>) DepParserTree.getAllDirectVerbDependencies(gs.allTypedDependencies());

				//all dependencies to find the prepositions
				Collection<TypedDependency> allDepenednciesList = gs.allTypedDependencies();
				//	DPTreeNode tree =  DepParserTree.generateTreeFromList(allVerbDependenciesList);

				//				
				//				subject = SVOTripleAssignment.assignSubject(gs);
				//				connectingElement = SVOTripleAssignment.assignVerb(gs);
				//				object = SVOTripleAssignment.assignObject(gs);




				for (TypedDependency td : allVerbDependenciesList) {
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
					for (TypedDependency td : allVerbDependenciesList) {
						if (englishObjectRelationTypes.contains(td.reln().toString())) {
							//	System.out.println("Dependency relation " + td.reln().toString());
							if (td.gov().beginPosition() == connectingElement.beginPosition()
									&& td.gov().endPosition() == connectingElement.endPosition()) {
								object = td.dep();							
								objectsDependency = td.reln().toString();

								if (!object.equals(null))
									// assign iobj if it exists, if the list is longer, it has definitely more arguments, 
									//BUT they are not necessary  arguments (i.e. temporal expressions)
									if (allVerbDependenciesList.size()>2 && englishIndirectObjectRelationTypesExtended.contains(td.reln().toString())){

										//										System.out.println("Sentence: " + sentence);
										//										System.out.println("verb dependencies' list + " +allVerbDependenciesList.size() + " " + td);
										//										System.out.println("IOBJ found " + td.reln());

										iobjectsDependency = td.reln().toString();

									}

									else break;
							}
						}
						else if (englishIndirectObjectRelationTypes.contains(td.reln().toString())){
							object = td.gov(); // NOTE: bit tricky; taking case (usually indirect object) relation as object here if nothing found for object. Could be interesting...
						}
					}
				}



				System.out.println("subj" + subject + " connectingElement " + connectingElement + " object " + object );





				//		System.out.println(subject.toString() + connectingElement.toString() + object.toString());
				if (!(subject == null) && !(connectingElement == null) && !(object == null)){
					System.out.println("DEBUGGING relation found:" + subject + " TAG "+subject.tag() + "___" + connectingElement + "___" + object + " " + object.tag());

					String subjectURI = SVOTripleAssignment.getURI(subject,tagged, entityMap);
					String objectURI = SVOTripleAssignment.getURI(object,tagged, entityMap);

					//					// Mendelsohn exception:
					//					if (subject.word().equalsIgnoreCase("i")){
					//						subjectURI = "Eric";
					//					}

					//lemma of the connectingElement
					String relationLemma = null;
					LinkedList<String> wordnetInformationSet = null;
					String pathToVerbnet = "/home/agata/Documents/programming/verbnet";
					String subjectThemRole = null;
					String objectThemRole = null;

					for (WordLemmaTag SentenceList : tlSentence){
						if (SentenceList.word().equals(connectingElement.word())){
							relationLemma = SentenceList.lemma();

							WordnetConnector.printWordnetSenses(relationLemma, pathToVerbnet);
							LinkedList <String> wordnetEntries = WordnetConnector.getWordnetInformation("give", pathToVerbnet);
							LinkedList<String> thetaRolesList = VerbnetConnector.assignThetaRoles(subject, object, objectsDependency, relationLemma, pathToVerbnet);
							
						
							System.out.println("size of theta roles " + thetaRolesList.size() + " wordnet entries " + wordnetEntries );
							System.out.println("relationLemma " + relationLemma);
							if (thetaRolesList.size()>0){
								subjectThemRole = thetaRolesList.get(0);
								objectThemRole = thetaRolesList.get(1);
								System.out.println("subject&object " + subjectThemRole + " obj " + objectThemRole);
							}



							/**


							//comparing the input verb to the list of verbs							
							ArrayList<String> commonSynsetsVerbs = new ArrayList <String> ();							
							for ( String element: ListOfAllVerbs.keySet()){
								boolean similarVerb = WordnetConnector.compare2VerbsSynsets(relationLemma, element, pathToVerbnet);

								//if the verb is similar, it is likely on the list there is a synonym
								//the list is used for building a LSA, where the synonym candidates are compared to each other using a cosinus similarity measure
								if (similarVerb){
									commonSynsetsVerbs.add(relationLemma);
								}
							}

							HashMap <LinkedList <String>, LinkedList <String>> allVerbInfo = new HashMap();
							allVerbInfo.put(wordnetEntries, thetaRolesList);
							ListOfAllVerbs.put(relationLemma, allVerbInfo);
							LSAmatrix.add(subject.toString());
							LSAmatrix.add(relationLemma);
							LSAmatrix.add(object.toString());



							System.out.println(ListOfAllVerbs.keySet().toString() + " ListOfAllVerbs ");
							 **/
						}

						//	SimilarityMeasure.getSublists(LSAmatrix);
					}

					if (!(subjectURI == null) && !(objectURI == null)){
						EntityRelationTriple t = new EntityRelationTriple();
						//t.setSubject(String.format("%s(%s)", subject, subjectURI));
						t.setSubject(String.format("%s", subjectThemRole.concat(subjectURI)));
						t.setRelation(connectingElement.word().concat(" lemma: ").concat(relationLemma));
						//t.setObject(String.format("%s(%s)", object, objectURI));
						t.setObject(String.format("%s", objectThemRole.concat(objectURI)));
						ert.add(t);
					}
				}
			}
		}
		return ert;
	}



	public static ArrayList <EntityRelationTriple> getSynonymRelationTriples (){
		ArrayList<EntityRelationTriple> ert = new ArrayList ();

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

				//System.out.println("Sentence: " + sentence);	


				List<TaggedWord> tagged = Tagger.tagger.tagSentence(sentence);
				/**
				 * 
				 */

				final StanfordLemmatizer lemmatizer = StanfordLemmatizer.getInstance(); 
				final List<WordLemmaTag> tlSentence = new ArrayList<WordLemmaTag>();

				/**
				 * 
				 */

				for (TaggedWord tw : tagged) {
					tlSentence.add((lemmatizer).lemmatize(tw));
					System.out.println((lemmatizer).lemmatize(tw));
				}


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
										String relation = null;
										String relationLemma = null;



										for (WordLemmaTag SentenceList : tlSentence){
											if (SentenceList.word().equals(result.indexedWord.word()))
												relationLemma = SentenceList.lemma();
										}



										relation = String.format("%s(%s)", "LEMMA: " + relationLemma, "http://wordnet...");

										if (iPosition < jPosition) {
											subject = String.format("%s(%s)", sentenceEntities.get(i).getText(), sentenceEntities.get(i).getURI());
											object = String.format("%s(%s)", sentenceEntities.get(j).getText(), sentenceEntities.get(j).getURI());
										} else {
											subject = String.format("%s(%s)", sentenceEntities.get(j).getText(), sentenceEntities.get(j).getURI());
											object = String.format("%s(%s)", sentenceEntities.get(i).getText(), sentenceEntities.get(i).getURI());
										}
										//System.out.println(
										//"result: " + leftWord + "===" + result.value + "===" + rightWord);

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




		Tagger.initTagger("de");
		DepParserTree.initParser("de");

		DocumentPreprocessor tokenizer = new DocumentPreprocessor(new StringReader("Seit zwei Wochen haben sie sich dort vor islamistischen Rebellen verschanzt."));
		for (List<HasWord> sentence : tokenizer) {
			System.out.println("Sentence: " + sentence);
			List<TaggedWord> tagged = Tagger.tagger.tagSentence(sentence);
			GrammaticalStructure gs = DepParserTree.parser.predict(tagged);
			Collection<TypedDependency> list = gs.allTypedDependencies();

		}
		//System.exit(1);

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


		//String ts = "Later that day I bought the janitor a beer.";
		//String ts = "Ginsberg was born into a Jewish family in Newark, New Jersey, and grew up in nearby Paterson.";
		//String ts = "Ginsberg had an auditory hallucination while reading the poetry of William Blake.";
		//String ts = "In 1957, Ginsberg surprised the literary world by abandoning San Francisco.";
		//				DocumentPreprocessor tokenizer2 = new DocumentPreprocessor(new StringReader(ts));
		//				for (List sentence : tokenizer2) {
		//					List tagged = Tagger.tagger.tagSentence(sentence);
		//					GrammaticalStructure gs = DepParserTree.parser.predict(tagged);
		//					System.out.println("DEBUGGING GS:" + gs);
		//					Collection<TypedDependency> allDepenednciesList = gs.allTypedDependencies();
		//					
		//					String connectingElement2 = null;
		//					
		//					Iterator<TypedDependency> dependenciesIterator = allDepenednciesList.iterator();
		//					while (dependenciesIterator.hasNext()){
		//						TypedDependency element = dependenciesIterator.next();
		//					
		//						if (element.reln().toString().equals("conj")){
		//						System.out.println(element + " " + element.gov().toString() + " " + element.dep().toString());
		//					
		//					//second connecting element
		//					connectingElement2 = element.dep().originalText();
		//					String connectingElementLemma = element.dep().toString();

		//2 possible cases - the same subject as in the first part or another nsubj relation

		//}

		//				
		//					
		//					
		//					}
		//					
		//
		//				}
		//				System.exit(1);



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
		//String docFolder = "/home/agata/Documents/programming/files_relation_extraction/wikipedia_NIF";
		String docFolder = "/home/agata/Documents/programming/files_relation_extraction/wikipedia_NIF";
		//String docFolder = "/home/agata/Documents/programming/files_relation_extraction/englishNifsCorefinized";
		//	String filePath = "/home/agata/Documents/programming/files_relation_extraction/NIFfiles_biographies/AllenGinsberg.nif";   	

		//	extractEventsFromTheSource(filePath);

		File df = new File(docFolder);
		System.out.println(df.getAbsolutePath());
		ArrayList<EntityRelationTriple> masterList = new ArrayList<EntityRelationTriple>();
		Date d3 = new Date();
		String debugOut = "/home/agata/Documents/programming/files_relation_extraction/debug.txt";
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
				ArrayList<EntityRelationTriple> ert = getDirectRelationsNIF(nifModel, "en");

				for (EntityRelationTriple t : ert) {
					masterList.add(t);
					System.out.println("Triple: " +t);
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
