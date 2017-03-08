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


	public static IndexedWord getSubject(GrammaticalStructure gs){
		return SVO_Subject.assignSubject(gs);
	}
	public static String getSubjectConjunction(GrammaticalStructure gs){
		return SVO_Subject.subjectConjunction(gs);
	}

	public static IndexedWord getVerb(GrammaticalStructure gs){
		return SVO_Verb.assignVerb(gs);
	}

	public static IndexedWord getObject(GrammaticalStructure gs){
		return SVO_Object.assignObject(gs);
	}

	public static IndexedWord getSecondObject(GrammaticalStructure gs){
		return SVO_Object.assignSecondObject(gs);
	}

	public static String getVerbConjRelation(GrammaticalStructure gs){
		String verbConjRelation= SVO_VerbRelationType.conjRelation(gs);
		return verbConjRelation;
	}

	public static String getSubjPassiveVoice (GrammaticalStructure gs){
		String subj = SVO_Subject.passiveVoiceSubject(gs);
		return subj;
	}
	private static String getPassiveVoiceObject(GrammaticalStructure gs) {
		String passiveVoiceObj = SVO_Object.getPassiveVoiceObject(gs);
		return passiveVoiceObj;
	}

	public static EntityRelationTriple setEntityRelationTriple(String subjectURI, String objectURI, GrammaticalStructure gs){
		EntityRelationTriple t = new EntityRelationTriple();

		
		if (SVO_VerbRelationType.getCopula(gs).length()>1){
			//in case of 'cop', the object is recognized as the verb, and the verb is an object; here-> reversed
			if (!(subjectURI == null) && !(objectURI == null)){
				t.setSubject(String.format("%s(%s)", getSubjectConjunction(gs), subjectURI));
				//	t.setSubject(String.format("%s", subjectThemRole.concat(subjectURI)));
				t.setRelation(getObject(gs).toString());
				//.concat(" lemma: ").concat(relationLemma));
				t.setObject(String.format("%s(%s)", getVerb(gs).word(), objectURI));
				//t.setObject(String.format("%s", objectThemRole.concat(objectURI)));
				System.out.println("----- FINAL TRIPLE1 --- " + getSubjectConjunction(gs) + " verb: " + getObject(gs).toString() + " object: " + getVerb(gs).word());

			}
			else {
				t.setSubject(String.format("%s(%s)", getSubjectConjunction(gs), subjectURI));
				t.setRelation(getObject(gs).toString());
				t.setObject(String.format("%s(%s)", getVerb(gs).word(), objectURI));
				System.out.println("----- FINAL TRIPLE2 --- " + getSubjectConjunction(gs) + " verb: " + getObject(gs).toString() + " object: " + getVerb(gs).word());
			}
		}
		else {
			if (!(subjectURI == null) && !(objectURI == null)){
				t.setSubject(String.format("%s(%s)", getSubjectConjunction(gs), subjectURI));
				//	t.setSubject(String.format("%s", subjectThemRole.concat(subjectURI)));
				t.setRelation(getVerb(gs).word());
				//.concat(" lemma: ").concat(relationLemma));
				t.setObject(String.format("%s(%s)",  getObject(gs), objectURI));
				//t.setObject(String.format("%s", objectThemRole.concat(objectURI)));
				System.out.println("----- FINAL TRIPLE1.1 --- " + getSubjectConjunction(gs) + " object: " + getObject(gs).toString() + " verb: " + getVerb(gs).word());

			}
			else {
				t.setSubject(String.format("%s(%s)", getSubjectConjunction(gs), subjectURI));
				t.setRelation( getVerb(gs).word() );
				t.setObject(String.format("%s(%s)", getObject(gs), objectURI));
				System.out.println("----- FINAL TRIPLE2.1 --- " + getSubjectConjunction(gs) + " object: " + getObject(gs).toString() + "  verb: " + getVerb(gs).word());
			}
		}

		


		if (SVO_VerbRelationType.advclRelation(gs).length()>1){

			if (!(subjectURI == null) && !(objectURI == null)){
				t.setSubject(String.format("%s(%s)", getSubjectConjunction(gs), subjectURI));
				//	t.setSubject(String.format("%s", subjectThemRole.concat(subjectURI)));
				t.setRelation(getVerb(gs).toString());
				//.concat(" lemma: ").concat(relationLemma));
				t.setObject(String.format("%s(%s)", getSecondObject(gs), objectURI));
				//t.setObject(String.format("%s", objectThemRole.concat(objectURI)));
				System.out.println("----- FINAL TRIPLE3 --- " + getSubjectConjunction(gs) + " verb: " + SVO_VerbRelationType.advclRelation(gs) + " object: " + getSecondObject(gs));

			}
			else {
				t.setSubject(String.format("%s(%s)", getSubjectConjunction(gs), " "));
				t.setRelation(SVO_VerbRelationType.advclRelation(gs));
				//.concat(" lemma: ").concat(relationLemma));
				t.setObject(String.format("%s(%s)", getSecondObject(gs), objectURI));
				//t.setObject(String.format("%s", objectThemRole.concat(objectURI)));
				System.out.println("----- FINAL TRIPLE4 --- " + getSubjectConjunction(gs) + " verb: " + SVO_VerbRelationType.advclRelation(gs) + " object: " + getSecondObject(gs));
			}
		}
		//	String verbConjRelation= SVO_VerbRelationType.conjRelation(gs);
		SVO_VerbRelationType verbRT = new SVO_VerbRelationType();
		String verbConjRelation = getVerbConjRelation(gs);

		if (verbConjRelation.length()>1){

			if (!(subjectURI == null) && !(objectURI == null)){
				t.setSubject(String.format("%s(%s)", getSubjectConjunction(gs), subjectURI));
				//	t.setSubject(String.format("%s", subjectThemRole.concat(subjectURI)));
				t.setRelation(verbConjRelation);
				//.concat(" lemma: ").concat(relationLemma));
				t.setObject(String.format("%s(%s)", getSecondObject(gs), objectURI));
				//t.setObject(String.format("%s", objectThemRole.concat(objectURI)));
				System.out.println("----- FINAL TRIPLE5 --- " + getSubjectConjunction(gs) + " verb: " + SVO_VerbRelationType.conjRelation(gs) + " object: " + getSecondObject(gs));
			}
			else {
				t.setSubject(String.format("%s(%s)", getSubjectConjunction(gs), " "));
				t.setRelation(verbConjRelation);
				//.concat(" lemma: ").concat(relationLemma));
				t.setObject(String.format("%s(%s)", getSecondObject(gs), objectURI));
				//t.setObject(String.format("%s", objectThemRole.concat(objectURI)));
				System.out.println("----- FINAL TRIPLE6 --- " + getSubjectConjunction(gs) + " verb: " + SVO_VerbRelationType.conjRelation(gs) + " object: " + getSecondObject(gs));
			}
		}

		String subjPass = getSubjPassiveVoice(gs);
		String objPass = getPassiveVoiceObject(gs);

		if (subjPass.length()>1 && objPass.length()>1){
			System.out.println("---" + " PASSIVE VOICE: " + subjPass + " v: " + getVerb(gs).word() + "objPass: " + objPass);

			if (!(subjectURI == null) && !(objectURI == null)){
				t.setSubject(String.format("%s(%s)", objPass, subjectURI));
				//	t.setSubject(String.format("%s", subjectThemRole.concat(subjectURI)));
				t.setRelation(getVerb(gs).word());
				//.concat(" lemma: ").concat(relationLemma));
				t.setObject(String.format("%s(%s)", subjPass, objectURI));
				//t.setObject(String.format("%s", objectThemRole.concat(objectURI)));
				System.out.println("----- FINAL TRIPLE7 --- " + objPass + " verb: " + getVerb(gs).word() + " object: " + subjPass);

			}
			else {
				t.setSubject(String.format("%s(%s)", objPass, " "));
				t.setRelation(getVerb(gs).word());
				//.concat(" lemma: ").concat(relationLemma));
				t.setObject(String.format("%s(%s)", subjPass, " " ));
				//t.setObject(String.format("%s", objectThemRole.concat(objectURI)));
				System.out.println("----- FINAL TRIPLE8 --- " + objPass + " verb: " + getVerb(gs).word() + " object: " + subjPass);
			}

		}


		return t;
	}


	public static EntityRelationTriple setEntityRelationTriple2(String subjectURI, String objectURI, GrammaticalStructure gs){
		EntityRelationTriple t = new EntityRelationTriple();

		if (!SVO_VerbRelationType.advclRelation(gs).equals(null)){

			if (!(subjectURI == null) && !(objectURI == null)){
				t.setSubject(String.format("%s(%s)", getSubjectConjunction(gs), subjectURI));
				//	t.setSubject(String.format("%s", subjectThemRole.concat(subjectURI)));
				t.setRelation(getVerb(gs).toString());
				//.concat(" lemma: ").concat(relationLemma));
				t.setObject(String.format("%s(%s)", getSecondObject(gs), objectURI));
				//t.setObject(String.format("%s", objectThemRole.concat(objectURI)));
				System.out.println("----- FINAL TRIPLE3 --- " + getSubjectConjunction(gs) + " verb: " + SVO_VerbRelationType.advclRelation(gs) + " object: " + getSecondObject(gs));

			}
			else {
				t.setSubject(String.format("%s(%s)", getSubjectConjunction(gs), " "));
				t.setRelation(SVO_VerbRelationType.advclRelation(gs));
				//.concat(" lemma: ").concat(relationLemma));
				t.setObject(String.format("%s(%s)", getSecondObject(gs), objectURI));
				//t.setObject(String.format("%s", objectThemRole.concat(objectURI)));
				System.out.println("----- FINAL TRIPLE4 --- " + getSubjectConjunction(gs) + " verb: " + SVO_VerbRelationType.advclRelation(gs) + " object: " + getSecondObject(gs));
			}
		}
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



}
