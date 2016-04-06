package de.dkt.eservices.eopennlp.modules;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class NLPCommons {

	
	public static String getSentenceTense(String sentence, String language){
		
		String posModelName = "pos-perceptron.bin"; // just randomly choosing perceptron here, we may just as well take the maxent one...
		
		String tokenModel = language+ "-token.bin";
		String[] tokens = Tokenizer.tokenizeInput(sentence, tokenModel);
		String[] tags = PartOfSpeechTagging.tagTokens(tokens, posModelName, language);
		
		//System.out.println("DEBUGGING tokens:" + Arrays.toString(tokens));
		//System.out.println("DEBUGGING ags   :" + Arrays.toString(tags));
		ArrayList<String> verbs = new ArrayList<String>();
		
		if (language.equals("de")){
			for (String tag : tags){
				if (tag.startsWith("V")){
					verbs.add(tag);
				}
			}
			
			//System.out.println("DEBUGGING verbs:" + verbs);
			
		}
		
		return "aapje";
	}
	
	
	public static void main(String[] args){
		//getSentenceTense("Seine nationalistischen Kräfte errangen mit der Eroberung Toledos am 27. September und der Beendigung der Belagerung des Alcázars von Toledo einen weiteren wichtigen propagandistischen Sieg. ", "de");
		getSentenceTense("im nächsten Jahr werden wir fertig sein", "de");
	}
}
