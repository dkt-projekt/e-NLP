package de.dkt.eservices.erattlesnakenlp.linguistic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.TypedDependency;

public class SVOTripleAssignment {
	static List<String> englishSubjectRelationTypes = new ArrayList<>(Arrays.asList("nsubj", "nsubjpass", "csubj", "csubjpass"));
	static List<String> englishObjectRelationTypes = new ArrayList<>(Arrays.asList("dobj", "cop", "nmod", "iobj", "advmod", "case", "ccomp")); 
	static List<String> englishIndirectObjectRelationTypes = new ArrayList<>(Arrays.asList("iobj", "case"));
	static List<String> excludedPosTagObjects = new ArrayList<>(Arrays.asList("PRP$", "POS", "TO", "JJ", "DT", "IN", "RB", "PRP", "POS"));
	static ArrayList<String> posTagsList = new ArrayList();
	static SVO_VerbRelationType verbRelType = new SVO_VerbRelationType();
	static HashMap <String, String> SVO_themRolesList = new HashMap();

	public static IndexedWord getSubject(GrammaticalStructure gs){
		return SVO_Subject.assignSubject(gs);
	}
	public static String getSubjectConjunction(GrammaticalStructure gs){
		return SVO_Subject.subjectConjunction(gs);
	}

	public static String getSubjPassiveVoice (GrammaticalStructure gs){
		String subj = SVO_Subject.passiveVoiceSubject(gs);
		return subj;
	}




	public static IndexedWord getVerb(GrammaticalStructure gs){
		return SVO_Verb.assignVerb(gs);
	}


