package de.dkt.eservices.erattlesnakenlp.linguistic;

import java.util.Collection;

import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.TypedDependency;

public class SVO_Subject {

	public static IndexedWord assignSubject(GrammaticalStructure gs){
		IndexedWord subject = SVO_Verb.findRootDependency(gs).dep();
		//System.out.println("getWordByDependency " + WordElement.getWordByDependency("nsubj",gs) + " ROOT:" + SVO_Verb.findRootDependency(gs).dep() + " getSecondSubj: " + getSecondSubject(gs));
		return subject;	
	}


	public static String subjectConjunction(GrammaticalStructure gs){	
		String subject = assignSubject(gs).word();

		if (!SVO_VerbRelationType.conjRelation(gs).equals(null)){
			if (SVO_Verb.preVerbPosition("conj:and", gs)){
				String subjectConjunction = WordElement.getWordByDependency("conj:and", gs);
				subject = subject.concat(" and " + subjectConjunction);	
			}
		}
		return subject;
	}


	public static String getSecondSubject(GrammaticalStructure gs){
		String secondSubject = null;
		Collection<TypedDependency> td = gs.typedDependenciesCollapsed();
		TypedDependency typedDependency;
		Object[] list = td.toArray();

		for (Object object : list) {
			typedDependency = (TypedDependency) object;
			if (SVOTripleAssignment.englishSubjectRelationTypes.contains(typedDependency.reln().toString())) {
				secondSubject = typedDependency.dep().word();
			}
		}
		return secondSubject;
	}

	public static TypedDependency getSecondObjectRelationType (GrammaticalStructure gs){
		TypedDependency objectRelationType = null;

		IndexedWord connectingElement = SVO_Verb.assignVerb(gs);
		if (!(connectingElement == null)){
			for (TypedDependency td : gs.typedDependencies()) {

				if (SVOTripleAssignment.englishObjectRelationTypes.contains(td.reln().toString())) {
					if (td.gov().beginPosition() == connectingElement.beginPosition()
							&& td.gov().endPosition() == connectingElement.endPosition()) {
						objectRelationType = td;
					}
				}
				else if (SVOTripleAssignment.englishIndirectObjectRelationTypes.contains(td.reln().toString())){
					objectRelationType = td;
				}
			}
		}
		return objectRelationType;
	}
}
