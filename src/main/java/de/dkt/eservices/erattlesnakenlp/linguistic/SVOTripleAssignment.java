package de.dkt.eservices.erattlesnakenlp.linguistic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.TypedDependency;

public class SVOTripleAssignment {
	static List<String> englishSubjectRelationTypes = new ArrayList<>(Arrays.asList("nsubj", "nsubjpass", "csubj", "csubjpass"));
	static List<String> englishObjectRelationTypes = new ArrayList<>(Arrays.asList("dobj", "cop", "nmod", "iobj", "advmod", "case", "ccomp")); 
	static List<String> englishIndirectObjectRelationTypes = new ArrayList<>(Arrays.asList("iobj", "case"));
	static SVO_VerbRelationType verbRelType = new SVO_VerbRelationType();


	public static IndexedWord getSubject(GrammaticalStructure gs){
		return SVO_Subject.assignSubject(gs);
	}
	public static String getSubjectConjunction(GrammaticalStructure gs){
		return SVO_Subject.subjectConjunction(gs);
	}





	public static IndexedWord getVerb(GrammaticalStructure gs){
		return SVO_Verb.assignVerb(gs);
	}


	public static String getVerbConjRelation(GrammaticalStructure gs){
		String verbConjRelation= verbRelType.conjRelation(gs);
		return verbConjRelation;
	}





	public static IndexedWord getObject(GrammaticalStructure gs){
		return SVO_Object.assignObject(gs);
	}



	public static IndexedWord getSecondObject(GrammaticalStructure gs){
		return SVO_Object.assignSecondObject(gs);
	}


	private static String getPassiveVoiceObject(GrammaticalStructure gs) {
		String passiveVoiceObj = SVO_Object.getPassiveVoiceObject(gs);
		return passiveVoiceObj;
	}	




	public static String getSubjPassiveVoice (GrammaticalStructure gs){
		String subj = SVO_Subject.passiveVoiceSubject(gs);
		return subj;
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


	public static EntityRelationTriple setEntityRelationTriple(String subjectURI, String objectURI, GrammaticalStructure gs){
		EntityRelationTriple t = new EntityRelationTriple();
		SVO_Verb verbClass = new SVO_Verb();
		String verbConjRelation = getVerbConjRelation(gs);

		System.out.println("2nd object = IOBJ ??: " + getSecondObject(gs));

		System.out.println();

		//check if the object is a dependent of the same verb 'isDirectDependentOfTheVerb'


		if (verbRelType.getCopula(gs).length()>1){
			//in case of 'cop', the object is recognized as the verb, and the verb is an object; here-> reversed			
			t = setTriple(getSubjectConjunction(gs), getObject(gs).toString(),  getVerb(gs).word(), subjectURI, objectURI );
			System.out.println("----- FINAL TRIPLE1 --- " + getSubjectConjunction(gs) + " verb: " + getObject(gs).toString() + " object: " + getVerb(gs).word());		
		}
		else {
			System.out.println("------");
			System.out.println("------");
			System.out.println(" position in the sentence: " + WordElement.getPositionWordInSentence(verbConjRelation, gs));

			if (WordElement.getPositionWordInSentence(verbConjRelation, gs) > WordElement.getPositionWordInSentence(getObject(gs).word(), gs)){
				t = setTriple(getSubjectConjunction(gs), getVerb(gs).word(),  getObject(gs).word(), subjectURI, objectURI );
				System.out.println("----- FINAL TRIPLE2.1 --- " + getSubjectConjunction(gs) + " object: " + getObject(gs).word() + "  verb: " + getVerb(gs).word());
			}
			else if (verbConjRelation.length()>1){
				t = setTriple(getSubjectConjunction(gs), getVerb(gs).word(),  "" , subjectURI, objectURI );
				System.out.println("----- FINAL TRIPLE2.2 --- " + getSubjectConjunction(gs) + " object: " + "empty" + "  verb: " + getVerb(gs).word());
			}
			else {
				t = setTriple(getSubjectConjunction(gs), getVerb(gs).word(),  getObject(gs).word(), subjectURI, objectURI );
				System.out.println("----- FINAL TRIPLE2.3 --- " + getSubjectConjunction(gs) + " object: " + getObject(gs).word() + "  verb: " + getVerb(gs).word());
			}
		}

		if (verbRelType.advclRelation(gs).length()>1){
			t = setTriple(getSubjectConjunction(gs), verbRelType.advclRelation(gs),  getSecondObject(gs).word(), subjectURI, objectURI);
			System.out.println("----- FINAL TRIPLE4 --- " + getSubjectConjunction(gs) + " verb: " + verbRelType.advclRelation(gs) + " object: " + getSecondObject(gs));
		}

		if (verbConjRelation.length()>1){
			t = setTriple(getSubjectConjunction(gs), verbConjRelation,  getSecondObject(gs).word(), subjectURI, objectURI);
			System.out.println("----- FINAL TRIPLE6 --- " + getSubjectConjunction(gs) + " verb: " + verbConjRelation + " object: " +
					getSecondObject(gs) + " objConjRel:" + SVO_Object.assignObjectConjRelation(gs));
		}

		String subjPass = getSubjPassiveVoice(gs);
		String objPass = getPassiveVoiceObject(gs);

		if (subjPass.length()>1 && objPass.length()>1){
			System.out.println("---" + " PASSIVE VOICE: " + subjPass + " v: " + getVerb(gs).word() + "objPass: " + objPass);

			t = setTriple(objPass, getVerb(gs).word(), subjPass, subjectURI, objectURI);
			System.out.println("----- FINAL TRIPLE8 --- " + objPass + " verb: " + getVerb(gs).word() + " object: " + subjPass);
		}


		String apposVerb = verbRelType .apposRelation(gs);
		System.out.println("apposition found: " + apposVerb);


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
