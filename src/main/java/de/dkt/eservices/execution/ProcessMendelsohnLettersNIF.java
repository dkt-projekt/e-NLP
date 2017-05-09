package de.dkt.eservices.execution;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

import de.dkt.common.niftools.DBO;
import de.dkt.common.niftools.DKTNIF;
import de.dkt.common.niftools.DktAnnotation;
import de.dkt.common.niftools.ITSRDF;
import de.dkt.common.niftools.NIF;
import de.dkt.common.niftools.NIFReader;
import de.dkt.common.niftools.NIFWriter;
import de.dkt.common.niftools.TIME;
import de.dkt.eservices.eheideltime.EHeideltimeService;
import de.dkt.eservices.eopennlp.EOpenNLPService;
import de.dkt.eservices.erattlesnakenlp.linguistic.MovementActionEvent;
import de.dkt.eservices.erattlesnakenlp.modules.LanguageIdentificator;
import de.dkt.eservices.erattlesnakenlp.modules.MendelsohnParser;
import de.dkt.eservices.erattlesnakenlp.modules.TravelModeDetection;
import de.dkt.eservices.erattlesnakenlp.modules.mae.MovementVerbDetection;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import eu.freme.common.conversion.rdf.RDFConstants;
import eu.freme.common.conversion.rdf.RDFConstants.RDFSerialization;

public class ProcessMendelsohnLettersNIF {

	MendelsohnParser mendelsohnParser = new MendelsohnParser();

	TravelModeDetection travelModeDetection = new TravelModeDetection();
	MovementVerbDetection movementVerbDetection = new MovementVerbDetection();
	protected StanfordCoreNLP pipeline;

	public ProcessMendelsohnLettersNIF() {
	}
	
	public void initializeModels() {
		travelModeDetection.initializeModels();
		movementVerbDetection.initializeModels();
		
		Properties props;
		props = new Properties();
		props.put("annotators", "tokenize, ssplit, pos, lemma");
		this.pipeline = new StanfordCoreNLP(props);
	}
	
	private String username="dba";
	private String password="r{XjnF18X,IU";

	public static void main(String[] args) throws Exception {
		ProcessMendelsohnLettersNIF pml = new ProcessMendelsohnLettersNIF();		
//		String sFolder = "/Users/jumo04/Documents/DFKI/Conferences/EventsStories/MendelsohnLetters/manual/";
//		String sFolder = "/Users/jumo04/Downloads/allCleanLetters2/";
//		String sFolder = "/Users/jumo04/Downloads/newsCorpus/";
		String sFolder = "/Users/jumo04/Downloads/enLetters/plainText3/";
		
		/**
		 * Code to process the plaintext files of the mendelsohn letters.
		 */
		pml.initializeModels();
//		float[] fs2 = new float[1];
//		String outputFolder = "nif_MD/";
//		pml.processInitialDocuments(sFolder, fs2, outputFolder);
		float [][] weights = {
//				{0.3f,0.2f,0.2f,0.1f,0.1f,0.1f},
//				{0.3f,0.2f,0.2f,0.1f,0.1f,0.1f},
//				{0.55f,0.15f,0.15f,0.05f,0.05f,0.05f},
//				{0.167f,0.167f,0.167f,0.167f,0.167f,0.167f},
				{0.3f,0.2f,0.2f,0.1f,0.1f,0.1f}
				};
		int counter = 1;
		for (float[] fs : weights) {
			String outputFolder = "nif_MD_WithoutDuplicates__OnlyTriggered_ALL_Sentences_"+counter+"/";
			pml.processSentences(sFolder,fs,outputFolder);
			pml.computeResults(sFolder+outputFolder);
			counter++;
		}
	}

	public void processDocuments(String sFolder,float[] weights,String outputFolder) throws Exception {
		File folder = new File(sFolder+"nif_MD2/");
		File[] files = folder.listFiles();
		for (File file : files) {
			if(!file.isDirectory() && !file.getName().startsWith(".") && file.getName().endsWith(".nif")){
				String inputText = IOUtils.toString(new FileInputStream(file));
//				System.out.println(inputText);
				String language = "en";
				Model inputModel = NIFReader.extractModelFromFormatString(inputText, RDFSerialization.TURTLE);
				Model outputModel = detectSextuplesInDocument(inputModel,language,RDFSerialization.TURTLE,weights);
//				Model outputModel = addLetterInformation(inputModel,language,RDFSerialization.TURTLE,weights);
				File outputFile = new File(sFolder+File.separator+outputFolder+File.separator+file.getName());
				File outputFolderAux = new File(sFolder+File.separator+outputFolder+File.separator);
				outputFolderAux.mkdirs();
				outputFile.createNewFile();
				IOUtils.write(NIFReader.model2String(outputModel, RDFSerialization.TURTLE),new FileOutputStream(outputFile));
			}
		}
	}
	
