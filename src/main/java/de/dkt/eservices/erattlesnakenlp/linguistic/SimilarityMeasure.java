package de.dkt.eservices.erattlesnakenlp.linguistic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import java.util.List;
import com.google.common.collect.Lists;


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
	
	public Vector <Integer> countVerbOccurencesInDocuments (String verb, ArrayList <String> document){
		Vector <Integer> verbOccurencesCounter= new Vector <Integer> ();
		ArrayList<ArrayList<String>> sublists = (ArrayList<ArrayList<String>>) getSublists(document);
		Iterator<?> sublistIterator = sublists.iterator();
		
		while (sublistIterator.hasNext()){
			ArrayList <String> sublist = (ArrayList<String>) sublistIterator.next();

		}
		
		/*
		 * TODO: iterate all docs and count the given verb
		 */
		return verbOccurencesCounter;
	}
	
	public static List<?> getSublists (List<?> allDocumensList){
		 List<?> documentsSublists = Lists.partition(allDocumensList, 1000);	
		// System.out.println("partition length " + documentsSublists.size());
		 
		return documentsSublists;
	}
	
	
	
	
	
	

}
