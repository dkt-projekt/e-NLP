package de.dkt.eservices.erattlesnakenlp.modules.mae;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by jan on 03.05.17.
 */
public class Trigger {

    String sentence;
    List<String> triggerWords = new ArrayList<String>();
    String doc;
	public String getSentence() {
		return sentence;
	}
	public void setSentence(String sentence) {
		this.sentence = sentence;
	}
	public List<String> getTriggerWords() {
		return triggerWords;
	}
	public void setTriggerWords(List<String> triggerWords) {
		this.triggerWords = triggerWords;
	}
	public String getDoc() {
		return doc;
	}
	public void setDoc(String doc) {
		this.doc = doc;
	}
    
    
}
