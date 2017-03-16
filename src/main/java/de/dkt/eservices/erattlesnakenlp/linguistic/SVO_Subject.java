package de.dkt.eservices.erattlesnakenlp.linguistic;

import java.util.Collection;

import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.TypedDependency;

public class SVO_Subject {



	public static IndexedWord assignSubject(GrammaticalStructure gs){
		IndexedWord subject = null;
		TypedDependency directRootDependency = SVO_Verb.findRootDependency(gs);
	//	System.out.println("directRootDependency: " + directRootDependency);
	//	System.out.println("assignSubjTest " + directRootDependency.reln().toString());

		if (SVOTripleAssignment.englishSubjectRelationTypes.contains(directRootDependency.reln().toString())){
			subject = directRootDependency.dep();

//			if ( !SVO_Verb.preVerbPosition(directRootDependency.reln().toString(), gs) 
//					&& WordElement.existPOStag("WDT", gs)){				
//
////				System.out.println("WDT " + WordElement.getDirectPreceder(subject.word(), gs).dep());	
//			}
		}		
		return subject;	
	}

	public static String getSubjectDependencyType (GrammaticalStructure gs){
		String subjectDependencyType= SVO_Verb.findRootDependency(gs).reln().toString();
		return subjectDependencyType;
	}


	public static String subjectConjunction(GrammaticalStructure gs){	
		String subject = assignSubject(gs).word();
		SVO_VerbRelationType verbRelType = new SVO_VerbRelationType();

		if (!verbRelType.conjRelation(gs).equals(null)){
			if (SVO_Verb.preVerbPosition("conj:and", gs)){
				String subjectConjunction = WordElement.getWordByDependency("conj:and", gs);
				subject = subject.concat(" and " + subjectConjunction);	
			}
		}
		if (!getCompound(gs).equals("") &&  SVO_Verb.preVerbPosition("compound",gs)){
			//System.out.println("YES! preverb position");
			String subjectCompound = getCompound(gs);
			subjectCompound = subjectCompound.concat(" " + subject);
			subject = subjectCompound;
		}
		//System.out.println("subjPosition: " + WordElement.getPositionWordInSentence(subject, gs));
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

	public static String passiveVoiceSubject (GrammaticalStructure gs){
		String passiveVoice = null;

		if (SVO_Subject.getSubjectDependencyType(gs).equals("nsubjpass")){
			passiveVoice = SVO_Subject.assignSubject(gs).word();
		}
		else passiveVoice = "";
		return passiveVoice;
	}

	public static String getCompound(GrammaticalStructure gs){
		String compoundSubject = "";

		if (!WordElement.getWordByDependency("compound", gs).equals("")){
			compoundSubject = WordElement.getWordByDependency("compound", gs);	

		}

		return compoundSubject;

	}

	//	public static String getAppositionSubject(GrammaticalStrucutre gs){
	//		if (!SVO_VerbRelationType.apposRelation(gs).equals(null)){
	//
	//		}
	//	}
}
