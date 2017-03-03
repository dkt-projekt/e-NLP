package de.dkt.eservices.erattlesnakenlp.linguistic;

import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.TypedDependency;

public class SVO_Object {
	// now do another loop to find object of the main verb/root
	// thing. This may also appear before the subject was
	// encountered, hence the need for two loops.
	public static IndexedWord assignObject(GrammaticalStructure gs){	
		IndexedWord object = null;
		TypedDependency relationType = getObjectRelationType(gs);
		System.out.println("relationType: " + relationType);
		System.out.println("WordElement.getWordByDependency(\"advcl\", gs).length() " + WordElement.getWordByDependency("advcl", gs));

		
		//advcl relationType		
		if (WordElement.getWordByDependency("advcl", gs).length()>1){
			System.out.println("WordElement.getWordByDependency(\"advcl\", gs).length() " + WordElement.getWordByDependency("advcl", gs).length());
			object = relationType.gov();
		}
		else 
		
		//if the given clause contains a WDT (wh-clause, i.e. 'which') that may be falsely taken for a dobj
		if (SVOTripleAssignment.englishObjectRelationTypes.contains(relationType.reln().toString()) && !SVO_Verb.preVerbPosition(relationType.reln().toString(), gs) 
				&& WordElement.existPOStag("WDT", gs)){
			object = relationType.dep();	
			
			System.out.println("WDT " + WordElement.getDirectPreceder(object.word(), gs));
			object = WordElement.getDirectPreceder(object.word(), gs).dep();
			
			//	System.out.println("... object ... " + object);
		}
		else if (SVOTripleAssignment.englishObjectRelationTypes.contains(relationType.reln().toString()) && !SVO_Verb.preVerbPosition(relationType.reln().toString(), gs)){
			object = relationType.dep();	
			
			//	System.out.println("... object ... " + object);
		}
		else if (SVOTripleAssignment.englishIndirectObjectRelationTypes.contains(relationType.reln().toString()) && !SVO_Verb.preVerbPosition(relationType.reln().toString(), gs))
			object = relationType.dep();

		else if (relationType.reln().toString().equals("nmod") & !WordElement.getWordByDependency("nsubjpass", gs).isEmpty()){
			object = relationType.dep();
			System.out.println("nsubjpassRelFound: " + object);
		}

		else 
			object = null;

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

				if (SVOTripleAssignment.englishObjectRelationTypes.contains(td.reln().toString())  && !SVO_Verb.preVerbPosition(td.reln().toString(), gs)) {
					if (td.gov().beginPosition() == connectingElement.beginPosition()
							&& td.gov().endPosition() == connectingElement.endPosition()) {
						objectRelationType = td;
						//break;
					}
				}
				else if (SVOTripleAssignment.englishIndirectObjectRelationTypes.contains(td.reln().toString())){
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


}
