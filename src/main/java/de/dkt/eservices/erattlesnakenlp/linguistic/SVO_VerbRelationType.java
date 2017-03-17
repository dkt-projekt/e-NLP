package de.dkt.eservices.erattlesnakenlp.linguistic;

import java.util.ArrayList;
import java.util.Arrays;

import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.TypedDependency;

public class SVO_VerbRelationType {

	public IndexedWord conjRelation (GrammaticalStructure gs){
		String verbConjRelation = "";
		IndexedWord verbConj = null;
		String secondVerbOfConjRelation = WordElement.getWordByDependency("conj:and", gs);
		boolean isInPreVerbPosition = SVO_Verb.preVerbPosition("conj:and", gs);

		if (!isInPreVerbPosition){
			if (secondVerbOfConjRelation != null){
				//	System.out.println("--- VERB conj relation found --- " + getWordByDependency("conj:and", gs));
				ArrayList <String> verbPOStags = new ArrayList<String>( Arrays.asList( "VB", "VBD", "VBG", "VBN", "VBP", "VBZ"));
				String secondsVerbPosTag = WordElement.getPOStagByWord(secondVerbOfConjRelation, gs);

				if (verbPOStags.contains(secondsVerbPosTag)){
					verbConjRelation = secondVerbOfConjRelation;

					for (TypedDependency td : gs.typedDependencies()) {
						IndexedWord dependent = td.dep();
						if (dependent.word().equals(verbConjRelation)){
							verbConj = dependent;
						}
					}
				}
			}
		}
		return verbConj;
	}



	public IndexedWord advclRelation (GrammaticalStructure gs){
		String verbAdvclRelation = "";
		IndexedWord verbAdvCl = null;
		String secondVerbOfAdvclRelation = WordElement.getWordByDependency("advcl", gs);

		if (secondVerbOfAdvclRelation != null){
			//	System.out.println("--- VERB advcl relation found --- " + getWordByDependency("advcl", gs));
			ArrayList <String> verbPOStags = new ArrayList<String>( Arrays.asList( "VB", "VBD", "VBG", "VBN", "VBP", "VBZ"));
			String secondsVerbPosTag = WordElement.getPOStagByWord(secondVerbOfAdvclRelation, gs);

			if (verbPOStags.contains(secondsVerbPosTag)){
				verbAdvclRelation = secondVerbOfAdvclRelation;
				for (TypedDependency td : gs.typedDependencies()) {
					IndexedWord dependent = td.dep();
					if (dependent.word().equals(verbAdvclRelation)){
						verbAdvCl = dependent;
					}
				}



			}
		}
		return verbAdvCl;
	}

	public String getCopula(GrammaticalStructure gs){
		String copula = "";		

		if (!WordElement.getWordByDependency("cop", gs).equals(null)){
			copula = WordElement.getWordByDependency("cop", gs);
		}
		return copula;
	}

	public String apposRelation (GrammaticalStructure gs){
		String apposRelationVerb = "";
		String secondVerbOfAdvclRelation = WordElement.getWordByDependency("acl:relcl", gs);

		if (secondVerbOfAdvclRelation != null){
			//	System.out.println("--- VERB advcl relation found --- " + getWordByDependency("advcl", gs));
			ArrayList <String> verbPOStags = new ArrayList<String>( Arrays.asList( "VB", "VBD", "VBG", "VBN", "VBP", "VBZ"));
			String secondsVerbPosTag = WordElement.getPOStagByWord(secondVerbOfAdvclRelation, gs);

			if (verbPOStags.contains(secondsVerbPosTag)){
				apposRelationVerb = secondVerbOfAdvclRelation;
			}
		}
		return apposRelationVerb;
	}


}
