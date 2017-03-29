package de.dkt.eservices.erattlesnakenlp.linguistic;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import de.dkt.common.filemanagement.FileFactory;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.TypedDependency;

public class SVOTripleAssignment {
	static List<String> englishSubjectRelationTypes = new ArrayList<>(Arrays.asList("nsubj", "nsubjpass", "csubj", "csubjpass"));
	static List<String> englishObjectRelationTypes = new ArrayList<>(Arrays.asList("dobj", "cop", "nmod", "iobj", "advmod", "case", "ccomp")); 
	static List<String> englishIndirectObjectRelationTypes = new ArrayList<>(Arrays.asList("iobj", "case"));
	static String pathToVerbnet = "/home/agata/Documents/programming/verbnet";
	static int tripleCounter = 0;
	static int nmodPrepGeneralizationsCounter = 0;
	static int themRolesFoundCounter=0;


	static List<String> excludedPosTagObjects = new ArrayList<>(Arrays.asList("PRP$", "POS", "TO", "JJ", "DT", "IN", "RB",  "POS"));
	static SVO_VerbRelationType verbRelType = new SVO_VerbRelationType();
	private static IndexedWord currentverb;

	//There is a slight difference between these two function getSubject() and getSubjectConjunction()
	//The first one is needed to return an IndexedWord, which contains all the grammatical information that is needed for further processing
	//The second one is a simple string which combines two nouns, that are connected via a conjunction relation, i.e. Anne and Mary; 
	//in this case the grammatical information is lost
	public static IndexedWord getSubject(GrammaticalStructure gs){
		return SVO_Subject.assignSubject(gs);
	}
	public static String getSubjectConjunction(GrammaticalStructure gs){
		return SVO_Subject.subjectConjunction(gs);
	}
	//The subject in the passive voice does not carry out the action, so in this place he is assigned as a subject, 
	//but later on while triple assignment the position of the subject and the object is swapped, 
	//so that the end user that sees the right person who has killed someone, 
	//i.e. He was killed by his wife. Letting the order as it is would result in having a triple (he, kill, wife) instead of (wife, kill, he)
	public static String getSubjPassiveVoice (GrammaticalStructure gs){
		String subj = SVO_Subject.passiveVoiceSubject(gs);
		return subj;
	}

	//Just as for the subject and subjectConjunction, there is the case where we find another verb in the sentence, that occurs in a conjunction relation with the first one.
	public static IndexedWord getVerb(GrammaticalStructure gs){
		return SVO_Verb.assignVerb(gs);
	}

	public static String getVerbConjRelation(GrammaticalStructure gs){
		String verbConjRelation= verbRelType.conjRelation(gs).word();
		return verbConjRelation;
	}

	public static IndexedWord getObject(GrammaticalStructure gs, TypedDependency objectDependencyType){
		return SVO_Object.assignObject(gs, objectDependencyType);
	}

	public static IndexedWord getSecondObject(GrammaticalStructure gs){
		return SVO_Object.assignSecondObject(gs);
	}


	private static String getPassiveVoiceObject(GrammaticalStructure gs) {
		String passiveVoiceObj = SVO_Object.getPassiveVoiceObject(gs);
		return passiveVoiceObj;
	}	


	public static EntityRelationTriple setTriple (String subject, String verb, String object, String subjectURI, String objectURI,  int start, int end){
		EntityRelationTriple t = new EntityRelationTriple();
		StanfordLemmatizer lemmatizer = new StanfordLemmatizer();

		if (!(subjectURI == null) && !(objectURI == null)){
			t.setSubject(String.format("%s(%s)", subject, subjectURI));
			t.setRelation(verb);
			t.setObject(String.format("%s(%s)", object, objectURI));
			t.setStartIndex(start);
			t.setEndIndex(end);
			t.setLemma(lemmatizer.lemmatizeWord(verb));
			t.setThemRoleSubj("themRoleSubj");
			t.setThemRoleSubj("themRoleObj");
		}

		return t;
	}



