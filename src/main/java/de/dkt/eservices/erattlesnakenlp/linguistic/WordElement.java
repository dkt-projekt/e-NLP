package de.dkt.eservices.erattlesnakenlp.linguistic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.TypedDependency;

public class WordElement {
	String dependentPOStag = null;
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

	public String getPOStagOfDependent(String word, GrammaticalStructure gs){

		Collection<TypedDependency> td = gs.typedDependenciesCollapsed();
		TypedDependency typedDependency;
		Object[] list = td.toArray();

		for (Object object : list) {
			typedDependency = (TypedDependency) object;


			//System.out.println("typedDependencies " + typedDependency.dep().word() + " " + typedDependency.dep().tag());

			if (typedDependency.gov().value().equals(word)) {
				dependentPOStag = typedDependency.dep().tag();
			}

		}
		return dependentPOStag;
	}

	

	public LinkedList <String> getPOStagsList (GrammaticalStructure gs){
		LinkedList <String> simplifiedPOSTagsList = new LinkedList<String>();


		Collection<TypedDependency> td = gs.typedDependenciesCollapsed();
		TypedDependency typedDependency;
		Object[] list = td.toArray();

		for (Object object : list) {
			typedDependency = (TypedDependency) object;


			//			System.out.println("typedDependencies " + typedDependency.dep().word() + " " + typedDependency.dep().tag());

			dependentPOStag = typedDependency.dep().tag();
			simplifiedPOSTagsList.add(dependentPOStag);
		}

		return simplifiedPOSTagsList;
	}

	public LinkedList <String> getSimplifiedPOSTagsList(GrammaticalStructure gs){
		System.out.println("simplified");
		LinkedList <String> posTagsList = getPOStagsList(gs);
		LinkedList <String> simplifiedPOSTagsList = new LinkedList <String>();

		ArrayList <String> simplifiedNPs = new ArrayList<>(Arrays.asList("NN", "NNS", "NNP", "NNPS", "PRP"));
		ArrayList <String> simplifiedVPs = new ArrayList<>(Arrays.asList("VB", "VBD", "VBG", "VBN", "VBP", "VBZ"));
		ArrayList <String> simplifiedPPs = new ArrayList<>(Arrays.asList("TO", "IN"));

		for (int i = 0; i < posTagsList.size(); i++){
			String currentPOStag = posTagsList.get(i);

			if (i>0 && simplifiedNPs.contains(currentPOStag)){
				if (!simplifiedPPs.contains(posTagsList.get(i-1))){
					simplifiedPOSTagsList.add("NP");
					System.out.println("NP added");
				}
			}
			else if (i==0 && simplifiedNPs.contains(currentPOStag)){
				simplifiedPOSTagsList.add("NP");
				System.out.println("NP added");

			}
			else if (simplifiedVPs.contains(currentPOStag)){
				simplifiedPOSTagsList.add("VP");
				System.out.println("VP added");

			}
			else if (simplifiedPPs.contains(currentPOStag)){
				simplifiedPOSTagsList.add("PP");
				System.out.println("PP added");
			}
		}
		return simplifiedPOSTagsList;
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

	public static int getPositionWordInSentence(String word, GrammaticalStructure gs){
		int positionInTheSentence = 0;


		Collection<TypedDependency> td = gs.typedDependenciesCollapsed();
		TypedDependency typedDependency;
		Object[] list = td.toArray();

		for (Object object : list) {
			typedDependency = (TypedDependency) object;
			IndexedWord dependent = typedDependency.dep();

			if (typedDependency.dep().word().equals(word)){
				positionInTheSentence = dependent.index();
			}
		}
		return positionInTheSentence;
	}

}