	public static String getVerbConjRelation(GrammaticalStructure gs){
		String verbConjRelation= verbRelType.conjRelation(gs);
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


	public static EntityRelationTriple setTriple (String subject, String verb, String object, String subjectURI, String objectURI){
		EntityRelationTriple t = new EntityRelationTriple();

		if (!(subjectURI == null) && !(objectURI == null)){
			t.setSubject(String.format("%s(%s)", subject, subjectURI));
			//	t.setSubject(String.format("%s", subjectThemRole.concat(subjectURI)));
			t.setRelation(verb);
			//.concat(" lemma: ").concat(relationLemma));
			t.setObject(String.format("%s(%s)", object, objectURI));
			//t.setObject(String.format("%s", objectThemRole.concat(objectURI)));
		}
		else {
			t.setSubject(String.format("%s(%s)", subject, subjectURI));
			t.setRelation(verb);
			t.setObject(String.format("%s(%s)", object, objectURI));
		}
		return t;
	}


	public static ArrayList<EntityRelationTriple> getEntityRelationTripleList(String subjectURI, String objectURI, GrammaticalStructure gs){
		ArrayList <EntityRelationTriple> entityRelationTripleList = new ArrayList <EntityRelationTriple> ();
		EntityRelationTriple t = new EntityRelationTriple();

		SVO_Verb verbClass = new SVO_Verb();
		String verbConjRelation = getVerbConjRelation(gs);
		ArrayList <TypedDependency> objectsList = SVO_Object.getIndirectObjectList(gs);

		for (int i = 0; i <objectsList.size(); i++){
			TypedDependency objectDependencyType = objectsList.get(i);

			if (!excludedPosTagObjects.contains(getObject(gs, objectDependencyType).tag())){
				System.out.println(getObject(gs, objectDependencyType).toString() + "getObj: " + getObject(gs, objectDependencyType) + " " + getObject(gs, objectDependencyType).tag());
				t = setEntityRelationTriple(subjectURI,objectURI, gs, objectDependencyType);
				entityRelationTripleList.add(t);
			}
		}
		return entityRelationTripleList;
	}

	public static HashMap <String, String> getThematicRoles (String subjectURI, String objectURI, GrammaticalStructure gs){
		HashMap <String, String> thematicRolesList = new HashMap();
		ArrayList<EntityRelationTriple> entityRelationTripleList = getEntityRelationTripleList(subjectURI, objectURI, gs);
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



	public static EntityRelationTriple setEntityRelationTriple(String subjectURI, String objectURI, GrammaticalStructure gs, TypedDependency objectDependencyType){
		EntityRelationTriple t = new EntityRelationTriple();
		SVO_Verb verbClass = new SVO_Verb();
		String verbConjRelation = getVerbConjRelation(gs);
		ArrayList <TypedDependency> objectsList = SVO_Object.getIndirectObjectList(gs);


		if (verbRelType.getCopula(gs).length()>1){
			//in case of 'cop', the object is recognized as the verb, and the verb is an object; here-> reversed			
			t = setTriple(getSubjectConjunction(gs), getObject(gs, objectDependencyType).toString(),  getVerb(gs).word(), subjectURI, objectURI );
			System.out.println("----- FINAL TRIPLE 1 (copula)--- " + getSubjectConjunction(gs) + " verb: " + getObject(gs, objectDependencyType).toString() + " object: " + getVerb(gs).word());		
		}
		else {

			if (WordElement.getPositionWordInSentence(verbConjRelation, gs) > WordElement.getPositionWordInSentence(getObject(gs, objectDependencyType).word(), gs)){
				t = setTriple(getSubjectConjunction(gs), getVerb(gs).word(),  getObject(gs, objectDependencyType).word(), subjectURI, objectURI );
				System.out.println("----- FINAL TRIPLE 2 !(copula)--- " + getSubjectConjunction(gs) + " object: " + getObject(gs, objectDependencyType).word() + "  verb: " + getVerb(gs).word());
			}
			else if (verbConjRelation.length()>1){
				t = setTriple(getSubjectConjunction(gs), getVerb(gs).word(),  "" , subjectURI, objectURI );
				System.out.println("----- FINAL TRIPLE 3 (VerbConj, SV) --- " + getSubjectConjunction(gs) + " object: " + "empty" + "  verb: " + getVerb(gs).word());
			}
			else {
				t = setTriple(getSubjectConjunction(gs), getVerb(gs).word(),  getObject(gs, objectDependencyType).word(), subjectURI, objectURI );
				System.out.println("----- FINAL TRIPLE 4 (VerbConj, SVO) --- " + getSubjectConjunction(gs) + " object: " + getObject(gs, objectDependencyType).word() + "  verb: " + getVerb(gs).word());
			}
		}

		if (verbRelType.advclRelation(gs).length()>1){
			t = setTriple(getSubjectConjunction(gs), verbRelType.advclRelation(gs),  getSecondObject(gs).word(), subjectURI, objectURI);
			System.out.println("----- FINAL TRIPLE 5 (AdvCl) --- " + getSubjectConjunction(gs) + " verb: " + verbRelType.advclRelation(gs) + " object: " + getSecondObject(gs));
		}

		if (verbConjRelation.length()>1){
			t = setTriple(getSubjectConjunction(gs), verbConjRelation,  getSecondObject(gs).word(), subjectURI, objectURI);
			System.out.println("----- FINAL TRIPLE 6 --- " + getSubjectConjunction(gs) + " verb: " + verbConjRelation + " object: " +
					getSecondObject(gs) + " objConjRel:" + SVO_Object.assignObjectConjRelation(gs, objectDependencyType));
		}

		String subjPass = getSubjPassiveVoice(gs);
		String objPass = getPassiveVoiceObject(gs);

		if (subjPass.length()>1 && objPass.length()>1){
			//System.out.println("--- " + " PASSIVE VOICE: " + subjPass + " v: " + getVerb(gs).word() + "objPass: " + objPass);
			t = setTriple(objPass, getVerb(gs).word(), subjPass, subjectURI, objectURI);
			System.out.println("----- FINAL TRIPLE 7 (pass) --- " + objPass + " verb: " + getVerb(gs).word() + " object: " + subjPass);
		}


		//		String apposVerb = verbRelType .apposRelation(gs);
		//		System.out.println("apposition found: " + apposVerb);

//		if (SVO_themRolesList.isEmpty()){
//			SVO_themRolesList.add(t.getSubject(), );
//		}

		return t;
	}




	public static ArrayList <EntityRelationTriple> setEntityRelationTripleSet(String subjectURI, String objectURI, GrammaticalStructure gs){
		ArrayList <EntityRelationTriple> tripleList = new ArrayList<EntityRelationTriple>();


		return tripleList;
	}

	public static String getURI (IndexedWord argument, List<TaggedWord> tagged, List<String[]> entityMap){
		String argumentURI = null;
		int argumentStart = tagged.get(argument.index()-1).beginPosition();
		int argumentEnd = tagged.get(argument.index()-1).endPosition();

		for (String[] l : entityMap){
			if (argumentStart >= Integer.parseInt(l[3]) && argumentEnd <= Integer.parseInt(l[4])){ // the >= and <= are because the dependency parser is likely to cut up multi-word entities and make the head of the MWE the subject
				argumentURI = l[0];
			}
		}
		return argumentURI;
	}

	//	public static String getURI (IndexedWord argument, List tagged, List entityMap, int sentenceStart){
	//		String argumentURI = null;int argumentStart = tagged.get(argument.index()-1).beginPosition() + sentenceStart; // TODO: debug if this is the beginposition in the sentence only (think so), or if it is the document index// suggestion: passing on sentenceStart as argumentint argumentEnd = tagged.get(argument.index()-1).endPosition() + sentenceStart;for (String[] l : entityMap){if (argumentStart >= Integer.parseInt(l[3]) && argumentEnd <= Integer.parseInt(l[4])){ // the >= and <= are because the dependency parser is likely to cut up multi-word entities and make the head of the MWE the subjectargumentURI = l[0];}}
	//		return argumentURI;}



}
