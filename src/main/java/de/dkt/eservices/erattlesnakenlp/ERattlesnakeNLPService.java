package de.dkt.eservices.erattlesnakenlp;

import de.dkt.common.niftools.DKTNIF;
import de.dkt.common.niftools.NIFReader;
import de.dkt.common.niftools.NIFWriter;
import de.dkt.common.tools.FileReadUtilities;
import de.dkt.common.tools.ParameterChecker;
import de.dkt.eservices.eopennlp.modules.DictionaryNameF;
import de.dkt.eservices.eopennlp.modules.NameFinder;
import de.dkt.eservices.eopennlp.modules.RegexFinder;
import de.dkt.eservices.erattlesnakenlp.linguistic.EntityCandidateExtractor;
import de.dkt.eservices.erattlesnakenlp.linguistic.EntityRelationTriple;
import de.dkt.eservices.erattlesnakenlp.linguistic.RelationExtraction;
import de.dkt.eservices.erattlesnakenlp.modules.LanguageIdentificator;
import de.dkt.eservices.erattlesnakenlp.modules.ParagraphDetector;
import eu.freme.common.conversion.rdf.RDFConstants;
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
    	if (LanguageIdentificator.ngrampMapDict.isEmpty()){
    		System.out.println("ngramMap is empty, initializing...");
    		LanguageIdentificator.initializeNgramMaps();
    	}
    	
		LanguageIdentificator.detectLanguageNIF(nifModel);
    	return nifModel;
	}
	
	public String extractRelations(Model nifModel, String languageParam, RDFConstants.RDFSerialization inFormat){
		ParameterChecker.checkNotNullOrEmpty(languageParam, "language", logger);

		ArrayList<EntityRelationTriple> ert = RelationExtraction.getDirectRelationsNIF(nifModel, languageParam);
		JSONObject jsonOutput = new JSONObject();
		JSONArray jsonArrayRelations = new JSONArray();

		for (EntityRelationTriple erti : ert) {
			JSONObject jsonRelation = new JSONObject();
			jsonRelation.put("subject",erti.getSubject());
			jsonRelation.put("relation",erti.getRelation());
			jsonRelation.put("object",erti.getObject());
			jsonArrayRelations.put(jsonRelation);
		}
		jsonOutput.put("relations", jsonArrayRelations);
		return jsonOutput.toString();

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
