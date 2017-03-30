package de.dkt.eservices.erattlesnakenlp.linguistic;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import edu.mit.jverbnet.index.IVerbIndex;
import edu.mit.jverbnet.index.VerbIndex;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.TypedDependency;

public class VerbnetConnector {
	LinkedList<String> assignedRolesList = new LinkedList<String> ();
	ArrayList<String> simplifiedPOStags = new ArrayList<String>();

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

		//the information about all the possible verb dependencies isn't enough in order to identify the arguments in the sentence
		//for that reason some of them are filtered by the function below
		ArrayList<TypedDependency> verbDependenciesList = listOnlyArgumentTypeDirectVerbDependencies(verb, gs);
		int amountOfArguments = verbDependenciesList.size();



		for (int i = 0; i < amountOfArguments; i++){
			////System.out.println("assignThematicRoles_verbDependenciesList: " + verbDependenciesList.get(i).reln().toString() + verbDependenciesList.get(i).reln().getShortName() + " " + verbDependenciesList.get(i).reln().toString() + " " );

			if (verbDependenciesList.get(i).reln().toString().equals("nsubj") || verbDependenciesList.get(i).reln().toString().equals("nsubjpass")){
			}
			//translate all relevant postVerbDependencies
			else {
				////System.out.println(verbDependenciesList.get(i) +" " +verbDependenciesList.get(i).dep() + " " + verbDependenciesList.get(i).dep().tag() + " " + verbDependenciesList.get(i).dep() );
				if (verbDependenciesList.get(i).reln().toString().equals("nmod")){

					simplifiedPOStags.add("none");
					simplifiedPOStags.add("NP");
				}
				else {
					simplifiedPOStags.add("NP");
				}
			}
		}