	public static ArrayList<EntityRelationTriple> getEntityRelationTripleList(String subjectURI, String objectURI, GrammaticalStructure gs, int sentenceStart, List<TaggedWord> tagged) throws IOException{
		
		ArrayList <EntityRelationTriple> entityRelationTripleList = new ArrayList <EntityRelationTriple> ();
		EntityRelationTriple t = new EntityRelationTriple();

		ArrayList <TypedDependency> objectsList = SVO_Object.getIndirectObjectList(gs);

		for (int i = 0; i <objectsList.size(); i++){
			TypedDependency objectDependencyType = objectsList.get(i);



			if (!excludedPosTagObjects.contains(getObject(gs, objectDependencyType).tag())){
				//System.out.println(getObject(gs, objectDependencyType).toString() + "getObj: " + getObject(gs, objectDependencyType) + " " + getObject(gs, objectDependencyType).tag());
				t = setEntityRelationTriple(subjectURI,objectURI, gs, objectDependencyType, sentenceStart, tagged);

				entityRelationTripleList.add(t);
				tripleCounter = tripleCounter +1;
				System.out.println("tripleCounter: " + tripleCounter);


			}
		}
		return entityRelationTripleList;
	}

	public static HashMap <String, String> getThematicRoles (String subjectURI, String objectURI, GrammaticalStructure gs, List<TaggedWord> tagged) throws IOException{
		HashMap <String, String> thematicRolesList = new HashMap();
		int dummyIndex = 0; // TODO: WARNING: this should be the sentenceStart index (in the whole document)
		ArrayList<EntityRelationTriple> entityRelationTripleList = getEntityRelationTripleList(subjectURI, objectURI, gs, dummyIndex, tagged);
		String subjectThemRole = null;
		String objectThemRole = null; 
		String iobjectThemRole = null; 


		//The length of the list refers to the amount of the arguments in the sentence 
		for (int i=0; i<entityRelationTripleList.size(); i++){
			EntityRelationTriple ert = entityRelationTripleList.get(i);
			if (subjectThemRole == null){
				ert.getSubject();

			}
			else if (iobjectThemRole == null){

			}

			//entityRelationTripleList.get(i).get


		}
		return thematicRolesList;
	}
	public static IndexedWord setCurrentVerb(IndexedWord verb){
		return currentverb;
	}
	public static IndexedWord getCurrentVerb(){
		return currentverb;
	}

