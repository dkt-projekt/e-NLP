package de.dkt.eservices.erattlesnakenlp.linguistic;

import java.util.ArrayList;
import java.util.Arrays;
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

	public ArrayList<TypedDependency> getDirectVerbDependencies(GrammaticalStructure gs, String verb){
		ArrayList<TypedDependency> verbDependenciesList = new ArrayList <TypedDependency>();
		System.out.println("verb__" + verb);
		for (TypedDependency td : gs.typedDependencies()) {
			//	System.out.println("TypedDependcy: " + td.gov() + " " + td.dep() + " " + td.reln() + " " + td.gov().word().equals(verb) + " " + td.gov().word() + " " + verb);
			System.out.println(td.gov());
			if (!td.gov().toString().equals("ROOT")){
				if (td.gov().word().equals(verb) ){
					if(!td.reln().toString().equals("conj") && !td.reln().toString().equals("advcl") && !td.reln().toString().equals("punct")&& !td.reln().toString().equals("cc") && !td.reln().toString().equals("aux") && !td.reln().toString().equals("auxpass")){
						System.out.println("inside the IF------>");
						System.out.println(td.gov().value() + " <gov&verb> "+ verb + td.reln().toString() + " " + td.reln().getShortName() + td.reln().getSpecific()+" " + td.toString());
						verbDependenciesList.add(td);			
					}
				}
			}
		}
		return verbDependenciesList;		
	}


	//	public ArrayList<TypedDependency> getDirectRootDependenciesList(GrammaticalStructure gs){
	//		ArrayList<TypedDependency> rootDependenciesList = new ArrayList <TypedDependency>();
	//		IndexedWord rootElement = assignVerb(gs);
	//		rootDependenciesList = getDirectVerbDependencies(gs,rootElement.word() );
	//
	//		return rootDependenciesList;		
	//	}

	public ArrayList<IndexedWord> getAllVerbs(GrammaticalStructure gs){
		ArrayList<IndexedWord> allVerbs = new ArrayList<IndexedWord>();
		ArrayList <String> verbPOStags = new ArrayList<String>( Arrays.asList( "VB", "VBD", "VBG", "VBN", "VBP", "VBZ"));

		for (TypedDependency td : gs.typedDependencies()) {
			if (verbPOStags.contains(td.gov().tag())){
				allVerbs.add(td.gov());
			}

		}
		return allVerbs;
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
