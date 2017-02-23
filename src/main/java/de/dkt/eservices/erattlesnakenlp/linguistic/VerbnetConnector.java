package de.dkt.eservices.erattlesnakenlp.linguistic;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import edu.mit.jverbnet.index.IVerbIndex;
import edu.mit.jverbnet.index.VerbIndex;
import edu.stanford.nlp.ling.IndexedWord;

public class VerbnetConnector {

	public static IVerbIndex openVerbnetConnection (String pathToVerbnet) throws MalformedURLException {
		LinkedList<String> wordnetTypes = new LinkedList();
		URL url = new URL("file", null , pathToVerbnet );
		IVerbIndex index = new VerbIndex (url);
		return index;
	}


	public static LinkedList<String> assignThetaRoles (IndexedWord subject, IndexedWord object, String objectsDependency, String relationLemma, String pathToVerbnet) throws IOException {
		LinkedList<String> assignedRolesList = new LinkedList ();

		String subjectThemRole;
		String objectThemRole;

		
		/*
		 * VALENCY 2, CASE 1: 2 arguments, the 2nd a dobj
		 */
		
		System.out.println(objectsDependency.toString() + " " +  FramenetConnector.getSortedFramesWithArguments(relationLemma, pathToVerbnet).get(1)!= null);
		System.out.println();
		
		if (objectsDependency.equals("dobj") & FramenetConnector.getSortedFramesWithArguments(relationLemma, pathToVerbnet).get(1)!= null){
			List<LinkedList<HashMap<String, String>>> keySetFrameArguments = FramenetConnector.getSortedFramesWithArguments(relationLemma, pathToVerbnet).get(1);

			for (LinkedList<HashMap<String, String>> value : keySetFrameArguments) {


				if (object.tag().equals("PRP") && !value.get(1).keySet().toString().equals("[T]")){
					/*
					 * TODO: extend with NER, if PRP or 'Person', then Patient 
					 */
					subjectThemRole = value.get(0).keySet().toString();
					objectThemRole = value.get(1).keySet().toString();
					
					assignedRolesList.add(subjectThemRole);
					assignedRolesList.add(objectThemRole);


					System.out.println("CASE 1: them role " +  subjectThemRole + " dobj " + objectThemRole);
					System.out.println();
				}
			}
		}
		
		/*
		 * 	VALENCY 2, CASE 2: 2 arguments, but 3 spots, one of it without a theta-role, because it's a prep
		 */
		
		else if (objectsDependency.equals("nmod") & FramenetConnector.getSortedFramesWithArguments(relationLemma, pathToVerbnet).get(2)!= null){
			List<LinkedList<HashMap<String, String>>> keySetFrameArguments = FramenetConnector.getSortedFramesWithArguments(relationLemma, pathToVerbnet).get(2);

			for (LinkedList<HashMap<String, String>> value : keySetFrameArguments) {

				//the first postVerbElement is empty because it is a preposition
				if (value.get(1).keySet().toString().equals("[none]")){

					System.out.println("[NONE] true");
					
					subjectThemRole = value.get(0).keySet().toString();
					objectThemRole = value.get(2).keySet().toString();
					
					
					/*
					 * TODO: extend --> "in" -> location/time ...
					 */
					if (object.tag().equals("IN")){
						assignedRolesList.add(subjectThemRole);
						assignedRolesList.add(objectThemRole);
						
						System.out.println("CASE 2: them role " + subjectThemRole + " nmod " + objectThemRole);

					}
					//GOAL/DESTINATION
					else if(object.tag().equals("TO") & value.get(2).keySet().equals("[G]") || value.get(2).keySet().equals("[D]") ){
						
						assignedRolesList.add(subjectThemRole);
						assignedRolesList.add(objectThemRole);
						
						System.out.println("CASE 3A: them role " + subjectThemRole + " nmod " + objectThemRole);

					}
					//SOURCE
					else if(object.tag().equals("FROM") & value.get(2).keySet().equals("[S]")){
						
						assignedRolesList.add(subjectThemRole);
						assignedRolesList.add(objectThemRole);
						
						System.out.println("CASE 3B: them role " + subjectThemRole + " nmod " + objectThemRole);

					}
					
					//BENEFICIARY
					else if(object.tag().equals("TO") || object.tag().equals("FOR") & value.get(2).keySet().equals("[B]")){
						
						assignedRolesList.add(subjectThemRole);
						assignedRolesList.add(objectThemRole);
						
						System.out.println("CASE 3C: them role " + subjectThemRole + " nmod " + objectThemRole);

					}
					else if (objectThemRole.equals(null)){
						assignedRolesList.add(subjectThemRole);
						assignedRolesList.add("0");
					}
					else {
						System.out.println("print TAGS " + object.tag() );
												
						assignedRolesList.add(subjectThemRole);
						assignedRolesList.add(objectThemRole);
						
						System.out.println("CASE 4: them role " + subjectThemRole + " nmod " + objectThemRole);

					}

				}
			}
		}

		//else if (objectsDependency.equals("iobj") & FramenetConnector.getSortedFramesWithArguments(relationLemma, pathToVerbnet).get(2)!= null){
//		else {
//			subjectThemRole = "0";
//			objectThemRole = "0";
//			assignedRolesList.add(subjectThemRole);
//			assignedRolesList.add(objectThemRole);
//		}

		//else if (FramenetConnector.getSortedFramesWithArguments(relationLemma, pathToVerbnet).get(2)!= null){
		
	
		return assignedRolesList;
	}
	

}
