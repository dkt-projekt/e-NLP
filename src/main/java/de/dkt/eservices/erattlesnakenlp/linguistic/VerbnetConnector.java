package de.dkt.eservices.erattlesnakenlp.linguistic;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import edu.mit.jverbnet.index.IVerbIndex;
import edu.mit.jverbnet.index.VerbIndex;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.TypedDependency;

public class VerbnetConnector {
	LinkedList<String> assignedRolesList = new LinkedList<String> ();

	public LinkedList<String> getAssignedRolesList (){
		return assignedRolesList;
	}


	public static IVerbIndex openVerbnetConnection (String pathToVerbnet) throws MalformedURLException {
		LinkedList<String> wordnetTypes = new LinkedList<String>();
		URL url = new URL("file", null , pathToVerbnet );
		IVerbIndex index = new VerbIndex (url);
		return index;
	}

	//because a sentence may contain more than one verb, it is passed to the method to get the right arguments and to assign the correct thematic roles
	//	public LinkedList<String> assignThematicRoles (IndexedWord verb, GrammaticalStructure gs, String pathToVerbnet) throws IOException {
	public void assignThematicRoles (IndexedWord verb, GrammaticalStructure gs, String pathToVerbnet) throws IOException {

		FramenetConnector framenetConn = new FramenetConnector();
		LinkedList<String> assignedList = new LinkedList<String>();
		StanfordLemmatizer Lemmatizer = new StanfordLemmatizer();
		SVO_Verb Verb = new SVO_Verb();
		SVOTripleAssignment tripleAssignment = new SVOTripleAssignment();


		ArrayList<TypedDependency> verbDependenciesList = listOnlyArgumentTypeDirectVerbDependencies(verb, gs);
		int amountOfArguments = verbDependenciesList.size();

		for (int i = 0; i < amountOfArguments; i++){
			System.out.println("assignThematicRoles_verbDependenciesList: " + verbDependenciesList.get(i).reln().toString());
		}



		String subjectThemRole = null;
		String objectThemRole = null;
		String iobjThemRole = null;
		//	return assignedList;
	}

	public ArrayList<TypedDependency> listOnlyArgumentTypeDirectVerbDependencies(IndexedWord verb, GrammaticalStructure gs){
		StanfordLemmatizer Lemmatizer = new StanfordLemmatizer();
		ArrayList<TypedDependency> listOnlyArguments = new ArrayList<TypedDependency>();
		SVO_Verb Verb = new SVO_Verb();
		String relationLemma = Lemmatizer.lemmatize(verb.word(), verb.tag()).word();
		System.out.println(relationLemma);

		ArrayList<TypedDependency> verbDependenciesList = Verb.getDirectVerbDependencies(gs, relationLemma);
		//System.out.println(Lemmatizer.lemmatize(verb.word(), verb.tag()).lemma() + "direct verb dependencies: " + verbDependenciesList.size());
		for (int i = 0; i < verbDependenciesList.size(); i++){
			TypedDependency currentDependency = verbDependenciesList.get(i);

			if (!SVOTripleAssignment.excludedPosTagObjects.contains(currentDependency.dep().tag())){
				System.out.println("currentDependency: " + currentDependency.reln().toString());
				listOnlyArguments.add(currentDependency);
			}
		}

		return listOnlyArguments;
	}


	public LinkedList<String> assignThetaRoles (IndexedWord subject, IndexedWord object, String objectsDependency, String iobjectsDependency, String relationLemma, String pathToVerbnet) throws IOException {
		FramenetConnector framenetConn = new FramenetConnector();
		String subjectThemRole = null;
		String objectThemRole = null;
		String iobjThemRole = null;




		/**
		 * VALENCY 3: object's & indirect object's dependencies exist
		 * 
		 */
		if (!objectsDependency.equals(null) && !iobjectsDependency.equals(null) ){


			if (framenetConn.getSortedFramesWithArguments(relationLemma, pathToVerbnet).get(3)!= null){
				List<LinkedList<HashMap<String, String>>> keySetFrameArguments = framenetConn.getSortedFramesWithArguments(relationLemma, pathToVerbnet).get(3);

				for (LinkedList<HashMap<String, String>> value : keySetFrameArguments) {

					//TODO:
					//prepositions:


					subjectThemRole = value.get(0).keySet().toString();
					objectThemRole = value.get(1).keySet().toString();
					iobjThemRole = value.get(2).keySet().toString();

					assignedRolesList.add(subjectThemRole);
					assignedRolesList.add(objectThemRole);
					assignedRolesList.add(iobjThemRole);
					System.out.println("Valency 3, following roles assigned: " + subjectThemRole + " " + objectThemRole + " " + iobjThemRole);			

				}
			}	
		}
		/**
		 * VALENCY 2: dobj's dependency exist
		 * 
		 */

		else if (!objectsDependency.equals(null) && iobjectsDependency.equals(null)){




			/*
			 * VALENCY 2, CASE 1: 2 arguments, the 2nd a dobj
			 */

			System.out.println("frameNet INFO: " + framenetConn.getSortedFramesWithArguments(relationLemma, pathToVerbnet).get(1));

			if (objectsDependency.equals("dobj") & framenetConn.getSortedFramesWithArguments(relationLemma, pathToVerbnet).get(1)!= null){
				List<LinkedList<HashMap<String, String>>> keySetFrameArguments = framenetConn.getSortedFramesWithArguments(relationLemma, pathToVerbnet).get(1);

				System.out.println("->subject: " + subject + " object: " + object + " <-" );

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
					else {
						subjectThemRole = value.get(0).keySet().toString();
						objectThemRole = value.get(1).keySet().toString();

						assignedRolesList.add(subjectThemRole);
						assignedRolesList.add(objectThemRole);
						System.out.println("CASE 1.01: them role " +  subjectThemRole + " dobj " + objectThemRole);
						System.out.println();

					}

				}
			}
		}

		/*
		 * 	VALENCY 2, CASE 2: 2 arguments, but 3 spots, one of it without a theta-role, because it's a prep
		 */

		else if (objectsDependency.equals("nmod") & framenetConn.getSortedFramesWithArguments(relationLemma, pathToVerbnet).get(2)!= null){
			List<LinkedList<HashMap<String, String>>> keySetFrameArguments = framenetConn.getSortedFramesWithArguments(relationLemma, pathToVerbnet).get(2);

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
		return assignedRolesList;
	}
}
