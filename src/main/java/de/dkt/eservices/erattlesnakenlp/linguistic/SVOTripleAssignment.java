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
				break;
			}
		}
		return rootRelation;		
	}

	public static IndexedWord assignVerb(GrammaticalStructure gs){
		return findRootDependency(gs).gov();	
	}

	public static IndexedWord assignSubject(GrammaticalStructure gs){
		IndexedWord subject = findRootDependency(gs).dep();
		System.out.println("get word by dependency " + getWordByDependency("nsubj",gs) + " " + findRootDependency(gs).dep() + " get second subj " + getSecondSubject(gs));
		return subject;	
	}

	public static String subjectConjunction(GrammaticalStructure gs){	
		String subject = assignSubject(gs).word();
		String subjectConjunction = getWordByDependency("conj:and", gs);

		if (!subjectConjunction.equals(null) & preVerbPosition("conj:and", gs))
			subject = subject.concat(" and " + subjectConjunction);	
		return subject;
	}


	public static String  getWordByDependency(String dependencyType, GrammaticalStructure gs){
		//	boolean dependencyTypeExists = false;
		String dependencyTypeWord = null;

		Collection<TypedDependency> td = gs.typedDependenciesCollapsed();
		TypedDependency typedDependency;
		Object[] list = td.toArray();

		for (Object object : list) {
			typedDependency = (TypedDependency) object;
			if (typedDependency.reln().toString().equals(dependencyType)) {
				dependencyTypeWord = typedDependency.dep().word();
			}
		}
		return dependencyTypeWord;
	}

	public static boolean preVerbPosition(String dependencyType, GrammaticalStructure gs){
		boolean preVerbPostition = false;
		Collection<TypedDependency> td = gs.typedDependenciesCollapsed();
		TypedDependency typedDependency;
		TypedDependency nextElement;
		Object[] list = td.toArray();

		for (int i = 0; i < list.length; i++){
			typedDependency = (TypedDependency) list[i];
			//System.out.println("# " +dependencyType + " typedDependency.reln().toString() " + typedDependency.reln().toString());

			if (typedDependency.reln().toString().equals(dependencyType)) {

				nextElement = (TypedDependency) list[i+1];
				//System.out.println("next element " + nextElement + " previous " + typedDependency);
				if (nextElement.reln().toString().equals("root")){

					preVerbPostition = true;
				}
			}
		}
		return preVerbPostition;
	}

	public static String getPOStagByWord(String word, GrammaticalStructure gs){
		String posTag = null;

		Collection<TypedDependency> td = gs.typedDependenciesCollapsed();
		TypedDependency typedDependency;
		Object[] list = td.toArray();

		for (Object object : list) {
			typedDependency = (TypedDependency) object;

			if (typedDependency.dep().word().equals(word)) {
				posTag = typedDependency.dep().tag();
			}
		}
		return posTag;
	}

	public static String  getSecondSubject(GrammaticalStructure gs){
		//	boolean dependencyTypeExists = false;
		String secondSubject = null;

		Collection<TypedDependency> td = gs.typedDependenciesCollapsed();
		TypedDependency typedDependency;
		Object[] list = td.toArray();

		for (Object object : list) {
			typedDependency = (TypedDependency) object;

			if (englishSubjectRelationTypes.contains(typedDependency.reln().toString())) {
				secondSubject = typedDependency.dep().word();
			}
		}
		return secondSubject;
	}

	public static String conjRelation (GrammaticalStructure gs){
		String verbConjRelation = null;
		String secondVerbOfConjRelation = getWordByDependency("conj:and", gs);
		boolean isInPreVerbPosition = preVerbPosition("conj:and", gs);

		if (!isInPreVerbPosition){
			if (secondVerbOfConjRelation != null){
				//	System.out.println("--- VERB conj relation found --- " + getWordByDependency("conj:and", gs));
				ArrayList <String> verbPOStags = new ArrayList<String>( Arrays.asList( "VB", "VBD", "VBG", "VBN", "VBP", "VBZ"));
				String secondsVerbPosTag = getPOStagByWord(secondVerbOfConjRelation, gs);

				if (verbPOStags.contains(secondsVerbPosTag)){
					verbConjRelation = secondVerbOfConjRelation;
				}
			}
		}
		return verbConjRelation;
	}

	// now do another loop to find object of the main verb/root
	// thing. This may also appear before the subject was
	// encountered, hence the need for two loops.
	public static IndexedWord assignObject(GrammaticalStructure gs){	
		IndexedWord object = null;
		TypedDependency relationType = getObjectRelationType(gs);


		if (englishObjectRelationTypes.contains(relationType.reln().toString())){
		//	System.out.println("---- preverb position " + !preVerbPosition(relationType.reln().toString(), gs));
			object = relationType.gov();	
		//	System.out.println("... object ... " + object);
		}
		else if (englishIndirectObjectRelationTypes.contains(relationType.reln().toString()) && !preVerbPosition(relationType.reln().toString(), gs))
			object = relationType.gov();
		else 
			object = null;

		return object;
	}

	public static IndexedWord assignSecondObject(GrammaticalStructure gs){	
		IndexedWord object = null;
		TypedDependency relationType = getSecondObjectRelationType(gs);

		if (englishObjectRelationTypes.contains(relationType.reln().toString())){
			object = relationType.dep();			
		}
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

				if (englishObjectRelationTypes.contains(td.reln().toString())  && !preVerbPosition(td.reln().toString(), gs)) {
					if (td.gov().beginPosition() == connectingElement.beginPosition()
							&& td.gov().endPosition() == connectingElement.endPosition()) {
						objectRelationType = td;
						//break;
					}
				}
				else if (englishIndirectObjectRelationTypes.contains(td.reln().toString())){
					objectRelationType = td;
				}
			}
		}

		return objectRelationType;
	}

	public static TypedDependency getSecondObjectRelationType (GrammaticalStructure gs){
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
	public static TypedDependency getDirectPreceder(String word, GrammaticalStructure gs){	

		Collection<TypedDependency> td = gs.typedDependenciesCollapsed();
		TypedDependency typedDependency;
		Object[] list = td.toArray();
		TypedDependency wordPreceder = null;

		for (int i = 0; i < list.length; i++){
			typedDependency = (TypedDependency) list[i];
			if (typedDependency.dep().word().equals(word)) {
				wordPreceder =  (TypedDependency) list[i-1];
			}
		}	
		return wordPreceder;
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
