package de.dkt.eservices.erattlesnakenlp.linguistic;

import java.util.Vector;

public class SimilarityMeasure {
	
	
	//vector a, vector b; whereas a vector is a list for one verb with its occurrences in every document. (Sentences are postprocessed and sorted into documents which have the same length) 
	//vector a = (0, 3)
	//vector b = (4, 0)
	//dot product = 0*4 + 3*0 + ...
	
	public double calculateCosinusSimilarity (Vector <Integer> a, Vector <Integer>b){
		
		double cosinusSimilarity = 0;
		double dotProductResult = 0;
		double sumAsqrt = 0;
		double sumBsqrt = 0;
		
		for (int i=1; i< a.size(); i++){
			dotProductResult = dotProductResult + a.get(i)* b.get(i);
			sumAsqrt = sumAsqrt + Math.sqrt(a.get(i));
			sumBsqrt = sumBsqrt + Math.sqrt(b.get(i));			
		}
		return dotProductResult/(sumAsqrt * sumBsqrt);
	}
	
	
	
	
	
	

}
