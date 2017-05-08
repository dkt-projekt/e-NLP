package de.dkt.eservices.erattlesnakenlp;

import de.dkt.common.niftools.DKTNIF;
import de.dkt.common.niftools.DktAnnotation;
import de.dkt.common.niftools.NIFReader;
import de.dkt.common.niftools.NIFWriter;
import de.dkt.common.tools.FileReadUtilities;
import de.dkt.common.tools.ParameterChecker;
import de.dkt.eservices.eheideltime.EHeideltimeService;
import de.dkt.eservices.eopennlp.EOpenNLPService;
import de.dkt.eservices.eopennlp.modules.DictionaryNameF;
import de.dkt.eservices.eopennlp.modules.NameFinder;
import de.dkt.eservices.eopennlp.modules.RegexFinder;
import de.dkt.eservices.erattlesnakenlp.linguistic.EntityCandidateExtractor;
import de.dkt.eservices.erattlesnakenlp.linguistic.EntityRelationTriple;
import de.dkt.eservices.erattlesnakenlp.linguistic.MovementActionEvent;
import de.dkt.eservices.erattlesnakenlp.linguistic.RelationExtraction;
import de.dkt.eservices.erattlesnakenlp.modules.LanguageIdentificator;
import de.dkt.eservices.erattlesnakenlp.modules.MendelsohnParser;
import de.dkt.eservices.erattlesnakenlp.modules.ParagraphDetector;
import de.dkt.eservices.erattlesnakenlp.modules.TravelModeDetection;
import de.dkt.eservices.erattlesnakenlp.modules.mae.MovementVerbDetection;
import de.dkt.eservices.erattlesnakenlp.modules.mae.Trigger;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import eu.freme.common.conversion.rdf.RDFConstants;
import eu.freme.common.conversion.rdf.RDFConstants.RDFSerialization;
import eu.freme.common.exception.BadRequestException;
import eu.freme.common.exception.ExternalServiceFailedException;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.annotation.PostConstruct;

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
import org.apache.jena.riot.RiotException;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

@Component
public class ERattlesnakeNLPService {
    
	Logger logger = Logger.getLogger(ERattlesnakeNLPService.class);
	
	@Autowired
	EOpenNLPService openNLPService;

	@Autowired
	EHeideltimeService heideltimeService;

	@Autowired
	LanguageIdentificator languageIdentificator;
	
	@Autowired
	MendelsohnParser mendelsohnParser;

	@Autowired
	TravelModeDetection travelModeDetection;

	@Autowired
	MovementVerbDetection movementVerbDetection;

	protected StanfordCoreNLP pipeline;

	@PostConstruct
	public void initializeModels(){
		Properties props;
		props = new Properties();
		props.put("annotators", "tokenize, ssplit, pos, lemma");
		this.pipeline = new StanfordCoreNLP(props);
	}
	
	public ResponseEntity<String> segmentParagraphs(String inputFile, String language) {
		ResponseEntity<String> responseCode = null;
		FileSystemResource fsr = new FileSystemResource(inputFile);
		String inputText;
		try {
			inputText = FileReadUtilities.readFile2String(fsr.getPath());
		} catch (IOException e) {
			logger.error("Error at reading input file");
			throw new BadRequestException("Error at reading input file");
		}
		Model nifModel = ModelFactory.createDefaultModel();
		//Model nifModel = NIFWriter.initializeOutputModel();
		NIFWriter.addInitialString(nifModel, inputText, DKTNIF.createDocumentURI());
		responseCode = ParagraphDetector.detectParagraphsNIF(nifModel);
		return responseCode;
	}
		
	public Model identifyInputLanguage(String textToProcess, RDFConstants.RDFSerialization inFormat) {
		
		
		Model nifModel = null;
    	if (inFormat.equals(RDFConstants.RDFSerialization.PLAINTEXT)){
			nifModel = NIFWriter.initializeOutputModel();
			NIFWriter.addInitialString(nifModel, textToProcess, DKTNIF.getDefaultPrefix());
		}
		else {
			try {
				nifModel = NIFReader.extractModelFromFormatString(textToProcess,inFormat);
			} catch (Exception e) {
				logger.error("Could not unserialize NIF string\n" + textToProcess + "\nwith format:\n" + inFormat);
				throw new BadRequestException("Unable to unserialize input NIF. Please validate.");
			}
		}
    	// ideally do initialization of ngramMaps as early as possible (or at least make sure that it is done only once). Depending on the size of the training corpus, it can take a while.
    	if (languageIdentificator.ngrampMapDict.isEmpty()){
    		System.out.println("ngramMap is empty, initializing...");
    		languageIdentificator.initializeNgramMaps();
    	}
    	
		languageIdentificator.detectLanguageNIF(nifModel);
    	return nifModel;
	}
	
