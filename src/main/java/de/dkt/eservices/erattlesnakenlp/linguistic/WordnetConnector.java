package de.dkt.eservices.erattlesnakenlp.linguistic;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import edu.mit.jverbnet.data.IMember;
import edu.mit.jverbnet.data.IThematicRole;
import edu.mit.jverbnet.data.IVerbClass;
import edu.mit.jverbnet.index.IVerbIndex;
import edu.mit.jverbnet.index.VerbIndex;

public class WordnetConnector {


	public LinkedList<String> getWordnetInformation(String inputVerb, String pathToVerbnet) throws IOException{

		LinkedList<String> wordnetTypes = new LinkedList();
		IVerbIndex index = VerbnetConnector.openVerbnetConnection(pathToVerbnet);
		index.open();
		Iterator<IVerbClass> verbnetIterator = index.iterator();

		
		while (verbnetIterator.hasNext()){
			List<IMember> verbnetEntry = verbnetIterator.next().getMembers();
			String wordnetType = null;

			for (int i = 0; i< verbnetEntry.size(); i++){
				if (verbnetEntry.get(i).getName().equals(inputVerb)){
					wordnetType = verbnetEntry.get(i).getWordnetTypes().toString();
					if (wordnetType.contains(",")){
						String[] subEntries = wordnetType.split(",");
						for (int j = 0; j < subEntries.length; j++){
							String element = subEntries[j];
							int wordnetTypeStart = element.indexOf("%");					
							int wordnetTypeStop = element.indexOf("=");
							element = element.substring(wordnetTypeStart +1, wordnetTypeStop+1);
							subEntries[j]=element;							
						}

						LinkedList<String> ll = new LinkedList<String>(Arrays.asList(subEntries));
						wordnetTypes.addAll(ll);
					}

					else{
						int wordnetTypeStart = wordnetType.indexOf("%");					
						int wordnetTypeStop = wordnetType.indexOf("=");
						wordnetType = wordnetType.substring(wordnetTypeStart +1, wordnetTypeStop +1);
						wordnetTypes.add(wordnetType);	
					}
				}
			}
		}
		return wordnetTypes;
	}

	public static boolean compare2VerbsSynsets (String verb1, String verb2,  String pathToVerbnet) throws IOException{
		boolean similarVerbs = false;
		WordnetConnector wordnetConn = new WordnetConnector ();
		LinkedList<String> list1 = wordnetConn.getWordnetInformation(verb1,  pathToVerbnet);
		LinkedList<String> list2 = wordnetConn.getWordnetInformation(verb2,  pathToVerbnet);
		Collection<LinkedList<String>> union = CollectionUtils.intersection(list1, list2);
		int unionSize =  union.size();
		
		if(!verb1.equals(verb2) && unionSize != 0) {  
			//System.out.println("VERB : " + verb1 + " " + verb2 + " " + union.toString());
			//similarVerbs = true;
		} 
		return similarVerbs;
	}



	public ListMultimap<String, ArrayList <LinkedList<String>>> compareWordnetEntries (String subject, String connectingElement, String object, LinkedList <String> wordnetInformationSet){

		ListMultimap<String, ArrayList <LinkedList<String>>> findWordnetsynsets = ArrayListMultimap.create();

		LinkedList <String> subjVobjRel = new LinkedList ();
		subjVobjRel.add(subject.toString());
		subjVobjRel.add(connectingElement.toString());
		subjVobjRel.add(object.toString());

		for (String wnListelement : wordnetInformationSet )
		{
			if (findWordnetsynsets.containsKey(wnListelement)){

				List<ArrayList<LinkedList<String>>> synonymeEntriesList = findWordnetsynsets.get(wnListelement);	

				ArrayList<LinkedList<String>> sublistEntries = new ArrayList();
				sublistEntries.add(subjVobjRel);
				synonymeEntriesList.add(sublistEntries);			
			}

			else {
				ArrayList<LinkedList<String>> sublistEntries = new ArrayList();
				sublistEntries.add(subjVobjRel);
				findWordnetsynsets.put(wnListelement, sublistEntries);
			}
		}
		return findWordnetsynsets;

	}
	

}
