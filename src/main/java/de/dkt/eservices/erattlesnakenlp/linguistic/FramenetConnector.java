package de.dkt.eservices.erattlesnakenlp.linguistic;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import edu.mit.jverbnet.data.IFrame;
import edu.mit.jverbnet.data.IMember;
import edu.mit.jverbnet.data.IVerbClass;
import edu.mit.jverbnet.data.syntax.ISyntaxArgDesc;
import edu.mit.jverbnet.index.IVerbIndex;
import edu.mit.jverbnet.index.VerbIndex;

public class FramenetConnector {


	public static List<IFrame> getVerbnetFrames(String inputVerb, String pathToVerbnet) throws IOException{
		List<IFrame> frameList = new ArrayList<IFrame>();

		IVerbIndex index = VerbnetConnector.openVerbnetConnection(pathToVerbnet);
		index.open();

		Iterator<IVerbClass> verbnetIterator = index.iterator();

		while (verbnetIterator.hasNext()){
			List<IMember> verbnetEntry = verbnetIterator.next().getMembers();

			for (int i = 0; i< verbnetEntry.size(); i++){
				if (verbnetEntry.get(i).getName().equals(inputVerb) && verbnetEntry.get(i).getGroupings() != null){
					for (int j = 0; j <  verbnetEntry.get(i).getVerbClass().getFrames().size(); j++){
						frameList.add(verbnetEntry.get(i).getVerbClass().getFrames().get(j));
					}
				}
			}
		}

		return frameList;
	}

	public  ListMultimap<Integer, LinkedList<HashMap<String, String>>> getSortedFramesWithArguments(String inputVerb, String pathToVerbnet) throws IOException{

		//ListMultiMap <AmountOfPostVerbElements (this counter may have multivalues => ListMultiMap), ListOfPair<S/O/..., Role>>
		ListMultimap <Integer,LinkedList<HashMap<String,String>>> sortedFramesbyLength = ArrayListMultimap.create();
		List<IFrame> verbnetFramesList = getVerbnetFrames(inputVerb, pathToVerbnet);
		
		System.out.println("length of the verbnet frames' list: " + verbnetFramesList.size());
		Iterator<IFrame> verbnetThemRolesIterator = verbnetFramesList.iterator();
		HashMap<String,String> frameEntry = new HashMap <String, String>();

		while (verbnetThemRolesIterator.hasNext()){
			frameEntry = new HashMap <String, String>();

			LinkedList <HashMap<String, String>> frameLinkedList = new LinkedList <HashMap<String, String>>();
			IFrame nextElement = verbnetThemRolesIterator.next();

			//put subject to frameEntry & put the subject to the LinkedList
			frameEntry.put(nextElement.getSyntax().getPreVerbDescriptors().get(0).getNounPhraseType() == null ? "none" : nextElement.getSyntax().getPreVerbDescriptors().get(0).getNounPhraseType().toString(), nextElement.getSyntax().getPreVerbDescriptors().get(0).getType().toString());
			frameLinkedList.add(frameEntry);

			if (nextElement.getSyntax().getPostVerbDescriptors().size() != 0){
				Iterator<ISyntaxArgDesc> postVerbElemIterator = nextElement.getSyntax().getPostVerbDescriptors().iterator();

				while (postVerbElemIterator.hasNext()){
					frameEntry = new HashMap <String, String>();
					ISyntaxArgDesc postVerbNextElement = postVerbElemIterator.next();

					frameEntry.put((postVerbNextElement.getNounPhraseType() == null ? "none" : postVerbNextElement.getNounPhraseType().toString()), postVerbNextElement.getType().toString());
					frameLinkedList.add(frameEntry);
				}
			}
			
			//System.out.println(nextElement.getSyntax().getPostVerbDescriptors().size() + " frameLinked" + frameLinkedList);
			sortedFramesbyLength.put(nextElement.getSyntax().getPostVerbDescriptors().size(), frameLinkedList);

		}

		return sortedFramesbyLength;
	}

}