	public Model extractRelations(Model nifModel, String languageParam, RDFConstants.RDFSerialization inFormat) throws IOException{
		ParameterChecker.checkNotNullOrEmpty(languageParam, "language", logger);

		ArrayList<EntityRelationTriple> ert = new ArrayList<EntityRelationTriple>();
		ert = RelationExtraction.getDirectRelationsNIF(nifModel, languageParam, ert);
		for (EntityRelationTriple t : ert){
			if (t.getRelation() != null && t.getSubject() != null && t.getObject() != null && t.getLemma() != null){ // TODO: find out why there is an empty first item in this list
				NIFWriter.addAnnotationRelation(nifModel, t.getStartIndex(), t.getEndIndex(), t.getRelation(), t.getSubject(), t.getLemma(), t.getObject(), t.getThemRoleSubj(), t.getThemRoleObj());
			}
		}
		
		return nifModel;

	}
	
	public Model detectEvents(Model nifModel, String languageParam, RDFConstants.RDFSerialization inFormat) throws IOException{
		try{
			String prefix = "";
			Model auxModel = openNLPService.analyze(NIFReader.extractIsString(nifModel), 
					languageParam, 
					"ner", 
					"ner-wikinerEn_PER;ner-wikinerEn_LOC;ner-wikinerEn_ORG",  
					inFormat, 
					"all", 
					prefix);
			
//			System.out.println(NIFReader.model2String(auxModel, RDFSerialization.TURTLE));
			
			auxModel = heideltimeService.annotateTime(auxModel, languageParam);

//			System.out.println(NIFReader.model2String(auxModel, RDFSerialization.TURTLE));

			ArrayList<EntityRelationTriple> ert = new ArrayList<EntityRelationTriple>();
			ert = RelationExtraction.getDirectRelationsNIF(auxModel, languageParam, ert);
			for (EntityRelationTriple t : ert){
				if (t.getRelation() != null && t.getSubject() != null && t.getObject() != null && t.getLemma() != null){ // TODO: find out why there is an empty first item in this list
					NIFWriter.addAnnotationRelation(auxModel, t.getStartIndex(), t.getEndIndex(), t.getRelation(), t.getSubject(), t.getLemma(), t.getObject(), t.getThemRoleSubj(), t.getThemRoleObj());
				}
			}
			
			//Combine the entities of NER with the elements in the relation extraction.
			
//			System.out.println(NIFReader.model2String(auxModel, RDFSerialization.TURTLE));
			
			
			//Do some timelining????
			
			
			
			
			
			//Do some .....

			
			
			
			return auxModel;
		}
		catch(Exception e){
			e.printStackTrace();
			return nifModel;
		}
	}
	
	private String username="dba";
	private String password="r{XjnF18X,IU";

	private CloseableHttpClient getHttpClient() {
		CredentialsProvider credsProvider = new BasicCredentialsProvider();
		credsProvider.setCredentials(new AuthScope(AuthScope.ANY_HOST,
				AuthScope.ANY_PORT), new UsernamePasswordCredentials(username,
				password));
		return HttpClients.custom()
				.setDefaultCredentialsProvider(credsProvider).build();
	}

