package de.dkt.eservices.erattlesnakenlp.linguistic;

import java.util.Collection;

import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.TypedDependency;

public class SVO_Verb {
	static boolean isPassive = false;
	static boolean isConjRelation = false;
	
	public static IndexedWord assignVerb(GrammaticalStructure gs){
		return findRootDependency(gs).gov();	
	}	
	
	public static  TypedDependency findRootDependency(GrammaticalStructure gs){
		TypedDependency rootRelation = null;
		for (TypedDependency td : gs.typedDependencies()) {
			IndexedWordTuple t = new IndexedWordTuple();
			t.setFirst(td.dep());
			if (SVOTripleAssignment.englishSubjectRelationTypes.contains(td.reln().toString())){
				rootRelation = td;
				if (td.reln().toString().equals("nsubjpass")){
					isPassive = true;
			}
				break;
			}
		}
		return rootRelation;		
	}
	
	public static boolean isPassive(){
		
		return isPassive;
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

}
