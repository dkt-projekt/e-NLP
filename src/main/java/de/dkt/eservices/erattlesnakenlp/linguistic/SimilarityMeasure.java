package de.dkt.eservices.erattlesnakenlp.linguistic;

import java.util.Vector;

public class SimilarityMeasure {
	
	
	//vector a, vector b; whereas a vector is a list for one verb with its occurrences in every document. (Sentences are postprocessed and sorted into documents which have the same length) 
	//vector a = (0, 3)
	//vector b = (4, 0)
	//dot product = 0*4 + 3*0 + ...
	public float calculateDotProduct (Vector <Integer> a, Vector <Integer>b){
		
		float dotProductResult = 0;
		
		for (int i=1; i< a.size(); i++){
			dotProductResult = dotProductResult + a.get(i)* b.get(i);
		}
		return dotProductResult;
	}
	
	
	

}
