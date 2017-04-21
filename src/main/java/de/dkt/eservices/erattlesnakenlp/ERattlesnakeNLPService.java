package de.dkt.eservices.erattlesnakenlp;

import de.dkt.common.niftools.DKTNIF;
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
import eu.freme.common.conversion.rdf.RDFConstants;
import eu.freme.common.conversion.rdf.RDFConstants.RDFSerialization;
import eu.freme.common.exception.BadRequestException;
import eu.freme.common.exception.ExternalServiceFailedException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

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
	
	public Model detectSextuples(Model nifModel, String languageParam, RDFConstants.RDFSerialization inFormat) throws IOException{
		try{
			
			String documentURI = NIFReader.extractDocumentWholeURI(nifModel);
			/**
			 * It only works for EN/DE.(If we want more languages, we should add more langiuage models.)
			 */
			String detectedLanguage = languageIdentificator.getLanguageNIF(nifModel);
			// TODO; this initialises the language models every time, perhaps do this at initiasation instead
			/**
			 * TODO If not english, translate it.
			 */
			System.out.println("DEBUGGING detectedlang:" + detectedLanguage);
			
			
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

			List<MovementActionEvent> maes = new LinkedList<MovementActionEvent>();
			/**
			 * TODO Event Detection
			 *   If we get good quality in the event detection, we will get the information for the MAE from the event, 
			 *    if not, we will get it from the author, location and date of the letter.
			// Just fill the list of movement action events.
			 */
//			List<MovementActionEvent> maes = method_that_retrieves_events(auxModel, detectedLanguage, 
//					letterAuthor, letterDate, letterLocation);
			
			for (MovementActionEvent mae : maes) {
				NIFWriter.addSextupleMAEAnnotation(auxModel, documentURI, 
						mae.getPerson(),mae.getOrigin(),mae.getDestination(),
						mae.getDepartureTime(),mae.getArrivalTime(),mae.getTravelMode(),
						mae.getStartIndex(),mae.getEndIndex()
						);
			}
//			System.out.println(NIFReader.model2String(auxModel, RDFSerialization.TURTLE));			
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