	public void processSentences(String sFolder,float[] weights,String outputFolder) throws Exception {
		File folder = new File(sFolder+"nifs_MD2/");
		File[] files = folder.listFiles();
		for (File file : files) {
			if(!file.isDirectory() && !file.getName().startsWith(".") && file.getName().endsWith(".nif")){
				String inputText = IOUtils.toString(new FileInputStream(file));
//				System.out.println(inputText);
				String language = "en";
				Model inputModel = NIFReader.extractModelFromFormatString(inputText, RDFSerialization.TURTLE);
				Model outputModel = detectSextuples(inputModel,language,RDFSerialization.TURTLE,weights);
//				Model outputModel = addLetterInformation(inputModel,language,RDFSerialization.TURTLE,weights);
				File outputFile = new File(sFolder+File.separator+outputFolder+File.separator+file.getName());
				File outputFolderAux = new File(sFolder+File.separator+outputFolder+File.separator);
				outputFolderAux.mkdirs();
				outputFile.createNewFile();
				IOUtils.write(NIFReader.model2String(outputModel, RDFSerialization.TURTLE),new FileOutputStream(outputFile));
			}
		}
	}

	public Model removeSextuples(Model nifModel, String languageParam, RDFConstants.RDFSerialization inFormat) throws IOException{
		try{
			String documentURI = NIFReader.extractDocumentWholeURI(nifModel);
//			System.out.println(NIFReader.model2String(nifModel, RDFSerialization.TURTLE));
			ResIterator it = nifModel.listResourcesWithProperty(RDF.type, DKTNIF.MovementActionEvent);
			while(it.hasNext()){
				Resource res = it.next();
				nifModel.remove(nifModel.listStatements(res, null, (RDFNode) null));
			}
		}
		catch(Exception e){
		}
		return nifModel;
	}

	public Model addLetterInformation(Model nifModel, String languageParam, RDFConstants.RDFSerialization inFormat, float [] weights) throws IOException{
		try{
			String documentURI = NIFReader.extractDocumentWholeURI(nifModel);
			byte[] key = mendelsohnParser.getHashKey(NIFReader.extractIsString(nifModel)); // done on the base of (smallest) edit distance
			String letterAuthor = mendelsohnParser.getAuthor(key);
			String letterDate = mendelsohnParser.getDate(key);
			String letterLocation = mendelsohnParser.getLocation(key);
			NIFWriter.addMetaDataInformation(nifModel, documentURI, letterAuthor, letterDate, letterLocation);
			return nifModel;
		}
		catch(Exception e){
			e.printStackTrace();
			return nifModel;
		}
	}

