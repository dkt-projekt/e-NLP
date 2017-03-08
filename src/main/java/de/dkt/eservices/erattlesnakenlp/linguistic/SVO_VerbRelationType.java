package de.dkt.eservices.erattlesnakenlp.linguistic;

import java.util.ArrayList;
import java.util.Arrays;

import edu.stanford.nlp.trees.GrammaticalStructure;

public class SVO_VerbRelationType {

	public static String conjRelation (GrammaticalStructure gs){
		String verbConjRelation = "";
		String secondVerbOfConjRelation = WordElement.getWordByDependency("conj:and", gs);
		boolean isInPreVerbPosition = SVO_Verb.preVerbPosition("conj:and", gs);

		if (!isInPreVerbPosition){
			if (secondVerbOfConjRelation != null){
				//	System.out.println("--- VERB conj relation found --- " + getWordByDependency("conj:and", gs));
				ArrayList <String> verbPOStags = new ArrayList<String>( Arrays.asList( "VB", "VBD", "VBG", "VBN", "VBP", "VBZ"));
				String secondsVerbPosTag = WordElement.getPOStagByWord(secondVerbOfConjRelation, gs);

				if (verbPOStags.contains(secondsVerbPosTag)){
					verbConjRelation = secondVerbOfConjRelation;
				}
			}
		}
		return verbConjRelation;
	}

	public static String advclRelation (GrammaticalStructure gs){
		String verbAdvclRelation = "";
		String secondVerbOfAdvclRelation = WordElement.getWordByDependency("advcl", gs);

		if (secondVerbOfAdvclRelation != null){
			//	System.out.println("--- VERB advcl relation found --- " + getWordByDependency("advcl", gs));
			ArrayList <String> verbPOStags = new ArrayList<String>( Arrays.asList( "VB", "VBD", "VBG", "VBN", "VBP", "VBZ"));
			String secondsVerbPosTag = WordElement.getPOStagByWord(secondVerbOfAdvclRelation, gs);

			if (verbPOStags.contains(secondsVerbPosTag)){
				verbAdvclRelation = secondVerbOfAdvclRelation;
			}
		}
		return verbAdvclRelation;
	}

	public static String getCopula(GrammaticalStructure gs){
		String copula = "";		
		System.out.println("getCopula: " + WordElement.getWordByDependency("cop", gs));

		if (!WordElement.getWordByDependency("cop", gs).equals(null)){
			copula = WordElement.getWordByDependency("cop", gs);
		}
		return copula;
	}
	
	
}
