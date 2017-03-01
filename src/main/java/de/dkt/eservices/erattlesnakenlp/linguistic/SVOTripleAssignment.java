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
	static List<String> englishObjectRelationTypes = new ArrayList<>(Arrays.asList("dobj", "cop", "nmod", "iobj", "advmod", "case")); 
	static List<String> englishIndirectObjectRelationTypes = new ArrayList<>(Arrays.asList("iobj", "case"));


	public static  TypedDependency findRootDependency(GrammaticalStructure gs){
		TypedDependency rootRelation = null;
		for (TypedDependency td : gs.typedDependencies()) {
			IndexedWordTuple t = new IndexedWordTuple();
			t.setFirst(td.dep());
			if (englishSubjectRelationTypes.contains(td.reln().toString())){
				rootRelation = td;
			}
		}
		return rootRelation;		
	}

	public static IndexedWord assignVerb(GrammaticalStructure gs){
		return findRootDependency(gs).gov();	
	}

	public static IndexedWord assignSubject(GrammaticalStructure gs){			 
		return findRootDependency(gs).dep();	
	}

	public IndexedWord assignSecondSubjOfConjRelation (Collection <TypedDependency> allDepenednciesList, GrammaticalStructure gs){
		IndexedWord subject = null;
		IndexedWord verb = getSecondVerbOfConjRelation(allDepenednciesList);
		if (!verb.equals(null)){
			subject = assignSubject(gs);
			/*
			 * TODO: 
			 */
		 }
		return subject;
	}
	
	// now do another loop to find object of the main verb/root
	// thing. This may also appear before the subject was
	// encountered, hence the need for two loops.
	public static IndexedWord assignObject(GrammaticalStructure gs){	
		IndexedWord object = null;
		TypedDependency relationType = getObjectRelationType(gs);

		if (englishObjectRelationTypes.contains(relationType.reln().toString()))
			object = relationType.dep();
		else if (englishIndirectObjectRelationTypes.contains(relationType.reln().toString()))
			object = relationType.gov();
		else 
			object = null;

		return object;
	}

	public static TypedDependency getObjectRelationType (GrammaticalStructure gs){
		TypedDependency objectRelationType = null;

		IndexedWord connectingElement = assignVerb(gs);
		if (!(connectingElement == null)){
			for (TypedDependency td : gs.typedDependencies()) {

				if (englishObjectRelationTypes.contains(td.reln().toString())) {
					if (td.gov().beginPosition() == connectingElement.beginPosition()
							&& td.gov().endPosition() == connectingElement.endPosition()) {
						objectRelationType = td;
					}
				}
				else if (englishIndirectObjectRelationTypes.contains(td.reln().toString())){
					objectRelationType = td;
				}
			}
		}

		return objectRelationType;
	}
	public IndexedWord getSecondVerbOfConjRelation (Collection <TypedDependency> allDepenednciesList ){
		IndexedWord connectingElement2 = null;

		Iterator<TypedDependency> dependenciesIterator = allDepenednciesList.iterator();
		while (dependenciesIterator.hasNext()){
			TypedDependency element = dependenciesIterator.next();

			if (element.reln().toString().equals("conj")){
				System.out.println(element + " " + element.gov().toString() + " " + element.dep().toString());
				connectingElement2 =  element.dep();
			}
		}
		return connectingElement2;
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