	public Model detectSextuples(Model nifModel, String languageParam, RDFConstants.RDFSerialization inFormat, float [] weights) throws IOException{
		try{
			String documentURI = NIFReader.extractDocumentWholeURI(nifModel);

//			System.out.println(NIFReader.model2String(nifModel, RDFSerialization.TURTLE));
			List<MovementActionEvent> maes = new LinkedList<MovementActionEvent>();
			/**
			 * Assign weights to the elements of the sextuple.
			 */
			float personWeight = weights[0];
			float originWeight = weights[1];
			float destinationWeight = weights[2];
			float departureTimeWeight = weights[3];
			float arrivalTimeWeight = weights[4];
			float modeWeight = weights[5];

			/*
			 * Split the text into sentences 
			 */
			try{
				String inputText = NIFReader.extractIsString(nifModel).toLowerCase();
				Annotation document = new Annotation(inputText);
				pipeline.annotate(document);
				
				List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
				for (CoreMap sentence : sentences) {
					int sentenceEnd = 0;
					int sentenceOffset = inputText.length();
	                for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
	                	int tokenStart = token.beginPosition();
	                	int tokenEnd = token.endPosition();
	                	if(tokenStart<sentenceOffset){
	                		sentenceOffset=tokenStart;
	                	}
	                	if(tokenEnd>sentenceEnd){
	                		sentenceEnd=tokenEnd;
	                	}
	                }
	                System.out.println(sentenceOffset+"--"+sentenceEnd);
					/**
					 * Check in every sentence if there are alements of the sextuple (take them from the NIF)
					 */
					Map<String, DktAnnotation> elements = NIFReader.extractAnnotations(nifModel);
					
//					System.out.println(elements.size());
					List<DktAnnotation> iPersons = new LinkedList<DktAnnotation>();
					List<DktAnnotation> iLocations= new LinkedList<DktAnnotation>();
					List<DktAnnotation> iTimes= new LinkedList<DktAnnotation>();
					List<DktAnnotation> iModes = new LinkedList<DktAnnotation>();
					List<DktAnnotation> iTriggers = new LinkedList<DktAnnotation>();

					if(elements==null){
						continue;
					}
					Set<String> elementsKeys = elements.keySet();
					for (String elementKey : elementsKeys) {
						DktAnnotation object = elements.get(elementKey);
						if( (object.getStart()>sentenceOffset && object.getStart()<sentenceEnd)
								||
								(object.getEnd()>sentenceOffset && object.getEnd()<sentenceEnd)){
							if(object.getType().equalsIgnoreCase("person")){
								iPersons.add(object);
							}
							else if(object.getType().equalsIgnoreCase("location")){
								iLocations.add(object);
							}
							else if(object.getType().equalsIgnoreCase("temp")){
								iTimes.add(object);
							}
							else if(object.getType().equalsIgnoreCase("mode")){
								iModes.add(object);
							}
							else if(object.getType().equalsIgnoreCase("triggerVerb")){
								iTriggers.add(object);
							}
							else if(object.getType().equalsIgnoreCase("triggerTerm")){
								iTriggers.add(object);
							}
						}
					}
					System.out.println(iPersons.size()+"--"+iLocations.size()+"--"+iTimes.size()+"--"+iModes.size()+"--"+iTriggers.size());
//					System.exit(0);
/**
 * If person, location or time are empty, then include the letter information.
 */	
					iPersons.add(new DktAnnotation());
					iLocations.add(new DktAnnotation());
					iTimes.add(new DktAnnotation());
					iModes.add(new DktAnnotation());

					String metaData [] = NIFReader.extractMetaData(nifModel);
					if(iPersons.isEmpty()){
						DktAnnotation auxAnno = new DktAnnotation();
						auxAnno.setUri(documentURI + "#author");
						HashMap<String, String> properties = new HashMap<String, String>();
						properties.put(NIF.beginIndex.toString(), "0");
						properties.put(NIF.endIndex.toString(), "0");
						properties.put(NIF.anchorOf.toString(), metaData[0]);
						properties.put(ITSRDF.taClassRef.toString(), DBO.person.toString());
						auxAnno.setProperties(properties);
						iPersons.add(auxAnno);
					}
					if(iLocations.isEmpty()){
						DktAnnotation auxAnno = new DktAnnotation();
						auxAnno.setUri(documentURI + "#location");
						HashMap<String, String> properties = new HashMap<String, String>();
						properties.put(NIF.beginIndex.toString(), "0");
						properties.put(NIF.endIndex.toString(), "0");
						properties.put(NIF.anchorOf.toString(), metaData[1]);
						properties.put(ITSRDF.taClassRef.toString(), DBO.location.toString());
						auxAnno.setProperties(properties);
						iLocations.add(auxAnno);
					}
					if(iTimes.isEmpty()){
						DktAnnotation auxAnno = new DktAnnotation();
						auxAnno.setUri(documentURI + "#author");
						HashMap<String, String> properties = new HashMap<String, String>();
						properties.put(NIF.beginIndex.toString(), "0");
						properties.put(NIF.endIndex.toString(), "0");
						DateFormat format1 = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN);
				        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
//				        2017-04-21T00:00:00
				        try{
				        	Date date = format1.parse(metaData[2]);
							String sDate = df.format(date); 
							properties.put(NIF.anchorOf.toString(), sDate);
							properties.put(ITSRDF.taClassRef.toString(), TIME.temporalEntity.toString());
							properties.put(TIME.intervalStarts.toString(), sDate);
							auxAnno.setProperties(properties);
							iTimes.add(auxAnno);
				        }
				        catch(Exception e){
				        	iTimes.add(new DktAnnotation());
				        }
//			        	iTimes.add(new DktAnnotation());
					}
					if(iModes.isEmpty()){
						iModes.add(new DktAnnotation());
					}

					if(iTriggers.isEmpty()){
						continue;
					}


					/**
					 * Generate all the possible sextuples
					 */
					for (DktAnnotation person: iPersons) {
						System.out.println("--------");
						for (DktAnnotation location1 : iLocations) {
							for (DktAnnotation location2 : iLocations) {
								if( !(!location1.isEmpty() && !location2.isEmpty() && 
										location1.getText().equalsIgnoreCase(location2.getText())) ){
									for (DktAnnotation time1 : iTimes) {
										for (DktAnnotation time2: iTimes) {
											if( !( !time1.isEmpty() && !time2.isEmpty() && 
													time1.getText().equalsIgnoreCase(time2.getText())) ){
											for (DktAnnotation mode: iModes) {
//												System.out.println(time1.getText());
												DateFormat format3 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
//										        2017-04-21T00:00:00
												Date dTime1 = (time1.getText()!=null) ? format3.parse(time1.getText()) : null;
												Date dTime2 = (time2.getText()!=null) ? format3.parse(time2.getText()) : null;

												float perVal=1,loc1Val=1,loc2Val=1,tim1Val=1,tim2Val=1,modVal=1;
												if(person.getType().equalsIgnoreCase("empty")){
													perVal=0;
												}
												if(location1.getType().equalsIgnoreCase("empty")){
													loc1Val=0;
												}
												if(location2.getType().equalsIgnoreCase("empty")){
													loc2Val=0;
												}
												if(time1.getType().equalsIgnoreCase("empty")){
													tim1Val=0;
												}
												if(time2.getType().equalsIgnoreCase("empty")){
													tim2Val=0;
												}
												if(mode.getType().equalsIgnoreCase("empty")){
													modVal=0;
												}
												float maeScore = perVal*personWeight + loc1Val*originWeight + loc2Val*destinationWeight + 
														tim1Val*departureTimeWeight + tim2Val*arrivalTimeWeight + modVal*modeWeight;

//											System.out.println("DEBUG TEXT: --------------");
//											System.out.println("DEBUG TEXT: "+sentence.get(CoreAnnotations.OriginalTextAnnotation.class));
//											System.out.println("DEBUG TEXT: "+sentence.get(CoreAnnotations.TextAnnotation.class));
//											System.out.println("DEBUG TEXT: "+sentence.get(CoreAnnotations.SentenceBeginAnnotation.class));
//											System.out.println("DEBUG TEXT: "+sentence.get(CoreAnnotations.OriginalTextAnnotation.class));
												MovementActionEvent auxmae = new MovementActionEvent(sentenceOffset, sentenceEnd, 
														person.getText(), 
														location1.getText(), location2.getText(), 
														dTime1, dTime2, 
														mode.getText(),
														sentence.get(CoreAnnotations.TextAnnotation.class),
														//sentence,
														maeScore);

												maes.add(auxmae);
											}
										}
										}
									}
								}
							}
						}
					}
				}
			}
			catch(Exception e){
				e.printStackTrace();
				return nifModel;
			}
			
//			/**
//			 * Detect Transportation modes: indirectly from some self-defined rules taking information from the defined MAEs.
//			 */
//			auxModel = travelModeDetection.detectIndirectTransportationModes(auxModel, languageParam, inFormat);