	public Model detectSextuples(Model nifModel, String languageParam, RDFConstants.RDFSerialization inFormat) throws IOException{
		try{
			String documentURI = NIFReader.extractDocumentWholeURI(nifModel);
			/**
			 * It only works for EN/DE.(If we want more languages, we should add more langiuage models.)
			 */
//			String detectedLanguage = languageIdentificator.getLanguageNIF(nifModel);
//			// TODO; this initialises the language models every time, perhaps do this at initiasation instead
//			/**
//			 * TODO If not english, translate it.
//			 */
//			System.out.println("DEBUGGING detectedlang:" + detectedLanguage);
			String detectedLanguage = languageParam;
			
			Model auxModel = null;
			String prefix = "";
			if(detectedLanguage.equalsIgnoreCase("en")){
				auxModel = openNLPService.analyze(NIFReader.extractIsString(nifModel), 
						detectedLanguage, 
						"ner", 
						"ner-wikinerEn_PER;ner-wikinerEn_LOC;ner-wikinerEn_ORG",  
						inFormat, 
						"all", 
						prefix);

				auxModel = openNLPService.analyze(NIFReader.extractIsString(nifModel), 
						detectedLanguage, 
						"temp", 
						"englishDates",
						inFormat, 
						"all", 
						prefix);
			}
			else{
				auxModel = openNLPService.analyze(NIFReader.extractIsString(nifModel), 
						detectedLanguage, 
						"ner", 
						"ner-de_aij-wikinerTrainPER;ner-de_aij-wikinerTrainORG;ner-de_aij-wikinerTrainLOC",
						inFormat, 
						"all", 
						prefix);

				auxModel = openNLPService.analyze(NIFReader.extractIsString(nifModel), 
						detectedLanguage, 
						"temp", 
						"germanDates",
						inFormat, 
						"all", 
						prefix);
			}			
//			System.out.println(NIFReader.model2String(auxModel, RDFSerialization.TURTLE));
			
			//auxModel = heideltimeService.annotateTime(auxModel, detectedLanguage); // TODO: fix this one
//			System.out.println(NIFReader.model2String(auxModel, RDFSerialization.TURTLE));

			// get md5 hash key (this is they key of the serialzed hashmap with all metadata)
			byte[] key = mendelsohnParser.getHashKey(NIFReader.extractIsString(nifModel)); // done on the base of (smallest) edit distance
			//TODO: this loads the serialized metadata everytime, perhaps do this at initiation (of the broker, through spring magic)
			
			String letterAuthor = mendelsohnParser.getAuthor(key);
			
			String letterDate = mendelsohnParser.getDate(key);
			
			String letterLocation = mendelsohnParser.getLocation(key);
			
			//TODO: next step to get an actual DBPedia uri/proper date for dates, and fill slots.
			System.out.println("DEBUGGING info:" + letterAuthor);
			System.out.println("DEBUGGING info:" + letterDate);
			System.out.println("DEBUGGING info:" + letterLocation);
			
			/**
			 * Detect Transportation modes: directly from a dictionary and indirectly from some self-defined rules.
			 */
			auxModel = travelModeDetection.detectTransportationModes(auxModel, languageParam, inFormat);

			auxModel = movementVerbDetection.detectMovementVerbs(auxModel, languageParam, inFormat);

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
						}
					}
					
