//package de.dkt.eservices.execution;
//
//import java.io.BufferedWriter;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.OutputStreamWriter;
//import java.net.URLEncoder;
//import java.text.DateFormat;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Locale;
//import java.util.Map;
//import java.util.Properties;
//import java.util.Set;
//
//import org.apache.commons.io.IOUtils;
//import org.apache.http.HttpEntity;
//import org.apache.http.StatusLine;
//import org.apache.http.auth.AuthScope;
//import org.apache.http.auth.UsernamePasswordCredentials;
//import org.apache.http.client.CredentialsProvider;
//import org.apache.http.client.methods.CloseableHttpResponse;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.entity.StringEntity;
//import org.apache.http.impl.client.BasicCredentialsProvider;
//import org.apache.http.impl.client.CloseableHttpClient;
//import org.apache.http.impl.client.HttpClients;
//import org.joda.time.format.DateTimeFormatter;
//import org.joda.time.format.ISODateTimeFormat;
//import org.json.JSONArray;
//import org.json.JSONObject;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import com.hp.hpl.jena.rdf.model.Model;
//import com.hp.hpl.jena.rdf.model.RDFNode;
//import com.hp.hpl.jena.rdf.model.ResIterator;
//import com.hp.hpl.jena.rdf.model.Resource;
//import com.hp.hpl.jena.vocabulary.RDF;
//
//import de.dkt.common.niftools.DBO;
//import de.dkt.common.niftools.DKTNIF;
//import de.dkt.common.niftools.DktAnnotation;
//import de.dkt.common.niftools.ITSRDF;
//import de.dkt.common.niftools.NIF;
//import de.dkt.common.niftools.NIFReader;
//import de.dkt.common.niftools.NIFWriter;
//import de.dkt.common.niftools.TIME;
//import de.dkt.eservices.eheideltime.EHeideltimeService;
//import de.dkt.eservices.eopennlp.EOpenNLPService;
//import de.dkt.eservices.erattlesnakenlp.linguistic.MovementActionEvent;
//import de.dkt.eservices.erattlesnakenlp.modules.LanguageIdentificator;
//import de.dkt.eservices.erattlesnakenlp.modules.MendelsohnParser;
//import de.dkt.eservices.erattlesnakenlp.modules.TravelModeDetection;
//import de.dkt.eservices.erattlesnakenlp.modules.mae.MovementVerbDetection;
//import de.dkt.eservices.eweka.modules.EMClustering;
//import de.dkt.eservices.eweka.modules.SimpleKMeansClustering;
//import de.dkt.eservices.execution.GenericEvent.Component;
//import edu.stanford.nlp.ling.CoreAnnotations;
//import edu.stanford.nlp.ling.CoreLabel;
//import edu.stanford.nlp.pipeline.Annotation;
//import edu.stanford.nlp.pipeline.StanfordCoreNLP;
//import edu.stanford.nlp.util.CoreMap;
//import eu.freme.common.conversion.rdf.RDFConstants;
//import eu.freme.common.conversion.rdf.RDFConstants.RDFSerialization;
//
//public class ProcessCondatCorpusNIF {
//
//	protected StanfordCoreNLP pipeline;
//
//	public ProcessCondatCorpusNIF() {
//	}
//	
//	public void initializeModels() {
//		Properties props;
//		props = new Properties();
////		props.put("annotators", "tokenize, ssplit, pos, lemma");
//		props.put("annotators", "tokenize, ssplit");
//		this.pipeline = new StanfordCoreNLP(props);
//	}
//	
//	public static void main(String[] args) throws Exception {
//		ProcessCondatCorpusNIF pml = new ProcessCondatCorpusNIF();		
////		String sFolder = "/Users/jumo04/Documents/DFKI/Conferences/EventsStories/MendelsohnLetters/manual/";
////		String sFolder = "/Users/jumo04/Downloads/allCleanLetters2/";
//		String sFolder = "/Users/jumo04/Documents/DFKI/Conferences/NLPmJ2017/data/condat_en/";
//		
//		/**
//		 * Code to process the plaintext files of the mendelsohn letters.
//		 */
//		pml.initializeModels();
////		float[] fs2 = new float[1];
////		String outputFolder = "nif_MD/";
////		pml.processInitialDocuments(sFolder, fs2, outputFolder);
//		
//		pml.evaluate(sFolder,"Events_MD_WithoutDuplicates_OnlyTriggered_ALL_Sentences",true,true,true);
//	}
//		
//	public void evaluate(String sFolder, String folderPart, boolean triggered, boolean includeMetadata, boolean deleteDuplicates) throws Exception{
//
//		float [][] weights = {
////				{0.3f,0.2f,0.2f,0.1f,0.1f,0.1f},
////				{0.3f,0.2f,0.2f,0.1f,0.1f,0.1f},
////				{0.55f,0.15f,0.15f,0.05f,0.05f,0.05f},
////				{0.167f,0.167f,0.167f,0.167f,0.167f,0.167f},
//				{0.3f,0.2f,0.2f,0.1f,0.1f,0.1f}
//				};
//		int counter = 1;
//		for (float[] fs : weights) {
//			String outputFolder = "nif_"+folderPart+"_"+counter+"/";
//			//processInitialDocuments(sFolder,fs,outputFolder,triggered,includeMetadata,deleteDuplicates);
//			String headers = generateARFFHeaders(sFolder,fs,outputFolder,triggered,includeMetadata,deleteDuplicates);
//			System.out.println(headers);
//			System.out.println("==============<<<<<<<<<<<<<<<<<<>>>>>>>>>>>>>>>>>>>>>>>>>===============");
//			System.out.println("==============<<<<<<<<<<<<<<<<<<>>>>>>>>>>>>>>>>>>>>>>>>>===============");
//			System.out.println("==============<<<<<<<<<<<<<<<<<<>>>>>>>>>>>>>>>>>>>>>>>>>===============");
//			String body = generateARFFBody(headers,sFolder,fs,outputFolder,triggered,includeMetadata,deleteDuplicates);
//			System.out.println(body);
////			String result = clusterARFF(headers+body);
//			counter++;
//		}
//		counter=1;
//		String output = "";
//		for (float[] fs : weights) {
//			String outputFolder = "nif_"+folderPart+"_"+counter+"/";
//			output += computeResults(sFolder+outputFolder);
//			counter++;
//		}
//		System.out.println(output);
//	}
//
//	public List<GenericEvent> getGenericEvents(String sFolder, String fileName){
//		try{
//			File file = new File(sFolder + fileName);
//			if(!file.exists()){
//				return new LinkedList<GenericEvent>();
//			}
//			String inputText = IOUtils.toString(new FileInputStream(file));
//	//				System.out.println(inputText);
//			List<GenericEvent> events = new LinkedList<GenericEvent>();
//			JSONArray array = new JSONArray(inputText);
////			System.out.println(file.getName());
//			for (int i = 0; i < array.length(); i++) {
//				JSONObject obj = (JSONObject) array.get(i);
//				GenericEvent ev = new GenericEvent();
//				ev.id = (i+1)+"";
//				ev.sentenceId = obj.getString("sentID");
//				ev.docId = obj.getString("docID");
//				ev.type = obj.getString("eventType");
//				JSONArray jsonComps = obj.getJSONArray("components");
//				for (int j = 0; j < jsonComps.length(); j++) {
//					JSONObject obj2 = (JSONObject) jsonComps.get(j);
//					ev.addComponent(Integer.parseInt(obj2.getString("initialSentPos")), 
//							Integer.parseInt(obj2.getString("finalSentPos")), 
//							obj2.getString("type"), 
//							obj2.getString("text"));
//				}
//				events.add(ev);
//			}
//			return events;
//		}
//		catch(Exception e){
//			e.printStackTrace();
//			return null;
//		}
//	}
//
////	HashMap<String, HashMap<String,Integer>> eventsMap = new HashMap<String, HashMap<String,Integer>>();
//	HashMap<String, Integer> eventsMap = new HashMap<String,Integer>();
//	HashMap<String, List<GenericEvent>> wholeEvents = new HashMap<String,List<GenericEvent>>();
//	public String generateARFFHeaders(String sFolder,float[] weights,String outputFolder,
//			boolean triggered, boolean includeMetadata, boolean deleteDuplicates) throws Exception {
//		String result = "";
//		File folder = new File(sFolder+"nifs/");
//		File[] files = folder.listFiles();
////		HashMap<String,Integer> docMap = new HashMap<String, Integer>();
////		for (File file : files) {
////			if(!file.isDirectory() && !file.getName().startsWith(".") && file.getName().endsWith(".nif")){
////				String fileName = file.getName();
////				docMap.put(fileName, 0);
////			}
////		}
////		files = folder.listFiles();
//		for (File file : files) {
//			if(!file.isDirectory() && !file.getName().startsWith(".") && file.getName().endsWith(".nif")){
//				String language = "en";
//				String fileName = file.getName().substring(0, file.getName().indexOf("."))+".xml.txt.condat.events.json";
//				System.out.println(file.getName());
//				List<GenericEvent> events = getGenericEvents(sFolder+"events/", fileName);
//				wholeEvents.put(file.getName(), events);
//				if(events!=null && events.size()>0){
//					String inputText = IOUtils.toString(new FileInputStream(file));
//					Model inputModel = NIFReader.extractModelFromFormatString(inputText, RDFSerialization.TURTLE);
//
//					for (GenericEvent ge : events) {
//						List<Component> components = ge.components;
//						for (Component c : components) {
////							System.out.println(c.type + "--"+ c.text);
//							if(!eventsMap.containsKey(c.text)){
////								HashMap<String,Integer> auxDocMap = new HashMap<String, Integer>();
////								Set<String> docMapKeys = docMap.keySet();
////								for (String string : docMapKeys) {
////									auxDocMap.put(string, docMap.get(string));
////								}
////								eventsMap.put(c.text, auxDocMap);
//								eventsMap.put(c.text.replace(' ', '_'), 0);
//							}
//						}
//					}
//				}
//			}
//		}
//		result += "@RELATION RegionalNews_101\n";
//		result += "@ATTRIBUTE docId STRING\n";
//		Set<String> eventsIds = eventsMap.keySet();
//		for (String string : eventsIds) {
//			result += "@ATTRIBUTE " + string + " NUMERIC\n";
////			String input = ""
////					+ "	@ATTRIBUTE Schwarz  NUMERIC\n"
////					+ "	@ATTRIBUTE Bayern  NUMERIC\n"
////					+ "	@ATTRIBUTE Frankfurter_Museums_Junge_Kunst_Armin_Hauer  NUMERIC\n"
////					+ "	@ATTRIBUTE Deutsche_Bahn  NUMERIC\n"
////					+ "	@ATTRIBUTE Rathaus  NUMERIC\n"
////					+ "	@ATTRIBUTE 1936  NUMERIC\n"
////					+ "\n"
////					+ "	@DATA\n"
//		}
//		result += "\n";
//		return result;
//	}
//
//	public String generateARFFBody(String headers, String sFolder,float[] weights,String outputFolder,
//			boolean triggered, boolean includeMetadata, boolean deleteDuplicates) throws Exception {
//		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(sFolder+File.separator+"arffDocs.txt"), "utf-8"));
//		bw.write(headers);
//		bw.write("@DATA\n");
////		String result = "";
////		result += "@DATA\n";
//		File folder = new File(sFolder+"nifs/");
//		File[] files = folder.listFiles();
//		HashMap<String,String> docMap = new HashMap<String, String>();
//		for (File file : files) {
//			if(!file.isDirectory() && !file.getName().startsWith(".") && file.getName().endsWith(".nif")){
//				String language = "en";
//				System.out.println(file.getName());
//				String fileName = file.getName().substring(0, file.getName().indexOf("."))+".xml.txt.condat.events.json";
//				List<GenericEvent> events = wholeEvents.get(file.getName());//getGenericEvents(sFolder+"events/", fileName);
//				if(events!=null && events.size()>0){
////					for (String string : eventsIds) {
////						eventsMap.put(string, 0);
////					}
//					String inputText = IOUtils.toString(new FileInputStream(file));
//					Model inputModel = NIFReader.extractModelFromFormatString(inputText, RDFSerialization.TURTLE);
//					for (GenericEvent ge : events) {
//						List<Component> components = ge.components;
//						for (Component c : components) {
////							System.out.println(c.type + "--"+ c.text);
////							if(!eventsMap.containsKey(c.text)){
////								HashMap<String,Integer> auxDocMap = new HashMap<String, Integer>();
//							eventsMap.put(c.text.replace(' ', '_'), eventsMap.get(c.text.replace(' ', '_'))+1);
////							}
//						}
//					}
//					String result = "";
//					result += file.getName() + ",";
//					Set<String> eventsIds = eventsMap.keySet();
//					Iterator<String> eventsIdsI = eventsIds.iterator();
//					while(eventsIdsI.hasNext()) {
//						String string = eventsIdsI.next();
//						result += eventsMap.get(string);
//						if(eventsIdsI.hasNext()){
//							result += ",";
//						}
//						eventsMap.put(string, 0);
//					}
////					for (String string : eventsIds) {
////					}
////					result = result.substring(0, result.length()-1);
//					result += "\n";
//					bw.write(result);
//					//
////					+ "	CondatTest6_3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,0,0,0,0,0\n"
////					+ "	CondatTest6_2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0\n"
//				}
//			}
//		}
//		bw.close();
//		return "";//result;
//	}
//
//	public String clusterARFF(String arff){
//		String mode = "text";
//		JSONObject obj = EMClustering.trainModelAndClusterInstances(mode, arff, "en");
//		//JSONObject obj = SimpleKMeansClustering.trainModelAndClusterInstances(mode, arff, "en");
//		return obj.toString();
//	}
//
//	public void processInitialDocuments(String sFolder,float[] weights,String outputFolder,
//			boolean triggered, boolean includeMetadata, boolean deleteDuplicates) throws Exception {
//		File folder = new File(sFolder+"nifs/");
//		File[] files = folder.listFiles();
//		for (File file : files) {
//			if(!file.isDirectory() && !file.getName().startsWith(".") && file.getName().endsWith(".nif")){
////				System.out.print("Processing: "+file.getName()+" ...");
////				System.out.println(inputText);
//				String language = "en";
//				String fileName = file.getName().substring(0, file.getName().indexOf("."))+".xml.txt.condat.events.json";
//				List<GenericEvent> events = getGenericEvents(sFolder+"events/", fileName);
//				if(events!=null && events.size()>0){
////					System.out.print(" ...["+events.size()+" events]...");
//					String inputText = IOUtils.toString(new FileInputStream(file));
////					System.out.println(fileName);
////					System.out.println("\t"+events.size());
//					Model inputModel = NIFReader.extractModelFromFormatString(inputText, RDFSerialization.TURTLE);
//				}
////				System.out.println(" ...DONE");
//			}
//		}
//	}
//
//	public String detectSextuplesWithEvents(Model nifModel, String languageParam, RDFConstants.RDFSerialization inFormat, float [] weights, List<GenericEvent> events,
//			boolean triggered, boolean includeMetadata, boolean deleteDuplicates) throws IOException{
//		try{
//			String documentURI = NIFReader.extractDocumentWholeURI(nifModel);
////			System.out.println(NIFReader.model2String(nifModel, RDFSerialization.TURTLE));
//			List<MovementActionEvent> maes = new LinkedList<MovementActionEvent>();
//			/**
//			 * Assign weights to the elements of the sextuple.
//			 */
//			float personWeight = weights[0];
//			float originWeight = weights[1];
//			float destinationWeight = weights[2];
//			float departureTimeWeight = weights[3];
//			float arrivalTimeWeight = weights[4];
//			float modeWeight = weights[5];
//
//			/*
//			 * Split the text into sentences 
//			 */
//			try{
//				String inputText = NIFReader.extractIsString(nifModel);
//				Annotation document = new Annotation(inputText);
//				pipeline.annotate(document);
////				Annotation document2 = new Annotation(inputText);
////				pipeline.annotate(document2);
//				
//				List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
////				List<CoreMap> sentences2 = document2.get(CoreAnnotations.SentencesAnnotation.class);
////				if(sentences.size()!=sentences2.size()){
////					errorCounter++;
////					System.out.println("ERROR in sentences size in file: "+documentURI);
////					System.out.println("-----");
////					System.out.println(t);
////					System.out.println("-----");
////					System.out.println(inputText);
////					System.exit(0);
////				}
////				return nifModel;
//				Map<String, DktAnnotation> elements = NIFReader.extractAnnotations(nifModel);
//				if(elements==null){
//					return nifModel;
//				}
//
//				// TODO Generate the ARFF for the clustering thing.
//				
//				for (GenericEvent event : events) {
//					int tokenCounter = 0;
////					System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
////					System.out.println(event.toString());
//					int sentenceId = Integer.parseInt(event.sentenceId);
//					CoreMap sentence = sentences.get(sentenceId);
////					System.out.println(sentence.get(CoreAnnotations.TextAnnotation.class));
//					
//					/**
//					 * If the event is too big, then we apply windows for getting a smaller number of results.
//					 */
//					int sentenceEnd = 0;
//					int sentenceOffset = inputText.length();
//		                for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
//		                	if(tokenCounter>=event.ini && tokenCounter<=event.end){
//			                	int tokenStart = token.beginPosition();
//			                	int tokenEnd = token.endPosition();
//			                	if(tokenStart<sentenceOffset){
//			                		sentenceOffset=tokenStart;
//			                	}
//			                	if(tokenEnd>sentenceEnd){
//			                		sentenceEnd=tokenEnd;
//			                	}
//		                	}
//		                	tokenCounter++;
//		                }
//					
////	                System.out.println(sentenceOffset+"--"+sentenceEnd);
//					/**
//					 * Check in every sentence if there are alements of the sextuple (take them from the NIF)
//					 */
//					
////					System.out.println(elements.size());
//					List<DktAnnotation> iPersons = new LinkedList<DktAnnotation>();
//					List<DktAnnotation> iLocations= new LinkedList<DktAnnotation>();
//					List<DktAnnotation> iTimes= new LinkedList<DktAnnotation>();
//					List<DktAnnotation> iModes = new LinkedList<DktAnnotation>();
//					List<DktAnnotation> iTriggers = new LinkedList<DktAnnotation>();
//
//					Set<String> elementsKeys = elements.keySet();
//					for (String elementKey : elementsKeys) {
//						DktAnnotation object = elements.get(elementKey);
//						if( (object.getStart()>sentenceOffset && object.getStart()<sentenceEnd)
//								||
//								(object.getEnd()>sentenceOffset && object.getEnd()<sentenceEnd)){
//							if(object.getType().equalsIgnoreCase("person")){
//								iPersons.add(object);
//							}
//							else if(object.getType().equalsIgnoreCase("location")){
//								iLocations.add(object);
//							}
//							else if(object.getType().equalsIgnoreCase("temp")){
//								iTimes.add(object);
//							}
//							else if(object.getType().equalsIgnoreCase("mode")){
//								iModes.add(object);
//							}
//							else if(object.getType().equalsIgnoreCase("triggerVerb")){
//								iTriggers.add(object);
//							}
//							else if(object.getType().equalsIgnoreCase("triggerTerm")){
//								iTriggers.add(object);
//							}
//						}
//					}
//					
//					List<Component> components = event.components;
//					for (Component c : components) {
//						// ORG or GPE
//						if(c.type.equalsIgnoreCase("PER")){
//							iPersons.add(c.getDktAnnotation());
//						}
//						else if(c.type.equalsIgnoreCase("ORG")){
//							iPersons.add(c.getDktAnnotation());
//						}
//						else if(c.type.equalsIgnoreCase("GPE")){
//							iPersons.add(c.getDktAnnotation());
//						}
//						else if(c.type.equalsIgnoreCase("LOC")){
//							iLocations.add(c.getDktAnnotation());
//						}
//						else if(c.type.equalsIgnoreCase("TIME")){
//							iTimes.add(c.getDktAnnotation());
//						}
////						else if(c.type.equalsIgnoreCase("mode")){
////							iModes.add(c.getDktAnnotation());
////						}
////						else if(c.type.equalsIgnoreCase("triggerVerb")){
////							iTriggers.add(c.getDktAnnotation());
////						}
////						else if(c.type.equalsIgnoreCase("triggerTerm")){
////							iTriggers.add(c.getDktAnnotation());
////						}
//					}
//					
////					System.out.println(iPersons.size()+"--"+iLocations.size()+"--"+iTimes.size()+"--"+iModes.size()+"--"+iTriggers.size());
//////					System.exit(0);
//					int upperLevel = (iPersons.size()+1) * (iLocations.size()+1) * (iLocations.size()+1) * (iTimes.size()+1) * (iTimes.size()+1) * (iModes.size()+1);
////					System.out.println(upperLevel);
//					if(upperLevel>10000 || upperLevel<0){
//						continue;
//					}
////					System.out.print(" ...[#"+upperLevel+"]...");
///**#
// * If person, location or time are empty, then include the letter information.
// */	
//					iPersons.add(new DktAnnotation());
//					iLocations.add(new DktAnnotation());
//					iTimes.add(new DktAnnotation());
//					iModes.add(new DktAnnotation());
//
//					if(triggered && iTriggers.isEmpty()){
//						continue;
//					}
//
//					/**
//					 * Generate all the possible sextuples
//					 */
//					for (DktAnnotation person: iPersons) {
////						System.out.println("--------");
//						for (DktAnnotation location1 : iLocations) {
//							for (DktAnnotation location2 : iLocations) {
//								if( !(!location1.isEmpty() && !location2.isEmpty() && 
//										location1.getText().equalsIgnoreCase(location2.getText())) ){
//									for (DktAnnotation time1 : iTimes) {
//										for (DktAnnotation time2: iTimes) {
//											if( !( !time1.isEmpty() && !time2.isEmpty() && 
//													time1.getText().equalsIgnoreCase(time2.getText())) ){
//											for (DktAnnotation mode: iModes) {
////												System.out.println(time1.getText());
//												DateFormat format3 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
////										        2017-04-21T00:00:00
//												Date dTime1 = null;
//												try{
//													dTime1 = (time1.getText()!=null) ? format3.parse(time1.getText()) : null;
//												}
//												catch(Exception e){
//												}
//												Date dTime2 = null; 
//												try{
//													dTime2 = (time2.getText()!=null) ? format3.parse(time2.getText()) : null;
//												}
//												catch(Exception e){
//												}
//
//												float perVal=1,loc1Val=1,loc2Val=1,tim1Val=1,tim2Val=1,modVal=1;
//												if(person.getType().equalsIgnoreCase("empty")){
//													perVal=0;
//												}
//												if(location1.getType().equalsIgnoreCase("empty")){
//													loc1Val=0;
//												}
//												if(location2.getType().equalsIgnoreCase("empty")){
//													loc2Val=0;
//												}
//												if(time1.getType().equalsIgnoreCase("empty")){
//													tim1Val=0;
//												}
//												if(time2.getType().equalsIgnoreCase("empty")){
//													tim2Val=0;
//												}
//												if(mode.getType().equalsIgnoreCase("empty")){
//													modVal=0;
//												}
//												float maeScore = perVal*personWeight + loc1Val*originWeight + loc2Val*destinationWeight + 
//														tim1Val*departureTimeWeight + tim2Val*arrivalTimeWeight + modVal*modeWeight;
//
////											System.out.println("DEBUG TEXT: --------------");
////											System.out.println("DEBUG TEXT: "+sentence.get(CoreAnnotations.OriginalTextAnnotation.class));
////											System.out.println("DEBUG TEXT: "+sentence.get(CoreAnnotations.TextAnnotation.class));
////											System.out.println("DEBUG TEXT: "+sentence.get(CoreAnnotations.SentenceBeginAnnotation.class));
////											System.out.println("DEBUG TEXT: "+sentence.get(CoreAnnotations.OriginalTextAnnotation.class));
//												MovementActionEvent auxmae = new MovementActionEvent(sentenceOffset, sentenceEnd, 
//														person.getText(), 
//														location1.getText(), location2.getText(), 
//														dTime1, dTime2, 
//														mode.getText(),
//														sentence.get(CoreAnnotations.TextAnnotation.class),
//														//sentence,
//														maeScore);
//
//												maes.add(auxmae);
//											}
//										}
//										}
//									}
//								}
//							}
//						}
//					}
//				}
//			}
//			catch(Exception e){
//				e.printStackTrace();
//				return nifModel;
//			}
//			
////			/**
////			 * Detect Transportation modes: indirectly from some self-defined rules taking information from the defined MAEs.
////			 */
////			auxModel = travelModeDetection.detectIndirectTransportationModes(auxModel, languageParam, inFormat);
////			System.out.print(" ...["+maes.size()+" maes]...");
//
//			for (MovementActionEvent mae : maes) {
//				NIFWriter.addSextupleMAEAnnotation(nifModel, documentURI, 
//						mae.getPerson(),mae.getOrigin(),mae.getDestination(),
//						mae.getDepartureTime(),mae.getArrivalTime(),mae.getTravelMode(),
//						mae.getStartIndex(),mae.getEndIndex(),
//						mae.getText(),mae.getScore()
//						);
//			}
////			System.out.println(NIFReader.model2String(nifModel, RDFSerialization.TURTLE));	
//			return nifModel;
//		}
//		catch(Exception e){
//			e.printStackTrace();
//			return nifModel;
//		}
//	}
//
//	public String computeResults(String sFolder) throws Exception {
//		String output = "";
//		List<MovementActionEvent> maes = new LinkedList<MovementActionEvent>();
//		File folder = new File(sFolder);
//		File[] files = folder.listFiles();
//		for (File file : files) {
//			if(!file.isDirectory() && !file.getName().startsWith(".") && file.getName().endsWith(".nif")){
//				String inputText = IOUtils.toString(new FileInputStream(file));
//				String language = "en";
//				Model nifModel = NIFReader.extractModelFromFormatString(inputText, RDFSerialization.TURTLE);
//				Map<String,Map<String,String>> ms = NIFReader.extractMAEs(nifModel);
//				if(ms==null){
//					continue;
//				}
//				Set<String> keys = ms.keySet();
//				for (String key : keys) {
//					Map<String,String> map = ms.get(key);
//					String start = ( map.containsKey(NIF.beginIndex.toString()) ) ? map.get(NIF.beginIndex.toString()) : "0" ;
//					String end = ( map.containsKey(NIF.endIndex.toString()) ) ? map.get(NIF.endIndex.toString()) : null ;
//					String person = ( map.containsKey(DKTNIF.maePerson.toString()) ) ? map.get(DKTNIF.maePerson.toString()) : null ;
//					String origin = ( map.containsKey(DKTNIF.maeOrigin.toString()) ) ? map.get(DKTNIF.maeOrigin.toString()) : null ;
//					String destination = ( map.containsKey(DKTNIF.maeDestination.toString()) ) ? map.get(DKTNIF.maeDestination.toString()) : null ;
//					
//					DateFormat format3 = new SimpleDateFormat("yyyyMMddHHmmss");
////					System.out.println(map.get(DKTNIF.maeDepartureTime.toString()));
//					Date depTime = ( map.containsKey(DKTNIF.maeDepartureTime.toString()) ) ? format3.parse(map.get(DKTNIF.maeDepartureTime.toString())) : null ;
//					Date arrTime = ( map.containsKey(DKTNIF.maeArrivalTime.toString()) ) ? format3.parse(map.get(DKTNIF.maeArrivalTime.toString())) : null ;
//					String travelMode = ( map.containsKey(DKTNIF.maeTravelMode.toString()) ) ? map.get(DKTNIF.maeTravelMode.toString()) : null ;
//					String score = ( map.containsKey(DKTNIF.maeScore.toString()) ) ? map.get(DKTNIF.maeScore.toString()) : null ;
//					String text = ( map.containsKey(NIF.anchorOf.toString()) ) ? map.get(NIF.anchorOf.toString()) : null ;
//					MovementActionEvent auxMae = new MovementActionEvent(Integer.parseInt(start), Integer.parseInt(end), 
//							person, origin, destination, 
//							depTime, arrTime, travelMode, 
//							text, Float.parseFloat(score)); 
//					maes.add(auxMae);
//				}
//			}
//		}
////		System.out.println("Folder: "+sFolder);
//		output += "Folder: "+sFolder+"\n";
//		float thresholds [] = {0.1f,0.2f,0.3f,0.4f,0.5f,0.6f,0.7f,0.8f,0.9f,1f};
//		for (float th : thresholds) {
//			int counter = 1;
//			JSONArray array = new JSONArray();
//			for (MovementActionEvent m : maes) {
//				if(m.getScore()>th){
//					JSONObject obj = new JSONObject();
//					obj.put("id", counter);
//					obj.put("startIndex", m.getStartIndex());
//					obj.put("endIndex", m.getEndIndex());
//					obj.put("person", m.getPerson());
//					obj.put("origin", m.getOrigin());
//					obj.put("destination", m.getDestination());
//					obj.put("departureTime", m.getDepartureTime());
//					obj.put("arrivalTime", m.getArrivalTime());
//					obj.put("travelMode", m.getTravelMode());
//					obj.put("text", m.getText());
//					obj.put("score", m.getScore());
//					counter++;
//					array.put(obj);
//				}
//			}
////			System.out.println("\tThreshold: "+th+"\tNumber of MAEs: "+array.length());
//			output += "\tThreshold: "+th+"\tNumber of MAEs: "+array.length() + "\n";
//			File outputFile = new File(sFolder+File.separator+th+".json");
//			outputFile.createNewFile();
//			IOUtils.write(array.toString(1),new FileOutputStream(outputFile));
//		}
//		return output;
//	}
//}