	//There are a few cases that need to be checked in order to assign the right arguments, like:
	//copula, conjunction (both: in the subject and in the verb position), advcl relation and passive
	public static EntityRelationTriple setEntityRelationTriple(String subjectURI, String objectURI, GrammaticalStructure gs, TypedDependency objectDependencyType, int sentenceStartIndex, List<TaggedWord> tagged) throws IOException{
		int nmodCounter = 0;

		EntityRelationTriple t = new EntityRelationTriple();
		String verbConjRelation = getVerbConjRelation(gs);
		IndexedWord relationVerb = getVerb(gs);
		VerbnetConnector verbnetConnector = new VerbnetConnector();
		//		<<<<<<< HEAD
		//		=======
		//		String pathToVerbnet2 = "verbnet" + File.separator + "new_vn_3.2" + File.separator;
		//		File f = FileFactory.generateFileInstance(pathToVerbnet2);
		//		String pathToVerbnet = f.getAbsolutePath();
		String pathToVerbnet = "/home/agata/Documents/programming/verbnet";
		//		>>>>>>> branch 'master' of https://github.com/dkt-projekt/e-NLP

		//Copula: the object is recognized as the verb, and the verb is an object; here-> swapped (He is an actor)
		//found triple: (he, an actor, is); changed to: (he, is an actor)
		if (verbRelType.getCopula(gs).length()>1){
			t = setTriple(getSubjectConjunction(gs), getObject(gs, objectDependencyType).word(),  relationVerb.word(), subjectURI, objectURI , tagged.get(getObject(gs, objectDependencyType).index()-1).beginPosition() + sentenceStartIndex, tagged.get(getObject(gs, objectDependencyType).index()-1).endPosition() + sentenceStartIndex);
			System.out.println("----- FINAL TRIPLE 1 (copula)--- " + getSubjectConjunction(gs) + " verb: " + getObject(gs, objectDependencyType).toString() + " object: " + getVerb(gs).word());		
		}
		else {
			if (WordElement.getPositionWordInSentence(verbConjRelation, gs) > WordElement.getPositionWordInSentence(getObject(gs, objectDependencyType).word(), gs)){
				t = setTriple(getSubjectConjunction(gs), relationVerb.word(),  getObject(gs, objectDependencyType).word(), subjectURI, objectURI  , tagged.get(relationVerb.index()-1).beginPosition() + sentenceStartIndex, tagged.get(relationVerb.index()-1).endPosition() + sentenceStartIndex);
				System.out.println("----- FINAL TRIPLE 2 not(copula)--- " + getSubjectConjunction(gs) + " object: " + getObject(gs, objectDependencyType).word() + "  verb: " + getVerb(gs).word());
			}
			else if (verbConjRelation != null){ 
				if (verbConjRelation.length()>1){
					t = setTriple(getSubjectConjunction(gs), relationVerb.word(),  "" , subjectURI, objectURI  , tagged.get(relationVerb.index()-1).beginPosition() + sentenceStartIndex, tagged.get(relationVerb.index()-1).endPosition() + sentenceStartIndex);
					System.out.println("----- FINAL TRIPLE 3 (VerbConj, SV) --- " + getSubjectConjunction(gs) + " object: " + "empty" + "  verb: " + getVerb(gs).word());
				}
			}
			else {
				t = setTriple(getSubjectConjunction(gs), relationVerb.word(),  getObject(gs, objectDependencyType).word(), subjectURI, objectURI  , tagged.get(relationVerb.index()-1).beginPosition() + sentenceStartIndex, tagged.get(relationVerb.index()-1).endPosition() + sentenceStartIndex);
				System.out.println("----- FINAL TRIPLE 4 (VerbConj, SVO) --- " + getSubjectConjunction(gs) + " object: " + getObject(gs, objectDependencyType).word() + "  verb: " + getVerb(gs).word());
			}
		}

		if (verbRelType.advclRelation(gs).word() != null){
			if (verbRelType.advclRelation(gs).word().length()>1){ 
				relationVerb = verbRelType.advclRelation(gs);
				t = setTriple(getSubjectConjunction(gs), verbRelType.advclRelation(gs).word(),  getSecondObject(gs).word(), subjectURI, objectURI , tagged.get(relationVerb.index()-1).beginPosition() + sentenceStartIndex, tagged.get(relationVerb.index()-1).endPosition() + sentenceStartIndex);
				System.out.println("----- FINAL TRIPLE 5 (AdvCl) --- " + getSubjectConjunction(gs) + " verb: " + verbRelType.advclRelation(gs) + " object: " + getSecondObject(gs));

			}
		}


		if (verbConjRelation != null){
			if (verbConjRelation.length()>1){
				relationVerb = verbRelType.conjRelation(gs);
				t = setTriple(getSubjectConjunction(gs), verbRelType.conjRelation(gs).word(),  getSecondObject(gs).word(), subjectURI, objectURI , tagged.get(relationVerb.index()-1).beginPosition() + sentenceStartIndex,  tagged.get(relationVerb.index()-1).endPosition() + sentenceStartIndex);
				System.out.println("----- FINAL TRIPLE 6 --- " + getSubjectConjunction(gs) + " verb: " + verbConjRelation + " object: " +
						getSecondObject(gs) + " objConjRel:" + SVO_Object.assignObjectConjRelation(gs, objectDependencyType));
			}
		}

		String subjPass = getSubjPassiveVoice(gs);
		String objPass = getPassiveVoiceObject(gs);
		relationVerb =  getVerb(gs);

		if (subjPass.length()>1 && objPass.length()>1){
			//System.out.println("--- " + " PASSIVE VOICE: " + subjPass + " v: " + getVerb(gs).word() + "objPass: " + objPass);
			t = setTriple(objPass, relationVerb.word(), subjPass, subjectURI, objectURI , tagged.get(getVerb(gs).index()-1).beginPosition() + sentenceStartIndex, tagged.get(getVerb(gs).index()-1).endPosition() + sentenceStartIndex);
			System.out.println("----- FINAL TRIPLE 7 (pass) --- " + objPass + " verb: " + getVerb(gs).word() + " object: " + subjPass);
		}

		System.out.println("--- start --- thematic role assignment.");
		verbnetConnector.assignThematicRoles(relationVerb, gs, pathToVerbnet);
		//LinkedList as Hashmap with thematic roles ([A], [P]...) & the phrase structure  (NP/PP)
		LinkedList<HashMap<String, String>> listOfThemRoles = verbnetConnector.matchPOStagStructureWithVerbnet(relationVerb, gs, pathToVerbnet);
		WordElement wordEl = new WordElement();

		if (listOfThemRoles.size()>0){
			themRolesFoundCounter = themRolesFoundCounter +1 ;

		}
		//THEMATIC ROLES
		//START
		//values -> NP/PREP; keySet() -> thematic roles
		for (int i=0; i<listOfThemRoles.size(); i++){
			System.out.println("themRolesValues " +listOfThemRoles.get(i).values() + " " +listOfThemRoles.get(i).keySet() + " short name: " +objectDependencyType.reln().getShortName() + " " + objectDependencyType.reln().getLongName() + objectDependencyType.gov() + " " +objectDependencyType.dep().ner() + objectDependencyType.dep().tag() + " " + wordEl.getPOStagOfDependent(objectDependencyType.dep().value(), gs));


			if (objectDependencyType.reln().getShortName().equals("nmod") && listOfThemRoles.get(i).keySet().toString().equals("[[PREP]]") || listOfThemRoles.get(i).keySet().toString().equals("[PREP]")){
				String posTagOfTheDependent = wordEl.getPOStagOfDependent(objectDependencyType.dep().value(), gs);
				System.out.println("inside --------------------------------------------------" + listOfThemRoles.get(i).keySet().toString() + " " +listOfThemRoles.get(i).keySet().toString().equals("[[PREP]]"));

				System.out.println("postag: posTagOfTheDependent" + posTagOfTheDependent + listOfThemRoles.get(i).keySet().toString().equals("[PREP]"));

				if (i<listOfThemRoles.size()){

					if (posTagOfTheDependent.equals("IN") && i<=listOfThemRoles.size() && listOfThemRoles.get(i+1).keySet().toString().equals("[L]") 
							||  listOfThemRoles.get(i+1).keySet().toString().equals("[T]") 
							|| listOfThemRoles.get(i+1).keySet().toString().equals("ILocation")){ //ILocation
					
						t.setThemRoleObj(listOfThemRoles.get(i+1).keySet().toString());
						
						System.out.println("NMOD, CASE 1: themRole (LOCATION)/TIME " + listOfThemRoles.get(i+1).keySet());
						nmodCounter = nmodCounter +1;
						nmodPrepGeneralizationsCounter = nmodPrepGeneralizationsCounter +1;

					}
					else if (posTagOfTheDependent.equals("TO") && i<=listOfThemRoles.size() && listOfThemRoles.get(i+1).keySet().toString().equals("[G]") || listOfThemRoles.get(i+1).keySet().toString().equals("[D]")){


						System.out.println("NMOD, CASE 2: themRole (GOAL&DESTINATION) " + listOfThemRoles.get(i+1).keySet());
						nmodCounter = nmodCounter +1;
						nmodPrepGeneralizationsCounter = nmodPrepGeneralizationsCounter +1;

					}
					else if (posTagOfTheDependent.equals("FOR") && i<=listOfThemRoles.size() && listOfThemRoles.get(i+1).keySet().toString().equals("[B]")){

						nmodCounter = nmodCounter +1;

						System.out.println("NMOD, CASE 3: themRole (BENEFICIARY) " + listOfThemRoles.get(i+1).keySet());

						nmodPrepGeneralizationsCounter = nmodPrepGeneralizationsCounter +1;

					}
					else if (posTagOfTheDependent.equals("FROM") && i<=listOfThemRoles.size() && listOfThemRoles.get(i+1).keySet().toString().equals("[S]")){

						nmodCounter = nmodCounter +1;

						System.out.println("NMOD, CASE 4: themRole (SOURCE) " + listOfThemRoles.get(i+1).keySet());
						nmodPrepGeneralizationsCounter = nmodPrepGeneralizationsCounter +1;

					}


				}


			}
			System.out.println("_____________themRolesCounter: " + themRolesFoundCounter +"_____nmodPrepGeneralizationsCounter: " + nmodPrepGeneralizationsCounter);
			//System.out.println("NMOD_COUNTER: --------------------------------------------------" + nmodCounter +);




		}


		return t;
	}


	public static String getURI (IndexedWord argument, List<TaggedWord> tagged, List<String[]> entityMap){
		String argumentURI = null;
		int argumentStart = tagged.get(argument.index()-1).beginPosition();
		int argumentEnd = tagged.get(argument.index()-1).endPosition();

		for (String[] l : entityMap){
			if (argumentStart >= Integer.parseInt(l[3]) && argumentEnd <= Integer.parseInt(l[4])){ // the >= and <= are because the dependency parser is likely to cut up multi-word entities and make the head of the MWE the subject
				//argumentURI = l[0]; // this gets the linked URI (DBPedia), but we also want to get it if there is no linking done. Hence below...
				argumentURI = l[5];
			}
		}
		return argumentURI;
	}
}
