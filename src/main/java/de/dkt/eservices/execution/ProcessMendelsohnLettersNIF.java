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
import org.springframework.beans.factory.annotation.Autowired;

import com.hp.hpl.jena.rdf.model.Model;

import de.dkt.common.niftools.DBO;
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
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import eu.freme.common.conversion.rdf.RDFConstants;
import eu.freme.common.conversion.rdf.RDFConstants.RDFSerialization;

public class ProcessMendelsohnLettersNIF {

	LanguageIdentificator languageIdentificator = new LanguageIdentificator();
	EOpenNLPService openNLPService = new EOpenNLPService();
	MendelsohnParser mendelsohnParser = new MendelsohnParser();
	EHeideltimeService heideltimeService = new EHeideltimeService();

	TravelModeDetection travelModeDetection = new TravelModeDetection();
	MovementVerbDetection movementVerbDetection = new MovementVerbDetection();
	protected StanfordCoreNLP pipeline;

	public ProcessMendelsohnLettersNIF() {
	}
	
	public void initializeModels() {
		languageIdentificator.initializeNgramMaps();
		openNLPService.initializeModels();
		heideltimeService.initializeModels();

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
		pml.processInitialDocuments(sFolder);
				
		/**
		 * Code to store the processed NIF of the mendelsohn letters into the virtuoso triple storage.
		 */
//		pml.storeInVirtuoso(sFolder);
	}

	public void processInitialDocuments(String sFolder) throws Exception {
		File folder = new File(sFolder);
		File[] files = folder.listFiles();
		for (File file : files) {
			if(!file.isDirectory() && !file.getName().startsWith(".") && file.getName().endsWith(".nif")){
				String inputText = IOUtils.toString(new FileInputStream(file));
				String language = "en";
				Model inputModel = NIFReader.extractModelFromFormatString(inputText, RDFSerialization.TURTLE);
				Model outputModel = detectSextuples(inputModel,language,RDFSerialization.TURTLE);
				File outputFile = new File(sFolder+File.separator+"nifWithMAEs"+File.separator+file.getName()+".nif");
				outputFile.createNewFile();
				IOUtils.write(NIFReader.model2String(outputModel, RDFSerialization.TURTLE),new FileOutputStream(outputFile));
			}
		}
	}

	public Model detectSextuples(Model nifModel, String languageParam, RDFConstants.RDFSerialization inFormat) throws IOException{
		try{
			String documentURI = NIFReader.extractDocumentWholeURI(nifModel);
			Model auxModel = null;
			
//			System.out.println(NIFReader.model2String(nifModel, RDFSerialization.TURTLE));
			
			byte[] key = mendelsohnParser.getHashKey(NIFReader.extractIsString(nifModel)); // done on the base of (smallest) edit distance
			String letterAuthor = mendelsohnParser.getAuthor(key);			
			String letterDate = mendelsohnParser.getDate(key);
			String letterLocation = mendelsohnParser.getLocation(key);
//			System.out.println("DEBUGGING info:" + letterAuthor);
//			System.out.println("DEBUGGING info:" + letterDate);
//			System.out.println("DEBUGGING info:" + letterLocation);
			
			NIFWriter.
			
			List<MovementActionEvent> maes = new LinkedList<MovementActionEvent>();
			/**
			 * Assign weights to the elements of the sextuple.
			 */
			float personWeight = 0.3f;
			float originWeight = 0.2f;
			float destinationWeight = 0.2f;
			float departureTimeWeight = 0.1f;
			float arrivalTimeWeight = 0.1f;
			float modeWeight = 0.1f;
			/**
			 * Threshold that defines if a sextuple is a real MAE
			 */
			float threshold = 0.5f;

			/*
			 * Split the text into sentences 
			 */
			try{
				int sentenceOffset = 0;
				String inputText = NIFReader.extractIsString(nifModel).toLowerCase();
				Annotation document = new Annotation(inputText);
				pipeline.annotate(document);
				List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
				for (CoreMap sentence : sentences) {
					int sentenceEnd = sentenceOffset + sentence.size();
					/**
					 * Check in every sentence if there are alements of the sextuple (take them from the NIF)
					 */
					Map<String, DktAnnotation> elements = NIFReader.extractAnnotations(auxModel);
					
					List<DktAnnotation> iPersons = new LinkedList<DktAnnotation>();
					List<DktAnnotation> iLocations= new LinkedList<DktAnnotation>();
					List<DktAnnotation> iTimes= new LinkedList<DktAnnotation>();
					List<DktAnnotation> iModes = new LinkedList<DktAnnotation>();
					List<DktAnnotation> iTriggers = new LinkedList<DktAnnotation>();
					
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
					if(iPersons.isEmpty()){
						DktAnnotation auxAnno = new DktAnnotation();
						auxAnno.setUri(documentURI + "#author");
						HashMap<String, String> properties = new HashMap<String, String>();
						properties.put(NIF.beginIndex.toString(), "0");
						properties.put(NIF.endIndex.toString(), "0");
						properties.put(NIF.anchorOf.toString(), letterAuthor);
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
						properties.put(NIF.anchorOf.toString(), letterLocation);
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
						Date date = format1.parse(letterDate);
						String sDate = df.format(date); 
						properties.put(NIF.anchorOf.toString(), sDate);
						properties.put(ITSRDF.taClassRef.toString(), TIME.temporalEntity.toString());
						properties.put(TIME.intervalStarts.toString(), sDate);
						auxAnno.setProperties(properties);
						iTimes.add(auxAnno);
					}
					if(iModes.isEmpty()){
						iModes.add(new DktAnnotation());
					}
					/**
					 * Generate all the possible sextuples
					 */
					for (DktAnnotation person: iPersons) {
						for (DktAnnotation location1 : iLocations) {
							for (DktAnnotation location2 : iLocations) {
//								if(location1.getText()!=null && location2.getText()!=null && 
//										!location1.getText().equalsIgnoreCase(location2.getText())){
									for (DktAnnotation time1 : iTimes) {
										for (DktAnnotation time2: iTimes) {
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
														maeScore);

												if(maeScore > threshold){
													maes.add(auxmae);
												}

											}
										}
//									}
								}
							}
						}
					}
					sentenceOffset += sentence.size();
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
				NIFWriter.addSextupleMAEAnnotation(auxModel, documentURI, 
						mae.getPerson(),mae.getOrigin(),mae.getDestination(),
						mae.getDepartureTime(),mae.getArrivalTime(),mae.getTravelMode(),
						mae.getStartIndex(),mae.getEndIndex(),
						mae.getText(),mae.getScore()
						);
			}
			System.out.println(NIFReader.model2String(auxModel, RDFSerialization.TURTLE));	

			return auxModel;
		}
		catch(Exception e){
			e.printStackTrace();
			return nifModel;
		}
	}

}
