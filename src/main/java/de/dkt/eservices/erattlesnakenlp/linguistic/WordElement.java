package de.dkt.eservices.erattlesnakenlp.linguistic;

import java.util.Collection;

import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.TypedDependency;

public class WordElement {

	public static TypedDependency getDirectPreceder(String word, GrammaticalStructure gs){	

		Collection<TypedDependency> td = gs.typedDependenciesCollapsed();
		TypedDependency typedDependency;
		Object[] list = td.toArray();
		TypedDependency wordPreceder = null;

		for (int i = 1; i < list.length; i++){
			typedDependency = (TypedDependency) list[i];
			if (typedDependency.dep().word().equals(word)) {
				wordPreceder =  (TypedDependency) list[i-1];
			}
		}	
		return wordPreceder;
	}


	public static  String  getWordByDependency(String dependencyType, GrammaticalStructure gs){
		String dependencyTypeWord = "";

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


	public static  String getPOStagByWord(String word, GrammaticalStructure gs){
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
	public static  boolean existPOStag(String postag, GrammaticalStructure gs){
		boolean posTagExists = false;

		Collection<TypedDependency> td = gs.typedDependenciesCollapsed();
		TypedDependency typedDependency;
		Object[] list = td.toArray();

		for (Object object : list) {
			typedDependency = (TypedDependency) object;

			if (typedDependency.dep().tag().equals(postag)) {
				posTagExists = true;
			}
		}
		return posTagExists;
	}

}
