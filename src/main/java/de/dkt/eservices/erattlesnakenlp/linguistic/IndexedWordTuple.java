package de.dkt.eservices.erattlesnakenlp.linguistic;

import edu.stanford.nlp.ling.IndexedWord;

public class IndexedWordTuple {

	private IndexedWord first;
	private IndexedWord second;
	
	
	public IndexedWordTuple() {
	}

	public IndexedWordTuple(IndexedWord first, IndexedWord second) {
		super();
		this.first = first;
		this.second = second;
		
	}

	public IndexedWord getFirst() {
		return first;
	}

	public void setFirst(IndexedWord first) {
		this.first = first;
	}
	
	
	public IndexedWord getSecond() {
		return second;
	}

	public void setSecond(IndexedWord second) {
		this.second = second;
	}
	
}