					if(iPersons.isEmpty()){
						iPersons.add(new DktAnnotation());
					}
					if(iLocations.isEmpty()){
						iLocations.add(new DktAnnotation());
					}
					if(iTimes.isEmpty()){
						iTimes.add(new DktAnnotation());
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
								for (DktAnnotation time1 : iTimes) {
									for (DktAnnotation time2: iTimes) {
										for (DktAnnotation mode: iModes) {
											MovementActionEvent auxmae = new MovementActionEvent(sentenceOffset, sentenceEnd, 
													person.getText(), 
													location1.getText(), location2.getText(), 
													new Date(time1.getText()), new Date(time2.getText()), 
													mode.getText());
											
											float perVal=1,loc1Val=1,loc2Val=2,tim1Val=1,tim2Val=1,modVal=1;
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
											
											if(maeScore > threshold){
												maes.add(auxmae);
											}
											
										}
									}
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
						mae.getStartIndex(),mae.getEndIndex()
						);
			}
			System.out.println(NIFReader.model2String(auxModel, RDFSerialization.TURTLE));	
			
//			String turtle = NIFReader.model2String(auxModel, RDFSerialization.TURTLE);
//			String graphUri = "";
//			String crudApiEndpoint = "https://dev.digitale-kuratierung.de/sparql-graph-crud-auth";
//			
//			CloseableHttpClient httpclient = null;
//			CloseableHttpResponse response = null;
//			boolean ok = false;
//			try {
//				httpclient = getHttpClient();
//				String uri = crudApiEndpoint + "?graph-uri="
//						+ URLEncoder.encode(graphUri, "utf-8");
//				HttpPost request = new HttpPost(uri);
//
//				request.addHeader("Content-Type", "text/turtle; charset=utf-8");
//
//				HttpEntity entity = new StringEntity(turtle, "utf-8");
//				request.setEntity(entity);
//
//				response = httpclient.execute(request);
//				StatusLine sl = response.getStatusLine();
//				ok = sl.getStatusCode() == 200 || sl.getStatusCode() == 201;
//				
//				if(!ok){
//					logger.error("Triplestore returned status \"" + sl.getStatusCode() + "\" when trying to write data to it.");
//				}
//			} finally {
//				if (response != null) {
//					response.close();
//				}
//				if (httpclient != null) {
//					httpclient.close();
//				}
//			}
			return auxModel;
		}
		catch(Exception e){
			e.printStackTrace();
			return nifModel;
		}
	}
	
	public HashMap<String, String> suggestEntityCandidates(Model nifModel, String languageParam, RDFConstants.RDFSerialization inFormat, double thresholdValue, ArrayList<String> classificationModels)
					throws ExternalServiceFailedException, BadRequestException, IOException, Exception {
		ParameterChecker.checkNotNullOrEmpty(languageParam, "language", logger);

		HashMap<String, String> entityCandidates = new HashMap<String, String>();
		try {
			String referenceCorpus=  null;
			if (languageParam.equalsIgnoreCase("en")){
				referenceCorpus = "englishReuters";
			}
			else if (languageParam.equalsIgnoreCase("de")){
				referenceCorpus = "germanZeitungMTCorp";
			}
			else { //TODO: add more ref corpora for other languages and modify here
				throw new BadRequestException("No reference corpus available yet for language: " + languageParam);
			}
			

			entityCandidates = EntityCandidateExtractor.entitySuggest(nifModel, languageParam, referenceCorpus, thresholdValue, classificationModels);
			
		} catch (BadRequestException e) {
			logger.error(e.getMessage());
			throw e;
		} catch (ExternalServiceFailedException e2) {
			logger.error(e2.getMessage());
			throw e2;
		}

		return entityCandidates;
	
	}
	
	
	public Model annotateEntityCandidates(Model nifModel, String languageParam, RDFConstants.RDFSerialization inFormat, double thresholdValue, ArrayList<String> classificationModels)
			throws ExternalServiceFailedException, BadRequestException, IOException, Exception{
		
		ParameterChecker.checkNotNullOrEmpty(languageParam, "language", logger);
		
		String sentModel = null;
		if (languageParam.equals("en") || languageParam.equals("de")){
			sentModel = languageParam + "-sent.bin";
		}
		else{
			logger.error("No sentence model language: "+ languageParam);
			throw new BadRequestException("No sentence model language: "+ languageParam);
		}

		try {
			String referenceCorpus=  null;
			if (languageParam.equalsIgnoreCase("en")){
				referenceCorpus = "englishReuters";
			}
			else if (languageParam.equalsIgnoreCase("de")){
				referenceCorpus = "germanZeitungMTCorp";
			}
			else { //TODO: add more ref corpora for other languages and modify here
				throw new BadRequestException("No reference corpus available yet for language: " + languageParam);
			}
			
			nifModel = EntityCandidateExtractor.entityAnnotate(nifModel, languageParam, referenceCorpus, thresholdValue, classificationModels, sentModel);
			
		} catch (BadRequestException e) {
			logger.error(e.getMessage());
			throw e;
		} catch (ExternalServiceFailedException e2) {
			logger.error(e2.getMessage());
			throw e2;
		}

		
		return nifModel;
	}
	
	
	public String uploadClassificationModel(String data, String modelName) throws ExternalServiceFailedException, BadRequestException, IOException, Exception {
		
		try {
			EntityCandidateExtractor.serializeClassLanguageModel(data, modelName);

		} catch (BadRequestException e) {
			logger.error(e.getMessage());
			throw e;
		} catch (ExternalServiceFailedException e2) {
			logger.error(e2.getMessage());
			throw e2;
		}

		return "success"; // TODO come up with a proper response here

}
	
	

}
