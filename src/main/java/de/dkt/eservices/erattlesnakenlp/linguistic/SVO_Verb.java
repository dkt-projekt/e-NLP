package de.dkt.eservices.erattlesnakenlp.linguistic;

import java.util.ArrayList;
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

	public ArrayList<TypedDependency> getDirectRootDependenciesList(GrammaticalStructure gs){
		ArrayList<TypedDependency> rootDependenciesList = new ArrayList <TypedDependency>();
		IndexedWord rootElement = assignVerb(gs);
		for (TypedDependency td : gs.typedDependencies()) {
			
			//look for all the dependencies, where the verb is a governor
			if (td.gov().equals(rootElement)){
				rootDependenciesList.add(td);			
				//System.out.println("-->gov: " + td.gov() + " dep " + td.dep() + " reln: " + td.reln());
			}
		}
		return rootDependenciesList;		
	}
	


	public static boolean isPassive(){

		return isPassive;
	}
	
//	public boolean isDirectDependentOfTheVerb(String dependent, String verb, GrammaticalStructure gs){
//		boolean isDirectDependent = false;
//		SVO_VerbRelationType verbRelTyp = new SVO_VerbRelationType();
//		for (TypedDependency td : gs.typedDependencies()) {
//			String governor = td.gov().word();
//			if (verb.equals(assignVerb(gs).word()) || verb.equals(verbRelTyp.conjRelation(gs)) ||
//					verb.equals(verbRelTyp.conjRelation(gs)))
//			System.out.println("governor: " + governor + " dependent " + dependent);
//			//look for all the dependencies, where the verb is a governor
//			if (!verb.equals(null) && governor.equals(verb)){
//				if (td.dep().word().equals(dependent));
//				System.out.println("TRUE: is direct verb dep:" + td.gov() + " dep " + td.dep() + " reln: " + td.reln());
//				isDirectDependent = true;
//			}
//		}
//		
//		
//		return isDirectDependent;
//		
//	}

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