		//
		//
		//		String subjectThemRole = null;
		//		String objectThemRole = null;
		//		String iobjThemRole = null;
		//	return assignedList;
	}


	public LinkedList<HashMap<String, String>> matchPOStagStructureWithVerbnet (IndexedWord verb, GrammaticalStructure gs, String pathToVerbnet) throws IOException{
		FramenetConnector framenetConn = new FramenetConnector();
		StanfordLemmatizer stanfordLemmatizer = new StanfordLemmatizer();
		int amountOfPostVerbArguments = simplifiedPOStags.size() ;

		//System.out.println("verb.value(): " + verb.value() + " " + verb.lemma() + " " + stanfordLemmatizer.lemmatizeWord(verb.value()) + " amountOfPostVerbArguments " + amountOfPostVerbArguments);

		List<LinkedList<HashMap<String, String>>> frameListWithSpecifiedLength = framenetConn.getSortedFramesWithArguments(stanfordLemmatizer.lemmatizeWord(verb.value()), pathToVerbnet).get(amountOfPostVerbArguments);
		//LinkedList<HashMap<String, String>> frameAssigned = getSimplePhraseStructureofVerbnet(frameListWithSpecifiedLength);


		LinkedList<HashMap<String, String>> frameAssigned = getSimplePhraseStructureofVerbnet(verb, pathToVerbnet);

		//List<LinkedList<HashMap<String, String>>> frameListWithSpecifiedLength = framenetConn.getSortedFramesWithArguments(verb, pathToVerbnet).get(amountOfPostVerbArguments);

		//getSimplePhraseStructureofVerbnet(frameListWithSpecifiedLength);
		//System.out.println(frameAssigned + " LENGTH_FRAME_ASSIGNED ");

		////System.out.println("xxxxxx_size_ " + amountOfPostVerbArguments + " "  + frameListWithSpecifiedLength);
		////System.out.println("simple_structure_verbnet: ");



		//		for (LinkedList<HashMap<String, String>> value : frameListWithSpecifiedLength) {
		//
		//			for (int i = 0; i < simplifiedPOStags.size(); i++){
		//				//System.out.println("postag-postag");
		//
		//				//System.out.println(simplifiedPOStags.get(i));
		//				if (simplifiedPOStags.get(i).equals("none") &&  value.get(i).keySet().toString().equals("[none]") || !simplifiedPOStags.get(i).equals("none") &&  !value.get(i).keySet().toString().equals("[none]")){
		//					//System.out.println("MATCHING_: " + simplifiedPOStags.get(i) + " " + value.get(i).keySet().toString());
		//				}
		//			}



		return frameAssigned;

	}







	//			subjectThemRole = value.get(0).keySet().toString();
	//			objectThemRole = value.get(1).keySet().toString();
	//			iobjThemRole = value.get(2).keySet().toString();


	//public LinkedList<String> getSimplePhraseStructureofVerbnet(List<LinkedList<HashMap<String, String>>> frameListWithSpecifiedLength ){

	//comparison of the Stanford Dependencies' Simplified Structure and the Verbnet
	//public LinkedList<HashMap<String, String>> getSimplePhraseStructureofVerbnet(List<LinkedList<HashMap<String, String>>> frameListWithSpecifiedLength ){

	public LinkedList<HashMap<String, String>> getSimplePhraseStructureofVerbnet(IndexedWord verb, String pathToVerbnet) throws IOException{

		ArrayList<String> framenetEntryPOStag = new ArrayList<String>();
		LinkedList<HashMap<String, String>> frameFoundAfterProcessing = new LinkedList<HashMap<String, String>>();
		LinkedList<HashMap<String, String>> frameWithSpecificLength = new LinkedList<HashMap<String, String>>();
		FramenetConnector framenetConn = new FramenetConnector();
		StanfordLemmatizer stanfordLemmatizer = new StanfordLemmatizer();
		int verbnetNPcounter = 0;
		int verbnetPPcounter = 0;
		int parserNPcounter = 0;
		int parserPPcounter = 0;

		for (int x = 0; x < simplifiedPOStags.size(); x++){
			if (simplifiedPOStags.get(x).equals("NP"))
				parserNPcounter = parserNPcounter + 1;
			if (simplifiedPOStags.get(x).equals("none"))
				parserPPcounter = parserPPcounter +1;
		}

		if (parserNPcounter > 3 && parserPPcounter == 0){
			List<LinkedList<HashMap<String, String>>> frameListWithSpecifiedLength = framenetConn.getSortedFramesWithArguments(stanfordLemmatizer.lemmatizeWord(verb.value()), pathToVerbnet).get(3);

			for (int i =0; i<frameListWithSpecifiedLength.size(); i++){
				//System.out.println("frameListWithSpecifiedLength.get(i)" + frameListWithSpecifiedLength.get(i) + " " + frameListWithSpecifiedLength.get(i).toArray());
				frameWithSpecificLength = frameListWithSpecifiedLength.get(i);
				verbnetNPcounter = verbnetPhraseCounter("[NP]", frameListWithSpecifiedLength.get(i));
				verbnetPPcounter = verbnetPhraseCounter("[PREP]", frameListWithSpecifiedLength.get(i));

				boolean PPscomparison = (parserPPcounter == verbnetPPcounter);
				boolean NPscomparison = (parserNPcounter == verbnetNPcounter);
				boolean listComparisonElementsAmount = (PPscomparison && NPscomparison);
				if(listComparisonElementsAmount)
				{
					//System.out.println("compare lists: " +compareList(framenetEntryPOStag, simplifiedPOStags) + " compare the amount of NPs and PPs: " + listComparisonElementsAmount + " parserNPcounter: " + parserNPcounter + " " +
					//		" verbnetNPcounter " +	verbnetNPcounter+ " parserPPcounter "  +	parserPPcounter + " verbnetPPcounter " + verbnetPPcounter);
					framenetEntryPOStag = new ArrayList<String>();
					frameFoundAfterProcessing = frameWithSpecificLength;


				}
			}
		}
		else if (parserNPcounter > 3	|| parserPPcounter == 1){
			List<LinkedList<HashMap<String, String>>> frameListWithSpecifiedLength = framenetConn.getSortedFramesWithArguments(stanfordLemmatizer.lemmatizeWord(verb.value()), pathToVerbnet).get(4);

			for (int i =0; i<frameListWithSpecifiedLength.size(); i++){
				//System.out.println("frameListWithSpecifiedLength.get(i)" + frameListWithSpecifiedLength.get(i) + " " + frameListWithSpecifiedLength.get(i).toArray());
				frameWithSpecificLength = frameListWithSpecifiedLength.get(i);
				verbnetNPcounter = verbnetPhraseCounter("[NP]", frameListWithSpecifiedLength.get(i));
				verbnetPPcounter = verbnetPhraseCounter("[PREP]", frameListWithSpecifiedLength.get(i));

				boolean PPscomparison = (parserPPcounter == verbnetPPcounter);
				boolean NPscomparison = (parserNPcounter == verbnetNPcounter);
				boolean listComparisonElementsAmount = (PPscomparison && NPscomparison);
				if(listComparisonElementsAmount)
				{
					//System.out.println("compare lists: " +compareList(framenetEntryPOStag, simplifiedPOStags) + " compare the amount of NPs and PPs: " + listComparisonElementsAmount + " parserNPcounter: " + parserNPcounter + " " +
					//		" verbnetNPcounter " +	verbnetNPcounter+ " parserPPcounter "  +	parserPPcounter + " verbnetPPcounter " + verbnetPPcounter);
					framenetEntryPOStag = new ArrayList<String>();
					frameFoundAfterProcessing = frameWithSpecificLength;
				}
			}

		}
		else if (parserNPcounter > 3	|| parserPPcounter > 2){
			List<LinkedList<HashMap<String, String>>> frameListWithSpecifiedLength = framenetConn.getSortedFramesWithArguments(stanfordLemmatizer.lemmatizeWord(verb.value()), pathToVerbnet).get(5);

			for (int i =0; i<frameListWithSpecifiedLength.size(); i++){
				//System.out.println("frameListWithSpecifiedLength.get(i)" + frameListWithSpecifiedLength.get(i) + " " + frameListWithSpecifiedLength.get(i).toArray());
				frameWithSpecificLength = frameListWithSpecifiedLength.get(i);
				verbnetNPcounter = verbnetPhraseCounter("[NP]", frameListWithSpecifiedLength.get(i));
				verbnetPPcounter = verbnetPhraseCounter("[PREP]", frameListWithSpecifiedLength.get(i));

				boolean PPscomparison = (parserPPcounter == verbnetPPcounter);
				boolean NPscomparison = (parserNPcounter == verbnetNPcounter);
				boolean listComparisonElementsAmount = (PPscomparison && NPscomparison);
				if(listComparisonElementsAmount)
				{
					//System.out.println("compare lists: " +compareList(framenetEntryPOStag, simplifiedPOStags) + " compare the amount of NPs and PPs: " + listComparisonElementsAmount + " parserNPcounter: " + parserNPcounter + " " +
							//" verbnetNPcounter " +	verbnetNPcounter+ " parserPPcounter "  +	parserPPcounter + " verbnetPPcounter " + verbnetPPcounter);
					framenetEntryPOStag = new ArrayList<String>();
					frameFoundAfterProcessing = frameWithSpecificLength;

				}
			}


		}

		else {
			List<LinkedList<HashMap<String, String>>> frameListWithSpecifiedLength = framenetConn.getSortedFramesWithArguments(stanfordLemmatizer.lemmatizeWord(verb.value()), pathToVerbnet).get(parserNPcounter + parserPPcounter);

			for (int i =0; i<frameListWithSpecifiedLength.size(); i++){
				//System.out.println("frameListWithSpecifiedLength.get(i)" + frameListWithSpecifiedLength.get(i) + " " + frameListWithSpecifiedLength.get(i).toArray());
				frameWithSpecificLength = frameListWithSpecifiedLength.get(i);
				verbnetNPcounter = verbnetPhraseCounter("[NP]", frameListWithSpecifiedLength.get(i));
				verbnetPPcounter = verbnetPhraseCounter("[PREP]", frameListWithSpecifiedLength.get(i));

				boolean PPscomparison = (parserPPcounter == verbnetPPcounter);
				boolean NPscomparison = (parserNPcounter == verbnetNPcounter);
				boolean listComparisonElementsAmount = (PPscomparison && NPscomparison);
				if(listComparisonElementsAmount)
				{
					//System.out.println("compare lists: " +compareList(framenetEntryPOStag, simplifiedPOStags) + " compare the amount of NPs and PPs: " + listComparisonElementsAmount + " parserNPcounter: " + parserNPcounter + " " +
							//" verbnetNPcounter " +	verbnetNPcounter+ " parserPPcounter "  +	parserPPcounter + " verbnetPPcounter " + verbnetPPcounter);
					framenetEntryPOStag = new ArrayList<String>();

				}
			}

		}

		parserNPcounter = 0;
		parserPPcounter = 0;
		verbnetNPcounter = 0;
		verbnetPPcounter = 0;

		return frameWithSpecificLength;
	}








	public int verbnetPhraseCounter(String phrase, LinkedList<HashMap<String, String>>frameListWithSpecifiedLength ){
		LinkedList<String> framenetEntryPOStag = new LinkedList<String>();
		int phraseCounter = 0;

		for (int j=0; j<frameListWithSpecifiedLength.size(); j++){

			if ( frameListWithSpecifiedLength.get(j).values().toString().equals(phrase)){
				framenetEntryPOStag.add(phrase);
				//System.out.println("-- " + phrase);
				phraseCounter = phraseCounter + 1;
			}

		}
		return phraseCounter;
	}



	//		for (int j=0; j<frameListWithSpecifiedLength.get(i).size(); j++){
	//
	//			if ( frameListWithSpecifiedLength.get(i).get(j).values().toString().equals("[NP]")){
	//				framenetEntryPOStag.add("NP");
	//				//System.out.println("NP_adddeeeed");
	//				verbnetNPcounter = verbnetNPcounter+1;
	//			}
	//			else if (frameListWithSpecifiedLength.get(i).get(j).values().toString().equals("[PREP]")){
	//				framenetEntryPOStag.add("none");
	//				//System.out.println("noooone");
	//				verbnetPPcounter = verbnetPPcounter + 1;
	//			}
	//		}


	public static boolean compareList(List ls1,List ls2){
		return ls1.toString().contentEquals(ls2.toString())?true:false;
	}

	public ArrayList<TypedDependency> listOnlyArgumentTypeDirectVerbDependencies(IndexedWord verb, GrammaticalStructure gs){
		StanfordLemmatizer Lemmatizer = new StanfordLemmatizer();
		ArrayList<TypedDependency> listOnlyArguments = new ArrayList<TypedDependency>();
		SVO_Verb Verb = new SVO_Verb();
		String relationLemma = Lemmatizer.lemmatize(verb.word(), verb.tag()).word();
		//System.out.println(relationLemma);

		ArrayList<TypedDependency> verbDependenciesList = Verb.getDirectVerbDependencies(gs, relationLemma);
		////System.out.println(Lemmatizer.lemmatize(verb.word(), verb.tag()).lemma() + "direct verb dependencies: " + verbDependenciesList.size());
		for (int i = 0; i < verbDependenciesList.size(); i++){
			TypedDependency currentDependency = verbDependenciesList.get(i);

			if (!SVOTripleAssignment.excludedPosTagObjects.contains(currentDependency.dep().tag())){
				//	//System.out.println("currentDependency: " + currentDependency.reln().toString() + " " + currentDependency.reln().getLongName());
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
					//System.out.println("Valency 3, following roles assigned: " + subjectThemRole + " " + objectThemRole + " " + iobjThemRole);			

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

			//System.out.println("frameNet INFO: " + framenetConn.getSortedFramesWithArguments(relationLemma, pathToVerbnet).get(1));

			if (objectsDependency.equals("dobj") & framenetConn.getSortedFramesWithArguments(relationLemma, pathToVerbnet).get(1)!= null){
				List<LinkedList<HashMap<String, String>>> keySetFrameArguments = framenetConn.getSortedFramesWithArguments(relationLemma, pathToVerbnet).get(1);

				//System.out.println("->subject: " + subject + " object: " + object + " <-" );

				for (LinkedList<HashMap<String, String>> value : keySetFrameArguments) {


					if (object.tag().equals("PRP") && !value.get(1).keySet().toString().equals("[T]")){
						/*
						 * TODO: extend with NER, if PRP or 'Person', then Patient 
						 */
						subjectThemRole = value.get(0).keySet().toString();
						objectThemRole = value.get(1).keySet().toString();

						assignedRolesList.add(subjectThemRole);
						assignedRolesList.add(objectThemRole);


						//System.out.println("CASE 1: them role " +  subjectThemRole + " dobj " + objectThemRole);
						//System.out.println();
					}
					else {
						subjectThemRole = value.get(0).keySet().toString();
						objectThemRole = value.get(1).keySet().toString();

						assignedRolesList.add(subjectThemRole);
						assignedRolesList.add(objectThemRole);
						//System.out.println("CASE 1.01: them role " +  subjectThemRole + " dobj " + objectThemRole);
						//System.out.println();

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

					//System.out.println("[NONE] true");

					subjectThemRole = value.get(0).keySet().toString();
					objectThemRole = value.get(2).keySet().toString();


					/*
					 * TODO: extend --> "in" -> location/time ...
					 */
					if (object.tag().equals("IN")){
						assignedRolesList.add(subjectThemRole);
						assignedRolesList.add(objectThemRole);

						//System.out.println("CASE 2: them role " + subjectThemRole + " nmod " + objectThemRole);

					}
					//GOAL/DESTINATION
					else if(object.tag().equals("TO") & value.get(2).keySet().equals("[G]") || value.get(2).keySet().equals("[D]") ){

						assignedRolesList.add(subjectThemRole);
						assignedRolesList.add(objectThemRole);

						//System.out.println("CASE 3A: them role " + subjectThemRole + " nmod " + objectThemRole);

					}
					//SOURCE
					else if(object.tag().equals("FROM") & value.get(2).keySet().equals("[S]")){

						assignedRolesList.add(subjectThemRole);
						assignedRolesList.add(objectThemRole);

						//System.out.println("CASE 3B: them role " + subjectThemRole + " nmod " + objectThemRole);

					}

					//BENEFICIARY
					else if(object.tag().equals("TO") || object.tag().equals("FOR") & value.get(2).keySet().equals("[B]")){

						assignedRolesList.add(subjectThemRole);
						assignedRolesList.add(objectThemRole);

						//System.out.println("CASE 3C: them role " + subjectThemRole + " nmod " + objectThemRole);

					}
					else if (objectThemRole.equals(null)){
						assignedRolesList.add(subjectThemRole);
						assignedRolesList.add("0");
					}
					else {
						//System.out.println("print TAGS " + object.tag() );

						assignedRolesList.add(subjectThemRole);
						assignedRolesList.add(objectThemRole);

						//System.out.println("CASE 4: them role " + subjectThemRole + " nmod " + objectThemRole);

					}
				}
			}
		}
		return assignedRolesList;
	}


}
