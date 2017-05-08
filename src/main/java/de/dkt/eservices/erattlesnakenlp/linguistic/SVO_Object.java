package de.dkt.eservices.erattlesnakenlp.linguistic;

import java.util.ArrayList;

import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.TypedDependency;

public class SVO_Object {

	public static IndexedWord getObject(GrammaticalStructure gs, TypedDependency relationType){

		IndexedWord object = null;


		//advcl relationType		
		if (WordElement.getWordByDependency("advcl", gs).length() > 1){
			object = relationType.dep();
		}
		else 
			//if the given clause contains a WDT (wh-clause, i.e. 'which') that may be falsely taken for a dobj
			if (SVOTripleAssignment.englishObjectRelationTypes.contains(relationType.reln().toString()) 
					&& !SVO_Verb.preVerbPosition(relationType.reln().toString(), gs) 
					&& WordElement.existPOStag("WDT", gs)){
				object = relationType.dep();	

				//	System.out.println("WDT " + WordElement.getDirectPreceder(object.word(), gs).dep() + " " + WordElement.getDirectPreceder(object.word(), gs).gov());

				if (object.equals(SVO_Subject.assignSubject(gs))){
					object = null;

				}
				object.setWord(WordElement.getDirectPreceder(object.word(), gs).dep().word().concat(" " + WordElement.getDirectPreceder(object.word(), gs).gov().word()));
			}
			else if (SVOTripleAssignment.englishObjectRelationTypes.contains(relationType.reln().toString()) && !SVO_Verb.preVerbPosition(relationType.reln().toString(), gs)){
				if (!relationType.dep().tag().equals("IN") && !relationType.dep().tag().equals("TO"))
					object = relationType.dep();			
				else 
					object = relationType.gov();
			}
			else if (SVOTripleAssignment.englishIndirectObjectRelationTypes.contains(relationType.reln().toString()) && !SVO_Verb.preVerbPosition(relationType.reln().toString(), gs))
				if (!relationType.dep().tag().equals("IN") && !relationType.dep().tag().equals("TO"))
					object = relationType.dep();
				else 
					object = relationType.gov();
			else if (relationType.reln().toString().equals("nmod") & !WordElement.getWordByDependency("nsubjpass", gs).isEmpty()){
				object = relationType.dep();
			}
			else
				object = null;
		return object;
	}


	// now do another loop to find object of the main verb/root
	// thing. This may also appear before the subject was
	// encountered, hence the need for two loops.
	public static IndexedWord assignObject(GrammaticalStructure gs,  TypedDependency relationType){	
		IndexedWord object = getObject(gs, relationType);
		return object;
	}

	public static  IndexedWord assignObjectConjRelation(GrammaticalStructure gs, TypedDependency relationType){
		IndexedWord object = getObject(gs, relationType);
		SVO_VerbRelationType verRelType = new SVO_VerbRelationType();
		//an extra 'if' to exclude all the objects of the sentence that are dependents of the first verb (in case of a i.a.: conj:relation)
		if (verRelType.conjRelation(gs).word().length()>1 && 
				WordElement.getPositionWordInSentence(object.word(), gs) < WordElement.getPositionWordInSentence(verRelType.conjRelation(gs).word(), gs)){
			//	System.out.println("objPosition: " + WordElement.getPositionWordInSentence(object.word(), gs));

			object = null;
		}

		return object;

	}

	public static IndexedWord assignSecondObject(GrammaticalStructure gs){	
		IndexedWord object = null;
		TypedDependency relationType = getSecondObjectRelationType(gs);

		if (SVOTripleAssignment.englishObjectRelationTypes.contains(relationType.reln().toString())){
			object = relationType.dep();			
		}
		else if (SVOTripleAssignment.englishIndirectObjectRelationTypes.contains(relationType.reln().toString()))
			object = relationType.gov();
		else 
			object = null;

		return object;
	}

	public static TypedDependency getObjectRelationType (GrammaticalStructure gs){
		TypedDependency objectRelationType = null;

		IndexedWord connectingElement = SVO_Verb.assignVerb(gs);
		if (!(connectingElement == null)){
			for (TypedDependency td : gs.typedDependencies()) {

				if (SVOTripleAssignment.englishObjectRelationTypes.contains(td.reln().toString())  &&  WordElement.getPositionWordInSentence(td.dep().word(), gs)> WordElement.getPositionWordInSentence(SVO_Subject.assignSubject(gs).word(), gs)) {
					if (td.gov().beginPosition() == connectingElement.beginPosition()
							&& td.gov().endPosition() == connectingElement.endPosition()) {
//						System.out.println("objPosition: " + WordElement.getPositionWordInSentence(td.dep().word(), gs) + " subj: " +  WordElement.getPositionWordInSentence(SVO_Subject.assignSubject(gs).word(), gs));

						objectRelationType = td;
						//TODO!
						break;
					}
				}
				else if (SVOTripleAssignment.englishIndirectObjectRelationTypes.contains(td.reln().toString()) &&  WordElement.getPositionWordInSentence(td.dep().word(), gs)> WordElement.getPositionWordInSentence(SVO_Subject.assignSubject(gs).word(), gs)){
					objectRelationType = td;
				}
			}
		}
		return objectRelationType;
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

	//some sentences do have more than 2 objects (i.e. 'An event took place on Thursday from 8 pm till midnight in Berlin', 
	//ignoring all the other arguments would result in a simple extraction of the triple <event, took, place> or <event, took place, Thursday>
	public static ArrayList<TypedDependency> getIndirectObjectList(GrammaticalStructure gs){
		ArrayList <TypedDependency> indirectObjectsList = new ArrayList();

		IndexedWord connectingElement = SVO_Verb.assignVerb(gs);
		if (!(connectingElement == null)){
			for (TypedDependency td : gs.typedDependencies()) {

				if (SVOTripleAssignment.englishObjectRelationTypes.contains(td.reln().toString()) &&  WordElement.getPositionWordInSentence(td.dep().word(), gs)> WordElement.getPositionWordInSentence(SVO_Subject.assignSubject(gs).word(), gs))  {
					if (td.gov().beginPosition() == connectingElement.beginPosition()
							&& td.gov().endPosition() == connectingElement.endPosition()) {
						indirectObjectsList.add(td);
					}
				}
				else if (SVOTripleAssignment.englishIndirectObjectRelationTypes.contains(td.reln().toString())){
					indirectObjectsList.add(td);
				}
			}
		}
		return indirectObjectsList;
	}

	public static String getPassiveVoiceObject(GrammaticalStructure gs){
		String passiveVoiceObject = "";
		if (!SVO_Subject.passiveVoiceSubject(gs).equals("")){
//			System.out.println("word by dependency " + WordElement.getWordByDependency("nmod:agent",gs));
			passiveVoiceObject = WordElement.getWordByDependency("nmod:agent",gs);
//			System.out.println(" obj Pass : " + passiveVoiceObject);
		}
		return passiveVoiceObject;
	}

}