			for (MovementActionEvent mae : maes) {
				NIFWriter.addSextupleMAEAnnotation(nifModel, documentURI, 
						mae.getPerson(),mae.getOrigin(),mae.getDestination(),
						mae.getDepartureTime(),mae.getArrivalTime(),mae.getTravelMode(),
						mae.getStartIndex(),mae.getEndIndex(),
						mae.getText(),mae.getScore()
						);
			}
//			System.out.println(NIFReader.model2String(nifModel, RDFSerialization.TURTLE));	
			return nifModel;
		}
		catch(Exception e){
			e.printStackTrace();
			return nifModel;
		}
	}

	public Model detectSextuplesInDocument(Model nifModel, String languageParam, RDFConstants.RDFSerialization inFormat, float [] weights) throws IOException{
		try{
			String documentURI = NIFReader.extractDocumentWholeURI(nifModel);

//			System.out.println(NIFReader.model2String(nifModel, RDFSerialization.TURTLE));
			List<MovementActionEvent> maes = new LinkedList<MovementActionEvent>();
			/**
			 * Assign weights to the elements of the sextuple.
			 */
			float personWeight = weights[0];
			float originWeight = weights[1];
			float destinationWeight = weights[2];
			float departureTimeWeight = weights[3];
			float arrivalTimeWeight = weights[4];
			float modeWeight = weights[5];

			/*
			 * Split the text into sentences 
			 */
			try{
				int sentenceOffset = 0;
				String inputText = NIFReader.extractIsString(nifModel).toLowerCase();
//				Annotation document = new Annotation(inputText);
//				pipeline.annotate(document);
//				
//				List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
//				for (CoreMap sentence : sentences) {
//					int sentenceEnd = sentenceOffset + sentence.size();
				String sentence = inputText;
				int sentenceEnd = sentenceOffset + sentence.length();
					/**
					 * Check in every sentence if there are alements of the sextuple (take them from the NIF)
					 */
					Map<String, DktAnnotation> elements = NIFReader.extractAnnotations(nifModel);
					
					List<DktAnnotation> iPersons = new LinkedList<DktAnnotation>();
					List<DktAnnotation> iLocations= new LinkedList<DktAnnotation>();
					List<DktAnnotation> iTimes= new LinkedList<DktAnnotation>();
					List<DktAnnotation> iModes = new LinkedList<DktAnnotation>();
					List<DktAnnotation> iTriggers = new LinkedList<DktAnnotation>();

					if(elements==null){
						return nifModel;
					}
					Set<String> elementsKeys = elements.keySet();
					for (String elementKey : elementsKeys) {
						DktAnnotation object = elements.get(elementKey);
						if( (object.getStart()>sentenceOffset && object.getStart()<sentenceEnd)
								||
								(object.getEnd()>sentenceOffset && object.getEnd()<sentenceEnd)){
							if(object.getType().equalsIgnoreCase("person")){
								iPersons.add(object);
							}
							else if(object.getType().equalsIgnoreCase("location")){
								iLocations.add(object);
							}
							else if(object.getType().equalsIgnoreCase("time")){
								iTimes.add(object);
							}
							else if(object.getType().equalsIgnoreCase("mode")){
								iModes.add(object);
							}
							else if(object.getType().equalsIgnoreCase("triggerVerb")){
								iTriggers.add(object);
							}
							else if(object.getType().equalsIgnoreCase("triggerTerm")){
								iTriggers.add(object);
							}
						}
					}
/**
 * If person, location or time are empty, then include the letter information.
 */
					iPersons.add(new DktAnnotation());
					iLocations.add(new DktAnnotation());
					iTimes.add(new DktAnnotation());
					iModes.add(new DktAnnotation());
					String metaData [] = NIFReader.extractMetaData(nifModel);
//					if(iPersons.isEmpty()){
//						DktAnnotation auxAnno = new DktAnnotation();
//						auxAnno.setUri(documentURI + "#author");
//						HashMap<String, String> properties = new HashMap<String, String>();
//						properties.put(NIF.beginIndex.toString(), "0");
//						properties.put(NIF.endIndex.toString(), "0");
//						properties.put(NIF.anchorOf.toString(), metaData[0]);
//						properties.put(ITSRDF.taClassRef.toString(), DBO.person.toString());
//						auxAnno.setProperties(properties);
//						iPersons.add(auxAnno);
//					}
//					if(iLocations.isEmpty()){
//						DktAnnotation auxAnno = new DktAnnotation();
//						auxAnno.setUri(documentURI + "#location");
//						HashMap<String, String> properties = new HashMap<String, String>();
//						properties.put(NIF.beginIndex.toString(), "0");
//						properties.put(NIF.endIndex.toString(), "0");
//						properties.put(NIF.anchorOf.toString(), metaData[1]);
//						properties.put(ITSRDF.taClassRef.toString(), DBO.location.toString());
//						auxAnno.setProperties(properties);
//						iLocations.add(auxAnno);
//					}
//					if(iTimes.isEmpty()){
//						DktAnnotation auxAnno = new DktAnnotation();
//						auxAnno.setUri(documentURI + "#author");
//						HashMap<String, String> properties = new HashMap<String, String>();
//						properties.put(NIF.beginIndex.toString(), "0");
//						properties.put(NIF.endIndex.toString(), "0");
//						DateFormat format1 = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN);
//				        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
////				        2017-04-21T00:00:00
//				        try{
//				        	Date date = format1.parse(metaData[2]);
//							String sDate = df.format(date); 
//							properties.put(NIF.anchorOf.toString(), sDate);
//							properties.put(ITSRDF.taClassRef.toString(), TIME.temporalEntity.toString());
//							properties.put(TIME.intervalStarts.toString(), sDate);
//							auxAnno.setProperties(properties);
//							iTimes.add(auxAnno);
//				        }
//				        catch(Exception e){
//				        	iTimes.add(new DktAnnotation());
//				        }
////			        	iTimes.add(new DktAnnotation());
//					}
//					if(iModes.isEmpty()){
//						iModes.add(new DktAnnotation());
//					}
					/**
					 * Generate all the possible sextuples
					 */
					for (DktAnnotation person: iPersons) {
						System.out.println("--------");
						for (DktAnnotation location1 : iLocations) {
							for (DktAnnotation location2 : iLocations) {
								if( !(!location1.isEmpty() && !location2.isEmpty() && 
										location1.getText().equalsIgnoreCase(location2.getText())) ){
									for (DktAnnotation time1 : iTimes) {
										for (DktAnnotation time2: iTimes) {
											if( !( !time1.isEmpty() && !time2.isEmpty() && 
													time1.getText().equalsIgnoreCase(time2.getText())) ){
											for (DktAnnotation mode: iModes) {
//												System.out.println(time1.getText());
												DateFormat format3 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
//										        2017-04-21T00:00:00
												Date dTime1 = (time1.getText()!=null) ? format3.parse(time1.getText()) : null;
												Date dTime2 = (time2.getText()!=null) ? format3.parse(time2.getText()) : null;

												float perVal=1,loc1Val=1,loc2Val=1,tim1Val=1,tim2Val=1,modVal=1;
												if(person.getType().equalsIgnoreCase("empty")){
													perVal=0;
												}
												if(location1.getType().equalsIgnoreCase("empty")){
													loc1Val=0;
												}
												if(location2.getType().equalsIgnoreCase("empty")){
													loc2Val=0;
												}
												if(time1.getType().equalsIgnoreCase("empty")){
													tim1Val=0;
												}
												if(time2.getType().equalsIgnoreCase("empty")){
													tim2Val=0;
												}
												if(mode.getType().equalsIgnoreCase("empty")){
													modVal=0;
												}
												float maeScore = perVal*personWeight + loc1Val*originWeight + loc2Val*destinationWeight + 
														tim1Val*departureTimeWeight + tim2Val*arrivalTimeWeight + modVal*modeWeight;

//											System.out.println("DEBUG TEXT: --------------");
//											System.out.println("DEBUG TEXT: "+sentence.get(CoreAnnotations.OriginalTextAnnotation.class));
//											System.out.println("DEBUG TEXT: "+sentence.get(CoreAnnotations.TextAnnotation.class));
//											System.out.println("DEBUG TEXT: "+sentence.get(CoreAnnotations.SentenceBeginAnnotation.class));
//											System.out.println("DEBUG TEXT: "+sentence.get(CoreAnnotations.OriginalTextAnnotation.class));
												MovementActionEvent auxmae = new MovementActionEvent(sentenceOffset, sentenceEnd, 
														person.getText(), 
														location1.getText(), location2.getText(), 
														dTime1, dTime2, 
														mode.getText(),
//														sentence.get(CoreAnnotations.TextAnnotation.class),
														sentence,
														maeScore);

												maes.add(auxmae);
											}
										}
										}
									}
								}
							}
						}
					}
					sentenceOffset += sentence.length();
//					sentenceOffset += sentence.size();
//				}
			}
			catch(Exception e){
				e.printStackTrace();
				return nifModel;
			}
			
//			/**
//			 * Detect Transportation modes: indirectly from some self-defined rules taking information from the defined MAEs.
//			 */
//			auxModel = travelModeDetection.detectIndirectTransportationModes(auxModel, languageParam, inFormat);

			for (MovementActionEvent mae : maes) {
				NIFWriter.addSextupleMAEAnnotation(nifModel, documentURI, 
						mae.getPerson(),mae.getOrigin(),mae.getDestination(),
						mae.getDepartureTime(),mae.getArrivalTime(),mae.getTravelMode(),
						mae.getStartIndex(),mae.getEndIndex(),
						mae.getText(),mae.getScore()
						);
			}
//			System.out.println(NIFReader.model2String(nifModel, RDFSerialization.TURTLE));	
			return nifModel;
		}
		catch(Exception e){
			e.printStackTrace();
			return nifModel;
		}
	}

	public void computeResults(String sFolder) throws Exception {
		List<MovementActionEvent> maes = new LinkedList<MovementActionEvent>();
		File folder = new File(sFolder);
		File[] files = folder.listFiles();
		for (File file : files) {
			if(!file.isDirectory() && !file.getName().startsWith(".") && file.getName().endsWith(".nif")){
				String inputText = IOUtils.toString(new FileInputStream(file));
				String language = "en";
				Model nifModel = NIFReader.extractModelFromFormatString(inputText, RDFSerialization.TURTLE);
				Map<String,Map<String,String>> ms = NIFReader.extractMAEs(nifModel);
				if(ms!=null){
					Set<String> keys = ms.keySet();
					for (String key : keys) {
						Map<String,String> map = ms.get(key);
						String start = ( map.containsKey(NIF.beginIndex.toString()) ) ? map.get(NIF.beginIndex.toString()) : "0" ;
						String end = ( map.containsKey(NIF.endIndex.toString()) ) ? map.get(NIF.endIndex.toString()) : null ;
						String person = ( map.containsKey(DKTNIF.maePerson.toString()) ) ? map.get(DKTNIF.maePerson.toString()) : null ;
						String origin = ( map.containsKey(DKTNIF.maeOrigin.toString()) ) ? map.get(DKTNIF.maeOrigin.toString()) : null ;
						String destination = ( map.containsKey(DKTNIF.maeDestination.toString()) ) ? map.get(DKTNIF.maeDestination.toString()) : null ;
				        DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
						Date depTime = ( map.containsKey(DKTNIF.maeDepartureTime.toString()) ) ? df.parse(map.get(DKTNIF.maeDepartureTime.toString())) : null ;
						Date arrTime = ( map.containsKey(DKTNIF.maeArrivalTime.toString()) ) ? df.parse(map.get(DKTNIF.maeArrivalTime.toString())) : null ;
						String travelMode = ( map.containsKey(DKTNIF.maeTravelMode.toString()) ) ? map.get(DKTNIF.maeTravelMode.toString()) : null ;
						String score = ( map.containsKey(DKTNIF.maeScore.toString()) ) ? map.get(DKTNIF.maeScore.toString()) : null ;
						String text = ( map.containsKey(NIF.anchorOf.toString()) ) ? map.get(NIF.anchorOf.toString()) : null ;
						MovementActionEvent auxMae = new MovementActionEvent(Integer.parseInt(start), Integer.parseInt(end), 
								person, origin, destination, 
								depTime, arrTime, travelMode, 
								text, Float.parseFloat(score)); 
						maes.add(auxMae);
					}
				}
			}
		}
		System.out.println("Folder: "+sFolder);
		float thresholds [] = {0.1f,0.2f,0.3f,0.4f,0.5f,0.6f,0.7f,0.8f,0.9f,1f};
		for (float th : thresholds) {
			int counter = 1;
			JSONArray array = new JSONArray();
			for (MovementActionEvent m : maes) {
				if(m.getScore()>th){
					JSONObject obj = new JSONObject();
					obj.put("id", counter);
					obj.put("startIndex", m.getStartIndex());
					obj.put("endIndex", m.getEndIndex());
					obj.put("person", m.getPerson());
					obj.put("origin", m.getOrigin());
					obj.put("destination", m.getDestination());
					obj.put("departureTime", m.getDepartureTime());
					obj.put("arrivalTime", m.getArrivalTime());
					obj.put("travelMode", m.getTravelMode());
					obj.put("text", m.getText());
					obj.put("score", m.getScore());
					counter++;
					array.put(obj);
				}
			}
			System.out.println("\tThreshold: "+th+"\tNumber of MAEs: "+array.length());
			File outputFile = new File(sFolder+File.separator+th+".json");
			outputFile.createNewFile();
			IOUtils.write(array.toString(1),new FileOutputStream(outputFile));
		}
	}
}
